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

            db.collection("Attendance")
                    .document(attendance.getAttendanceId())
                    .set(attendance);
            System.out.println("Attendance Synchronously Saved: " + attendance.getAttendanceId() + " [" + attendance.getStatus() + "]");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
    
    public List<Attendance> getAttendanceByWorker(String workerMobile) {
        List<Attendance> attendances = new ArrayList<>();
        if (workerMobile == null || workerMobile.isBlank()) return attendances;
        String cleanMob = workerMobile.replaceAll("[\\s\\-\\(\\)]", "");
        String digits = cleanMob.replaceAll("\\D", "");

        for (Attendance att : LOCAL_ATTENDANCE_MAP.values()) {
            if (att.getWorkerMobile() != null) {
                String aMob = att.getWorkerMobile().replaceAll("[\\s\\-\\(\\)]", "");
                String aDigits = aMob.replaceAll("\\D", "");
                if (cleanMob.equals(aMob) || (!digits.isEmpty() && digits.equals(aDigits))) {
                    attendances.add(att);
                }
            }
        }

        try {
            ApiFuture<QuerySnapshot> future = db.collection("Attendance").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Attendance att = document.toObject(Attendance.class);
                if (att != null && att.getWorkerMobile() != null) {
                    if (att.getAttendanceId() == null || att.getAttendanceId().isBlank()) att.setAttendanceId(document.getId());
                    LOCAL_ATTENDANCE_MAP.put(att.getAttendanceId(), att);
                    String aMob = att.getWorkerMobile().replaceAll("[\\s\\-\\(\\)]", "");
                    String aDigits = aMob.replaceAll("\\D", "");
                    if (cleanMob.equals(aMob) || (digits.length() >= 10 && aDigits.endsWith(digits.substring(digits.length() - 10))) || (aDigits.length() >= 10 && digits.endsWith(aDigits.substring(aDigits.length() - 10)))) {
                        if (attendances.stream().noneMatch(a -> att.getAttendanceId().equals(a.getAttendanceId()))) {
                            attendances.add(att);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
}

