package com.dihadi.view.worker.Electrician;

import com.dihadi.view.AppNavigator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Electrician opportunity marketplace, matching the Carpenter job-role flow.
 */
public class ElectricianJobRole {
    private static final String[] HERO_IMAGES = { "/assets/images/worker/electrician/skill-01.jpg",
            "/assets/images/worker/electrician/skill-02.jpg", "/assets/images/worker/electrician/skill-04.jpg",
            "/assets/images/worker/electrician/skill-06.jpg" };
    private static final Job[] JOBS = {
            new Job("Residential Electrician", "Pune, Maharashtra", "₹1,000", 1),
            new Job("Industrial Wireman", "Nashik, Maharashtra", "₹1,250", 2),
            new Job("Electrical Helper", "Bangalore South, Karnataka", "₹800", 3),
            new Job("Cable Jointer", "Chennai, Tamil Nadu", "₹1,400", 4),
            new Job("Lineman", "Gurgaon, Haryana", "₹1,350", 5),
            new Job("HVAC Technician", "New Delhi, Delhi", "₹1,500", 6),
            new Job("Substation Attendant", "Mumbai, Maharashtra", "₹1,300", 7),
            new Job("Fire Safety Technician", "Hyderabad, Telangana", "₹1,450", 8),
            new Job("Power Testing Technician", "Bhiwandi, Maharashtra", "₹1,200", 9) };
    private final FlowPane jobs = new FlowPane(24, 24);
    private ImageView heroImage;
    private Timeline slider;
    private int heroIndex;

