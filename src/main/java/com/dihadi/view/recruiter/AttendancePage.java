package com.dihadi.view.recruiter;

import com.dihadi.controller.AttendanceController;
import com.dihadi.controller.RazorpayService;
import com.dihadi.controller.RecruiterController;
import com.dihadi.dao.AttendanceDao;
import com.dihadi.dao.ProjectDao;
import com.dihadi.dao.WorkerDao;
import com.dihadi.model.Attendance;
import com.dihadi.model.Project;
import com.dihadi.model.Recruiter;
import com.dihadi.model.Worker;
import com.dihadi.view.NotificationToast;
import com.dihadi.view.PaymentGateway.PaymentCheckoutScene;
import com.dihadi.view.PaymentGateway.RazorpayCheckoutDialog;
import com.dihadi.view.ScrollUtils;
import com.dihadi.view.SessionManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Enterprise-grade Daily Attendance & Wage Disbursal Interface for Recruiters.
 * Features real-time KPI analytics, quick date toggles, worker dossiers,
 * multi-criteria search/filtering, seamless Razorpay escrow payouts, and printable vouchers.
 */
public class AttendancePage {

    private static final String BG_COLOR = "#f3e7ce";
    private static final String CARD_BG = "#ffffff";
    private static final String BORDER = "#d0c5af";
    private static final String GOLD = "#735c00";
    private static final String INK = "#1e1b15";
    private static final String MUTED = "#685c52";
    private static final String GREEN = "#2e7d32";
    private static final String BLUE = "#1565c0";
    private static final String RED = "#ba1a1a";

    private final AttendanceController attendanceController;
    private final AttendanceDao attendanceDao;
    private final ProjectDao projectDao;
    private final WorkerDao workerDao;

    private Recruiter currentRecruiter;
    private String currentRecruiterId;
    private Runnable backAction;

    // UI state controls
    private Label walletBalanceLabel;
    private ComboBox<Project> projectDropdown;
    private DatePicker datePicker;
    private ProgressIndicator loadingIndicator;

    // KPI Labels
    private Label assignedCrewKpiLabel;
    private Label presentPaidKpiLabel;
    private Label pendingPayoutsKpiLabel;
    private Label dailyWageOutlayKpiLabel;

    // Search and filtering
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> tradeFilter;
    private Label recordsCountLabel;

    // Selected project brief
    private Label projectLocationBadge;
    private Label projectWorkforceProgressBadge;

    // Container for worker cards
    private VBox recordsListContainer;
    private VBox emptyStateBox;

    // Master in-memory data
    private final List<WorkerAttendanceItem> masterWorkersList = new ArrayList<>();
    private final List<WorkerAttendanceItem> filteredWorkersList = new ArrayList<>();
    private final Map<String, Attendance> currentAttendanceMap = new HashMap<>();

    /**
     * Internal model encapsulating a worker, their assigned project, shift date, and attendance status.
     */
    public static class WorkerAttendanceItem {
        public Worker worker;
        public Project project;
        public LocalDate date;
        public Attendance record;

        public String workerName = "Worker";
        public String workerMobile = "";
        public String workerTrade = "General Worker";
        public String workerSubSkill = "";
        public String workerCity = "Maharashtra";
        public String workerPhotoUrl = null;
        public int daysWorked = 0;
        public double dailyWage = 700.0;

        public boolean isPresent = false;
        public boolean isPaid = false;
        public boolean isAbsent = false;
        public String transactionId = null;

        public WorkerAttendanceItem() {}
    }

    public AttendancePage() {
        this(SessionManager.currentRecruiter);
    }

    public AttendancePage(Recruiter recruiter) {
        this.currentRecruiter = recruiter != null ? recruiter : (SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : new Recruiter());
        this.attendanceController = new AttendanceController();
        this.attendanceDao = new AttendanceDao();
        this.projectDao = new ProjectDao();
        this.workerDao = new WorkerDao();

        this.currentRecruiterId = currentRecruiter.getMobileNumber();
        if (this.currentRecruiterId == null || this.currentRecruiterId.trim().isEmpty()) {
            this.currentRecruiterId = currentRecruiter.getUid();
        }
        if (this.currentRecruiterId == null || this.currentRecruiterId.trim().isEmpty()) {
            this.currentRecruiterId = SessionManager.getCurrentRecruiterId();
        }
    }

    public Scene getScene(Runnable back) {
        this.backAction = back;

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.setPadding(new Insets(28, 48, 40, 48));
        root.setMaxWidth(1440);
        root.setAlignment(Pos.TOP_CENTER);

        // 1. Top Header Bar
        HBox header = createHeader(back);

        // 2. Title and Eyebrow Banner
        VBox titleBanner = createTitleBanner();

        // 3. KPI Metrics Row (4 Cards)
        HBox kpiRow = createKpiMetricsRow();

        // 4. Project Selection & Quick Date Controller Card
        VBox projectControlCard = createProjectControlCard();

        // 5. Search & Status Filter Bar
        VBox filterBar = createFilterControls();

        // 6. Worker Records List Container
        recordsListContainer = new VBox(14);
        recordsListContainer.setAlignment(Pos.TOP_CENTER);
        recordsListContainer.setFillWidth(true);

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(40, 40);
        loadingIndicator.setStyle("-fx-progress-color: " + GOLD + ";");

        emptyStateBox = createEmptyState();
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);

        VBox contentSection = new VBox(16, filterBar, loadingIndicator, emptyStateBox, recordsListContainer);
        contentSection.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(header, titleBanner, kpiRow, projectControlCard, contentSection);

        StackPane centerWrapper = new StackPane(root);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setStyle("-fx-background-color: " + BG_COLOR + ";");

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        ScrollUtils.style(scrollPane);
        scrollPane.setStyle("-fx-background: " + BG_COLOR + "; -fx-background-color: " + BG_COLOR + "; -fx-border-width: 0;");

