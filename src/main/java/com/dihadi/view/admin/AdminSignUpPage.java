package com.dihadi.view.admin;

import com.dihadi.view.AppNavigator;
import com.dihadi.view.NotificationToast;
import com.dihadi.view.ScrollUtils;
import com.dihadi.view.SessionManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Native DIHADI admin registration form styled with full-bleed background imagery
 * and frosted glass form card positioned on the right side.
 */
public class AdminSignUpPage {

    private final TextField name = field("e.g. Ram");
    private final TextField personalEmail = field("e.g. ram@email.com");
    private final PasswordField password = passwordField("Create a strong password");
    private final PasswordField confirmation = passwordField("Re-enter password");
    private final TextField mobile = field("e.g. +91 9876543210");
    private final TextField officialEmail = field("e.g. rushi.sawant@meridihadi.com");
    private final TextField adminCode = field("Enter Admin Access Code");

    public Scene getAdminSignUpScene(Runnable backAction) {
        Region bg = new Region();
        String bgUrl = resolveBgUrl();
        bg.setStyle("-fx-background-image: url('" + bgUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;");

        ScrollPane scroll = createForm(backAction);
        StackPane root = new StackPane(bg, scroll);
        return new Scene(root, 1400, 780);
    }

    private ScrollPane createForm(Runnable backAction) {
        Button back = new Button("← Back");
        back.setStyle(
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        back.setOnAction(event -> {
            if (backAction != null) {
                backAction.run();
            } else {
                Stage stage = (Stage) back.getScene().getWindow();
                AppNavigator.open(stage, "Home");
            }
        });

        Label badge = text("ADMIN REGISTRATION",
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-padding:6px 14px;");
        HBox topBar = new HBox(12, back, badge);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 68, 68);
        VBox branding = new VBox(3, logo,
                text("DIHADI", "-fx-font-family:'Georgia';-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#735c00;"),
                text("Meri Dihadi ~ Mera Haq", "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        branding.setAlignment(Pos.CENTER);

        Label welcome = text("Create Admin Account", "-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label intro = text("Enter your official credentials and DIHADI Admin Code to register.", "-fx-font-size:13px;-fx-text-fill:#594f42;");
        intro.setWrapText(true);
        intro.setMaxWidth(460);
        intro.setAlignment(Pos.CENTER);
        VBox header = new VBox(6, branding, welcome, intro);
        header.setAlignment(Pos.CENTER);

        GridPane fields = new GridPane();
        fields.setHgap(16);
        fields.setVgap(14);
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        left.setHgrow(Priority.ALWAYS);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        right.setHgrow(Priority.ALWAYS);
        fields.getColumnConstraints().addAll(left, right);

        add(fields, 0, 0, "Full Name *", name, 1);
        add(fields, 1, 0, "Personal Email Address *", personalEmail, 1);
        add(fields, 0, 1, "Password *", password, 1);
        add(fields, 1, 1, "Confirm Password *", confirmation, 1);
        add(fields, 0, 2, "Mobile Number *", mobile, 2);
        add(fields, 0, 3, "Official Email Address *", officialEmail, 2);
        add(fields, 0, 4, "Enter Your DIHADI Admin Code *", adminCode, 2);

        Button submit = new Button("CREATE ADMIN ACCOUNT");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.18),10,0,0,3px);");
        submit.setOnAction(event -> register(submit, backAction));

        Button loginLink = new Button("Already have an admin account? Login");
        loginLink.setOnAction(event -> {
            if (!SessionManager.checkAccessAllowed(SessionManager.Role.ADMIN)) return;
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new AdminLoginPage().getAdminLoginScene(backAction));
        });
        loginLink.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:800;-fx-cursor:hand;");

        VBox actions = new VBox(14, submit, loginLink);
        actions.setAlignment(Pos.CENTER);

        VBox card = new VBox(18, topBar, header, fields, actions);
        card.setMaxWidth(580);
        card.setPadding(new Insets(28, 36, 28, 36));
        card.setStyle(
                "-fx-background-color:rgba(255,253,248,0.88);" +
                "-fx-background-radius:22px;" +
                "-fx-border-color:rgba(212,175,55,0.45);" +
                "-fx-border-radius:22px;" +
                "-fx-border-width:1.5px;" +
                "-fx-effect:dropshadow(gaussian,rgba(30,24,16,0.22),30,0,0,10px);");

        // Align card to the RIGHT side
        VBox content = new VBox(card);
        content.setAlignment(Pos.TOP_RIGHT);
        content.setPadding(new Insets(24, 70, 48, 20));

        ScrollPane pane = new ScrollPane(content);
        ScrollUtils.style(pane);
        pane.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        pane.setFitToWidth(true);
        return pane;
    }

    private void register(Button submitBtn, Runnable backAction) {
        if (!SessionManager.checkAccessAllowed(SessionManager.Role.ADMIN)) {
            return;
        }
        String nameStr = name.getText().trim();
        String personalEmailStr = personalEmail.getText().trim();
        String officialEmailStr = officialEmail.getText().trim();
        String mobileStr = mobile.getText().trim();
        String adminCodeStr = adminCode.getText().trim();
        String passwordStr = password.getText().trim();
        String confirmStr = confirmation.getText().trim();

        if (nameStr.isBlank() || officialEmailStr.isBlank() || passwordStr.isBlank()
                || personalEmailStr.isBlank() || mobileStr.isBlank() || adminCodeStr.isBlank()) {
            info("Complete your details", "Please fill in every required field.");
            return;
        }
        if (!passwordStr.equals(confirmStr)) {
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
                    SessionManager.clearAllSessions();
                    Stage stage = (Stage) submitBtn.getScene().getWindow();
                    NotificationToast.show(stage, "Admin Account Created",
                            "Your DIHADI admin account has been registered successfully. Please login to proceed.",
                            NotificationToast.ToastType.SUCCESS);
                    stage.setScene(new AdminLoginPage().getAdminLoginScene(backAction));
                } else {
                    info("Registration failed", "Unable to save admin record to Firebase. Please check your network connection.");
                }
            });
        }).start();
    }

    private String resolveBgUrl() {
        try {
            var res = getClass().getResource("/assets/images/admin_auth_bg.jpg");
            if (res != null) return res.toExternalForm();
            java.io.File f1 = new java.io.File("src/main/resources/assets/images/admin_auth_bg.jpg");
            if (f1.exists()) return f1.toURI().toString();
            java.io.File f2 = new java.io.File("target/classes/assets/images/admin_auth_bg.jpg");
            if (f2.exists()) return f2.toURI().toString();
        } catch (Exception ignored) {}
        return "";
    }

    private static TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private static PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private void add(GridPane grid, int column, int row, String title, javafx.scene.Node field, int span) {
        VBox box = new VBox(6, text(title, "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#2c251d;"), field);
        GridPane.setHgrow(box, Priority.ALWAYS);
        grid.add(box, column, row, span, 1);
    }

    private ImageView image(String path, double width, double height) {
        ImageView image = new ImageView();
        try {
            var res = getClass().getResource(path);
            if (res != null) {
                image.setImage(new Image(res.toExternalForm()));
            } else {
                java.io.File f = new java.io.File("src/main/resources" + (path.startsWith("/") ? "" : "/") + path);
                if (f.exists()) {
                    image.setImage(new Image(f.toURI().toString()));
                }
            }
        } catch (Exception ignored) {}
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        return image;
    }

    private static Label text(String value, String style) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private static String inputStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.85);" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: rgba(200, 185, 165, 0.6);" +
                "-fx-border-radius: 10px;" +
                "-fx-border-width: 1.2px;" +
                "-fx-text-fill: #1e1b15;" +
                "-fx-font-weight: 600;" +
                "-fx-prompt-text-fill: #7d7263;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10px 13px;" +
                "-fx-pref-height: 44px;";
    }

    private void info(String title, String message) {
        NotificationToast.show(title, message, NotificationToast.ToastType.INFO);
    }
}
