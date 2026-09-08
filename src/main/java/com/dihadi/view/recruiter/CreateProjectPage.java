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
        com.dihadi.view.ScrollUtils.style(formScroll);
        formScroll.setStyle("-fx-background:transparent;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        HBox.setHgrow(formScroll, Priority.ALWAYS);

        StackPane visualPanel = visualPanel();
        HBox page = new HBox(formScroll, visualPanel);
        page.setStyle("-fx-background-color:#f3e7ce;");
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
        logo.setPreserveRatio(true);
        VBox identity = new VBox(4, logo,
                text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:800;-fx-text-fill:#27438a;"),
                text("Meri Dihadi ~ Mera Haq",
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
        Button back = new Button("← Back to Dashboard");
        String backIdle = "-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;";
        String backHover = "-fx-background-color:#ffffff;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#735c00;-fx-border-radius:10px;-fx-background-radius:10px;";
        back.setStyle(backIdle);
        back.setOnMouseEntered(e -> back.setStyle(backHover));
        back.setOnMouseExited(e -> back.setStyle(backIdle));
        back.setOnAction(e -> {
            if (closeAction != null)
                closeAction.run();
        });
        Label heading = text("ADD NEW PROJECT",
                "-fx-background-color:rgba(212,175,55,.16);-fx-background-radius:12px;-fx-border-color:rgba(212,175,55,.58);-fx-border-radius:12px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.2px;-fx-padding:10px 16px;");
        HBox title = new HBox(8, back, heading);
        title.setAlignment(Pos.CENTER_LEFT);

        projectName = input("Enter project name");
        contactName = input("Enter contact name");
        mobile = input("Enter mobile number");
        TextField alternateMobile = input("Enter alternate mobile");
        email = input("Enter email");

        VBox details = new VBox(15,
                fieldBox("Project name *", projectName),
                fieldBox("Project contact person name *", contactName),
                fieldBox("Mobile number *", mobile),
                fieldBox("Alternate mobile number", alternateMobile),
                fieldBox("Email address *", email), uploadArea());

        Label addressHeading = text("ADDRESS DETAILS",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#1e1b15;");
        pincode = input("Enter pincode");
        VBox pinRow = fieldBox("Pincode *", pincode);
        TextField city = input("Enter city");
        TextField state = input("Enter state");
        HBox locationRow = new HBox(16, fieldBox("City", city), fieldBox("State", state));
        equalWidth(locationRow);

        addressLine = input("Street address");
        TextField addressLine2 = input("Apartment, suite, etc.");
        landmark = input("Near by landmark");

        VBox address = new VBox(15, addressHeading, pinRow, locationRow,
                fieldBox("Address line 1 *", addressLine),
                fieldBox("Address line 2", addressLine2),
                fieldBox("Landmark *", landmark));

        Button save = new Button("SAVE PROJECT");
        save.setMaxWidth(Double.MAX_VALUE);
        save.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#ffffff;-fx-font-size:17px;-fx-font-weight:700;-fx-padding:14px 22px;-fx-cursor:hand;");
        save.setOnAction(e -> {
            if (!validProject())
                return;

            save.setDisable(true);
            save.setText("Opening Workforce Requirements...");

            // Collect image URLs: use already uploaded Cloudinary URLs or local file URIs as immediate fallback
            List<String> projectImages = new ArrayList<>();
            synchronized (uploadedImageUrls) {
                projectImages.addAll(uploadedImageUrls);
            }
            for (File file : selectedFiles) {
                if (file != null && file.exists()) {
                    String localUri = file.toURI().toString();
                    if (!projectImages.contains(localUri) && projectImages.size() < selectedFiles.size()) {
                        projectImages.add(localUri);
                    }
                }
            }

            String recruiterMobile = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber() != null && !com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber().isBlank())
                    ? com.dihadi.view.SessionManager.currentRecruiter.getMobileNumber()
                    : mobile.getText().trim();
            String recruiterEmail = (com.dihadi.view.SessionManager.currentRecruiter != null && com.dihadi.view.SessionManager.currentRecruiter.getEmail() != null && !com.dihadi.view.SessionManager.currentRecruiter.getEmail().isBlank())
                    ? com.dihadi.view.SessionManager.currentRecruiter.getEmail()
                    : email.getText().trim();

            String projectId = String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000));
            String pName = projectName.getText().trim();
            String cName = contactName.getText().trim();
            String altMobile = alternateMobile.getText().trim();
            String pPin = pincode.getText().trim();
            String pCity = city.getText().trim();
            String pState = state.getText().trim();
            String addr1 = addressLine.getText().trim();
            String addr2 = addressLine2.getText().trim();
            String landm = landmark.getText().trim();

            com.dihadi.model.Project project = new com.dihadi.model.Project(
                projectId,
                pName,
                cName,
                recruiterMobile,
                altMobile,
                recruiterEmail,
                pPin,
                pCity,
                pState,
                addr1,
                addr2,
                landm,
                new ArrayList<>(projectImages)
            );
            // The newly created project is immediately the active project for recruitment
            project.setStatus("Active");

            // Cache immediately in memory so lookups succeed in 0ms
            com.dihadi.dao.ProjectDao.cacheProject(project);
            com.dihadi.view.SessionManager.currentRecruiterProject = project;

            // Save to Firestore in background without blocking the UI thread
            final List<File> filesToUpload = new ArrayList<>(selectedFiles);
            new Thread(() -> {
                try {
                    new com.dihadi.controller.ProjectController().addProject(project);

                    boolean hasNewCloudinary = false;
                    for (File file : filesToUpload) {
                        boolean hasRemote = false;
                        synchronized (uploadedImageUrls) {
                            for (String u : uploadedImageUrls) {
                                if (u != null && u.startsWith("http")) {
                                    hasRemote = true;
                                    break;
                                }
                            }
                        }
                        if (!hasRemote && file != null && file.exists()) {
                            try {
                                ImageUploadController uploadController = new ImageUploadController();
                                String url = uploadController.imageUpload(file);
                                if (url != null && !url.isBlank()) {
                                    synchronized (uploadedImageUrls) {
                                        uploadedImageUrls.add(url);
                                    }
                                    hasNewCloudinary = true;
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                    if (hasNewCloudinary) {
                        synchronized (uploadedImageUrls) {
                            project.setImageUrls(new ArrayList<>(uploadedImageUrls));
                        }
                        new com.dihadi.controller.ProjectController().addProject(project);
                        com.dihadi.dao.ProjectDao.cacheProject(project);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Navigate immediately to AddWorkersPage (instant transition!)
            String firstImage = !projectImages.isEmpty() ? projectImages.get(0) : "";
            Stage stage = (Stage) save.getScene().getWindow();
            Runnable returnToDashboard = () -> {
                if (closeAction != null) {
                    closeAction.run();
                } else {
                    com.dihadi.model.Recruiter r = com.dihadi.view.SessionManager.currentRecruiter;
                    stage.setScene(new RecruiterDashboard(r)
                            .getScene(() -> com.dihadi.view.AppNavigator.open(stage, "Home")));
                }
            };
            stage.setScene(
                    new AddWorkersPage(projectId, pName, cName, recruiterMobile, recruiterEmail, addr1, firstImage)
                            .getAddWorkersScene(returnToDashboard));
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
                alert.setContentText("Please complete all fields marked * before saving the project.");
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
                    thumbnail.setFitWidth(62);
                    thumbnail.setFitHeight(62);
                    thumbnail.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(62, 62);
                    clip.setArcWidth(10);
                    clip.setArcHeight(10);
                    thumbnail.setClip(clip);

                    final boolean[] isRemoved = new boolean[] { false };
                    final String[] uploadedUrlHolder = new String[1];

                    Button removeBtn = new Button("✕");
                    removeBtn.setStyle("-fx-background-color:rgba(30,27,21,0.85);-fx-text-fill:#ffffff;-fx-font-size:9px;-fx-font-weight:900;-fx-background-radius:999px;-fx-min-width:18px;-fx-min-height:18px;-fx-max-width:18px;-fx-max-height:18px;-fx-padding:0;-fx-cursor:hand;");
                    removeBtn.setOnMouseEntered(ev -> removeBtn.setStyle("-fx-background-color:#ba1a1a;-fx-text-fill:#ffffff;-fx-font-size:9px;-fx-font-weight:900;-fx-background-radius:999px;-fx-min-width:18px;-fx-min-height:18px;-fx-max-width:18px;-fx-max-height:18px;-fx-padding:0;-fx-cursor:hand;"));
                    removeBtn.setOnMouseExited(ev -> removeBtn.setStyle("-fx-background-color:rgba(30,27,21,0.85);-fx-text-fill:#ffffff;-fx-font-size:9px;-fx-font-weight:900;-fx-background-radius:999px;-fx-min-width:18px;-fx-min-height:18px;-fx-max-width:18px;-fx-max-height:18px;-fx-padding:0;-fx-cursor:hand;"));
                    StackPane.setAlignment(removeBtn, Pos.TOP_RIGHT);
                    StackPane.setMargin(removeBtn, new Insets(2, 2, 0, 0));

                    StackPane thumbPane = new StackPane(thumbnail, removeBtn);
                    thumbPane.setPrefSize(66, 66);
                    thumbPane.setStyle("-fx-border-color:#735c00;-fx-border-radius:8px;-fx-border-width:1.5px;-fx-background-radius:8px;");
                    thumbPane.setOnMouseClicked(ev -> ev.consume());

                    removeBtn.setOnMouseClicked(ev -> ev.consume());
                    removeBtn.setOnAction(ev -> {
                        ev.consume();
                        isRemoved[0] = true;
                        previewsContainer.getChildren().remove(thumbPane);
                        selectedFiles.remove(file);
                        if (uploadedUrlHolder[0] != null) {
                            synchronized (uploadedImageUrls) {
                                uploadedImageUrls.remove(uploadedUrlHolder[0]);
                            }
                        }
                        int count = uploadedImageUrls.size();
                        if (count == 0) {
                            uploadStatusLabel.setText("");
                        } else {
                            uploadStatusLabel.setText(count + " image(s) uploaded successfully.");
                        }
                    });

                    previewsContainer.getChildren().add(thumbPane);

                    uploadStatusLabel.setText("Uploading " + selectedFiles.size() + " image(s)...");

                    new Thread(() -> {
                        ImageUploadController uploadController = new ImageUploadController();
                        String url = uploadController.imageUpload(file);
                        if (url != null) {
                            uploadedUrlHolder[0] = url;
                            synchronized (uploadedImageUrls) {
                                if (!isRemoved[0]) {
                                    uploadedImageUrls.add(url);
                                }
                            }
                            Platform.runLater(() -> {
                                if (!isRemoved[0]) {
                                    uploadStatusLabel.setText(uploadedImageUrls.size() + " image(s) uploaded successfully.");
                                }
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
        label.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        return label;
    }

    private String labelStyle() {
        return "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1e1b15;";
    }

    private String inputStyle() {
        return "-fx-background-color:#f4ece1;-fx-background-radius:8px;-fx-border-color:transparent;-fx-border-radius:8px;-fx-padding:11px 12px;-fx-font-size:14px;-fx-text-fill:#1e1b15;";
    }
}
