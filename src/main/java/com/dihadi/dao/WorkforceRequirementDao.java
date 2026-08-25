package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.WorkforceRequirement;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class WorkforceRequirementDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveRequirement(WorkforceRequirement req) {
        try {
            db.collection("WorkforceRequirements")
                    .document(req.getRequirementId())
                    .set(req);
            System.out.println("Workforce Requirement Data Inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<WorkforceRequirement> getRequirementsForProject(String projectId) {
        List<WorkforceRequirement> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("WorkforceRequirements")
                    .whereEqualTo("projectId", projectId).get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                WorkforceRequirement req = doc.toObject(WorkforceRequirement.class);
                list.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<WorkforceRequirement> getAllRequirements() {
        List<WorkforceRequirement> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("WorkforceRequirements").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                WorkforceRequirement req = doc.toObject(WorkforceRequirement.class);
                list.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
