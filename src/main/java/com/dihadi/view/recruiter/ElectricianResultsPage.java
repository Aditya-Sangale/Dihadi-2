package com.dihadi.view.recruiter;

import com.dihadi.view.AppNavigator;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** Recruiter results screen shown when the Electrician category is selected. */
public class ElectricianResultsPage {
        private static final String[] PHOTOS = {
                        "/assets/images/worker/electrician/skill-00.jpg",
                        "/assets/images/worker/electrician/skill-01.jpg",
                        "/assets/images/worker/electrician/skill-02.jpg",
                        "/assets/images/worker/electrician/skill-03.jpg",
                        "/assets/images/worker/electrician/skill-04.jpg",
                        "/assets/images/worker/electrician/skill-05.jpg",
                        "/assets/images/worker/electrician/skill-06.jpg",
                        "/assets/images/worker/electrician/skill-07.jpg" };
        private static final String[][] WORKERS = {
                        { "Manoj Mari", "35 Years, Male", "Maharashtra", "2500" },
                        { "Rajesh Kumar", "28 Years, Male", "Delhi", "1800" },
                        { "Priya Sharma", "30 Years, Female", "Maharashtra", "2000" },
                        { "Amit Singh", "25 Years, Male", "Uttar Pradesh", "1500" },
                        { "Suresh Patel", "40 Years, Male", "Gujarat", "2200" },
                        { "Anil Gupta", "33 Years, Male", "Madhya Pradesh", "1900" },
                        { "Vikram Reddy", "29 Years, Male", "Telangana", "2100" },
                        { "Dinesh Verma", "50 Years, Male", "Haryana", "2500" },
                        { "Sunita Devi", "35 Years, Female", "Bihar", "1600" },
                        { "Rajesh B.", "42 Years, Male", "Maharashtra", "1800" },
                        { "Kavita S.", "26 Years, Female", "Gujarat", "1500" },
                        { "Mohan Lal", "55 Years, Male", "Rajasthan", "2200" },
                        { "Rahul K.", "23 Years, Male", "Delhi", "1200" },
                        { "Vinay T.", "38 Years, Male", "Karnataka", "2000" },
                        { "Priya G.", "29 Years, Female", "Tamil Nadu", "1600" },
                        { "Sanjay V.", "32 Years, Male", "Uttar Pradesh", "1700" },
                        { "Deep Singh", "48 Years, Male", "Bihar", "2100" },
                        { "Anil M.", "27 Years, Male", "Madhya Pradesh", "1400" },
                        { "Meera D.", "30 Years, Female", "West Bengal", "1550" } };

