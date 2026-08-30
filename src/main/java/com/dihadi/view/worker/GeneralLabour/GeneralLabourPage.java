package com.dihadi.view.worker.GeneralLabour;

import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Selectable General Labour sub-skill page based on the supplied design
 * reference.
 */
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
    private final Set<Integer> selectedSkills = new LinkedHashSet<>();

    public Scene getGeneralLabourScene(Runnable backAction, Runnable homeAction, Runnable aboutAction) {
        VBox content = new VBox(48, createHero(), createSkills());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(1260);
        content.setMaxWidth(1260);

        StackPane scrollContent = new StackPane(content);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setPadding(new Insets(18, 70, 118, 70));

        ScrollPane scroll = new ScrollPane(scrollContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-border-width:0;-fx-padding:0;");

        BorderPane page = new BorderPane();
        page.setTop(createHeader(backAction, homeAction, aboutAction));
        page.setCenter(scroll);
        page.setBottom(createActionBar(backAction));
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private HBox createHero() {
        ImageView photo = image("/assets/images/general-labour/skill-00.jpg", 465, 310);
        roundImage(photo, 465, 310, 24);
        StackPane photoFrame = new StackPane(photo);
        photoFrame.setPrefSize(465, 310);
        photoFrame.setStyle(
                "-fx-background-radius:24px;-fx-border-radius:24px;-fx-overflow:hidden;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),30,0,0,10px);");

        Label quote = label(
                "\"The foundation of every great nation is built by the\nhands of its dedicated workforce.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:5px;");
        VBox quoteBox = new VBox(quote);
        quoteBox.setAlignment(Pos.CENTER_LEFT);
        quoteBox.setPrefHeight(310);
        quoteBox.setPadding(new Insets(10, 0, 10, 22));
        quoteBox.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        HBox hero = new HBox(42, photoFrame, quoteBox);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPrefWidth(1260);
        hero.setMaxWidth(1260);
        hero.setPadding(new Insets(18, 0, 0, 0));
        return hero;
    }

    private VBox createSkills() {
        Label title = label("General Labour Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:#1f1b13;");
        Label subtitle = label("You can select more than one sub-skill based on your expertise.",
                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-text-fill:#4d4635;");
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setPrefWrapLength(1260);
        for (int i = 0; i < SKILLS.length; i++) {
            grid.getChildren().add(skillCard(i));
        }
        VBox section = new VBox(36, new VBox(10, title, subtitle), grid);
        section.setAlignment(Pos.TOP_LEFT);
        return section;
    }

    private Button skillCard(int index) {
        ImageView picture = image(String.format("/assets/images/general-labour/skill-%02d.jpg", index + 1), 280, 140);
        Label title = label(SKILLS[index],
                "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#343027;");
        title.setWrapText(true);
        Label hindi = label(HINDI_SKILLS[index], "-fx-font-size:14px;-fx-text-fill:#7f7663;");
        VBox text = new VBox(4, title, hindi);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(14, 12, 14, 12));
        text.setPrefHeight(80);
        VBox graphic = new VBox(picture, text);
        graphic.setPrefSize(280, 220);

        Button card = new Button();
        card.setGraphic(graphic);
        card.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        card.setPadding(Insets.EMPTY);
        card.setPrefSize(280, 220);
        setCardStyle(card, false);
        card.setOnAction(event -> {
            boolean isSelected = selectedSkills.contains(index);
            if (isSelected) {
                selectedSkills.remove(index);
            } else {
                selectedSkills.add(index);
            }
            setCardStyle(card, !isSelected);
        });
        return card;
    }

    private void setCardStyle(Button card, boolean selected) {
        card.setStyle("-fx-background-color:#fffdf9;-fx-background-radius:15px;-fx-border-radius:15px;"
                + "-fx-border-width:2px;-fx-border-color:" + (selected ? "#d4af37" : "transparent") + ";"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39," + (selected ? ".18" : ".08")
                + "),30,0,0,10px);-fx-cursor:hand;");
    }

    private HBox createActionBar(Runnable backAction) {
        Button back = outlineButton("←  BACK");
        back.setOnAction(event -> {
            if (backAction != null)
                backAction.run();
        });
        Button save = primaryButton("SAVE & NEXT  →");
        save.setOnAction(event -> {
            Stage stage = (Stage) save.getScene().getWindow();
            stage.setScene(new GeneralLabourJobRole().getGeneralLabourJobRoleScene(backAction));
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(back, spacer, save);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),30,0,0,-10px);");
        return bar;
    }

    private void saveSelection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("General Labour Skills");
        alert.setHeaderText(null);
        if (selectedSkills.isEmpty()) {
            alert.setContentText("Select at least one sub-skill to continue.");
        } else {
            alert.setContentText(selectedSkills.size() + " skill" + (selectedSkills.size() == 1 ? " has" : "s have")
                    + " been selected.");
        }
        alert.show();
    }

    private BorderPane createHeader(Runnable backAction, Runnable homeAction, Runnable aboutAction) {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 54, 54);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);

        Button home = navButton("Home", false);
        home.setOnAction(e -> {
            if (homeAction != null)
                homeAction.run();
        });
        Button business = navButton("Business", false);
        Button worker = navButton("Worker", true);
        worker.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
        });
        Button recruiter = navButton("Recruiter", false);
        Button about = navButton("About Us", false);
        about.setOnAction(e -> {
            if (aboutAction != null)
                aboutAction.run();
        });
        Button contact = navButton("Contact Us", false);
        HBox navigation = new HBox(20, home, business, worker, recruiter, about, contact);
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);

        Button login = outlineButton("Login"), signUp = primaryButton("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((Stage) signUp.getScene().getWindow(),
                () -> com.dihadi.view.AppNavigator.open((Stage) signUp.getScene().getWindow(), "Worker")));
        login.setMouseTransparent(true);
        signUp.setMouseTransparent(true);
        HBox account = new HBox(14, login, signUp);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(account);
        header.setPadding(new Insets(20, 70, 18, 70));
        header.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return header;
    }

    private Button navButton(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-family:'Georgia';-fx-font-size:14px;-fx-text-fill:"
                        + (active ? "#735c00" : "#4d4635") + ";-fx-font-weight:" + (active ? "700" : "400")
                        + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                        + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 2px;-fx-cursor:hand;");
        return button;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#343027;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:11px 24px;-fx-cursor:hand;");
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#343027;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;");
        return button;
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(loadImage(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    private void roundImage(ImageView image, double width, double height, double radius) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        image.setClip(clip);
    }

    private Image loadImage(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void setWorkerBackground(StackPane root) {
        var resource = getClass().getResource("/assets/images/background image.jpeg");
        if (resource == null) {
            root.setBackground(
                    new Background(new BackgroundFill(Color.web("#f3e7ce"), CornerRadii.EMPTY, Insets.EMPTY)));
            return;
        }
        Image background = new Image(resource.toExternalForm());
        BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        root.setBackground(new Background(
                new BackgroundFill[] { new BackgroundFill(Color.web("#f3e7ce99"), CornerRadii.EMPTY, Insets.EMPTY) },
                new BackgroundImage[] { backgroundImage }));
    }

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }
}
