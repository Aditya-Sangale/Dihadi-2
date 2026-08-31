package com.dihadi.view;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Premium paper-and-gold About Us page with integrated cinematic showcase video. */
public class AboutUs {
    private static final String CARD = "-fx-background-color:#f8eedb;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    private static final String GOLD_COLOR = "#d4af37";

    private MediaPlayer videoPlayer;

    public Scene getAboutScene(Runnable back) {
        return getAboutScene(back, null);
    }

    public Scene getAboutScene(Runnable back, Runnable workerAction) {
        VBox content = new VBox(32, videoShowcase(), hero(), mission(), stats(), faq(), footer());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(26, 24, 36, 24));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(header(back, workerAction));
        page.setStyle("-fx-background-color:#f3e7ce;");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        setBackground(root);
        return new Scene(root, 1400, 780);
    }

    private VBox hero() {
        Label eyebrow = label("THE REALITY OF INDIA'S WORKFORCE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1.4px;");
        Label title = label(
                "Every day of work should bring security, dignity, and hope.\nDIHADI is here to make that possible.",
                "-fx-font-size:37px;-fx-font-weight:800;-fx-text-fill:#3a3027;-fx-line-spacing:4px;");
        title.setWrapText(true);
        title.setMaxWidth(525);
        Label copy = label(
                "DIHADI bridges the gap between the informal chowk and the digital future—eliminating middlemen, wage theft, and insecurity.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        copy.setWrapText(true);
        copy.setMaxWidth(525);
        VBox text = new VBox(15, eyebrow, title, copy);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPrefWidth(535);
        ImageView photo = image("/assets/images/generalLabour.jpeg", 535, 320);
        StackPane imageBox = new StackPane(photo);
        imageBox.setStyle(
                "-fx-background-color:#ead8b5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
        HBox row = new HBox(34, text, imageBox);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(28, 32, 28, 32));
        row.setMaxWidth(1180);
        row.setStyle(CARD);
        VBox section = new VBox(row);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(1180);
        return section;
    }

    /** Cinematic Showcase Video Section displaying the video cleanly in the card. */
    private VBox videoShowcase() {
        MediaView mediaView = new MediaView();
        mediaView.setFitWidth(1140);
        mediaView.setFitHeight(620);
        mediaView.setPreserveRatio(false);

        // Rounded corners for the video
        Rectangle clip = new Rectangle(1140, 620);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        mediaView.setClip(clip);

        try {
            Path videoFile = resolveResourceToFile("/assets/videos/dihadi_AboutUs.mp4", ".mp4");
            if (videoFile != null) {
                Media media = new Media(videoFile.toUri().toString());
                videoPlayer = new MediaPlayer(media);
                videoPlayer.setAutoPlay(true);
                videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaView.setMediaPlayer(videoPlayer);

                mediaView.setCursor(Cursor.HAND);
                mediaView.setOnMouseClicked(e -> {
                    if (videoPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        videoPlayer.pause();
                    } else {
                        videoPlayer.play();
                    }
                });
            } else {
                System.err.println("About Us video resource not found: /assets/videos/dihadi_AboutUs.mp4");
            }
        } catch (Exception ex) {
            System.err.println("Could not load About Us video: " + ex.getMessage());
        }

        StackPane videoContainer = new StackPane(mediaView);
        videoContainer.setPrefSize(1140, 620);
        videoContainer.setMaxSize(1140, 620);
        videoContainer.setStyle("-fx-background-color:#161311;-fx-background-radius:24px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),20,0,0,8px);");

        VBox showcaseCard = new VBox(videoContainer);
        showcaseCard.setAlignment(Pos.CENTER);
        showcaseCard.setPadding(new Insets(10));
        showcaseCard.setMaxWidth(1180);
        showcaseCard.setStyle("-fx-background-color:#eedebf;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),18,0,0,6px);");

        return showcaseCard;
    }

    private HBox highlightPill(String text) {
        Label l = label(text, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#e9c349;");
        HBox box = new HBox(l);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8, 16, 8, 16));
        box.setStyle("-fx-background-color:rgba(233,195,73,0.12);-fx-background-radius:16px;-fx-border-color:rgba(233,195,73,0.35);-fx-border-radius:16px;");
        return box;
    }

    private String formatTime(Duration duration) {
        if (duration == null || duration.isUnknown()) {
            return "00:00";
        }
        int seconds = (int) Math.floor(duration.toSeconds());
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private VBox mission() {
        VBox a = card("Our Mission",
                "To protect, empower, organize, and uplift hardworking laborers by providing transparent, digital access to dignified work and fair wages.",
                true);
        VBox b = card("Our Vision",
                "We are building more than an app: a global workforce marketplace where every laborer is recognized, protected, and able to build a stable life without fear of exploitation.",
                false);
        HBox row = new HBox(24, a, b);
        row.setAlignment(Pos.CENTER);
        VBox section = new VBox(row);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(26));
        section.setMaxWidth(1180);
        section.setStyle(
                "-fx-background-color:#eedebf;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
        return section;
    }

    private VBox card(String title, String text, boolean quote) {
        Label h = label(title, "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Label p = label(text, "-fx-font-size:" + (quote ? "20px" : "16px") + ";-fx-font-style:"
                + (quote ? "italic" : "normal") + ";-fx-text-fill:#3a3027;-fx-line-spacing:4px;");
        p.setWrapText(true);
        VBox box = new VBox(12, h, p);
        box.setPadding(new Insets(28));
        box.setPrefWidth(535);
        box.setStyle(CARD);
        return box;
    }

    private VBox stats() {
        Label quote = label(
                "“Every worker deserves fair work, fair wages, and the dignity to build a better tomorrow.”",
                "-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#f8f0e2;-fx-line-spacing:3px;");
        quote.setWrapText(true);
        quote.setMaxWidth(620);
        Label middlemen = label("100%", "-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label middlemenText = label("MIDDLEMEN ELIMINATED",
                "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#f8f0e2;-fx-letter-spacing:1px;");
        Label zero = label("Zero", "-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label zeroText = label("TOLERANCE FOR EXPLOITATION",
                "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#f8f0e2;-fx-letter-spacing:1px;");
        VBox a = new VBox(6, middlemen, middlemenText), b = new VBox(6, zero, zeroText);
        a.setAlignment(Pos.CENTER);
        b.setAlignment(Pos.CENTER);
        HBox row = new HBox(42, quote, a, b);
        row.setAlignment(Pos.CENTER);
        VBox out = new VBox(row);
        out.setAlignment(Pos.CENTER);
        out.setPadding(new Insets(25, 32, 25, 32));
        out.setMaxWidth(1180);
        out.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return out;
    }

    private VBox faq() {
        VBox list = new VBox(10);
        list.setMaxWidth(900);
        list.setAlignment(Pos.CENTER);
        add(list, "What is DIHADI?",
                "DIHADI is a comprehensive digital labor workforce ecosystem designed to connect daily wage earners directly with contractors and enterprises, eliminating exploitative middlemen and ensuring fair, timely compensation.");
        add(list, "How does the platform protect workers?",
                "We provide verified digital labor IDs, transparent wage tracking directly to bank accounts, and an immutable record of work history, preventing wage theft and unauthorized deductions.");
        add(list, "Can contractors hire large teams?",
                "Yes. Contractors can source, manage, and pay large cohorts of verified workers through a dependable digital workflow.");
        add(list, "What about worker welfare?",
                "Welfare is built in, with access to financial services and safety-net options that help workers and their families plan for the future.");
        VBox out = new VBox(17, label("Understanding the DIHADI Ecosystem",
                "-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:#3a3027;"), list);
        out.setAlignment(Pos.CENTER);
        out.setPadding(new Insets(30, 24, 34, 24));
        out.setMaxWidth(1180);
        out.setStyle("-fx-background-color:#f0e1c5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
        return out;
    }

    private void add(VBox list, String qText, String answer) {
        Label q = label(qText, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                plus = label("+", "-fx-font-size:24px;-fx-text-fill:#735c00;-fx-font-weight:700;"),
                a = label(answer, "-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:2px;");
        a.setWrapText(true);
        a.setVisible(false);
        a.setManaged(false);
        HBox head = new HBox(q, plus);
        head.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(q, Priority.ALWAYS);
        VBox item = new VBox(0, head, a);
        item.setPadding(new Insets(16, 20, 16, 20));
        item.setStyle(CARD);
        item.setCursor(javafx.scene.Cursor.HAND);
        item.setOnMouseClicked(e -> {
            boolean open = !a.isVisible();
            a.setVisible(open);
            a.setManaged(open);
            plus.setText(open ? "−" : "+");
            item.setSpacing(open ? 10 : 0);
        });
        list.getChildren().add(item);
    }

    private VBox footer() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(310);
        VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
        identity.setPrefWidth(360);
        VBox explore = footerColumn("Explore", "Home", () -> navigateTo("Home"), "Find Work", () -> navigateTo("Worker"), "About Us",
                () -> navigateTo("About Us"));
        VBox contact = footerColumn("Contact", "9561789599", () -> navigateTo("Contact Us"), "info@meridihadi.com",
                () -> navigateTo("Contact Us"), "Pune, Maharashtra", () -> navigateTo("Contact Us"));
        HBox top = new HBox(64, identity, explore, contact);
        top.setAlignment(Pos.TOP_LEFT);
        VBox out = new VBox(22, top, label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        out.setPadding(new Insets(32, 42, 24, 42));
        out.setMaxWidth(1180);
        out.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return out;
    }

    private void navigateTo(String destination) {
        stopVideo();
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                AppNavigator.open(stage, destination);
                return;
            }
        }
    }

    private void stopVideo() {
        if (videoPlayer != null) {
            try {
                videoPlayer.stop();
                videoPlayer.dispose();
                videoPlayer = null;
            } catch (Exception ignored) {}
        }
    }

    private VBox footerColumn(String heading, String textOne, Runnable actionOne, String textTwo, Runnable actionTwo,
            String textThree, Runnable actionThree) {
        VBox column = new VBox(7, label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                footerLink(textOne, actionOne), footerLink(textTwo, actionTwo), footerLink(textThree, actionThree));
        column.setPrefWidth(180);
        return column;
    }

    private Button footerLink(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setStyle(
                "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.82;-fx-font-size:13px;-fx-cursor:hand;");
        return button;
    }

    private BorderPane header(Runnable back, Runnable workerAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo,
                label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12);
        nav.setAlignment(Pos.CENTER);
        for (String n : new String[] { "Home", "Business", "Worker", "Recruiter", "About Us", "Contact Us" }) {
            Button b = navButton(n, n.equals("About Us"));
            b.setOnAction(e -> {
                stopVideo();
                AppNavigator.open((Stage) b.getScene().getWindow(), n);
            });
            nav.getChildren().add(b);
        }
        Button admin = AppNavigator.createHeaderAdminButton();
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(nav);
        bar.setRight(new HBox(10, admin));
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private Button navButton(String t, boolean active) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return l;
    }

    private ImageView image(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        if (p.contains("/assets/logo/")) {
            v.setPreserveRatio(true);
        }
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setSmooth(true);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private Path resolveResourceToFile(String resourcePath, String suffix) throws IOException, URISyntaxException {
        var resource = getClass().getResource(resourcePath);
        if (resource != null) {
            URI resourceUri = resource.toURI();
            if ("file".equals(resourceUri.getScheme())) {
                return Path.of(resourceUri);
            }
            Path tempFile = Files.createTempFile("dihadi-video-", suffix);
            tempFile.toFile().deleteOnExit();
            try (var in = resource.openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        }

        // Direct file fallback in case of local IDE paths
        Path directPath = Path.of("src/main/resources" + resourcePath);
        if (Files.exists(directPath)) {
            return directPath.toAbsolutePath();
        }
        return null;
    }

    private void setBackground(StackPane root) {
        root.setBackground(
                new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    public void start(Stage stage) {
        stage.setScene(getAboutScene(null));
        stage.setTitle("Dihadi - About Us");
        stage.setMaximized(true);
        stage.show();
    }
}
