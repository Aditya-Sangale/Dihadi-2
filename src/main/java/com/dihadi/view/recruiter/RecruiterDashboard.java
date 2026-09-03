package com.dihadi.view.recruiter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.NotificationController;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.RazorpayService;
import com.dihadi.controller.RecruiterController;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Notification;
import com.dihadi.view.NotificationToast;
import com.dihadi.model.Project;
import com.dihadi.model.Recruiter;
import com.dihadi.view.PaymentGateway.PaymentCheckoutScene;
import com.dihadi.view.SessionManager;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Recruiter & Contractor Dashboard with interactive card hover effects and clean, realistic content.
 */
public class RecruiterDashboard {
    private static final String GOLD = "#735c00", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private final Recruiter recruiter;
    private Timeline livePoller;

    private VBox activeOngoingPanel;
    private VBox activeProjPanel;
    private VBox upcomingProjPanel;
    private VBox pastProjPanel;
    private VBox reqPanel;
    private VBox notificationsPanel;

    private final java.util.Set<String> seenRecruiterNotifIds = new java.util.HashSet<>();
    private boolean isUpdating = false;

    public RecruiterDashboard(Recruiter recruiter) {
        this.recruiter = recruiter != null ? recruiter : (SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : new Recruiter());
    }

    public Scene getScene(Runnable back) {
        Recruiter currentR = SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : recruiter;
        String first = val(currentR.getFirstName(), "Recruiter");
        String name = (first + (blank(currentR.getLastName()) ? "" : " " + currentR.getLastName())).trim();
        String company = val(currentR.getCompanyName(), "Construction Group");

        HBox headerBar = createHeader(currentR, name, company, back);
        HBox heroRow = createHero(currentR, name, company, back);
        HBox metricsRow = createMetrics(currentR, back);
        HBox bodyRow = createBody(currentR, back);

        VBox content = new VBox(24,
                headerBar,
                heroRow,
                metricsRow,
                bodyRow,
                createFooter()
        );
        content.setPadding(new Insets(24, 48, 40, 48));
        content.setMaxWidth(1360);
        content.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");

        pollRecruiterLiveState(currentR, back);

        livePoller = new Timeline(new KeyFrame(Duration.seconds(3), e -> pollRecruiterLiveState(currentR, back)));
        livePoller.setCycleCount(Timeline.INDEFINITE);
        livePoller.play();

        return new Scene(scroll, 1400, 850);
    }

    private HBox createHeader(Recruiter currentR, String name, String company, Runnable back) {
        ImageView logoImg = image("/assets/logo/dihadi logo.jpeg", 44, 44);
        Label logo = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logoImg, logo);
        brand.setAlignment(Pos.CENTER_LEFT);

