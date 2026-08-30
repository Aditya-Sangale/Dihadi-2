package com.dihadi.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseConfig {
    static {
        getFirebaseConfig();
    }

    private static void getFirebaseConfig() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            String credentialPath = System.getenv("DIHADI_FIREBASE_CREDENTIALS");
            InputStream serviceAccount = credentialPath == null || credentialPath.isBlank()
                    ? FirebaseConfig.class.getResourceAsStream("/assets/dihadi_fb.json")
                    : Files.newInputStream(Path.of(credentialPath));

            if (serviceAccount == null) {
                throw new IllegalStateException(
                        "Firebase credentials are missing. Set DIHADI_FIREBASE_CREDENTIALS to your service-account JSON path.");
            }

            FirebaseOptions options;
            try (serviceAccount) {
                options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }

            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
