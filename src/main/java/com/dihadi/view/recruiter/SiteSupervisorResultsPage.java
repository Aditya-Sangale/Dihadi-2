package com.dihadi.view.recruiter;

import com.dihadi.view.AppNavigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/** Recruiter marketplace for verified site supervisors. */
public class SiteSupervisorResultsPage {
    private static final String[] PHOTOS = { "/assets/images/worker/foreman/skill-00.jpg",
            "/assets/images/worker/foreman/skill-01.jpg", "/assets/images/worker/foreman/skill-02.jpg",
            "/assets/images/worker/foreman/skill-03.jpg", "/assets/images/worker/foreman/skill-04.jpg",
            "/assets/images/worker/foreman/skill-05.jpg" };
    private static final String[][] PEOPLE = {
            { "Arjun Patil", "32 Years | Male", "Site Supervisor", "Pune", "1500" },
            { "Kavita More", "28 Years | Female", "Safety Officer", "Mumbai", "1750" },
            { "Sanjay Jadhav", "42 Years | Male", "Construction Foreman", "Bangalore", "2100" },
            { "Rohit Sharma", "38 Years | Male", "Site Supervisor", "Delhi", "1850" },
            { "Priya Nair", "35 Years | Female", "Quality Supervisor", "Kochi", "1900" },
            { "Vijay Kumar", "46 Years | Male", "Senior Supervisor", "Chennai", "2200" },
            { "Amit Joshi", "29 Years | Male", "Site Coordinator", "Nagpur", "1450" },
            { "Meena Singh", "40 Years | Female", "Site Manager", "Pune", "2150" },
            { "Rahul Verma", "33 Years | Male", "Site Supervisor", "Lucknow", "1700" },
            { "Siddharth Rao", "41 Years | Male", "Senior Supervisor", "Hyderabad", "2350" },
            { "Anjali Patel", "30 Years | Female", "Site Supervisor", "Ahmedabad", "1550" },
            { "Manoj Yadav", "45 Years | Male", "Construction Foreman", "Jaipur", "2050" } };

