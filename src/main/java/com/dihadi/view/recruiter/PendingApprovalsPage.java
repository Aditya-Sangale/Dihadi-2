package com.dihadi.view.recruiter;

import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.NotificationController;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkerController;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Project;
import com.dihadi.model.Recruiter;
import com.dihadi.model.Worker;
import com.dihadi.service.WorkerAvailabilityService;
import com.dihadi.view.NotificationToast;
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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Enterprise-grade Pending Worker Applications Interface for Recruiters.
 * Features live KPI analytics, multi-criteria filtering, rich applicant dossiers,
 * project fulfillment tracking, and detailed worker credential modals.
 */
public class PendingApprovalsPage {

    private static final String BG_COLOR = "#f3e7ce";
    private static final String CARD_BG = "#ffffff";
    private static final String BORDER = "#d0c5af";
    private static final String GOLD = "#735c00";
    private static final String INK = "#1e1b15";
    private static final String MUTED = "#685c52";
    private static final String GREEN = "#2e7d32";
    private static final String BLUE = "#1565c0";

    private final Recruiter recruiter;
    private final JobApplicationController jobAppCtrl = new JobApplicationController();
    private final WorkerController workerCtrl = new WorkerController();
    private final ProjectController projectCtrl = new ProjectController();

    // Data lists
    private final List<ApplicationItem> masterList = new ArrayList<>();
    private final List<ApplicationItem> filteredList = new ArrayList<>();
    private final Map<String, Project> projectMap = new HashMap<>();

    // UI elements for live updates
    private Label pendingMetricLabel;
    private Label projectsMetricLabel;
    private Label workforceNeededMetricLabel;
    private Label avgWageMetricLabel;

    private TextField searchField;
    private ComboBox<String> projectFilter;
    private ComboBox<String> tradeFilter;
    private ComboBox<String> sortFilter;
    private Label recordsCountLabel;

    private VBox recordsListContainer;
    private ProgressIndicator loadingSpinner;
    private VBox emptyStateBox;

    /**
     * Complete item model representing a worker application and its rich context.
     */
    public static class ApplicationItem {
        public JobApplication app;
        public Worker worker;
        public Project project;

        public String workerName = "Worker";
        public String workerMobile = "";
        public String workerTrade = "General Worker";
        public String workerSubSkill = "";
        public String workerExperience = "Experienced";
        public String workerCity = "Maharashtra";
        public String workerState = "India";
        public String workerEducation = "Standard";
        public String workerPhotoUrl = null;

        public String projectId = "";
        public String projectName = "General Construction Site";
        public String projectLocation = "Maharashtra Site";
        public int targetWorkers = 0;
        public int acceptedWorkers = 0;

        public double dailyWage = 0.0;
        public Date appliedDate = new Date();

        public ApplicationItem() {}
    }

    public PendingApprovalsPage() {
        this(SessionManager.currentRecruiter);
    }

    public PendingApprovalsPage(Recruiter recruiter) {
        this.recruiter = recruiter != null ? recruiter : (SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : new Recruiter());
    }

    public Scene getScene(Runnable back) {
        VBox root = new VBox(22);
        root.setPadding(new Insets(28, 48, 40, 48));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.setMaxWidth(1440);
        root.setAlignment(Pos.TOP_CENTER);

        // 1. Brand Header with standardized Back to Dashboard button
        HBox header = createHeader(back);

        // 2. Title and Banner
        VBox titleBanner = createTitleBanner();

        // 3. Live KPI Summary Row
        HBox kpiRow = createKpiMetricsRow();

        // 4. Search and Filter Bar
        VBox filterBar = createFilterControls();

        // 5. Records Container
        recordsListContainer = new VBox(14);
        recordsListContainer.setAlignment(Pos.TOP_CENTER);
        recordsListContainer.setFillWidth(true);

        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setPrefSize(44, 44);
        loadingSpinner.setStyle("-fx-progress-color: " + GOLD + ";");

        emptyStateBox = createEmptyState();
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);

        VBox contentSection = new VBox(16, filterBar, loadingSpinner, emptyStateBox, recordsListContainer);
        contentSection.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(header, titleBanner, kpiRow, contentSection);

        StackPane centerWrapper = new StackPane(root);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setStyle("-fx-background-color: " + BG_COLOR + ";");

        ScrollPane scroll = new ScrollPane(centerWrapper);
        scroll.setFitToWidth(true);
        ScrollUtils.style(scroll);
        scroll.setStyle("-fx-background:" + BG_COLOR + ";-fx-background-color:" + BG_COLOR + ";-fx-border-width:0;");

        // Load data asynchronously
        loadApplicationsAsync(back);

