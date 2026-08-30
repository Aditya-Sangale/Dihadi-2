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

    public RecruiterWorkerProfilePage(String name, String category, String demographic, String location, String wage,
            String photo) {
        this(name, category, demographic, location, wage, photo, "");
    }

    public RecruiterWorkerProfilePage(String name, String category, String demographic, String location, String wage,
            String photo, String workerMobile) {
        this.name = name;
        this.category = category;
        this.demographic = demographic;
        this.location = location;
        this.wage = wage;
        this.photo = photo;
        this.workerMobile = workerMobile;
    }

    public Scene getProfileScene(Runnable back, javafx.scene.Scene currentScene) {
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
        VBox personal = new VBox(12, heading("Personal Information"), details("Location", location),
                details("Daily wage", "Rs. " + wage + " / day"),
                details("Availability", "Available for suitable projects"));
        personal.setPadding(new Insets(20));
        personal.setStyle(boxStyle());
        VBox skills = new VBox(12, heading("Skills & Work Details"),
                label("Category", "-fx-font-size:12px;-fx-text-fill:#7e7665;"),
                label(category, "-fx-font-size:16px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"),
                label("This worker’s profile and image are supplied from the recruiter marketplace records.",
                        "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        skills.setPadding(new Insets(20));
        skills.setStyle(boxStyle());
        Button hire = new Button("HIRE THIS WORKER");
        hire.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:13px 22px;");
        hire.setOnAction(e -> {
            hire.setText("HIRING REQUEST SENT ✓");
            hire.setDisable(true);
            new Thread(() -> {
                try {
                    String recruiterMobile = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber() != null)
                            ? com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber()
                            : "";
                    String appId = String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000));
                    String targetMobile = (workerMobile != null && !workerMobile.isBlank()) ? workerMobile : "";
                    
                    if (targetMobile.isBlank()) {
                        java.util.List<com.dihadi.model.Worker> allW = new com.dihadi.controller.WorkerController().getAllWorkers();
                        if (allW != null) {
                            for (com.dihadi.model.Worker w : allW) {
                                String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " + (w.getLastName() != null ? w.getLastName() : "")).trim();
                                if (fullName.equalsIgnoreCase(name) || (w.getFirstName() != null && name.contains(w.getFirstName()))) {
                                    targetMobile = w.getMobileNumber();
                                    break;
                                }
                            }
                        }
                    }

                    com.dihadi.model.JobApplication app = new com.dihadi.model.JobApplication(
                            appId,
                            targetMobile,
                            category + " Hiring Request",
                            location,
                            wage,
                            "Pending",
                            "",
                            recruiterMobile,
                            ""
                    );
                    new com.dihadi.controller.JobApplicationController().saveApplication(app);
                    System.out.println("Hiring request sent successfully to worker: " + targetMobile);
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
        scroll.setFitToWidth(true);
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