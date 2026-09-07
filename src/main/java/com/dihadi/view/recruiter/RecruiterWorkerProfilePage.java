package com.dihadi.view.recruiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Compact profile card shown when a recruiter selects a worker. */
public class RecruiterWorkerProfilePage {
    private final String name, category, demographic, location, wage, photo, workerMobile;
    private final Runnable onWorkerHired;

    public RecruiterWorkerProfilePage(String name, String category, String demographic, String location, String wage,
            String photo) {
        this(name, category, demographic, location, wage, photo, "", null);
    }

    public RecruiterWorkerProfilePage(String name, String category, String demographic, String location, String wage,
            String photo, String workerMobile) {
        this(name, category, demographic, location, wage, photo, workerMobile, null);
    }

    /** Optional callback lets a source result card reflect a completed hire immediately. */
    public RecruiterWorkerProfilePage(String name, String category, String demographic, String location, String wage,
            String photo, String workerMobile, Runnable onWorkerHired) {
        this.name = name;
        this.category = category;
        this.demographic = demographic;
        this.location = location;
        this.wage = wage;
        this.photo = photo;
        this.workerMobile = workerMobile;
        this.onWorkerHired = onWorkerHired;
    }

    /** Updates the originating recruiter-result card after a hire is confirmed. */
    public static void markResultCardHired(VBox card) {
        if (card == null || card.getChildren().size() < 3) {
            return;
        }

        // Update Top -> details -> availability & subtext labels
        if (card.getChildren().get(0) instanceof HBox top && top.getChildren().size() >= 2
                && top.getChildren().get(1) instanceof VBox details && details.getChildren().size() >= 5) {
            if (details.getChildren().get(4) instanceof Label avail) {
                avail.setText("•  Already Hired");
                avail.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");
            }
            if (details.getChildren().size() >= 6 && details.getChildren().get(5) instanceof Label sub) {
                sub.setText("Already hired for current project");
                sub.setStyle("-fx-font-size:10px;-fx-font-style:italic;-fx-text-fill:#2e7d32;");
                sub.setVisible(true);
                sub.setManaged(true);
            }
        }

        if (card.getChildren().get(2) instanceof HBox actions
                && actions.getChildren().size() >= 3 && actions.getChildren().get(2) instanceof Button hire) {
            hire.setText("ALREADY HIRED");
            hire.setDisable(true);
            hire.setStyle(
                    "-fx-background-color:#e8f5e9;-fx-background-radius:18px;-fx-border-color:#a5d6a7;-fx-border-radius:18px;-fx-text-fill:#2e7d32;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;");
        }
    }

