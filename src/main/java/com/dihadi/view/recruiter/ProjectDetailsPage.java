package com.dihadi.view.recruiter;

import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/** Final project summary shown after workforce requirements are added. */
public class ProjectDetailsPage {
    private final String name, contact, mobile, email, address, priority, type, skill, workers, wage, imagePath, facilities;

    public ProjectDetailsPage(String name, String contact, String mobile, String email, String address, String priority,
            String type, String skill, String workers, String wage, String imagePath, String facilities) {
        this.name = name;
        this.contact = contact;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.priority = priority;
        this.type = type;
        this.skill = skill;
        this.workers = workers;
        this.wage = wage;
        this.imagePath = imagePath;
        this.facilities = facilities;
    }

    public Scene getScene(Runnable exit) {
        HBox imageContainer = new HBox(12);
        imageContainer.setAlignment(Pos.CENTER_LEFT);

        List<String> imgUrls = new ArrayList<>();
        if (imagePath != null && !imagePath.isBlank()) {
            imgUrls.add(imagePath);
        }

        try {
            List<com.dihadi.model.Project> allProj = new com.dihadi.controller.ProjectController().getAllProjects();
            if (allProj != null) {
                for (com.dihadi.model.Project p : allProj) {
                    if (mobile != null && p.getMobile() != null && p.getMobile().replaceAll("\\D", "").equals(mobile.replaceAll("\\D", ""))) {
                        if (p.getImageUrls() != null && !p.getImageUrls().isEmpty()) {
                            for (String url : p.getImageUrls()) {
                                if (url != null && !url.isBlank() && !imgUrls.contains(url)) {
                                    imgUrls.add(url);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imgUrls.isEmpty()) {
            imgUrls.add("/assets/images/recruiter/slide-03.jpeg");
        }

        for (String url : imgUrls) {
            Image img = load(url);
            if (img != null) {
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(360);
                imgView.setFitHeight(230);
                imgView.setPreserveRatio(false);
                Rectangle clip = new Rectangle(360, 230);
                clip.setArcWidth(18);
                clip.setArcHeight(18);
                imgView.setClip(clip);
                imageContainer.getChildren().add(imgView);
            }
        }

        ScrollPane mediaScroll = new ScrollPane(imageContainer);
        mediaScroll.setFitToHeight(true);
        mediaScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mediaScroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");

        VBox media = new VBox(12,
                heading("Uploaded Site Images"),
                mediaScroll);
        media.setPadding(new Insets(20));
        media.setStyle(box());
        media.setPrefWidth(420);

        VBox overview = new VBox(10,
                heading("Project & Contact Overview"),
                detail("Project Name", name),
                detail("Contact Person", contact),
                detail("Contact Number", mobile),
                detail("Email", email));
        overview.setPadding(new Insets(20));
        overview.setStyle(box());
        overview.setPrefWidth(450);

        VBox addressBox = new VBox(10,
                heading("Site Address & Location"),
                detail("Address", address));
        addressBox.setPadding(new Insets(20));
        addressBox.setStyle(box());

        Label urgency = label("Priority: " + (priority != null ? priority : "Standard"),
                "-fx-background-color:#ffdad6;-fx-background-radius:999px;-fx-padding:7px 14px;-fx-text-fill:#93000a;-fx-font-weight:700;");
        
        VBox workforce = new VBox(14,
                heading("Workforce & Wage Requirement"),
                new HBox(20,
                        detail("Worker Type", type),
                        detail("Sub Skill", skill),
                        detail("Workers Needed", workers),
                        detail("Daily Wage", "Rs. " + wage + " / day")),
                urgency,
                detail("Facilities Provided", facilities));
        workforce.setPadding(new Insets(20));
        workforce.setStyle(box());

        Button exitButton = new Button("EXIT TO DASHBOARD");
        exitButton.setStyle("-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-weight:700;-fx-font-size:15px;-fx-padding:12px 32px;-fx-cursor:hand;");
        exitButton.setOnAction(e -> {
            if (exit != null) exit.run();
        });
        
        HBox footer = new HBox(exitButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(18, 0, 0, 0));

        VBox content = new VBox(22,
                new VBox(4, heading("Project Details Summary"), label("Your project and workforce requirement have been saved successfully.", "-fx-text-fill:#4c4637;-fx-font-size:15px;")),
                new HBox(20, overview, media),
                addressBox,
                workforce,
                footer);
        content.setPadding(new Insets(28));
        content.setMaxWidth(1050);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");

        StackPane card = new StackPane(scroll);
        card.setMaxSize(1080, 680);
        card.setStyle("-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),26,0,0,8px);");

        StackPane root = new StackPane(card);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:#f3e7ce;");

        Scene scene = new Scene(root, 1400, 780);
        scene.windowProperty().addListener((o, a, w) -> {
            if (w instanceof Stage s) {
                s.setMinWidth(980);
                s.setMinHeight(680);
            }
        });
        return scene;
    }

    private Label heading(String s) {
        return label(s, "-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#685c52;");
    }

    private VBox detail(String k, String v) {
        return new VBox(3,
                label(k, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;"),
                label(v == null || v.isBlank() ? "Not provided" : v, "-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"));
    }

    private Label label(String s, String st) {
        Label l = new Label(s);
        l.setWrapText(true);
        l.setStyle("-fx-font-family:'Segoe UI';" + st);
        return l;
    }

    private String box() {
        return "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#e9e2d7;-fx-border-radius:12px;";
    }

    private Image load(String path) {
        try {
            if (path == null || path.isBlank()) {
                var r = getClass().getResource("/assets/images/recruiter/slide-03.jpeg");
                return r == null ? null : new Image(r.toExternalForm());
            }
            if (path.startsWith("http://") || path.startsWith("https://")) {
                return new Image(path, true);
            }
            if (new java.io.File(path).exists()) {
                return new Image(new java.io.File(path).toURI().toString());
            }
            var r = getClass().getResource(path);
            return r == null ? null : new Image(r.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}