package com.dihadi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

            DocumentSnapshot document = future.get(10, TimeUnit.SECONDS);
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
            String value = identifier == null ? "" : identifier.trim();
            boolean emailLogin = value.contains("@");

            // Recruiter records use the mobile number as their document ID. Email
            // sign-in should query the email field first, avoiding an unnecessary
            // document request and making the login screen respond promptly.
            if (!emailLogin) {
                Recruiter recruiter = getRecruiter(value);
                if (recruiter != null) return recruiter;
            }

            ApiFuture<QuerySnapshot> future = db.collection("Recruiters")
                    .whereEqualTo("email", value)
                    .get();
            java.util.List<com.google.cloud.firestore.QueryDocumentSnapshot> documents = future
                    .get(10, TimeUnit.SECONDS).getDocuments();
            if (!documents.isEmpty()) return documents.get(0).toObject(Recruiter.class);

            return null;
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

    public void updateWalletBalance(String mobileNumber, double newBalance) {
        try {
            db.collection("Recruiters")
                    .document(mobileNumber)
                    .update("walletBalance", newBalance);
            System.out.println("Wallet balance updated for " + mobileNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
