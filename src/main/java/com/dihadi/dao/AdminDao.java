package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Admin;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminDao {
    private final Firestore db = FirebaseConfig.getFirestore();

    public boolean saveAdmin(Admin admin) {
        if (admin == null) return false;
        try {
            String docId = null;
            if (admin.getOfficialEmail() != null && !admin.getOfficialEmail().isBlank()) {
                docId = admin.getOfficialEmail().trim().toLowerCase();
            } else if (admin.getMobile() != null && !admin.getMobile().isBlank()) {
                docId = admin.getMobile().replaceAll("[^0-9a-zA-Z]", "");
            }
            if (docId == null || docId.isBlank()) {
                docId = "admin_" + System.currentTimeMillis();
            }

            db.collection("Admins")
                    .document(docId)
                    .set(admin)
                    .get();

            System.out.println("Admin saved successfully: " + docId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Admin getAdminByEmailOrMobile(String identifier) {
        if (identifier == null || identifier.isBlank()) return null;
        String raw = identifier.trim();
        String rawLower = raw.toLowerCase();
        String rawCleanDigits = raw.replaceAll("\\D", "");

        try {
            // 1. Direct document check by official email lower
            ApiFuture<DocumentSnapshot> directFuture = db.collection("Admins").document(rawLower).get();
            DocumentSnapshot directDoc = directFuture.get();
            if (directDoc.exists()) {
                return directDoc.toObject(Admin.class);
            }

            // 2. Direct document check by clean digits
            if (!rawCleanDigits.isEmpty()) {
                DocumentSnapshot mobDoc = db.collection("Admins").document(rawCleanDigits).get().get();
                if (mobDoc.exists()) {
                    return mobDoc.toObject(Admin.class);
                }
            }

            // 3. Scan Admins collection for matching email or mobile
            ApiFuture<QuerySnapshot> allFuture = db.collection("Admins").get();
            List<QueryDocumentSnapshot> docs = allFuture.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                Admin a = doc.toObject(Admin.class);
                if (a == null) continue;

                if (a.getOfficialEmail() != null && a.getOfficialEmail().trim().equalsIgnoreCase(raw)) {
                    return a;
                }
                if (a.getPersonalEmail() != null && a.getPersonalEmail().trim().equalsIgnoreCase(raw)) {
                    return a;
                }
                if (a.getMobile() != null) {
                    String aDigits = a.getMobile().replaceAll("\\D", "");
                    if (!aDigits.isEmpty() && !rawCleanDigits.isEmpty()) {
                        if (aDigits.equals(rawCleanDigits) || aDigits.endsWith(rawCleanDigits) || rawCleanDigits.endsWith(aDigits)) {
                            return a;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Admin validateCredentials(String identifier, String password) {
        if (identifier == null || password == null || identifier.isBlank() || password.isBlank()) {
            return null;
        }

        Admin admin = getAdminByEmailOrMobile(identifier);
        if (admin != null && admin.getPassword() != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("Admins").get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                Admin a = doc.toObject(Admin.class);
                if (a != null) list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
