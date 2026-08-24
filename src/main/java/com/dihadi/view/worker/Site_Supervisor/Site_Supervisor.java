package com.dihadi.view.worker.Site_Supervisor;

import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
 * Foreman sub-skill selection screen based on the supplied Foreman reference.
 */
public class Site_Supervisor {
    private static final String[] SKILLS = {
            "Labour", "Mason", "Carpenter", "Painter", "Electrician", "Plumber"
    };
    private static final String[] HINDI_SKILLS = {
            "लेबर", "मिस्त्री", "बढ़ई", "पेंटर", "इलेक्ट्रीशियन", "प्लंबर"
    };
    private final Set<Integer> selectedSkills = new LinkedHashSet<>();

    public Scene getSiteSupervisorScene(Runnable backAction) {
        return getSiteSupervisorScene(backAction, null, null);
    }

    public Scene getSiteSupervisorScene(Runnable backAction, Runnable homeAction, Runnable aboutAction) {
        VBox content = new VBox(48, createHero(), createSkills());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(1260);
        content.setMaxWidth(1260);

        StackPane scrollContent = new StackPane(content);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setPadding(new Insets(24, 70, 118, 70));
        ScrollPane scroll = new ScrollPane(scrollContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-border-width:0;-fx-padding:0;");

        BorderPane page = new BorderPane();
        page.setTop(createHeader(backAction, homeAction, aboutAction));
        page.setCenter(scroll);
        page.setBottom(createActionBar(backAction));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
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
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(navigation);
        bar.setRight(account);
        bar.setPadding(new Insets(16, 24, 14, 24));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return bar;
    }

    private HBox createHero() {
        ImageView photo = image("/assets/images/worker/foreman/skill-00.jpg", 505, 310);
        roundImage(photo, 505, 310, 24);
        StackPane photoBox = new StackPane(photo);
        photoBox.setPrefSize(505, 310);
        photoBox.setStyle("-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),30,0,0,10px);");
        Label quote = label(
                "\"Great projects are built on strong foundations.\nThey come to life through the guidance, support,\nand vision of a dedicated site supervisor.\"",
                "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4c4637;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setMaxWidth(570);
        VBox quoteBox = new VBox(quote);
        quoteBox.setAlignment(Pos.CENTER_LEFT);
        quoteBox.setPrefHeight(310);
        quoteBox.setPadding(new Insets(10, 0, 10, 22));
        quoteBox.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        HBox hero = new HBox(42, photoBox, quoteBox);
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPrefWidth(1260);
        hero.setMaxWidth(1260);
        return hero;
    }

    private VBox createSkills() {
        Label title = label("Site Supervisor Work-Skills",
                "-fx-font-family:'Georgia';-fx-font-size:42px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        Label subtitle = label("You can select more than one sub-skill based on your expertise.",
                "-fx-font-size:16px;-fx-text-fill:#4c4637;");
        VBox heading = new VBox(10, title, subtitle);
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setPrefWrapLength(1260);
        for (int i = 0; i < SKILLS.length; i++)
            grid.getChildren().add(skillCard(i));
        return new VBox(30, heading, grid);
    }

    private Button skillCard(int index) {
        ImageView picture = image(String.format("/assets/images/worker/foreman/skill-%02d.jpg", index + 1), 280, 140);
        Label name = label(SKILLS[index],
                "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#1e1b15;-fx-alignment:center;");
        name.setWrapText(true);
        name.setMaxWidth(250);
        name.setAlignment(Pos.CENTER);
        name.setMinHeight(36);
        name.setPrefHeight(36);
        name.setMaxHeight(36);
        Label hindi = label(HINDI_SKILLS[index], "-fx-font-size:14px;-fx-text-fill:#4c4637;-fx-alignment:center;");
        hindi.setWrapText(true);
        hindi.setMaxWidth(250);
        hindi.setAlignment(Pos.CENTER);
        hindi.setMinHeight(30);
        hindi.setPrefHeight(30);
        hindi.setMaxHeight(30);
        VBox text = new VBox(4, name, hindi);
        text.setAlignment(Pos.CENTER);
        text.setPadding(new Insets(10, 12, 10, 12));
        text.setPrefHeight(80);
        VBox graphic = new VBox(picture, text);
        graphic.setPrefSize(280, 220);

        Button card = new Button();
        card.setGraphic(graphic);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setPadding(Insets.EMPTY);
        card.setMinSize(280, 220);
        card.setPrefSize(280, 220);
        card.setMaxSize(280, 220);
        setCardStyle(card, false);
        card.setOnAction(event -> {
            boolean selected = selectedSkills.contains(index);
            if (selected)
                selectedSkills.remove(index);
            else
                selectedSkills.add(index);
            setCardStyle(card, !selected);
        });
        return card;
    }

    private HBox createActionBar(Runnable backAction) {
        Button back = outlineButton("BACK");
        back.setOnAction(event -> {
            if (backAction != null)
                backAction.run();
        });
        Button save = primaryButton("SAVE & CONTINUE");
        save.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) save.getScene().getWindow();
            stage.setScene(new SiteSupervisorJobRolesPage().getScene(backAction));
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(back, spacer, save);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle(
                "-fx-background-color:#fff8f0;-fx-border-color:#cfc6b2;-fx-border-width:1px 0 0 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),12,0,0,-4px);");
        return bar;
    }

    private void saveSelection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Site Supervisor Skills");
        alert.setHeaderText(null);
        alert.setContentText(selectedSkills.isEmpty() ? "Select at least one sub-skill to continue."
                : selectedSkills.size() + " skill" + (selectedSkills.size() == 1 ? " has" : "s have")
                        + " been selected.");
        alert.show();
    }

    private void setCardStyle(Button card, boolean selected) {
        card.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:15px;-fx-border-radius:15px;-fx-border-width:2px;-fx-border-color:"
                        + (selected ? "#d4af37" : "transparent") + ";-fx-effect:dropshadow(gaussian,rgba(58,48,39,"
                        + (selected ? ".18" : ".10") + "),30,0,0,8px);-fx-cursor:hand;");
    }

    private Button navButton(String text, boolean active) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:0;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                        + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent")
                        + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;");
        return button;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#231b00;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 25px;-fx-cursor:hand;");
        return button;
    }

    private Button outlineButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#7e7665;-fx-border-radius:18px;-fx-text-fill:#1e1b15;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 24px;-fx-cursor:hand;");
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

    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }
}
