package com.dihadi.view;

import com.dihadi.view.worker.WokerSignUp;
import com.dihadi.view.recruiter.SignUpRecruiter;
import com.dihadi.view.recruiter.RecruiterPage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import javafx.stage.Stage;

/** Shared navigation and account actions for every DIHADI page. */
public final class AppNavigator {
    private AppNavigator() {
    }

    public static void open(Stage stage, String destination) {
        switch (destination) {
            case "Home" -> stage.setScene(new HomePage(stage).getHomeScene());
            case "Business" -> stage.setScene(new BusinessPage().getBusinessScene(
                    () -> open(stage, "Home"), () -> open(stage, "Worker")));
            case "Worker" -> stage.setScene(new WokerSignUp().getSignUpScene(() -> open(stage, "Home")));
            case "About Us" -> stage.setScene(new AboutUs().getAboutScene(
                    () -> open(stage, "Home"), () -> open(stage, "Worker")));
            case "Contact Us" -> stage.setScene(new ContactUs().getContactScene(
                    () -> open(stage, "Home"), () -> open(stage, "Business"),
                    () -> open(stage, "Worker"), () -> open(stage, "About Us")));
            case "Recruiter" -> stage.setScene(new SignUpRecruiter().getRecruiterSignUpScene(
                    () -> open(stage, "Home")));
            case "Admin" -> stage.setScene(new com.dihadi.view.admin.AdminHomePage().getAdminHomeScene(() -> open(stage, "Home")));
            case "AdminLogin" -> stage.setScene(new com.dihadi.view.admin.AdminLoginPage().getAdminLoginScene(() -> open(stage, "Home")));
            case "Dashboard" -> openDashboard(stage);
            default -> throw new IllegalArgumentException("Unknown destination: " + destination);
        }
    }

    public static void openDashboard(Stage stage) {
        if (SessionManager.currentWorker != null) {
            stage.setScene(new com.dihadi.view.worker.WorkerDashboard(SessionManager.currentWorker).getScene(() -> open(stage, "Home")));
        } else if (SessionManager.currentRecruiter != null) {
            stage.setScene(new com.dihadi.view.recruiter.RecruiterDashboard(SessionManager.currentRecruiter).getScene(() -> open(stage, "Home")));
        } else {
            stage.setScene(new com.dihadi.view.admin.AdminHomePage().getAdminHomeScene(() -> open(stage, "Home")));
        }
    }

    public static Button createHeaderAdminButton() {
        Button btn = new Button("Admin");
        btn.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            open(stage, "Admin");
        });
        return btn;
    }

    public static Button createHeaderActionButton() {
        boolean isLoggedIn = SessionManager.currentWorker != null || SessionManager.currentRecruiter != null;
        Button btn = new Button(isLoggedIn ? "Dashboard" : "Admin");
        btn.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        btn.setOnAction(e -> {
            Stage stage = (Stage) btn.getScene().getWindow();
            if (isLoggedIn) {
                openDashboard(stage);
            } else {
                open(stage, "Admin");
            }
        });
        return btn;
    }

    public static void signUp(Stage stage, Runnable returnAction) {
        javafx.scene.Scene sourceScene = stage.getScene();
        stage.setScene(new WokerSignUp().getSignUpScene(() -> stage.setScene(sourceScene)));
    }

    public static void login() {
        for (Window window : Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                stage.setScene(new com.dihadi.view.worker.WorkerLoginPage(() -> open(stage, "Home")).getLoginScene());
                return;
            }
        }
    }

    /**
     * Wires a page-specific header to the same destination map used by the main
     * pages.
     */
    public static void activateNavigation(HBox navigation) {
        for (javafx.scene.Node node : navigation.getChildren()) {
            if (node instanceof Button button) {
                button.setOnAction(event -> open((Stage) button.getScene().getWindow(), button.getText()));
            }
        }
    }

    public static void information(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.show();
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