    public Scene getElectricianJobRoleScene(Runnable back) {
        VBox content = new VBox(28, hero(), filter(), jobSection());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 36, 42, 36));
        content.setMaxWidth(1280);
        StackPane canvas = new StackPane(content);
        canvas.setAlignment(Pos.TOP_CENTER);
        canvas.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(header());
        page.setBottom(actionBar(back));
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private VBox hero() {
        Label eyebrow = label("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;");
        Label title = label("Electrician Job Roles",
                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        heroImage = image(HERO_IMAGES[0], 640, 360);
        clip(heroImage, 640, 360, 16);
        StackPane visual = new StackPane(heroImage);
        visual.setPrefSize(640, 360);
        visual.setStyle(
                "-fx-background-color:#faf3e8;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),20,0,0,6px);");
        startSlider();
        Label quote = label(
                "“Bringing light to the dark and power to the future. Every safe connection begins with a skilled electrician.”",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setMaxWidth(385);
        quote.setAlignment(Pos.CENTER);
        VBox words = new VBox(18, eyebrow, title, quote);
        words.setAlignment(Pos.CENTER);
        words.setPrefWidth(410);
        words.setPadding(new Insets(18, 0, 18, 26));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        HBox row = new HBox(34, visual, words);
        row.setAlignment(Pos.CENTER);
        VBox box = new VBox(row);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28, 30, 28, 30));
        box.setMaxWidth(1200);
        box.setStyle(cardStyle("#fff8f0"));
        return box;
    }

    private VBox filter() {
        Label title = label("Find a suitable job role for you",
                "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        ComboBox<String> state = combo("All states", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi", "Haryana"),
                city = combo("All cities", "Pune", "Nashik", "Bangalore South", "Chennai", "New Delhi"),
                skill = combo("All electrician skills", "Electrician", "Wireman", "Cable Jointer", "Lineman", "HVAC");
        Button clear = outline("Clear filters"), search = primary("Find roles");
        search.setOnAction(e -> renderJobs(state.getValue(), city.getValue(), skill.getValue()));
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
            renderJobs(null, null, null);
        });
        HBox controls = new HBox(12, state, city, skill, clear, search);
        controls.setAlignment(Pos.CENTER);
        VBox box = new VBox(14, title, controls);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(22, 24, 22, 24));
        box.setMaxWidth(1200);
        box.setStyle(cardStyle("#faf3e8"));
        return box;
    }

    private VBox jobSection() {
        Label heading = label("Available opportunities",
                "-fx-font-family:'Georgia';-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                sub = label("Explore verified electrician opportunities and apply directly through DIHADI.",
                        "-fx-font-size:15px;-fx-text-fill:#4d4635;");
        jobs.setAlignment(Pos.CENTER);
        jobs.setPrefWrapLength(1160);
        renderJobs(null, null, null);
        VBox section = new VBox(8, heading, sub, jobs);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(1200);
        return section;
    }

    private void renderJobs(String state, String city, String skill) {
        jobs.getChildren().clear();
        for (Job job : JOBS) {
            String all = (job.title + " " + job.location).toLowerCase();
            boolean a = state == null || state.startsWith("All") || all.contains(state.toLowerCase()),
                    b = city == null || city.startsWith("All") || all.contains(city.toLowerCase()),
                    c = skill == null || skill.startsWith("All") || all.contains(skill.toLowerCase());
            if (a && b && c)
                jobs.getChildren().add(jobCard(job));
        }
        if (jobs.getChildren().isEmpty())
            jobs.getChildren().add(label("No exact role found. Clear filters to view all electrician roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
    }

    private VBox jobCard(Job job) {
        ImageView photo = image(String.format("/assets/images/worker/electrician/skill-%02d.jpg", job.image), 316, 178);
        clip(photo, 316, 178, 12);
        Label name = label(job.title, "-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                location = label("⌖  " + job.location, "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wageLabel = label("Daily wage", "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wage = label(job.wage, "-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        location.setAlignment(Pos.CENTER);
        location.setMaxWidth(Double.MAX_VALUE);
        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);
        Button apply = primary("Apply now");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(e -> {
            apply.setText("Applied ✓");
            apply.setDisable(true);
        });
        HBox pay = new HBox(wageLabel, wage);
        pay.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(wageLabel, Priority.ALWAYS);
        VBox card = new VBox(14, photo, name, location, space, pay, apply);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(344, 380);
        card.setPadding(new Insets(14));
        card.setStyle(cardStyle("#fff8f0"));
        return card;
    }

    private BorderPane header() {
        Label brand = label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        HBox navigation = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", true),
                nav("Recruiter", false), nav("About Us", false), nav("Contact Us", false));
        navigation.setAlignment(Pos.CENTER);
        AppNavigator.activateNavigation(navigation);
        Button login = outline("Login"), signup = primary("Sign Up");
        login.setOnAction(e -> AppNavigator.login());
        signup.setOnAction(e -> AppNavigator.signUp((Stage) signup.getScene().getWindow(),
                () -> AppNavigator.open((Stage) signup.getScene().getWindow(), "Worker")));
        HBox account = new HBox(10, login, signup);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(navigation);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return bar;
    }

    private HBox actionBar(Runnable back) {
        Button previous = outline("← Back to skills");
        previous.setOnAction(e -> {
            stopSlider();
            if (back != null)
                back.run();
        });
        Label hint = label("Choose an opportunity to start your next job.",
                "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox bar = new HBox(16, previous, space, hint);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(14, 60, 14, 60));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private ComboBox<String> combo(String... v) {
        ComboBox<String> b = new ComboBox<>();
        b.getItems().addAll(v);
        b.getSelectionModel().selectFirst();
        b.setPrefWidth(190);
        b.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;-fx-background-radius:12px;-fx-font-size:13px;");
        return b;
    }

    private Button nav(String t, boolean a) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (a ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (a ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
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
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return l;
    }

    private String cardStyle(String c) {
        return "-fx-background-color:" + c
                + ";-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    }

    private ImageView image(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        v.setSmooth(true);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private void clip(ImageView v, double w, double h, double r) {
        Rectangle s = new Rectangle(w, h);
        s.setArcWidth(r * 2);
        s.setArcHeight(r * 2);
        v.setClip(s);
    }

    private void startSlider() {
        stopSlider();
        slider = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            heroIndex = (heroIndex + 1) % HERO_IMAGES.length;
            heroImage.setImage(load(HERO_IMAGES[heroIndex]));
        }));
        slider.setCycleCount(Timeline.INDEFINITE);
        slider.play();
    }

    private void stopSlider() {
        if (slider != null)
            slider.stop();
    }

    private record Job(String title, String location, String wage, int image) {
    }
}
