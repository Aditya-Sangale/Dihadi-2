package com.dihadi.view.worker.Carpenter;

import com.dihadi.view.AppNavigator;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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

/** Marketplace-style Carpenter jobs page reached after skill selection. */
public class CarpenterJobRole {
    private static final String[] HERO_IMAGES = {
            "/assets/images/worker/carpenter/skill-01.jpg", "/assets/images/worker/carpenter/skill-02.jpg",
            "/assets/images/worker/carpenter/skill-04.jpg", "/assets/images/worker/carpenter/skill-06.jpg"
    };
    private static final String[][] JOBS = {
            {"Elite Living Interiors", "Pune, Maharashtra", "₹1,000", "01", null, null, null, "Furniture Carpenter"},
            {"Skyline Tower Frame Work", "Mumbai, Maharashtra", "₹1,200", "02", null, null, null, "Framing Carpenter"},
            {"Godrej Woods Woodwork", "Nashik, Maharashtra", "₹1,150", "03", null, null, null, "Cabinet Maker"},
            {"Brigade Tech Shuttering", "Bangalore, Karnataka", "₹1,100", "04", null, null, null, "Formwork Carpenter"},
            {"Capital Heights Roofing", "New Delhi, Delhi", "₹1,300", "05", null, null, null, "Roofing Carpenter"},
            {"Ocean Crest Finishings", "Chennai, Tamil Nadu", "₹1,050", "06", null, null, null, "Trim Carpenter"}
    };

    private final FlowPane jobs = new FlowPane(24, 24);
    private ImageView heroImage;
    private Timeline slider;
    private int heroIndex;

    public Scene getCarpenterJobRoleScene(Runnable back) {
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
        Label title = label("Carpenter Job Roles",
                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label eyebrow = label("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;");
        heroImage = new ImageView(load(HERO_IMAGES[0]));
        heroImage.setFitWidth(640);
        heroImage.setFitHeight(360);
        heroImage.setPreserveRatio(false);
        StackPane visual = new StackPane(heroImage);
        visual.setPrefSize(640, 360);
        visual.setStyle(cardStyle("#faf3e8"));
        clip(heroImage, 640, 360, 22);
        slider = new Timeline(new KeyFrame(Duration.seconds(3.2), e -> {
            heroIndex = (heroIndex + 1) % HERO_IMAGES.length;
            heroImage.setImage(load(HERO_IMAGES[heroIndex]));
        }));
        slider.setCycleCount(Timeline.INDEFINITE);
        slider.play();

        Label quote = label("“Crafting spaces with precision, passion, and pride across every project.”",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setMaxWidth(390);

        VBox meta = new VBox(16, eyebrow, title, quote);
        meta.setAlignment(Pos.CENTER);
        meta.setPrefWidth(410);

        HBox wrapper = new HBox(34, visual, meta);
        wrapper.setAlignment(Pos.CENTER);
        VBox card = new VBox(wrapper);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28));
        card.setMaxWidth(1200);
        card.setStyle(cardStyle("#fff8f0"));
        return card;
    }

    private VBox filter() {
        ComboBox<String> state = combo("All States", "Maharashtra", "Karnataka", "Tamil Nadu",
                "Delhi");
        ComboBox<String> city = combo("All Cities", "Pune", "Mumbai", "Nashik", "Bangalore",
                "New Delhi", "Chennai");
        ComboBox<String> skill = combo("All Skills", "Furniture", "Framing", "Cabinet",
                "Formwork", "Roofing", "Trim");
        Button clear = outline("Clear filters");
        Button find = primary("Find roles");
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);

        VBox box = new VBox(14,
                label("Find a suitable carpenter job role for you",
                        "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                controls);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(22, 24, 22, 24));
        box.setMaxWidth(1200);
        box.setStyle(cardStyle("#faf3e8"));

        find.setOnAction(e -> renderJobs(state.getValue(), city.getValue(), skill.getValue()));
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
            renderJobs(null, null, null);
        });
        return box;
    }

    private VBox jobSection() {
        VBox section = new VBox(22,
                label("Available Opportunities",
                        "-fx-font-family:'Georgia';-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                jobs);
        renderJobs(null, null, null);
        section.setMaxWidth(1200);
        return section;
    }

    private List<String[]> getAllJobs() {
        List<String[]> all = new java.util.ArrayList<>();
        try {
            List<com.dihadi.model.WorkforceRequirement> reqs = new com.dihadi.controller.WorkforceRequirementController().getAllRequirements();
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
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("carpenter")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Carpenter";
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
                        String imgNum = (photoUrl != null && !photoUrl.isBlank()) ? photoUrl : String.format("%02d", (imgIdx % 6) + 1);
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

    private void renderJobs(String state, String city, String skill) {
        jobs.getChildren().clear();
        List<String[]> allJobs = getAllJobs();
        for (String[] job : allJobs) {
            String roleTitle = job.length > 7 && job[7] != null ? job[7] : job[0];
            String searchable = (job[0] + " " + job[1] + " " + roleTitle).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase()) || roleTitle.toLowerCase().contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches)
                jobs.getChildren().add(jobCard(job));
        }
        if (jobs.getChildren().isEmpty())
            jobs.getChildren().add(label("No exact role found. Clear filters to view all carpenter roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
    }

    private VBox jobCard(String[] j) {
        String imgPath = j[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = String.format("/assets/images/worker/carpenter/skill-%s.jpg", j[3]);
        }
        ImageView photo = image(imgPath, 316, 178);
        clip(photo, 316, 178, 12);
        String projectName = j[0];
        String roleTitle = j.length > 7 ? jobTitleFallback(j) : j[0];

        Label name = label(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        name.setWrapText(true);
        Label role = label("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label location = label("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Label wageLabel = label("Daily wage", "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Label wage = label(j[2], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#735c00;");
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
                            if ((app.getJobTitle() != null && app.getJobTitle().equalsIgnoreCase(roleTitle)) || (j[4] != null && j[4].equals(app.getProjectId()))) {
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

        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/carpenter/skill-01.jpg";
        apply.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow();
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, j[1], j[2], detailImg, j[4], j[5], j[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene));
        });
        HBox pay = new HBox(wageLabel, wage);
        pay.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(wageLabel, Priority.ALWAYS);
        VBox card = new VBox(10, photo, name, role, location, space, pay, apply);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        role.setAlignment(Pos.CENTER);
        role.setMaxWidth(Double.MAX_VALUE);
        location.setAlignment(Pos.CENTER);
        location.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(344, 400);
        card.setPadding(new Insets(14));
        card.setStyle(cardStyle("#fff8f0"));
        return card;
    }

    private String jobTitleFallback(String[] j) {
        return j.length > 7 && j[7] != null ? j[7] : j[0];
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
        Button previous = outline("? Back to skills");
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

    private ComboBox<String> combo(String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.getSelectionModel().selectFirst();
        box.setPrefWidth(190);
        box.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;-fx-background-radius:12px;-fx-font-size:13px;");
        return box;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private Label label(String text, String style) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return l;
    }

    private String cardStyle(String colour) {
        return "-fx-background-color:" + colour
                + ";-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView();
        Image img = load(path);
        if (img == null) {
            img = load("/assets/images/worker/carpenter/skill-01.jpg");
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

    private void clip(ImageView view, double width, double height, double radius) {
        Rectangle shape = new Rectangle(width, height);
        shape.setArcWidth(radius * 2);
        shape.setArcHeight(radius * 2);
        view.setClip(shape);
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

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
