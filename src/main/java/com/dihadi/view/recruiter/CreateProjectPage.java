package com.dihadi.view.recruiter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.dihadi.controller.ImageUploadController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Project creation form shown from the recruiter dashboard. */
public class CreateProjectPage {
    private static final List<String> VISUALS = List.of(
            "/assets/images/recruiter/slide-01.jpeg", "/assets/images/recruiter/slide-02.jpeg",
            "/assets/images/recruiter/slide-03.jpeg", "/assets/images/recruiter/slide-04.jpeg");
    private Timeline slideshow;
    private int visualIndex;
    private TextField projectName, contactName, mobile, email, pincode, addressLine, landmark;
    private final List<String> uploadedImageUrls = new ArrayList<>();
    private final List<File> selectedFiles = new ArrayList<>();
    private HBox previewsContainer;
    private Label uploadStatusLabel;

    public Scene getCreateProjectScene(Runnable closeAction) {
        VBox form = new VBox(28, identity(), welcome(), projectCard(closeAction));
        form.setAlignment(Pos.TOP_CENTER);
        form.setPadding(new Insets(42, 50, 54, 50));
        form.setPrefWidth(700);

        ScrollPane formScroll = new ScrollPane(form);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setStyle("-fx-background:transparent;-fx-background-color:#f2f2f2;-fx-border-width:0;");
        HBox.setHgrow(formScroll, Priority.ALWAYS);

        StackPane visualPanel = visualPanel();
        HBox page = new HBox(formScroll, visualPanel);
        page.setStyle("-fx-background-color:#f2f2f2;");
        Scene scene = new Scene(page, 1400, 780);
        scene.windowProperty().addListener((observable, oldWindow, window) -> {
            if (window instanceof Stage stage) {
                stage.setMinWidth(1100);
                stage.setMinHeight(700);
            }
        });
        return scene;
    }

