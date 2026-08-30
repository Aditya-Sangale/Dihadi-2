package com.dihadi.view.worker.ITI_Technician;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/** ITI/Technician job marketplace opened after Save & Continue. */
public class ITI_TechnicianJobRole {
        private static final String[][] JOBS = {
            { "Tata Motors Line Setup", "Pune, Maharashtra", "₹1,200", "01", null, null, null, "Fitter" },
            { "L&T Heavy Engineering Unit", "Mumbai, Maharashtra", "₹1,300", "02", null, null, null, "Machinist" },
            { "Mahindra Auto Assembly", "Nashik, Maharashtra", "₹1,150", "03", null, null, null, "Turner" },
            { "BHEL Turbine Assembly", "Bangalore, Karnataka", "₹1,250", "04", null, null, null, "Welder" },
            { "Delhi Transport Depot", "New Delhi, Delhi", "₹1,400", "05", null, null, null, "Mechanic Motor Vehicle" },
            { "Integral Coach Factory", "Chennai, Tamil Nadu", "₹1,350", "06", null, null, null, "Electrician (ITI)" }
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
                                        if (req.getWorkerType() != null && req.getWorkerType().toLowerCase().contains("iti")) {
                                                String title = req.getSubSkill() != null && !req.getSubSkill().isBlank() ? req.getSubSkill() : "ITI Technician";
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
                for (String[] x : JOBS) {
                        all.add(x);
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
                                grid.getChildren().add(card(j));
                        }
                }
                if (grid.getChildren().isEmpty()) {
                        grid.getChildren().add(l("No exact roles found matching your filter. Clear filters to view all roles.",
                                "-fx-font-size:15px;-fx-text-fill:#4d4635;"));
                }
        }

        public Scene getItiTechnicianJobRoleScene(Runnable back) {
                return getITI_TechnicianJobRoleScene(back);
        }

        public Scene getITI_TechnicianJobRoleScene(Runnable back) {
                Label eye = l("DIHADI WORK MARKETPLACE",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                                title = l("ITI Technician Job Roles",
                                                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                                quote = l("“Certified technical precision driving next-generation infrastructure across industrial and residential zones.”",
                                                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
                VBox heroMeta = new VBox(12, eye, title, quote);
                heroMeta.setAlignment(Pos.CENTER);
                heroMeta.setPadding(new Insets(32, 36, 30, 36));
                heroMeta.setMaxWidth(1140);
                heroMeta.setStyle(style());

                ComboBox<String> state = c("Select state", "All States", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi"),
                                city = c("Select city", "All Cities", "Pune", "Nashik", "Bangalore South", "New Delhi"),
                                skill = c("Select technical skill", "All Skills", "Fitter", "Pump Operator", "Electrical",
                                                "Mechanic", "Technician");
                Button clear = o("Clear filters"), find = p("Find roles");
                HBox controls = new HBox(12, state, city, skill, clear, find);
                controls.setAlignment(Pos.CENTER);
                VBox filter = new VBox(14, l("Find a suitable job role for you", "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"), controls);
                filter.setAlignment(Pos.CENTER);
                filter.setPadding(new Insets(22));
                filter.setMaxWidth(1140);
                filter.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");

                FlowPane grid = new FlowPane(24, 24);
                grid.setAlignment(Pos.CENTER);
                grid.setPrefWrapLength(1100);

                java.util.List<String[]> allJobs = getAllJobs();
                renderJobs(grid, allJobs, null, null, null);

                find.setOnAction(ev -> renderJobs(grid, allJobs, state.getValue(), city.getValue(), skill.getValue()));
                clear.setOnAction(ev -> {
                        state.getSelectionModel().selectFirst();
                        city.getSelectionModel().selectFirst();
                        skill.getSelectionModel().selectFirst();
                        renderJobs(grid, allJobs, null, null, null);
                });

                VBox content = new VBox(28, heroMeta, filter,
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
                Button prev = o("← Back to categories");
                prev.setOnAction(e -> {
                        if (back != null)
                                back.run();
                });
                HBox bottom = new HBox(prev);
                bottom.setAlignment(Pos.CENTER_LEFT);
                bottom.setPadding(new Insets(14, 60, 14, 60));
                bottom.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
                BorderPane page = new BorderPane(scroll);
                page.setBottom(bottom);
                page.setStyle("-fx-background-color:#f3e7ce;");
                return new Scene(page, 1400, 780);
        }

        private VBox card(String[] j) {
                String imgPath = j[3];
                if (imgPath != null && imgPath.matches("\\d+")) {
                    imgPath = "/assets/images/worker/iti/skill-" + j[3] + ".jpg";
                }
                ImageView im = image(imgPath, 316, 178);
                String projectName = j[0];
                String roleTitle = j.length > 7 && j[7] != null ? j[7] : j[0];

                Label n = l(projectName, "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
                Label role = l("Role: " + roleTitle, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#735c00;");
                Label loc = l("⌖  " + j[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;");
                Label w = l("Daily wage  " + j[2], "-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:#735c00;");
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

                final String detailImg = (imgPath != null && !imgPath.isBlank()) ? imgPath : "/assets/images/worker/iti/skill-01.jpg";
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
                v.setStyle(style());
                return v;
        }

        private ImageView image(String path, double width, double height) {
            ImageView view = new ImageView();
            Image img = load(path);
            if (img == null) {
                img = load("/assets/images/worker/iti/skill-01.jpg");
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

        private ComboBox<String> c(String... s) {
                ComboBox<String> b = new ComboBox<>();
                b.getItems().addAll(s);
                b.getSelectionModel().selectFirst();
                b.setPrefWidth(190);
                b.setStyle(
                                "-fx-background-color:#f3e7ce;-fx-border-color:#c6a15b;-fx-border-radius:12px;-fx-background-radius:12px;-fx-font-size:13px;");
                return b;
        }

        private Label l(String t, String s) {
                Label x = new Label(t);
                x.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
                return x;
        }

        private String style() {
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

    private String workerCardStyle(boolean active) {
        return "-fx-background-color:#ffffff;-fx-background-radius:13px;-fx-border-color:"
                + (active ? "#d4af37" : "transparent") + ";-fx-border-width:" + (active ? "2px" : "1px")
                + ";-fx-border-radius:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                + (active ? ".14" : ".06") + ")," + (active ? "17" : "8") + ",0,0," + (active ? "4" : "2") + "px);";
    }
}
