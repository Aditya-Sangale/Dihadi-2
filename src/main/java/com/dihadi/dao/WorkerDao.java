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
            if (worker.getLastLogin() == null || worker.getLastLogin().isBlank()) {
                worker.setLastLogin(com.dihadi.util.UserActivityUtil.getCurrentTimestamp());
            }
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

    public void updateLastLogin(String mobileNumber, String lastLogin) {
        try {
            db.collection("Workers")
                    .document(mobileNumber)
                    .update("lastLogin", lastLogin);
            System.out.println("Worker lastLogin updated for " + mobileNumber);
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
            return list;
        }

        try {
            // Find project to get title for fallback matching
            com.dihadi.dao.ProjectDao projectDao = new com.dihadi.dao.ProjectDao();
            com.dihadi.model.Project proj = projectDao.getProject(projectId);
            String projName = (proj != null && proj.getTitle() != null) ? proj.getTitle().trim().toLowerCase() : "";

            // Find all accepted applications for this project
            JobApplicationDao jobAppDao = new JobApplicationDao();
            List<com.dihadi.model.JobApplication> allApps = jobAppDao.getAllApplications();

            java.util.Map<String, com.dihadi.model.JobApplication> acceptedByWorker = new java.util.LinkedHashMap<>();
            if (allApps != null) {
                for (com.dihadi.model.JobApplication app : allApps) {
                    if ("Accepted".equalsIgnoreCase(app.getStatus())) {
                        boolean matchesProject = false;
                        String aProjId = app.getProjectId() != null ? app.getProjectId().trim() : "";
                        String aTitle = app.getJobTitle() != null ? app.getJobTitle().trim().toLowerCase() : "";

                        if (!aProjId.isEmpty() && projectId.equalsIgnoreCase(aProjId)) {
                            matchesProject = true;
                        } else if (!projName.isEmpty() && (aTitle.contains(projName) || projName.contains(aTitle))) {
                            matchesProject = true;
                        }

                        if (matchesProject && app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank()) {
                            String clean = app.getWorkerMobile().replaceAll("\\D", "");
                            String key = clean.length() >= 10 ? clean.substring(clean.length() - 10) : clean;
                            acceptedByWorker.putIfAbsent(key, app);
                        }
                    }
                }
            }

            for (com.dihadi.model.JobApplication app : acceptedByWorker.values()) {
                String mobile = app.getWorkerMobile();
                Worker w = getWorkerByEmailOrMobile(mobile);
                if (w != null) {
                    // Populate worker's name if missing in worker profile
                    if ((w.getName().equalsIgnoreCase("Worker") || (w.getFirstName() == null || w.getFirstName().isBlank()))
                            && app.getWorkerName() != null && !app.getWorkerName().isBlank()) {
                        w.setName(app.getWorkerName());
                    }
                    if (list.stream().noneMatch(existing -> (w.getId() != null && !w.getId().isEmpty() && w.getId().equals(existing.getId()))
                            || (mobile != null && mobile.equals(existing.getMobileNumber())))) {
                        list.add(w);
                    }
                } else {
                    // Create worker from accepted application details
                    Worker fallback = new Worker();
                    fallback.setMobileNumber(mobile);
                    fallback.setId(mobile);
                    if (app.getWorkerName() != null && !app.getWorkerName().isBlank()) {
                        fallback.setName(app.getWorkerName());
                    } else {
                        fallback.setFirstName("Worker (" + mobile + ")");
                    }
                    fallback.setWorkerType(app.getJobTitle() != null ? app.getJobTitle() : "General Labour");
                    try {
                        if (app.getJobWage() != null) {
                            fallback.setDailyWage((int) Double.parseDouble(app.getJobWage().replaceAll("[^0-9.]", "")));
                        } else {
                            fallback.setDailyWage(600);
                        }
                    } catch (Exception ignored) {
                        fallback.setDailyWage(600);
                    }
                    list.add(fallback);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
