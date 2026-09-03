package com.dihadi.view.worker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dihadi.controller.AttendanceController;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.NotificationController;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkerDashboardController;
import com.dihadi.model.Attendance;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Notification;
import com.dihadi.model.Project;
import com.dihadi.model.Worker;
import com.dihadi.view.ExploreProjectsPage;
import com.dihadi.view.NotificationToast;
import com.dihadi.view.SessionManager;
import com.dihadi.view.WorkerPage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Worker Dashboard with interactive card hover effects and clean, realistic content.
 */
public class WorkerDashboard {
    private static final String GOLD = "#735c00", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private final Worker worker;
    private Timeline liveRefresher;

    private final Label walletMetricValue = new Label("Rs. 0");
    private final Label walletMetricSub = new Label("Total wages earned");

    private final Label attendanceMetricValue = new Label("0 Days");
    private final Label attendanceMetricSub = new Label("No attendance recorded");

    private final Label jobRequestsMetricLabel = new Label("0");
    private final Label dailyWageMetricValue = new Label("Rs. 0");
    private final Label dailyWageMetricSub = new Label("Base daily wage");

    private VBox historyPanel;
    private VBox recruiterRequestsPanel;
    private VBox pendingApplicationsPanel;
    private VBox liveAttendancePanel;
    private VBox notificationsPanel;

    private final java.util.Set<String> seenNotifIds = new java.util.HashSet<>();
    private boolean isUpdating = false;

    public WorkerDashboard() {
        this(SessionManager.currentWorker != null ? SessionManager.currentWorker : new Worker());
    }

    public WorkerDashboard(Worker worker) {
        this.worker = (worker != null) ? worker : (SessionManager.currentWorker != null ? SessionManager.currentWorker : new Worker());
    }

