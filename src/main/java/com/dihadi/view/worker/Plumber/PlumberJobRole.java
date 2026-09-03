package com.dihadi.view.worker.Plumber;

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

/** Plumber job marketplace matching the Carpenter job-role interface. */
public class PlumberJobRole {
    private static final String[] HERO_IMAGES = {
            "/assets/images/worker/plumber/skill-00.jpg", "/assets/images/worker/plumber/skill-01.jpg",
            "/assets/images/worker/plumber/skill-02.jpg", "/assets/images/worker/plumber/skill-03.jpg"
    };
    private static final String[][] JOBS = {
            { "Lodha Crown Pipeline Network", "Pune, Maharashtra", "₹950", "01", "CURATED_PL_01", null, null, "Residential Plumber" },
            { "Oberoi Sky City Water Mains", "Mumbai, Maharashtra", "₹1,100", "03", "CURATED_PL_02", null, null, "Pipe Fitting Plumber" },
            { "Nashik Smart City Drainage", "Nashik, Maharashtra", "₹1,050", "04", "CURATED_PL_03", null, null, "Waterline Technician" },
            { "Brigade Tech Gardens Sanitary", "Bangalore, Karnataka", "₹1,150", "05", "CURATED_PL_04", null, null, "Sanitary Plumber" },
            { "NTPC Plant High Pressure Lines", "New Delhi, Delhi", "₹1,300", "07", "CURATED_PL_05", null, null, "Industrial Plumber" },
            { "Chennai Port Storm Water System", "Chennai, Tamil Nadu", "₹1,000", "09", "CURATED_PL_06", null, null, "Drainage Specialist" },
            { "Hitec City Luxury Residency", "Hyderabad, Telangana", "₹1,100", "11", "CURATED_PL_07", null, null, "Bathroom Fitter" },
            { "Bhiwandi Warehousing Sewerage", "Bhiwandi, Maharashtra", "₹800", "13", "CURATED_PL_08", null, null, "Plumbing Helper" }
    };

    private final FlowPane jobs = new FlowPane(24, 24);
    private ImageView heroImage;
    private Timeline slider;
    private int heroIndex;

    public Scene getPlumberJobRoleScene(Runnable back) {
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
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(root, 1400, 780);
    }

