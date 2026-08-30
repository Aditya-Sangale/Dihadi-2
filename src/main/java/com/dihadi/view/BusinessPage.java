package com.dihadi.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

/** Business-facing DIHADI workforce solutions landing page. */
public class BusinessPage {
        private static final String[] HERO_IMAGES = {
                        "/assets/images/business/business1.png",
                        "/assets/images/business/business2.png",
                        "/assets/images/business/business3.png",
                        "/assets/images/business/business4.png"
        };

        private Runnable homeAction;
        private Runnable workerAction;
        private ImageView heroImage;
        private Timeline heroSlider;
        private int heroImageIndex;

        public Scene getBusinessScene(Runnable home, Runnable worker) {
                homeAction = home;
                workerAction = worker;
                VBox content = new VBox(hero(), solutions(), standard(), callsToAction(), footer());
                content.setAlignment(Pos.TOP_CENTER);

                ScrollPane scroll = new ScrollPane(content);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
                BorderPane page = new BorderPane(scroll);
                page.setTop(header());
                page.setStyle("-fx-background-color:#f3e7ce;");
                StackPane root = new StackPane(page);
                root.setPadding(new Insets(24));
                root.setStyle("-fx-background-color:#f3e7ce;");
                return new Scene(root, 1400, 780);
        }

