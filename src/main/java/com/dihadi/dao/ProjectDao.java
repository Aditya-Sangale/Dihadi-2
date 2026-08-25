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
            db.collection("Projects")
                    .document(project.getMobile())
                    .set(project);
            System.out.println("Project Data Inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Project getProject(String getMobile) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("Projects")
                    .document(getMobile).get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Project.class);
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
}
