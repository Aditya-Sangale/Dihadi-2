package com.dihadi.view;

import com.dihadi.view.recruiter.SignUpRecruiter;
import com.dihadi.view.recruiter.RecruiterPage;
import com.dihadi.view.admin.AdminHomePage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Main application controller and premium DIHADI landing page. */
public class HomePage extends Application {
    private static final String[] HERO_IMAGES = {
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_crane_operator_inside_a_high_tech_cabin_overlooking_a_large_bridge/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_electrician_repairing_a_complex_electrical_panel_in_a_corporate/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/an_indian_site_supervisor_in_a_reflective_jacket_and_helmet_with_dihadi/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_female_factory_worker_at_a_high_precision_assembly_line/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_hvac_technician_servicing_a_large_air_conditioning_unit/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_plumber_working_on_industrial_piping._he_is_wearing_a/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_professional_indian_welder_working_in_an_industrial_workshop_wearing/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_an_indian_female_civil_engineer_on_a/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_high_quality_photograph_of_a_professional_indian_construction_mason/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photograph_of_an_indian_delivery_professional_with_a_modern/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_carpenter_in_a_woodworking_shop_wearing_a_dihadi/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_industrial_mechanic_repairing_a_large_gear/screen.png",
            "/assets/images/homepage-slider/stitch_dihadi_workforce_ecosystem/a_realistic_photo_of_an_indian_warehouse_worker_operating_a_forklift_in_a/screen.png"
    };

    private Stage primaryStage;
    private Timeline imageSlider;
    private ImageView heroImage;
    private int imageIndex;

    public HomePage() {
    }

    public HomePage(Stage stage) {
        primaryStage = stage;
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("DIHADI - Meri Dihadi ~ Mera Haq");
        primaryStage.setFullScreen(false);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(780);
        primaryStage.setMaximized(true);
        showWelcome();
        primaryStage.show();
    }

    /** Sets the WelcomePage scene on the same stage owned by HomePage. */
    private void showWelcome() {
        stopSlider();
        primaryStage.setScene(new WelcomePage().getWelcomeScene(this::showHome));
    }

    /** Used by WelcomePage after its welcome interaction. */
    public Scene getHomeScene() {
        stopSlider();
        return createHomeScene();
    }

