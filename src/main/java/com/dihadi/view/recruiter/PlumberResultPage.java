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

/** Recruiter marketplace screen for hiring verified plumbers. */
public class PlumberResultPage {
    private static final String PAPER = "#fff8f0", INK = "#1e1b15", MUTED = "#4c4637", GOLD = "#735c00",
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
        page.setCenter(content());
        page.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content() {
        Label title = l("Looking for Skilled Plumbers",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label sub = l("Hire dependable plumbing specialists for clean, reliable, and lasting infrastructure.",
                "-fx-font-size:14px;-fx-text-fill:" + MUTED + ";");
        VBox body = new VBox(30, new VBox(6, title, sub), hero(), filters(), resultTitle(), cards(), more(), footer());
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
        ImageView image = img("/assets/images/plumber.jpeg", 555, 335);
        image.setPreserveRatio(false);
        StackPane photo = new StackPane(image);
        photo.setPrefSize(555, 335);
        photo.setStyle(
                "-fx-background-radius:14px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.13),15,0,0,3px);");
        Label quote = l(
                "\"Ensuring flawless flow and lasting infrastructure. Hire verified, skilled plumbers who bring expertise, reliability, and precision to your plumbing and sanitation projects.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:" + MUTED
                        + ";-fx-line-spacing:4px;");
        quote.setWrapText(true);
        quote.setMaxWidth(490);
        VBox copy = new VBox(quote);
        copy.setAlignment(Pos.CENTER_LEFT);
        copy.setPadding(new Insets(22, 20, 22, 27));
        copy.setPrefWidth(565);
        copy.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 7px;");
        HBox row = new HBox(44, photo, copy);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (int i = 0; i < PLUMBERS.length; i++)
            grid.getChildren().add(card(PLUMBERS[i], PHOTOS[i]));
        return grid;
    }

    private VBox card(String[] p, String path) {
        Label watermark = l("DIHADI", "-fx-font-size:15px;-fx-font-weight:800;-fx-letter-spacing:2px;-fx-text-fill:"
                + GOLD + ";-fx-opacity:.10;");
        ImageView portrait = img(path, 76, 76);
        portrait.setPreserveRatio(false);
        portrait.setClip(new Circle(38, 38, 38));
        StackPane avatar = new StackPane(portrait);
        avatar.setPrefSize(76, 76);
        avatar.setStyle(
                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = l(p[0] + "  ✓", "-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label age = l(p[1], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        HBox profile = new HBox(14, avatar, new VBox(5, name, age));
        profile.setAlignment(Pos.CENTER_LEFT);
        Label skill = l(p[4],
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-border-color:#d4af37;-fx-border-radius:12px;-fx-padding:4px 9px;");
        Label location = l("⌖  " + p[2], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        HBox info = new HBox(12, skill, location);
        info.setAlignment(Pos.CENTER_LEFT);
        Region line = new Region();
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("-fx-background-color:#cfc6b2;");
        Label wage = l("₹" + p[3], "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#d4af37;");
        Label per = l(" / day", "-fx-font-size:11px;-fx-text-fill:" + MUTED + ";");
        HBox pay = new HBox(wage, per);
        pay.setAlignment(Pos.BASELINE_LEFT);
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#ffffff;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 16px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + p[0],
                "Your hiring request for " + p[0] + " has been initiated. We will connect you shortly."));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bottom = new HBox(pay, gap, hire);
        bottom.setAlignment(Pos.CENTER_LEFT);
        VBox content = new VBox(15, profile, info, line, bottom);
        content.setPadding(new Insets(20));
        StackPane root = new StackPane(content, watermark);
        StackPane.setAlignment(watermark, Pos.TOP_RIGHT);
        StackPane.setMargin(watermark, new Insets(13, 17, 0, 0));
        root.setPrefSize(360, 210);
        root.setStyle(cardStyle(false));
        root.setOnMouseEntered(e -> root.setStyle(cardStyle(true)));
        root.setOnMouseExited(e -> root.setStyle(cardStyle(false)));
        return new VBox(root);
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:15px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".13" : ".08") + ")," + (active ? "18" : "11") + ",0,0," + (active ? "4" : "2") + "px);";
    }

    private VBox more() {
        Button b = new Button("View More Plumbers");
        b.setStyle("-fx-background-color:#ffffff;-fx-background-radius:19px;-fx-border-color:" + BORDER
                + ";-fx-border-radius:19px;-fx-text-fill:" + GOLD
                + ";-fx-font-size:12px;-fx-font-weight:800;-fx-padding:10px 26px;-fx-cursor:hand;");
        b.setOnAction(e -> AppNavigator.information("Plumbers", "More verified plumber profiles will be loaded here."));
        VBox box = new VBox(b);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private BorderPane header() {
        ImageView logo = img("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setPreserveRatio(true);
        HBox brand = new HBox(10, logo, l("DIHADI",
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
        ImageView logo = img("/assets/logo/dihadi logo.jpeg", 58, 58);
        logo.setPreserveRatio(true);
        Label promise = l("Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(300);
        VBox identity = new VBox(9,
                new HBox(12, logo, l("DIHADI",
                        "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;-fx-letter-spacing:1px;")),
                promise);
        identity.setPrefWidth(340);
        HBox main = new HBox(58, identity, footerCol("Company", "About Dihadi", "Contact Us"),
                footerCol("Opportunities", "Find Work", "Worker Categories"),
                footerCol("Support", "Help Centre", "Privacy & Terms"));
        main.setAlignment(Pos.TOP_LEFT);
        VBox foot = new VBox(24, main, l("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        foot.setMaxWidth(1180);
        foot.setPadding(new Insets(32, 42, 24, 42));
        foot.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return foot;
    }

    private VBox footerCol(String title, String... links) {
        VBox col = new VBox(8, l(title, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"));
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
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }
}
