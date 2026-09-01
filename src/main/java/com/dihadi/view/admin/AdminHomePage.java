package com.dihadi.view.admin;

import com.dihadi.view.AppNavigator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Enterprise Admin Portal Home Page.
 * Features executive administration architecture, live ecosystem metrics,
 * dynamic workforce showcase, and direct command center access.
 */
public class AdminHomePage {
    private static final double CONTENT_WIDTH = 1180;
    private static final String GOLD = "#735c00";
    private static final String ACCENT_GOLD = "#d4af37";

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
                    "Field-tested leadership overseeing site safety, attendance verification, and contractor compliance."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png",
                    "INDUSTRIAL ELECTRICIAN",
                    "Certified Industrial & Commercial Electricians",
                    "Specialized in three-phase power grids, high-voltage substations, and automated industrial panels."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_a_professional_indian_construction_mason/screen.png",
                    "MASTER MASON",
                    "Structural Masonry & Concrete Construction",
                    "Precision craftsmen commanding foundation casting, reinforced concrete, and industrial brickwork."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png",
                    "CRANE & HEAVY MACHINERY",
                    "Heavy Machinery & Tower Crane Operators",
                    "Certified operators managing tower cranes, bridge girders, and large-scale site earthmovers."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_welder_working_in_an_industrial_workshop_wearing/screen.png",
                    "PRECISION WELDER",
                    "Precision Arc, TIG & MIG Structural Welders",
                    "High-tolerance pipeline fabricators, pressure vessel welders, and industrial framework specialists."
            ),
            new SlideItem(
                    "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_plumber_working_on_industrial_piping._he_is_wearing_a/screen.png",
                    "PLUMBING & PIPING",
                    "Industrial & Commercial Piping Engineers",
                    "Specialists in high-pressure fluid networks, industrial drainage, and municipal conduit infrastructure."
            )
    };

    private Timeline workforceSlider;
    private int currentSlideIndex = 0;

    public Scene getAdminHomeScene(Runnable homeAction) {
        VBox pageContent = new VBox(32,
                createHero(),
                createWorkforceGallery(),
                createCorePillars(),
                createCommandPortals(),
                createPlatformWorkflow(),
                createOverviewMetrics(),
                createFooter()
        );
        pageContent.setAlignment(Pos.TOP_CENTER);
        pageContent.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));

        ScrollPane scroll = new ScrollPane(pageContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");

        BorderPane shell = new BorderPane(scroll);
        shell.setTop(createHeader(homeAction));

        StackPane root = new StackPane(shell);
        root.setPadding(new Insets(24));
        root.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
        return new Scene(root, 1400, 780);
    }

    private BorderPane createHeader(Runnable homeAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        Label name = label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, name);
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox nav = new HBox(12,
                nav("Home", false, homeAction),
                nav("Business", false, () -> go("Business")),
                nav("Worker", false, () -> go("Worker")),
                nav("Recruiter", false, () -> go("Recruiter")),
                nav("About Us", false, () -> go("About Us")),
                nav("Contact Us", false, () -> go("Contact Us"))
        );
        nav.setAlignment(Pos.CENTER);

        Button admin = new Button("Admin Control");
        admin.setStyle("-fx-background-color:#735c00;-fx-background-radius:18px;-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 20px;-fx-cursor:hand;");
        admin.setOnAction(e -> openAdminLogin(admin));

        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(nav);
        header.setRight(new HBox(10, admin));
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    private VBox createHero() {
        Label eyebrow = label("ENTERPRISE GOVERNANCE & CONTROL",
                "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.8px;-fx-text-fill:" + GOLD + ";-fx-background-color:#faf0dd;-fx-background-radius:20px;-fx-padding:6px 14px;-fx-border-color:#e2d4bd;-fx-border-radius:20px;");

        Label title = label("DIHADI Admin Command Center",
                "-fx-font-family:Georgia;-fx-font-size:44px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        Label subtitle = label(
                "Centralized governance platform for pan-India skilled workforce management, verified enterprise projects, automated escrow security, and dispute resolution.",
                "-fx-font-size:16px;-fx-text-fill:#4c4637;-fx-line-spacing:3px;-fx-text-alignment:center;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(820);

        Button loginBtn = action("Admin Login", true);
        loginBtn.setOnAction(e -> openAdminLogin(loginBtn));

        Button signupBtn = action("Admin Sign Up", false);
        signupBtn.setOnAction(e -> openAdminSignUp(signupBtn));

        HBox actions = new HBox(16, loginBtn, signupBtn);
        actions.setAlignment(Pos.CENTER);

        VBox hero = new VBox(16, eyebrow, title, subtitle, actions);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(42, 24, 20, 24));
        hero.setMaxWidth(CONTENT_WIDTH);
        return hero;
    }

    private VBox createWorkforceGallery() {
        ImageView slideImageView = new ImageView();
        slideImageView.setFitWidth(CONTENT_WIDTH);
        slideImageView.setFitHeight(460);
        slideImageView.setPreserveRatio(false);
        slideImageView.setSmooth(true);

        Label badgeLbl = new Label();
        badgeLbl.setStyle("-fx-background-color:rgba(212,175,55,0.25);-fx-border-color:#d4af37;-fx-border-radius:999px;-fx-background-radius:999px;-fx-text-fill:#ffd54f;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-padding:6px 14px;");

        Label titleLbl = new Label();
        titleLbl.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#ffffff;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.6),8,0,0,2px);");

        Label descLbl = new Label();
        descLbl.setStyle("-fx-font-size:14px;-fx-text-fill:#f5eedf;-fx-line-spacing:3px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.6),6,0,0,1px);");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(780);

        VBox textOverlay = new VBox(8, badgeLbl, titleLbl, descLbl);
        textOverlay.setAlignment(Pos.BOTTOM_LEFT);
        textOverlay.setPadding(new Insets(30, 38, 30, 38));
        textOverlay.setMaxWidth(CONTENT_WIDTH);

        StackPane gradientScrim = new StackPane();
        gradientScrim.setPrefSize(CONTENT_WIDTH, 460);
        gradientScrim.setStyle("-fx-background-color:linear-gradient(to top, rgba(16,13,9,0.92) 0%, rgba(16,13,9,0.55) 45%, rgba(16,13,9,0.12) 75%, transparent 100%);");

        Runnable[] updateSlide = new Runnable[1];
        updateSlide[0] = () -> {
            SlideItem item = WORKFORCE_SLIDES[currentSlideIndex];
            Image img = loadImage(item.imagePath);
            if (img != null) {
                slideImageView.setImage(img);
            }
            badgeLbl.setText(item.badge);
            titleLbl.setText(item.role);
            descLbl.setText(item.tagline);
        };

        updateSlide[0].run();

        BorderPane bottomBar = new BorderPane();
        bottomBar.setLeft(textOverlay);
        bottomBar.setPickOnBounds(false);

        StackPane card = new StackPane(slideImageView, gradientScrim, bottomBar);
        card.setPrefSize(CONTENT_WIDTH, 460);
        card.setMinSize(CONTENT_WIDTH, 460);
        card.setMaxSize(CONTENT_WIDTH, 460);
        Rectangle clip = new Rectangle(CONTENT_WIDTH, 460);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        card.setClip(clip);
        card.setStyle("-fx-background-color:#1e1b15;-fx-background-radius:24px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.22),24,0,0,8px);");

        if (workforceSlider != null) workforceSlider.stop();
        workforceSlider = new Timeline(new KeyFrame(Duration.millis(2400), event -> {
            currentSlideIndex = (currentSlideIndex + 1) % WORKFORCE_SLIDES.length;
            updateSlide[0].run();
        }));
        workforceSlider.setCycleCount(Timeline.INDEFINITE);
        workforceSlider.play();

        card.setOnMouseEntered(e -> workforceSlider.pause());
        card.setOnMouseExited(e -> workforceSlider.play());

        VBox gallerySection = new VBox(card);
        gallerySection.setAlignment(Pos.CENTER);
        gallerySection.setMaxWidth(CONTENT_WIDTH);
        return gallerySection;
    }

    private HBox createCorePillars() {
        HBox trustPillars = new HBox(16,
                pillarBadge("100% KYC Verified", "Multi-tier identity and background authentication across all registered trades."),
                pillarBadge("Live Attendance", "Automated biometric and geo-fenced daily shifts and work verification."),
                pillarBadge("Pan-India Sites", "Strategic deployment spanning commercial, residential, and mega infrastructure."),
                pillarBadge("Escrow Security", "Protected daily wage payouts with zero leakage and guaranteed settlements.")
        );
        trustPillars.setAlignment(Pos.CENTER);
        trustPillars.setMaxWidth(CONTENT_WIDTH);
        for (javafx.scene.Node n : trustPillars.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return trustPillars;
    }

    private VBox pillarBadge(String title, String subtitle) {
        Label titleLbl = label(title, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label subLbl = label(subtitle, "-fx-font-size:12px;-fx-text-fill:#685c52;-fx-line-spacing:2px;");
        subLbl.setWrapText(true);
        VBox box = new VBox(5, titleLbl, subLbl);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(16, 18, 16, 18));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),10,0,0,2px);");
        return box;
    }

    private VBox createCommandPortals() {
        Label heading = label("Core Administration Modules",
                "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        FlowPane cards = new FlowPane(20, 20,
                adminPortalCard("WORKFORCE", "Workforce Governance", "Manage onboarding, verify Aadhaar KYC, audit trade certifications, and regulate active worker pools.", "Access Workforce"),
                adminPortalCard("PROJECTS", "Enterprise Projects", "Track live site requirements, monitor contractor daily wages, and audit active infrastructure projects.", "Access Projects"),
                adminPortalCard("ESCROW", "Financial & Payouts", "Oversee daily transaction volume, escrow deposits, wage settlements, and contractor billing.", "Access Financials"),
                adminPortalCard("DISPUTES", "Grievance Resolution", "Investigate workplace safety breaches, payment escalations, and contractor compliance cases.", "Access Grievances")
        );
        cards.setAlignment(Pos.CENTER);

        VBox section = new VBox(24, heading, cards);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(10, 24, 30, 24));
        section.setMaxWidth(CONTENT_WIDTH);
        return section;
    }

    private VBox adminPortalCard(String tag, String title, String text, String cta) {
        Label badge = label(tag,
                "-fx-background-color:#faf0dd;-fx-background-radius:6px;-fx-text-fill:#735c00;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:4px 10px;-fx-border-color:#ebdccb;-fx-border-radius:6px;");

        Label heading = label(title, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label copy = label(text, "-fx-font-size:12px;-fx-text-fill:#4c4637;-fx-line-spacing:2px;");
        copy.setWrapText(true);
        copy.setMaxWidth(235);

        Button ctaBtn = new Button(cta + " ->");
        ctaBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:4px 0;-fx-cursor:hand;");
        ctaBtn.setOnAction(e -> openAdminLogin(ctaBtn));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(10, badge, heading, copy, spacer, ctaBtn);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefSize(275, 205);
        card.setPadding(new Insets(20, 22, 18, 22));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),12,0,0,3px);-fx-cursor:hand;");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),16,0,0,5px);-fx-cursor:hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#e5d9c7;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),12,0,0,3px);"));
        card.setOnMouseClicked(e -> openAdminLogin(ctaBtn));

        return card;
    }

    private VBox createPlatformWorkflow() {
        Label title = label("Unified Ecosystem Architecture",
                "-fx-font-family:Georgia;-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");

        HBox steps = new HBox(32,
                workflowStep("1. Supply", "Workforce Sourcing"),
                workflowStep("2. Verify", "KYC & Skill Audit"),
                workflowStep("3. Match", "Project Placement"),
                workflowStep("4. Demand", "Live Contractor Quota"),
                workflowStep("5. Escrow", "Guaranteed Settlement")
        );
        steps.setAlignment(Pos.CENTER);

        VBox section = new VBox(28, title, steps);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(42, 24, 42, 24));
        section.setMaxWidth(CONTENT_WIDTH);
        section.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:20px;-fx-border-color:#ebdccb;-fx-border-radius:20px;");
        return section;
    }

    private VBox workflowStep(String stepTitle, String subtitle) {
        Label stepNum = label(stepTitle, "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Label desc = label(subtitle, "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#4c4637;");
        VBox box = new VBox(4, stepNum, desc);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(185, 75);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:#d4af37;-fx-border-width:1.25px;-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");
        return box;
    }

    private VBox createOverviewMetrics() {
        Label heading = label("Ecosystem Overview", "-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        HBox stats = new HBox(60,
                statistic("25,000+", "REGISTERED WORKERS"),
                statistic("1,840+", "VERIFIED CONTRACTORS"),
                statistic("740+", "ACTIVE PROJECTS"),
                statistic("99.4%", "ESCROW SETTLEMENT")
        );
        stats.setAlignment(Pos.CENTER);

        VBox panel = new VBox(20, heading, stats);
        panel.setPadding(new Insets(28, 36, 28, 36));
        panel.setMaxWidth(CONTENT_WIDTH);
        panel.setStyle("-fx-background-color:#272727;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,.25),16,0,0,6px);");

        VBox section = new VBox(panel);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20, 24, 35, 24));
        return section;
    }

    private VBox statistic(String number, String name) {
        Label numLbl = label(number, "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#ffffff;");
        Label nameLbl = label(name, "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#e9c349;");
        VBox box = new VBox(4, numLbl, nameLbl);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(180);
        return box;
    }

    private VBox createFooter() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(310);
        VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
        identity.setPrefWidth(360);
        VBox explore = footerColumn("Explore", "Home", () -> go("Home"), "Find Work", () -> go("Worker"), "About Us",
                () -> go("About Us"));
        VBox contact = footerColumn("Contact", "9561789599", () -> go("Contact Us"), "info@meridihadi.com",
                () -> go("Contact Us"), "Pune, Maharashtra", () -> go("Contact Us"));
        HBox top = new HBox(64, identity, explore, contact);
        top.setAlignment(Pos.TOP_LEFT);
        VBox footer = new VBox(22, top, label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        footer.setPadding(new Insets(32, 42, 24, 42));
        footer.setMaxWidth(1180);
        footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return footer;
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

    private Button nav(String name, boolean active, Runnable action) {
        Button button = new Button(name);
        button.setOnAction(event -> {
            if (workforceSlider != null) workforceSlider.stop();
            action.run();
        });
        button.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;"
                + (active ? "-fx-text-fill:#735c00;-fx-border-color:#735c00;" : "-fx-text-fill:#4d4635;-fx-border-color:transparent;"));
        return button;
    }

    private Button action(String name, boolean filled) {
        Button button = new Button(name);
        button.setStyle("-fx-background-radius:99px;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:12px 36px;-fx-cursor:hand;"
                + (filled ? "-fx-background-color:linear-gradient(to right, #735c00, #5a4800);-fx-text-fill:white;-fx-effect:dropshadow(gaussian,rgba(115,92,0,.35),10,0,0,2px);"
                        : "-fx-background-color:#ffffff;-fx-border-color:#735c00;-fx-border-width:1.5px;-fx-border-radius:99px;-fx-text-fill:#735c00;"));
        return button;
    }

    private ImageView image(String path, double width, double height) {
        try {
            var r = getClass().getResource(path);
            if (r == null) return new ImageView();
            ImageView view = new ImageView(new Image(r.toExternalForm()));
            view.setFitWidth(width);
            view.setFitHeight(height);
            view.setPreserveRatio(path.contains("/assets/logo/"));
            view.setSmooth(true);
            return view;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private Image loadImage(String path) {
        try {
            var r = getClass().getResource(path);
            if (r != null) return new Image(r.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private void go(String destination) {
        if (workforceSlider != null) workforceSlider.stop();
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                AppNavigator.open(stage, destination);
                return;
            }
        }
    }

    private void openAdminSignUp(Button source) {
        if (workforceSlider != null) workforceSlider.stop();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new AdminSignUpPage().getAdminSignUpScene(
                () -> stage.setScene(getAdminHomeScene(() -> AppNavigator.open(stage, "Home")))));
    }

    private void openAdminLogin(Button source) {
        if (workforceSlider != null) workforceSlider.stop();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new AdminLoginPage().getAdminLoginScene(
                () -> stage.setScene(getAdminHomeScene(() -> AppNavigator.open(stage, "Home")))));
    }
}
