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
import java.util.List;
import java.util.Locale;

/**
 * Interactive Site Supervisor job-role marketplace built from the supplied
 * design.
 */
public class SiteSupervisorJobRolesPage {
    private static final String ALL = "All";
    // Title, Location, Wage, Image, Project ID, Mobile, Requirement ID
    private static final String[][] JOBS = {
            {"Lodha Grandeur Site Office", "Pune, Maharashtra", "₹1,500", "01", null, null, null, "Foreman"},
            {"Coastal Road Safety Zone", "Mumbai, Maharashtra", "₹1,600", "02", null, null, null, "Safety Supervisor"},
            {"Nashik Expressway QA Unit", "Nashik, Maharashtra", "₹1,450", "03", null, null, null, "Quality Inspector"},
            {"Electronic City Highrise", "Bangalore, Karnataka", "₹1,700", "04", null, null, null, "General Supervisor"},
            {"Central Vista Materials Yard", "New Delhi, Delhi", "₹1,550", "05", null, null, null, "Material Supervisor"},
            {"Chennai Metro Rail Phase 2", "Chennai, Tamil Nadu", "₹1,800", "06", null, null, null, "Site Engineer"},
            {"Hitec Smart City Tower B", "Hyderabad, Telangana", "₹1,650", "07", null, null, null, "Project Coordinator"},
            {"DLF Cyber City Expansion", "Gurgaon, Haryana", "₹1,750", "08", null, null, null, "Construction Supervisor"},
            {"Bhiwandi Amazon SEZ", "Bhiwandi, Maharashtra", "₹1,400", "09", null, null, null, "Shift Incharge"}
    };

