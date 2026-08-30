package com.dihadi.controller;

import com.dihadi.dao.NotificationDao;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Notification;

import java.util.List;
import java.util.UUID;

public class NotificationController {
    private final NotificationDao notificationDao = new NotificationDao();

    public void sendNotification(Notification notification) {
        notificationDao.saveNotification(notification);
    }

    public List<Notification> getNotifications(String recipientKey) {
        return notificationDao.getNotificationsForRecipient(recipientKey);
    }

    public void markAsRead(String notificationId) {
        notificationDao.markAsRead(notificationId);
    }

    public void deleteNotification(String notificationId) {
        notificationDao.deleteNotification(notificationId);
    }

    private String generateNotificationId(String recipientMobile) {
        String clean = (recipientMobile != null) ? recipientMobile.replaceAll("[^0-9a-zA-Z]", "") : "RECIPIENT";
        if (clean.isBlank()) clean = "RECIPIENT";
        return clean + "_" + System.currentTimeMillis();
    }

    /**
     * Notify Worker when Recruiter accepts their application
     */
    public void notifyWorkerApplicationAccepted(JobApplication app, String recruiterName, String projectName) {
        if (app == null || app.getWorkerMobile() == null || app.getWorkerMobile().isBlank()) return;
        String proj = (projectName != null && !projectName.isBlank()) ? projectName : (app.getJobTitle() != null ? app.getJobTitle() : "Project");
        String rec = (recruiterName != null && !recruiterName.isBlank()) ? recruiterName : "Site Recruiter";

        Notification n = new Notification(
                generateNotificationId(app.getWorkerMobile()),
                app.getWorkerMobile(),
                "WORKER",
                rec,
                app.getRecruiterMobile(),
                "Application Accepted! 🎉",
                "Your application for " + app.getJobTitle() + " on " + proj + " has been accepted by " + rec + ". Welcome to the project!",
                "APPLICATION_ACCEPTED",
                app.getProjectId(),
                proj,
                app.getJobTitle()
        );
        sendNotification(n);
    }

    /**
     * Notify Worker when Recruiter sends a direct hiring offer/request
     */
    public void notifyWorkerHiringRequest(String workerMobile, String recruiterName, String recruiterMobile,
                                           String role, String location, String wage, String projectName) {
        if (workerMobile == null || workerMobile.isBlank()) return;
        String rec = (recruiterName != null && !recruiterName.isBlank()) ? recruiterName : "Site Recruiter";
        String proj = (projectName != null && !projectName.isBlank()) ? projectName : "DIHADI Project Site";

        Notification n = new Notification(
                generateNotificationId(workerMobile),
                workerMobile,
                "WORKER",
                rec,
                recruiterMobile,
                "New Direct Hiring Offer! 📋",
                rec + " has sent you a direct hiring request for the role of " + role + " (" + location + ") at " + wage + ". Review and accept on your dashboard.",
                "HIRING_REQUEST",
                "",
                proj,
                role
        );
        sendNotification(n);
    }

    /**
     * Notify Recruiter when Worker accepts their direct hiring invitation
     */
    public void notifyRecruiterHiringAccepted(JobApplication app, String workerName, String workerMobile) {
        if (app == null || app.getRecruiterMobile() == null || app.getRecruiterMobile().isBlank()) return;
        String worker = (workerName != null && !workerName.isBlank()) ? workerName : (workerMobile != null ? workerMobile : "Worker");

        Notification n = new Notification(
                generateNotificationId(app.getRecruiterMobile()),
                app.getRecruiterMobile(),
                "RECRUITER",
                worker,
                workerMobile,
                "Hiring Offer Accepted! ✓",
                "Worker " + worker + " has accepted your direct hiring offer for " + app.getJobTitle() + " (" + app.getJobLocation() + ")!",
                "HIRING_ACCEPTED",
                app.getProjectId(),
                app.getJobTitle(),
                app.getJobTitle()
        );
        sendNotification(n);
    }

    /**
     * Notify Recruiter when Worker submits a new job application
     */
    public void notifyRecruiterApplicationReceived(JobApplication app, String workerName, String workerMobile, String projectName) {
        if (app == null || app.getRecruiterMobile() == null || app.getRecruiterMobile().isBlank()) return;
        String worker = (workerName != null && !workerName.isBlank()) ? workerName : (workerMobile != null ? workerMobile : "Worker");
        String proj = (projectName != null && !projectName.isBlank()) ? projectName : (app.getJobTitle() != null ? app.getJobTitle() : "Project");

        Notification n = new Notification(
                generateNotificationId(app.getRecruiterMobile()),
                app.getRecruiterMobile(),
                "RECRUITER",
                worker,
                workerMobile,
                "New Application Received 📥",
                "Worker " + worker + " applied for " + app.getJobTitle() + " on " + proj + ". Check pending approvals to review.",
                "APPLICATION_RECEIVED",
                app.getProjectId(),
                proj,
                app.getJobTitle()
        );
        sendNotification(n);
    }
}
