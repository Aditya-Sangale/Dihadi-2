package com.dihadi.view.worker.Plumber;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Plumber opportunities marketplace matching the DIHADI JobRole design. */
public class PlumberJobRole {
    private static final String[][] J = { { "Residential Plumber", "Pune, Maharashtra", "₹950", "01" },
            { "Pipe Fitting Plumber", "Mumbai, Maharashtra", "₹1,100", "03" },
            { "Waterline Technician", "Nashik, Maharashtra", "₹1,050", "04" },
            { "Sanitary Plumber", "Bangalore, Karnataka", "₹1,150", "05" },
            { "Industrial Plumber", "New Delhi, Delhi", "₹1,300", "07" },
            { "Drainage Specialist", "Chennai, Tamil Nadu", "₹1,000", "09" },
            { "Bathroom Fitter", "Hyderabad, Telangana", "₹1,100", "11" },
            { "Plumbing Helper", "Bhiwandi, Maharashtra", "₹800", "13" } };
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
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("plumber")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Plumber";
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
        for (String[] j : J) {
            all.add(j);
        }
        return all;
    }

    private void renderJobs(FlowPane grid, java.util.List<String[]> jobsList, String state, String city, String skill) {
        grid.getChildren().clear();
        for (String[] j : jobsList) {
            String searchable = (j[0] + " " + j[1]).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches) {
                grid.getChildren().add(job(j));
            }
        }
        if (grid.getChildren().isEmpty()) {
            grid.getChildren().add(l("No exact roles found matching your filter. Clear filters to view all roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
        }
    }

    public Scene getPlumberJobRoleScene(Runnable back) {
        Label badge = l("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                t = l("Plumber Job Roles",
                        "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                q = l("“Every reliable waterline begins with a skilled hand and careful craft.�?",
                        "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#4d4635;");
        q.setWrapText(true);
        q.setMaxWidth(390);
        VBox words = new VBox(15, badge, t, q);
        words.setAlignment(Pos.CENTER);
        StackPane visual = slider();
        HBox row = new HBox(34, visual, words);
        row.setAlignment(Pos.CENTER);
        VBox hero = new VBox(row);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(28));
        hero.setMaxWidth(1200);
        hero.setStyle(style("#fff8f0"));
        ComboBox<String> state = c("Select state", "All States", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi"),
                city = c("Select city", "All Cities", "Pune", "Mumbai", "Nashik", "Bangalore"),
                skill = c("Select plumbing skill", "All Skills", "Pipe fitting", "Sanitary", "Drainage", "Waterline", "Bathroom", "Plumber");
        Button clear = o("Clear filters"), find = p("Find roles");
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);
        VBox filter = new VBox(14,
                l("Find a suitable job role for you", "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                controls);
        filter.setAlignment(Pos.CENTER);
        filter.setPadding(new Insets(22));
        filter.setMaxWidth(1200);
        filter.setStyle(style("#faf3e8"));
        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1140);

        java.util.List<String[]> allJobs = getAllJobs();
        renderJobs(grid, allJobs, null, null, null);

        find.setOnAction(e -> renderJobs(grid, allJobs, state.getValue(), city.getValue(), skill.getValue()));
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
            renderJobs(grid, allJobs, null, null, null);
        });

        VBox content = new VBox(28, hero, filter,
                l("Available opportunities",
                        "-fx-font-family:'Georgia';-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                grid);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 36, 42, 36));
        StackPane canvas = new StackPane(content);
        canvas.setStyle("-fx-background-color:#f3e7ce;");
        ScrollPane scroll = new ScrollPane(canvas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        Button backButton = o("�? Back to skills");
        backButton.setOnAction(event -> {
            if (back != null)
                back.run();
        });
        HBox bottom = new HBox(backButton);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(14, 60, 14, 60));
        bottom.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        BorderPane page = new BorderPane(scroll);
        page.setBottom(bottom);
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private StackPane slider() {
        slide = img("/assets/images/worker/plumber/skill-00.jpg", 640, 320);
        StackPane box = new StackPane(slide);
        box.setPrefSize(640, 320);
        box.setStyle(style("#faf3e8"));
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            index = (index + 1) % 8;
            slide.setImage(load(String.format("/assets/images/worker/plumber/skill-%02d.jpg", index + 1)));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        return box;
    }

    private VBox job(String[] j) {
        ImageView im = img("/assets/images/worker/plumber/skill-" + j[3] + ".jpg", 316, 178);
        Label n = l(j[0], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                loc = l("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                w = l("Daily wage  " + j[2], "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        n.setAlignment(Pos.CENTER);
        n.setMaxWidth(Double.MAX_VALUE);
        Button a = p("Apply now");
        a.setMaxWidth(Double.MAX_VALUE);
        a.setOnAction(e -> { 
            javafx.stage.Stage stage = (javafx.stage.Stage) a.getScene().getWindow(); 
            javafx.scene.Scene currentScene = a.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(j[0], j[1], j[2], "/assets/images/worker/plumber/skill-01.jpg", j[4], j[5], j[6]).getScene(() -> stage.setScene(currentScene), currentScene)); 
        });
        VBox v = new VBox(13, im, n, loc, w, a);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(14));
        v.setPrefSize(344, 350);
        v.setStyle(style("#fff8f0"));
        return v;
    }

    private ComboBox<String> c(String... x) {
        ComboBox<String> b = new ComboBox<>();
        b.getItems().addAll(x);
        b.getSelectionModel().selectFirst();
        b.setPrefWidth(190);
        b.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;-fx-background-radius:12px;");
        return b;
    }

    private ImageView img(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(24);
        r.setArcHeight(24);
        v.setClip(r);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private Label l(String t, String s) {
        Label x = new Label(t);
        x.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return x;
    }

    private String style(String c) {
        return "-fx-background-color:" + c
                + ";-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
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
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-padding:9px 18px;");
        return b;
    }
}
