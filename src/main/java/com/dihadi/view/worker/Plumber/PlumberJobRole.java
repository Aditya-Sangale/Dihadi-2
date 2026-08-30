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
    private static final String[][] J = {
            { "Lodha Crown Pipeline Network", "Pune, Maharashtra", "₹950", "01", null, null, null, "Residential Plumber" },
            { "Oberoi Sky City Water Mains", "Mumbai, Maharashtra", "₹1,100", "03", null, null, null, "Pipe Fitting Plumber" },
            { "Nashik Smart City Drainage", "Nashik, Maharashtra", "₹1,050", "04", null, null, null, "Waterline Technician" },
            { "Brigade Tech Gardens Sanitary", "Bangalore, Karnataka", "₹1,150", "05", null, null, null, "Sanitary Plumber" },
            { "NTPC Plant High Pressure Lines", "New Delhi, Delhi", "₹1,300", "07", null, null, null, "Industrial Plumber" },
            { "Chennai Port Storm Water System", "Chennai, Tamil Nadu", "₹1,000", "09", null, null, null, "Drainage Specialist" },
            { "Hitec City Luxury Residency", "Hyderabad, Telangana", "₹1,100", "11", null, null, null, "Bathroom Fitter" },
            { "Bhiwandi Warehousing Sewerage", "Bhiwandi, Maharashtra", "₹800", "13", null, null, null, "Plumbing Helper" }
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
        for (String[] j : J) {
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

    public Scene getPlumberJobRoleScene(Runnable back) {
        Label eyebrow = l("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;");
        Label title = l("Plumber Job Roles",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label subtitle = l(
                "Ensuring high-efficiency water, drainage, and industrial fluid systems across modern infrastructures.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        VBox heroMeta = new VBox(12, eyebrow, title, subtitle);
        heroMeta.setAlignment(Pos.CENTER);
        heroMeta.setPadding(new Insets(32, 36, 30, 36));
        heroMeta.setMaxWidth(1140);
        heroMeta.setStyle(style("#fff8f0"));

        FlowPane grid = new FlowPane(24, 24);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1100);

        java.util.List<String[]> allJobs = getAllJobs();
        renderJobs(grid, allJobs, null, null, null);

        VBox content = new VBox(28, heroMeta, slider(), filterBox(grid, allJobs),
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

        Button backBtn = o("← Back to categories");
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

    private StackPane slider() {
        slide = img("/assets/images/worker/plumber/skill-01.jpg", 600, 300);
        StackPane s = new StackPane(slide);
        s.setPrefSize(600, 300);
        s.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            index = (index % 12) + 1;
            slide.setImage(load(String.format("/assets/images/worker/plumber/skill-%02d.jpg", index)));
        }));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        return s;
    }

    private HBox filterBox(FlowPane grid, java.util.List<String[]> allJobs) {
        ComboBox<String> stateFilter = c("All States", "Maharashtra", "Karnataka", "Delhi", "Tamil Nadu");
        ComboBox<String> cityFilter = c("All Cities", "Pune", "Mumbai", "Nashik", "Bangalore", "New Delhi", "Chennai");
        ComboBox<String> skillFilter = c("All Skills", "Commercial Plumber", "Residential Plumber", "Pipefitter",
                "Drain Technician", "Steamfitter");
        stateFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        cityFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        skillFilter.setOnAction(e -> renderJobs(grid, allJobs, stateFilter.getValue(), cityFilter.getValue(), skillFilter.getValue()));
        HBox box = new HBox(15, stateFilter, cityFilter, skillFilter);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox job(String[] j) {
        String imgPath = j[3];
        if (imgPath != null && imgPath.matches("\\d+")) {
            imgPath = "/assets/images/worker/plumber/skill-" + j[3] + ".jpg";
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
        n.setMaxWidth(Double.MAX_VALUE);
        role.setAlignment(Pos.CENTER);
        role.setMaxWidth(Double.MAX_VALUE);
        loc.setAlignment(Pos.CENTER);
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

        final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/plumber/skill-01.jpg";
        a.setOnAction(e -> { 
            javafx.stage.Stage stage = (javafx.stage.Stage) a.getScene().getWindow(); 
            javafx.scene.Scene currentScene = a.getScene();
            stage.setScene(new com.dihadi.view.worker.SiteDetailsCardPage(roleTitle, j[1], j[2], detailImg, j[4], j[5], j[6]).getScene(() -> {
                checkAppliedStatus.run();
                stage.setScene(currentScene);
            }, currentScene)); 
        });
        VBox v = new VBox(10, im, n, role, loc, w, a);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(14));
        v.setPrefSize(344, 380);
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
        ImageView v = new ImageView();
        Image img = load(p);
        if (img == null) {
            img = load("/assets/images/worker/plumber/skill-01.jpg");
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

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
