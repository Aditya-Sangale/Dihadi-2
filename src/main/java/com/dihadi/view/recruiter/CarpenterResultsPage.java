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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/** Recruiter results screen shown when the Carpenter category is selected. */
public class CarpenterResultsPage {
        private static final String[] PHOTOS = {
                        "/assets/images/worker/carpenter/skill-00.jpg", "/assets/images/worker/carpenter/skill-01.jpg",
                        "/assets/images/worker/carpenter/skill-02.jpg", "/assets/images/worker/carpenter/skill-03.jpg",
                        "/assets/images/worker/carpenter/skill-04.jpg", "/assets/images/worker/carpenter/skill-05.jpg",
                        "/assets/images/worker/carpenter/skill-06.jpg",
                        "/assets/images/worker/carpenter/skill-07.jpg" };
        private static final String[][] WORKERS = {
                        { "Ravi Kumar", "38 Years, Male", "Maharashtra", "2800" },
                        { "Deepak Singh", "32 Years, Male", "Delhi", "2200" },
                        { "Vikram Patel", "45 Years, Male", "Gujarat", "2600" },
                        { "Sunil Rao", "29 Years, Male", "Karnataka", "1900" },
                        { "Prakash Verma", "50 Years, Male", "Uttar Pradesh", "2500" },
                        { "Amit Sharma", "35 Years, Male", "Madhya Pradesh", "2100" },
                        { "Rajesh Kumar", "41 Years, Male", "Rajasthan", "2400" },
                        { "Varun Gupta", "27 Years, Male", "Haryana", "1800" },
                        { "Mohan Reddy", "48 Years, Male", "Telangana", "2700" },
                        { "Sanjay Kumar", "34 Years, Male", "Bihar", "2000" },
                        { "Nitin Sharma", "26 Years, Male", "Punjab", "1700" },
                        { "Anoop Singh", "44 Years, Male", "West Bengal", "2300" },
                        { "Ashok Patel", "30 Years, Male", "Tamil Nadu", "1950" },
                        { "Ramesh Yadav", "55 Years, Male", "Andhra Pradesh", "2800" },
                        { "Harsh Malhotra", "28 Years, Male", "Himachal Pradesh", "1650" },
                        { "Naveen Kumar", "36 Years, Male", "Uttarakhand", "2050" },
                        { "Saurabh Singh", "31 Years, Male", "Jharkhand", "2150" },
                        { "Vikas Rao", "42 Years, Male", "Goa", "2500" },
                        { "Arjun Patel", "25 Years, Male", "Chhattisgarh", "1600" } };

        public Scene getCarpenterScene(Runnable back) {
                BorderPane page = new BorderPane();
                page.setTop(header());
                page.setCenter(content(back));
                page.setStyle("-fx-background-color:#f3e7ce;");
                StackPane root = new StackPane(page);
                root.setPadding(new Insets(24));
                root.setStyle("-fx-background-color:#f3e7ce;");
                return new Scene(root, 1400, 780);
        }

