package com.dihadi.view.worker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Compact project-opening details card shown when a worker selects Apply Now.
 */
public class SiteDetailsCardPage {
    private final String title, location, wage, imagePath;
    private final String projectId, recruiterMobile, requirementId;

    public SiteDetailsCardPage(String title, String location, String wage, String imagePath, String projectId, String recruiterMobile, String requirementId) {
        this.title = title;
        this.location = location;
        this.wage = wage;
        this.imagePath = imagePath;
        this.projectId = projectId;
        this.recruiterMobile = recruiterMobile;
        this.requirementId = requirementId;
    }

    public Scene getScene(Runnable back, Scene currentScene) {
        VBox overview = new VBox(14,
                heading("Project Overview"), details("Project Name", title),
                details("Contact Person", "Project Site Supervisor"),
                details("Worker requirement", "Skilled " + title), details("Daily wage", wage));
        overview.setPadding(new Insets(20));
        overview.setStyle(boxStyle());
        VBox address = new VBox(10, heading("Site Address"), label(
                "Construction site\n" + location + "\nContact the project team after your application is reviewed."));
        address.setPadding(new Insets(20));
        address.setStyle(boxStyle());
        VBox left = new VBox(20, overview, address);
        left.setPrefWidth(560);
        ImageView image = new ImageView(load(imagePath));
        image.setFitWidth(360);
        image.setFitHeight(300);
        image.setPreserveRatio(false);
        VBox right = new VBox(14, heading("Site Imagery"), image);
        right.setPadding(new Insets(20));
        right.setStyle(boxStyle());
        Button apply = new Button("APPLY FOR THIS JOB");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:13px 20px;");
        
        if (com.dihadi.view.SessionManager.currentWorker != null) {
            apply.setText("CHECKING STATUS...");
            apply.setDisable(true);
            new Thread(() -> {
                boolean hasApplied = false;
                java.util.List<com.dihadi.model.JobApplication> apps = new com.dihadi.controller.JobApplicationController().getApplicationsByWorker(com.dihadi.view.SessionManager.currentWorker.getMobileNumber());
                for (com.dihadi.model.JobApplication app : apps) {
                    if (app.getJobTitle().equals(title) && app.getJobLocation().equals(location)) {
                        hasApplied = true;
                        break;
                    }
                }
                final boolean finalHasApplied = hasApplied;
                javafx.application.Platform.runLater(() -> {
                    if (finalHasApplied) {
                        apply.setText("ALREADY APPLIED ✓");
                        apply.setDisable(true);
                    } else {
                        apply.setText("APPLY FOR THIS JOB");
                        apply.setDisable(false);
                    }
                });
            }).start();
        }

        apply.setOnAction(e -> {
            apply.setText("Applied ✓");
            apply.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:14px 28px;-fx-background-radius:8px;");
            apply.setDisable(true);
            if (com.dihadi.view.SessionManager.currentWorker != null) {
                new Thread(() -> { 
                    com.dihadi.model.JobApplication app = new com.dihadi.model.JobApplication(
                            String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)),
                            com.dihadi.view.SessionManager.currentWorker.getMobileNumber(),
                            title,
                            location,
                            wage,
                            "Pending",
                            projectId,
                            recruiterMobile,
                            requirementId
                    );
                    new com.dihadi.controller.JobApplicationController().saveApplication(app);
                }).start();
            }
        });
        Button close = new Button("←  BACK");
        close.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        close.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-cursor:hand;");
        HBox actions = new HBox(14, close, apply);
        HBox.setHgrow(apply, Priority.ALWAYS);
        actions.setAlignment(Pos.CENTER_RIGHT);
        VBox content = new VBox(22,
                new VBox(4, heading("Construction Site Details"),
                        label("Ref: DIHADI-" + title.toUpperCase().replace(' ', '-'))),
                new HBox(20, left, right), actions);
        content.setPadding(new Insets(28));
        content.setMaxWidth(1020);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setMaxSize(1040, 640);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;");
        StackPane card = new StackPane(scroll);
        card.setMaxSize(1080, 670);
        card.setStyle("-fx-background-color:rgba(255,253,249,0.85);-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.22),30,0,0,8px);");
        
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
        scene.windowProperty().addListener((o, a, w) -> { if (w instanceof Stage stage) { stage.setMinWidth(980); stage.setMinHeight(680); } });
        return scene;
    }

    private Label heading(String s) {
        return label(s, "-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#735c00;");
    }

    private VBox details(String k, String v) {
        return new VBox(3, label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;"),
                label(v, "-fx-font-size:15px;-fx-text-fill:#1e1b15;"));
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
        return "-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-border-color:#e9e2d7;-fx-border-radius:10px;";
    }

    private Image load(String path) {
        var r = getClass().getResource(path);
        return r == null ? null : new Image(r.toExternalForm());
    }
}