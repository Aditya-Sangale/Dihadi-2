package com.dihadi.view.worker.ITI_Technician;

import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

/** Self-contained ITI / Technician work-skill selection page. */
public class ITI_Technician {
    private static final String[] SKILLS = {"Pump Operator", "PCM Operator", "Fitter", "Woodwork Technician", "Belt Jointer", "Lift & Escalator Mechanic", "Electrician", "Electrical ITI"};
    private final Set<Integer> selectedSkills = new LinkedHashSet<>();

    public Scene getItiTechnicianScene(Runnable backAction) {
        VBox content = new VBox(38, hero(), skills());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPrefWidth(1260);
        content.setMaxWidth(1260);
        StackPane scrollContent = new StackPane(content);
        scrollContent.setAlignment(Pos.TOP_CENTER);
        scrollContent.setPadding(new Insets(24, 24, 118, 24));

        ScrollPane scroll = new ScrollPane(scrollContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent;-fx-border-width:0;");

        BorderPane page = new BorderPane(scroll);
        page.setTop(header(backAction));
        page.setBottom(actionBar(backAction));
        page.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(page, 1400, 780);
    }

    private HBox hero() {
        Label quote = label("\"Precision, expertise, and technical mastery—\ntechnicians keep the modern world moving.\"", "-fx-font-family:'Georgia';-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#4d4635;-fx-line-spacing:6px;");
        VBox copy = new VBox(quote);
        copy.setAlignment(Pos.CENTER_LEFT);
        copy.setPrefSize(590, 300);
        copy.setPadding(new Insets(24, 30, 24, 30));
        copy.setStyle("-fx-border-color:#d4af37;-fx-border-width:0 0 0 4px;");
        ImageView image = image(0, 500, 300);
        round(image, 500, 300);
        HBox hero = new HBox(42, copy, image);
        hero.setAlignment(Pos.CENTER);
        hero.setPrefWidth(1260);
        return hero;
    }

    private VBox skills() {
        Label title = label("ITI / Technician Work-Skills", "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label subtitle = label("You can select more than one sub-skill based on your expertise.", "-fx-font-size:16px;-fx-text-fill:#4d4635;");
        FlowPane grid = new FlowPane(20, 20);
        grid.setAlignment(Pos.CENTER);
        grid.setPrefWrapLength(1180);
        grid.setMaxWidth(1180);
        for (int index = 0; index < SKILLS.length; index++) grid.getChildren().add(skillCard(index));
        VBox out = new VBox(26, new VBox(8, title, subtitle), grid);
        out.setAlignment(Pos.TOP_CENTER);
        return out;
    }

    private Button skillCard(int index) {
        ImageView photo = image(index + 1, 280, 150);
        Label name = label(SKILLS[index], "-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#342f28;-fx-alignment:center;");
        name.setAlignment(Pos.CENTER);
        name.setWrapText(true);
        name.setPrefSize(250, 60);
        VBox graphic = new VBox(photo, name);
        graphic.setAlignment(Pos.CENTER);
        Button card = new Button();
        card.setGraphic(graphic);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setMinSize(280, 220);
        card.setPrefSize(280, 220);
        card.setMaxSize(280, 220);
        cardStyle(card, false);
        card.setOnAction(event -> { boolean active = selectedSkills.contains(index); if (active) selectedSkills.remove(index); else selectedSkills.add(index); cardStyle(card, !active); });
        return card;
    }

    private BorderPane header(Runnable backAction) {
        Label brand = label("DIHADI", "-fx-font-family:'Georgia';-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        HBox navigation = new HBox(20, navButton("Home", false), navButton("Business", false), navButton("Worker", true), navButton("Recruiter", false), navButton("About Us", false), navButton("Contact Us", false));
        navigation.setAlignment(Pos.CENTER);
        com.dihadi.view.AppNavigator.activateNavigation(navigation);
        navigation.getChildren().get(2).setOnMouseClicked(event -> { if (backAction != null) backAction.run(); });
        Button login = outline("Login"), signUp = primary("Sign Up");
        login.setOnAction(e -> com.dihadi.view.AppNavigator.login());
        signUp.setOnAction(e -> com.dihadi.view.AppNavigator.signUp((Stage) signUp.getScene().getWindow(), () -> com.dihadi.view.AppNavigator.open((Stage) signUp.getScene().getWindow(), "Worker")));
        login.setMouseTransparent(true); signUp.setMouseTransparent(true);
        HBox account = new HBox(12, login, signUp);
        account.setAlignment(Pos.CENTER_RIGHT);
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(account);
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;");
        return header;
    }

    private HBox actionBar(Runnable backAction) {
        Button back = outline("←  BACK");
        back.setOnAction(event -> { if (backAction != null) backAction.run(); });
        Button next = primary("SAVE & CONTINUE");
        next.setOnAction(event -> {
            Stage stage = (Stage) next.getScene().getWindow();
            stage.setScene(new ITI_TechnicianJobRole().getItiTechnicianJobRoleScene(backAction));
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(back, spacer, next);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 70, 16, 70));
        bar.setStyle("-fx-background-color:rgba(255,248,240,.96);-fx-border-color:#d0c5af;-fx-border-width:1px 0 0 0;");
        return bar;
    }

    private void save() { Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setTitle("ITI / Technician Skills"); alert.setHeaderText(null); alert.setContentText(selectedSkills.isEmpty() ? "Select at least one sub-skill to continue." : selectedSkills.size() + " skill(s) have been selected."); alert.show(); }
    private void cardStyle(Button card, boolean selected) { card.setStyle("-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-radius:20px;-fx-border-width:2px;-fx-border-color:" + (selected ? "#d4af37" : "#d0c5af") + ";-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);-fx-cursor:hand;"); }
    private Button navButton(String text, boolean active) { Button button = new Button(text); button.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:" + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (active ? "#735c00" : "transparent") + ";-fx-border-width:0 0 2px 0;-fx-padding:8px 4px;-fx-cursor:hand;"); return button; }
    private Button primary(String text) { Button button = new Button(text); button.setStyle("-fx-background-color:#d4af37;-fx-background-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:11px 24px;-fx-cursor:hand;"); return button; }
    private Button outline(String text) { Button button = new Button(text); button.setStyle("-fx-background-color:transparent;-fx-background-radius:18px;-fx-border-color:#806c47;-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 23px;-fx-cursor:hand;"); return button; }
    private ImageView image(int index, double width, double height) { ImageView view = new ImageView(new Image(getClass().getResource(String.format("/assets/images/worker/iti/skill-%02d.jpg", index)).toExternalForm())); view.setFitWidth(width); view.setFitHeight(height); view.setPreserveRatio(false); return view; }
    private void round(ImageView image, double width, double height) { Rectangle clip = new Rectangle(width, height); clip.setArcWidth(40); clip.setArcHeight(40); image.setClip(clip); }
    private Label label(String text, String style) { Label label = new Label(text); label.setStyle(style); return label; }
}
