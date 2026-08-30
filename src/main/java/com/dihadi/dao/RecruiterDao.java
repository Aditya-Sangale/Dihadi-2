package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Recruiter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class RecruiterDao {
    private Firestore db = FirebaseConfig.getFirestore();

    public void saveRecruiter(Recruiter recruiter) {
        try {
            db.collection("Recruiters")
                    .document(recruiter.getMobileNumber())
                    .set(recruiter);

            System.out.println("Recruiter Data Inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Recruiter getRecruiter(String mobileNumber) {
        try {
            ApiFuture<DocumentSnapshot> future = db.collection("Recruiters")
                    .document(mobileNumber).get();

            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Recruiter.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Recruiter getRecruiterByEmailOrMobile(String identifier) {
        try {
            // First check by document ID (which is mobile number)
            Recruiter r = getRecruiter(identifier);
            if (r != null) return r;
            
            // If not found, check by email field
            ApiFuture<QuerySnapshot> future = db.collection("Recruiters")
                    .whereEqualTo("email", identifier)
                    .get();
            java.util.List<com.google.cloud.firestore.QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                return documents.get(0).toObject(Recruiter.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRecruiter(Recruiter recruiter) {
        try {
            db.collection("Recruiters")
                    .document(recruiter.getMobileNumber())
                    .update("firstName", recruiter.getFirstName(),
                            "middleName", recruiter.getMiddleName(),
                            "lastName", recruiter.getLastName(),
                            "gender", recruiter.getGender(),
                            "alternateMobile", recruiter.getAlternateMobile(),
                            "email", recruiter.getEmail(),
                            "alternateEmail", recruiter.getAlternateEmail(),
                            "companyName", recruiter.getCompanyName(),
                            "businessType", recruiter.getBusinessType());

            System.out.println("Recruiter Data Updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRecruiter(String mobileNumber) {
        try {
            db.collection("Recruiters")
                    .document(mobileNumber).delete();
            System.out.println("Recruiter Data Deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Recruiter> getAllRecruiters() {
        List<Recruiter> list = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = db.collection("Recruiters").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                Recruiter recruiter = doc.toObject(Recruiter.class);
                list.add(recruiter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
