package com.dihadi.view;

import com.dihadi.view.worker.GeneralLabour.GeneralLabourPage;
import com.dihadi.view.worker.Electrician.ElectricianPage;
import com.dihadi.view.worker.Mason.MasonPage;
import com.dihadi.view.worker.Plumber.PlumberPage;
import com.dihadi.view.worker.Carpenter.CarpenterPage;
import com.dihadi.view.worker.ITI_Technician.ITI_Technician;
import com.dihadi.view.worker.Site_Supervisor.Site_Supervisor;
import com.dihadi.view.worker.Painter.PainterPage;
import com.dihadi.view.recruiter.SignUpRecruiter;
import com.dihadi.view.worker.WokerSignUp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Worker category page with the WelcomePage visual language. */
public class WorkerPage extends Application {
    private static final String[] NAMES = { "Carpenter", "Electrician", "General Labour", "ITI / Technician",
            "Mason", "Painter", "Plumber", "Site Supervisor" };
    private static final String[] IMAGES = { "/assets/images/carpenter.jpeg", "/assets/images/electrician.jpeg",
            "/assets/images/generalLabour.jpeg", "/assets/images/welder.jpeg", "/assets/images/mason.jpeg",
            "/assets/images/painter.jpeg", "/assets/images/plumber.jpeg", "/assets/images/sitesuperviser.jpeg" };
    private static final String[] DETAILS = { "Woodwork, framing, fitting,\nand finishing details.",
            "Electrical installation, maintenance, and repairs.", "Essential support for smooth, safe site operations.",
            "Fabrication, structural welding, and metalwork.", "Brickwork, stonework, and concrete finishing.",
            "Interior and exterior surface preparation and painting.", "Piping, drainage, repair, and installation work.",
            "Site coordination, work quality, and team guidance." };
    private ImageView heroImage;
    private Timeline heroSlider;
    private int heroImageIndex;
    private Runnable homeAction;
    private Runnable aboutPageAction;

    public Scene getWorkerScene(Runnable backAction) {
        return getWorkerScene(backAction, null);
    }

