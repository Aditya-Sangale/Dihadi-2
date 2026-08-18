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

/** Recruiter directory for verified ITI-qualified technicians. */
public class ITI_TechnicianResultPage {
    private static final String PAPER = "#fff8f0", INK = "#1e1b15", MUTED = "#4c4637", GOLD = "#735c00",
            BORDER = "#d0c5af";
    private static final String[] PHOTOS = {
            "/assets/images/worker/iti/skill-00.jpg", "/assets/images/worker/iti/skill-01.jpg",
            "/assets/images/worker/iti/skill-02.jpg",
            "/assets/images/worker/iti/skill-03.jpg", "/assets/images/worker/iti/skill-04.jpg",
            "/assets/images/worker/iti/skill-05.jpg",
            "/assets/images/worker/iti/skill-06.jpg", "/assets/images/worker/iti/skill-07.jpg",
            "/assets/images/worker/iti/skill-08.jpg" };
    private static final String[][] TECHNICIANS = {
            { "Amit Patel", "32 Years, Male", "Ahmedabad, Gujarat", "2,200", "Electrical Technician" },
            { "Kiran Kumar", "26 Years, Male", "Bengaluru, Karnataka", "2,800", "Fitter Technician" },
            { "Suresh Reddy", "35 Years, Male", "Hyderabad, Telangana", "3,000", "Mechanical Technician" },
            { "Vikram Singh", "29 Years, Male", "Jaipur, Rajasthan", "1,800", "Welder Technician" },
            { "Manoj Das", "31 Years, Male", "Kolkata, West Bengal", "2,100", "Maintenance Technician" },
            { "Ramesh Kumar", "42 Years, Male", "Chennai, Tamil Nadu", "2,600", "Electrical Technician" },
            { "Priya Sharma", "28 Years, Female", "Lucknow, Uttar Pradesh", "1,900", "Quality Technician" },
            { "Ganesh Iyer", "45 Years, Male", "Indore, Madhya Pradesh", "3,100", "Machine Operator" },
            { "Sunil Chhetri", "34 Years, Male", "Bhopal, Madhya Pradesh", "2,400", "Fitter Technician" } };