        private ScrollPane content(Runnable back) {
                Label title = label("Looking for Skilled Carpenter",
                                "-fx-font-family:'Georgia';-fx-font-size:35px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                VBox page = new VBox(29, title, hero(), filterBar(), cards(), bottomActions(back), footer());
                page.setMaxWidth(1190);
                page.setPadding(new Insets(35, 0, 46, 0));
                StackPane canvas = new StackPane(page);
                canvas.setAlignment(Pos.TOP_CENTER);
                canvas.setPadding(new Insets(0, 38, 0, 38));
                canvas.setStyle("-fx-background-color:#f3e7ce;");
                ScrollPane scroll = new ScrollPane(canvas);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
                return scroll;
        }

        private HBox hero() {
                // The source photograph is 16:9, so retain that ratio instead of stretching it.
                ImageView image = image("/assets/images/carpenter.jpeg", 480, 268);
                image.setPreserveRatio(true);
                Rectangle imageClip = new Rectangle(480, 268);
                imageClip.setArcWidth(20);
                imageClip.setArcHeight(20);
                StackPane picture = new StackPane(image);
                picture.setPrefSize(480, 268);
                picture.setMinSize(480, 268);
                picture.setMaxSize(480, 268);
                picture.setClip(imageClip);
                picture.setStyle("-fx-background-radius:10px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),12,.18,0,3px);");
                Label quote = label(
                                "\"Crafting excellence through precision and passion. Hire verified, skilled, and experienced carpenters who turn your vision into masterpieces.\"",
                                "-fx-font-family:'Georgia',serif;-fx-font-size:24px;-fx-font-weight:700;-fx-text-fill:#272119;-fx-line-spacing:4px;");
                quote.setWrapText(true);
                quote.setPrefWidth(510);
                quote.setMaxWidth(510);
                HBox box = new HBox(82, picture, quote);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(32));
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

        private java.util.List<WorkerCardData> getAllCarpenterWorkers() {
                java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
                try {
                        java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
                        if (realWorkers != null) {
                                int pIdx = 0;
                                for (com.dihadi.model.Worker w : realWorkers) {
                                        if (w.getWorkerType() != null && w.getWorkerType().toLowerCase().contains("carpenter")) {
                                                String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                                                  (w.getLastName() != null ? w.getLastName() : "")).trim();
                                                if (fullName.isBlank()) fullName = "Verified Carpenter";
                                                String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                                              + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                                                String loc = (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                                                String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "2200";
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
                for (WorkerCardData w : getAllCarpenterWorkers())
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
                Label skill = label("Carpenter",
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
                card.setOnMouseClicked(e -> { javafx.stage.Stage stage = (javafx.stage.Stage) card.getScene().getWindow(); javafx.scene.Scene currentScene = card.getScene(); stage.setScene(new RecruiterWorkerProfilePage(w.name, "Carpenter", w.demographic, w.location, w.wage, w.photo).getProfileScene(() -> stage.setScene(currentScene), currentScene)); });
                return card;
        }

        private String cardStyle(boolean active) {
            return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                    + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                    + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                    + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
        }

        private HBox bottomActions(Runnable backAction) {
                Button back = outline("←  Back to categories");
                if (backAction != null) {
                        back.setOnAction(e -> backAction.run());
                }
                HBox row = new HBox(back);
                row.setAlignment(Pos.CENTER_LEFT);
                return row;
        }

        private BorderPane header() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
                logo.setPreserveRatio(true);
                logo.setSmooth(true);
                Label title = label("DIHADI",
                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
                HBox brand = new HBox(10, logo, title);
                brand.setAlignment(Pos.CENTER_LEFT);
                HBox navigation = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
                navigation.setAlignment(Pos.CENTER);
                Button admin = AppNavigator.createHeaderActionButton();
                HBox account = new HBox(10, admin);
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
                                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                                                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                                                + (active ? "#735c00" : "transparent") + ";");
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
                field.setPromptText("Pincode");
                field.setPrefWidth(160);
                field.setPrefHeight(31);
                field.setStyle(inputStyle());
                return field;
        }

        private String inputStyle() {
                return "-fx-background-color:#ffffff;-fx-background-radius:6px;-fx-border-color:#d0c5af;-fx-border-radius:6px;-fx-text-fill:#1e1b15;-fx-font-size:12px;-fx-padding:5px 10px;";
        }

        private Label label(String text, String style) {
                Label l = new Label(text);
                l.setStyle(style);
                return l;
        }

        private ImageView image(String path, double w, double h) {
                if (path == null || path.isBlank()) return new ImageView();
                if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                        return new ImageView(new Image(path, w, h, false, true));
                }
                var res = getClass().getResource(path);
                return res == null ? new ImageView() : new ImageView(new Image(res.toExternalForm(), w, h, false, true));
        }
}
