package com.dihadi.view;

import java.util.ArrayList;
import java.util.List;

import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkforceRequirementController;
import com.dihadi.model.Project;
import com.dihadi.model.WorkforceRequirement;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Dedicated Informational Project Details View for Explore Projects page.
 * Loads and displays real live project images (including Cloudinary uploads & site banners),
 * authentic site addresses, and comprehensive contractor requirements without the worker application form.
 */
public class ExploreProjectDetailsPage {
    private static final String SURFACE = "#f3e7ce";
    private static final String CARD_BG = "#ffffff";
    private static final String BORDER = "#d0c5af";
    private static final String GOLD = "#735c00";
    private static final String ACCENT_GOLD = "#d4af37";

    private final String projectId;
    private final String projectName;
    private final String location;
    private final String company;
    private final String trade;
    private final String wage;
    private final String workersNeeded;
    private final String status;
    private final String sector;
    private final String recruiterPhone;
    private final String requirementId;
    private final String imagePath;
    private final List<String> initialImageUrls;
    private final boolean hasWater;
    private final boolean hasPower;
    private final boolean hasStay;
    private final boolean hasTransport;
    private final Runnable backAction;

    private final List<String> liveImageUrls = new ArrayList<>();
    private ImageView siteImageView;
    private Label addressLine1Label;
    private Label addressLine2Label;
    private Label landmarkLabel;
    private Label supervisorLabel;
    private Timeline photoTimeline;
    private int photoIndex = 0;

    public ExploreProjectDetailsPage(
            String projectId,
            String projectName,
            String location,
            String company,
            String trade,
            String wage,
            String workersNeeded,
            String status,
            String sector,
            String recruiterPhone,
            String requirementId,
            String imagePath,
            List<String> imageUrls,
            boolean hasWater,
            boolean hasPower,
            boolean hasStay,
            boolean hasTransport,
            Runnable backAction
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.location = location;
        this.company = company;
        this.trade = trade;
        this.wage = wage;
        this.workersNeeded = workersNeeded;
        this.status = status;
        this.sector = sector;
        this.recruiterPhone = recruiterPhone;
        this.requirementId = requirementId;
        this.imagePath = imagePath;
        this.initialImageUrls = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls : List.of(val(imagePath, "/assets/images/explore/explore_slide_1.jpg"));
        this.hasWater = hasWater;
        this.hasPower = hasPower;
        this.hasStay = hasStay;
        this.hasTransport = hasTransport;
        this.backAction = backAction;

        this.liveImageUrls.addAll(this.initialImageUrls);
    }

    public Scene getScene() {
        BorderPane page = new BorderPane();
        page.setTop(header());
        page.setCenter(body());
        page.setStyle("-fx-background-color:" + SURFACE + ";");
        StackPane root = new StackPane(page);
        root.setPadding(new Insets(24));
        root.setBackground(new Background(new BackgroundFill(Color.web(SURFACE), CornerRadii.EMPTY, Insets.EMPTY)));

        // Load complete Firestore real site details asynchronously
        loadFirestoreProjectDetails();

        return new Scene(root, 1400, 780, Color.web(SURFACE));
    }

