package com.dihadi.view.worker.GeneralLabour;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;

/** General Labour work-skills selection screen with unified DIHADI category styling. */
public class GeneralLabourPage {
    private static final String[] SKILLS = {
            "General Labour", "Material Shifting Helper", "Factory Worker Helper", "Heavy Building Helper",
            "Lanter Worker Helper", "Road Construction", "Sewage Worker Helper", "Concrete Mixer Labour",
            "Mason Helper", "Loading & Unloading", "Stone Crusher", "Mines Excavator",
            "Manual Scavengers", "Bar Bender Helper", "Shuttering Helper", "Gravels Segmentation"
    };
    private static final String[] HINDI_SKILLS = {
            "सामान्य लेबर", "सामग्री स्थानांतरण लेबर", "फैक्ट्री हेल्पर", "बड़ी बिल्डिंग हेल्पर",
            "लेंटर हेल्पर", "सड़क निर्माण हेल्पर", "सीवेज हेल्पर", "कंक्रीट लेबर",
            "मिस्त्री हेल्पर", "लोडिंग और अनलोडिंग लेबर", "पत्थर तोड़ने वाला", "खानों की खुदाई करने वाला",
            "मैनुअल स्कवेंजर", "सरिया हेल्पर", "शटरिंग हेल्पर", "बजरी विभाजन लेबर"
    };
    private final Set<Integer> selected = new LinkedHashSet<>();

    public Scene getGeneralLabourScene(Runnable backAction, Runnable homeAction, Runnable aboutAction) {
        VBox content = new VBox(38, hero(), skills());
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
        page.setTop(header(backAction, homeAction, aboutAction));
        page.setBottom(actionBar(backAction));
        page.setStyle("-fx-background-color:#f3e7ce;");

        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(root, 1400, 780);
    }

    private HBox hero() {
        Label quote = label(
                "\"The foundation of every great nation is built by the\nhands of its dedicated workforce.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        VBox words = new VBox(quote);
        words.setAlignment(Pos.CENTER_LEFT);
        words.setPrefSize(590, 310);
        words.setPadding(new Insets(24, 30, 24, 30));
        words.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");

        ImageView photo = image("/assets/images/general-labour/skill-00.jpg", 500, 310);
        round(photo, 500, 310, 24);
        StackPane photoFrame = new StackPane(photo);
        photoFrame.setStyle("-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),20,0,0,6px);");

        HBox hero = new HBox(42, words, photoFrame);
        hero.setPrefWidth(1260);
        hero.setMaxWidth(1260);
        hero.setAlignment(Pos.CENTER);
        return hero;
    }

    private VBox skills() {
        Label title = label("General Labour Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label sub = label("You can select more than one sub-skill based on your expertise.",
                "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setPrefWrapLength(1260);
        for (int i = 0; i < SKILLS.length; i++) {
            grid.getChildren().add(card(i));
        }
        return new VBox(28, new VBox(8, title, sub), grid);
    }

    private Button card(int index) {
        ImageView photo = image(String.format("/assets/images/general-labour/skill-%02d.jpg", index + 1), 232, 145);
        roundTop(photo, 232, 145, 18);

        Label name = label(SKILLS[index],
                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        name.setWrapText(true);
        name.setAlignment(Pos.CENTER);
        name.setPrefSize(208, 32);
        name.setMaxSize(208, 32);

        Label hindi = label(HINDI_SKILLS[index], "-fx-font-size:13px;-fx-text-fill:#6b6255;-fx-alignment:center;");
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
            if (active) {
                selected.remove(index);
            } else {
                selected.add(index);
            }
            cardStyle(card, !active);
        });
        card.setOnMouseEntered(e -> {
            if (!selected.contains(index)) {
                card.setStyle(
                        "-fx-background-color:#ffffff;-fx-background-radius:20px;-fx-border-radius:20px;-fx-border-width:2px;-fx-border-color:#d4af37;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),16,0,0,5px);-fx-cursor:hand;");
            }
        });
        card.setOnMouseExited(e -> cardStyle(card, selected.contains(index)));
        return card;
    }

    private BorderPane header(Runnable back, Runnable home, Runnable about) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
        brand.setAlignment(Pos.CENTER_LEFT);

        Button h = nav("Home", false);
        h.setOnAction(e -> {
            if (home != null) home.run();
            else com.dihadi.view.AppNavigator.open((Stage) h.getScene().getWindow(), "Home");
        });
        Button b = nav("Business", false);
        b.setOnAction(e -> com.dihadi.view.AppNavigator.open((Stage) b.getScene().getWindow(), "Business"));
        Button w = nav("Worker", true);
        w.setOnAction(e -> {
            if (back != null) back.run();
            else com.dihadi.view.AppNavigator.open((Stage) w.getScene().getWindow(), "Worker");
        });
        Button r = nav("Recruiter", false);
        r.setOnAction(e -> com.dihadi.view.AppNavigator.open((Stage) r.getScene().getWindow(), "Recruiter"));
        Button a = nav("About Us", false);
        a.setOnAction(e -> {
            if (about != null) about.run();
            else com.dihadi.view.AppNavigator.open((Stage) a.getScene().getWindow(), "About Us");
        });
        Button c = nav("Contact Us", false);
        c.setOnAction(e -> com.dihadi.view.AppNavigator.open((Stage) c.getScene().getWindow(), "Contact Us"));

        HBox navigation = new HBox(12, h, b, w, r, a, c);
        navigation.setAlignment(Pos.CENTER);

        Button admin = com.dihadi.view.AppNavigator.createHeaderActionButton();
        HBox account = new HBox(10, admin);
        account.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(navigation);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private HBox actionBar(Runnable back) {
        Button previous = outline("←  Back to categories");
        previous.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button next = primary("Save & Next →");
        next.setOnAction(e -> {
            Stage stage = (Stage) next.getScene().getWindow();
            stage.setScene(new GeneralLabourJobRole().getGeneralLabourJobRoleScene(back));
        });
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox bar = new HBox(previous, gap, next);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
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
        b.setStyle("-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
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

    private void roundTop(ImageView image, double width, double height, double radius) {
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
