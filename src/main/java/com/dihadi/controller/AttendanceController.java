package com.dihadi.controller;

import com.dihadi.config.FirebaseConfig;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AttendanceController {

    private final RazorpayService razorpayService;
    private final WalletTransactionService walletService;
    private final Firestore db;

    public AttendanceController() {
        this.razorpayService = new RazorpayService();
        this.walletService = new WalletTransactionService();
        this.db = FirebaseConfig.getFirestore();
    }

    public RazorpayService getRazorpayService() {
        return this.razorpayService;
    }

    /**
     * Records worker attendance independently in the 'attendance' collection.
     */
    public void recordAttendance(String attendanceId,
                                 String workerId,
                                 String recruiterId,
                                 String projectId,
                                 String dateStr,
                                 String status,
                                 Runnable onSuccess,
                                 Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DocumentReference attRef = db.collection("attendance").document(attendanceId);
                DocumentReference attAltRef = db.collection("Attendance").document(attendanceId);

                Map<String, Object> data = new HashMap<>();
                data.put("attendanceId", attendanceId);
                data.put("workerId", workerId);
                data.put("recruiterId", recruiterId);
                data.put("projectId", projectId);
                data.put("date", dateStr);
                data.put("status", status != null ? status.toUpperCase() : "PRESENT");
                data.put("paymentStatus", "UNPAID");
                data.put("markedAt", FieldValue.serverTimestamp());
                data.put("timestamp", FieldValue.serverTimestamp());

                attRef.set(data, SetOptions.merge()).get();
                attAltRef.set(data, SetOptions.merge()).get();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.run();
        });
        task.setOnFailed(e -> {
            if (onError != null) onError.accept(task.getException() != null ? task.getException().getMessage() : "Failed to record attendance.");
        });

        new Thread(task).start();
    }

    /**
     * Step 1: Creates an order asynchronously via Razorpay.
     */
    public void createPaymentOrder(double wage, String receiptId, String notes, Consumer<String> onOrderCreated, Consumer<String> onError) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return razorpayService.createWagePaymentOrder(wage, receiptId, notes);
            }
        };

        task.setOnSucceeded(e -> {
            if (onOrderCreated != null) {
                onOrderCreated.accept(task.getValue());
            }
        });

        task.setOnFailed(e -> {
            if (onError != null) {
                onError.accept(task.getException() != null ? task.getException().getMessage() : "Order creation failed");
            }
        });

        new Thread(task).start();
    }

    /**
     * Step 2 / Alias: Verifies payment signature and credits the worker's wallet atomically in Firestore.
     */
    public void processPaymentAndCreditWorker(
            String recruiterId,
            String workerId,
            double wage,
            String attendanceId,
            String projectId,
            String orderId,
            String paymentId,
            String signature,
            Runnable onSuccess,
            Consumer<String> onError
    ) {
        verifyAndCompleteAttendance(recruiterId, workerId, wage, attendanceId, projectId, orderId, paymentId, signature, onSuccess, onError);
    }

    /**
     * Verifies payment signature and credits the worker's wallet atomically in Firestore.
     */
    public void verifyAndCompleteAttendance(
            String recruiterId,
            String workerId,
            double wage,
            String attendanceId,
            String projectId,
            String orderId,
            String paymentId,
            String signature,
            Runnable onSuccess,
            Consumer<String> onError
    ) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Authenticate signature (or direct API verification)
                boolean isValid = false;
                if ("api_verified".equalsIgnoreCase(signature)) {
                    isValid = razorpayService.isOrderPaid(orderId);
                } else {
                    isValid = razorpayService.verifyPaymentSignature(orderId, paymentId, signature);
                    if (!isValid) {
                        // Fallback check directly with Razorpay API
                        isValid = razorpayService.isOrderPaid(orderId);
                    }
                }
                if (!isValid) {
                    throw new SecurityException("Tampered payment response: Razorpay signature verification failed.");
                }

                // 2. Perform atomic credit to worker wallet and update attendance
                DocumentReference workerRef = db.collection("workers").document(workerId);
                DocumentReference workerAltRef = db.collection("Workers").document(workerId);

                DocumentReference attendanceRef = db.collection("attendance").document(attendanceId);
                DocumentReference attendanceAltRef = db.collection("Attendance").document(attendanceId);

                DocumentReference txnRef = db.collection("wallet_transactions").document(paymentId);

                db.runTransaction(transaction -> {
                    // Check existing attendance status
                    var attSnap = transaction.get(attendanceRef).get();
                    var attAltSnap = transaction.get(attendanceAltRef).get();
                    if ((attSnap.exists() && "PRESENT".equalsIgnoreCase(attSnap.getString("status"))) ||
                        (attAltSnap.exists() && "PRESENT".equalsIgnoreCase(attAltSnap.getString("status")))) {
                        throw new IllegalStateException("Attendance already marked and paid for this record.");
                    }

                    // Worker wallet balance & totalDaysWorked retrieval
                    var workerSnap = transaction.get(workerRef).get();
                    var workerAltSnap = transaction.get(workerAltRef).get();
                    Double currentWorkerBalance = 0.0;
                    Long daysWorked = 0L;

                    if (workerSnap.exists()) {
                        if (workerSnap.contains("walletBalance")) currentWorkerBalance = workerSnap.getDouble("walletBalance");
                        if (workerSnap.contains("totalDaysWorked")) daysWorked = workerSnap.getLong("totalDaysWorked");
                    } else if (workerAltSnap.exists()) {
                        if (workerAltSnap.contains("walletBalance")) currentWorkerBalance = workerAltSnap.getDouble("walletBalance");
                        if (workerAltSnap.contains("totalDaysWorked")) daysWorked = workerAltSnap.getLong("totalDaysWorked");
                    }
                    if (currentWorkerBalance == null) currentWorkerBalance = 0.0;
                    if (daysWorked == null) daysWorked = 0L;

                    double newBalance = currentWorkerBalance + wage;
                    long newDaysWorked = daysWorked + 1;

                    // Credit worker wallet & days worked in both casing variants
                    Map<String, Object> workerUpdates = new HashMap<>();
                    workerUpdates.put("workerId", workerId);
                    workerUpdates.put("walletBalance", newBalance);
                    workerUpdates.put("totalDaysWorked", newDaysWorked);
                    workerUpdates.put("updatedAt", FieldValue.serverTimestamp());
                    transaction.set(workerRef, workerUpdates, SetOptions.merge());
                    transaction.set(workerAltRef, workerUpdates, SetOptions.merge());

                    // Update attendance record with paymentStatus and transactionId
                    Map<String, Object> attMap = new HashMap<>();
                    attMap.put("attendanceId", attendanceId);
                    attMap.put("projectId", projectId);
                    attMap.put("recruiterId", recruiterId);
                    attMap.put("workerId", workerId);
                    attMap.put("status", "PRESENT");
                    attMap.put("paidAmount", wage);
                    attMap.put("paymentStatus", "PAID");
                    attMap.put("paymentTransactionId", paymentId);
                    attMap.put("transactionId", paymentId);
                    attMap.put("razorpayPaymentId", paymentId);
                    attMap.put("razorpayOrderId", orderId);
                    attMap.put("markedAt", FieldValue.serverTimestamp());
                    attMap.put("timestamp", FieldValue.serverTimestamp());
                    transaction.set(attendanceRef, attMap, SetOptions.merge());
                    transaction.set(attendanceAltRef, attMap, SetOptions.merge());

                    // Create transaction log in dedicated financial ledger
                    Map<String, Object> txnMap = new HashMap<>();
                    txnMap.put("transactionId", paymentId);
                    txnMap.put("orderId", orderId);
                    txnMap.put("razorpayOrderId", orderId);
                    txnMap.put("razorpayPaymentId", paymentId);
                    txnMap.put("recruiterId", recruiterId);
                    txnMap.put("workerId", workerId);
                    txnMap.put("projectId", projectId);
                    txnMap.put("attendanceId", attendanceId);
                    txnMap.put("amount", wage);
                    txnMap.put("currency", "INR");
                    txnMap.put("paymentMethod", "DEBIT_CARD_OR_GATEWAY");
                    txnMap.put("type", "DAILY_WAGE_CREDIT");
                    txnMap.put("status", "SUCCESS");
                    txnMap.put("timestamp", FieldValue.serverTimestamp());
                    transaction.set(txnRef, txnMap);

                    // Sync in-memory SessionManager.currentWorker if active
                    if (com.dihadi.view.SessionManager.currentWorker != null) {
                        com.dihadi.view.SessionManager.currentWorker.setWalletBalance(newBalance);
                        com.dihadi.view.SessionManager.currentWorker.setTotalDaysWorked((int) newDaysWorked);
                    }

                    return null;
                }).get();

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.run();
        });
        task.setOnFailed(e -> {
            if (onError != null) onError.accept(task.getException() != null ? task.getException().getMessage() : "Verification failed.");
        });

        new Thread(task).start();
    }

    public void markWorkerPresent(String recruiterId,
                                  String workerId,
                                  double wage,
                                  String attendanceId,
                                  String projectId,
                                  Runnable onSuccess,
                                  Consumer<String> onError) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return walletService.transferDailyWage(recruiterId, workerId, wage, attendanceId, projectId);
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex instanceof WalletTransactionService.InsufficientBalanceException) {
                onError.accept("LOW_BALANCE: " + ex.getMessage());
            } else {
                onError.accept("ERROR: " + (ex != null ? ex.getMessage() : "Transaction failed."));
            }
        });

        new Thread(task).start();
    }

    public java.util.List<com.dihadi.model.Attendance> getAttendanceByWorker(String workerMobile) {
        return new com.dihadi.dao.AttendanceDao().getAttendanceByWorker(workerMobile);
    }

    public java.util.List<com.dihadi.model.Attendance> getAttendanceByProject(String projectId) {
        return new com.dihadi.dao.AttendanceDao().getAttendanceByProject(projectId);
    }

    public com.dihadi.model.Attendance getAttendanceRecord(String attendanceId) {
        return new com.dihadi.dao.AttendanceDao().getAttendanceRecord(attendanceId);
    }

    public void saveAttendance(com.dihadi.model.Attendance attendance) {
        new com.dihadi.dao.AttendanceDao().saveAttendance(attendance);
    }

    public java.util.List<com.dihadi.model.Attendance> getAttendanceByRecruiter(String recruiterId) {
        return new com.dihadi.dao.AttendanceDao().getAttendanceByRecruiter(recruiterId);
    }
}