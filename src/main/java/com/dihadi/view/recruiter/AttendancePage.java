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
import com.dihadi.view.SessionManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AttendancePage {

    private static final String GOLD = "#735c00";
    private static final String BORDER = "#E0D9CE";

    private final AttendanceController attendanceController;
    private final AttendanceDao attendanceDao;
    private final ProjectDao projectDao;
    private final WorkerDao workerDao;

    private VBox tableRowsContainer;
    private ComboBox<Project> projectDropdown;
    private DatePicker datePicker;
    private ProgressIndicator loadingIndicator;
    private Label statusSummaryLabel;
    private Label walletBalanceLabel;

    private Recruiter currentRecruiter;
    private String currentRecruiterId;
    private Runnable backAction;

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

        this.tableRowsContainer = new VBox(12);
        this.projectDropdown = new ComboBox<>();
        this.datePicker = new DatePicker(LocalDate.now());
        this.loadingIndicator = new ProgressIndicator();
        this.statusSummaryLabel = new Label("Select a project to manage daily attendance and wage payouts.");
        this.walletBalanceLabel = new Label();

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #f3e7ce;");
        root.setPadding(new Insets(24, 48, 40, 48));
        root.setMaxWidth(1360);
        root.setAlignment(Pos.TOP_CENTER);

        // 1. Top Navigation Bar
        HBox navBar = new HBox(16);
        navBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Dashboard");
        backButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #4c4637; -fx-font-size: 13px; " +
                "-fx-font-weight: bold; -fx-padding: 8 16; -fx-border-color: #d0c5af; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
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

        Label titleLabel = new Label("Daily Attendance & Wage Payouts");
        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1e1b15"));

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        // Recruiter Wallet Info Badge
        updateWalletDisplay();
        walletBalanceLabel.setStyle("-fx-font-family: Georgia; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + GOLD + ";");

        Button walletRechargeTopBtn = new Button("+ Recharge Wallet");
        walletRechargeTopBtn.setStyle("-fx-background-color: #272727; -fx-text-fill: #ffd54f; -fx-font-size: 12px; " +
                "-fx-font-weight: bold; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;");
        walletRechargeTopBtn.setOnAction(e -> triggerRechargeFlow(walletRechargeTopBtn, 1000.00));

        HBox walletBadgeBox = new HBox(10, walletBalanceLabel, walletRechargeTopBtn);
        walletBadgeBox.setAlignment(Pos.CENTER_RIGHT);
        walletBadgeBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 6 14; -fx-background-radius: 10; -fx-border-color: #d0c5af; -fx-border-radius: 10;");

        navBar.getChildren().addAll(backButton, titleLabel, navSpacer, walletBadgeBox);

        // 2. Filter Bar (Project selector & DatePicker)
        HBox filterBar = new HBox(18);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle("-fx-background-color: #ffffff; -fx-padding: 16 22; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.05), 8, 0, 0, 2);");

        Label projectSelectLabel = new Label("Select Project:");
        projectSelectLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        projectSelectLabel.setTextFill(Color.web("#4c4637"));

        projectDropdown.setPromptText("Choose Project");
        projectDropdown.setPrefWidth(260);
        projectDropdown.setStyle("-fx-font-size: 13px; -fx-background-radius: 6;");
        projectDropdown.setOnAction(e -> refreshAttendanceList());

        Label dateLabel = new Label("Date:");
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        dateLabel.setTextFill(Color.web("#4c4637"));

        datePicker.setStyle("-fx-font-size: 13px; -fx-background-radius: 6;");
        datePicker.setOnAction(e -> refreshAttendanceList());

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color: #faf3e8; -fx-text-fill: #735c00; -fx-font-weight: bold; -fx-border-color: #d0c5af; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;");
        refreshBtn.setOnAction(e -> refreshAttendanceList());

        loadingIndicator.setPrefSize(20, 20);
        loadingIndicator.setVisible(false);

        filterBar.getChildren().addAll(projectSelectLabel, projectDropdown, dateLabel, datePicker, refreshBtn, loadingIndicator);

        statusSummaryLabel.setFont(Font.font("Segoe UI", 13));
        statusSummaryLabel.setTextFill(Color.web("#685c52"));

        // 3. Table Wrapper
        HBox tableHeader = createTableHeader();
        tableRowsContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane innerScroll = new ScrollPane(tableRowsContainer);
        innerScroll.setFitToWidth(true);
        innerScroll.setPrefHeight(450);
        innerScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        VBox tableWrapper = new VBox(tableHeader, innerScroll);
        tableWrapper.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.06), 10, 0, 0, 3);");
        VBox.setVgrow(innerScroll, Priority.ALWAYS);
        VBox.setVgrow(tableWrapper, Priority.ALWAYS);

        root.getChildren().addAll(navBar, filterBar, statusSummaryLabel, tableWrapper);

        loadProjects();

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f3e7ce; -fx-background-color: #f3e7ce; -fx-border-width: 0;");
        return new Scene(scrollPane, 1400, 850);
    }

    public Scene getScene() {
        return getScene(() -> {});
    }

    private void updateWalletDisplay() {
        double balance = currentRecruiter != null ? currentRecruiter.getWalletBalance() : 0.0;
        if (walletBalanceLabel != null) {
            walletBalanceLabel.setText(String.format("Wallet: ₹%,.2f", balance));
        }
    }

    private HBox createTableHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color: #faf5eb; -fx-border-color: #ebdccb; -fx-border-width: 0 0 1 0; -fx-background-radius: 12 12 0 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label colWorker = createHeaderLabel("WORKER DETAILS", 260);
        Label colRole = createHeaderLabel("ROLE / TRADE", 180);
        Label colWage = createHeaderLabel("DAILY WAGE", 140);
        Label colStatus = createHeaderLabel("STATUS", 160);
        Label colAction = createHeaderLabel("WAGE PAYOUT / ACTION", 220);

        header.getChildren().addAll(colWorker, colRole, colWage, colStatus, colAction);
        return header;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#735c00"));
        label.setPrefWidth(width);
        return label;
    }

    private void loadProjects() {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            try {
                List<Project> projects = projectDao.getProjectsByRecruiterId(currentRecruiterId);
                Platform.runLater(() -> {
                    projectDropdown.getItems().clear();
                    if (projects != null && !projects.isEmpty()) {
                        projectDropdown.getItems().addAll(projects);
                        projectDropdown.getSelectionModel().selectFirst();
                        refreshAttendanceList();
                    } else {
                        statusSummaryLabel.setText("No active projects found for current recruiter.");
                    }
                    loadingIndicator.setVisible(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusSummaryLabel.setText("Failed to load projects: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void refreshAttendanceList() {
        Project selectedProject = projectDropdown.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedProject == null || selectedDate == null) {
            return;
        }

        loadingIndicator.setVisible(true);
        tableRowsContainer.getChildren().clear();

        new Thread(() -> {
            try {
                String projectId = selectedProject.getId();
                List<Worker> assignedWorkers = workerDao.getWorkersByProjectId(projectId);

                Platform.runLater(() -> {
                    tableRowsContainer.getChildren().clear();
                    if (assignedWorkers == null || assignedWorkers.isEmpty()) {
                        Label emptyLabel = new Label("No workers currently assigned to project: " + selectedProject.getTitle());
                        emptyLabel.setFont(Font.font("Segoe UI", 14));
                        emptyLabel.setTextFill(Color.web("#685c52"));
                        emptyLabel.setPadding(new Insets(36));
                        tableRowsContainer.getChildren().add(emptyLabel);
                        statusSummaryLabel.setText("0 workers assigned to project: " + selectedProject.getTitle());
                    } else {
                        statusSummaryLabel.setText("Assigned Workers: " + assignedWorkers.size() + " | Date: " + selectedDate);
                        for (Worker worker : assignedWorkers) {
                            tableRowsContainer.getChildren().add(buildWorkerRow(worker, selectedProject, selectedDate));
                        }
                    }
                    loadingIndicator.setVisible(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusSummaryLabel.setText("Error loading attendance records: " + ex.getMessage());
                });
            }
        }).start();
    }

    private HBox buildWorkerRow(Worker worker, Project project, LocalDate date) {
        HBox row = new HBox();
        row.setPadding(new Insets(14, 24, 14, 24));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: #ffffff;");

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: #faf5eb;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-background-color: #ffffff;"));

        // 1. Worker Details
        VBox workerDetails = new VBox(3);
        workerDetails.setPrefWidth(260);
        Label nameLabel = new Label(worker.getName() != null ? worker.getName() : "Worker");
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#1e1b15"));

        Label phoneLabel = new Label("ID: " + worker.getId() + " | " + (worker.getPhone() != null && !worker.getPhone().isBlank() ? worker.getPhone() : "N/A"));
        phoneLabel.setFont(Font.font("Segoe UI", 12));
        phoneLabel.setTextFill(Color.web("#685c52"));
        workerDetails.getChildren().addAll(nameLabel, phoneLabel);

        // 2. Role
        Label roleLabel = new Label(worker.getSkill() != null ? worker.getSkill() : "Daily Labour");
        roleLabel.setFont(Font.font("Segoe UI", 13));
        roleLabel.setTextFill(Color.web("#4c4637"));
        roleLabel.setPrefWidth(180);

        // 3. Daily Wage
        double wage = worker.getDailyWage() > 0 ? worker.getDailyWage() : 600.00;
        Label wageLabel = new Label(String.format("₹%.2f", wage));
        wageLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        wageLabel.setTextFill(Color.web("#1b5e20"));
        wageLabel.setPrefWidth(140);

        // 4. Status Indicator
        HBox statusBadge = new HBox(6);
        statusBadge.setAlignment(Pos.CENTER_LEFT);
        statusBadge.setPrefWidth(160);

        Circle statusDot = new Circle(4);
        Label statusText = new Label("Checking...");
        statusText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusBadge.getChildren().addAll(statusDot, statusText);

        // 5. Action / Payout Button
        Button markPresentBtn = new Button("Mark Present & Pay");
        markPresentBtn.setStyle("-fx-background-color: #272727; -fx-text-fill: #ffd54f; -fx-font-weight: bold; " +
                "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        markPresentBtn.setPrefWidth(190);

        // Deterministic attendance record ID: ATT_{projectId}_{workerId}_{date}
        String attendanceRecordId = String.format("ATT_%s_%s_%s", project.getId(), worker.getId(), date.toString());

        // Check existing status in background
        new Thread(() -> {
            try {
                Attendance record = attendanceDao.getAttendanceRecord(attendanceRecordId);
                Platform.runLater(() -> {
                    if (record != null && "PRESENT".equalsIgnoreCase(record.getStatus())) {
                        setMarkedPresentState(markPresentBtn, statusDot, statusText, record.getTransactionId());
                    } else {
                        setPendingState(markPresentBtn, statusDot, statusText);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> setPendingState(markPresentBtn, statusDot, statusText));
            }
        }).start();

        // Trigger Direct Razorpay Payment Gateway & Verification
        markPresentBtn.setOnAction(evt -> {
            markPresentBtn.setDisable(true);
            markPresentBtn.setText("Initiating Gateway...");

            String receiptId = "RCPT_" + System.currentTimeMillis();
            String notes = "Attendance Wage: " + worker.getName() + " on " + date;

            // Step 1: Create Razorpay Order via Controller
            attendanceController.createPaymentOrder(
                wage,
                receiptId,
                notes,
                orderId -> {
                    markPresentBtn.setText("Awaiting Card Payment...");

                    Stage parentStage = markPresentBtn.getScene() != null ? (Stage) markPresentBtn.getScene().getWindow() : null;
                    String keyId = attendanceController.getRazorpayService().getKeyId();

                    // Step 2: Open Razorpay interactive checkout dialog (Debit/Credit Card/UPI)
                    com.dihadi.view.PaymentGateway.RazorpayCheckoutDialog.showPaymentWindow(
                        parentStage,
                        keyId,
                        orderId,
                        wage,
                        worker.getName(),
                        (paymentId, returnedOrderId, signature) -> {
                            // Step 3: Payment succeeded at Gateway -> Verify signature & credit worker
                            markPresentBtn.setText("Verifying...");
                            attendanceController.verifyAndCompleteAttendance(
                                currentRecruiterId,
                                worker.getId(),
                                wage,
                                attendanceRecordId,
                                project.getId(),
                                returnedOrderId,
                                paymentId,
                                signature,
                                () -> {
                                    setMarkedPresentState(markPresentBtn, statusDot, statusText, paymentId);
                                    NotificationToast.show(markPresentBtn, "Payout Successful",
                                            String.format("Paid ₹%.2f to %s via Razorpay", wage, worker.getName()),
                                            NotificationToast.ToastType.SUCCESS);
                                },
                                verifyError -> {
                                    markPresentBtn.setDisable(false);
                                    markPresentBtn.setText("Mark Present & Pay");
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Verification Error");
                                    alert.setHeaderText("Payment Verification Failed");
                                    alert.setContentText("Unable to verify payment signature: " + verifyError);
                                    alert.showAndWait();
                                }
                            );
                        },
                        cancelOrError -> {
                            // Step 4: Card declined or user closed window
                            markPresentBtn.setDisable(false);
                            markPresentBtn.setText("Mark Present & Pay");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Payment Incomplete");
                            alert.setHeaderText("Wage Not Paid");
                            alert.setContentText("Payment was not completed: " + cancelOrError);
                            alert.showAndWait();
                        }
                    );
                },
                orderError -> {
                    markPresentBtn.setDisable(false);
                    markPresentBtn.setText("Mark Present & Pay");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Gateway Error");
                    alert.setHeaderText("Order Creation Failed");
                    alert.setContentText("Could not initiate Razorpay order: " + orderError);
                    alert.showAndWait();
                }
            );
        });

        row.getChildren().addAll(workerDetails, roleLabel, wageLabel, statusBadge, markPresentBtn);
        return row;
    }

    private void setMarkedPresentState(Button button, Circle dot, Label text, String txnId) {
        dot.setFill(Color.web("#10b981"));
        text.setText("PAID & PRESENT");
        text.setTextFill(Color.web("#065f46"));

        button.setDisable(true);
        button.setText("Paid");
        button.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #059669; -fx-border-color: #a7f3d0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold;");
        if (txnId != null) {
            button.setTooltip(new Tooltip("Txn Ref: " + txnId));
        }
    }

    private void setPendingState(Button button, Circle dot, Label text) {
        dot.setFill(Color.web("#f59e0b"));
        text.setText("NOT MARKED");
        text.setTextFill(Color.web("#92400e"));

        button.setDisable(false);
        button.setText("Mark Present & Pay");
        button.setStyle("-fx-background-color: #272727; -fx-text-fill: #ffd54f; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
    }

    private void handleLowBalanceAlert(Button sourceButton, double requiredWage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Insufficient Wallet Balance");
        alert.setHeaderText("Cannot Complete Daily Wage Payment");
        alert.setContentText(String.format("Your current wallet balance is insufficient to disburse ₹%.2f to the worker.\n\n" +
                "Please recharge your wallet using the Razorpay payment gateway to proceed.", requiredWage));

        ButtonType rechargeBtn = new ButtonType("Recharge Wallet Now");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(rechargeBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == rechargeBtn) {
            triggerRechargeFlow(sourceButton, requiredWage);
        }
    }

    private void triggerRechargeFlow(Button sourceButton, double defaultAmount) {
        TextInputDialog dialog = new TextInputDialog(String.format("%.0f", Math.max(500, defaultAmount)));
        dialog.setTitle("Recharge Recruiter Wallet");
        dialog.setHeaderText("Add funds to your DIHADI escrow wallet:");
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
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Recharge Successful");
                                    alert.setHeaderText("Wallet Credited");
                                    alert.setContentText(String.format("₹%.2f credited to your wallet!\nTxn ID: %s", amount, paymentId));
                                    alert.showAndWait();
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