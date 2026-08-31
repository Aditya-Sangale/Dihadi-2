package com.dihadi.view.worker.Painter;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Painter jobs marketplace with a reference-style slider, filters, and Apply
 * actions.
 */
public class PainterJobRole {
    private static final String[][] JOBS = {
            { "Royal Palms Luxury Villas", "Pune, Maharashtra", "₹950", "01", null, null, null, "Interior Painter" },
            { "Sea Princess Texture Works", "Mumbai, Maharashtra", "₹1,100", "02", null, null, null, "Wall Texture Painter" },
            { "Nashik Industrial Coatings", "Nashik, Maharashtra", "₹1,200", "03", null, null, null, "Industrial Painter" },
            { "Prestige Tech Park Spraying", "Bangalore, Karnataka", "₹1,150", "04", null, null, null, "Spray Painter" },
            { "Lutyens Bungalow Woodwork", "New Delhi, Delhi", "₹1,300", "05", null, null, null, "Wood Polish Painter" },
            { "East Coast Road Residences", "Chennai, Tamil Nadu", "₹1,000", "06", null, null, null, "Exterior Painter" }
    };
    private ImageView slide;
    private int index;

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
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("painter")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Painter";
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

    private void renderJobs(FlowPane grid, java.util.List<String[]> jobsList, String state, String city, String skill) {
        grid.getChildren().clear();
        for (String[] j : jobsList) {
            String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];
            String searchable = (j[0] + " " + j[1] + " " + roleTitle).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase()) || roleTitle.toLowerCase().contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches) {
                grid.getChildren().add(job(j));
            }
        }
        if (grid.getChildren().isEmpty()) {
            grid.getChildren().add(l("No exact roles found matching your filter. Clear filters to view all roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
        }
    }

    private HBox controls(FlowPane grid, java.util.List<String[]> allJobs) {
        ComboBox<String> state = c("Select state", "All States", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi"),
                city = c("Select city", "All Cities", "Pune", "Mumbai", "Bangalore", "New Delhi"),
                skill = c("Select painting skill", "All Skills", "Interior", "Exterior", "Texture", "Spray", "Putty", "Polish");
        Button clear = o("Clear filters"), find = p("Find roles");
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);
        VBox filter = new VBox(14,
                l("Find a suitable job role for you", "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                controls);
        filter.setAlignment(Pos.CENTER);
        filter.setPadding(new Insets(22));
        filter.setStyle(
                "-fx-background-color:#faf3e8;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");
        find.setOnAction(e -> renderJobs(grid, allJobs, state.getValue(), city.getValue(), skill.getValue()));
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
            renderJobs(grid, allJobs, null, null, null);
        });
        return new HBox(filter);
    }

    public Scene getPainterJobRoleScene(Runnable back) {
        Label eye = l("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                title = l("Painter Job Roles",
                        "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                intro = l("Transforming spaces with precision, color, and craft across residential and commercial sites.",
                        "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        VBox hero = new VBox(12, eye, title, intro);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(32, 36, 30, 36));
        hero.setMaxWidth(1140);
        hero.setStyle(card());

        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1100);

        java.util.List<String[]> allJobs = getAllJobs();
        renderJobs(grid, allJobs, null, null, null);

        VBox content = new VBox(28, hero, slider(), controls(grid, allJobs),
                l("Available opportunities",
                        "-fx-font-family:'Georgia';-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                grid);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 36, 42, 36));
        content.setMaxWidth(1240);

        StackPane canvas = new StackPane(content);
        canvas.setAlignment(Pos.TOP_CENTER);
        canvas.setStyle("-fx-background-color:#f3e7ce;");

        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");

        Button prev = o("←  Back to categories");
        prev.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Label hint = l("Choose an opportunity to start your next job.",
                "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);
        HBox bottom = new HBox(16, prev, space, hint);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(16, 70, 16, 70));
        bottom.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(header());
        page.setBottom(bottom);
        page.setStyle("-fx-background-color:#f3e7ce;");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(root, 1400, 780);
    }

    private StackPane slider() {
        slide = img("/assets/images/worker/painter/skill-01.jpg", 600, 300);
        StackPane s = new StackPane(slide);
        s.setPrefSize(600, 300);
        s.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            index = (index % 6) + 1;
            slide.setImage(load("/assets/images/worker/painter/skill-0" + index + ".jpg"));
        }));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        return s;
    }

    private VBox job(String[] j) {
        String imgPath = j[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = "/assets/images/worker/painter/skill-" + j[3] + ".jpg";
        }
        ImageView im = img(imgPath, 316, 178);
        String projectName = j[0];
        String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];

        Label n = l(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label role = l("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label loc = l("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Label w = l("Daily wage  " + j[2], "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        n.setWrapText(true);
        n.setAlignment(Pos.CENTER);
        n.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        n.setMaxWidth(Double.MAX_VALUE);
        role.setWrapText(true);
        role.setAlignment(Pos.CENTER);
        role.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        role.setMaxWidth(Double.MAX_VALUE);
        loc.setWrapText(true);
        loc.setAlignment(Pos.CENTER);
        loc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        loc.setMaxWidth(Double.MAX_VALUE);
        Button a = p("Apply now");
        a.setMaxWidth(Double.MAX_VALUE);
        
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
                                a.setText("Already applied ✓");
                                a.setStyle("-fx-background-color:#2a7e3b;-fx-background-radius:12px;-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:10px 18px;");
                                a.setDisable(true);
                            });
                        }
                    } catch (Exception ignored) {}
                }).start();
            }
        };
        checkAppliedStatus.run();

        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/painter/skill-01.jpg";
        Runnable openDetails = () -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) a.getScene().getWindow(); 
            javafx.scene.Scene currentScene = a.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, j[1], j[2], detailImg, j[4], j[5], j[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene)); 
        };
        a.setOnAction(e -> openDetails.run());
        VBox v = new VBox(10, im, n, role, loc, w, a);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(16));
        v.setPrefWidth(350);
        v.setMinHeight(410);
        v.setStyle(card());
        v.setOnMouseClicked(e -> openDetails.run());
        v.setOnMouseEntered(e -> v.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:22px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:22px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),20,0,0,8px);"));
        v.setOnMouseExited(e -> v.setStyle(card()));
        return v;
    }

    private ImageView img(String path, double w, double h) {
        ImageView v = new ImageView();
        Image img = load(path);
        if (img == null) {
            img = load("/assets/images/worker/painter/skill-01.jpg");
        }
        v.setImage(img);
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        v.setSmooth(true);
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(24);
        r.setArcHeight(24);
        v.setClip(r);
        return v;
    }

    private Image load(String p) {
        if (p == null || p.isBlank()) return null;
        try {
            if (p.startsWith("http://") || p.startsWith("https://") || p.startsWith("file:")) {
                return new Image(p, true);
            }
            java.io.File file = new java.io.File(p);
            if (file.exists()) {
                return new Image(file.toURI().toString(), true);
            }
            var r = getClass().getResource(p);
            if (r != null) {
                return new Image(r.toExternalForm());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private ComboBox<String> c(String... x) {
        ComboBox<String> b = new ComboBox<>();
        b.getItems().addAll(x);
        b.getSelectionModel().selectFirst();
        b.setPrefWidth(190);
        b.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;");
        return b;
    }

    private Label l(String t, String s) {
        Label x = new Label(t);
        x.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return x;
    }

    private String card() {
        return "-fx-background-color:#fff8f0;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
    }

    private Button p(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;");
        return b;
    }

    private Button o(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return b;
    }

    private BorderPane header() {
        ImageView logo = new ImageView(new Image(getClass().getResource("/assets/logo/dihadi logo.jpeg").toExternalForm()));
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        Label brand = l("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brandLockup = new HBox(10, logo, brand);
        brandLockup.setAlignment(Pos.CENTER_LEFT);

        Button home = nav("Home", false);
        home.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) home.getScene().getWindow(), "Home"));
        Button business = nav("Business", false);
        business.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) business.getScene().getWindow(), "Business"));
        Button worker = nav("Worker", true);
        worker.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) worker.getScene().getWindow(), "Worker"));
        Button recruiter = nav("Recruiter", false);
        recruiter.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) recruiter.getScene().getWindow(), "Recruiter"));
        Button about = nav("About Us", false);
        about.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) about.getScene().getWindow(), "About Us"));
        Button contact = nav("Contact Us", false);
        contact.setOnAction(e -> com.dihadi.view.AppNavigator.open((javafx.stage.Stage) contact.getScene().getWindow(), "Contact Us"));
        HBox navigation = new HBox(12, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);
        Button admin = com.dihadi.view.AppNavigator.createHeaderActionButton();
        HBox account = new HBox(10, admin);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane bar = new BorderPane();
        bar.setLeft(brandLockup);
        bar.setCenter(navigation);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
