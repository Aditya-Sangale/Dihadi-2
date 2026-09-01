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

/** Recruiter search results for the General Labour category. */
public class GeneralLabourResultsPage {
        private static final String PAPER = "#f3e7ce";
        private static final String INK = "#1e1b15";
        private static final String GOLD = "#735c00";
        private static final String BORDER = "#d0c5af";
        private static final String[] PHOTOS = {
                        "/assets/images/general-labour/skill-00.jpg", "/assets/images/general-labour/skill-01.jpg",
                        "/assets/images/general-labour/skill-02.jpg", "/assets/images/general-labour/skill-03.jpg",
                        "/assets/images/general-labour/skill-04.jpg", "/assets/images/general-labour/skill-05.jpg",
                        "/assets/images/general-labour/skill-06.jpg", "/assets/images/general-labour/skill-07.jpg"
        };
        private static final String[][] WORKERS = {
                        { "Ram kumar Setu", "40 Years, Male", "General Labour", "Maharashtra", "600" },
                        { "Arjun Patil", "32 Years, Male", "Shuttering Helper", "Gujarat", "650" },
                        { "Sunil Desai", "28 Years, Male", "Mine Excavator", "Karnataka", "700" },
                        { "Prakash M", "45 Years, Male", "General Labour", "Maharashtra", "550" },
                        { "Lakshmi Bai", "35 Years, Female", "Site Cleaner", "Gujarat", "500" },
                        { "Vikram Singh", "50 Years, Male", "Mason Helper", "Karnataka", "750" },
                        { "Ajay Varma", "34 Years, Male", "General Labour", "Uttar Pradesh", "620" },
                        { "Meera Bai", "29 Years, Female", "Site Cleaner", "Madhya Pradesh", "580" },
                        { "Harish Singh", "52 Years, Male", "Mason Helper", "Rajasthan", "740" },
                        { "Rahul Patil", "26 Years, Male", "Shuttering Helper", "Maharashtra", "610" },
                        { "Anita Devi", "31 Years, Female", "Material Shifting", "Bihar", "560" },
                        { "Suresh Kumar", "38 Years, Male", "Concrete Mixer", "Haryana", "690" },
                        { "Kavita Rani", "27 Years, Female", "Site Helper", "Punjab", "590" },
                        { "Manoj Yadav", "35 Years, Male", "Loading Worker", "Delhi", "630" },
                        { "Vinod Rao", "24 Years, Male", "Road Construction", "Tamil Nadu", "650" },
                        { "Sunita Sharma", "30 Years, Female", "General Labour", "Karnataka", "600" }
        };

        public Scene getGeneralLabourScene(Runnable back) {
                BorderPane page = new BorderPane();
                page.setTop(standardHeader());
                page.setCenter(body(back));
                page.setStyle("-fx-background-color:" + PAPER + ";");
                StackPane root = new StackPane(page);
                root.setPadding(new Insets(24));
                root.setStyle("-fx-background-color:" + PAPER + ";");
                return new Scene(root, 1400, 780);
        }

        private ScrollPane body(Runnable back) {
                Label title = label("Looking for Skilled General Labour",
                                "-fx-font-family:'Georgia';-fx-font-size:35px;-fx-font-weight:800;-fx-text-fill:#574500;");
                VBox content = new VBox(31, title, hero(), filters(), results(), bottomActions(back), footer());
                content.setMaxWidth(1190);
                content.setPadding(new Insets(45, 0, 46, 0));
                StackPane canvas = new StackPane(content);
                canvas.setAlignment(Pos.TOP_CENTER);
                canvas.setPadding(new Insets(0, 38, 0, 38));
                canvas.setStyle("-fx-background-color:" + PAPER + ";");
                ScrollPane scroll = new ScrollPane(canvas);
                scroll.setFitToWidth(true);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
                return scroll;
        }

