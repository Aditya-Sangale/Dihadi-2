package com.dihadi.view.recruiter;

import com.dihadi.view.AppNavigator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Recruiter landing page for finding and hiring skilled workers. */
public class HireSuitableSkilledWorkersPage {
        private static final String PAPER = "#f3e7ce";
        private static final String INK = "#1e1b15";
        private static final String MUTED = "#4c4637";
        private static final String GOLD = "#735c00";
        private static final String BORDER = "#d0c5af";
        private static final String[] HERO_IMAGES = {
                        "/assets/images/worker 5.jpeg", "/assets/images/sitesuperviser.jpeg",
                        "/assets/images/electrician.jpeg", "/assets/images/carpenter.jpeg",
                        "/assets/images/worker 2.jpeg"
        };

        private Timeline carousel;
        private int slide;
        private ImageView heroImage;

        public Scene getHireWorkersScene(Runnable homeAction) {
                BorderPane page = new BorderPane();
                page.setTop(header());
                page.setCenter(content(homeAction));
                page.setStyle("-fx-background-color:" + PAPER + ";");
                StackPane root = new StackPane(page);
                root.setPadding(new Insets(24));
                root.setStyle("-fx-background-color:" + PAPER + ";");
                return new Scene(root, 1400, 780);
        }

        private ScrollPane content(Runnable returnToRecruiter) {
                Label title = label("Hire Suitable Skilled Workers",
                                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:" + INK
                                                + ";");
                Label subtitle = label(
                                "Connect directly with verified, high-quality professionals across various industrial and construction trades.",
                                "-fx-font-size:16px;-fx-text-fill:" + MUTED + ";");
                subtitle.setWrapText(true);
                subtitle.setMaxWidth(760);

                VBox heading = new VBox(9, title, subtitle);
                HBox backRow = new HBox(recruiterBackButton(returnToRecruiter));
                backRow.setAlignment(Pos.CENTER_LEFT);

                VBox body = new VBox(44, heading, hero(), hiringQuote(), workerGrid(), backRow, footer());
                body.setMaxWidth(1240);
                body.setPadding(new Insets(28, 0, 0, 0));
                body.setStyle("-fx-background-color:" + PAPER + ";");

                StackPane wrapper = new StackPane(body);
                wrapper.setAlignment(Pos.TOP_CENTER);
                wrapper.setPadding(new Insets(0, 36, 0, 36));
                wrapper.setStyle("-fx-background-color:" + PAPER + ";");
                ScrollPane scroll = new ScrollPane(wrapper);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
                return scroll;
        }

