package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Grievance;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class GrievanceDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveGrievance(Grievance grievance) {
        try {
            if (db == null) return;
            String docId = (grievance.getGrievanceId() != null && !grievance.getGrievanceId().isBlank())
                    ? grievance.getGrievanceId()
                    : "GR-" + System.currentTimeMillis();
            grievance.setGrievanceId(docId);
            db.collection("Grievances")
                    .document(docId)
                    .set(grievance);
            System.out.println("Grievance/Query inserted: " + docId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Grievance> getAllGrievances() {
        List<Grievance> list = new ArrayList<>();
        try {
            if (db == null) return list;
            ApiFuture<QuerySnapshot> future = db.collection("Grievances").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Grievance g = doc.toObject(Grievance.class);
                if (g != null) {
                    if (g.getGrievanceId() == null || g.getGrievanceId().isBlank()) {
                        g.setGrievanceId(doc.getId());
                    }
                    list.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteGrievance(String grievanceId) {
        try {
            if (db == null || grievanceId == null || grievanceId.isBlank()) return false;
            db.collection("Grievances").document(grievanceId).delete().get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(String grievanceId, String newStatus, String notes) {
        try {
            if (db == null || grievanceId == null || grievanceId.isBlank()) return false;
            db.collection("Grievances").document(grievanceId)
                    .update("status", newStatus, "resolutionNotes", notes != null ? notes : "Resolved by System Administrator").get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
