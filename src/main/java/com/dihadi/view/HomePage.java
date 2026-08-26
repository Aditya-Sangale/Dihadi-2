package com.dihadi.view;

import com.dihadi.view.recruiter.HireSuitableSkilledWorkersPage;
import com.dihadi.view.recruiter.SignUpRecruiter;
import com.dihadi.view.recruiter.RecruiterPage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Main application controller and premium DIHADI landing page. */
public class HomePage extends Application {
    private static final String[] HERO_IMAGES = {
            "/assets/images/worker 1 (2).jpeg", "/assets/images/worker 2.jpeg",
            "/assets/images/worker 5.jpeg", "/assets/images/woker 6.jpeg",
            "/assets/images/sitesuperviser.jpeg", "/assets/images/welder.jpeg"
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
        primaryStage.setTitle("DIHADI - Mera Haq ~ Meri Dihadi");
        primaryStage.setFullScreen(false);
        primaryStage.setWidth(1400);
        primaryStage.setHeight(780);
        primaryStage.setMaximized(true);
        showWelcome();
    }

    /** Opens the existing WelcomePage with the Stage owned by HomePage. */
    private void showWelcome() {
        stopSlider();
        new WelcomePage().show(primaryStage, this::showHome);
    }

    /** Used by WelcomePage after its welcome interaction. */
    public Scene getHomeScene() {
        stopSlider();
        return createHomeScene();
    }

    public void showHome() {
        stopSlider();
        primaryStage.setScene(createHomeScene());
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

        Button admin = primaryButton("Admin");
        admin.setOnAction(event -> AppNavigator.adminLoginInProgress());
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
                        "Explore projects",
                        () -> comingSoon("Find Projects", "Business project listings are coming soon.")));
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
        VBox footer = new VBox(22, top, label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
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

    /** Worker navigation and job discovery begin with the worker profile form. */
    private void showWorker() {
        stopSlider();
        AppNavigator.signUp(primaryStage, null);
    }

    private void showBusiness() {
        stopSlider();
        primaryStage.setScene(new BusinessPage().getBusinessScene(this::showHome, this::showWorker));
    }

    private void showRecruiter() {
        stopSlider();
        primaryStage.setScene(new RecruiterPage().getRecruiterScene(this::showHome));
    }

    private void showRecruiterSignUp() {
        stopSlider();
        primaryStage.setScene(new SignUpRecruiter().getRecruiterSignUpScene(this::showRecruiter));
    }

    private void showAbout() {
        stopSlider();
        primaryStage.setScene(new AboutUs().getAboutScene(this::showHome, this::showWorker));
    }

    private void showContact() {
        stopSlider();
        primaryStage.setScene(
                new ContactUs().getContactScene(this::showHome, this::showBusiness, this::showWorker, this::showAbout));
    }

    private void comingSoon(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void startSlider() {
        imageSlider = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            imageIndex = (imageIndex + 1) % HERO_IMAGES.length;
            heroImage.setImage(loadImage(HERO_IMAGES[imageIndex]));
        }));
        imageSlider.setCycleCount(Timeline.INDEFINITE);
        imageSlider.play();
    }

    private void stopSlider() {
        if (imageSlider != null)
            imageSlider.stop();
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return button;
    }

    private Button heroOutlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#f8f0e2;-fx-border-radius:18px;-fx-text-fill:#fff8f0;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return button;
    }

    /**
     * Matches the transparent, underline-based navigation used by Worker and About
     * Us.
     */
    private Button navButton(String text, boolean active, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        String state = active
                ? "-fx-text-fill:#735c00;-fx-border-color:#735c00;"
                : "-fx-text-fill:#4d4635;-fx-border-color:transparent;";
        button.setStyle("-fx-background-color:transparent;-fx-background-radius:0;"
                + "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;"
                + "-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;" + state);
        return button;
    }

    private void prepareLogo(ImageView logo) {
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setFitWidth(62);
        logo.setFitHeight(62);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(loadImage(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    private Image loadImage(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private String surfaceCard() {
        return "-fx-background-color:#fff8f0;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    }
}
