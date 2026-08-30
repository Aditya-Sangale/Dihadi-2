package com.dihadi.view.worker;

import com.dihadi.model.Worker;
import com.dihadi.model.Attendance;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Project;
import com.dihadi.controller.AttendanceController;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.ProjectController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dihadi.model.Notification;
import com.dihadi.controller.NotificationController;

/**
 * Full Worker Portal dashboard, populated from the authenticated Worker record with real-time live attendance, wage updates & notifications.
 */
public class WorkerDashboard {
    private final Worker worker;
    private Timeline liveRefresher;

    private final Label walletMetricValue = new Label("₹ 0");
    private final Label walletMetricSub = new Label("+ Earnings will appear here");

    private final Label attendanceMetricValue = new Label("0 Days Present");
    private final Label attendanceMetricSub = new Label("No attendance recorded");

    private final Label jobRequestsMetricLabel = new Label("0 New");
    private final Label reputationMetricValue = new Label("5.0 ★ Top Rated");
    private final Label reputationMetricSub = new Label("Verified KYC ✓");

    private VBox historyPanel;
    private VBox recruiterRequestsPanel;
    private VBox pendingApplicationsPanel;
    private VBox liveAttendancePanel;
    private VBox notificationsPanel;

    private final java.util.Set<String> seenNotifIds = new java.util.HashSet<>();
    private boolean initialLoadDone = false;

    public WorkerDashboard(Worker worker) {
        this.worker = worker;
    }

