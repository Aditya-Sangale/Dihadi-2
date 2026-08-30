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
            { "Skyline Heights Project", "Pune, Maharashtra", "₹1,000", "01", null, null, null, "Brick Mason" },
            { "Metro Line Corridor", "Mumbai, Maharashtra", "₹1,200", "02", null, null, null, "Stone Mason" },
            { "Greenfield Commercial Hub", "Nashik, Maharashtra", "₹1,150", "03", null, null, null, "Concrete Finisher" },
            { "Prestige Tech Park", "Bangalore, Karnataka", "₹1,100", "04", null, null, null, "Tilesetter" },
            { "Capital Expressway Site", "New Delhi, Delhi", "₹1,300", "05", null, null, null, "Refractory Mason" },
            { "Coastal Urban Residency", "Chennai, Tamil Nadu", "₹1,050", "06", null, null, null, "Terrazzo Worker" }
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
                        String imgNum = (photoUrl != null && !photoUrl.isBlank()) ? photoUrl : String.format("%02d", (imgIdx % 10) + 1);
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

    private void renderJobs(FlowPane grid, List<String[]> jobsList, String state, String city, String skill) {
        grid.getChildren().clear();
        for (String[] j : jobsList) {
            String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];
            String searchable = (j[0] + " " + j[1] + " " + roleTitle).toLowerCase();
            boolean stateMatches = state == null || state.startsWith("All") || searchable.contains(state.toLowerCase());
            boolean cityMatches = city == null || city.startsWith("All") || searchable.contains(city.toLowerCase());
            boolean skillMatches = skill == null || skill.startsWith("All") || searchable.contains(skill.toLowerCase()) || roleTitle.toLowerCase().contains(skill.toLowerCase());
            if (stateMatches && cityMatches && skillMatches) {
                grid.getChildren().add(jobCard(j));
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
        Label subtitle = label(
                "Building enduring foundations with skilled brick, stone, and tile artisans across major hubs.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        VBox heroMeta = new VBox(12, eyebrow, title, subtitle);
        heroMeta.setAlignment(Pos.CENTER);
        heroMeta.setPadding(new Insets(32, 36, 30, 36));
        heroMeta.setMaxWidth(1140);
        heroMeta.setStyle(cardStyle("#fff8f0"));

        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1100);

        List<String[]> allJobs = getAllJobs();
        renderJobs(grid, allJobs, null, null, null);

        VBox content = new VBox(28, heroMeta, sliderBox(), filterBox(grid, allJobs),
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

        Button backBtn = outline("← Back to categories");
        backBtn.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        HBox bottom = new HBox(backBtn);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(14, 60, 14, 60));
        bottom.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");

        BorderPane page = new BorderPane(scroll);
        page.setBottom(bottom);
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private StackPane sliderBox() {
        slide = image("/assets/images/worker/mason/skill-01.jpg", 600, 300);
        StackPane visual = new StackPane(slide);
        visual.setPrefSize(600, 300);
        visual.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            slideIndex = (slideIndex % 10) + 1;
            slide.setImage(load(String.format("/assets/images/worker/mason/skill-%02d.jpg", slideIndex)));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        return visual;
    }

    private HBox filterBox(FlowPane grid, List<String[]> allJobs) {
        ComboBox<String> stateFilter = choice("All States", "Maharashtra", "Karnataka", "Delhi", "Tamil Nadu");
        ComboBox<String> cityFilter = choice("All Cities", "Pune", "Mumbai", "Nashik", "Bangalore", "New Delhi", "Chennai");
        ComboBox<String> skillFilter = choice("All Skills", "Brick Mason", "Stone Mason", "Concrete Finisher", "Tilesetter", "Refractory Mason", "Terrazzo Worker");
        stateFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        cityFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        skillFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        HBox box = new HBox(15, stateFilter, cityFilter, skillFilter);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox jobCard(String[] j) {
        String imgPath = j[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = String.format("/assets/images/worker/mason/skill-%02d.jpg", Integer.parseInt(imgPath));
        }
        ImageView picture = image(imgPath, 316, 178);
        String projectName = j[0];
        String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];
        
        Label name = label(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label role = label("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label location = label("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
        Label wage = label("Daily wage  " + j[2], "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(Double.MAX_VALUE);
        role.setAlignment(Pos.CENTER);
        role.setMaxWidth(Double.MAX_VALUE);
        location.setAlignment(Pos.CENTER);
        location.setMaxWidth(Double.MAX_VALUE);
        
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

        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/mason/skill-01.jpg";
        apply.setOnAction(e -> { 
            javafx.stage.Stage stage = (javafx.stage.Stage) apply.getScene().getWindow(); 
            javafx.scene.Scene currentScene = apply.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, j[1], j[2], detailImg, j[4], j[5], j[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene)); 
        });
        VBox card = new VBox(10, picture, name, role, location, wage, apply);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14));
        card.setPrefSize(344, 380);
        card.setStyle(cardStyle("#ffffff"));
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
        ImageView view = new ImageView();
        Image img = load(path);
        if (img == null) {
            img = load("/assets/images/worker/mason/skill-01.jpg");
        }
        view.setImage(img);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        view.setClip(clip);
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
