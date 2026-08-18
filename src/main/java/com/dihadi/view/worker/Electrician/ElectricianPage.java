package com.dihadi.view.worker.Electrician;

import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** Electrician sub-skill selection flow based on the supplied reference. */
public class ElectricianPage {
    private static final String[] SKILLS = { "Electrician", "Power Testing", "Electrician Helper", "Cable Jointer",
            "Cable Jointer Helper", "Lineman", "Wireman", "Sub Station Attendant", "HVAC Technician",
            "Fire Safety Technician" };
    private static final String[] HINDI = { "इलेक्ट्रीशियन", "पॉवर जांच", "हेल्पर", "केबल जॉइंटर", "केबल जॉइंटर सहायक",
            "लाइनमैन", "वायरमैन", "सब स्टेशन अटेंडेंट", "HVAC तकनीशियन", "फायर सेफ्टी टेक्नीशियन" };
    private final Set<Integer> selected = new LinkedHashSet<>();

    public Scene getElectricianScene(Runnable back, Runnable home, Runnable about) {
        VBox content = new VBox(40, hero(), skills());
        content.setPrefWidth(1260);
        content.setMaxWidth(1260);
        content.setAlignment(Pos.TOP_CENTER);
        StackPane scrollContent = new StackPane(content);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setPadding(new Insets(24, 24, 118, 24));
        ScrollPane scroll = new ScrollPane(scrollContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-border-width:0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(header(back, home, about));
        page.setBottom(actionBar(back));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private HBox hero() {
        Label quote = label(
                "\"Bringing light to the dark,\nand power to the future—\ntrue craftsmanship sparks progress.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:7px;");
        quote.setWrapText(true);
        quote.setMaxWidth(460);
        VBox words = new VBox(quote);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefWidth(560);
        words.setPrefHeight(300);
        words.setPadding(new Insets(24, 38, 24, 30));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        ImageView photo = image("/assets/images/worker/electrician/skill-00.jpg", 500, 300);
        round(photo, 500, 300, 20);
        StackPane picture = new StackPane(photo);
        picture.setPrefSize(500, 300);
        HBox hero = new HBox(words, picture);
        hero.setAlignment(Pos.CENTER);
        hero.setPrefWidth(1260);
        hero.setStyle(
                "-fx-background-color:#f5eddf;-fx-background-radius:24px;-fx-border-color:#e5d9c7;-fx-border-radius:24px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.06),20,0,0,7px);");
        return hero;
    }

    private VBox skills() {
        Label title = label("Electrician Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:39px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Label sub = label("You can select more than one sub-skill based on your expertise.",
                "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setPrefWrapLength(1260);
        for (int i = 0; i < SKILLS.length; i++)
            grid.getChildren().add(card(i));
        return new VBox(28, new VBox(8, title, sub), grid);
    }

    private Button card(int index) {
        ImageView photo = image(String.format("/assets/images/worker/electrician/skill-%02d.jpg", index + 1), 232, 145);
        Label name = label(SKILLS[index],
                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setPrefSize(208, 32);
        name.setMaxSize(208, 32);
        Label hindi = label(HINDI[index], "-fx-font-size:13px;-fx-text-fill:#6b6255;-fx-alignment:center;");
        hindi.setWrapText(true);
        hindi.setAlignment(Pos.CENTER);
        hindi.setPrefSize(208, 30);
        hindi.setMaxSize(208, 30);
        VBox detail = new VBox(3, name, hindi);
        detail.setAlignment(Pos.CENTER);
        detail.setPrefHeight(76);
        VBox graphic = new VBox(photo, detail);
        graphic.setPrefSize(232, 221);
        Button card = new Button();
        card.setGraphic(graphic);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setPadding(Insets.EMPTY);
        card.setMinSize(232, 221);
        card.setPrefSize(232, 221);
        card.setMaxSize(232, 221);
        cardStyle(card, false);
        card.setOnAction(e -> {
            boolean active = selected.contains(index);
            if (active)
                selected.remove(index);
            else
                selected.add(index);
            cardStyle(card, !active);
        });
        return card;
    }

    private BorderPane header(Runnable back, Runnable home, Runnable about) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        Button h = nav("Home", false);
        h.setOnAction(e -> {
            if (home != null)
                home.run();
        });
        Button b = nav("Business", false);
        Button w = nav("Worker", true);
        w.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button r = nav("Recruiter", false);
        Button a = nav("About Us", false);
        a.setOnAction(e -> {
            if (about != null)
                about.run();
        });
        Button c = nav("Contact Us", false);
        HBox navigation = new HBox(20, h, b, w, r, a, c);
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);
        Button login = outline("Login"), signUp = primary("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((Stage) signUp.getScene().getWindow(), () -> com.dihadi.view.AppNavigator.open((Stage) signUp.getScene().getWindow(), "Worker")));
        login.setMouseTransparent(true); signUp.setMouseTransparent(true);
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

    private HBox actionBar(Runnable back) {
        Button previous = outline("←  BACK");
        previous.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button next = primary("SAVE & CONTINUE");
        next.setOnAction(e -> {
            Stage stage = (Stage) next.getScene().getWindow();
            stage.setScene(new ElectricianJobRole().getElectricianJobRoleScene(back));
        });
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bar = new HBox(previous, gap, next);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:rgba(255,248,240,.96);-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private void cardStyle(Button card, boolean active) {
        card.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-radius:20px;-fx-border-width:2px;-fx-border-color:"
                        + (active ? "#d4af37" : "#d0c5af") + ";-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                        + (active ? ".18" : ".08") + "),16,0,0,5px);-fx-cursor:hand;");
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

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    private Image load(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void round(ImageView image, double width, double height, double radius) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        image.setClip(clip);
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }
}
