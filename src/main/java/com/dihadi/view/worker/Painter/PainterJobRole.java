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
    private static final String[][] JOBS = { { "Interior Painter", "Pune, Maharashtra", "₹950", "01" },
            { "Wall Texture Painter", "Mumbai, Maharashtra", "₹1,100", "02" },
            { "Industrial Painter", "Nashik, Maharashtra", "₹1,200", "03" },
            { "Spray Painter", "Bangalore, Karnataka", "₹1,150", "04" },
            { "Wood Polish Painter", "New Delhi, Delhi", "₹1,300", "05" },
            { "Exterior Painter", "Chennai, Tamil Nadu", "₹1,000", "06" } };
    private ImageView slide;
    private int index;

    private java.util.List<String[]> getAllJobs() {
        java.util.List<String[]> all = new java.util.ArrayList<>();
        try {
            java.util.List<com.dihadi.model.WorkforceRequirement> reqs = new com.dihadi.controller.WorkforceRequirementController().getAllRequirements();
            java.util.List<com.dihadi.model.Project> projects = new com.dihadi.controller.ProjectController().getAllProjects();
            java.util.Map<String, String> projectLocations = new java.util.HashMap<>();
            if (projects != null) {
                for (com.dihadi.model.Project p : projects) {
                    String loc = (p.getCity() != null && !p.getCity().isBlank() ? p.getCity() : "Pune") + ", " +
                                 (p.getState() != null && !p.getState().isBlank() ? p.getState() : "Maharashtra");
                    if (p.getProjectId() != null) projectLocations.put(p.getProjectId(), loc);
                    if (p.getMobile() != null) projectLocations.put(p.getMobile(), loc);
                }
            }
            if (reqs != null) {
                int imgIdx = 1;
                for (com.dihadi.model.WorkforceRequirement req : reqs) {
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("painter")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Painter";
                        String loc = req.getProjectId() != null && projectLocations.containsKey(req.getProjectId()) 
                                    ? projectLocations.get(req.getProjectId()) : "Pune, Maharashtra";
                        String wage = "₹" + String.format("%,d", (long)req.getDailyWages());
                        String imgNum = String.format("%02d", (imgIdx % 6) + 1);
                        imgIdx++;
                        all.add(new String[]{ title, loc, wage, imgNum });
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

    public Scene getPainterJobRoleScene(Runnable back) {
        Label eye = l("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                title = l("Painter Job Roles",
                        "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                quote = l("“Every finish tells a story. Find work that values your colour, care, and craftsmanship.”",
                        "-fx-font-family:'Georgia';-fx-font-size:19px;-fx-font-style:italic;-fx-text-fill:#4d4635;");
        quote.setWrapText(true);
        quote.setMaxWidth(390);
        VBox words = new VBox(14, eye, title, quote);
        words.setAlignment(Pos.CENTER);
        StackPane slider = slider();
        HBox heroRow = new HBox(34, slider, words);
        heroRow.setAlignment(Pos.CENTER);
        VBox hero = new VBox(heroRow);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(28));
        hero.setStyle(card());
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
        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1100);

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
        Button prev = o("← Back to skills");
        prev.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        BorderPane page = new BorderPane(scroll);
        page.setBottom(prev);
        BorderPane.setMargin(prev, new Insets(14, 60, 14, 60));
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private StackPane slider() {
        slide = img("/assets/images/worker/painter/skill-00.jpg", 600, 300);
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
        ImageView im = img("/assets/images/worker/painter/skill-" + j[3] + ".jpg", 316, 178);
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
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(j[0], j[1], j[2], "/assets/images/worker/painter/skill-01.jpg").getScene(() -> stage.setScene(currentScene))); 
        });
        VBox v = new VBox(13, im, n, loc, w, a);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(14));
        v.setPrefSize(344, 350);
        v.setStyle(card());
        return v;
    }

    private ImageView img(String path, double w, double h) {
        ImageView v = new ImageView(load(path));
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
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-padding:9px 18px;");
        return b;
    }
}
