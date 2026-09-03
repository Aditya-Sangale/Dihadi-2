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

/** A recruiter-facing directory for finding and hiring verified masons. */
public class MasonResultPage {
    private static final String PAPER = "#f3e7ce";
    private static final String INK = "#1e1b15";
    private static final String MUTED = "#4c4637";
    private static final String GOLD = "#735c00";
    private static final String BORDER = "#d0c5af";
    private static final String[] PHOTOS = {
            "/assets/images/worker/mason/skill-01.jpg", "/assets/images/worker/mason/skill-02.jpg",
            "/assets/images/worker/mason/skill-03.jpg", "/assets/images/worker/mason/skill-04.jpg",
            "/assets/images/worker/mason/skill-05.jpg", "/assets/images/worker/mason/skill-06.jpg",
            "/assets/images/worker/mason/skill-07.jpg", "/assets/images/worker/mason/skill-08.jpg",
            "/assets/images/worker/mason/skill-09.jpg", "/assets/images/worker/mason/skill-10.jpg",
            "/assets/images/worker/mason/skill-11.jpg", "/assets/images/worker/mason/skill-12.jpg" };
    private static final String[][] WORKERS = {
            { "Ramesh Kumar", "42 Years, Male", "Jaipur, Rajasthan", "950", "Brick Mason" },
            { "Mahesh Patil", "35 Years, Male", "Mumbai, Maharashtra", "1,050", "Construction Mason" },
            { "Imran Khan", "31 Years, Male", "New Delhi, Delhi", "900", "Plaster Mason" },
            { "Sanjay Yadav", "46 Years, Male", "Lucknow, Uttar Pradesh", "880", "Brick Mason" },
            { "Dinesh Solanki", "38 Years, Male", "Ahmedabad, Gujarat", "1,000", "Stone Mason" },
            { "Kishan Lal", "50 Years, Male", "Raipur, Chhattisgarh", "920", "Tile Mason" },
            { "Raju Verma", "29 Years, Male", "Pune, Maharashtra", "860", "Construction Mason" },
            { "Babulal Meena", "44 Years, Male", "Kota, Rajasthan", "980", "Brick Mason" },
            { "Arvind Sharma", "34 Years, Male", "Indore, Madhya Pradesh", "930", "Plaster Mason" },
            { "Suresh Naik", "40 Years, Male", "Bengaluru, Karnataka", "1,100", "Stone Mason" },
            { "Manoj Tiwari", "36 Years, Male", "Varanasi, Uttar Pradesh", "900", "Tile Mason" },
            { "Gopal Das", "48 Years, Male", "Bhubaneswar, Odisha", "940", "Construction Mason" } };