    private VBox identity() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 86, 86);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        VBox identity = new VBox(4, logo,
                text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:800;-fx-text-fill:#27438a;"),
                text("Mera Haq ~ Meri Dihadi",
                        "-fx-font-family:Georgia;-fx-font-size:17px;-fx-font-style:italic;-fx-text-fill:#4c4637;"));
        identity.setAlignment(Pos.CENTER);
        return identity;
    }

    private VBox welcome() {
        VBox copy = new VBox(7,
                text("Welcome to DIHADI",
                        "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                text("Go ahead. Enter your project details to proceed ahead.",
                        "-fx-font-size:14px;-fx-text-fill:#4d4635;"));
        copy.setAlignment(Pos.CENTER);
        return copy;
    }

    private VBox projectCard(Runnable closeAction) {
        Button back = new Button("<");
        back.setStyle(
                "-fx-background-color:transparent;-fx-padding:0 2px 0 0;-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#1e1b15;-fx-cursor:hand;");
        back.setOnAction(e -> {
            if (closeAction != null)
                closeAction.run();
        });
        Label heading = text("ADD NEW PROJECT",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#1e1b15;");
        HBox title = new HBox(8, back, heading);
        title.setAlignment(Pos.CENTER_LEFT);

        projectName = input("Enter project name");
        contactName = input("Enter contact name");
        mobile = input("Enter mobile number");
        TextField alternateMobile = input("Enter alternate mobile");
        email = input("Enter email");

        VBox details = new VBox(15,
                fieldBox("Project name *required", projectName),
                fieldBox("Project contact person name *required", contactName),
                fieldBox("Mobile number *required", mobile),
                fieldBox("Alternate mobile number", alternateMobile),
                fieldBox("Email address *required", email), uploadArea());

        Label addressHeading = text("ADDRESS DETAILS",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#1e1b15;");
        pincode = input("Enter pincode");
        VBox pinRow = fieldBox("Pincode *required", pincode);
        TextField city = input("Enter city");
        TextField state = input("Enter state");
        HBox locationRow = new HBox(16, fieldBox("City", city), fieldBox("State", state));
        equalWidth(locationRow);

        addressLine = input("Street address");
        TextField addressLine2 = input("Apartment, suite, etc.");
        landmark = input("Near by landmark");

        VBox address = new VBox(15, addressHeading, pinRow, locationRow,
                fieldBox("Address line 1 *required", addressLine),
                fieldBox("Address line 2", addressLine2),
                fieldBox("Landmark *required", landmark));

        Button save = new Button("SAVE PROJECT");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:17px;-fx-font-weight:700;-fx-padding:14px 22px;-fx-cursor:hand;");
        save.setOnAction(e -> {
            if (!validProject())
                return;

            // Ensure any pending selected files get uploaded before saving
            for (File file : selectedFiles) {
                if (uploadedImageUrls.isEmpty() && selectedFiles.size() > 0) {
                    ImageUploadController uploadController = new ImageUploadController();
                    String url = uploadController.imageUpload(file);
                    if (url != null && !uploadedImageUrls.contains(url)) {
                        uploadedImageUrls.add(url);
                    }
                }
            }

            String recruiterMobile = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber() != null && !com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber().isBlank())
                    ? com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber()
                    : mobile.getText().trim();
            String recruiterEmail = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getEmail() != null && !com.dihadi.view.SessionManager.currentRecruiter.getEmail().isBlank())
                    ? com.dihadi.view.SessionManager.currentRecruiter.getEmail()
                    : email.getText().trim();

            boolean hasActive = false;
            try {
                List<com.dihadi.model.Project> existingProjects = new com.dihadi.controller.ProjectController().getAllProjects();
                if (existingProjects != null) {
                    String cleanRecruiterMobile = recruiterMobile.replaceAll("\\D", "");
                    for (com.dihadi.model.Project p : existingProjects) {
                        String pMobile = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
                        boolean mobileMatch = !cleanRecruiterMobile.isBlank() && !pMobile.isBlank() && (pMobile.endsWith(cleanRecruiterMobile) || cleanRecruiterMobile.endsWith(pMobile));
                        boolean emailMatch = recruiterEmail != null && !recruiterEmail.isBlank() && p.getEmail() != null && p.getEmail().equalsIgnoreCase(recruiterEmail);
                        if (mobileMatch || emailMatch) {
                            if ("Active".equalsIgnoreCase(p.getStatus())) {
                                hasActive = true;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {}

            String projectId = String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000));
            com.dihadi.model.Project project = new com.dihadi.model.Project(
                projectId,
                projectName.getText().trim(),
                contactName.getText().trim(),
                recruiterMobile,
                alternateMobile.getText().trim(),
                recruiterEmail,
                pincode.getText().trim(),
                city.getText().trim(),
                state.getText().trim(),
                addressLine.getText().trim(),
                addressLine2.getText().trim(),
                landmark.getText().trim(),
                new ArrayList<>(uploadedImageUrls)
            );
            project.setStatus(hasActive ? "Upcoming" : "Active");
            new com.dihadi.controller.ProjectController().addProject(project);

            Stage stage = (Stage) save.getScene().getWindow();
            stage.setScene(
                    new AddWorkersPage(projectId, projectName.getText().trim(), contactName.getText().trim(), mobile.getText().trim(), email.getText().trim(), addressLine.getText().trim(), uploadedImageUrls.isEmpty() ? "" : uploadedImageUrls.get(0)).getAddWorkersScene(() -> stage.setScene(getCreateProjectScene(closeAction))));
        });
        Button close = new Button("Close");
        close.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:16px;-fx-font-weight:700;-fx-cursor:hand;");
        close.setOnAction(e -> {
            if (closeAction != null)
                closeAction.run();
        });
        VBox actions = new VBox(12, save, close);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(23, 0, 0, 0));
        actions.setStyle("-fx-border-color:#e9e2d7 transparent transparent transparent;-fx-border-width:1px 0 0 0;");

        VBox card = new VBox(24, title, details, divider(), address, actions);
        card.setMaxWidth(560);
        card.setPadding(new Insets(30));
        card.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#e9e2d7;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),20,0,0,5px);");
        return card;
    }

    private boolean validProject() {
        TextField[] required = { projectName, contactName, mobile, email, pincode, addressLine, landmark };
        for (TextField field : required) {
            if (field == null || field.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Required details");
                alert.setHeaderText(null);
                alert.setContentText("Please complete all fields marked *required before saving the project.");
                alert.show();
                return false;
            }
        }
        return true;
    }

    private VBox uploadArea() {
        Label prompt = text("Add 2 to 3 Recent Pictures of Project/Construction Site", labelStyle());
        Label camera = text("[+]", "-fx-font-size:23px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label line = text("Click or drag to upload pictures", "-fx-font-size:14px;-fx-text-fill:#4c4637;");
        Label hint = text("Max 3 images  |  Total capacity: 30MB",
                "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;");
        uploadStatusLabel = text("", "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#735c00;");

        previewsContainer = new HBox(8);
        previewsContainer.setAlignment(Pos.CENTER);

        VBox dropZone = new VBox(4, camera, line, hint, previewsContainer, uploadStatusLabel);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.setPadding(new Insets(10));
        dropZone.setMinHeight(126);
        dropZone.setStyle(
                "-fx-background-color:#f4ece1;-fx-background-radius:8px;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-border-style:segments(5,5);-fx-border-width:2px;-fx-cursor:hand;");

        dropZone.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Project / Construction Site Pictures");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
            List<File> files = fileChooser.showOpenMultipleDialog(dropZone.getScene().getWindow());
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    if (selectedFiles.size() >= 3) {
                        break;
                    }
                    selectedFiles.add(file);

                    ImageView thumbnail = new ImageView(new Image(file.toURI().toString()));
                    thumbnail.setFitWidth(60);
                    thumbnail.setFitHeight(60);
                    thumbnail.setPreserveRatio(false);
                    StackPane thumbPane = new StackPane(thumbnail);
                    thumbPane.setStyle("-fx-border-color:#735c00;-fx-border-radius:6px;-fx-border-width:1px;");
                    previewsContainer.getChildren().add(thumbPane);

                    uploadStatusLabel.setText("Uploading " + selectedFiles.size() + " image(s)...");

                    new Thread(() -> {
                        ImageUploadController uploadController = new ImageUploadController();
                        String url = uploadController.imageUpload(file);
                        if (url != null) {
                            synchronized (uploadedImageUrls) {
                                uploadedImageUrls.add(url);
                            }
                            Platform.runLater(() -> {
                                uploadStatusLabel.setText(uploadedImageUrls.size() + " image(s) uploaded successfully.");
                            });
                        }
                    }).start();
                }
            }
        });

        return new VBox(8, prompt, dropZone);
    }

    private VBox field(String label, String prompt) {
        return fieldBox(label, input(prompt));
    }

    private VBox selectField(String label, String prompt) {
        ComboBox<String> box = new ComboBox<>();
        box.setPromptText(prompt);
        box.setMaxWidth(Double.MAX_VALUE);
        box.getItems().addAll("Select post office");
        box.setStyle(inputStyle());
        return fieldBox(label, box);
    }

    private VBox fieldBox(String label, javafx.scene.Node input) {
        VBox box = new VBox(7, text(label, labelStyle()), input);
        VBox.setVgrow(input, Priority.NEVER);
        return box;
    }

    private TextField input(String prompt) {
        TextField input = new TextField();
        input.setPromptText(prompt);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setStyle(inputStyle());
        return input;
    }

    private TextField disabledField(String prompt) {
        TextField input = input(prompt);
        input.setDisable(true);
        input.setStyle(inputStyle() + "-fx-opacity:.68;");
        return input;
    }

    private void equalWidth(HBox row) {
        for (javafx.scene.Node node : row.getChildren())
            HBox.setHgrow(node, Priority.ALWAYS);
    }

    private Region divider() {
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#d0c5af;");
        return divider;
    }

    private StackPane visualPanel() {
        ImageView photo = image(VISUALS.get(0), 650, 730);
        photo.setPreserveRatio(false);
        photo.setFitWidth(650);
        photo.setFitHeight(730);
        StackPane frame = new StackPane(photo);
        frame.setPrefSize(650, 730);
        frame.setMaxSize(650, 730);
        Rectangle clip = new Rectangle(650, 730);
        clip.setArcWidth(58);
        clip.setArcHeight(58);
        frame.setClip(clip);
        frame.setStyle(
                "-fx-background-color:#e0d9ce;-fx-background-radius:30px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,5px);");

        StackPane panel = new StackPane(frame);
        panel.setPrefWidth(700);
        panel.setMinWidth(520);
        panel.setPadding(new Insets(24, 28, 24, 0));
        panel.setStyle("-fx-background-color:#fff8f0;");
        startSlideshow(photo);
        return panel;
    }

    private void startSlideshow(ImageView image) {
        if (slideshow != null)
            slideshow.stop();
        slideshow = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
            visualIndex = (visualIndex + 1) % VISUALS.size();
            image.setImage(load(VISUALS.get(visualIndex)));

        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private ImageView image(String path, double width, double height) {
        ImageView image = new ImageView(load(path));
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setSmooth(true);
        return image;
    }

    private Image load(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private Label text(String value, String style) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private String labelStyle() {
        return "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1e1b15;";
    }

    private String inputStyle() {
        return "-fx-background-color:#f4ece1;-fx-background-radius:8px;-fx-border-color:transparent;-fx-border-radius:8px;-fx-padding:11px 12px;-fx-font-size:14px;-fx-text-fill:#1e1b15;";
    }
}