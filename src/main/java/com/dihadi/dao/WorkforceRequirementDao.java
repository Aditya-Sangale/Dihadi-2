package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.WorkforceRequirement;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class WorkforceRequirementDao {
    private Firestore db = FirebaseConfig.getFirestore();
    private static final Map<String, WorkforceRequirement> LOCAL_REQ_MAP = new ConcurrentHashMap<>();

    public void saveRequirement(WorkforceRequirement req) {
        if (req == null) return;
        try {
            String docId = (req.getProjectId() != null && !req.getProjectId().isBlank())
                    ? req.getProjectId()
                    : (req.getRequirementId() != null && !req.getRequirementId().isBlank()
                            ? req.getRequirementId()
                            : String.valueOf(System.currentTimeMillis()));
            if (req.getRequirementId() == null || req.getRequirementId().isBlank()) {
                req.setRequirementId(docId);
            }
            LOCAL_REQ_MAP.put(docId, req);

            db.collection("WorkforceRequirements")
                    .document(docId)
                    .set(req);
            System.out.println("Workforce Requirement Data Inserted: " + docId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<WorkforceRequirement> getRequirementsForProject(String projectIdOrMobile) {
        List<WorkforceRequirement> list = new ArrayList<>();
        if (projectIdOrMobile == null) return list;
        for (WorkforceRequirement req : LOCAL_REQ_MAP.values()) {
            if (projectIdOrMobile.equals(req.getProjectId()) || projectIdOrMobile.equals(req.getRequirementId())) {
                list.add(req);
            }
        }
        if (!list.isEmpty()) return list;

        try {
            List<WorkforceRequirement> all = getAllRequirements();
            if (all != null) {
                for (WorkforceRequirement req : all) {
                    if (projectIdOrMobile.equals(req.getProjectId()) || projectIdOrMobile.equals(req.getRequirementId())) {
                        list.add(req);
                    }
                }
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
                if (req != null) {
                    if (req.getRequirementId() == null || req.getRequirementId().isBlank()) {
                        req.setRequirementId(doc.getId());
                    }
                    if (req.getProjectId() == null || req.getProjectId().isBlank()) {
                        req.setProjectId(doc.getId());
                    }
                    LOCAL_REQ_MAP.put(req.getRequirementId(), req);
                    list.add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, WorkforceRequirement> entry : LOCAL_REQ_MAP.entrySet()) {
            boolean exists = list.stream().anyMatch(r -> entry.getKey().equals(r.getRequirementId()) || entry.getKey().equals(r.getProjectId()));
            if (!exists) {
                list.add(0, entry.getValue());
            }
        }
        return list;
    }
}
