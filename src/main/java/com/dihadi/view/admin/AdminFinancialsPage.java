package com.dihadi.view.admin;

import com.dihadi.view.NotificationToast;
import com.dihadi.view.ScrollUtils;
import com.dihadi.view.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Real-time Admin Financials & Escrow Overview Page.
 * Uses Command Control layout and sidebar.
 */
public class AdminFinancialsPage {
    private static final String GOLD = "#735c00";
    private static final String DARK = "#272727";
    private static final String MAIN = "#fffcf3";
    private static final String BORDER = "#e0d7c7";

    private Timeline clock;
    private TextField searchField;
    private ComboBox<String> typeCombo;
    private ComboBox<String> statusCombo;
    private VBox transactionsContainer;

    public record FinancialTransaction(
            String txnId,
            String projectName,
            String contractor,
            String type,
            String amount,
            String date,
            String status,
            String notes
    ) {}

    private final List<FinancialTransaction> allTransactions = new ArrayList<>();

    public Scene getFinancialsScene(Runnable dashboardAction, Runnable logout) {
        BorderPane layout = new BorderPane();
        layout.setLeft(AdminSidebar.create(AdminSidebar.Category.FINANCIALS, logout, this::stopTimers));
        layout.setCenter(mainContent(logout));

        loadTransactions();

        return new Scene(layout, 1400, 780);
    }

    private void stopTimers() {
        if (clock != null) {
            clock.stop();
        }
    }

