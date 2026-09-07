package com.dihadi.view.worker;

import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkforceRequirementController;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Project;
import com.dihadi.model.WorkforceRequirement;
import com.dihadi.view.SessionManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Compact project-opening details card shown when a worker selects Apply Now.
 */
public class SiteDetailsCardPage {
    private final String projectName;
    private final String roleTitle;
    private final String location, wage, imagePath;
    private final String projectId, recruiterMobile, requirementId;
    private Project matchedProj;

    public SiteDetailsCardPage(String projectName, String roleTitle, String location, String wage, String imagePath, String projectId, String recruiterMobile, String requirementId) {
        this.projectName = (projectName != null && !projectName.isBlank()) ? projectName : (roleTitle != null ? roleTitle : "Project");
        this.roleTitle = (roleTitle != null && !roleTitle.isBlank()) ? roleTitle : this.projectName;
        this.location = location != null ? location : "Site Location";
        this.wage = wage != null ? wage : "Daily Wage";
        this.imagePath = imagePath;
        this.projectId = projectId;
        this.recruiterMobile = recruiterMobile;
        this.requirementId = requirementId;
    }

    public SiteDetailsCardPage(String title, String location, String wage, String imagePath, String projectId, String recruiterMobile, String requirementId) {
        this(title, title, location, wage, imagePath, projectId, recruiterMobile, requirementId);
    }

    public Scene getScene(Runnable back) {
        return getScene(back, null);
    }

