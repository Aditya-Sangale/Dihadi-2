package com.dihadi.view;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

/**
 * Standard default system notification pop-up dialogs using JavaFX Alert.
 */
public class NotificationToast {

    public enum ToastType {
        SUCCESS,
        INFO,
        ACTION,
        ALERT,
        ERROR
    }

    public static void show(Node anyNodeInScene, String title, String message, ToastType type) {
        if (anyNodeInScene == null) return;
        Scene scene = anyNodeInScene.getScene();
        if (scene == null) return;
        show(scene.getWindow(), title, message, type);
    }

    public static void show(Window window, String title, String message, ToastType type) {
        Platform.runLater(() -> {
            try {
                AlertType alertType = AlertType.INFORMATION;
                if (type == ToastType.ERROR) {
                    alertType = AlertType.ERROR;
                } else if (type == ToastType.ALERT) {
                    alertType = AlertType.WARNING;
                }

                Alert alert = new Alert(alertType);
                if (window != null) {
                    alert.initOwner(window);
                }
                alert.setTitle(title != null ? title : "Notification");
                alert.setHeaderText(null);
                alert.setContentText(message != null ? message : title);
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void show(Node anyNode, String title, ToastType type) {
        show(anyNode, title, "", type);
    }

    public static void show(Window window, String title, ToastType type) {
        show(window, title, "", type);
    }
}
