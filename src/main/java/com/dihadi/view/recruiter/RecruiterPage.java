package com.dihadi.view.recruiter;

import com.dihadi.view.AppNavigator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Recruiter landing page built from the supplied premium recruiter reference.
 */
public class RecruiterPage {
    private static final String IMAGE_PACK = "/assets/images/recruiter/stitch_dihadi_workforce_ecosystem/";
    private static final String[] WORKFORCE_IMAGES = {
            IMAGE_PACK + "a_single_senior_indian_male_general_contractor_in_dihadi_branded_workwear_and_a/screen.png",
            IMAGE_PACK + "a_single_professional_indian_female_site_engineer_in_dihadi_branded_navy_blue/screen.png",
            IMAGE_PACK + "a_single_professional_indian_male_builder_in_dihadi_branded_apparel_and_a_white/screen.png",
            IMAGE_PACK + "a_single_professional_indian_male_construction_recruiter_in_dihadi_branded_navy/screen.png",
            IMAGE_PACK + "a_single_professional_indian_female_manpower_supplier_in_a_dihadi_branded_navy/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_28_year_old_young_indian_male_carpenter/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_30_year_old_indian_male_carpenter_wearing_a/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_35_year_old_indian_female_carpenter_specializing/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_42_year_old_indian_male_carpenter_wearing_a/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_50_year_old_senior_indian_master_carpenter/screen.png",
            IMAGE_PACK + "professional_1_1_portrait_of_a_55_year_old_senior_indian_carpenter_with_a_beard/screen.png" };
    private Timeline talentFlow;
    private Timeline heroSlider;
    private int heroImageIndex;

    public Scene getRecruiterScene(Runnable home) {
        VBox content = new VBox(42, hero(home), partnerSection(), imageGallery(), footer(home));
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(0, 0, 30, 0));
        content.setMinWidth(1280);
        content.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        BorderPane page = new BorderPane(scroll);
        page.setMinSize(1280, 720);
        page.setTop(header(home));
        page.setStyle("-fx-background-color:#f3e7ce;");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setBackground(new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(root, 1400, 780);
        scene.windowProperty().addListener((observable, oldWindow, window) -> {
            if (window instanceof Stage stage) {
                stage.setMinWidth(1280);
                stage.setMinHeight(720);
            }
        });
        return scene;
    }