        private HBox hero() {
                // The source photograph is 16:9, so retain that ratio instead of stretching it.
                ImageView image = image("/assets/images/generalLabour.jpeg", 480, 268);
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
                                "\"Behind every great project are the tireless hands that build it. Hire verified, skilled, and dedicated general labour ready to bring your vision to life.\"",
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

        private VBox filters() {
                FlowPane fields = new FlowPane(16, 12,
                                field("Select Country", combo("India")),
                                field("Select State", combo("Maharashtra", "Gujarat", "Karnataka")),
                                field("Select City", combo("Mumbai", "Pune", "Nagpur")),
                                field("Select Pincode", pincode()));
                VBox box = new VBox(fields);
                box.setPadding(new Insets(17, 20, 17, 20));
                box.setStyle(
                                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),8,0,0,2px);");
                return box;
        }

        private static class WorkerCardData {
                String name;
                String age;
                String skill;
                String location;
                String wage;
                String photo;

                WorkerCardData(String name, String age, String skill, String location, String wage, String photo) {
                        this.name = name;
                        this.age = age;
                        this.skill = skill;
                        this.location = location;
                        this.wage = wage;
                        this.photo = photo;
                }
        }

        private java.util.List<WorkerCardData> getAllLabourWorkers() {
                java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
                try {
                        java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
                        if (realWorkers != null) {
                                int pIdx = 0;
                                for (com.dihadi.model.Worker w : realWorkers) {
                                        if (w.getWorkerType() != null && (w.getWorkerType().toLowerCase().contains("labour") || w.getWorkerType().toLowerCase().contains("labor"))) {
                                                String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                                                  (w.getLastName() != null ? w.getLastName() : "")).trim();
                                                if (fullName.isBlank()) fullName = "Verified Worker";
                                                String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                                              + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                                                String skillTag = w.getSubSkill() != null && !w.getSubSkill().isBlank() ? w.getSubSkill() : "General Labour";
                                                String loc = (w.getCity() != null && !w.getCity().isBlank() ? w.getCity() + ", " : "") +
                                                             (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                                                String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "850";
                                                String photo = w.getProfilePhotoUrl() != null && !w.getProfilePhotoUrl().isBlank() 
                                                               ? w.getProfilePhotoUrl() : PHOTOS[pIdx % PHOTOS.length];
                                                pIdx++;
                                                list.add(new WorkerCardData(fullName, demo, skillTag, loc, wage, photo));
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                for (int i = 0; i < WORKERS.length; i++) {
                        list.add(new WorkerCardData(WORKERS[i][0], WORKERS[i][1], WORKERS[i][2], WORKERS[i][3], WORKERS[i][4], PHOTOS[i % PHOTOS.length]));
                }
                return list;
        }

        private TilePane results() {
                TilePane grid = new TilePane();
                grid.setPrefColumns(3);
                grid.setHgap(26);
                grid.setVgap(24);
                for (WorkerCardData w : getAllLabourWorkers())
                        grid.getChildren().add(workerCard(w));
                return grid;
        }

        private VBox workerCard(WorkerCardData worker) {
                ImageView portrait = image(worker.photo, 64, 64);
                portrait.setPreserveRatio(false);
                portrait.setClip(new Circle(32, 32, 32));
                StackPane avatar = new StackPane(portrait);
                avatar.setPrefSize(64, 64);
                avatar.setStyle(
                                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
                Label name = label(worker.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
                Label age = label(worker.age, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                Label skill = label(worker.skill,
                                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
                Label location = label("⌖  " + worker.location, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
                VBox details = new VBox(4, name, age, skill, location);
                HBox top = new HBox(14, avatar, details);
                top.setAlignment(Pos.TOP_LEFT);
                Region divider = new Region();
                divider.setMinHeight(1);
                divider.setPrefHeight(1);
                divider.setMaxWidth(Double.MAX_VALUE);
                divider.setStyle("-fx-background-color:#e9e2d7;");
                Label wage = label("Wage:  ₹" + worker.wage + " / day",
                                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#d4a300;");
                Button hire = new Button("HIRE NOW");
                hire.setStyle(
                                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-text-fill:#b48700;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;");
                hire.setOnAction(e -> AppNavigator.information("Hire " + worker.name,
                                "Your hiring request for " + worker.name + " has been initiated. We will connect you shortly."));
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
                card.setOnMouseClicked(e -> { javafx.stage.Stage stage = (javafx.stage.Stage) card.getScene().getWindow(); javafx.scene.Scene currentScene = card.getScene(); stage.setScene(new RecruiterWorkerProfilePage(worker.name, "General Labour", worker.age, worker.location, worker.wage, worker.photo).getProfileScene(() -> stage.setScene(currentScene), currentScene)); });
                return card;
        }

        private String cardStyle(boolean active) {
            return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                    + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                    + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                    + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
        }        /** Shared DIHADI header, matching the Home, Worker, and recruiter pages. */
        private BorderPane standardHeader() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
                logo.setPreserveRatio(true);
                logo.setSmooth(true);
                Label title = label("DIHADI",
                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
                HBox brand = new HBox(10, logo, title);
                brand.setAlignment(Pos.CENTER_LEFT);
                HBox navigation = new HBox(12, standardNav("Home", false), standardNav("Business", false),
                                standardNav("Worker", false),
                                standardNav("Recruiter", true), standardNav("About Us", false),
                                standardNav("Contact Us", false));
                navigation.setAlignment(Pos.CENTER);
                Button admin = AppNavigator.createHeaderActionButton();
                HBox accountActions = new HBox(10, admin);
                accountActions.setAlignment(Pos.CENTER_RIGHT);
                BorderPane header = new BorderPane();
                header.setLeft(brand);
                header.setCenter(navigation);
                header.setRight(accountActions);
                header.setPadding(new Insets(16, 24, 14, 24));
                header.setStyle(
                                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
                return header;
        }

        private Button standardNav(String text, boolean active) {
                Button button = new Button(text);
                button.setStyle(
                                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                                                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                                                + (active ? "#735c00" : "transparent")
                                                + ";");
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

        /** Shared DIHADI footer, matching the other desktop pages. */
        private HBox bottomActions(Runnable backAction) {
                Button back = outlineButton("←  Back to categories");
                if (backAction != null) {
                        back.setOnAction(e -> backAction.run());
                }
                HBox row = new HBox(back);
                row.setAlignment(Pos.CENTER_LEFT);
                return row;
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

        private VBox field(String label, javafx.scene.Node input) {
                VBox box = new VBox(5,
                                label(label, "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-letter-spacing:.4px;"),
                                input);
                box.setPrefWidth(260);
                return box;
        }

        private ComboBox<String> combo(String selected, String... more) {
                ComboBox<String> box = new ComboBox<>();
                box.getItems().add(selected);
                box.getItems().addAll(more);
                box.setValue(selected);
                box.setPrefWidth(260);
                box.setPrefHeight(34);
                box.setStyle(inputStyle());
                return box;
        }

        private TextField pincode() {
                TextField field = new TextField();
                field.setPromptText("Enter Pincode");
                field.setPrefWidth(260);
                field.setPrefHeight(34);
                field.setStyle(inputStyle());
                return field;
        }

        private String inputStyle() {
                return "-fx-background-color:#f4ede2;-fx-background-radius:7px;-fx-border-color:" + BORDER
                                + ";-fx-border-radius:7px;-fx-font-size:12px;-fx-padding:5px 10px;";
        }

        private Label label(String text, String style) {
                Label label = new Label(text);
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
                if (path == null || path.isBlank()) return null;
                if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                        return new Image(path, true);
                }
                var resource = getClass().getResource(path);
                return resource == null ? null : new Image(resource.toExternalForm());
        }
}
