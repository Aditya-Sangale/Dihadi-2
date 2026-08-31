package com.dihadi.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Modern floating notification toast / pop-up banner for real-time alerts.
 */
public class NotificationToast {

    public enum ToastType {
        SUCCESS("🎉", "#2a7e3b", "#e8f5e9", "#a5d6a7"),
        INFO("📥", "#735c00", "#fff8e1", "#ffe082"),
        ACTION("📋", "#1565c0", "#e3f2fd", "#90caf9"),
        ALERT("🔔", "#b8921e", "#fffcf5", "#d4af37"),
        ERROR("⚠️", "#c62828", "#ffebee", "#ef9a9a");

        final String icon;
        final String textCol;
        final String bgCol;
        final String borderCol;

        ToastType(String icon, String textCol, String bgCol, String borderCol) {
            this.icon = icon;
            this.textCol = textCol;
            this.bgCol = bgCol;
            this.borderCol = borderCol;
        }
    }

    public static void show(Node anyNodeInScene, String title, String message, ToastType type) {
        if (anyNodeInScene == null) return;
        Scene scene = anyNodeInScene.getScene();
        if (scene == null) return;
        show(scene.getWindow(), title, message, type);
    }

    public static void show(Window window, String title, String message, ToastType type) {
        if (window == null) return;

        Platform.runLater(() -> {
            try {
                Popup popup = new Popup();
                popup.setAutoFix(true);
                popup.setAutoHide(true);

                ToastType t = (type != null) ? type : ToastType.ALERT;

                Label iconLbl = new Label(t.icon);
                iconLbl.setStyle("-fx-font-size: 24px;");

                StackPane iconBubble = new StackPane(iconLbl);
                iconBubble.setPrefSize(44, 44);
                iconBubble.setMinSize(44, 44);
                iconBubble.setMaxSize(44, 44);
                iconBubble.setStyle("-fx-background-color: " + t.bgCol + "; -fx-background-radius: 12px; -fx-border-color: " + t.borderCol + "; -fx-border-radius: 12px;");

                Label titleLbl = new Label(title);
                titleLbl.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #1e1b15;");

                Label msgLbl = new Label(message);
                msgLbl.setWrapText(true);
                msgLbl.setMaxWidth(340);
                msgLbl.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px; -fx-text-fill: #4c4637; -fx-line-spacing: 2px;");

                VBox textVBox = new VBox(4, titleLbl, msgLbl);
                HBox.setHgrow(textVBox, Priority.ALWAYS);

                Button closeBtn = new Button("✕");
                closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8c7e6b; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 4px;");
                closeBtn.setOnAction(e -> popup.hide());

                HBox root = new HBox(14, iconBubble, textVBox, closeBtn);
                root.setAlignment(Pos.CENTER_LEFT);
                root.setPadding(new Insets(14, 18, 14, 18));
                root.setMaxWidth(440);
                root.setMinWidth(380);
                root.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16px; "
                        + "-fx-border-color: #d4af37; -fx-border-width: 2px; -fx-border-radius: 16px; "
                        + "-fx-effect: dropshadow(gaussian, rgba(34,34,34,0.22), 22, 0, 0, 8px);");

                popup.getContent().add(root);

                // Position near top-right of the window
                double x = window.getX() + window.getWidth() - 470;
                double y = window.getY() + 75;

                popup.show(window, x, y);

                // Fade-in animation
                root.setOpacity(0);
                root.setTranslateY(-15);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), root);
                fadeIn.setToValue(1.0);

                TranslateTransition slideDown = new TranslateTransition(Duration.millis(250), root);
                slideDown.setToY(0);

                fadeIn.play();
                slideDown.play();

                // Auto-dismiss after 4.5 seconds
                PauseTransition delay = new PauseTransition(Duration.seconds(4.5));
                delay.setOnFinished(e -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(fe -> popup.hide());
                    fadeOut.play();
                });
                delay.play();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