    public void showHome() {
        stopSlider();
        if (primaryStage == null) {
            for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                if (w instanceof Stage s && s.isShowing()) {
                    primaryStage = s;
                    break;
                }
            }
        }
        if (primaryStage != null) {
            primaryStage.setScene(createHomeScene());
        }
    }

    private Scene createHomeScene() {
        VBox content = new VBox(26, createHero(), createActionSection(), createTrustSection(), createFooter());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(26, 24, 30, 24));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(createHeader());
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
        return new Scene(root, 1400, 780);
    }

    private BorderPane createHeader() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        prepareLogo(logo);
        Label name = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, name);
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox navigation = new HBox(12,
                navButton("Home", true, this::showHome),
                navButton("Business", false, this::showBusiness),
                navButton("Worker", false, this::showWorker),
                navButton("Recruiter", false, this::showRecruiterSignUp),
                navButton("About Us", false, this::showAbout),
                navButton("Contact Us", false, this::showContact));
        navigation.setAlignment(Pos.CENTER);

        Button admin = AppNavigator.createHeaderActionButton();
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(new HBox(10, admin));
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    private VBox createHero() {
        heroImage = image(HERO_IMAGES[0], 1130, 520);
        heroImage.setPreserveRatio(false);
        StackPane imageBox = new StackPane(heroImage);
        imageBox.setPrefSize(1130, 520);
        imageBox.setMaxWidth(1130);
        imageBox.setStyle(
                "-fx-background-color:#343027;-fx-background-radius:28px;-fx-border-color:#e8d7a8;-fx-border-radius:28px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.20),30,0,0,10px);");
        startSlider();

        Label eyebrow = label("TRUSTED WORKFORCE. REAL OPPORTUNITY.",
                "-fx-font-family:'Segoe UI Semibold';-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1.8px;");
        Label title = label("Build better days\nwith the right people.",
                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:#3a3027;-fx-line-spacing:4px;");
        Label intro = label(
                "From skilled jobs to dependable teams, DIHADI brings every workday closer to the people who make it happen.",
                "-fx-font-family:'Verdana';-fx-font-size:15px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        intro.setWrapText(true);
        intro.setMaxWidth(640);
        Button findJob = primaryButton("Find a Job");
        findJob.setOnAction(event -> showWorker());
        Button hireWorkers = outlineButton("Hire Workers");
        hireWorkers.setOnAction(event -> showRecruiterSignUp());

        VBox textPanel = new VBox(15, eyebrow, title, intro, new HBox(12, findJob, hireWorkers));
        textPanel.setAlignment(Pos.CENTER_LEFT);
        textPanel.setPadding(new Insets(32, 42, 34, 42));
        textPanel.setMaxWidth(1130);
        textPanel.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
        VBox hero = new VBox(18, imageBox, textPanel);
        hero.setAlignment(Pos.TOP_CENTER);
        return hero;
    }

    private VBox createActionSection() {
        Label title = label("A better day of work starts here",
                "-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label subtitle = label("Choose how you want to use DIHADI today.", "-fx-font-size:15px;-fx-text-fill:#4d4635;");
        FlowPane cards = new FlowPane(20, 20);
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
                actionCard("WORK", "Find Jobs", "Discover dignified work opportunities that match your skills.",
                        "Explore work", this::showWorker),
                actionCard("TEAM", "Hire Workers", "Connect with a verified, skilled, and reliable workforce.",
                        "Start hiring", this::showRecruiterSignUp),
                actionCard("BUILD", "Find Projects", "Explore contracting opportunities and build stronger teams.",
                        "Explore projects", this::showExploreProjects));
        VBox section = new VBox(10, title, subtitle, cards);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(34, 28, 38, 28));
        section.setMaxWidth(1180);
        section.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
        return section;
    }

    private VBox actionCard(String tag, String title, String description, String actionText, Runnable action) {
        Label chip = label(tag,
                "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;-fx-background-color:#f1dfd2;-fx-background-radius:999px;-fx-padding:8px 12px;");
        Label heading = label(title, "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label copy = label(description, "-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:2px;");
        copy.setWrapText(true);
        copy.setPrefWidth(250);
        Button actionButton = outlineButton(actionText);
        actionButton.setOnAction(event -> action.run());
        VBox card = new VBox(16, chip, heading, copy, actionButton);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setPrefSize(300, 245);
        card.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
        return card;
    }

    private VBox createTrustSection() {
        Label quote = label(
                "“Every worker deserves fair work, fair wages, and the dignity to build a better tomorrow.”",
                "-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#f8f0e2;-fx-line-spacing:3px;");
        quote.setWrapText(true);
        quote.setMaxWidth(590);
        VBox verified = stat("Verified", "WORKFORCE CONNECTIONS");
        VBox direct = stat("Direct", "NO MIDDLEMEN, MORE TRUST");
        HBox row = new HBox(52, quote, verified, direct);
        row.setAlignment(Pos.CENTER);
        VBox section = new VBox(row);
        section.setPadding(new Insets(28, 36, 28, 36));
        section.setMaxWidth(1180);
        section.setStyle("-fx-background-color:#343027;-fx-background-radius:22px;");
        return section;
    }

    private VBox stat(String value, String caption) {
        VBox box = new VBox(6, label(value, "-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                label(caption, "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#f8f0e2;-fx-letter-spacing:1px;"));
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox createFooter() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        prepareLogo(logo);
        Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(310);
        VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
        identity.setPrefWidth(360);
        VBox explore = footerColumn("Explore", "Home", this::showHome, "Find Work", this::showWorker, "About Us",
                this::showAbout);
        VBox contact = footerColumn("Contact", "9561789599", this::showContact, "info@meridihadi.com",
                this::showContact, "Pune, Maharashtra", this::showContact);
        HBox top = new HBox(64, identity, explore, contact);
        top.setAlignment(Pos.TOP_LEFT);
        VBox footer = new VBox(22, top,
                label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
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

    private Button navButton(String text, boolean active, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        String activeStyle = active
                ? "-fx-text-fill:#735c00;-fx-border-color:#735c00;-fx-border-width:0 0 2px 0;"
                : "-fx-text-fill:#4d4635;-fx-border-color:transparent;-fx-border-width:0 0 2px 0;";
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:8px 4px;-fx-cursor:hand;"
                        + activeStyle);
        return button;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:10px 20px;-fx-cursor:hand;");
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:999px;-fx-border-color:#806c47;-fx-border-radius:999px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:9px 18px;-fx-cursor:hand;");
        return button;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private ImageView image(String path, double width, double height) {
        Image image = new Image(getClass().getResource(path).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    private void prepareLogo(ImageView logo) {
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
    }

    private void showBusiness() {
        stopSlider();
        primaryStage.setScene(new BusinessPage().getBusinessScene(this::showHome, this::showWorker));
    }

    private void showExploreProjects() {
        stopSlider();
        primaryStage.setScene(new ExploreProjectsPage(this::showHome).getExploreProjectsScene());
    }

    private void showWorker() {
        stopSlider();
        if (SessionManager.currentWorker != null) {
            primaryStage.setScene(new com.dihadi.view.worker.WorkerDashboard(SessionManager.currentWorker).getScene(this::showHome));
            return;
        }
        if (!SessionManager.checkAccessAllowed(SessionManager.Role.WORKER)) {
            return;
        }
        primaryStage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(this::showHome));
    }

    private void showRecruiterSignUp() {
        stopSlider();
        if (SessionManager.currentRecruiter != null) {
            primaryStage.setScene(new com.dihadi.view.recruiter.RecruiterDashboard(SessionManager.currentRecruiter).getScene(this::showHome));
            return;
        }
        if (!SessionManager.checkAccessAllowed(SessionManager.Role.RECRUITER)) {
            return;
        }
        primaryStage.setScene(new SignUpRecruiter().getRecruiterSignUpScene(this::showHome));
    }

    private void showAbout() {
        stopSlider();
        primaryStage.setScene(new AboutUs().getAboutScene(this::showHome, this::showWorker));
    }

    private void showContact() {
        stopSlider();
        primaryStage.setScene(new ContactUs().getContactScene(this::showHome, this::showBusiness, this::showWorker, this::showAbout));
    }

    private void showAdmin() {
        stopSlider();
        if (SessionManager.currentAdmin != null) {
            primaryStage.setScene(new com.dihadi.view.admin.AdminDashboard().getDashboardScene(() -> {
                SessionManager.clearAllSessions();
                showHome();
            }));
            return;
        }
        if (!SessionManager.checkAccessAllowed(SessionManager.Role.ADMIN)) {
            return;
        }
        primaryStage.setScene(new AdminHomePage().getAdminHomeScene(this::showHome));
    }

    private void startSlider() {
        if (imageSlider != null) {
            imageSlider.stop();
        }
        imageSlider = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            imageIndex = (imageIndex + 1) % HERO_IMAGES.length;
            heroImage.setImage(new Image(getClass().getResource(HERO_IMAGES[imageIndex]).toExternalForm()));
        }));
        imageSlider.setCycleCount(Timeline.INDEFINITE);
        imageSlider.play();
    }

    private void stopSlider() {
        if (imageSlider != null) {
            imageSlider.stop();
        }
    }

    private void comingSoon(String title, String message) {
        NotificationToast.show(title, message, NotificationToast.ToastType.INFO);
    }
}
