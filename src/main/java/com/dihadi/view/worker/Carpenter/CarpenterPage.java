package com.dihadi.view.worker.Carpenter;

import javafx.stage.Stage;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class CarpenterPage {
    private static final String[] N = { "Furniture Carpenter", "Door & Window Carpenter", "Modular Furniture",
            "Shuttering Carpenter", "Wood Polishing", "Kitchen Carpenter", "False Ceiling", "Flooring Carpenter",
            "Carpenter Helper", "Wood Carving", "Plywood Work", "Other Woodwork" };

    public Scene getCarpenterScene(Runnable back) {
        VBox c = new VBox(38, hero(), grid());
        c.setAlignment(Pos.TOP_CENTER);
        c.setPrefWidth(1260);
        c.setMaxWidth(1260);
        StackPane root = new StackPane(c);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(24, 24, 118, 24));
        ScrollPane s = new ScrollPane(root);
        s.setFitToWidth(true);
        s.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane p = new BorderPane(s);
        p.setTop(head());
        p.setBottom(actions(back));
        p.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(p, 1400, 780);
    }

    private HBox hero() {
        Label q = new Label("\"Craftsmanship turns raw wood into\nuseful, beautiful spaces for every life.\"");
        q.setStyle(
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        VBox words = new VBox(q);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefSize(590, 300);
        words.setPadding(new Insets(24, 30, 24, 30));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        ImageView i = im(0, 500, 300);
        HBox h = new HBox(42, words, i);
        h.setPrefWidth(1260);
        h.setAlignment(Pos.CENTER);
        return h;
    }

    private VBox grid() {
        Label t = new Label("Carpenter Work-Skills");
        t.setStyle("-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label sub = new Label("You can select more than one sub-skill based on your expertise.");
        sub.setStyle("-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane f = new FlowPane(20, 20);
        f.setAlignment(Pos.CENTER);
        f.setPrefWrapLength(1180);
        f.setMinWidth(1180);
        f.setMaxWidth(1180);
        for (int x = 0; x < N.length; x++)
            f.getChildren().add(card(x));
        VBox out = new VBox(25, new VBox(8, t, sub), f);
        out.setAlignment(Pos.TOP_CENTER);
        return out;
    }

    private Button card(int x) {
        ImageView i = im(x + 1, 280, 150);
        Label n = new Label(N[x]);
        n.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        n.setAlignment(Pos.CENTER);
        n.setPrefSize(250, 60);
        n.setWrapText(true);
        VBox v = new VBox(i, n);
        v.setAlignment(Pos.CENTER);
        Button b = new Button();
        b.setGraphic(v);
        b.setMinSize(280, 220);
        b.setPrefSize(280, 220);
        b.setMaxSize(280, 220);
        b.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;");
        return b;
    }

    private BorderPane head() {
        Label d = new Label("DIHADI");
        d.setStyle("-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        HBox brand = new HBox(d);
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox navigation = new HBox(20, nav("Home", false), nav("Business", false), nav("Worker", true),
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
        BorderPane h = new BorderPane();
        h.setLeft(brand);
        h.setCenter(navigation);
        h.setRight(account);
        h.setPadding(new Insets(16, 24, 14, 24));
        h.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return h;
    }

    private Button nav(String text, boolean active) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;");
        return b;
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String text) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return b;
    }

    private HBox actions(Runnable back) {
        Button b = outline("←  BACK");
        b.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        Button n = primary("SAVE & CONTINUE");
        n.setOnAction(e -> {
            Stage stage = (Stage) n.getScene().getWindow();
            stage.setScene(new CarpenterJobRole().getCarpenterJobRoleScene(back));
        });
        HBox h = new HBox(b, r, n);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(16, 70, 16, 70));
        h.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return h;
    }

    private ImageView im(int x, double w, double h) {
        ImageView i = new ImageView(new Image(getClass()
                .getResource(String.format("/assets/images/worker/carpenter/skill-%02d.jpg", x)).toExternalForm()));
        i.setFitWidth(w);
        i.setFitHeight(h);
        i.setPreserveRatio(false);
        return i;
    }
}