        /** Returns to the already-open recruiter page without resetting the login session. */
        private Button recruiterBackButton(Runnable returnToRecruiter) {
                Button back = new Button("←  Back");
                back.setStyle(
                                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:10px 4px;-fx-cursor:hand;");
                back.setOnAction(e -> {
                        if (returnToRecruiter != null) {
                                returnToRecruiter.run();
                        }
                });
                back.setOnMouseEntered(e -> back.setStyle(
                                "-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:10px 4px;-fx-cursor:hand;"));
                back.setOnMouseExited(e -> back.setStyle(
                                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-font-family:'Segoe UI';-fx-padding:10px 4px;-fx-cursor:hand;"));
                return back;
        }

        private HBox hero() {
                heroImage = image(HERO_IMAGES[0], 780, 400);
                heroImage.setPreserveRatio(false);
                StackPane photo = new StackPane(heroImage);
                photo.setPrefSize(780, 400);
                photo.setMinWidth(520);
                photo.setStyle(cardStyle(15) + "-fx-background-color:#f4ede2;");
                HBox.setHgrow(photo, Priority.ALWAYS);
                startCarousel();

                Label quote = label(
                                "\"True strength lies in humility and honest labor. Discover professionals who build the future with pride.\"",
                                "-fx-font-family:'Georgia',serif;-fx-font-size:24px;-fx-font-weight:700;-fx-text-fill:"
                                                + INK
                                                + ";-fx-line-spacing:4px;");
                quote.setWrapText(true);
                quote.setPrefWidth(298);
                quote.setMaxWidth(298);
                Label verified = label("✧  DIHADI Verified Professionals",
                                "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:" + MUTED + ";");
                VBox words = new VBox(29, quote, verified);
                words.setAlignment(Pos.CENTER_LEFT);
                words.setPadding(new Insets(30, 34, 30, 38));
                words.setPrefWidth(370);
                words.setMinWidth(300);
                words.setStyle("-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:" + BORDER
                                + ";-fx-border-width:1 1 1 7px;-fx-border-radius:15px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),10,0,0,2px);");
                HBox hero = new HBox(24, photo, words);
                hero.setAlignment(Pos.CENTER);
                return hero;
        }

        private HBox hiringQuote() {
                Label quoteMark = label("“",
                                "-fx-font-family:'Georgia';-fx-font-size:54px;-fx-font-weight:800;-fx-text-fill:#d4af37;");
                quoteMark.setTranslateY(-7);
                Label quote = label(
                                "Every successful project depends on a workforce you can trust. Connect with verified skilled professionals who deliver craftsmanship, accountability, and dependable performance from day one.",
                                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-weight:700;-fx-text-fill:" + INK
                                                + ";-fx-line-spacing:3px;");
                quote.setWrapText(true);
                quote.setMaxWidth(900);
                Label signature = label("DIHADI — YOUR TRUSTED WORKFORCE PARTNER",
                                "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#9b7500;");
                VBox words = new VBox(9, quote, signature);
                words.setAlignment(Pos.CENTER_LEFT);
                HBox box = new HBox(18, quoteMark, words);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(24, 34, 24, 28));
                box.setStyle(
                                "-fx-background-color:#fffaf0;-fx-background-radius:14px;-fx-border-color:#d0c5af;-fx-border-width:1px 1px 1px 6px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.07),10,0,0,2px);");
                return box;
        }

        private VBox workerGrid() {
                Label heading = label("Looking for",
                                "-fx-font-family:'Georgia';-fx-font-size:29px;-fx-font-weight:700;-fx-text-fill:" + INK
                                                + ";");
                TilePane tiles = new TilePane();
                tiles.setPrefColumns(4);
                tiles.setHgap(20);
                tiles.setVgap(20);
                tiles.getChildren().addAll(
                                workerCard("Carpenter", "/assets/images/carpenter.jpeg"),
                                workerCard("Electrician", "/assets/images/electrician.jpeg"),
                                workerCard("General Labour", "/assets/images/generalLabour.jpeg"),
                                workerCard("ITI / Technician", "/assets/images/welder.jpeg"),
                                workerCard("Mason", "/assets/images/mason.jpeg"),
                                workerCard("Painter", "/assets/images/painter.jpeg"),
                                workerCard("Plumber", "/assets/images/plumber.jpeg"),
                                workerCard("Site Supervisor", "/assets/images/sitesuperviser.jpeg"));
                return new VBox(22, heading, tiles);
        }

        private VBox workerCard(String name, String path) {
                ImageView photo = image(path, 270, 176);
                photo.setPreserveRatio(false);
                StackPane imageArea = new StackPane(photo);
                imageArea.setPrefSize(270, 176);
                Label caption = label(name, "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:" + INK + ";");
                StackPane captionArea = new StackPane(caption);
                captionArea.setPrefSize(270, 68);
                VBox card = new VBox(imageArea, captionArea);
                card.setPrefSize(270, 244);
                card.setStyle(cardStyle(12));
                card.setOnMouseClicked(e -> {
                        Stage stage = (Stage) card.getScene().getWindow();
                        if ("General Labour".equals(name)) {
                                stage.setScene(new GeneralLabourResultsPage().getGeneralLabourScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("Electrician".equals(name)) {
                                stage.setScene(new ElectricianResultsPage().getElectricianScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("Site Supervisor".equals(name)) {
                stage.setScene(new SiteSupervisorResultsPage().getSiteSupervisorScene(
                        () -> stage.setScene(getHireWorkersScene(() -> AppNavigator.open(stage, "Home")))));
            } else if ("Carpenter".equals(name)) {
                                stage.setScene(new CarpenterResultsPage().getCarpenterScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("Mason".equals(name)) {
                                stage.setScene(new MasonResultPage().getMasonScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("ITI / Technician".equals(name)) {
                                stage.setScene(new ITI_TechnicianResultPage().getITITechnicianScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("Painter".equals(name)) {
                                stage.setScene(new PainterResultPage().getPainterScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else if ("Plumber".equals(name)) {
                                stage.setScene(new PlumberResultPage().getPlumberScene(
                                                () -> stage.setScene(getHireWorkersScene(
                                                                () -> AppNavigator.open(stage, "Home")))));
                        } else {
                                AppNavigator.information(name, "Worker profiles for " + name + " will be shown here.");
                        }
                });
                card.setOnMouseEntered(e -> card.setStyle(
                                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:12px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),14,0,0,4px);"));
                card.setOnMouseExited(e -> card.setStyle(cardStyle(12)));
                return card;
        }

        /**
         * Uses the same footer structure and visual treatment as the other DIHADI
         * pages.
         */
        private VBox footer() {
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
                VBox explore = footerColumn("Explore", "Home", () -> navigateTo("Home"), "Find Work", () -> navigateTo("Worker"), "About Us",
                                () -> navigateTo("About Us"));
                VBox contact = footerColumn("Contact", "9561789599", () -> navigateTo("Contact Us"), "info@meridihadi.com",
                                () -> navigateTo("Contact Us"), "Pune, Maharashtra", () -> navigateTo("Contact Us"));
                HBox top = new HBox(64, identity, explore, contact);
                top.setAlignment(Pos.TOP_LEFT);
                VBox footer = new VBox(22, top, label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
                footer.setPadding(new Insets(32, 42, 24, 42));
                footer.setMaxWidth(1180);
                footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
                return footer;
        }

        private void navigateTo(String destination) {
                for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                        if (window.isFocused() && window instanceof Stage stage) {
                                AppNavigator.open(stage, destination);
                                return;
                        }
                }
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

        /**
         * Uses the same branding, navigation spacing, and inactive account actions as
         * WorkerPage.
         */
        private BorderPane header() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
                logo.setPreserveRatio(true);
                logo.setSmooth(true);
                Label title = label("DIHADI",
                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
                HBox brand = new HBox(10, logo, title);
                brand.setAlignment(Pos.CENTER_LEFT);
                HBox navigation = new HBox(12, navButton("Home", false), navButton("Business", false),
                                navButton("Worker", false),
                                navButton("Recruiter", true), navButton("About Us", false),
                                navButton("Contact Us", false));
                navigation.setAlignment(Pos.CENTER);
                Button admin = AppNavigator.createHeaderActionButton();
                HBox rightActions = new HBox(10, admin);
                rightActions.setAlignment(Pos.CENTER_RIGHT);
                BorderPane header = new BorderPane();
                header.setLeft(brand);
                header.setCenter(navigation);
                header.setRight(rightActions);
                header.setPadding(new Insets(16, 24, 14, 24));
                header.setStyle(
                                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
                return header;
        }

        private Button navButton(String text, boolean selected) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                                                + (selected ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                                                + (selected ? "#735c00" : "transparent") + ";");
                button.setOnAction(e -> AppNavigator.open((Stage) button.getScene().getWindow(), text));
                return button;
        }

        private Button primaryButton(String text) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
                return button;
        }

        private Button outlineButton(String text) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
                return button;
        }

        private ComboBox<String> combo(String prompt, String... values) {
                ComboBox<String> box = new ComboBox<>();
                box.setPromptText(prompt);
                box.getItems().addAll(values);
                box.setPrefWidth(154);
                box.setPrefHeight(38);
                box.setStyle(fieldStyle());
                return box;
        }

        private String fieldStyle() {
                return "-fx-background-color:#f4ede2;-fx-background-radius:7px;-fx-font-size:13px;-fx-text-fill:" + INK
                                + ";-fx-prompt-text-fill:#685c52;";
        }

        private String cardStyle(int radius) {
                return "-fx-background-color:#ffffff;-fx-background-radius:" + radius + "px;-fx-border-color:" + BORDER
                                + ";-fx-border-radius:" + radius
                                + "px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),8,0,0,2px);";
        }

        private Label label(String value, String style) {
                Label label = new Label(value);
                label.setStyle("-fx-font-family:'Segoe UI';" + style);
                return label;
        }

        private ImageView image(String path, double width, double height) {
                ImageView view = new ImageView(load(path));
                view.setFitWidth(width);
                view.setFitHeight(height);
                view.setSmooth(true);
                return view;
        }

        private Image load(String path) {
                var resource = getClass().getResource(path);
                return resource == null ? null : new Image(resource.toExternalForm());
        }

        private void startCarousel() {
                carousel = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                        slide = (slide + 1) % HERO_IMAGES.length;
                        heroImage.setImage(load(HERO_IMAGES[slide]));
                }));
                carousel.setCycleCount(Timeline.INDEFINITE);
                carousel.play();
        }

        public void stopCarousel() {
                if (carousel != null)
                        carousel.stop();
        }
}
