package com.dihadi.view.admin;

import com.dihadi.view.AppNavigator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Full desktop Admin Control Center, based on the supplied DIHADI design. */
public class AdminHomePage {
    private static final double CONTENT_WIDTH = 1180;
    private static class SlideItem {
        final String imagePath;
        final String badge;
        final String role;
        final String tagline;

        SlideItem(String imagePath, String badge, String role, String tagline) {
            this.imagePath = imagePath;
            this.badge = badge;
            this.role = role;
            this.tagline = tagline;
        }
    }

    private static final SlideItem[] WORKFORCE_SLIDES = {
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png",
                    "SITE SUPERVISOR",
                    "Site Operations & Workforce Coordination",
                    "Field-tested leaders managing construction workflows, on-site safety, and project compliance."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png",
                    "INDUSTRIAL ELECTRICIAN",
                    "Certified Industrial & Commercial Electricians",
                    "Specialized in high-voltage panels, three-phase power grids, switchgears, and building automation."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_a_professional_indian_construction_mason/screen.png",
                    "MASTER MASON",
                    "Structural Masonry & Concrete Construction",
                    "Master craftsmen in foundation laying, reinforced brickwork, stone masonry, and precision finish."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png",
                    "CRANE & HEAVY MACHINERY",
                    "Heavy Machinery & Tower Crane Operators",
                    "Certified equipment operators commanding bridge construction, tower cranes, and site earthmovers."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_welder_working_in_an_industrial_workshop_wearing/screen.png",
                    "PRECISION WELDER",
                    "Precision Arc, TIG & MIG Welders",
                    "High-tolerance structural steel fabricators, pipeline welders, and industrial fabrication specialists."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_plumber_working_on_industrial_piping._he_is_wearing_a/screen.png",
                    "PLUMBING & PIPING",
                    "Industrial & Commercial Piping Engineers",
                    "Experts in high-pressure supply systems, drainage infrastructure, and commercial fluid conduits."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_carpenter_in_a_woodworking_shop_wearing_a_dihadi/screen.png",
                    "JOINERY & CARPENTRY",
                    "Architectural Framework & Finish Joinery",
                    "Master formwork carpenters, interior joiners, custom framework builders, and shuttering specialists."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_female_factory_worker_at_a_high_precision_assembly_line/screen.png",
                    "PRECISION MANUFACTURING",
                    "Advanced Manufacturing & Assembly Workforce",
                    "Skilled line technicians driving precision assembly, testing protocols, and industrial manufacturing."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_hvac_technician_servicing_a_large_air_conditioning_unit/screen.png",
                    "HVAC & CLIMATE SYSTEMS",
                    "Commercial HVAC & Industrial Cooling",
                    "Trained technicians servicing centralized air handling units, chillers, and industrial ventilation."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_warehouse_worker_operating_a_forklift_in_a/screen.png",
                    "LOGISTICS & WAREHOUSE",
                    "Warehouse Logistics & Material Handlers",
                    "Forklift operators and inventory crews maintaining smooth dispatch and supply chain mobility."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_an_indian_female_civil_engineer_on_a/screen.png",
                    "CIVIL ENGINEERING",
                    "Civil Architecture & Quality Assurance",
                    "Technical engineers supervising structural integrity, quality testing, and architectural blueprints."
            )
    };

    private Timeline workforceSlider;
    private int currentSlideIndex = 0;

    public Scene getAdminHomeScene(Runnable homeAction) {
        VBox pageContent = new VBox(createHero(), createWorkforceGallery(), createQuoteAndActions(), createAdministration(),
                createPlatformFlow(), createOverview(), createFooter());
        pageContent.setAlignment(Pos.TOP_CENTER);
        pageContent.setBackground(new Background(new BackgroundFill(Color.web("#fff8f0"), CornerRadii.EMPTY, Insets.EMPTY)));
        ScrollPane scroll = new ScrollPane(pageContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane shell = new BorderPane(scroll);
        shell.setTop(createHeader(homeAction));
        StackPane root = new StackPane(shell);
        root.setBackground(new Background(new BackgroundFill(Color.web("#fff8f0"), CornerRadii.EMPTY, Insets.EMPTY)));
        return new Scene(root, 1400, 780);
    }

    private BorderPane createHeader(Runnable homeAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 60, 60);
        Label name = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:24px;-fx-text-fill:#3a3027;");
        HBox brand = new HBox(10, logo, name); brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(27, nav("Home", true, homeAction), nav("Business", false, () -> go("Business")),
                nav("Worker", false, () -> go("Worker")), nav("Recruiter", false, () -> go("Recruiter")),
                nav("About Us", false, () -> go("About Us")), nav("Contact Us", false, () -> go("Contact Us")));
        nav.setAlignment(Pos.CENTER);
        Button admin = new Button("Admin"); admin.setDisable(true);
        admin.setStyle("-fx-background-color:#d4af37;-fx-background-radius:99px;-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8px 22px;");
        BorderPane header = new BorderPane(); header.setLeft(brand); header.setCenter(nav); header.setRight(admin);
        header.setPadding(new Insets(15, 80, 15, 80));
        header.setStyle("-fx-background-color:#fff8f0;-fx-border-color:#eee7dc;-fx-border-width:0 0 1px 0;");
        return header;
    }

    private VBox createHero() {
        Label title = label("ADMIN CONTROL CENTER", "-fx-font-family:Georgia;-fx-font-size:46px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label subtitle = label("Empowering trusted workforce management through transparency, technology and dignity.",
                "-fx-font-size:17px;-fx-text-fill:#4c4637;");
        VBox hero = new VBox(18, title, subtitle); hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(60, 24, 40, 24)); hero.setMaxWidth(CONTENT_WIDTH);
        return hero;
    }

    private VBox createWorkforceGallery() {
        ImageView slideImageView = new ImageView();
        slideImageView.setFitWidth(CONTENT_WIDTH);
        slideImageView.setFitHeight(480);
        slideImageView.setPreserveRatio(false);
        slideImageView.setSmooth(true);

        Label badgeLbl = new Label();
        badgeLbl.setStyle("-fx-background-color:rgba(212,175,55,0.22);-fx-border-color:#d4af37;-fx-border-radius:999px;-fx-background-radius:999px;-fx-text-fill:#ffd54f;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-padding:6px 14px;");

        Label titleLbl = new Label();
        titleLbl.setStyle("-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.6),8,0,0,2px);");

        Label descLbl = new Label();
        descLbl.setStyle("-fx-font-size:15px;-fx-text-fill:#f5eedf;-fx-line-spacing:3px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.6),6,0,0,1px);");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(800);

        VBox textOverlay = new VBox(8, badgeLbl, titleLbl, descLbl);
        textOverlay.setAlignment(Pos.BOTTOM_LEFT);
        textOverlay.setPadding(new Insets(32, 42, 32, 42));
        textOverlay.setMaxWidth(CONTENT_WIDTH);

        // Bottom gradient scrim
        StackPane gradientScrim = new StackPane();
        gradientScrim.setPrefSize(CONTENT_WIDTH, 480);
        gradientScrim.setStyle("-fx-background-color:linear-gradient(to top, rgba(16,13,9,0.92) 0%, rgba(16,13,9,0.60) 40%, rgba(16,13,9,0.15) 75%, transparent 100%);");

        Runnable[] updateSlide = new Runnable[1];
        updateSlide[0] = () -> {
            SlideItem item = WORKFORCE_SLIDES[currentSlideIndex];
            Image img = loadImage(item.imagePath);
            if (img != null) {
                slideImageView.setImage(img);
            }
            badgeLbl.setText("✦ " + item.badge);
            titleLbl.setText(item.role);
            descLbl.setText(item.tagline);
        };

        updateSlide[0].run();

        BorderPane bottomBar = new BorderPane();
        bottomBar.setLeft(textOverlay);
        bottomBar.setPickOnBounds(false);

        StackPane card = new StackPane(slideImageView, gradientScrim, bottomBar);
        card.setPrefSize(CONTENT_WIDTH, 480);
        card.setMinSize(CONTENT_WIDTH, 480);
        card.setMaxSize(CONTENT_WIDTH, 480);
        Rectangle clip = new Rectangle(CONTENT_WIDTH, 480);
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        card.setClip(clip);
        card.setStyle("-fx-background-color:#1e1b15;-fx-background-radius:28px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:28px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.22),24,0,0,8px);");

        // Autoplay Timeline with fast, dynamic image transitions
        if (workforceSlider != null) workforceSlider.stop();
        workforceSlider = new Timeline(new KeyFrame(Duration.millis(1800), event -> {
            currentSlideIndex = (currentSlideIndex + 1) % WORKFORCE_SLIDES.length;
            updateSlide[0].run();
        }));
        workforceSlider.setCycleCount(Timeline.INDEFINITE);
        workforceSlider.play();

        card.setOnMouseEntered(e -> workforceSlider.pause());
        card.setOnMouseExited(e -> workforceSlider.play());

        // 4 trust pillars below the showcase
        HBox trustPillars = new HBox(18,
                pillarBadge("🛡️", "100% KYC Verified", "Background-checked workers"),
                pillarBadge("⚡", "Live Attendance Tracking", "Automated daily logging"),
                pillarBadge("📍", "Pan-India Coverage", "Skilled workforce across states"),
                pillarBadge("💳", "Escrow Wage Security", "Protected daily wage payouts")
        );
        trustPillars.setAlignment(Pos.CENTER);
        trustPillars.setMaxWidth(CONTENT_WIDTH);
        for (javafx.scene.Node n : trustPillars.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        VBox gallerySection = new VBox(22, card, trustPillars);
        gallerySection.setAlignment(Pos.CENTER);
        gallerySection.setMaxWidth(CONTENT_WIDTH);
        return gallerySection;
    }

    private Image loadImage(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new Image(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private VBox pillarBadge(String icon, String title, String subtitle) {
        Label iconLbl = label(icon, "-fx-font-size:20px;");
        Label titleLbl = label(title, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label subLbl = label(subtitle, "-fx-font-size:12px;-fx-text-fill:#685c52;");
        VBox box = new VBox(4, iconLbl, titleLbl, subLbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(14, 18, 14, 18));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,2px);");
        return box;
    }

    private VBox createQuoteAndActions() {
        Label marks = label("“”,", "-fx-font-family:Georgia;-fx-font-size:105px;-fx-text-fill:#eee7dc;");
        Label quote = label("\"Value for Time. Skill. Money. A trusted portal that gives labourers the right to\nwork, the value of their skill, and the dignity they deserve.\"",
                "-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-style:italic;-fx-text-fill:#1e1b15;-fx-text-alignment:center;");
        StackPane quoteBox = new StackPane(marks, quote); quoteBox.setPrefHeight(205);
        Button login = action("ADMIN LOGIN", true);
        login.setOnAction(event -> openAdminLogin(login));
        Button signup = action("ADMIN SIGN UP", false);
        signup.setOnAction(event -> openAdminSignUp(signup));
        HBox actions = new HBox(20, login, signup); actions.setAlignment(Pos.CENTER);
        VBox block = new VBox(quoteBox, actions); block.setAlignment(Pos.CENTER);
        block.setPadding(new Insets(35, 24, 90, 24)); block.setMaxWidth(CONTENT_WIDTH);
        return block;
    }

    private VBox createAdministration() {
        Label title = label("DIHADI ADMINISTRATION", "-fx-font-family:Georgia;-fx-font-size:38px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label goldLine = new Label(); goldLine.setPrefSize(70, 3); goldLine.setStyle("-fx-background-color:#d4af37;");
        FlowPane cards = new FlowPane(20, 20, managementCard("WORKFORCE", "Workforce Management", "Onboard and verify skilled workers safely."),
                managementCard("DEMAND", "Labour Demand", "Track geographic requirements and project needs."),
                managementCard("MATCH", "Smart Matching", "Connect verified skills with active requirements."),
                managementCard("TRUST", "Trust & Transparency", "Ensure compliance and secure payments."));
        cards.setAlignment(Pos.CENTER);
        VBox section = new VBox(15, title, goldLine, new VBox(45, cards)); section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(0, 24, 92, 24)); section.setMaxWidth(CONTENT_WIDTH);
        return section;
    }

    private VBox managementCard(String tag, String title, String text) {
        Button badge = new Button(tag); badge.setOnAction(event -> showAction(title));
        badge.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:99px;-fx-text-fill:#a77f00;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:12px 15px;");
        Label heading = label(title, "-fx-font-size:18px;-fx-font-weight:700;-fx-text-fill:#1e1b15;-fx-text-alignment:center;");
        Label copy = label(text, "-fx-font-size:13px;-fx-text-fill:#4c4637;-fx-text-alignment:center;"); copy.setWrapText(true); copy.setMaxWidth(225);
        VBox card = new VBox(17, badge, heading, copy); card.setAlignment(Pos.CENTER); card.setPrefSize(265, 205);
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:#e9e2d7;-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),12,.15,0,3px);");
        return card;
    }

    private VBox createPlatformFlow() {
        Label title = label("ONE PLATFORM. COMPLETE CONTROL.", "-fx-font-family:Georgia;-fx-font-size:38px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        HBox steps = new HBox(42,
                step(icon("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zM8 11c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C15 14.17 10.33 13 8 13zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"), "Supply"),
                step(icon("M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-8 14-4-4 1.41-1.41L11 14.17l4.59-4.59L17 11l-6 6z"), "Verify"),
                step(icon("M7 7l-4 4 4 4v-3h7v-2H7V7zm10 2h-7v2h7v3l4-4-4-4v3z"), "Match"),
                step(icon("M20 6h-4V4c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10-2h4v2h-4V4zm10 14H4v-5h6v1h4v-1h6v5z"), "Demand"),
                step(icon("M3 3h18v18H3V3zm2 2v14h14V5H5zm3 10h2v2H8v-2zm3-4h2v6h-2v-6zm3-3h2v9h-2V8z"), "Deploy"));
        steps.setAlignment(Pos.CENTER);
        VBox section = new VBox(48, title, steps); section.setAlignment(Pos.CENTER); section.setPadding(new Insets(76, 24, 76, 24));
        section.setMaxWidth(Double.MAX_VALUE); section.setStyle("-fx-background-color:#faf3e8;"); return section;
    }

    private VBox step(javafx.scene.Node icon, String title) {
        Label name = label(title, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#3a3027;");
        VBox circle = new VBox(13, icon, name);
        circle.setAlignment(Pos.CENTER);
        circle.setPrefSize(160, 160);
        circle.setMinSize(160, 160);
        circle.setMaxSize(160, 160);
        circle.setStyle("-fx-background-color:#fffdf9;-fx-background-radius:100px;"
                + "-fx-border-color:#d4af37;-fx-border-radius:100px;-fx-border-width:1.25px;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,.12,0,2px);");
        return circle;
    }

    private StackPane icon(String svgPath) {
        javafx.scene.shape.SVGPath path = new javafx.scene.shape.SVGPath();
        path.setContent(svgPath);
        path.setFill(Color.web("#c79b16"));
        path.setScaleX(1.18);
        path.setScaleY(1.18);
        StackPane icon = new StackPane(path);
        icon.setPrefSize(32, 32);
        return icon;
    }

    private VBox createOverview() {
        Label heading = label("System Overview", "-fx-font-size:20px;-fx-text-fill:#e3c466;");
        HBox stats = new HBox(78, statistic("25K+", "TOTAL WORKERS"), statistic("12K", "AVAILABLE NOW"), statistic("850", "ACTIVE PROJECTS"), statistic("99%", "MATCH RATE")); stats.setAlignment(Pos.CENTER);
        VBox panel = new VBox(30, heading, stats); panel.setPadding(new Insets(30, 42, 30, 42)); panel.setMaxWidth(CONTENT_WIDTH);
        panel.setStyle("-fx-background-color:#3a3027;-fx-background-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),12,.18,0,4px);");
        VBox section = new VBox(panel); section.setAlignment(Pos.CENTER); section.setPadding(new Insets(55, 24, 55, 24)); return section;
    }

    private VBox statistic(String number, String name) { VBox box = new VBox(3, label(number, "-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:white;"), label(name, "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#f7f0e5;")); box.setAlignment(Pos.CENTER); box.setPrefWidth(155); return box; }

    private VBox createFooter() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 52);
        VBox brand = new VBox(13, logo, label("Mera Haq ~ Meri Dihadi. Empowering India's skilled\nworkforce with dignity, transparency, and opportunity.", "-fx-font-size:12px;-fx-text-fill:#f7f0e5;")); brand.setPrefWidth(320);
        VBox links = column("Links", "Company\n\nOpportunities\n\nLegal\n\nAddresses\n\nContact");
        VBox address = column("Pune Headquarters", "⌖  Tech Park One, Yerawada, Pune, Maharashtra 411006\n\n✉  admin@dihadi.com");
        HBox top = new HBox(75, brand, links, address); top.setAlignment(Pos.TOP_LEFT);
        VBox footer = new VBox(44, top, label("© 2024 DIHADI. Mera Haq ~ Meri Dihadi. All Rights Reserved.", "-fx-font-size:11px;-fx-text-fill:#f7f0e5;")); footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(46, 80, 28, 80)); footer.setMaxWidth(Double.MAX_VALUE); footer.setStyle("-fx-background-color:#3a3027;"); return footer;
    }

    private VBox column(String heading, String body) { VBox column = new VBox(15, label(heading, "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:#ffe085;"), label(body, "-fx-font-size:12px;-fx-text-fill:#f7f0e5;")); column.setPrefWidth(245); return column; }
    private Button nav(String name, boolean active, Runnable action) { Button button = new Button(name); button.setOnAction(event -> action.run()); button.setStyle("-fx-background-color:transparent;-fx-text-fill:" + (active ? "#1e1b15" : "#4c4637") + ";-fx-font-size:12px;-fx-padding:6px 1px;" + (active ? "-fx-border-color:#1e1b15;-fx-border-width:0 0 1px 0;" : "")); return button; }
    private Button action(String name, boolean filled) { Button button = new Button(name); button.setStyle("-fx-background-radius:99px;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:13px 42px;" + (filled ? "-fx-background-color:#3a3027;-fx-text-fill:white;" : "-fx-background-color:#fff8f0;-fx-border-color:#d4af37;-fx-border-radius:99px;-fx-text-fill:#1e1b15;")); return button; }
    private ImageView image(String path, double width, double height) { ImageView view = new ImageView(new Image(getClass().getResource(path).toExternalForm())); view.setFitWidth(width); view.setFitHeight(height); view.setPreserveRatio(false); view.setSmooth(true); return view; }
    private Label label(String text, String style) { Label label = new Label(text); label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style); return label; }
    private void go(String destination) { for (javafx.stage.Window window : javafx.stage.Window.getWindows()) if (window.isFocused() && window instanceof Stage stage) { AppNavigator.open(stage, destination); return; } }
    private void openAdminSignUp(Button source) {
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new AdminSignUpPage().getAdminSignUpScene(
                () -> stage.setScene(getAdminHomeScene(() -> AppNavigator.open(stage, "Home")))));
    }
    private void openAdminLogin(Button source) {
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new AdminLoginPage().getAdminLoginScene(
                () -> stage.setScene(getAdminHomeScene(() -> AppNavigator.open(stage, "Home")))));
    }
    private void showAction(String action) { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle(action); alert.setHeaderText(action); alert.setContentText("This admin action is ready to connect to the corresponding administration workflow."); alert.show(); }
}