    public Scene getSiteSupervisorScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content(back));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content(Runnable back) {
        Label heading = label("Looking for Skilled Supervisors",
                "-fx-font-family:'Georgia';-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        VBox body = new VBox(28, heading, hero(), filters(), grid(), bottomActions(back), footer());
        body.setMaxWidth(1190);
        body.setPadding(new Insets(35, 0, 45, 0));
        StackPane canvas = new StackPane(body);
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
        ImageView image = image("/assets/images/sitesuperviser.jpeg", 500, 290);
        image.setPreserveRatio(false);
        StackPane photo = new StackPane(image);
        photo.setPrefSize(500, 290);
        photo.setStyle("-fx-background-radius:15px;-fx-border-color:#d0c5af;-fx-border-radius:15px;");
        Label quote = label(
                "\"Strong leadership keeps every project on track. Hire verified supervisors who bring safety, clarity, and confidence to your site.\"",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#4c4637;-fx-line-spacing:3px;");
        quote.setWrapText(true);
        quote.setMaxWidth(475);
        VBox words = new VBox(quote);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPadding(new Insets(30));
        words.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;-fx-border-radius:15px;");
        HBox row = new HBox(48, photo, words);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox filters() {
        Label title = label("☷  Filter Supervisors", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;");
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox box = new HBox(10, title, combo("Select Country"), combo("Select State"), combo("Select City"), pincode());
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(16));
        box.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:#cfc6b2;-fx-border-radius:15px;");
        return box;
    }

    private TilePane grid() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(24);
        grid.setVgap(24);
        for (int i = 0; i < PEOPLE.length; i++)
            grid.getChildren().add(card(PEOPLE[i], PHOTOS[i % PHOTOS.length]));
        return grid;
    }

    private VBox card(String[] p, String imagePath) {
        ImageView avatar = image(imagePath, 72, 72);
        avatar.setPreserveRatio(false);
        avatar.setClip(new Circle(36, 36, 36));
        StackPane picture = new StackPane(avatar);
        picture.setPrefSize(72, 72);
        picture.setStyle("-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;");
        VBox identity = new VBox(4, label(p[0], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                label(p[1], "-fx-font-size:13px;-fx-text-fill:#4c4637;"));
        HBox top = new HBox(14, picture, identity);
        top.setAlignment(Pos.CENTER_LEFT);
        Label role = label("▣  " + p[2],
                "-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-padding:7px 10px;");
        Label location = label("⌾  " + p[3], "-fx-font-size:13px;-fx-text-fill:#4c4637;");
        Region line = new Region();
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("-fx-background-color:#e9e2d7;");
        Label wage = label("₹" + p[4], "-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#d4a300;");
        VBox pay = new VBox(2, label("Wage:", "-fx-font-size:12px;-fx-text-fill:#4c4637;"),
                new HBox(wage, label(" / day", "-fx-font-size:11px;-fx-text-fill:#4c4637;")));
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#231b00;-fx-font-size:11px;-fx-font-weight:800;-fx-padding:9px 17px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + p[0], "Your hiring request has been started."));
        HBox bottom = new HBox(pay, hire);
        bottom.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pay, Priority.ALWAYS);
        VBox card = new VBox(15, top, role, location, line, bottom);
        card.setPrefSize(365, 255);
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:15px;-fx-border-color:#cfc6b2;-fx-border-radius:15px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),8,0,0,2px);");
        return card;
    }

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setPreserveRatio(true);
        HBox brand = new HBox(10, logo,
                label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
        nav.setAlignment(Pos.CENTER);
        Button login = outline("Login"), signup = primary("Sign Up");
        login.setOnAction(e -> AppNavigator.adminLoginInProgress());
        signup.setOnAction(e -> AppNavigator.adminLoginInProgress());
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(nav);
        bar.setRight(new HBox(10, login, signup));
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return bar;
    }

    private Button nav(String t, boolean active) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:8px 4px;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                        + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                        + ";-fx-cursor:hand;");
        b.setOnAction(e -> AppNavigator.open((Stage) b.getScene().getWindow(), t));
        return b;
    }

    private HBox bottomActions(Runnable backAction) {
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color:transparent;-fx-font-size:14px;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-cursor:hand;");
        if (backAction != null) {
            back.setOnAction(e -> backAction.run());
        }
        HBox row = new HBox(back);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox footer() {
        VBox identity = new VBox(10,
                label("DIHADI",
                        "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                label("Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                        "-fx-font-size:13px;-fx-text-fill:#f8f0e2;"));
        identity.setPrefWidth(340);
        HBox main = new HBox(58, identity, footCol("Company", "About Dihadi", "Contact Us"),
                footCol("Opportunities", "Find Work", "Worker Categories"),
                footCol("Support", "Help Centre", "Privacy & Terms"));
        VBox footer = new VBox(24, main, label("© 2026 DIHADI • Mera Haq ~ Meri Dihadi. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;"));
        footer.setPadding(new Insets(32, 42, 24, 42));
        footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return footer;
    }

    private VBox footCol(String h, String... links) {
        VBox col = new VBox(8, label(h, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"));
        col.setPrefWidth(150);
        for (String link : links) {
            Button b = new Button(link);
            b.setStyle(
                    "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-font-size:13px;-fx-cursor:hand;");
            b.setOnAction(e -> AppNavigator.openFooterLink((Stage) b.getScene().getWindow(), link));
            col.getChildren().add(b);
        }
        return col;
    }

    private ComboBox<String> combo(String prompt) {
        ComboBox<String> b = new ComboBox<>();
        b.setPromptText(prompt);
        b.setPrefWidth(160);
        b.setPrefHeight(34);
        b.setStyle(fieldStyle());
        return b;
    }

    private TextField pincode() {
        TextField f = new TextField();
        f.setPromptText("Pincode");
        f.setPrefWidth(130);
        f.setPrefHeight(34);
        f.setStyle(fieldStyle());
        return f;
    }

    private String fieldStyle() {
        return "-fx-background-color:#f4ede2;-fx-background-radius:7px;-fx-border-color:transparent;-fx-font-size:12px;";
    }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-weight:700;-fx-padding:10px 20px;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-padding:9px 18px;");
        return b;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI';" + s);
        return l;
    }

    private ImageView image(String path, double w, double h) {
        ImageView v = new ImageView(load(path));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setSmooth(true);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }
}