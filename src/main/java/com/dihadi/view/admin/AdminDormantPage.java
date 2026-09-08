package com.dihadi.view.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.dihadi.view.NotificationToast;
import com.dihadi.view.ScrollUtils;
import com.dihadi.view.SessionManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Dedicated Admin Dormant Records & Archives Category Page.
 * Displays all deleted and archived cards across Workers, Recruiters, Projects, and Grievances.
 * Provides live filtering, comprehensive detail inspection, and active restoration capabilities.
 */
public class AdminDormantPage {
    private static final String DARK = "#272727", GOLD = "#D4AF37", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private Timeline clock;
    private StackPane rootStack;
    private StackPane modalContainer;
    private GridPane cardsGrid;
    private TextField searchField;
    private ComboBox<String> categoryCombo;

    private Label totalDormantKpi;
    private Label workersDormantKpi;
    private Label recruitersDormantKpi;
    private Label projectsGrievancesKpi;

    public Scene getDormantScene(Runnable dashboardAction) {
        return getDormantScene(dashboardAction, dashboardAction);
    }

    public Scene getDormantScene(Runnable dashboardAction, Runnable logout) {
        BorderPane layout = new BorderPane();
        layout.setLeft(AdminSidebar.create(AdminSidebar.Category.DORMANT, logout, this::stopTimers));
        layout.setCenter(mainContent(logout));

        modalContainer = new StackPane();
        modalContainer.setPickOnBounds(false);
        modalContainer.setVisible(false);

        rootStack = new StackPane(layout, modalContainer);

        DormantManager.getInstance().addListener(this::refreshCardsAndKpis);
        refreshCardsAndKpis();

        return new Scene(rootStack, 1400, 780);
    }

    private void stopTimers() {
        if (clock != null) clock.stop();
        DormantManager.getInstance().removeListener(this::refreshCardsAndKpis);
    }

