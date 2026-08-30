package com.dihadi.view.worker.Painter;

import javafx.stage.Stage;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class PainterPage {
    private static final String[] N = { "Enamel Painting", "Roller Painting", "POP Work", "Wall Putty", "Stencil Work",
            "Texture Painting", "Waterproofing", "Wood Polish" };

    public Scene getPainterScene(Runnable back) {
        VBox c = new VBox(38, hero(), body());
        c.setPrefWidth(1260);
        c.setMaxWidth(1260);
        c.setAlignment(Pos.TOP_CENTER);
        StackPane root = new StackPane(c);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(24, 24, 118, 24));
        ScrollPane s = new ScrollPane(root);
        s.setFitToWidth(true);
        s.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane p = new BorderPane(s);
        p.setTop(head(back));
        p.setBottom(footer(back));
        p.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(p, 1400, 780);
    }

    private HBox hero() {
        Label q = new Label("\"Colour brings spaces to life—\nevery careful stroke reflects craft and care.\"");
        q.setText("\"Colour brings spaces to life—\nevery careful stroke reflects craft and care.\"");
        q.setStyle(
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        VBox v = new VBox(q);
        v.setAlignment(Pos.CENTER_LEFT);
        v.setPrefSize(590, 300);
        v.setPadding(new Insets(24, 30, 24, 30));
        v.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        return new HBox(42, v, img(0, 500, 300));
    }

    private VBox body() {
        Label t = new Label("Painter Work-Skills");
        t.setStyle("-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label x = new Label("You can select more than one sub-skill based on your expertise.");
        x.setStyle("-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane f = new FlowPane(20, 20);
        f.setAlignment(Pos.CENTER);
        f.setPrefWrapLength(1180);
        for (int i = 0; i < N.length; i++)
            f.getChildren().add(card(i));
        VBox b = new VBox(26, new VBox(8, t, x), f);
        b.setAlignment(Pos.TOP_CENTER);
        return b;
    }

    private Button card(int k) {
        ImageView i = img(k + 1, 280, 150);
        Label n = new Label(N[k]);
        n.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        n.setAlignment(Pos.CENTER);
        n.setPrefSize(250, 60);
        VBox v = new VBox(i, n);
        v.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(v);
        b.setPrefSize(280, 220);
        b.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;");
        return b;
    }

    private BorderPane head(Runnable back) {
        ImageView logo = new ImageView(
                new Image(getClass().getResource("/assets/logo/dihadi logo.jpeg").toExternalForm()));
        logo.setFitWidth(54);
        logo.setFitHeight(54);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        Label d = new Label("DIHADI");
        d.setStyle("-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        HBox brand = new HBox(10, logo, d);
        brand.setAlignment(Pos.CENTER_LEFT);

        Button worker = nav("Worker", true);
        worker.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        HBox navigation = new HBox(20, nav("Home", false), nav("Business", false), worker,
                nav("Recruiter", false), nav("About Us", false), nav("Contact Us", false));
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);
        Button login = outline("Login"), signUp = primary("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((Stage) signUp.getScene().getWindow(),
                () -> com.dihadi.view.AppNavigator.open((Stage) signUp.getScene().getWindow(), "Worker")));
        login.setMouseTransparent(true);
        signUp.setMouseTransparent(true);
        HBox account = new HBox(12, login, signUp);
        account.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(navigation);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return bar;
    }

    private HBox footer(Runnable back) {
        Button b = new Button("←  BACK");
        b.setText("\u2190  BACK");
        b.setStyle("-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;"
                + "-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;"
                + "-fx-padding:10px 23px;-fx-cursor:hand;");
        b.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Button n = new Button("SAVE & CONTINUE");
        n.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;"
                + "-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        n.setOnAction(e -> {
            Stage stage = (Stage) n.getScene().getWindow();
            stage.setScene(new PainterJobRole().getPainterJobRoleScene(back));
        });
        HBox h = new HBox(b, r, n);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(16, 70, 16, 70));
        h.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return h;
    }

    private ImageView img(int n, double w, double h) {
        ImageView i = new ImageView(new Image(getClass()
                .getResource(String.format("/assets/images/worker/painter/skill-%02d.jpg", n)).toExternalForm()));
        i.setFitWidth(w);
        i.setFitHeight(h);
        i.setPreserveRatio(false);
        return i;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;"
                + "-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;"
                + "-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;"
                + "-fx-padding:10px 23px;-fx-cursor:hand;");
        return b;
    }
}
