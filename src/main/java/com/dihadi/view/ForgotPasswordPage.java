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
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Shared password-recovery layout used by worker and recruiter account flows.
 */
public class ForgotPasswordPage {
    private final boolean recruiter;
    private final Runnable back;
    private Timeline slideshow;
    private final String[] workerImages = { "/assets/images/worker 5.jpeg", "/assets/images/worker 2.jpeg",
            "/assets/images/sitesuperviser.jpeg", "/assets/images/welder.jpeg" };
    private final String[] recruiterImages = { "/assets/images/recruiter/slide-01.jpeg",
            "/assets/images/recruiter/slide-02.jpeg", "/assets/images/recruiter/slide-03.jpeg",
            "/assets/images/recruiter/slide-04.jpeg", "/assets/images/recruiter/slide-05.jpeg" };

    public ForgotPasswordPage(boolean recruiter, Runnable back) {
        this.recruiter = recruiter;
        this.back = back;
    }

    public Scene getForgotPasswordScene() {
        BorderPane page = new BorderPane();
        page.setLeft(form());
        page.setCenter(visual());
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private VBox form() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        HBox brand = new HBox(12, logo, label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        Label title = label("↻  Reset Your Password",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label copy = label(
                "Please enter your registered mobile number or email address to receive an OTP and reset your password.",
                "-fx-font-size:16px;-fx-text-fill:#4c4637;-fx-line-spacing:3px;");
        copy.setWrapText(true);
        copy.setMaxWidth(490);
        Label caption = label("ENTER YOUR REGISTERED DETAILS",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#1e1b15;-fx-letter-spacing:1px;");
        TextField contact = new TextField();
        contact.setPromptText("Enter your Mobile Number or Email Address");
        contact.setStyle(
                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:transparent;-fx-font-size:15px;-fx-padding:14px 16px;-fx-pref-height:55px;");
        Button send = new Button("Send OTP  →");
        send.setMaxWidth(Double.MAX_VALUE);
        send.setStyle(
                "-fx-background-color:#735c00;-fx-background-radius:999px;-fx-text-fill:#f6d676;-fx-font-size:16px;-fx-font-weight:700;-fx-padding:14px;-fx-cursor:hand;");
        send.setOnAction(e -> AppNavigator.information("Send OTP",
                "OTP verification will be available when the backend is connected."));
        Button login = link("←  Back to Login");
        login.setOnAction(e -> {
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(recruiter ? new com.dihadi.view.recruiter.RecruiterLoginPage(back).getLoginScene() : new com.dihadi.view.worker.WorkerLoginPage(back).getLoginScene());
        });
        VBox content = new VBox(28, brand, new VBox(12, title, copy), new VBox(8, caption, contact), send, login);
        content.setMaxWidth(510);
        content.setAlignment(Pos.CENTER_LEFT);
        VBox pane = new VBox(content);
        pane.setAlignment(Pos.CENTER);
        pane.setPrefWidth(700);
        pane.setPadding(new Insets(70));
        pane.setStyle("-fx-background-color:#ffffff;");
        return pane;
    }

    /**
     * Mirrors the Worker and Recruiter sign-up side panel while retaining each
     * flow's existing images.
     */
    private StackPane visual() {
        String[] images = recruiter ? recruiterImages : workerImages;
        ImageView view = image(images[0], 520, 430);
        view.setPreserveRatio(true);
        StackPane photo = new StackPane(view);
        photo.setPrefSize(540, 455);
        photo.setMaxSize(540, 455);
        photo.setPadding(new Insets(12));
        photo.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,7px);");
        Label eyebrow = label(recruiter ? "DIHADI RECRUITER COMMUNITY" : "DIHADI WORKER COMMUNITY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#d4af37;-fx-letter-spacing:1.5px;");
        Label title = label(recruiter ? "Build a team\nthat delivers." : "Build a profile\nthat works for you.",
                "-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#fff8f0;-fx-line-spacing:4px;");
        Label copy = label(recruiter
                ? "Your business details help DIHADI connect you with verified, skilled professionals for every project."
                : "Your work, skills and wage expectations stay clear, professional and ready for the right opportunity.",
                "-fx-font-size:15px;-fx-text-fill:#f8f0e2;-fx-opacity:.86;");
        copy.setWrapText(true);
        copy.setMaxWidth(500);
        VBox visualContent = new VBox(24, photo, new VBox(12, eyebrow, title, copy));
        visualContent.setAlignment(Pos.CENTER);
        visualContent.setPadding(new Insets(38));
        StackPane pane = new StackPane(visualContent);
        pane.setPrefWidth(700);
        pane.setMinWidth(400);
        pane.setStyle("-fx-background-color:linear-gradient(to bottom right,#343027,#4c4233);");
        start(view, images);
        return pane;
    }

    private void start(ImageView v, String[] images) {
        final int[] i = { 0 };
        slideshow = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            i[0] = (i[0] + 1) % images.length;
            v.setImage(load(images[i[0]]));
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
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
}