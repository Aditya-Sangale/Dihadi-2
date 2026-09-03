package com.dihadi.controller;

import com.dihadi.config.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WorkerDashboardController {

    private final Firestore db;

    public WorkerDashboardController() {
        this.db = FirebaseConfig.getFirestore();
    }

    public static class WorkerDashboardData {
        public double walletBalance = 0.0;
        public int totalDaysWorked = 0;
        public List<Map<String, Object>> recentTransactions = new ArrayList<>();
        public List<Map<String, Object>> attendanceHistory = new ArrayList<>();
    }

    /**
     * Loads live metrics and history for the authenticated worker.
     */
    public void loadDashboardData(String workerId, Consumer<WorkerDashboardData> onLoaded, Consumer<String> onError) {
        Task<WorkerDashboardData> task = new Task<>() {
            @Override
            protected WorkerDashboardData call() throws Exception {
                WorkerDashboardData data = new WorkerDashboardData();
                if (workerId == null || workerId.isBlank()) {
                    return data;
                }

                String cleanId = workerId.trim();
                String cleanMob = cleanId.replaceAll("[\\s\\-\\(\\)]", "");

                // 1. Fetch Worker Profile for live Wallet Balance and Days Worked
                DocumentSnapshot workerDoc = null;
                try {
                    workerDoc = db.collection("workers").document(cleanId).get().get();
                    if (!workerDoc.exists()) {
                        workerDoc = db.collection("Workers").document(cleanId).get().get();
                    }
                    if (!workerDoc.exists() && !cleanMob.equals(cleanId)) {
                        workerDoc = db.collection("Workers").document(cleanMob).get().get();
                    }
                } catch (Exception ignored) {}

                if (workerDoc != null && workerDoc.exists()) {
                    Double bal = workerDoc.getDouble("walletBalance");
                    data.walletBalance = (bal != null) ? bal : 0.0;

                    Long days = workerDoc.getLong("totalDaysWorked");
                    data.totalDaysWorked = (days != null) ? days.intValue() : 0;
                }

                // 2. Fetch Attendance Records (Independent System)
                List<com.dihadi.model.Attendance> attList = new AttendanceController().getAttendanceByWorker(cleanId);
                if (attList != null) {
                    for (com.dihadi.model.Attendance att : attList) {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("attendanceId", att.getAttendanceId());
                        map.put("date", att.getDate());
                        map.put("status", att.getStatus() != null ? att.getStatus().toUpperCase() : "PRESENT");
                        map.put("projectId", att.getProjectId());
                        map.put("paymentStatus", att.getPaymentStatus() != null ? att.getPaymentStatus() : "PAID");
                        map.put("paymentTransactionId", att.getPaymentTransactionId() != null ? att.getPaymentTransactionId() : att.getTransactionId());
                        map.put("paidAmount", att.getPaidAmount());
                        data.attendanceHistory.add(map);
                    }
                }

                // Sort attendance by date descending
                data.attendanceHistory.sort((a, b) -> {
                    String d1 = (String) a.getOrDefault("date", "");
                    String d2 = (String) b.getOrDefault("date", "");
                    return d2.compareTo(d1);
                });

                // Compute / verify totalDaysWorked from PRESENT attendance records if 0 or uninitialized
                int presentCount = 0;
                for (Map<String, Object> att : data.attendanceHistory) {
                    if ("PRESENT".equalsIgnoreCase((String) att.get("status"))) {
                        presentCount++;
                    }
                }
                if (data.totalDaysWorked <= 0 || presentCount > data.totalDaysWorked) {
                    data.totalDaysWorked = presentCount;
                }

                // 3. Fetch Transaction History (Dedicated Financial Ledger)
                try {
                    QuerySnapshot txnSnap = db.collection("wallet_transactions")
                            .get().get();

                    for (QueryDocumentSnapshot doc : txnSnap) {
                        Map<String, Object> txnData = doc.getData();
                        String wId = (String) txnData.get("workerId");
                        if (wId != null && (wId.equalsIgnoreCase(cleanId) || wId.equalsIgnoreCase(cleanMob) || cleanId.contains(wId) || wId.contains(cleanId))) {
                            data.recentTransactions.add(txnData);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[WorkerDashboardController] Error querying wallet_transactions: " + e.getMessage());
                }

                // If walletBalance is still 0, aggregate from confirmed wallet_transactions
                if (data.walletBalance <= 0.0 && !data.recentTransactions.isEmpty()) {
                    double sum = 0.0;
                    for (Map<String, Object> txn : data.recentTransactions) {
                        if ("SUCCESS".equalsIgnoreCase((String) txn.get("status")) || "COMPLETED".equalsIgnoreCase((String) txn.get("status"))) {
                            Object amtObj = txn.get("amount");
                            if (amtObj instanceof Number) {
                                sum += ((Number) amtObj).doubleValue();
                            }
                        }
                    }
                    if (sum > 0) {
                        data.walletBalance = sum;
                    }
                }

                return data;
            }
        };

        task.setOnSucceeded(e -> {
            if (onLoaded != null) {
                onLoaded.accept(task.getValue());
            }
        });

        task.setOnFailed(e -> {
            if (onError != null) {
                onError.accept(task.getException() != null ? task.getException().getMessage() : "Error loading dashboard metrics.");
            }
        });

        new Thread(task).start();
    }
}
