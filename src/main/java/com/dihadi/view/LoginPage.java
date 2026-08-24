package com.dihadi.view;

import javafx.animation.KeyFrame;
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
        BorderPane page = new BorderPane();
        page.setLeft(form());
        page.setCenter(visual());
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private VBox form() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 72);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        VBox brand = new VBox(4, logo,
                label("DIHADI",
                        "-fx-font-family:'Georgia';-fx-font-size:34px;-fx-font-weight:800;-fx-text-fill:#27438a;"),
                label("Mera Haq ~ Meri Dihadi",
                        "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        brand.setAlignment(Pos.CENTER);
        Label welcome = label("👋  Welcome to DIHADI", "-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label intro = label("Please enter your login details to proceed ahead and join the network.",
                "-fx-font-size:15px;-fx-text-fill:#4c4637;");
        intro.setWrapText(true);
        intro.setMaxWidth(470);
        intro.setAlignment(Pos.CENTER);
        VBox introBox = new VBox(8, welcome, intro);
        introBox.setAlignment(Pos.CENTER);
        TextField account = new TextField();
        account.setPromptText("e.g. 9876543210");
        account.setStyle(inputStyle());
        VBox credentials = new VBox(12,
                label("Enter your Mobile Number", "-fx-font-size:19px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                account);
        Button continueButton = new Button("Continue with OTP");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-size:18px;-fx-font-weight:700;-fx-padding:13px;-fx-cursor:hand;");
        continueButton.setOnAction(e -> handleLogin(account, continueButton));
        Button create = link(recruiter ? "New recruiter? Create an account" : "New worker? Create an account");
        create.setOnAction(e -> {
            Stage stage = (Stage) create.getScene().getWindow();
            if (recruiter)
                stage.setScene(new com.dihadi.view.recruiter.SignUpRecruiter().getRecruiterSignUpScene(back));
            else
                stage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(back));
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

    private void handleLogin(TextField account, Button continueButton) {
        String mobileInput = account.getText().trim();
        if (mobileInput.isEmpty()) {
            AppNavigator.information("Login", "Please enter your mobile number.");
            return;
        }

        // Clean input: remove spaces, hyphens, parentheses
        String cleaned = mobileInput.replaceAll("[\\s\\-\\(\\)]", "");

        String phoneNumber;
        if (cleaned.startsWith("+91") && cleaned.length() == 13 && cleaned.substring(3).matches("\\d{10}")) {
            phoneNumber = cleaned;
        } else if (cleaned.startsWith("91") && cleaned.length() == 12 && cleaned.matches("\\d{12}")) {
            phoneNumber = "+" + cleaned;
        } else if (cleaned.length() == 10 && cleaned.matches("\\d{10}")) {
            phoneNumber = "+91" + cleaned;
        } else if (cleaned.startsWith("+") && cleaned.length() >= 8 && cleaned.substring(1).matches("\\d+")) {
            phoneNumber = cleaned;
        } else {
            AppNavigator.information("Invalid Mobile Number",
                    "Please enter a valid 10-digit mobile number (e.g., 9876543210 or +919876543210).");
            return;
        }

        continueButton.setDisable(true);
        continueButton.setText("Sending OTP...");

        final String finalPhone = phoneNumber;
        new Thread(() -> {
            String sessionInfo = AuthService.sendOtp(finalPhone);
            javafx.application.Platform.runLater(() -> {
                continueButton.setDisable(false);
                continueButton.setText("Continue");

                if (sessionInfo == null) {
                    AppNavigator.information("OTP Error",
                            "Failed to send OTP. Please check the mobile number and try again.");
                    return;
                }

                // Show OTP input dialog
                TextInputDialog otpDialog = new TextInputDialog();
                otpDialog.setTitle("OTP Verification");
                otpDialog.setHeaderText("Enter the 6-digit OTP sent to " + finalPhone);
                otpDialog.setContentText("OTP:");
                Optional<String> result = otpDialog.showAndWait();

                if (result.isPresent() && !result.get().isBlank()) {
                    String otp = result.get().trim();
                    continueButton.setDisable(true);
                    continueButton.setText("Verifying...");

                    new Thread(() -> {
                        String uid = AuthService.verifyOtp(sessionInfo, otp);
                        javafx.application.Platform.runLater(() -> {
                            continueButton.setDisable(false);
                            continueButton.setText("Continue");

                            if (uid == null) {
                                AppNavigator.information("Verification Failed",
                                        "Invalid OTP. Please try again.");
                                return;
                            }

                            Stage stage = (Stage) continueButton.getScene().getWindow();
                            Runnable homeNav = (back != null) ? back : () -> AppNavigator.open(stage, "Home");
                            if (recruiter) {
                                stage.setScene(new com.dihadi.view.recruiter.RecruiterPage().getRecruiterScene(homeNav));
                            } else {
                                stage.setScene(new com.dihadi.view.WorkerPage().getWorkerScene(homeNav));
                            }
                        });
                    }).start();
                }
            });
        }).start();
    }
}