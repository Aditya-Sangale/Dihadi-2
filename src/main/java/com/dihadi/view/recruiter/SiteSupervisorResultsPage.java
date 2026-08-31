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
        page.setStyle("-fx-background-color:#f3e7ce;");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(root, 1400, 780);
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
        canvas.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        return scroll;
    }

    private HBox hero() {
        ImageView image = image("/assets/images/sitesuperviser.jpeg", 410, 275);
        image.setPreserveRatio(false);
        StackPane photo = new StackPane(image);
        photo.setPrefSize(410, 275);
        photo.setStyle("-fx-background-radius:12px;-fx-border-radius:12px;");
        Label quote = label(
                "\"Strong leadership keeps every project on track.\nHire verified supervisors who bring safety, clarity,\nand confidence to your site.\"",
                "-fx-font-family:'Georgia';-fx-font-size:18px;-fx-text-fill:#1e1b15;-fx-line-spacing:2px;");
        HBox box = new HBox(78, photo, quote);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(24));
        box.setStyle(
                "-fx-background-color:#f4ede2;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;");
        return box;
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

    private static class WorkerCardData {
        String name;
        String demo;
        String role;
        String location;
        String wage;
        String photo;

        WorkerCardData(String name, String demo, String role, String location, String wage, String photo) {
            this.name = name;
            this.demo = demo;
            this.role = role;
            this.location = location;
            this.wage = wage;
            this.photo = photo;
        }
    }

    private java.util.List<WorkerCardData> getAllSupervisorWorkers() {
        java.util.List<WorkerCardData> list = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.Worker> realWorkers = new com.dihadi.controller.WorkerController().getAllWorkers();
            if (realWorkers != null) {
                int pIdx = 0;
                for (com.dihadi.model.Worker w : realWorkers) {
                    if (w.getWorkerType() != null && (w.getWorkerType().toLowerCase().contains("supervisor") || w.getWorkerType().toLowerCase().contains("foreman") || w.getWorkerType().toLowerCase().contains("site"))) {
                        String fullName = ((w.getFirstName() != null ? w.getFirstName() : "") + " " +
                                          (w.getLastName() != null ? w.getLastName() : "")).trim();
                        if (fullName.isBlank()) fullName = "Verified Supervisor";
                        String demo = (w.getExperience() != null && !w.getExperience().equals("Select") ? w.getExperience() : "Experienced")
                                      + " | " + (w.getGender() != null && !w.getGender().equals("Select") ? w.getGender() : "Male");
                        String skillTag = w.getSubSkill() != null && !w.getSubSkill().isBlank() ? w.getSubSkill() : "Site Supervisor";
                        String loc = (w.getCity() != null && !w.getCity().isBlank() ? w.getCity() : "Pune");
                        String wage = w.getDailyWage() > 0 ? String.format("%,d", (long)w.getDailyWage()) : "1500";
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
        for (int i = 0; i < PEOPLE.length; i++) {
            list.add(new WorkerCardData(PEOPLE[i][0], PEOPLE[i][1], PEOPLE[i][2], PEOPLE[i][3], PEOPLE[i][4], PHOTOS[i % PHOTOS.length]));
        }
        return list;
    }

    private TilePane grid() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(24);
        grid.setVgap(24);
        for (WorkerCardData w : getAllSupervisorWorkers())
            grid.getChildren().add(card(w));
        return grid;
    }

    private VBox card(WorkerCardData p) {
        ImageView portrait = image(p.photo, 64, 64);
        portrait.setPreserveRatio(false);
        portrait.setClip(new Circle(32, 32, 32));
        StackPane avatar = new StackPane(portrait);
        avatar.setPrefSize(64, 64);
        avatar.setStyle(
                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = label(p.name, "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label age = label(p.demo, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
        Label skill = label(p.role,
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#b48700;-fx-background-color:#f4ede2;-fx-background-radius:5px;-fx-padding:4px 7px;");
        Label location = label("⌖  " + p.location, "-fx-font-size:12px;-fx-text-fill:#4c4637;");
        VBox details = new VBox(4, name, age, skill, location);
        HBox top = new HBox(14, avatar, details);
        top.setAlignment(Pos.TOP_LEFT);
        Region divider = new Region();
        divider.setMinHeight(1);
        divider.setPrefHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#e9e2d7;");
        Label wage = label("Wage:  ₹" + p.wage + " / day",
                "-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:#d4a300;");
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-radius:18px;-fx-text-fill:#b48700;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + p.name,
                "Your hiring request for " + p.name + " has been initiated. We will connect you shortly."));
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
        card.setOnMouseClicked(e -> { javafx.stage.Stage stage = (javafx.stage.Stage) card.getScene().getWindow(); javafx.scene.Scene currentScene = card.getScene(); stage.setScene(new RecruiterWorkerProfilePage(p.name, "Site Supervisor", p.demo, p.location, p.wage, p.photo).getProfileScene(() -> stage.setScene(currentScene), currentScene)); });
        return card;
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo,
                label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", false),
                nav("Recruiter", true), nav("About Us", false), nav("Contact Us", false));
        nav.setAlignment(Pos.CENTER);
        Button admin = AppNavigator.createHeaderActionButton();
        HBox account = new HBox(10, admin);
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(nav);
        bar.setRight(new HBox(10, admin));
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private Button nav(String t, boolean active) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:8px 4px;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                        + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                        + ";-fx-cursor:hand;");
        b.setOnAction(e -> AppNavigator.open((Stage) b.getScene().getWindow(), t));
        return b;
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
        if (p == null || p.isBlank()) return null;
        if (p.startsWith("http://") || p.startsWith("https://") || p.startsWith("file:")) {
            return new Image(p, true);
        }
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }
}
