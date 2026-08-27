package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.JobApplication;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.Query.Direction;

import java.util.ArrayList;
import java.util.List;

public class JobApplicationDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveApplication(JobApplication application) {
        try {
            db.collection("JobApplications")
                    .document(application.getApplicationId())
                    .set(application);
            System.out.println("Job Application Saved: " + application.getApplicationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteApplication(String applicationId) {
        try {
            db.collection("JobApplications")
                    .document(applicationId)
                    .delete();
            System.out.println("Job Application Withdrawn: " + applicationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<JobApplication> getApplicationsByWorker(String workerMobile) {
        List<JobApplication> applications = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("JobApplications")
                    .whereEqualTo("workerMobile", workerMobile)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                applications.add(document.toObject(JobApplication.class));
            }
            
            // Sort locally to avoid needing a Firebase composite index
            applications.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applications;
    }
}
