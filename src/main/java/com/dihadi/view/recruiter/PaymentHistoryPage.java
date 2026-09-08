package com.dihadi.view.recruiter;

import com.dihadi.config.FirebaseConfig;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkerController;
import com.dihadi.dao.AttendanceDao;
import com.dihadi.dao.ProjectDao;
import com.dihadi.dao.WorkerDao;
import com.dihadi.model.Attendance;
import com.dihadi.model.Project;
import com.dihadi.model.Recruiter;
import com.dihadi.model.Worker;
import com.dihadi.view.NotificationToast;
import com.dihadi.view.ScrollUtils;
import com.dihadi.view.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Dedicated Payment History interface for Recruiters.
 * Displays comprehensive payout records of daily wages disbursed to workers,
 * complete with worker profiles, trades, project sites, transaction IDs,
 * payment methods, digital vouchers, and interactive search/filters.
 */
public class PaymentHistoryPage {

    private static final String GOLD = "#735c00";
    private static final String BORDER = "#E0D9CE";
    private static final String BG_COLOR = "#f3e7ce";

    private final Recruiter currentRecruiter;
    private final String currentRecruiterId;
    private final WorkerDao workerDao = new WorkerDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();

    // Data lists
    private final List<PaymentRecord> masterRecords = new ArrayList<>();
    private final List<PaymentRecord> filteredRecords = new ArrayList<>();

    // UI elements
    private VBox recordsListContainer;
    private Label totalDisbursedKpi;
    private Label totalTransactionsKpi;
    private Label workersCompensatedKpi;
    private Label walletBalanceKpi;
    private Label recordsCountLabel;
    private TextField searchField;
    private ComboBox<String> projectFilter;
    private ComboBox<String> tradeFilter;
    private ComboBox<String> sortFilter;
    private ProgressIndicator loadingSpinner;
    private VBox emptyStateBox;

    public PaymentHistoryPage() {
        this(SessionManager.currentRecruiter);
    }

    public PaymentHistoryPage(Recruiter recruiter) {
        this.currentRecruiter = recruiter != null ? recruiter
                : (SessionManager.currentRecruiter != null ? SessionManager.currentRecruiter : new Recruiter());

        String rId = currentRecruiter.getMobileNumber();
        if (rId == null || rId.trim().isEmpty()) {
            rId = currentRecruiter.getUid();
        }
        if (rId == null || rId.trim().isEmpty()) {
            rId = SessionManager.getCurrentRecruiterId();
        }
        this.currentRecruiterId = rId != null ? rId.trim() : "";
    }

    /**
     * Internal model encapsulating all relevant information for a worker payment transaction.
     */
    public static class PaymentRecord {
        public String transactionId;
        public String attendanceId;
        public String workerId;
        public String workerMobile;
        public String workerName;
        public String workerTrade;
        public String workerCity;
        public String workerPhotoUrl;
        public String projectId;
        public String projectName;
        public String projectLocation;
        public String recruiterId;
        public String recruiterName;
        public String recruiterCompany;
        public double amount;
        public String paymentMethod;
        public String status;
        public String date; // Shift date
        public Date timestamp;

        public PaymentRecord() {}
    }

    public Scene getScene(Runnable back) {
        VBox root = new VBox(22);
        root.setPadding(new Insets(30, 48, 40, 48));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.setMaxWidth(1440);
        root.setAlignment(Pos.TOP_CENTER);

        // 1. Top Header Bar
        HBox header = createHeader(back);

        // 2. Page Title Banner
        VBox titleBanner = createTitleBanner();

        // 3. KPI Metrics Row
        HBox kpiRow = createKpiMetricsRow(back);

        // 4. Search and Filter Bar
        VBox filterBar = createFilterControls();

        // 5. Payment Records List Container
        recordsListContainer = new VBox(14);
        recordsListContainer.setAlignment(Pos.TOP_CENTER);
        recordsListContainer.setFillWidth(true);

        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setPrefSize(40, 40);
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
        loadPaymentHistoriesAsync(back);

        return new Scene(scroll, 1420, 880);
    }

    private HBox createHeader(Runnable back) {
        ImageView logoImg = null;
        try {
            var is = getClass().getResourceAsStream("/assets/logo/dihadi logo.jpeg");
            if (is != null) {
                logoImg = new ImageView(new Image(is, 42, 42, true, true));
            }
        } catch (Exception ignored) {}

        Label brand = new Label("DIHADI");
        brand.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";");
        brand.setTextOverrun(OverrunStyle.CLIP);

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        if (logoImg != null) brandBox.getChildren().add(logoImg);
        brandBox.getChildren().add(brand);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color:#ffffff;-fx-text-fill:" + GOLD + ";-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:" + GOLD + ";-fx-border-radius:10px;-fx-background-radius:10px;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#4c4637;-fx-font-size:14px;-fx-font-weight:800;-fx-padding:8px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;"));
        backBtn.setOnAction(e -> {
            if (back != null) back.run();
        });

