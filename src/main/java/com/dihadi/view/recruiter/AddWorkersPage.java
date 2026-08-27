package com.dihadi.view.recruiter;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Workforce requirement screen shown after a recruiter saves a project. */
public class AddWorkersPage {
    private static final List<String> VISUALS = List.of(
            "/assets/images/recruiter/slide-01.jpeg", "/assets/images/recruiter/slide-02.jpeg",
            "/assets/images/recruiter/slide-03.jpeg", "/assets/images/recruiter/slide-04.jpeg",
            "/assets/images/recruiter/slide-05.jpeg");

    private static final String[] LABOUR_SKILLS = { "General Labour", "Material Shifting Helper",
            "Factory Worker Helper", "Heavy Building Helper", "Lanter Worker Helper", "Road Construction",
            "Sewage Worker Helper", "Concrete Mixer Labour", "Mason Helper", "Loading & Unloading", "Stone Crusher",
            "Mines Excavator", "Manual Scavengers", "Bar Bender Helper", "Shuttering Helper", "Gravels Segmentation" };
    private static final String[] MASON_SKILLS = { "Brick Mason", "Lanter Mason", "Plastering Mason", "Stone Mason",
            "Tiles Mason", "Cement Concrete Mason", "Bar Bender", "Shuttering", "Marbles Mason", "Flooring",
            "Composite Mason", "Garters/Columns Mason", "Lime Concrete Mason", "Scaffolding", "Reinforcement Fitter" };
    private static final String[] CARPENTER_SKILLS = { "Furniture Carpenter", "Door & Window Carpenter",
            "Modular Furniture", "Shuttering Carpenter", "Wood Polishing", "Kitchen Carpenter", "False Ceiling",
            "Flooring Carpenter", "Carpenter Helper", "Wood Carving", "Plywood Work", "Other Woodwork" };
    private static final String[] ELECTRICIAN_SKILLS = { "Electrician", "Power Testing", "Electrician Helper",
            "Cable Jointer", "Cable Jointer Helper", "Lineman", "Wireman", "Sub Station Attendant", "HVAC Technician",
            "Fire Safety Technician" };
    private static final String[] PLUMBER_SKILLS = { "Basin & Sink", "Bath Fixing", "Blockage", "Water Tank",
            "Bathroom Fitting", "Submersible / Motor", "Pipe Fittings", "Testing & Tapping", "Water Tank Cleaning",
            "Plumber Helper", "GI Pipe Installation", "Gas Meter & Valve", "Copper Pipe Install", "LPG To PNG Conv" };
    private static final String[] PAINTER_SKILLS = { "Enamel Painting", "Roller Painting", "POP Work", "Wall Putty",
            "Stencil Work", "Texture Painting", "Waterproofing", "Wood Polish" };
    private static final String[] ITI_SKILLS = { "Pump Operator", "Fitter Technician", "Belt Jointer",
            "Lift & Escalator Mechanic", "Electrical ITI Technician", "Woodwork Technician", "Fabricator", "Welder", "Machinist" };
    private static final String[] SUPERVISOR_SKILLS = { "Site Supervisor", "Construction Foreman",
            "Electrical Supervisor", "Painting Supervisor", "Masonry Supervisor", "Safety Supervisor", "Plumbing Supervisor",
            "Carpenter Supervisor", "General Labour Supervisor", "Quality Supervisor", "Tiles Mason Supervisor" };
    private Timeline slideshow;
    private int visualIndex;
    private ComboBox<String> priorityField, workerTypeField, skillField, quantityField;
    private TextField wageField;
    private String projectId, projectName, contactName, mobile, email, projectAddress, projectImage;
    private CheckBox waterBox, electricityBox, accommodationBox, transportationBox;

    public AddWorkersPage() {
    }

    public AddWorkersPage(String projectId) {
        this.projectId = projectId; this.projectName = "Project"; this.contactName = ""; this.mobile = ""; this.email = ""; this.projectAddress = ""; this.projectImage = "/assets/images/recruiter/slide-03.jpeg";
    }

