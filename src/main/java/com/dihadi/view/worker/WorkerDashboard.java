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
import javafx.scene.layout.StackPane;
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
        content.setPrefWidth(1360);
        content.setAlignment(Pos.TOP_CENTER);

        StackPane centerWrapper = new StackPane(content);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setStyle("-fx-background-color:#f3e7ce;");

        ScrollPane scroll = new ScrollPane(centerWrapper);
        com.dihadi.view.ScrollUtils.style(scroll);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");

        refreshWorkerData(heroContainer);

        liveRefresher = new Timeline(new KeyFrame(Duration.seconds(25), e -> refreshWorkerData(heroContainer)));
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

        HBox navBar = new HBox(18, navHome);
        navBar.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("←  Back");
        backBtn.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:10px 4px;-fx-cursor:hand;");
        backBtn.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            Stage stage = (Stage) backBtn.getScene().getWindow();
            Worker currentWorker = SessionManager.currentWorker != null ? SessionManager.currentWorker : worker;
            stage.setScene(new WorkerPage(currentWorker).getWorkerScene(
                    (back != null) ? back : () -> com.dihadi.view.AppNavigator.open(stage, "Home")));
        });

        Label userPill = label(name + " (" + category + ")",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#3f392e;-fx-background-color:#fffaf0;-fx-background-radius:16px;-fx-border-color:#d0c5af;-fx-border-radius:16px;-fx-padding:0 20px;");
        userPill.setMinHeight(54);
        userPill.setPrefHeight(54);
        userPill.setAlignment(Pos.CENTER);

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setMinHeight(54);
        logoutBtn.setPrefHeight(54);
        logoutBtn.setStyle(
                "-fx-background-color:#fff1f1;-fx-background-radius:16px;-fx-border-color:#f1bcbc;-fx-border-radius:16px;-fx-text-fill:#a51d1d;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:0 20px;-fx-cursor:hand;");
        logoutBtn.setOnAction(e -> {
            if (liveRefresher != null) liveRefresher.stop();
            SessionManager.clearAllSessions();
            Stage stage = null;
            if (logoutBtn.getScene() != null && logoutBtn.getScene().getWindow() instanceof Stage s) {
                stage = s;
            } else {
                for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                    if (w instanceof Stage s && s.isShowing()) {
                        stage = s;
                        break;
                    }
                }
            }
            NotificationToast.show("Signed Out", "You have signed out of your worker session.", NotificationToast.ToastType.INFO);
            if (stage != null) {
                com.dihadi.view.AppNavigator.open(stage, "Home");
            }
        });

        HBox accountActions = new HBox(10, backBtn, userPill, logoutBtn);
        accountActions.setAlignment(Pos.CENTER_RIGHT);

        HBox h = new HBox(20, brandLockup, navBar, spacer, accountActions);
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
        Label activeProjTitle = label("No Active Project Assigned",
                "-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        VBox roleDetail = detail("Role", category);
        VBox locationDetail = detail("Site Location", location);
        VBox wageDetail = detail("Daily Wage", "Rs. " + String.format("%,d", worker.getDailyWage() > 0 ? worker.getDailyWage() : 950) + " / day");
        VBox siteStatusDetail = detail("Status", "Available");

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
                if ((workerMob == null || workerMob.isBlank() || "WRK_DEFAULT".equals(workerMob)) && SessionManager.currentWorker != null) {
                    if (SessionManager.currentWorker.getMobileNumber() != null && !SessionManager.currentWorker.getMobileNumber().isBlank()) {
                        workerMob = SessionManager.currentWorker.getMobileNumber();
                    }
                }
                String workerFullName = ((worker.getFirstName() != null ? worker.getFirstName() : "") + " " +
                                        (worker.getLastName() != null ? worker.getLastName() : "")).trim();

                List<JobApplication> apps = new JobApplicationController().getApplicationsForWorker(workerMob, workerFullName, worker.getWorkerType());
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

                    // 4. Job Invitations, Applications Panels & Recent Work History
                    if (recruiterRequestsPanel != null && pendingApplicationsPanel != null && historyPanel != null) {
                        recruiterRequestsPanel.getChildren().clear();
                        recruiterRequestsPanel.getChildren().add(panelHeader("Job Invitations"));

                        pendingApplicationsPanel.getChildren().clear();
                        pendingApplicationsPanel.getChildren().add(panelHeader("Submitted Applications"));

                        historyPanel.getChildren().clear();
                        historyPanel.getChildren().add(panelHeader("Recent Work History"));

                        int reqCount = 0;
                        int appCount = 0;
                        JobApplication activeApp = null;
                        List<JobApplication> completedApps = new ArrayList<>();
                        java.util.Set<String> seenCompletedProjects = new java.util.HashSet<>();

                        if (apps != null) {
                            for (JobApplication app : apps) {
                                boolean isDirectRequest = (app.getJobTitle() != null && app.getJobTitle().contains("Hiring Request"))
                                        || "DIRECT_HIRE".equalsIgnoreCase(app.getRequirementId());

                                Project proj = (app.getProjectId() != null) ? projectMap.get(app.getProjectId()) : null;
                                boolean isCompleted = "Completed".equalsIgnoreCase(app.getStatus())
                                        || (proj != null && "Completed".equalsIgnoreCase(proj.getStatus()));

                                if (isCompleted) {
                                    String projKey = app.getProjectId() != null && !app.getProjectId().isBlank()
                                            ? app.getProjectId()
                                            : extractProjectName(app, projectMap);
                                    if (seenCompletedProjects.add(projKey)) {
                                        completedApps.add(app);
                                    }
                                } else if ("Accepted".equalsIgnoreCase(app.getStatus())) {
                                    if (activeApp == null) {
                                        activeApp = app;
                                    } else if (app.getTimestamp() != null && activeApp.getTimestamp() != null) {
                                        if (app.getTimestamp().after(activeApp.getTimestamp())) {
                                            activeApp = app;
                                        }
                                    } else if (app.getApplicationId() != null && activeApp.getApplicationId() != null
                                            && app.getApplicationId().compareTo(activeApp.getApplicationId()) > 0) {
                                        activeApp = app;
                                    }
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
                                                JobApplication activeAssignment = new JobApplicationController().getActiveAssignedApplicationForWorker(worker.getMobileNumber());
                                                if (activeAssignment != null && activeAssignment.getApplicationId() != null
                                                        && !activeAssignment.getApplicationId().equals(currentApp.getApplicationId())
                                                        && activeAssignment.getProjectId() != null
                                                        && !activeAssignment.getProjectId().equals(currentApp.getProjectId())) {
                                                    try {
                                                        Project activeAssignedProj = new ProjectController().getProject(activeAssignment.getProjectId());
                                                        if (activeAssignedProj != null && ("Active".equalsIgnoreCase(activeAssignedProj.getStatus()) || "Unavailable".equalsIgnoreCase(activeAssignedProj.getStatus()))) {
                                                            String activeProj = activeAssignedProj.getProjectName() != null ? activeAssignedProj.getProjectName() : activeAssignment.getJobTitle();
                                                            Platform.runLater(() -> {
                                                                acceptBtn.setDisable(false);
                                                                declineBtn.setDisable(false);
                                                                NotificationToast.show("Already Assigned", "You are already assigned to active project '" + activeProj + "'. A worker can only be assigned to one project at a time.", NotificationToast.ToastType.ALERT);
                                                            });
                                                            return;
                                                        }
                                                    } catch (Exception ignored) {}
                                                }

                                                // Complete any prior accepted assignments for this worker
                                                List<JobApplication> allWorkerApps = new JobApplicationController().getApplicationsByWorker(worker.getMobileNumber());
                                                if (allWorkerApps != null) {
                                                    for (JobApplication oldApp : allWorkerApps) {
                                                        if ("Accepted".equalsIgnoreCase(oldApp.getStatus())
                                                                && oldApp.getApplicationId() != null
                                                                && !oldApp.getApplicationId().equals(currentApp.getApplicationId())) {
                                                            oldApp.setStatus("Completed");
                                                            new JobApplicationController().saveApplication(oldApp);
                                                        }
                                                    }
                                                }

                                                currentApp.setStatus("Accepted");
                                                String cleanProjectName = extractProjectName(currentApp, projectMap);
                                                currentApp.setJobTitle(cleanProjectName);
                                                new JobApplicationController().saveApplication(currentApp);

                                                if (currentApp.getProjectId() != null && !currentApp.getProjectId().isBlank()) {
                                                    new com.dihadi.controller.ProjectController().updateProjectStatus(currentApp.getProjectId(), "Requirement Fulfilled");
                                                }

                                                String acceptedWorkerName = ((worker.getFirstName() != null ? worker.getFirstName() : "") + " " +
                                                        (worker.getLastName() != null ? worker.getLastName() : "")).trim();
                                                if (acceptedWorkerName.isEmpty()) acceptedWorkerName = "Worker (" + worker.getMobileNumber() + ")";
                                                new NotificationController().notifyRecruiterHiringAccepted(
                                                        currentApp,
                                                        acceptedWorkerName,
                                                        worker.getMobileNumber()
                                                );
                                                Platform.runLater(() -> {
                                                    NotificationToast.show("Offer Accepted", "You accepted the hiring offer for " + cleanProjectName + ". Project status is now set to Requirement Fulfilled.", NotificationToast.ToastType.SUCCESS);
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

                        // Populate Recent Work History (deduplicated, single time)
                        if (completedApps.isEmpty()) {
                            String category = value(worker.getWorkerType(), "Skilled Worker");
                            String location = (value(worker.getCity()) + ", " + value(worker.getState()))
                                    .replace("Not provided, Not provided", "Pune, Maharashtra");
                            historyPanel.getChildren().addAll(
                                    label("No completed work history yet.", "-fx-font-size:13px;-fx-text-fill:#4c4637;"),
                                    detail("Location", location),
                                    detail("Experience", value(worker.getExperience()))
                            );
                        } else {
                            for (JobApplication cApp : completedApps) {
                                addWorkHistoryCard(historyPanel, cApp, projectMap);
                            }
                        }

                        // Update or reset the Hero Assigned Project card
                        if (heroContainer != null && !heroContainer.getChildren().isEmpty()) {
                            HBox heroHBox = (HBox) heroContainer.getChildren().get(0);
                            Node[] details = (Node[]) heroHBox.getUserData();
                            if (activeApp != null) {
                                updateActiveProjectCard(details, activeApp, projectMap);
                            } else {
                                String category = value(worker.getWorkerType(), "Skilled Worker");
                                String location = (value(worker.getCity()) + ", " + value(worker.getState()))
                                        .replace("Not provided, Not provided", "Pune, Maharashtra");
                                resetActiveProjectCard(details, category, location);
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

    private String extractProjectName(JobApplication app, Map<String, Project> projectMap) {
        if (app == null) return "Active Project Site";

        if (app.getProjectId() != null && !app.getProjectId().isBlank()) {
            if (projectMap != null && projectMap.containsKey(app.getProjectId())) {
                Project p = projectMap.get(app.getProjectId());
                if (p != null) {
                    if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                        return p.getProjectName().trim();
                    } else if (p.getTitle() != null && !p.getTitle().isBlank()) {
                        return p.getTitle().trim();
                    }
                }
            }
            try {
                Project p = new ProjectController().getProject(app.getProjectId());
                if (p != null) {
                    if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                        return p.getProjectName().trim();
                    } else if (p.getTitle() != null && !p.getTitle().isBlank()) {
                        return p.getTitle().trim();
                    }
                }
            } catch (Exception ignored) {}
        }

        String jt = app.getJobTitle();
        if (jt != null && !jt.isBlank()) {
            if (jt.contains(" for ")) {
                return jt.substring(jt.lastIndexOf(" for ") + 5).trim();
            } else if (jt.startsWith("Hiring Request:")) {
                return jt.substring("Hiring Request:".length()).trim();
            } else if (jt.contains("Hiring Request")) {
                return jt.replace("Hiring Request", "").replaceAll("^[:\\-\\s]+", "").trim();
            }
            return jt.trim();
        }

        return "Active Project Site";
    }

    private void updateActiveProjectCard(Node[] details, JobApplication app, Map<String, Project> projectMap) {
        if (details == null || details.length < 5) return;
        Label title = (Label) details[0];
        VBox role = (VBox) details[1];
        VBox loc = (VBox) details[2];
        VBox wage = (VBox) details[3];
        VBox status = (VBox) details[4];

        String cleanProjectName = extractProjectName(app, projectMap);
        title.setText(cleanProjectName);
        setDetailValue(role, value(worker.getWorkerType(), "Worker"));
        setDetailValue(loc, app.getJobLocation() != null ? app.getJobLocation() : "Pune, Maharashtra");
        setDetailValue(wage, "Rs. " + app.getJobWage() + " / day");
        setDetailValue(status, "Active");
    }

    private void resetActiveProjectCard(Node[] details, String category, String location) {
        if (details == null || details.length < 5) return;
        Label title = (Label) details[0];
        VBox role = (VBox) details[1];
        VBox loc = (VBox) details[2];
        VBox wage = (VBox) details[3];
        VBox status = (VBox) details[4];

        title.setText("No Active Project Assigned");
        setDetailValue(role, category);
        setDetailValue(loc, location);
        setDetailValue(wage, "Rs. " + String.format("%,d", worker.getDailyWage() > 0 ? worker.getDailyWage() : 950) + " / day");
        setDetailValue(status, "Available");
    }

    private void addWorkHistoryCard(VBox historyPanel, JobApplication app, Map<String, Project> projectMap) {
        String cleanProjectName = extractProjectName(app, projectMap);
        VBox card = new VBox(4,
                label(cleanProjectName, "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
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
