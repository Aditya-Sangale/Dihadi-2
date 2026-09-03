package com.dihadi.controller;

import com.dihadi.config.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WalletTransactionService {

    private final Firestore db;

    public WalletTransactionService() {
        this.db = FirebaseConfig.getFirestore();
    }

    public static class InsufficientBalanceException extends Exception {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }

    /**
     * Executes atomic deduction from recruiter wallet and credit to worker wallet.
     */
    public String transferDailyWage(String recruiterId,
                                    String workerId,
                                    double wageAmount,
                                    String attendanceId,
                                    String projectId)
            throws InsufficientBalanceException, ExecutionException, InterruptedException {

        DocumentReference recruiterRef = db.collection("recruiters").document(recruiterId);
        DocumentReference recruiterAltRef = db.collection("Recruiters").document(recruiterId);

        DocumentReference workerRef = db.collection("workers").document(workerId);
        DocumentReference workerAltRef = db.collection("Workers").document(workerId);

        DocumentReference attendanceRef = db.collection("attendance").document(attendanceId);
        DocumentReference attendanceAltRef = db.collection("Attendance").document(attendanceId);

        String txnId = "TXN_" + UUID.randomUUID().toString();
        DocumentReference txnRef = db.collection("wallet_transactions").document(txnId);

        ApiFuture<String> futureTransaction = db.runTransaction(transaction -> {
            DocumentSnapshot recruiterSnap = transaction.get(recruiterRef).get();
            DocumentSnapshot recruiterAltSnap = transaction.get(recruiterAltRef).get();

            DocumentSnapshot workerSnap = transaction.get(workerRef).get();
            DocumentSnapshot workerAltSnap = transaction.get(workerAltRef).get();

            DocumentSnapshot attendanceSnap = transaction.get(attendanceRef).get();
            DocumentSnapshot attendanceAltSnap = transaction.get(attendanceAltRef).get();

            // 1. Prevent double payment for already marked attendance
            boolean isPresent = (attendanceSnap.exists() && "PRESENT".equalsIgnoreCase(attendanceSnap.getString("status"))) ||
                                (attendanceAltSnap.exists() && "PRESENT".equalsIgnoreCase(attendanceAltSnap.getString("status")));
            if (isPresent) {
                throw new IllegalStateException("Attendance already marked and paid for this record.");
            }

            // 2. Validate recruiter balance (check both documents or SessionManager)
            Double recruiterBalance = null;
            if (recruiterSnap.exists() && recruiterSnap.contains("walletBalance")) {
                recruiterBalance = recruiterSnap.getDouble("walletBalance");
            } else if (recruiterAltSnap.exists() && recruiterAltSnap.contains("walletBalance")) {
                recruiterBalance = recruiterAltSnap.getDouble("walletBalance");
            } else if (com.dihadi.view.SessionManager.currentRecruiter != null &&
                       recruiterId.equals(com.dihadi.view.SessionManager.getCurrentRecruiterId())) {
                recruiterBalance = com.dihadi.view.SessionManager.currentRecruiter.getWalletBalance();
            }

            if (recruiterBalance == null || recruiterBalance < wageAmount) {
                double currentBal = recruiterBalance == null ? 0.0 : recruiterBalance;
                throw new RuntimeException(new InsufficientBalanceException(
                        String.format("Insufficient recruiter wallet balance. Required: ₹%.2f, Available: ₹%.2f", wageAmount, currentBal)
                ));
            }

            // 3. Worker current balance
            Double workerBalance = 0.0;
            if (workerSnap.exists() && workerSnap.contains("walletBalance")) {
                workerBalance = workerSnap.getDouble("walletBalance");
            } else if (workerAltSnap.exists() && workerAltSnap.contains("walletBalance")) {
                workerBalance = workerAltSnap.getDouble("walletBalance");
            }
            if (workerBalance == null) {
                workerBalance = 0.0;
            }

            // 4. Calculate new balances
            double updatedRecruiterBalance = recruiterBalance - wageAmount;
            double updatedWorkerBalance = workerBalance + wageAmount;

            // 5. Atomic balance updates with updatedAt timestamp on both casing variants
            Map<String, Object> recruiterUpdates = new HashMap<>();
            recruiterUpdates.put("recruiterId", recruiterId);
            recruiterUpdates.put("walletBalance", updatedRecruiterBalance);
            recruiterUpdates.put("updatedAt", FieldValue.serverTimestamp());
            transaction.set(recruiterRef, recruiterUpdates, SetOptions.merge());
            transaction.set(recruiterAltRef, recruiterUpdates, SetOptions.merge());

            Map<String, Object> workerUpdates = new HashMap<>();
            workerUpdates.put("workerId", workerId);
            workerUpdates.put("walletBalance", updatedWorkerBalance);
            workerUpdates.put("updatedAt", FieldValue.serverTimestamp());
            transaction.set(workerRef, workerUpdates, SetOptions.merge());
            transaction.set(workerAltRef, workerUpdates, SetOptions.merge());

            // 6. Record audit ledger entry
            Map<String, Object> txnData = new HashMap<>();
            txnData.put("transactionId", txnId);
            txnData.put("recruiterId", recruiterId);
            txnData.put("workerId", workerId);
            txnData.put("amount", wageAmount);
            txnData.put("type", "DAILY_WAGE_PAYMENT");
            txnData.put("attendanceId", attendanceId);
            txnData.put("projectId", projectId);
            txnData.put("timestamp", FieldValue.serverTimestamp());
            txnData.put("status", "COMPLETED");
            transaction.set(txnRef, txnData);

            // 7. Update attendance document
            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("attendanceId", attendanceId);
            attendanceData.put("projectId", projectId);
            attendanceData.put("recruiterId", recruiterId);
            attendanceData.put("workerId", workerId);
            attendanceData.put("status", "PRESENT");
            attendanceData.put("transactionId", txnId);
            attendanceData.put("paidAmount", wageAmount);
            attendanceData.put("markedAt", FieldValue.serverTimestamp());
            attendanceData.put("timestamp", FieldValue.serverTimestamp());
            transaction.set(attendanceRef, attendanceData, SetOptions.merge());
            transaction.set(attendanceAltRef, attendanceData, SetOptions.merge());

            return txnId;
        });

        try {
            String resultTxn = futureTransaction.get();
            // Sync SessionManager recruiter balance in memory
            if (com.dihadi.view.SessionManager.currentRecruiter != null &&
                recruiterId.equals(com.dihadi.view.SessionManager.getCurrentRecruiterId())) {
                double current = com.dihadi.view.SessionManager.currentRecruiter.getWalletBalance();
                com.dihadi.view.SessionManager.currentRecruiter.setWalletBalance(Math.max(0, current - wageAmount));
            }
            return resultTxn;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof InsufficientBalanceException) {
                    throw (InsufficientBalanceException) cause;
                }
                cause = cause.getCause();
            }
            throw e;
        }
    }
}