    private BorderPane mainContent(Runnable logout) {
        String adminName = SessionManager.getAdminDisplayName();

        Label nameLbl = label(adminName, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        Label sep = label("   >   ", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#8c7b6d;");
        Label pageLbl = label("Dormant Archives", "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button signOutBtn = new Button("Sign Out");
        signOutBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:20px;-fx-border-color:#ffcdd2;-fx-border-radius:20px;-fx-text-fill:#ba1a1a;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
        signOutBtn.setOnAction(e -> {
            stopTimers();
            SessionManager.clearAllSessions();
            Stage stage = (signOutBtn.getScene() != null && signOutBtn.getScene().getWindow() instanceof Stage s) ? s : null;
            if (stage != null) {
                stage.setScene(new AdminHomePage().getAdminHomeScene(() -> com.dihadi.view.AppNavigator.open(stage, "Home")));
            } else if (logout != null) {
                logout.run();
            }
        });

        HBox breadcrumb = new HBox(12, nameLbl, sep, pageLbl, spacer, signOutBtn);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        breadcrumb.setPadding(new Insets(0, 40, 0, 40));
        breadcrumb.setPrefHeight(75);
        breadcrumb.setStyle("-fx-background-color:" + MAIN + ";-fx-border-color:" + BORDER + "80;-fx-border-width:0 0 1px 0;");

        VBox content = new VBox(24,
                heroBanner(),
                kpiSection(),
                filterBar(),
                cardsSection()
        );
        content.setPadding(new Insets(32, 40, 52, 40));
        content.setMaxWidth(1300);

        ScrollPane scroll = new ScrollPane(content);
        ScrollUtils.style(scroll);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:" + MAIN + ";-fx-border-width:0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(breadcrumb);
        page.setBottom(status());
        page.setStyle("-fx-background-color:" + MAIN + ";");
        return page;
    }

    private VBox heroBanner() {
        Label title = label("Dormant Records & Archives",
                "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#ffffff;");

        Label subtitle = label("Historical registry of all deleted, decommissioned, and archived cards across Workers, Recruiters, Projects, and Grievances. Cards here are preserved for compliance, review, or reactivation.",
                "-fx-font-size:14px;-fx-text-fill:#d4cebe;");
        subtitle.setWrapText(true);

        VBox banner = new VBox(8, title, subtitle);
        banner.setPadding(new Insets(26, 32, 26, 32));
        banner.setStyle("-fx-background-color:linear-gradient(to right, #241e17, #3b3225);-fx-background-radius:14px;-fx-border-color:" + GOLD + "50;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.20),18,0,0,6);");
        return banner;
    }

    private GridPane kpiSection() {
        GridPane grid = grid(4);
        grid.setHgap(18);

        totalDormantKpi = new Label("0");
        workersDormantKpi = new Label("0");
        recruitersDormantKpi = new Label("0");
        projectsGrievancesKpi = new Label("0");

        grid.add(kpiCard("TOTAL DORMANT", totalDormantKpi, "Preserved Records", "#735c00"), 0, 0);
        grid.add(kpiCard("DELETED WORKERS", workersDormantKpi, "Archived Profiles", "#92400e"), 1, 0);
        grid.add(kpiCard("DELETED RECRUITERS", recruitersDormantKpi, "Dormant Organizations", "#1e40af"), 2, 0);
        grid.add(kpiCard("PROJECTS & CASES", projectsGrievancesKpi, "Decommissioned Sites & Cases", "#065f46"), 3, 0);

        return grid;
    }

    private VBox kpiCard(String title, Label valLabel, String sub, String accentColor) {
        Label t = label(title, "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:" + accentColor + ";");
        valLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        Label s = label(sub, "-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#7e7467;");

        VBox card = new VBox(4, t, valLabel, s);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);");
        return card;
    }

    private HBox filterBar() {
        searchField = new TextField();
        searchField.setPromptText("Search deleted cards by title, ID, trade, location");
        searchField.setPrefWidth(420);
        searchField.setStyle("-fx-background-color:#ffffff;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:9px;-fx-background-radius:9px;-fx-padding:10px 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());

        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("All Categories", "Workers", "Recruiters", "Projects", "Grievances");
        categoryCombo.setValue("All Categories");
        categoryCombo.setStyle("-fx-background-color:#ffffff;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:9px;-fx-background-radius:9px;-fx-font-size:13px;");
        categoryCombo.valueProperty().addListener((obs, o, n) -> applyFilters());

        Button refreshBtn = new Button("Clear Filters");
        refreshBtn.setStyle("-fx-background-color:#faf5eb;-fx-border-color:" + BORDER + ";-fx-border-radius:9px;-fx-background-radius:9px;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:9px 16px;-fx-cursor:hand;");
        refreshBtn.setOnAction(e -> {
            searchField.clear();
            categoryCombo.setValue("All Categories");
            applyFilters();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(14, searchField, categoryCombo, spacer, refreshBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:12px;");
        return bar;
    }

    private VBox cardsSection() {
        cardsGrid = new GridPane();
        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(50.0);
            col.setHgrow(Priority.ALWAYS);
            cardsGrid.getColumnConstraints().add(col);
        }

        VBox container = new VBox(12, cardsGrid);
        return container;
    }

    private void refreshCardsAndKpis() {
        Platform.runLater(() -> {
            List<DormantManager.DormantItem> all = DormantManager.getInstance().getAllDormantItems();
            int total = all.size();
            long workers = all.stream().filter(i -> "Worker".equalsIgnoreCase(i.category())).count();
            long recruiters = all.stream().filter(i -> "Recruiter".equalsIgnoreCase(i.category())).count();
            long rest = total - workers - recruiters;

            if (totalDormantKpi != null) totalDormantKpi.setText(String.valueOf(total));
            if (workersDormantKpi != null) workersDormantKpi.setText(String.valueOf(workers));
            if (recruitersDormantKpi != null) recruitersDormantKpi.setText(String.valueOf(recruiters));
            if (projectsGrievancesKpi != null) projectsGrievancesKpi.setText(String.valueOf(rest));

            applyFilters();
        });
    }

    private void applyFilters() {
        if (cardsGrid == null) return;
        cardsGrid.getChildren().clear();

        String q = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String catFilter = categoryCombo != null ? categoryCombo.getValue() : "All Categories";

        List<DormantManager.DormantItem> all = DormantManager.getInstance().getAllDormantItems();
        int index = 0;

        for (DormantManager.DormantItem item : all) {
            boolean matchCat = "All Categories".equals(catFilter)
                    || ("Workers".equals(catFilter) && "Worker".equalsIgnoreCase(item.category()))
                    || ("Recruiters".equals(catFilter) && "Recruiter".equalsIgnoreCase(item.category()))
                    || ("Projects".equals(catFilter) && "Project".equalsIgnoreCase(item.category()))
                    || ("Grievances".equals(catFilter) && "Grievance".equalsIgnoreCase(item.category()));

            boolean matchQ = q.isEmpty()
                    || item.title().toLowerCase().contains(q)
                    || item.subtitle().toLowerCase().contains(q)
                    || item.identifierText().toLowerCase().contains(q)
                    || item.location().toLowerCase().contains(q)
                    || item.category().toLowerCase().contains(q);

            if (matchCat && matchQ) {
                VBox card = renderDormantCard(item);
                int col = index % 2;
                int row = index / 2;
                cardsGrid.add(card, col, row);
                GridPane.setHgrow(card, Priority.ALWAYS);
                index++;
            }
        }

        if (index == 0) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50, 20, 50, 20));
            Label emptyLbl = label("No dormant records match your filter criteria.", "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#8c7b6d;");
            Label subEmpty = label("When items are deleted from Workers, Recruiters, Projects, or Grievances, they appear here.", "-fx-font-size:13px;-fx-text-fill:#9c8c7c;");
            emptyBox.getChildren().addAll(emptyLbl, subEmpty);
            cardsGrid.add(emptyBox, 0, 0, 2, 1);
        }
    }

    /**
     * Renders a clean, sufficient information card for each Dormant item.
     * Shows key highlights while removing noise, with Restore, Inspect, and Purge actions.
     */
    private VBox renderDormantCard(DormantManager.DormantItem item) {
        // Category Badge Color Styling
        String catColor = "#735c00";
        String catBg = "#fff8e1";
        if ("Recruiter".equalsIgnoreCase(item.category())) {
            catColor = "#1e40af";
            catBg = "#eff6ff";
        } else if ("Project".equalsIgnoreCase(item.category())) {
            catColor = "#065f46";
            catBg = "#ecfdf5";
        } else if ("Grievance".equalsIgnoreCase(item.category())) {
            catColor = "#991b1b";
            catBg = "#fef2f2";
        }

        Label catBadge = label(item.category().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + catColor + ";-fx-background-color:" + catBg + ";-fx-background-radius:6px;-fx-padding:3px 9px;");

        Label dormantBadge = label("● DORMANT ARCHIVE",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;-fx-background-color:#ffebee;-fx-background-radius:6px;-fx-padding:3px 8px;-fx-border-color:#ffcdd2;-fx-border-radius:6px;");

        Label dateLbl = label("Archived: " + item.dateDeleted(), "-fx-font-family:Consolas;-fx-font-size:11px;-fx-text-fill:#8c7b6d;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        HBox topStrip = new HBox(8, catBadge, dormantBadge, topSpacer, dateLbl);
        topStrip.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = label(item.title(), "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLabel.setWrapText(true);
        titleLabel.setTextOverrun(OverrunStyle.CLIP);
        Label subtitleLabel = label(item.subtitle(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setTextOverrun(OverrunStyle.CLIP);
        Label metaLabel = label(item.identifierText() + "   •   Location: " + item.location(), "-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#7e7467;");
        metaLabel.setWrapText(true);
        metaLabel.setTextOverrun(OverrunStyle.CLIP);

        // Two primary highlights (clean & sufficient info)
        HBox block1 = dataHighlightBlock(item.highlight1Title(), item.highlight1Val(), PRIMARY);
        HBox block2 = dataHighlightBlock(item.highlight2Title(), item.highlight2Val(), "#1A1A1A");
        HBox dataStrip = new HBox(10, block1, block2);
        dataStrip.setAlignment(Pos.CENTER_LEFT);

        // Action Controls
        Button restoreBtn = new Button("Restore to Active");
        restoreBtn.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-text-fill:#1b5e20;-fx-border-color:#c8e6c9;-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        restoreBtn.setOnAction(e -> confirmAndRestore(item));

        Button inspectBtn = new Button("Inspect Details ->");
        inspectBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-border-color:" + GOLD + ";-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        inspectBtn.setOnAction(e -> openDormantDetailsModal(item));

        Button purgeDustbinBtn = DormantManager.createDustbinButton("Permanently Delete / Purge Record", () -> confirmAndPurge(item));

        Region btmSpacer = new Region();
        HBox.setHgrow(btmSpacer, Priority.ALWAYS);

        HBox btmRow = new HBox(8, btmSpacer, restoreBtn, inspectBtn, purgeDustbinBtn);
        btmRow.setAlignment(Pos.CENTER_RIGHT);
        btmRow.setPadding(new Insets(6, 0, 0, 0));
        btmRow.setStyle("-fx-border-color:" + BORDER + "60;-fx-border-width:1px 0 0 0;");

        VBox card = new VBox(10, topStrip, titleLabel, subtitleLabel, metaLabel, dataStrip, btmRow);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(18, 20, 16, 20));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);-fx-cursor:hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + GOLD + ";-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.28),16,0,0,5px);-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);"));

        card.setOnMouseClicked(e -> {
            if (e.getTarget() != restoreBtn && e.getTarget() != inspectBtn && e.getTarget() != purgeDustbinBtn) {
                openDormantDetailsModal(item);
            }
        });

        return card;
    }

    private HBox dataHighlightBlock(String title, String val, String color) {
        Label t = label(title + ": ", "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#685c52;");
        Label v = label(val, "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + color + ";");
        HBox box = new HBox(2, t, v);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(4, 8, 4, 8));
        box.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:6px;-fx-border-color:#ebdccb;-fx-border-radius:6px;");
        return box;
    }

    private void confirmAndRestore(DormantManager.DormantItem item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Record Restoration");
        confirm.setHeaderText("Restore " + item.category() + ": " + item.title() + "?");
        confirm.setContentText("This will reactivate this " + item.category() + " record and restore it from Dormant archives to the active " + item.category() + " directory.");

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                DormantManager.getInstance().removeDormantItem(item.id());
                NotificationToast.show("Restored Successfully", item.title() + " has been restored to active " + item.category() + " records.", NotificationToast.ToastType.SUCCESS);
            }
        });
    }

