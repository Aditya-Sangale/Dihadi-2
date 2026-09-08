package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Attendance;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttendanceDao {
    private Firestore db = FirebaseConfig.getFirestore();
    private static final Map<String, Attendance> LOCAL_ATTENDANCE_MAP = new ConcurrentHashMap<>();

    static {
        try {
            List<Attendance> cached = LocalCacheManager.loadAttendances();
            for (Attendance a : cached) {
                if (a != null && a.getAttendanceId() != null && !a.getAttendanceId().isBlank()) {
                    LOCAL_ATTENDANCE_MAP.put(a.getAttendanceId(), a);
                }
            }
        } catch (Exception ignored) {}
    }

    public void saveAttendance(Attendance attendance) {
        if (attendance == null) return;
        try {
            if (attendance.getAttendanceId() == null || attendance.getAttendanceId().isBlank()) {
                attendance.setAttendanceId(String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)));
            }

            // Check if existing attendance record exists for the same worker, project, and date
            if (attendance.getWorkerMobile() != null && attendance.getDate() != null) {
                for (Attendance att : LOCAL_ATTENDANCE_MAP.values()) {
                    if (attendance.getDate().equals(att.getDate()) && 
                        attendance.getWorkerMobile().equals(att.getWorkerMobile()) &&
                        (attendance.getProjectId() == null || attendance.getProjectId().equals(att.getProjectId()))) {
                        attendance.setAttendanceId(att.getAttendanceId());
                        break;
                    }
                }
            }

            LOCAL_ATTENDANCE_MAP.put(attendance.getAttendanceId(), attendance);
            LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());

            if (!LocalCacheManager.isQuotaExhausted()) {
                try {
                    db.collection("Attendance")
                            .document(attendance.getAttendanceId())
                            .set(attendance);
                    db.collection("attendance")
                            .document(attendance.getAttendanceId())
                            .set(attendance);
                    System.out.println("Attendance Synchronously Saved: " + attendance.getAttendanceId() + " [" + attendance.getStatus() + "]");
                } catch (Exception e) {
                    if (LocalCacheManager.isQuotaExhaustedException(e)) {
                        LocalCacheManager.markQuotaExhausted();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Attendance> getAttendanceByProject(String projectId) {
        List<Attendance> attendances = new ArrayList<>();
        for (Attendance att : LOCAL_ATTENDANCE_MAP.values()) {
            if (projectId == null || projectId.equals(att.getProjectId())) {
                attendances.add(att);
            }
        }
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                ApiFuture<QuerySnapshot> future = db.collection("Attendance").get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (QueryDocumentSnapshot document : documents) {
                    Attendance att = document.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null || att.getAttendanceId().isBlank()) att.setAttendanceId(document.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);
                        if ((projectId == null || projectId.equals(att.getProjectId())) && attendances.stream().noneMatch(a -> att.getAttendanceId().equals(a.getAttendanceId()))) {
                            attendances.add(att);
                        }
                    }
                }
                LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                }
            }
        }
        return attendances;
    }
    
    public List<Attendance> getAttendanceByWorker(String workerMobile) {
        List<Attendance> attendances = new ArrayList<>();
        if (workerMobile == null || workerMobile.isBlank()) return attendances;
        String cleanMob = workerMobile.replaceAll("[\\s\\-\\(\\)]", "");
        String digits = cleanMob.replaceAll("\\D", "");

        for (Attendance att : LOCAL_ATTENDANCE_MAP.values()) {
            if (isWorkerMatch(att, cleanMob, digits)) {
                attendances.add(att);
            }
        }

        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                // 1. Query "attendance" collection
                ApiFuture<QuerySnapshot> future1 = db.collection("attendance").get();
                for (QueryDocumentSnapshot document : future1.get().getDocuments()) {
                    Attendance att = document.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null || att.getAttendanceId().isBlank()) att.setAttendanceId(document.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);

                        if (isWorkerMatch(att, cleanMob, digits) && attendances.stream().noneMatch(a -> att.getAttendanceId().equals(a.getAttendanceId()))) {
                            attendances.add(att);
                        }
                    }
                }

                // 2. Query "Attendance" collection
                ApiFuture<QuerySnapshot> future2 = db.collection("Attendance").get();
                for (QueryDocumentSnapshot document : future2.get().getDocuments()) {
                    Attendance att = document.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null || att.getAttendanceId().isBlank()) att.setAttendanceId(document.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);

                        if (isWorkerMatch(att, cleanMob, digits) && attendances.stream().noneMatch(a -> att.getAttendanceId().equals(a.getAttendanceId()))) {
                            attendances.add(att);
                        }
                    }
                }
                LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                }
            }
        }
        return attendances;
    }

    private boolean isWorkerMatch(Attendance att, String cleanMob, String digits) {
        if (att == null) return false;
        if (att.getWorkerId() != null) {
            String wId = att.getWorkerId().replaceAll("[\\s\\-\\(\\)]", "");
            if (cleanMob.equalsIgnoreCase(wId) || (!cleanMob.isEmpty() && wId.contains(cleanMob))) {
                return true;
            }
        }
        if (att.getWorkerMobile() != null) {
            String aMob = att.getWorkerMobile().replaceAll("[\\s\\-\\(\\)]", "");
            String aDigits = aMob.replaceAll("\\D", "");
            if (cleanMob.equalsIgnoreCase(aMob) || (!digits.isEmpty() && digits.equals(aDigits))
                    || (digits.length() >= 10 && aDigits.endsWith(digits.substring(digits.length() - 10)))
                    || (aDigits.length() >= 10 && digits.endsWith(aDigits.substring(aDigits.length() - 10)))) {
                return true;
            }
        }
        if (att.getAttendanceId() != null) {
            if (!cleanMob.isEmpty() && att.getAttendanceId().contains(cleanMob)) return true;
            if (!digits.isEmpty() && digits.length() >= 10 && att.getAttendanceId().contains(digits.substring(digits.length() - 10))) return true;
        }
        return false;
    }

    public Attendance getAttendanceRecord(String attendanceId) {
        if (attendanceId == null || attendanceId.isBlank()) return null;
        if (LOCAL_ATTENDANCE_MAP.containsKey(attendanceId)) {
            return LOCAL_ATTENDANCE_MAP.get(attendanceId);
        }
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                // First try "attendance" collection
                var docSnap = db.collection("attendance").document(attendanceId).get().get();
                if (docSnap.exists()) {
                    Attendance att = docSnap.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null) att.setAttendanceId(docSnap.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);
                        LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());
                        return att;
                    }
                }
                // Fallback to "Attendance" collection
                var docSnap2 = db.collection("Attendance").document(attendanceId).get().get();
                if (docSnap2.exists()) {
                    Attendance att = docSnap2.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null) att.setAttendanceId(docSnap2.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);
                        LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());
                        return att;
                    }
                }
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                }
            }
        }
        return null;
    }

    public Attendance getAttendance(String attendanceId) {
        return getAttendanceRecord(attendanceId);
    }

    public List<Attendance> getAttendanceByRecruiter(String recruiterId) {
        List<Attendance> attendances = new ArrayList<>();
        if (recruiterId == null || recruiterId.isBlank()) return attendances;
        for (Attendance att : LOCAL_ATTENDANCE_MAP.values()) {
            if (recruiterId.equals(att.getRecruiterId())) {
                attendances.add(att);
            }
        }
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                ApiFuture<QuerySnapshot> future = db.collection("attendance").whereEqualTo("recruiterId", recruiterId).get();
                for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                    Attendance att = document.toObject(Attendance.class);
                    if (att != null) {
                        if (att.getAttendanceId() == null) att.setAttendanceId(document.getId());
                        LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);
                        if (attendances.stream().noneMatch(a -> att.getAttendanceId().equals(a.getAttendanceId()))) {
                            attendances.add(att);
                        }
                    }
                }
                LocalCacheManager.saveAttendances(LOCAL_ATTENDANCE_MAP.values());
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                }
            }
        }
        return attendances;
    }
}