    public Scene getITITechnicianScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content());
        page.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content() {
        Label title = label("Looking for Skilled ITI / Technician",
                "-fx-font-family:'Georgia';-fx-font-size:37px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label subTitle = label("Connect with certified professionals for safe, precise, and reliable technical work.",
                "-fx-font-size:14px;-fx-text-fill:" + MUTED + ";");
        VBox heading = new VBox(6, title, subTitle);
        VBox body = new VBox(28, heading, hero(), filters(), resultHeader(), cards(), loadMore(), footer());
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
        ImageView heroImage = image("/assets/images/welder.jpeg", 660, 330);
        heroImage.setPreserveRatio(false);
        Label verified = label("✓  VERIFIED PROFESSIONALS",
                "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:.8px;-fx-text-fill:#231b00;-fx-background-color:#ffe085;-fx-background-radius:14px;-fx-padding:6px 11px;");
        StackPane visual = new StackPane(heroImage, verified);
        visual.setPrefSize(660, 330);
        StackPane.setAlignment(verified, Pos.BOTTOM_LEFT);
        StackPane.setMargin(verified, new Insets(0, 0, 18, 20));
        visual.setStyle(
                "-fx-background-radius:24px;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.11),14,0,0,3px);");
        Label quoteMark = label("“",
                "-fx-font-family:'Georgia';-fx-font-size:58px;-fx-text-fill:#d0c5af;-fx-opacity:.55;");
        Label quote = label(
                "Empowering industries with technical mastery. Hire verified, certified ITI technicians who bring precision, safety, and operational excellence to your most complex projects.",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:" + MUTED
                        + ";-fx-line-spacing:4px;");
        quote.setWrapText(true);
        quote.setMaxWidth(400);
        VBox copy = new VBox(-16, quoteMark, quote);
        copy.setPadding(new Insets(28, 32, 28, 34));
        copy.setPrefWidth(500);
        copy.setAlignment(Pos.CENTER_LEFT);
        copy.setStyle("-fx-background-color:#ffffff;-fx-background-radius:24px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1px 1px 1px 6px;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.07),11,0,0,2px);");
        HBox row = new HBox(26, visual, copy);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox filters() {
        Label caption = label("☰  Filters:", "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:" + MUTED + ";");
        ComboBox<String> country = combo("Country: India", "India"),
                state = combo("Select State", "Maharashtra", "Gujarat", "Karnataka", "Tamil Nadu"),
                city = combo("Select City", "Mumbai", "Pune", "Bengaluru", "Chennai");
        TextField pin = new TextField();
        pin.setPromptText("Pincode");
        pin.setPrefSize(130, 34);
        pin.setStyle(inputStyle());
        Button clear = new Button("Clear All");
        clear.setStyle("-fx-background-color:transparent;-fx-text-fill:" + GOLD
                + ";-fx-font-size:12px;-fx-font-weight:800;-fx-cursor:hand;");
        clear.setOnAction(e -> {
            country.setValue(null);
            state.setValue(null);
            city.setValue(null);
            pin.clear();
        });
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bar = new HBox(12, caption, country, state, city, pin, gap, clear);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15, 18, 15, 18));
        bar.setStyle("-fx-background-color:#e9e2d7;-fx-background-radius:13px;-fx-border-color:" + BORDER
                + ";-fx-border-radius:13px;");
        return bar;
    }

    private HBox resultHeader() {
        Label title = label("Available ITI Technicians",
                "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label count = label("9 verified profiles", "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox box = new HBox(title, gap, count);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (int i = 0; i < TECHNICIANS.length; i++)
            grid.getChildren().add(card(TECHNICIANS[i], PHOTOS[i]));
        return grid;
    }

    private VBox card(String[] technician, String photoPath) {
        Label watermark = label("DIHADI", "-fx-font-size:15px;-fx-font-weight:800;-fx-letter-spacing:2px;-fx-text-fill:"
                + GOLD + ";-fx-opacity:.10;");
        ImageView image = image(photoPath, 66, 66);
        image.setPreserveRatio(false);
        image.setClip(new Circle(33, 33, 33));
        StackPane avatar = new StackPane(image);
        avatar.setPrefSize(66, 66);
        avatar.setStyle(
                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = label(technician[0], "-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label age = label(technician[1], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Label trade = label(technician[4], "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + INK
                + ";-fx-background-color:#e9e2d7;-fx-background-radius:6px;-fx-border-color:#cfc6b2;-fx-border-radius:6px;-fx-padding:4px 7px;");
        Label location = label("⌖  " + technician[2], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        VBox details = new VBox(4, name, age, trade, location);
        HBox top = new HBox(14, avatar, details);
        top.setAlignment(Pos.TOP_LEFT);
        Region line = new Region();
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("-fx-background-color:#cfc6b2;");
        Label wage = label("Wage:  ₹" + technician[3] + " / day",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#b48700;");
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#ffffff;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 15px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + technician[0],
                "Your hiring request for " + technician[0] + " has been initiated. We will connect you shortly."));
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox bottom = new HBox(wage, space, hire);
        bottom.setAlignment(Pos.CENTER_LEFT);
        VBox content = new VBox(16, top, line, bottom);
        content.setPadding(new Insets(20));
        StackPane card = new StackPane(content, watermark);
        StackPane.setAlignment(watermark, Pos.TOP_RIGHT);
        StackPane.setMargin(watermark, new Insets(13, 16, 0, 0));
        card.setPrefSize(360, 196);
        card.setStyle(cardStyle(false));
        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
        return new VBox(card);
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:"
                + (active ? "#d4af37" : "#e9e2d7") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:15px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".13" : ".06") + ")," + (active ? "18" : "9") + ",0,0," + (active ? "4" : "2") + "px);";
    }

    private VBox loadMore() {
        Button b = new Button("Load More Profiles  ▾");
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:19px;-fx-border-color:#7e7665;-fx-border-radius:19px;-fx-text-fill:"
                        + GOLD + ";-fx-font-size:12px;-fx-font-weight:800;-fx-padding:10px 23px;-fx-cursor:hand;");
        b.setOnAction(e -> AppNavigator.information("ITI Technicians",
                "More verified technician profiles will be loaded here."));
        VBox box = new VBox(b);
        box.setAlignment(Pos.CENTER);
        return box;
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
        b.setPrefSize(170, 34);
        b.setStyle(inputStyle());
        return b;
    }

    private String inputStyle() {
        return "-fx-background-color:#eeeeee;-fx-background-radius:18px;-fx-border-color:transparent;-fx-text-fill:"
                + INK + ";-fx-font-size:12px;-fx-padding:5px 11px;";
    }

    private Label label(String text, String style) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family:'Segoe UI';" + style);
        return l;
    }

    private ImageView image(String path, double width, double height) {
        ImageView v = new ImageView(load(path));
        v.setFitWidth(width);
        v.setFitHeight(height);
        v.setSmooth(true);
        return v;
    }

    private Image load(String path) {
        var r = getClass().getResource(path);
        return r == null ? null : new Image(r.toExternalForm());
    }
}