    private void confirmAndPurge(DormantManager.DormantItem item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Permanent Purge");
        confirm.setHeaderText("Permanently Delete " + item.title() + "?");
        confirm.setContentText("WARNING: This will permanently delete this record from the dormant archives and database. This action cannot be undone.");

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                DormantManager.getInstance().removeDormantItem(item.id());
                NotificationToast.show("Record Purged", item.title() + " permanently purged from archives.", NotificationToast.ToastType.INFO);
            }
        });
    }

    private void openDormantDetailsModal(DormantManager.DormantItem item) {
        modalContainer.getChildren().clear();
        modalContainer.setPickOnBounds(true);
        modalContainer.setVisible(true);

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color:rgba(18, 15, 12, 0.70);");
        backdrop.setOnMouseClicked(e -> closeModal());

        Label catBadge = label("ARCHIVED " + item.category().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#272727;-fx-background-radius:6px;-fx-padding:4px 10px;");
        Label statusBadge = label("DORMANT",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-background-color:#ba1a1a;-fx-background-radius:6px;-fx-padding:4px 10px;");

        HBox topBadges = new HBox(8, catBadge, statusBadge);
        topBadges.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = label(item.title(), "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLbl.setWrapText(true);
        titleLbl.setTextOverrun(OverrunStyle.CLIP);
        Label subLbl = label(item.subtitle() + "   |   " + item.identifierText() + "   |   Location: " + item.location(),
                "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        subLbl.setWrapText(true);
        subLbl.setTextOverrun(OverrunStyle.CLIP);
        VBox titleBox = new VBox(6, topBadges, titleLbl, subLbl);

        Button restoreModalBtn = new Button("Restore to Active");
        restoreModalBtn.setStyle("-fx-background-color:#1b5e20;-fx-background-radius:10px;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 18px;-fx-cursor:hand;");
        restoreModalBtn.setOnAction(e -> {
            closeModal();
            confirmAndRestore(item);
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-text-fill:#1A1A1A;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8px 16px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;");
        closeBtn.setOnAction(e -> closeModal());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(14, titleBox, spacer, restoreModalBtn, closeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 16, 0));
        topBar.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1.5px 0;");

        VBox detailBox = new VBox(12);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        Label heading = label("Preserved Archive Records & History", "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        Label summaryText = label(item.detailsSummary(), "-fx-font-family:Consolas;-fx-font-size:13px;-fx-text-fill:#383129;");
        summaryText.setWrapText(true);
        summaryText.setTextOverrun(OverrunStyle.CLIP);

        detailBox.getChildren().addAll(heading, summaryText);

        VBox modalCard = new VBox(18, topBar, detailBox);
        modalCard.setPrefWidth(780);
        modalCard.setMaxWidth(780);
        modalCard.setPadding(new Insets(26, 30, 26, 30));
        modalCard.setStyle("-fx-background-color:linear-gradient(to bottom, #ffffff, #fcf8f0);-fx-background-radius:20px;-fx-border-color:" + GOLD + ";-fx-border-width:2px;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,.50),28,0,0,10px);");

        modalContainer.getChildren().addAll(backdrop, modalCard);
    }

    private void closeModal() {
        modalContainer.getChildren().clear();
        modalContainer.setVisible(false);
        modalContainer.setPickOnBounds(false);
    }

    private HBox status() {
        Label time = label("", "-fx-font-family:Consolas;-fx-font-size:11px;-fx-text-fill:#dedbd5;");
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> time.setText("System Time: " + ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("hh:mm:ss a 'IST'")))), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        HBox footer = new HBox(28, health("Database: OK"), health("Archive Registry: OK"), health("Security Vault: OK"), spacer(), time);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(0, 28, 0, 28));
        footer.setPrefHeight(36);
        footer.setStyle("-fx-background-color:" + DARK + ";");
        return footer;
    }

    private HBox health(String value) {
        HBox item = new HBox(8, new Circle(4, Color.web("#22c55e")), label(value, "-fx-font-family:Consolas;-fx-font-size:11px;-fx-text-fill:#dedbd5;"));
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private GridPane grid(int columns) {
        GridPane grid = new GridPane();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / columns);
            col.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(col);
        }
        return grid;
    }

    private Label label(String value, String style) {
        Label label = new Label(value);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
