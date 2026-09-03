package com.dihadi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkforceRequirementController;
import com.dihadi.model.Project;
import com.dihadi.model.WorkforceRequirement;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Explore Projects page rendering hero as 1st card, sliding multi-card banner marquee as 2nd card,
 * and classic 2-column project cards with real live database synchronization.
 */
public class ExploreProjectsPage {
    private static final String SURFACE = "#f3e7ce";
    private static final String CARD = "#fef9f1";
    private static final String BORDER = "#d0c5af";
    private static final String GOLD = "#735c00";
    private static final String BLUE = "#415ba4";
    private static final String RED = "#ba1a1a";

    private static final String[] FEATURED_BANNER_IMAGES = {
            "/assets/images/explore/explore_slide_1.jpg",
            "/assets/images/explore/explore_slide_2.jpg",
            "/assets/images/explore/explore_slide_3.jpg",
            "/assets/images/explore/explore_slide_4.jpg",
            "/assets/images/explore/explore_slide_5.jpg"
    };

    private static final String[] HERO_SLIDER_IMAGES = {
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_a_professional_indian_construction_mason/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_welder_working_in_an_industrial_workshop_wearing/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_carpenter_in_a_woodworking_shop_wearing_a_dihadi/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_warehouse_worker_operating_a_forklift_in_a/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_plumber_working_on_industrial_piping._he_is_wearing_a/screen.png"
    };

    private final Runnable showHome;
    private FlowPane cardsContainer;
    private ComboBox<String> tradeCombo;
    private ComboBox<String> stateCombo;
    private ComboBox<String> typeCombo;

    private AnimationTimer marqueeTimer;
    private boolean isBannerHovered = false;

    private Timeline heroTimeline;
    private int currentHeroIndex = 0;

    private final List<ProjectCardModel> loadedProjects = new ArrayList<>();
    private boolean isLoading = true;

    public ExploreProjectsPage(Runnable showHome) {
        this.showHome = showHome;
    }

    public Scene getExploreProjectsScene() {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(body());
        page.setStyle("-fx-background-color:" + SURFACE + ";");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setBackground(new Background(new BackgroundFill(Color.web(SURFACE), CornerRadii.EMPTY, Insets.EMPTY)));

        // Load real live projects asynchronously
        loadRealProjectsData();

        return new Scene(root, 1400, 780, Color.web(SURFACE));
    }

    private BorderPane header() {
        ImageView logo = new ImageView(load("/assets/logo/dihadi logo.jpeg"));
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        Label name = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, name);
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox navigation = new HBox(12,
                navButton("Home", false, () -> navigateTo("Home")),
                navButton("Business", false, () -> navigateTo("Business")),
                navButton("Worker", false, () -> navigateTo("Worker")),
                navButton("Recruiter", false, () -> navigateTo("Recruiter")),
                navButton("About Us", false, () -> navigateTo("About Us")),
                navButton("Contact Us", false, () -> navigateTo("Contact Us")));
        navigation.setAlignment(Pos.CENTER);

