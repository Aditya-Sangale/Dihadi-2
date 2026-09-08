package com.dihadi.view.admin;

import com.dihadi.controller.AdminController;
import com.dihadi.model.Admin;
import com.dihadi.view.AppNavigator;
import com.dihadi.view.SessionManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * DIHADI Admin Login Page styled with full-bleed background imagery
 * and frosted glass form card positioned on the right side to match
 * the recruiter and worker authentication screens.
 */
public class AdminLoginPage {

    public Scene getAdminLoginScene(Runnable backAction) {
        Region bg = new Region();
        String bgUrl = resolveBgUrl();
        bg.setStyle("-fx-background-image: url('" + bgUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;");

        VBox formWrapper = createLoginForm(backAction);
        StackPane root = new StackPane(bg, formWrapper);
        return new Scene(root, 1400, 780);
    }

    private VBox createLoginForm(Runnable backAction) {
        // Back navigation button and badge
        Button back = new Button("← Back");
        back.setStyle(
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        back.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                Stage stage = (Stage) back.getScene().getWindow();
                AppNavigator.open(stage, "Home");
            }
        });

        Label adminBadge = label("ADMIN PORTAL",
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-padding:6px 14px;");
        HBox topBar = new HBox(12, back, adminBadge);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Branding
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 72);
        VBox brand = new VBox(4, logo,
                label("DIHADI",
                        "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#735c00;"),
                label("Meri Dihadi ~ Mera Haq",
                        "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        brand.setAlignment(Pos.CENTER);

        // Intro
        Label welcome = label("Admin Login", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label intro = label("Enter your official DIHADI email address and password to access the control center.",
                "-fx-font-size:14px;-fx-text-fill:#594f42;");
        intro.setWrapText(true);
        intro.setMaxWidth(400);
        intro.setAlignment(Pos.CENTER);
        VBox introBox = new VBox(6, welcome, intro);
        introBox.setAlignment(Pos.CENTER);

        // Input fields
        TextField email = new TextField();
        email.setPromptText("Enter your Official Email Address");
        email.setStyle(transparentInputStyle());

        PasswordField password = new PasswordField();
        password.setPromptText("Enter your Password");
        password.setStyle(transparentInputStyle());

        VBox credentials = new VBox(12,
                label("Official Email Address", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2c251d;"),
                email,
                label("Password", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2c251d;"),
                password);

        Button login = new Button("Login to Admin Portal");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-size:17px;-fx-font-weight:800;-fx-padding:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.18),10,0,0,3px);");
        login.setOnAction(event -> authenticate(email, password, login, backAction));

        Button signup = new Button("New admin? Create an account");
        signup.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-cursor:hand;");
        signup.setOnAction(event -> {
            Stage stage = (Stage) signup.getScene().getWindow();
            stage.setScene(new AdminSignUpPage().getAdminSignUpScene(backAction));
        });

        VBox card = new VBox(18, topBar, brand, introBox, credentials, login, signup);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(480);
        card.setPadding(new Insets(32, 38, 32, 38));
        card.setStyle(
                "-fx-background-color:rgba(255,253,248,0.88);" +
                "-fx-background-radius:22px;" +
                "-fx-border-color:rgba(212,175,55,0.45);" +
                "-fx-border-radius:22px;" +
                "-fx-border-width:1.5px;" +
                "-fx-effect:dropshadow(gaussian,rgba(30,24,16,0.22),30,0,0,10px);");

        // Keep form aligned on the RIGHT side
        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER_RIGHT);
        wrapper.setPadding(new Insets(30, 80, 30, 20));
        return wrapper;
    }

    private void authenticate(TextField email, PasswordField password, Button login, Runnable backAction) {
        if (!SessionManager.checkAccessAllowed(SessionManager.Role.ADMIN)) {
            return;
        }

        String emailText = email.getText().trim();
        String passText = password.getText().trim();

        if (emailText.isBlank() || passText.isBlank()) {
            notice("Invalid Input", "Please enter your official email address and password.");
            return;
        }

        login.setDisable(true);
        login.setText("VERIFYING");

        new Thread(() -> {
            try {
                Admin admin = new AdminController().authenticate(emailText, passText);

                Platform.runLater(() -> {
                    if (admin != null) {
                        SessionManager.currentAdmin = admin;
                        Stage stage = (Stage) login.getScene().getWindow();
                        com.dihadi.view.NotificationToast.show(stage, "Login Successful", "Welcome back, " + admin.getFullName() + "!", com.dihadi.view.NotificationToast.ToastType.SUCCESS);
                        stage.setScene(new AdminDashboard().getDashboardScene(
                                () -> {
                                    SessionManager.clearAllSessions();
                                    stage.setScene(new AdminHomePage().getAdminHomeScene(backAction != null ? backAction : () -> AppNavigator.open(stage, "Home")));
                                }));
                    } else {
                        login.setDisable(false);
                        login.setText("Login to Admin Portal");
                        notice("Invalid Credentials", "Invalid email address or password. Please check your credentials and try again.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    login.setDisable(false);
                    login.setText("Login to Admin Portal");
                    notice("Network Error", "Unable to verify credentials. Please check your internet connection.");
                });
            }
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

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView();
        try {
            var res = getClass().getResource(path);
            if (res != null) {
                view.setImage(new Image(res.toExternalForm()));
            } else {
                java.io.File f = new java.io.File("src/main/resources" + (path.startsWith("/") ? "" : "/") + path);
                if (f.exists()) {
                    view.setImage(new Image(f.toURI().toString()));
                }
            }
        } catch (Exception ignored) {}
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private String transparentInputStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.85);" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: rgba(200, 185, 165, 0.6);" +
                "-fx-border-radius: 10px;" +
                "-fx-border-width: 1.2px;" +
                "-fx-text-fill: #1e1b15;" +
                "-fx-font-weight: 600;" +
                "-fx-prompt-text-fill: #7d7263;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 12px 14px;" +
                "-fx-pref-height: 48px;";
    }

    private void notice(String title, String message) {
        com.dihadi.view.NotificationToast.show(title, message, com.dihadi.view.NotificationToast.ToastType.ALERT);
    }
}