        private BorderPane header() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 48, 48);
                logo.setViewport(new Rectangle2D(380, 0, 840, 840));
                logo.setPreserveRatio(true);
                HBox brand = new HBox(10, logo, label("DIHADI",
                                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
                brand.setAlignment(Pos.CENTER_LEFT);

                Button home = nav("Home", false), business = nav("Business", true), worker = nav("Worker", false),
                                recruiter = nav("Recruiter", false), about = nav("About Us", false),
                                contact = nav("Contact Us", false);
                home.setOnAction(e -> navigate(home, "Home"));
                business.setOnAction(e -> navigate(business, "Business"));
                worker.setOnAction(e -> navigate(worker, "Worker"));
                recruiter.setOnAction(e -> navigate(recruiter, "Recruiter"));
                about.setOnAction(e -> navigate(about, "About Us"));
                contact.setOnAction(e -> navigate(contact, "Contact Us"));
                HBox navigation = new HBox(12, home, business, worker, recruiter, about, contact);
                navigation.setAlignment(Pos.CENTER);
                Button admin = primary("Admin");
                admin.setOnAction(e -> AppNavigator.adminLoginInProgress());
                HBox account = new HBox(12, admin);
                account.setAlignment(Pos.CENTER_RIGHT);

                BorderPane bar = new BorderPane();
                bar.setLeft(brand);
                bar.setCenter(navigation);
                bar.setRight(account);
                bar.setPadding(new Insets(16, 42, 14, 42));
                bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;"
                                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
                return bar;
        }

        private void navigate(Button source, String destination) {
                javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
                switch (destination) {
                        case "Home" -> stage.setScene(new HomePage(stage).getHomeScene());
                        case "Worker" -> AppNavigator.signUp(stage, null);
                        case "Recruiter" -> stage.setScene(new com.dihadi.view.recruiter.SignUpRecruiter()
                                        .getRecruiterSignUpScene(() -> stage.setScene(getBusinessScene(null, null))));
                        case "About Us" ->
                                stage.setScene(new AboutUs().getAboutScene(
                                                () -> stage.setScene(new HomePage(stage).getHomeScene()),
                                                () -> stage.setScene(new WorkerPage()
                                                                .getWorkerScene(() -> stage.setScene(
                                                                                new HomePage(stage).getHomeScene()),
                                                                                null))));
                        case "Contact Us" ->
                                stage.setScene(new ContactUs().getContactScene(
                                                () -> stage.setScene(new HomePage(stage).getHomeScene()),
                                                () -> stage.setScene(getBusinessScene(null, null)),
                                                () -> stage.setScene(new WorkerPage()
                                                                .getWorkerScene(() -> stage.setScene(
                                                                                new HomePage(stage).getHomeScene()),
                                                                                null)),
                                                null));
                        default -> {
                        }
                }
        }

        private void openSignUp(Button source) {
                javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
                javafx.scene.Scene previous = stage.getScene();
                stage.setScene(new com.dihadi.view.worker.WokerSignUp().getSignUpScene(() -> stage.setScene(previous)));
        }

        private VBox hero() {
                Label eyebrow = label("ENTERPRISE WORKFORCE SOLUTIONS", smallGold());
                Label title = label("Empower Your Projects\nwith India’s Verified\nWorkforce.", headline(40));
                Label copy = label(
                                "Finding dependable, skilled labour should not be a bottleneck. DIHADI\nseamlessly connects your enterprise with verified blue-collar\nprofessionals—from specialized artisans to large-scale general\nworkforces. Mobilize with confidence, speed, and absolute\ntransparency.",
                                body(14));
                Button hire = primary("HIRE WORKERS NOW");
                hire.setOnAction(event -> AppNavigator.open(
                                (javafx.stage.Stage) hire.getScene().getWindow(), "Recruiter"));
                VBox words = new VBox(18, eyebrow, title, copy, hire);
                words.setAlignment(Pos.CENTER_LEFT);
                words.setPrefWidth(560);

                heroImage = image(HERO_IMAGES[0], 550, 310);
                StackPane photoBox = roundedImage(heroImage, 550, 310, 22);
                startHeroSlider();
                HBox row = new HBox(72, words, photoBox);
                row.setAlignment(Pos.CENTER);
                row.setMaxWidth(1220);
                VBox section = new VBox(row);
                section.setAlignment(Pos.CENTER);
                section.setPadding(new Insets(76, 42, 76, 42));
                section.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
                return section;
        }

        private VBox solutions() {
                Label title = label("Enterprise Solutions for Every Scale", headline(30));
                Label subtitle = label("Tailored workforce deployments for your specific industry needs.", body(13));
                FlowPane cards = new FlowPane(20, 20);
                cards.setAlignment(Pos.CENTER);
                cards.getChildren().addAll(
                                solution("/assets/images/worker 1 (2).jpeg", "General Contractors",
                                                "Deploy verified teams for massive infrastructure and commercial builds with a single click."),
                                solution("/assets/images/worker/mason/skill-02.jpg", "Specialized Sub-\nContractors",
                                                "Source niche skills—from precision welders to master carpenters—exactly when you phase requires them."),
                                solution("/assets/images/sitesuperviser.jpeg", "Manpower Aggregators",
                                                "Scale your supply capabilities rapidly by tapping into our pan-India network of registered professionals."),
                                solution("/assets/images/mason.jpeg", "Builders & Developers",
                                                "Ensure zero downtime on your real estate projects with a continuous, reliable flow of skilled labour."));
                VBox section = new VBox(12, title, subtitle, cards);
                section.setAlignment(Pos.CENTER);
                section.setPadding(new Insets(62, 42, 70, 42));
                section.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
                return section;
        }

        private VBox solution(String picture, String name, String description) {
                ImageView image = image(picture, 230, 136);
                StackPane imageBox = roundedImage(image, 230, 136, 12);
                Label heading = label(name,
                                "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1f1b13;");
                Label copy = label(description, body(13));
                copy.setWrapText(true);
                copy.setPrefWidth(230);
                VBox card = new VBox(13, imageBox, heading, copy);
                card.setAlignment(Pos.TOP_LEFT);
                card.setPadding(new Insets(16));
                card.setPrefSize(262, 342);
                card.setStyle(
                                "-fx-background-color:#f1e4cc;-fx-background-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
                return card;
        }

        private VBox standard() {
                Label title = label("The DIHADI Standard", headline(30));
                VBox left = new VBox(42,
                                feature("⌘", "Vast Verified Network",
                                                "Access a deeply vetted pool of skilled and\nunskilled professionals across the nation."),
                                feature("♧", "Uncompromising Safety",
                                                "We prioritize verified credentials and safety\ncompliance."));
                ImageView welder = image("/assets/images/welder.jpeg", 310, 190);
                StackPane middle = roundedImage(welder, 310, 190, 18);
                VBox right = new VBox(42,
                                feature("♜", "Pan-India Mobilization",
                                                "Projects in metro hubs or developing corridors."),
                                feature("⌬", "Precision Matching",
                                                "Intelligent ecosystem matches project\nrequirements with precise skill sets."));
                HBox body = new HBox(82, left, middle, right);
                body.setAlignment(Pos.CENTER);
                VBox section = new VBox(60, title, body);
                section.setAlignment(Pos.CENTER);
                section.setPadding(new Insets(70, 42, 80, 42));
                section.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
                return section;
        }

        private VBox feature(String icon, String name, String copy) {
                Label symbol = label(icon, "-fx-font-size:20px;-fx-text-fill:#1f1b13;");
                Label title = label(name,
                                "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1f1b13;");
                Label description = label(copy, body(13));
                return new VBox(8, symbol, title, description);
        }

        private VBox callsToAction() {
                VBox scale = callCard("Scale Your Operations", "Streamline your hiring process.",
                                "300,000+ Verified Profiles",
                                "10+ Specialized Trades", "BECOME A PARTNER", "#57483a", "#fff8f0");
                VBox discover = callCard("Discover Dignified\nOpportunities", "Unlock continuous project deployments.",
                                "12,000+ Active Employers", "10,000+ Monthly Deployments", "POST A REQUIREMENT",
                                "#d4af37", "#1f1b13");
                HBox row = new HBox(24, scale, discover);
                row.setAlignment(Pos.CENTER);
                VBox section = new VBox(row);
                section.setAlignment(Pos.CENTER);
                section.setPadding(new Insets(76, 42, 76, 42));
                section.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:24px;-fx-border-color:#d0c5af;-fx-border-radius:24px;");
                return section;
        }

        private VBox callCard(String title, String sub, String first, String second, String action, String background,
                        String text) {
                Label heading = label(title,
                                "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:700;-fx-text-fill:" + text
                                                + ";");
                Label subtitle = label(sub, "-fx-font-size:13px;-fx-text-fill:" + text + ";-fx-opacity:.9;");
                Label divider = label("", "-fx-border-color:" + text + ";-fx-opacity:.35;-fx-border-width:1px 0 0 0;");
                divider.setPrefWidth(390);
                Label one = label(first, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:" + text + ";");
                Label two = label(second, "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:" + text + ";");
                Button button = primary(action);
                if ("BECOME A PARTNER".equalsIgnoreCase(action)) {
                        button.setOnAction(e -> navigate(button, "Contact Us"));
                } else if ("POST A REQUIREMENT".equalsIgnoreCase(action)) {
                        button.setOnAction(e -> navigate(button, "Recruiter"));
                }
                VBox card = new VBox(16, heading, subtitle, divider, one, two, spacer(24), button);
                card.setAlignment(Pos.TOP_LEFT);
                card.setPadding(new Insets(44));
                card.setPrefSize(510, 410);
                card.setStyle("-fx-background-color:" + background
                                + ";-fx-background-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),20,0,0,7px);");
                return card;
        }

        private VBox footer() {
                Label brand = label("DIHADI",
                                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
                Label quote = label(
                                "“A nation is not built on blueprints alone, but on the\nunwavering strength, sweat, and calloused hands of the\nlabourers who turn visions into towering realities.”",
                                "-fx-font-family:'Georgia';-fx-font-size:18px;-fx-font-style:italic;-fx-text-fill:#fff8f0;");
                VBox identity = new VBox(22, brand, quote);
                identity.setPrefWidth(550);
                VBox links = new VBox(10, footerLink("Company"), footerLink("Opportunities"), footerLink("Legal"),
                                footerLink("Addresses"), footerLink("Contact"));
                VBox contact = new VBox(12, footerLink("info@meridihadi.com"), footerLink("9561789599"));
                HBox top = new HBox(90, identity, links, contact);
                top.setAlignment(Pos.TOP_LEFT);
                Label copyright = label("© 2024 DIHADI. Mera Haq ~ Meri Dihadi. All Rights Reserved.",
                                "-fx-font-size:12px;-fx-text-fill:#fff8f0;-fx-opacity:.72;");
                VBox footer = new VBox(54, top, copyright);
                footer.setMaxWidth(1400);
                footer.setPadding(new Insets(56, 86, 36, 86));
                footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
                return footer;
        }

        private Label footerLink(String text) {
                return label(text, "-fx-font-size:13px;-fx-text-fill:#fff8f0;-fx-opacity:.82;");
        }

        private Region spacer(double height) {
                Region region = new Region();
                region.setPrefHeight(height);
                return region;
        }

        private StackPane roundedImage(ImageView image, double width, double height, double radius) {
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(width, height);
                clip.setArcWidth(radius * 2);
                clip.setArcHeight(radius * 2);
                image.setClip(clip);
                StackPane box = new StackPane(image);
                box.setPrefSize(width, height);
                return box;
        }

        private ImageView image(String path, double width, double height) {
                ImageView view = new ImageView(load(path));
                view.setFitWidth(width);
                view.setFitHeight(height);
                view.setPreserveRatio(false);
                view.setSmooth(true);
                return view;
        }

        private Image load(String path) {
                var resource = getClass().getResource(path);
                return resource == null ? null : new Image(resource.toExternalForm());
        }

        private void startHeroSlider() {
                if (heroSlider != null)
                        heroSlider.stop();
                heroImageIndex = 0;
                heroSlider = new Timeline(new KeyFrame(Duration.seconds(3), event -> showNextHeroImage()));
                heroSlider.setCycleCount(Timeline.INDEFINITE);
                heroSlider.play();
        }

        private void showNextHeroImage() {
                heroImageIndex = (heroImageIndex + 1) % HERO_IMAGES.length;
                if (heroImage != null)
                        heroImage.setImage(load(HERO_IMAGES[heroImageIndex]));
        }

        private Label label(String text, String style) {
                Label label = new Label(text);
                label.setStyle(style);
                return label;
        }

        private String headline(int size) {
                return "-fx-font-family:'Georgia';-fx-font-size:" + size
                                + "px;-fx-font-weight:700;-fx-text-fill:#1f1b13;-fx-line-spacing:3px;";
        }

        private String body(int size) {
                return "-fx-font-family:'Georgia';-fx-font-size:" + size
                                + "px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;";
        }

        private String smallGold() {
                return "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#b98900;-fx-letter-spacing:1px;";
        }

        private Button nav(String text, boolean active) {
                Button button = new Button(text);
                button.setStyle("-fx-background-color:transparent;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                                + (active ? "#735c00" : "transparent")
                                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
                return button;
        }

        private Button primary(String text) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
                return button;
        }

        private Button outline(String text) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
                return button;
        }

        private void run(Runnable action) {
                if (action != null)
                        action.run();
        }
}
