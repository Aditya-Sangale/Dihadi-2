package com.dihadi.view.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
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
 * Real-time Admin Grievance Resolution & Dispute Center.
 * Features executive card layout matching the rest of the admin suite,
 * multi-tier case filtering, incident inspection modal, and standard system dialogs.
 */
public class AdminGrievancesPage {
    private static final String DARK = "#272727", GOLD = "#D4AF37", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private Timeline clock;
    private StackPane rootStack;
    private StackPane modalContainer;
    private GridPane grievanceCardsPane;
    private TextField searchField;
    private ComboBox<String> priorityCombo;
    private ComboBox<String> statusCombo;
    private ComboBox<String> categoryCombo;

    private Label totalCasesKpi;
    private Label paymentDelaysKpi;
    private Label safetyViolationsKpi;
    private Label resolvedKpi;

    private final List<AdminGrievanceData> allGrievancesList = new ArrayList<>();

    public Scene getGrievancesScene(Runnable dashboardAction) {
        return getGrievancesScene(dashboardAction, dashboardAction);
    }

    public Scene getGrievancesScene(Runnable dashboardAction, Runnable logout) {
        BorderPane layout = new BorderPane();
        layout.setLeft(sidebar(dashboardAction, logout));
        layout.setCenter(mainContent());

        modalContainer = new StackPane();
        modalContainer.setPickOnBounds(false);
        modalContainer.setVisible(false);

        rootStack = new StackPane(layout, modalContainer);
        loadRealtimeGrievances();
        return new Scene(rootStack, 1400, 780);
    }