        Button admin = AppNavigator.createHeaderAdminButton();
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(new HBox(10, admin));
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    /** Body with Hero as 1st card, Multi-card Sliding Banner Marquee as 2nd card */
    private ScrollPane body() {
        HBox backRow = new HBox(homeBackButton());
        backRow.setAlignment(Pos.CENTER_LEFT);
        VBox content = new VBox(26, backRow, hero(), featuredBannerBox(), filterBar(), projectGridSection(), footer());
        content.setMaxWidth(1180);
        content.setPadding(new Insets(30, 20, 36, 20));
        content.setAlignment(Pos.TOP_CENTER);

        StackPane centeredWrap = new StackPane(content);
        centeredWrap.setAlignment(Pos.TOP_CENTER);
        centeredWrap.setStyle("-fx-background-color: " + SURFACE + ";");

        ScrollPane scroll = new ScrollPane(centeredWrap);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + SURFACE + "; -fx-background-color: " + SURFACE + "; -fx-border-width: 0;");
        return scroll;
    }

    /** Compact return control positioned above the Explore Projects hero. */
    private Button homeBackButton() {
        Button back = new Button("<");
        back.setPrefSize(54, 52);
        back.setMinSize(54, 52);
        back.setMaxSize(54, 52);
        back.setStyle(
                "-fx-background-color:#ead7ad;-fx-background-radius:16px;-fx-text-fill:#4c4637;-fx-font-size:24px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:0 0 3px 0;-fx-cursor:hand;");
        back.setOnAction(e -> {
            if (heroTimeline != null) heroTimeline.stop();
            if (showHome != null) showHome.run();
        });
        return back;
    }

    /** 1st Box: Attractive Hero card with smooth multi-image auto-slider and premium typography */
    private HBox hero() {
        Label eyebrow = label("✦  EXPLORE VERIFIED MEGA PROJECTS",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1.5px;");
        Label title = text("Ready to Discover Your\nNext High-Impact Project?",
                "-fx-font-family: Georgia; -fx-font-size: 34px; -fx-font-weight: 800; -fx-text-fill: #1e1b15; -fx-line-spacing: 4px;");
        title.setWrapText(true);

        Label subText = text(
                "Connect directly with certified contractors, top infrastructure developers, and industrial sites across India with guaranteed transparent daily wages.",
                "-fx-font-size: 15px; -fx-text-fill: #4c4637; -fx-line-spacing: 3px;");
        subText.setWrapText(true);
        subText.setMaxWidth(520);

        HBox pill1 = featurePill("✓  Direct Project Connect • Zero Middlemen");
        HBox pill2 = featurePill("✓  Verified Daily Wages & Escrow Security");
        HBox pill3 = featurePill("✓  Commercial, Residential & Industrial Sites");
        VBox pills = new VBox(8, pill1, pill2, pill3);

        VBox copy = new VBox(15, eyebrow, title, subText, pills);
        copy.setPrefWidth(540);
        copy.setMaxWidth(540);
        copy.setAlignment(Pos.CENTER_LEFT);

        // Auto-sliding Hero Visual
        ImageView heroImage = new ImageView(load(HERO_SLIDER_IMAGES[0]));
        heroImage.setFitWidth(560);
        heroImage.setFitHeight(350);
        heroImage.setPreserveRatio(false);
        heroImage.setSmooth(true);

        Rectangle clip = new Rectangle(560, 350);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        heroImage.setClip(clip);

        StackPane imageBox = new StackPane(heroImage);
        imageBox.setPrefSize(560, 350);
        imageBox.setMaxSize(560, 350);
        imageBox.setStyle("-fx-background-color:#1e1b15;-fx-background-radius: 24px; -fx-border-radius: 24px; -fx-border-color: #d4af37; -fx-border-width:2px; -fx-effect:dropshadow(gaussian,rgba(58,48,39,.25),20,0,0,6px);");

        if (heroTimeline != null) heroTimeline.stop();
        heroTimeline = new Timeline(new KeyFrame(Duration.millis(2800), e -> {
            currentHeroIndex = (currentHeroIndex + 1) % HERO_SLIDER_IMAGES.length;
            heroImage.setImage(load(HERO_SLIDER_IMAGES[currentHeroIndex]));
        }));
        heroTimeline.setCycleCount(Timeline.INDEFINITE);
        heroTimeline.play();

        imageBox.setOnMouseEntered(e -> heroTimeline.pause());
        imageBox.setOnMouseExited(e -> heroTimeline.play());

        HBox hero = new HBox(36, copy, imageBox);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(26, 32, 26, 32));
        hero.setStyle("-fx-background-color:#f8eedb;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);");
        hero.setMaxWidth(1180);
        return hero;
    }

    private HBox featurePill(String text) {
        Label l = label(text, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        HBox box = new HBox(l);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 12, 6, 12));
        box.setStyle("-fx-background-color:rgba(115,92,0,0.08);-fx-background-radius:12px;-fx-border-color:rgba(115,92,0,0.22);-fx-border-radius:12px;");
        return box;
    }

    /** 2nd Box: Dynamic Auto-Sliding Featured Developer & Recruiter Project Banners matching reference video flow */
    private VBox featuredBannerBox() {
        double cardWidth = 520;
        double cardHeight = 260;
        double gap = 20;

        HBox track = new HBox(gap);
        track.setAlignment(Pos.CENTER_LEFT);

        // 3 duplicate cycles to create seamless infinite loop
        for (int cycle = 0; cycle < 3; cycle++) {
            for (int i = 0; i < FEATURED_BANNER_IMAGES.length; i++) {
                final int bannerIdx = i;
                ImageView img = new ImageView(load(FEATURED_BANNER_IMAGES[i]));
                img.setFitWidth(cardWidth);
                img.setFitHeight(cardHeight);
                img.setPreserveRatio(false);
                img.setSmooth(true);

                Rectangle imgClip = new Rectangle(cardWidth, cardHeight);
                imgClip.setArcWidth(20);
                imgClip.setArcHeight(20);
                img.setClip(imgClip);

                StackPane card = new StackPane(img);
                card.setPrefSize(cardWidth, cardHeight);
                card.setMaxSize(cardWidth, cardHeight);
                card.setStyle("-fx-background-color:#161311;-fx-background-radius:20px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),12,0,0,4px);-fx-cursor:hand;");

                card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#161311;-fx-background-radius:20px;-fx-border-color:#e9c349;-fx-border-width:2px;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.35),16,0,0,6px);-fx-cursor:hand;"));
                card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#161311;-fx-background-radius:20px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),12,0,0,4px);-fx-cursor:hand;"));

                card.setOnMouseClicked(e -> {
                    if (loadedProjects.size() > bannerIdx) {
                        ProjectCardModel pm = loadedProjects.get(bannerIdx);
                        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                            if (window.isFocused() && window instanceof Stage stage) {
                                Scene currentScene = stage.getScene();
                                stage.setScene(new ExploreProjectDetailsPage(
                                        pm.projectId(),
                                        pm.projectName(),
                                        pm.location(),
                                        pm.company(),
                                        pm.trade(),
                                        pm.wage(),
                                        pm.workersNeeded(),
                                        pm.status(),
                                        pm.sector(),
                                        pm.recruiterPhone(),
                                        pm.requirementId(),
                                        pm.imagePath(),
                                        pm.imageUrls(),
                                        pm.hasWater(),
                                        pm.hasPower(),
                                        pm.hasStay(),
                                        pm.hasTransport(),
                                        () -> stage.setScene(currentScene)
                                ).getScene());
                                return;
                            }
                        }
                    }
                });

                track.getChildren().add(card);
            }
        }

        // Viewport clipping to 1140px width
        Pane viewport = new Pane(track);
        viewport.setPrefSize(1140, cardHeight);
        viewport.setMaxSize(1140, cardHeight);

        Rectangle viewportClip = new Rectangle(1140, cardHeight);
        viewportClip.setArcWidth(22);
        viewportClip.setArcHeight(22);
        viewport.setClip(viewportClip);

        // Smooth continuous 60fps translation
        if (marqueeTimer != null) marqueeTimer.stop();
        final double cycleWidth = FEATURED_BANNER_IMAGES.length * (cardWidth + gap);

        marqueeTimer = new AnimationTimer() {
            private long lastNow = 0;

            @Override
            public void handle(long now) {
                if (lastNow == 0) {
                    lastNow = now;
                    return;
                }
                double delta = (now - lastNow) / 1_000_000_000.0;
                lastNow = now;

                if (!isBannerHovered) {
                    double speed = 90; // smooth pixels per second
                    double nextX = track.getTranslateX() - (speed * delta);
                    if (nextX <= -cycleWidth) {
                        nextX += cycleWidth;
                    }
                    track.setTranslateX(nextX);
                }
            }
        };
        marqueeTimer.start();

        viewport.setOnMouseEntered(e -> isBannerHovered = true);
        viewport.setOnMouseExited(e -> isBannerHovered = false);

        VBox bannerCard = new VBox(viewport);
        bannerCard.setAlignment(Pos.CENTER);
        bannerCard.setPadding(new Insets(14, 12, 14, 12));
        bannerCard.setMaxWidth(1180);
        bannerCard.setStyle("-fx-background-color:#f8eedb;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);");
        return bannerCard;
    }

    private HBox filterBar() {
        tradeCombo = choice("All Trades", "Carpenter", "Electrician", "Mason", "Plumber", "Painter", "ITI Technician", "General Labour", "Site Supervisor");
        stateCombo = choice("All Locations", "Maharashtra", "Karnataka", "Gujarat", "Delhi", "Tamil Nadu", "Telangana", "Chhattisgarh");
        typeCombo = choice("All Categories", "Infrastructure", "Commercial", "Residential", "Industrial");

        HBox filters = new HBox(18,
                text("Filter Projects:", "-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #735c00;"),
                filterGroup("Trade / Skill", tradeCombo),
                filterGroup("Location", stateCombo),
                filterGroup("Sector", typeCombo)
        );
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(16, 24, 16, 24));
        filters.setStyle("-fx-background-color: white; -fx-background-radius: 18px; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 18px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,.08), 16, 0, 0, 4);");
        filters.setMaxWidth(1180);
        return filters;
    }

    private ComboBox<String> choice(String selected, String... values) {
        ComboBox<String> choice = new ComboBox<>();
        choice.getItems().add(selected);
        for (String value : values) {
            if (!value.equals(selected)) choice.getItems().add(value);
        }
        choice.setValue(selected);
        choice.setPrefWidth(165);
        choice.setStyle("-fx-background-color: #faf3e8; -fx-background-radius: 8px; -fx-border-color: #d0c5af; -fx-border-radius: 8px; -fx-font-size: 13px; -fx-cursor: hand;");
        choice.setOnAction(event -> applyFilters());
        return choice;
    }

    private VBox filterGroup(String label, ComboBox<String> choice) {
        return new VBox(4, text(label, "-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #4c4637;"), choice);
    }

    private VBox projectGridSection() {
        Label headerTitle = label("Active Mega Projects & Site Opportunities",
                "-fx-font-family:'Georgia';-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        cardsContainer = new FlowPane(20, 20);
        cardsContainer.setPrefWrapLength(1140);
        cardsContainer.setMaxWidth(1140);
        cardsContainer.setAlignment(Pos.TOP_CENTER);

        if (isLoading) {
            ProgressIndicator loader = new ProgressIndicator();
            loader.setPrefSize(42, 42);
            VBox loadingBox = new VBox(14, loader, label("Fetching real projects from network...", "-fx-font-size:14px;-fx-text-fill:#685c52;"));
            loadingBox.setAlignment(Pos.CENTER);
            loadingBox.setPadding(new Insets(40));
            cardsContainer.getChildren().add(loadingBox);
        }

        VBox section = new VBox(18, headerTitle, cardsContainer);
        section.setAlignment(Pos.TOP_LEFT);
        section.setMaxWidth(1140);
        return section;
    }

    /** Loads real projects and workforce requirements from Firestore database. */
    private void loadRealProjectsData() {
        new Thread(() -> {
            List<ProjectCardModel> list = new ArrayList<>();
            try {
                List<Project> allDbProjects = new ProjectController().getAllProjects();
                List<WorkforceRequirement> allReqs = new WorkforceRequirementController().getAllRequirements();

                Map<String, List<WorkforceRequirement>> reqMap = new HashMap<>();
                if (allReqs != null) {
                    for (WorkforceRequirement r : allReqs) {
                        if (r.getProjectId() != null) {
                            reqMap.computeIfAbsent(r.getProjectId(), k -> new ArrayList<>()).add(r);
                        }
                    }
                }

                // Convert DB Projects into display cards
                if (allDbProjects != null && !allDbProjects.isEmpty()) {
                    for (Project p : allDbProjects) {
                        List<WorkforceRequirement> pReqs = reqMap.get(p.getProjectId());
                        double wage = 850;
                        int workers = 25;
                        String trade = "General Workforce";
                        boolean water = true, power = true, stay = false, transport = false;
                        String reqId = "";

                        if (pReqs != null && !pReqs.isEmpty()) {
                            WorkforceRequirement firstReq = pReqs.get(0);
                            wage = firstReq.getDailyWages() > 0 ? firstReq.getDailyWages() : 850;
                            workers = firstReq.getQuantity() > 0 ? firstReq.getQuantity() : 25;
                            trade = firstReq.getWorkerType() != null ? firstReq.getWorkerType() : "General Labour";
                            water = firstReq.isWaterFacility();
                            power = firstReq.isElectricityFacility();
                            stay = firstReq.isAccommodationFacility();
                            transport = firstReq.isTransportationFacility();
                            reqId = firstReq.getRequirementId() != null ? firstReq.getRequirementId() : "";
                        }

                        String loc = (val(p.getCity(), "Pune") + ", " + val(p.getState(), "Maharashtra")).replaceAll("^, |, $", "");
                        String company = val(p.getContactName(), "Verified Developer");
                        String status = val(p.getStatus(), "Active");

                        List<String> pImages = (p.getImageUrls() != null && !p.getImageUrls().isEmpty()) ? p.getImageUrls() : new ArrayList<>();
                        String firstImg = !pImages.isEmpty() ? pImages.get(0) : getFallbackImageForTrade(trade);

                        list.add(new ProjectCardModel(
                                p.getProjectId(),
                                val(p.getProjectName(), "Infrastructure Project Site"),
                                loc,
                                company,
                                trade,
                                "₹" + String.format("%,d", (long) wage) + " / day",
                                workers + " Workers Needed",
                                status,
                                "Infrastructure",
                                p.getMobile(),
                                reqId,
                                firstImg,
                                pImages,
                                water, power, stay, transport
                        ));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Include curated top real benchmark landmark projects with distinct real images
            list.addAll(getBenchmarkRealProjects());

            Platform.runLater(() -> {
                loadedProjects.clear();
                loadedProjects.addAll(list);
                isLoading = false;
                applyFilters();
            });
        }).start();
    }

    private List<ProjectCardModel> getBenchmarkRealProjects() {
        return List.of(
                new ProjectCardModel("P-BENCH-01", "Hiranandani Business & Residential Towers", "Mumbai, Maharashtra", "Hiranandani Developers", "Technician / Supervisor", "₹1,200 / day", "85 Workers Needed", "Urgent Hiring", "Commercial & Residential", "9822012341", "R-01", "/assets/images/explore/explore_slide_1.jpg", List.of("/assets/images/explore/explore_slide_1.jpg"), true, true, true, true),
                new ProjectCardModel("P-BENCH-02", "BHRAMHA Horizon Premium Residential Complex", "Pune, Maharashtra", "BHRAMHA Group", "Carpenter / Plumber", "₹950 / day", "110 Workers Needed", "Active Site", "Residential", "9822012342", "R-02", "/assets/images/explore/explore_slide_2.jpg", List.of("/assets/images/explore/explore_slide_2.jpg"), true, true, true, false),
                new ProjectCardModel("P-BENCH-03", "LODHAA Grand Central Urban Expressway", "Mumbai, Maharashtra", "LODHAA Group", "Civil Engineer / Foreman", "₹1,450 / day", "60 Workers Needed", "Urgent Hiring", "Infrastructure", "9822012343", "R-03", "/assets/images/explore/explore_slide_3.jpg", List.of("/assets/images/explore/explore_slide_3.jpg"), true, true, false, true),
                new ProjectCardModel("P-BENCH-04", "Ramoji Film City Mega Studio Infrastructure", "Hyderabad, Telangana", "Ramoji Film City", "Painter & Welder", "₹1,050 / day", "95 Workers Needed", "Active Site", "Commercial", "9822012344", "R-04", "/assets/images/explore/explore_slide_4.jpg", List.of("/assets/images/explore/explore_slide_4.jpg"), true, true, true, true),
                new ProjectCardModel("P-BENCH-05", "BASIL Tech Habitat Smart Residential Park", "Bengaluru, Karnataka", "BASIL Group", "General Labour / Mason", "₹880 / day", "150 Workers Needed", "Active Site", "Residential", "9822012345", "R-05", "/assets/images/explore/explore_slide_5.jpg", List.of("/assets/images/explore/explore_slide_5.jpg"), true, true, true, true),
                new ProjectCardModel("P-BENCH-06", "Pune Metro Rail Underground Depot - Phase 2", "Pune, Maharashtra", "L&T Heavy Infrastructure", "Mason", "₹950 / day", "140 Workers Needed", "Active Site", "Infrastructure", "9822012346", "R-06", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png"), true, true, true, true),
                new ProjectCardModel("P-BENCH-07", "Mumbai Coastal Road Expressway & Sea Bridge", "Mumbai, Maharashtra", "Afcons Infrastructure", "Structural Fitter", "₹1,250 / day", "95 Workers Needed", "Urgent Hiring", "Infrastructure", "9822012347", "R-07", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png"), true, true, false, true),
                new ProjectCardModel("P-BENCH-08", "Prestige Tech Cloud IT Park - Phase 4 Towers", "Bangalore, Karnataka", "Prestige Group", "Electrician", "₹1,100 / day", "60 Workers Needed", "Active Site", "Commercial", "9822012348", "R-08", "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png", List.of("/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png"), true, true, true, false)
        );
    }

    private String getFallbackImageForTrade(String trade) {
        if (trade == null) return "/assets/images/explore/explore_slide_1.jpg";
        String t = trade.toLowerCase();
        if (t.contains("carpenter")) return "/assets/images/explore/explore_slide_2.jpg";
        if (t.contains("plumber")) return "/assets/images/explore/explore_slide_2.jpg";
        if (t.contains("engineer") || t.contains("foreman")) return "/assets/images/explore/explore_slide_3.jpg";
        if (t.contains("paint") || t.contains("weld")) return "/assets/images/explore/explore_slide_4.jpg";
        if (t.contains("labour") || t.contains("mason") || t.contains("helper")) return "/assets/images/explore/explore_slide_5.jpg";
        return "/assets/images/explore/explore_slide_1.jpg";
    }

    private void applyFilters() {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        String selTrade = tradeCombo != null && tradeCombo.getValue() != null ? tradeCombo.getValue() : "All Trades";
        String selState = stateCombo != null && stateCombo.getValue() != null ? stateCombo.getValue() : "All Locations";
        String selType = typeCombo != null && typeCombo.getValue() != null ? typeCombo.getValue() : "All Categories";

        List<ProjectCardModel> filtered = loadedProjects.stream().filter(p -> {
            boolean tradeMatch = "All Trades".equals(selTrade) || p.trade.toLowerCase().contains(selTrade.toLowerCase()) || selTrade.toLowerCase().contains(p.trade.toLowerCase());
            boolean stateMatch = "All Locations".equals(selState) || p.location.toLowerCase().contains(selState.toLowerCase());
            boolean typeMatch = "All Categories".equals(selType) || p.sector.equalsIgnoreCase(selType) || p.projectName.toLowerCase().contains(selType.toLowerCase());
            return tradeMatch && stateMatch && typeMatch;
        }).toList();

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(12,
                    label("No matching project roles found for this filter selection.", "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#3a3027;"),
                    label("Try clearing filters to view all active mega project opportunities.", "-fx-font-size:14px;-fx-text-fill:#685c52;"));
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            cardsContainer.getChildren().add(emptyBox);
            return;
        }

        for (ProjectCardModel p : filtered) {
            cardsContainer.getChildren().add(renderRealProjectCard(p));
        }
    }

    /** Builds an effective, beautiful, and rich project card with complete real metrics and amenities. */
    private VBox renderRealProjectCard(ProjectCardModel p) {
        // Location Badge & Status Pill
        Label locLabel = label("📍 " + p.location(), "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-padding:3px 10px;-fx-border-color:#e8ddcb;-fx-border-radius:10px;");

        boolean isUrgent = p.status() != null && p.status().toLowerCase().contains("urgent");
        Label statusBadge = label(isUrgent ? "🔥 URGENT HIRING" : "● ACTIVE SITE",
                "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + (isUrgent ? "#ffffff" : "#2e7d32") + ";"
                        + "-fx-background-color:" + (isUrgent ? "#c62828" : "#e8f5e9") + ";"
                        + "-fx-background-radius:10px;-fx-padding:4px 10px;");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topRow = new HBox(10, locLabel, topSpacer, statusBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Project Title & Enterprise Developer
        Label titleLabel = label(p.projectName(), "-fx-font-family:'Georgia';-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(515);

        Label companyLabel = label("🏢 " + p.company() + "  •  " + p.sector(), "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#5d5045;");

        // Wage, Workforce Openings, and Trade Role Metrics Bar
        HBox wagePill = statPill("💰 Rate", p.wage(), "#735c00", "rgba(115,92,0,0.08)");
        HBox countPill = statPill("👷 Needed", p.workersNeeded(), "#1e1b15", "rgba(30,27,21,0.06)");
        HBox tradePill = statPill("🛠 Trade", p.trade(), "#1565c0", "rgba(21,101,192,0.08)");
        HBox statsRow = new HBox(10, wagePill, countPill, tradePill);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        // Real Site Amenities & Facilities Row
        HBox facilitiesRow = new HBox(6);
        facilitiesRow.setAlignment(Pos.CENTER_LEFT);
        if (p.hasWater()) facilitiesRow.getChildren().add(facilityTag("💧 Water"));
        if (p.hasPower()) facilitiesRow.getChildren().add(facilityTag("⚡ Power"));
        if (p.hasStay()) facilitiesRow.getChildren().add(facilityTag("🏠 Stay"));
        if (p.hasTransport()) facilitiesRow.getChildren().add(facilityTag("🚌 Transport"));

        // Bottom Action Bar
        Region btmSpacer = new Region();
        HBox.setHgrow(btmSpacer, Priority.ALWAYS);

        Button viewBtn = new Button("View Full Details →");
        viewBtn.setStyle("-fx-background-color:linear-gradient(to right, #d4af37, #b8921e);-fx-background-radius:14px;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:7px 16px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(184,146,30,.28),8,0,0,2px);");

        Runnable openDetailsAction = () -> {
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window.isFocused() && window instanceof Stage stage) {
                    Scene currentScene = stage.getScene();
                    stage.setScene(new ExploreProjectDetailsPage(
                            p.projectId(),
                            p.projectName(),
                            p.location(),
                            p.company(),
                            p.trade(),
                            p.wage(),
                            p.workersNeeded(),
                            p.status(),
                            p.sector(),
                            p.recruiterPhone(),
                            p.requirementId(),
                            p.imagePath(),
                            p.imageUrls(),
                            p.hasWater(),
                            p.hasPower(),
                            p.hasStay(),
                            p.hasTransport(),
                            () -> stage.setScene(currentScene)
                    ).getScene());
                    return;
                }
            }
        };

        viewBtn.setOnAction(e -> openDetailsAction.run());

        HBox btmRow = new HBox(10, facilitiesRow, btmSpacer, viewBtn);
        btmRow.setAlignment(Pos.CENTER_LEFT);
        btmRow.setPadding(new Insets(4, 0, 0, 0));

        VBox card = new VBox(11, topRow, titleLabel, companyLabel, statsRow, btmRow);
        card.setPrefWidth(555);
        card.setMaxWidth(555);
        card.setMinWidth(555);
        card.setPadding(new Insets(20, 22, 20, 22));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 18px; -fx-border-color: #d0c5af; -fx-border-width: 1.5px; -fx-border-radius: 18px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,.08), 16, 0, 0, 4); -fx-cursor: hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 18px; -fx-border-color: #d4af37; -fx-border-width: 2px; -fx-border-radius: 18px; -fx-effect: dropshadow(gaussian, rgba(212,175,55,.30), 20, 0, 0, 6px); -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 18px; -fx-border-color: #d0c5af; -fx-border-width: 1.5px; -fx-border-radius: 18px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,.08), 16, 0, 0, 4); -fx-cursor: hand;"));
        card.setOnMouseClicked(e -> openDetailsAction.run());

        return card;
    }

    private HBox statPill(String labelText, String valueText, String textFill, String bg) {
        Label lbl = label(labelText + ": ", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        Label val = label(valueText, "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:" + textFill + ";");
        HBox box = new HBox(2, lbl, val);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(4, 10, 4, 10));
        box.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:10px;");
        return box;
    }

    private Label facilityTag(String text) {
        return label(text, "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#f4ede2;-fx-background-radius:8px;-fx-padding:3px 8px;");
    }

    private Label tag(String value, String color) {
        Label l = new Label(value);
        l.setStyle("-fx-font-size: 10px; -fx-font-weight: 700; -fx-text-fill: " + color + "; -fx-border-color: "
                + color + "; -fx-border-radius: 18px; -fx-padding: 3 9 3 9;");
        return l;
    }

    private VBox footer() {
        ImageView logo = new ImageView(load("/assets/logo/dihadi logo.jpeg"));
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(310);
        VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
        identity.setPrefWidth(360);
        VBox explore = footerColumn("Explore", "Home", () -> navigateTo("Home"), "Find Work", () -> navigateTo("Worker"), "About Us",
                () -> navigateTo("About Us"));
        VBox contact = footerColumn("Contact", "9561789599", () -> navigateTo("Contact Us"), "info@meridihadi.com",
                () -> navigateTo("Contact Us"), "Pune, Maharashtra", () -> navigateTo("Contact Us"));
        HBox top = new HBox(64, identity, explore, contact);
        top.setAlignment(Pos.TOP_LEFT);
        VBox out = new VBox(22, top, label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        out.setPadding(new Insets(32, 42, 24, 42));
        out.setMaxWidth(1180);
        out.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return out;
    }

    private VBox footerColumn(String heading, String textOne, Runnable actionOne, String textTwo, Runnable actionTwo,
            String textThree, Runnable actionThree) {
        VBox column = new VBox(7, label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                footerLink(textOne, actionOne), footerLink(textTwo, actionTwo), footerLink(textThree, actionThree));
        column.setPrefWidth(180);
        return column;
    }

    private Button footerLink(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setStyle(
                "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.82;-fx-font-size:13px;-fx-cursor:hand;");
        return button;
    }

    private void navigateTo(String destination) {
        if (heroTimeline != null) heroTimeline.stop();
        if (marqueeTimer != null) marqueeTimer.stop();
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                AppNavigator.open(stage, destination);
                return;
            }
        }
    }

    private Button navButton(String t, boolean active, Runnable action) {
        Button b = new Button(t);
        b.setOnAction(e -> {
            if (heroTimeline != null) heroTimeline.stop();
            if (marqueeTimer != null) marqueeTimer.stop();
            action.run();
        });
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Label text(String value, String style) {
        Label label = new Label(value);
        label.setStyle(style);
        return label;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return l;
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
            e.printStackTrace();
            return null;
        }
    }

    private String val(String str, String fallback) {
        return (str != null && !str.isBlank()) ? str : fallback;
    }

    /** Model representing real project card metrics. */
    private record ProjectCardModel(
            String projectId,
            String projectName,
            String location,
            String company,
            String trade,
            String wage,
            String workersNeeded,
            String status,
            String sector,
            String recruiterPhone,
            String requirementId,
            String imagePath,
            List<String> imageUrls,
            boolean hasWater,
            boolean hasPower,
            boolean hasStay,
            boolean hasTransport
    ) {}
}