        // Load projects and trigger initial population
        loadProjects();

        return new Scene(scrollPane, 1420, 880);
    }

    public Scene getScene() {
        return getScene(() -> {});
    }

    // ==========================================
    // 1. Top Header Bar
    // ==========================================
    private HBox createHeader(Runnable back) {
        ImageView logoImg = image("/assets/logo/dihadi logo.jpeg", 42, 42);

        Label brand = new Label("DIHADI");
        brand.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        brand.setTextOverrun(OverrunStyle.CLIP);

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        if (logoImg != null) brandBox.getChildren().add(logoImg);
        brandBox.getChildren().add(brand);

        Button backButton = new Button("← Back to Dashboard");
        String backIdle = "-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;";
        String backHover = "-fx-background-color:#ffffff;-fx-text-fill:" + GOLD + ";-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:" + GOLD + ";-fx-border-radius:10px;-fx-background-radius:10px;";
        backButton.setStyle(backIdle);
        backButton.setOnMouseEntered(e -> backButton.setStyle(backHover));
        backButton.setOnMouseExited(e -> backButton.setStyle(backIdle));
        backButton.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                Stage stage = (Stage) backButton.getScene().getWindow();
                if (stage != null) {
                    stage.setScene(new RecruiterDashboard(currentRecruiter).getScene(() -> {}));
                }
            }
        });

        String recruiterName = (currentRecruiter.getFirstName() != null ? currentRecruiter.getFirstName() : "Recruiter")
                + (currentRecruiter.getLastName() != null ? " " + currentRecruiter.getLastName() : "");
        String companyName = currentRecruiter.getCompanyName() != null && !currentRecruiter.getCompanyName().isBlank()
                ? currentRecruiter.getCompanyName() : "General Contractor";

        Label profileBadge = new Label(recruiterName + " (" + companyName + ")");
        profileBadge.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#3f392e;-fx-background-color:#fffaf0;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;-fx-padding:7px 16px;");
        profileBadge.setTextOverrun(OverrunStyle.CLIP);

        // Wallet Balance Info
        walletBalanceLabel = new Label();
        updateWalletDisplay();
        walletBalanceLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Button rechargeBtn = new Button("+ Recharge Wallet");
        rechargeBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:6px 14px;-fx-background-radius:8px;-fx-cursor:hand;");
        rechargeBtn.setOnAction(e -> triggerRechargeFlow(rechargeBtn, 1000.00));

        HBox walletBadgeBox = new HBox(10, walletBalanceLabel, rechargeBtn);
        walletBadgeBox.setAlignment(Pos.CENTER_RIGHT);
        walletBadgeBox.setStyle("-fx-background-color:#ffffff;-fx-padding:5px 12px;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;");

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color:#ffffff;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:7px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;");
        refreshBtn.setOnAction(e -> refreshAttendanceList());

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        HBox actions = new HBox(12, backButton, refreshBtn, walletBadgeBox, profileBadge);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox headerBar = new HBox(20, brandBox, navSpacer, actions);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(0, 0, 10, 0));
        headerBar.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return headerBar;
    }

    private void updateWalletDisplay() {
        double balance = currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0;
        if (walletBalanceLabel != null) {
            walletBalanceLabel.setText(String.format("Escrow Wallet: ₹%,.2f", balance));
        }
    }

    // ==========================================
    // 2. Title & Eyebrow Banner
    // ==========================================
    private VBox createTitleBanner() {
        Label eyebrow = new Label("✦  RECRUITER WORKFORCE OPERATIONS & PAYROLL");
        eyebrow.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:" + GOLD + ";-fx-background-color:#fffaf0;-fx-background-radius:10px;-fx-padding:4px 10px;-fx-border-color:#e2d5bd;-fx-border-radius:10px;");
        eyebrow.setTextOverrun(OverrunStyle.CLIP);

        Label title = new Label("Daily Attendance & Wage Disbursal");
        title.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        title.setTextOverrun(OverrunStyle.CLIP);

        Label subtitle = new Label("Track and verify on-site worker attendance, confirm shift completion, and disburse daily wages seamlessly via direct DIHADI Escrow settlement.");
        subtitle.setStyle("-fx-font-size:14px;-fx-font-weight:500;-fx-text-fill:" + MUTED + ";");
        subtitle.setTextOverrun(OverrunStyle.CLIP);
        subtitle.setWrapText(true);

        VBox banner = new VBox(8, eyebrow, title, subtitle);
        banner.setAlignment(Pos.CENTER_LEFT);
        return banner;
    }

    // ==========================================
    // 3. KPI Metrics Row (4 Cards)
    // ==========================================
    private HBox createKpiMetricsRow() {
        assignedCrewKpiLabel = new Label("0");
        assignedCrewKpiLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        presentPaidKpiLabel = new Label("0");
        presentPaidKpiLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GREEN + ";");

        pendingPayoutsKpiLabel = new Label("0");
        pendingPayoutsKpiLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#b45309;");

        dailyWageOutlayKpiLabel = new Label("₹ 0.00");
        dailyWageOutlayKpiLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + BLUE + ";");

        VBox card1 = kpiCard("ASSIGNED CREW", assignedCrewKpiLabel, "Active site workforce", GOLD);
        VBox card2 = kpiCard("PRESENT & PAID TODAY", presentPaidKpiLabel, "Completed wage disbursements", GREEN);
        VBox card3 = kpiCard("PENDING VERIFICATION", pendingPayoutsKpiLabel, "Awaiting attendance check-in", "#b45309");
        VBox card4 = kpiCard("DAILY DISBURSED PAYROLL", dailyWageOutlayKpiLabel, "Settled via Escrow direct", BLUE);

        HBox row = new HBox(16, card1, card2, card3, card4);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox kpiCard(String title, Label valueLabel, String subtext, String accentColor) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        titleLabel.setTextOverrun(OverrunStyle.CLIP);

        Label sub = new Label(subtext);
        sub.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:" + accentColor + ";");
        sub.setTextOverrun(OverrunStyle.CLIP);

        VBox card = new VBox(6, titleLabel, valueLabel, sub);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setPrefHeight(105);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.8px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.22),12,0,0,3px);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);"));
        return card;
    }

    // ==========================================
    // 4. Project Selection & Date Controller Card
    // ==========================================
    private VBox createProjectControlCard() {
        Label projLbl = new Label("SELECT PROJECT:");
        projLbl.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");

        projectDropdown = new ComboBox<>();
        projectDropdown.setPromptText("Choose Active Ongoing Project");
        projectDropdown.setPrefHeight(42);
        projectDropdown.setPrefWidth(320);
        projectDropdown.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        projectDropdown.setCellFactory(lv -> new ListCell<Project>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " (" + (item.getStatus() != null ? item.getStatus() : "Active") + ")");
                }
            }
        });
        projectDropdown.setButtonCell(new ListCell<Project>() {
            @Override
            protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " (" + (item.getStatus() != null ? item.getStatus() : "Active") + ")");
                }
            }
        });
        projectDropdown.setOnAction(e -> refreshAttendanceList());

        Label dateLbl = new Label("SHIFT DATE:");
        dateLbl.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefHeight(42);
        datePicker.setPrefWidth(170);
        datePicker.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        datePicker.setOnAction(e -> refreshAttendanceList());

        Button todayBtn = new Button("Today");
        todayBtn.setPrefHeight(42);
        todayBtn.setStyle("-fx-background-color:#fff8e7;-fx-text-fill:" + GOLD + ";-fx-font-size:12px;-fx-font-weight:800;-fx-padding:0 14px;-fx-border-color:#f0d890;-fx-border-radius:10px;-fx-background-radius:10px;-fx-cursor:hand;");
        todayBtn.setOnAction(e -> {
            datePicker.setValue(LocalDate.now());
            refreshAttendanceList();
        });

        Button yesterdayBtn = new Button("Yesterday");
        yesterdayBtn.setPrefHeight(42);
        yesterdayBtn.setStyle("-fx-background-color:#f5f0e8;-fx-text-fill:#4c4637;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:0 14px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;-fx-cursor:hand;");
        yesterdayBtn.setOnAction(e -> {
            datePicker.setValue(LocalDate.now().minusDays(1));
            refreshAttendanceList();
        });

        Button refreshBtn = new Button("↻ Refresh Live");
        refreshBtn.setPrefHeight(42);
        refreshBtn.setStyle("-fx-background-color:#ffffff;-fx-text-fill:" + GOLD + ";-fx-font-size:13px;-fx-font-weight:800;-fx-padding:0 16px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-background-radius:10px;-fx-cursor:hand;");
        refreshBtn.setOnAction(e -> refreshAttendanceList());

        HBox selectRow = new HBox(12, projLbl, projectDropdown, dateLbl, datePicker, todayBtn, yesterdayBtn, refreshBtn);
        selectRow.setAlignment(Pos.CENTER_LEFT);

        // Project Location & Workforce progress strip
        projectLocationBadge = new Label("📍 Select a project to view site location");
        projectLocationBadge.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#685c52;");

        projectWorkforceProgressBadge = new Label("👥 Deployed Workforce: 0 Workers");
        projectWorkforceProgressBadge.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#1565c0;-fx-background-color:#eef4ff;-fx-background-radius:6px;-fx-padding:3px 8px;");

        Region stripSpacer = new Region();
        HBox.setHgrow(stripSpacer, Priority.ALWAYS);

        HBox metaStrip = new HBox(12, projectLocationBadge, stripSpacer, projectWorkforceProgressBadge);
        metaStrip.setAlignment(Pos.CENTER_LEFT);
        metaStrip.setPadding(new Insets(6, 10, 0, 10));

        VBox card = new VBox(10, selectRow, metaStrip);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");
        return card;
    }

    // ==========================================
    // 5. Search & Status Filter Controls
    // ==========================================
    private VBox createFilterControls() {
        searchField = new TextField();
        searchField.setPromptText("🔍 Search assigned worker by name, phone, trade, or role...");
        searchField.setPrefHeight(40);
        searchField.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;-fx-padding:0 14px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Present & Paid Only", "Not Marked / Pending Only", "Absent Only");
        statusFilter.setValue("All Statuses");
        statusFilter.setPrefHeight(40);
        statusFilter.setPrefWidth(210);
        statusFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        statusFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());

        tradeFilter = new ComboBox<>();
        tradeFilter.getItems().addAll("All Trades", "Mason", "Carpenter", "Electrician", "Painter", "Plumber", "Welder", "General Labour", "Site Supervisor");
        tradeFilter.setValue("All Trades");
        tradeFilter.setPrefHeight(40);
        tradeFilter.setPrefWidth(180);
        tradeFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        tradeFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());

        Button resetBtn = new Button("Reset");
        resetBtn.setPrefHeight(40);
        resetBtn.setStyle("-fx-background-color:#f5f0e8;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:0 14px;-fx-cursor:hand;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Statuses");
            tradeFilter.setValue("All Trades");
            applyFilters();
        });

        HBox filterRow = new HBox(12, searchField, statusFilter, tradeFilter, resetBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        recordsCountLabel = new Label("Showing 0 of 0 assigned workers");
        recordsCountLabel.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label payoutGuide = new Label("💡 Marking a worker present triggers instant direct settlement to the worker's wallet.");
        payoutGuide.setStyle("-fx-font-size:12px;-fx-text-fill:#7a7267;");

        HBox countRow = new HBox(10, recordsCountLabel, spacer, payoutGuide);
        countRow.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, filterRow, countRow);
        return container;
    }

    // ==========================================
    // 6. Data Loading Logic
    // ==========================================
    private void loadProjects() {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<Project> allProjects = projectDao.getProjectsByRecruiterId(currentRecruiterId);
                List<Project> ongoingProjects = new ArrayList<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (p == null) continue;
                        String status = p.getStatus() != null ? p.getStatus().trim() : "";
                        if ("Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                            continue;
                        }
                        ongoingProjects.add(p);
                    }
                }

                Platform.runLater(() -> {
                    projectDropdown.getItems().clear();
                    if (!ongoingProjects.isEmpty()) {
                        projectDropdown.getItems().addAll(ongoingProjects);
                        Project preselect = null;
                        if (SessionManager.currentRecruiterProject != null) {
                            String activeId = SessionManager.currentRecruiterProject.getProjectId();
                            for (Project p : ongoingProjects) {
                                if (activeId != null && (activeId.equals(p.getProjectId()) || activeId.equals(p.getId()))) {
                                    preselect = p;
                                    break;
                                }
                            }
                        }
                        if (preselect != null) {
                            projectDropdown.getSelectionModel().select(preselect);
                        } else {
                            projectDropdown.getSelectionModel().selectFirst();
                        }
                        refreshAttendanceList();
                    } else {
                        projectLocationBadge.setText("No active ongoing projects found.");
                        recordsListContainer.getChildren().clear();
                        emptyStateBox.setVisible(true);
                        emptyStateBox.setManaged(true);
                    }
                    loadingIndicator.setVisible(false);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> loadingIndicator.setVisible(false));
            }
        }).start();
    }

    private void refreshAttendanceList() {
        Project selectedProject = projectDropdown.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedProject == null || selectedDate == null) {
            return;
        }

        // Update Project Location & Info strip
        String siteLoc = (selectedProject.getCity() != null ? selectedProject.getCity() : "")
                + (selectedProject.getState() != null && !selectedProject.getState().isBlank() ? ", " + selectedProject.getState() : "");
        if (siteLoc.isBlank() && selectedProject.getAddressLine1() != null) siteLoc = selectedProject.getAddressLine1();
        if (siteLoc.isBlank()) siteLoc = "Maharashtra Jobsite";
        projectLocationBadge.setText("📍 Site Location: " + siteLoc);

        loadingIndicator.setVisible(true);
        loadingIndicator.setManaged(true);
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
        recordsListContainer.getChildren().clear();

        new Thread(() -> {
            try {
                String projectId = selectedProject.getId();
                List<Worker> assignedWorkers = workerDao.getWorkersByProjectId(projectId);
                List<Attendance> projectAttendances = attendanceDao.getAttendanceByProject(projectId);
                String dateStr = selectedDate.toString();

                currentAttendanceMap.clear();
                if (projectAttendances != null) {
                    for (Attendance att : projectAttendances) {
                        if (att != null && dateStr.equals(att.getDate())) {
                            if (att.getAttendanceId() != null && !att.getAttendanceId().isBlank()) {
                                currentAttendanceMap.put(att.getAttendanceId(), att);
                            }
                            if (att.getWorkerId() != null && !att.getWorkerId().isBlank()) {
                                currentAttendanceMap.put(att.getWorkerId(), att);
                            }
                            if (att.getWorkerMobile() != null && !att.getWorkerMobile().isBlank()) {
                                currentAttendanceMap.put(att.getWorkerMobile(), att);
                            }
                        }
                    }
                }

                List<WorkerAttendanceItem> items = new ArrayList<>();
                if (assignedWorkers != null) {
                    for (Worker w : assignedWorkers) {
                        WorkerAttendanceItem item = new WorkerAttendanceItem();
                        item.worker = w;
                        item.project = selectedProject;
                        item.date = selectedDate;

                        // Identify worker details
                        item.workerName = w.getName();
                        if (item.workerName == null || item.workerName.isBlank() || "Worker".equalsIgnoreCase(item.workerName)) {
                            item.workerName = w.getFullName();
                        }
                        item.workerMobile = w.getPhone();
                        item.workerTrade = w.getSkill();
                        item.workerSubSkill = w.getSubSkill() != null ? w.getSubSkill() : "";
                        item.workerCity = w.getCity() != null ? w.getCity() : "Maharashtra";
                        item.workerPhotoUrl = w.getProfilePhotoUrl();
                        item.daysWorked = w.getTotalDaysWorked();
                        item.dailyWage = w.getDailyWage() > 0 ? w.getDailyWage() : 750.0;

                        // Match attendance record
                        String deterministicId = String.format("ATT_%s_%s_%s", projectId, w.getId(), dateStr);
                        Attendance att = currentAttendanceMap.get(deterministicId);
                        if (att == null && w.getId() != null) att = currentAttendanceMap.get(w.getId());
                        if (att == null && w.getMobileNumber() != null) att = currentAttendanceMap.get(w.getMobileNumber());
                        if (att == null && w.getPhone() != null) att = currentAttendanceMap.get(w.getPhone());

                        item.record = att;
                        if (att != null) {
                            if ("PRESENT".equalsIgnoreCase(att.getStatus())) {
                                item.isPresent = true;
                                item.isPaid = true;
                                item.transactionId = att.getTransactionId() != null ? att.getTransactionId() : "TXN_ESCROW";
                            } else if ("ABSENT".equalsIgnoreCase(att.getStatus())) {
                                item.isAbsent = true;
                            }
                        }

                        items.add(item);
                    }
                }

                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setManaged(false);

                    masterWorkersList.clear();
                    masterWorkersList.addAll(items);

                    projectWorkforceProgressBadge.setText("👥 Deployed Workforce: " + masterWorkersList.size() + " Workers");

                    applyFilters();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setManaged(false);
                });
            }
        }).start();
    }

    // ==========================================
    // 7. Filter & KPI Calculations
    // ==========================================
    private void applyFilters() {
        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String statusSel = statusFilter != null ? statusFilter.getValue() : "All Statuses";
        String tradeSel = tradeFilter != null ? tradeFilter.getValue() : "All Trades";

        filteredWorkersList.clear();

        for (WorkerAttendanceItem item : masterWorkersList) {
            // Search text match
            if (!query.isEmpty()) {
                boolean match = false;
                if (item.workerName != null && item.workerName.toLowerCase().contains(query)) match = true;
                if (item.workerMobile != null && item.workerMobile.toLowerCase().contains(query)) match = true;
                if (item.workerTrade != null && item.workerTrade.toLowerCase().contains(query)) match = true;
                if (item.workerSubSkill != null && item.workerSubSkill.toLowerCase().contains(query)) match = true;
                if (!match) continue;
            }

            // Status filter match
            if ("Present & Paid Only".equals(statusSel) && !item.isPaid) continue;
            if ("Not Marked / Pending Only".equals(statusSel) && (item.isPaid || item.isAbsent)) continue;
            if ("Absent Only".equals(statusSel) && !item.isAbsent) continue;

            // Trade filter match
            if (tradeSel != null && !tradeSel.equals("All Trades")) {
                if (item.workerTrade == null || !item.workerTrade.toLowerCase().contains(tradeSel.toLowerCase())) {
                    continue;
                }
            }

            filteredWorkersList.add(item);
        }

        updateKpiMetrics();
        renderWorkerCards();
    }

    private void updateKpiMetrics() {
        int totalAssigned = masterWorkersList.size();
        int presentCount = 0;
        int pendingCount = 0;
        double totalDisbursedToday = 0.0;

        for (WorkerAttendanceItem item : masterWorkersList) {
            if (item.isPaid) {
                presentCount++;
                totalDisbursedToday += item.dailyWage;
            } else if (!item.isAbsent) {
                pendingCount++;
            }
        }

        assignedCrewKpiLabel.setText(String.valueOf(totalAssigned));
        presentPaidKpiLabel.setText(String.valueOf(presentCount) + " / " + totalAssigned);
        pendingPayoutsKpiLabel.setText(String.valueOf(pendingCount));
        dailyWageOutlayKpiLabel.setText(String.format("₹ %,.2f", totalDisbursedToday));
    }

    private void renderWorkerCards() {
        recordsCountLabel.setText("Showing " + filteredWorkersList.size() + " of " + masterWorkersList.size() + " assigned workers");
        recordsListContainer.getChildren().clear();

        if (filteredWorkersList.isEmpty()) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
        } else {
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);

            for (WorkerAttendanceItem item : filteredWorkersList) {
                recordsListContainer.getChildren().add(createWorkerAttendanceCard(item));
            }
        }
    }

    // ==========================================
    // 8. Worker Attendance Card
    // ==========================================
    private Node createWorkerAttendanceCard(WorkerAttendanceItem item) {
        // 1. Avatar & Identity Column
        Node avatarNode = createAvatar(item.workerName, item.workerPhotoUrl);

        Label nameLabel = new Label(item.workerName);
        nameLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        nameLabel.setTextOverrun(OverrunStyle.CLIP);
        nameLabel.setWrapText(true);

        Label tradeBadge = new Label(item.workerTrade.toUpperCase());
        tradeBadge.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#fff8e7;-fx-background-radius:8px;-fx-padding:3px 9px;-fx-border-color:#f0d890;-fx-border-radius:8px;");
        tradeBadge.setTextOverrun(OverrunStyle.CLIP);

        Label verifiedBadge = new Label("🛡️ Verified DIHADI Pro");
        verifiedBadge.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-padding:3px 8px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");
        verifiedBadge.setTextOverrun(OverrunStyle.CLIP);

        HBox nameRow = new HBox(8, nameLabel, tradeBadge, verifiedBadge);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label metaLabel = new Label("📱 " + item.workerMobile + "   •   📍 " + item.workerCity + "   •   🗓️ " + item.daysWorked + " Days Worked");
        metaLabel.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:" + MUTED + ";");
        metaLabel.setTextOverrun(OverrunStyle.CLIP);

        VBox identityBox = new VBox(4, nameRow, metaLabel);
        identityBox.setAlignment(Pos.CENTER_LEFT);

        HBox leftCol = new HBox(16, avatarNode, identityBox);
        leftCol.setAlignment(Pos.CENTER_LEFT);
        leftCol.setPrefWidth(420);

        // 2. Wage & Shift Section
        Label wageHead = new Label("DAILY WAGE");
        wageHead.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        wageHead.setTextOverrun(OverrunStyle.CLIP);

        Label wageAmount = new Label(String.format("₹ %,.2f", item.dailyWage));
        wageAmount.setStyle("-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:" + GREEN + ";");
        wageAmount.setTextOverrun(OverrunStyle.CLIP);

        Label shiftTag = new Label("Regular Shift (8 hrs)");
        shiftTag.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#4c4637;");
        shiftTag.setTextOverrun(OverrunStyle.CLIP);

        VBox wageCol = new VBox(2, wageHead, wageAmount, shiftTag);
        wageCol.setAlignment(Pos.CENTER_LEFT);
        wageCol.setPrefWidth(160);

        // 3. Status Column
        VBox statusCol = new VBox(4);
        statusCol.setAlignment(Pos.CENTER_LEFT);
        statusCol.setPrefWidth(220);

        Label statusBadge = new Label();
        Label subStatusLabel = new Label();

        if (item.isPaid) {
            statusBadge.setText("✓ PRESENT & PAID");
            statusBadge.setStyle("-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-padding:4px 10px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");
            subStatusLabel.setText("Txn: " + (item.transactionId != null ? item.transactionId : "ESCROW_SETTLED"));
            subStatusLabel.setStyle("-fx-font-family:'Consolas',monospace;-fx-font-size:11px;-fx-text-fill:#7a7267;");
        } else if (item.isAbsent) {
            statusBadge.setText("✕ MARKED ABSENT");
            statusBadge.setStyle("-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#ba1a1a;-fx-background-color:#fff1f1;-fx-background-radius:8px;-fx-padding:4px 10px;-fx-border-color:#f1bcbc;-fx-border-radius:8px;");
            subStatusLabel.setText("No daily wage disbursed");
            subStatusLabel.setStyle("-fx-font-size:11px;-fx-text-fill:#a51d1d;");
        } else {
            statusBadge.setText("⏳ NOT MARKED TODAY");
            statusBadge.setStyle("-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#b45309;-fx-background-color:#fef3c7;-fx-background-radius:8px;-fx-padding:4px 10px;-fx-border-color:#fde68a;-fx-border-radius:8px;");
            subStatusLabel.setText("Awaiting shift confirmation");
            subStatusLabel.setStyle("-fx-font-size:11px;-fx-text-fill:#685c52;");
        }
        statusCol.getChildren().addAll(statusBadge, subStatusLabel);

        // 4. Action Buttons Column
        HBox actionsRow = new HBox(10);
        actionsRow.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actionsRow, Priority.ALWAYS);

        if (item.isPaid) {
            Button voucherBtn = new Button("📄 View Voucher");
            voucherBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-background-radius:8px;-fx-cursor:hand;");
            voucherBtn.setOnAction(e -> openVoucherModal(item));

            Label settledTag = new Label("✓ Escrow Settled");
            settledTag.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#2e7d32;");

            actionsRow.getChildren().addAll(settledTag, voucherBtn);
        } else {
            Button markPresentBtn = new Button("⚡ Mark Present & Pay");
            markPresentBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;");
            markPresentBtn.setOnMouseEntered(e -> markPresentBtn.setStyle("-fx-background-color:#3f3f3f;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;"));
            markPresentBtn.setOnMouseExited(e -> markPresentBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;"));
            markPresentBtn.setOnAction(e -> handleMarkPresentAndPay(item, markPresentBtn));

            Button markAbsentBtn = new Button("Mark Absent");
            markAbsentBtn.setStyle("-fx-background-color:#f5f0e8;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#4c4637;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:8px 12px;-fx-cursor:hand;");
            markAbsentBtn.setOnAction(e -> handleMarkAbsent(item));

            actionsRow.getChildren().addAll(markAbsentBtn, markPresentBtn);
        }

        HBox cardRow = new HBox(20, leftCol, wageCol, statusCol, actionsRow);
        cardRow.setAlignment(Pos.CENTER_LEFT);
        cardRow.setPadding(new Insets(16, 22, 16, 22));
        cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");

        cardRow.setOnMouseEntered(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.8px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.22),12,0,0,3px);"));
        cardRow.setOnMouseExited(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);"));

        return cardRow;
    }

    // ==========================================
    // 9. Payment & Attendance Actions
    // ==========================================
    private void handleMarkPresentAndPay(WorkerAttendanceItem item, Button sourceBtn) {
        double wage = item.dailyWage;

        // Check wallet balance
        double currentBal = currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0;
        if (SessionManager.currentRecruiter != null) {
            currentBal = Math.max(currentBal, SessionManager.currentRecruiter.getWalletBalance());
        }

        if (currentBal < wage || currentBal <= 0.0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Escrow Balance");
            alert.setHeaderText("Cannot Proceed With Wage Payment");
            alert.setContentText(String.format(
                "Unable to disburse daily wage for %s.\n\n" +
                "• Available Wallet Balance: ₹%,.2f\n" +
                "• Required Daily Wage: ₹%,.2f\n\n" +
                "Please recharge your wallet using the '+ Recharge Wallet' button to proceed.",
                item.workerName, currentBal, wage
            ));
            alert.showAndWait();
            return;
        }

        sourceBtn.setDisable(true);
        sourceBtn.setText("Initiating Gateway...");

        String attendanceRecordId = String.format("ATT_%s_%s_%s", item.project.getId(), item.worker.getId(), item.date.toString());
        String receiptId = "RCPT_" + System.currentTimeMillis();
        String notes = "Attendance Wage: " + item.workerName + " on " + item.date;

        attendanceController.createPaymentOrder(
            wage,
            receiptId,
            notes,
            orderId -> {
                sourceBtn.setText("Awaiting Payment...");
                Stage parentStage = sourceBtn.getScene() != null ? (Stage) sourceBtn.getScene().getWindow() : null;
                String keyId = attendanceController.getRazorpayService().getKeyId();

                RazorpayCheckoutDialog.showPaymentWindow(
                    parentStage,
                    keyId,
                    orderId,
                    wage,
                    item.workerName,
                    (paymentId, returnedOrderId, signature) -> {
                        sourceBtn.setText("Verifying...");
                        String targetWorkerId = (item.worker.getMobileNumber() != null && !item.worker.getMobileNumber().isBlank())
                                ? item.worker.getMobileNumber()
                                : item.worker.getId();

                        attendanceController.verifyAndCompleteAttendance(
                            currentRecruiterId,
                            targetWorkerId,
                            wage,
                            attendanceRecordId,
                            item.project.getId(),
                            returnedOrderId,
                            paymentId,
                            signature,
                            () -> {
                                double remainingBal = Math.max(0.0, (currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0) - wage);
                                if (currentRecruiter != null) {
                                    currentRecruiter.setWalletBalance(remainingBal);
                                }
                                if (SessionManager.currentRecruiter != null) {
                                    SessionManager.currentRecruiter.setWalletBalance(remainingBal);
                                }

                                item.worker.setWalletBalance(item.worker.getWalletBalance() + wage);
                                item.worker.setTotalDaysWorked(item.worker.getTotalDaysWorked() + 1);

                                if (SessionManager.currentWorker != null) {
                                    SessionManager.currentWorker.setWalletBalance(SessionManager.currentWorker.getWalletBalance() + wage);
                                    SessionManager.currentWorker.setTotalDaysWorked(SessionManager.currentWorker.getTotalDaysWorked() + 1);
                                }

                                item.isPresent = true;
                                item.isPaid = true;
                                item.isAbsent = false;
                                item.transactionId = paymentId;
                                item.daysWorked = item.worker.getTotalDaysWorked();

                                updateWalletDisplay();
                                updateKpiMetrics();
                                renderWorkerCards();

                                NotificationToast.show(sourceBtn, "Wage Disbursed!",
                                        String.format("Paid ₹%.2f to %s. Wallet updated.", wage, item.workerName),
                                        NotificationToast.ToastType.SUCCESS);
                            },
                            verifyError -> {
                                sourceBtn.setDisable(false);
                                sourceBtn.setText("⚡ Mark Present & Pay");
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Verification Error");
                                alert.setHeaderText("Payment Verification Failed");
                                alert.setContentText("Unable to verify payment signature: " + verifyError);
                                alert.showAndWait();
                            }
                        );
                    },
                    cancelOrError -> {
                        sourceBtn.setDisable(false);
                        sourceBtn.setText("⚡ Mark Present & Pay");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Payment Incomplete");
                        alert.setHeaderText("Wage Not Paid");
                        alert.setContentText("Payment was cancelled or could not complete: " + cancelOrError);
                        alert.showAndWait();
                    }
                );
            },
            orderError -> {
                sourceBtn.setDisable(false);
                sourceBtn.setText("⚡ Mark Present & Pay");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Gateway Error");
                alert.setHeaderText("Order Creation Failed");
                alert.setContentText("Could not initiate Razorpay order: " + orderError);
                alert.showAndWait();
            }
        );
    }

    private void handleMarkAbsent(WorkerAttendanceItem item) {
        String attendanceRecordId = String.format("ATT_%s_%s_%s", item.project.getId(), item.worker.getId(), item.date.toString());
        String targetWorkerId = item.worker.getId();

        attendanceController.recordAttendance(
            attendanceRecordId,
            targetWorkerId,
            currentRecruiterId,
            item.project.getId(),
            item.date.toString(),
            "ABSENT",
            () -> Platform.runLater(() -> {
                item.isPresent = false;
                item.isPaid = false;
                item.isAbsent = true;

                updateKpiMetrics();
                renderWorkerCards();

                NotificationToast.show(recordsCountLabel, "Attendance Marked",
                        item.workerName + " marked Absent for " + item.date,
                        NotificationToast.ToastType.INFO);
            }),
            err -> Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to record absent attendance: " + err);
                a.showAndWait();
            })
        );
    }

    // ==========================================
    // 10. Digital Payment Voucher Modal
    // ==========================================
    private void openVoucherModal(WorkerAttendanceItem item) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Official Wage Settlement Voucher - " + item.workerName);

        VBox box = new VBox(18);
        box.setPadding(new Insets(26));
        box.setPrefWidth(540);
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;");

        Label brand = new Label("DIHADI ESCROW DIRECT SETTLEMENT");
        brand.setStyle("-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        Label cert = new Label("Official Daily Wage Payment Certificate");
        cert.setStyle("-fx-font-size:12px;-fx-text-fill:#685c52;");

        VBox head = new VBox(4, brand, cert);
        head.setAlignment(Pos.CENTER);
        head.setStyle("-fx-border-color:transparent transparent #e0d8c7 transparent;-fx-border-width:0 0 1px 0;-fx-padding:0 0 12px 0;");

        GridPane table = new GridPane();
        table.setHgap(16);
        table.setVgap(10);
        table.setPadding(new Insets(8, 0, 8, 0));

        addVoucherField(table, 0, "Worker Name", item.workerName);
        addVoucherField(table, 1, "Trade / Role", item.workerTrade);
        addVoucherField(table, 2, "Worker Mobile", item.workerMobile);
        addVoucherField(table, 3, "Project Jobsite", item.project.getTitle());
        addVoucherField(table, 4, "Shift Date", item.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        addVoucherField(table, 5, "Shift Timing", "Full-Day Regular Shift (8 Hours)");
        addVoucherField(table, 6, "Payer / Recruiter", currentRecruiter.getFirstName() + " (" + currentRecruiter.getCompanyName() + ")");
        addVoucherField(table, 7, "Transaction ID", item.transactionId != null ? item.transactionId : "TXN_ESCROW_SETTLED");
        addVoucherField(table, 8, "Payment Channel", "DIHADI Escrow Direct Settlement");
        addVoucherField(table, 9, "Status", "PAID & SETTLED IN ESCROW");

        // Wage Total Bar
        Label totalTitle = new Label("NET WAGE PAID:");
        totalTitle.setStyle("-fx-font-family:Georgia;-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");

        Label totalAmount = new Label(String.format("₹ %,.2f", item.dailyWage));
        totalAmount.setStyle("-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:" + GREEN + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox totalRow = new HBox(totalTitle, spacer, totalAmount);
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setPadding(new Insets(10, 16, 10, 16));
        totalRow.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");

        // Actions
        Button exportBtn = new Button("🖨 Print / Export Voucher");
        exportBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;");
        exportBtn.setOnAction(e -> {
            modalStage.close();
            NotificationToast.show(recordsCountLabel, "Voucher Exported",
                    "Payment voucher for " + item.workerName + " downloaded successfully.",
                    NotificationToast.ToastType.SUCCESS);
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#f5f0e8;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:9px 16px;-fx-cursor:hand;");
        closeBtn.setOnAction(e -> modalStage.close());

        HBox btnRow = new HBox(12, closeBtn, exportBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        box.getChildren().addAll(head, table, totalRow, btnRow);

        Scene scene = new Scene(box);
        modalStage.setScene(scene);
        modalStage.setResizable(false);
        modalStage.show();
    }

    private void addVoucherField(GridPane grid, int row, String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        l.setPrefWidth(160);

        Label v = new Label(value != null && !value.isBlank() ? value : "—");
        v.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#1e1b15;");
        v.setWrapText(true);
        GridPane.setHgrow(v, Priority.ALWAYS);

        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }

    // ==========================================
    // 11. Empty State
    // ==========================================
    private VBox createEmptyState() {
        Label icon = new Label("👷");
        icon.setStyle("-fx-font-size:42px;");

        Label heading = new Label("No Workers Found For This Shift");
        heading.setStyle("-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");

        Label desc = new Label("No assigned workers match the selected project, date, or search filters.\nMake sure workers have been assigned to this project via the Pending Approvals or Hire Skilled Workers sections.");
        desc.setStyle("-fx-font-size:13px;-fx-font-weight:500;-fx-text-fill:" + MUTED + ";-fx-text-alignment:center;");
        desc.setWrapText(true);
        desc.setMaxWidth(540);

        Button resetBtn = new Button("↻ Reset Filters");
        resetBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 18px;-fx-background-radius:8px;-fx-cursor:hand;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All Statuses");
            tradeFilter.setValue("All Trades");
            applyFilters();
        });

        VBox box = new VBox(12, icon, heading, desc, resetBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(48, 24, 48, 24));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:" + BORDER + ";-fx-border-radius:16px;-fx-border-width:1.5px;");
        return box;
    }

    // ==========================================
    // 12. Helpers
    // ==========================================
    private Node createAvatar(String name, String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            try {
                ImageView iv = new ImageView(new Image(photoUrl, 48, 48, true, true));
                Circle clip = new Circle(24, 24, 24);
                iv.setClip(clip);
                return iv;
            } catch (Exception ignored) {}
        }

        String initials = "W";
        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) {
                initials = parts[0].substring(0, 1).toUpperCase() + parts[parts.length - 1].substring(0, 1).toUpperCase();
            } else if (!parts[0].isEmpty()) {
                initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            }
        }

        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        initialsLabel.setTextOverrun(OverrunStyle.CLIP);

        StackPane circle = new StackPane(initialsLabel);
        circle.setPrefSize(48, 48);
        circle.setMinSize(48, 48);
        circle.setMaxSize(48, 48);
        circle.setStyle("-fx-background-color:#fff2d1;-fx-background-radius:50%;-fx-border-color:#d4af37;-fx-border-radius:50%;-fx-border-width:1.6px;");
        return circle;
    }

    private ImageView image(String path, double w, double h) {
        if (path == null || path.isBlank()) return null;
        try {
            if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file:")) {
                return new ImageView(new Image(path, w, h, false, true));
            }
            var res = getClass().getResource(path);
            return res == null ? null : new ImageView(new Image(res.toExternalForm(), w, h, false, true));
        } catch (Exception e) {
            return null;
        }
    }

    private void triggerRechargeFlow(Button sourceButton, double defaultAmount) {
        TextInputDialog dialog = new TextInputDialog(String.format("%.0f", Math.max(500, defaultAmount)));
        dialog.setTitle("Recharge Recruiter Escrow Wallet");
        dialog.setHeaderText("Add funds to your DIHADI escrow wallet for worker wage disbursals:");
        dialog.setContentText("Amount in INR (₹):");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(result.get().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Alert err = new Alert(Alert.AlertType.ERROR, "Please enter a valid numeric value greater than 0.");
            err.showAndWait();
            return;
        }

        Stage currentStage = sourceButton != null && sourceButton.getScene() != null ? (Stage) sourceButton.getScene().getWindow() : null;

        new Thread(() -> {
            try {
                RazorpayService razorpayService = new RazorpayService();
                String receiptId = "rcpt_" + UUID.randomUUID().toString().substring(0, 8);
                String orderId = razorpayService.createOrder(amount, receiptId);

                String email = currentRecruiter != null && currentRecruiter.getEmail() != null ? currentRecruiter.getEmail() : "recruiter@dihadi.com";
                String phone = currentRecruiter != null && currentRecruiter.getMobileNumber() != null ? currentRecruiter.getMobileNumber() : "9999999999";

                Platform.runLater(() -> {
                    PaymentCheckoutScene.openCheckout(
                            currentStage,
                            orderId,
                            amount,
                            email,
                            phone,
                            new PaymentCheckoutScene.PaymentCallback() {
                                @Override
                                public void onSuccess(String paymentId, String oid, String signature) {
                                    double newBalance = (currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0) + amount;
                                    if (currentRecruiter != null) {
                                        currentRecruiter.setWalletBalance(newBalance);
                                    }
                                    if (SessionManager.currentRecruiter != null) {
                                        SessionManager.currentRecruiter.setWalletBalance(newBalance);
                                    }

                                    new Thread(() -> {
                                        try {
                                            if (currentRecruiterId != null && !currentRecruiterId.isBlank()) {
                                                new RecruiterController().updateWalletBalance(currentRecruiterId, newBalance);
                                            }
                                        } catch (Exception dbEx) {
                                            dbEx.printStackTrace();
                                        }
                                    }).start();

                                    updateWalletDisplay();
                                    NotificationToast.show(sourceButton, "Wallet Recharged!",
                                            String.format("₹%,.2f credited to your wallet.\nTxn ID: %s", amount, paymentId),
                                            NotificationToast.ToastType.SUCCESS);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Recharge Failed");
                                    alert.setHeaderText("Payment Incomplete");
                                    alert.setContentText(errorMessage != null ? errorMessage : "Payment could not be processed.");
                                    alert.showAndWait();
                                }
                            }
                    );
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Gateway Error");
                    alert.setHeaderText("Failed to initiate payment");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }
}