    public Scene getScene(Runnable back) {
        String name = value(worker.getFirstName()) + (worker.getLastName() == null ? "" : " " + worker.getLastName());
        String category = value(worker.getWorkerType(), "Skilled Worker");
        String location = (value(worker.getCity()) + ", " + value(worker.getState()))
                .replace("Not provided, Not provided", "Location not provided");
        VBox heroContainer = hero(name, category);
        VBox content = new VBox(24, header(name), heroContainer, metrics(), lowerSections(location, heroContainer),
                footer(back));
        content.setPadding(new Insets(32, 80, 40, 80));
        content.setMaxWidth(1440);
        content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;");

        // Initial fetch
        refreshWorkerData(heroContainer);

        // Real-time live polling refresher every 3 seconds
        liveRefresher = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshWorkerData(heroContainer)));
        liveRefresher.setCycleCount(Timeline.INDEFINITE);
        liveRefresher.play();

        return new Scene(scroll, 1440, 900);
    }

    private HBox header(String name) {
        Label brand = label("DIHADI",
                "-fx-font-family:Georgia;-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label user = label(name, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#4c4637;");
        HBox h = new HBox(16, brand, spacer, user);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(0, 0, 16, 0));
        h.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return h;
    }

    private VBox hero(String name, String category) {
        Label title = label("Namaste, " + name + "!",
                "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label role = label(category + "   •   KYC Verified ✓",
                "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label copy = label("Your skills are in high demand. Check your active projects and upcoming invitations below.",
                "-fx-font-size:15px;-fx-text-fill:#4c4637;");
        copy.setMaxWidth(420);
        copy.setWrapText(true);
        VBox welcome = new VBox(10, title, role, copy);
        welcome.setPrefWidth(410);
        Label activeProjTitle = label("No active project assigned",
                "-fx-font-family:Georgia;-fx-font-size:23px;-fx-font-weight:700;");
        VBox roleDetail = detail("Role", category);
        VBox locationDetail = detail("Location", "Location not provided");
        VBox wageDetail = detail("Daily Wage", "₹ " + worker.getDailyWage() + " / day");
        VBox siteStatusDetail = detail("Site Status", "Ready for opportunities");
        VBox project = panel("ACTIVE SITE PROJECT", activeProjTitle, roleDetail, locationDetail, wageDetail,
                siteStatusDetail);
        project.setMinWidth(720);
        HBox.setHgrow(project, Priority.ALWAYS);

        HBox h = new HBox(36, welcome, project);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setUserData(new Node[] { activeProjTitle, roleDetail, locationDetail, wageDetail, siteStatusDetail });
        return new VBox(h);
    }

    private HBox metrics() {
        walletMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        walletMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#3f4938;");

        attendanceMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        attendanceMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#3f4938;");

        jobRequestsMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        reputationMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        reputationMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#3f4938;");

        HBox row = new HBox(24,
                panel("Wallet Balance", walletMetricValue, walletMetricSub),
                panel("Attendance (Month)", attendanceMetricValue, attendanceMetricSub),
                panel("Job Requests", jobRequestsMetricLabel, label("Recruiter Direct Offers", "-fx-font-size:13px;-fx-text-fill:#3f4938;")),
                panel("Reputation", reputationMetricValue, reputationMetricSub));
        for (Node n : row.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }

    private HBox lowerSections(String location, VBox heroContainer) {
        historyPanel = panel("RECENT WORK HISTORY",
                label("No completed work history yet.", "-fx-font-size:15px;-fx-text-fill:#4c4637;"),
                detail("Current location", location), detail("Experience", value(worker.getExperience())));

        recruiterRequestsPanel = panel("RECRUITER JOB REQUESTS",
                label("Loading requests...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        pendingApplicationsPanel = panel("PENDING JOB APPLICATIONS",
                label("Loading applications...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        liveAttendancePanel = panel("LIVE ATTENDANCE & WAGE LEDGER",
                label("Loading real-time attendance logs...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        notificationsPanel = panel("NOTIFICATIONS & ALERTS",
                label("Loading real-time notifications...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        VBox kyc = panel("KYC VERIFICATION HUB", detail("Profile status", "Submitted for verification"),
                detail("Education", value(worker.getEducation())),
                detail("Documents", "Upload documents when available"));

        VBox settings = panel("SKILL & PREF SETTINGS", detail("Primary skill", value(worker.getSubSkill())),
                detail("Desired daily wage", "₹ " + worker.getDailyWage()),
                detail("Work radius", "Open to suitable projects"));

        VBox left = new VBox(16, liveAttendancePanel, historyPanel, recruiterRequestsPanel, kyc);
        VBox right = new VBox(16, notificationsPanel, pendingApplicationsPanel, settings);
        HBox h = new HBox(18, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        return h;
    }

    private boolean isUpdating = false;

    private void refreshWorkerData(VBox heroContainer) {
        if (isUpdating) return;
        isUpdating = true;

        new Thread(() -> {
            try {
                String workerMob = worker.getMobileNumber();
                List<JobApplication> apps = new JobApplicationController().getApplicationsByWorker(workerMob);
                List<Attendance> attendances = new AttendanceController().getAttendanceByWorker(workerMob);
                List<Project> allProjects = new ProjectController().getAllProjects();

                Map<String, Project> projectMap = new HashMap<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (p.getProjectId() != null) projectMap.put(p.getProjectId(), p);
                    }
                }

                // Process Attendance metrics and wage calculations
                int daysPresent = 0;
                int daysAbsent = 0;
                double effectiveDailyWage = worker.getDailyWage();
                String todayStr = LocalDate.now().toString();
                String todayStatus = "Pending";

                if (apps != null) {
                    for (JobApplication a : apps) {
                        if ("Accepted".equalsIgnoreCase(a.getStatus()) && a.getJobWage() != null) {
                            try {
                                effectiveDailyWage = Double.parseDouble(a.getJobWage().replaceAll("[^0-9.]", ""));
                            } catch (Exception ignored) {}
                        }
                    }
                }

                if (attendances != null) {
                    for (Attendance att : attendances) {
                        if ("Present".equalsIgnoreCase(att.getStatus())) {
                            daysPresent++;
                        } else if ("Absent".equalsIgnoreCase(att.getStatus())) {
                            daysAbsent++;
                        }
                        if (todayStr.equals(att.getDate())) {
                            todayStatus = att.getStatus();
                        }
                    }
                }

                double totalEarned = daysPresent * effectiveDailyWage;
                final int finalDaysPresent = daysPresent;
                final double finalTotalEarned = totalEarned;
                final double finalDailyWage = effectiveDailyWage;
                final String finalTodayStatus = todayStatus;

                List<Notification> notifications = new NotificationController().getNotifications(workerMob);

                Platform.runLater(() -> {
                    // Update Notifications Panel
                    if (notificationsPanel != null) {
                        notificationsPanel.getChildren().clear();
                        notificationsPanel.getChildren().add(label("✦  NOTIFICATIONS & ALERTS",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));

                        if (notifications == null || notifications.isEmpty()) {
                            notificationsPanel.getChildren().addAll(
                                    label("No notifications yet", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Updates regarding your applications & hiring offers will appear here in real time.", "-fx-font-size:13px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            int count = 0;
                            for (Notification n : notifications) {
                                if (n.getNotificationId() != null && !seenNotifIds.contains(n.getNotificationId())) {
                                    seenNotifIds.add(n.getNotificationId());
                                    if (initialLoadDone) {
                                        com.dihadi.view.NotificationToast.ToastType toastType = "APPLICATION_ACCEPTED".equalsIgnoreCase(n.getType())
                                                ? com.dihadi.view.NotificationToast.ToastType.SUCCESS
                                                : com.dihadi.view.NotificationToast.ToastType.ACTION;
                                        com.dihadi.view.NotificationToast.show(heroContainer, n.getTitle(), n.getMessage(), toastType);
                                    }
                                }

                                if (count++ >= 8) continue; // show up to 8 most recent in panel
                                String icon = "🔔";
                                if ("APPLICATION_ACCEPTED".equalsIgnoreCase(n.getType())) icon = "🎉";
                                else if ("HIRING_REQUEST".equalsIgnoreCase(n.getType())) icon = "📋";
                                else if ("APPLICATION_RECEIVED".equalsIgnoreCase(n.getType())) icon = "📥";
                                else if ("APPLICATION_REJECTED".equalsIgnoreCase(n.getType())) icon = "ℹ️";

                                Label titleLbl = label(icon + "  " + (n.getTitle() != null ? n.getTitle() : "Update"),
                                        "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                                Label msgLbl = label(n.getMessage() != null ? n.getMessage() : "",
                                        "-fx-font-size:12px;-fx-text-fill:#4d4635;");
                                msgLbl.setWrapText(true);

                                VBox notifCard = new VBox(4, titleLbl, msgLbl);
                                notifCard.setPadding(new Insets(10, 12, 10, 12));
                                if ("APPLICATION_ACCEPTED".equalsIgnoreCase(n.getType())) {
                                    notifCard.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-border-color:#a5d6a7;-fx-border-width:1px;-fx-border-radius:8px;");
                                } else if ("HIRING_REQUEST".equalsIgnoreCase(n.getType())) {
                                    notifCard.setStyle("-fx-background-color:#fff8e1;-fx-background-radius:8px;-fx-border-color:#ffe082;-fx-border-width:1px;-fx-border-radius:8px;");
                                } else {
                                    notifCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:8px;");
                                }
                                notificationsPanel.getChildren().add(notifCard);
                            }
                            initialLoadDone = true;
                        }
                    }

                    // Update Top Metric Cards
                    walletMetricValue.setText("₹ " + String.format("%,.0f", finalTotalEarned));
                    walletMetricSub.setText("+ ₹ " + String.format("%,.0f", finalDailyWage) + " / day • " + finalDaysPresent + " Days Paid");

                    attendanceMetricValue.setText(finalDaysPresent + " Day" + (finalDaysPresent == 1 ? "" : "s") + " Present");
                    if ("Present".equalsIgnoreCase(finalTodayStatus)) {
                        attendanceMetricSub.setText("✓ Present Today • ₹ " + String.format("%,.0f", finalDailyWage) + " Credited");
                        attendanceMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#2a7e3b;-fx-font-weight:700;");
                    } else if ("Absent".equalsIgnoreCase(finalTodayStatus)) {
                        attendanceMetricSub.setText("❌ Marked Absent Today");
                        attendanceMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#ba1a1a;-fx-font-weight:700;");
                    } else {
                        attendanceMetricSub.setText("○ Today's Attendance Pending");
                        attendanceMetricSub.setStyle("-fx-font-size:13px;-fx-text-fill:#735c00;");
                    }

                    // Update Live Attendance & Wage Ledger Panel
                    if (liveAttendancePanel != null) {
                        liveAttendancePanel.getChildren().clear();
                        liveAttendancePanel.getChildren().add(label("✦  LIVE ATTENDANCE & WAGE LEDGER",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));

                        if (attendances == null || attendances.isEmpty()) {
                            liveAttendancePanel.getChildren().addAll(
                                    label("No attendance logged yet", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Daily logs recorded by your project recruiter will appear here in real time.", "-fx-font-size:13px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            // Show the 10 most recent attendance records
                            int count = 0;
                            for (Attendance att : attendances) {
                                if (count++ >= 10) break;
                                boolean isPresent = "Present".equalsIgnoreCase(att.getStatus());
                                Label dateLabel = label("📅 " + formatDate(att.getDate()), "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");

                                Label statusBadge = new Label(isPresent ? "✓ PRESENT" : "✗ ABSENT");
                                statusBadge.setStyle(isPresent
                                        ? "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#2a7e3b;-fx-background-color:#e8f5e9;-fx-background-radius:6px;-fx-padding:3px 8px;"
                                        : "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;-fx-background-color:#ffebee;-fx-background-radius:6px;-fx-padding:3px 8px;");

                                String siteName = "DIHADI Project Site";
                                if (att.getProjectId() != null && projectMap.containsKey(att.getProjectId())) {
                                    Project p = projectMap.get(att.getProjectId());
                                    if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                                        siteName = p.getProjectName();
                                    }
                                }
                                Label siteLabel = label("Site: " + siteName, "-fx-font-size:13px;-fx-text-fill:#4c4637;");

                                Label wageBadge = new Label(isPresent ? "+ ₹ " + String.format("%,.0f", finalDailyWage) : "₹ 0");
                                wageBadge.setStyle(isPresent
                                        ? "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#2a7e3b;"
                                        : "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#ba1a1a;");

                                Region spacer = new Region();
                                HBox.setHgrow(spacer, Priority.ALWAYS);

                                HBox topRow = new HBox(12, dateLabel, statusBadge, spacer, wageBadge);
                                topRow.setAlignment(Pos.CENTER_LEFT);

                                Label verifiedTag = label("Recorded by Site Recruiter ✓", "-fx-font-size:11px;-fx-text-fill:#735c00;-fx-font-weight:600;");

                                VBox attCard = new VBox(5, topRow, siteLabel, verifiedTag);
                                attCard.setPadding(new Insets(12, 14, 12, 14));
                                attCard.setStyle(isPresent
                                        ? "-fx-background-color:#f4f9f4;-fx-background-radius:10px;-fx-border-color:#c8e6c9;-fx-border-width:1.5px;-fx-border-radius:10px;"
                                        : "-fx-background-color:#fff5f5;-fx-background-radius:10px;-fx-border-color:#ffcdd2;-fx-border-width:1.5px;-fx-border-radius:10px;");

                                liveAttendancePanel.getChildren().add(attCard);
                            }
                        }
                    }

                    // Update Applications, Requests, and History
                    if (recruiterRequestsPanel != null && pendingApplicationsPanel != null && historyPanel != null) {
                        recruiterRequestsPanel.getChildren().clear();
                        recruiterRequestsPanel.getChildren().add(label("✦  RECRUITER JOB REQUESTS",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));

                        pendingApplicationsPanel.getChildren().clear();
                        pendingApplicationsPanel.getChildren().add(label("✦  PENDING JOB APPLICATIONS",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));

                        int reqCount = 0;
                        int appCount = 0;
                        int completedCount = 0;

                        if (apps != null) {
                            for (JobApplication app : apps) {
                                boolean isDirectRequest = (app.getJobTitle() != null && app.getJobTitle().contains("Hiring Request"))
                                        || (app.getRecruiterMobile() != null && !app.getRecruiterMobile().isBlank());

                                if ("Completed".equalsIgnoreCase(app.getStatus())) {
                                    if (completedCount == 0) {
                                        historyPanel.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().contains("No completed work history yet"));
                                    }
                                    completedCount++;
                                    addWorkHistoryCard(historyPanel, app);
                                } else if ("Accepted".equalsIgnoreCase(app.getStatus())) {
                                    HBox heroHBox = (HBox) heroContainer.getChildren().get(0);
                                    Node[] details = (Node[]) heroHBox.getUserData();
                                    updateActiveProjectCard(details, app);
                                } else if ("Pending".equalsIgnoreCase(app.getStatus())) {
                                    if (isDirectRequest) {
                                        reqCount++;
                                        Button acceptBtn = new Button("ACCEPT REQUEST");
                                        acceptBtn.setStyle("-fx-background-color:#d4af37;-fx-background-radius:6px;-fx-text-fill:#ffffff;-fx-font-weight:700;-fx-font-size:12px;-fx-padding:6px 14px;-fx-cursor:hand;");

                                        Button declineBtn = new Button("DECLINE");
                                        declineBtn.setStyle("-fx-background-color:transparent;-fx-border-color:#ba1a1a;-fx-border-radius:6px;-fx-background-radius:6px;-fx-text-fill:#ba1a1a;-fx-font-weight:700;-fx-font-size:12px;-fx-padding:5px 12px;-fx-cursor:hand;");

                                        HBox actionBox = new HBox(10, acceptBtn, declineBtn);
                                        actionBox.setAlignment(Pos.CENTER_LEFT);
                                        actionBox.setPadding(new Insets(6, 0, 0, 0));

                                        VBox reqCard = new VBox(5,
                                                label(app.getJobTitle(), "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                                label("⌖ " + app.getJobLocation() + "  |  Daily Wage: ₹ " + app.getJobWage(), "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                                label("Direct Recruiter Hiring Offer  •  Status: Pending Approval", "-fx-font-size:12px;-fx-text-fill:#735c00;-fx-font-weight:700;"),
                                                actionBox);
                                        reqCard.setPadding(new Insets(14));
                                        reqCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:10px;");

                                        final JobApplication currentApp = app;
                                        acceptBtn.setOnAction(ev -> {
                                            acceptBtn.setDisable(true);
                                            declineBtn.setDisable(true);
                                            new Thread(() -> {
                                                currentApp.setStatus("Accepted");
                                                new JobApplicationController().saveApplication(currentApp);
                                                String workerFullName = ((worker.getFirstName() != null ? worker.getFirstName() : "") + " " +
                                                        (worker.getLastName() != null ? worker.getLastName() : "")).trim();
                                                if (workerFullName.isEmpty()) workerFullName = "Worker (" + worker.getMobileNumber() + ")";
                                                new NotificationController().notifyRecruiterHiringAccepted(
                                                        currentApp,
                                                        workerFullName,
                                                        worker.getMobileNumber()
                                                );
                                                Platform.runLater(() -> {
                                                    com.dihadi.view.NotificationToast.show(acceptBtn, "Offer Accepted! 🎉",
                                                            "You accepted the hiring offer for " + currentApp.getJobTitle() + ". Recruiter notified!",
                                                            com.dihadi.view.NotificationToast.ToastType.SUCCESS);
                                                    refreshWorkerData(heroContainer);
                                                });
                                            }).start();
                                        });

                                        declineBtn.setOnAction(ev -> {
                                            acceptBtn.setDisable(true);
                                            declineBtn.setDisable(true);
                                            new Thread(() -> {
                                                new JobApplicationController().deleteApplication(currentApp.getApplicationId());
                                                Platform.runLater(() -> {
                                                    com.dihadi.view.NotificationToast.show(declineBtn, "Offer Declined",
                                                            "The direct hiring offer has been declined.",
                                                            com.dihadi.view.NotificationToast.ToastType.INFO);
                                                    refreshWorkerData(heroContainer);
                                                });
                                            }).start();
                                        });

                                        recruiterRequestsPanel.getChildren().add(reqCard);
                                    } else {
                                        appCount++;
                                        Label withdraw = new Label("Withdraw application");
                                        withdraw.setStyle("-fx-font-size:12px;-fx-text-fill:#ba1a1a;-fx-underline:true;-fx-cursor:hand;");

                                        VBox appCard = new VBox(4,
                                                label(app.getJobTitle(), "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                                label("⌖ " + app.getJobLocation() + "  |  Wage: ₹ " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                                label("Status: Pending Recruiter Approval", "-fx-font-size:13px;-fx-text-fill:#735c00;-fx-font-weight:700;"),
                                                withdraw);
                                        appCard.setPadding(new Insets(12));
                                        appCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:8px;");

                                        final JobApplication currentApp = app;
                                        withdraw.setOnMouseClicked(ev -> {
                                            new Thread(() -> {
                                                new JobApplicationController().deleteApplication(currentApp.getApplicationId());
                                                Platform.runLater(() -> refreshWorkerData(heroContainer));
                                            }).start();
                                        });

                                        pendingApplicationsPanel.getChildren().add(appCard);
                                    }
                                }
                            }
                        }

                        jobRequestsMetricLabel.setText(reqCount + " New");

                        if (recruiterRequestsPanel.getChildren().size() == 1) {
                            recruiterRequestsPanel.getChildren().addAll(
                                    label("No pending recruiter requests", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Direct recruiter hiring offers will appear here.", "-fx-font-size:13px;-fx-text-fill:#8c7e6b;"));
                        }
                        if (pendingApplicationsPanel.getChildren().size() == 1) {
                            pendingApplicationsPanel.getChildren().addAll(
                                    label("No pending applications", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Your applied project opportunities will appear here.", "-fx-font-size:13px;-fx-text-fill:#8c7e6b;"));
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isUpdating = false;
            }
        }).start();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return "Today";
        if (dateStr.equals(LocalDate.now().toString())) return "Today (" + dateStr + ")";
        try {
            LocalDate d = LocalDate.parse(dateStr);
            return d.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void updateActiveProjectCard(Node[] details, com.dihadi.model.JobApplication app) {
        String pName = app.getJobTitle();
        String pLoc = app.getJobLocation();

        try {
            if (app.getProjectId() != null && !app.getProjectId().isBlank()) {
                com.dihadi.model.Project proj = new com.dihadi.controller.ProjectController()
                        .getProject(app.getProjectId());
                if (proj != null && proj.getProjectName() != null && !proj.getProjectName().isBlank()) {
                    pName = proj.getProjectName();
                    String locStr = (proj.getCity() != null ? proj.getCity() : "") + ", "
                            + (proj.getState() != null ? proj.getState() : "");
                    locStr = locStr.replaceAll("^, |, $", "").trim();
                    if (!locStr.isBlank())
                        pLoc = locStr;
                }
            }
        } catch (Exception ex) {
        }

        if (pName == null || pName.isBlank() || pName.toLowerCase().contains("hiring request")) {
            pName = "Active Construction Project";
        }

        final String displayTitle = pName;
        final String displayLoc = (pLoc != null && !pLoc.isBlank()) ? pLoc : "Location not provided";
        String roleName = (app.getJobTitle() != null && !app.getJobTitle().isBlank()) ? app.getJobTitle() : (worker.getWorkerType() != null ? worker.getWorkerType() : "Skilled Worker");

        ((Label) details[0]).setText(displayTitle);
        ((Label) ((VBox) details[1]).getChildren().get(1)).setText(roleName);
        ((Label) ((VBox) details[2]).getChildren().get(1)).setText(displayLoc);
        ((Label) ((VBox) details[3]).getChildren().get(1)).setText("₹ " + app.getJobWage() + " / day");
        ((Label) ((VBox) details[4]).getChildren().get(1)).setText("Assigned & Active");
    }

    private void addWorkHistoryCard(VBox historyContainer, com.dihadi.model.JobApplication app) {
        if (historyContainer == null || app == null) return;
        String pName = app.getJobTitle();
        if (pName == null || pName.isBlank() || pName.toLowerCase().contains("hiring request")) {
            pName = "Completed Site Project";
        }

        VBox compCard = new VBox(4,
            label(pName, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
            label("⌖ " + app.getJobLocation() + "  |  Daily Wage: ₹ " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
            label("✓ Project Completed", "-fx-font-size:12px;-fx-text-fill:#27ae60;-fx-font-weight:700;")
        );
        compCard.setPadding(new Insets(12));
        compCard.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:10px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:10px;");
        historyContainer.getChildren().add(compCard);
    }

    private HBox footer(Runnable back) {
        Button exit = new Button("Back to Worker Page");
        exit.setOnAction(e -> {
            if (liveRefresher != null) {
                liveRefresher.stop();
            }
            if (back != null)
                back.run();
        });
        exit.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-weight:700;-fx-padding:11px 22px;");
        HBox h = new HBox(exit);
        h.setAlignment(Pos.CENTER_RIGHT);
        return h;
    }

    private VBox panel(String title, Node... nodes) {
        VBox v = new VBox(12);
        String headerTitle = title.startsWith("✦") ? title : "✦  " + title;
        v.getChildren().add(
                label(headerTitle, "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));
        v.getChildren().addAll(nodes);
        v.setPadding(new Insets(24));
        v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffcf5 100%);-fx-background-radius:18px;-fx-border-color:#e8ddc8;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(115,92,0,.08),16,0,0,5px);");
        v.setOnMouseEntered(e -> v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffbf0 100%);-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:18px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.30),22,0,0,7px);"));
        v.setOnMouseExited(e -> v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffcf5 100%);-fx-background-radius:18px;-fx-border-color:#e8ddc8;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(115,92,0,.08),16,0,0,5px);"));
        return v;
    }

    private VBox detail(String k, String v) {
        return new VBox(3, label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-letter-spacing:.3px;-fx-text-fill:#8c7e6b;"),
                label(value(v), "-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#231b00;"));
    }

    private String value(String s) {
        return s == null || s.isBlank() ? "Not provided" : s;
    }

    private String value(String s, String fallback) {
        return s == null || s.isBlank() ? fallback : s;
    }

    private Label label(String s, String st) {
        Label l = new Label(s);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + st);
        return l;
    }
}