        Button overview = nav("Overview", true);
        Button attendance = nav("Attendance", false);
        attendance.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            Stage stage = (Stage) attendance.getScene().getWindow();
            stage.setScene(new AttendancePage(currentR).getScene(() -> stage.setScene(getScene(back))));
        });

        Button pendingApprovals = nav("Pending Approvals", false);
        pendingApprovals.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            Stage stage = (Stage) pendingApprovals.getScene().getWindow();
            stage.setScene(new PendingApprovalsPage(currentR).getScene(() -> stage.setScene(getScene(back))));
        });

        HBox navBar = new HBox(18, overview, attendance, pendingApprovals);
        navBar.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label profileBadge = label(name + " (" + company + ")",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#faf3e8;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-padding:7px 16px;");

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:20px;-fx-border-color:#ffcdd2;-fx-border-radius:20px;-fx-text-fill:#ba1a1a;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
        logoutBtn.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            SessionManager.clearAllSessions();
            NotificationToast.show("Signed Out", "You have signed out of your recruiter session.", NotificationToast.ToastType.INFO);
            back.run();
        });

        Button hireWorkersBtn = new Button("<");
        hireWorkersBtn.setPrefSize(54, 52);
        hireWorkersBtn.setMinSize(54, 52);
        hireWorkersBtn.setMaxSize(54, 52);
        hireWorkersBtn.setStyle(
                "-fx-background-color:#ead7ad;-fx-background-radius:16px;-fx-text-fill:#4c4637;-fx-font-size:24px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:0 0 3px 0;-fx-cursor:hand;");
        hireWorkersBtn.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            Stage stage = (Stage) hireWorkersBtn.getScene().getWindow();
            stage.setScene(new HireSuitableSkilledWorkersPage().getHireWorkersScene(
                    () -> stage.setScene(new RecruiterPage().getRecruiterScene(
                            () -> com.dihadi.view.AppNavigator.open(stage, "Home")))));
        });

        HBox headerBar = new HBox(20, brand, navBar, spacer, profileBadge, hireWorkersBtn, logoutBtn);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(0, 0, 14, 0));
        headerBar.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return headerBar;
    }

    private HBox createHero(Recruiter currentR, String name, String company, Runnable back) {
        Label welcomeLabel = label("Welcome, " + name,
                "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        Label companyLabel = label(company + "  |  " + val(currentR.getBusinessType(), "General Contractor"),
                "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";");

        Label subtextLabel = label("Manage your active project sites, assigned workforce, candidate worker approvals, and wallet balance from one place.",
                "-fx-font-size:14px;-fx-text-fill:#4c4637;-fx-line-spacing:2px;");
        subtextLabel.setMaxWidth(440);
        subtextLabel.setWrapText(true);

        VBox welcomeBox = new VBox(8, welcomeLabel, companyLabel, subtextLabel);
        welcomeBox.setPrefWidth(480);

        Label activeProjTag = label("ACTIVE PROJECT", "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Label activeProjTitle = label("Loading active project...",
                "-fx-font-family:Georgia;-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        activeProjTitle.setWrapText(true);

        activeOngoingPanel = new VBox(10, activeProjTag, activeProjTitle);
        activeOngoingPanel.setPadding(new Insets(18, 22, 16, 22));
        activeOngoingPanel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),10,0,0,2px);");
        activeOngoingPanel.setOnMouseEntered(e -> activeOngoingPanel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),14,0,0,4px);-fx-cursor:hand;"));
        activeOngoingPanel.setOnMouseExited(e -> activeOngoingPanel.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),10,0,0,2px);"));
        HBox.setHgrow(activeOngoingPanel, Priority.ALWAYS);

        HBox heroRow = new HBox(28, welcomeBox, activeOngoingPanel);
        heroRow.setAlignment(Pos.CENTER_LEFT);
        heroRow.setPadding(new Insets(22, 26, 22, 26));
        heroRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:16px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),12,0,0,3px);");
        return heroRow;
    }

    private Label walletBalanceLabel;
    private Label workersMetricLabel;
    private Label workersMetricSub;
    private Label projectsMetricLabel;
    private Label reqCountLabel;

    private HBox createMetrics(Recruiter currentR, Runnable back) {
        walletBalanceLabel = label(String.format("Rs. %,.2f", currentR.getWalletBalance()),
                "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Button addFundsBtn = new Button("+ Add Funds");
        addFundsBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:5px 12px;-fx-cursor:hand;");
        addFundsBtn.setOnAction(e -> handleAddFunds(currentR, addFundsBtn, walletBalanceLabel, back));

        HBox walletHeadRow = new HBox(8, label("WALLET BALANCE", "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;"), spacer(), addFundsBtn);
        walletHeadRow.setAlignment(Pos.CENTER_LEFT);

        VBox walletMetric = new VBox(6, walletHeadRow, walletBalanceLabel, label("Available deposit balance", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#2e7d32;"));
        walletMetric.setPadding(new Insets(16, 18, 16, 18));
        walletMetric.setPrefHeight(115);
        walletMetric.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);");
        walletMetric.setOnMouseEntered(e -> walletMetric.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),14,0,0,4px);-fx-cursor:hand;"));
        walletMetric.setOnMouseExited(e -> walletMetric.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);"));

        workersMetricLabel = label("0", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        workersMetricSub = label("Workers on active project", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        VBox workersMetric = kpiCard("ASSIGNED WORKERS", workersMetricLabel, workersMetricSub);

        projectsMetricLabel = label("0", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#1565c0;");
        VBox projectsMetric = kpiCard("TOTAL PROJECTS", projectsMetricLabel, label("Created projects", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1565c0;"));

        reqCountLabel = label("0", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;");
        VBox approvalsMetric = kpiCard("PENDING APPROVALS", reqCountLabel, label("Worker applications", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#ba1a1a;"));

        HBox metricsRow = new HBox(18, walletMetric, workersMetric, projectsMetric, approvalsMetric);
        for (Node n : metricsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return metricsRow;
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

    private HBox createBody(Recruiter currentR, Runnable back) {
        activeProjPanel = executivePanel("Active Projects", label("Loading...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
        upcomingProjPanel = executivePanel("Upcoming Projects", label("Loading upcoming projects...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
        pastProjPanel = executivePanel("Past Completed Projects", label("Loading past projects...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        reqPanel = executivePanel("Pending Worker Applications", label("Loading candidates...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
        notificationsPanel = executivePanel("Notifications", label("Loading updates...", "-fx-font-size:13px;-fx-text-fill:#685c52;"));

        VBox recruiterProfilePanel = executivePanel("Company Profile",
                detail("Contact Person", val(currentR.getFirstName()) + " " + val(currentR.getLastName())),
                detail("Mobile Number", val(currentR.getMobileNumber(), "Not provided")),
                detail("Email Address", val(currentR.getEmail(), "Not provided")),
                detail("Business Structure", val(currentR.getBusinessType(), "General Contractor")),
                detail("Account Status", "Active"));

        VBox leftBody = new VBox(18, activeProjPanel, upcomingProjPanel, pastProjPanel);
        VBox rightBody = new VBox(18, reqPanel, notificationsPanel, recruiterProfilePanel);
        HBox.setHgrow(leftBody, Priority.ALWAYS);
        HBox.setHgrow(rightBody, Priority.ALWAYS);

        HBox bodyRow = new HBox(20, leftBody, rightBody);
        return bodyRow;
    }

    private void pollRecruiterLiveState(Recruiter currentR, Runnable back) {
        if (isUpdating) return;
        isUpdating = true;

        new Thread(() -> {
            try {
                String fullName = (val(currentR.getFirstName(), "Recruiter") + " " + val(currentR.getLastName(), "")).trim();
                String company = val(currentR.getCompanyName(), "Construction Group");

                List<Project> allProjects = new ProjectController().getAllProjects();
                List<JobApplication> allApps = new JobApplicationController().getAllApplications();
                List<Notification> recruiterNotifs = new NotificationController().getNotifications(currentR.getMobileNumber());

                List<Project> recruiterProjects = new ArrayList<>();
                Project activeProj = null;
                List<Project> upcoming = new ArrayList<>();
                List<Project> completed = new ArrayList<>();

                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (isMatch(p, currentR, currentR.getCompanyName())
                                || isMatch(p, currentR, currentR.getMobileNumber())
                                || isMatch(p, currentR, currentR.getEmail())) {
                            recruiterProjects.add(p);
                            if ("Active".equalsIgnoreCase(p.getStatus())) {
                                if (activeProj == null) activeProj = p;
                            } else if ("Upcoming".equalsIgnoreCase(p.getStatus())) {
                                upcoming.add(p);
                            } else if ("Completed".equalsIgnoreCase(p.getStatus())) {
                                completed.add(p);
                            }
                        }
                    }
                }

                if (activeProj == null && !recruiterProjects.isEmpty()) {
                    activeProj = recruiterProjects.get(0);
                }

                final Project finalActiveProj = activeProj;
                final List<Project> finalUpcoming = upcoming;
                final List<Project> finalCompleted = completed;
                final int finalProjCount = recruiterProjects.size();

                int assignedWorkersCount = 0;
                if (allApps != null && finalActiveProj != null && finalActiveProj.getProjectId() != null) {
                    assignedWorkersCount = (int) allApps.stream()
                            .filter(a -> finalActiveProj.getProjectId().equals(a.getProjectId()) && "Accepted".equalsIgnoreCase(a.getStatus()))
                            .map(JobApplication::getWorkerMobile)
                            .filter(m -> m != null && !m.isBlank())
                            .distinct()
                            .count();
                }
                final int finalAssignedWorkers = assignedWorkersCount;

                java.util.Set<String> recruiterProjIds = new java.util.HashSet<>();
                for (Project p : recruiterProjects) {
                    if (p.getProjectId() != null && !p.getProjectId().isBlank()) {
                        recruiterProjIds.add(p.getProjectId());
                    }
                }

                String rMobDigits = currentR.getMobileNumber() != null ? currentR.getMobileNumber().replaceAll("\\D", "") : "";

                Map<String, JobApplication> deduplicatedPending = new java.util.LinkedHashMap<>();
                if (allApps != null) {
                    for (JobApplication a : allApps) {
                        if ("Pending".equalsIgnoreCase(a.getStatus())) {
                            if (a.getProjectId() != null && recruiterProjIds.contains(a.getProjectId())) {
                                String key = (a.getWorkerMobile() != null ? a.getWorkerMobile().replaceAll("\\D", "") : "") + "_" + a.getProjectId();
                                deduplicatedPending.putIfAbsent(key, a);
                            } else {
                                String appRMob = a.getRecruiterMobile() != null ? a.getRecruiterMobile().replaceAll("\\D", "") : "";
                                if (!rMobDigits.isEmpty() && !appRMob.isEmpty() && (appRMob.equals(rMobDigits) || appRMob.endsWith(rMobDigits) || rMobDigits.endsWith(appRMob))) {
                                    String key = (a.getWorkerMobile() != null ? a.getWorkerMobile().replaceAll("\\D", "") : "") + "_" + (a.getProjectId() != null ? a.getProjectId() : "");
                                    deduplicatedPending.putIfAbsent(key, a);
                                }
                            }
                        }
                    }
                }

                final List<JobApplication> finalPendingList = new ArrayList<>(deduplicatedPending.values());

                Platform.runLater(() -> {
                    if (walletBalanceLabel != null) {
                        walletBalanceLabel.setText(String.format("Rs. %,.2f", currentR.getWalletBalance()));
                    }

                    if (workersMetricLabel != null) {
                        workersMetricLabel.setText(String.valueOf(finalAssignedWorkers));
                    }
                    if (workersMetricSub != null) {
                        workersMetricSub.setText(finalActiveProj != null ? "Assigned to " + finalActiveProj.getProjectName() : "No active project");
                    }
                    if (projectsMetricLabel != null) {
                        projectsMetricLabel.setText(String.valueOf(finalProjCount));
                    }
                    if (reqCountLabel != null) {
                        reqCountLabel.setText(String.valueOf(finalPendingList.size()));
                    }

                    if (activeOngoingPanel != null) {
                        activeOngoingPanel.getChildren().clear();
                        Label tag = label("ACTIVE PROJECT", "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#735c00;");

                        if (finalActiveProj == null) {
                            Label noProj = label("No Active Project Running", "-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                            VBox compDetail = detail("Company", company);
                            VBox statusDetail = detail("Status", "Idle");

                            Button createProjBtn = new Button("+ Create New Project");
                            createProjBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
                            createProjBtn.setOnAction(ev -> {
                                Stage stage = (Stage) createProjBtn.getScene().getWindow();
                                stage.setScene(new CreateProjectPage().getCreateProjectScene(() -> stage.setScene(getScene(back))));
                            });

                            HBox gridDetails = new HBox(16, compDetail, statusDetail);
                            gridDetails.setAlignment(Pos.CENTER_LEFT);

                            HBox foot = new HBox(createProjBtn);
                            foot.setAlignment(Pos.CENTER_RIGHT);

                            activeOngoingPanel.getChildren().addAll(tag, noProj, gridDetails, foot);
                        } else {
                            Label projName = label(finalActiveProj.getProjectName(), "-fx-font-family:Georgia;-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                            VBox contactDetail = detail("Supervisor", val(finalActiveProj.getContactName(), fullName));
                            String loc = (val(finalActiveProj.getCity(), "") + ", " + val(finalActiveProj.getState(), "")).replaceAll("^, |, $", "");
                            VBox siteDetail = detail("Location", loc.isBlank() ? "Pune, Maharashtra" : loc);
                            VBox statusDetail = detail("Status", "In Progress");

                            Button completeBtn = new Button("Mark Milestone Completed");
                            completeBtn.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-text-fill:#1b5e20;-fx-border-color:#c8e6c9;-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
                            completeBtn.setOnAction(ev -> {
                                completeBtn.setDisable(true);
                                new Thread(() -> {
                                    finalActiveProj.setStatus("Completed");
                                    new ProjectController().addProject(finalActiveProj);

                                    if (!finalUpcoming.isEmpty()) {
                                        Project nextActive = finalUpcoming.get(0);
                                        nextActive.setStatus("Active");
                                        new ProjectController().addProject(nextActive);
                                    }

                                    Platform.runLater(() -> {
                                        Stage stage = (Stage) completeBtn.getScene().getWindow();
                                        stage.setScene(getScene(back));
                                    });
                                }).start();
                            });

                            HBox gridDetails = new HBox(16, contactDetail, siteDetail, statusDetail);
                            gridDetails.setAlignment(Pos.CENTER_LEFT);

                            HBox foot = new HBox(completeBtn);
                            foot.setAlignment(Pos.CENTER_RIGHT);

                            activeOngoingPanel.getChildren().addAll(tag, projName, gridDetails, foot);
                        }
                    }

                    if (activeProjPanel != null) {
                        activeProjPanel.getChildren().clear();
                        activeProjPanel.getChildren().add(panelHeader("Active Projects"));
                        List<Project> activeList = recruiterProjects.stream().filter(p -> "Active".equalsIgnoreCase(p.getStatus())).toList();
                        if (activeList.isEmpty()) {
                            activeProjPanel.getChildren().add(label("No active projects currently.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
                        } else {
                            for (Project actP : activeList) {
                                String locStr = (val(actP.getCity(), "") + ", " + val(actP.getState(), "")).replaceAll("^, |, $", "");
                                VBox activeCard = new VBox(4,
                                        label(actP.getProjectName(), "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                                        label("Location: " + (locStr.isBlank() ? "Pune" : locStr), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                        label("Status: Active", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#2e7d32;")
                                );
                                activeCard.setPadding(new Insets(10, 12, 10, 12));
                                activeCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                activeCard.setOnMouseEntered(e -> activeCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                activeCard.setOnMouseExited(e -> activeCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));
                                activeProjPanel.getChildren().add(activeCard);
                            }
                        }
                    }

                    if (upcomingProjPanel != null) {
                        upcomingProjPanel.getChildren().clear();
                        upcomingProjPanel.getChildren().add(panelHeader("Upcoming Projects"));
                        if (finalUpcoming.isEmpty()) {
                            upcomingProjPanel.getChildren().add(label("No upcoming projects scheduled.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
                        } else {
                            for (Project up : finalUpcoming) {
                                String upLoc = (val(up.getCity(), "") + ", " + val(up.getState(), "")).replaceAll("^, |, $", "");
                                VBox upCard = new VBox(4,
                                        label(up.getProjectName(), "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                                        label("Location: " + (upLoc.isBlank() ? "Pune" : upLoc), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                        label("Status: Upcoming", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#735c00;")
                                );
                                upCard.setPadding(new Insets(10, 12, 10, 12));
                                upCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                upCard.setOnMouseEntered(e -> upCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                upCard.setOnMouseExited(e -> upCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));
                                upcomingProjPanel.getChildren().add(upCard);
                            }
                        }
                    }

                    if (pastProjPanel != null) {
                        pastProjPanel.getChildren().clear();
                        pastProjPanel.getChildren().add(panelHeader("Past Completed Projects"));
                        if (finalCompleted.isEmpty()) {
                            pastProjPanel.getChildren().add(label("No completed project records yet.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
                        } else {
                            for (Project cp : finalCompleted) {
                                String cpLoc = (val(cp.getCity(), "") + ", " + val(cp.getState(), "")).replaceAll("^, |, $", "");
                                VBox cpCard = new VBox(4,
                                        label(cp.getProjectName(), "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                                        label("Location: " + (cpLoc.isBlank() ? "Pune" : cpLoc), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                        label("Status: Completed", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#2e7d32;")
                                );
                                cpCard.setPadding(new Insets(10, 12, 10, 12));
                                cpCard.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;");
                                cpCard.setOnMouseEntered(e -> cpCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                cpCard.setOnMouseExited(e -> cpCard.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:8px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:8px;"));
                                pastProjPanel.getChildren().add(cpCard);
                            }
                        }
                    }

                    if (reqPanel != null) {
                        reqPanel.getChildren().clear();
                        reqPanel.getChildren().add(panelHeader("Pending Worker Applications"));

                        if (finalPendingList.isEmpty()) {
                            reqPanel.getChildren().add(label("No pending worker applications awaiting action.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
                        } else {
                            for (JobApplication a : finalPendingList) {
                                Label nameLbl = label("Applicant Worker (Mobile: " + val(a.getWorkerMobile(), "Worker") + ")", "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                                Label jobLbl = label("Position: " + val(a.getJobTitle(), "Site Position") + "  |  Daily Wage: Rs. " + val(a.getJobWage(), "950"), "-fx-font-size:12px;-fx-text-fill:#4d4635;");

                                Button viewAllBtn = new Button("Review Application");
                                viewAllBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:6px;-fx-text-fill:#ffd54f;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:5px 12px;-fx-cursor:hand;");
                                viewAllBtn.setOnAction(ev -> {
                                    if (livePoller != null) livePoller.stop();
                                    Stage stage = (Stage) viewAllBtn.getScene().getWindow();
                                    stage.setScene(new PendingApprovalsPage(currentR).getScene(() -> stage.setScene(getScene(back))));
                                });

                                VBox appCard = new VBox(5, nameLbl, jobLbl, new HBox(viewAllBtn));
                                appCard.setPadding(new Insets(10, 12, 10, 12));
                                appCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;");
                                appCard.setOnMouseEntered(e -> appCard.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-cursor:hand;"));
                                appCard.setOnMouseExited(e -> appCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:8px;-fx-border-color:#ebdccb;-fx-border-width:1px;-fx-border-radius:8px;"));
                                reqPanel.getChildren().add(appCard);
                            }
                        }
                    }

                    if (notificationsPanel != null) {
                        notificationsPanel.getChildren().clear();
                        notificationsPanel.getChildren().add(panelHeader("Notifications"));

                        if (recruiterNotifs == null || recruiterNotifs.isEmpty()) {
                            notificationsPanel.getChildren().addAll(
                                    label("No notifications yet", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Worker applications and accepted job offers will appear here.", "-fx-font-size:12px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            int count = 0;
                            for (Notification n : recruiterNotifs) {
                                if (n.getNotificationId() != null && !seenRecruiterNotifIds.contains(n.getNotificationId())) {
                                    seenRecruiterNotifIds.add(n.getNotificationId());
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
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isUpdating = false;
            }
        }).start();
    }

    private void handleAddFunds(Recruiter currentR, Button addFundsBtn, Label walletBalanceLabel, Runnable back) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Funds to Wallet");
        dialog.setHeaderText("Enter amount to deposit in INR:");
        dialog.setContentText("Amount (Rs.):");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(result.get().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            displayAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric value greater than 0.");
            return;
        }

        addFundsBtn.setDisable(true);
        addFundsBtn.setText("Processing...");

        final Stage dashboardStage = (Stage) addFundsBtn.getScene().getWindow();

        new Thread(() -> {
            try {
                RazorpayService razorpayService = new RazorpayService();
                String receiptId = "rcpt_" + UUID.randomUUID().toString().substring(0, 8);
                String orderId = razorpayService.createOrder(amount, receiptId);

                String email = val(currentR.getEmail(), "recruiter@dihadi.com");
                String phone = val(currentR.getMobileNumber(), "9999999999");

                Platform.runLater(() -> {
                    PaymentCheckoutScene.openCheckout(
                            dashboardStage,
                            orderId,
                            amount,
                            email,
                            phone,
                            new PaymentCheckoutScene.PaymentCallback() {
                                @Override
                                public void onSuccess(String paymentId, String oid, String signature) {
                                    double newBalance = currentR.getWalletBalance() + amount;
                                    currentR.setWalletBalance(newBalance);
                                    if (SessionManager.currentRecruiter != null) {
                                        SessionManager.currentRecruiter.setWalletBalance(newBalance);
                                    }

                                    new Thread(() -> {
                                        try {
                                            String mobile = currentR.getMobileNumber();
                                            if (mobile != null && !mobile.isBlank()) {
                                                new RecruiterController().updateWalletBalance(mobile, newBalance);
                                            }
                                        } catch (Exception dbEx) {
                                            dbEx.printStackTrace();
                                        }
                                    }).start();

                                    displayAlert(Alert.AlertType.INFORMATION, "Payment Successful",
                                            "Rs. " + String.format("%.2f", amount) + " credited to your wallet.\nTxn ID: " + paymentId);
                                    dashboardStage.setScene(getScene(back));
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    addFundsBtn.setDisable(false);
                                    addFundsBtn.setText("+ Add Funds");
                                    displayAlert(Alert.AlertType.ERROR, "Payment Failed", errorMessage);
                                }
                            }
                    );
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    addFundsBtn.setDisable(false);
                    addFundsBtn.setText("+ Add Funds");
                    displayAlert(Alert.AlertType.ERROR, "Gateway Error", "Failed to initiate payment: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void displayAlert(Alert.AlertType type, String title, String message) {
        NotificationToast.ToastType toastType = NotificationToast.ToastType.INFO;
        if (type == Alert.AlertType.ERROR) toastType = NotificationToast.ToastType.ERROR;
        else if (type == Alert.AlertType.WARNING) toastType = NotificationToast.ToastType.ALERT;
        NotificationToast.show(title, message, toastType);
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
        return label(text, "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:" + PRIMARY + ";");
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + (active ? "#faf3e8" : "transparent") + ";-fx-text-fill:" + GOLD + ";-fx-font-size:13px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-background-radius:8px;-fx-cursor:hand;");
        return b;
    }

    private VBox detail(String k, String v) {
        return new VBox(2, label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#685c52;"),
                label(val(v, "Not provided"), "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"));
    }

    private ImageView image(String path, double width, double height) {
        try {
            var r = getClass().getResource(path);
            if (r == null) return new ImageView();
            ImageView view = new ImageView(new Image(r.toExternalForm()));
            view.setFitWidth(width);
            view.setFitHeight(height);
            view.setPreserveRatio(true);
            return view;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private boolean blank(String v) {
        return v == null || v.isBlank();
    }

    private String val(String v, String fallback) {
        return blank(v) ? fallback : v;
    }

    private String val(String v) {
        return val(v, "Not provided");
    }

    private Label label(String v, String s) {
        Label l = new Label(v);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + s);
        return l;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private boolean isMatch(Project p, Recruiter r, String searchKey) {
        if (p == null) return false;
        String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
        String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";

        String rMob = r != null && r.getMobileNumber() != null ? r.getMobileNumber().replaceAll("\\D", "") : "";
        String rEmail = r != null && r.getEmail() != null ? r.getEmail().trim().toLowerCase() : "";
        String sKey = searchKey != null ? searchKey.trim().toLowerCase() : "";
        String sKeyDigits = searchKey != null ? searchKey.replaceAll("\\D", "") : "";

        if (!pMob.isEmpty() && !rMob.isEmpty() && (pMob.equals(rMob) || pMob.endsWith(rMob) || rMob.endsWith(pMob))) return true;
        if (!pMob.isEmpty() && !sKeyDigits.isEmpty() && (pMob.equals(sKeyDigits) || pMob.endsWith(sKeyDigits) || sKeyDigits.endsWith(pMob))) return true;
        if (!pEmail.isEmpty() && (!rEmail.isEmpty() && pEmail.equals(rEmail))) return true;
        if (!pEmail.isEmpty() && (!sKey.isEmpty() && pEmail.equals(sKey))) return true;

        return false;
    }

    private HBox createFooter() {
        Label info = label("© 2026 DIHADI. All rights reserved.", "-fx-font-size:12px;-fx-text-fill:#8c7e6b;");
        HBox f = new HBox(info);
        f.setAlignment(Pos.CENTER);
        f.setPadding(new Insets(16, 0, 8, 0));
        return f;
    }
}
