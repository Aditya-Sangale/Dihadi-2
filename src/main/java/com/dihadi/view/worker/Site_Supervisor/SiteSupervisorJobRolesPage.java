package com.dihadi.view.worker.Site_Supervisor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Locale;

/**
 * Interactive Site Supervisor job-role marketplace built from the supplied
 * design.
 */
public class SiteSupervisorJobRolesPage {
    private static final String ALL = "All";
    // Title, Location, Wage, Image, Project ID, Mobile, Requirement ID
    private static final String[][] JOBS = {
            {"Foreman", "Pune, Maharashtra", "₹1,500", "01", null, null, null},
            {"Safety Supervisor", "Mumbai, Maharashtra", "₹1,600", "02", null, null, null},
            {"Quality Inspector", "Nashik, Maharashtra", "₹1,450", "03", null, null, null},
            {"General Supervisor", "Bangalore, Karnataka", "₹1,700", "04", null, null, null},
            {"Material Supervisor", "New Delhi, Delhi", "₹1,550", "05", null, null, null},
            {"Site Engineer", "Chennai, Tamil Nadu", "₹1,800", "06", null, null, null},
            {"Project Coordinator", "Hyderabad, Telangana", "₹1,650", "07", null, null, null},
            {"Construction Supervisor", "Gurgaon, Haryana", "₹1,750", "08", null, null, null},
            {"Shift Incharge", "Bhiwandi, Maharashtra", "₹1,400", "09", null, null, null}
    };

    private java.util.List<String[]> getAllJobs() {
        java.util.List<String[]> all = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.WorkforceRequirement> reqs = new com.dihadi.controller.WorkforceRequirementController().getAllRequirements();
            java.util.List<com.dihadi.model.Project> projects = new com.dihadi.controller.ProjectController().getAllProjects();
            java.util.Map<String, com.dihadi.model.Project> projectMap = new java.util.HashMap<>();
            if (projects != null) {
                for (com.dihadi.model.Project p : projects) {
                    if (p.getProjectId() != null) projectMap.put(p.getProjectId(), p);
                }
            }
            if (reqs != null) {
                int imgIdx = 1;
                for (com.dihadi.model.WorkforceRequirement req : reqs) {
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("supervisor")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Site Supervisor";
                        com.dihadi.model.Project p = req.getProjectId() != null ? projectMap.get(req.getProjectId()) : null;
                        String loc = (p != null && p.getCity() != null && !p.getCity().isBlank() ? p.getCity() : "Pune") + ", " +
                                     (p != null && p.getState() != null && !p.getState().isBlank() ? p.getState() : "Maharashtra");
                        String wage = "₹" + String.format("%,d", (long)req.getDailyWages());
                        String imgNum = String.format("%02d", (imgIdx % 12) + 1);
                        imgIdx++;
                        String recruiterMobile = p != null ? p.getMobile() : null;
                        all.add(new String[]{ title, loc, wage, imgNum, req.getProjectId(), recruiterMobile, req.getRequirementId() });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String[] j : JOBS) {
            all.add(j);
        }
        return all;
    }

    private final FlowPane cards = new FlowPane(24, 24);
    private final Label resultText = new Label();
    private ComboBox<String> state;
    private ComboBox<String> city;
    private ComboBox<String> role;
    private ImageView heroImage;
    private int slideIndex;
    private final Label slideStatus = new Label();

    public Scene getScene(Runnable backAction) {
        BorderPane page = new BorderPane();
        page.setTop(header(backAction));
        page.setCenter(content());
        page.setBottom(bottomBar(backAction));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private Node header(Runnable backAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 50, 50);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        HBox brand = new HBox(12, logo, label("Supervisor Job Roles",
                "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:700;-fx-text-fill:#574500;"));
        brand.setAlignment(Pos.CENTER_LEFT);

        Button home = nav("Home", false), business = nav("Business", false), worker = nav("Worker", true);
        Button recruiter = nav("Recruiter", false), about = nav("About Us", false), contact = nav("Contact Us", false);
        worker.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
        });
        HBox navigation = new HBox(20, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);

