package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Project;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class ProjectDao {
    private Firestore db = FirebaseConfig.getFirestore();
    private static final Map<String, Project> LOCAL_PROJECT_MAP = new ConcurrentHashMap<>();
    private static volatile long lastProjectsFetch = 0;
    private static final long CACHE_TTL_MS = 15000;

    static {
        try {
            List<Project> disk = LocalCacheManager.loadProjects();
            for (Project p : disk) {
                if (p != null && p.getProjectId() != null && !p.getProjectId().isBlank()) {
                    LOCAL_PROJECT_MAP.put(p.getProjectId(), p);
                }
            }
        } catch (Exception ignored) {}
    }

    public static void cacheProject(Project project) {
        if (project == null) return;
        if (project.getProjectId() != null && !project.getProjectId().isBlank()) {
            LOCAL_PROJECT_MAP.put(project.getProjectId(), project);
        }
        if (project.getId() != null && !project.getId().isBlank()) {
            LOCAL_PROJECT_MAP.put(project.getId(), project);
        }
        LocalCacheManager.saveProjects(LOCAL_PROJECT_MAP.values());
    }

    public void saveProject(Project project) {
        if (project == null) return;
        try {
            String docId = (project.getProjectId() != null && !project.getProjectId().isBlank())
                    ? project.getProjectId()
                    : (project.getMobile() != null && !project.getMobile().isBlank()
                            ? project.getMobile() + "_" + System.currentTimeMillis()
                            : String.valueOf(System.currentTimeMillis()));
            if (project.getProjectId() == null || project.getProjectId().isBlank()) {
                project.setProjectId(docId);
            }
            LOCAL_PROJECT_MAP.put(docId, project);
            LocalCacheManager.saveProjects(LOCAL_PROJECT_MAP.values());

            if (!LocalCacheManager.isQuotaExhausted()) {
                db.collection("Projects")
                        .document(docId)
                        .set(project);
                System.out.println("Project Data Inserted for Recruiter (" + project.getMobile() + "): " + docId);
            }
        } catch (Exception e) {
            if (LocalCacheManager.isQuotaExhaustedException(e)) {
                LocalCacheManager.markQuotaExhausted();
            } else {
                System.err.println("[ProjectDao] Save notice: " + e.getMessage());
            }
        }
    }

    public void updateProjectStatus(String projectId, String newStatus) {
        if (projectId == null || projectId.isBlank()) return;
        Project p = getProject(projectId);
        if (p != null) {
            p.setStatus(newStatus);
            saveProject(p);
        } else {
            try {
                if (!LocalCacheManager.isQuotaExhausted()) {
                    db.collection("Projects").document(projectId).update("status", newStatus);
                }
            } catch (Exception ignored) {}
        }
    }

    public Project getProject(String idOrMobile) {
        if (idOrMobile == null) return null;
        if (LOCAL_PROJECT_MAP.containsKey(idOrMobile)) {
            return LOCAL_PROJECT_MAP.get(idOrMobile);
        }
        for (Project p : LOCAL_PROJECT_MAP.values()) {
            if (idOrMobile.equals(p.getProjectId()) || idOrMobile.equals(p.getMobile())) {
                return p;
            }
        }
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                ApiFuture<DocumentSnapshot> future = db.collection("Projects")
                        .document(idOrMobile).get();
                DocumentSnapshot document = future.get();
                if (document.exists()) {
                    Project p = document.toObject(Project.class);
                    if (p != null) {
                        if (p.getProjectId() == null || p.getProjectId().isBlank()) p.setProjectId(document.getId());
                        LOCAL_PROJECT_MAP.put(p.getProjectId(), p);
                        LocalCacheManager.saveProjects(LOCAL_PROJECT_MAP.values());
                        return p;
                    }
                }
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                }
            }
        }
        List<Project> all = getAllProjects();
        for (Project p : all) {
            if (idOrMobile.equals(p.getProjectId()) || idOrMobile.equals(p.getMobile())) {
                return p;
            }
        }
        return null;
    }

    public List<Project> getAllProjects() {
        long now = System.currentTimeMillis();
        if ((now - lastProjectsFetch < CACHE_TTL_MS || LocalCacheManager.isQuotaExhausted()) && !LOCAL_PROJECT_MAP.isEmpty()) {
            return new ArrayList<>(LOCAL_PROJECT_MAP.values());
        }

        List<Project> list = new ArrayList<>();
        if (!LocalCacheManager.isQuotaExhausted()) {
            try {
                ApiFuture<QuerySnapshot> future = db.collection("Projects").get();
                QuerySnapshot snapshot = future.get();
                lastProjectsFetch = now;
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    Project project = doc.toObject(Project.class);
                    if (project != null) {
                        if (project.getProjectId() == null || project.getProjectId().isBlank()) {
                            project.setProjectId(doc.getId());
                        }
                        LOCAL_PROJECT_MAP.put(project.getProjectId(), project);
                        list.add(project);
                    }
                }
                LocalCacheManager.saveProjects(LOCAL_PROJECT_MAP.values());
            } catch (Exception e) {
                if (LocalCacheManager.isQuotaExhaustedException(e)) {
                    LocalCacheManager.markQuotaExhausted();
                } else {
                    System.err.println("[ProjectDao] Remote fetch notice: " + e.getMessage());
                }
            }
        }

        for (Map.Entry<String, Project> entry : LOCAL_PROJECT_MAP.entrySet()) {
            boolean exists = list.stream().anyMatch(p -> entry.getKey().equals(p.getProjectId()));
            if (!exists) {
                list.add(0, entry.getValue());
            }
        }
        return list;
    }

    public boolean deleteProject(String projectId) {
        try {
            if (projectId == null || projectId.isBlank()) return false;
            LOCAL_PROJECT_MAP.remove(projectId);
            db.collection("Projects").document(projectId).delete().get();
            ApiFuture<QuerySnapshot> future = db.collection("Projects").whereEqualTo("projectId", projectId).get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                doc.getReference().delete().get();
            }
            System.out.println("Project deleted successfully: " + projectId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Project> getProjectsByRecruiterId(String recruiterId) {
        List<Project> all = getAllProjects();
        if (recruiterId == null || recruiterId.isBlank() || "REC_DEFAULT".equalsIgnoreCase(recruiterId)) {
            return all;
        }
        String cleanId = recruiterId.replaceAll("\\D", "");
        List<Project> matched = new ArrayList<>();
        for (Project p : all) {
            String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
            if (recruiterId.equalsIgnoreCase(p.getMobile()) || recruiterId.equalsIgnoreCase(p.getEmail()) ||
                (!cleanId.isEmpty() && !pMob.isEmpty() && (cleanId.equals(pMob) || cleanId.endsWith(pMob) || pMob.endsWith(cleanId)))) {
                matched.add(p);
            }
        }
        // If no strict match, fallback to all projects so recruiter isn't blocked
        return !matched.isEmpty() ? matched : all;
    }
}
