package com.dihadi.view.worker.Mason;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Mason opportunities marketplace with a professional DIHADI theme. */
public class MasonJobRole {
    private static final String[][] JOBS = {
            { "Brick Mason", "Pune, Maharashtra", "₹1,000", "01", null, null, null },
            { "Stone Mason", "Mumbai, Maharashtra", "₹1,200", "02", null, null, null },
            { "Concrete Finisher", "Nashik, Maharashtra", "₹1,150", "03", null, null, null },
            { "Tilesetter", "Bangalore, Karnataka", "₹1,100", "04", null, null, null },
            { "Refractory Mason", "New Delhi, Delhi", "₹1,300", "05", null, null, null },
            { "Terrazzo Worker", "Chennai, Tamil Nadu", "₹1,050", "06", null, null, null }
    };
    private ImageView slide;
    private int slideIndex;

    private List<String[]> getAllJobs() {
        List<String[]> all = new ArrayList<>();
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
                    if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("mason")) {
                        String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "Mason";
                        com.dihadi.model.Project p = req.getProjectId() != null ? projectMap.get(req.getProjectId()) : null;
                        String loc = (p != null && p.getCity() != null && !p.getCity().isBlank() ? p.getCity() : "Pune") + ", " +
                                     (p != null && p.getState() != null && !p.getState().isBlank() ? p.getState() : "Maharashtra");
                        String wage = "₹" + String.format("%,d", (long)req.getDailyWages());
                        String imgNum = String.format("%02d", (imgIdx % 10) + 1);
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

    private void renderJobs(FlowPane grid, List<String[]> jobsList, String state, String city, String skill) {
        grid.getChildren().clear();
        for (String[] job : jobsList) {
            String searchable = (job[0] + " " + job[1]).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches) {
                grid.getChildren().add(jobCard(job));
            }
        }
        if (grid.getChildren().isEmpty()) {
            grid.getChildren().add(label("No exact roles found matching your filter. Clear filters to view all roles.",
                    "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
        }
    }

    public Scene getMasonJobRoleScene(Runnable back) {
        Label eyebrow = label("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;");
        Label title = label("Mason Job Roles",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label quote = label(
                "“The art of building tomorrow rests in the skilled hands and enduring spirit of today’s masons.”",
                "-fx-font-family:'Georgia';-fx-font-size:20px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setMaxWidth(390);
        VBox words = new VBox(15, eyebrow, title, quote);
        words.setAlignment(Pos.CENTER);
        words.setPrefWidth(410);
        StackPane imageBox = slider();
        HBox heroRow = new HBox(34, imageBox, words);
        heroRow.setAlignment(Pos.CENTER);
        VBox hero = new VBox(heroRow);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(28));
        hero.setStyle(cardStyle("#fff8f0"));
        hero.setMaxWidth(1200);

        ComboBox<String> state = choice("Select state", "All States", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi", "Haryana");
        ComboBox<String> city = choice("Select city", "All Cities", "Pune", "Mumbai", "Nashik", "Bangalore", "New Delhi");
        ComboBox<String> skill = choice("Select mason skill", "All Skills", "Brick", "Plaster", "Stone", "Tiles",
                "Shuttering", "Concrete", "Labour");
        Button clear = outline("Clear filters");
        Button find = primary("Find roles");
        HBox controls = new HBox(12, state, city, skill, clear, find);
        controls.setAlignment(Pos.CENTER);
        VBox suitable = new VBox(14, label("Find a suitable job role for you",
                "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"), controls);
        suitable.setAlignment(Pos.CENTER);
        suitable.setPadding(new Insets(22, 24, 22, 24));
        suitable.setMaxWidth(1200);
        suitable.setStyle(cardStyle("#faf3e8"));

        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1140);
        List<String[]> allJobs = getAllJobs();
        renderJobs(grid, allJobs, null, null, null);

        find.setOnAction(e -> renderJobs(grid, allJobs, state.getValue(), city.getValue(), skill.getValue()));
        clear.setOnAction(e -> {
            state.getSelectionModel().selectFirst();
            city.getSelectionModel().selectFirst();
            skill.getSelectionModel().selectFirst();
            renderJobs(grid, allJobs, null, null, null);
        });

        VBox content = new VBox(28, hero, suitable,
                label("Available opportunities",
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
        Button backButton = outline("← Back to skills");
        backButton.setOnAction(e -> {
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
        slide = image("/assets/images/worker/mason/hero.jpg", 640, 320);
        StackPane box = new StackPane(slide);
        box.setPrefSize(640, 320);
        box.setStyle(cardStyle("#faf3e8"));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            slideIndex = (slideIndex + 1) % 8;
            slide.setImage(load(String.format("/assets/images/worker/mason/skill-%02d.jpg", slideIndex + 1)));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        return box;
    }

    private VBox jobCard(String[] j) {
        ImageView picture = image(String.format("/assets/images/worker/mason/skill-%02d.jpg", Integer.parseInt(j[3])),
                316, 178);
        Label name = label(j[0], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                location = label("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                wage = label("Daily wage  " + j[2], "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        location.setAlignment(Pos.CENTER);
        location.setMaxWidth(Double.MAX_VALUE);
        Button apply = primary("Apply now");
        apply.setMaxWidth(Double.MAX_VALUE);
        apply.setOnAction(e -> { 
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow(); 
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(j[0], j[1], j[2], "/assets/images/worker/mason/skill-01.jpg", j[4], j[5], j[6]).getScene(() -> stage.setScene(currentScene), currentScene)); 
        });
        VBox card = new VBox(13, picture, name, location, wage, apply);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(344, 350);
        card.setPadding(new Insets(14));
        card.setStyle(cardStyle("#fff8f0"));
        return card;
    }

    private ComboBox<String> choice(String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.getSelectionModel().selectFirst();
        box.setPrefWidth(190);
        box.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;-fx-background-radius:12px;-fx-font-size:13px;");
        return box;
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        view.setClip(clip);
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

    private String cardStyle(String color) {
        return "-fx-background-color:" + color
                + ";-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);";
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
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return button;
    }

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
