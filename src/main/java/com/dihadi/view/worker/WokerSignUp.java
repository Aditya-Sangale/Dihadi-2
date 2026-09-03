package com.dihadi.view.worker;

import java.io.File;

import com.dihadi.controller.ImageUploadController;
import com.dihadi.controller.WorkerController;
import com.dihadi.view.AppNavigator;

import javafx.beans.binding.Bindings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Worker personal-details form opened from the Worker page. */
public class WokerSignUp {
    private final TextField firstName = field("e.g. Ram");
    private final TextField middleName = field("e.g. D");
    private final TextField lastName = field("e.g. Kumar");
    private final TextField mobile = field("10-digit mobile number");
    private final TextField alternateMobile = field("Optional");
    private final TextField email = field("name@example.com");
    private final TextField dailyWage = field("e.g. 900");
    private final ComboBox<String> gender = combo("Select", "Male", "Female", "Other");
    private final ComboBox<String> education = combo("Select", "5th Pass Or Below", "8th Pass", "10th Pass",
            "12th Pass", "ITI", "Graduate");
    private final ComboBox<String> experience = combo("Select", "Fresher", "0-1 Years", "1-3 Years", "3-5 Years",
            "5+ Years");
    private final ComboBox<String> workerType = combo("Labour (General Labour)", "Mason", "Carpenter", "Electrician",
            "Plumber", "Painter", "ITI / Technician", "Site Supervisor");
    private final TextField city = field("e.g. Pune");
    private final ComboBox<String> state = combo("Maharashtra", "Delhi", "Gujarat", "Karnataka", "Uttar Pradesh",
            "Rajasthan", "Tamil Nadu", "Other");
    private final javafx.scene.control.PasswordField password = new javafx.scene.control.PasswordField();
    private final DatePicker dateOfBirth = new DatePicker();
    private final ImageView profileImage = new ImageView();
    private String profilePhotoUrl;
    private File selectedPhotoFile;
    private Runnable backAction;

    public Scene getSignUpScene(Runnable onBack) {
        backAction = onBack;
        dailyWage.setTextFormatter(new javafx.scene.control.TextFormatter<String>(
                change -> change.getControlNewText().matches("\\d{0,6}") ? change : null));
        dateOfBirth.setPromptText("DD/MM/YYYY");
        dateOfBirth.setStyle(inputStyle());

        Region bg = new Region();
        var res = getClass().getResource("/assets/images/worker_auth_bg.jpg");
        String bgUrl = (res != null) ? res.toExternalForm() : "";
        bg.setStyle("-fx-background-image: url('" + bgUrl + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center center;" +
                "-fx-background-repeat: no-repeat;");

        ScrollPane scroll = createForm();
        StackPane root = new StackPane(bg, scroll);
        return new Scene(root, 1400, 780);
    }