        public Scene getElectricianScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content(back));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content(Runnable back) {
        Label title = label("Looking for Skilled Electrician",
                "-fx-font-family:'Georgia';-fx-font-size:35px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        VBox page = new VBox(29, title, hero(), filterBar(), cards(), bottomActions(back), footer());
                page.setMaxWidth(1190);
                page.setPadding(new Insets(35, 0, 46, 0));
                StackPane canvas = new StackPane(page);
                canvas.setAlignment(Pos.TOP_CENTER);
                canvas.setPadding(new Insets(0, 38, 0, 38));
                canvas.setStyle("-fx-background-color:#fff8f0;");
                ScrollPane scroll = new ScrollPane(canvas);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
                return scroll;
        }

        private HBox hero() {
                ImageView image = image("/assets/images/electrician.jpeg", 410, 275);
                image.setPreserveRatio(false);
                StackPane picture = new StackPane(image);
                picture.setPrefSize(410, 275);
                picture.setStyle("-fx-background-radius:12px;-fx-border-radius:12px;");
                Label quote = label(
                                "\"Sparking progress and powering the\nfuture. Hire verified, skilled, and safe\nelectricians who bring light and life to your\nprojects.\"",
                                "-fx-font-family:'Georgia';-fx-font-size:18px;-fx-text-fill:#1e1b15;-fx-line-spacing:2px;");
                HBox box = new HBox(78, picture, quote);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(24));
                box.setStyle(
                                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;");
                return box;
        }

        private HBox filterBar() {
                Label filters = label("☰  Filters", "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                HBox row = new HBox(12, filters, compactCombo("Select Country"), compactCombo("Select State"),
                                compactCombo("Select City"), compactPincode());
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(14, 17, 14, 17));
                row.setStyle(
                                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;");
                return row;
        }

        private TilePane cards() {
                TilePane grid = new TilePane();
                grid.setPrefColumns(3);
                grid.setHgap(26);
                grid.setVgap(24);
                for (int i = 0; i < WORKERS.length; i++)
                        grid.getChildren().add(card(WORKERS[i], PHOTOS[i % PHOTOS.length]));
                return grid;
        }

        private VBox card(String[] w, String photoPath) {
                ImageView portrait = image(photoPath, 54, 54);
                portrait.setPreserveRatio(false);
                portrait.setClip(new Circle(27, 27, 27));
                StackPane portraitBox = new StackPane(portrait);
                portraitBox.setPrefSize(54, 54);
                portraitBox.setStyle(
                                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
                VBox identity = new VBox(3,
                                label(w[0], "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                                label(w[1], "-fx-font-size:12px;-fx-text-fill:#4c4637;"));
                HBox profile = new HBox(13, portraitBox, identity);
                profile.setAlignment(Pos.CENTER_LEFT);
                Label skill = label("Electrician",
                                "-fx-font-size:10px;-fx-text-fill:#574500;-fx-border-color:#d4af37;-fx-border-radius:10px;-fx-padding:3px 7px;");
                Label location = label("⌾  " + w[2], "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                Region line = new Region();
                line.setMinHeight(1);
                line.setPrefHeight(1);
                line.setMaxWidth(Double.MAX_VALUE);
                line.setStyle("-fx-background-color:#d0c5af;");
                VBox pay = new VBox(1, label("Wage", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#4c4637;"),
                                new HBox(label("₹" + w[3],
                                                "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#d4a300;"),
                                                label(" / day", "-fx-font-size:10px;-fx-text-fill:#4c4637;")));
                Button hire = new Button("HIRE NOW");
                hire.setStyle(
                                "-fx-background-color:#735c00;-fx-background-radius:18px;-fx-text-fill:#f6d676;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:7px 14px;-fx-cursor:hand;");
                hire.setOnAction(
                                e -> AppNavigator.information("Hire " + w[0], "Your hiring request has been started."));
                HBox bottom = new HBox(pay, hire);
                bottom.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(pay, Priority.ALWAYS);
                VBox card = new VBox(12, profile, skill, location, line, bottom);
                card.setPrefSize(360, 185);
                card.setPadding(new Insets(17));
                card.setStyle(
                                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),7,0,0,2px);");
                return card;
        }

    private HBox bottomActions(Runnable backAction) {
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color:transparent;-fx-font-size:14px;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-cursor:hand;");
        if (backAction != null) {
            back.setOnAction(e -> backAction.run());
        }

        Button more = new Button("View More Electricians");
        more.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-font-size:11px;-fx-padding:7px 34px;-fx-cursor:hand;");
        more.setOnAction(e -> AppNavigator.information("Electricians", "More electrician profiles will be loaded here."));

        HBox centerBox = new HBox(more);
        centerBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        HBox row = new HBox(back, centerBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 70, 0, 0)); // To visually balance the center if needed
        return row;
    }

        private BorderPane header() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
                logo.setPreserveRatio(true);
                Label title = label("DIHADI",
                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
                HBox brand = new HBox(10, logo, title);
                brand.setAlignment(Pos.CENTER_LEFT);
                HBox navigation = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
                navigation.setAlignment(Pos.CENTER);
                Button login = outline("Login"), signup = primary("Sign Up");
                login.setOnAction(e -> AppNavigator.adminLoginInProgress());
                signup.setOnAction(e -> AppNavigator.adminLoginInProgress());
                HBox account = new HBox(10, login, signup);
                account.setAlignment(Pos.CENTER_RIGHT);
                BorderPane bar = new BorderPane();
                bar.setLeft(brand);
                bar.setCenter(navigation);
                bar.setRight(account);
                bar.setPadding(new Insets(16, 24, 14, 24));
                bar.setStyle(
                                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
                return bar;
        }

        private Button nav(String text, boolean active) {
                Button b = new Button(text);
                b.setStyle(
                                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                                                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                                                + (active ? "#735c00" : "transparent")
                                                + ";");
                b.setOnAction(e -> AppNavigator.open((Stage) b.getScene().getWindow(), text));
                return b;
        }

        private Button primary(String text) {
                Button b = new Button(text);
                b.setStyle(
                                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;");
                return b;
        }

        private Button outline(String text) {
                Button b = new Button(text);
                b.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;");
                return b;
        }

        private VBox footer() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 58, 58);
                logo.setPreserveRatio(true);
                VBox identity = new VBox(9,
                                new HBox(12, logo, label("DIHADI",
                                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;")),
                                label("Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                                                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;"));
                identity.setPrefWidth(340);
                HBox main = new HBox(58, identity, footerColumn("Company", "About Dihadi", "Contact Us"),
                                footerColumn("Opportunities", "Find Work", "Worker Categories"),
                                footerColumn("Support", "Help Centre", "Privacy & Terms"));
                Label copy = label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
                                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;");
                VBox footer = new VBox(24, main, copy);
                footer.setMaxWidth(1180);
                footer.setPadding(new Insets(32, 42, 24, 42));
                footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
                return footer;
        }

        private VBox footerColumn(String heading, String... links) {
                VBox column = new VBox(8,
                                label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"));
                column.setPrefWidth(150);
                for (String link : links) {
                        Button b = new Button(link);
                        b.setStyle(
                                        "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.80;-fx-font-size:13px;-fx-cursor:hand;");
                        b.setOnAction(e -> AppNavigator.openFooterLink((Stage) b.getScene().getWindow(), link));
                        column.getChildren().add(b);
                }
                return column;
        }

        private ComboBox<String> compactCombo(String prompt) {
                ComboBox<String> box = new ComboBox<>();
                box.setPromptText(prompt);
                box.setPrefWidth(160);
                box.setPrefHeight(31);
                box.setStyle(inputStyle());
                return box;
        }

        private TextField compactPincode() {
                TextField field = new TextField();
                field.setPromptText("Select Pincode");
                field.setPrefWidth(160);
                field.setPrefHeight(31);
                field.setStyle(inputStyle());
                return field;
        }

        private String inputStyle() {
                return "-fx-background-color:#ffffff;-fx-background-radius:5px;-fx-border-color:#d0c5af;-fx-border-radius:5px;-fx-font-size:11px;";
        }

        private Label label(String text, String style) {
                Label l = new Label(text);
                l.setStyle("-fx-font-family:'Segoe UI';" + style);
                return l;
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
}
