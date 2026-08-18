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

/** A recruiter-facing directory for finding and hiring verified masons. */
public class MasonResultPage {
    private static final String PAPER = "#fff8f0";
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
            {"Ramesh Kumar", "42 Years, Male", "Jaipur, Rajasthan", "950", "Brick Mason"},
            {"Mahesh Patil", "35 Years, Male", "Mumbai, Maharashtra", "1,050", "Construction Mason"},
            {"Imran Khan", "31 Years, Male", "New Delhi, Delhi", "900", "Plaster Mason"},
            {"Sanjay Yadav", "46 Years, Male", "Lucknow, Uttar Pradesh", "880", "Brick Mason"},
            {"Dinesh Solanki", "38 Years, Male", "Ahmedabad, Gujarat", "1,000", "Stone Mason"},
            {"Kishan Lal", "50 Years, Male", "Raipur, Chhattisgarh", "920", "Tile Mason"},
            {"Raju Verma", "29 Years, Male", "Pune, Maharashtra", "860", "Construction Mason"},
            {"Babulal Meena", "44 Years, Male", "Kota, Rajasthan", "980", "Brick Mason"},
            {"Arvind Sharma", "34 Years, Male", "Indore, Madhya Pradesh", "930", "Plaster Mason"},
            {"Suresh Naik", "40 Years, Male", "Bengaluru, Karnataka", "1,100", "Stone Mason"},
            {"Manoj Tiwari", "36 Years, Male", "Varanasi, Uttar Pradesh", "900", "Tile Mason"},
            {"Gopal Das", "48 Years, Male", "Bhubaneswar, Odisha", "940", "Construction Mason"} };

    public Scene getMasonScene(Runnable back) {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(content());
        page.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane content() {
        Label title = label("Looking for Skilled Mason",
                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label count = label("12 verified masons available near you", "-fx-font-size:13px;-fx-text-fill:" + MUTED + ";");
        VBox heading = new VBox(5, title, count);
        VBox body = new VBox(28, heading, hero(), filterBar(), resultsHeader(), cards(), viewMore(), footer());
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
        ImageView photo = image("/assets/images/worker/mason/hero.jpg", 535, 300);
        photo.setPreserveRatio(false);
        StackPane photoBox = new StackPane(photo);
        photoBox.setPrefSize(535, 300);
        photoBox.setStyle("-fx-background-radius:14px;-fx-border-radius:14px;-fx-overflow:hidden;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),12,0,0,3px);");
        Label quote = label("\"Laying the foundation of tomorrow, one brick at a time.\nHire verified, skilled masons who bring strength, precision,\nand enduring craftsmanship to every construction project.\"",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:" + MUTED + ";-fx-line-spacing:4px;");
        Label proof = label("✓  VERIFIED CRAFTSMEN", "-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:" + GOLD + ";");
        VBox words = new VBox(22, quote, proof);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPadding(new Insets(24, 30, 24, 30));
        words.setPrefWidth(580);
        words.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1px 1px 1px 5px;-fx-border-radius:14px;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.07),10,0,0,2px);");
        HBox row = new HBox(26, photoBox, words);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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
        Label heading = label("Available Masons", "-fx-font-family:'Georgia';-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label note = label("Profiles are verified by DIHADI", "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(heading, spacer, note); header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private TilePane cards() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3); grid.setHgap(26); grid.setVgap(24);
        for (int index = 0; index < WORKERS.length; index++) grid.getChildren().add(card(WORKERS[index], PHOTOS[index]));
        return grid;
    }

    private VBox card(String[] worker, String photoPath) {
        ImageView portrait = image(photoPath, 56, 56); portrait.setPreserveRatio(false); portrait.setClip(new Circle(28, 28, 28));
        StackPane avatar = new StackPane(portrait); avatar.setPrefSize(56, 56);
        avatar.setStyle("-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = label(worker[0], "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        Label demographic = label(worker[1], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        HBox profile = new HBox(13, avatar, new VBox(3, name, demographic)); profile.setAlignment(Pos.CENTER_LEFT);
        Label verified = label("✓ Verified", "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#fff5cf;-fx-background-radius:10px;-fx-padding:4px 8px;");
        Label skill = label(worker[4], "-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";-fx-border-color:#d4af37;-fx-border-radius:10px;-fx-padding:3px 8px;");
        HBox tags = new HBox(7, skill, verified); tags.setAlignment(Pos.CENTER_LEFT);
        Label location = label("⌖  " + worker[2], "-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        Region divider = new Region(); divider.setMinHeight(1); divider.setPrefHeight(1); divider.setMaxWidth(Double.MAX_VALUE); divider.setStyle("-fx-background-color:" + BORDER + ";");
        Label wageCaption = label("DAILY WAGE", "-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:.6px;-fx-text-fill:" + MUTED + ";");
        Label wage = label("₹" + worker[3], "-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#b48700;");
        Label perDay = label(" / day", "-fx-font-size:10px;-fx-text-fill:" + MUTED + ";");
        HBox wageLine = new HBox(wage, perDay); wageLine.setAlignment(Pos.BASELINE_LEFT);
        VBox pay = new VBox(1, wageCaption, wageLine);
        Button hire = new Button("HIRE NOW");
        hire.setStyle("-fx-background-color:" + GOLD + ";-fx-background-radius:18px;-fx-text-fill:#f6d676;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:8px 15px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + worker[0], "Your hiring request for " + worker[0] + " has been initiated. We will connect you shortly."));
        HBox bottom = new HBox(pay, hire); bottom.setAlignment(Pos.CENTER_LEFT); HBox.setHgrow(pay, Priority.ALWAYS);
        VBox card = new VBox(12, profile, tags, location, divider, bottom);
        card.setPrefSize(360, 194); card.setPadding(new Insets(18));
        card.setStyle(cardStyle(false));
        card.setOnMouseEntered(e -> card.setStyle(cardStyle(true)));
        card.setOnMouseExited(e -> card.setStyle(cardStyle(false)));
        return card;
    }

    private String cardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:" + (active ? "#d4af37" : BORDER)
                + ";-fx-border-width:" + (active ? "2px" : "1px") + ";-fx-border-radius:13px;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39," + (active ? ".15" : ".07") + ")," + (active ? "15" : "8") + ",0,0," + (active ? "5" : "2") + "px);";
    }

    private VBox viewMore() {
        Button button = new Button("View More Masons");
        button.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:" + BORDER + ";-fx-border-radius:18px;-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";-fx-padding:9px 31px;-fx-cursor:hand;");
        button.setOnAction(e -> AppNavigator.information("Masons", "More verified mason profiles will be available here soon."));
        VBox box = new VBox(button); box.setAlignment(Pos.CENTER); return box;
    }

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54); logo.setPreserveRatio(true);
        Label title = label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, title); brand.setAlignment(Pos.CENTER_LEFT);
        HBox nav = new HBox(12, navButton("Home", false), navButton("Business", false), navButton("Worker", false), navButton("Recruiter", true), navButton("About Us", false), navButton("Contact Us", false)); nav.setAlignment(Pos.CENTER);
        Button login = outlineButton("Login"), signUp = primaryButton("Sign Up");
        login.setOnAction(e -> AppNavigator.adminLoginInProgress()); signUp.setOnAction(e -> AppNavigator.adminLoginInProgress());
        HBox account = new HBox(10, login, signUp); account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane bar = new BorderPane(); bar.setLeft(brand); bar.setCenter(nav); bar.setRight(account); bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private Button navButton(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:" + (active ? GOLD : "#4d4635") + ";-fx-border-color:" + (active ? GOLD : "transparent") + ";");
        button.setOnAction(e -> AppNavigator.open((Stage) button.getScene().getWindow(), text)); return button;
    }
    private Button primaryButton(String text) { Button b = new Button(text); b.setStyle("-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;"); return b; }
    private Button outlineButton(String text) { Button b = new Button(text); b.setStyle("-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:" + GOLD + ";-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;"); return b; }
    private VBox footer() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 58, 58); logo.setPreserveRatio(true);
        Label promise = label("Connecting skilled workers with verified opportunities, fair work, and a stronger future.", "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;"); promise.setWrapText(true); promise.setMaxWidth(300);
        VBox identity = new VBox(9, new HBox(12, logo, label("DIHADI", "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;-fx-letter-spacing:1px;")), promise); identity.setPrefWidth(340);
        HBox main = new HBox(58, identity, footerColumn("Company", "About Dihadi", "Contact Us"), footerColumn("Opportunities", "Find Work", "Worker Categories"), footerColumn("Support", "Help Centre", "Privacy & Terms")); main.setAlignment(Pos.TOP_LEFT);
        Label copyright = label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.", "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;");
        VBox footer = new VBox(24, main, copyright); footer.setMaxWidth(1180); footer.setPadding(new Insets(32, 42, 24, 42)); footer.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;-fx-border-color:rgba(208,197,175,.32);-fx-border-radius:20px;-fx-border-width:1px 0 0 0;"); return footer;
    }
    private VBox footerColumn(String heading, String... links) { VBox col = new VBox(8, label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;")); col.setPrefWidth(150); for (String link : links) { Button b = new Button(link); b.setStyle("-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.80;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-cursor:hand;"); b.setOnAction(e -> AppNavigator.openFooterLink((Stage) b.getScene().getWindow(), link)); col.getChildren().add(b); } return col; }
    private ComboBox<String> combo(String prompt, String... values) { ComboBox<String> box = new ComboBox<>(); box.setPromptText(prompt); box.getItems().addAll(values); box.setPrefWidth(184); box.setPrefHeight(35); box.setStyle(inputStyle()); return box; }
    private TextField pincode() { TextField field = new TextField(); field.setPromptText("Select Pincode"); field.setPrefWidth(175); field.setPrefHeight(35); field.setStyle(inputStyle()); return field; }
    private String inputStyle() { return "-fx-background-color:#ffffff;-fx-background-radius:7px;-fx-border-color:#cfc6b2;-fx-border-radius:7px;-fx-text-fill:" + INK + ";-fx-font-size:12px;-fx-padding:5px 10px;"; }
    private Label label(String text, String style) { Label label = new Label(text); label.setStyle("-fx-font-family:'Segoe UI';" + style); return label; }
    private ImageView image(String path, double width, double height) { ImageView view = new ImageView(load(path)); view.setFitWidth(width); view.setFitHeight(height); view.setSmooth(true); return view; }
    private Image load(String path) { var resource = getClass().getResource(path); return resource == null ? null : new Image(resource.toExternalForm()); }
}
