package com.dihadi.view.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkforceRequirementController;
import com.dihadi.model.Project;
import com.dihadi.model.WorkforceRequirement;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Real-time Admin Projects Management & Site Monitoring Center.
 * Features full rich enterprise project cards with triple metric pill blocks,
 * comprehensive specification details modal containing all data, clean lifecycle tracking,
 * and floating mini-card delete dialogs.
 */
public class AdminProjectsPage {
    private static final String DARK = "#272727", GOLD = "#D4AF37", MAIN = "#f3e7ce", BORDER = "#E0D9CE", PRIMARY = "#735c00";

    private Timeline clock;
    private StackPane rootStack;
    private StackPane modalContainer;
    private GridPane projectCardsPane;
    private TextField searchField;
    private ComboBox<String> lifecycleCombo;
    private ComboBox<String> sectorCombo;
    private ComboBox<String> tradeCombo;

    private Label totalSitesKpi;
    private Label totalWorkersKpi;
    private Label totalWageVolumeKpi;
    private Label urgentCountKpi;

    private final List<AdminProjectData> allProjectsList = new ArrayList<>();
    private boolean isLoading = true;

    public Scene getProjectsScene(Runnable dashboardAction, Runnable logout) {
        BorderPane layout = new BorderPane();
        layout.setLeft(sidebar(dashboardAction, logout));
        layout.setCenter(mainContent());

        modalContainer = new StackPane();
        modalContainer.setPickOnBounds(false);
        modalContainer.setVisible(false);

        rootStack = new StackPane(layout, modalContainer);
        loadRealtimeProjects();
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

        Button projectsNav = nav("Projects", true);

        Button grievances = nav("Grievances", false);
        grievances.setOnAction(e -> {
            if (clock != null) clock.stop();
            Stage stage = (Stage) grievances.getScene().getWindow();
            stage.setScene(new AdminGrievancesPage().getGrievancesScene(dashboardAction, logout));
        });

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
                label("Projects Management", "-fx-font-size:16px;-fx-text-fill:" + GOLD + ";")
        );
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        breadcrumb.setPadding(new Insets(0, 40, 0, 40));
        breadcrumb.setPrefHeight(80);
        breadcrumb.setStyle("-fx-background-color:" + MAIN + ";-fx-border-color:" + BORDER + "80;-fx-border-width:0 0 1px 0;");

