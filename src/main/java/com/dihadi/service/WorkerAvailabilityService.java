package com.dihadi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dihadi.config.FirebaseConfig;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

/**
 * Tracks worker hire records, determines real-time availability for recruiters,
 * and automatically restores availability once the assigned workday completes.
 */
public class WorkerAvailabilityService {

    @IgnoreExtraProperties
    public static class WorkerHireRecord {
        private String workerMobile;
        private String workerName;
        private String projectId;
        private String projectName;
        private String recruiterMobile;
        private String hireDate; // YYYY-MM-DD
        private String status;   // "Active" or "Completed"

        public WorkerHireRecord() {}

        public WorkerHireRecord(String workerMobile, String workerName, String projectId,
                                String projectName, String recruiterMobile, String hireDate, String status) {
            this.workerMobile = workerMobile;
            this.workerName = workerName;
            this.projectId = projectId;
            this.projectName = projectName;
            this.recruiterMobile = recruiterMobile;
            this.hireDate = hireDate;
            this.status = status;
        }

        public String getWorkerMobile() { return workerMobile; }
        public void setWorkerMobile(String workerMobile) { this.workerMobile = workerMobile; }

        public String getWorkerName() { return workerName; }
        public void setWorkerName(String workerName) { this.workerName = workerName; }

        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getRecruiterMobile() { return recruiterMobile; }
        public void setRecruiterMobile(String recruiterMobile) { this.recruiterMobile = recruiterMobile; }

