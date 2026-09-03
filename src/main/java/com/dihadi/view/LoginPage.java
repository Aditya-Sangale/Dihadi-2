package com.dihadi.view;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.dihadi.config.AuthService;
import com.dihadi.dao.WorkerDao;
import com.dihadi.dao.RecruiterDao;
import com.dihadi.model.Worker;
import com.dihadi.model.Recruiter;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/** Shared DIHADI login layout with worker and recruiter visual variants. */
public class LoginPage {
    private final boolean recruiter;
    private final Runnable back;
    private Timeline slideshow;
    private final String[] workerImages = { "/assets/images/worker 5.jpeg", "/assets/images/worker 2.jpeg",
            "/assets/images/sitesuperviser.jpeg", "/assets/images/welder.jpeg" };
    private final String[] recruiterImages = { "/assets/images/recruiter/slide-01.jpeg",
            "/assets/images/recruiter/slide-02.jpeg", "/assets/images/recruiter/slide-03.jpeg",
            "/assets/images/recruiter/slide-04.jpeg", "/assets/images/recruiter/slide-05.jpeg" };

    public LoginPage(boolean recruiter, Runnable back) {
        this.recruiter = recruiter;
        this.back = back;
    }

    public Scene getLoginScene() {
        if (!recruiter) {
            return getWorkerLoginScene();
        }
        BorderPane page = new BorderPane();
        page.setLeft(form());
        page.setCenter(visual());
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private Scene getWorkerLoginScene() {
        Region bg = new Region();
        var res = getClass().getResource("/assets/images/worker_auth_bg.jpg");
        String bgUrl = (res != null) ? res.toExternalForm() : "";
        bg.setStyle("-fx-background-image: url('" + bgUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;");

        VBox formCard = workerLoginForm();
        StackPane root = new StackPane(bg, formCard);
        return new Scene(root, 1400, 780);
    }

    private VBox workerLoginForm() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 72);
        logo.setPreserveRatio(true);
        VBox brand = new VBox(4, logo,
                label("DIHADI",
                        "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#735c00;"),
                label("Meri Dihadi ~ Mera Haq",
                        "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        brand.setAlignment(Pos.CENTER);

        Label welcome = label("Worker Login", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label intro = label("Enter your mobile number or email to access your worker account.",
                "-fx-font-size:14px;-fx-text-fill:#594f42;");
        intro.setWrapText(true);
        intro.setMaxWidth(400);
        intro.setAlignment(Pos.CENTER);
        VBox introBox = new VBox(6, welcome, intro);
        introBox.setAlignment(Pos.CENTER);

        TextField account = new TextField();
        account.setPromptText("Mobile Number or Email");
        account.setStyle(transparentInputStyle());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle(transparentInputStyle());

        VBox credentials = new VBox(12,
                label("Mobile Number or Email", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2c251d;"),
                account,
                label("Password", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2c251d;"),
                passwordField);

        Button continueButton = new Button("Login");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-size:17px;-fx-font-weight:800;-fx-padding:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.18),10,0,0,3px);");
        continueButton.setOnAction(e -> handleLogin(account, passwordField, continueButton));

        Button create = link("New worker? Create an account");
        create.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-cursor:hand;");
        create.setOnAction(e -> {
            if (!SessionManager.checkAccessAllowed(SessionManager.Role.WORKER)) return;
            Stage stage = (Stage) create.getScene().getWindow();
            stage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(() -> stage.setScene(getWorkerLoginScene())));
        });

        VBox card = new VBox(20, brand, introBox, credentials, continueButton, create);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(460);
        card.setPadding(new Insets(34, 38, 34, 38));
        card.setStyle(
                "-fx-background-color:rgba(255,253,248,0.84);" +
                "-fx-background-radius:22px;" +
                "-fx-border-color:rgba(212,175,55,0.45);" +
                "-fx-border-radius:22px;" +
                "-fx-border-width:1.5px;" +
                "-fx-effect:dropshadow(gaussian,rgba(30,24,16,0.18),28,0,0,10px);");

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        wrapper.setPadding(new Insets(30, 20, 30, 80));
        return wrapper;
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

    private VBox form() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 72);
        logo.setPreserveRatio(true);
        VBox brand = new VBox(4, logo,
                label("DIHADI",
                        "-fx-font-family:'Georgia';-fx-font-size:34px;-fx-font-weight:800;-fx-text-fill:#27438a;"),
                label("Meri Dihadi ~ Mera Haq",
                        "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        brand.setAlignment(Pos.CENTER);
        Label welcome = label("Welcome to DIHADI", "-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label intro = label("Please enter your login details to proceed ahead and join the network.",
                "-fx-font-size:15px;-fx-text-fill:#4c4637;");
        intro.setWrapText(true);
        intro.setMaxWidth(470);
        intro.setAlignment(Pos.CENTER);
        VBox introBox = new VBox(8, welcome, intro);
        introBox.setAlignment(Pos.CENTER);
        TextField account = new TextField();
        account.setPromptText("Mobile Number or Email");
        account.setStyle(inputStyle());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle(inputStyle());

        VBox credentials = new VBox(12,
                label("Enter Mobile Number or Email", "-fx-font-size:19px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                account,
                passwordField);

        Button continueButton = new Button("Login");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-size:18px;-fx-font-weight:700;-fx-padding:13px;-fx-cursor:hand;");
        continueButton.setOnAction(e -> handleLogin(account, passwordField, continueButton));
        Button create = link(recruiter ? "New recruiter? Create an account" : "New worker? Create an account");
        create.setOnAction(e -> {
            Stage stage = (Stage) create.getScene().getWindow();
            if (recruiter) {
                if (!SessionManager.checkAccessAllowed(SessionManager.Role.RECRUITER)) return;
                stage.setScene(new com.dihadi.view.recruiter.SignUpRecruiter().getRecruiterSignUpScene(back));
            } else {
                if (!SessionManager.checkAccessAllowed(SessionManager.Role.WORKER)) return;
                stage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(() -> stage.setScene(getLoginScene())));
            }
        });
        VBox content = new VBox(24, brand, introBox, credentials, continueButton, create);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(520);
        content.setPadding(new Insets(50));
        VBox pane = new VBox(content);
        pane.setAlignment(Pos.CENTER);
        pane.setPrefWidth(700);
        pane.setStyle("-fx-background-color:#ffffff;");
        return pane;
    }

    private StackPane visual() {
        String[] images = recruiter ? recruiterImages : workerImages;
        ImageView view = image(images[0], 520, 430);
        view.setPreserveRatio(true);
        StackPane photo = new StackPane(view);
        photo.setPrefSize(540, 455);
        photo.setPadding(new Insets(12));
        photo.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,7px);");
        Label eyebrow = label(recruiter ? "DIHADI RECRUITER COMMUNITY" : "DIHADI WORKER COMMUNITY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#d4af37;-fx-letter-spacing:1.5px;");
        Label title = label(recruiter ? "Build a team\nthat delivers." : "Build a profile\nthat works for you.",
                "-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#fff8f0;-fx-line-spacing:4px;");
        Label copy = label(
                recruiter ? "Connect with verified professionals for every project."
                        : "Your work, skills and wage expectations stay ready for the right opportunity.",
                "-fx-font-size:15px;-fx-text-fill:#f8f0e2;-fx-opacity:.86;");
        copy.setWrapText(true);
        copy.setMaxWidth(500);
        VBox content = new VBox(24, photo, new VBox(12, eyebrow, title, copy));
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(38));
        StackPane panel = new StackPane(content);
        panel.setPrefWidth(700);
        panel.setStyle("-fx-background-color:linear-gradient(to bottom right,#343027,#4c4233);");
        start(view, images);
        return panel;
    }

    private void start(ImageView view, String[] images) {
        final int[] i = { 0 };
        slideshow = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            i[0] = (i[0] + 1) % images.length;
            view.setImage(load(images[i[0]]));
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private String inputStyle() {
        return "-fx-background-color:#eee7dc;-fx-background-radius:12px;-fx-border-color:#cfc6b2;-fx-border-radius:12px;-fx-font-size:15px;-fx-padding:13px 16px;-fx-pref-height:54px;";
    }

    private Button link(String s) {
        Button b = new Button(s);
        b.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;");
        return b;
    }

    private Label label(String s, String style) {
        Label l = new Label(s);
        l.setStyle("-fx-font-family:'Segoe UI';" + style);
        return l;
    }

    private ImageView image(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setSmooth(true);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private void handleLogin(TextField account, PasswordField passwordField, Button continueButton) {
        SessionManager.Role targetRole = recruiter ? SessionManager.Role.RECRUITER : SessionManager.Role.WORKER;
        if (!SessionManager.checkAccessAllowed(targetRole)) {
            return;
        }

        String identifierInput = account.getText().trim();
        if (identifierInput.isEmpty()) {
            NotificationToast.show("Login", "Please enter your mobile number or email address.", NotificationToast.ToastType.ALERT);
            return;
        }

        String password = passwordField.getText();
        if (password == null || password.trim().isEmpty()) {
            NotificationToast.show("Login", "Please enter your password.", NotificationToast.ToastType.ALERT);
            return;
        }

        continueButton.setDisable(true);
        continueButton.setText("Verifying...");
        AtomicBoolean loginCompleted = new AtomicBoolean(false);
        PauseTransition loginTimeout = new PauseTransition(Duration.seconds(25));
        loginTimeout.setOnFinished(e -> {
            if (loginCompleted.compareAndSet(false, true)) {
                continueButton.setDisable(false);
                continueButton.setText("Login");
                NotificationToast.show("Login Timed Out",
                        "The account service did not respond. Please check your internet connection and try again.",
                        NotificationToast.ToastType.ERROR);
            }
        });
        loginTimeout.play();

        new Thread(() -> {
            try {
                if (recruiter) {
                    com.dihadi.controller.RecruiterController recruiterController = new com.dihadi.controller.RecruiterController();
                    Recruiter r = recruiterController.getRecruiterByEmailOrMobile(identifierInput);

                    javafx.application.Platform.runLater(() -> {
                        if (!loginCompleted.compareAndSet(false, true)) return;
                        loginTimeout.stop();
                        continueButton.setDisable(false);
                        continueButton.setText("Login");

                        if (r == null || r.getPassword() == null || !r.getPassword().equals(password)) {
                            NotificationToast.show("Login Failed", "Invalid credentials. Please try again.", NotificationToast.ToastType.ERROR);
                        } else {
                            com.dihadi.view.SessionManager.currentRecruiter = r;
                            Stage stage = (Stage) continueButton.getScene().getWindow();
                            NotificationToast.show(stage, "Login Successful", "Welcome back, " + r.getFirstName() + "!", NotificationToast.ToastType.SUCCESS);
                            Runnable homeNav = (back != null) ? back : () -> AppNavigator.open(stage, "Home");
                            stage.setScene(new com.dihadi.view.recruiter.RecruiterPage().getRecruiterScene(homeNav));
                        }
                    });
                } else {
                    com.dihadi.controller.WorkerController workerController = new com.dihadi.controller.WorkerController();
                    Worker worker = workerController.getWorkerByEmailOrMobile(identifierInput);

                    javafx.application.Platform.runLater(() -> {
                        if (!loginCompleted.compareAndSet(false, true)) return;
                        loginTimeout.stop();
                        continueButton.setDisable(false);
                        continueButton.setText("Login");

                        if (worker == null || worker.getPassword() == null || !worker.getPassword().equals(password)) {
                            NotificationToast.show("Login Failed", "Invalid credentials. Please try again.", NotificationToast.ToastType.ERROR);
                        } else {
                            com.dihadi.view.SessionManager.currentWorker = worker;
                            Stage stage = (Stage) continueButton.getScene().getWindow();
                            NotificationToast.show(stage, "Login Successful", "Welcome back, " + worker.getFirstName() + "!", NotificationToast.ToastType.SUCCESS);
                            Runnable homeNav = (back != null) ? back : () -> AppNavigator.open(stage, "Home");
                            stage.setScene(new com.dihadi.view.WorkerPage(worker).getWorkerScene(homeNav));
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    if (!loginCompleted.compareAndSet(false, true)) return;
                    loginTimeout.stop();
                    continueButton.setDisable(false);
                    continueButton.setText("Login");
                    NotificationToast.show("Network Error",
                            "Unable to verify credentials. Please check your internet connection.",
                            NotificationToast.ToastType.ERROR);
                });
            }
        }).start();
    }
}
