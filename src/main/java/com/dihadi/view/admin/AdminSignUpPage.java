package com.dihadi.view.admin;

import com.dihadi.view.NotificationToast;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Native DIHADI admin registration form matching the supplied admin sign-up layout. */
public class AdminSignUpPage {
    private static final String[] ADMIN_VISUALS = {
            "/assets/images/sitesuperviser.jpeg",
            "/assets/images/worker 5.jpeg",
            "/assets/images/welder.jpeg",
            "/assets/images/electrician.jpeg",
            "/assets/images/carpenter.jpeg"
    };

    private Timeline visualRotation;
    private final TextField name = field("e.g. Ram");
    private final TextField officialEmail = field("e.g. rushi.sawant@meridihadi.com");
    private final PasswordField password = passwordField("Create a strong password");
    private final PasswordField confirmation = passwordField("Re-enter password");
    private final TextField personalEmail = field("e.g. ram@email.com");
    private final TextField mobile = field("e.g. +91 9876543210");
    private final TextField adminCode = field("Enter Admin Access Code");

    public Scene getAdminSignUpScene(Runnable backAction) {
        BorderPane page = new BorderPane();
        page.setLeft(createFormPanel(backAction));
        page.setCenter(createVisualPanel());
        page.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
        return new Scene(page, 1400, 780);
    }

    private ScrollPane createFormPanel(Runnable backAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 76, 76);
        Label brand = text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#27438a;");
        Label tagline = text("Meri Dihadi ~ Mera Haq", "-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-style:italic;-fx-text-fill:#685c52;");
        VBox branding = new VBox(5, logo, brand, tagline); branding.setAlignment(Pos.CENTER);

        Label welcome = text("👋  Welcome to DIHADI", "-fx-font-family:Georgia;-fx-font-size:29px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label title = text("Sign In to Create an Admin Account", "-fx-font-size:19px;-fx-text-fill:#4c4637;");
        Label note = text("Remember to enter your DIHADI Admin Code to complete the sign-up process.", "-fx-font-size:15px;-fx-text-fill:#685c52;-fx-text-alignment:center;");
        note.setWrapText(true);
        VBox header = new VBox(11, branding, welcome, title, note); header.setAlignment(Pos.CENTER);

        Button back = new Button("‹");
        back.setOnAction(event -> backAction.run());
        back.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-text-fill:#735c00;-fx-font-size:24px;-fx-font-weight:700;-fx-padding:2px 13px 6px 13px;-fx-cursor:hand;");
        Label formHeadingLbl = text("ENTER YOUR DETAILS", "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#4d4635;-fx-letter-spacing:1px;-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-padding:10px 15px;");
        HBox sectionHeader = new HBox(10, back, formHeadingLbl);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        GridPane fields = new GridPane();
        fields.setHgap(18); fields.setVgap(16);
        add(fields, 0, 0, "Full Name *", name, 1);
        add(fields, 1, 0, "Personal Email Address *", personalEmail, 1);
        add(fields, 0, 1, "Password *", password, 1);
        add(fields, 1, 1, "Confirm Password *", confirmation, 1);
        add(fields, 0, 2, "Mobile Number *", mobile, 2);
        add(fields, 0, 3, "Official Email Address *", officialEmail, 2);
        add(fields, 0, 4, "Enter Your DIHADI Admin Code *", adminCode, 2);

        Button submit = new Button("CREATE ADMIN ACCOUNT"); submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle("-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-size:18px;-fx-font-weight:800;-fx-padding:14px;-fx-cursor:hand;");
        submit.setOnAction(event -> register(submit, backAction));

        Button login = new Button("Already have an admin account? Login");
        login.setOnAction(event -> { Stage stage = (Stage) login.getScene().getWindow(); stage.setScene(new AdminLoginPage().getAdminLoginScene(backAction)); });
        login.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;");
        VBox actions = new VBox(13, submit, login); actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(24, 0, 0, 0));
        VBox card = new VBox(22, header, divider(), sectionHeader, fields, divider(), actions);
        card.setMaxWidth(600); card.setPadding(new Insets(30, 42, 28, 42));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:17px;-fx-border-color:#e2d9ca;-fx-border-radius:17px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.09),20,.15,0,5px);");

