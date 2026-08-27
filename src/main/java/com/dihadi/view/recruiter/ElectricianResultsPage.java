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

        private static class WorkerCardData {
                String name;
                String demographic;
                String location;
                String wage;
                String photo;

                WorkerCardData(String name, String demographic, String location, String wage, String photo) {
                        this.name = name;
                        this.demographic = demographic;
                        this.location = location;
                        this.wage = wage;
                        this.photo = photo;
                }
        }

        private java.util.List<WorkerCardData> getAllElectricianWorkers() {
                java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
                try {
                        java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
                        if (realWorkers != null) {
                                int pIdx = 0;
                                for (com.dihadi.model.Worker w : realWorkers) {
                                        if (w.getWorkerType() != null && w.getWorkerType().toLowerCase().contains("electrician")) {
                                                String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                                                  (w.getLastName() != null ? w.getLastName() : "")).trim();
                                                if (fullName.isBlank()) fullName = "Verified Electrician";
                                                String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                                              + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                                                String loc = (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                                                String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "1200";
                                                String photo = w.getProfilePhotoUrl() != null && !w.getProfilePhotoUrl().isBlank() 
                                                               ? w.getProfilePhotoUrl() : PHOTOS[pIdx % PHOTOS.length];
                                                pIdx++;
                                                list.add(new WorkerCardData(fullName, demo, loc, wage, photo));
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                for (int i = 0; i < WORKERS.length; i++) {
                        list.add(new WorkerCardData(WORKERS[i][0], WORKERS[i][1], WORKERS[i][2], WORKERS[i][3], PHOTOS[i % PHOTOS.length]));
                }
                return list;
        }

        private TilePane cards() {
                TilePane grid = new TilePane();
                grid.setPrefColumns(3);
                grid.setHgap(26);
                grid.setVgap(24);
                for (WorkerCardData w : getAllElectricianWorkers())
                        grid.getChildren().add(card(w));
                return grid;
        }

        private VBox card(WorkerCardData w) {
                ImageView portrait = image(w.photo, 64, 64);
                portrait.setPreserveRatio(false);
                portrait.setClip(new Circle(32, 32, 32));
                StackPane avatar = new StackPane(portrait);
                avatar.setPrefSize(64, 64);
                avatar.setStyle(
                                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
                Label name = label(w.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                Label age = label(w.demographic, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                Label skill = label("Electrician",
                                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
                Label location = label("⌖  " + w.location, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                VBox details = new VBox(4, name, age, skill, location);
                HBox top = new HBox(14, avatar, details);
                top.setAlignment(Pos.TOP_LEFT);
                Region divider = new Region();
                divider.setMinHeight(1);
                divider.setPrefHeight(1);
                divider.setMaxWidth(Double.MAX_VALUE);
                divider.setStyle("-fx-background-color:#e9e2d7;");
                Label wage = label("Wage:  ₹" + w.wage + " / day",
                                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#d4a300;");
                Button hire = new Button("HIRE NOW");
                hire.setStyle(
                                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-text-fill:#b48700;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;");
                hire.setOnAction(e -> AppNavigator.information("Hire " + w.name,
                                "Your hiring request for " + w.name + " has been initiated. We will connect you shortly."));
                Region gap = new Region();
                HBox.setHgrow(gap, Priority.ALWAYS);
                HBox bottom = new HBox(wage, gap, hire);
                bottom.setAlignment(Pos.CENTER_LEFT);
                VBox card = new VBox(16, top, divider, bottom);
                card.setPrefSize(360, 194);
                card.setPadding(new Insets(20));
                card.setStyle(cardStyle(false));
                card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
                card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
                card.setOnMouseClicked(e -> { javafx.stage.Stage stage = (javafx.stage.Stage) card.getScene().getWindow(); javafx.scene.Scene currentScene = card.getScene(); stage.setScene(new RecruiterWorkerProfilePage(w.name, "Electrician", w.demographic, w.location, w.wage, w.photo).getProfileScene(() -> stage.setScene(currentScene), currentScene)); });
                return card;
        }

        private String cardStyle(boolean active) {
            return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                    + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                    + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                    + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
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
                if (path == null || path.isBlank()) return null;
                if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                        return new Image(path, true);
                }
                var resource = getClass().getResource(path);
                return resource == null ? null : new Image(resource.toExternalForm());
        }
}
