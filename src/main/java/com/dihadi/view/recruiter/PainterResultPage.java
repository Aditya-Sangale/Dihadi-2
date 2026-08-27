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
import javafx.stage.Stage;

/** Recruiter search results for verified painting professionals. */
public class PainterResultPage {
    private static final String PAPER = "#fff8f0", INK = "#1e1b15", MUTED = "#4c4637", GOLD = "#735c00",
            BORDER = "#d0c5af";
    private static final String[] PHOTOS = { "/assets/images/worker/painter/skill-00.jpg",
            "/assets/images/worker/painter/skill-01.jpg", "/assets/images/worker/painter/skill-02.jpg",
            "/assets/images/worker/painter/skill-03.jpg", "/assets/images/worker/painter/skill-04.jpg",
            "/assets/images/worker/painter/skill-05.jpg", "/assets/images/worker/painter/skill-06.jpg",
            "/assets/images/worker/painter/skill-07.jpg", "/assets/images/worker/painter/skill-08.jpg" };
    private static final String[][] PAINTERS = {
            { "Rahul Kumar", "28 Years, Male", "Mumbai, Maharashtra", "800", "Interior Painter" },
            { "Amit Singh", "32 Years, Male", "Pune, Maharashtra", "900", "Wall Finishing" },
            { "Suresh Patel", "45 Years, Male", "New Delhi, NCR", "1,200", "Texture Painter" },
            { "Vikram Das", "29 Years, Male", "Bengaluru, Karnataka", "1,000", "Exterior Painter" },
            { "Manoj Tiwari", "38 Years, Male", "Kolkata, West Bengal", "850", "Interior Painter" },
            { "Priya Sharma", "26 Years, Female", "Mumbai, Maharashtra", "950", "Decorative Painter" },
            { "Ravi Kumar", "41 Years, Male", "Hyderabad, Telangana", "1,100", "Texture Painter" },
            { "Sanjay Gupta", "35 Years, Male", "Chennai, Tamil Nadu", "1,050", "Exterior Painter" },
            { "Geeta Devi", "46 Years, Female", "Bhopal, Madhya Pradesh", "1,100", "Wall Finishing" } };