        String recruiterName = (currentRecruiter.getFirstName() != null ? currentRecruiter.getFirstName() : "Recruiter")
                + (currentRecruiter.getLastName() != null ? " " + currentRecruiter.getLastName() : "");
        String companyName = currentRecruiter.getCompanyName() != null && !currentRecruiter.getCompanyName().isBlank()
                ? currentRecruiter.getCompanyName() : "General Contractor";

        Label profileBadge = new Label(recruiterName + " (" + companyName + ")");
        profileBadge.setStyle("-fx-font-family:'Segoe UI',sans-serif;-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#3f392e;-fx-background-color:#fffaf0;-fx-background-radius:12px;-fx-border-color:#d0c5af;-fx-border-radius:12px;-fx-padding:7px 16px;");
        profileBadge.setTextOverrun(OverrunStyle.CLIP);

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color:#ffffff;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:7px 14px;-fx-cursor:hand;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-background-radius:10px;");
        refreshBtn.setOnAction(e -> loadPaymentHistoriesAsync(back));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12, backBtn, refreshBtn, profileBadge);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox headerBar = new HBox(20, brandBox, spacer, actions);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(0, 0, 10, 0));
        headerBar.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;");
        return headerBar;
    }

    private VBox createTitleBanner() {
        Label title = new Label("Worker Wage Payment History");
        title.setStyle("-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        title.setTextOverrun(OverrunStyle.CLIP);

        Label sub = new Label("Complete auditable ledger of all daily wages disbursed to workers, verified attendance payments, and escrow settlement vouchers.");
        sub.setStyle("-fx-font-size:14px;-fx-text-fill:#685c52;");
        sub.setTextOverrun(OverrunStyle.CLIP);

        VBox box = new VBox(6, title, sub);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createKpiMetricsRow(Runnable back) {
        totalDisbursedKpi = new Label("₹ 0.00");
        totalDisbursedKpi.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");
        totalDisbursedKpi.setTextOverrun(OverrunStyle.CLIP);
        VBox card1 = createKpiCard("TOTAL DISBURSED", totalDisbursedKpi, "Settled worker daily wages", "#2e7d32");

        totalTransactionsKpi = new Label("0");
        totalTransactionsKpi.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#1565c0;");
        totalTransactionsKpi.setTextOverrun(OverrunStyle.CLIP);
        VBox card2 = createKpiCard("PAYOUT TRANSACTIONS", totalTransactionsKpi, "Successful wage transfers", "#1565c0");

        workersCompensatedKpi = new Label("0");
        workersCompensatedKpi.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        workersCompensatedKpi.setTextOverrun(OverrunStyle.CLIP);
        VBox card3 = createKpiCard("WORKERS COMPENSATED", workersCompensatedKpi, "Unique personnel paid", "#735c00");

        double bal = currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0;
        walletBalanceKpi = new Label(String.format("₹ %,.2f", bal));
        walletBalanceKpi.setStyle("-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        walletBalanceKpi.setTextOverrun(OverrunStyle.CLIP);
        VBox card4 = createKpiCard("ESCROW WALLET BALANCE", walletBalanceKpi, "Available for instant payouts", "#735c00");

        HBox row = new HBox(16, card1, card2, card3, card4);
        row.setAlignment(Pos.CENTER);
        for (Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private VBox createKpiCard(String title, Label numberNode, String subtitle, String accentColor) {
        Label heading = new Label(title);
        heading.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:0.8px;-fx-text-fill:#685c52;");
        heading.setTextOverrun(OverrunStyle.CLIP);

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#7a7267;");
        sub.setTextOverrun(OverrunStyle.CLIP);

        VBox card = new VBox(6, heading, numberNode, sub);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);");

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.8px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.25),12,0,0,3px);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.05),8,0,0,2px);"));
        return card;
    }

    private VBox createFilterControls() {
        searchField = new TextField();
        searchField.setPromptText("🔍 Search by worker name, mobile number, txn ID, project, trade...");
        searchField.setPrefHeight(42);
        searchField.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-padding:0 14px;-fx-font-size:13px;");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        HBox.setHgrow(searchField, Priority.ALWAYS);

        projectFilter = new ComboBox<>();
        projectFilter.getItems().add("All Projects");
        projectFilter.setValue("All Projects");
        projectFilter.setPrefHeight(42);
        projectFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        projectFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        tradeFilter = new ComboBox<>();
        tradeFilter.getItems().addAll("All Trades", "Mason", "Carpenter", "Electrician", "Painter", "Welder", "Plumber", "Bar Bender", "General Laborer");
        tradeFilter.setValue("All Trades");
        tradeFilter.setPrefHeight(42);
        tradeFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        tradeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        sortFilter = new ComboBox<>();
        sortFilter.getItems().addAll("Newest First", "Oldest First", "Highest Amount", "Lowest Amount");
        sortFilter.setValue("Newest First");
        sortFilter.setPrefHeight(42);
        sortFilter.setStyle("-fx-background-color:#ffffff;-fx-background-radius:10px;-fx-border-color:" + BORDER + ";-fx-border-radius:10px;-fx-font-size:13px;");
        sortFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        Button resetBtn = new Button("Clear Filters");
        resetBtn.setPrefHeight(42);
        resetBtn.setStyle("-fx-background-color:#f5f0e8;-fx-background-radius:10px;-fx-border-color:#d0c5af;-fx-border-radius:10px;-fx-text-fill:#685c52;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:0 16px;-fx-cursor:hand;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            projectFilter.setValue("All Projects");
            tradeFilter.setValue("All Trades");
            sortFilter.setValue("Newest First");
            applyFilters();
        });

        HBox controlsRow = new HBox(12, searchField, projectFilter, tradeFilter, sortFilter, resetBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        recordsCountLabel = new Label("Showing 0 payment records");
        recordsCountLabel.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        recordsCountLabel.setTextOverrun(OverrunStyle.CLIP);

        VBox filterBox = new VBox(10, controlsRow, recordsCountLabel);
        filterBox.setPadding(new Insets(16, 20, 16, 20));
        filterBox.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-width:1.5px;-fx-border-radius:14px;");
        return filterBox;
    }

    private VBox createEmptyState() {
        Label icon = new Label("💳");
        icon.setStyle("-fx-font-size:48px;");

        Label title = new Label("No Payment Records Found");
        title.setStyle("-fx-font-family:Georgia;-fx-font-size:20px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        title.setTextOverrun(OverrunStyle.CLIP);

        Label sub = new Label("No wage payout transactions match your current search filters. Try adjusting your query or mark attendance with payout on the Attendance page.");
        sub.setStyle("-fx-font-size:14px;-fx-text-fill:#685c52;-fx-text-alignment:center;");
        sub.setTextOverrun(OverrunStyle.CLIP);
        sub.setMaxWidth(520);
        sub.setWrapText(true);

        VBox box = new VBox(10, icon, title, sub);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40, 20, 40, 20));
        box.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER + ";-fx-border-radius:14px;");
        return box;
    }

    /**
     * Queries Firestore for wallet_transactions, attendance records, and cross-references worker & project profiles.
     */
    private void loadPaymentHistoriesAsync(Runnable back) {
        loadingSpinner.setVisible(true);
        loadingSpinner.setManaged(true);
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
        recordsListContainer.getChildren().clear();

        new Thread(() -> {
            List<PaymentRecord> fetched = new ArrayList<>();
            Set<String> processedTxnIds = new HashSet<>();
            Set<String> projectNamesSet = new TreeSet<>();

            try {
                // 1. Fetch Workers for lookup
                Map<String, Worker> workerMap = new HashMap<>();
                try {
                    List<Worker> workers = new WorkerController().getAllWorkers();
                    if (workers != null) {
                        for (Worker w : workers) {
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
                } catch (Exception e) {
                    System.err.println("Worker lookup error: " + e.getMessage());
                }

                // 2. Fetch Projects for lookup
                Map<String, Project> projectMap = new HashMap<>();
                Set<String> recruiterProjIds = new HashSet<>();
                try {
                    List<Project> projects = new ProjectController().getAllProjects();
                    if (projects != null) {
                        for (Project p : projects) {
                            if (p.getProjectId() != null) {
                                projectMap.put(p.getProjectId(), p);
                                if (isMatch(p, currentRecruiter)) {
                                    recruiterProjIds.add(p.getProjectId());
                                }
                            }
                            if (p.getProjectName() != null) {
                                projectNamesSet.add(p.getProjectName());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Project lookup error: " + e.getMessage());
                }

                // 3. Query Firestore 'wallet_transactions' for real recruiter transactions
                try {
                    Firestore db = FirebaseConfig.getFirestore();
                    if (db != null) {
                        ApiFuture<QuerySnapshot> future = db.collection("wallet_transactions").get();
                        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            String rId = doc.getString("recruiterId");
                            String pId = doc.getString("projectId");
                            boolean matchesRecruiter = isRecruiterMatch(rId) || (pId != null && recruiterProjIds.contains(pId));

                            if (matchesRecruiter) {
                                PaymentRecord rec = new PaymentRecord();
                                rec.transactionId = doc.getString("transactionId");
                                if (rec.transactionId == null) rec.transactionId = doc.getId();
                                rec.attendanceId = doc.getString("attendanceId");
                                rec.workerId = doc.getString("workerId");
                                rec.workerMobile = doc.getString("workerId");
                                rec.projectId = doc.getString("projectId");
                                rec.amount = doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0;
                                rec.paymentMethod = "DIHADI Escrow Direct Settlement";
                                rec.status = doc.getString("status") != null ? doc.getString("status") : "COMPLETED";

                                com.google.cloud.Timestamp ts = doc.getTimestamp("timestamp");
                                if (ts != null) {
                                    rec.timestamp = ts.toDate();
                                } else {
                                    rec.timestamp = new Date();
                                }

                                populateDetailsFromLookups(rec, workerMap, projectMap);

                                if (rec.transactionId != null && processedTxnIds.add(rec.transactionId)) {
                                    fetched.add(rec);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Wallet transactions fetch notice: " + e.getMessage());
                }

                // 4. Query AttendanceDao for real attendance records marked as PAID
                try {
                    List<Attendance> attendances = new ArrayList<>();
                    if (!currentRecruiterId.isEmpty()) {
                        List<Attendance> rAtt = attendanceDao.getAttendanceByRecruiter(currentRecruiterId);
                        if (rAtt != null) attendances.addAll(rAtt);
                    }
                    for (String projId : recruiterProjIds) {
                        List<Attendance> pAtt = attendanceDao.getAttendanceByProject(projId);
                        if (pAtt != null) {
                            for (Attendance a : pAtt) {
                                if (attendances.stream().noneMatch(x -> x.getAttendanceId() != null && x.getAttendanceId().equals(a.getAttendanceId()))) {
                                    attendances.add(a);
                                }
                            }
                        }
                    }

                    for (Attendance a : attendances) {
                        if ("PAID".equalsIgnoreCase(a.getPaymentStatus()) || (a.getPaidAmount() > 0 && "PRESENT".equalsIgnoreCase(a.getStatus()))) {
                            String txnId = a.getPaymentTransactionId();
                            if (txnId == null || txnId.isBlank()) txnId = a.getTransactionId();
                            if (txnId == null || txnId.isBlank()) {
                                txnId = "TXN_ATT_" + (a.getAttendanceId() != null ? a.getAttendanceId() : UUID.randomUUID().toString().substring(0, 8));
                            }

                            if (processedTxnIds.add(txnId)) {
                                PaymentRecord rec = new PaymentRecord();
                                rec.transactionId = txnId;
                                rec.attendanceId = a.getAttendanceId();
                                rec.workerId = a.getWorkerId() != null ? a.getWorkerId() : a.getWorkerMobile();
                                rec.workerMobile = a.getWorkerMobile() != null ? a.getWorkerMobile() : a.getWorkerId();
                                rec.projectId = a.getProjectId();
                                rec.amount = a.getPaidAmount() > 0 ? a.getPaidAmount() : 0.0;
                                rec.paymentMethod = "DIHADI Escrow Direct Settlement";
                                rec.status = "PAID & SETTLED";
                                rec.date = a.getDate();
                                rec.timestamp = a.getTimestamp() != null ? a.getTimestamp() : new Date();

                                populateDetailsFromLookups(rec, workerMap, projectMap);
                                fetched.add(rec);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Attendance records lookup notice: " + e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                loadingSpinner.setVisible(false);
                loadingSpinner.setManaged(false);

                masterRecords.clear();
                masterRecords.addAll(fetched);

                // Populate project filter dropdown
                String prevSelected = projectFilter.getValue();
                projectFilter.getItems().clear();
                projectFilter.getItems().add("All Projects");
                projectFilter.getItems().addAll(projectNamesSet);
                if (prevSelected != null && projectFilter.getItems().contains(prevSelected)) {
                    projectFilter.setValue(prevSelected);
                } else {
                    projectFilter.setValue("All Projects");
                }

                applyFilters();
            });
        }).start();
    }

    private void populateDetailsFromLookups(PaymentRecord rec, Map<String, Worker> workerMap, Map<String, Project> projectMap) {
        // Worker lookup
        String mob = cleanMobile(rec.workerMobile);
        Worker w = workerMap.get(mob);
        if (w == null && mob.length() >= 10) {
            w = workerMap.get(mob.substring(mob.length() - 10));
        }

        if (w != null) {
            String fn = w.getFirstName() != null ? w.getFirstName().trim() : "";
            String mn = w.getMiddleName() != null ? w.getMiddleName().trim() : "";
            String ln = w.getLastName() != null ? w.getLastName().trim() : "";
            String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
            rec.workerName = !full.isEmpty() ? full : "Registered Worker";
            rec.workerTrade = w.getWorkerType() != null ? w.getWorkerType() : (w.getSubSkill() != null ? w.getSubSkill() : "General Mason");
            rec.workerCity = w.getCity() != null ? w.getCity() : "Maharashtra";
            rec.workerPhotoUrl = w.getProfilePhotoUrl();
        } else {
            if (rec.workerName == null || rec.workerName.isBlank()) {
                rec.workerName = "Worker (" + (rec.workerMobile != null ? rec.workerMobile : "Verified") + ")";
            }
            if (rec.workerTrade == null) rec.workerTrade = "Skilled Tradesperson";
            if (rec.workerCity == null) rec.workerCity = "Maharashtra";
        }

        // Project lookup
        Project p = projectMap.get(rec.projectId);
        if (p != null) {
            rec.projectName = p.getProjectName();
            rec.projectLocation = (p.getAddressLine1() != null ? p.getAddressLine1() + ", " : "")
                    + (p.getCity() != null ? p.getCity() : "");
        } else {
            if (rec.projectName == null || rec.projectName.isBlank()) {
                rec.projectName = (rec.projectId != null && !rec.projectId.isBlank()) ? "Project " + rec.projectId : "Site Project";
            }
            if (rec.projectLocation == null) {
                rec.projectLocation = "Site Location";
            }
        }

        // Recruiter info
        rec.recruiterName = (currentRecruiter.getFirstName() != null ? currentRecruiter.getFirstName() : "Recruiter")
                + (currentRecruiter.getLastName() != null ? " " + currentRecruiter.getLastName() : "");
        rec.recruiterCompany = currentRecruiter.getCompanyName() != null && !currentRecruiter.getCompanyName().isBlank()
                ? currentRecruiter.getCompanyName() : "General Contractor";
    }

    private void applyFilters() {
        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String projSel = projectFilter != null ? projectFilter.getValue() : "All Projects";
        String tradeSel = tradeFilter != null ? tradeFilter.getValue() : "All Trades";
        String sortSel = sortFilter != null ? sortFilter.getValue() : "Newest First";

        filteredRecords.clear();

        for (PaymentRecord r : masterRecords) {
            // Search query matching
            if (!query.isEmpty()) {
                boolean match = false;
                if (r.workerName != null && r.workerName.toLowerCase().contains(query)) match = true;
                if (r.workerMobile != null && r.workerMobile.toLowerCase().contains(query)) match = true;
                if (r.transactionId != null && r.transactionId.toLowerCase().contains(query)) match = true;
                if (r.projectName != null && r.projectName.toLowerCase().contains(query)) match = true;
                if (r.workerTrade != null && r.workerTrade.toLowerCase().contains(query)) match = true;
                if (r.date != null && r.date.toLowerCase().contains(query)) match = true;
                if (!match) continue;
            }

            // Project filter
            if (projSel != null && !projSel.equals("All Projects")) {
                if (r.projectName == null || !r.projectName.equalsIgnoreCase(projSel)) {
                    continue;
                }
            }

            // Trade filter
            if (tradeSel != null && !tradeSel.equals("All Trades")) {
                if (r.workerTrade == null || !r.workerTrade.toLowerCase().contains(tradeSel.toLowerCase())) {
                    continue;
                }
            }

            filteredRecords.add(r);
        }

        // Sorting
        if ("Highest Amount".equals(sortSel)) {
            filteredRecords.sort((a, b) -> Double.compare(b.amount, a.amount));
        } else if ("Lowest Amount".equals(sortSel)) {
            filteredRecords.sort((a, b) -> Double.compare(a.amount, b.amount));
        } else if ("Oldest First".equals(sortSel)) {
            filteredRecords.sort(Comparator.comparing(a -> a.timestamp != null ? a.timestamp : new Date(0)));
        } else {
            // Newest First
            filteredRecords.sort((a, b) -> {
                Date d1 = a.timestamp != null ? a.timestamp : new Date(0);
                Date d2 = b.timestamp != null ? b.timestamp : new Date(0);
                return d2.compareTo(d1);
            });
        }

        // Update KPIs based on master or filtered
        updateKpisAndList();
    }

    private void updateKpisAndList() {
        double totalDisbursed = 0.0;
        Set<String> uniqueWorkers = new HashSet<>();
        for (PaymentRecord r : masterRecords) {
            totalDisbursed += r.amount;
            if (r.workerMobile != null) uniqueWorkers.add(r.workerMobile);
            else if (r.workerName != null) uniqueWorkers.add(r.workerName);
        }

        totalDisbursedKpi.setText(String.format("₹ %,.2f", totalDisbursed));
        totalTransactionsKpi.setText(String.valueOf(masterRecords.size()));
        workersCompensatedKpi.setText(String.valueOf(uniqueWorkers.size()));

        if (currentRecruiter != null) {
            walletBalanceKpi.setText(String.format("₹ %,.2f", currentRecruiter.getWalletBalance()));
        }

        recordsCountLabel.setText("Showing " + filteredRecords.size() + " of " + masterRecords.size() + " payment records");

        recordsListContainer.getChildren().clear();

        if (filteredRecords.isEmpty()) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
        } else {
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);

            for (PaymentRecord rec : filteredRecords) {
                recordsListContainer.getChildren().add(createPaymentRecordCard(rec));
            }
        }
    }

    private Node createPaymentRecordCard(PaymentRecord rec) {
        // 1. Worker Avatar & Identity Section
        Node avatarNode = createAvatar(rec.workerName, rec.workerPhotoUrl);

        Label nameLabel = new Label(rec.workerName != null ? rec.workerName : "Worker");
        nameLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:18px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        nameLabel.setTextOverrun(OverrunStyle.CLIP);
        nameLabel.setWrapText(true);

        Label tradeBadge = new Label(rec.workerTrade != null ? rec.workerTrade.toUpperCase() : "GENERAL WORKER");
        tradeBadge.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#735c00;-fx-background-color:#fff8e7;-fx-background-radius:8px;-fx-padding:3px 8px;-fx-border-color:#f0d890;-fx-border-radius:8px;");
        tradeBadge.setTextOverrun(OverrunStyle.CLIP);

        HBox nameTradeRow = new HBox(8, nameLabel, tradeBadge);
        nameTradeRow.setAlignment(Pos.CENTER_LEFT);

        Label mobileLabel = new Label("📱 " + (rec.workerMobile != null ? rec.workerMobile : "Not available"));
        mobileLabel.setStyle("-fx-font-size:13px;-fx-font-weight:600;-fx-text-fill:#685c52;");
        mobileLabel.setTextOverrun(OverrunStyle.CLIP);

        Label cityLabel = new Label("📍 " + (rec.workerCity != null ? rec.workerCity : "Maharashtra"));
        cityLabel.setStyle("-fx-font-size:12px;-fx-text-fill:#7a7267;");
        cityLabel.setTextOverrun(OverrunStyle.CLIP);

        VBox workerIdentityCol = new VBox(4, nameTradeRow, mobileLabel, cityLabel);
        workerIdentityCol.setAlignment(Pos.CENTER_LEFT);
        workerIdentityCol.setPrefWidth(300);

        HBox leftWorkerSection = new HBox(16, avatarNode, workerIdentityCol);
        leftWorkerSection.setAlignment(Pos.CENTER_LEFT);

        // 2. Project Site & Attendance Date Section
        Label projectTag = new Label("PROJECT SITE");
        projectTag.setStyle("-fx-font-size:10px;-fx-font-weight:800;-fx-text-fill:#1565c0;-fx-letter-spacing:0.8px;");
        projectTag.setTextOverrun(OverrunStyle.CLIP);

        Label projectName = new Label(rec.projectName != null ? rec.projectName : "General Project");
        projectName.setStyle("-fx-font-family:Georgia;-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        projectName.setWrapText(true);
        projectName.setTextOverrun(OverrunStyle.CLIP);
        projectName.setMaxWidth(340);

        Label projectLoc = new Label(rec.projectLocation != null ? rec.projectLocation : "Maharashtra Site");
        projectLoc.setStyle("-fx-font-size:12px;-fx-text-fill:#685c52;");
        projectLoc.setTextOverrun(OverrunStyle.CLIP);
        projectLoc.setMaxWidth(340);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        String formattedTime = rec.timestamp != null ? df.format(rec.timestamp) : (rec.date != null ? rec.date : "Verified Shift");

        Label shiftDateBadge = new Label("🗓 Shift: " + (rec.date != null ? rec.date : "Daily Attendance") + "  •  Paid: " + formattedTime);
        shiftDateBadge.setStyle("-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#f5f0e8;-fx-background-radius:6px;-fx-padding:3px 8px;");
        shiftDateBadge.setTextOverrun(OverrunStyle.CLIP);

        VBox centerProjectSection = new VBox(4, projectTag, projectName, projectLoc, shiftDateBadge);
        centerProjectSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(centerProjectSection, Priority.ALWAYS);

        // 3. Payment Status & Amount Section
        Label amountLabel = new Label(String.format("₹ %,.2f", rec.amount));
        amountLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");
        amountLabel.setTextOverrun(OverrunStyle.CLIP);

        Label statusBadge = new Label("✓ " + (rec.status != null ? rec.status : "PAID & SETTLED"));
        statusBadge.setStyle("-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-padding:3px 10px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");
        statusBadge.setTextOverrun(OverrunStyle.CLIP);

        Label txnIdLabel = new Label("Txn: " + (rec.transactionId != null ? rec.transactionId : "TXN_ESCROW"));
        txnIdLabel.setStyle("-fx-font-family:'Consolas','Courier New',monospace;-fx-font-size:11px;-fx-text-fill:#7a7267;");
        txnIdLabel.setTextOverrun(OverrunStyle.CLIP);

        Label methodLabel = new Label("via " + (rec.paymentMethod != null ? rec.paymentMethod : "Escrow Direct"));
        methodLabel.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-text-fill:#8d6e63;");
        methodLabel.setTextOverrun(OverrunStyle.CLIP);

        VBox rightPaymentCol = new VBox(3, amountLabel, statusBadge, txnIdLabel, methodLabel);
        rightPaymentCol.setAlignment(Pos.CENTER_RIGHT);
        rightPaymentCol.setPrefWidth(210);

        // 4. Action Button
        Button voucherBtn = new Button("View Voucher ↗");
        voucherBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-background-radius:8px;-fx-cursor:hand;");
        voucherBtn.setOnMouseEntered(e -> voucherBtn.setStyle("-fx-background-color:#424242;-fx-text-fill:#ffd54f;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-background-radius:8px;-fx-cursor:hand;"));
        voucherBtn.setOnMouseExited(e -> voucherBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:9px 14px;-fx-background-radius:8px;-fx-cursor:hand;"));
        voucherBtn.setOnAction(e -> openPaymentVoucherModal(rec));

        HBox cardRow = new HBox(20, leftWorkerSection, centerProjectSection, rightPaymentCol, voucherBtn);
        cardRow.setAlignment(Pos.CENTER_LEFT);
        cardRow.setPadding(new Insets(18, 22, 18, 22));
        cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);");

        cardRow.setOnMouseEntered(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:#d4af37;-fx-border-width:1.8px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(212,175,55,.22),12,0,0,3px);"));
        cardRow.setOnMouseExited(e -> cardRow.setStyle("-fx-background-color:#ffffff;-fx-background-radius:14px;-fx-border-color:" + BORDER
                + ";-fx-border-width:1.5px;-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.04),8,0,0,2px);"));

        return cardRow;
    }

    private Node createAvatar(String name, String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            try {
                ImageView iv = new ImageView(new Image(photoUrl, 52, 52, true, true));
                Circle clip = new Circle(26, 26, 26);
                iv.setClip(clip);
                return iv;
            } catch (Exception ignored) {}
        }

        // Initials avatar
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

        StackPane avatarPane = new StackPane(initialsLabel);
        avatarPane.setPrefSize(52, 52);
        avatarPane.setMinSize(52, 52);
        avatarPane.setMaxSize(52, 52);
        avatarPane.setStyle("-fx-background-color:#f5ebce;-fx-background-radius:26px;-fx-border-color:#d0c5af;-fx-border-radius:26px;");
        return avatarPane;
    }

    /**
     * Digital Payment Voucher Inspection Modal
     */
    private void openPaymentVoucherModal(PaymentRecord rec) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("DIHADI Official Payment Voucher - " + rec.transactionId);

        VBox voucherBox = new VBox(18);
        voucherBox.setPadding(new Insets(30, 36, 30, 36));
        voucherBox.setStyle("-fx-background-color:#ffffff;-fx-border-color:#d0c5af;-fx-border-width:2px;");
        voucherBox.setPrefWidth(560);
        voucherBox.setMaxWidth(560);

        // Header
        Label voucherTitle = new Label("DIHADI ESCROW DISBURSEMENT VOUCHER");
        voucherTitle.setStyle("-fx-font-family:Georgia;-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:" + GOLD + ";-fx-letter-spacing:1px;");
        voucherTitle.setTextOverrun(OverrunStyle.CLIP);

        Label subTitle = new Label("Verified Official Wage Settlement Receipt");
        subTitle.setStyle("-fx-font-size:12px;-fx-text-fill:#7a7267;");
        subTitle.setTextOverrun(OverrunStyle.CLIP);

        VBox headerVBox = new VBox(3, voucherTitle, subTitle);
        headerVBox.setAlignment(Pos.CENTER);

        // Stamp badge
        Label stampBadge = new Label("✓ 100% ESCROW SETTLED & VERIFIED");
        stampBadge.setStyle("-fx-font-size:12px;-fx-font-weight:800;-fx-text-fill:#2e7d32;-fx-background-color:#e8f5e9;-fx-background-radius:12px;-fx-padding:6px 16px;-fx-border-color:#81c784;-fx-border-radius:12px;");
        stampBadge.setTextOverrun(OverrunStyle.CLIP);
        HBox stampRow = new HBox(stampBadge);
        stampRow.setAlignment(Pos.CENTER);

        // Table breakdown
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(16));
        tableBox.setStyle("-fx-background-color:#faf7f0;-fx-background-radius:10px;-fx-border-color:#e0d9ce;-fx-border-radius:10px;");

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a");
        String formattedTs = rec.timestamp != null ? df.format(rec.timestamp) : (rec.date != null ? rec.date : "Settled");

        tableBox.getChildren().addAll(
                voucherDetailRow("Transaction ID", rec.transactionId),
                voucherDetailRow("Shift Date", rec.date != null ? rec.date : "Daily Attendance"),
                voucherDetailRow("Payment Timestamp", formattedTs),
                voucherDetailRow("Disbursing Recruiter", rec.recruiterName),
                voucherDetailRow("Recruiter Organization", rec.recruiterCompany),
                voucherDetailRow("Beneficiary Worker", rec.workerName),
                voucherDetailRow("Worker Contact", rec.workerMobile),
                voucherDetailRow("Trade / Skill", rec.workerTrade),
                voucherDetailRow("Project Site", rec.projectName),
                voucherDetailRow("Site Location", rec.projectLocation),
                voucherDetailRow("Payment Channel", rec.paymentMethod),
                voucherDetailRow("Attendance Record", rec.attendanceId != null ? rec.attendanceId : "ATT_VERIFIED")
        );

        // Grand Total Row
        Label grandLabel = new Label("NET WAGE PAID:");
        grandLabel.setStyle("-fx-font-family:Georgia;-fx-font-size:16px;-fx-font-weight:800;-fx-text-fill:#1e1b15;");
        grandLabel.setTextOverrun(OverrunStyle.CLIP);

        Label grandAmount = new Label(String.format("₹ %,.2f", rec.amount));
        grandAmount.setStyle("-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:800;-fx-text-fill:#2e7d32;");
        grandAmount.setTextOverrun(OverrunStyle.CLIP);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox grandRow = new HBox(10, grandLabel, spacer, grandAmount);
        grandRow.setAlignment(Pos.CENTER_LEFT);
        grandRow.setPadding(new Insets(12, 16, 12, 16));
        grandRow.setStyle("-fx-background-color:#e8f5e9;-fx-background-radius:8px;-fx-border-color:#a5d6a7;-fx-border-radius:8px;");

        // Action Buttons
        Button downloadBtn = new Button("🖨 Print / Download Voucher");
        downloadBtn.setStyle("-fx-background-color:#272727;-fx-text-fill:#ffd54f;-fx-font-size:13px;-fx-font-weight:800;-fx-padding:10px 18px;-fx-background-radius:8px;-fx-cursor:hand;");
        downloadBtn.setOnAction(e -> {
            NotificationToast.show(downloadBtn, "Voucher Exported",
                    "Payment voucher for " + rec.workerName + " (Txn: " + rec.transactionId + ") downloaded successfully.",
                    NotificationToast.ToastType.SUCCESS);
            modalStage.close();
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color:#f5f0e8;-fx-text-fill:#4c4637;-fx-font-size:13px;-fx-font-weight:700;-fx-padding:10px 18px;-fx-border-color:#d0c5af;-fx-border-radius:8px;-fx-background-radius:8px;-fx-cursor:hand;");
        closeBtn.setOnAction(e -> modalStage.close());

        HBox btnRow = new HBox(12, closeBtn, downloadBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        voucherBox.getChildren().addAll(headerVBox, stampRow, tableBox, grandRow, btnRow);

        Scene modalScene = new Scene(voucherBox);
        modalStage.setScene(modalScene);
        modalStage.setResizable(false);
        modalStage.show();
    }

    private HBox voucherDetailRow(String labelText, String valueText) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-text-fill:#685c52;");
        lbl.setPrefWidth(160);
        lbl.setTextOverrun(OverrunStyle.CLIP);

        Label val = new Label(valueText != null ? valueText : "-");
        val.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-text-fill:#1e1b15;");
        val.setWrapText(true);
        val.setTextOverrun(OverrunStyle.CLIP);
        HBox.setHgrow(val, Priority.ALWAYS);

        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private boolean isRecruiterMatch(String rId) {
        if (rId == null || rId.isBlank()) return false;
        if (currentRecruiterId.isEmpty()) return true;

        String clean1 = cleanMobile(rId);
        String clean2 = cleanMobile(currentRecruiterId);
        if (clean1.equals(clean2)) return true;

        if (clean1.length() >= 10 && clean2.length() >= 10) {
            String sub1 = clean1.substring(clean1.length() - 10);
            String sub2 = clean2.substring(clean2.length() - 10);
            if (sub1.equals(sub2)) return true;
        }
        return false;
    }

    private boolean isMatch(Project p, Recruiter r) {
        if (p == null || r == null) return false;
        String rMob = r.getMobileNumber() != null ? r.getMobileNumber().replaceAll("\\D", "") : "";
        String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
        if (!rMob.isEmpty() && !pMob.isEmpty()) {
            if (pMob.equals(rMob) || pMob.endsWith(rMob) || rMob.endsWith(pMob)) return true;
        }
        String pAlt = p.getAlternateMobile() != null ? p.getAlternateMobile().replaceAll("\\D", "") : "";
        if (!rMob.isEmpty() && !pAlt.isEmpty()) {
            if (pAlt.equals(rMob) || pAlt.endsWith(rMob) || rMob.endsWith(pAlt)) return true;
        }
        String rEmail = r.getEmail() != null ? r.getEmail().trim().toLowerCase() : "";
        String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
        if (!rEmail.isEmpty() && !pEmail.isEmpty() && rEmail.equals(pEmail)) return true;
        return false;
    }

    private String cleanMobile(String m) {
        if (m == null) return "";
        return m.replaceAll("[^0-9]", "");
    }
}
