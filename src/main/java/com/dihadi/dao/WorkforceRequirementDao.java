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
    private static volatile long lastReqsFetch = 0;
    private static final long CACHE_TTL_MS = 15000;

    static {
        try {
            List<WorkforceRequirement> disk = LocalCacheManager.loadRequirements();
            for (WorkforceRequirement r : disk) {
                if (r != null && r.getRequirementId() != null && !r.getRequirementId().isBlank()) {
                    LOCAL_REQ_MAP.put(r.getRequirementId(), r);
                }
            }
        } catch (Exception ignored) {}
    }

    public void saveRequirement(WorkforceRequirement req) {
        if (req == null) return;
        try {
            String docId = (req.getRequirementId() != null && !req.getRequirementId().isBlank())
                    ? req.getRequirementId()
                    : ((req.getProjectId() != null && !req.getProjectId().isBlank())
                            ? req.getProjectId() + "_" + System.currentTimeMillis()
                            : String.valueOf(System.currentTimeMillis()));
            req.setRequirementId(docId);
            LOCAL_REQ_MAP.put(docId, req);
            LocalCacheManager.saveRequirements(LOCAL_REQ_MAP.values());

            if (!LocalCacheManager.isQuotaExhausted()) {
                db.collection("WorkforceRequirements")
                        .document(docId)
                        .set(req);
                System.out.println("Workforce Requirement Data Inserted: " + docId);
            }
        } catch (Exception e) {
            if (LocalCacheManager.isQuotaExhaustedException(e)) {
                LocalCacheManager.markQuotaExhausted();
            } else {
                System.err.println("[WorkforceRequirementDao] Save notice: " + e.getMessage());
            }
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
        } catch (Exception ignored) {}
        return list;
    }

    public List<WorkforceRequirement> getAllRequirements() {
        long now = System.currentTimeMillis();
        if ((now - lastReqsFetch < CACHE_TTL_MS || LocalCacheManager.isQuotaExhausted()) && !LOCAL_REQ_MAP.isEmpty()) {
            return new ArrayList<>(LOCAL_REQ_MAP.values());
        }

        List<WorkforceRequirement> list = new ArrayList<>();
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                ApiFuture<QuerySnapshot> future = db.collection("WorkforceRequirements").get();
                QuerySnapshot snapshot = future.get();
                lastReqsFetch = now;
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
                LocalCacheManager.saveRequirements(LOCAL_REQ_MAP.values());
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                } else {
                    System.err.println("[WorkforceRequirementDao] Remote fetch notice: " + e.getMessage());
                }
            }
        }
        for (Map.Entry<String, WorkforceRequirement> entry : LOCAL_REQ_MAP.entrySet()) {
            boolean exists = list.stream().anyMatch(r -> entry.getKey().equals(r.getRequirementId())
                    || (r.getRequirementId() != null && r.getRequirementId().equals(entry.getValue().getRequirementId())));
            if (!exists) {
                list.add(0, entry.getValue());
            }
        }
        return list;
    }
}
