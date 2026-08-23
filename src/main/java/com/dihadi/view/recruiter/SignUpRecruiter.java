package com.dihadi.view.recruiter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Recruiter registration form, matching the supplied sign-up design. */
public class SignUpRecruiter {
    private static final String[] VISUALS = { "/assets/images/recruiter/slide-01.jpeg",
            "/assets/images/recruiter/slide-02.jpeg", "/assets/images/recruiter/slide-03.jpeg",
            "/assets/images/recruiter/slide-04.jpeg", "/assets/images/recruiter/slide-05.jpeg" };
    private Timeline rotation;

    public Scene getRecruiterSignUpScene(Runnable back) {
        VBox formColumn = new VBox(28, identity(), introduction(), registrationCard(back));
        formColumn.setAlignment(Pos.TOP_CENTER);
        formColumn.setPrefWidth(700);
        formColumn.setPadding(new Insets(52, 50, 56, 50));

        ScrollPane formScroll = new ScrollPane(formColumn);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setStyle("-fx-background:transparent;-fx-background-color:#fff8f0;-fx-border-width:0;");
        HBox.setHgrow(formScroll, Priority.ALWAYS);

        ImageView visual = image(VISUALS[0], 520, 430);
        visual.setPreserveRatio(true);
        startRotation(visual);
        StackPane photo = new StackPane(visual);
        photo.setPrefSize(540, 455);
        photo.setMaxSize(540, 455);
        photo.setPadding(new Insets(12));
        photo.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;"
                        + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.16),18,0,0,7px);");
        Label eyebrow = text("DIHADI RECRUITER COMMUNITY",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#d4af37;-fx-letter-spacing:1.5px;");
        Label headline = text("Build a team\nthat delivers.",
                "-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#fff8f0;-fx-line-spacing:4px;");
        Label copy = text(
                "Your business details help DIHADI connect you with verified, skilled professionals for every project.",
                "-fx-font-size:15px;-fx-text-fill:#f8f0e2;-fx-opacity:.86;");
        copy.setWrapText(true);
        copy.setMaxWidth(500);
        VBox words = new VBox(12, eyebrow, headline, copy);
        words.setAlignment(Pos.CENTER_LEFT);
        VBox visualContent = new VBox(24, photo, words);
        visualContent.setAlignment(Pos.CENTER);
        visualContent.setPadding(new Insets(38));
        StackPane visualColumn = new StackPane(visualContent);
        visualColumn.setAlignment(Pos.CENTER);
        visualColumn.setPrefWidth(700);
        visualColumn.setMinWidth(520);
        visualColumn.setStyle("-fx-background-color:linear-gradient(to bottom right,#343027,#4c4233);");

        HBox layout = new HBox(formScroll, visualColumn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color:#fff8f0;");
        return new Scene(layout, 1400, 780);
    }

    private VBox identity() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 112, 112);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        VBox box = new VBox(8, logo,
                text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:43px;-fx-font-weight:800;-fx-text-fill:#1f1b13;"),
                text("Mera Haq ~ Meri Dihadi",
                        "-fx-font-family:Georgia;-fx-font-size:21px;-fx-font-style:italic;-fx-text-fill:#574d40;"));
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox introduction() {
        VBox box = new VBox(10,
                text("Welcome to DIHADI",
                        "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:700;-fx-text-fill:#1f1b13;"),
                text("Sign In or Create a New Account", "-fx-font-size:18px;-fx-text-fill:#342f28;"),
                text("Go ahead. Enter your business details to proceed ahead.",
                        "-fx-font-size:17px;-fx-text-fill:#41392f;"));
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox registrationCard(Runnable back) {
        GridPane fields = new GridPane();
        fields.setHgap(28);
        fields.setVgap(18);
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        left.setHgrow(Priority.ALWAYS);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        right.setHgrow(Priority.ALWAYS);
        fields.getColumnConstraints().addAll(left, right);
        fields.add(field("First Name *required", "e.g. Ram"), 0, 0, 2, 1);
        fields.add(field("Middle Name", ""), 0, 1);
        fields.add(field("Last Name", ""), 1, 1);
        fields.add(choice("Gender", "Select Gender", "Male", "Female", "Other"), 0, 2, 2, 1);
        fields.add(field("Mobile Number\n*required", "+91     00000 0000"), 0, 3);
        fields.add(field("Alternate Mobile", "+91     00000 0000"), 1, 3);
        fields.add(field("Email Address *required", "name@example.com"), 0, 4);
        fields.add(field("Alternate Email", "Optional"), 1, 4);
        fields.add(field("Company / Organisation Name *", "Your company name"), 0, 5, 2, 1);
        fields.add(choice("Business Type *", "Select business type", "Builder", "Developer", "General Contractor",
                "Sub-contractor"), 0, 6, 2, 1);

        Button backButton = button("←  BACK", false);
        backButton.setOnAction(e -> {
            if (back != null)
                back.run();
        });
        Button submit = button("CREATE RECRUITER ACCOUNT", true);
        Button loginLink = new Button("Already having account? Login");
        loginLink.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;");
        loginLink.setOnAction(e -> ((javafx.stage.Stage) loginLink.getScene().getWindow()).setScene(new RecruiterLoginPage(back).getLoginScene()));
        VBox actionArea = new VBox(10, loginLink); actionArea.setAlignment(Pos.CENTER_RIGHT);
        HBox actions = new HBox(14, backButton, submit);
        actions.setAlignment(Pos.CENTER_RIGHT);
        VBox card = new VBox(22,
                text("Personal Details", "-fx-font-size:25px;-fx-font-weight:700;-fx-text-fill:#1f1b13;"), divider(),
                fields, actionArea, actions);
        card.setMaxWidth(600);
        card.setPadding(new Insets(32, 42, 34, 42));
        card.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:18px;-fx-border-color:#ddd4c6;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
        return card;
    }

    private VBox field(String label, String prompt) {
        TextField input = new TextField();
        input.setPromptText(prompt);
        input.setStyle(inputStyle());
        return new VBox(8, text(label, labelStyle()), input);
    }

    private VBox choice(String label, String prompt, String... values) {
        ComboBox<String> input = new ComboBox<>();
        input.getItems().addAll(values);
        input.setPromptText(prompt);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setStyle(inputStyle());
        return new VBox(8, text(label, labelStyle()), input);
    }

    private Region divider() {
        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color:#ddd4c6;");
        return line;
    }

    private Button button(String label, boolean primary) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color:" + (primary ? "#d4af37" : "#fff8f0")
                + ";-fx-background-radius:18px;-fx-border-color:" + (primary ? "transparent" : "#806c47")
                + ";-fx-border-radius:18px;-fx-text-fill:#342f28;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:13px 20px;-fx-cursor:hand;");
        return button;
    }

    private String inputStyle() {
        return "-fx-background-color:#f2eadc;-fx-background-radius:10px;-fx-border-color:transparent;-fx-font-size:16px;-fx-padding:13px 16px;-fx-pref-height:58px;";
    }

    private String labelStyle() {
        return "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#2d2923;";
    }

    private Label text(String value, String style) {
        Label label = new Label(value);
        label.setStyle(style);
        return label;
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(load(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setSmooth(true);
        return view;
    }

    private Image load(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void startRotation(ImageView view) {
        if (rotation != null)
            rotation.stop();
        final int[] index = { 0 };
        rotation = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            index[0] = (index[0] + 1) % VISUALS.length;
            view.setImage(load(VISUALS[index[0]]));
        }));
        rotation.setCycleCount(Timeline.INDEFINITE);
        rotation.play();
    }
}
