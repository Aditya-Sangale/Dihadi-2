package com.dihadi.view.worker.Plumber;

import javafx.stage.Stage;

import java.util.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

/** Plumber work-skill selection screen. */
public class PlumberPage {
    private static final String[] N = { "Basin & Sink", "Bath Fixing", "Blockage", "Water Tank", "Bathroom Fitting",
            "Submersible / Motor", "Pipe Fittings", "Testing & Tapping", "Water Tank Cleaning", "Plumber Helper",
            "GI Pipe Installation", "Gas Meter & Valve", "Copper Pipe Install", "LPG To PNG Conv",
            "Pressure & Leak Test", "Fire Safety Tech" };
    private static final String[] H = { "सिंक और बेसिन", "बाथ फिक्स", "ब्लॉकेज", "पानी टंकी", "बाथरूम फिटिंग",
            "सबमर्सिबल/मोटर", "पाइप फिटिंग", "वॉटर लाइन", "पानी टंकी सफाई", "हेल्पर", "जीआई पाइप फिटिंग",
            "गैस मीटर और वाल्व", "कॉपर पाइप फिटिंग", "एलपीजी से पीएनजी", "प्रेशर और लीकेज टेस्ट",
            "फायर सेफ्टी टेक्नीशियन" };
    private final Set<Integer> s = new HashSet<>();

    public Scene getPlumberScene(Runnable back, Runnable home, Runnable about) {
        VBox c = new VBox(36, hero(), body());
        c.setPrefWidth(1260);
        c.setMaxWidth(1260);
        c.setAlignment(Pos.TOP_CENTER);
        StackPane sc = new StackPane(c);
        sc.setAlignment(Pos.TOP_CENTER);
        sc.setPadding(new Insets(24, 24, 118, 24));
        ScrollPane sp = new ScrollPane(sc);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane p = new BorderPane(sp);
        p.setTop(head(back, home, about));
        p.setBottom(actions(back));
        p.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(p, 1400, 780);
    }

    private HBox hero() {
        Label q = l(
                "\"Every drop of water carries the promise of life—\na skilled plumber keeps that promise flowing.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        VBox v = new VBox(q);
        v.setAlignment(Pos.CENTER_LEFT);
        v.setPrefSize(590, 310);
        v.setPadding(new Insets(24, 30, 24, 30));
        v.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        ImageView i = im("/assets/images/worker/plumber/skill-00.jpg", 385, 320);
        round(i, 385, 320);
        HBox h = new HBox(45, v, i);
        h.setAlignment(Pos.CENTER);
        h.setPrefWidth(1260);
        h.setMaxWidth(1260);
        return h;
    }

    private VBox body() {
        Label t = l("Plumber Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label x = l("You can select more than one sub-skill based on your expertise.",
                "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        VBox heading = new VBox(8, t, x);
        heading.setMaxWidth(1180);
        FlowPane g = new FlowPane(20, 20);
        g.setAlignment(Pos.CENTER);
        g.setPrefWrapLength(1180);
        g.setMaxWidth(1180);
        for (int i = 0; i < N.length; i++)
            g.getChildren().add(card(i));
        VBox body = new VBox(26, heading, g);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private Button card(int k) {
        ImageView i = im(String.format("/assets/images/worker/plumber/skill-%02d.jpg", k + 1), 280, 150);
        Label n = l(N[k], "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        n.setWrapText(true);
        n.setAlignment(Pos.CENTER);
        n.setPrefSize(250, 32);
        Label h = l(H[k], "-fx-font-size:13px;-fx-text-fill:#6b6255;-fx-alignment:center;");
        h.setWrapText(true);
        h.setAlignment(Pos.CENTER);
        h.setPrefSize(250, 30);
        VBox text = new VBox(4, n, h);
        text.setAlignment(Pos.CENTER);
        text.setPrefHeight(80);
        VBox v = new VBox(i, text);
        v.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(v);
        b.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        b.setMinSize(280, 230);
        b.setPrefSize(280, 230);
        b.setMaxSize(280, 230);
        b.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-cursor:hand;");
        b.setOnAction(e -> {
            if (s.remove(k))
                b.setStyle(
                        "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;");
            else {
                s.add(k);
                b.setStyle(
                        "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:20px;");
            }
        });
        return b;
    }

    private BorderPane head(Runnable back, Runnable home, Runnable about) {
        ImageView i = im("/assets/logo/dihadi logo.jpeg", 54, 54);
        i.setViewport(new Rectangle2D(380, 0, 840, 840));
        i.setPreserveRatio(true);
        HBox brand = new HBox(10, i,
                l("DIHADI", "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        Button h = b("Home");
        h.setOnAction(e -> {
            if (home != null)
                home.run();
        });
        Button w = b("Worker");
        w.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-border-color:#735c00;-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        w.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button a = b("About Us");
        a.setOnAction(e -> {
            if (about != null)
                about.run();
        });
        HBox navigation = new HBox(20, h, b("Business"), w, b("Recruiter"), a, b("Contact Us"));
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);
        Button login = o("Login"), signUp = pr("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((Stage) signUp.getScene().getWindow(), () -> com.dihadi.view.AppNavigator.open((Stage) signUp.getScene().getWindow(), "Worker")));
        login.setMouseTransparent(true); signUp.setMouseTransparent(true);
        HBox account = new HBox(12, login, signUp);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane p = new BorderPane();
        p.setLeft(brand);
        p.setCenter(navigation);
        p.setRight(account);
        p.setPadding(new Insets(16, 24, 14, 24));
        p.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return p;
    }

    private HBox actions(Runnable back) {
        Button x = o("←  BACK");
        x.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Button n = pr("SAVE & CONTINUE");
        n.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) n.getScene().getWindow();
            stage.setScene(new PlumberJobRole().getPlumberJobRoleScene(back));
        });
        HBox bar = new HBox(x, r, n);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:rgba(255,248,240,.96);-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private Button b(String t) {
        Button x = new Button(t);
        x.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4d4635;-fx-border-color:transparent;-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return x;
    }

    private Button pr(String t) {
        Button x = new Button(t);
        x.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        return x;
    }

    private Button o(String t) {
        Button x = new Button(t);
        x.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return x;
    }

    private ImageView im(String p, double w, double h) {
        ImageView x = new ImageView(new Image(getClass().getResource(p).toExternalForm()));
        x.setFitWidth(w);
        x.setFitHeight(h);
        x.setPreserveRatio(false);
        return x;
    }

    private void round(ImageView x, double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setArcWidth(40);
        r.setArcHeight(40);
        x.setClip(r);
    }

    private Label l(String t, String s) {
        Label x = new Label(t);
        x.setStyle(s);
        return x;
    }
}
