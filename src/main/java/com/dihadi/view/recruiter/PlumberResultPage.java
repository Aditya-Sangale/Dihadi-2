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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/** Recruiter marketplace screen for hiring verified plumbers. */
public class PlumberResultPage {
    private static final String PAPER = "#f3e7ce", INK = "#1e1b15", MUTED = "#4c4637", GOLD = "#735c00",
            BORDER = "#d0c5af";
    private static final String[] PHOTOS = { "/assets/images/worker/plumber/skill-00.jpg",
            "/assets/images/worker/plumber/skill-01.jpg", "/assets/images/worker/plumber/skill-02.jpg",
            "/assets/images/worker/plumber/skill-03.jpg", "/assets/images/worker/plumber/skill-04.jpg",
            "/assets/images/worker/plumber/skill-05.jpg", "/assets/images/worker/plumber/skill-06.jpg",
            "/assets/images/worker/plumber/skill-07.jpg", "/assets/images/worker/plumber/skill-08.jpg" };
    private static final String[][] PLUMBERS = {
            { "Hariprasad Yadav", "45 Years, Male", "Gangtok, Sikkim", "1,300", "Pipe Fitting" },
            { "Rajesh Sharma", "45 Years, Male", "New Delhi, Delhi", "1,500", "Sanitary Plumbing" },
            { "Manoj Kumar", "32 Years, Male", "Mumbai, Maharashtra", "1,400", "Leak Repair" },
            { "Sunil Patel", "50 Years, Male", "Ahmedabad, Gujarat", "1,600", "Commercial Plumbing" },
            { "Rahul Verma", "24 Years, Male", "Pune, Maharashtra", "900", "Pipe Fitting" },
            { "Vikram Singh", "38 Years, Male", "Bengaluru, Karnataka", "1,450", "Bathroom Fitting" },
            { "Amit Desai", "29 Years, Male", "Surat, Gujarat", "1,100", "Drainage" },
            { "Deepak Rao", "35 Years, Male", "Chennai, Tamil Nadu", "1,250", "Sanitary Plumbing" },
            { "Sanjay Mishra", "41 Years, Male", "Lucknow, Uttar Pradesh", "1,050", "Leak Repair" } };

    public Scene getPlumberScene(Runnable back) {
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
        Label title = l("Looking for Skilled Plumbers",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label sub = l("Hire dependable plumbing specialists for clean, reliable, and lasting infrastructure.",
                "-fx-font-size:14px;-fx-text-fill:" + MUTED + ";");
        VBox body = new VBox(30, new VBox(6, title, sub), hero(), filters(), resultTitle(), cards(), bottomActions(back), footer());
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
        // The source photograph is 16:9, so retain that ratio instead of stretching it.
        ImageView image = img("/assets/images/plumber.jpeg", 480, 268);
        image.setPreserveRatio(true);
        Rectangle imageClip = new Rectangle(480, 268);
        imageClip.setArcWidth(20);
        imageClip.setArcHeight(20);
        StackPane photo = new StackPane(image);
        photo.setPrefSize(480, 268);
        photo.setMinSize(480, 268);
        photo.setMaxSize(480, 268);
        photo.setClip(imageClip);
        photo.setStyle("-fx-background-radius:10px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),12,.18,0,3px);");
        Label quote = l(
                "\"Ensuring flawless flow and lasting infrastructure. Hire verified, skilled plumbers who bring expertise, reliability, and precision to your plumbing and sanitation projects.\"",
                "-fx-font-family:'Georgia',serif;-fx-font-size:24px;-fx-font-weight:700;-fx-text-fill:#272119;-fx-line-spacing:4px;");
        quote.setWrapText(true);
        quote.setPrefWidth(510);
        quote.setMaxWidth(510);
        HBox box = new HBox(82, photo, quote);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(32));
        box.setStyle(
                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;");
        return box;
    }

    private HBox filters() {
        ComboBox<String> country = combo("Select Country", "India"),
                state = combo("Select State", "Sikkim", "Maharashtra", "Delhi", "Gujarat"),
                city = combo("Select City", "Gangtok", "Mumbai", "Pune", "New Delhi");
        TextField pin = new TextField();
        pin.setPromptText("Select Pincode");
        pin.setPrefSize(210, 39);
        pin.setStyle(input());
        HBox row = new HBox(14, country, state, city, pin);
        row.setPadding(new Insets(22));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
                "-fx-background-color:#eeeeee;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),7,0,0,2px);");
        return row;
    }