    private BorderPane mainContent(Runnable logout) {
        String adminName = SessionManager.getAdminDisplayName();

        Label nameLbl = label(adminName, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        Label sep = label("   >   ", "-fx-font-size:15px;-fx-text-fill:#8c7b6d;");
        Label pageLbl = label("Financials & Escrow", "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button signOutBtn = new Button("Sign Out");
        signOutBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:20px;-fx-border-color:#ffcdd2;-fx-border-radius:20px;-fx-text-fill:#ba1a1a;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
        signOutBtn.setOnAction(e -> {
            stopTimers();
            SessionManager.clearAllSessions();
            Stage stage = (signOutBtn.getScene() != null && signOutBtn.getScene().getWindow() instanceof Stage s) ? s : null;
            if (stage == null) {
                for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                    if (w instanceof Stage s && s.isShowing()) {
                        stage = s;
                        break;
                    }
                }
            }
            NotificationToast.show("Signed Out", "You have signed out of your administrator session.", NotificationToast.ToastType.INFO);
            if (stage != null) {
                final Stage finalStage = stage;
                stage.setScene(new AdminHomePage().getAdminHomeScene(() -> com.dihadi.view.AppNavigator.open(finalStage, "Home")));
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
                heading(),
                kpiRow(),
                filterSearchBar(),
                transactionsSection()
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

    private VBox heading() {
        Label title = label("Financial & Escrow Management", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        Label subtitle = label("Comprehensive audit of platform transactions, project escrow deposits, daily wage disbursals, and contractor settlements.", "-fx-font-size:14px;-fx-text-fill:#6b6359;");
        return new VBox(6, title, subtitle);
    }

    private GridPane kpiRow() {
        GridPane grid = grid(4);
        grid.setHgap(20);

        grid.add(kpiCard("₹24,85,000", "Total Escrow Reserves", "Secured across 42 active construction projects", "#107c41"), 0, 0);
        grid.add(kpiCard("₹12,40,500", "Disbursed Wages (MTD)", "100% Direct bank transfer & UPI settlements", GOLD), 1, 0);
        grid.add(kpiCard("₹1,85,200", "Pending Clearance", "Under 24h banking settlement verification", "#d97706"), 2, 0);
        grid.add(kpiCard("99.8%", "Settlement Success Rate", "Guaranteed escrow protection & anti-fraud audits", "#2563eb"), 3, 0);

        return grid;
    }

    private VBox kpiCard(String value, String title, String subtitle, String accentColor) {
        Label valLbl = label(value, "-fx-font-size:24px;-fx-font-weight:900;-fx-text-fill:" + accentColor + ";");
        Label titleLbl = label(title, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#3b332b;");
        Label subLbl = label(subtitle, "-fx-font-size:11px;-fx-text-fill:#7e7467;");
        subLbl.setWrapText(true);

        VBox card = new VBox(4, valLbl, titleLbl, subLbl);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(cardStyle());
        return card;
    }

    private HBox filterSearchBar() {
        searchField = new TextField();
        searchField.setPromptText("Search project, contractor, or transaction ID...");
        searchField.setPrefWidth(320);
        searchField.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:9px 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("All Types", "Escrow Deposit", "Wage Disbursal", "Advance Release", "Contractor Settlement");
        typeCombo.setValue("All Types");
        typeCombo.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-font-size:13px;");
        typeCombo.setOnAction(e -> applyFilters());

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("All Statuses", "Completed", "In Escrow", "Processing", "Under Audit");
        statusCombo.setValue("All Statuses");
        statusCombo.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-font-size:13px;");
        statusCombo.setOnAction(e -> applyFilters());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export Audit Log");
        exportBtn.setStyle("-fx-background-color:" + GOLD + ";-fx-text-fill:white;-fx-background-radius:10px;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        exportBtn.setOnAction(e -> NotificationToast.show("Export Complete", "Financial transaction log exported to audit reports.", NotificationToast.ToastType.SUCCESS));

        HBox bar = new HBox(12, searchField, typeCombo, statusCombo, spacer, exportBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 20, 16, 20));
        bar.setStyle(cardStyle());
        return bar;
    }

    private VBox transactionsSection() {
        transactionsContainer = new VBox(12);
        return transactionsContainer;
    }

    private void loadTransactions() {
        allTransactions.clear();
        allTransactions.add(new FinancialTransaction("TXN-2026-8910", "Metro Corridor Phase 3", "L&T Infrastructure Ltd.", "Escrow Deposit", "₹4,50,000", "06 Sep 2026", "Completed", "Milestone 2 Worker Escrow Fund locked in reserve."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8909", "Godrej Woods Residential", "Apex Building Solutions", "Wage Disbursal", "₹1,24,000", "06 Sep 2026", "Completed", "Bi-weekly wage release for 48 verified masons and fitters."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8908", "Phoenix Marketcity Expansion", "Shapoorji Pallonji Co.", "Escrow Deposit", "₹6,80,000", "05 Sep 2026", "Completed", "Full project advance escrow holding."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8907", "Smart Highway Overpass", "InfraBuild Ventures", "Advance Release", "₹85,000", "05 Sep 2026", "In Escrow", "Pre-mobilization worker safety gear and equipment allowance."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8906", "Tech Park Tower B", "Buildcon Projects Ltd.", "Wage Disbursal", "₹2,10,000", "04 Sep 2026", "Completed", "Automated daily wage settlement via UPI bank gateway."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8905", "Railway Station Redevelopment", "National Constr. Corp", "Contractor Settlement", "₹3,40,000", "03 Sep 2026", "Under Audit", "Phase 1 completion inspection and final contractor payout reconciliation."));
        allTransactions.add(new FinancialTransaction("TXN-2026-8904", "Green Valley Villas", "Prestige Estates", "Wage Disbursal", "₹96,500", "02 Sep 2026", "Completed", "Disbursed to 26 carpenters and electricians."));

        applyFilters();
    }

    private void applyFilters() {
        if (transactionsContainer == null) return;
        transactionsContainer.getChildren().clear();

        String q = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String selType = typeCombo != null ? typeCombo.getValue() : "All Types";
        String selStatus = statusCombo != null ? statusCombo.getValue() : "All Statuses";

        int matches = 0;
        for (FinancialTransaction txn : allTransactions) {
            boolean matchQ = q.isEmpty()
                    || txn.projectName().toLowerCase().contains(q)
                    || txn.contractor().toLowerCase().contains(q)
                    || txn.txnId().toLowerCase().contains(q);

            boolean matchType = "All Types".equals(selType) || txn.type().equalsIgnoreCase(selType);
            boolean matchStatus = "All Statuses".equals(selStatus) || txn.status().equalsIgnoreCase(selStatus);

            if (matchQ && matchType && matchStatus) {
                transactionsContainer.getChildren().add(renderTransactionCard(txn));
                matches++;
            }
        }

        if (matches == 0) {
            Label empty = label("No financial records match the selected search criteria.", "-fx-font-size:14px;-fx-text-fill:#8c7b6d;-fx-padding:30px;");
            transactionsContainer.getChildren().add(empty);
        }
    }

    private VBox renderTransactionCard(FinancialTransaction txn) {
        Label idLbl = label(txn.txnId(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-background-color:#f6f1e3;-fx-padding:4px 8px;-fx-background-radius:6px;");
        Label dateLbl = label(txn.date(), "-fx-font-size:12px;-fx-text-fill:#8c7b6d;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        String statusBg = switch (txn.status()) {
            case "Completed" -> "#dcfce7";
            case "In Escrow" -> "#fef9c3";
            case "Under Audit" -> "#fee2e2";
            default -> "#f1f5f9";
        };
        String statusText = switch (txn.status()) {
            case "Completed" -> "#15803d";
            case "In Escrow" -> "#a16207";
            case "Under Audit" -> "#b91c1c";
            default -> "#475569";
        };

        Label statusLbl = label(txn.status().toUpperCase(), "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + statusText + ";-fx-background-color:" + statusBg + ";-fx-padding:5px 10px;-fx-background-radius:12px;");

        HBox topRow = new HBox(12, idLbl, dateLbl, sp1, statusLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label projectLbl = label(txn.projectName(), "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        Label contractorLbl = label("Contractor: " + txn.contractor(), "-fx-font-size:13px;-fx-text-fill:#574e44;");

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Label amountLbl = label(txn.amount(), "-fx-font-size:18px;-fx-font-weight:900;-fx-text-fill:#1A1A1A;");
        Label typeLbl = label(txn.type(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";");
        VBox rightAmount = new VBox(2, amountLbl, typeLbl);
        rightAmount.setAlignment(Pos.CENTER_RIGHT);

        HBox middleRow = new HBox(12, new VBox(3, projectLbl, contractorLbl), sp2, rightAmount);
        middleRow.setAlignment(Pos.CENTER_LEFT);

        Label notesLbl = label("Notes: " + txn.notes(), "-fx-font-size:12px;-fx-text-fill:#7e7467;");

        VBox card = new VBox(10, topRow, middleRow, notesLbl);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(cardStyle());
        return card;
    }

    private HBox status() {
        Label time = label("", "-fx-font-family:Consolas;-fx-font-size:12px;-fx-text-fill:#dedbd5;");
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> time.setText("System Time: " + ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("hh:mm:ss a 'IST'")))), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        HBox footer = new HBox(time);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(0, 28, 0, 28));
        footer.setPrefHeight(36);
        footer.setStyle("-fx-background-color:" + DARK + ";");
        return footer;
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

    private Label label(String text, String style) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return l;
    }

    private String cardStyle() {
        return "-fx-background-color:#FFFFFF;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);";
    }
}
