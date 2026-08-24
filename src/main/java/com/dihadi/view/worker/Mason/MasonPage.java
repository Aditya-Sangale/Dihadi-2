package com.dihadi.view.worker.Mason;

import java.util.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/** Mason trade selection screen. */
public class MasonPage {
    private static final String[] NAMES = { "Brick Mason", "Lanter Mason", "Plastering Mason", "Stone Mason",
            "Tiles Mason", "Cement Concrete Mason", "Bar Bender", "Shuttering", "Marbles Mason", "Flooring",
            "Composite Mason", "Garters/Columns Mason", "Lime Concrete Mason", "Scaffolding", "Reinforcement Fitter" };
    private static final String[] HINDI = { "ईंट मिस्त्री", "लेंटर मिस्त्री", "प्लास्टर मिस्त्री", "पत्थर मिस्त्री",
            "टाइल्स मिस्त्री", "सीमेंट कंक्रीट मिस्त्री", "सरिया बांधने वाला", "शटरिंग", "मार्बल मिस्त्री", "फ्लोरिंग",
            "कंपोजिट मिस्त्री", "गर्डर/कॉलम मिस्त्री", "चूना कंक्रीट मिस्त्री", "मचान बांधने वाला",
            "रीइन्फोर्समेंट फिटर" };
    private final Set<Integer> selected = new LinkedHashSet<>();

    public Scene getMasonScene(Runnable back, Runnable home, Runnable about) {
        VBox c = new VBox(38, hero(), content());
        c.setPrefWidth(1260);
        c.setMaxWidth(1260);
        StackPane s = new StackPane(c);
        s.setAlignment(Pos.TOP_CENTER);
        s.setPadding(new Insets(24, 24, 118, 24));
        ScrollPane sp = new ScrollPane(s);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane p = new BorderPane(sp);
        p.setTop(header(back, home, about));
        p.setBottom(actions(back));
        p.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(p, 1400, 780);
    }

    private HBox hero() {
        Label q = label(
                "\"The art of building tomorrow rests\nin the skilled hands and enduring\nspirit of today's craftsmen.\"",
                "-fx-font-family:'Georgia';-fx-font-size:22px;-fx-font-style:italic;-fx-text-fill:#3a3027;-fx-line-spacing:6px;");
        VBox words = new VBox(q);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefSize(590, 310);
        words.setPadding(new Insets(25, 35, 25, 30));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        ImageView im = image("/assets/images/worker/mason/hero.jpg", 500, 310);
        round(im, 500, 310);
        HBox h = new HBox(42, words, new StackPane(im));
        h.setAlignment(Pos.CENTER);
        h.setPrefWidth(1260);
        return h;
    }

    private VBox content() {
        Label t = label("Mason Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label sub = label("You can select more than one sub-skill based on your expertise.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane g = new FlowPane(20, 20);
        g.setPrefWrapLength(1260);
        for (int i = 0; i < NAMES.length; i++)
            g.getChildren().add(card(i));
        return new VBox(26, new VBox(8, t, sub), g);
    }

    private Button card(int i) {
        String path = String.format("/assets/images/worker/mason/skill-%02d.jpg", i + 1);
        ImageView im = image(path, 232, 145);
        Label n = label(NAMES[i], "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        n.setWrapText(true);
        n.setPrefSize(210, 32);
        n.setAlignment(Pos.CENTER);
        Label hi = label(HINDI[i], "-fx-font-size:13px;-fx-text-fill:#6b6255;-fx-alignment:center;");
        hi.setWrapText(true);
        hi.setPrefSize(210, 30);
        hi.setAlignment(Pos.CENTER);
        VBox v = new VBox(im, new VBox(3, n, hi));
        v.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(v);
        b.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        b.setPadding(Insets.EMPTY);
        b.setPrefSize(232, 221);
        b.setMinSize(232, 221);
        b.setMaxSize(232, 221);
        style(b, false);
        b.setOnAction(e -> {
            boolean on = selected.contains(i);
            if (on)
                selected.remove(i);
            else
                selected.add(i);
            style(b, !on);
        });
        return b;
    }

    private BorderPane header(Runnable back, Runnable home, Runnable about) {
        ImageView l = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        l.setViewport(new Rectangle2D(380, 0, 840, 840));
        l.setPreserveRatio(true);
        HBox brand = new HBox(10, l, label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        Button h = nav("Home", false);
        h.setOnAction(e -> {
            if (home != null)
                home.run();
        });
        Button w = nav("Worker", true);
        w.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button a = nav("About Us", false);
        a.setOnAction(e -> {
            if (about != null)
                about.run();
        });
        HBox nav = new HBox(20, h, nav("Business", false), w, nav("Recruiter", false), a, nav("Contact Us", false));
        nav.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(nav);
        Button login = outline("Login"), signUp = primary("Sign Up");
        login.setDisable(true);
        signUp.setDisable(true);
        BorderPane p = new BorderPane();
        p.setLeft(brand);
        p.setCenter(nav);
        p.setRight(new HBox(12, login, signUp));
        p.setPadding(new Insets(16, 24, 14, 24));
        p.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return p;
    }

    private HBox actions(Runnable back) {
        Button b = outline("←  BACK");
        b.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button n = primary("SAVE & CONTINUE");
        n.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) n.getScene().getWindow();
            stage.setScene(new MasonJobRole().getMasonJobRoleScene(back));
        });
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        HBox h = new HBox(b, r, n);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(16, 70, 16, 70));
        h.setStyle("-fx-background-color:rgba(255,248,240,.96);-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return h;
    }

    private void save() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Mason Skills");
        a.setHeaderText(null);
        a.setContentText(selected.isEmpty() ? "Select at least one sub-skill to continue."
                : selected.size() + " skills have been selected.");
        a.show();
    }

    private void style(Button b, boolean on) {
        b.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-radius:20px;-fx-border-width:2px;-fx-border-color:"
                        + (on ? "#d4af37" : "#d0c5af")
                        + ";-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);-fx-cursor:hand;");
    }

    private Button nav(String t, boolean on) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (on ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (on ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;");
        return b;
    }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-weight:800;-fx-padding:11px 24px;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-weight:700;-fx-padding:10px 23px;");
        return b;
    }

    private ImageView image(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private void round(ImageView i, double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(40);
        r.setArcHeight(40);
        i.setClip(r);
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle(s);
        return l;
    }
}