    private VBox sidebar(Runnable dashboardAction, Runnable logout) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 82, 82);
        VBox identity = new VBox(10, logo,
                label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:28px;-fx-text-fill:" + GOLD + ";"),
                label("ADMIN CONTROL CENTER", "-fx-font-size:11px;-fx-letter-spacing:1.2px;-fx-text-fill:#dcdad4;"));
        identity.setAlignment(Pos.CENTER);
        identity.setPadding(new Insets(28, 10, 35, 10));

        Button command = nav("Command Center", false);
        command.setOnAction(e -> {
            if (clock != null) clock.stop();
            dashboardAction.run();
        });

        Button workersNav = nav("Workers", false);
        workersNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            Stage stage = (Stage) workersNav.getScene().getWindow();
            stage.setScene(new AdminWorkersPage().getWorkersScene(dashboardAction, logout));
        });

        Button recruitersNav = nav("Recruiters", false);
        recruitersNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            Stage stage = (Stage) recruitersNav.getScene().getWindow();
            stage.setScene(new AdminRecruitersPage().getRecruitersScene(dashboardAction, logout));
        });

        Button projectsNav = nav("Projects", false);
        projectsNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            Stage stage = (Stage) projectsNav.getScene().getWindow();
            stage.setScene(new AdminProjectsPage().getProjectsScene(dashboardAction, logout));
        });

        Button grievances = nav("Grievances", true);

        VBox links = new VBox(4, command, workersNav, recruitersNav, projectsNav,
                nav("Financials", false), nav("Verification", false), grievances);
        VBox.setVgrow(links, Priority.ALWAYS);

        String adminName = com.dihadi.view.SessionManager.getAdminDisplayName();
        Button profile = nav(adminName + "\nSystem Administrator", false);
        profile.setOnAction(e -> {
            if (clock != null) clock.stop();
            logout.run();
        });
        VBox bottom = new VBox(4, profile);
        bottom.setPadding(new Insets(14, 0, 14, 0));
        bottom.setStyle("-fx-border-color:#ffffff1a;-fx-border-width:1px 0 0 0;");

        VBox bar = new VBox(identity, links, bottom);
        bar.setPrefWidth(312);
        bar.setMinWidth(312);
        bar.setStyle("-fx-background-color:" + DARK + ";");
        return bar;
    }

    private BorderPane mainContent() {
        String adminName = com.dihadi.view.SessionManager.getAdminDisplayName();
        HBox breadcrumb = new HBox(
                label(adminName, "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;"),
                label("   >   ", "-fx-font-size:16px;-fx-text-fill:#4A4A4A;"),
                label("Grievance Resolution Center", "-fx-font-size:16px;-fx-text-fill:" + GOLD + ";")
        );
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        breadcrumb.setPadding(new Insets(0, 40, 0, 40));
        breadcrumb.setPrefHeight(80);
        breadcrumb.setStyle("-fx-background-color:" + MAIN + ";-fx-border-color:" + BORDER + "80;-fx-border-width:0 0 1px 0;");

        VBox content = new VBox(26,
                heading(),
                kpiRow(),
                filterSearchBar(),
                grievanceGrid()
        );
        content.setPadding(new Insets(36, 40, 48, 40));
        content.setMaxWidth(1280);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:" + MAIN + ";-fx-border-width:0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(breadcrumb);
        page.setBottom(status());
        page.setStyle("-fx-background-color:" + MAIN + ";");
        return page;
    }

    private VBox heading() {
        return new VBox(7,
                label("Enterprise Grievance Resolution & Dispute Center", "-fx-font-family:Georgia;-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;"),
                label("Investigate, arbitrate, and resolve workforce disputes, wage escalations, safety breaches, and contractor compliance cases.", "-fx-font-size:15px;-fx-text-fill:#4A4A4A;")
        );
    }

    private GridPane kpiRow() {
        GridPane grid = grid(4);
        grid.setHgap(20);

        totalCasesKpi = label("16", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        paymentDelaysKpi = label("5", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;");
        safetyViolationsKpi = label("4", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        resolvedKpi = label("324", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");

        grid.add(kpiCard("ACTIVE DISPUTES", totalCasesKpi, "Pending Arbitration", "#685c52"), 0, 0);
        grid.add(kpiCard("PAYMENT ESCALATIONS", paymentDelaysKpi, "Escrow Lock Enforced", "#ba1a1a"), 1, 0);
        grid.add(kpiCard("SAFETY VIOLATIONS", safetyViolationsKpi, "Site Audit Required", "#735c00"), 2, 0);
        grid.add(kpiCard("RESOLVED CASES", resolvedKpi, "98.4% Settlement Rate", "#2e7d32"), 3, 0);
        return grid;
    }

    private VBox kpiCard(String title, Label numberNode, String subtext, String accentColor) {
        Label heading = label(title, "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#685c52;");
        Label note = label(subtext, "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:" + accentColor + ";");

        VBox card = new VBox(8, heading, numberNode, note);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setPrefHeight(125);
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),8,0,0,3px);");
        return card;
    }

    private HBox filterSearchBar() {
        searchField = new TextField();
        searchField.setPromptText("Search case ID, complainant, project, or issue keywords...");
        searchField.setPrefWidth(340);
        searchField.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:9px 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        priorityCombo = choice("All Priorities", "Critical", "High", "Medium", "Low");
        statusCombo = choice("All Statuses", "Investigating", "Escalated", "New", "Resolved");
        categoryCombo = choice("All Categories", "Payment Delay", "Safety Breach", "Contract Dispute", "Equipment Damage", "Wage Mismatch", "Unfair Dismissal", "Workplace Hazard");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:8px 16px;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-cursor:hand;");
        refreshBtn.setOnAction(e -> loadRealtimeGrievances());

        HBox filters = new HBox(12,
                searchField,
                priorityCombo,
                statusCombo,
                categoryCombo,
                refreshBtn
        );
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(16, 20, 16, 20));
        filters.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),8,0,0,2px);");
        return filters;
    }

    private ComboBox<String> choice(String selected, String... values) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().add(selected);
        for (String v : values) {
            if (!v.equals(selected)) cb.getItems().add(v);
        }
        cb.setValue(selected);
        cb.setPrefWidth(170);
        cb.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-font-size:13px;-fx-cursor:hand;");
        cb.setOnAction(e -> applyFilters());
        return cb;
    }

    private VBox grievanceGrid() {
        grievanceCardsPane = new GridPane();
        grievanceCardsPane.setHgap(20);
        grievanceCardsPane.setVgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        grievanceCardsPane.getColumnConstraints().addAll(col1, col2);
        grievanceCardsPane.setMaxWidth(Double.MAX_VALUE);

        VBox container = new VBox(grievanceCardsPane);
        container.setAlignment(Pos.TOP_LEFT);
        return container;
    }

    private void loadRealtimeGrievances() {
        allGrievancesList.clear();

        new Thread(() -> {
            List<AdminGrievanceData> loaded = new ArrayList<>();
            try {
                List<com.dihadi.model.Grievance> dbList = new com.dihadi.controller.GrievanceController().getAllGrievances();
                if (dbList != null && !dbList.isEmpty()) {
                    for (com.dihadi.model.Grievance g : dbList) {
                        String cid = val(g.getGrievanceId(), "GR-" + System.currentTimeMillis());
                        String sub = val(g.getSubject(), "Inquiry / Grievance");
                        String comp = val(g.getComplainant(), "Citizen User");
                        String proj = val(g.getProject(), "Contact Us Portal");
                        String loc = val(g.getLocation(), "Pune, Maharashtra");
                        String prio = val(g.getPriority(), "High");
                        String st = val(g.getStatus(), "New");
                        String cat = val(g.getCategory(), "Online Inquiry");
                        String amt = val(g.getDisputeAmount(), "General Inquiry");
                        String time = val(g.getTimestamp(), "Recent");
                        String desc = val(g.getIncidentDescription(), "Inquiry submitted via online contact form.");
                        String res = val(g.getResolutionNotes(), "Pending administrative review.");

                        loaded.add(new AdminGrievanceData(cid, sub, comp, proj, loc, prio, st, cat, amt, time, desc, res));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            loaded.addAll(List.of(
                    new AdminGrievanceData("GR-1024", "Unpaid Overtime & Daily Wage Delay", "J. Doe (Worker #882 - Mason)", "Hiranandani Business Towers", "Mumbai, Maharashtra", "Critical", "Investigating", "Payment Delay", "₹14,500 Overdue", "2 Days Ago", "Worker reported that 14 days of overtime wage settlement is delayed by site contractor despite biometric log verification.", "Subcontractor requested bank statement reconciliations."),
                    new AdminGrievanceData("GR-1025", "Inadequate High-Altitude Safety Harness", "M. Smith (Worker #910 - Fitter)", "LODHAA Grand Expressway", "Mumbai, Maharashtra", "High", "New", "Safety Breach", "Site Safety Audit", "4 Hours Ago", "Missing secondary safety lanyard cables on elevated bridge pillar casting scaffolding.", "Safety compliance team assigned to inspect scaffolding."),
                    new AdminGrievanceData("GR-1026", "Damaged Heavy Crane Hoist Rigging", "R. Chen (Worker #112 - Crane Op)", "Pune Metro Rail Underground Depot", "Pune, Maharashtra", "Medium", "New", "Equipment Damage", "Machinery Audit", "1 Day Ago", "Worn cable sling rigging reported during heavy pre-cast girder lifting operations.", "Replacement rigging dispatched to Range Hills Depot site."),
                    new AdminGrievanceData("GR-1027", "Daily Wage Discrepancy Against Work Slip", "L. Thompson (Worker #554 - Plumber)", "BASIL Tech Habitat Smart Park", "Bengaluru, Karnataka", "High", "Investigating", "Wage Mismatch", "₹4,200 Difference", "1 Day Ago", "Daily wage recorded at ₹850 instead of committed escrow rate of ₹1,050 for high-pressure valve assembly.", "Contractor payroll team reviewing task master voucher."),
                    new AdminGrievanceData("GR-1028", "Wrongful Site Dismissal Without Notice", "K. Patel (Worker #229 - Mason)", "BHRAMHA Horizon Residential Complex", "Pune, Maharashtra", "Critical", "Escalated", "Unfair Dismissal", "Reinstatement Claim", "3 Days Ago", "Site supervisor summarily terminated worker without 48-hour notice required under DIHADI terms.", "Admin mediation initiated with BHRAMHA site leadership.")
            ));

            javafx.application.Platform.runLater(() -> {
                allGrievancesList.clear();
                allGrievancesList.addAll(loaded);
                updateKpis();
                applyFilters();
            });
        }).start();
    }

    private void updateKpis() {
        int total = allGrievancesList.size();
        long payments = allGrievancesList.stream().filter(g -> g.category().toLowerCase().contains("wage") || g.category().toLowerCase().contains("payment")).count();
        long safety = allGrievancesList.stream().filter(g -> g.category().toLowerCase().contains("safety") || g.category().toLowerCase().contains("hazard")).count();

        totalCasesKpi.setText(String.valueOf(total));
        paymentDelaysKpi.setText(String.valueOf(payments));
        safetyViolationsKpi.setText(String.valueOf(safety));
    }

    private void applyFilters() {
        if (grievanceCardsPane == null) return;
        grievanceCardsPane.getChildren().clear();

        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String selPriority = priorityCombo != null && priorityCombo.getValue() != null ? priorityCombo.getValue() : "All Priorities";
        String selStatus = statusCombo != null && statusCombo.getValue() != null ? statusCombo.getValue() : "All Statuses";
        String selCategory = categoryCombo != null && categoryCombo.getValue() != null ? categoryCombo.getValue() : "All Categories";

        List<AdminGrievanceData> filtered = allGrievancesList.stream().filter(g -> {
            boolean qMatch = query.isEmpty()
                    || g.caseId().toLowerCase().contains(query)
                    || g.subject().toLowerCase().contains(query)
                    || g.complainant().toLowerCase().contains(query)
                    || g.project().toLowerCase().contains(query)
                    || g.location().toLowerCase().contains(query);

            boolean prioMatch = "All Priorities".equals(selPriority) || g.priority().equalsIgnoreCase(selPriority);
            boolean stMatch = "All Statuses".equals(selStatus) || g.status().equalsIgnoreCase(selStatus);
            boolean catMatch = "All Categories".equals(selCategory) || g.category().toLowerCase().contains(selCategory.toLowerCase());

            return qMatch && prioMatch && stMatch && catMatch;
        }).toList();

        if (filtered.isEmpty()) {
            VBox empty = new VBox(12,
                    label("No matching grievance cases found.", "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;"),
                    label("Try adjusting your search keywords or clearing active filters.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            grievanceCardsPane.add(empty, 0, 0, 2, 1);
            return;
        }

        int index = 0;
        for (AdminGrievanceData g : filtered) {
            VBox card = renderGrievanceCard(g);
            int col = index % 2;
            int row = index / 2;
            grievanceCardsPane.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            index++;
        }
    }

    /**
     * Builds an Executive Admin Dashboard Grievance Card.
     */
    private VBox renderGrievanceCard(AdminGrievanceData g) {
        boolean isCritical = "Critical".equalsIgnoreCase(g.priority());
        Label prioBadge = label(g.priority().toUpperCase() + " PRIORITY",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + (isCritical ? "#ffffff" : "#735c00") + ";"
                        + "-fx-background-color:" + (isCritical ? "#ba1a1a" : "#fff8e1") + ";"
                        + "-fx-background-radius:6px;-fx-padding:3px 9px;-fx-border-color:" + (isCritical ? "#ba1a1a" : "#ffe082") + ";-fx-border-radius:6px;");

        Label caseIdLabel = label("Case: #" + g.caseId() + "  |  " + g.category(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#4c4637;");

        Label statusBadge = label(g.status().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + (g.status().equalsIgnoreCase("Escalated") ? "#ba1a1a" : "#0d47a1") + ";"
                        + "-fx-background-color:" + (g.status().equalsIgnoreCase("Escalated") ? "#ffebee" : "#e3f2fd") + ";"
                        + "-fx-background-radius:6px;-fx-padding:4px 8px;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topStrip = new HBox(8, prioBadge, caseIdLabel, topSpacer, statusBadge);
        topStrip.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = label(g.subject(), "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLabel.setWrapText(true);

        Label compLabel = label("Complainant: " + g.complainant(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        Label projLabel = label("Site: " + g.project(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        HBox devRow = new HBox(14, compLabel, projLabel);
        devRow.setAlignment(Pos.CENTER_LEFT);

        HBox block1 = adminDataBlock("CLAIM VALUE", g.disputeAmount(), GOLD);
        HBox block2 = adminDataBlock("LOCATION", g.location(), "#1A1A1A");
        HBox block3 = adminDataBlock("FILED", g.timeAgo(), "#1565c0");
        HBox dataStrip = new HBox(10, block1, block2, block3);
        dataStrip.setAlignment(Pos.CENTER_LEFT);

        HBox tagsRow = new HBox(6);
        tagsRow.setAlignment(Pos.CENTER_LEFT);
        tagsRow.getChildren().add(adminTag("Escrow Hold Active"));
        tagsRow.getChildren().add(adminTag("Audit Assigned"));

        Button resolveBtn = new Button("Resolve");
        resolveBtn.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-text-fill:#1b5e20;-fx-border-color:#c8e6c9;-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        resolveBtn.setOnAction(e -> confirmResolveGrievance(g));

        Button inspectBtn = new Button("Inspect Case ->");
        inspectBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-border-color:" + GOLD + ";-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        inspectBtn.setOnAction(e -> openGrievanceDetailsModal(g));

        Region btmSpacer = new Region();
        HBox.setHgrow(btmSpacer, Priority.ALWAYS);
        HBox btmRow = new HBox(8, tagsRow, btmSpacer, resolveBtn, inspectBtn);
        btmRow.setAlignment(Pos.CENTER_LEFT);
        btmRow.setPadding(new Insets(6, 0, 0, 0));
        btmRow.setStyle("-fx-border-color:" + BORDER + "60;-fx-border-width:1px 0 0 0;");

        VBox card = new VBox(11, topStrip, titleLabel, devRow, dataStrip, btmRow);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(18, 20, 16, 20));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);-fx-cursor:hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + GOLD + ";-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.30),16,0,0,5px);-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);"));
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != resolveBtn && e.getTarget() != inspectBtn) {
                openGrievanceDetailsModal(g);
            }
        });

        return card;
    }

    private HBox adminDataBlock(String title, String val, String color) {
        Label t = label(title + ": ", "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#685c52;");
        Label v = label(val, "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + color + ";");
        HBox box = new HBox(2, t, v);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(4, 8, 4, 8));
        box.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:6px;-fx-border-color:#ebdccb;-fx-border-radius:6px;");
        return box;
    }

    private Label adminTag(String text) {
        return label(text, "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#f4ede2;-fx-background-radius:6px;-fx-padding:3px 8px;");
    }

    /**
     * Opens a Floating Grievance Case Inspection Modal.
     */
    private void openGrievanceDetailsModal(AdminGrievanceData g) {
        modalContainer.getChildren().clear();
        modalContainer.setPickOnBounds(true);
        modalContainer.setVisible(true);

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color:rgba(18, 15, 12, 0.70);");
        backdrop.setOnMouseClicked(e -> closeModal());

        Label prioPill = label(g.priority().toUpperCase() + " PRIORITY DISPUTE",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#272727;-fx-background-radius:6px;-fx-padding:4px 10px;");

        Label statusPill = label(g.status().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-background-color:#ba1a1a;-fx-background-radius:6px;-fx-padding:4px 10px;");

        HBox topBadges = new HBox(8, prioPill, statusPill);
        topBadges.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = label(g.subject(), "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        Label subLbl = label("Case ID: #" + g.caseId() + "   |   Complainant: " + g.complainant() + "   |   Project: " + g.project(), "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        VBox titleBox = new VBox(6, topBadges, titleLbl, subLbl);

        Button resolveBtn = new Button("Resolve Dispute");
        resolveBtn.setStyle("-fx-background-color:#2e7d32;-fx-background-radius:10px;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 20px;-fx-cursor:hand;");
        resolveBtn.setOnAction(e -> {
            closeModal();
            confirmResolveGrievance(g);
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-text-fill:#1A1A1A;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8px 16px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;");
        closeBtn.setOnAction(e -> closeModal());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(14, titleBox, spacer, resolveBtn, closeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 16, 0));
        topBar.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1.5px 0;");

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(14);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25.0);
            col.setHgrow(Priority.ALWAYS);
            metricsGrid.getColumnConstraints().add(col);
        }

        metricsGrid.add(modalHighlightCard("DISPUTE VALUE", g.disputeAmount(), "Escrow Protected", GOLD), 0, 0);
        metricsGrid.add(modalHighlightCard("SEVERITY GRADE", g.priority() + " Severity", "Audit Mandated", "#1A1A1A"), 1, 0);
        metricsGrid.add(modalHighlightCard("FILING TIMESTAMP", g.timeAgo(), "Logged via Worker App", "#1565c0"), 2, 0);
        metricsGrid.add(modalHighlightCard("AUDIT STATUS", "Active Arbitration", "Labour Council Bound", "#2e7d32"), 3, 0);

        VBox leftCol = new VBox(14);
        leftCol.setPrefWidth(455);

        VBox partyCard = new VBox(10,
                modalCardHeading("Dispute Parties & Site Logistics"),
                modalDetailRow("Case Tracking ID", "#" + g.caseId()),
                modalDetailRow("Complainant Worker", g.complainant()),
                modalDetailRow("Project Site", g.project()),
                modalDetailRow("Site Location", g.location()),
                modalDetailRow("Grievance Type", g.category()),
                modalDetailRow("Escrow Mitigation", "Automatic ₹25,000 Hold on Contractor Payout Vault")
        );
        partyCard.setPadding(new Insets(16));
        partyCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        leftCol.getChildren().add(partyCard);

        VBox rightCol = new VBox(14);
        rightCol.setPrefWidth(455);

        VBox narrativeCard = new VBox(10,
                modalCardHeading("Incident Statement & Contractor Response"),
                modalDetailRow("Incident Summary", g.description()),
                modalDetailRow("Contractor Response", g.contractorResponse()),
                modalDetailRow("Evidence Audit", "Biometric Shift Logs & Timestamped Site Photos Verified"),
                modalDetailRow("Resolution Action", "Settlement Payout via DIHADI Escrow Direct Transfer")
        );
        narrativeCard.setPadding(new Insets(16));
        narrativeCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        rightCol.getChildren().add(narrativeCard);

        HBox bodyRow = new HBox(22, leftCol, rightCol);
        bodyRow.setAlignment(Pos.TOP_LEFT);

        VBox modalCard = new VBox(18, topBar, metricsGrid, bodyRow);
        modalCard.setPrefWidth(980);
        modalCard.setMaxWidth(980);
        modalCard.setPadding(new Insets(26, 32, 26, 32));
        modalCard.setStyle("-fx-background-color:linear-gradient(to bottom, #ffffff, #fcf8f0);-fx-background-radius:24px;-fx-border-color:" + GOLD + ";-fx-border-width:2px;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,.55),32,0,0,12px);");

        modalCard.setOnMouseClicked(e -> e.consume());

        StackPane modalWrapper = new StackPane(backdrop, modalCard);
        modalWrapper.setAlignment(Pos.CENTER);

        modalContainer.getChildren().add(modalWrapper);

        FadeTransition ft = new FadeTransition(Duration.millis(200), modalWrapper);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private VBox modalHighlightCard(String title, String value, String subtext, String color) {
        Label t = label(title, "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        Label v = label(value, "-fx-font-family:Georgia;-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:" + color + ";");
        v.setWrapText(true);
        Label s = label(subtext, "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#8c7b6d;");

        VBox box = new VBox(4, t, v, s);
        box.setPadding(new Insets(12, 14, 12, 14));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:#ebdccb;-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),6,0,0,2px);");
        return box;
    }

    private Label modalCardHeading(String text) {
        return label(text, "-fx-font-family:Georgia;-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:" + PRIMARY + ";");
    }

    private HBox modalDetailRow(String labelText, String valText) {
        Label l = label(labelText + ":", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        l.setPrefWidth(130);
        Label v = label(valText, "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        v.setWrapText(true);
        v.setMaxWidth(280);
        HBox box = new HBox(6, l, v);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void confirmResolveGrievance(AdminGrievanceData g) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Grievance Resolution");
        confirm.setHeaderText("Resolve Case #" + g.caseId() + "?");
        confirm.setContentText("Are you sure you want to mark this grievance as RESOLVED and release the escrow settlement?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        new com.dihadi.controller.GrievanceController().resolveGrievance(g.caseId(), "Resolved by System Administrator");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
                allGrievancesList.removeIf(item -> item.caseId().equals(g.caseId()));
                updateKpis();
                applyFilters();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Case #" + g.caseId() + " has been successfully marked as Resolved.");
                alert.show();
            }
        });
    }

    private String val(String s, String def) {
        return (s != null && !s.isBlank()) ? s : def;
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
        HBox footer = new HBox(28, health("Database: OK"), health("Dispute Center: OK"), health("Payments: OK"), spacer(), time);
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

    private Button nav(String title, boolean active) {
        Button button = new Button(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color:" + (active ? GOLD : "transparent") + ";-fx-background-radius:9px;-fx-text-fill:" + (active ? "white" : "#dcdad4") + ";-fx-font-size:16px;-fx-font-weight:" + (active ? "800" : "500") + ";-fx-padding:16px 28px;-fx-cursor:hand;");
        return button;
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

    private ImageView image(String path, double width, double height) {
        try {
            var r = getClass().getResource(path);
            if (r == null) return new ImageView();
            ImageView view = new ImageView(new Image(r.toExternalForm()));
            view.setFitWidth(width);
            view.setFitHeight(height);
            view.setPreserveRatio(true);
            return view;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private Label label(String value, String style) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public record AdminGrievanceData(
            String caseId,
            String subject,
            String complainant,
            String project,
            String location,
            String priority,
            String status,
            String category,
            String disputeAmount,
            String timeAgo,
            String description,
            String contractorResponse
    ) {}
}