    public Scene getMasonScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content(back));
        page.setStyle("-fx-background-color:" + PAPER + ";");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(root, 1400, 780);
    }

    private ScrollPane content(Runnable back) {
        Label title = label("Looking for Skilled Mason",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label count = label("12 verified masons available near you", "-fx-font-size:13px;-fx-text-fill:" + MUTED + ";");
        VBox heading = new VBox(5, title, count);
        VBox body = new VBox(28, heading, hero(), filterBar(), resultsHeader(), cards(), bottomActions(back), footer());
        body.setMaxWidth(1190);
        body.setPadding(new Insets(40, 0, 48, 0));
        StackPane canvas = new StackPane(body);
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
        ImageView photo = image("/assets/images/worker/mason/hero.jpg", 480, 343);
        photo.setPreserveRatio(true);
        Rectangle imageClip = new Rectangle(480, 268);
        imageClip.setArcWidth(20);
        imageClip.setArcHeight(20);
        StackPane photoBox = new StackPane(photo);
        photoBox.setPrefSize(480, 268);
        photoBox.setMinSize(480, 268);
        photoBox.setMaxSize(480, 268);
        photoBox.setClip(imageClip);
        photoBox.setStyle("-fx-background-radius:10px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),12,.18,0,3px);");
        Label quote = label(
                "\"Laying the foundation of tomorrow, one brick at a time. Hire verified, skilled masons who bring strength, precision, and enduring craftsmanship to every construction project.\"",
                "-fx-font-family:'Georgia',serif;-fx-font-size:24px;-fx-font-weight:700;-fx-text-fill:#272119;-fx-line-spacing:4px;");
        quote.setWrapText(true);
        quote.setPrefWidth(510);
        quote.setMaxWidth(510);
        HBox box = new HBox(82, photoBox, quote);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(32));
        box.setStyle(
                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;");
        return box;
    }

    private HBox filterBar() {
        Label filterIcon = label("☰", "-fx-font-size:17px;-fx-text-fill:" + GOLD + ";");
        Label filterText = label("Filters", "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:" + MUTED + ";");
        HBox filterLabel = new HBox(7, filterIcon, filterText);
        filterLabel.setAlignment(Pos.CENTER_LEFT);
        HBox row = new HBox(13, filterLabel,
                combo("Select Country", "India"),
                combo("Select State", "Rajasthan", "Maharashtra", "Delhi", "Gujarat", "Uttar Pradesh"),
                combo("Select City", "Jaipur", "Mumbai", "New Delhi", "Ahmedabad", "Lucknow"), pincode());
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 18, 15, 18));
        row.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:13px;-fx-border-color:" + BORDER
                + ";-fx-border-radius:13px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),7,0,0,2px);");
        return row;
    }

    private HBox resultsHeader() {
        Label heading = label("Available Masons",
                "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label note = label("Profiles are verified by DIHADI", "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(heading, spacer, note);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private static class WorkerCardData {
        String name;
        String demographic;
        String location;
        String wage;
        String skill;
        String photo;

        WorkerCardData(String name, String demographic, String location, String wage, String skill, String photo) {
            this.name = name;
            this.demographic = demographic;
            this.location = location;
            this.wage = wage;
            this.skill = skill;
            this.photo = photo;
        }
    }

    private java.util.List<WorkerCardData> getAllMasonWorkers() {
        java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
            if (realWorkers != null) {
                int pIdx = 0;
                for (com.dihadi.model.Worker w : realWorkers) {
                    if (w.getWorkerType() == null || w.getWorkerType().toLowerCase().contains("mason")) {
                        String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                          (w.getLastName() != null ? w.getLastName() : "")).trim();
                        if (fullName.isBlank()) fullName = "Verified Worker";
                        String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                      + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                        String loc = (w.getCity() != null && !w.getCity().isBlank() ? w.getCity() : "Pune") + ", " +
                                     (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                        String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "950";
                        String skillTag = w.getSubSkill() != null && !w.getSubSkill().isBlank() ? w.getSubSkill() : "Brick Mason";
                        String photo = w.getProfilePhotoUrl() != null && !w.getProfilePhotoUrl().isBlank() 
                                       ? w.getProfilePhotoUrl() : PHOTOS[pIdx % PHOTOS.length];
                        pIdx++;
                        list.add(new WorkerCardData(fullName, demo, loc, wage, skillTag, photo));
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

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (WorkerCardData w : getAllMasonWorkers())
            grid.getChildren().add(card(w));
        return grid;
    }

    private VBox card(WorkerCardData m) {
        ImageView portrait = image(m.photo, 52, 52);
        portrait.setPreserveRatio(false);
        portrait.setClip(new Circle(26, 26, 26));
        StackPane avatar = new StackPane(portrait);
        avatar.setPrefSize(64, 64);
        avatar.setMinSize(64, 64);
        avatar.setMaxSize(64, 64);
        avatar.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:999px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-padding:4px;");
        Label name = label(m.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label age = label(m.demographic, "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Label skill = label(m.skill,
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
        Label location = label("•  Based in " + m.location, "-fx-font-size:11px;-fx-text-fill:" + MUTED + ";");
        Label availability = label("•  Available for new projects",
                "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#477044;");
        VBox details = new VBox(4, name, age, skill, location, availability);
        HBox top = new HBox(14, avatar, details);
        top.setAlignment(Pos.TOP_LEFT);
        Region divider = new Region();
        divider.setMinHeight(1);
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#e9e2d7;");
        Label wage = label("Wage:  ₹" + m.wage + " / day",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#d4a300;");
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-text-fill:#b48700;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bottom = new HBox(wage, gap, hire);
        bottom.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(16, top, divider, bottom);
        card.setPrefSize(360, 220);
        card.setPadding(new Insets(20));
        card.setStyle(cardStyle(false));
        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
        hire.setOnAction(e -> openMasonProfile(card, m));
        card.setOnMouseClicked(e -> openMasonProfile(card, m));
        return card;
    }

    private void openMasonProfile(VBox card, WorkerCardData mason) {
        Stage stage = (Stage) card.getScene().getWindow();
        Scene currentScene = card.getScene();
        stage.setScene(new RecruiterWorkerProfilePage(mason.name, "Mason", mason.demographic, mason.location,
                mason.wage, mason.photo, "", () -> RecruiterWorkerProfilePage.markResultCardHired(card)).getProfileScene(() -> stage.setScene(currentScene), currentScene));
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : BORDER)
                + ";-fx-border-width:" + (active ? "2px" : "1px") + ";-fx-border-radius:13px;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39," + (active ? ".15" : ".07") + "),"
                + (active ? "15" : "8") + ",0,0," + (active ? "5" : "2") + "px);";
    }

    private HBox bottomActions(Runnable backAction) {
        Button back = outlineButton("←  Back to categories");
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
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, title);
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, navButton("Home", false), navButton("Business", false), navButton("Worker", false),
                navButton("Recruiter", true), navButton("About Us", false), navButton("Contact Us", false));
        nav.setAlignment(Pos.CENTER);
        Button admin = AppNavigator.createHeaderActionButton();
        HBox account = new HBox(10, admin);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(nav);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:" + BORDER
                + ";-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private Button navButton(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                        + (active ? GOLD : "#4d4635") + ";-fx-border-color:" + (active ? GOLD : "transparent") + ";");
        button.setOnAction(e -> AppNavigator.open((Stage) button.getScene().getWindow(), text));
        return button;
    }

    private Button primaryButton(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button outlineButton(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:"
                        + GOLD + ";-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
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

    private ComboBox<String> combo(String prompt, String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.setPromptText(prompt);
        box.getItems().addAll(values);
        box.setPrefWidth(184);
        box.setPrefHeight(35);
        box.setStyle(inputStyle());
        return box;
    }

    private TextField pincode() {
        TextField field = new TextField();
        field.setPromptText("Select Pincode");
        field.setPrefWidth(175);
        field.setPrefHeight(35);
        field.setStyle(inputStyle());
        return field;
    }

    private String inputStyle() {
        return "-fx-background-color:#ffffff;-fx-background-radius:7px;-fx-border-color:#cfc6b2;-fx-border-radius:7px;-fx-text-fill:"
                + INK + ";-fx-font-size:12px;-fx-padding:5px 10px;";
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
