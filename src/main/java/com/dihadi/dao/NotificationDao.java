package com.dihadi.dao;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.model.Notification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    private final Firestore db = FirebaseConfig.getFirestore();

    public void saveNotification(Notification notification) {
        if (notification == null) return;
        try {
            String recipient = notification.getRecipientId() != null ? notification.getRecipientId().trim() : "";
            String cleanRecipient = recipient.replaceAll("[^0-9a-zA-Z]", "");
            if (cleanRecipient.isBlank()) cleanRecipient = "RECIPIENT";

            // Format notification ID as the recipient mobile number with a timestamp suffix
            if (notification.getNotificationId() == null || notification.getNotificationId().isBlank() || notification.getNotificationId().contains("-")) {
                notification.setNotificationId(cleanRecipient + "_" + System.currentTimeMillis());
            }
            if (notification.getTimestamp() == null) {
                notification.setTimestamp(new java.util.Date());
            }
            db.collection("Notifications")
                    .document(notification.getNotificationId())
                    .set(notification)
                    .get();
            System.out.println("Notification Saved: " + notification.getNotificationId() + " for recipient: " + notification.getRecipientId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsForRecipient(String recipientKey) {
        List<Notification> list = new ArrayList<>();
        if (recipientKey == null || recipientKey.isBlank()) return list;

        String rawClean = recipientKey.replaceAll("\\D", "");
        String tenDigit = rawClean.length() >= 10 ? rawClean.substring(rawClean.length() - 10) : rawClean;

        try {
            ApiFuture<QuerySnapshot> future = db.collection("Notifications").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                Notification n = doc.toObject(Notification.class);
                if (n == null || n.getRecipientId() == null) continue;

                String rKey = n.getRecipientId().trim();
                String rClean = rKey.replaceAll("\\D", "");

                boolean match = rKey.equalsIgnoreCase(recipientKey.trim());
                if (!match && !tenDigit.isEmpty() && !rClean.isEmpty()) {
                    match = rClean.endsWith(tenDigit) || tenDigit.endsWith(rClean);
                }

                if (match) {
                    list.add(n);
                }
            }

            // Sort by newest timestamp first
            list.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAsRead(String notificationId) {
        if (notificationId == null || notificationId.isBlank()) return;
        try {
            db.collection("Notifications")
                    .document(notificationId)
                    .update("read", true)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNotification(String notificationId) {
        if (notificationId == null || notificationId.isBlank()) return;
        try {
            db.collection("Notifications")
                    .document(notificationId)
                    .delete()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