        public String getHireDate() { return hireDate; }
        public void setHireDate(String hireDate) { this.hireDate = hireDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class AvailabilityInfo {
        private final boolean available;
        private final boolean hiredByCurrentRecruiter;
        private final String statusText;
        private final String badgeText;
        private final String subtext;
        private final String buttonText;
        private final String projectName;
        private final String hireDate;

        public AvailabilityInfo(boolean available, boolean hiredByCurrentRecruiter,
                                String statusText, String badgeText,
                                String subtext, String buttonText,
                                String projectName, String hireDate) {
            this.available = available;
            this.hiredByCurrentRecruiter = hiredByCurrentRecruiter;
            this.statusText = statusText;
            this.badgeText = badgeText;
            this.subtext = subtext;
            this.buttonText = buttonText;
            this.projectName = projectName;
            this.hireDate = hireDate;
        }

        public boolean isAvailable() { return available; }
        public boolean isHiredByCurrentRecruiter() { return hiredByCurrentRecruiter; }
        public String getStatusText() { return statusText; }
        public String getBadgeText() { return badgeText; }
        public String getSubtext() { return subtext; }
        public String getButtonText() { return buttonText; }
        public String getProjectName() { return projectName; }
        public String getHireDate() { return hireDate; }
    }

    private static final Map<String, WorkerHireRecord> ACTIVE_HIRES = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    static {
        initFromStorage();
    }

    private static void initFromStorage() {
        if (initialized) return;
        new Thread(() -> {
            try {
                Firestore db = FirebaseConfig.getFirestore();
                if (db != null) {
                    ApiFuture<QuerySnapshot> future = db.collection("WorkerHires").whereEqualTo("status", "Active").get();
                    List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                    for (QueryDocumentSnapshot doc : docs) {
                        WorkerHireRecord rec = doc.toObject(WorkerHireRecord.class);
                        if (rec != null && rec.getWorkerMobile() != null) {
                            String key = normalizeKey(rec.getWorkerMobile(), rec.getWorkerName());
                            ACTIVE_HIRES.put(key, rec);
                        }
                    }
                }
                initialized = true;
            } catch (Exception e) {
                initialized = true;
            }
        }).start();
    }

    public static String normalizeKey(String mobile, String name) {
        if (mobile != null && !mobile.isBlank()) {
            String digits = mobile.replaceAll("\\D", "");
            if (digits.length() >= 10) {
                return digits.substring(digits.length() - 10);
            }
            if (!digits.isEmpty()) {
                return digits;
            }
        }
        if (name != null && !name.isBlank()) {
            return "NAME_" + name.trim().toLowerCase().replaceAll("\\s+", "_");
        }
        return "UNKNOWN";
    }

    /**
     * Marks a worker as hired for the current day on the given project.
     */
    public static void hireWorker(String workerMobile, String workerName, String projectId,
                                  String projectName, String recruiterMobile) {
        String today = LocalDate.now().toString();
        String projName = (projectName != null && !projectName.isBlank()) ? projectName.trim() : "Active Project";
        String projId = (projectId != null) ? projectId.trim() : "";
        String rMob = (recruiterMobile != null) ? recruiterMobile.trim() : "";

        WorkerHireRecord record = new WorkerHireRecord(
                workerMobile != null ? workerMobile.trim() : "",
                workerName != null ? workerName.trim() : "",
                projId,
                projName,
                rMob,
                today,
                "Active"
        );

        String primaryKey = normalizeKey(workerMobile, workerName);
        ACTIVE_HIRES.put(primaryKey, record);

        // If worker has a name, also key by name so searches by either match accurately
        if (workerName != null && !workerName.isBlank()) {
            String nameKey = "NAME_" + workerName.trim().toLowerCase().replaceAll("\\s+", "_");
            ACTIVE_HIRES.put(nameKey, record);
        }

        new Thread(() -> {
            try {
                Firestore db = FirebaseConfig.getFirestore();
                if (db != null) {
                    String docId = primaryKey + "_" + today;
                    db.collection("WorkerHires").document(docId).set(record);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Evaluates real-time availability for a worker:
     * - If actively hired for today:
     *     - Current hiring recruiter sees "Already hired for current project".
     *     - Other recruiters see "Unavailable, already assigned to other project".
     * - If hired for a previous day: that day has completed -> automatically returns to Available!
     */
    public static AvailabilityInfo getAvailability(String workerMobile, String workerName) {
        String currentRecruiterMob = null;
        if (com.dihadi.view.SessionManager.currentRecruiter != null) {
            currentRecruiterMob = com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber();
        }
        return getAvailability(workerMobile, workerName, currentRecruiterMob);
    }

    public static AvailabilityInfo getAvailability(String workerMobile, String workerName, String viewingRecruiterMobile) {
        String key = normalizeKey(workerMobile, workerName);
        WorkerHireRecord record = ACTIVE_HIRES.get(key);

        if (record == null && workerName != null && !workerName.isBlank()) {
            String nameKey = "NAME_" + workerName.trim().toLowerCase().replaceAll("\\s+", "_");
            record = ACTIVE_HIRES.get(nameKey);
        }

        if (record == null || !"Active".equalsIgnoreCase(record.getStatus())) {
            return new AvailabilityInfo(
                    true,
                    false,
                    "Available",
                    "•  Available for new projects",
                    "Ready for assignment",
                    "HIRE NOW",
                    "",
                    ""
            );
        }

        String hireDateStr = record.getHireDate();
        LocalDate today = LocalDate.now();

        if (hireDateStr != null && !hireDateStr.isBlank()) {
            try {
                LocalDate hireDate = LocalDate.parse(hireDateStr.trim());
                if (hireDate.isBefore(today)) {
                    // That workday has completed! Worker is now available again.
                    record.setStatus("Completed");
                    updateRecordStatusInDb(record);
                    return new AvailabilityInfo(
                            true,
                            false,
                            "Available",
                            "•  Available for new projects",
                            "Previous project completed",
                            "HIRE NOW",
                            "",
                            hireDateStr
                    );
                }
            } catch (Exception ignored) {
                // If parse fails, check if equal as string
                if (!today.toString().equals(hireDateStr)) {
                    record.setStatus("Completed");
                    return new AvailabilityInfo(
                            true,
                            false,
                            "Available",
                            "•  Available for new projects",
                            "Previous project completed",
                            "HIRE NOW",
                            "",
                            hireDateStr
                    );
                }
            }
        }

        // Currently hired for today!
        boolean hiredByCurrentRecruiter = false;
        if (viewingRecruiterMobile != null && !viewingRecruiterMobile.isBlank()
                && record.getRecruiterMobile() != null && !record.getRecruiterMobile().isBlank()) {
            String viewerDigits = viewingRecruiterMobile.replaceAll("\\D", "");
            String hireRecDigits = record.getRecruiterMobile().replaceAll("\\D", "");
            String viewerLast10 = viewerDigits.length() >= 10 ? viewerDigits.substring(viewerDigits.length() - 10) : viewerDigits;
            String hireLast10 = hireRecDigits.length() >= 10 ? hireRecDigits.substring(hireRecDigits.length() - 10) : hireRecDigits;

            if (viewingRecruiterMobile.trim().equalsIgnoreCase(record.getRecruiterMobile().trim())
                    || (!viewerLast10.isEmpty() && viewerLast10.equals(hireLast10))
                    || (!viewerDigits.isEmpty() && viewerDigits.equals(hireRecDigits))) {
                hiredByCurrentRecruiter = true;
            }
        }

        String proj = (record.getProjectName() != null && !record.getProjectName().isBlank())
                ? record.getProjectName()
                : "another project";

        if (hiredByCurrentRecruiter) {
            return new AvailabilityInfo(
                    false,
                    true,
                    "Already hired for current project",
                    "•  Already Hired",
                    "Already hired for current project",
                    "ALREADY HIRED",
                    proj,
                    hireDateStr
            );
        } else {
            return new AvailabilityInfo(
                    false,
                    false,
                    "Unavailable",
                    "•  Unavailable",
                    "Already assigned to other project",
                    "UNAVAILABLE",
                    proj,
                    hireDateStr
            );
        }
    }

    /**
     * Completes the work day for a worker, returning them to Available status immediately.
     */
    public static void completeDayWork(String workerMobile, String workerName) {
        String key = normalizeKey(workerMobile, workerName);
        WorkerHireRecord record = ACTIVE_HIRES.get(key);
        if (record != null) {
            record.setStatus("Completed");
            updateRecordStatusInDb(record);
        }
        if (workerName != null && !workerName.isBlank()) {
            String nameKey = "NAME_" + workerName.trim().toLowerCase().replaceAll("\\s+", "_");
            WorkerHireRecord nameRec = ACTIVE_HIRES.get(nameKey);
            if (nameRec != null) {
                nameRec.setStatus("Completed");
            }
        }
    }

    /**
     * Completes all worker hire records associated with a project when the project is marked completed.
     */
    public static void completeProjectHires(String projectId) {
        if (projectId == null || projectId.isBlank()) return;
        for (WorkerHireRecord record : ACTIVE_HIRES.values()) {
            if (projectId.equalsIgnoreCase(record.getProjectId())) {
                record.setStatus("Completed");
                updateRecordStatusInDb(record);
            }
        }
    }

    private static void updateRecordStatusInDb(WorkerHireRecord record) {
        new Thread(() -> {
            try {
                Firestore db = FirebaseConfig.getFirestore();
                if (db != null && record != null) {
                    String docId = normalizeKey(record.getWorkerMobile(), record.getWorkerName()) + "_" + record.getHireDate();
                    db.collection("WorkerHires").document(docId).update("status", "Completed");
                }
            } catch (Exception ignored) {}
        }).start();
    }
}