        Button login = outline("Login"), signUp = primary("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((javafx.stage.Stage) signUp.getScene().getWindow(),
                () -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) signUp.getScene().getWindow(), "Worker")));
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(new HBox(12, login, signUp));
        header.setPadding(new Insets(15, 38, 15, 38));
        header.setStyle("-fx-background-color:#fff8f0;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return header;
    }

    private Node content() {
        VBox layout = new VBox(38, hero(), filterBar(), opportunities());
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(1280);
        layout.setPadding(new Insets(34, 58, 45, 58));
        StackPane canvas = new StackPane(layout);
        canvas.setAlignment(Pos.TOP_CENTER);
        canvas.setStyle("-fx-background-color:#fff8f0;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");
        return scroll;
    }

    private Node hero() {
        heroImage = image("/assets/images/worker/foreman/skill-00.jpg", 590, 330);
        heroImage.setClip(roundClip(590, 330));
        slideStatus.setStyle(
                "-fx-background-color:rgba(30,27,21,.68);-fx-background-radius:14px;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:6px 11px;");
        StackPane visual = new StackPane(heroImage, slideStatus);
        StackPane.setAlignment(slideStatus, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(slideStatus, new Insets(0, 14, 14, 0));
        visual.setPrefSize(590, 330);
        visual.setStyle(cardStyle("#e9e2d7"));
        updateSlideStatus();
        startSlider();
        Label quote = label(
                "\"Great structures rise not just from bricks and mortar, but from the clear vision, steady guidance, and unwavering dedication of a skilled supervisor.\"",
                "-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        quote.setWrapText(true);
        quote.setMaxWidth(500);
        VBox words = new VBox(quote);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefHeight(285);
        words.setPadding(new Insets(12, 0, 12, 26));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        HBox hero = new HBox(54, visual, words);
        hero.setAlignment(Pos.CENTER);
        return hero;
    }

    private Node filterBar() {
        state = choice(ALL, "Maharashtra", "Karnataka", "Odisha", "Haryana", "Uttar Pradesh", "Kerala", "Gujarat",
                "Madhya Pradesh");
        city = choice(ALL, "Pune", "Mumbai", "Bangalore", "Gurgaon", "Noida", "Panvel", "Jalna", "Indore");
        role = choice(ALL, "Site", "Construction", "Electrical", "Safety", "Masonry", "Plumbing", "Carpenter",
                "Labour");
        Button clear = outline("Clear filters");
        clear.setOnAction(e -> {
            state.setValue(ALL);
            city.setValue(ALL);
            role.setValue(ALL);
            showMatches();
        });
        Button find = primary("Find roles");
        find.setOnAction(e -> showMatches());
        HBox controls = new HBox(12, filterField("STATE", state), filterField("CITY", city), filterField("ROLE", role),
                clear, find);
        controls.setAlignment(Pos.BOTTOM_CENTER);
        VBox bar = new VBox(15, label("Find a suitable job role for you",
                "-fx-font-size:20px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"), controls);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(24));
        bar.setStyle(cardStyle("#ffffff"));
        return bar;
    }

    private Node opportunities() {
        resultText.setStyle("-fx-font-size:14px;-fx-text-fill:#4d4635;");
        Label eyebrow = label("CURATED FOR YOU",
                "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        VBox heading = new VBox(5, eyebrow,
                label("Available opportunities",
                        "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                resultText);
        cards.setAlignment(Pos.CENTER);
        cards.setPrefWrapLength(1170);
        showMatches();
        return new VBox(20, heading, cards);
    }

    private void showMatches() {
        if (state == null)
            return;
        cards.getChildren().clear();
        int count = 0;
        for (String[] job : getAllJobs())
            if (matches(job)) {
                cards.getChildren().add(card(job));
                count++;
            }
        resultText.setText(count == 0 ? "No roles found. Try clearing one or more filters."
                : count + " role" + (count == 1 ? "" : "s") + " available");
    }

    private boolean matches(String[] job) {
        return (ALL.equals(state.getValue()) || job[1].contains(state.getValue()))
                && (ALL.equals(city.getValue()) || job[1].contains(city.getValue()))
                && (ALL.equals(role.getValue())
                        || job[0].toLowerCase(Locale.ROOT).contains(role.getValue().toLowerCase(Locale.ROOT)));
    }

    private Node card(String[] job) {
        ImageView photo = image(String.format("/assets/images/worker/foreman/skill-%s.jpg", job[3]), 316, 178);
        photo.setClip(roundClip(316, 178));
        Label name = label(job[0], "-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                location = label("⌖  " + job[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wageLabel = label("Daily wage", "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wage = label(job[2], "-fx-font-size:19px;-fx-font-weight:800;-fx-text-fill:#735c00;");
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
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow();
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(job[0], job[1], job[2], "/assets/images/worker/site_supervisor/skill-01.jpg", job[4], job[5], job[6]).getScene(() -> stage.setScene(currentScene), currentScene));
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

    private Node bottomBar(Runnable backAction) {
        Button back = outline("�? Back to skills");
        back.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
        });
        HBox bar = new HBox(back);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(13, 58, 13, 58));
        bar.setStyle("-fx-background-color:#fff8f0;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private void startSlider() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            slideIndex = (slideIndex + 1) % 6;
            heroImage.setImage(load(String.format("/assets/images/worker/foreman/skill-%02d.jpg", slideIndex + 1)));
            updateSlideStatus();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private ComboBox<String> choice(String... values) {
        ComboBox<String> box = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(values)));
        box.setValue(ALL);
        box.setPrefWidth(180);
        box.setStyle(
                "-fx-background-color:#faf3e8;-fx-border-color:#7e7665;-fx-border-radius:10px;-fx-background-radius:10px;-fx-font-size:13px;-fx-padding:3px 8px;");
        return box;
    }

    private VBox filterField(String title, ComboBox<String> field) {
        Label caption = label(title,
                "-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#685c52;-fx-letter-spacing:1px;");
        return new VBox(5, caption, field);
    }

    private void updateSlideStatus() {
        slideStatus.setText((slideIndex + 1) + " / 6");
    }

    private void applyForRole(String[] job, Button apply) {
        apply.setText("APPLIED");
        apply.setDisable(true);
        apply.setStyle(
                "-fx-background-color:#685c52;-fx-background-radius:10px;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 22px;");
        Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
        confirmation.setTitle("Application sent");
        confirmation.setHeaderText("Application submitted for " + job[0]);
        confirmation.setContentText("Your application has been saved. We will notify you when the employer responds.");
        confirmation.show();
    }

    private Rectangle roundClip(double width, double height) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        return clip;
    }

    private Node line() {
        StackPane line = new StackPane();
        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);
        line.setStyle("-fx-background-color:#e5ded2;");
        return line;
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    private Image load(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private Button nav(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return button;
    }

    private Button primary(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#735c00;-fx-background-radius:10px;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 22px;-fx-cursor:hand;");
        return button;
    }

    private Button outline(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:10px;-fx-border-color:#7e7665;-fx-border-radius:10px;-fx-text-fill:#1e1b15;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return button;
    }

    private String cardStyle(String background) {
        return "-fx-background-color:" + background
                + ";-fx-background-radius:14px;-fx-border-color:#d0c5af;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),16,0,0,5px);";
    }

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
