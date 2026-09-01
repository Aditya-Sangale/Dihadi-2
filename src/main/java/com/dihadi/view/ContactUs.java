package com.dihadi.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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

/** Contact page matching the DIHADI business visual system with clean text and no emojis. */
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
                StackPane root = new StackPane(page);
                root.setPadding(new Insets(24));
                root.setStyle("-fx-background-color:#f3e7ce;");
                return new Scene(root, 1400, 780);
        }

        private BorderPane header() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
                logo.setPreserveRatio(true);
                logo.setSmooth(true);
                HBox brand = new HBox(10, logo, label("DIHADI",
                                "-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-letter-spacing:1px;"));
                brand.setAlignment(Pos.CENTER_LEFT);
                Button h = nav("Home", false), b = nav("Business", false), w = nav("Worker", false),
                                recruiter = nav("Recruiter", false), a = nav("About Us", false),
                                contact = nav("Contact Us", true);
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
                Button admin = AppNavigator.createHeaderAdminButton();
                bar.setRight(new HBox(10, admin));
                bar.setPadding(new Insets(16, 24, 14, 24));
                bar.setStyle("-fx-background-color:#f3e7ce;-fx-border-color:#d0c5af;-fx-border-width:0 0 1px 0;"
                                + "-fx-effect:dropshadow(gaussian,rgba(58,48,39,.10),10,.28,0,1.5px);");
                return bar;
        }

        private void navigate(Button source, String destination) {
                javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
                switch (destination) {
                        case "Home" -> stage.setScene(new HomePage(stage).getHomeScene());
                        case "Business" -> stage.setScene(new BusinessPage().getBusinessScene(
                                        () -> stage.setScene(new HomePage(stage).getHomeScene()),
                                        () -> stage.setScene(new WorkerPage()
                                                        .getWorkerScene(() -> stage
                                                                        .setScene(new HomePage(stage).getHomeScene()),
                                                                        null))));
                        case "Worker" -> AppNavigator.signUp(stage, null);
                        case "Recruiter" -> stage.setScene(new com.dihadi.view.recruiter.SignUpRecruiter()
                                        .getRecruiterSignUpScene(
                                                        () -> stage.setScene(getContactScene(null, null, null, null))));
                        case "About Us" ->
                                stage.setScene(new AboutUs().getAboutScene(
                                                () -> stage.setScene(new HomePage(stage).getHomeScene()),
                                                () -> stage.setScene(new WorkerPage()
                                                                .getWorkerScene(() -> stage.setScene(
                                                                                new HomePage(stage).getHomeScene()),
                                                                                null))));
                        default -> {
                        }
                }
        }

        private VBox contactHero() {
                Label badge = label("GET IN TOUCH WITH DIHADI",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
                Label title = label("We're Here to Connect, Support & Empower",
                                "-fx-font-family:'Georgia';-fx-font-size:38px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
                Label intro = label(
                                "Whether you are looking to mobilize project workforces, discover skilled job opportunities, or explore enterprise partnerships, our support team is always within reach.",
                                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
                intro.setWrapText(true);
                intro.setMaxWidth(880);
                intro.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                VBox heroMeta = new VBox(12, badge, title, intro);
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
                Label badge = label("DIRECT TOUCHPOINTS",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
                Label heading = label("Contact Information",
                                "-fx-font-family:'Georgia';-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:#1f1b13;");
                Label subtext = label(
                                "Reach out through our direct communication channels or visit our regional operations office in Pune.",
                                "-fx-font-family:'Georgia';-fx-font-size:14px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
                subtext.setWrapText(true);

                VBox touchpoints = new VBox(14,
                                contactTile("PHONE NUMBER", "+91 9561789599", "Monday to Saturday (9:00 AM - 7:30 PM)",
                                                "Direct Line"),
                                contactTile("EMAIL ADDRESS", "info@meridihadi.com", "Digital Support Desk",
                                                "Online Desk"),
                                contactTile("OPERATIONS HEADQUARTERS",
                                                "Walhekar Properties, 3rd Floor,\nCore2web Technologies, Narhe, Pune",
                                                "Maharashtra, India - 411041", "Regional Hub"),
                                contactTile("WORKING HOURS", "Monday to Saturday: 9:00 AM - 7:30 PM",
                                                "Sunday: Project Operations On-Call", "Active"));

                Label responseFootnote = label("Average response time under 15 minutes during active business hours.",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#4e7037;");
                responseFootnote.setWrapText(true);

                VBox box = new VBox(18, badge, heading, subtext, touchpoints, responseFootnote);
                box.setPrefWidth(500);
                box.setPadding(new Insets(44, 38, 44, 38));
                box.setStyle("-fx-background-color:#ead8b5;-fx-background-radius:26px 0 0 26px;");
                return box;
        }

        private VBox contactTile(String title, String value, String sub, String tag) {
                Label t = label(title,
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#6a5520;");
                Label tagLabel = label(tag,
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-background-color:#dfcaa3;-fx-background-radius:999px;-fx-padding:2px 8px;");
                HBox topRow = new HBox(8, t, tagLabel);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label v = label(value,
                                "-fx-font-family:'Georgia';-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#2b261f;");
                v.setWrapText(true);
                Label s = label(sub, "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-text-fill:#5f5644;");
                s.setWrapText(true);

                VBox text = new VBox(3, topRow, v, s);

                VBox tile = new VBox(text);
                tile.setPadding(new Insets(10, 14, 10, 14));
                tile.setStyle("-fx-background-color:rgba(255,248,240,0.65);-fx-background-radius:14px;-fx-border-color:rgba(115,92,0,0.14);-fx-border-radius:14px;");
                tile.setOnMouseEntered(e -> tile.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.20),10,0,0,2px);"));
                tile.setOnMouseExited(e -> tile.setStyle("-fx-background-color:rgba(255,248,240,0.65);-fx-background-radius:14px;-fx-border-color:rgba(115,92,0,0.14);-fx-border-radius:14px;"));
                return tile;
        }

        private VBox form() {
                Label badge = label("ONLINE INQUIRY",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
                Label formTitle = label("Send Us a Message",
                                "-fx-font-family:'Georgia';-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
                Label formSub = label(
                                "Fill out the form below and our regional coordination team will contact you within 24 hours.",
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
                feedbackLabel.setStyle(
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;");
                feedbackLabel.setVisible(false);

                Button send = primary("SEND MESSAGE NOW");
                send.setStyle("-fx-background-color:#d4af37;-fx-background-radius:14px;-fx-text-fill:#343027;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:13px 32px;-fx-cursor:hand;");

                send.setOnAction(e -> {
                        String name = nameField.getText().trim();
                        String mobile = mobileField.getText().trim();
                        String email = emailField.getText().trim();
                        String subject = subjectField.getText().trim();
                        String message = messageArea.getText().trim();

                        if (name.isEmpty() || mobile.isEmpty()) {
                                feedbackLabel.setText("Please provide both your name and mobile number.");
                                feedbackLabel.setStyle(
                                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#b03525;");
                                feedbackLabel.setVisible(true);
                        } else {
                                feedbackLabel.setText("Submitting inquiry...");
                                feedbackLabel.setStyle(
                                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#735c00;");
                                feedbackLabel.setVisible(true);

                                String sub = subject.isBlank() ? "Online Portal Inquiry" : subject;
                                String desc = message.isBlank() ? "General inquiry submitted from DIHADI contact page." : message;
                                String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

                                com.dihadi.model.Grievance g = new com.dihadi.model.Grievance(
                                        "GR-" + System.currentTimeMillis(),
                                        sub,
                                        name + " (" + mobile + ")",
                                        mobile,
                                        email.isBlank() ? "Not provided" : email,
                                        "Contact Us Portal",
                                        "Pune, Maharashtra",
                                        "Medium",
                                        "New",
                                        "Online Inquiry",
                                        "General Inquiry",
                                        timeStr,
                                        desc,
                                        "Received via Contact Us Page. Awaiting support review."
                                );

                                new Thread(() -> {
                                        try {
                                                new com.dihadi.controller.GrievanceController().saveGrievance(g);
                                        } catch (Exception ex) {
                                                ex.printStackTrace();
                                        }
                                        javafx.application.Platform.runLater(() -> {
                                                feedbackLabel.setText("Thank you, " + name + "! Your inquiry has been submitted and registered.");
                                                feedbackLabel.setStyle(
                                                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#2a7e3b;");
                                                nameField.clear();
                                                emailField.clear();
                                                mobileField.clear();
                                                subjectField.clear();
                                                messageArea.clear();
                                        });
                                }).start();
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

        private VBox narrativeSection() {
                Label eyebrow = label("BUILT ON TRUST, SPEED AND RELIABILITY",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.5px;-fx-text-fill:#735c00;");
                Label title = label("Why Leading Projects Partner With DIHADI",
                                "-fx-font-family:'Georgia';-fx-font-size:32px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
                Label sub = label(
                                "We combine verified trade competencies with digital deployment tools to keep construction projects running on schedule.",
                                "-fx-font-family:'Georgia';-fx-font-size:15px;-fx-text-fill:#4d4635;");
                VBox heading = new VBox(10, eyebrow, title, sub);
                heading.setAlignment(Pos.CENTER);

                HBox cards = new HBox(22,
                                trustPillar("Workforce Mobilization",
                                                "Deploy verified artisans, technicians, and site crews with rapid turnaround times across key project hubs."),
                                trustPillar("Verified Profiles",
                                                "Every worker is authenticated with government-issued identity documents and validated trade skills."),
                                trustPillar("Pan-India Network",
                                                "Scaling operations from metropolitan infrastructure to expanding regional industrial corridors."),
                                trustPillar("Daily Logs & Ledgers",
                                                "Automated daily attendance logs and transparent wage calculations for project supervisors."));
                cards.setAlignment(Pos.CENTER);
                cards.setMaxWidth(1160);

                Label quote = label(
                                "\"From groundbreaking to final finishing, DIHADI stands as the trusted bridge ensuring every worker earns with dignity and every project finishes on schedule.\"",
                                "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-style:italic;-fx-text-fill:#fff8f0;-fx-line-spacing:5px;");
                quote.setWrapText(true);
                quote.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                quote.setMaxWidth(960);

                Label quoteAuthor = label("The DIHADI Mission: Meri Dihadi ~ Mera Haq",
                                "-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#e9c349;");

                VBox quoteBox = new VBox(12, quote, quoteAuthor);
                quoteBox.setAlignment(Pos.CENTER);
                quoteBox.setPadding(new Insets(28, 48, 28, 48));
                quoteBox.setMaxWidth(1160);
                quoteBox.setStyle(
                                "-fx-background-color:#343027;-fx-background-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.12),18,0,0,6px);");

                VBox section = new VBox(32, heading, cards, quoteBox);
                section.setAlignment(Pos.CENTER);
                section.setPadding(new Insets(40, 40, 48, 40));
                section.setMaxWidth(1220);
                section.setStyle(
                                "-fx-background-color:#fbf3e5;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;");
                return section;
        }

        private VBox trustPillar(String title, String description) {
                Label titleLabel = label(title,
                                "-fx-font-family:'Georgia';-fx-font-size:17px;-fx-font-weight:800;-fx-text-fill:#3a3027;");
                titleLabel.setWrapText(true);
                Label descLabel = label(description,
                                "-fx-font-family:'Georgia';-fx-font-size:13px;-fx-text-fill:#4d4635;-fx-line-spacing:3px;");
                descLabel.setWrapText(true);

                VBox box = new VBox(10, titleLabel, descLabel);
                box.setAlignment(Pos.TOP_LEFT);
                box.setPadding(new Insets(22, 20, 22, 20));
                box.setPrefSize(265, 185);
                box.setMaxSize(265, 185);
                box.setStyle("-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),12,0,0,4px);");
                box.setOnMouseEntered(e -> box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:18px;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),14,0,0,4px);-fx-cursor:hand;"));
                box.setOnMouseExited(e -> box.setStyle("-fx-background-color:#fff8f0;-fx-background-radius:18px;-fx-border-color:#d0c5af;-fx-border-radius:18px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),12,0,0,4px);"));
                return box;
        }

        private VBox footer() {
                ImageView logo = image("/assets/logo/dihadi logo.jpeg", 52, 52);
                logo.setPreserveRatio(true);
                Label brand = label("DIHADI", "-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#e9c349;");
                Label promise = label(
                                "Connecting skilled workers with verified opportunities, fair work, and a stronger future.",
                                "-fx-font-size:13px;-fx-text-fill:#f8f0e2;-fx-opacity:.82;");
                promise.setWrapText(true);
                promise.setMaxWidth(310);
                VBox identity = new VBox(9, new HBox(10, logo, brand), promise);
                identity.setPrefWidth(360);
                VBox explore = footerColumn("Explore", "Home", () -> navigateTo("Home"), "Find Work",
                                () -> navigateTo("Worker"), "About Us",
                                () -> navigateTo("About Us"));
                VBox contact = footerColumn("Contact", "9561789599", () -> navigateTo("Contact Us"),
                                "info@meridihadi.com",
                                () -> navigateTo("Contact Us"), "Pune, Maharashtra", () -> navigateTo("Contact Us"));
                HBox top = new HBox(64, identity, explore, contact);
                top.setAlignment(Pos.TOP_LEFT);
                VBox out = new VBox(22, top, label("© 2026 DIHADI. All rights reserved.",
                                "-fx-font-size:12px;-fx-text-fill:#f8f0e2;-fx-opacity:.65;"));
                out.setPadding(new Insets(32, 42, 24, 42));
                out.setMaxWidth(1180);
                out.setStyle("-fx-background-color:#343027;-fx-background-radius:20px;");
                return out;
        }

        private void navigateTo(String destination) {
                for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                        if (window.isFocused() && window instanceof Stage stage) {
                                AppNavigator.open(stage, destination);
                                return;
                        }
                }
        }

        private VBox footerColumn(String heading, String textOne, Runnable actionOne, String textTwo,
                        Runnable actionTwo,
                        String textThree, Runnable actionThree) {
                VBox column = new VBox(7,
                                label(heading, "-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#e9c349;"),
                                footerLink(textOne, actionOne), footerLink(textTwo, actionTwo),
                                footerLink(textThree, actionThree));
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
}