    public Scene getScene(Runnable back) {
        String name = value(worker.getFirstName()) + (worker.getLastName() == null ? "" : " " + worker.getLastName());
        if (name.trim().isEmpty()) name = "Worker";
        String category = value(worker.getWorkerType(), "Skilled Worker");
        String location = (value(worker.getCity()) + ", " + value(worker.getState()))
                .replace("Not provided, Not provided", "Pune, Maharashtra");

        VBox heroContainer = hero(name, category, location, back);
        VBox content = new VBox(24,
                header(name, category, back),
                heroContainer,
                metrics(),
                lowerSections(location, heroContainer),
                footer()
        );
        content.setPadding(new Insets(24, 48, 40, 48));
        content.setMaxWidth(1360);
        content.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");

        refreshWorkerData(heroContainer);

        liveRefresher = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshWorkerData(heroContainer)));
        liveRefresher.setCycleCount(Timeline.INDEFINITE);
        liveRefresher.play();

        return new Scene(scroll, 1400, 850);
    }

    public Scene getWorkerDashboardScene(Runnable back) {
        return getScene(back);
    }

    private HBox header(String name, String category, Runnable back) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 44, 44);
        Label brand = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;");
        HBox brandLockup = new HBox(10, logo, brand);
        brandLockup.setAlignment(Pos.CENTER_LEFT);

        Button navHome = navLink("Browse Projects", false);
        navHome.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            Stage stage = (Stage) navHome.getScene().getWindow();
            stage.setScene(new ExploreProjectsPage(() -> stage.setScene(getScene(back))).getExploreProjectsScene());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userPill = label(name + " (" + category + ")",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#faf3e8;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-padding:7px 16px;");

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:20px;-fx-border-color:#ffcdd2;-fx-border-radius:20px;-fx-text-fill:#ba1a1a;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
        logoutBtn.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            SessionManager.currentWorker = null;
            if (back != null) {
                back.run();
            }
            SessionManager.clearAllSessions();
            NotificationToast.show("Signed Out", "You have signed out of your worker session.", NotificationToast.ToastType.INFO);
            back.run();
        });

        Button workerPageBtn = new Button("←  Back");
        workerPageBtn.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:10px 4px;-fx-cursor:hand;");
        workerPageBtn.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            Stage stage = (Stage) workerPageBtn.getScene().getWindow();
            Worker currentWorker = SessionManager.currentWorker != null ? SessionManager.currentWorker : worker;
            stage.setScene(new WorkerPage(currentWorker).getWorkerScene(back));
        });

        HBox h = new HBox(16, brandLockup, navHome, spacer, userPill, workerPageBtn, logoutBtn);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(0, 0, 14, 0));
        h.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return h;
    }

    private Button navLink(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? GOLD : "#4c4637") + ";-fx-padding:6px 12px;-fx-cursor:hand;");
        return b;
    }

    private VBox hero(String name, String category, String location, Runnable back) {
        Label greeting = label("Namaste, " + name,
                "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label meta = label(category + "  |  " + location,
                "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";");
        Label desc = label("View your daily attendance records, total wages earned, assigned project site, and pending recruiter invitations below.",
                "-fx-font-size:13px;-fx-text-fill:#4d4635;");

        VBox left = new VBox(6, greeting, meta, desc);
        left.setAlignment(Pos.CENTER_LEFT);

        Label badge = label("ASSIGNED PROJECT",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#1565c0;");
        Label activeProjTitle = label(category,
                "-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        VBox roleDetail = detail("Role", category);
        VBox locationDetail = detail("Site Location", location);
        VBox wageDetail = detail("Daily Wage", "Rs. " + String.format("%,d", worker.getDailyWage() > 0 ? worker.getDailyWage() : 950) + " / day");
        VBox siteStatusDetail = detail("Status", "Active");

        HBox projDetails = new HBox(20, roleDetail, locationDetail, wageDetail, siteStatusDetail);

        Button browseProjectsBtn = new Button("Browse Open Projects");
        browseProjectsBtn.setStyle("-fx-background-color:#1e1b15;-fx-background-radius:8px;-fx-text-fill:#ffffff;-fx-font-weight:800;-fx-font-size:12px;-fx-padding:9px 18px;-fx-cursor:hand;");
        browseProjectsBtn.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            Stage stage = (Stage) browseProjectsBtn.getScene().getWindow();
            stage.setScene(new ExploreProjectsPage(() -> stage.setScene(getScene(back))).getExploreProjectsScene());
        });

        HBox btnRow = new HBox(browseProjectsBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox right = new VBox(10, badge, activeProjTitle, projDetails, btnRow);
        right.setPadding(new Insets(14, 18, 14, 18));
        right.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:12px;-fx-border-color:#ebdccb;-fx-border-width:1.5px;-fx-border-radius:12px;");
        right.setPrefWidth(480);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox h = new HBox(20, left, spacer, right);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setUserData(new Node[] { activeProjTitle, roleDetail, locationDetail, wageDetail, siteStatusDetail });

        VBox heroContainer = new VBox(h);
        heroContainer.setPadding(new Insets(22, 26, 22, 26));
        heroContainer.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:16px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),12,0,0,3px);");
        return heroContainer;
    }

    private HBox metrics() {
        walletMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        walletMetricSub.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#2e7d32;");

        attendanceMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        attendanceMetricSub.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");

        jobRequestsMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#1565c0;");

        dailyWageMetricValue.setText("Rs. " + worker.getDailyWage());
        dailyWageMetricValue.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        dailyWageMetricSub.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");

        HBox row = new HBox(18,
                kpiCard("TOTAL WAGES EARNED", walletMetricValue, walletMetricSub),
                kpiCard("DAYS WORKED", attendanceMetricValue, attendanceMetricSub),
                kpiCard("JOB INVITATIONS", jobRequestsMetricLabel, label("Pending recruiter requests", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1565c0;")),
                kpiCard("DAILY RATE", dailyWageMetricValue, dailyWageMetricSub)
        );
        for (Node n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }

    private VBox kpiCard(String title, Label numberNode, Label subtextNode) {
        Label heading = label(title, "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        VBox card = new VBox(6, heading, numberNode, subtextNode);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setPrefHeight(115);
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),14,0,0,4px);-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);"));
        return card;
    }

    private HBox lowerSections(String location, VBox heroContainer) {
        liveAttendancePanel = executivePanel("Daily Attendance & Wages",
                label("Loading attendance logs...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        historyPanel = executivePanel("Recent Work History",
                label("No completed work history yet.", "-fx-font-size:13px;-fx-text-fill:#4c4637;"),
                detail("Location", location), detail("Experience", value(worker.getExperience())));

        recruiterRequestsPanel = executivePanel("Job Invitations",
                label("Loading requests...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        pendingApplicationsPanel = executivePanel("Submitted Applications",
                label("Loading applications...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        notificationsPanel = executivePanel("Notifications",
                label("Loading notifications...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        VBox kyc = executivePanel("KYC & Verification",
                detail("Aadhaar Identity", "Verified"),
                detail("Bank Details", "Verified"),
                detail("Education", value(worker.getEducation())),
                detail("Skill Level", value(worker.getWorkerType())));

        VBox settings = executivePanel("Work Preferences",
                detail("Primary Skill", value(worker.getWorkerType())),
                detail("Specialization", value(worker.getSubSkill())),
                detail("Expected Daily Wage", "Rs. " + worker.getDailyWage() + " / day"),
                detail("Availability", "Immediate"));

        VBox left = new VBox(18, liveAttendancePanel, historyPanel, recruiterRequestsPanel, kyc);
        VBox right = new VBox(18, notificationsPanel, pendingApplicationsPanel, settings);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox h = new HBox(20, left, right);
        return h;
    }

    private void refreshWorkerData(VBox heroContainer) {
        if (isUpdating) return;
        isUpdating = true;

        new Thread(() -> {
            try {
                String workerId = worker.getId() != null && !worker.getId().isBlank()
                        ? worker.getId()
                        : (worker.getMobileNumber() != null ? worker.getMobileNumber() : SessionManager.getCurrentWorkerId());

                String workerMob = worker.getMobileNumber() != null ? worker.getMobileNumber() : workerId;

                List<JobApplication> apps = new JobApplicationController().getApplicationsByWorker(workerMob);
                List<Attendance> attendances = new AttendanceController().getAttendanceByWorker(workerMob);
                List<Project> allProjects = new ProjectController().getAllProjects();

                Map<String, Project> projectMap = new HashMap<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (p.getProjectId() != null) projectMap.put(p.getProjectId(), p);
                        if (p.getId() != null) projectMap.put(p.getId(), p);
                    }
                }

                int daysPresent = 0;
                int daysAbsent = 0;
                double effectiveDailyWage = worker.getDailyWage() > 0 ? worker.getDailyWage() : 800;
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

                // Check live wallet balance and verified days worked from Worker model / controller
                double liveBalance = worker.getWalletBalance() > 0 ? worker.getWalletBalance() : (daysPresent * effectiveDailyWage);
                int liveDaysWorked = worker.getTotalDaysWorked() > 0 ? worker.getTotalDaysWorked() : daysPresent;

                final int finalDaysPresent = liveDaysWorked;
                final double finalTotalEarned = liveBalance;
                final double finalDailyWage = effectiveDailyWage;
                final String finalTodayStatus = todayStatus;

                List<Notification> notifications = new NotificationController().getNotifications(workerMob);

                Platform.runLater(() -> {
                    // 1. Dynamic Metric Values (TOTAL WAGES EARNED & DAYS WORKED)
                    walletMetricValue.setText("Rs. " + String.format("%,.0f", finalTotalEarned));
                    walletMetricSub.setText("Rs. " + String.format("%,.0f", finalDailyWage) + "/day rate (" + finalDaysPresent + " days)");

                    attendanceMetricValue.setText(finalDaysPresent + " Days");
                    if ("Present".equalsIgnoreCase(finalTodayStatus)) {
                        attendanceMetricSub.setText("Marked Present Today");
                        attendanceMetricSub.setStyle("-fx-font-size:12px;-fx-text-fill:#2e7d32;-fx-font-weight:800;");
                    } else if ("Absent".equalsIgnoreCase(finalTodayStatus)) {
                        attendanceMetricSub.setText("Marked Absent Today");
                        attendanceMetricSub.setStyle("-fx-font-size:12px;-fx-text-fill:#ba1a1a;-fx-font-weight:800;");
                    } else {
                        attendanceMetricSub.setText("Today's attendance pending");
                        attendanceMetricSub.setStyle("-fx-font-size:12px;-fx-text-fill:#735c00;");
                    }

                    // 2. Notifications Panel
                    if (notificationsPanel != null) {
                        notificationsPanel.getChildren().clear();
                        notificationsPanel.getChildren().add(panelHeader("Notifications"));

                        if (notifications == null || notifications.isEmpty()) {
                            notificationsPanel.getChildren().addAll(
                                    label("No notifications yet", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Updates regarding your applications and job offers will appear here.", "-fx-font-size:12px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            int count = 0;
                            for (Notification n : notifications) {
                                if (n.getNotificationId() != null && !seenNotifIds.contains(n.getNotificationId())) {
                                    seenNotifIds.add(n.getNotificationId());
                                }

                                if (count++ >= 6) continue;

                                Label titleLbl = label(n.getTitle() != null ? n.getTitle() : "Notification",
                                        "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                                Label msgLbl = label(n.getMessage() != null ? n.getMessage() : "",
                                        "-fx-font-size:12px;-fx-text-fill:#4d4635;");
                                msgLbl.setWrapText(true);

                                VBox notifCard = new VBox(4, titleLbl, msgLbl);
                                notifCard.setPadding(new Insets(10, 12, 10, 12));
                                notifCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                notifCard.setOnMouseEntered(e -> notifCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                notifCard.setOnMouseExited(e -> notifCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));
                                notificationsPanel.getChildren().add(notifCard);
                            }
                        }
                    }

                    // 3. Daily Attendance & Wages Panel
                    if (liveAttendancePanel != null) {
                        liveAttendancePanel.getChildren().clear();
                        liveAttendancePanel.getChildren().add(panelHeader("Daily Attendance & Wages"));

                        if (attendances == null || attendances.isEmpty()) {
                            liveAttendancePanel.getChildren().addAll(
                                    label("No attendance logged yet", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Daily logs recorded by your project supervisor will appear here.", "-fx-font-size:12px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            int count = 0;
                            for (Attendance att : attendances) {
                                if (count++ >= 6) break;
                                boolean isPresent = "Present".equalsIgnoreCase(att.getStatus());
                                Label dateLabel = label(formatDate(att.getDate()), "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

                                Label statusBadge = new Label(isPresent ? "PRESENT" : "ABSENT");
                                statusBadge.setStyle(isPresent
                                        ? "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:6px;-fx-padding:3px 8px;"
                                        : "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;-fx-background-color:#ffebee;-fx-background-radius:6px;-fx-padding:3px 8px;");

                                String siteName = "DIHADI Project Site";
                                if (att.getProjectId() != null && projectMap.containsKey(att.getProjectId())) {
                                    Project p = projectMap.get(att.getProjectId());
                                    if (p.getTitle() != null && !p.getTitle().isBlank()) {
                                        siteName = p.getTitle();
                                    } else if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                                        siteName = p.getProjectName();
                                    }
                                }
                                Label siteLabel = label("Site: " + siteName, "-fx-font-size:12px;-fx-text-fill:#4c4637;");

                                Label wageBadge = new Label(isPresent ? "+ Rs. " + String.format("%,.0f", (att.getPaidAmount() > 0 ? att.getPaidAmount() : finalDailyWage)) : "Rs. 0");
                                wageBadge.setStyle(isPresent
                                        ? "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#2e7d32;"
                                        : "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#ba1a1a;");

                                Region spacer = new Region();
                                HBox.setHgrow(spacer, Priority.ALWAYS);

                                HBox topRow = new HBox(10, dateLabel, statusBadge, spacer, wageBadge);
                                topRow.setAlignment(Pos.CENTER_LEFT);

                                VBox attCard = new VBox(4, topRow, siteLabel);
                                attCard.setPadding(new Insets(10, 12, 10, 12));
                                attCard.setStyle(isPresent
                                        ? "-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;"
                                        : "-fx-background-color:#fff5f5;-fx-background-radius:8px;-fx-border-color:#ffcdd2;-fx-border-width:1px;-fx-border-radius:8px;");
                                attCard.setOnMouseEntered(e -> attCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                attCard.setOnMouseExited(e -> attCard.setStyle(isPresent
                                        ? "-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;"
                                        : "-fx-background-color:#fff5f5;-fx-background-radius:8px;-fx-border-color:#ffcdd2;-fx-border-width:1px;-fx-border-radius:8px;"));
                                liveAttendancePanel.getChildren().add(attCard);
                            }
                        }
                    }

                    // 4. Job Invitations & Applications Panels
                    if (recruiterRequestsPanel != null && pendingApplicationsPanel != null && historyPanel != null) {
                        recruiterRequestsPanel.getChildren().clear();
                        recruiterRequestsPanel.getChildren().add(panelHeader("Job Invitations"));

                        pendingApplicationsPanel.getChildren().clear();
                        pendingApplicationsPanel.getChildren().add(panelHeader("Submitted Applications"));

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
                                        Button acceptBtn = new Button("Accept");
                                        acceptBtn.setStyle("-fx-background-color:#2e7d32;-fx-background-radius:6px;-fx-text-fill:#ffffff;-fx-font-weight:800;-fx-font-size:11px;-fx-padding:6px 14px;-fx-cursor:hand;");

                                        Button declineBtn = new Button("Decline");
                                        declineBtn.setStyle("-fx-background-color:transparent;-fx-border-color:#ba1a1a;-fx-border-radius:6px;-fx-background-radius:6px;-fx-text-fill:#ba1a1a;-fx-font-weight:800;-fx-font-size:11px;-fx-padding:5px 12px;-fx-cursor:hand;");

                                        HBox actionBox = new HBox(8, acceptBtn, declineBtn);
                                        actionBox.setAlignment(Pos.CENTER_RIGHT);

                                        VBox reqCard = new VBox(6,
                                                label(app.getJobTitle(), "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                                                label("Location: " + app.getJobLocation() + "  |  Daily Wage: Rs. " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                                actionBox);
                                        reqCard.setPadding(new Insets(10, 12, 10, 12));
                                        reqCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                        reqCard.setOnMouseEntered(e -> reqCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                        reqCard.setOnMouseExited(e -> reqCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));

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
                                                    NotificationToast.show("Offer Accepted", "You accepted the hiring offer for " + currentApp.getJobTitle() + ".", NotificationToast.ToastType.SUCCESS);
                                                    refreshWorkerData(heroContainer);
                                                });
                                            }).start();
                                        });

                                        declineBtn.setOnAction(ev -> {
                                            acceptBtn.setDisable(true);
                                            declineBtn.setDisable(true);
                                            new Thread(() -> {
                                                currentApp.setStatus("Declined");
                                                new JobApplicationController().saveApplication(currentApp);
                                                Platform.runLater(() -> {
                                                    NotificationToast.show("Offer Declined", "The offer for " + currentApp.getJobTitle() + " was declined.", NotificationToast.ToastType.INFO);
                                                    refreshWorkerData(heroContainer);
                                                });
                                            }).start();
                                        });

                                        recruiterRequestsPanel.getChildren().add(reqCard);
                                    } else {
                                        appCount++;
                                        VBox appCard = new VBox(4,
                                                label(app.getJobTitle(), "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                                                label("Location: " + app.getJobLocation() + "  |  Daily Wage: Rs. " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                                label("Status: Under Review", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#1565c0;"));
                                        appCard.setPadding(new Insets(10, 12, 10, 12));
                                        appCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                        appCard.setOnMouseEntered(e -> appCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                        appCard.setOnMouseExited(e -> appCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));
                                        pendingApplicationsPanel.getChildren().add(appCard);
                                    }
                                }
                            }
                        }

                        jobRequestsMetricLabel.setText(String.valueOf(reqCount));

                        if (reqCount == 0) {
                            recruiterRequestsPanel.getChildren().add(label("No direct recruiter job offers currently.", "-fx-font-size:12px;-fx-text-fill:#685c52;"));
                        }
                        if (appCount == 0) {
                            pendingApplicationsPanel.getChildren().add(label("No pending job applications.", "-fx-font-size:12px;-fx-text-fill:#685c52;"));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isUpdating = false;
            }
        }).start();
    }

    private void updateActiveProjectCard(Node[] details, JobApplication app) {
        if (details == null || details.length < 5) return;
        Label title = (Label) details[0];
        VBox role = (VBox) details[1];
        VBox loc = (VBox) details[2];
        VBox wage = (VBox) details[3];
        VBox status = (VBox) details[4];

        title.setText(app.getJobTitle() != null ? app.getJobTitle() : "Active Project Site");
        setDetailValue(role, value(worker.getWorkerType(), "Worker"));
        setDetailValue(loc, app.getJobLocation() != null ? app.getJobLocation() : "Pune, Maharashtra");
        setDetailValue(wage, "Rs. " + app.getJobWage() + " / day");
        setDetailValue(status, "Active");
    }

    private void addWorkHistoryCard(VBox historyPanel, JobApplication app) {
        VBox card = new VBox(4,
                label(app.getJobTitle() != null ? app.getJobTitle() : "Completed Project", "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                label("Location: " + (app.getJobLocation() != null ? app.getJobLocation() : "Pune") + "  |  Daily Wage: Rs. " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                label("Status: Completed", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#2e7d32;")
        );
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;"));
        historyPanel.getChildren().add(card);
    }

    private VBox executivePanel(String heading, Node... nodes) {
        VBox panel = new VBox(10);
        panel.getChildren().add(panelHeader(heading));
        for (Node n : nodes) {
            panel.getChildren().add(n);
        }
        panel.setPadding(new Insets(18, 20, 18, 20));
        panel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);");
        panel.setOnMouseEntered(e -> panel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.20),14,0,0,4px);"));
        panel.setOnMouseExited(e -> panel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);"));
        return panel;
    }

    private Label panelHeader(String text) {
        Label l = label(text, "-fx-font-family:Georgia;-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        l.setPadding(new Insets(0, 0, 6, 0));
        return l;
    }

    private VBox detail(String k, String v) {
        Label kl = label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        Label vl = label(v, "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        return new VBox(2, kl, vl);
    }

    private void setDetailValue(VBox detailBox, String value) {
        if (detailBox != null && detailBox.getChildren().size() > 1) {
            Label vl = (Label) detailBox.getChildren().get(1);
            vl.setText(value);
        }
    }

    private HBox footer() {
        Label f = label("DIHADI ~ Meri Dihadi Mera Haq  |  Secure Direct Payments Powered by Razorpay",
                "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        HBox h = new HBox(f);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(16, 0, 0, 0));
        return h;
    }

    private Label label(String text, String style) {
        Label l = new Label(text);
        l.setStyle(style);
        return l;
    }

    private ImageView image(String path, double w, double h) {
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            ImageView iv = new ImageView();
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            return iv;
        }
    }

    private String value(String s) {
        return value(s, "Not provided");
    }

    private String value(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s.trim();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return "Today";
        try {
            LocalDate d = LocalDate.parse(dateStr);
            return d.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return dateStr;
        }
    }
}
