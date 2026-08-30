package com.dihadi.view.worker;

import com.dihadi.model.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Full Worker Portal dashboard, populated from the authenticated Worker record.
 */
public class WorkerDashboard {
    private final Worker worker;

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
        VBox wageDetail = detail("Daily Wage", "Rs. " + worker.getDailyWage() + " / day");
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

    private Label jobRequestsMetricLabel = new Label("0 New");

    private HBox metrics() {
        jobRequestsMetricLabel
                .setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        HBox row = new HBox(24,
                metric("Wallet Balance", "Rs. 0", "+ Earnings will appear here"),
                metric("Attendance (Month)", "0 Days Present", "No attendance recorded"),
                panel("Job Requests", jobRequestsMetricLabel,
                        label("Recruiter Direct Offers", "-fx-font-size:13px;-fx-text-fill:#3f4938;")),
                metric("Reputation", "New", "No deployments yet"));
        for (Node n : row.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }

    private VBox metric(String title, String value, String note) {
        VBox v = panel(title,
                label(value, "-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;"),
                label(note, "-fx-font-size:13px;-fx-text-fill:#3f4938;"));
        v.setMinHeight(145);
        return v;
    }

    private HBox lowerSections(String location, VBox heroContainer) {
        VBox history = panel("RECENT WORK HISTORY",
                label("No completed work history yet.", "-fx-font-size:15px;-fx-text-fill:#4c4637;"),
                detail("Current location", location), detail("Experience", value(worker.getExperience())));

        VBox recruiterRequests = panel("RECRUITER JOB REQUESTS",
                label("Loading requests...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        VBox pendingApplications = panel("PENDING JOB APPLICATIONS",
                label("Loading applications...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));

        new Thread(() -> {
            java.util.List<com.dihadi.model.JobApplication> apps = new com.dihadi.controller.JobApplicationController()
                    .getApplicationsByWorker(worker.getMobileNumber());
            javafx.application.Platform.runLater(() -> {
                recruiterRequests.getChildren().clear();
                recruiterRequests.getChildren().add(label("RECRUITER JOB REQUESTS",
                        "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#685c52;"));

                pendingApplications.getChildren().clear();
                pendingApplications.getChildren().add(label("PENDING JOB APPLICATIONS",
                        "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#685c52;"));

                int reqCount = 0;
                int appCount = 0;
                int completedCount = 0;

                if (apps != null) {
                    for (com.dihadi.model.JobApplication app : apps) {
                        boolean isDirectRequest = (app.getJobTitle() != null
                                && app.getJobTitle().contains("Hiring Request"))
                                || (app.getRecruiterMobile() != null && !app.getRecruiterMobile().isBlank());

                        if ("Completed".equalsIgnoreCase(app.getStatus())) {
                            if (completedCount == 0) {
                                history.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().contains("No completed work history yet"));
                            }
                            completedCount++;
                            addWorkHistoryCard(history, app);
                        } else if ("Accepted".equalsIgnoreCase(app.getStatus())) {
                            // Update Active Project Panel on Worker Dashboard
                            HBox heroHBox = (HBox) heroContainer.getChildren().get(0);
                            Node[] details = (Node[]) heroHBox.getUserData();
                            updateActiveProjectCard(details, app);
                        } else if ("Pending".equalsIgnoreCase(app.getStatus())) {
                            if (isDirectRequest) {
                                reqCount++;
                                Button acceptBtn = new Button("ACCEPT REQUEST");
                                acceptBtn.setStyle(
                                        "-fx-background-color:#d4af37;-fx-background-radius:6px;-fx-text-fill:#ffffff;-fx-font-weight:700;-fx-font-size:12px;-fx-padding:6px 14px;-fx-cursor:hand;");

                                Button declineBtn = new Button("DECLINE");
                                declineBtn.setStyle(
                                        "-fx-background-color:transparent;-fx-border-color:#ba1a1a;-fx-border-radius:6px;-fx-background-radius:6px;-fx-text-fill:#ba1a1a;-fx-font-weight:700;-fx-font-size:12px;-fx-padding:5px 12px;-fx-cursor:hand;");

                                HBox actionBox = new HBox(10, acceptBtn, declineBtn);
                                actionBox.setAlignment(Pos.CENTER_LEFT);
                                actionBox.setPadding(new Insets(6, 0, 0, 0));
                                VBox reqCard = new VBox(5,
                                        label(app.getJobTitle(),
                                                "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                        label("⌖ " + app.getJobLocation() + "  |  Daily Wage: Rs. " + app.getJobWage(),
                                                "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                        label("Direct Recruiter Hiring Offer  •  Status: Pending Approval",
                                                "-fx-font-size:12px;-fx-text-fill:#735c00;-fx-font-weight:700;"),
                                        actionBox);
                                reqCard.setPadding(new Insets(14));
                                reqCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:10px;");
                                reqCard.setOnMouseEntered(e -> reqCard.setStyle("-fx-background-color:#fffdf7;-fx-background-radius:10px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:10px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),12,0,0,3px);"));
                                reqCard.setOnMouseExited(e -> reqCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:10px;"));

                                acceptBtn.setOnAction(ev -> {
                                    acceptBtn.setDisable(true);
                                    declineBtn.setDisable(true);
                                    new Thread(() -> {
                                        if (apps != null) {
                                            for (com.dihadi.model.JobApplication a : apps) {
                                                if ("Accepted".equalsIgnoreCase(a.getStatus()) && !a.getApplicationId().equals(app.getApplicationId())) {
                                                    a.setStatus("Completed");
                                                    new com.dihadi.controller.JobApplicationController().saveApplication(a);
                                                    final com.dihadi.model.JobApplication compApp = a;
                                                    javafx.application.Platform.runLater(() -> {
                                                        history.getChildren().removeIf(node -> node instanceof Label && ((Label)node).getText().contains("No completed work history yet"));
                                                        addWorkHistoryCard(history, compApp);
                                                    });
                                                }
                                            }
                                        }

                                        app.setStatus("Accepted");
                                        new com.dihadi.controller.JobApplicationController().saveApplication(app);
                                        javafx.application.Platform.runLater(() -> {
                                            recruiterRequests.getChildren().remove(reqCard);
                                            try {
                                                int curr = Integer.parseInt(jobRequestsMetricLabel.getText().replace(" New", "").trim());
                                                jobRequestsMetricLabel.setText(Math.max(0, curr - 1) + " New");
                                            } catch (Exception ex) {}

                                            HBox heroHBox = (HBox) heroContainer.getChildren().get(0);
                                            Node[] details = (Node[]) heroHBox.getUserData();
                                            updateActiveProjectCard(details, app);

                                            if (recruiterRequests.getChildren().size() == 1) {
                                                recruiterRequests.getChildren().addAll(
                                                        label("No pending recruiter requests",
                                                                "-fx-font-size:17px;-fx-font-weight:700;"),
                                                        label("Direct recruiter hiring offers will appear here.",
                                                                "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                                            }
                                        });
                                    }).start();
                                });

                                declineBtn.setOnAction(ev -> {
                                    acceptBtn.setDisable(true);
                                    declineBtn.setDisable(true);
                                    new Thread(() -> {
                                        new com.dihadi.controller.JobApplicationController()
                                                .deleteApplication(app.getApplicationId());
                                        javafx.application.Platform.runLater(() -> {
                                            recruiterRequests.getChildren().remove(reqCard);
                                            try {
                                                int curr = Integer.parseInt(jobRequestsMetricLabel.getText().replace(" New", "").trim());
                                                jobRequestsMetricLabel.setText(Math.max(0, curr - 1) + " New");
                                            } catch (Exception ex) {}

                                            if (recruiterRequests.getChildren().size() == 1) {
                                                recruiterRequests.getChildren().addAll(
                                                        label("No pending recruiter requests",
                                                                "-fx-font-size:17px;-fx-font-weight:700;"),
                                                        label("Direct recruiter hiring offers will appear here.",
                                                                "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                                            }
                                        });
                                    }).start();
                                });

                                recruiterRequests.getChildren().add(reqCard);
                            } else {
                                appCount++;
                                Label withdraw = new Label("Withdraw application");
                                withdraw.setStyle(
                                        "-fx-font-size:12px;-fx-text-fill:#e74c3c;-fx-underline:true;-fx-cursor:hand;");

                                VBox appCard = new VBox(4,
                                        label(app.getJobTitle(),
                                                "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                        label("⌖ " + app.getJobLocation() + "  |  Wage: Rs. " + app.getJobWage(),
                                                "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                                        label("Status: Pending Recruiter Approval",
                                                "-fx-font-size:13px;-fx-text-fill:#d4af37;-fx-font-weight:700;"),
                                        withdraw);
                                appCard.setPadding(new Insets(12));
                                appCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:8px;");
                                appCard.setOnMouseEntered(e -> appCard.setStyle("-fx-background-color:#fffdf7;-fx-background-radius:8px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:8px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),10,0,0,3px);"));
                                appCard.setOnMouseExited(e -> appCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-width:1px;-fx-border-radius:8px;"));

                                withdraw.setOnMouseClicked(ev -> {
                                    new Thread(() -> {
                                        new com.dihadi.controller.JobApplicationController()
                                                .deleteApplication(app.getApplicationId());
                                        javafx.application.Platform.runLater(() -> {
                                            pendingApplications.getChildren().remove(appCard);
                                            if (pendingApplications.getChildren().size() == 1) {
                                                pendingApplications.getChildren().addAll(
                                                        label("No pending applications",
                                                                "-fx-font-size:17px;-fx-font-weight:700;"),
                                                        label("Your applied project opportunities will appear here.",
                                                                "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                                            }
                                        });
                                    }).start();
                                });

                                pendingApplications.getChildren().add(appCard);
                            }
                        }
                    }
                }

                jobRequestsMetricLabel.setText(reqCount + " New");

                if (recruiterRequests.getChildren().size() == 1) {
                    recruiterRequests.getChildren().addAll(
                            label("No pending recruiter requests", "-fx-font-size:17px;-fx-font-weight:700;"),
                            label("Direct recruiter hiring offers will appear here.",
                                    "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                }
                if (pendingApplications.getChildren().size() == 1) {
                    pendingApplications.getChildren().addAll(
                            label("No pending applications", "-fx-font-size:17px;-fx-font-weight:700;"),
                            label("Your applied project opportunities will appear here.",
                                    "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                }
            });
        }).start();

        VBox kyc = panel("KYC VERIFICATION HUB", detail("Profile status", "Submitted for verification"),
                detail("Education", value(worker.getEducation())),
                detail("Documents", "Upload documents when available"));
        VBox settings = panel("SKILL & PREF SETTINGS", detail("Primary skill", value(worker.getSubSkill())),
                detail("Desired daily wage", "Rs. " + worker.getDailyWage()),
                detail("Work radius", "Open to suitable projects"));
        VBox left = new VBox(16, history, recruiterRequests, kyc);
        VBox right = new VBox(16, pendingApplications, settings);
        HBox h = new HBox(18, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        return h;
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
        ((Label) ((VBox) details[3]).getChildren().get(1)).setText("Rs. " + app.getJobWage() + " / day");
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
            label("⌖ " + app.getJobLocation() + "  |  Daily Wage: Rs. " + app.getJobWage(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
            label("✓ Project Completed", "-fx-font-size:12px;-fx-text-fill:#27ae60;-fx-font-weight:700;")
        );
        compCard.setPadding(new Insets(12));
        compCard.setStyle("-fx-background-color:#f4f9f4;-fx-background-radius:10px;-fx-border-color:#c8e6c9;-fx-border-width:1px;-fx-border-radius:10px;");
        historyContainer.getChildren().add(compCard);
    }

    private HBox footer(Runnable back) {
        Button exit = new Button("Back to Worker Page");
        exit.setOnAction(e -> {
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