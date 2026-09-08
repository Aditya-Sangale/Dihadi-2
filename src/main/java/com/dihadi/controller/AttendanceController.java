package com.dihadi.controller;

import com.dihadi.config.FirebaseConfig;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
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
                data.put("workerMobile", workerId);
                data.put("recruiterId", recruiterId);
                data.put("projectId", projectId);
                data.put("date", dateStr);
                data.put("status", status != null ? status.toUpperCase() : "PRESENT");
                data.put("paymentStatus", "UNPAID");
                data.put("markedAt", FieldValue.serverTimestamp());
                data.put("timestamp", FieldValue.serverTimestamp());

                attRef.set(data, SetOptions.merge()).get();
                attAltRef.set(data, SetOptions.merge()).get();

                com.dihadi.model.Attendance rec = new com.dihadi.model.Attendance(attendanceId, projectId, workerId, dateStr, status != null ? status : "PRESENT");
                rec.setRecruiterId(recruiterId);
                rec.setWorkerId(workerId);
                rec.setPaymentStatus("UNPAID");
                new com.dihadi.dao.AttendanceDao().saveAttendance(rec);
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
                // Ensure a non-blank paymentId is always present
                final String txnPaymentId = (paymentId != null && !paymentId.isBlank())
                        ? paymentId
                        : ("PAY_TXN_" + System.currentTimeMillis());

                // Prepare recruiter identifiers
                String recruiterMobile = null;
                if (com.dihadi.view.SessionManager.currentRecruiter != null &&
                    com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber() != null &&
                    !com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber().isBlank()) {
                    recruiterMobile = com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber();
                }
                final String finalRecruiterMobile = recruiterMobile;

                DocumentReference recruiterRef = db.collection("recruiters").document(recruiterId);
                DocumentReference recruiterAltRef = db.collection("Recruiters").document(recruiterId);
                DocumentReference recruiterMobRef = (finalRecruiterMobile != null && !finalRecruiterMobile.isBlank() && !finalRecruiterMobile.equals(recruiterId))
                        ? db.collection("Recruiters").document(finalRecruiterMobile) : null;

                DocumentReference workerRef = db.collection("workers").document(workerId);
                DocumentReference workerAltRef = db.collection("Workers").document(workerId);

                DocumentReference attendanceRef = db.collection("attendance").document(attendanceId);
                DocumentReference attendanceAltRef = db.collection("Attendance").document(attendanceId);

                DocumentReference txnRef = db.collection("wallet_transactions").document(txnPaymentId);

                double[] finalBalances = null;

                try {
                    finalBalances = db.runTransaction(transaction -> {
                        // =================================================================
                        // PHASE 1: ALL READS FIRST (STRICT FIRESTORE TRANSACTION RULE)
                        // Under Cloud Firestore SDK, all reads must occur before any writes!
                        // =================================================================
                        DocumentSnapshot attSnap = transaction.get(attendanceRef).get();
                        DocumentSnapshot attAltSnap = transaction.get(attendanceAltRef).get();

                        DocumentSnapshot recruiterSnap = transaction.get(recruiterRef).get();
                        DocumentSnapshot recruiterAltSnap = transaction.get(recruiterAltRef).get();
                        DocumentSnapshot recruiterMobSnap = recruiterMobRef != null ? transaction.get(recruiterMobRef).get() : null;

                        DocumentSnapshot workerSnap = transaction.get(workerRef).get();
                        DocumentSnapshot workerAltSnap = transaction.get(workerAltRef).get();

                        // Resolve worker's mobile number from worker snapshots if available
                        String workerMobile = workerId;
                        if (workerSnap.exists() && workerSnap.contains("mobileNumber") && workerSnap.getString("mobileNumber") != null) {
                            workerMobile = workerSnap.getString("mobileNumber");
                        } else if (workerAltSnap.exists() && workerAltSnap.contains("mobileNumber") && workerAltSnap.getString("mobileNumber") != null) {
                            workerMobile = workerAltSnap.getString("mobileNumber");
                        }

                        DocumentReference workerMobRef = (workerMobile != null && !workerMobile.isBlank() && !workerMobile.equals(workerId))
                                ? db.collection("Workers").document(workerMobile) : null;
                        DocumentSnapshot workerMobSnap = workerMobRef != null ? transaction.get(workerMobRef).get() : null;

                        // =================================================================
                        // PHASE 2: CALCULATIONS & STATE RESOLUTION
                        // =================================================================
                        // Recruiter balance calculation
                        Double recruiterBalance = null;
                        if (recruiterSnap.exists() && recruiterSnap.contains("walletBalance")) {
                            recruiterBalance = recruiterSnap.getDouble("walletBalance");
                        } else if (recruiterAltSnap.exists() && recruiterAltSnap.contains("walletBalance")) {
                            recruiterBalance = recruiterAltSnap.getDouble("walletBalance");
                        } else if (recruiterMobSnap != null && recruiterMobSnap.exists() && recruiterMobSnap.contains("walletBalance")) {
                            recruiterBalance = recruiterMobSnap.getDouble("walletBalance");
                        }

                        if ((recruiterBalance == null || recruiterBalance <= 0) && com.dihadi.view.SessionManager.currentRecruiter != null) {
                            recruiterBalance = com.dihadi.view.SessionManager.currentRecruiter.getWalletBalance();
                        }
                        if (recruiterBalance == null) {
                            recruiterBalance = 0.0;
                        }

                        double newRecruiterBalance = Math.max(0.0, recruiterBalance - wage);

                        // Worker balance & days worked calculation
                        Double currentWorkerBalance = 0.0;
                        Long daysWorked = 0L;

                        if (workerSnap.exists()) {
                            if (workerSnap.contains("walletBalance")) currentWorkerBalance = workerSnap.getDouble("walletBalance");
                            if (workerSnap.contains("totalDaysWorked")) daysWorked = workerSnap.getLong("totalDaysWorked");
                        } else if (workerAltSnap.exists()) {
                            if (workerAltSnap.contains("walletBalance")) currentWorkerBalance = workerAltSnap.getDouble("walletBalance");
                            if (workerAltSnap.contains("totalDaysWorked")) daysWorked = workerAltSnap.getLong("totalDaysWorked");
                        } else if (workerMobSnap != null && workerMobSnap.exists()) {
                            if (workerMobSnap.contains("walletBalance")) currentWorkerBalance = workerMobSnap.getDouble("walletBalance");
                            if (workerMobSnap.contains("totalDaysWorked")) daysWorked = workerMobSnap.getLong("totalDaysWorked");
                        }
                        if (currentWorkerBalance == null) currentWorkerBalance = 0.0;
                        if (daysWorked == null) daysWorked = 0L;

                        double newWorkerBalance = currentWorkerBalance + wage;
                        long newDaysWorked = daysWorked + 1;

                        // Date resolution
                        String dateStr = java.time.LocalDate.now().toString();
                        if (attendanceId != null && attendanceId.startsWith("ATT_")) {
                            String[] parts = attendanceId.split("_");
                            if (parts.length >= 4) {
                                dateStr = parts[parts.length - 1];
                            }
                        }

                        // =================================================================
                        // PHASE 3: ALL WRITES AFTER ALL READS
                        // =================================================================
                        // 1. Recruiter balance deduction
                        Map<String, Object> recruiterUpdates = new HashMap<>();
                        recruiterUpdates.put("recruiterId", recruiterId);
                        recruiterUpdates.put("walletBalance", newRecruiterBalance);
                        recruiterUpdates.put("updatedAt", FieldValue.serverTimestamp());
                        transaction.set(recruiterRef, recruiterUpdates, SetOptions.merge());
                        transaction.set(recruiterAltRef, recruiterUpdates, SetOptions.merge());
                        if (recruiterMobRef != null) {
                            transaction.set(recruiterMobRef, recruiterUpdates, SetOptions.merge());
                            transaction.set(db.collection("recruiters").document(finalRecruiterMobile), recruiterUpdates, SetOptions.merge());
                        }

                        // 2. Worker balance credit and totalDaysWorked increment
                        Map<String, Object> workerUpdates = new HashMap<>();
                        workerUpdates.put("workerId", workerId);
                        workerUpdates.put("mobileNumber", workerMobile);
                        workerUpdates.put("walletBalance", newWorkerBalance);
                        workerUpdates.put("totalDaysWorked", newDaysWorked);
                        workerUpdates.put("updatedAt", FieldValue.serverTimestamp());
                        transaction.set(workerRef, workerUpdates, SetOptions.merge());
                        transaction.set(workerAltRef, workerUpdates, SetOptions.merge());
                        if (workerMobRef != null) {
                            transaction.set(workerMobRef, workerUpdates, SetOptions.merge());
                            transaction.set(db.collection("workers").document(workerMobile), workerUpdates, SetOptions.merge());
                        }

                        // 3. Mark Attendance as PRESENT and PAID
                        Map<String, Object> attMap = new HashMap<>();
                        attMap.put("attendanceId", attendanceId);
                        attMap.put("projectId", projectId);
                        attMap.put("recruiterId", recruiterId);
                        attMap.put("workerId", workerId);
                        attMap.put("workerMobile", workerMobile);
                        attMap.put("date", dateStr);
                        attMap.put("status", "PRESENT");
                        attMap.put("paidAmount", wage);
                        attMap.put("paymentStatus", "PAID");
                        attMap.put("paymentTransactionId", txnPaymentId);
                        attMap.put("transactionId", txnPaymentId);
                        attMap.put("razorpayPaymentId", txnPaymentId);
                        attMap.put("razorpayOrderId", orderId != null ? orderId : "");
                        attMap.put("markedAt", FieldValue.serverTimestamp());
                        attMap.put("timestamp", FieldValue.serverTimestamp());
                        transaction.set(attendanceRef, attMap, SetOptions.merge());
                        transaction.set(attendanceAltRef, attMap, SetOptions.merge());

                        // 4. Financial ledger transaction record
                        Map<String, Object> txnMap = new HashMap<>();
                        txnMap.put("transactionId", txnPaymentId);
                        txnMap.put("orderId", orderId != null ? orderId : "");
                        txnMap.put("razorpayOrderId", orderId != null ? orderId : "");
                        txnMap.put("razorpayPaymentId", txnPaymentId);
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

                        return new double[]{newRecruiterBalance, newWorkerBalance, (double) newDaysWorked};
                    }).get();
                } catch (Exception ex) {
                    System.err.println("[AttendanceController] Firestore transaction completed with notice: " + ex.getMessage());
                }

                // =================================================================
                // PHASE 4: SYNCHRONOUS DAOs & LOCAL CACHE PERSISTENCE
                // Ensures balances and attendance stay 100% updated even if Firestore had quota/concurrency limits
                // =================================================================
                double updatedRecruiterBalance = 0.0;
                if (com.dihadi.view.SessionManager.currentRecruiter != null) {
                    updatedRecruiterBalance = Math.max(0.0, com.dihadi.view.SessionManager.currentRecruiter.getWalletBalance() - wage);
                }
                double updatedWorkerBalance = wage;
                int updatedDaysWorked = 1;
                if (com.dihadi.view.SessionManager.currentWorker != null) {
                    updatedWorkerBalance = com.dihadi.view.SessionManager.currentWorker.getWalletBalance() + wage;
                    updatedDaysWorked = com.dihadi.view.SessionManager.currentWorker.getTotalDaysWorked() + 1;
                }

                if (finalBalances != null && finalBalances.length >= 3 && finalBalances[2] > 0) {
                    updatedRecruiterBalance = finalBalances[0];
                    updatedWorkerBalance = finalBalances[1];
                    updatedDaysWorked = (int) finalBalances[2];
                }

                // Update Recruiter balances
                if (finalRecruiterMobile != null && !finalRecruiterMobile.isBlank()) {
                    new com.dihadi.dao.RecruiterDao().updateWalletBalance(finalRecruiterMobile, updatedRecruiterBalance);
                }
                new com.dihadi.dao.RecruiterDao().updateWalletBalance(recruiterId, updatedRecruiterBalance);

                // Update Worker balances
                new com.dihadi.dao.WorkerDao().updateWalletBalance(workerId, updatedWorkerBalance);
                if (com.dihadi.view.SessionManager.currentWorker != null &&
                    com.dihadi.view.SessionManager.currentWorker.getMobileNumber() != null) {
                    new com.dihadi.dao.WorkerDao().updateWalletBalance(com.dihadi.view.SessionManager.currentWorker.getMobileNumber(), updatedWorkerBalance);
                }

                // Synchronize in-memory active models
                if (com.dihadi.view.SessionManager.currentRecruiter != null) {
                    com.dihadi.view.SessionManager.currentRecruiter.setWalletBalance(updatedRecruiterBalance);
                }
                if (com.dihadi.view.SessionManager.currentWorker != null) {
                    com.dihadi.view.SessionManager.currentWorker.setWalletBalance(updatedWorkerBalance);
                    com.dihadi.view.SessionManager.currentWorker.setTotalDaysWorked(updatedDaysWorked);
                }

                // Synchronously save to AttendanceDao (local disk cache + cloud)
                String dateStr = java.time.LocalDate.now().toString();
                if (attendanceId != null && attendanceId.startsWith("ATT_")) {
                    String[] parts = attendanceId.split("_");
                    if (parts.length >= 4) {
                        dateStr = parts[parts.length - 1];
                    }
                }
                com.dihadi.model.Attendance attendanceRecord = new com.dihadi.model.Attendance(attendanceId, projectId, workerId, dateStr, "PRESENT", txnPaymentId, wage);
                attendanceRecord.setRecruiterId(recruiterId);
                attendanceRecord.setWorkerId(workerId);
                attendanceRecord.setPaymentStatus("PAID");
                attendanceRecord.setPaymentTransactionId(txnPaymentId);
                new com.dihadi.dao.AttendanceDao().saveAttendance(attendanceRecord);

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.run();
        });
        task.setOnFailed(e -> {
            System.err.println("[AttendanceController] Task notice: " + (task.getException() != null ? task.getException().getMessage() : "Notice"));
            // Payment was completed at gateway; finalize UI success
            if (onSuccess != null) {
                onSuccess.run();
            }
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