    private java.util.List<String[]> getAllJobs() {
        java.util.List<String[]> all = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.WorkforceRequirement> reqs = new com.dihadi.controller.WorkforceRequirementController().getAllRequirements();
            List<com.dihadi.model.Project> projects = new com.dihadi.controller.ProjectController().getAllProjects();
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
                        String projectName = (p != null && p.getProjectName() != null && !p.getProjectName().isBlank())
                                ? p.getProjectName()
                                : title + " Project";
                        String loc = (p != null && p.getCity() != null && !p.getCity().isBlank() ? p.getCity() : "Pune") + ", " +
                                     (p != null && p.getState() != null && !p.getState().isBlank() ? p.getState() : "Maharashtra");
                        String wage = "₹" + String.format("%,d", (long)req.getDailyWages());
                        String photoUrl = null;
                        if (p != null && p.getImageUrls() != null && !p.getImageUrls().isEmpty()) {
                            photoUrl = p.getImageUrls().get(0);
                        }
                        String imgNum = (photoUrl != null && !photoUrl.isBlank()) ? photoUrl : String.format("%02d", (imgIdx % 12) + 1);
                        imgIdx++;
                        String recruiterMobile = p != null ? p.getMobile() : null;
                        all.add(new String[]{ projectName, loc, wage, imgNum, req.getProjectId(), recruiterMobile, req.getRequirementId(), title });
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

    public Scene getScene(Runnable backAction) {
        BorderPane page = new BorderPane();
        page.setTop(header(backAction));
        page.setCenter(content());
        page.setBottom(bottomBar(backAction));
        page.setStyle("-fx-background-color:#f3e7ce;");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(root, 1400, 780);
    }

    private Node header(Runnable backAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);

        Button home = nav("Home", false), business = nav("Business", false), worker = nav("Worker", true);
        Button recruiter = nav("Recruiter", false), about = nav("About Us", false), contact = nav("Contact Us", false);
        home.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) home.getScene().getWindow(), "Home"));
        business.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) business.getScene().getWindow(), "Business"));
        worker.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
            else
                com.dihadi.view.AppNavigator.open((javafx.stage.Stage) worker.getScene().getWindow(), "Worker");
        });
        recruiter.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) recruiter.getScene().getWindow(), "Recruiter"));
        about.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) about.getScene().getWindow(), "About Us"));
        contact.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) contact.getScene().getWindow(), "Contact Us"));
        HBox navigation = new HBox(12, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);

        Button admin = com.dihadi.view.AppNavigator.createHeaderActionButton();
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(new HBox(10, admin));
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    private Node content() {
        VBox layout = new VBox(38, hero(), filterBar(), opportunities());
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(1280);
        layout.setPadding(new Insets(34, 58, 45, 58));
        StackPane canvas = new StackPane(layout);
        canvas.setAlignment(Pos.TOP_CENTER);
        canvas.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        return scroll;
    }

    private Node hero() {
        heroImage = image("/assets/images/worker/foreman/skill-00.jpg", 590, 330);
        heroImage.setClip(roundClip(590, 330));
        StackPane visual = new StackPane(heroImage);
        visual.setPrefSize(590, 330);
        visual.setStyle(cardStyle("#e9e2d7"));
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
        for (String[] job : getAllJobs()) {
            if (matches(job)) {
                cards.getChildren().add(card(job));
                count++;
            }
        }
        resultText.setText(count == 0 ? "No roles found. Try clearing one or more filters."
                : count + " role" + (count == 1 ? "" : "s") + " available");
    }

    private boolean matches(String[] job) {
        String roleTitle = job.length > 7 && job[7] != null ? job[7] : job[0];
        return (ALL.equals(state.getValue()) || job[1].contains(state.getValue()))
                && (ALL.equals(city.getValue()) || job[1].contains(city.getValue()))
                && (ALL.equals(role.getValue())
                        || job[0].toLowerCase(Locale.ROOT).contains(role.getValue().toLowerCase(Locale.ROOT))
                        || roleTitle.toLowerCase(Locale.ROOT).contains(role.getValue().toLowerCase(Locale.ROOT)));
    }

    private Node card(String[] job) {
        String imgPath = job[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = String.format("/assets/images/worker/foreman/skill-%s.jpg", job[3]);
        }
        ImageView photo = image(imgPath, 316, 178);
        photo.setClip(roundClip(316, 178));
        String projectName = job[0];
        String roleTitle = job.length > 7 && job[7] != null ? job[7] : job[0];

        Label name = label(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        name.setWrapText(true);
        name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label roleLabel = label("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        roleLabel.setWrapText(true);
        roleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label location = label("⌖  " + job[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        location.setWrapText(true);
        location.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label wageLabel = label("Daily wage", "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Label wage = label(job[2], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);
        Button apply = primary("Apply now");
        apply.setMaxWidth(Double.MAX_VALUE);
        
        Runnable checkAppliedStatus = () -> {
            if (com.dihadi.view.SessionManager.currentWorker != null) {
                new Thread(() -> {
                    try {
                        java.util.List<com.dihadi.model.JobApplication> apps = new com.dihadi.controller.JobApplicationController().getApplicationsByWorker(com.dihadi.view.SessionManager.currentWorker.getMobileNumber());
                        boolean hasApplied = false;
                        for (com.dihadi.model.JobApplication app : apps) {
                            if ((app.getJobTitle() != null && app.getJobTitle().equalsIgnoreCase(roleTitle)) || (job[4] != null && job[4].equals(app.getProjectId()))) {
                                hasApplied = true;
                                break;
                            }
                        }
                        if (hasApplied) {
                            javafx.application.Platform.runLater(() -> {
                                apply.setText("Already applied ✓");
                                apply.setStyle("-fx-background-color:#2a7e3b;-fx-background-radius:12px;-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:10px 18px;");
                                apply.setDisable(true);
                            });
                        }
                    } catch (Exception ignored) {}
                }).start();
            }
        };
        checkAppliedStatus.run();
        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/foreman/skill-01.jpg";
        Runnable openDetails = () -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow();
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, job[1], job[2], detailImg, job[4], job[5], job[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene));
        };
        apply.setOnAction(e -> openDetails.run());
        HBox pay = new HBox(wageLabel, wage);
        pay.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(wageLabel, Priority.ALWAYS);
        VBox card = new VBox(10, photo, name, roleLabel, location, space, pay, apply);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        roleLabel.setAlignment(Pos.CENTER);
        roleLabel.setMaxWidth(Double.MAX_VALUE);
        location.setAlignment(Pos.CENTER);
        location.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(350);
        card.setMinHeight(410);
        card.setPadding(new Insets(16));
        card.setStyle(cardStyle("#fff8f0"));
        card.setOnMouseClicked(e -> openDetails.run());
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:22px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:22px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),20,0,0,8px);"));
        card.setOnMouseExited(e -> card.setStyle(cardStyle("#fff8f0")));
        return card;
    }

    private String jobTitleFallback(String[] job) {
        return job.length > 7 && job[7] != null ? job[7] : job[0];
    }

    private Node bottomBar(Runnable backAction) {
        Button back = outline("←  Back to categories");
        back.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
        });
        Label hint = label("Choose an opportunity to start your next job.",
                "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox bar = new HBox(16, back, space, hint);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private void startSlider() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            slideIndex = (slideIndex + 1) % 6;
            heroImage.setImage(load(String.format("/assets/images/worker/foreman/skill-%02d.jpg", slideIndex + 1)));
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
        ImageView view = new ImageView();
        Image img = load(path);
        if (img == null) {
            img = load("/assets/images/worker/foreman/skill-01.jpg");
        }
        view.setImage(img);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    private Image load(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                return new Image(path, true);
            }
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                return new Image(file.toURI().toString(), true);
            }
            var resource = getClass().getResource(path);
            if (resource != null) {
                return new Image(resource.toExternalForm());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private Button nav(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return button;
    }

    private Button primary(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return button;
    }

    private Button outline(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
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
