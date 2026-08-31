package com.dihadi.view.recruiter;

import com.dihadi.controller.RecruiterController;
import com.dihadi.view.AppNavigator;

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
import javafx.stage.Stage;
import javafx.util.Duration;

/** Recruiter registration form, matching the supplied sign-up design. */
public class SignUpRecruiter {
    private static final String[] VISUALS = { "/assets/images/recruiter/slide-01.jpeg",
            "/assets/images/recruiter/slide-02.jpeg", "/assets/images/recruiter/slide-03.jpeg",
            "/assets/images/recruiter/slide-04.jpeg", "/assets/images/recruiter/slide-05.jpeg" };
    private Timeline rotation;

    // Form field references for Firestore submission
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField lastNameField;
    private ComboBox<String> genderField;
    private TextField mobileField;
    private TextField alternateMobileField;
    private TextField emailField;
    private TextField alternateEmailField;
    private TextField companyNameField;
    private ComboBox<String> businessTypeField;
    private javafx.scene.control.PasswordField passwordField;
    private Runnable backAction;

    public Scene getRecruiterSignUpScene(Runnable back) {
        this.backAction = back;
        VBox formColumn = new VBox(28, identity(), introduction(), registrationCard(back));
        formColumn.setAlignment(Pos.TOP_CENTER);
        formColumn.setPrefWidth(700);
        formColumn.setPadding(new Insets(52, 50, 56, 50));

        ScrollPane formScroll = new ScrollPane(formColumn);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setStyle("-fx-background:transparent;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        HBox.setHgrow(formScroll, Priority.ALWAYS);

        ImageView visual = image(VISUALS[0], 520, 430);
        visual.setPreserveRatio(true);
        visual.setSmooth(true);
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
        layout.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(layout, 1400, 780);
    }

    private VBox identity() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 112, 112);
        logo.setPreserveRatio(true);
        VBox box = new VBox(8, logo,
                text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:43px;-fx-font-weight:800;-fx-text-fill:#1f1b13;"),
                text("Meri Dihadi ~ Mera Haq",
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

        firstNameField = new TextField();
        firstNameField.setPromptText("e.g. Ram");
        firstNameField.setStyle(inputStyle());
        fields.add(new VBox(8, text("First Name *required", labelStyle()), firstNameField), 0, 0, 2, 1);

        middleNameField = new TextField();
        middleNameField.setPromptText("");
        middleNameField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Middle Name", labelStyle()), middleNameField), 0, 1);

        lastNameField = new TextField();
        lastNameField.setPromptText("");
        lastNameField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Last Name", labelStyle()), lastNameField), 1, 1);

        genderField = new ComboBox<>();
        genderField.getItems().addAll("Male", "Female", "Other");
        genderField.setPromptText("Select Gender");
        genderField.setMaxWidth(Double.MAX_VALUE);
        genderField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Gender", labelStyle()), genderField), 0, 2, 2, 1);

        mobileField = new TextField();
        mobileField.setPromptText("+91 000000000");
        mobileField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Mobile Number\n*required", labelStyle()), mobileField), 0, 3);

        alternateMobileField = new TextField();
        alternateMobileField.setPromptText("+91 000000000");
        alternateMobileField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Alternate Mobile", labelStyle()), alternateMobileField), 1, 3);

        emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Email Address *required", labelStyle()), emailField), 0, 4);

        alternateEmailField = new TextField();
        alternateEmailField.setPromptText("Optional");
        alternateEmailField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Alternate Email", labelStyle()), alternateEmailField), 1, 4);

        companyNameField = new TextField();
        companyNameField.setPromptText("Your company name");
        companyNameField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Company / Organisation Name *", labelStyle()), companyNameField), 0, 5, 2, 1);

        businessTypeField = new ComboBox<>();
        businessTypeField.getItems().addAll("Builder", "Developer", "General Contractor", "Sub-contractor");
        businessTypeField.setPromptText("Select business type");
        businessTypeField.setMaxWidth(Double.MAX_VALUE);
        businessTypeField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Business Type *", labelStyle()), businessTypeField), 0, 6, 2, 1);

        passwordField = new javafx.scene.control.PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(inputStyle());
        fields.add(new VBox(8, text("Create Dihadi Password *required", labelStyle()), passwordField), 0, 7, 2, 1);

        Button backButton = button("←  BACK", false);
        backButton.setOnAction(e -> AppNavigator.open((Stage) backButton.getScene().getWindow(), "Home"));
        Button sectionBack = new Button("<");
        sectionBack.setStyle(
                "-fx-background-color:#e8d7b6;-fx-background-radius:12px;-fx-text-fill:#4d4635;-fx-font-size:18px;-fx-font-weight:800;-fx-padding:7px 14px;-fx-cursor:hand;");
        sectionBack.setOnAction(e -> AppNavigator.open((Stage) sectionBack.getScene().getWindow(), "Home"));
        Label personalDetails = text("PERSONAL DETAILS",
                "-fx-background-color:#e8d7b6;-fx-background-radius:12px;-fx-text-fill:#4d4635;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-padding:11px 16px;");
        Button submit = button("CREATE RECRUITER ACCOUNT", true);
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submitRecruiter());
        Button loginLink = new Button("Already having account? Login");
        loginLink.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:700;-fx-cursor:hand;");
        loginLink.setOnAction(e -> {
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new RecruiterLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        });
        VBox actionArea = new VBox(14, submit, loginLink);
        actionArea.setAlignment(Pos.CENTER);
        actionArea.setPadding(new Insets(10, 0, 0, 0));
        HBox formHeading = new HBox(10, sectionBack, personalDetails);
        formHeading.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(22,
                formHeading, divider(),
                fields, actionArea);
        card.setMaxWidth(600);
        card.setPadding(new Insets(32, 42, 34, 42));
        card.setStyle(
                "-fx-background-color:#fffdf9;-fx-background-radius:18px;-fx-border-color:#ddd4c6;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),16,0,0,5px);");
        return card;
    }

    private void submitRecruiter() {
        if (firstNameField.getText().isBlank() || mobileField.getText().isBlank()
                || emailField.getText().isBlank() || companyNameField.getText().isBlank()
                || passwordField.getText().isBlank()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Complete your details");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all required fields (First Name, Mobile, Email, Company Name).");
            alert.show();
            return;
        }

        try {
            RecruiterController controller = new RecruiterController();
            controller.addRecruiter(
                    firstNameField.getText().trim(),
                    middleNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    genderField.getValue() != null ? genderField.getValue() : "",
                    mobileField.getText().trim(),
                    alternateMobileField.getText().trim(),
                    emailField.getText().trim(),
                    alternateEmailField.getText().trim(),
                    companyNameField.getText().trim(),
                    businessTypeField.getValue() != null ? businessTypeField.getValue() : "",
                    passwordField.getText().trim());

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new RecruiterLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create account. Please check your internet connection and try again.");
            alert.show();
        }
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
