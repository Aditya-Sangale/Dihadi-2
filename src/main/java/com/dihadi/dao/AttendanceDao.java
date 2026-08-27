package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Attendance;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveAttendance(Attendance attendance) {
        try {
            db.collection("Attendance")
                    .document(attendance.getAttendanceId())
                    .set(attendance);
            System.out.println("Attendance Saved: " + attendance.getAttendanceId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Attendance> getAttendanceByProject(String projectId) {
        List<Attendance> attendances = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Attendance")
                    .whereEqualTo("projectId", projectId)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                attendances.add(document.toObject(Attendance.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
    
    public List<Attendance> getAttendanceByWorker(String workerMobile) {
        List<Attendance> attendances = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Attendance")
                    .whereEqualTo("workerMobile", workerMobile)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                attendances.add(document.toObject(Attendance.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendances;
    }
}
