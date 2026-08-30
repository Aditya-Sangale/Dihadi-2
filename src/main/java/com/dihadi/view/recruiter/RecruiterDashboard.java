package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import com.dihadi.model.Project;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Notification;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.RecruiterController;
import com.dihadi.controller.WorkerController;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.NotificationController;
import com.dihadi.view.SessionManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/** Recruiter Portal overview matching the updated dashboard reference. */
public class RecruiterDashboard {
    private final Recruiter recruiter;
    private Timeline livePoller;

    private VBox activeOngoingPanel;
    private VBox activeProjPanel;
    private VBox upcomingProjPanel;
    private VBox pastProjPanel;
    private VBox reqPanel;
    private VBox notificationsPanel;

    private final java.util.Set<String> seenRecruiterNotifIds = new java.util.HashSet<>();
    private boolean initialLoadDone = false;

    public RecruiterDashboard(Recruiter recruiter) {
        this.recruiter = recruiter != null ? recruiter : (SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : new Recruiter());
    }

    public Scene getScene(Runnable back) {
        Recruiter currentR = SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : recruiter;
        String first = val(currentR.getFirstName(), "Recruiter");
        String name = (first + (blank(currentR.getLastName()) ? "" : " " + currentR.getLastName())).trim();
        String company = val(currentR.getCompanyName(), "Organisation not provided");

        Label logo = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Button overview = nav("Overview", true), projects = nav("My Projects", false),
                attendance = nav("Attendance", false), wallet = nav("Wallet", false);
        attendance.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            Stage stage = (Stage) attendance.getScene().getWindow();
            stage.setScene(new AttendancePage(currentR).getScene(() -> stage.setScene(getScene(back))));
        });
        HBox nav = new HBox(22, overview, projects, attendance, wallet);
        nav.setAlignment(Pos.CENTER);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label profileBadge = label(name, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#faf3e8;-fx-background-radius:999px;-fx-border-color:#cfc6b2;-fx-border-radius:999px;-fx-padding:9px 15px;");
        HBox headerBar = new HBox(30, logo, nav, spacer, profileBadge);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(0, 0, 15, 0));
        headerBar.setStyle("-fx-border-color:transparent transparent #cfc6b2 transparent;-fx-border-width:0 0 1px 0;");

        Label welcomeLabel = label("Welcome back, " + name + "!", "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label companyLabel = label(company + "  •  Recruiter Account", "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label subtextLabel = label("Manage your projects, workforce and hiring activity from one place.", "-fx-font-size:15px;-fx-text-fill:#4c4637;");
        VBox welcomeBox = new VBox(9, welcomeLabel, companyLabel, subtextLabel);
        welcomeBox.setPrefWidth(420);

        activeOngoingPanel = panel("ACTIVE ONGOING PROJECT", label("Loading project...", "-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:700;"));
        HBox.setHgrow(activeOngoingPanel, Priority.ALWAYS);
        HBox heroRow = new HBox(48, welcomeBox, activeOngoingPanel);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        VBox walletMetric = metric("Escrow / Wallet", "₹0.00", "Add funds");
        VBox workersMetric = metric("Assigned Workers", "Loading...", "Active project workforce");
        VBox projectsMetric = metric("Total Projects", "Loading...", "Active projects");
        HBox metricsRow = new HBox(20, walletMetric, workersMetric, projectsMetric);
        for (Node n : metricsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        activeProjPanel = panel("ACTIVE PROJECTS", label("Loading...", "-fx-font-size:16px;"));
        upcomingProjPanel = panel("UPCOMING PROJECTS", label("Loading upcoming projects...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        pastProjPanel = panel("PAST COMPLETED PROJECTS", label("Loading past projects...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        Label reqCountLabel = label("Loading...", "-fx-font-size:16px;-fx-font-weight:700;");
        Button viewReqsBtn = action("VIEW APPROVALS", true);
        viewReqsBtn.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            Stage stage = (Stage) viewReqsBtn.getScene().getWindow();
            stage.setScene(new PendingApprovalsPage(currentR).getScene(() -> stage.setScene(getScene(back))));
        });
        reqPanel = panel("PENDING APPROVAL REQUESTS", reqCountLabel, viewReqsBtn);

        notificationsPanel = panel("NOTIFICATIONS & ALERTS",
                label("Loading real-time updates...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        VBox recruiterProfilePanel = panel("RECRUITER PROFILE",
                detail("Mobile", val(currentR.getMobileNumber(), "Not provided")),
                detail("Email", val(currentR.getEmail(), "Not provided")),
                detail("Business Type", val(currentR.getBusinessType(), "Not provided")));

        VBox leftBody = new VBox(20, activeProjPanel, upcomingProjPanel, pastProjPanel);
        VBox rightBody = new VBox(20, reqPanel, notificationsPanel, recruiterProfilePanel);
        HBox bodyRow = new HBox(20, leftBody, rightBody);
        HBox.setHgrow(leftBody, Priority.ALWAYS);
        HBox.setHgrow(rightBody, Priority.ALWAYS);

        Button backBtn = action("BACK TO RECRUITER PAGE", true);
        backBtn.setOnAction(e -> {
            if (livePoller != null) livePoller.stop();
            if (back != null) back.run();
        });
        HBox footerRow = new HBox(backBtn);
        footerRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(26, headerBar, new VBox(heroRow), metricsRow, bodyRow, footerRow);
        content.setPadding(new Insets(26, 72, 38, 72));
        content.setMaxWidth(1440);
        content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");

        // Initial Fetch
        refreshRecruiterData(currentR, welcomeLabel, companyLabel, profileBadge, workersMetric, projectsMetric, reqCountLabel, back);

        // Real-time live polling refresher every 3 seconds
        livePoller = new Timeline(new KeyFrame(Duration.seconds(3), e ->
                refreshRecruiterData(currentR, welcomeLabel, companyLabel, profileBadge, workersMetric, projectsMetric, reqCountLabel, back)
        ));
        livePoller.setCycleCount(Timeline.INDEFINITE);
        livePoller.play();

        return new Scene(scroll, 1440, 900);
    }

    private boolean isUpdating = false;

    private void refreshRecruiterData(Recruiter currentR, Label welcomeLabel, Label companyLabel, Label profileBadge,
                                      VBox workersMetric, VBox projectsMetric, Label reqCountLabel, Runnable back) {
        if (isUpdating) return;
        isUpdating = true;

        new Thread(() -> {
            try {
                String searchKey = val(currentR.getMobileNumber(), currentR.getEmail());
                Recruiter dbRecruiter = null;
                if (!searchKey.isEmpty()) {
                    dbRecruiter = new RecruiterController().getRecruiterByEmailOrMobile(searchKey);
                }

                final Recruiter finalR = dbRecruiter != null ? dbRecruiter : currentR;
                SessionManager.currentRecruiter = finalR;

                String finalFirstName = val(finalR.getFirstName(), "Recruiter");
                String finalFullName = (finalFirstName + (blank(finalR.getLastName()) ? "" : " " + finalR.getLastName())).trim();
                String finalCompany = val(finalR.getCompanyName(), "Organisation not provided");

                int totalRecruiters = new RecruiterController().getAllRecruiters().size();
                List<Project> allProjectsList = new ProjectController().getAllProjects();
                List<JobApplication> allApps = new JobApplicationController().getAllApplications();
                List<Notification> recruiterNotifs = new NotificationController().getNotifications(searchKey);

                List<Project> recruiterProjects = new ArrayList<>();
                if (allProjectsList != null) {
                    for (Project p : allProjectsList) {
                        if (isMatch(p, finalR, searchKey)) {
                            recruiterProjects.add(p);
                        }
                    }
                    if (recruiterProjects.isEmpty()) {
                        // Fallback match by contact name, company name, or single-recruiter fallback
                        String nameClean = finalFullName.toLowerCase();
                        String compClean = finalCompany.toLowerCase();
                        for (Project p : allProjectsList) {
                            String cName = p.getContactName() != null ? p.getContactName().toLowerCase() : "";
                            String pName = p.getProjectName() != null ? p.getProjectName().toLowerCase() : "";
                            if (!cName.isEmpty() && (nameClean.contains(cName) || cName.contains(nameClean))) {
                                recruiterProjects.add(p);
                            } else if (!compClean.isEmpty() && !compClean.contains("organisation not provided") && (pName.contains(compClean) || compClean.contains(pName))) {
                                recruiterProjects.add(p);
                            }
                        }
                        if (recruiterProjects.isEmpty() && (allProjectsList.size() == 1 || (totalRecruiters <= 1 && !allProjectsList.isEmpty()))) {
                            recruiterProjects.addAll(allProjectsList);
                        }
                    }
                }

                Project activeProj = null;
                List<Project> upcomingList = new ArrayList<>();
                List<Project> completedList = new ArrayList<>();

                for (Project p : recruiterProjects) {
                    String st = p.getStatus();
                    if ("Completed".equalsIgnoreCase(st)) {
                        completedList.add(p);
                    } else if ("Upcoming".equalsIgnoreCase(st)) {
                        upcomingList.add(p);
                    } else if ("Active".equalsIgnoreCase(st) && activeProj == null) {
                        activeProj = p;
                    } else {
                        if (activeProj == null && !"Completed".equalsIgnoreCase(st)) {
                            activeProj = p;
                        } else {
                            upcomingList.add(p);
                        }
                    }
                }

                final Project finalActiveProj = activeProj;
                final List<Project> finalUpcoming = upcomingList;
                final List<Project> finalCompleted = completedList;
                final int finalProjCount = recruiterProjects.size();
                
                int assignedWorkersCount = 0;
                if (finalActiveProj != null && finalActiveProj.getProjectId() != null) {
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
                
                String rMobDigits = finalR.getMobileNumber() != null ? finalR.getMobileNumber().replaceAll("\\D", "") : "";
                
                long pendingAppsCount = allApps.stream()
                        .filter(a -> "Pending".equalsIgnoreCase(a.getStatus()))
                        .filter(a -> {
                            if (a.getProjectId() != null && recruiterProjIds.contains(a.getProjectId())) {
                                return true;
                            }
                            String appRMob = a.getRecruiterMobile() != null ? a.getRecruiterMobile().replaceAll("\\D", "") : "";
                            if (!rMobDigits.isEmpty() && !appRMob.isEmpty()) {
                                return appRMob.equals(rMobDigits) || appRMob.endsWith(rMobDigits) || rMobDigits.endsWith(appRMob);
                            }
                            return false;
                        })
                        .count();

                Platform.runLater(() -> {
                    welcomeLabel.setText("Welcome back, " + finalFullName + "!");
                    companyLabel.setText(finalCompany + "  •  Recruiter Account");
                    profileBadge.setText(finalFullName);

                    ((Label) workersMetric.getChildren().get(1)).setText(String.valueOf(finalAssignedWorkers));
                    if (finalActiveProj != null) {
                        ((Label) workersMetric.getChildren().get(2)).setText("Assigned to " + finalActiveProj.getProjectName());
                    } else {
                        ((Label) workersMetric.getChildren().get(2)).setText("No active project");
                    }
                    ((Label) projectsMetric.getChildren().get(1)).setText(String.valueOf(finalProjCount));

                    ((Label) reqCountLabel).setText(pendingAppsCount == 0 ? "No pending worker approvals." : pendingAppsCount + " pending application(s)");

                    // Populate Notifications Panel
                    if (notificationsPanel != null) {
                        notificationsPanel.getChildren().clear();
                        notificationsPanel.getChildren().add(label("✦  NOTIFICATIONS & REAL-TIME ALERTS",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));

                        if (recruiterNotifs == null || recruiterNotifs.isEmpty()) {
                            notificationsPanel.getChildren().addAll(
                                    label("No activity notifications yet", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                    label("Incoming worker applications & accepted hiring offers will appear here in real time.", "-fx-font-size:13px;-fx-text-fill:#8c7e6b;"));
                        } else {
                            int count = 0;
                            for (Notification n : recruiterNotifs) {
                                if (n.getNotificationId() != null && !seenRecruiterNotifIds.contains(n.getNotificationId())) {
                                    seenRecruiterNotifIds.add(n.getNotificationId());
                                    if (initialLoadDone) {
                                        com.dihadi.view.NotificationToast.ToastType toastType = "HIRING_ACCEPTED".equalsIgnoreCase(n.getType())
                                                ? com.dihadi.view.NotificationToast.ToastType.SUCCESS
                                                : com.dihadi.view.NotificationToast.ToastType.INFO;
                                        com.dihadi.view.NotificationToast.show(welcomeLabel, n.getTitle(), n.getMessage(), toastType);
                                    }
                                }

                                if (count++ >= 8) continue; // show up to 8 in panel
                                String icon = "🔔";
                                if ("APPLICATION_RECEIVED".equalsIgnoreCase(n.getType())) icon = "📥";
                                else if ("HIRING_ACCEPTED".equalsIgnoreCase(n.getType())) icon = "🎉";
                                else if ("APPLICATION_ACCEPTED".equalsIgnoreCase(n.getType())) icon = "✓";

                                Label titleLbl = label(icon + "  " + (n.getTitle() != null ? n.getTitle() : "Notification"),
                                        "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                                Label msgLbl = label(n.getMessage() != null ? n.getMessage() : "",
                                        "-fx-font-size:12px;-fx-text-fill:#4d4635;");
                                msgLbl.setWrapText(true);

                                VBox notifCard = new VBox(4, titleLbl, msgLbl);
                                notifCard.setPadding(new Insets(10, 12, 10, 12));
                                if ("HIRING_ACCEPTED".equalsIgnoreCase(n.getType())) {
                                    notifCard.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-border-color:#a5d6a7;-fx-border-width:1px;-fx-border-radius:8px;");
                                } else if ("APPLICATION_RECEIVED".equalsIgnoreCase(n.getType())) {
                                    notifCard.setStyle("-fx-background-color:#fff8e1;-fx-background-radius:8px;-fx-border-color:#ffe082;-fx-border-width:1px;-fx-border-radius:8px;");
                                } else {
                                    notifCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:8px;");
                                }
                                notificationsPanel.getChildren().add(notifCard);
                            }
                            initialLoadDone = true;
                        }
                    }

                    // Populate Hero Active Ongoing Project Panel
                    activeOngoingPanel.getChildren().clear();
                    activeOngoingPanel.getChildren().add(label("ACTIVE ONGOING PROJECT", "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.3px;-fx-text-fill:#685c52;"));

                    if (finalActiveProj == null) {
                        Label noProj = label("No active project assigned", "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:700;");
                        VBox compDetail = detail("Company", finalCompany);
                        VBox statusDetail = detail("Site status", "No active project data yet");

                        Button createProjBtn = action("CREATE PROJECT", true);
                        createProjBtn.setOnAction(ev -> {
                            Stage stage = (Stage) createProjBtn.getScene().getWindow();
                            stage.setScene(new CreateProjectPage().getCreateProjectScene(() -> stage.setScene(getScene(back))));
                        });

                        activeOngoingPanel.getChildren().addAll(noProj, compDetail, statusDetail, new HBox(12, createProjBtn));
                    } else {
                        Label projName = label(finalActiveProj.getProjectName(), "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:700;");
                        VBox contactDetail = detail("Contact Person", val(finalActiveProj.getContactName(), finalCompany));
                        String loc = (val(finalActiveProj.getCity(), "") + ", " + val(finalActiveProj.getState(), "")).replaceAll("^, |, $", "");
                        VBox siteDetail = detail("Site Location", loc.isBlank() ? "Location not provided" : loc);

                        Button completeBtn = action("Mark As Complete", true);
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

                        activeOngoingPanel.getChildren().addAll(projName, contactDetail, siteDetail, new HBox(12, completeBtn));
                    }

                    // Populate Body Active Projects Panel
                    activeProjPanel.getChildren().clear();
                    activeProjPanel.getChildren().add(label("ACTIVE PROJECTS", "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.3px;-fx-text-fill:#685c52;"));
                    if (finalActiveProj == null) {
                        activeProjPanel.getChildren().add(label("No active projects yet.", "-fx-font-size:15px;-fx-text-fill:#4c4637;"));
                    } else {
                        String locStr = (val(finalActiveProj.getCity(), "") + ", " + val(finalActiveProj.getState(), "")).replaceAll("^, |, $", "");
                        VBox activeCard = new VBox(4,
                                label(finalActiveProj.getProjectName(), "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                label("⌖ " + (locStr.isBlank() ? "Location not provided" : locStr), "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                label("Status: Active Ongoing", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;")
                        );
                        activeCard.setPadding(new Insets(12));
                        activeCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:10px;");
                        activeCard.setOnMouseEntered(e -> activeCard.setStyle("-fx-background-color:linear-gradient(to right, #ffffff, #fffdf2);-fx-background-radius:10px;-fx-border-color:#b8921e;-fx-border-width:2px;-fx-border-radius:10px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),12,0,0,3px);"));
                        activeCard.setOnMouseExited(e -> activeCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:10px;"));
                        activeProjPanel.getChildren().add(activeCard);
                    }

                    // Populate Upcoming Projects Panel
                    upcomingProjPanel.getChildren().clear();
                    upcomingProjPanel.getChildren().add(label("UPCOMING PROJECTS", "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.3px;-fx-text-fill:#685c52;"));
                    if (finalUpcoming.isEmpty()) {
                        upcomingProjPanel.getChildren().add(label("Projects scheduled by your organisation will appear here.", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                    } else {
                        for (Project up : finalUpcoming) {
                            String upLoc = (val(up.getCity(), "") + ", " + val(up.getState(), "")).replaceAll("^, |, $", "");
                            VBox upCard = new VBox(4,
                                    label(up.getProjectName(), "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                    label("⌖ " + (upLoc.isBlank() ? "Location not provided" : upLoc), "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                    label("Status: Upcoming", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;")
                            );
                            upCard.setPadding(new Insets(12));
                            upCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:10px;");
                            upCard.setOnMouseEntered(e -> upCard.setStyle("-fx-background-color:linear-gradient(to right, #ffffff, #fffdf2);-fx-background-radius:10px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:10px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),12,0,0,3px);"));
                            upCard.setOnMouseExited(e -> upCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:10px;"));
                            upcomingProjPanel.getChildren().add(upCard);
                        }
                    }

                    // Populate Past Completed Projects Panel
                    pastProjPanel.getChildren().clear();
                    pastProjPanel.getChildren().add(label("PAST COMPLETED PROJECTS", "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.3px;-fx-text-fill:#685c52;"));
                    if (finalCompleted.isEmpty()) {
                        pastProjPanel.getChildren().add(label("No completed project records yet.", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                    } else {
                        for (Project cp : finalCompleted) {
                            String cpLoc = (val(cp.getCity(), "") + ", " + val(cp.getState(), "")).replaceAll("^, |, $", "");
                            VBox cpCard = new VBox(4,
                                    label(cp.getProjectName(), "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                    label("⌖ " + (cpLoc.isBlank() ? "Location not provided" : cpLoc), "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                    label("Status: Completed ✓", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;")
                            );
                            cpCard.setPadding(new Insets(12));
                            cpCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:10px;");
                            cpCard.setOnMouseEntered(e -> cpCard.setStyle("-fx-background-color:linear-gradient(to right, #ffffff, #fffdf2);-fx-background-radius:10px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:10px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),12,0,0,3px);"));
                            cpCard.setOnMouseExited(e -> cpCard.setStyle("-fx-background-color:linear-gradient(to right, #fffcf5, #fef8eb);-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:10px;"));
                            pastProjPanel.getChildren().add(cpCard);
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

    private VBox metric(String t, String v, String n) {
        VBox p = panel(t,
                label(v, "-fx-font-family:Georgia;-fx-font-size:25px;-fx-font-weight:700;-fx-text-fill:#735c00;"),
                label(n, "-fx-font-size:13px;-fx-text-fill:#4c4637;"));
        p.setMinHeight(138);
        return p;
    }

    private VBox panel(String title, Node... nodes) {
        String headerTitle = title.startsWith("✦") ? title : "✦  " + title;
        VBox v = new VBox(12, label(headerTitle, "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-text-fill:#735c00;"));
        v.getChildren().addAll(nodes);
        v.setPadding(new Insets(22));
        v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffcf5 100%);-fx-background-radius:18px;-fx-border-color:#e8ddc8;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(115,92,0,.08),16,0,0,5px);");
        v.setOnMouseEntered(e -> v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffbf0 100%);-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:18px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.30),22,0,0,7px);"));
        v.setOnMouseExited(e -> v.setStyle("-fx-background-color:linear-gradient(to bottom right, #ffffff 0%, #fffcf5 100%);-fx-background-radius:18px;-fx-border-color:#e8ddc8;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(115,92,0,.08),16,0,0,5px);"));
        return v;
    }

    private Button action(String text, boolean filled) {
        Button b = new Button(text);
        b.setStyle(filled
                ? "-fx-background-color:linear-gradient(to right, #d4af37, #b8921e);-fx-background-radius:8px;-fx-text-fill:#ffffff;-fx-font-weight:800;-fx-font-size:12px;-fx-padding:9px 18px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(184,146,30,.3),8,0,0,2px);"
                : "-fx-background-color:#ffffff;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:8px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:8px 16px;-fx-cursor:hand;");
        return b;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-padding:8px 2px;-fx-font-size:13px;-fx-font-weight:"
                + (active ? "800" : "500") + ";-fx-text-fill:" + (active ? "#735c00" : "#4c4637") + ";-fx-border-color:"
                + (active ? "#735c00" : "transparent") + ";-fx-border-width:0 0 2px 0;-fx-cursor:hand;");
        return b;
    }

    private VBox detail(String k, String v) {
        return new VBox(3, label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-letter-spacing:.3px;-fx-text-fill:#8c7e6b;"),
                label(val(v, "Not provided"), "-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#231b00;"));
    }

    private boolean blank(String v) {
        return v == null || v.isBlank();
    }

    private String val(String v, String fallback) {
        return blank(v) ? fallback : v;
    }

    private String val(String v1, String v2, String fallback) {
        if (!blank(v1)) return v1;
        if (!blank(v2)) return v2;
        return fallback;
    }

    private Label label(String v, String s) {
        Label l = new Label(v);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + s);
        return l;
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
}