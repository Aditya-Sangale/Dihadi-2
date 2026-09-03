package com.dihadi.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Custom, attractive, and effective notification and modal alert system for DIHADI.
 * Replaces generic OS default alerts with branded, modern floating toasts and modal dialogs.
 */
public class NotificationToast {

    public enum ToastType {
        SUCCESS,
        INFO,
        ACTION,
        ALERT,
        ERROR
    }

    public static Window getActiveWindow() {
        for (Window w : Window.getWindows()) {
            if (w.isFocused() && w.isShowing()) {
                return w;
            }
        }
        for (Window w : Window.getWindows()) {
            if (w.isShowing()) {
                return w;
            }
        }
        return null;
    }

    public static void show(String title, String message, ToastType type) {
        show((Window) null, title, message, type);
    }

    public static void show(Node anyNodeInScene, String title, String message, ToastType type) {
        if (anyNodeInScene != null && anyNodeInScene.getScene() != null) {
            show(anyNodeInScene.getScene().getWindow(), title, message, type);
        } else {
            show((Window) null, title, message, type);
        }
    }

    public static void show(Window window, String title, String message, ToastType type) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(window, title, message, type));
            return;
        }

        Window target = (window != null && window.isShowing()) ? window : getActiveWindow();
        if (target == null) return;

        Popup popup = new Popup();
        popup.setAutoHide(true);

        String accentColor;
        String typeBadgeText;
        switch (type) {
            case SUCCESS -> {
                accentColor = "#2a7e3b";
                typeBadgeText = "SUCCESS";
            }
            case ALERT -> {
                accentColor = "#d97706";
                typeBadgeText = "NOTICE";
            }
            case ERROR -> {
                accentColor = "#dc2626";
                typeBadgeText = "ERROR";
            }
            default -> {
                accentColor = "#d4af37";
                typeBadgeText = "INFO";
            }
        }

        Label badge = new Label(typeBadgeText);
        badge.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:10px;-fx-font-weight:800;"
                + "-fx-text-fill:" + accentColor + ";-fx-background-color:rgba(255,255,255,0.08);"
                + "-fx-background-radius:4px;-fx-padding:2px 6px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-family:'Georgia',serif;-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#ffffff;");

        HBox topRow = new HBox(8, badge, titleLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox textContainer = new VBox(4, topRow);
        if (message != null && !message.isBlank()) {
            Label msgLabel = new Label(message);
            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(380);
            msgLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-text-fill:#e0d9ce;");
            textContainer.getChildren().add(msgLabel);
        }

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#8c8273;-fx-font-size:12px;-fx-font-weight:700;-fx-cursor:hand;");
        closeBtn.setOnAction(e -> popup.hide());

        Region bar = new Region();
        bar.setPrefWidth(4);
        bar.setMinWidth(4);
        bar.setMaxWidth(4);
        bar.setStyle("-fx-background-color:" + accentColor + ";-fx-background-radius:4px;");

        HBox content = new HBox(12, bar, textContainer, closeBtn);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(12, 16, 12, 12));
        content.setMaxWidth(460);
        content.setStyle("-fx-background-color:#1c1914;-fx-background-radius:12px;-fx-border-color:" + accentColor + "88;-fx-border-width:1.2px;-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.45),20,0,0,6);-fx-cursor:hand;");

        content.setOnMouseClicked(e -> popup.hide());

        popup.getContent().add(content);
        popup.show(target);

        double x = target.getX() + (target.getWidth() - 380) / 2;
        double y = target.getY() + 48;
        popup.setX(x);
        popup.setY(y);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), content);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.seconds(3.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), content);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> popup.hide());

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.play();
    }

    public static void show(Node anyNode, String title, ToastType type) {
        show(anyNode, title, "", type);
    }

    public static void show(Window window, String title, ToastType type) {
        show(window, title, "", type);
    }

    /**
     * Attractive, custom modal alert dialog with dark gold DIHADI styling.
     * Replaces standard native JavaFX Alert dialogs with a modern luxury interface.
     */
    public static void showModal(String title, String message, ToastType type) {
        showModal(getActiveWindow(), title, message, type);
    }

    public static void showModal(Window owner, String title, String message, ToastType type) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> showModal(owner, title, message, type));
            return;
        }

        Window targetOwner = (owner != null && owner.isShowing()) ? owner : getActiveWindow();

        Stage modal = new Stage();
        if (targetOwner != null) {
            modal.initOwner(targetOwner);
        }
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        String accentColor = switch (type) {
            case SUCCESS -> "#2a7e3b";
            case ALERT -> "#d4af37";
            case ERROR -> "#dc2626";
            default -> "#d4af37";
        };

        String badgeText = switch (type) {
            case SUCCESS -> "SUCCESS";
            case ALERT -> "SESSION CONFLICT";
            case ERROR -> "ATTENTION REQUIRED";
            default -> "NOTICE";
        };

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;"
                + "-fx-text-fill:" + accentColor + ";-fx-background-color:rgba(212,175,55,0.12);"
                + "-fx-background-radius:6px;-fx-padding:4px 10px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-family:'Georgia',serif;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#ffffff;");
        titleLabel.setWrapText(true);

        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(400);
        msgLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:14px;-fx-text-fill:#ded5c6;-fx-line-spacing:3px;");

        Button okBtn = new Button("Understood");
        okBtn.setStyle("-fx-background-color:#d4af37;-fx-background-radius:10px;-fx-text-fill:#1e1b15;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:11px 28px;-fx-cursor:hand;");
        okBtn.setOnAction(e -> modal.close());

        VBox card = new VBox(16, badge, titleLabel, msgLabel, okBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 36, 28, 36));
        card.setMaxWidth(440);
        card.setStyle("-fx-background-color:#1e1b15;-fx-background-radius:18px;-fx-border-color:" + accentColor + ";-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.65),32,0,0,10);");

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color:rgba(0,0,0,0.55);");
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        if (targetOwner != null) {
            modal.setWidth(targetOwner.getWidth());
            modal.setHeight(targetOwner.getHeight());
            modal.setX(targetOwner.getX());
            modal.setY(targetOwner.getY());
        }

        modal.showAndWait();
    }
}