    /**
     * Builds the worker page and optionally supplies navigation for the About Us
     * tab.
     */
    public Scene getWorkerScene(Runnable backAction, Runnable aboutAction) {
        stopHeroSlider();
        homeAction = backAction;
        aboutPageAction = aboutAction;
        Label eyebrow = label("WORKERS FOR EVERY JOB",
                "-fx-font-size: 12px; -fx-font-weight: 800; -fx-text-fill: #735c00; -fx-letter-spacing: 1.3px;");
        Label title = label("Find meaningful work that respects\nyour skills and supports your future.",
                "-fx-font-size: 40px; -fx-font-weight: 800; -fx-text-fill: #3a3027; -fx-line-spacing: 4px;");
        title.setWrapText(true);
        title.setMaxWidth(440);
        Label intro = label(
                "Choose your trade and explore work opportunities that match your experience. Dihadi helps skilled workers connect with the right projects.",
                "-fx-font-size: 16px; -fx-text-fill: #4d4635; -fx-line-spacing: 3px;");
        intro.setWrapText(true);
        intro.setMaxWidth(360);
        Label highlight = label("SKILLED HANDS. VERIFIED OPPORTUNITIES.",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"
                        + "-fx-background-color:#f1dfd2;-fx-background-radius:999px;-fx-padding:9px 13px;");
        Label supportingText = label("Explore the trades that power every project, from essential site support to specialist craftsmanship.",
                "-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        supportingText.setWrapText(true);
        supportingText.setMaxWidth(360);
        VBox copy = new VBox(16, eyebrow, title, intro, highlight, supportingText);
        copy.setAlignment(Pos.CENTER_LEFT);
        copy.setPrefWidth(360);

        heroImage = image(IMAGES[0], 660, 372);
        StackPane photo = new StackPane(heroImage);
        photo.setPrefSize(660, 372);
        startHeroSlider();
        photo.setStyle(
                "-fx-background-color: #fbf3e5; -fx-background-radius: 24px; -fx-border-color: rgba(115,92,0,0.24); -fx-border-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.16), 24, 0, 0, 8px);");
        HBox hero = new HBox(24, photo, copy);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(30, 30, 30, 30));
        hero.setMaxWidth(1180);
        hero.setStyle(
                "-fx-background-color: rgba(255,248,240,0.94); -fx-background-radius: 24px; -fx-border-color: rgba(115,92,0,0.18); -fx-border-radius: 24px;");

        Label categoriesTitle = label("Find roles that match your mastery",
                "-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: #3a3027;");
        Label categoriesText = label("Select a worker category to view its available roles.",
                "-fx-font-size: 15px; -fx-text-fill: #3c3c3c;");
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxWidth(1160);
        for (int i = 0; i < NAMES.length; i++)
            grid.getChildren().add(categoryCard(i));
        VBox categories = new VBox(10, categoriesTitle, categoriesText, grid);
        categories.setAlignment(Pos.CENTER);
        categories.setPadding(new Insets(40, 32, 44, 32));
        categories.setMaxWidth(1220);
        categories.setStyle(
                "-fx-background-color: rgba(251,243,229,0.96); -fx-background-radius: 24px; -fx-border-color: rgba(115,92,0,0.18); -fx-border-radius: 24px;");

        VBox footer = createDesktopFooter();
        VBox content = new VBox(24, hero, categories, footer);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(24, 24, 24, 24));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(header(backAction, aboutAction));
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        setWelcomeBackground(root);
        return new Scene(root, 1400, 780);
    }

    private BorderPane header(Runnable backAction, Runnable aboutAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setPreserveRatio(true);
        Label title = label("DIHADI",
                "-fx-font-size: 25px; -fx-font-weight: 800; -fx-text-fill: #735c00; -fx-letter-spacing: 1px;");
        HBox brand = new HBox(10, logo, title);
        brand.setAlignment(Pos.CENTER_LEFT);

        Button home = navButton("Home", false);
        home.setOnAction(e -> navigate("Home"));
        Button business = navButton("Business", false);
        business.setOnAction(e -> navigate("Business"));
        Button worker = navButton("Worker", true);
        worker.setOnAction(e -> navigate("Worker"));
        Button recruiter = navButton("Recruiter", false);
        recruiter.setOnAction(e -> navigate("Recruiter"));
        Button about = navButton("About Us", false);
        about.setOnAction(e -> navigate("About Us"));
        Button contact = navButton("Contact Us", false);
        contact.setOnAction(e -> navigate("Contact Us"));
        HBox navigation = new HBox(12, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);

        // Account actions are intentionally inactive on this landing page.
        // "Create Worker Profile" is the single entry point to worker signup here.
        Button login = outlineButton("Login");
        Button signUp = primaryButton("Sign Up");
        login.setOnAction(e -> AppNavigator.adminLoginInProgress());
        signUp.setOnAction(e -> AppNavigator.adminLoginInProgress());
        HBox accountActions = new HBox(10, login, signUp);
        accountActions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(accountActions);
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle(
                "-fx-background-color: #f3e7ce; -fx-border-color: #d0c5af; -fx-border-width: 0 0 1px 0; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.10), 10, 0.28, 0, 1.5px);");
        return header;
    }

    private Button navButton(String text, boolean active) {
        Button button = new Button(text);
        String activeStyle = active ? "-fx-text-fill: #735c00; -fx-border-color: #735c00; -fx-border-width: 0 0 2px 0;"
                : "-fx-text-fill: #4d4635; -fx-border-color: transparent; -fx-border-width: 0 0 2px 0;";
        button.setStyle(
                "-fx-background-color: transparent; -fx-background-radius: 0; -fx-font-size: 13px; -fx-font-weight: 700; -fx-font-family: 'Segoe UI', sans-serif; -fx-padding: 8px 4px; -fx-cursor: hand; "
                        + activeStyle);
        return button;
    }

    private VBox categoryCard(int i) {
        ImageView picture = image(IMAGES[i], 238, 150);
        Label name = label(NAMES[i], "-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #3a3027;");
        Label detail = label(DETAILS[i], "-fx-font-size: 13px; -fx-text-fill: #3c3c3c;");
        detail.setWrapText(true);
        detail.setPrefWidth(238);
        Button view = primaryButton("View Roles");
        view.setMaxWidth(Double.MAX_VALUE);
        view.setOnAction(e -> clicked(NAMES[i] + " View Roles"));
        if (i == 2) {
            view.setOnAction(e -> showGeneralLabour());
        }
        if (i == 7) {
            view.setOnAction(e -> showSiteSupervisor());
        }
        if (i == 1) {
            view.setOnAction(e -> showElectrician());
        }
        if (i == 4)
            view.setOnAction(e -> showMason());
        if (i == 6)
            view.setOnAction(e -> showPlumber());
        if (i == 0)
            view.setOnAction(e -> showCarpenter());
        if (i == 3)
            view.setOnAction(e -> showItiTechnician());
        if (i == 5)
            view.setOnAction(e -> showPainter());
        VBox card = new VBox(12, picture, name, detail, view);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefSize(270, 325);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: #fbf3e5; -fx-background-radius: 20px; -fx-border-color: rgba(115,92,0,0.20); -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.10), 16, 0, 0, 5px);");
        if (i == 2) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showGeneralLabour());
        }
        if (i == 7) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showSiteSupervisor());
        }
        if (i == 1) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showElectrician());
        }
        if (i == 4) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showMason());
        }
        if (i == 6) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showPlumber());
        }
        if (i == 0) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showCarpenter());
        }
        if (i == 3) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showItiTechnician());
        }
        if (i == 5) {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setOnMouseClicked(e -> showPainter());
        }
        return card;
    }

    private VBox createDesktopFooter() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 58, 58);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI",
                "-fx-font-size: 25px; -fx-font-weight: 800; -fx-text-fill: #e9c349; -fx-letter-spacing: 1px;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size: 13px; -fx-text-fill: #f8f0e2; -fx-opacity: 0.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(300);
        VBox brandArea = new VBox(9, new HBox(12, logo, brand), promise);
        brandArea.setPrefWidth(340);

        VBox companyLinks = footerColumn("Company", "About Dihadi", "Contact Us");
        VBox workLinks = footerColumn("Opportunities", "Find Work", "Worker Categories");
        VBox supportLinks = footerColumn("Support", "Help Centre", "Privacy & Terms");
        HBox footerMain = new HBox(58, brandArea, companyLinks, workLinks, supportLinks);
        footerMain.setAlignment(Pos.TOP_LEFT);

        Label copyright = label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
                "-fx-font-size: 12px; -fx-text-fill: #f8f0e2; -fx-opacity: 0.65;");
        VBox footer = new VBox(24, footerMain, copyright);
        footer.setMaxWidth(1180);
        footer.setPadding(new Insets(32, 42, 24, 42));
        footer.setStyle(
                "-fx-background-color: #343027; -fx-background-radius: 20px; -fx-border-color: rgba(208,197,175,0.32); -fx-border-radius: 20px; -fx-border-width: 1px 0 0 0;");
        return footer;
    }

    private VBox footerColumn(String heading, String... links) {
        Label title = label(heading, "-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #e9c349;");
        VBox column = new VBox(8, title);
        column.setPrefWidth(150);
        for (String link : links) {
            Button button = footerLink(link);
            button.setOnAction(event -> AppNavigator.openFooterLink(
                    (Stage) button.getScene().getWindow(), link));
            column.getChildren().add(button);
        }
        return column;
    }

    private Button footerLink(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent; -fx-padding: 2px 0; -fx-text-fill: #f8f0e2; -fx-opacity: 0.80; -fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif; -fx-cursor: hand;");
        return button;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; " + style);
        return label;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #d4af37; -fx-background-radius: 999px; -fx-text-fill: #3a3027; -fx-font-size: 14px; -fx-font-weight: 700; -fx-font-family: 'Segoe UI', sans-serif; -fx-padding: 10px 20px;");
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #fbf3e5; -fx-background-radius: 999px; -fx-border-color: #735c00; -fx-border-radius: 999px; -fx-text-fill: #735c00; -fx-font-size: 14px; -fx-font-weight: 700; -fx-font-family: 'Segoe UI', sans-serif; -fx-padding: 9px 18px;");
        return button;
    }

    private ImageView image(String path, double width, double height) {
        ImageView image = new ImageView(loadImage(path));
        if (path.contains("/assets/logo/")) {
            image.setViewport(new Rectangle2D(380, 0, 840, 840));
            image.setPreserveRatio(true);
        } else {
            image.setPreserveRatio(false);
        }
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setSmooth(true);
        return image;
    }

    private Image loadImage(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void startHeroSlider() {
        heroSlider = new Timeline(new KeyFrame(Duration.seconds(3), event -> showNextHeroImage()));
        heroSlider.setCycleCount(Timeline.INDEFINITE);
        heroSlider.play();
    }

    private void showNextHeroImage() {
        heroImageIndex = (heroImageIndex + 1) % IMAGES.length;
        if (heroImage != null)
            heroImage.setImage(loadImage(IMAGES[heroImageIndex]));
    }

    private void stopHeroSlider() {
        if (heroSlider != null)
            heroSlider.stop();
    }

    private void showGeneralLabour() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new GeneralLabourPage().getGeneralLabourScene(
                () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)),
                homeAction,
                aboutPageAction));
    }

    private void showSiteSupervisor() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new Site_Supervisor().getSiteSupervisorScene(
                () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)), homeAction, aboutPageAction));
    }

    private void showElectrician() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new ElectricianPage().getElectricianScene(
                () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)), homeAction, aboutPageAction));
    }

    private void showMason() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new MasonPage().getMasonScene(() -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)),
                homeAction, aboutPageAction));
    }

    private void showPlumber() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new PlumberPage().getPlumberScene(
                () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)), homeAction, aboutPageAction));
    }

    private void showCarpenter() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new CarpenterPage()
                .getCarpenterScene(() -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
    }

    private void showItiTechnician() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new ITI_Technician()
                .getItiTechnicianScene(() -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
    }

    private void showPainter() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(
                new PainterPage().getPainterScene(() -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
    }

    private void showWorkerSignUp() {
        stopHeroSlider();
        Stage stage = (Stage) heroImage.getScene().getWindow();
        stage.setScene(new WokerSignUp().getSignUpScene(
                () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
    }

    private void navigate(String destination) {
        Stage stage = (Stage) heroImage.getScene().getWindow();
        switch (destination) {
            case "Home" -> homeAction.run();
            case "Business" -> stage.setScene(new BusinessPage().getBusinessScene(homeAction,
                    () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
            case "About Us" -> {
                if (aboutPageAction != null)
                    aboutPageAction.run();
            }
            case "Contact Us" -> stage.setScene(new ContactUs().getContactScene(homeAction,
                    () -> stage.setScene(new BusinessPage().getBusinessScene(homeAction,
                            () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)))),
                    () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction)), aboutPageAction));
            case "Recruiter" -> stage.setScene(new SignUpRecruiter().getRecruiterSignUpScene(
                    () -> stage.setScene(getWorkerScene(homeAction, aboutPageAction))));
            default -> {
            }
        }
    }

    private void clicked(String action) {
        System.out.println(action + " clicked");
    }

    private void setWelcomeBackground(StackPane root) {
        var resource = getClass().getResource("/assets/images/background image.jpeg");
        if (resource == null) {
            root.setBackground(
                    new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
            return;
        }
        Image background = new Image(resource.toExternalForm());
        BackgroundImage bg = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        root.setBackground(new Background(
                new BackgroundFill[] { new BackgroundFill(Color.web("#f3e7ce99"), CornerRadii.EMPTY, Insets.EMPTY) },
                new BackgroundImage[] { bg }));
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(getWorkerScene(null));
        stage.setTitle("Dihadi - Worker Page");
        stage.setWidth(1400);
        stage.setHeight(780);
        stage.setMaximized(true);
        stage.show();
    }
}