    public Scene getProfileScene(Runnable back, javafx.scene.Scene currentScene) {
        com.dihadi.service.WorkerAvailabilityService.AvailabilityInfo avail =
                com.dihadi.service.WorkerAvailabilityService.getAvailability(workerMobile, name);

        ImageView portrait = new ImageView(load(photo));
        portrait.setFitWidth(180);
        portrait.setFitHeight(180);
        portrait.setPreserveRatio(false);
        StackPane portraitFrame = new StackPane(portrait);
        portraitFrame.setPrefSize(180, 180);
        portraitFrame.setStyle(
                "-fx-background-color:#f4ede2;-fx-background-radius:999px;-fx-border-color:#d4af37;-fx-border-width:3px;-fx-border-radius:999px;");
        VBox identity = new VBox(10,
                label(name, "-fx-font-family:Georgia;-fx-font-size:29px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                label(category,
                        "-fx-background-color:#fff8f0;-fx-border-color:#d4af37;-fx-border-radius:999px;-fx-background-radius:999px;-fx-padding:7px 12px;-fx-text-fill:#735c00;-fx-font-weight:700;"),
                label(demographic, "-fx-font-size:15px;-fx-text-fill:#4c4637;"),
                label("Experience: Verified DIHADI worker profile", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        identity.setAlignment(Pos.CENTER_LEFT);
        HBox hero = new HBox(28, portraitFrame, identity);
        hero.setAlignment(Pos.CENTER_LEFT);

        String availDesc = avail.isAvailable()
                ? "Available for suitable projects"
                : (avail.isHiredByCurrentRecruiter()
                        ? "Already hired for current project"
                        : "Unavailable, already assigned to other project");
        VBox personal = new VBox(12, heading("Personal Information"), details("Location", location),
                details("Daily wage", "Rs. " + wage + " / day"),
                details("Availability", availDesc));
        personal.setPadding(new Insets(20));
        personal.setStyle(boxStyle());

        VBox skills = new VBox(12, heading("Skills & Work Details"),
                label("Category", "-fx-font-size:12px;-fx-text-fill:#7e7665;"),
                label(category, "-fx-font-size:16px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"),
                label("This worker’s profile and image are supplied from the recruiter marketplace records.",
                        "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        skills.setPadding(new Insets(20));
        skills.setStyle(boxStyle());

        Button hire = new Button(avail.isAvailable() ? "HIRE THIS WORKER"
                : (avail.isHiredByCurrentRecruiter() ? "ALREADY HIRED FOR CURRENT PROJECT" : "WORKER CURRENTLY UNAVAILABLE"));
        if (avail.isAvailable()) {
            hire.setStyle(
                    "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:13px 22px;-fx-cursor:hand;");
        } else if (avail.isHiredByCurrentRecruiter()) {
            hire.setDisable(true);
            hire.setStyle(
                    "-fx-background-color:#e8f5e9;-fx-background-radius:999px;-fx-border-color:#a5d6a7;-fx-border-width:1.5px;-fx-text-fill:#2e7d32;-fx-font-size:15px;-fx-font-weight:700;-fx-padding:13px 22px;");
        } else {
            hire.setDisable(true);
            hire.setStyle(
                    "-fx-background-color:#f8d7da;-fx-background-radius:999px;-fx-text-fill:#721c24;-fx-font-size:15px;-fx-font-weight:700;-fx-padding:13px 22px;");
        }

        hire.setOnAction(e -> {
            hire.setText("ALREADY HIRED FOR CURRENT PROJECT");
            hire.setDisable(true);
            hire.setStyle(
                    "-fx-background-color:#e8f5e9;-fx-background-radius:999px;-fx-border-color:#a5d6a7;-fx-border-width:1.5px;-fx-text-fill:#2e7d32;-fx-font-size:15px;-fx-font-weight:700;-fx-padding:13px 22px;");
            if (onWorkerHired != null) {
                onWorkerHired.run();
            }
            new Thread(() -> {
                try {
                    String recruiterMobile = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber() != null)
                            ? com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber()
                            : "";
                    String appId = String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000));

                    String assignedProjectId = "";
                    String assignedProjectName = category + " Project";
                    try {
                        if (com.dihadi.view.SessionManager.currentRecruiterProject != null) {
                            com.dihadi.model.Project cp = com.dihadi.view.SessionManager.currentRecruiterProject;
                            assignedProjectId = cp.getProjectId() != null ? cp.getProjectId() : "";
                            if (cp.getProjectName() != null && !cp.getProjectName().isBlank()) {
                                assignedProjectName = cp.getProjectName().trim();
                            }
                        }

                        if (assignedProjectId.isEmpty()) {
                            java.util.List<com.dihadi.model.Project> recProjects = new com.dihadi.controller.ProjectController().getAllProjects();
                            if (recProjects != null && com.dihadi.view.SessionManager.currentRecruiter != null) {
                                com.dihadi.model.Recruiter curR = com.dihadi.view.SessionManager.currentRecruiter;
                                String rMob = curR.getMobileNumber();
                                String rMobDigits = rMob != null ? rMob.replaceAll("\\D", "") : "";
                                String rEmail = curR.getEmail() != null ? curR.getEmail().trim().toLowerCase() : "";
                                String rComp = curR.getCompanyName() != null ? curR.getCompanyName().trim().toLowerCase() : "";

                                java.util.List<com.dihadi.model.Project> myProjects = new java.util.ArrayList<>();
                                for (com.dihadi.model.Project p : recProjects) {
                                    String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
                                    String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
                                    String pComp = p.getContactName() != null ? p.getContactName().trim().toLowerCase() : "";

                                    boolean matchesRecruiter = (!rMobDigits.isEmpty() && !pMob.isEmpty() && (pMob.equals(rMobDigits) || pMob.endsWith(rMobDigits) || rMobDigits.endsWith(pMob)))
                                            || (!rEmail.isEmpty() && !pEmail.isEmpty() && pEmail.equals(rEmail))
                                            || (!rComp.isEmpty() && !pComp.isEmpty() && (pComp.contains(rComp) || rComp.contains(pComp)));

                                    if (matchesRecruiter) {
                                        myProjects.add(p);
                                    }
                                }

                                // Sort newest first (highest numeric/alphanumeric projectId)
                                myProjects.sort((p1, p2) -> {
                                    String id1 = p1.getProjectId() != null ? p1.getProjectId() : "";
                                    String id2 = p2.getProjectId() != null ? p2.getProjectId() : "";
                                    return id2.compareTo(id1);
                                });

                                for (com.dihadi.model.Project p : myProjects) {
                                    if ("Active".equalsIgnoreCase(p.getStatus()) || "Available".equalsIgnoreCase(p.getStatus()) || "Unavailable".equalsIgnoreCase(p.getStatus())) {
                                        assignedProjectId = p.getProjectId() != null ? p.getProjectId() : "";
                                        if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                                            assignedProjectName = p.getProjectName().trim();
                                        }
                                        break;
                                    } else if (assignedProjectId.isEmpty()) {
                                        assignedProjectId = p.getProjectId() != null ? p.getProjectId() : "";
                                        if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                                            assignedProjectName = p.getProjectName().trim();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    java.util.List<com.dihadi.model.Worker> allW = new com.dihadi.controller.WorkerController().getAllWorkers();
                    com.dihadi.model.Worker matchedWorker = null;
                    String targetMobile = "";
                    String targetWorkerName = name;

                    // A. Check if workerMobile matches a real registered worker
                    if (workerMobile != null && !workerMobile.isBlank()) {
                        String cleanDigits = workerMobile.replaceAll("\\D", "");
                        String last10 = cleanDigits.length() >= 10 ? cleanDigits.substring(cleanDigits.length() - 10) : cleanDigits;
                        if (allW != null) {
                            for (com.dihadi.model.Worker w : allW) {
                                String wDigits = w.getMobileNumber() != null ? w.getMobileNumber().replaceAll("\\D", "") : "";
                                String wLast10 = wDigits.length() >= 10 ? wDigits.substring(wDigits.length() - 10) : wDigits;
                                if (!last10.isEmpty() && last10.equals(wLast10)) {
                                    matchedWorker = w;
                                    break;
                                }
                            }
                        }
                    }

                    // B. If not matched, check if any registered worker matches the name
                    if (matchedWorker == null && allW != null && name != null && !name.isBlank()) {
                        String searchName = name.trim().toLowerCase();
                        for (com.dihadi.model.Worker w : allW) {
                            String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " + (w.getLastName() != null ? w.getLastName() : "")).trim().toLowerCase();
                            if (fullName.equals(searchName) || (w.getFirstName() != null && !w.getFirstName().isBlank() && searchName.contains(w.getFirstName().toLowerCase()))) {
                                matchedWorker = w;
                                break;
                            }
                        }
                    }

                    // C. If not matched, check if any registered worker matches category/trade
                    if (matchedWorker == null && allW != null && category != null && !category.isBlank()) {
                        String catLower = category.trim().toLowerCase();
                        for (com.dihadi.model.Worker w : allW) {
                            if (w.getWorkerType() != null && (w.getWorkerType().toLowerCase().contains(catLower) || catLower.contains(w.getWorkerType().toLowerCase()))) {
                                matchedWorker = w;
                                break;
                            }
                        }
                    }

                    // D. If not matched and SessionManager.currentWorker exists, link to current logged-in worker
                    if (matchedWorker == null && com.dihadi.view.SessionManager.currentWorker != null) {
                        matchedWorker = com.dihadi.view.SessionManager.currentWorker;
                    }

                    // E. If not matched, pick first worker from allW if any
                    if (matchedWorker == null && allW != null && !allW.isEmpty()) {
                        matchedWorker = allW.get(0);
                    }

                    if (matchedWorker != null) {
                        targetMobile = matchedWorker.getMobileNumber() != null ? matchedWorker.getMobileNumber() : "";
                        String fName = ((matchedWorker.getFirstName() != null ? matchedWorker.getFirstName() : "") + " " + (matchedWorker.getLastName() != null ? matchedWorker.getLastName() : "")).trim();
                        if (!fName.isEmpty()) targetWorkerName = fName;
                    }

                    if (targetMobile.isBlank()) {
                        targetMobile = (workerMobile != null && !workerMobile.isBlank()) ? workerMobile : "9822010001";
                    }

                    String jobTitle = "Hiring Request: " + category + " for " + assignedProjectName;
                    com.dihadi.model.JobApplication app = new com.dihadi.model.JobApplication(
                            appId,
                            targetMobile,
                            jobTitle,
                            location,
                            wage,
                            "Pending",
                            assignedProjectId,
                            recruiterMobile,
                            "DIRECT_HIRE",
                            targetWorkerName
                    );
                    new com.dihadi.controller.JobApplicationController().saveApplication(app);

                    // If workerMobile is a benchmark or alternative number, save secondary mapping
                    if (workerMobile != null && !workerMobile.isBlank() && !workerMobile.equals(targetMobile)) {
                        com.dihadi.model.JobApplication altApp = new com.dihadi.model.JobApplication(
                                appId + "_alt",
                                workerMobile,
                                jobTitle,
                                location,
                                wage,
                                "Pending",
                                assignedProjectId,
                                recruiterMobile,
                                "DIRECT_HIRE",
                                name
                        );
                        new com.dihadi.controller.JobApplicationController().saveApplication(altApp);
                    }

                    com.dihadi.service.WorkerAvailabilityService.hireWorker(
                            targetMobile,
                            targetWorkerName,
                            assignedProjectId,
                            assignedProjectName,
                            recruiterMobile
                    );
                    if (workerMobile != null && !workerMobile.isBlank() && !workerMobile.equals(targetMobile)) {
                        com.dihadi.service.WorkerAvailabilityService.hireWorker(
                                workerMobile,
                                name,
                                assignedProjectId,
                                assignedProjectName,
                                recruiterMobile
                        );
                    }

                    String recName = "Site Recruiter";
                    if (com.dihadi.view.SessionManager.currentRecruiter != null) {
                        String fn = com.dihadi.view.SessionManager.currentRecruiter.getFirstName() != null ? com.dihadi.view.SessionManager.currentRecruiter.getFirstName() : "";
                        String ln = com.dihadi.view.SessionManager.currentRecruiter.getLastName() != null ? com.dihadi.view.SessionManager.currentRecruiter.getLastName() : "";
                        String full = (fn + " " + ln).trim();
                        if (!full.isEmpty()) recName = full;
                    }
                    new com.dihadi.controller.NotificationController().notifyWorkerHiringRequest(
                            targetMobile,
                            recName,
                            recruiterMobile,
                            category,
                            location,
                            wage,
                            assignedProjectName
                    );
                    if (workerMobile != null && !workerMobile.isBlank() && !workerMobile.equals(targetMobile)) {
                        new com.dihadi.controller.NotificationController().notifyWorkerHiringRequest(
                                workerMobile,
                                recName,
                                recruiterMobile,
                                category,
                                location,
                                wage,
                                assignedProjectName
                        );
                    }

                    final String alertWorkerName = targetWorkerName;
                    javafx.application.Platform.runLater(() -> {
                        com.dihadi.view.NotificationToast.show(
                                "Hiring Request Sent",
                                "Hiring request sent to " + alertWorkerName + ". It is now waiting in their Worker Dashboard.",
                                com.dihadi.view.NotificationToast.ToastType.SUCCESS
                        );
                    });
                    System.out.println("Hiring request sent successfully to worker: " + targetMobile + " (" + targetWorkerName + ") for project: " + assignedProjectId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        Button close = new Button("←  BACK TO WORKERS");
        close.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-cursor:hand;");
        close.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        HBox actions = new HBox(14, close, hire);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(hire, Priority.ALWAYS);
        VBox heroCard = new VBox(14, hero);
        heroCard.setPadding(new Insets(20));
        heroCard.setStyle(boxStyle());

        actions.setPadding(new Insets(14, 20, 14, 20));
        actions.setStyle(boxStyle());

        VBox content = new VBox(20, heroCard, new HBox(18, personal, skills), actions);
        content.setPadding(new Insets(24));
        content.setMaxWidth(900);
        content.setStyle("-fx-background-color:transparent;");

        ScrollPane scroll = new ScrollPane(content);
        com.dihadi.view.ScrollUtils.style(scroll);
        scroll.setMaxSize(930, 620);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        
        StackPane card = new StackPane(scroll);
        card.setMaxSize(960, 650);
        card.setStyle("-fx-background-color:rgba(255,248,240,0.30);-fx-background-radius:24px;-fx-border-color:rgba(212,175,55,0.45);-fx-border-radius:24px;-fx-border-width:1.5px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,.25),35,0,0,10px);");

        StackPane root = new StackPane();
        if (currentScene != null) {
            javafx.scene.image.WritableImage snapshot = currentScene.snapshot(null);
            ImageView bgView = new ImageView(snapshot);
            javafx.scene.effect.BoxBlur blur = new javafx.scene.effect.BoxBlur(14, 14, 3);
            bgView.setEffect(blur);
            root.getChildren().add(bgView);
        }
        root.getChildren().add(card);
        root.setStyle("-fx-background-color:rgba(30, 27, 21, 0.45);");
        Scene scene = new Scene(root, currentScene != null ? currentScene.getWidth() : 1120, currentScene != null ? currentScene.getHeight() : 740);
        scene.windowProperty().addListener((o, a, w) -> {
            if (w instanceof Stage s) {
                s.setMinWidth(900);
                s.setMinHeight(640);
            }
        });
        return scene;
    }

    private VBox details(String key, String value) {
        return new VBox(3, label(key, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;"),
                label(value, "-fx-font-size:15px;-fx-text-fill:#1e1b15;"));
    }

    private Label heading(String value) {
        return label(value, "-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:700;-fx-text-fill:#735c00;");
    }

    private Label label(String value, String style) {
        Label l = new Label(value);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + style);
        return l;
    }

    private String boxStyle() {
        return "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),16,0,0,5px);";
    }

    private Image load(String path) {
        try {
            if (path == null || path.isBlank())
                return null;
            if (path.startsWith("http"))
                return new Image(path, true);
            var r = getClass().getResource(path);
            return r == null ? null : new Image(r.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }
}