        VBox wrap = new VBox(card); wrap.setAlignment(Pos.CENTER); wrap.setPadding(new Insets(36)); wrap.setPrefWidth(700);
        ScrollPane pane = new ScrollPane(wrap); pane.setFitToWidth(true); pane.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        return pane;
    }

    private StackPane createVisualPanel() {
        ImageView visual = image(ADMIN_VISUALS[0], 520, 430);
        visual.setPreserveRatio(true);
        startVisualRotation(visual);
        StackPane photo = new StackPane(visual);
        photo.setPrefSize(540, 455);
        photo.setMaxSize(540, 455);
        photo.setPadding(new Insets(12));
        photo.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;"
                        + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,7px);");

        Label eyebrow = text("DIHADI ADMIN COMMUNITY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#d4af37;-fx-letter-spacing:1.5px;");
        Label headline = text("Lead with trust.\nBuild with clarity.",
                "-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#fff8f0;-fx-line-spacing:4px;");
        Label copy = text(
                "Manage verified workforce, requirements and operations from one dependable DIHADI control center.",
                "-fx-font-size:15px;-fx-text-fill:#f8f0e2;-fx-opacity:.86;");
        copy.setWrapText(true);
        copy.setMaxWidth(500);
        VBox words = new VBox(12, eyebrow, headline, copy);
        words.setAlignment(Pos.CENTER_LEFT);
        VBox content = new VBox(24, photo, words);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(38));
        StackPane panel = new StackPane(content);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(700);
        panel.setMinWidth(520);
        panel.setStyle("-fx-background-color:linear-gradient(to bottom right,#343027,#4c4233);");
        return panel;
    }

    private void startVisualRotation(ImageView visual) {
        if (visualRotation != null) visualRotation.stop();
        final int[] index = { 0 };
        visualRotation = new Timeline(new KeyFrame(Duration.millis(1800), event -> {
            index[0] = (index[0] + 1) % ADMIN_VISUALS.length;
            try {
                java.net.URL url = getClass().getResource(ADMIN_VISUALS[index[0]]);
                if (url != null) visual.setImage(new Image(url.toExternalForm()));
            } catch (Exception e) {}
        }));
        visualRotation.setCycleCount(Timeline.INDEFINITE);
        visualRotation.play();
    }

    private void register(Button submitBtn, Runnable backAction) {
        String nameStr = name.getText().trim();
        String personalEmailStr = personalEmail.getText().trim();
        String officialEmailStr = officialEmail.getText().trim();
        String mobileStr = mobile.getText().trim();
        String adminCodeStr = adminCode.getText().trim();
        String passwordStr = password.getText().trim();
        String confirmStr = confirmation.getText().trim();

        if (nameStr.isBlank() || officialEmailStr.isBlank() || passwordStr.isBlank()
                || personalEmailStr.isBlank() || mobileStr.isBlank() || adminCodeStr.isBlank()) {
            NotificationToast.show(submitBtn, "Incomplete Form ⚠️", "Please fill in every required field.", NotificationToast.ToastType.ALERT);
            info("Complete your details", "Please fill in every required field.");
            return;
        }
        if (!passwordStr.equals(confirmStr)) {
            NotificationToast.show(submitBtn, "Password Mismatch ⚠️", "Re-enter the same password in both fields.", NotificationToast.ToastType.ERROR);
            info("Passwords do not match", "Re-enter the same password in both password fields.");
            return;
        }

        submitBtn.setDisable(true);
        submitBtn.setText("CREATING ACCOUNT...");

        new Thread(() -> {
            boolean success = new com.dihadi.controller.AdminController().registerAdmin(
                    nameStr, personalEmailStr, officialEmailStr, mobileStr, adminCodeStr, passwordStr
            );

            Platform.runLater(() -> {
                submitBtn.setDisable(false);
                submitBtn.setText("CREATE ADMIN ACCOUNT");
                if (success) {
                    NotificationToast.show(submitBtn, "Account Created! 🎉", "Admin registered successfully! Redirecting to login...", NotificationToast.ToastType.SUCCESS);
                    info("Admin account created", "Your DIHADI admin account has been registered successfully. Please login to proceed.");
                    Stage stage = (Stage) submitBtn.getScene().getWindow();
                    stage.setScene(new AdminLoginPage().getAdminLoginScene(backAction));
                } else {
                    NotificationToast.show(submitBtn, "Registration Failed ⚠️", "Unable to create admin account. Please try again.", NotificationToast.ToastType.ERROR);
                    info("Registration failed", "Unable to save admin record to Firebase. Please check your network connection.");
                }
            });
        }).start();
    }

    private static TextField field(String prompt) { TextField field = new TextField(); field.setPromptText(prompt); field.setStyle(inputStyle()); return field; }
    private static PasswordField passwordField(String prompt) { PasswordField field = new PasswordField(); field.setPromptText(prompt); field.setStyle(inputStyle()); return field; }
    private static ColumnConstraints copy(ColumnConstraints source) { ColumnConstraints copy = new ColumnConstraints(); copy.setPercentWidth(source.getPercentWidth()); copy.setHgrow(Priority.ALWAYS); return copy; }
    private void add(GridPane grid, int column, int row, String title, javafx.scene.Node field, int span) { VBox box = new VBox(7, text(title, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;"), field); GridPane.setHgrow(box, Priority.ALWAYS); grid.add(box, column, row, span, 1); }
    private Region divider() { Region line = new Region(); line.setPrefHeight(1); line.setStyle("-fx-background-color:#e9e2d7;"); return line; }
    private Label footer() { return text("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.", "-fx-font-size:12px;-fx-text-fill:#685c52;"); }
    private ImageView image(String path, double width, double height) { ImageView image = new ImageView(new Image(getClass().getResource(path).toExternalForm())); image.setFitWidth(width); image.setFitHeight(height); image.setPreserveRatio(true); image.setSmooth(true); return image; }
    private Label text(String value, String style) { Label label = new Label(value); label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style); return label; }
    private static String inputStyle() { return "-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-border-color:transparent;-fx-font-size:15px;-fx-padding:12px 14px;-fx-pref-height:48px;"; }
    private void info(String title, String message) { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.show(); }
}