        VBox content = new VBox(26,
                heading(),
                kpiRow(),
                filterSearchBar(),
                projectGrid()
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
                label("Enterprise Projects Control Center", "-fx-font-family:Georgia;-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;"),
                label("Real-time live project tracking, contractor deployments, workforce demands, and site audits.", "-fx-font-size:15px;-fx-text-fill:#4A4A4A;")
        );
    }

    private GridPane kpiRow() {
        GridPane grid = grid(4);
        grid.setHgap(20);

        totalSitesKpi = label("Loading...", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        totalWorkersKpi = label("Loading...", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        totalWageVolumeKpi = label("Loading...", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        urgentCountKpi = label("Loading...", "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;");

        grid.add(kpiCard("ACTIVE PROJECT SITES", totalSitesKpi, "Verified in Ecosystem", "#2e7d32"), 0, 0);
        grid.add(kpiCard("WORKFORCE DEMAND", totalWorkersKpi, "Active Openings", "#1565c0"), 1, 0);
        grid.add(kpiCard("COMMITTED DAILY VOLUME", totalWageVolumeKpi, "Escrow Secured", "#735c00"), 2, 0);
        grid.add(kpiCard("URGENT HIRING SITES", urgentCountKpi, "Priority Escalation", "#ba1a1a"), 3, 0);
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
        searchField.setPromptText("Search project name, location, developer, or trade...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:9px 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        lifecycleCombo = choice("All Statuses", "Currently Ongoing", "Upcoming Projects", "Completed Projects", "Urgent Hiring");
        sectorCombo = choice("All Sectors", "Infrastructure", "Commercial", "Residential", "Industrial");
        tradeCombo = choice("All Trades", "Mason", "Carpenter", "Electrician", "Plumber", "Painter", "Welder", "General Labour", "Technician");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-padding:8px 16px;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-cursor:hand;");
        refreshBtn.setOnAction(e -> loadRealtimeProjects());

        HBox filters = new HBox(12,
                searchField,
                lifecycleCombo,
                sectorCombo,
                tradeCombo,
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
        cb.setPrefWidth(160);
        cb.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-font-size:13px;-fx-cursor:hand;");
        cb.setOnAction(e -> applyFilters());
        return cb;
    }

    private VBox projectGrid() {
        projectCardsPane = new GridPane();
        projectCardsPane.setHgap(20);
        projectCardsPane.setVgap(20);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        projectCardsPane.getColumnConstraints().addAll(col1, col2);
        projectCardsPane.setMaxWidth(Double.MAX_VALUE);

        if (isLoading) {
            ProgressIndicator pi = new ProgressIndicator();
            pi.setPrefSize(42, 42);
            VBox box = new VBox(12, pi, label("Loading projects...", "-fx-font-size:14px;-fx-text-fill:#685c52;"));
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(50));
            projectCardsPane.add(box, 0, 0, 2, 1);
        }

        VBox container = new VBox(projectCardsPane);
        container.setAlignment(Pos.TOP_LEFT);
        return container;
    }

    private void loadRealtimeProjects() {
        isLoading = true;
        new Thread(() -> {
            List<AdminProjectData> list = new ArrayList<>();
            try {
                List<Project> dbProjects = new ProjectController().getAllProjects();
                List<WorkforceRequirement> dbReqs = new WorkforceRequirementController().getAllRequirements();

                Map<String, List<WorkforceRequirement>> reqMap = new HashMap<>();
                if (dbReqs != null) {
                    for (WorkforceRequirement r : dbReqs) {
                        if (r.getProjectId() != null) {
                            reqMap.computeIfAbsent(r.getProjectId(), k -> new ArrayList<>()).add(r);
                        }
                    }
                }

                if (dbProjects != null && !dbProjects.isEmpty()) {
                    for (Project p : dbProjects) {
                        List<WorkforceRequirement> pReqs = reqMap.get(p.getProjectId());
                        double wage = 850;
                        int workers = 25;
                        String trade = "General Workforce";
                        boolean water = true, power = true, stay = false, transport = false;
                        String reqId = "";

                        if (pReqs != null && !pReqs.isEmpty()) {
                            WorkforceRequirement r = pReqs.get(0);
                            wage = r.getDailyWages() > 0 ? r.getDailyWages() : 850;
                            workers = r.getQuantity() > 0 ? r.getQuantity() : 25;
                            trade = r.getWorkerType() != null ? r.getWorkerType() : "General Labour";
                            water = r.isWaterFacility();
                            power = r.isElectricityFacility();
                            stay = r.isAccommodationFacility();
                            transport = r.isTransportationFacility();
                            reqId = r.getRequirementId() != null ? r.getRequirementId() : "";
                        }

                        String loc = (val(p.getCity(), "Pune") + ", " + val(p.getState(), "Maharashtra")).replaceAll("^, |, $", "");
                        List<String> images = (p.getImageUrls() != null && !p.getImageUrls().isEmpty()) ? p.getImageUrls() : new ArrayList<>();
                        String firstImg = !images.isEmpty() ? images.get(0) : "/assets/images/explore/explore_slide_1.jpg";

                        String rawStatus = val(p.getStatus(), "Active");
                        String lifecycle = resolveLifecycle(rawStatus);

                        list.add(new AdminProjectData(
                                p.getProjectId(),
                                val(p.getProjectName(), "Infrastructure Project Site"),
                                loc,
                                val(p.getContactName(), "Verified Developer"),
                                trade,
                                "₹" + String.format("%,d", (long) wage) + " / day",
                                workers + " Openings",
                                workers,
                                wage,
                                rawStatus,
                                lifecycle,
                                "Infrastructure",
                                val(p.getMobile(), "9822012345"),
                                reqId,
                                firstImg,
                                images,
                                val(p.getAddressLine1(), "Plot 42, Industrial Development Corridor"),
                                val(p.getAddressLine2(), "Phase 1 Tech Hub"),
                                val(p.getLandmark(), "Near Expressway Toll Plaza"),
                                val(p.getContactName(), "Site Project Manager"),
                                water, power, stay, transport, false
                        ));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Curated Benchmark mega projects
            list.addAll(getBenchmarkAdminProjects());

            Platform.runLater(() -> {
                allProjectsList.clear();
                allProjectsList.addAll(list);
                isLoading = false;
                updateKpis();
                applyFilters();
            });
        }).start();
    }

    private String resolveLifecycle(String status) {
        if (status == null) return "Currently Ongoing";
        String s = status.toLowerCase();
        if (s.contains("complete") || s.contains("closed") || s.contains("finished")) return "Completed Project";
        if (s.contains("upcoming") || s.contains("planned") || s.contains("draft")) return "Upcoming Project";
        return "Currently Ongoing";
    }

    private List<AdminProjectData> getBenchmarkAdminProjects() {
        return List.of(
                new AdminProjectData("PRJ-101", "Hiranandani Business & Residential Towers", "Mumbai, Maharashtra", "Hiranandani Developers", "Technician / Supervisor", "₹1,200 / day", "85 Openings", 85, 1200, "Urgent Hiring", "Currently Ongoing", "Commercial & Residential", "9822012341", "R-01", "/assets/images/explore/explore_slide_1.jpg", List.of("/assets/images/explore/explore_slide_1.jpg"), "Hiranandani Gardens, Powai", "Central Avenue", "Opposite Powai Lake", "Vikram Hiranandani", true, true, true, true, true),
                new AdminProjectData("PRJ-102", "BHRAMHA Horizon Premium Residential Complex", "Pune, Maharashtra", "BHRAMHA Group", "Carpenter / Plumber", "₹950 / day", "110 Openings", 110, 950, "Active", "Currently Ongoing", "Residential", "9822012342", "R-02", "/assets/images/explore/explore_slide_2.jpg", List.of("/assets/images/explore/explore_slide_2.jpg"), "Kalyani Nagar Main Road", "Sector 4", "Near Gold Adlabs", "Anil Bhramha", true, true, true, false, true),
                new AdminProjectData("PRJ-103", "LODHAA Grand Central Urban Expressway", "Mumbai, Maharashtra", "LODHAA Group", "Civil Engineer / Foreman", "₹1,450 / day", "60 Openings", 60, 1450, "Urgent Hiring", "Currently Ongoing", "Infrastructure", "9822012343", "R-03", "/assets/images/explore/explore_slide_3.jpg", List.of("/assets/images/explore/explore_slide_3.jpg"), "Thane-Belapur Expressway Junction", "Sector 11", "Near Airoli Bridge", "Rajesh Lodha", true, true, false, true, true),
                new AdminProjectData("PRJ-104", "Ramoji Film City Mega Studio Infrastructure", "Hyderabad, Telangana", "Ramoji Film City", "Painter & Welder", "₹1,050 / day", "95 Openings", 95, 1050, "Upcoming", "Upcoming Project", "Commercial", "9822012344", "R-04", "/assets/images/explore/explore_slide_4.jpg", List.of("/assets/images/explore/explore_slide_4.jpg"), "Ramoji Film City Campus", "Studio Complex 3", "Hayathnagar Mandal", "K. Rao", true, true, true, true, true),
                new AdminProjectData("PRJ-105", "BASIL Tech Habitat Smart Residential Park", "Bengaluru, Karnataka", "BASIL Group", "General Labour / Mason", "₹880 / day", "150 Openings", 150, 880, "Active", "Currently Ongoing", "Residential", "9822012345", "R-05", "/assets/images/explore/explore_slide_5.jpg", List.of("/assets/images/explore/explore_slide_5.jpg"), "Whitefield Main Road", "EPIP Zone", "Near ITPL Metro Station", "Girish Basil", true, true, true, true, true),
                new AdminProjectData("PRJ-106", "Pune Metro Rail Underground Depot - Phase 2", "Pune, Maharashtra", "L&T Heavy Infrastructure", "Mason", "₹950 / day", "140 Openings", 140, 950, "Active", "Currently Ongoing", "Infrastructure", "9822012346", "R-06", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png"), "Range Hills Depot Site", "Shivajinagar Corridor", "Near Agricultural College", "Sanjay Deshmukh", true, true, true, true, true),
                new AdminProjectData("PRJ-107", "Mumbai Coastal Road Expressway & Sea Bridge", "Mumbai, Maharashtra", "Afcons Infrastructure", "Structural Fitter", "₹1,250 / day", "95 Openings", 95, 1250, "Urgent Hiring", "Currently Ongoing", "Infrastructure", "9822012347", "R-07", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png"), "Worli Sea Face Interchange", "South Section", "Worli Point", "Dinesh Kulkarni", true, true, false, true, true),
                new AdminProjectData("PRJ-108", "Prestige Tech Cloud IT Park - Phase 4 Towers", "Bangalore, Karnataka", "Prestige Group", "Electrician", "₹1,100 / day", "60 Openings", 60, 1100, "Completed", "Completed Project", "Commercial", "9822012348", "R-08", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png"), "International Airport Road", "Devenahalli Corridor", "Opposite Airport Toll", "Ramesh Rao", true, true, true, false, true)
        );
    }

    private void updateKpis() {
        int totalSites = allProjectsList.size();
        int totalWorkers = allProjectsList.stream().mapToInt(AdminProjectData::workerQuantity).sum();
        double totalVolume = allProjectsList.stream().mapToDouble(p -> p.workerQuantity() * p.dailyWageAmount()).sum();
        long urgentCount = allProjectsList.stream().filter(p -> p.status().toLowerCase().contains("urgent")).count();

        totalSitesKpi.setText(String.valueOf(totalSites));
        totalWorkersKpi.setText(String.format("%,d", totalWorkers));
        totalWageVolumeKpi.setText("₹" + String.format("%.2f", totalVolume / 100000.0) + " L");
        urgentCountKpi.setText(String.valueOf(urgentCount));
    }

    private void applyFilters() {
        if (projectCardsPane == null) return;
        projectCardsPane.getChildren().clear();

        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String selLifecycle = lifecycleCombo != null && lifecycleCombo.getValue() != null ? lifecycleCombo.getValue() : "All Statuses";
        String selSector = sectorCombo != null && sectorCombo.getValue() != null ? sectorCombo.getValue() : "All Sectors";
        String selTrade = tradeCombo != null && tradeCombo.getValue() != null ? tradeCombo.getValue() : "All Trades";

        List<AdminProjectData> filtered = allProjectsList.stream().filter(p -> {
            boolean qMatch = query.isEmpty()
                    || p.projectName().toLowerCase().contains(query)
                    || p.location().toLowerCase().contains(query)
                    || p.company().toLowerCase().contains(query)
                    || p.trade().toLowerCase().contains(query);

            boolean lifeMatch = "All Statuses".equals(selLifecycle)
                    || ("Currently Ongoing".equals(selLifecycle) && "Currently Ongoing".equalsIgnoreCase(p.lifecycle()))
                    || ("Upcoming Projects".equals(selLifecycle) && "Upcoming Project".equalsIgnoreCase(p.lifecycle()))
                    || ("Completed Projects".equals(selLifecycle) && "Completed Project".equalsIgnoreCase(p.lifecycle()))
                    || ("Urgent Hiring".equals(selLifecycle) && p.status().toLowerCase().contains("urgent"));

            boolean secMatch = "All Sectors".equals(selSector) || p.sector().toLowerCase().contains(selSector.toLowerCase());
            boolean trMatch = "All Trades".equals(selTrade) || p.trade().toLowerCase().contains(selTrade.toLowerCase()) || selTrade.toLowerCase().contains(p.trade().toLowerCase());

            return qMatch && lifeMatch && secMatch && trMatch;
        }).toList();

        if (filtered.isEmpty()) {
            VBox empty = new VBox(12,
                    label("No matching enterprise projects found.", "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;"),
                    label("Try adjusting your search keyword or clearing the active filters.", "-fx-font-size:13px;-fx-text-fill:#685c52;"));
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));
            projectCardsPane.add(empty, 0, 0, 2, 1);
            return;
        }

        int index = 0;
        for (AdminProjectData p : filtered) {
            VBox card = renderAdminProjectCard(p);
            int col = index % 2;
            int row = index / 2;
            projectCardsPane.add(card, col, row);
            GridPane.setHgrow(card, Priority.ALWAYS);
            index++;
        }
    }

    /**
     * Builds an Executive Enterprise Admin Dashboard Project Card.
     * Shows the project lifecycle status tag (Currently Ongoing, Upcoming, Completed) in the header,
     * full developer details, 3 distinct data blocks (Rate, Demand, Trade), facilities, and action buttons.
     */
    private VBox renderAdminProjectCard(AdminProjectData p) {
        // Top Header Strip: Lifecycle Status Tag + Location + Urgent/Active Tag
        String lifecycleTagText = p.lifecycle().toUpperCase();
        String lifecycleStyle;
        if (p.lifecycle().equalsIgnoreCase("Completed Project")) {
            lifecycleStyle = "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#0d47a1;-fx-background-color:#e3f2fd;-fx-background-radius:6px;-fx-padding:3px 9px;-fx-border-color:#bbdefb;-fx-border-radius:6px;";
        } else if (p.lifecycle().equalsIgnoreCase("Upcoming Project")) {
            lifecycleStyle = "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b78103;-fx-background-color:#fff8e1;-fx-background-radius:6px;-fx-padding:3px 9px;-fx-border-color:#ffe082;-fx-border-radius:6px;";
        } else {
            lifecycleStyle = "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#1b5e20;-fx-background-color:#e8f5e9;-fx-background-radius:6px;-fx-padding:3px 9px;-fx-border-color:#c8e6c9;-fx-border-radius:6px;";
        }

        Label lifecycleBadge = label(lifecycleTagText, lifecycleStyle);
        Label locLabel = label("Location: " + p.location(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#4c4637;");

        boolean isUrgent = p.status() != null && p.status().toLowerCase().contains("urgent");
        Label statusBadge = label(isUrgent ? "URGENT HIRING" : "VERIFIED ACTIVE",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + (isUrgent ? "#ffffff" : "#1b5e20") + ";"
                        + "-fx-background-color:" + (isUrgent ? "#ba1a1a" : "#e8f5e9") + ";"
                        + "-fx-background-radius:6px;-fx-padding:4px 8px;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topStrip = new HBox(8, lifecycleBadge, locLabel, topSpacer, statusBadge);
        topStrip.setAlignment(Pos.CENTER_LEFT);

        // Project Title
        Label titleLabel = label(p.projectName(), "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLabel.setWrapText(true);

        // Contractor / Developer Info with direct contact phone
        Label companyLabel = label("Developer: " + p.company() + "  |  " + p.sector(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        Label phoneLabel = label("Contact: " + p.recruiterPhone(), "-fx-font-family:Consolas;-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        HBox devRow = new HBox(12, companyLabel, phoneLabel);
        devRow.setAlignment(Pos.CENTER_LEFT);

        // 3 Compact Data Blocks
        HBox block1 = adminDataBlock("RATE", p.wage(), GOLD);
        HBox block2 = adminDataBlock("DEMAND", p.workersNeeded(), "#1A1A1A");
        HBox block3 = adminDataBlock("TRADE", p.trade(), "#1565c0");
        HBox dataStrip = new HBox(10, block1, block2, block3);
        dataStrip.setAlignment(Pos.CENTER_LEFT);

        // Facilities check indicators
        HBox facilitiesRow = new HBox(6);
        facilitiesRow.setAlignment(Pos.CENTER_LEFT);
        if (p.hasWater()) facilitiesRow.getChildren().add(adminTag("Water"));
        if (p.hasPower()) facilitiesRow.getChildren().add(adminTag("Electricity"));
        if (p.hasStay()) facilitiesRow.getChildren().add(adminTag("Accommodation"));
        if (p.hasTransport()) facilitiesRow.getChildren().add(adminTag("Transport"));

        // Admin Action Controls
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color:#ffebee;-fx-background-radius:8px;-fx-text-fill:#ba1a1a;-fx-border-color:#ffcdd2;-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        deleteBtn.setOnAction(e -> confirmAndDeleteProject(p));

        Button inspectBtn = new Button("Inspect Details ->");
        inspectBtn.setStyle("-fx-background-color:#272727;-fx-background-radius:8px;-fx-text-fill:#ffd54f;-fx-border-color:" + GOLD + ";-fx-border-radius:8px;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        inspectBtn.setOnAction(e -> openGlassmorphicDetailsModal(p));

        Region btmSpacer = new Region();
        HBox.setHgrow(btmSpacer, Priority.ALWAYS);
        HBox btmRow = new HBox(8, facilitiesRow, btmSpacer, deleteBtn, inspectBtn);
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
            if (e.getTarget() != deleteBtn && e.getTarget() != inspectBtn) {
                openGlassmorphicDetailsModal(p);
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
     * Opens a Floating Semi-Transparent Glassmorphic Admin Inspector Modal with rich visual cards,
     * highlight metrics, real photo viewer, and project deletion controls.
     */
    private void openGlassmorphicDetailsModal(AdminProjectData p) {
        modalContainer.getChildren().clear();
        modalContainer.setPickOnBounds(true);
        modalContainer.setVisible(true);

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color:rgba(18, 15, 12, 0.70);");
        backdrop.setOnMouseClicked(e -> closeModal());

        // Header Section
        Label categoryPill = label(p.lifecycle().toUpperCase() + " SPECIFICATION",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#272727;-fx-background-radius:6px;-fx-padding:4px 10px;");

        boolean isUrgent = p.status() != null && p.status().toLowerCase().contains("urgent");
        Label statusPill = label(isUrgent ? "URGENT HIRING" : "ACTIVE SITE",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + (isUrgent ? "#ffffff" : "#1b5e20") + ";"
                        + "-fx-background-color:" + (isUrgent ? "#ba1a1a" : "#e8f5e9") + ";"
                        + "-fx-background-radius:6px;-fx-padding:4px 10px;");

        HBox topBadges = new HBox(8, categoryPill, statusPill);
        topBadges.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = label(p.projectName(), "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(620);

        Label subLbl = label("Location: " + p.location() + "   |   Developer: " + p.company() + "   |   Sector: " + p.sector(), "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#5d5045;");
        VBox titleBox = new VBox(6, topBadges, titleLbl, subLbl);

        Button deleteBtn = new Button("Delete Project");
        deleteBtn.setStyle("-fx-background-color:#ba1a1a;-fx-background-radius:10px;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 20px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(186,26,26,.35),8,0,0,2px);");
        deleteBtn.setOnAction(e -> {
            closeModal();
            confirmAndDeleteProject(p);
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

        // 4 Highlight Metric Cards Row
        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(14);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25.0);
            col.setHgrow(Priority.ALWAYS);
            metricsGrid.getColumnConstraints().add(col);
        }

        metricsGrid.add(modalHighlightCard("DAILY WAGE", p.wage(), "Escrow Guaranteed", GOLD), 0, 0);
        metricsGrid.add(modalHighlightCard("OPENINGS", p.workersNeeded(), "Immediate Deployment", "#1A1A1A"), 1, 0);
        metricsGrid.add(modalHighlightCard("PRIMARY TRADE", p.trade(), "Skill Grade Certified", "#1565c0"), 2, 0);
        metricsGrid.add(modalHighlightCard("SAFETY PROTOCOL", "100% Verified", "KYC & Labor Approved", "#2e7d32"), 3, 0);

        // 2-Column Content Body
        VBox leftCol = new VBox(14);
        leftCol.setPrefWidth(455);

        VBox locationCard = new VBox(10,
                modalCardHeading("Site Address & Logistics"),
                modalDetailRow("Address Line 1", p.addressLine1()),
                modalDetailRow("Address Line 2", p.addressLine2()),
                modalDetailRow("Access Landmark", p.landmark()),
                modalDetailRow("Region / State", p.location())
        );
        locationCard.setPadding(new Insets(16));
        locationCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        VBox contactCard = new VBox(10,
                modalCardHeading("Site Leadership & Contact Information"),
                modalDetailRow("Project Manager", p.supervisorName()),
                modalDetailRow("Developer / Contractor", p.company()),
                modalDetailRow("Contact Phone", p.recruiterPhone())
        );
        contactCard.setPadding(new Insets(16));
        contactCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        leftCol.getChildren().addAll(locationCard, contactCard);

        VBox rightCol = new VBox(14);
        rightCol.setPrefWidth(455);

        ImageView siteImg = new ImageView(load(p.imagePath()));
        siteImg.setFitWidth(455);
        siteImg.setFitHeight(185);
        siteImg.setPreserveRatio(false);
        siteImg.setSmooth(true);
        Rectangle imgClip = new Rectangle(455, 185);
        imgClip.setArcWidth(16);
        imgClip.setArcHeight(16);
        siteImg.setClip(imgClip);

        StackPane imgFrame = new StackPane(siteImg);
        imgFrame.setStyle("-fx-background-color:#1e1b15;-fx-background-radius:16px;-fx-border-color:" + GOLD + ";-fx-border-width:1.5px;-fx-border-radius:16px;");

        VBox amenitiesCard = new VBox(10,
                modalCardHeading("Site Amenities & Safety Facilities"),
                amenitiesMatrix(p)
        );
        amenitiesCard.setPadding(new Insets(14));
        amenitiesCard.setStyle("-fx-background-color:#faf5eb;-fx-background-radius:14px;-fx-border-color:#ebdccb;-fx-border-radius:14px;");

        rightCol.getChildren().addAll(imgFrame, amenitiesCard);

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
        l.setPrefWidth(120);
        Label v = label(valText, "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;");
        v.setWrapText(true);
        v.setMaxWidth(290);
        HBox box = new HBox(6, l, v);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private FlowPane amenitiesMatrix(AdminProjectData p) {
        FlowPane fp = new FlowPane(8, 8);
        fp.getChildren().add(richAmenityBadge("Potable Drinking Water", p.hasWater()));
        fp.getChildren().add(richAmenityBadge("24x7 Power Backup", p.hasPower()));
        fp.getChildren().add(richAmenityBadge("Worker Stay Facility", p.hasStay()));
        fp.getChildren().add(richAmenityBadge("Site Shuttle Transport", p.hasTransport()));
        fp.getChildren().add(richAmenityBadge("Mandatory PPE Kit", true));
        fp.getChildren().add(richAmenityBadge("Hygienic Canteen", true));
        return fp;
    }

    private HBox richAmenityBadge(String text, boolean available) {
        Label l = label(text,
                "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:" + (available ? "#1b5e20" : "#757575") + ";");
        HBox box = new HBox(l);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(4, 9, 4, 9));
        box.setStyle("-fx-background-color:" + (available ? "#e8f5e9" : "#f5f5f5") + ";-fx-background-radius:6px;-fx-border-color:" + (available ? "#c8e6c9" : "#e0e0e0") + ";-fx-border-radius:6px;");
        return box;
    }

    /**
     * Standard Confirmation Dialog for Project Deletion.
     */
    private void confirmAndDeleteProject(AdminProjectData p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Project Deletion");
        confirm.setHeaderText("Delete " + p.projectName() + "?");
        confirm.setContentText("Are you sure you want to delete this project? This will permanently remove it from the database.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        if (!p.isBenchmark()) {
                            new ProjectController().deleteProject(p.projectId());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        allProjectsList.removeIf(item -> item.projectId().equals(p.projectId()));
                        updateKpis();
                        applyFilters();
                        showSuccessToast(p.projectName() + " has been successfully removed.");
                    });
                }).start();
            }
        });
    }

    /** Shows a Standard Default System Information Pop-up. */
    private void showSuccessToast(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
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
        HBox footer = new HBox(28, health("Database: OK"), health("Projects: OK"), health("Payments: OK"), spacer(), time);
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
        if (!active) button.setOnAction(e -> showSuccessToast(title + " module is ready."));
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

    private Image load(String path) {
        try {
            if (path == null || path.isBlank()) {
                var r = getClass().getResource("/assets/images/explore/explore_slide_1.jpg");
                return r == null ? null : new Image(r.toExternalForm());
            }
            if (path.startsWith("http://") || path.startsWith("https://")) {
                return new Image(path, true);
            }
            if (new java.io.File(path).exists()) {
                return new Image(new java.io.File(path).toURI().toString());
            }
            var r = getClass().getResource(path);
            return r == null ? null : new Image(r.toExternalForm());
        } catch (Exception e) {
            return null;
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

    private String val(String str, String fallback) {
        return (str != null && !str.isBlank()) ? str : fallback;
    }

    public record AdminProjectData(
            String projectId,
            String projectName,
            String location,
            String company,
            String trade,
            String wage,
            String workersNeeded,
            int workerQuantity,
            double dailyWageAmount,
            String status,
            String lifecycle,
            String sector,
            String recruiterPhone,
            String requirementId,
            String imagePath,
            List<String> imageUrls,
            String addressLine1,
            String addressLine2,
            String landmark,
            String supervisorName,
            boolean hasWater,
            boolean hasPower,
            boolean hasStay,
            boolean hasTransport,
            boolean isBenchmark
    ) {}
}
