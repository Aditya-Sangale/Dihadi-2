package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Project;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class ProjectDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveProject(Project project) {
        try {
            String docId = (project.getProjectId() != null && !project.getProjectId().isBlank())
                    ? project.getProjectId()
                    : (project.getMobile() != null && !project.getMobile().isBlank()
                            ? project.getMobile() + "_" + System.currentTimeMillis()
                            : String.valueOf(System.currentTimeMillis()));
            db.collection("Projects")
                    .document(docId)
                    .set(project);
            System.out.println("Project Data Inserted for Recruiter (" + project.getMobile() + "): " + docId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Project getProject(String idOrMobile) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("Projects")
                    .document(idOrMobile).get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Project.class);
            }
            List<Project> all = getAllProjects();
            for (Project p : all) {
                if (idOrMobile.equals(p.getProjectId()) || idOrMobile.equals(p.getMobile())) {
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Project> getAllProjects() {
        List<Project> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Projects").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Project project = doc.toObject(Project.class);
                list.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteProject(String projectId) {
        try {
            if (projectId == null || projectId.isBlank()) return false;
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
}