        return new Scene(scroll, 1420, 880);
    }

    // ==========================================
    // 1. Header Bar
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

        Button backBtn = new Button("← Back to Dashboard");
        String backIdle = "-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;";
        String backHover = "-fx-background-color:#ffffff;-fx-text-fill:" + GOLD + ";-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:" + GOLD + ";-fx-border-radius:10px;-fx-background-radius:10px;";
        backBtn.setStyle(backIdle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(backIdle));
        backBtn.setOnAction(e -> {
            if (back != null) back.run();
        });

        String recruiterName = (recruiter.getFirstName() != null ? recruiter.getFirstName() : "Recruiter")
                + (recruiter.getLastName() != null ? " " + recruiter.getLastName() : "");
        String companyName = recruiter.getCompanyName() != null && !recruiter.getCompanyName().isBlank()
                ? recruiter.getCompanyName() : "General Contractor";

        Label profileBadge = new Label(recruiterName + " (" + companyName + ")");
        profileBadge.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#3f392e;-fx-background-color:#fffaf0;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;-fx-padding:7px 16px;");
        profileBadge.setTextOverrun(OverrunStyle.CLIP);

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color:#ffffff;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:7px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;");
        refreshBtn.setOnAction(e -> loadApplicationsAsync(back));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12, backBtn, refreshBtn, profileBadge);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(20, brandBox, spacer, actions);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return header;
    }

    // ==========================================
    // 2. Title Banner
    // ==========================================
    private VBox createTitleBanner() {
        Label eyebrow = new Label("✦  RECRUITER TALENT DESK & ONBOARDING");
        eyebrow.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:" + GOLD + ";-fx-background-color:#fffaf0;-fx-background-radius:10px;-fx-padding:4px 10px;-fx-border-color:#e2d5bd;-fx-border-radius:10px;");
        eyebrow.setTextOverrun(OverrunStyle.CLIP);

        Label title = new Label("Pending Worker Applications");
        title.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        title.setTextOverrun(OverrunStyle.CLIP);

        Label subtitle = new Label("Review incoming worker applications, examine verified skill credentials, inspect jobsite filling status, and assign workers directly to your projects.");
        subtitle.setStyle("-fx-font-size:14px;-fx-font-weight:500;-fx-text-fill:" + MUTED + ";");
        subtitle.setTextOverrun(OverrunStyle.CLIP);
        subtitle.setWrapText(true);

        VBox banner = new VBox(8, eyebrow, title, subtitle);
        banner.setAlignment(Pos.CENTER_LEFT);
        return banner;
    }

    // ==========================================
    // 3. KPI Metrics Row
    // ==========================================
    private HBox createKpiMetricsRow() {
        pendingMetricLabel = new Label("0");
        pendingMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");

        projectsMetricLabel = new Label("0");
        projectsMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + BLUE + ";");

        workforceNeededMetricLabel = new Label("0");
        workforceNeededMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#8d6e63;");

        avgWageMetricLabel = new Label("₹ 0.00");
        avgWageMetricLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:" + GREEN + ";");

        VBox card1 = kpiCard("PENDING REVIEWS", pendingMetricLabel, "Awaiting recruiter decision", GOLD);
        VBox card2 = kpiCard("ACTIVE SITES IN NEED", projectsMetricLabel, "Projects receiving applications", BLUE);
        VBox card3 = kpiCard("TARGET WORKFORCE NEEDED", workforceNeededMetricLabel, "Total positions across projects", "#8d6e63");
        VBox card4 = kpiCard("AVERAGE OFFERED WAGE", avgWageMetricLabel, "Standard daily 8-hr shift rate", GREEN);

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
    // 4. Search and Filter Controls
    // ==========================================
    private VBox createFilterControls() {
        searchField = new TextField();
        searchField.setPromptText("🔍 Search by worker name, phone number, trade, sub-skill, or project...");
        searchField.setPrefHeight(42);
        searchField.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;-fx-padding:0 14px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());

        projectFilter = new ComboBox<>();
        projectFilter.getItems().add("All Projects");
        projectFilter.setValue("All Projects");
        projectFilter.setPrefHeight(42);
        projectFilter.setPrefWidth(220);
        projectFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        projectFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());

        tradeFilter = new ComboBox<>();
        tradeFilter.getItems().addAll("All Trades", "Mason", "Carpenter", "Electrician", "Painter", "Plumber", "Welder", "General Labour", "Site Supervisor", "ITI Technician");
        tradeFilter.setValue("All Trades");
        tradeFilter.setPrefHeight(42);
        tradeFilter.setPrefWidth(180);
        tradeFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        tradeFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());

        sortFilter = new ComboBox<>();
        sortFilter.getItems().addAll("Newest First", "Oldest First", "Highest Wage", "Lowest Wage", "Worker Name (A-Z)");
        sortFilter.setValue("Newest First");
        sortFilter.setPrefHeight(42);
        sortFilter.setPrefWidth(170);
        sortFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        sortFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());

        Button clearBtn = new Button("Clear Filters");
        clearBtn.setPrefHeight(42);
        clearBtn.setStyle("-fx-background-color:#f5f0e8;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:0 16px;-fx-cursor:hand;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            projectFilter.setValue("All Projects");
            tradeFilter.setValue("All Trades");
            sortFilter.setValue("Newest First");
            applyFilters();
        });

        HBox filterRow = new HBox(12, searchField, projectFilter, tradeFilter, sortFilter, clearBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        recordsCountLabel = new Label("Showing 0 of 0 pending applications");
        recordsCountLabel.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label infoHint = new Label("💡 Tip: Approving a worker automatically issues SMS notification and links attendance.");
        infoHint.setStyle("-fx-font-size:12px;-fx-text-fill:#7a7267;");

        HBox statusRow = new HBox(10, recordsCountLabel, spacer, infoHint);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(10, filterRow, statusRow);
        container.setPadding(new Insets(4, 0, 4, 0));
        return container;
    }

    // ==========================================
    // 5. Asynchronous Data Loading
    // ==========================================
    private void loadApplicationsAsync(Runnable back) {
        loadingSpinner.setVisible(true);
        loadingSpinner.setManaged(true);
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
        recordsListContainer.getChildren().clear();

        new Thread(() -> {
            List<ApplicationItem> items = new ArrayList<>();
            Set<String> projectNamesSet = new TreeSet<>();

            try {
                // 1. Fetch Workers for lookup
                List<Worker> allWorkers = workerCtrl.getAllWorkers();
                Map<String, Worker> workerMap = new HashMap<>();
                if (allWorkers != null) {
                    for (Worker w : allWorkers) {
                        if (w.getMobileNumber() != null) {
                            String clean = cleanMobile(w.getMobileNumber());
                            workerMap.put(clean, w);
                            if (clean.length() >= 10) {
                                workerMap.put(clean.substring(clean.length() - 10), w);
                            }
                        }
                        if (w.getUid() != null) {
                            workerMap.put(w.getUid(), w);
                        }
                    }
                }

                // 2. Fetch Projects for lookup
                List<Project> allProjects = projectCtrl.getAllProjects();
                projectMap.clear();
                Set<String> recruiterProjIds = new HashSet<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (p.getProjectId() != null) {
                            projectMap.put(p.getProjectId(), p);
                            if (isMatch(p, recruiter)) {
                                recruiterProjIds.add(p.getProjectId());
                            }
                        }
                    }
                }

                String rMobDigits = recruiter != null && recruiter.getMobileNumber() != null
                        ? cleanMobile(recruiter.getMobileNumber())
                        : "";

                // 3. Fetch Job Applications
                List<JobApplication> allApps = jobAppCtrl.getAllApplications();
                Map<String, JobApplication> deduplicatedPending = new LinkedHashMap<>();

                if (allApps != null) {
                    for (JobApplication app : allApps) {
                        if ("Pending".equalsIgnoreCase(app.getStatus())) {
                            boolean isOutgoingHire = (app.getJobTitle() != null && app.getJobTitle().contains("Hiring Request"))
                                    || "DIRECT_HIRE".equalsIgnoreCase(app.getRequirementId());
                            if (isOutgoingHire) {
                                continue;
                            }

                            boolean matchesRecruiter = false;
                            if (app.getProjectId() != null && recruiterProjIds.contains(app.getProjectId())) {
                                matchesRecruiter = true;
                            } else {
                                String appRMob = app.getRecruiterMobile() != null ? cleanMobile(app.getRecruiterMobile()) : "";
                                if (!rMobDigits.isEmpty() && !appRMob.isEmpty()) {
                                    matchesRecruiter = appRMob.equals(rMobDigits) || appRMob.endsWith(rMobDigits) || rMobDigits.endsWith(appRMob);
                                }
                            }

                            if (matchesRecruiter) {
                                String workerKey = (app.getWorkerMobile() != null ? cleanMobile(app.getWorkerMobile()) : "")
                                        + "_" + (app.getProjectId() != null ? app.getProjectId() : "");
                                deduplicatedPending.putIfAbsent(workerKey, app);
                            }
                        }
                    }
                }

                // 4. Construct ApplicationItem objects
                for (JobApplication app : deduplicatedPending.values()) {
                    ApplicationItem item = new ApplicationItem();
                    item.app = app;
                    item.projectId = app.getProjectId() != null ? app.getProjectId() : "";
                    item.workerMobile = app.getWorkerMobile() != null ? app.getWorkerMobile() : "";

                    String cleanMob = cleanMobile(item.workerMobile);
                    Worker w = workerMap.get(cleanMob);
                    if (w == null && cleanMob.length() >= 10) {
                        w = workerMap.get(cleanMob.substring(cleanMob.length() - 10));
                    }
                    item.worker = w;

                    // Worker Identity & Details
                    if (w != null) {
                        String fn = w.getFirstName() != null ? w.getFirstName().trim() : "";
                        String mn = w.getMiddleName() != null ? w.getMiddleName().trim() : "";
                        String ln = w.getLastName() != null ? w.getLastName().trim() : "";
                        String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
                        if (!full.isEmpty()) {
                            item.workerName = full;
                        }
                        if (w.getWorkerType() != null && !w.getWorkerType().isBlank()) {
                            item.workerTrade = w.getWorkerType();
                        }
                        if (w.getSubSkill() != null && !w.getSubSkill().isBlank()) {
                            item.workerSubSkill = w.getSubSkill();
                        }
                        if (w.getExperience() != null && !w.getExperience().isBlank()) {
                            item.workerExperience = w.getExperience();
                        }
                        if (w.getCity() != null && !w.getCity().isBlank()) {
                            item.workerCity = w.getCity();
                        }
                        if (w.getState() != null && !w.getState().isBlank()) {
                            item.workerState = w.getState();
                        }
                        if (w.getEducation() != null && !w.getEducation().isBlank()) {
                            item.workerEducation = w.getEducation();
                        }
                        item.workerPhotoUrl = w.getProfilePhotoUrl();
                    } else if (app.getWorkerName() != null && !app.getWorkerName().isBlank()) {
                        item.workerName = app.getWorkerName();
                    }

                    if (item.workerTrade.equals("General Worker") && app.getJobTitle() != null && !app.getJobTitle().isBlank()) {
                        item.workerTrade = app.getJobTitle();
                    }

                    // Project Details
                    Project p = projectMap.get(item.projectId);
                    item.project = p;
                    if (p != null) {
                        if (p.getProjectName() != null && !p.getProjectName().isBlank()) {
                            item.projectName = p.getProjectName();
                        }
                        String loc = (p.getCity() != null ? p.getCity() : "")
                                + (p.getState() != null && !p.getState().isBlank() ? ", " + p.getState() : "");
                        if (!loc.isBlank()) item.projectLocation = loc;
                        else if (p.getAddressLine1() != null) item.projectLocation = p.getAddressLine1();

                        projectNamesSet.add(item.projectName);
                    } else if (app.getJobLocation() != null && !app.getJobLocation().isBlank()) {
                        item.projectLocation = app.getJobLocation();
                    }

                    // Workforce target and accepted count
                    if (!item.projectId.isBlank()) {
                        try {
                            item.targetWorkers = projectCtrl.getTargetWorkforceCount(item.projectId);
                            item.acceptedWorkers = projectCtrl.getAcceptedWorkerCount(item.projectId);
                        } catch (Exception ignored) {}
                    }

                    // Wage parsing
                    if (app.getJobWage() != null && !app.getJobWage().isBlank()) {
                        try {
                            String cleanWage = app.getJobWage().replaceAll("[^0-9.]", "");
                            item.dailyWage = Double.parseDouble(cleanWage);
                        } catch (Exception ignored) {
                            if (w != null && w.getDailyWage() > 0) item.dailyWage = w.getDailyWage();
                            else item.dailyWage = 800.0;
                        }
                    } else if (w != null && w.getDailyWage() > 0) {
                        item.dailyWage = w.getDailyWage();
                    } else {
                        item.dailyWage = 800.0;
                    }

                    if (app.getTimestamp() != null) {
                        item.appliedDate = app.getTimestamp();
                    }

                    items.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                loadingSpinner.setManaged(false);

                masterList.clear();
                masterList.addAll(items);

                // Populate project filter dropdown
                String prevProjVal = projectFilter.getValue();
                projectFilter.getItems().clear();
                projectFilter.getItems().add("All Projects");
                projectFilter.getItems().addAll(projectNamesSet);
                if (projectNamesSet.contains(prevProjVal)) {
                    projectFilter.setValue(prevProjVal);
                } else {
                    projectFilter.setValue("All Projects");
                }

                applyFilters();
            });
        }).start();
    }

    // ==========================================
    // 6. Filtering & KPI Aggregations
    // ==========================================
    private void applyFilters() {
        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String projSel = projectFilter != null ? projectFilter.getValue() : "All Projects";
        String tradeSel = tradeFilter != null ? tradeFilter.getValue() : "All Trades";
        String sortSel = sortFilter != null ? sortFilter.getValue() : "Newest First";

        filteredList.clear();

        for (ApplicationItem item : masterList) {
            // Search matching
            if (!query.isEmpty()) {
                boolean match = false;
                if (item.workerName != null && item.workerName.toLowerCase().contains(query)) match = true;
                if (item.workerMobile != null && item.workerMobile.toLowerCase().contains(query)) match = true;
                if (item.workerTrade != null && item.workerTrade.toLowerCase().contains(query)) match = true;
                if (item.workerSubSkill != null && item.workerSubSkill.toLowerCase().contains(query)) match = true;
                if (item.projectName != null && item.projectName.toLowerCase().contains(query)) match = true;
                if (item.projectLocation != null && item.projectLocation.toLowerCase().contains(query)) match = true;
                if (!match) continue;
            }

            // Project filter
            if (projSel != null && !projSel.equals("All Projects")) {
                if (item.projectName == null || !item.projectName.equalsIgnoreCase(projSel)) {
                    continue;
                }
            }

            // Trade filter
            if (tradeSel != null && !tradeSel.equals("All Trades")) {
                if (item.workerTrade == null || !item.workerTrade.toLowerCase().contains(tradeSel.toLowerCase())) {
                    continue;
                }
            }

            filteredList.add(item);
        }

        // Sorting
        if ("Highest Wage".equals(sortSel)) {
            filteredList.sort((a, b) -> Double.compare(b.dailyWage, a.dailyWage));
        } else if ("Lowest Wage".equals(sortSel)) {
            filteredList.sort((a, b) -> Double.compare(a.dailyWage, b.dailyWage));
        } else if ("Oldest First".equals(sortSel)) {
            filteredList.sort(Comparator.comparing(a -> a.appliedDate != null ? a.appliedDate : new Date(0)));
        } else if ("Worker Name (A-Z)".equals(sortSel)) {
            filteredList.sort((a, b) -> a.workerName.compareToIgnoreCase(b.workerName));
        } else {
            // Newest First
            filteredList.sort((a, b) -> {
                Date d1 = a.appliedDate != null ? a.appliedDate : new Date(0);
                Date d2 = b.appliedDate != null ? b.appliedDate : new Date(0);
                return d2.compareTo(d1);
            });
        }

        updateKpiMetrics();
        renderApplicantCards();
    }

    private void updateKpiMetrics() {
        int pendingCount = masterList.size();
        Set<String> uniqueProjects = new HashSet<>();
        int totalPositionsNeeded = 0;
        double sumWage = 0.0;

        for (ApplicationItem item : masterList) {
            if (item.projectId != null && !item.projectId.isBlank()) {
                uniqueProjects.add(item.projectId);
            } else if (item.projectName != null) {
                uniqueProjects.add(item.projectName);
            }
            sumWage += item.dailyWage;
        }

        for (String pId : uniqueProjects) {
            Project p = projectMap.get(pId);
            if (p != null) {
                try {
                    int target = projectCtrl.getTargetWorkforceCount(pId);
                    int current = projectCtrl.getAcceptedWorkerCount(pId);
                    int remaining = Math.max(0, target - current);
                    totalPositionsNeeded += (remaining > 0 ? remaining : target);
                } catch (Exception ignored) {}
            }
        }

        double avgWage = pendingCount > 0 ? (sumWage / pendingCount) : 0.0;

        pendingMetricLabel.setText(String.valueOf(pendingCount));
        projectsMetricLabel.setText(String.valueOf(uniqueProjects.size()));
        workforceNeededMetricLabel.setText(String.valueOf(totalPositionsNeeded > 0 ? totalPositionsNeeded : pendingCount * 2));
        avgWageMetricLabel.setText(String.format("₹ %,.2f", avgWage));
    }

    private void renderApplicantCards() {
        recordsCountLabel.setText("Showing " + filteredList.size() + " of " + masterList.size() + " pending applications");
        recordsListContainer.getChildren().clear();

        if (filteredList.isEmpty()) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
        } else {
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);

            for (ApplicationItem item : filteredList) {
                recordsListContainer.getChildren().add(createApplicantCard(item));
            }
        }
    }

    // ==========================================
    // 7. Rich Applicant Card Layout
    // ==========================================
    private Node createApplicantCard(ApplicationItem item) {
        // 1. Left: Worker Avatar & Status Indicator
        Node avatarNode = createAvatar(item.workerName, item.workerPhotoUrl);

        Label statusDot = new Label("🟢 Available for Hire");
        statusDot.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#2e7d32;");
        statusDot.setTextOverrun(OverrunStyle.CLIP);

        VBox leftCol = new VBox(6, avatarNode, statusDot);
        leftCol.setAlignment(Pos.CENTER);
        leftCol.setPrefWidth(90);

        // 2. Middle: Worker Details & Project Site Context
        Label nameLabel = new Label(item.workerName);
        nameLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");
        nameLabel.setTextOverrun(OverrunStyle.CLIP);
        nameLabel.setWrapText(true);

        Label tradeBadge = new Label(item.workerTrade.toUpperCase());
        tradeBadge.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-background-color:#fff8e7;-fx-background-radius:8px;-fx-padding:3px 9px;-fx-border-color:#f0d890;-fx-border-radius:8px;");
        tradeBadge.setTextOverrun(OverrunStyle.CLIP);

        Label expBadge = new Label("⭐ " + item.workerExperience + " Exp");
        expBadge.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#f5f0e8;-fx-background-radius:8px;-fx-padding:3px 9px;-fx-border-color:#d0c5af;-fx-border-radius:8px;");
        expBadge.setTextOverrun(OverrunStyle.CLIP);

        Label verifiedBadge = new Label("🛡️ Verified DIHADI Pro");
        verifiedBadge.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-padding:3px 8px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");
        verifiedBadge.setTextOverrun(OverrunStyle.CLIP);

        HBox topRow = new HBox(8, nameLabel, tradeBadge, expBadge, verifiedBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label contactInfo = new Label("📱 " + item.workerMobile + "   •   📍 " + item.workerCity + ", " + item.workerState + "   •   🎓 " + item.workerEducation);
        contactInfo.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:" + MUTED + ";");
        contactInfo.setTextOverrun(OverrunStyle.CLIP);

        // Project Site Strip
        Label projIcon = new Label("🏗️");
        Label projTitle = new Label("Applying For: " + item.projectName);
        projTitle.setStyle("-fx-font-family:Georgia;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1565c0;");
        projTitle.setTextOverrun(OverrunStyle.CLIP);

        Label projLoc = new Label("(" + item.projectLocation + ")");
        projLoc.setStyle("-fx-font-size:12px;-fx-text-fill:#685c52;");
        projLoc.setTextOverrun(OverrunStyle.CLIP);

        String progressText = item.targetWorkers > 0
                ? "👥 " + item.acceptedWorkers + " / " + item.targetWorkers + " Workers Assigned"
                : "👥 Ongoing Project Requirement";
        Label progressBadge = new Label(progressText);
        progressBadge.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#735c00;-fx-background-color:#fef8eb;-fx-background-radius:6px;-fx-padding:2px 8px;");
        progressBadge.setTextOverrun(OverrunStyle.CLIP);

        Region siteSpacer = new Region();
        HBox.setHgrow(siteSpacer, Priority.ALWAYS);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        Label appliedDateLabel = new Label("🗓 Applied: " + df.format(item.appliedDate));
        appliedDateLabel.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#7a7267;");
        appliedDateLabel.setTextOverrun(OverrunStyle.CLIP);

        HBox projectStrip = new HBox(8, projIcon, projTitle, projLoc, progressBadge, siteSpacer, appliedDateLabel);
        projectStrip.setAlignment(Pos.CENTER_LEFT);
        projectStrip.setPadding(new Insets(6, 12, 6, 12));
        projectStrip.setStyle("-fx-background-color:#fbf7ef;-fx-background-radius:8px;-fx-border-color:#e6dccb;-fx-border-radius:8px;");

        VBox middleCol = new VBox(6, topRow, contactInfo, projectStrip);
        if (item.workerSubSkill != null && !item.workerSubSkill.isBlank()) {
            Label subSkillLabel = new Label("Specialization / Core Skills: " + item.workerSubSkill);
            subSkillLabel.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#735c00;");
            middleCol.getChildren().add(subSkillLabel);
        }
        middleCol.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(middleCol, Priority.ALWAYS);

        // 3. Right: Wage Quote & Decision Actions
        Label wageHead = new Label("DAILY WAGE RATE");
        wageHead.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        wageHead.setTextOverrun(OverrunStyle.CLIP);

        Label wageAmount = new Label(String.format("₹ %,.2f", item.dailyWage));
        wageAmount.setStyle("-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:" + GREEN + ";");
        wageAmount.setTextOverrun(OverrunStyle.CLIP);

        Label shiftLabel = new Label("Full-Day Shift (8 hrs)");
        shiftLabel.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#4c4637;");
        shiftLabel.setTextOverrun(OverrunStyle.CLIP);

        VBox wageBox = new VBox(2, wageHead, wageAmount, shiftLabel);
        wageBox.setAlignment(Pos.CENTER_RIGHT);
        wageBox.setPrefWidth(160);

        // Action Buttons
        Button acceptBtn = new Button("✓ Accept & Assign");
        acceptBtn.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;");
        acceptBtn.setOnMouseEntered(e -> acceptBtn.setStyle("-fx-background-color:#1e5e2b;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;"));
        acceptBtn.setOnMouseExited(e -> acceptBtn.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 16px;-fx-background-radius:8px;-fx-cursor:hand;"));

        Button declineBtn = new Button("✕ Decline");
        declineBtn.setStyle("-fx-background-color:#fff1f1;-fx-border-color:#f1bcbc;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#a51d1d;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-cursor:hand;");
        declineBtn.setOnMouseEntered(e -> declineBtn.setStyle("-fx-background-color:#fee2e2;-fx-border-color:#e0a0a0;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#a51d1d;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-cursor:hand;"));
        declineBtn.setOnMouseExited(e -> declineBtn.setStyle("-fx-background-color:#fff1f1;-fx-border-color:#f1bcbc;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#a51d1d;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-cursor:hand;"));

        Button profileBtn = new Button("👁 View Profile");
        profileBtn.setStyle("-fx-background-color:#f5f0e8;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 12px;-fx-cursor:hand;");
        profileBtn.setOnMouseEntered(e -> profileBtn.setStyle("-fx-background-color:#ffffff;-fx-border-color:#735c00;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 12px;-fx-cursor:hand;"));
        profileBtn.setOnMouseExited(e -> profileBtn.setStyle("-fx-background-color:#f5f0e8;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#735c00;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 12px;-fx-cursor:hand;"));

        HBox cardRow = new HBox(18, leftCol, middleCol, wageBox, acceptBtn, declineBtn, profileBtn);
        cardRow.setAlignment(Pos.CENTER_LEFT);
        cardRow.setPadding(new Insets(18, 22, 18, 22));
        cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");

        cardRow.setOnMouseEntered(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.8px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.22),12,0,0,3px);"));
        cardRow.setOnMouseExited(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);"));

        // Button Event Handlers
        acceptBtn.setOnAction(e -> handleAcceptWorker(item, cardRow, acceptBtn, declineBtn));
        declineBtn.setOnAction(e -> handleDeclineWorker(item, cardRow, acceptBtn, declineBtn));
        profileBtn.setOnAction(e -> openWorkerProfileModal(item,
                () -> handleAcceptWorker(item, cardRow, acceptBtn, declineBtn),
                () -> handleDeclineWorker(item, cardRow, acceptBtn, declineBtn)));

        return cardRow;
    }

    // ==========================================
    // 8. Accept / Decline Actions
    // ==========================================
    private void handleAcceptWorker(ApplicationItem item, Node cardNode, Button acceptBtn, Button declineBtn) {
        acceptBtn.setDisable(true);
        declineBtn.setDisable(true);

        new Thread(() -> {
            JobApplication activeAssignment = jobAppCtrl.getActiveAssignedApplicationForWorker(item.app.getWorkerMobile());
            if (activeAssignment != null && (activeAssignment.getProjectId() == null || !activeAssignment.getProjectId().equals(item.app.getProjectId()))) {
                String activeProj = activeAssignment.getJobTitle() != null ? activeAssignment.getJobTitle() : "another active project";
                Platform.runLater(() -> {
                    acceptBtn.setDisable(false);
                    declineBtn.setDisable(false);
                    NotificationToast.show(acceptBtn, "Worker Already Assigned",
                            item.workerName + " is already actively working on '" + activeProj + "'. A worker can only be assigned to one project at a time.",
                            NotificationToast.ToastType.ALERT);
                });
                return;
            }

            // Mark Accepted
            item.app.setStatus("Accepted");
            jobAppCtrl.saveApplication(item.app);
            jobAppCtrl.updateWorkerApplicationsForProject(item.app.getWorkerMobile(), item.app.getProjectId(), "Accepted");

            // Hire worker via Availability Service
            WorkerAvailabilityService.hireWorker(
                    item.app.getWorkerMobile(),
                    item.workerName,
                    item.app.getProjectId(),
                    item.app.getJobTitle() != null ? item.app.getJobTitle() : item.workerTrade,
                    item.app.getRecruiterMobile()
            );

            // Project workforce update
            boolean isFulfilled = false;
            int targetCount = 0;
            int acceptedCount = 0;
            if (item.app.getProjectId() != null && !item.app.getProjectId().isBlank()) {
                targetCount = projectCtrl.getTargetWorkforceCount(item.app.getProjectId());
                acceptedCount = projectCtrl.getAcceptedWorkerCount(item.app.getProjectId());
                isFulfilled = projectCtrl.checkAndUpdateProjectFulfilledStatus(item.app.getProjectId());
            }

            String recName = (recruiter != null && recruiter.getFirstName() != null)
                    ? (recruiter.getFirstName() + " " + (recruiter.getLastName() != null ? recruiter.getLastName() : "")).trim()
                    : "Site Recruiter";

            new NotificationController().notifyWorkerApplicationAccepted(item.app, recName, item.projectName);

            final boolean finalFulfilled = isFulfilled;
            final int finalTarget = targetCount;
            final int finalAccepted = acceptedCount;

            Platform.runLater(() -> {
                String msg;
                if (finalFulfilled) {
                    msg = item.workerName + " assigned to " + item.projectName + ". Target of " + finalTarget + " workers fulfilled! Project marked as Requirement Fulfilled.";
                } else if (finalTarget > 0) {
                    msg = item.workerName + " assigned to " + item.projectName + " (" + finalAccepted + "/" + finalTarget + " workers assigned).";
                } else {
                    msg = item.workerName + " has been assigned to project " + item.projectName + ".";
                }

                NotificationToast.show(acceptBtn, "Worker Assigned!", msg, NotificationToast.ToastType.SUCCESS);

                // Remove from local lists and update UI
                masterList.remove(item);
                filteredList.remove(item);
                recordsListContainer.getChildren().remove(cardNode);

                updateKpiMetrics();
                recordsCountLabel.setText("Showing " + filteredList.size() + " of " + masterList.size() + " pending applications");

                if (recordsListContainer.getChildren().isEmpty()) {
                    emptyStateBox.setVisible(true);
                    emptyStateBox.setManaged(true);
                }
            });
        }).start();
    }

    private void handleDeclineWorker(ApplicationItem item, Node cardNode, Button acceptBtn, Button declineBtn) {
        acceptBtn.setDisable(true);
        declineBtn.setDisable(true);

        new Thread(() -> {
            item.app.setStatus("Rejected");
            jobAppCtrl.saveApplication(item.app);
            jobAppCtrl.updateWorkerApplicationsForProject(item.app.getWorkerMobile(), item.app.getProjectId(), "Rejected");

            Platform.runLater(() -> {
                NotificationToast.show(declineBtn, "Application Declined",
                        "The application for " + item.workerName + " has been declined.",
                        NotificationToast.ToastType.INFO);

                masterList.remove(item);
                filteredList.remove(item);
                recordsListContainer.getChildren().remove(cardNode);

                updateKpiMetrics();
                recordsCountLabel.setText("Showing " + filteredList.size() + " of " + masterList.size() + " pending applications");

                if (recordsListContainer.getChildren().isEmpty()) {
                    emptyStateBox.setVisible(true);
                    emptyStateBox.setManaged(true);
                }
            });
        }).start();
    }

    // ==========================================
    // 9. Interactive Worker Profile Modal
    // ==========================================
    private void openWorkerProfileModal(ApplicationItem item, Runnable onAccept, Runnable onDecline) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Worker Profile Dossier - " + item.workerName);

        VBox modalBox = new VBox(20);
        modalBox.setPadding(new Insets(26));
        modalBox.setPrefWidth(640);
        modalBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;");

        // Modal Header
        Node modalAvatar = createAvatar(item.workerName, item.workerPhotoUrl);
        Label name = new Label(item.workerName);
        name.setStyle("-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");

        Label trade = new Label(item.workerTrade.toUpperCase() + "  •  " + item.workerExperience + " Experience");
        trade.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:" + GOLD + ";");

        Label verified = new Label("🛡️ DIHADI VERIFIED CANDIDATE");
        verified.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:6px;-fx-padding:3px 8px;");

        VBox nameCol = new VBox(4, name, trade, verified);
        nameCol.setAlignment(Pos.CENTER_LEFT);

        HBox modalHead = new HBox(16, modalAvatar, nameCol);
        modalHead.setAlignment(Pos.CENTER_LEFT);
        modalHead.setPadding(new Insets(0, 0, 14, 0));
        modalHead.setStyle("-fx-border-color:transparent transparent #e0d8c7 transparent;-fx-border-width:0 0 1px 0;");

        // Profile Table
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(8, 0, 8, 0));

        addProfileField(grid, 0, 0, "Mobile Number", item.workerMobile);
        addProfileField(grid, 1, 0, "Location", item.workerCity + ", " + item.workerState);
        addProfileField(grid, 0, 1, "Specialized Skills", item.workerSubSkill.isEmpty() ? "Standard Field Work" : item.workerSubSkill);
        addProfileField(grid, 1, 1, "Education Level", item.workerEducation);
        addProfileField(grid, 0, 2, "Applying For Project", item.projectName);
        addProfileField(grid, 1, 2, "Job Site Location", item.projectLocation);
        addProfileField(grid, 0, 3, "Quoted Daily Wage", String.format("₹ %,.2f / day", item.dailyWage));
        addProfileField(grid, 1, 3, "Shift Timing", "Full-Day Shift (8 Hours)");

        // Actions Row
        Button acceptModalBtn = new Button("✓ Accept & Assign Worker");
        acceptModalBtn.setStyle("-fx-background-color:#2a7e3b;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:10px 18px;-fx-background-radius:8px;-fx-cursor:hand;");
        acceptModalBtn.setOnAction(e -> {
            modalStage.close();
            if (onAccept != null) onAccept.run();
        });

        Button declineModalBtn = new Button("✕ Decline");
        declineModalBtn.setStyle("-fx-background-color:#fff1f1;-fx-border-color:#f1bcbc;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#a51d1d;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:10px 16px;-fx-cursor:hand;");
        declineModalBtn.setOnAction(e -> {
            modalStage.close();
            if (onDecline != null) onDecline.run();
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#f5f0e8;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 16px;-fx-cursor:hand;");
        closeBtn.setOnAction(e -> modalStage.close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox modalActions = new HBox(12, closeBtn, spacer, declineModalBtn, acceptModalBtn);
        modalActions.setAlignment(Pos.CENTER_RIGHT);

        modalBox.getChildren().addAll(modalHead, grid, modalActions);

        Scene modalScene = new Scene(modalBox);
        modalStage.setScene(modalScene);
        modalStage.setResizable(false);
        modalStage.show();
    }

    private void addProfileField(GridPane grid, int col, int row, String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7a7267;");
        l.setTextOverrun(OverrunStyle.CLIP);

        Label v = new Label(value != null && !value.isBlank() ? value : "Not specified");
        v.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        v.setWrapText(true);
        v.setTextOverrun(OverrunStyle.CLIP);

        VBox box = new VBox(2, l, v);
        box.setPrefWidth(280);
        grid.add(box, col, row);
    }

    // ==========================================
    // 10. Empty State Layout
    // ==========================================
    private VBox createEmptyState() {
        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size:42px;");

        Label heading = new Label("No Pending Applications Found");
        heading.setStyle("-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:" + INK + ";");

        Label desc = new Label("All incoming worker applications have been evaluated, or no applications match your current search and filters.\nWhen new workers apply to your active project requirements, they will appear here in real-time.");
        desc.setStyle("-fx-font-size:13px;-fx-font-weight:500;-fx-text-fill:" + MUTED + ";-fx-text-alignment:center;");
        desc.setWrapText(true);
        desc.setMaxWidth(560);

        Button clearFilterBtn = new Button("↻ Reset All Filters");
        clearFilterBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:9px 18px;-fx-background-radius:8px;-fx-cursor:hand;");
        clearFilterBtn.setOnAction(e -> {
            searchField.clear();
            projectFilter.setValue("All Projects");
            tradeFilter.setValue("All Trades");
            sortFilter.setValue("Newest First");
            applyFilters();
        });

        VBox box = new VBox(12, icon, heading, desc, clearFilterBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(48, 24, 48, 24));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:" + BORDER + ";-fx-border-radius:16px;-fx-border-width:1.5px;");
        return box;
    }

    // ==========================================
    // 11. Helper Utilities
    // ==========================================
    private Node createAvatar(String name, String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            try {
                ImageView iv = new ImageView(new Image(photoUrl, 52, 52, true, true));
                Circle clip = new Circle(26, 26, 26);
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
        initialsLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        initialsLabel.setTextOverrun(OverrunStyle.CLIP);

        StackPane circle = new StackPane(initialsLabel);
        circle.setPrefSize(52, 52);
        circle.setMinSize(52, 52);
        circle.setMaxSize(52, 52);
        circle.setStyle("-fx-background-color:#fff2d1;-fx-background-radius:50%;-fx-border-color:#d4af37;-fx-border-radius:50%;-fx-border-width:1.8px;");
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

    private boolean isMatch(Project p, Recruiter r) {
        if (p == null || r == null) return false;
        String pMob = p.getMobile() != null ? cleanMobile(p.getMobile()) : "";
        String rMob = r.getMobileNumber() != null ? cleanMobile(r.getMobileNumber()) : "";
        if (!rMob.isEmpty() && !pMob.isEmpty()) {
            if (pMob.equals(rMob) || pMob.endsWith(rMob) || rMob.endsWith(pMob)) return true;
        }
        String pAlt = p.getAlternateMobile() != null ? cleanMobile(p.getAlternateMobile()) : "";
        if (!rMob.isEmpty() && !pAlt.isEmpty()) {
            if (pAlt.equals(rMob) || pAlt.endsWith(rMob) || rMob.endsWith(pAlt)) return true;
        }
        String rEmail = r.getEmail() != null ? r.getEmail().trim().toLowerCase() : "";
        String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
        if (!rEmail.isEmpty() && !pEmail.isEmpty() && rEmail.equals(pEmail)) return true;
        String rComp = r.getCompanyName() != null ? r.getCompanyName().trim().toLowerCase() : "";
        String pComp = p.getContactName() != null ? p.getContactName().trim().toLowerCase() : "";
        if (!rComp.isEmpty() && !pComp.isEmpty() && (pComp.contains(rComp) || rComp.contains(pComp))) return true;
        return false;
    }

    private String cleanMobile(String m) {
        if (m == null) return "";
        return m.replaceAll("[^0-9]", "");
    }
}
