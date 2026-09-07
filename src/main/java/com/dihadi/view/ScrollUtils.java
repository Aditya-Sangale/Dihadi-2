package com.dihadi.view;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Utility to eliminate vertical scrollbars from the right side across all DIHADI pages
 * while ensuring full and smooth scrollability via mouse wheel, trackpad, touch, and keys.
 */
public final class ScrollUtils {
    private static String stylesheetUrl;

    static {
        try {
            java.net.URL res = ScrollUtils.class.getResource("/styles/scrollbar.css");
            if (res != null) {
                stylesheetUrl = res.toExternalForm();
            }
        } catch (Exception ignored) {}
    }

    private ScrollUtils() {}

    public static String getStylesheetUrl() {
        return stylesheetUrl;
    }

    /**
     * Styles and configures a ScrollPane with hidden vertical/horizontal scrollbars,
     * maintaining full responsiveness to scroll gestures.
     */
    public static ScrollPane style(ScrollPane scroll) {
        if (scroll == null) return null;
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        if (stylesheetUrl != null && !scroll.getStylesheets().contains(stylesheetUrl)) {
            scroll.getStylesheets().add(stylesheetUrl);
        }
        return scroll;
    }

    /**
     * Applies the universal scrollbar stylesheet to any Scene.
     */
    public static void applyToScene(Scene scene) {
        if (scene != null && stylesheetUrl != null && !scene.getStylesheets().contains(stylesheetUrl)) {
            scene.getStylesheets().add(stylesheetUrl);
        }
    }

    /**
     * Initializes global tracking on a primary Stage and all active Windows
     * so every scene automatically inherits the clean scrollbar styling.
     */
    public static void initGlobal(Stage stage) {
        if (stage != null) {
            stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                applyToScene(newScene);
            });
            applyToScene(stage.getScene());
        }

        try {
            Window.getWindows().addListener((ListChangeListener<Window>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (Window w : change.getAddedSubList()) {
                            if (w instanceof Stage s) {
                                s.sceneProperty().addListener((obs, oldScene, newScene) -> {
                                    applyToScene(newScene);
                                });
                                applyToScene(s.getScene());
                            }
                        }
                    }
                }
            });
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage s) {
                    applyToScene(s.getScene());
                }
            }
        } catch (Exception ignored) {}
    }
}