    public Scene getPainterScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content(back));
        page.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content(Runnable back) {
        Label title = label("Looking for Skilled Painters",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label intro = label("Find trusted professionals who bring color, detail, and lasting finish to every space.",
                "-fx-font-size:14px;-fx-text-fill:" + MUTED + ";");
        VBox body = new VBox(30, new VBox(6, title, intro), hero(), filters(), resultsHeader(), cards(), bottomActions(back),
                footer());
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
        ImageView photo = image("/assets/images/painter.jpeg", 560, 340);
        photo.setPreserveRatio(false);
        StackPane imageBox = new StackPane(photo);
        imageBox.setPrefSize(560, 340);
        imageBox.setStyle(
                "-fx-background-radius:14px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.11),12,0,0,3px);");
        Label quote = label(
                "\"Transform your spaces with DIHADI’s trusted professionals. Bringing color, precision, and life to your walls, one stroke at a time.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:" + MUTED
                        + ";-fx-line-spacing:4px;");
        quote.setWrapText(true);
        quote.setMaxWidth(480);
        Label detail = label("EXPERT PAINTERS • QUALITY FINISHES",
                "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:" + GOLD + ";");
        VBox words = new VBox(22, quote, detail);
        words.setPadding(new Insets(24, 26, 24, 28));
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefWidth(560);
        words.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1px 1px 1px 4px;-fx-border-radius:14px;");
        HBox row = new HBox(40, imageBox, words);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox filters() {
        ComboBox<String> country = combo("India", "India"),
                state = combo("Select State", "Maharashtra", "Delhi", "Karnataka", "Gujarat"),
                city = combo("Select City", "Mumbai", "Pune", "Bengaluru", "New Delhi");
        TextField pin = new TextField();
        pin.setPromptText("Enter Pincode");
        pin.setPrefSize(210, 39);
        pin.setStyle(fieldStyle());
        VBox countryField = field("COUNTRY", country), stateField = field("STATE", state),
                cityField = field("CITY", city), pinField = field("PINCODE", pin);
        Button search = new Button("SEARCH");
        search.setStyle("-fx-background-color:" + GOLD
                + ";-fx-background-radius:8px;-fx-text-fill:#f6d676;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:12px 23px;-fx-cursor:hand;");
        search.setOnAction(e -> AppNavigator.information("Painter search",
                "Painter profiles have been updated for your selected location."));
        HBox row = new HBox(13, countryField, stateField, cityField, pinField, search);
        row.setAlignment(Pos.BOTTOM_LEFT);
        row.setPadding(new Insets(21));
        row.setStyle("-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:" + BORDER
                + ";-fx-border-radius:13px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),8,0,0,2px);");
        return row;
    }

    private VBox field(String name, javafx.scene.Node input) {
        Label label = label(name,
                "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:.7px;-fx-text-fill:" + MUTED + ";");
        VBox field = new VBox(6, label, input);
        field.setPrefWidth(210);
        return field;
    }

    private HBox resultsHeader() {
        Label heading = label("Available Painters",
                "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label count = label("9 verified professionals", "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox row = new HBox(heading, gap, count);
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

    private java.util.List<WorkerCardData> getAllPainterWorkers() {
        java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
            if (realWorkers != null) {
                int pIdx = 0;
                for (com.dihadi.model.Worker w : realWorkers) {
                    if (w.getWorkerType() != null && w.getWorkerType().toLowerCase().contains("painter")) {
                        String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                          (w.getLastName() != null ? w.getLastName() : "")).trim();
                        if (fullName.isBlank()) fullName = "Verified Painter";
                        String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                      + ", " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                        String loc = (w.getCity() != null && !w.getCity().isBlank() ? w.getCity() + ", " : "") +
                                     (w.getState() != null && !w.getState().isBlank() ? w.getState() : "Maharashtra");
                        String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "950";
                        String skillTag = w.getSubSkill() != null && !w.getSubSkill().isBlank() ? w.getSubSkill() : "Painter";
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
        for (int i = 0; i < PAINTERS.length; i++) {
            list.add(new WorkerCardData(PAINTERS[i][0], PAINTERS[i][1], PAINTERS[i][2], PAINTERS[i][3], PAINTERS[i][4], PHOTOS[i % PHOTOS.length]));
        }
        return list;
    }

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (WorkerCardData w : getAllPainterWorkers())
            grid.getChildren().add(card(w));
        return grid;
    }

    private VBox card(WorkerCardData painter) {
        ImageView portrait = image(painter.photo, 64, 64);
        portrait.setPreserveRatio(false);
        portrait.setClip(new Circle(32, 32, 32));
        StackPane avatar = new StackPane(portrait);
        avatar.setPrefSize(64, 64);
        avatar.setStyle(
                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = label(painter.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label age = label(painter.age, "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Label skill = label(painter.skill, "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD
                + ";-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
        Label location = label("⌖  " + painter.location, "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        VBox details = new VBox(4, name, age, skill, location);
        HBox top = new HBox(14, avatar, details);
        top.setAlignment(Pos.TOP_LEFT);
        Region divider = new Region();
        divider.setMinHeight(1);
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#e9e2d7;");
        Label wage = label("Wage:  ₹" + painter.wage + " / day",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-text-fill:#b48700;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + painter.name,
                "Your hiring request for " + painter.name + " has been initiated. We will connect you shortly."));
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
        card.setOnMouseClicked(e -> { javafx.stage.Stage stage = (javafx.stage.Stage) card.getScene().getWindow(); javafx.scene.Scene currentScene = card.getScene(); stage.setScene(new RecruiterWorkerProfilePage(painter.name, "Painter", painter.age, painter.location, painter.wage, painter.photo).getProfileScene(() -> stage.setScene(currentScene), currentScene)); });
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

        Button button = new Button("View More Painters");
        button.setStyle("-fx-background-color:#ffffff;-fx-background-radius:19px;-fx-border-color:" + BORDER
                + ";-fx-border-radius:19px;-fx-text-fill:" + GOLD
                + ";-fx-font-size:12px;-fx-font-weight:800;-fx-padding:10px 25px;-fx-cursor:hand;");
        button.setOnAction(
                e -> AppNavigator.information("Painters", "More verified painter profiles will be loaded here."));
        
        HBox centerBox = new HBox(button);
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
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
        nav.setAlignment(Pos.CENTER);
        Button login = outline("Login"), signup = primary("Sign Up");
        login.setOnAction(e -> AppNavigator.adminLoginInProgress());
        signup.setOnAction(e -> AppNavigator.adminLoginInProgress());
        HBox account = new HBox(10, login, signup);
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
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
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
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 58, 58);
        logo.setPreserveRatio(true);
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(300);
        VBox identity = new VBox(9,
                new HBox(12, logo, label("DIHADI",
                        "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;-fx-letter-spacing:1px;")),
                promise);
        identity.setPrefWidth(340);
        HBox main = new HBox(58, identity, footerCol("Company", "About Dihadi", "Contact Us"),
                footerCol("Opportunities", "Find Work", "Worker Categories"),
                footerCol("Support", "Help Centre", "Privacy & Terms"));
        main.setAlignment(Pos.TOP_LEFT);
        VBox foot = new VBox(24, main, label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        foot.setMaxWidth(1180);
        foot.setPadding(new Insets(32, 42, 24, 42));
        foot.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return foot;
    }

    private VBox footerCol(String head, String... links) {
        VBox col = new VBox(8, label(head, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"));
        col.setPrefWidth(150);
        for (String link : links) {
            Button b = new Button(link);
            b.setStyle(
                    "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.80;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-cursor:hand;");
            b.setOnAction(e -> AppNavigator.openFooterLink((Stage) b.getScene().getWindow(), link));
            col.getChildren().add(b);
        }
        return col;
    }

    private ComboBox<String> combo(String prompt, String... values) {
        ComboBox<String> b = new ComboBox<>();
        b.setPromptText(prompt);
        b.getItems().addAll(values);
        b.setPrefSize(210, 39);
        b.setStyle(fieldStyle());
        return b;
    }

    private String fieldStyle() {
        return "-fx-background-color:#eeeeee;-fx-background-radius:7px;-fx-border-color:transparent;-fx-text-fill:"
                + INK + ";-fx-font-size:12px;-fx-padding:6px 10px;";
    }

    private Label label(String text, String style) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family:'Segoe UI';" + style);
        return l;
    }

    private ImageView image(String path, double w, double h) {
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
        var r = getClass().getResource(path);
        return r == null ? null : new Image(r.toExternalForm());
    }
}
