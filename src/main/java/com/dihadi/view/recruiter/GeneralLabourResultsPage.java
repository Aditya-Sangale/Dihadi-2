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

/** Recruiter search results for the General Labour category. */
public class GeneralLabourResultsPage {
    private static final String PAPER = "#fff8f0";
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
        page.setCenter(body());
        page.setStyle("-fx-background-color:" + PAPER + ";");
        return new Scene(page, 1400, 780);
    }

    private ScrollPane body() {
        Label title = label("Looking for Skilled General Labour",
                "-fx-font-family:'Georgia';-fx-font-size:35px;-fx-font-weight:800;-fx-text-fill:#574500;");
        VBox content = new VBox(31, title, hero(), filters(), results(), footer());
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
        ImageView image = image("/assets/images/generalLabour.jpeg", 440, 305);
        image.setPreserveRatio(false);
        StackPane picture = new StackPane(image);
        picture.setPrefSize(440, 305);
        picture.setStyle(
                "-fx-background-radius:12px;-fx-border-radius:12px;-fx-background-color:#ffffff;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),8,0,0,2px);");
        Label quote = label(
                "\"Behind every great project are the tireless hands\nthat build it. Hire verified, skilled, and dedicated\ngeneral labour ready to bring your vision to life.\"",
                "-fx-font-family:'Georgia';-fx-font-size:19px;-fx-font-style:italic;-fx-text-fill:#4c4637;-fx-line-spacing:3px;");
        VBox words = new VBox(quote);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPadding(new Insets(0, 0, 0, 22));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        HBox row = new HBox(50, picture, words);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox filters() {
        FlowPane fields = new FlowPane(16, 12,
                field("Select Country", combo("India")),
                field("Select State", combo("Maharashtra", "Gujarat", "Karnataka")),
                field("Select City", combo("Mumbai", "Pune", "Nagpur")), field("Select Pincode", pincode()));
        VBox box = new VBox(fields);
        box.setPadding(new Insets(17, 20, 17, 20));
        box.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),8,0,0,2px);");
        return box;
    }

    private TilePane results() {
        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(26);
        grid.setVgap(24);
        for (int i = 0; i < WORKERS.length; i++)
            grid.getChildren().add(workerCard(WORKERS[i], PHOTOS[i % PHOTOS.length]));
        return grid;
    }

    private VBox workerCard(String[] worker, String photoPath) {
        ImageView portrait = image(photoPath, 54, 54);
        portrait.setPreserveRatio(false);
        Circle clip = new Circle(27, 27, 27);
        portrait.setClip(clip);
        StackPane portraitBox = new StackPane(portrait);
        portraitBox.setPrefSize(54, 54);
        portraitBox.setStyle(
                "-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;");
        Label name = label(worker[0], "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:" + INK + ";");
        Label age = label(worker[1], "-fx-font-size:12px;-fx-text-fill:#4c4637;");
        HBox profile = new HBox(13, portraitBox, new VBox(3, name, age));
        profile.setAlignment(Pos.CENTER_LEFT);
        Label skill = label(worker[2],
                "-fx-font-size:10px;-fx-text-fill:#574500;-fx-border-color:#d4af37;-fx-border-radius:10px;-fx-padding:3px 7px;");
        Label location = label("⌾  " + worker[3], "-fx-font-size:12px;-fx-text-fill:#4c4637;");
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMinHeight(1);
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color:#d0c5af;");
        Label wageLabel = label("Wage", "-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#4c4637;");
        Label wage = label("₹" + worker[4], "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#d4a300;");
        Label day = label(" / day", "-fx-font-size:10px;-fx-text-fill:#4c4637;");
        HBox wageLine = new HBox(wage, day);
        wageLine.setAlignment(Pos.BASELINE_LEFT);
        Button hire = new Button("HIRE NOW");
        hire.setStyle(
                "-fx-background-color:#735c00;-fx-background-radius:18px;-fx-text-fill:#f6d676;-fx-font-size:10px;-fx-font-weight:800;-fx-padding:7px 14px;-fx-cursor:hand;");
        hire.setOnAction(e -> AppNavigator.information("Hire " + worker[0], "Your hiring request has been started."));
        VBox wageBox = new VBox(1, wageLabel, wageLine);
        HBox bottom = new HBox(wageBox, hire);
        bottom.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(wageBox, Priority.ALWAYS);
        VBox card = new VBox(12, profile, skill, location, divider, bottom);
        card.setPrefSize(360, 185);
        card.setPadding(new Insets(17));
        card.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),7,0,0,2px);");
        return card;
    }

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 25, 25);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#574500;");
        HBox identity = new HBox(10, logo, brand);
        identity.setAlignment(Pos.CENTER_LEFT);
        Button marketplace = nav("Marketplace", false), services = nav("Services", true),
                history = nav("History", false);
        HBox navigation = new HBox(31, marketplace, services, history);
        navigation.setAlignment(Pos.CENTER);
        Button bell = icon("♧"), account = icon("☻");
        HBox actions = new HBox(17, bell, account);
        actions.setAlignment(Pos.CENTER_RIGHT);
        BorderPane header = new BorderPane();
        header.setLeft(identity);
        header.setCenter(navigation);
        header.setRight(actions);
        header.setPadding(new Insets(10, 38, 10, 38));
        header.setStyle("-fx-background-color:" + PAPER
                + ";-fx-border-color:#eee7dc;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),6,0,0,1px);");
        return header;
    }

    private Button nav(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-font-size:11px;-fx-padding:8px 1px;-fx-border-width:0 0 2px 0;-fx-border-color:"
                        + (active ? "#d4af37" : "transparent") + ";-fx-text-fill:" + (active ? "#574500" : "#4c4637")
                        + ";" + (active ? "-fx-font-weight:700;" : "") + "-fx-cursor:hand;");
        button.setOnAction(e -> {
            if (text.equals("Marketplace"))
                AppNavigator.open((Stage) button.getScene().getWindow(), "Recruiter");
        });
        return button;
    }

    private Button icon(String value) {
        Button button = new Button(value);
        button.setStyle(
                "-fx-background-color:transparent;-fx-font-size:15px;-fx-text-fill:#574500;-fx-padding:5px;-fx-cursor:hand;");
        return button;
    }

    /** Shared DIHADI header, matching the Home, Worker, and recruiter pages. */
    private BorderPane standardHeader() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setPreserveRatio(true);
        Label title = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, title);
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox navigation = new HBox(12, standardNav("Home", false), standardNav("Business", false),
                standardNav("Worker", false),
                standardNav("Recruiter", true), standardNav("About Us", false), standardNav("Contact Us", false));
        navigation.setAlignment(Pos.CENTER);
        Button login = outlineButton("Login"), signUp = primaryButton("Sign Up");
        login.setOnAction(e -> AppNavigator.adminLoginInProgress());
        signUp.setOnAction(e -> AppNavigator.adminLoginInProgress());
        HBox accountActions = new HBox(10, login, signUp);
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
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-padding:8px 4px;-fx-cursor:hand;-fx-border-width:0 0 2px 0;-fx-text-fill:"
                        + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
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
    private VBox footer() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 58, 58);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#e9c349;-fx-letter-spacing:1px;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(300);
        VBox identity = new VBox(9, new HBox(12, logo, brand), promise);
        identity.setPrefWidth(340);
        HBox main = new HBox(58, identity, standardFooterColumn("Company", "About Dihadi", "Contact Us"),
                standardFooterColumn("Opportunities", "Find Work", "Worker Categories"),
                standardFooterColumn("Support", "Help Centre", "Privacy & Terms"));
        main.setAlignment(Pos.TOP_LEFT);
        Label copyright = label("© 2026 DIHADI  •  Mera Haq ~ Meri Dihadi. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;");
        VBox footer = new VBox(24, main, copyright);
        footer.setMaxWidth(1180);
        footer.setPadding(new Insets(32, 42, 24, 42));
        footer.setStyle(
                "-fx-background-color:#343027;-fx-background-radius:20px;-fx-border-color:rgba(208,197,175,.32);-fx-border-radius:20px;-fx-border-width:1px 0 0 0;");
        return footer;
    }

    private VBox standardFooterColumn(String heading, String... links) {
        VBox column = new VBox(8, label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"));
        column.setPrefWidth(150);
        for (String link : links) {
            Button button = new Button(link);
            button.setStyle(
                    "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.80;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-cursor:hand;");
            button.setOnAction(e -> AppNavigator.openFooterLink((Stage) button.getScene().getWindow(), link));
            column.getChildren().add(button);
        }
        return column;
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
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }
}
