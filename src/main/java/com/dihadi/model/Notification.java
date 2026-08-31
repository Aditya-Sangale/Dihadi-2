package com.dihadi.model;

import java.util.Date;

public class Notification {
    private String notificationId;
    private String recipientId;    // Worker mobile or Recruiter mobile/email
    private String recipientType;  // "WORKER" or "RECRUITER"
    private String senderName;     // Sender's name/company/role
    private String senderContact;  // Sender's phone/email
    private String title;          // Short notification title
    private String message;        // Full notification message
    private String type;           // e.g. "APPLICATION_ACCEPTED", "HIRING_REQUEST", "HIRING_ACCEPTED", "APPLICATION_RECEIVED"
    private String projectId;
    private String projectName;
    private String jobRole;
    private Date timestamp;
    private boolean read;

    public Notification() {
        this.timestamp = new Date();
        this.read = false;
    }

    public Notification(String notificationId, String recipientId, String recipientType, String senderName,
                        String senderContact, String title, String message, String type,
                        String projectId, String projectName, String jobRole) {
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.senderName = senderName;
        this.senderContact = senderContact;
        this.title = title;
        this.message = message;
        this.type = type;
        this.projectId = projectId;
        this.projectName = projectName;
        this.jobRole = jobRole;
        this.timestamp = new Date();
        this.read = false;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String recipientType) { this.recipientType = recipientType; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderContact() { return senderContact; }
    public void setSenderContact(String senderContact) { this.senderContact = senderContact; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
