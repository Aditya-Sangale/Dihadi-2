package com.dihadi.view.admin;

import com.dihadi.controller.AdminController;
import com.dihadi.model.Admin;
import com.dihadi.view.AppNavigator;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** DIHADI Admin Login with Firebase authentication and real-time validation popups. */
public class AdminLoginPage {
    private static final String[] ADMIN_VISUALS = {
            "/assets/images/sitesuperviser.jpeg",
            "/assets/images/worker 5.jpeg",
            "/assets/images/welder.jpeg",
            "/assets/images/electrician.jpeg",
            "/assets/images/carpenter.jpeg"
    };

    private Timeline slideshow;

    public Scene getAdminLoginScene(Runnable backAction) {
        BorderPane page = new BorderPane();
        page.setLeft(form(backAction));
        page.setCenter(visual());
        page.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
        return new Scene(page, 1400, 780);
    }

    private VBox form(Runnable backAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 76, 76);
        VBox brand = new VBox(5, logo,
                label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#27438a;"),
                label("Meri Dihadi ~ Mera Haq", "-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        brand.setAlignment(Pos.CENTER);

        Label welcome = label("Welcome to DIHADI", "-fx-font-family:Georgia;-fx-font-size:29px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label intro = label("Please enter your DIHADI official email address\nand password to proceed ahead.",
                "-fx-font-size:16px;-fx-text-fill:#4c4637;-fx-text-alignment:center;");
        VBox introduction = new VBox(9, welcome, intro); introduction.setAlignment(Pos.CENTER);

        Button back = new Button("‹");
        back.setOnAction(event -> backAction.run());
        back.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-text-fill:#735c00;-fx-font-size:22px;-fx-font-weight:700;-fx-padding:2px 13px 5px 13px;-fx-cursor:hand;");
        Label credentialsTitle = label("Enter your Login Credentials & Password", "-fx-font-size:18px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        HBox credentialsHeader = new HBox(10, back, credentialsTitle);
        credentialsHeader.setAlignment(Pos.CENTER_LEFT);

        TextField email = new TextField(); email.setPromptText("Enter your Official Email Address"); email.setStyle(inputStyle());
        PasswordField password = new PasswordField(); password.setPromptText("Enter your Password"); password.setStyle(inputStyle());
        VBox credentials = new VBox(15, credentialsHeader, email, password);

        Button login = new Button("LOGIN"); login.setMaxWidth(Double.MAX_VALUE);
        login.setStyle("-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-size:18px;-fx-font-weight:800;-fx-padding:14px;-fx-cursor:hand;");
        login.setOnAction(event -> authenticate(email, password, login, backAction));
        Button signup = link("New admin? Create an account");
        signup.setOnAction(event -> { Stage stage = (Stage) signup.getScene().getWindow(); stage.setScene(new AdminSignUpPage().getAdminSignUpScene(backAction)); });

        VBox content = new VBox(26, brand, introduction, credentials, login, signup);
        content.setMaxWidth(520); content.setAlignment(Pos.CENTER); content.setPadding(new Insets(44));
        VBox panel = new VBox(content); panel.setAlignment(Pos.CENTER); panel.setPrefWidth(700);
        panel.setStyle("-fx-background-color:#ffffff;");
        return panel;
    }

    private StackPane visual() {
        ImageView view = image(ADMIN_VISUALS[0], 520, 430);
        view.setPreserveRatio(true);
        StackPane photo = new StackPane(view);
        photo.setPrefSize(540, 455);
        photo.setMaxSize(540, 455);
        photo.setPadding(new Insets(12));
        photo.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,7px);");

        Label eyebrow = label("DIHADI ADMIN COMMUNITY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#d4af37;-fx-letter-spacing:1.5px;");
        Label title = label("Lead with trust.\nManage with clarity.",
                "-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#fff8f0;-fx-line-spacing:4px;");
        Label copy = label(
                "A single, dependable control center for your workforce, requirements and operations.",
                "-fx-font-size:15px;-fx-text-fill:#f8f0e2;-fx-opacity:.86;");
        copy.setWrapText(true);
        copy.setMaxWidth(500);

        VBox content = new VBox(24, photo, new VBox(12, eyebrow, title, copy));
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(38));

        StackPane panel = new StackPane(content);
        panel.setPrefWidth(700);
        panel.setStyle("-fx-background-color:linear-gradient(to bottom right,#343027,#4c4233);");
        start(view);
        return panel;
    }

    private void start(ImageView view) {
        final int[] index = { 0 };
        if (slideshow != null) slideshow.stop();
        slideshow = new Timeline(new KeyFrame(Duration.millis(1800), event -> {
            index[0] = (index[0] + 1) % ADMIN_VISUALS.length;
            try {
                java.net.URL url = getClass().getResource(ADMIN_VISUALS[index[0]]);
                if (url != null) view.setImage(new Image(url.toExternalForm()));
            } catch (Exception e) {}
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private void authenticate(TextField email, PasswordField password, Button login, Runnable backAction) {
        if (!com.dihadi.view.SessionManager.checkAccessAllowed(com.dihadi.view.SessionManager.Role.ADMIN)) {
            return;
        }

        String emailText = email.getText().trim();
        String passText = password.getText().trim();

        if (emailText.isBlank() || passText.isBlank()) {
            notice("Invalid Input", "Please enter your official email address and password.");
            return;
        }

        login.setDisable(true);
        login.setText("VERIFYING...");

        new Thread(() -> {
            try {
                Admin admin = new AdminController().authenticate(emailText, passText);

                Platform.runLater(() -> {
                    if (admin != null) {
                        com.dihadi.view.SessionManager.currentAdmin = admin;
                        Stage stage = (Stage) login.getScene().getWindow();
                        com.dihadi.view.NotificationToast.show(stage, "Login Successful", "Welcome back, " + admin.getFullName() + "!", com.dihadi.view.NotificationToast.ToastType.SUCCESS);
                        stage.setScene(new AdminDashboard().getDashboardScene(
                                () -> {
                                    com.dihadi.view.SessionManager.clearAllSessions();
                                    stage.setScene(getAdminLoginScene(backAction));
                                }));
                    } else {
                        login.setDisable(false);
                        login.setText("LOGIN");
                        notice("Invalid Credentials", "Invalid email address or password. Please check your credentials and try again.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    login.setDisable(false);
                    login.setText("LOGIN");
                    notice("Network Error", "Unable to verify credentials. Please check your internet connection.");
                });
            }
        }).start();
    }

    private ImageView image(String path, double width, double height) { ImageView view = new ImageView(new Image(getClass().getResource(path).toExternalForm())); view.setFitWidth(width); view.setFitHeight(height); view.setSmooth(true); return view; }
    private Label label(String text, String style) { Label label = new Label(text); label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style); return label; }
    private Button link(String text) { Button button = new Button(text); button.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;"); return button; }
    private String inputStyle() { return "-fx-background-color:#eee7dc;-fx-background-radius:12px;-fx-border-color:#cfc6b2;-fx-border-radius:12px;-fx-font-size:16px;-fx-padding:13px 16px;-fx-pref-height:56px;"; }
    private void notice(String title, String message) { com.dihadi.view.NotificationToast.show(title, message, com.dihadi.view.NotificationToast.ToastType.ALERT); }
}
