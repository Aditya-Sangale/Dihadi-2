package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.JobApplication;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobApplicationDao {
    private Firestore db = FirebaseConfig.getFirestore();
    private static final Map<String, JobApplication> LOCAL_APP_MAP = new ConcurrentHashMap<>();

    public void saveApplication(JobApplication application) {
        if (application == null) return;
        try {
            if (application.getApplicationId() == null || application.getApplicationId().isBlank()) {
                application.setApplicationId(String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)));
            }

            // Check if application for same worker and project already exists in cache or DB
            if (application.getWorkerMobile() != null && application.getProjectId() != null) {
                for (JobApplication existing : LOCAL_APP_MAP.values()) {
                    if (application.getWorkerMobile().equals(existing.getWorkerMobile()) &&
                        application.getProjectId().equals(existing.getProjectId())) {
                        application.setApplicationId(existing.getApplicationId());
                        break;
                    }
                }
            }

            LOCAL_APP_MAP.put(application.getApplicationId(), application);

            db.collection("JobApplications")
                    .document(application.getApplicationId())
                    .set(application);
            System.out.println("Job Application Saved: " + application.getApplicationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateWorkerApplicationsForProject(String workerMobile, String projectId, String newStatus) {
        if (workerMobile == null || projectId == null) return;
        String digits = workerMobile.replaceAll("\\D", "");
        for (JobApplication app : new ArrayList<>(LOCAL_APP_MAP.values())) {
            String appDigits = app.getWorkerMobile() != null ? app.getWorkerMobile().replaceAll("\\D", "") : "";
            if (projectId.equals(app.getProjectId()) && (workerMobile.equals(app.getWorkerMobile()) || (!digits.isEmpty() && digits.equals(appDigits)))) {
                app.setStatus(newStatus);
                LOCAL_APP_MAP.put(app.getApplicationId(), app);
                try {
                    db.collection("JobApplications").document(app.getApplicationId()).update("status", newStatus);
                } catch (Exception ignored) {}
            }
        }
    }

    public void deleteApplication(String applicationId) {
        if (applicationId == null) return;
        LOCAL_APP_MAP.remove(applicationId);
        try {
            db.collection("JobApplications")
                    .document(applicationId)
                    .delete();
            System.out.println("Job Application Withdrawn: " + applicationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JobApplication> getApplicationsByWorker(String workerMobile) {
        return getApplicationsForWorker(workerMobile, null, null);
    }

    public List<JobApplication> getApplicationsForWorker(String workerMobile, String workerName, String workerType) {
        List<JobApplication> applications = new ArrayList<>();
        String cleanMob = workerMobile != null ? workerMobile.replaceAll("[\\s\\-\\(\\)]", "") : "";
        String digits = cleanMob.replaceAll("\\D", "");
        String last10 = digits.length() >= 10 ? digits.substring(digits.length() - 10) : digits;
        String cleanName = (workerName != null && !workerName.isBlank()) ? workerName.trim().toLowerCase() : "";

        for (JobApplication app : LOCAL_APP_MAP.values()) {
            boolean matches = false;
            if (app.getWorkerMobile() != null && !cleanMob.isEmpty()) {
                String aMob = app.getWorkerMobile().replaceAll("[\\s\\-\\(\\)]", "");
                String aDigits = aMob.replaceAll("\\D", "");
                String aLast10 = aDigits.length() >= 10 ? aDigits.substring(aDigits.length() - 10) : aDigits;
                if (cleanMob.equals(aMob) || (!digits.isEmpty() && !aDigits.isEmpty() && (digits.equals(aDigits) || digits.endsWith(aDigits) || aDigits.endsWith(digits)))
                        || (!last10.isEmpty() && !aLast10.isEmpty() && last10.equals(aLast10))) {
                    matches = true;
                }
            }

            if (!matches && !cleanName.isEmpty() && app.getWorkerName() != null && !app.getWorkerName().isBlank()) {
                String aName = app.getWorkerName().trim().toLowerCase();
                if (aName.equals(cleanName) || aName.contains(cleanName) || cleanName.contains(aName)) {
                    matches = true;
                }
            }

            if (matches) {
                applications.add(app);
            }
        }

        try {
            if (workerMobile != null && !workerMobile.isBlank()) {
                ApiFuture<QuerySnapshot> future = db.collection("JobApplications")
                        .whereEqualTo("workerMobile", workerMobile)
                        .get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    JobApplication app = document.toObject(JobApplication.class);
                    if (app != null) {
                        if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                            app.setApplicationId(document.getId());
                        }
                        LOCAL_APP_MAP.put(app.getApplicationId(), app);
                        if (applications.stream().noneMatch(a -> app.getApplicationId().equals(a.getApplicationId()))) {
                            applications.add(app);
                        }
                    }
                }

                if (!last10.isEmpty() && !last10.equals(workerMobile)) {
                    ApiFuture<QuerySnapshot> future10 = db.collection("JobApplications")
                            .whereEqualTo("workerMobile", last10)
                            .get();
                    for (QueryDocumentSnapshot doc : future10.get().getDocuments()) {
                        JobApplication app = doc.toObject(JobApplication.class);
                        if (app != null) {
                            if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                                app.setApplicationId(doc.getId());
                            }
                            LOCAL_APP_MAP.put(app.getApplicationId(), app);
                            if (applications.stream().noneMatch(a -> app.getApplicationId().equals(a.getApplicationId()))) {
                                applications.add(app);
                            }
                        }
                    }

                    String plus91 = "+91" + last10;
                    if (!plus91.equals(workerMobile)) {
                        try {
                            ApiFuture<QuerySnapshot> future91 = db.collection("JobApplications")
                                    .whereEqualTo("workerMobile", plus91)
                                    .get();
                            for (QueryDocumentSnapshot doc : future91.get().getDocuments()) {
                                JobApplication app = doc.toObject(JobApplication.class);
                                if (app != null) {
                                    if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                                        app.setApplicationId(doc.getId());
                                    }
                                    LOCAL_APP_MAP.put(app.getApplicationId(), app);
                                    if (applications.stream().noneMatch(a -> app.getApplicationId().equals(a.getApplicationId()))) {
                                        applications.add(app);
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }

            if (workerName != null && !workerName.isBlank()) {
                try {
                    ApiFuture<QuerySnapshot> futureName = db.collection("JobApplications")
                            .whereEqualTo("workerName", workerName.trim())
                            .get();
                    for (QueryDocumentSnapshot doc : futureName.get().getDocuments()) {
                        JobApplication app = doc.toObject(JobApplication.class);
                        if (app != null) {
                            if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                                app.setApplicationId(doc.getId());
                            }
                            LOCAL_APP_MAP.put(app.getApplicationId(), app);
                            if (applications.stream().noneMatch(a -> app.getApplicationId().equals(a.getApplicationId()))) {
                                applications.add(app);
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }

            applications.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<JobApplication> getApplicationsByRecruiter(String recruiterMobile) {
        List<JobApplication> applications = new ArrayList<>();
        if (recruiterMobile == null || recruiterMobile.isBlank()) return applications;
        String cleanMob = recruiterMobile.replaceAll("[\\s\\-\\(\\)]", "");
        String digits = cleanMob.replaceAll("\\D", "");

        for (JobApplication app : LOCAL_APP_MAP.values()) {
            if (app.getRecruiterMobile() != null) {
                String aMob = app.getRecruiterMobile().replaceAll("[\\s\\-\\(\\)]", "");
                String aDigits = aMob.replaceAll("\\D", "");
                if (cleanMob.equals(aMob) || (!digits.isEmpty() && digits.equals(aDigits))) {
                    applications.add(app);
                }
            }
        }

        try {
            ApiFuture<QuerySnapshot> future = db.collection("JobApplications")
                    .whereEqualTo("recruiterMobile", recruiterMobile)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                JobApplication app = document.toObject(JobApplication.class);
                if (app != null) {
                    if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                        app.setApplicationId(document.getId());
                    }
                    LOCAL_APP_MAP.put(app.getApplicationId(), app);
                    if (applications.stream().noneMatch(a -> app.getApplicationId().equals(a.getApplicationId()))) {
                        applications.add(app);
                    }
                }
            }

            applications.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<JobApplication> getAllApplications() {
        List<JobApplication> applications = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("JobApplications").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                JobApplication app = document.toObject(JobApplication.class);
                if (app != null) {
                    if (app.getApplicationId() == null || app.getApplicationId().isBlank()) {
                        app.setApplicationId(document.getId());
                    }
                    LOCAL_APP_MAP.put(app.getApplicationId(), app);
                    applications.add(app);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, JobApplication> entry : LOCAL_APP_MAP.entrySet()) {
            if (applications.stream().noneMatch(a -> entry.getKey().equals(a.getApplicationId()))) {
                applications.add(entry.getValue());
            }
        }
        return applications;
    }

    public void updateProjectApplicationsStatus(String projectId, String newStatus) {
        if (projectId == null || projectId.isBlank()) return;
        for (JobApplication app : new ArrayList<>(LOCAL_APP_MAP.values())) {
            if (projectId.equals(app.getProjectId())) {
                app.setStatus(newStatus);
                LOCAL_APP_MAP.put(app.getApplicationId(), app);
                try {
                    db.collection("JobApplications").document(app.getApplicationId()).update("status", newStatus);
                } catch (Exception ignored) {}
            }
        }
        try {
            ApiFuture<QuerySnapshot> future = db.collection("JobApplications")
                    .whereEqualTo("projectId", projectId)
                    .get();
            for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                doc.getReference().update("status", newStatus);
            }
        } catch (Exception ignored) {}
    }

    public JobApplication getActiveAssignedApplicationForWorker(String workerMobile) {
        if (workerMobile == null || workerMobile.isBlank()) return null;
        List<JobApplication> apps = getApplicationsByWorker(workerMobile);
        if (apps == null || apps.isEmpty()) return null;

        ProjectDao projectDao = new ProjectDao();
        for (JobApplication app : apps) {
            if ("Accepted".equalsIgnoreCase(app.getStatus())) {
                String pId = app.getProjectId();
                if (pId != null && !pId.isBlank()) {
                    try {
                        com.dihadi.model.Project p = projectDao.getProject(pId);
                        if (p != null && "Completed".equalsIgnoreCase(p.getStatus())) {
                            continue;
                        }
                    } catch (Exception ignored) {}
                }
                return app;
            }
        }
        return null;
    }
}
