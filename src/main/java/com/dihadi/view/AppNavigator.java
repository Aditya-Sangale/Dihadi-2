package com.dihadi.view;

import com.dihadi.view.worker.WokerSignUp;
import com.dihadi.view.recruiter.SignUpRecruiter;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import javafx.stage.Stage;

/** Shared navigation and account actions for every DIHADI page. */
public final class AppNavigator {
    private AppNavigator() { }

    public static void open(Stage stage, String destination) {
        switch (destination) {
            case "Home" -> stage.setScene(new HomePage(stage).getHomeScene());
            case "Business" -> stage.setScene(new BusinessPage().getBusinessScene(
                    () -> open(stage, "Home"), () -> open(stage, "Worker")));
            case "Worker" -> signUp(stage, null);
            case "About Us" -> stage.setScene(new AboutUs().getAboutScene(
                    () -> open(stage, "Home"), () -> open(stage, "Worker")));
            case "Contact Us" -> stage.setScene(new ContactUs().getContactScene(
                    () -> open(stage, "Home"), () -> open(stage, "Business"),
                    () -> open(stage, "Worker"), () -> open(stage, "About Us")));
            case "Recruiter" -> stage.setScene(new SignUpRecruiter().getRecruiterSignUpScene(
                    () -> open(stage, "Home")));
            default -> throw new IllegalArgumentException("Unknown destination: " + destination);
        }
    }

    public static void signUp(Stage stage, Runnable returnAction) {
        javafx.scene.Scene sourceScene = stage.getScene();
        stage.setScene(new WokerSignUp().getSignUpScene(() -> stage.setScene(sourceScene)));
    }

    public static void login() {
        for (Window window : Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                signUp(stage, null);
                return;
            }
        }
    }

    /** Wires a page-specific header to the same destination map used by the main pages. */
    public static void activateNavigation(HBox navigation) {
        for (javafx.scene.Node node : navigation.getChildren()) {
            if (node instanceof Button button) {
                button.setOnAction(event -> open((Stage) button.getScene().getWindow(), button.getText()));
            }
        }
    }

    public static void information(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(text); alert.show();
    }

    /** Header account controls are reserved for the upcoming admin portal. */
    public static void adminLoginInProgress() {
        information("Admin Login", "Admin login is in progress. This portal will be available soon.");
    }

    /** Shared destinations for the standard footer links on every page. */
    public static void openFooterLink(Stage stage, String link) {
        switch (link) {
            case "About Dihadi" -> open(stage, "About Us");
            case "Contact Us" -> open(stage, "Contact Us");
            case "Find Work" -> open(stage, "Worker");
            case "Worker Categories" -> stage.setScene(new WorkerPage().getWorkerScene(
                    () -> open(stage, "Home"), () -> open(stage, "About Us")));
            default -> information(link, link + " is coming soon.");
        }
    }
}
