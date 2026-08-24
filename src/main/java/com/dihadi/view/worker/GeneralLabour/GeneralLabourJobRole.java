package com.dihadi.view.worker.GeneralLabour;

import com.dihadi.view.AppNavigator;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/** General Labour job marketplace opened by Save & Continue. */
public class GeneralLabourJobRole {
    private static final String[][] JOBS = { { "Construction General Labour", "Pune, Maharashtra", "₹700", "01" },
            { "Material Shifting Helper", "Bhiwandi, Maharashtra", "₹750", "02" },
            { "Factory Worker Helper", "Nashik, Maharashtra", "₹800", "03" },
            { "Road Construction Labour", "Chennai, Tamil Nadu", "₹850", "06" },
            { "Loading & Unloading Helper", "New Delhi, Delhi", "₹750", "10" },
            { "Concrete Mixer Labour", "Bangalore South, Karnataka", "₹900", "08" },
            { "Shuttering Helper", "Gurgaon, Haryana", "₹950", "15" },
            { "Mason Helper", "Mumbai, Maharashtra", "₹850", "09" } };

    public Scene getGeneralLabourJobRoleScene(Runnable back) {
        Label eye = label("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                title = label("General Labour Job Roles",
                        "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                intro = label("Explore verified daily-wage opportunities and apply directly through DIHADI.",
                        "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        VBox hero = new VBox(12, eye, title, intro);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(32, 36, 30, 36));
        hero.setMaxWidth(1140);
        hero.setStyle(cardStyle());
        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1100);
        for (String[] j : JOBS)
            grid.getChildren().add(card(j));
        VBox content = new VBox(28, hero, suitableJobBox(),
                label("Available opportunities",
                        "-fx-font-family:'Georgia';-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                grid);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 36, 42, 36));
        content.setMaxWidth(1200);
        StackPane canvas = new StackPane(content);
        canvas.setAlignment(Pos.TOP_CENTER);
        canvas.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        Button previous = outline("← Back to skills");
        previous.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bottom = new HBox(previous, gap,
                label("Choose an opportunity to start your next job.", "-fx-font-size:13px;-fx-text-fill:#4d4635;"));
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 60, 16, 60));
        bottom.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(createHeader(back));
        page.setBottom(bottom);
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    /** Standard DIHADI desktop header used by the main application pages. */
    private BorderPane createHeader(Runnable back) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        Label brandName = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, brandName); brand.setAlignment(Pos.CENTER_LEFT);

        Button home = headerNav("Home", false);
        Button business = headerNav("Business", false);
        Button worker = headerNav("Worker", true);
        Button recruiter = headerNav("Recruiter", false);
        Button about = headerNav("About Us", false);
        Button contact = headerNav("Contact Us", false);
        home.setOnAction(e -> AppNavigator.open(stageOf(home), "Home"));
        business.setOnAction(e -> AppNavigator.open(stageOf(business), "Business"));
        worker.setOnAction(e -> { if (back != null) back.run(); });
        recruiter.setOnAction(e -> AppNavigator.open(stageOf(recruiter), "Recruiter"));
        about.setOnAction(e -> AppNavigator.open(stageOf(about), "About Us"));
        contact.setOnAction(e -> AppNavigator.open(stageOf(contact), "Contact Us"));
        HBox navigation = new HBox(12, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);

        Button login = headerOutline("Login"); login.setOnAction(e -> AppNavigator.adminLoginInProgress());
        Button signUp = headerPrimary("Sign Up");
        signUp.setOnAction(e -> AppNavigator.signUp(stageOf(signUp), back));
        HBox account = new HBox(10, login, signUp); account.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setLeft(brand); header.setCenter(navigation); header.setRight(account);
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    /**
     * Filter panel from the supplied Job Roles reference, styled to match the other
     * JobRole pages.
     */
    private VBox suitableJobBox() {
        Label heading = label("Find a suitable job role for you",
                "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        ComboBox<String> state = choice("Select state", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi", "Haryana"),
                city = choice("Select city", "Pune", "Bhiwandi", "Nashik", "Bangalore South", "New Delhi"),
                skill = choice("Select labour skill", "Construction", "Material shifting", "Factory work", "Road work",
                        "Loading work");
        Button clear = outline("Clear filters"), find = primary("Find roles");
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
        });
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);
        VBox box = new VBox(14, heading, controls);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(22, 24, 22, 24));
        box.setMaxWidth(1140);
        box.setStyle(
                "-fx-background-color:#faf3e8;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
        return box;
    }

    private VBox card(String[] j) {
        ImageView pic = image("/assets/images/general-labour/skill-" + j[3] + ".jpg", 316, 178);
        Label name = label(j[0], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                loc = label("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wage = label("Daily wage  " + j[2], "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        loc.setAlignment(Pos.CENTER);
        loc.setMaxWidth(Double.MAX_VALUE);
        Button apply = primary("Apply now");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(e -> {
            apply.setText("Applied ✓");
            apply.setDisable(true);
        });
        VBox box = new VBox(13, pic, name, loc, wage, apply);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(344, 350);
        box.setPadding(new Insets(14));
        box.setStyle(cardStyle());
        return box;
    }

    private ComboBox<String> choice(String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.getSelectionModel().selectFirst();
        box.setPrefWidth(190);
        box.setStyle(
                "-fx-background-color:#fff8f0;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-background-radius:18px;"
                        + "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:14px;-fx-text-fill:#3a3027;-fx-padding:3px 8px;");
        return box;
    }

    private ImageView image(String p, double w, double h) {
        var r = getClass().getResource(p);
        ImageView v = new ImageView(r == null ? null : new Image(r.toExternalForm()));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        Rectangle c = new Rectangle(w, h);
        c.setArcWidth(24);
        c.setArcHeight(24);
        v.setClip(c);
        return v;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return l;
    }

    private String cardStyle() {
        return "-fx-background-color:#fff8f0;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    }

    private Button headerNav(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-padding:8px 4px;"
                + "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;"
                + "-fx-text-fill:" + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                + (active ? "#735c00" : "transparent") + ";-fx-border-width:0 0 2px 0;");
        return button;
    }

    private Button headerPrimary(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#3a3027;"
                + "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:14px;-fx-font-weight:700;"
                + "-fx-padding:10px 20px;-fx-cursor:hand;");
        return button;
    }

    private Button headerOutline(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:#fbf3e5;-fx-background-radius:999px;-fx-border-color:#735c00;"
                + "-fx-border-radius:999px;-fx-text-fill:#735c00;-fx-font-family:'Segoe UI',sans-serif;"
                + "-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return button;
    }

    private Stage stageOf(Button button) { return (Stage) button.getScene().getWindow(); }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#343027;-fx-font-size:13px;"
                        + "-fx-font-weight:700;-fx-padding:11px 24px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;"
                        + "-fx-text-fill:#343027;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return b;
    }
}
