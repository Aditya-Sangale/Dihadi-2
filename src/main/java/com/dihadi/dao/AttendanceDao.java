package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Attendance;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveAttendance(Attendance attendance) {
        try {
            if (attendance.getAttendanceId() == null || attendance.getAttendanceId().isBlank()) {
                attendance.setAttendanceId(String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)));
            }

            // Check if existing attendance record exists for the same worker, project, and date
            if (attendance.getWorkerMobile() != null && attendance.getDate() != null) {
                List<Attendance> existing = getAttendanceByWorker(attendance.getWorkerMobile());
                for (Attendance att : existing) {
                    if (attendance.getDate().equals(att.getDate()) && 
                        (attendance.getProjectId() == null || attendance.getProjectId().equals(att.getProjectId()))) {
                        attendance.setAttendanceId(att.getAttendanceId());
                        break;
                    }
                }
            }

            db.collection("Attendance")
                    .document(attendance.getAttendanceId())
                    .set(attendance)
                    .get();
            System.out.println("Attendance Synchronously Saved: " + attendance.getAttendanceId() + " [" + attendance.getStatus() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Attendance> getAttendanceByProject(String projectId) {
        List<Attendance> attendances = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Attendance").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Attendance att = document.toObject(Attendance.class);
                if (att != null && (projectId == null || projectId.equals(att.getProjectId()))) {
                    attendances.add(att);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
    
    public List<Attendance> getAttendanceByWorker(String workerMobile) {
        List<Attendance> attendances = new ArrayList<>();
        try {
            if (workerMobile == null || workerMobile.isBlank()) return attendances;
            String cleanMob = workerMobile.replaceAll("[\\s\\-\\(\\)]", "");
            String digits = cleanMob.replaceAll("\\D", "");

            ApiFuture<QuerySnapshot> future = db.collection("Attendance").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Attendance att = document.toObject(Attendance.class);
                if (att != null && att.getWorkerMobile() != null) {
                    String aMob = att.getWorkerMobile().replaceAll("[\\s\\-\\(\\)]", "");
                    String aDigits = aMob.replaceAll("\\D", "");
                    if (cleanMob.equals(aMob) || (digits.length() >= 10 && aDigits.endsWith(digits.substring(digits.length() - 10))) || (aDigits.length() >= 10 && digits.endsWith(aDigits.substring(aDigits.length() - 10)))) {
                        attendances.add(att);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
}

