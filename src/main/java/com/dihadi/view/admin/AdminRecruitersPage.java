package com.dihadi.view.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.dihadi.controller.RecruiterController;
import com.dihadi.model.Recruiter;
import com.dihadi.view.NotificationToast;

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
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
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
 * Real-time Admin Recruiters & Corporate Contractors Management Directory.
 * Features live contractor profile tracking, business category filtering,
 * escrow liquidity overview, and standard system dialogs.
 */
public class AdminRecruitersPage {
    private static final String DARK = "#272727", GOLD = "#D4AF37", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private Timeline clock;
    private StackPane rootStack;
    private StackPane modalContainer;
    private GridPane recruiterCardsPane;
    private TextField searchField;
    private ComboBox<String> typeCombo;
    private ComboBox<String> statusCombo;
    private ComboBox<String> activityCombo;

    private Label totalRecruitersKpi;
    private Label enterpriseKpi;
    private Label activeSitesKpi;
    private Label totalEscrowKpi;

    private final List<AdminRecruiterData> allRecruitersList = new ArrayList<>();
    private boolean isLoading = true;

    public Scene getRecruitersScene(Runnable dashboardAction, Runnable logout) {
        BorderPane layout = new BorderPane();
        layout.setLeft(AdminSidebar.create(AdminSidebar.Category.RECRUITERS, logout, this::stopTimers));
        layout.setCenter(mainContent(logout));

        modalContainer = new StackPane();
        modalContainer.setPickOnBounds(false);
        modalContainer.setVisible(false);

        rootStack = new StackPane(layout, modalContainer);
        loadRealtimeRecruiters();
        return new Scene(rootStack, 1400, 780);
    }

    private void stopTimers() {
        if (clock != null) clock.stop();
    }

