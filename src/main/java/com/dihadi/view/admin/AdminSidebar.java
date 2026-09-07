package com.dihadi.view.admin;

import com.dihadi.view.NotificationToast;
import com.dihadi.view.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Centralized Command Control Sidebar for all Admin interface categories.
 * Ensures consistent layout, branding, account footer, and category-aware active indicators.
 */
public class AdminSidebar {
    public static final String GOLD = "#735c00";
    public static final String DARK = "#272727";

    public enum Category {
        COMMAND_CENTER("Command Center"),
        WORKERS("Workers"),
        RECRUITERS("Recruiters"),
        PROJECTS("Projects"),
        FINANCIALS("Financials"),
        VERIFICATION("Verification"),
        GRIEVANCES("Grievances");

        private final String title;

        Category(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    /**
     * Builds and returns the Command Control sidebar for the specified category.
     *
     * @param activeCategory The current active category to highlight
     * @param logout         Logout action handler
     * @param onCleanup      Cleanup callback to stop timers/pollers on the current page before navigation
     * @return Fully styled and functional VBox sidebar
     */
    public static VBox create(Category activeCategory, Runnable logout, Runnable onCleanup) {
        // Identity Header
        ImageView logo = createLogo();
        Label brandTitle = new Label("DIHADI");
        brandTitle.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-text-fill:" + GOLD + ";");
        Label brandSubtitle = new Label("ADMIN CONTROL CENTER");
        brandSubtitle.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-letter-spacing:1.2px;-fx-text-fill:#dcdad4;");

        VBox identity = new VBox(10, logo, brandTitle, brandSubtitle);
        identity.setAlignment(Pos.CENTER);
        identity.setPadding(new Insets(28, 10, 35, 10));

        // Navigation Links
        VBox links = new VBox(4);
        VBox.setVgrow(links, Priority.ALWAYS);

        for (Category cat : Category.values()) {
            boolean isActive = (cat == activeCategory);
            Button btn = createNavButton(cat.getTitle(), isActive);
            btn.setOnAction(e -> {
                if (isActive) {
                    return; // Already on this category
                }
                if (onCleanup != null) {
                    try {
                        onCleanup.run();
                    } catch (Exception ignored) {}
                }
                Stage stage = (Stage) btn.getScene().getWindow();
                if (stage == null) return;
                navigateTo(stage, cat, logout);
            });
            links.getChildren().add(btn);
        }

        // Account Footer - Just User's Name
        String adminName = SessionManager.getAdminDisplayName();
        Label userLabel = new Label(adminName);
        userLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#f8f0e2;");

        VBox bottom = new VBox(userLabel);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 12, 16, 12));
        bottom.setStyle("-fx-border-color:#ffffff1a;-fx-border-width:1px 0 0 0;");

        VBox bar = new VBox(identity, links, bottom);
        bar.setPrefWidth(312);
        bar.setMinWidth(312);
        bar.setStyle("-fx-background-color:" + DARK + ";");
        return bar;
    }

    private static void navigateTo(Stage stage, Category target, Runnable logout) {
        switch (target) {
            case COMMAND_CENTER:
                stage.setScene(new AdminDashboard().getDashboardScene(logout));
                break;
            case WORKERS:
                stage.setScene(new AdminWorkersPage().getWorkersScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
            case RECRUITERS:
                stage.setScene(new AdminRecruitersPage().getRecruitersScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
            case PROJECTS:
                stage.setScene(new AdminProjectsPage().getProjectsScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
            case FINANCIALS:
                stage.setScene(new AdminFinancialsPage().getFinancialsScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
            case VERIFICATION:
                stage.setScene(new AdminVerificationPage().getVerificationScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
            case GRIEVANCES:
                stage.setScene(new AdminGrievancesPage().getGrievancesScene(
                        () -> stage.setScene(new AdminDashboard().getDashboardScene(logout)),
                        logout));
                break;
        }
    }

    private static Button createNavButton(String title, boolean active) {
        Button button = new Button(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        String activeStyle = "-fx-background-color:" + GOLD + ";-fx-background-radius:9px;-fx-text-fill:white;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:16px 28px;-fx-cursor:hand;";
        String inactiveStyle = "-fx-background-color:transparent;-fx-background-radius:9px;-fx-text-fill:#dcdad4;-fx-font-size:16px;-fx-font-weight:500;-fx-padding:16px 28px;-fx-cursor:hand;";
        String hoverStyle = "-fx-background-color:#ffffff14;-fx-background-radius:9px;-fx-text-fill:#ffffff;-fx-font-size:16px;-fx-font-weight:600;-fx-padding:16px 28px;-fx-cursor:hand;";

        if (active) {
            button.setStyle(activeStyle);
        } else {
            button.setStyle(inactiveStyle);
            button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
            button.setOnMouseExited(e -> button.setStyle(inactiveStyle));
        }

        return button;
    }

    private static ImageView createLogo() {
        try {
            var r = AdminSidebar.class.getResource("/assets/logo/dihadi logo.jpeg");
            if (r != null) {
                ImageView view = new ImageView(new Image(r.toExternalForm()));
                view.setFitWidth(82);
                view.setFitHeight(82);
                view.setPreserveRatio(true);
                return view;
            }
        } catch (Exception ignored) {}
        return new ImageView();
    }
}