    public Scene getScene(Runnable back, Scene currentScene) {
        String displayProjName = (projectName != null && !projectName.isBlank()) ? projectName : roleTitle;
        String opportunityText = (roleTitle != null && !roleTitle.isBlank() ? roleTitle.toUpperCase() : "WORK") + " OPPORTUNITY";

        // UI Labels for Project Overview
        Label projNameVal = label(displayProjName, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label contactVal = label("Site Project Supervisor", "-fx-font-size:15px;-fx-text-fill:#1e1b15;");
        Label reqVal = label(roleTitle + " Required", "-fx-font-size:15px;-fx-text-fill:#1e1b15;");
        Label wageVal = label(wage, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#735c00;");

        VBox overview = new VBox(14,
                heading("Project Information"),
                detailBox("Project Name", projNameVal),
                detailBox("Contact Person", contactVal),
                detailBox("Worker Requirement", reqVal),
                detailBox("Daily Wage", wageVal)
        );
        overview.setPadding(new Insets(20));
        overview.setPrefWidth(420);
        overview.setStyle(boxStyle());

        // UI Labels for Site Address
        Label addressLine1Val = label("📍 " + location + " Construction Zone", "-fx-font-size:14px;-fx-text-fill:#1e1b15;");
        Label addressLine2Val = label("", "-fx-font-size:14px;-fx-text-fill:#4c4637;");
        addressLine2Val.setVisible(false);
        addressLine2Val.setManaged(false);
        Label landmarkVal = label("", "-fx-font-size:13px;-fx-font-weight:600;-fx-text-fill:#735c00;");
        landmarkVal.setVisible(false);
        landmarkVal.setManaged(false);
        Label locationPinVal = label("Region: " + location, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        FlowPane facilitiesPane = new FlowPane(8, 8);
        facilitiesPane.getChildren().addAll(
                facilityBadge("💧 Clean Water"),
                facilityBadge("⚡ Electricity"),
                facilityBadge("🏠 Accommodation")
        );

        VBox address = new VBox(10,
                heading("Site & Work Details"),
                addressLine1Val,
                addressLine2Val,
                landmarkVal,
                locationPinVal,
                facilitiesPane
        );
        address.setPadding(new Insets(20));
        address.setStyle(boxStyle());
        HBox.setHgrow(address, Priority.ALWAYS);

        ImageView image = new ImageView(load(imagePath));
        image.setFitWidth(220);
        image.setFitHeight(200);
        image.setPreserveRatio(false);
        StackPane imageFrame = new StackPane(image);
        imageFrame.setPrefSize(220, 200);
        imageFrame.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;");

        Label heroNameLabel = label(displayProjName, "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        heroNameLabel.setWrapText(true);
        heroNameLabel.setMaxWidth(460);

        Label heroBadgeLabel = label(opportunityText, "-fx-background-color:#fff8f0;-fx-border-color:#d4af37;-fx-border-radius:999px;-fx-background-radius:999px;-fx-padding:7px 12px;-fx-text-fill:#735c00;-fx-font-weight:700;");
        Label heroLocLabel = label(location, "-fx-font-size:15px;-fx-text-fill:#4c4637;");
        Label heroWageLabel = label("Daily wage: " + wage + " / day", "-fx-font-size:14px;-fx-text-fill:#4c4637;");

        VBox identity = new VBox(10, heroNameLabel, heroBadgeLabel, heroLocLabel, heroWageLabel);
        identity.setAlignment(Pos.CENTER_LEFT);
        HBox hero = new HBox(28, imageFrame, identity);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(20));
        hero.setStyle(boxStyle());

        Button apply = new Button("APPLY FOR THIS JOB");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:13px 20px;-fx-cursor:hand;");

        // Background Data Fetching for Real Project and Requirement
        new Thread(() -> {
            matchedProj = null;
            WorkforceRequirement matchedReq = null;
            boolean isCurated = (projectId == null || projectId.isBlank() || projectId.startsWith("CURATED_"));

            if (!isCurated) {
                try {
                    ProjectController pc = new ProjectController();
                    Project p = pc.getProject(projectId);

                    if (p == null && recruiterMobile != null && !recruiterMobile.isBlank()) {
                        List<Project> allProjects = pc.getAllProjects();
                        if (allProjects != null) {
                            String cleanMob = recruiterMobile.replaceAll("\\D", "");
                            for (Project proj : allProjects) {
                                String pMob = proj.getMobile() != null ? proj.getMobile().replaceAll("\\D", "") : "";
                                if (!cleanMob.isEmpty() && (pMob.equals(cleanMob) || pMob.endsWith(cleanMob) || cleanMob.endsWith(pMob))) {
                                    p = proj;
                                    break;
                                }
                            }
                        }
                    }
                    matchedProj = p;

                    WorkforceRequirementController rc = new WorkforceRequirementController();
                    List<WorkforceRequirement> allReqs = rc.getAllRequirements();
                    if (requirementId != null && !requirementId.isBlank() && allReqs != null) {
                        for (WorkforceRequirement r : allReqs) {
                            if (requirementId.equals(r.getRequirementId())) {
                                matchedReq = r;
                                break;
                            }
                        }
                    }
                    if (matchedReq == null && matchedProj != null) {
                        List<WorkforceRequirement> projReqs = rc.getRequirementsForProject(matchedProj.getProjectId());
                        if (projReqs != null && !projReqs.isEmpty()) {
                            for (WorkforceRequirement r : projReqs) {
                                if (roleTitle != null && (roleTitle.equalsIgnoreCase(r.getSubSkill()) || roleTitle.equalsIgnoreCase(r.getWorkerType()))) {
                                    matchedReq = r;
                                    break;
                                }
                            }
                            if (matchedReq == null) {
                                matchedReq = projReqs.get(0);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            final Project finalP = matchedProj;
            final WorkforceRequirement finalReq = matchedReq;

            Platform.runLater(() -> {
                if (finalP != null) {
                    // Real Project Name
                    if (finalP.getProjectName() != null && !finalP.getProjectName().isBlank()) {
                        heroNameLabel.setText(finalP.getProjectName());
                        projNameVal.setText(finalP.getProjectName());
                    }

                    // Real Contact Person
                    String contact = finalP.getContactName() != null && !finalP.getContactName().isBlank()
                            ? finalP.getContactName()
                            : "Site Manager";
                    if (finalP.getMobile() != null && !finalP.getMobile().isBlank()) {
                        contact += "  (" + finalP.getMobile() + ")";
                    }
                    contactVal.setText(contact);

                    // Real Address Details
                    String a1 = finalP.getAddressLine1() != null ? finalP.getAddressLine1().trim() : "";
                    String a2 = finalP.getAddressLine2() != null ? finalP.getAddressLine2().trim() : "";
                    String lm = finalP.getLandmark() != null ? finalP.getLandmark().trim() : "";
                    String city = finalP.getCity() != null ? finalP.getCity().trim() : "";
                    String state = finalP.getState() != null ? finalP.getState().trim() : "";
                    String pin = finalP.getPincode() != null ? finalP.getPincode().trim() : "";

                    if (!a1.isEmpty()) {
                        addressLine1Val.setText("📍 " + a1);
                    } else {
                        addressLine1Val.setText("📍 Site Plot / Sector Location");
                    }

                    if (!a2.isEmpty()) {
                        addressLine2Val.setText(a2);
                        addressLine2Val.setVisible(true);
                        addressLine2Val.setManaged(true);
                    } else {
                        addressLine2Val.setVisible(false);
                        addressLine2Val.setManaged(false);
                    }

                    if (!lm.isEmpty()) {
                        landmarkVal.setText("Landmark: " + lm);
                        landmarkVal.setVisible(true);
                        landmarkVal.setManaged(true);
                    } else {
                        landmarkVal.setVisible(false);
                        landmarkVal.setManaged(false);
                    }

                    String fullLoc = (city + (state.isEmpty() ? "" : ", " + state) + (pin.isEmpty() ? "" : " - " + pin)).trim();
                    if (!fullLoc.isEmpty()) {
                        locationPinVal.setText("City & State: " + fullLoc);
                        heroLocLabel.setText(fullLoc);
                    }

                    // Real Site Image if available
                    if (finalP.getImageUrls() != null && !finalP.getImageUrls().isEmpty()) {
                        String firstImg = finalP.getImageUrls().get(0);
                        if (firstImg != null && !firstImg.isBlank()) {
                            Image loaded = load(firstImg);
                            if (loaded != null) {
                                image.setImage(loaded);
                            }
                        }
                    }
                }

                if (finalReq != null) {
                    // Real Worker Requirement
                    String skillText = finalReq.getSubSkill() != null && !finalReq.getSubSkill().isBlank()
                            ? finalReq.getSubSkill()
                            : (finalReq.getWorkerType() != null ? finalReq.getWorkerType() : roleTitle);
                    String qty = finalReq.getQuantity() > 0 ? finalReq.getQuantity() + " " : "";
                    reqVal.setText(qty + skillText + " Required");

                    // Real Daily Wage
                    if (finalReq.getDailyWages() > 0) {
                        String wStr = "₹" + String.format("%,d", (long) finalReq.getDailyWages()) + " / day";
                        wageVal.setText(wStr);
                        heroWageLabel.setText("Daily wage: " + wStr);
                    }

                    // Facilities Badges
                    facilitiesPane.getChildren().clear();
                    if (finalReq.isWaterFacility()) facilitiesPane.getChildren().add(facilityBadge("💧 Clean Water"));
                    if (finalReq.isElectricityFacility()) facilitiesPane.getChildren().add(facilityBadge("⚡ Electricity"));
                    if (finalReq.isAccommodationFacility()) facilitiesPane.getChildren().add(facilityBadge("🏠 Accommodation"));
                    if (finalReq.isTransportationFacility()) facilitiesPane.getChildren().add(facilityBadge("🚌 Transportation"));
                    if (facilitiesPane.getChildren().isEmpty()) {
                        facilitiesPane.getChildren().add(facilityBadge("Standard Site Amenities"));
                    }
                }
            });
        }).start();
        // Check if already applied
        if (SessionManager.currentWorker != null) {
            apply.setText("CHECKING STATUS...");
            apply.setDisable(true);
            new Thread(() -> {
                JobApplicationController controller = new JobApplicationController();
                boolean hasApplied = controller.hasWorkerApplied(
                        SessionManager.currentWorker.getMobileNumber(),
                        projectId,
                        requirementId,
                        roleTitle,
                        location
                );
                JobApplication activeAssignment = controller.getActiveAssignedApplicationForWorker(SessionManager.currentWorker.getMobileNumber());
                Platform.runLater(() -> {
                    if (matchedProj != null && "Completed".equalsIgnoreCase(matchedProj.getStatus())) {
                        apply.setText("PROJECT COMPLETED");
                        apply.setStyle("-fx-background-color:#9e9e9e;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:14px 28px;-fx-background-radius:8px;");
                        apply.setDisable(true);
                    } else if (matchedProj != null && ("Requirement Fulfilled".equalsIgnoreCase(matchedProj.getStatus()) || "Unavailable".equalsIgnoreCase(matchedProj.getStatus()))) {
                        apply.setText("REQUIREMENT FULFILLED");
                        apply.setStyle("-fx-background-color:#fff3e0;-fx-text-fill:#b48700;-fx-border-color:#ffe082;-fx-border-radius:8px;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:14px 28px;-fx-background-radius:8px;");
                        apply.setDisable(true);
                    } else if (hasApplied) {
                        apply.setText("ALREADY APPLIED ✓");
                        apply.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:14px 28px;-fx-background-radius:8px;");
                        apply.setDisable(true);
                    } else if (activeAssignment != null) {
                        apply.setText("ASSIGNED TO ANOTHER PROJECT");
                        apply.setStyle("-fx-background-color:#c67d00;-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:14px 24px;-fx-background-radius:8px;");
                        apply.setDisable(true);
                    } else {
                        apply.setText("APPLY FOR THIS JOB");
                        apply.setStyle("-fx-background-color:#d4af37;-fx-text-fill:#1e1b15;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:14px 28px;-fx-background-radius:8px;-fx-cursor:hand;");
                        apply.setDisable(false);
                    }
                });
            }).start();
        } else {
            apply.setText("APPLY FOR THIS JOB");
            apply.setStyle("-fx-background-color:#d4af37;-fx-text-fill:#1e1b15;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:14px 28px;-fx-background-radius:8px;-fx-cursor:hand;");
            apply.setDisable(false);
        }

        apply.setOnAction(e -> {
            if (SessionManager.currentWorker == null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow();
                javafx.scene.Scene returnScene = (currentScene != null) ? currentScene : apply.getScene();
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Worker Account Required");
                alert.setHeaderText("Please log in or sign up to apply");
                alert.setContentText("You need an active worker profile to apply for " + roleTitle + ".\n\nChoose an option to continue:");
                
                javafx.scene.control.ButtonType loginBtnType = new javafx.scene.control.ButtonType("Login");
                javafx.scene.control.ButtonType signUpBtnType = new javafx.scene.control.ButtonType("Create Account");
                javafx.scene.control.ButtonType cancelBtnType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(loginBtnType, signUpBtnType, cancelBtnType);

                var result = alert.showAndWait();
                if (result.isPresent() && result.get() == loginBtnType) {
                    stage.setScene(new com.dihadi.view.worker.WorkerLoginPage(() -> stage.setScene(returnScene)).getLoginScene());
                } else if (result.isPresent() && result.get() == signUpBtnType) {
                    stage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(() -> stage.setScene(returnScene)));
                }
                return;
            }

            if (matchedProj != null && "Completed".equalsIgnoreCase(matchedProj.getStatus())) {
                com.dihadi.view.NotificationToast.show(apply, "Project Completed",
                        "This project has already been completed by the recruiter and moved to past projects.",
                        com.dihadi.view.NotificationToast.ToastType.ALERT);
                return;
            }

            if (matchedProj != null && ("Requirement Fulfilled".equalsIgnoreCase(matchedProj.getStatus()) || "Unavailable".equalsIgnoreCase(matchedProj.getStatus()))) {
                com.dihadi.view.NotificationToast.show(apply, "Requirement Fulfilled",
                        "Workforce requirements for this project have already been fulfilled.",
                        com.dihadi.view.NotificationToast.ToastType.ALERT);
                return;
            }

            JobApplication activeAssignment = new JobApplicationController().getActiveAssignedApplicationForWorker(SessionManager.currentWorker.getMobileNumber());
            if (activeAssignment != null) {
                String activeProj = activeAssignment.getJobTitle() != null ? activeAssignment.getJobTitle() : "another active project";
                com.dihadi.view.NotificationToast.show(apply, "Already Assigned",
                        "You are already assigned to active project '" + activeProj + "'. A worker can only be assigned to one project at a time.",
                        com.dihadi.view.NotificationToast.ToastType.ALERT);
                return;
            }

            apply.setText("Applied ✓");
            apply.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:14px 28px;-fx-background-radius:8px;");
            apply.setDisable(true);
            new Thread(() -> {
                JobApplication app = new JobApplication(
                        String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int) (Math.random() * 1000)),
                        SessionManager.currentWorker.getMobileNumber(),
                        roleTitle,
                        location,
                        wage,
                        "Pending",
                        projectId,
                        recruiterMobile,
                        requirementId
                );
                new JobApplicationController().saveApplication(app);
                String workerName = (SessionManager.currentWorker.getFirstName() != null ? SessionManager.currentWorker.getFirstName() : "") + " " +
                        (SessionManager.currentWorker.getLastName() != null ? SessionManager.currentWorker.getLastName() : "");
                workerName = workerName.trim();
                if (workerName.isEmpty()) workerName = "Worker (" + SessionManager.currentWorker.getMobileNumber() + ")";

                String notifProjName = (matchedProj != null && matchedProj.getProjectName() != null && !matchedProj.getProjectName().isBlank())
                        ? matchedProj.getProjectName()
                        : projectName;

                new com.dihadi.controller.NotificationController().notifyRecruiterApplicationReceived(
                        app,
                        workerName,
                        SessionManager.currentWorker.getMobileNumber(),
                        notifProjName
                );
                javafx.application.Platform.runLater(() -> {
                    com.dihadi.view.NotificationToast.show(apply, "Application Submitted",
                            "Your application for " + roleTitle + " has been successfully submitted.",
                            com.dihadi.view.NotificationToast.ToastType.SUCCESS);
                });
            }).start();
        });

        Button close = new Button("←  BACK TO PROJECT");
        close.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        close.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-cursor:hand;");
        apply.setMaxWidth(Region.USE_PREF_SIZE);
        HBox actions = new HBox(14, close, apply);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 20, 16, 20));
        actions.setStyle(boxStyle());

        VBox content = new VBox(22,
                hero, new HBox(20, overview, address), actions);
        content.setPadding(new Insets(28));
        content.setMaxWidth(1020);
        ScrollPane scroll = new ScrollPane(content);
        com.dihadi.view.ScrollUtils.style(scroll);
        scroll.setMaxSize(1040, 640);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;");
        StackPane card = new StackPane(scroll);
        card.setMaxSize(1080, 670);
        card.setStyle("-fx-background-color:rgba(255,253,249,0.76);-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.22),30,0,0,8px);");

        StackPane root = new StackPane();
        if (currentScene != null) {
            javafx.scene.image.WritableImage snapshot = currentScene.snapshot(null);
            ImageView bgView = new ImageView(snapshot);
            javafx.scene.effect.BoxBlur blur = new javafx.scene.effect.BoxBlur(12, 12, 3);
            bgView.setEffect(blur);
            root.getChildren().add(bgView);
        }
        root.getChildren().add(card);
        root.setStyle("-fx-background-color:rgba(233, 226, 215, 0.4);");
        Scene scene = new Scene(root, 1120, 740);
        scene.windowProperty().addListener((o, a, w) -> {
            if (w instanceof Stage stage) {
                stage.setMinWidth(980);
                stage.setMinHeight(680);
            }
        });
        return scene;
    }

    private Label heading(String s) {
        return label(s, "-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#735c00;");
    }

    private VBox detailBox(String k, Label valueLabel) {
        return new VBox(3, label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;"), valueLabel);
    }

    private Label facilityBadge(String text) {
        Label l = label(text, "-fx-background-color:#e8f4ea;-fx-text-fill:#2a7e3b;-fx-font-weight:700;-fx-font-size:12px;-fx-padding:4px 10px;-fx-background-radius:999px;-fx-border-color:#b7dfb9;-fx-border-radius:999px;");
        return l;
    }

    private Label label(String s) {
        return label(s, "-fx-font-size:15px;-fx-text-fill:#1e1b15;");
    }

    private Label label(String s, String style) {
        Label l = new Label(s);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + style);
        return l;
    }

    private String boxStyle() {
        return "-fx-background-color:rgba(255,250,242,.72);-fx-background-radius:18px;-fx-border-color:rgba(217,207,189,.90);-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),12,0,0,3px);";
    }

    private Image load(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                return new Image(path, true);
            }
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                return new Image(file.toURI().toString(), true);
            }
            var r = getClass().getResource(path);
            if (r != null) {
                return new Image(r.toExternalForm());
            }
        } catch (Exception ignored) {}
        return null;
    }
}