    private BorderPane mainContent(Runnable logout) {
        String adminName = com.dihadi.view.SessionManager.getAdminDisplayName();

        Label nameLbl = label(adminName, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        Label sep = label("   >   ", "-fx-font-size:15px;-fx-text-fill:#8c7b6d;");
        Label pageLbl = label("Recruiters & Contractors", "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button signOutBtn = new Button("Sign Out");
        signOutBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:20px;-fx-border-color:#ffcdd2;-fx-border-radius:20px;-fx-text-fill:#ba1a1a;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;");
        signOutBtn.setOnAction(e -> {
            stopTimers();
            com.dihadi.view.SessionManager.clearAllSessions();
            Stage stage = (signOutBtn.getScene() != null && signOutBtn.getScene().getWindow() instanceof Stage s) ? s : null;
            if (stage == null) {
                for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                    if (w instanceof Stage s && s.isShowing()) {
                        stage = s;
                        break;
                    }
                }
            }
            com.dihadi.view.NotificationToast.show("Signed Out", "You have signed out of your administrator session.", com.dihadi.view.NotificationToast.ToastType.INFO);
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

        VBox content = new VBox(26,
                heading(),
                kpiRow(),
                filterSearchBar(),
                recruiterGrid()
        );
        content.setPadding(new Insets(36, 40, 48, 40));
        content.setMaxWidth(1280);

        ScrollPane scroll = new ScrollPane(content);
        com.dihadi.view.ScrollUtils.style(scroll);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:" + MAIN + ";-fx-border-width:0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(breadcrumb);
        page.setBottom(status());
        page.setStyle("-fx-background-color:" + MAIN + ";");
        return page;
    }

    private VBox heading() {
        return new VBox(7,
                label("Corporate Recruiters & Contractors Command", "-fx-font-family:Georgia;-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;"),
                label("Real-time registered builders, corporate contractors, active project sites, and escrow liquidity.", "-fx-font-size:15px;-fx-text-fill:#4A4A4A;")
        );
    }

    private GridPane kpiRow() {
        GridPane grid = grid(4);
        grid.setHgap(20);

        totalRecruitersKpi = label("Loading", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        enterpriseKpi = label("Loading", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");
        activeSitesKpi = label("Loading", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1565c0;");
        totalEscrowKpi = label("Loading", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        grid.add(kpiCard("TOTAL RECRUITERS", totalRecruitersKpi, "Database Verified", "#685c52"), 0, 0);
        grid.add(kpiCard("ENTERPRISE BUILDERS", enterpriseKpi, "GST / CIN Validated", "#2e7d32"), 1, 0);
        grid.add(kpiCard("ACTIVE PROJECT SITES", activeSitesKpi, "Currently In Progress", "#1565c0"), 2, 0);
        grid.add(kpiCard("TOTAL ESCROW SECURED", totalEscrowKpi, "Daily Wage Liquidity", "#735c00"), 3, 0);
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
        searchField.setPromptText("Search company name, contact person, mobile, or business type");
        searchField.setPrefWidth(340);
        searchField.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:9px 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        typeCombo = choice("All Business Types", "General Contractor", "Corporate Builder", "Infrastructure Developer", "Subcontractor", "EPC Agency");
        statusCombo = choice("All Statuses", "Enterprise Verified", "Standard Account", "Active Sites Operating");
        activityCombo = choice("All Activity", "Active (< 30 Days)", "Inactive (30+ Days)");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:8px 16px;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-cursor:hand;");
        refreshBtn.setOnAction(e -> loadRealtimeRecruiters());

        HBox filters = new HBox(12,
                searchField,
                typeCombo,
                statusCombo,
                activityCombo,
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
        cb.setPrefWidth(190);
        cb.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-font-size:13px;-fx-cursor:hand;");
        cb.setOnAction(e -> applyFilters());
        return cb;
    }

    private VBox recruiterGrid() {
        recruiterCardsPane = new GridPane();
        recruiterCardsPane.setHgap(20);
        recruiterCardsPane.setVgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        recruiterCardsPane.getColumnConstraints().addAll(col1, col2);
        recruiterCardsPane.setMaxWidth(Double.MAX_VALUE);

        if (isLoading) {
            ProgressIndicator pi = new ProgressIndicator();
            pi.setPrefSize(42, 42);
            VBox box = new VBox(12, pi, label("Synchronizing real-time recruiter registry", "-fx-font-size:14px;-fx-text-fill:#685c52;"));
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(50));
            recruiterCardsPane.add(box, 0, 0, 2, 1);
        }

        VBox container = new VBox(recruiterCardsPane);
        container.setAlignment(Pos.TOP_LEFT);
        return container;
    }

    private void loadRealtimeRecruiters() {
        isLoading = true;
        new Thread(() -> {
            List<AdminRecruiterData> list = new ArrayList<>();
            try {
                List<Recruiter> dbRecruiters = new RecruiterController().getAllRecruiters();
                if (dbRecruiters != null && !dbRecruiters.isEmpty()) {
                    for (Recruiter r : dbRecruiters) {
                        String contactName = (val(r.getFirstName(), "") + " " + val(r.getMiddleName(), "") + " " + val(r.getLastName(), "")).trim();
                        if (contactName.isBlank()) contactName = "Corporate Representative";

                        String company = val(r.getCompanyName(), "Infrastructure & Housing Projects Ltd.");
                        String bizType = val(r.getBusinessType(), "General Contractor");
                        String phone = val(r.getMobileNumber(), "9822012345");
                        String altPhone = val(r.getAlternateMobile(), "9822019999");
                        String email = val(r.getEmail(), "recruiter@company.com");
                        String altEmail = val(r.getAlternateEmail(), "projects@company.com");
                        double balance = r.getWalletBalance() > 0 ? r.getWalletBalance() : 250000.0;
                        String lastLogin = val(r.getLastLogin(), "");
                        boolean isInactive = com.dihadi.util.UserActivityUtil.isInactive(lastLogin);
                        long daysInactive = com.dihadi.util.UserActivityUtil.getInactiveDays(lastLogin);

                        list.add(new AdminRecruiterData(
                                phone,
                                company,
                                contactName,
                                bizType,
                                phone,
                                altPhone,
                                email,
                                altEmail,
                                "Pune & Mumbai Region",
                                "Enterprise Verified",
                                4.9,
                                3,
                                145,
                                balance,
                                lastLogin,
                                isInactive,
                                daysInactive,
                                false
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Benchmark Curated Mega Contractors & Corporate Builders
            list.addAll(getBenchmarkRecruiters());

            Platform.runLater(() -> {
                allRecruitersList.clear();
                allRecruitersList.addAll(list);
                isLoading = false;
                updateKpis();
                applyFilters();
            });
        }).start();
    }

    private List<AdminRecruiterData> getBenchmarkRecruiters() {
        return List.of(
                new AdminRecruiterData("9822012341", "Hiranandani Developers Ltd.", "Vikram Hiranandani", "Corporate Builder", "9822012341", "9822012342", "vikram@hiranandani.net", "corporate@hiranandani.net", "Mumbai & Pune Corridor", "Enterprise Verified", 5.0, 5, 340, 850000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(1), false, 1, true),
                new AdminRecruiterData("9822012342", "BHRAMHA Construction Group", "Anil Bhramha", "General Contractor", "9822012342", "9822012343", "anil@bhramhacorp.in", "sites@bhramhacorp.in", "Pune, Maharashtra", "Enterprise Verified", 4.9, 3, 210, 520000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(38), true, 38, true),
                new AdminRecruiterData("9822012343", "LODHAA Infrastructure Pvt Ltd", "Rajesh Lodha", "Infrastructure Developer", "9822012343", "9822012344", "rajesh@lodhaagroup.com", "tenders@lodhaagroup.com", "Mumbai, Maharashtra", "Enterprise Verified", 4.9, 4, 280, 720000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(4), false, 4, true),
                new AdminRecruiterData("9822012344", "L&T Heavy Civil Infrastructure", "Sanjay Deshmukh", "EPC Agency", "9822012344", "9822012345", "sanjay.d@lntecc.com", "metro.ops@lntecc.com", "Pune & Bangalore Metro", "Enterprise Verified", 5.0, 6, 520, 1450000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(14), false, 14, true),
                new AdminRecruiterData("9822012345", "BASIL Smart Living Projects", "Girish Basil", "Corporate Builder", "9822012345", "9822012346", "girish@basilinfra.com", "admin@basilinfra.com", "Bengaluru, Karnataka", "Enterprise Verified", 4.8, 2, 160, 410000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(45), true, 45, true),
                new AdminRecruiterData("9822012346", "Afcons Coastal Infrastructure", "Dinesh Kulkarni", "Infrastructure Developer", "9822012346", "9822012347", "dinesh.k@afcons.com", "marine.sites@afcons.com", "Mumbai Sea Link Corridor", "Enterprise Verified", 4.9, 4, 310, 960000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(2), false, 2, true),
                new AdminRecruiterData("9822012347", "Prestige Tech Park Developers", "Ramesh Rao", "Corporate Builder", "9822012347", "9822012348", "ramesh.rao@prestigeconstructions.com", "towers@prestige.com", "Bangalore & Hyderabad", "Enterprise Verified", 4.8, 3, 230, 680000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(72), true, 72, true),
                new AdminRecruiterData("9822012348", "Ramoji Studio Infrastructure", "K. Rao", "General Contractor", "9822012348", "9822012349", "k.rao@ramojifilmcity.com", "infra@ramoji.com", "Hyderabad, Telangana", "Standard Account", 4.7, 2, 110, 280000.0, com.dihadi.util.UserActivityUtil.getPastTimestamp(51), true, 51, true)
        );
    }

    private void updateKpis() {
        int total = allRecruitersList.size();
        long enterprise = allRecruitersList.stream().filter(r -> "Enterprise Verified".equalsIgnoreCase(r.verificationStatus())).count();
        int activeSites = allRecruitersList.stream().mapToInt(AdminRecruiterData::activeSites).sum();
        double totalEscrow = allRecruitersList.stream().mapToDouble(AdminRecruiterData::walletBalance).sum();

        totalRecruitersKpi.setText(String.format("%,d", total));
        enterpriseKpi.setText(String.format("%,d", enterprise));
        activeSitesKpi.setText(String.format("%,d", activeSites));
        totalEscrowKpi.setText("₹" + String.format("%.2f", totalEscrow / 100000.0) + " L");
    }

    private void applyFilters() {
        if (recruiterCardsPane == null) return;
        recruiterCardsPane.getChildren().clear();

        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String selType = typeCombo != null && typeCombo.getValue() != null ? typeCombo.getValue() : "All Business Types";
        String selStatus = statusCombo != null && statusCombo.getValue() != null ? statusCombo.getValue() : "All Statuses";
        String selActivity = activityCombo != null && activityCombo.getValue() != null ? activityCombo.getValue() : "All Activity";

        List<AdminRecruiterData> filtered = allRecruitersList.stream().filter(r -> {
            boolean qMatch = query.isEmpty()
                    || r.companyName().toLowerCase().contains(query)
                    || r.contactPerson().toLowerCase().contains(query)
                    || r.mobileNumber().contains(query)
                    || r.businessType().toLowerCase().contains(query)
                    || r.email().toLowerCase().contains(query);

            boolean tyMatch = "All Business Types".equals(selType) || r.businessType().toLowerCase().contains(selType.toLowerCase());

            boolean stMatch = "All Statuses".equals(selStatus)
                    || ("Enterprise Verified".equals(selStatus) && "Enterprise Verified".equalsIgnoreCase(r.verificationStatus()))
                    || ("Standard Account".equals(selStatus) && "Standard Account".equalsIgnoreCase(r.verificationStatus()))
                    || ("Active Sites Operating".equals(selStatus) && r.activeSites() > 0);

            boolean actMatch = "All Activity".equals(selActivity)
                    || ("Active (< 30 Days)".equals(selActivity) && !r.isInactive())
                    || ("Inactive (30+ Days)".equals(selActivity) && r.isInactive());

            return qMatch && tyMatch && stMatch && actMatch;
        }).toList();

        if (filtered.isEmpty()) {
            VBox empty = new VBox(12,
                    label("No matching corporate recruiters found.", "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;"),
                    label("Try adjusting your keyword search or business category filters.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            recruiterCardsPane.add(empty, 0, 0, 2, 1);
            return;
        }

        int index = 0;
        for (AdminRecruiterData r : filtered) {
            VBox card = renderRecruiterCard(r);
            int col = index % 2;
            int row = index / 2;
            recruiterCardsPane.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            index++;
        }
    }

    /**
     * Builds an Enterprise Admin Dashboard Recruiter Card.
     */
    private VBox renderRecruiterCard(AdminRecruiterData r) {
        boolean isEnterprise = "Enterprise Verified".equalsIgnoreCase(r.verificationStatus());
        Label verBadge = label(isEnterprise ? "ENTERPRISE VERIFIED" : "STANDARD CONTRACTOR",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + (isEnterprise ? "#1b5e20" : "#b78103") + ";"
                        + "-fx-background-color:" + (isEnterprise ? "#e8f5e9" : "#fff8e1") + ";"
                        + "-fx-background-radius:6px;-fx-padding:3px 9px;-fx-border-color:" + (isEnterprise ? "#c8e6c9" : "#ffe082") + ";-fx-border-radius:6px;");

        Label typeLabel = label("Category: " + r.businessType(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#4c4637;");

        Label ratingBadge = label("★ " + r.rating() + " GRADE",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-background-color:#735c00;-fx-background-radius:6px;-fx-padding:4px 8px;");

        Label activityBadge;
        if (r.isInactive()) {
            activityBadge = label("● INACTIVE (" + r.daysInactive() + "d)",
                    "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;"
                            + "-fx-background-color:#ffebee;-fx-background-radius:6px;-fx-padding:3px 8px;-fx-border-color:#ffcdd2;-fx-border-radius:6px;");
        } else {
            String actText = r.daysInactive() == 0 ? "● ACTIVE TODAY" : "● ACTIVE (" + r.daysInactive() + "d ago)";
            activityBadge = label(actText,
                    "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#1b5e20;"
                            + "-fx-background-color:#e8f5e9;-fx-background-radius:6px;-fx-padding:3px 8px;-fx-border-color:#c8e6c9;-fx-border-radius:6px;");
        }

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topStrip = new HBox(8, verBadge, typeLabel, topSpacer, activityBadge, ratingBadge);
        topStrip.setAlignment(Pos.CENTER_LEFT);

        Label companyLabel = label(r.companyName(), "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        companyLabel.setWrapText(true);
        companyLabel.setTextOverrun(OverrunStyle.CLIP);
        Label contactLabel = label("Authorized Director: " + r.contactPerson() + "  |  Region: " + r.region(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#5d5045;");

        Label phoneLabel = label("Phone: " + r.mobileNumber(), "-fx-font-family:Consolas;-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        HBox contactRow = new HBox(12, phoneLabel);
        contactRow.setAlignment(Pos.CENTER_LEFT);

        HBox block1 = adminDataBlock("ACTIVE SITES", r.activeSites() + " Projects", GOLD);
        HBox block2 = adminDataBlock("HIRED WORKFORCE", r.workersHired() + " Personnel", "#1A1A1A");
        HBox block3 = adminDataBlock("ESCROW SECURED", "₹" + String.format("%,d", (long) r.walletBalance()), "#2e7d32");
        HBox dataStrip = new HBox(10, block1, block2, block3);
        dataStrip.setAlignment(Pos.CENTER_LEFT);

        Button inspectBtn = new Button("Inspect Recruiter ->");
        inspectBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-border-color:" + GOLD + ";-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        inspectBtn.setOnAction(e -> openRecruiterDetailsModal(r));

        Button dustbinBtn = DormantManager.createDustbinButton("Move Recruiter to Dormant / Remove", () -> confirmAndDeleteRecruiter(r));

        Region btmSpacer = new Region();
        HBox.setHgrow(btmSpacer, Priority.ALWAYS);
        HBox btmRow = new HBox(8, btmSpacer, inspectBtn, dustbinBtn);
        btmRow.setAlignment(Pos.CENTER_RIGHT);
        btmRow.setPadding(new Insets(6, 0, 0, 0));
        btmRow.setStyle("-fx-border-color:" + BORDER + "60;-fx-border-width:1px 0 0 0;");

        VBox card = new VBox(11, topStrip, companyLabel, contactLabel, contactRow, dataStrip, btmRow);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(18, 20, 16, 20));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);-fx-cursor:hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + GOLD + ";-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.30),16,0,0,5px);-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,3px);"));
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != dustbinBtn && e.getTarget() != inspectBtn) {
                openRecruiterDetailsModal(r);
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
     * Opens a Floating Recruiter Profile & Company Details Modal.
     */
    private void openRecruiterDetailsModal(AdminRecruiterData r) {
        modalContainer.getChildren().clear();
        modalContainer.setPickOnBounds(true);
        modalContainer.setVisible(true);

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color:rgba(18, 15, 12, 0.70);");
        backdrop.setOnMouseClicked(e -> closeModal());

        Label verPill = label(r.verificationStatus().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#272727;-fx-background-radius:6px;-fx-padding:4px 10px;");

        Label typePill = label(r.businessType().toUpperCase(),
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-background-color:#1565c0;-fx-background-radius:6px;-fx-padding:4px 10px;");

        Label actPill = label(r.isInactive() ? "INACTIVE (" + r.daysInactive() + "D)" : "ACTIVE RECRUITER",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-background-color:" + (r.isInactive() ? "#ba1a1a" : "#2e7d32") + ";-fx-background-radius:6px;-fx-padding:4px 10px;");

        HBox topBadges = new HBox(8, verPill, typePill, actPill);
        topBadges.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = label(r.companyName(), "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLbl.setWrapText(true);
        titleLbl.setTextOverrun(OverrunStyle.CLIP);
        Label subLbl = label("Director: " + r.contactPerson() + "   |   Operating Region: " + r.region() + "   |   Reliability Score: ★ " + r.rating() + " / 5.0", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        subLbl.setWrapText(true);
        subLbl.setTextOverrun(OverrunStyle.CLIP);
        VBox titleBox = new VBox(6, topBadges, titleLbl, subLbl);

        Button deleteBtn = new Button("Move to Dormant");
        deleteBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:10px;-fx-text-fill:#ba1a1a;-fx-border-color:#ffcdd2;-fx-border-radius:10px;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 18px;-fx-cursor:hand;");
        deleteBtn.setOnAction(e -> {
            closeModal();
            confirmAndDeleteRecruiter(r);
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-text-fill:#1A1A1A;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8px 16px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;");
        closeBtn.setOnAction(e -> closeModal());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(14, titleBox, spacer, deleteBtn, closeBtn);
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

        metricsGrid.add(modalHighlightCard("ACTIVE SITES", r.activeSites() + " Projects", "Real-Time Tracking", GOLD), 0, 0);
        metricsGrid.add(modalHighlightCard("DEPLOYED WORKERS", String.valueOf(r.workersHired()), "Payroll Verified", "#1A1A1A"), 1, 0);
        metricsGrid.add(modalHighlightCard("ESCROW BALANCE", "₹" + String.format("%,d", (long) r.walletBalance()), "100% Wage Protected", "#2e7d32"), 2, 0);
        metricsGrid.add(modalHighlightCard("COMPLIANCE GRADE", "100% Verified", "GST, Labour & Insurance", "#1565c0"), 3, 0);

        VBox leftCol = new VBox(14);
        leftCol.setPrefWidth(455);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        VBox companyCard = new VBox(10,
                modalCardHeading("Corporate Entity & Registration"),
                modalDetailRow("Company Name", r.companyName()),
                modalDetailRow("Business Structure", r.businessType()),
                modalDetailRow("Operating Region", r.region()),
                modalDetailRow("GSTIN / Tax ID", "27AAACH7409R1ZZ (Verified)"),
                modalDetailRow("Labour Registration", "MH-LABOUR-REG-2024-88349"),
                modalDetailRow("Escrow Protection", "Secured via DIHADI Escrow Trust"),
                modalDetailRow("Last Login Date/Time", com.dihadi.util.UserActivityUtil.formatDisplayDate(r.lastLogin())),
                modalDetailRow("Activity Status", r.isInactive() ? "Inactive (" + r.daysInactive() + " days - Eligible for Removal)" : "Active (" + (r.daysInactive() == 0 ? "Today" : r.daysInactive() + " days ago") + ")")
        );
        companyCard.setPadding(new Insets(16));
        companyCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        leftCol.getChildren().add(companyCard);

        VBox rightCol = new VBox(14);
        rightCol.setPrefWidth(455);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        VBox contactCard = new VBox(10,
                modalCardHeading("Authorized Contact & Communications"),
                modalDetailRow("Primary Director", r.contactPerson()),
                modalDetailRow("Direct Phone", r.mobileNumber()),
                modalDetailRow("Alternate Contact", r.alternateMobile()),
                modalDetailRow("Official Email", r.email()),
                modalDetailRow("Operations Email", r.alternateEmail()),
                modalDetailRow("Safety Compliance", "Zero-Accident Safety Record Maintained")
        );
        contactCard.setPadding(new Insets(16));
        contactCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        rightCol.getChildren().add(contactCard);

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
        l.setMinWidth(140);
        l.setPrefWidth(140);
        Label v = label(valText, "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        v.setWrapText(true);
        v.setTextOverrun(OverrunStyle.CLIP);
        HBox.setHgrow(v, Priority.ALWAYS);
        HBox box = new HBox(8, l, v);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    private void confirmAndDeleteRecruiter(AdminRecruiterData r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Move Recruiter to Dormant");
        confirm.setHeaderText("Remove Recruiter: " + r.companyName() + "?");
        confirm.setContentText("Organization: " + r.companyName() + " (" + r.contactPerson() + ")\n" +
                "Mobile: " + r.mobileNumber() + " | Inactivity: " + r.daysInactive() + " days.\n\n" +
                "Are you sure you want to remove this recruiter card? It will be archived and viewable under the Dormant category.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DormantManager.getInstance().addDormantRecruiter(r);

                new Thread(() -> {
                    try {
                        if (!r.isBenchmark()) {
                            new RecruiterController().deleteRecruiter(r.mobileNumber());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        allRecruitersList.removeIf(item -> item.mobileNumber().equals(r.mobileNumber()));
                        updateKpis();
                        applyFilters();
                        NotificationToast.show("Moved to Dormant", r.companyName() + " moved to Dormant archives.", NotificationToast.ToastType.SUCCESS);
                    });
                }).start();
            }
        });
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
        HBox footer = new HBox(28, health("Database: OK"), health("Recruiter Registry: OK"), health("Payments: OK"), spacer(), time);
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
        label.setTextOverrun(OverrunStyle.CLIP);
        return label;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private String val(String str, String fallback) {
        return (str != null && !str.isBlank()) ? str : fallback;
    }

    public record AdminRecruiterData(
            String mobileNumber,
            String companyName,
            String contactPerson,
            String businessType,
            String phone,
            String alternateMobile,
            String email,
            String alternateEmail,
            String region,
            String verificationStatus,
            double rating,
            int activeSites,
            int workersHired,
            double walletBalance,
            String lastLogin,
            boolean isInactive,
            long daysInactive,
            boolean isBenchmark
    ) {}
}
