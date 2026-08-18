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

    public Scene getPlumberJobRoleScene(Runnable back) {
        Label badge = l("DIHADI WORK MARKETPLACE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.4px;-fx-text-fill:#735c00;"),
                t = l("Plumber Job Roles",
                        "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;"),
                q = l("“Every reliable waterline begins with a skilled hand and careful craft.”",
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
        ComboBox<String> state = c("Select state", "Maharashtra", "Karnataka", "Tamil Nadu", "Delhi"),
                city = c("Select city", "Pune", "Mumbai", "Nashik", "Bangalore"),
                skill = c("Select plumbing skill", "Pipe fitting", "Sanitary", "Drainage", "Waterline");
        HBox controls = new HBox(12, state, city, skill, o("Clear filters"), p("Find roles"));
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
        for (String[] j : J)
            grid.getChildren().add(job(j));
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
        Button backButton = o("← Back to skills");
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
        a.setOnAction(event -> {
            a.setText("Applied ✓");
            a.setDisable(true);
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