    private BorderPane header(Runnable home) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        Label brandName = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, brandName);
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, nav("Home", false, home), nav("Business", false, () -> navigate("Business")),
                nav("Worker", false, () -> navigate("Worker")), nav("Recruiter", true, () -> {
                }), nav("About Us", false, () -> navigate("About Us")),
                nav("Contact Us", false, () -> navigate("Contact Us")));
        nav.setAlignment(Pos.CENTER);
        Button dashboard = primary("Dashboard");
        dashboard.setOnAction(e -> { 
            Stage stage = (Stage) dashboard.getScene().getWindow(); 
            com.dihadi.model.Recruiter r = com.dihadi.view.SessionManager.currentRecruiter != null ? com.dihadi.view.SessionManager.currentRecruiter : new com.dihadi.model.Recruiter();
            stage.setScene(new RecruiterDashboard(r).getScene(() -> stage.setScene(getRecruiterScene(home)))); 
        });
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(nav);
        bar.setRight(new HBox(10, dashboard));
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private VBox hero(Runnable home) {
        Label tag = label("FOR THE LEADERS OF INDUSTRY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.8px;-fx-text-fill:#735c00;-fx-background-color:#f1dfd2;-fx-background-radius:999px;-fx-padding:9px 13px;");
        Label title = label("Great structures are built by great teams.\nFind the hands that will build your vision.",
                "-fx-font-family:Georgia;-fx-font-size:48px;-fx-font-weight:800;-fx-text-fill:#3a3027;-fx-line-spacing:7px;");
        title.setWrapText(true);
        title.setMaxWidth(590);
        Label text = label(
                "Step into a transparent ecosystem where premium recruitment meets verified talent, designed specifically for India's high-stakes construction and infrastructure projects.",
                "-fx-font-size:17px;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        text.setWrapText(true);
        text.setMaxWidth(545);
        Button create = heroButton("CREATE PROJECT");
        create.setOnAction(e -> scene(create,
                new CreateProjectPage().getCreateProjectScene(() -> scene(create, getRecruiterScene(home)))));
        Button hire = heroButton("HIRE WORKERS");
        hire.setOnAction(e -> scene(hire,
                new HireSuitableSkilledWorkersPage().getHireWorkersScene(() -> scene(hire, getRecruiterScene(home)))));
        VBox copy = new VBox(26, tag, title, text, new HBox(14, create, hire));
        copy.setAlignment(Pos.CENTER_LEFT);
        copy.setPrefWidth(610);
        StackPane visual = slidingHeroFrame();
        HBox row = new HBox(56, copy, visual);
        row.setAlignment(Pos.CENTER);
        HBox.setMargin(visual, new Insets(18, 32, 18, 12));
        VBox hero = new VBox(row);
        hero.setPadding(new Insets(62, 78, 66, 78));
        hero.setMaxWidth(1400);
        hero.setStyle("-fx-background-color:#f3e7ce;");
        return hero;
    }

    private VBox partnerSection() {
        Label title = label("Our actively recruiting partners",
                "-fx-font-family:Georgia;-fx-font-size:34px;-fx-font-weight:700;-fx-text-fill:#3a3027;");
        Label sub = label("A tailored hiring experience for every kind of construction leader.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane cards = new FlowPane(18, 18);
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(card("GENERAL CONTRACTOR",
                "Manage large-scale labour deployment and streamline your multi-site workforce with precision tools.",
                WORKFORCE_IMAGES[0]),
                card("SUB-CONTRACTOR",
                        "Find specialised skilled tradespeople quickly to keep every project phase on schedule.",
                        WORKFORCE_IMAGES[1]),
                card("MANPOWER SUPPLIER",
                        "Digitise your talent pool and connect verified workers with leading construction firms.",
                        WORKFORCE_IMAGES[4]),
                card("BUILDER & DEVELOPER",
                        "Oversee labour needs and find dependable teams across all your developments.",
                        WORKFORCE_IMAGES[2]));
        VBox section = new VBox(14, title, sub, cards);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(14, 44, 22, 44));
        section.setStyle("-fx-background-color:#f3e7ce;");
        return section;
    }

    /** Desktop gallery with a continuous, one-directional workforce flow. */
    private VBox imageGallery() {
        Label title = label("Access India's Verified Workforce",
                "-fx-font-family:Georgia;-fx-font-size:34px;-fx-font-weight:700;-fx-text-fill:#3a3027;");
        HBox track = new HBox(22);
        track.setAlignment(Pos.CENTER_LEFT);
        for (int repeat = 0; repeat < 2; repeat++)
            for (String imagePath : WORKFORCE_IMAGES)
                track.getChildren().add(talentCard(imagePath));
        StackPane viewport = new StackPane(track);
        viewport.setAlignment(Pos.CENTER_LEFT);
        viewport.setPrefSize(1160, 410);
        viewport.setMaxSize(1160, 410);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        startTalentFlow(track);
        VBox section = new VBox(24, title, viewport);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setPadding(new Insets(46, 120, 50, 120));
        section.setMinWidth(1280);
        section.setMaxWidth(1400);
        section.setStyle("-fx-background-color:#f3e7ce;");
        return section;
    }

    private VBox card(String heading, String text, String imagePath) {
        StackPane imageFrame = coverFrame(imagePath, 225, 145,
                "-fx-background-color:#f1dfd2;-fx-background-radius:10px;-fx-border-radius:10px;");
        Label title = label(heading,
                "-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:.4px;-fx-text-fill:#3a3027;");
        Label copy = label(text, "-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        copy.setWrapText(true);
        copy.setPrefWidth(225);
        VBox card = new VBox(16, imageFrame, title, copy);
        card.setPadding(new Insets(20));
        card.setPrefSize(265, 330);
        card.setStyle(
                "-fx-background-color:#f3e7ce;-fx-background-radius:15px;-fx-border-color:#e8d7ca;-fx-border-radius:15px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),18,0,0,6px);");
        return card;
    }

    private StackPane talentCard(String path) {
        StackPane imageFrame = coverFrame(path, 278, 400,
                "-fx-background-color:#382f26;-fx-background-radius:15px;-fx-border-radius:15px;");
        Label badge = new Label();
        HBox badgeRow = new HBox(8, label("✓", "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#e9c349;"), badge);
        badgeRow.setAlignment(Pos.CENTER_LEFT);
        badgeRow.setPadding(new Insets(0, 18, 18, 18));
        badgeRow.setMaxWidth(Double.MAX_VALUE);
        badgeRow.setVisible(false);
        badgeRow.setManaged(false);
        StackPane card = new StackPane(imageFrame);
        card.setPrefSize(278, 400);
        card.setMaxSize(278, 400);
        card.setStyle(
                "-fx-background-color:#382f26;-fx-background-radius:15px;-fx-border-radius:15px;-fx-border-color:#d0c5af;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
        return card;
    }

    private StackPane slidingHeroFrame() {
        ImageView heroImage = new ImageView();
        setCoverImage(heroImage, WORKFORCE_IMAGES[heroImageIndex], 530, 470);
        StackPane frame = new StackPane(heroImage);
        frame.setAlignment(Pos.CENTER);
        frame.setPrefSize(530, 470);
        frame.setMinSize(530, 470);
        frame.setMaxSize(530, 470);
        Rectangle clip = new Rectangle(530, 470);
        clip.setArcWidth(84);
        clip.setArcHeight(84);
        frame.setClip(clip);
        frame.setStyle(
                "-fx-background-color:#fff8f5;-fx-background-radius:82px 22px 82px 22px;-fx-border-radius:82px 22px 82px 22px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.15),24,0,0,8px);");
        startHeroSlider(heroImage);
        return frame;
    }

    private VBox footer(Runnable home) {
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
        VBox explore = footerColumn("Explore", "Home", () -> {
            if (home != null) home.run();
            else navigate("Home");
        }, "Find Work", () -> navigate("Worker"), "About Us", () -> navigate("About Us"));
        VBox contact = footerColumn("Contact", "9561789599", () -> navigate("Contact Us"), "info@meridihadi.com",
                () -> navigate("Contact Us"), "Pune, Maharashtra", () -> navigate("Contact Us"));
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

    private Button nav(String name, boolean selected, Runnable action) {
        Button button = new Button(name);
        button.setOnAction(e -> action.run());
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-padding:8 4px;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-cursor:hand;-fx-text-fill:"
                        + (selected ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                        + (selected ? "#735c00" : "transparent") + ";-fx-border-width:0 0 2px 0;");
        return button;
    }

    private Button primary(String value) {
        Button b = new Button(value);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button secondary(String value) {
        Button b = new Button(value);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:999px;-fx-border-color:#735c00;-fx-border-radius:999px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private Label label(String value, String style) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private ImageView image(String path, double w, double h) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setSmooth(true);
        return view;
    }

    private StackPane coverFrame(String path, double width, double height, String style) {
        Image source = load(path);
        ImageView photo = new ImageView(source);
        setCoverImage(photo, source, width, height);
        StackPane frame = new StackPane(photo);
        frame.setAlignment(Pos.CENTER);
        frame.setPrefSize(width, height);
        frame.setMinSize(width, height);
        frame.setMaxSize(width, height);
        frame.setClip(new Rectangle(width, height));
        frame.setStyle(style);
        return frame;
    }

    private void setCoverImage(ImageView imageView, String path, double width, double height) {
        setCoverImage(imageView, load(path), width, height);
    }

    private void setCoverImage(ImageView imageView, Image source, double width, double height) {
        imageView.setImage(source);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(0);
        imageView.setFitHeight(0);
        if (source != null && source.getWidth() / source.getHeight() > width / height)
            imageView.setFitHeight(height);
        else
            imageView.setFitWidth(width);
        imageView.setSmooth(true);
    }

    private static final java.util.Map<String, Image> IMAGE_CACHE = new java.util.HashMap<>();

    private Image load(String path) {
        return IMAGE_CACHE.computeIfAbsent(path, p -> {
            var resource = getClass().getResource(p);
            return resource == null ? null : new Image(resource.toExternalForm(), 800, 800, true, true);
        });
    }

    private void scene(Button button, Scene scene) {
        javafx.stage.Window window = button.getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.setScene(scene);
        } else {
            sceneFromFocused(scene);
        }
    }

    private void sceneFromFocused(Scene scene) {
        for (javafx.stage.Window window : javafx.stage.Window.getWindows())
            if (window.isFocused() && window instanceof Stage stage) {
                stage.setScene(scene);
                return;
            }
    }

    private void navigate(String destination) {
        for (javafx.stage.Window window : javafx.stage.Window.getWindows())
            if (window.isFocused() && window instanceof Stage stage) {
                AppNavigator.open(stage, destination);
                return;
            }
    }

    private Button heroButton(String value) {
        Button b = new Button(value);
        b.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:6px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:12px 22px;-fx-cursor:hand;");
        return b;
    }

    private void startHeroSlider(ImageView imageView) {
        if (heroSlider != null)
            heroSlider.stop();
        heroSlider = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            heroImageIndex = (heroImageIndex + 1) % WORKFORCE_IMAGES.length;
            setCoverImage(imageView, WORKFORCE_IMAGES[heroImageIndex], 530, 470);
        }));
        heroSlider.setCycleCount(Timeline.INDEFINITE);
        heroSlider.play();
    }

    private void startTalentFlow(HBox track) {
        if (talentFlow != null)
            talentFlow.stop();
        talentFlow = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(track.translateXProperty(), 0)),
                new KeyFrame(Duration.seconds(38), new KeyValue(track.translateXProperty(), -3300)));
        talentFlow.setCycleCount(Timeline.INDEFINITE);
        talentFlow.play();
    }
}
