package com.dihadi.view;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/** Displays the welcome video as the complete welcome screen. */
public class WelcomePage {
    private MediaPlayer welcomePlayer;
    private boolean transitioning;

    public Scene getWelcomeScene(Runnable showHome) {
        MediaView mediaView = new MediaView();
        mediaView.setPreserveRatio(false);

        try {
            Path videoFile = resolveResourceToFile("/assets/videos/welcome_video.mp4", ".mp4");
            if (videoFile != null) {
                welcomePlayer = new MediaPlayer(new Media(videoFile.toUri().toString()));
                welcomePlayer.setCycleCount(MediaPlayer.INDEFINITE);
                welcomePlayer.setAutoPlay(true);
                mediaView.setMediaPlayer(welcomePlayer);
            } else {
                System.err.println("Video resource not found: /assets/videos/welcome_video.mp4");
            }
        } catch (Exception e) {
            System.err.println("Video could not be loaded: " + e.getMessage());
        }

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 1400, 780, Color.BLACK);
        mediaView.fitWidthProperty().bind(scene.widthProperty());
        mediaView.fitHeightProperty().bind(scene.heightProperty());

        // Retain the existing click-to-continue behavior without displaying UI.
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> showNextPage(showHome));

        return scene;
    }

    public void show(Stage homeStage, Runnable showHome) {
        homeStage.setScene(getWelcomeScene(showHome));
        homeStage.show();
    }

    /** Opens HomePage and releases media resources before the scene changes. */
    private void showNextPage(Runnable showHome) {
        if (transitioning) {
            return;
        }
        transitioning = true;
        stopWelcomeVideo();
        showHome.run();
    }

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
