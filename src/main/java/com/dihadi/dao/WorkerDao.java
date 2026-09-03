package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Worker;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class WorkerDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveWorker(Worker worker) {
        try {
            db.collection("Workers")
                    .document(worker.getMobileNumber())
                    .set(worker);

            System.out.println("Worker Data Inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Worker getWorker(String mobileNumber) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("Workers")
                    .document(mobileNumber).get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Worker.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Worker getWorkerByEmailOrMobile(String identifier) {
        try {
            // First try by mobile number (document id)
            String cleaned = identifier.replaceAll("[\\s\\-\\(\\)]", "");
            String phoneNumber = null;
            if (cleaned.startsWith("+91") && cleaned.length() == 13 && cleaned.substring(3).matches("\\d{10}")) {
                phoneNumber = cleaned;
            } else if (cleaned.startsWith("91") && cleaned.length() == 12 && cleaned.matches("\\d{12}")) {
                phoneNumber = "+" + cleaned;
            } else if (cleaned.length() == 10 && cleaned.matches("\\d{10}")) {
                phoneNumber = "+91" + cleaned;
            } else if (cleaned.startsWith("+") && cleaned.length() >= 8 && cleaned.substring(1).matches("\\d+")) {
                phoneNumber = cleaned;
            }

            if (phoneNumber != null) {
                Worker w = getWorker(phoneNumber);
                if (w != null) return w;
                
                // fallback to try original string just in case
                w = getWorker(identifier);
                if (w != null) return w;
            } else {
                Worker w = getWorker(identifier);
                if (w != null) return w;
            }

            // Then try by email
            ApiFuture<QuerySnapshot> future = db.collection("Workers").whereEqualTo("email", identifier).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                return snapshot.getDocuments().get(0).toObject(Worker.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateWorker(Worker worker) {
        try {
            db.collection("Workers")
                    .document(worker.getMobileNumber())
                    .update("firstName", worker.getFirstName(),
                            "middleName", worker.getMiddleName(),
                            "lastName", worker.getLastName(),
                            "alternateMobile", worker.getAlternateMobile(),
                            "email", worker.getEmail(),
                            "gender", worker.getGender(),
                            "dateOfBirth", worker.getDateOfBirth(),
                            "education", worker.getEducation(),
                            "experience", worker.getExperience(),
                            "dailyWage", worker.getDailyWage(),
                            "profilePhotoUrl", worker.getProfilePhotoUrl());

            System.out.println("Worker Data Updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWorker(String mobileNumber) {
        try {
            db.collection("Workers")
                    .document(mobileNumber).delete();
            System.out.println("Worker Data Deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Worker> getAllWorkers() {
        List<Worker> list = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = db.collection("Workers").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Worker worker = doc.toObject(Worker.class);
                list.add(worker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Worker> getWorkersByProjectId(String projectId) {
        List<Worker> list = new ArrayList<>();
        if (projectId == null || projectId.isBlank()) {
            return getAllWorkers();
        }

        try {
            // Find all accepted applications for this project
            JobApplicationDao jobAppDao = new JobApplicationDao();
            List<com.dihadi.model.JobApplication> allApps = jobAppDao.getAllApplications();
            
            java.util.Set<String> workerMobiles = new java.util.HashSet<>();
            if (allApps != null) {
                for (com.dihadi.model.JobApplication app : allApps) {
                    if (projectId.equals(app.getProjectId()) && "Accepted".equalsIgnoreCase(app.getStatus())) {
                        if (app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank()) {
                            workerMobiles.add(app.getWorkerMobile());
                        }
                    }
                }
                // If no accepted applications, check all applications for this project
                if (workerMobiles.isEmpty()) {
                    for (com.dihadi.model.JobApplication app : allApps) {
                        if (projectId.equals(app.getProjectId())) {
                            if (app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank()) {
                                workerMobiles.add(app.getWorkerMobile());
                            }
                        }
                    }
                }
            }

            for (String mobile : workerMobiles) {
                Worker w = getWorkerByEmailOrMobile(mobile);
                if (w != null && list.stream().noneMatch(existing -> mobile.equals(existing.getMobileNumber()))) {
                    list.add(w);
                } else if (w == null) {
                    // Create minimal worker from mobile
                    Worker fallback = new Worker();
                    fallback.setMobileNumber(mobile);
                    fallback.setFirstName("Worker (" + mobile + ")");
                    fallback.setDailyWage(500);
                    list.add(fallback);
                }
            }

            // If still empty, return all workers as fallback for testing
            if (list.isEmpty()) {
                list = getAllWorkers();
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = getAllWorkers();
        }

        return list;
    }

    public void updateWalletBalance(String mobileNumberOrId, double newBalance) {
        try {
            db.collection("Workers")
                    .document(mobileNumberOrId)
                    .update("walletBalance", newBalance);
            System.out.println("Worker wallet balance updated for " + mobileNumberOrId);
        } catch (Exception e) {
            try {
                db.collection("workers")
                        .document(mobileNumberOrId)
                        .update("walletBalance", newBalance);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
