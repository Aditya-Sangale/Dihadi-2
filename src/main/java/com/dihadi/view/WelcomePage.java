package com.dihadi.view;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WelcomePage extends Application{
    private static final double SCENE_WIDTH = 1400;
    private static final double SCENE_HEIGHT = 780;
    private MediaPlayer welcomePlayer;
    private boolean transitioning;

    @Override
    public void start(Stage Homestage){

        Label header = new Label("Welcome to Dihadi");
        header.setStyle("-fx-font-size: 32px; -fx-font-weight: 800; -fx-text-fill: #111111; -fx-letter-spacing: 1.0px; -fx-font-family: 'Segoe UI', sans-serif;");

        Label leftLine = new Label("┃");
        leftLine.setStyle("-fx-text-fill: #111111; -fx-font-size: 18px; -fx-font-weight: 700;");

        Label rightLine = new Label("┃");
        rightLine.setStyle("-fx-text-fill: #111111; -fx-font-size: 18px; -fx-font-weight: 700;");

        ImageView logoView = new ImageView();
        logoView.setFitWidth(62);
        logoView.setFitHeight(62);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        logoView.setViewport(new Rectangle2D(380, 0, 840, 840));

        try {
            String logoPath = "/assets/logo/dihadi logo.jpeg";
            var logoResource = getClass().getResource(logoPath);
            if (logoResource != null) {
                logoView.setImage(new Image(logoResource.toExternalForm()));
            }
        } catch (Exception e) {
            // Ignore missing logo; keeps the app running.
        }

        HBox titleWrap = new HBox(16, leftLine, header, rightLine);
        titleWrap.setAlignment(Pos.CENTER);

        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(logoView);
        headerPane.setCenter(titleWrap);
        headerPane.setPadding(new Insets(16, 24, 14, 24));
        headerPane.setPrefWidth(Double.MAX_VALUE);
        headerPane.setStyle("-fx-background-color: #f3e7ce; -fx-border-color: #d0c5af; -fx-border-width: 0 0 1px 0; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.08), 10, 0.28, 0, 1.5px);");

        VBox topBar = new VBox(headerPane);
        topBar.setPadding(new Insets(0, 0, 0, 0));

        MediaView mediaView = new MediaView();
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(1100);
        mediaView.setFitHeight(620);

        try {
            String videoPath = "/assets/videos/welcome_video.mp4";
            Path videoFile = resolveResourceToFile(videoPath, ".mp4");
            if (videoFile != null) {
                Media media = new Media(videoFile.toUri().toString());
                welcomePlayer = new MediaPlayer(media);
                welcomePlayer.setCycleCount(MediaPlayer.INDEFINITE);
                welcomePlayer.setAutoPlay(true);
                mediaView.setMediaPlayer(welcomePlayer);
            } else {
                System.err.println("Video resource not found: " + videoPath);
            }
        } catch (Exception e) {
            System.err.println("Video could not be loaded: " + e.getMessage());
        }

        StackPane videoContainer = new StackPane(mediaView);
        videoContainer.setPadding(new Insets(30, 0, 0, 0));
        videoContainer.setStyle("-fx-background-color: #fff8f0; -fx-background-radius: 24px; -fx-border-color:#d0c5af; -fx-border-radius:24px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.12), 24, 0, 0, 8);");

        // Short project introduction shown beside the welcome video.
        Label sideTitle = new Label("WORK MADE SIMPLE");
        sideTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: #111111; -fx-font-family: 'Segoe UI', sans-serif;");

        Label lineOne = new Label("Find skilled workers for every job.");
        lineOne.setStyle("-fx-font-size: 17px; -fx-font-weight: 700; -fx-text-fill: #735c00; -fx-font-family: 'Segoe UI Semibold', sans-serif;");

        Label lineTwo = new Label("Connect with trusted teams in your area.");
        lineTwo.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Verdana', sans-serif;");

        Label lineThree = new Label("Manage daily work, attendance, and payments with ease.");
        lineThree.setWrapText(true);
        lineThree.setMaxWidth(360);
        lineThree.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: 'Tahoma', sans-serif;");

        Label lineFour = new Label("Dihadi keeps workers and projects moving forward.");
        lineFour.setWrapText(true);
        lineFour.setMaxWidth(360);
        lineFour.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #111111; -fx-font-family: 'Trebuchet MS', sans-serif;");

        Label lineFive = new Label("Post requirements and reach dependable local talent.");
        lineFive.setWrapText(true);
        lineFive.setMaxWidth(360);
        lineFive.setStyle("-fx-font-size: 14px; -fx-text-fill: #3c3c3c; -fx-font-family: 'Lucida Sans', sans-serif;");

        Label lineSix = new Label("Build every day with confidence, clarity, and trust.");
        lineSix.setWrapText(true);
        lineSix.setMaxWidth(360);
        lineSix.setStyle("-fx-font-size: 15px; -fx-font-style: italic; -fx-text-fill: #735c00; -fx-font-family: 'Georgia', serif;");

        Label bullet1 = new Label("• Professional workforce coordination for every site");
        bullet1.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Lucida Sans', sans-serif;");
        Label bullet2 = new Label("• Safety-first planning, skilled equipment handling, and quality delivery");
        bullet2.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Lucida Sans', sans-serif;");
        Label bullet3 = new Label("• Transparent communication, dependable execution, and ongoing site support");
        bullet3.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: 'Lucida Sans', sans-serif;");

        Label highlightTitle = new Label("Core strengths");
        highlightTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #735c00; -fx-font-family: 'Georgia', serif;");

        Label highlight1 = new Label("Skilled personnel ready for every job phase.");
        highlight1.setWrapText(true);
        highlight1.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #2d2d2d; -fx-font-family: 'Arial', sans-serif;");

        Label highlight2 = new Label("Responsive support that keeps projects moving.");
        highlight2.setWrapText(true);
        highlight2.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #2d2d2d; -fx-font-family: 'Arial', sans-serif;");

        Button actionButton = new Button("Get Started");
        actionButton.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #342f28; -fx-background-color: #d4af37; -fx-background-radius: 999px; -fx-padding: 12 26 12 26; -fx-font-family: 'Arial', sans-serif;");

        VBox sidePanel = new VBox(14, sideTitle, lineOne, lineTwo, lineThree, lineFour, lineFive, lineSix, actionButton);
        sidePanel.setAlignment(Pos.CENTER_LEFT);
        sidePanel.setPadding(new Insets(30, 30, 30, 30));
        sidePanel.setPrefWidth(420);
        sidePanel.setStyle("-fx-background-color: #fff8f0; -fx-border-color: #d0c5af; -fx-border-width: 1px; -fx-background-radius: 24px; -fx-border-radius: 24px;");

        HBox mainContent = new HBox(24, videoContainer, sidePanel);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(0, 20, 0, 20));

        VBox content = new VBox(20, topBar, mainContent);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(0, 0, 20, 0));

        StackPane root = new StackPane();
        root.setPadding(new Insets(24, 24, 24, 24));
        root.getChildren().add(content);

        try {
            String backgroundPath = "/assets/images/background image.jpeg";
            var bgResource = getClass().getResource(backgroundPath);
            if (bgResource != null) {
                Image backgroundImage = new Image(bgResource.toExternalForm(), 1920, 1080, true, true, true);
                BackgroundSize bgSize = new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        false,
                        false,
                        true,
                        true
                );
                BackgroundImage bgImage = new BackgroundImage(
                        backgroundImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        bgSize
                );
                BackgroundFill overlay = new BackgroundFill(Color.web("#00000022"), CornerRadii.EMPTY, Insets.EMPTY);
                root.setBackground(new Background(new BackgroundFill[]{overlay}, new BackgroundImage[]{bgImage}));
            } else {
                root.setBackground(new Background(new BackgroundFill(Color.web("#e9e6da"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        } catch (Exception e) {
            root.setBackground(new Background(new BackgroundFill(Color.web("#e9e6da"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        // Capture clicks anywhere on the welcome UI, including the video and text panel.
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> showNextPage(Homestage));
        Homestage.setScene(scene);
        Homestage.setFullScreen(false);
        Homestage.setWidth(SCENE_WIDTH);
        Homestage.setHeight(SCENE_HEIGHT);
        Homestage.setMaximized(true);
        Homestage.show();
    }

    /** Opens HomePage and its navigation after the welcome interaction. */
    private void showNextPage(Stage stage) {
        if (transitioning) {
            return;
        }
        transitioning = true;
        stopWelcomeVideo();
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setWidth(SCENE_WIDTH);
        stage.setHeight(SCENE_HEIGHT);
        stage.setScene(new HomePage(stage).getHomeScene());
        // Apply the shared size after the new scene has been attached. JavaFX can otherwise restore
        // the previous window bounds while leaving full-screen mode.
        Platform.runLater(() -> {
            stage.setWidth(SCENE_WIDTH);
            stage.setHeight(SCENE_HEIGHT);
            stage.setMaximized(true);
        });
    }

    /** Releases the welcome media resources before the scene is replaced. */
    private void stopWelcomeVideo() {
        if (welcomePlayer != null) {
            welcomePlayer.stop();
            welcomePlayer.dispose();
            welcomePlayer = null;
        }
    }
    private Path resolveResourceToFile(String resourcePath, String suffix) throws IOException, URISyntaxException {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            return null;
        }

        URI resourceUri = resource.toURI();
        if ("file".equals(resourceUri.getScheme())) {
            return Path.of(resourceUri);
        }

        Path tempFile = Files.createTempFile("dihadi-resource-", suffix);
        tempFile.toFile().deleteOnExit();
        try (var in = resource.openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }
}