    private HBox resultTitle() {
        Label heading = l("Verified Plumbers Near You",
                "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label count = l("9 skilled professionals", "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox row = new HBox(heading, space, count);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static class WorkerCardData {
        String name;
        String age;
        String location;
        String wage;
        String skill;
        String photo;

        WorkerCardData(String name, String age, String location, String wage, String skill, String photo) {
            this.name = name;
            this.age = age;
            this.location = location;
            this.wage = wage;
            this.skill = skill;
            this.photo = photo;
        }
    }

    private java.util.List<WorkerCardData> getAllPlumberWorkers() {
        java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
            if (realWorkers != null) {
                int pIdx = 0;
                for (com.dihadi.model.Worker w : realWorkers) {
                    if (w.getWorkerType() != null && w.getWorkerType().toLowerCase().contains("plumber")) {
                        String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                          (w.getLastName() != null ? w.getLastName() : "")).trim();
                        if (fullName.isBlank()) fullName = "Verified Plumber";
                        String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                      + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                        String loc = (w.getCity() != null && !w.getCity().isBlank() ? w.getCity() + ", " : "") +
                                     (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                        String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "950";
                        String skillTag = w.getSubSkill() != null && !w.getSubSkill().isBlank() ? w.getSubSkill() : "Plumber";
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
        for (int i = 0; i < PLUMBERS.length; i++) {
            list.add(new WorkerCardData(PLUMBERS[i][0], PLUMBERS[i][1], PLUMBERS[i][2], PLUMBERS[i][3], PLUMBERS[i][4], PHOTOS[i % PHOTOS.length]));
        }
        return list;
    }

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (WorkerCardData w : getAllPlumberWorkers())
            grid.getChildren().add(card(w));
        return grid;
    }

    private VBox card(WorkerCardData p) {
        ImageView portrait = img(p.photo, 52, 52);
        portrait.setPreserveRatio(false);
        portrait.setClip(new Circle(26, 26, 26));
        StackPane avatar = new StackPane(portrait);
        avatar.setPrefSize(64, 64);
        avatar.setMinSize(64, 64);
        avatar.setMaxSize(64, 64);
        avatar.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:999px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-padding:4px;");
        Label name = l(p.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label age = l(p.age, "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Label skill = l(p.skill,
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
        Label location = l("•  Based in " + p.location, "-fx-font-size:11px;-fx-text-fill:" + MUTED + ";");
        Label availability = l("•  Available for new projects",
                "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#477044;");
        VBox details = new VBox(4, name, age, skill, location, availability);
        HBox top = new HBox(14, avatar, details);
        top.setAlignment(Pos.TOP_LEFT);
        Region divider = new Region();
        divider.setMinHeight(1);
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#e9e2d7;");
        Label wage = l("Wage:  ₹" + p.wage + " / day",
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
        hire.setOnAction(e -> openPlumberProfile(card, p));
        card.setOnMouseClicked(e -> openPlumberProfile(card, p));
        return card;
    }

    private void openPlumberProfile(VBox card, WorkerCardData plumber) {
        Stage stage = (Stage) card.getScene().getWindow();
        Scene currentScene = card.getScene();
        stage.setScene(new RecruiterWorkerProfilePage(plumber.name, "Plumber", plumber.age, plumber.location,
                plumber.wage, plumber.photo, "", () -> RecruiterWorkerProfilePage.markResultCardHired(card)).getProfileScene(() -> stage.setScene(currentScene), currentScene));
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:"
                + (active ? "#d4af37" : "#e9e2d7") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:15px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".13" : ".08") + ")," + (active ? "18" : "11") + ",0,0," + (active ? "4" : "2") + "px);";
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
        ImageView logo = img("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo, l("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
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

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                        + (active ? GOLD : "#4d4635") + ";-fx-border-color:" + (active ? GOLD : "transparent") + ";");
        b.setOnAction(e -> AppNavigator.open((Stage) b.getScene().getWindow(), text));
        return b;
    }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:"
                        + GOLD + ";-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private VBox footer() {
        ImageView logo = img("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        Label brand = l("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = l(
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
        VBox foot = new VBox(22, top, l("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        foot.setPadding(new Insets(32, 42, 24, 42));
        foot.setMaxWidth(1180);
        foot.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return foot;
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
        VBox column = new VBox(7, l(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
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
        ComboBox<String> b = new ComboBox<>();
        b.setPromptText(prompt);
        b.getItems().addAll(values);
        b.setPrefSize(210, 39);
        b.setStyle(input());
        return b;
    }

    private String input() {
        return "-fx-background-color:#ffffff;-fx-background-radius:7px;-fx-border-color:#cfc6b2;-fx-border-radius:7px;-fx-text-fill:"
                + INK + ";-fx-font-size:12px;-fx-padding:6px 10px;";
    }

    private Label l(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI';" + style);
        return label;
    }

    private ImageView img(String path, double w, double h) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(w);
        view.setFitHeight(h);
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