    private ScrollPane createForm() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 72, 72);
        logo.setPreserveRatio(true);
        VBox identity = new VBox(3, logo,
                label("DIHADI", "-fx-font-family:'Georgia';-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#735c00;"),
                label("Meri Dihadi ~ Mera Haq", "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-font-style:italic;-fx-text-fill:#685c52;"));
        identity.setAlignment(Pos.CENTER);
        VBox intro = new VBox(6,
                label("Create Worker Account", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                label("Enter your personal details to register your profile.", "-fx-font-size:14px;-fx-text-fill:#594f42;"));
        intro.setAlignment(Pos.CENTER);
        Button back = new Button("<");
        back.setStyle(
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:16px;-fx-font-weight:800;-fx-padding:6px 12px;-fx-cursor:hand;");
        back.setOnAction(event -> {
            Stage stage = (Stage) back.getScene().getWindow();
            if (backAction != null) backAction.run();
            else AppNavigator.open(stage, "Home");
        });
        Label personalDetails = label("PERSONAL DETAILS",
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-padding:9px 14px;");
        HBox formHeading = new HBox(10, back, personalDetails);
        formHeading.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(20, formHeading, identity, intro, createPhotoPicker(), createFields(), createActions());
        card.setMaxWidth(600);
        card.setPadding(new Insets(32, 36, 36, 36));
        card.setStyle(
                "-fx-background-color:rgba(255,253,248,0.84);" +
                "-fx-background-radius:22px;" +
                "-fx-border-color:rgba(212,175,55,0.45);" +
                "-fx-border-radius:22px;" +
                "-fx-border-width:1.5px;" +
                "-fx-effect:dropshadow(gaussian,rgba(30,24,16,0.18),28,0,0,10px);");
        VBox content = new VBox(card);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(24, 20, 48, 70));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        return scroll;
    }

    private HBox createPhotoPicker() {
        profileImage.setFitWidth(90);
        profileImage.setFitHeight(90);
        profileImage.setPreserveRatio(false);
        javafx.scene.shape.SVGPath userIcon = new javafx.scene.shape.SVGPath();
        userIcon.setContent("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z");
        userIcon.setFill(Color.web("#735c00"));
        StackPane avatar = new StackPane(userIcon, profileImage);
        avatar.setPrefSize(94, 94);
        avatar.setStyle(
                "-fx-background-color:rgba(212,175,55,0.12);-fx-background-radius:47px;-fx-border-color:rgba(212,175,55,0.45);-fx-border-radius:47px;-fx-border-width:2px;");
        Button change = secondaryButton("Upload Photo");
        change.setStyle(
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:999px;-fx-border-color:rgba(212,175,55,0.45);-fx-border-radius:999px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-weight:800;-fx-padding:9px 18px;-fx-cursor:hand;");
        change.setOnAction(event -> choosePhoto());
        HBox row = new HBox(18, avatar, change);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private GridPane createFields() {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(17);
        ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        left.setHgrow(Priority.ALWAYS);
        ColumnConstraints right = new ColumnConstraints();
        right.setPercentWidth(50);
        right.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(left, right);
        add(grid, 0, 0, "First name *", firstName, 2);
        add(grid, 0, 1, "Middle name", middleName, 1);
        add(grid, 1, 1, "Last name", lastName, 1);
        add(grid, 0, 2, "Gender *", gender, 1);
        add(grid, 1, 2, "Date of birth *", dateOfBirth, 1);
        HBox phone = new HBox();
        Label code = label("+91",
                "-fx-background-color:rgba(212,175,55,0.15);-fx-padding:10px 14px;-fx-text-fill:#735c00;-fx-font-weight:800;-fx-border-color:rgba(200,185,165,0.6);-fx-border-width:1.2px 0 1.2px 1.2px;-fx-background-radius:10px 0 0 10px;-fx-border-radius:10px 0 0 10px;");
        mobile.setStyle(inputStyle() + "-fx-background-radius:0 10px 10px 0;-fx-border-radius:0 10px 10px 0;");
        HBox.setHgrow(mobile, Priority.ALWAYS);
        phone.getChildren().addAll(code, mobile);
        add(grid, 0, 3, "Mobile number *", phone, 2);
        add(grid, 0, 4, "Alternate mobile number", alternateMobile, 2);
        add(grid, 0, 5, "Email", email, 2);
        add(grid, 0, 6, "Worker Type / Trade *", workerType, 2);
        add(grid, 0, 7, "State *", state, 1);
        add(grid, 1, 7, "City / Town", city, 1);
        add(grid, 0, 8, "Education / Qualification *", education, 1);
        add(grid, 1, 8, "Experience *", experience, 1);
        password.setPromptText("Create Dihadi Password");
        password.setStyle(inputStyle());
        add(grid, 0, 9, "Create Dihadi Password *", password, 2);
        Label expectedDay = label("", "-fx-font-size:14px;-fx-text-fill:#735c00;-fx-font-weight:800;");
        Label expectedMonth = label("", "-fx-font-size:14px;-fx-text-fill:#056a48;-fx-font-weight:800;");
        expectedDay.textProperty().bind(Bindings.createStringBinding(() -> "Your expected daily wages: ₹" + wageValue(),
                dailyWage.textProperty()));
        expectedMonth.textProperty().bind(Bindings.createStringBinding(
                () -> "Your expected monthly wages: ₹" + (wageValue() * 30), dailyWage.textProperty()));
        VBox wages = new VBox(8, label("Daily wages *", labelStyle()), dailyWage, expectedDay, expectedMonth);
        wages.setPadding(new Insets(18));
        wages.setStyle(
                "-fx-background-color:rgba(212,175,55,0.14);-fx-background-radius:12px;-fx-border-color:rgba(212,175,55,0.35);-fx-border-radius:12px;-fx-border-width:1px;");
        grid.add(wages, 0, 10, 2, 1);
        return grid;
    }

    private VBox createActions() {
        CheckBox consent = new CheckBox(
                "I authorise DIHADI to send notifications via SMS, email, RCS and other channels, as described in the Terms of Service and Privacy Policy.");
        consent.setWrapText(true);
        consent.setStyle("-fx-text-fill:#4c4637;-fx-font-size:13px;");
        Button submit = new Button("Continue");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-size:17px;-fx-font-weight:800;-fx-padding:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.18),10,0,0,3px);");
        submit.setOnAction(event -> submit(consent.isSelected()));
        Button login = new Button("Already having account? Login");
        login.setStyle("-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-weight:800;-fx-font-size:14px;-fx-cursor:hand;");
        login.setOnAction(event -> {
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(new WorkerLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        });
        VBox actions = new VBox(18, consent, submit, login);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(22, 0, 0, 0));
        return actions;
    }

    private void add(GridPane grid, int column, int row, String text, javafx.scene.Node input, int span) {
        VBox box = new VBox(7, label(text, labelStyle()), input);
        GridPane.setHgrow(box, Priority.ALWAYS);
        grid.add(box, column, row, span, 1);
    }

    private void choosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose profile photo");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(profileImage.getScene().getWindow());
        if (file != null) {
            selectedPhotoFile = file;
            profileImage.setImage(new Image(file.toURI().toString()));
            new Thread(() -> {
                ImageUploadController uploadController = new ImageUploadController();
                String url = uploadController.imageUpload(file);
                if (url != null) {
                    profilePhotoUrl = url;
                }
            }).start();
        }
    }

    private void submit(boolean consent) {
        if (firstName.getText().isBlank() || gender.getValue().equals("Select") || dateOfBirth.getValue() == null
                || !mobile.getText().matches("\\d{10}") || education.getValue().equals("Select")
                || experience.getValue().equals("Select") || wageValue() <= 0 || password.getText().isBlank()) {
            message(Alert.AlertType.WARNING, "Complete your profile",
                    "Enter all required fields, a valid 10-digit mobile number, a daily wage, and a password.");
            return;
        }
        if (!consent) {
            message(Alert.AlertType.WARNING, "Consent required", "Please authorise notifications to continue.");
            return;
        }

        try {
            if (selectedPhotoFile != null && profilePhotoUrl == null) {
                ImageUploadController uploadController = new ImageUploadController();
                profilePhotoUrl = uploadController.imageUpload(selectedPhotoFile);
            }

            WorkerController controller = new WorkerController();
            com.dihadi.model.Worker newWorker = new com.dihadi.model.Worker(
                    firstName.getText().trim(),
                    middleName.getText().trim(),
                    lastName.getText().trim(),
                    mobile.getText().trim(),
                    alternateMobile.getText().trim(),
                    email.getText().trim(),
                    gender.getValue(),
                    dateOfBirth.getValue().toString(),
                    education.getValue(),
                    experience.getValue(),
                    wageValue(),
                    profilePhotoUrl,
                    workerType.getValue(),
                    workerType.getValue(),
                    city.getText().trim().isEmpty() ? "Pune" : city.getText().trim(),
                    state.getValue());
            newWorker.setPassword(password.getText().trim());
            controller.addWorker(newWorker);

            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.setScene(new WorkerLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        } catch (Exception e) {
            e.printStackTrace();
            message(Alert.AlertType.ERROR, "Error",
                    "Failed to save profile. Please check your internet connection and try again.");
        }
    }

    private int wageValue() {
        try {
            return Integer.parseInt(dailyWage.getText());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(inputStyle());
        return field;
    }

    private static ComboBox<String> combo(String... values) {
        ComboBox<String> box = new ComboBox<>();
        box.getItems().addAll(values);
        box.setValue(values[0]);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(inputStyle());
        return box;
    }

    private static String inputStyle() {
        return "-fx-background-color: rgba(255, 255, 255, 0.85);" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: rgba(200, 185, 165, 0.6);" +
                "-fx-border-radius: 10px;" +
                "-fx-border-width: 1.2px;" +
                "-fx-text-fill: #1e1b15;" +
                "-fx-font-weight: 600;" +
                "-fx-prompt-text-fill: #7d7263;" +
                "-fx-padding: 10px 14px;" +
                "-fx-font-size: 14px;" +
                "-fx-pref-height: 44px;";
    }

    private static String labelStyle() {
        return "-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #2c251d;";
    }

    private static Label label(String text, String style) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color:#e9e2d7;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-weight:700;-fx-padding:10px 18px;-fx-cursor:hand;");
        return button;
    }

    private ImageView image(String path, double width, double height) {
        ImageView view = new ImageView(loadImage(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setSmooth(true);
        return view;
    }

    private Image loadImage(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void message(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.show();
    }
}