    private VBox hero() {
        Label title = label("Plumber Job Roles",
                "-fx-font-family:'Georgia';-fx-font-size:36px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);
        Label eyebrow = label("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;");
        heroImage = new ImageView(load(HERO_IMAGES[0]));
        heroImage.setFitWidth(580);
        heroImage.setFitHeight(340);
        heroImage.setPreserveRatio(false);
        StackPane visual = new StackPane(heroImage);
        visual.setPrefSize(580, 340);
        visual.setStyle(cardStyle("#faf3e8"));
        clip(heroImage, 580, 340, 22);
        slider = new Timeline(new KeyFrame(Duration.seconds(3.2), e -> {
            heroIndex = (heroIndex + 1) % HERO_IMAGES.length;
            heroImage.setImage(load(HERO_IMAGES[heroIndex]));
        }));
        slider.setCycleCount(Timeline.INDEFINITE);
        slider.play();

        Label quote = label("“Ensuring high-efficiency water, drainage, and industrial fluid systems across modern infrastructures.”",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setMaxWidth(480);

        VBox meta = new VBox(16, eyebrow, title, quote);
        meta.setAlignment(Pos.CENTER_LEFT);
        meta.setPrefWidth(480);
        meta.setMaxWidth(480);

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
        ComboBox<String> state = combo("All States", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi", "Telangana");
        ComboBox<String> city = combo("All Cities", "Pune", "Mumbai", "Nashik", "Bangalore", "New Delhi", "Chennai", "Hyderabad", "Bhiwandi");
        ComboBox<String> skill = combo("All Skills", "Residential", "Pipe Fitting", "Waterline", "Sanitary", "Industrial", "Drainage", "Bathroom", "Helper");
        Button clear = outline("Clear filters");
        Button find = primary("Find roles");
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);

        VBox box = new VBox(14,
                label("Find a suitable plumber job role for you",
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
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("plumber")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Plumber";
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

    private void renderJobs(String state, String city, String skill) {
        jobs.getChildren().clear();
        for (String[] j : getAllJobs()) {
            String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];
            String searchable = (j[0] + " " + j[1] + " " + roleTitle).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase()) || roleTitle.toLowerCase().contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches) {
                jobs.getChildren().add(card(j));
            }
        }
        if (jobs.getChildren().isEmpty()) {
            jobs.getChildren().add(label("No exact roles found matching your filter. Clear filters to view all roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
        }
    }

    private VBox card(String[] j) {
        String imgPath = j[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = String.format("/assets/images/worker/plumber/skill-%s.jpg", j[3]);
        }
        ImageView photo = image(imgPath, 316, 178);
        clip(photo, 316, 178, 16);

        String projectName = j[0];
        String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];

        Label name = label(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        name.setWrapText(true);
        name.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label role = label("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        role.setWrapText(true);
        role.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Label location = label("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        location.setWrapText(true);
        location.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
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
                        boolean hasApplied = new com.dihadi.controller.JobApplicationController().hasWorkerApplied(
                                com.dihadi.view.SessionManager.currentWorker.getMobileNumber(),
                                j[4],
                                j.length > 6 ? j[6] : null,
                                roleTitle,
                                j[1]
                        );
                        if (hasApplied) {
                            javafx.application.Platform.runLater(() -> {
                                apply.setText("Already applied ✓");
                                apply.setStyle("-fx-background-color:#2a7e3b;-fx-background-radius:12px;-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:10px 18px;");
                                apply.setDisable(true);
                            });
                        } else {
                            javafx.application.Platform.runLater(() -> {
                                apply.setText("Apply now");
                                apply.setStyle("-fx-background-color:#d4af37;-fx-background-radius:12px;-fx-text-fill:#342f28;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:10px 18px;-fx-cursor:hand;");
                                apply.setDisable(false);
                            });
                        }
                    } catch (Exception ignored) {}
                }).start();
            } else {
                javafx.application.Platform.runLater(() -> {
                    apply.setText("Apply now");
                    apply.setStyle("-fx-background-color:#d4af37;-fx-background-radius:12px;-fx-text-fill:#342f28;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:10px 18px;-fx-cursor:hand;");
                    apply.setDisable(false);
                });
            }
        };
        checkAppliedStatus.run();
        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/plumber/skill-01.jpg";
        Runnable openDetails = () -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow(); 
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, j[1], j[2], detailImg, j[4], j[5], j[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene)); 
        };
        apply.setOnAction(e -> openDetails.run());
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

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);

        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(navBar());
        Button headerAction = AppNavigator.createHeaderActionButton();
        HBox rightBox = new HBox(10, headerAction);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        bar.setRight(rightBox);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private HBox navBar() {
        HBox navigation = new HBox(12, nav("Home", false), nav("Business", false), nav("Worker", true),
                nav("Recruiter", false), nav("About Us", false), nav("Contact Us", false));
        navigation.setAlignment(Pos.CENTER);
        AppNavigator.activateNavigation(navigation);
        return navigation;
    }

    private HBox actionBar(Runnable back) {
        Button previous = outline("←  Back to categories");
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
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private ComboBox<String> combo(String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.getSelectionModel().selectFirst();
        box.setPrefWidth(180);
        box.setStyle(
                "-fx-background-color:#faf3e8;-fx-border-color:#7e7665;-fx-border-radius:10px;-fx-background-radius:10px;-fx-font-size:13px;-fx-padding:3px 8px;");
        return box;
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return b;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
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
            img = load("/assets/images/worker/plumber/skill-00.jpg");
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

    private void clip(ImageView image, double width, double height, double radius) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        image.setClip(clip);
    }

    private void stopSlider() {
        if (slider != null) {
            slider.stop();
        }
    }
}
