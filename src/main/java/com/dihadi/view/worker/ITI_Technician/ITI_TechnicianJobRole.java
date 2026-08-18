package com.dihadi.view.worker.ITI_Technician;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/** ITI/Technician job marketplace opened after Save & Continue. */
public class ITI_TechnicianJobRole {
        private static final String[][] J = { { "Pump Operator", "Pune, Maharashtra", "₹1,100", "01" },
                        { "Fitter Technician", "Nashik, Maharashtra", "₹1,250", "03" },
                        { "Belt Jointer", "Bangalore South, Karnataka", "₹1,300", "05" },
                        { "Lift & Escalator Mechanic", "New Delhi, Delhi", "₹1,500", "06" },
                        { "Electrical ITI Technician", "Mumbai, Maharashtra", "₹1,400", "08" },
                        { "Woodwork Technician", "Chennai, Tamil Nadu", "₹1,200", "04" } };

        public Scene getItiTechnicianJobRoleScene(Runnable back) {
                Label e = l("DIHADI WORK MARKETPLACE",
                                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                                t = l("ITI / Technician Job Roles",
                                                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                                i = l("Explore verified technical opportunities and apply directly through DIHADI.",
                                                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
                VBox hero = new VBox(10, e, t, i);
                hero.setAlignment(Pos.CENTER);
                hero.setPadding(new Insets(28));
                hero.setMaxWidth(1140);
                hero.setStyle(style());
                ComboBox<String> state = c("Select state", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi"),
                                city = c("Select city", "Pune", "Nashik", "Bangalore South", "New Delhi"),
                                skill = c("Select technical skill", "Fitter", "Pump Operator", "Electrical",
                                                "Mechanic");
                HBox controls = new HBox(12, state, city, skill, o("Clear filters"), p("Find roles"));
                controls.setAlignment(Pos.CENTER);
                VBox filter = new VBox(14,
                                l("Find a suitable job role for you",
                                                "-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                                controls);
                filter.setAlignment(Pos.CENTER);
                filter.setPadding(new Insets(22));
                filter.setMaxWidth(1140);
                filter.setStyle(
                                "-fx-background-color:#faf3e8;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-radius:22px;");
                FlowPane grid = new FlowPane(24, 24);
                grid.setAlignment(Pos.CENTER);
                grid.setPrefWrapLength(1100);
                for (String[] x : J)
                        grid.getChildren().add(card(x));
                VBox content = new VBox(28, hero, filter,
                                l("Available opportunities",
                                                "-fx-font-family:'Georgia';-fx-font-size:29px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                                grid);
                content.setAlignment(Pos.TOP_CENTER);
                content.setPadding(new Insets(30, 36, 42, 36));
                StackPane canvas = new StackPane(content);
                canvas.setAlignment(Pos.TOP_CENTER);
                canvas.setStyle("-fx-background-color:#f3e7ce;");
                ScrollPane scroll = new ScrollPane(canvas);
                scroll.setFitToWidth(true);
                scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
                Button prev = o("← Back to skills");
                prev.setOnAction(x -> {
                        if (back != null)
                                back.run();
                });
                BorderPane page = new BorderPane(scroll);
                page.setBottom(new HBox(prev));
                BorderPane.setMargin(prev, new Insets(14, 60, 14, 60));
                page.setStyle("-fx-background-color:#f3e7ce;");
                return new Scene(page, 1400, 780);
        }

        private VBox card(String[] x) {
                var r = getClass().getResource("/assets/images/worker/iti/skill-" + x[3] + ".jpg");
                ImageView im = new ImageView(r == null ? null : new Image(r.toExternalForm()));
                im.setFitWidth(316);
                im.setFitHeight(178);
                im.setPreserveRatio(false);
                Rectangle clip = new Rectangle(316, 178);
                clip.setArcWidth(24);
                clip.setArcHeight(24);
                im.setClip(clip);
                Label n = l(x[0], "-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                                loc = l("⌖  " + x[1], "-fx-font-size:13px;-fx-text-fill:#4d4635;"),
                                w = l("Daily wage  " + x[2],
                                                "-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#735c00;");
                n.setAlignment(Pos.CENTER);
                n.setMaxWidth(Double.MAX_VALUE);
                Button a = p("Apply now");
                a.setMaxWidth(Double.MAX_VALUE);
                a.setOnAction(e -> {
                        a.setText("Applied ✓");
                        a.setDisable(true);
                });
                VBox v = new VBox(13, im, n, loc, w, a);
                v.setAlignment(Pos.CENTER);
                v.setPadding(new Insets(14));
                v.setPrefSize(344, 350);
                v.setStyle(style());
                return v;
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
}
