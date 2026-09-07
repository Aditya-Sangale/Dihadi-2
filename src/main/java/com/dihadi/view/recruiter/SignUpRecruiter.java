package com.dihadi.view.recruiter;

import com.dihadi.controller.RecruiterController;
import com.dihadi.view.AppNavigator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Recruiter registration form, styled to match the worker sign-up interface
 * with full-bleed background imagery and a frosted glass card layout.
 */
public class SignUpRecruiter {

    // Form field references for submission
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
    private PasswordField passwordField;
    private Runnable backAction;

    public Scene getRecruiterSignUpScene(Runnable back) {
        this.backAction = back;

        Region bg = new Region();
        var res = getClass().getResource("/assets/images/recruiter_auth_bg.jpg");
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
                label("Create Recruiter Account", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),
                label("Enter your business details to register your organization.", "-fx-font-size:14px;-fx-text-fill:#594f42;"));
        intro.setAlignment(Pos.CENTER);

        Button back = new Button("← Back");
        back.setStyle(
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-cursor:hand;");
        back.setOnAction(event -> {
            Stage stage = null;
            try {
                if (back.getScene() != null && back.getScene().getWindow() instanceof Stage s) {
                    stage = s;
                }
            } catch (Exception ignored) {}

            if (stage == null) {
                for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                    if (w instanceof Stage s && s.isShowing()) {
                        stage = s;
                        break;
                    }
                }
            }

            if (stage != null) {
                AppNavigator.open(stage, "Home");
            } else if (backAction != null) {
                try {
                    backAction.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Label recruiterDetails = label("RECRUITER DETAILS",
                "-fx-background-color:rgba(212,175,55,0.18);-fx-background-radius:10px;-fx-border-color:rgba(212,175,55,0.4);-fx-border-radius:10px;-fx-border-width:1.2px;-fx-text-fill:#735c00;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-padding:9px 14px;");
        HBox formHeading = new HBox(10, back, recruiterDetails);
        formHeading.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(20, formHeading, identity, intro, createFields(), createActions());
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
        com.dihadi.view.ScrollUtils.style(scroll);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;-fx-border-width:0;");
        return scroll;
    }

    private GridPane createFields() {
        GridPane fields = new GridPane();
        fields.setHgap(18);
        fields.setVgap(17);
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
        add(fields, 0, 0, "First Name *", firstNameField, 2);

        middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        middleNameField.setStyle(inputStyle());
        add(fields, 0, 1, "Middle Name", middleNameField, 1);

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setStyle(inputStyle());
        add(fields, 1, 1, "Last Name", lastNameField, 1);

        genderField = new ComboBox<>();
        genderField.getItems().addAll("Male", "Female", "Other");
        genderField.setValue("Male");
        genderField.setMaxWidth(Double.MAX_VALUE);
        genderField.setStyle(inputStyle());
        add(fields, 0, 2, "Gender", genderField, 2);

        mobileField = new TextField();
        mobileField.setPromptText("10-digit mobile number");
        HBox phone = new HBox();
        Label code = label("+91",
                "-fx-background-color:rgba(212,175,55,0.15);-fx-padding:10px 14px;-fx-text-fill:#735c00;-fx-font-weight:800;-fx-border-color:rgba(200,185,165,0.6);-fx-border-width:1.2px 0 1.2px 1.2px;-fx-background-radius:10px 0 0 10px;-fx-border-radius:10px 0 0 10px;");
        mobileField.setStyle(inputStyle() + "-fx-background-radius:0 10px 10px 0;-fx-border-radius:0 10px 10px 0;");
        HBox.setHgrow(mobileField, Priority.ALWAYS);
        phone.getChildren().addAll(code, mobileField);
        add(fields, 0, 3, "Mobile Number *", phone, 1);

        alternateMobileField = new TextField();
        alternateMobileField.setPromptText("Alternate Mobile");
        alternateMobileField.setStyle(inputStyle());
        add(fields, 1, 3, "Alternate Mobile", alternateMobileField, 1);

        emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.setStyle(inputStyle());
        add(fields, 0, 4, "Email Address *", emailField, 1);

        alternateEmailField = new TextField();
        alternateEmailField.setPromptText("Optional");
        alternateEmailField.setStyle(inputStyle());
        add(fields, 1, 4, "Alternate Email", alternateEmailField, 1);

        companyNameField = new TextField();
        companyNameField.setPromptText("Your company name");
        companyNameField.setStyle(inputStyle());
        add(fields, 0, 5, "Company / Organisation Name *", companyNameField, 2);

        businessTypeField = new ComboBox<>();
        businessTypeField.getItems().addAll("Builder", "Developer", "General Contractor", "Sub-contractor");
        businessTypeField.setValue("Builder");
        businessTypeField.setMaxWidth(Double.MAX_VALUE);
        businessTypeField.setStyle(inputStyle());
        add(fields, 0, 6, "Business Type *", businessTypeField, 2);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(inputStyle());
        add(fields, 0, 7, "Create Dihadi Password *", passwordField, 2);

        return fields;
    }

    private VBox createActions() {
        CheckBox consent = new CheckBox(
                "I authorise DIHADI to send notifications via SMS, email, RCS and other channels, as described in the Terms of Service and Privacy Policy.");
        consent.setWrapText(true);
        consent.setStyle("-fx-text-fill:#4c4637;-fx-font-size:13px;");

        Button submit = new Button("Create Recruiter Account");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setStyle(
                "-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#1e1b15;-fx-font-size:17px;-fx-font-weight:800;-fx-padding:13px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.18),10,0,0,3px);");
        submit.setOnAction(e -> {
            if (!consent.isSelected()) {
                com.dihadi.view.NotificationToast.show("Consent Required",
                        "Please authorise notifications to continue.",
                        com.dihadi.view.NotificationToast.ToastType.ALERT);
                return;
            }
            submitRecruiter();
        });

        Button loginLink = new Button("Already having account? Login");
        loginLink.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:800;-fx-cursor:hand;");
        loginLink.setOnAction(e -> {
            if (!com.dihadi.view.SessionManager.checkAccessAllowed(com.dihadi.view.SessionManager.Role.RECRUITER)) return;
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new RecruiterLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        });

        VBox actions = new VBox(18, consent, submit, loginLink);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(20, 0, 0, 0));
        return actions;
    }

    private void submitRecruiter() {
        if (!com.dihadi.view.SessionManager.checkAccessAllowed(com.dihadi.view.SessionManager.Role.RECRUITER)) {
            return;
        }

        if (firstNameField.getText().isBlank() || mobileField.getText().isBlank()
                || emailField.getText().isBlank() || companyNameField.getText().isBlank()
                || passwordField.getText().isBlank()) {
            com.dihadi.view.NotificationToast.show("Complete Your Details",
                    "Please fill in all required fields (First Name, Mobile, Email, Company Name, Password).",
                    com.dihadi.view.NotificationToast.ToastType.ALERT);
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

            com.dihadi.view.SessionManager.clearAllSessions();

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            com.dihadi.view.NotificationToast.show(stage, "Account Created",
                    "Your recruiter account has been registered. Please login to continue.",
                    com.dihadi.view.NotificationToast.ToastType.SUCCESS);
            stage.setScene(new RecruiterLoginPage(() -> AppNavigator.open(stage, "Home")).getLoginScene());
        } catch (Exception ex) {
            ex.printStackTrace();
            com.dihadi.view.NotificationToast.show("Registration Failed",
                    "Failed to create account. Please check your internet connection and try again.",
                    com.dihadi.view.NotificationToast.ToastType.ERROR);
        }
    }

    private void add(GridPane grid, int column, int row, String text, javafx.scene.Node input, int span) {
        VBox box = new VBox(7, label(text, labelStyle()), input);
        GridPane.setHgrow(box, Priority.ALWAYS);
        grid.add(box, column, row, span, 1);
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
}
