package com.dihadi.view;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

/** Contact page matching the DIHADI business visual system. */
public class ContactUs {
    private Runnable home, business, worker, about;

    public Scene getContactScene(Runnable home, Runnable business, Runnable worker, Runnable about) {
        this.home = home;
        this.business = business;
        this.worker = worker;
        this.about = about;
        VBox content = new VBox(48, contactHero(), narrativeSection(), footer());
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(36, 0, 0, 0));
        
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        BorderPane page = new BorderPane(scroll);
        page.setTop(header());
        page.setStyle("-fx-background-color:#f3e7ce;");
        return new Scene(page, 1400, 780);
    }

    private BorderPane header() {
        ImageView logo = image("/assets/logo/dihadi logo.jpeg", 50, 50);
        logo.setViewport(new Rectangle2D(380, 0, 840, 840));
        logo.setPreserveRatio(true);
        HBox brand = new HBox(10, logo, label("DIHADI",
                "-fx-font-family:Georgia;-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;"));
        brand.setAlignment(Pos.CENTER_LEFT);
        Button h = nav("Home", false), b = nav("Business", false), w = nav("Worker", false),
                recruiter = nav("Recruiter", false), a = nav("About Us", false), contact = nav("Contact Us", true);
        h.setOnAction(e -> navigate(h, "Home"));
        b.setOnAction(e -> navigate(b, "Business"));
        w.setOnAction(e -> navigate(w, "Worker"));
        recruiter.setOnAction(e -> navigate(recruiter, "Recruiter"));
        a.setOnAction(e -> navigate(a, "About Us"));
        contact.setOnAction(e -> navigate(contact, "Contact Us"));
        HBox links = new HBox(12, h, b, w, recruiter, a, contact);
        links.setAlignment(Pos.CENTER);
        BorderPane bar = new BorderPane();
        bar.setLeft(brand);
        bar.setCenter(links);
        Button admin = primary("Admin");
        admin.setOnAction(e -> AppNavigator.adminLoginInProgress());
        bar.setRight(new HBox(10, admin));
        bar.setPadding(new Insets(16, 32, 14, 32));
        bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;"
                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
        return bar;
    }

    private void navigate(Button source, String destination) {
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        switch (destination) {
            case "Home" -> stage.setScene(new HomePage(stage).getHomeScene());
            case "Business" -> stage.setScene(new BusinessPage().getBusinessScene(
                    () -> stage.setScene(new HomePage(stage).getHomeScene()), () -> stage.setScene(new WorkerPage()
                            .getWorkerScene(() -> stage.setScene(new HomePage(stage).getHomeScene()), null))));
            case "Worker" -> AppNavigator.signUp(stage, null);
            case "Recruiter" -> stage.setScene(new com.dihadi.view.recruiter.SignUpRecruiter()
                    .getRecruiterSignUpScene(() -> stage.setScene(getContactScene(null, null, null, null))));
            case "About Us" ->
                stage.setScene(new AboutUs().getAboutScene(() -> stage.setScene(new HomePage(stage).getHomeScene()),
                        () -> stage.setScene(new WorkerPage()
                                .getWorkerScene(() -> stage.setScene(new HomePage(stage).getHomeScene()), null))));
            default -> {
            }
        }
    }

    private VBox contactHero() {
        Label badge = label("✦ GET IN TOUCH WITH DIHADI",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:2px;-fx-text-fill:#735c00;");
        Label title = label("We're Here to Connect, Support & Empower",
                "-fx-font-family:'Georgia';-fx-font-size:40px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label intro = label(
                "Whether you are looking to mobilize project workforces, discover skilled job opportunities, or explore enterprise partnerships, our dedicated support team is always within reach.",
                "-fx-font-family:'Georgia';-fx-font-size:16px;-fx-text-fill:#4d4635;-fx-line-spacing:4px;");
        intro.setWrapText(true);
        intro.setMaxWidth(880);
        intro.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox heroMeta = new VBox(14, badge, title, intro);
        heroMeta.setAlignment(Pos.CENTER);
        heroMeta.setPadding(new Insets(0, 20, 20, 20));

        HBox panel = new HBox(details(), form());
        panel.setPrefWidth(1220);
        panel.setMaxWidth(1220);
        panel.setMinHeight(640);
        panel.setStyle(
                "-fx-background-color:#fff8f0;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;"
                        + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.14),28,0,0,10px);");

        VBox container = new VBox(28, heroMeta, panel);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 24, 10, 24));
        return container;
    }

    private VBox details() {
        Label badge = label("✦ DIRECT TOUCHPOINTS",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
        Label heading = label("Get in Touch",
                "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#1f1b13;");
        Label subtext = label(
                "We bridge digital recruitment with ground-level reliability. Reach out through our dedicated channels or visit our headquarters.",
                "-fx-font-family:'Georgia';-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        subtext.setWrapText(true);

        VBox touchpoints = new VBox(16,
                contactTile("📞", "PHONE NUMBER", "+91 9561789599", "Mon - Sat (9:00 AM - 7:30 PM)", "Direct Line"),
                contactTile("✉", "EMAIL ADDRESS", "info@meridihadi.com", "24/7 Digital Support Desk", "Online 24/7"),
                contactTile("📍", "OPERATIONS HEADQUARTERS", "Walhekar Properties, 3rd Floor,\nCore2web Technologies, Narhe, Pune", "Maharashtra, India - 411041", "Visit Us"),
                contactTile("🕒", "BUSINESS HOURS", "Monday – Saturday: 9:00 AM – 7:30 PM", "Sunday: On-call emergency deployment", "Active"));

        Label responseFootnote = label("✓ Average response time under 15 minutes during active business hours.",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#4e7037;");
        responseFootnote.setWrapText(true);

        VBox box = new VBox(20, badge, heading, subtext, touchpoints, responseFootnote);
        box.setPrefWidth(500);
        box.setPadding(new Insets(46, 38, 46, 38));
        box.setStyle("-fx-background-color:#ead8b5;-fx-background-radius:26px 0 0 26px;");
        return box;
    }

    private VBox contactTile(String icon, String title, String value, String sub, String tag) {
        Label iconLabel = label(icon, "-fx-font-size:22px;-fx-text-fill:#735c00;");
        StackPane iconBubble = new StackPane(iconLabel);
        iconBubble.setPrefSize(42, 42);
        iconBubble.setMinSize(42, 42);
        iconBubble.setMaxSize(42, 42);
        iconBubble.setStyle("-fx-background-color:#dfcaa3;-fx-background-radius:12px;");

        Label t = label(title, "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#6a5520;");
        Label tagLabel = label(tag, "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-background-color:#dfcaa3;-fx-background-radius:999px;-fx-padding:2px 8px;");
        HBox topRow = new HBox(8, t, tagLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label v = label(value, "-fx-font-family:'Georgia';-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#2b261f;");
        v.setWrapText(true);
        Label s = label(sub, "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-text-fill:#5f5644;");
        s.setWrapText(true);

        VBox text = new VBox(3, topRow, v, s);
        HBox row = new HBox(14, iconBubble, text);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox tile = new VBox(row);
        tile.setPadding(new Insets(10, 14, 10, 14));
        tile.setStyle("-fx-background-color:rgba(255,248,240,0.65);-fx-background-radius:14px;-fx-border-color:rgba(115,92,0,0.14);-fx-border-radius:14px;");
        return tile;
    }

    private VBox form() {
        Label badge = label("✦ ONLINE INQUIRY",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
        Label formTitle = label("Send Us a Message",
                "-fx-font-family:'Georgia';-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label formSub = label("Fill out the form below and our regional coordination team will contact you within 24 hours.",
                "-fx-font-family:'Georgia';-fx-font-size:14px;-fx-text-fill:#6b6250;");
        formSub.setWrapText(true);

        VBox headerBox = new VBox(6, badge, formTitle, formSub);

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(20);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle(input());

        TextField emailField = new TextField();
        emailField.setPromptText("name@company.com");
        emailField.setStyle(input());

        TextField mobileField = new TextField();
        mobileField.setPromptText("+91 98765 43210");
        mobileField.setStyle(input());

        TextField subjectField = new TextField();
        subjectField.setPromptText("Workforce Inquiry / Partnership");
        subjectField.setStyle(input());

        grid.add(field("Full Name *", nameField), 0, 0);
        grid.add(field("Email Address", emailField), 1, 0);
        grid.add(field("Mobile Number *", mobileField), 0, 1);
        grid.add(field("Inquiry Subject", subjectField), 1, 1);

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Describe your workforce requirements, site location, timeline, or query...");
        messageArea.setPrefRowCount(5);
        messageArea.setPrefHeight(125);
        messageArea.setMaxHeight(140);
        messageArea.setWrapText(true);
        messageArea.setStyle(input());
        VBox msg = new VBox(8, label("Detailed Message", caption()), messageArea);

        Label feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;");
        feedbackLabel.setVisible(false);

        Button send = primary("SEND MESSAGE NOW");
        send.setStyle("-fx-background-color:#d4af37;-fx-background-radius:14px;-fx-text-fill:#343027;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:13px 32px;-fx-cursor:hand;");
        
        send.setOnAction(e -> {
            String name = nameField.getText().trim();
            String mobile = mobileField.getText().trim();
            if (name.isEmpty() || mobile.isEmpty()) {
                feedbackLabel.setText("⚠ Please provide both your name and mobile number.");
                feedbackLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#b03525;");
                feedbackLabel.setVisible(true);
            } else {
                feedbackLabel.setText("✓ Thank you, " + name + "! Your inquiry has been received. Our team will contact you shortly.");
                feedbackLabel.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;");
                feedbackLabel.setVisible(true);
                nameField.clear();
                emailField.clear();
                mobileField.clear();
                subjectField.clear();
                messageArea.clear();
            }
        });

        HBox actionRow = new HBox(18, send, feedbackLabel);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(22, headerBox, grid, msg, actionRow);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPrefWidth(720);
        box.setPadding(new Insets(46, 50, 42, 50));
        box.setStyle("-fx-background-color:#fff8f0;-fx-background-radius:0 26px 26px 0;");
        return box;
    }

    private VBox field(String name, TextField f) {
        f.setPrefHeight(46);
        return new VBox(8, label(name, caption()), f);
    }

    private String caption() {
        return "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:700;-fx-letter-spacing:.6px;-fx-text-fill:#6a5520;";
    }

    private String input() {
        return "-fx-background-color:#fbf4e6;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-border-width:1px;-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:14px;-fx-prompt-text-fill:#9b8c74;-fx-padding:10px 14px;";
    }

    /**
     * Replaces the old two cards with an attractive, beautiful, and engaging narrative & trust showcase.
     */
    private VBox narrativeSection() {
        Label eyebrow = label("BUILT ON TRUST, SPEED & TRANSPARENCY",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:2px;-fx-text-fill:#735c00;");
        Label title = label("Why India's Leading Projects Partner With DIHADI",
                "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        Label sub = label("We combine certified workforce verification with real-time digital deployments to keep operations moving without downtime.",
                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-text-fill:#4d4635;");
        VBox heading = new VBox(10, eyebrow, title, sub);
        heading.setAlignment(Pos.CENTER);

        HBox cards = new HBox(22,
                trustPillar("⚡", "Rapid Workforce Mobilization", "Deploy verified artisans, technicians, and site crews with rapid turnaround times across key hubs."),
                trustPillar("🛡️", "100% Verified Profiles", "Every worker is authenticated with government-grade Aadhaar and vetted trade competencies."),
                trustPillar("📍", "Pan-India Reach", "Seamlessly scaling operations from metro infrastructure to expanding regional industrial corridors."),
                trustPillar("📊", "Live Attendance & Ledgers", "Automated daily logs and transparent wage calculations providing full visibility to site recruiters."));
        cards.setAlignment(Pos.CENTER);
        cards.setMaxWidth(1160);

        // Inspiring Quote Banner
        Label quote = label("“From groundbreaking to final finishing, DIHADI stands as the trusted bridge ensuring every worker earns with dignity and every project finishes on schedule.”",
                "-fx-font-family:'Georgia';-fx-font-size:18px;-fx-font-style:italic;-fx-text-fill:#fff8f0;-fx-line-spacing:5px;");
        quote.setWrapText(true);
        quote.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        quote.setMaxWidth(960);

        Label quoteAuthor = label("— The DIHADI Mission: Mera Haq ~ Meri Dihadi",
                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#e9c349;");

        VBox quoteBox = new VBox(14, quote, quoteAuthor);
        quoteBox.setAlignment(Pos.CENTER);
        quoteBox.setPadding(new Insets(32, 48, 32, 48));
        quoteBox.setMaxWidth(1160);
        quoteBox.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),18,0,0,6px);");

        VBox section = new VBox(36, heading, cards, quoteBox);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(42, 40, 56, 40));
        section.setMaxWidth(1220);
        section.setStyle("-fx-background-color:#fbf3e5;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;");
        return section;
    }

    private VBox trustPillar(String icon, String title, String description) {
        Label iconLabel = label(icon, "-fx-font-size:26px;-fx-text-fill:#735c00;");
        Label titleLabel = label(title, "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
        titleLabel.setWrapText(true);
        Label descLabel = label(description, "-fx-font-family:'Georgia';-fx-font-size:13px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
        descLabel.setWrapText(true);

        VBox box = new VBox(12, iconLabel, titleLabel, descLabel);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(24, 20, 24, 20));
        box.setPrefSize(265, 210);
        box.setMaxSize(265, 210);
        box.setStyle("-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),12,0,0,4px);");
        return box;
    }

    private VBox footer() {
        Label brand = label("DIHADI",
                "-fx-font-family:Georgia;-fx-font-size:27px;-fx-font-weight:800;-fx-text-fill:#f1d679;");
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox top = new HBox(20, brand, gap, label("© 2024 DIHADI. Mera Haq ~ Meri Dihadi. All Rights Reserved.",
                "-fx-font-size:12px;-fx-text-fill:#fff8f0;-fx-opacity:.82;"));
        VBox cols = new VBox(22,
                new HBox(92, column("Company", "About Us", "Careers", "Press"),
                        column("Opportunities", "For Workers", "For Businesses", "For Recruiters"),
                        column("Legal", "Terms of Service", "Privacy Policy", "Cookie Policy"),
                        column("Contact", "Contact Us", "Support", "Addresses")));
        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color:#978b7b;");
        VBox box = new VBox(34, top, line, cols);
        box.setPadding(new Insets(52, 86, 54, 86));
        box.setStyle("-fx-background-color:#6b5e52;");
        return box;
    }

    private VBox column(String heading, String... links) {
        VBox b = new VBox(15, label(heading,
                "-fx-font-family:Georgia;-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#f1d679;"));
        for (String s : links)
            b.getChildren().add(label(s, "-fx-font-size:13px;-fx-text-fill:#fff8f0;-fx-opacity:.75;"));
        b.setPrefWidth(160);
        return b;
    }

    private ImageView image(String p, double w, double h) {
        ImageView v = new ImageView(load(p));
        v.setFitWidth(w);
        v.setFitHeight(h);
        v.setPreserveRatio(false);
        return v;
    }

    private Image load(String p) {
        var r = getClass().getResource(p);
        return r == null ? null : new Image(r.toExternalForm());
    }

    private Label label(String t, String s) {
        Label l = new Label(t);
        l.setStyle(s);
        return l;
    }

    private String body(int s) {
        return "-fx-font-family:Georgia;-fx-font-size:" + s + "px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;";
    }

    private String head(int s) {
        return "-fx-font-family:Georgia;-fx-font-size:" + s + "px;-fx-font-weight:700;-fx-text-fill:#1f1b13;";
    }

    private Button nav(String t, boolean on) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:transparent;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:"
                + (on ? "#735c00" : "#4d4635") + ";-fx-border-color:" + (on ? "#735c00" : "transparent")
                + ";-fx-border-width:0 0 2px 0;-fx-font-family:'Segoe UI',sans-serif;-fx-padding:8px 4px;-fx-cursor:hand;");
        return b;
    }

    private Button primary(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#d8c39d;-fx-background-radius:18px;-fx-text-fill:#3a3027;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:10px 20px;-fx-cursor:hand;");
        return b;
    }

    private Button outline(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color:#fbf3e5;-fx-background-radius:18px;-fx-border-color:#c6a15b;-fx-border-radius:18px;-fx-text-fill:#735c00;-fx-font-size:14px;-fx-font-weight:700;-fx-padding:9px 18px;-fx-cursor:hand;");
        return b;
    }

    private void run(Runnable r) {
        if (r != null)
            r.run();
    }
}