    private BorderPane header() {
        ImageView logo = new ImageView(load("/assets/logo/dihadi logo.jpeg"));
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        Label name = label("DIHADI",
                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;");
        HBox brand = new HBox(10, logo, name);
        brand.setAlignment(Pos.CENTER_LEFT);

        HBox navigation = new HBox(12,
                navButton("Home", false, () -> navigateTo("Home")),
                navButton("Business", false, () -> navigateTo("Business")),
                navButton("Worker", false, () -> navigateTo("Worker")),
                navButton("Recruiter", false, () -> navigateTo("Recruiter")),
                navButton("About Us", false, () -> navigateTo("About Us")),
                navButton("Contact Us", false, () -> navigateTo("Contact Us")));
        navigation.setAlignment(Pos.CENTER);

        Button admin = AppNavigator.createHeaderAdminButton();
        BorderPane header = new BorderPane();
        header.setLeft(brand);
        header.setCenter(navigation);
        header.setRight(new HBox(10, admin));
        header.setPadding(new Insets(16, 24, 14, 24));
        header.setStyle(
                "-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return header;
    }

    private ScrollPane body() {
        VBox content = new VBox(26,
                topNavigationBanner(),
                heroProjectHeader(),
                metricsRow(),
                detailedSpecifications(),
                footer()
        );
        content.setMaxWidth(1180);
        content.setPadding(new Insets(24, 24, 36, 24));
        content.setAlignment(Pos.TOP_CENTER);

        StackPane centeredWrap = new StackPane(content);
        centeredWrap.setAlignment(Pos.TOP_CENTER);
        centeredWrap.setStyle("-fx-background-color: " + SURFACE + ";");

        ScrollPane scroll = new ScrollPane(centeredWrap);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: " + SURFACE + "; -fx-background-color: " + SURFACE + "; -fx-border-width: 0;");
        return scroll;
    }

    private HBox topNavigationBanner() {
        Button backBtn = new Button("← Back to Explore Projects");
        backBtn.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:8px 18px;-fx-border-color:#d0c5af;-fx-border-radius:12px;-fx-cursor:hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:12px;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:8px 18px;-fx-border-color:#d4af37;-fx-border-radius:12px;-fx-cursor:hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color:#ffffff;-fx-background-radius:12px;-fx-text-fill:#735c00;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:8px 18px;-fx-border-color:#d0c5af;-fx-border-radius:12px;-fx-cursor:hand;"));
        backBtn.setOnAction(e -> {
            if (photoTimeline != null) photoTimeline.stop();
            if (backAction != null) backAction.run();
        });

        Label breadcrumb = label("Explore Projects   ›   Project Information & Real Site Imagery", "-fx-font-size:13px;-fx-font-weight:600;-fx-text-fill:#685c52;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(16, backBtn, spacer, breadcrumb);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setMaxWidth(1180);
        return bar;
    }

    private VBox heroProjectHeader() {
        Label eyebrow = label("VERIFIED ENTERPRISE PROJECT SITE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1.5px;");

        Label title = label(projectName,
                "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        title.setWrapText(true);
        title.setMaxWidth(1100);

        Label locBadge = label(location, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-background-color:#faf3e8;-fx-background-radius:10px;-fx-padding:4px 12px;-fx-border-color:#d0c5af;-fx-border-radius:10px;");
        Label devBadge = label(company + " (Verified Developer)", "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1565c0;-fx-background-color:#e3f2fd;-fx-background-radius:10px;-fx-padding:4px 12px;");
        
        boolean isUrgent = status != null && status.toLowerCase().contains("urgent");
        Label statusBadge = label(isUrgent ? "URGENT HIRING" : "ACTIVE PROJECT SITE",
                "-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:" + (isUrgent ? "#ffffff" : "#2e7d32") + ";"
                        + "-fx-background-color:" + (isUrgent ? "#c62828" : "#e8f5e9") + ";"
                        + "-fx-background-radius:10px;-fx-padding:4px 12px;");

        Label sectorBadge = label(sector, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-padding:4px 12px;");

        HBox badges = new HBox(12, locBadge, devBadge, statusBadge, sectorBadge);
        badges.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(14, eyebrow, title, badges);
        card.setPadding(new Insets(26, 32, 26, 32));
        card.setMaxWidth(1180);
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:22px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:22px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),18,0,0,6px);");
        return card;
    }

    private HBox metricsRow() {
        HBox wageCard = highlightMetricCard("Daily Wage Rate", wage, "Guaranteed Direct Escrow Protected", "#735c00", "rgba(115,92,0,0.06)");
        HBox openingsCard = highlightMetricCard("Openings / Workforce", workersNeeded, "Active Daily Requirement", "#1e1b15", "rgba(30,27,21,0.05)");
        HBox tradeCard = highlightMetricCard("Primary Trade Skill", trade, "Certified Skill Requirement", "#1565c0", "rgba(21,101,192,0.06)");

        HBox row = new HBox(20, wageCard, openingsCard, tradeCard);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(1180);
        return row;
    }

    private HBox highlightMetricCard(String title, String val, String sub, String valColor, String bg) {
        Label t = label(title, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        Label v = label(val, "-fx-font-family:'Georgia';-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:" + valColor + ";");
        Label s = label("Verified " + sub, "-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#2e7d32;");

        VBox box = new VBox(6, t, v, s);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(20, 24, 20, 24));
        box.setPrefWidth(380);
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");

        HBox wrapper = new HBox(box);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private HBox detailedSpecifications() {
        // Left Column: Project Overview & Site Address
        VBox leftCol = new VBox(18);
        leftCol.setPrefWidth(570);

        supervisorLabel = label(recruiterPhone != null && !recruiterPhone.isBlank() ? "Site Project Lead (PoC: " + recruiterPhone + ")" : "Project Supervisor (Verified PoC)", "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");

        VBox specBox = new VBox(14,
                sectionHeading("Project Specifications"),
                specItem("Project Title", projectName),
                specItem("Developer / Enterprise", company),
                specItem("Project Sector", sector),
                specItem("Work Model", "Direct Contractor Labour Supply"),
                specItem("Primary Trade", trade),
                specItemNode("Site Contact Person", supervisorLabel)
        );
        specBox.setPadding(new Insets(22));
        specBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");

        addressLine1Label = label("Site Address: " + location, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        addressLine2Label = label("Sector Zone: Core Construction & Staging Facility", "-fx-font-size:13px;-fx-font-weight:600;-fx-text-fill:#5d5045;");
        landmarkLabel = label("Landmark: Arterial Infrastructure Corridor", "-fx-font-size:13px;-fx-font-weight:600;-fx-text-fill:#735c00;");

        VBox addressBox = new VBox(14,
                sectionHeading("Site Location & Access"),
                specItem("City & State", location),
                specItemNode("Main Address", addressLine1Label),
                specItemNode("Staging Area", addressLine2Label),
                specItemNode("Access Landmark", landmarkLabel),
                specItem("Reporting Zone", "Main Site Security Gate 1 & Supervisor Cabin"),
                specItem("Safety Clearance", "Standard PPE & Onboarding Safety Induction Provided")
        );
        addressBox.setPadding(new Insets(22));
        addressBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");

        leftCol.getChildren().addAll(specBox, addressBox);

        // Right Column: Facilities & Amenities Breakdown + Visual Site Frame
        VBox rightCol = new VBox(18);
        rightCol.setPrefWidth(570);

        VBox amenitiesBox = new VBox(14,
                sectionHeading("Site Amenities & Facilities"),
                facilityStatusCard("Clean Drinking Water Facility", hasWater, "RO filtered drinking water stations across work areas."),
                facilityStatusCard("24x7 Electricity & Power", hasPower, "Dedicated generator power backup for power tools."),
                facilityStatusCard("Worker Accommodation / Stay", hasStay, "On-site barracks or nearby authorized worker lodging."),
                facilityStatusCard("Site Transportation", hasTransport, "Daily shuttle service from major transit junctions."),
                facilityStatusCard("Mandatory Safety Gear (PPE)", true, "Helmets, safety vests, gloves & boots provided at check-in."),
                facilityStatusCard("Canteen & Rest Zone", true, "Covered dining and shaded rest areas on site premises.")
        );
        amenitiesBox.setPadding(new Insets(22));
        amenitiesBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");

        // High-Quality Project Site Visual Card loading the REAL project image
        siteImageView = new ImageView(load(val(imagePath, "/assets/images/explore/explore_slide_1.jpg")));
        siteImageView.setFitWidth(524);
        siteImageView.setFitHeight(270);
        siteImageView.setPreserveRatio(false);
        siteImageView.setSmooth(true);
        Rectangle imgClip = new Rectangle(524, 270);
        imgClip.setArcWidth(20);
        imgClip.setArcHeight(20);
        siteImageView.setClip(imgClip);

        StackPane imgFrame = new StackPane(siteImageView);
        imgFrame.setStyle("-fx-background-color:#161311;-fx-background-radius:20px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:20px;");

        VBox imgBox = new VBox(12,
                sectionHeading("Site Infrastructure Visual"),
                imgFrame
        );
        imgBox.setPadding(new Insets(22));
        imgBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-width:1.5px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),14,0,0,4px);");

        rightCol.getChildren().addAll(amenitiesBox, imgBox);

        HBox specs = new HBox(20, leftCol, rightCol);
        specs.setAlignment(Pos.TOP_CENTER);
        specs.setMaxWidth(1180);
        return specs;
    }

    private void loadFirestoreProjectDetails() {
        new Thread(() -> {
            try {
                List<Project> allProjects = new ProjectController().getAllProjects();
                Project matched = null;
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (projectId != null && projectId.equals(p.getProjectId())) {
                            matched = p;
                            break;
                        }
                        if (recruiterPhone != null && !recruiterPhone.isBlank() && p.getMobile() != null) {
                            String cleanA = recruiterPhone.replaceAll("\\D", "");
                            String cleanB = p.getMobile().replaceAll("\\D", "");
                            if (!cleanA.isEmpty() && cleanA.equals(cleanB)) {
                                matched = p;
                                break;
                            }
                        }
                    }
                }

                if (matched != null) {
                    final Project fp = matched;
                    Platform.runLater(() -> {
                        if (fp.getAddressLine1() != null && !fp.getAddressLine1().isBlank()) {
                            addressLine1Label.setText(fp.getAddressLine1());
                        }
                        if (fp.getAddressLine2() != null && !fp.getAddressLine2().isBlank()) {
                            addressLine2Label.setText(fp.getAddressLine2());
                        }
                        if (fp.getLandmark() != null && !fp.getLandmark().isBlank()) {
                            landmarkLabel.setText("Landmark: " + fp.getLandmark());
                        }
                        if (fp.getContactName() != null && !fp.getContactName().isBlank()) {
                            supervisorLabel.setText(fp.getContactName() + " (PoC: " + val(fp.getMobile(), recruiterPhone) + ")");
                        }

                        // Load real images uploaded to Cloudinary
                        if (fp.getImageUrls() != null && !fp.getImageUrls().isEmpty()) {
                            liveImageUrls.clear();
                            for (String u : fp.getImageUrls()) {
                                if (u != null && !u.isBlank()) liveImageUrls.add(u);
                            }
                            if (!liveImageUrls.isEmpty()) {
                                Image newImg = load(liveImageUrls.get(0));
                                if (newImg != null) siteImageView.setImage(newImg);

                                if (liveImageUrls.size() > 1) {
                                    if (photoTimeline != null) photoTimeline.stop();
                                    photoTimeline = new Timeline(new KeyFrame(Duration.millis(3000), e -> {
                                        photoIndex = (photoIndex + 1) % liveImageUrls.size();
                                        Image slideImg = load(liveImageUrls.get(photoIndex));
                                        if (slideImg != null) siteImageView.setImage(slideImg);
                                    }));
                                    photoTimeline.setCycleCount(Timeline.INDEFINITE);
                                    photoTimeline.play();
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private HBox specItem(String key, String value) {
        Label k = label(key, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        k.setPrefWidth(160);

        Label v = label(value, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        v.setWrapText(true);
        HBox.setHgrow(v, Priority.ALWAYS);

        HBox row = new HBox(12, k, v);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        row.setStyle("-fx-border-color:transparent transparent #f0e6d6 transparent;-fx-border-width:0 0 1px 0;");
        return row;
    }

    private HBox specItemNode(String key, Label valNode) {
        Label k = label(key, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        k.setPrefWidth(160);
        valNode.setWrapText(true);
        HBox.setHgrow(valNode, Priority.ALWAYS);

        HBox row = new HBox(12, k, valNode);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        row.setStyle("-fx-border-color:transparent transparent #f0e6d6 transparent;-fx-border-width:0 0 1px 0;");
        return row;
    }

    private HBox facilityStatusCard(String title, boolean available, String note) {
        Label t = label(title, "-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label n = label(note, "-fx-font-size:11px;-fx-font-weight:500;-fx-text-fill:#685c52;");
        VBox texts = new VBox(2, t, n);
        texts.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusTag = label(available ? "✓ AVAILABLE" : "— NOT REQUIRED",
                "-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + (available ? "#2e7d32" : "#8d7f72") + ";"
                        + "-fx-background-color:" + (available ? "#e8f5e9" : "#f5f0e8") + ";"
                        + "-fx-background-radius:8px;-fx-padding:3px 8px;");

        HBox box = new HBox(10, texts, spacer, statusTag);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color:#faf4ea;-fx-background-radius:10px;-fx-border-color:#e8ddcb;-fx-border-radius:10px;");
        return box;
    }

    private Label sectionHeading(String title) {
        return label(title, "-fx-font-family:'Georgia';-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
    }

    private VBox footer() {
        ImageView logo = new ImageView(load("/assets/logo/dihadi logo.jpeg"));
        logo.setFitWidth(52);
        logo.setFitHeight(52);
        logo.setPreserveRatio(true);
        Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
        Label promise = label(
                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
        promise.setWrapText(true);
        promise.setMaxWidth(310);
        VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
        identity.setPrefWidth(360);
        VBox explore = footerColumn("Explore", "Home", () -> navigateTo("Home"), "Find Work", () -> navigateTo("Worker"), "About Us",
                () -> navigateTo("About Us"));
        VBox contact = footerColumn("Contact", "9561789599", () -> navigateTo("Contact Us"), "info@meridihadi.com",
                () -> navigateTo("Contact Us"), "Pune, Maharashtra", () -> navigateTo("Contact Us"));
        HBox top = new HBox(64, identity, explore, contact);
        top.setAlignment(Pos.TOP_LEFT);
        VBox out = new VBox(22, top, label("© 2026 DIHADI  •  Meri Dihadi ~ Mera Haq. All rights reserved.",
                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
        out.setPadding(new Insets(32, 42, 24, 42));
        out.setMaxWidth(1180);
        out.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
        return out;
    }

    private VBox footerColumn(String heading, String textOne, Runnable actionOne, String textTwo, Runnable actionTwo,
            String textThree, Runnable actionThree) {
        VBox column = new VBox(7, label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                footerLink(textOne, actionOne), footerLink(textTwo, actionTwo), footerLink(textThree, actionThree));
        column.setPrefWidth(180);
        return column;
    }

    private Button footerLink(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.setStyle(
                "-fx-background-color:transparent;-fx-padding:2 0;-fx-text-fill:#f8f0e2;-fx-opacity:.82;-fx-font-size:13px;-fx-cursor:hand;");
        return button;
    }

    private void navigateTo(String destination) {
        if (photoTimeline != null) photoTimeline.stop();
        for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage stage) {
                AppNavigator.open(stage, destination);
                return;
            }
        }
    }

    private Button navButton(String t, boolean active, Runnable action) {
        Button b = new Button(t);
        b.setOnAction(e -> {
            if (photoTimeline != null) photoTimeline.stop();
            action.run();
        });
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (active ? "#735c00" : "#4d4635") + ";-fx-border-color:"
                + (active ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI',sans-serif;" + s);
        return l;
    }

    private Image load(String path) {
        try {
            if (path == null || path.isBlank()) {
                return loadResource("/assets/images/explore/explore_slide_1.jpg");
            }
            String clean = path.trim();
            if (clean.startsWith("http://") || clean.startsWith("https://")) {
                return new Image(clean, true);
            }
            if (clean.startsWith("file:")) {
                return new Image(clean, true);
            }
            java.io.File file = new java.io.File(clean);
            if (file.exists()) {
                return new Image(file.toURI().toString(), true);
            }
            var r = getClass().getResource(clean);
            if (r != null) {
                return new Image(r.toExternalForm());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadResource("/assets/images/explore/explore_slide_1.jpg");
    }

    private Image loadResource(String path) {
        try {
            var r = getClass().getResource(path);
            return r == null ? null : new Image(r.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    private String val(String str, String fallback) {
        return (str != null && !str.isBlank()) ? str : fallback;
    }
}