    public AddWorkersPage(String projectId, String projectName, String contactName, String mobile, String email, String projectAddress, String projectImage) {
        this.projectId = projectId; this.projectName = projectName; this.contactName = contactName; this.mobile = mobile; this.email = email; this.projectAddress = projectAddress; this.projectImage = projectImage;
    }

    public Scene getAddWorkersScene(Runnable backAction) {
        VBox formColumn = new VBox(26, identity(), welcome(), workerCard(backAction), addButton(backAction));
        formColumn.setAlignment(Pos.TOP_CENTER);
        formColumn.setPadding(new Insets(48, 50, 42, 50));
        formColumn.setPrefWidth(700);

        ScrollPane scroll = new ScrollPane(formColumn);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:#fff8f0;-fx-border-width:0;");
        HBox.setHgrow(scroll, Priority.ALWAYS);

        StackPane imagePanel = imagePanel();
        HBox page = new HBox(scroll, imagePanel);
        page.setStyle("-fx-background-color:#fff8f0;");
        Scene scene = new Scene(page, 1400, 780);
        return scene;
    }

    private VBox identity() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 86, 86);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        VBox identity = new VBox(4, logo,
                text("DIHADI", "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:800;-fx-text-fill:#735c00;"),
                text("Mera Haq ~ Meri Dihadi",
                        "-fx-font-family:Georgia;-fx-font-size:17px;-fx-font-style:italic;-fx-text-fill:#4c4637;"));
        identity.setAlignment(Pos.CENTER);
        return identity;
    }

    private VBox welcome() {
        VBox welcome = new VBox(7,
                text("Welcome to DIHADI", "-fx-font-size:21px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                text("Go ahead. Add your workforce requirement details", "-fx-font-size:16px;-fx-text-fill:#4c4637;"));
        welcome.setAlignment(Pos.CENTER);
        return welcome;
    }

    private VBox workerCard(Runnable backAction) {
        ImageView illustration = image("/assets/images/recruiter/add-workers-illustration.png", 500, 188);
        illustration.setPreserveRatio(false);
        illustration.setFitWidth(500);
        illustration.setFitHeight(188);
        StackPane illustrationFrame = new StackPane(illustration);
        illustrationFrame.setPrefHeight(188);
        illustrationFrame.setMaxWidth(Double.MAX_VALUE);
        Rectangle clip = new Rectangle(500, 188);
        clip.widthProperty().bind(illustrationFrame.widthProperty());
        illustrationFrame.setClip(clip);
        illustrationFrame.setStyle("-fx-background-color:#f4ede2;-fx-background-radius:8px;");

        ComboBox<String> priority = priorityField = combo("Very Urgent", "Urgent", "Not Urgent");
        ComboBox<String> workerType = workerTypeField = combo("Labour (General Labour)", "Mason", "Carpenter",
                "Electrician", "Plumber", "Painter", "ITI / Technician", "Site Supervisor");
        ComboBox<String> skill = skillField = new ComboBox<>();
        skill.setMaxWidth(Double.MAX_VALUE);
        skill.setStyle(inputStyle());

        skill.getItems().addAll(LABOUR_SKILLS);
        skill.setValue(LABOUR_SKILLS[0]);

        workerType.valueProperty().addListener((obs, oldVal, newVal) -> {
            skill.getItems().clear();
            if (newVal == null)
                return;
            switch (newVal) {
                case "Labour (General Labour)" -> skill.getItems().addAll(LABOUR_SKILLS);
                case "Mason" -> skill.getItems().addAll(MASON_SKILLS);
                case "Carpenter" -> skill.getItems().addAll(CARPENTER_SKILLS);
                case "Electrician" -> skill.getItems().addAll(ELECTRICIAN_SKILLS);
                case "Plumber" -> skill.getItems().addAll(PLUMBER_SKILLS);
                case "Painter" -> skill.getItems().addAll(PAINTER_SKILLS);
                case "ITI / Technician" -> skill.getItems().addAll(ITI_SKILLS);
                case "Site Supervisor" -> skill.getItems().addAll(SUPERVISOR_SKILLS);
            }
            if (!skill.getItems().isEmpty()) {
                skill.setValue(skill.getItems().get(0));
            }
        });
        ComboBox<String> quantity = quantityField = combo("50", "100", "200");
        TextField wage = wageField = input("");

        HBox numbers = new HBox(16, field("No. of worker *required", quantity),
                wageField("Daily wages *required", wage));
        for (javafx.scene.Node node : numbers.getChildren())
            HBox.setHgrow(node, Priority.ALWAYS);

        waterBox = new CheckBox();
        electricityBox = new CheckBox();
        accommodationBox = new CheckBox();
        transportationBox = new CheckBox();

        VBox facilities = new VBox(14,
                text("Select option that you are providing to workers", "-fx-font-size:15px;-fx-text-fill:#4c4637;"),
                toggle("Water Facility", waterBox, true),
                toggle("Electricity Facility", electricityBox, false),
                toggle("Accomodation Facility", accommodationBox, true),
                toggle("Transportation Facility", transportationBox, false));
        facilities.setPadding(new Insets(20, 0, 0, 0));
        facilities.setStyle("-fx-border-color:#e9e2d7 transparent transparent transparent;-fx-border-width:1px 0 0 0;");

        VBox card = new VBox(23, illustrationFrame,
                field("Hiring Priority *required", priority), field("Select Worker Type *required", workerType),
                field("Select sub skill *required", skill), numbers, facilities);
        card.setMaxWidth(540);
        card.setPadding(new Insets(28));
        card.setStyle(
                "-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");
        return card;
    }

    private VBox addButton(Runnable backAction) {
        Button button = new Button("Add worker");
        button.setMaxWidth(540);
        button.setPrefHeight(52);
        button.setStyle(
                "-fx-background-color:#735c00;-fx-background-radius:999px;-fx-text-fill:#f6d676;-fx-font-size:16px;-fx-font-weight:700;-fx-cursor:hand;");
        button.setOnAction(e -> {
            if (priorityField.getValue() == null || workerTypeField.getValue() == null || skillField.getValue() == null
                    || quantityField.getValue() == null || wageField.getText().isBlank()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Required details");
                alert.setHeaderText(null);
                alert.setContentText("Please complete all fields marked *required before adding a worker.");
                alert.show();
                return;
            }
            double wages = 0;
            try {
                wages = Double.parseDouble(wageField.getText().trim());
                if (wages < 400 || wages > 2000)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Invalid daily wages");
                alert.setHeaderText(null);
                alert.setContentText("Daily wages must be between Rs.400 and Rs.2000.");
                alert.show();
                return;
            }

            // Save details to Firebase
            String requirementId = java.util.UUID.randomUUID().toString();
            int qty = Integer.parseInt(quantityField.getValue());
            com.dihadi.model.WorkforceRequirement req = new com.dihadi.model.WorkforceRequirement(
                    requirementId,
                    projectId != null ? projectId : "",
                    priorityField.getValue(),
                    workerTypeField.getValue(),
                    skillField.getValue(),
                    qty,
                    wages,
                    waterBox.isSelected(),
                    electricityBox.isSelected(),
                    accommodationBox.isSelected(),
                    transportationBox.isSelected());
            new com.dihadi.controller.WorkforceRequirementController().addRequirement(req);

            javafx.stage.Stage stage = (javafx.stage.Stage) button.getScene().getWindow();
            stage.setScene(new ProjectDetailsPage(projectName, contactName, mobile, email, projectAddress, priorityField.getValue(), workerTypeField.getValue(), skillField.getValue(), quantityField.getValue(), wageField.getText(), projectImage, facilitiesText()).getScene(() -> com.dihadi.view.AppNavigator.open(stage, "Recruiter")));
        });
        Button close = new Button("Close");
        close.setStyle(
                "-fx-background-color:transparent;-fx-text-fill:#735c00;-fx-font-size:16px;-fx-font-weight:700;-fx-cursor:hand;");
        close.setOnAction(e -> {
            if (backAction != null)
                backAction.run();
        });
        VBox area = new VBox(10, button, close);
        area.setMaxWidth(540);
        area.setAlignment(Pos.CENTER);
        return area;
    }

    private HBox toggle(String name, CheckBox check, boolean selected) {
        Label label = text(name, "-fx-font-size:15px;-fx-text-fill:#1e1b15;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        check.setSelected(selected);
        check.setStyle("-fx-font-size:18px;-fx-cursor:hand;-fx-text-fill:#735c00;");
        HBox row = new HBox(12, label, spacer, check);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private String facilitiesText() {
        return "Water: " + (waterBox.isSelected() ? "Provided" : "Not provided") + " | Electricity: " + (electricityBox.isSelected() ? "Provided" : "Not provided") + " | Accommodation: " + (accommodationBox.isSelected() ? "Provided" : "Not provided") + " | Transportation: " + (transportationBox.isSelected() ? "Provided" : "Not provided");
    }

    private VBox wageField(String label, TextField input) {
        Label note = text("Wages must be between Rs.400 and Rs.2000", "-fx-font-size:11px;-fx-text-fill:#ba1a1a;");
        return new VBox(6, text(label, labelStyle()), input, note);
    }

    private VBox field(String label, javafx.scene.Node control) {
        VBox field = new VBox(7, text(label, labelStyle()), control);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private ComboBox<String> combo(String first, String... rest) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().add(first);
        combo.getItems().addAll(rest);
        combo.setValue(first);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.setStyle(inputStyle());
        return combo;
    }

    private TextField input(String value) {
        TextField input = new TextField(value);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setStyle(inputStyle());
        return input;
    }

    private StackPane imagePanel() {
        ImageView photo = image(VISUALS.get(0), 650, 720);
        photo.setPreserveRatio(false);
        photo.setFitWidth(650);
        photo.setFitHeight(720);
        StackPane frame = new StackPane(photo);
        frame.setPrefSize(650, 720);
        frame.setMaxSize(650, 720);
        Rectangle clip = new Rectangle(650, 720);
        clip.setArcWidth(60);
        clip.setArcHeight(60);
        frame.setClip(clip);
        frame.setStyle(
                "-fx-background-color:#eee7dc;-fx-background-radius:30px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.15),24,0,0,7px);");
        StackPane panel = new StackPane(frame);
        panel.setPrefWidth(700);
        panel.setMinWidth(520);
        panel.setPadding(new Insets(28, 30, 28, 0));
        panel.setStyle("-fx-background-color:#fff8f0;");
        startSlideshow(photo);
        return panel;
    }

    private void startSlideshow(ImageView image) {
        slideshow = new Timeline(new KeyFrame(Duration.seconds(7), event -> {
            visualIndex = (visualIndex + 1) % VISUALS.size();
            image.setImage(load(VISUALS.get(visualIndex)));
        }));
        slideshow.setCycleCount(Timeline.INDEFINITE);
        slideshow.play();
    }

    private ImageView image(String path, double width, double height) {
        ImageView image = new ImageView(load(path));
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setSmooth(true);
        return image;
    }

    private Image load(String path) {
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private Label text(String value, String style) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + style);
        return label;
    }

    private String labelStyle() {
        return "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1e1b15;";
    }

    private String inputStyle() {
        return "-fx-background-color:#f4ede2;-fx-background-radius:8px;-fx-border-color:transparent;-fx-border-radius:8px;-fx-padding:11px 12px;-fx-font-size:14px;-fx-text-fill:#1e1b15;";
    }
}