package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import com.dihadi.model.Project;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Attendance;
import com.dihadi.model.Worker;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.AttendanceController;
import com.dihadi.controller.WorkerController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.*;
import java.time.LocalDate;

public class AttendancePage {
    private final Recruiter recruiter;
    private final List<Project> recruiterProjects = new ArrayList<>();
    private Project selectedProject = null; // null represents "All Projects"
    
    private FlowPane projectSelectorPane;
    private VBox workerListContainer;
    private Label totalWorkersKpi;
    private Label presentWorkersKpi;
    private Label absentWorkersKpi;
    private Label pendingWorkersKpi;
    
    public AttendancePage(Recruiter recruiter) {
        this.recruiter = recruiter;
    }
    
    public Scene getScene(Runnable back) {
        VBox content = new VBox(24);
        content.setPadding(new Insets(32, 60, 48, 60));
        content.setStyle("-fx-background-color: #f3e7ce;");
        content.setMaxWidth(1300);
        content.setAlignment(Pos.TOP_CENTER);
        
        // Navigation & Header
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4c4637; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
        backBtn.setOnAction(e -> { if (back != null) back.run(); });
        
        Label pageTitle = new Label("Daily Workforce Attendance");
        pageTitle.setStyle("-fx-font-family: Georgia; -fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: #1e1b15;");
        
        Label dateSubtitle = new Label("Date: " + LocalDate.now().toString() + "  •  Mark and verify daily site attendance across all projects");
        dateSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #685c52;");
        
        VBox headerText = new VBox(4, pageTitle, dateSubtitle);
        headerText.setAlignment(Pos.CENTER_LEFT);
        
        VBox headerBar = new VBox(12, backBtn, headerText);
        headerBar.setMaxWidth(1180);
        
        // KPI Summary Bar
        HBox kpiBar = createKpiBar();
        kpiBar.setMaxWidth(1180);
        
        // Project Selector Section (All Projects visible)
        VBox projectSection = new VBox(10);
        projectSection.setMaxWidth(1180);
        Label projSectionTitle = new Label("Select Project to Mark Attendance:");
        projSectionTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #735c00;");
        
        projectSelectorPane = new FlowPane(12, 12);
        projectSelectorPane.setAlignment(Pos.CENTER_LEFT);
        projectSelectorPane.setPadding(new Insets(10, 0, 10, 0));
        
        projectSection.getChildren().addAll(projSectionTitle, projectSelectorPane);
        
        // Worker Cards List Container
        workerListContainer = new VBox(16);
        workerListContainer.setMaxWidth(1180);
        
        VBox mainLayout = new VBox(22, headerBar, kpiBar, projectSection, workerListContainer);
        mainLayout.setMaxWidth(1180);
        mainLayout.setAlignment(Pos.TOP_LEFT);
        content.getChildren().add(mainLayout);
        
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        
        // Initial Data Load
        loadAllData();
        
        return new Scene(scroll, 1400, 880);
    }
    
    private HBox createKpiBar() {
        totalWorkersKpi = new Label("0");
        presentWorkersKpi = new Label("0");
        absentWorkersKpi = new Label("0");
        pendingWorkersKpi = new Label("0");
        
        HBox c1 = kpiCard("Total Accepted Workers", totalWorkersKpi, "#1e1b15");
        HBox c2 = kpiCard("Marked Present Today", presentWorkersKpi, "#2a7e3b");
        HBox c3 = kpiCard("Marked Absent Today", absentWorkersKpi, "#d32f2f");
        HBox c4 = kpiCard("Pending Verification", pendingWorkersKpi, "#735c00");
        
        HBox row = new HBox(16, c1, c2, c3, c4);
        row.setAlignment(Pos.CENTER);
        return row;
    }
    
    private HBox kpiCard(String title, Label valLabel, String color) {
        valLabel.setStyle("-fx-font-family: Georgia; -fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #685c52;");
        
        VBox box = new VBox(4, titleLabel, valLabel);
        box.setPadding(new Insets(14, 18, 14, 18));
        box.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: #d0c5af; -fx-border-width: 1.2px; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.06), 10, 0, 0, 3px);");
        box.setPrefWidth(280);
        
        HBox wrapper = new HBox(box);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }
    
    private void loadAllData() {
        new Thread(() -> {
            try {
                ProjectController pc = new ProjectController();
                List<Project> allProjects = pc.getAllProjects();
                recruiterProjects.clear();
                
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (isMatch(p, recruiter)) {
                            recruiterProjects.add(p);
                        }
                    }
                    if (recruiterProjects.isEmpty()) {
                        recruiterProjects.addAll(allProjects);
                    }
                }
                
                Platform.runLater(() -> {
                    renderProjectSelectorPills();
                    renderWorkersList();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void renderProjectSelectorPills() {
        projectSelectorPane.getChildren().clear();
        
        // "All Projects" Button
        Button allBtn = new Button("All Projects (" + recruiterProjects.size() + ")");
        boolean isAllSelected = (selectedProject == null);
        styleProjectPill(allBtn, isAllSelected);
        allBtn.setOnAction(e -> {
            selectedProject = null;
            renderProjectSelectorPills();
            renderWorkersList();
        });
        projectSelectorPane.getChildren().add(allBtn);
        
        // Individual Project Pills
        for (Project p : recruiterProjects) {
            String name = p.getProjectName() != null ? p.getProjectName() : p.getProjectId();
            String status = p.getStatus() != null ? p.getStatus() : "Active";
            Button pBtn = new Button(name + " [" + status + "]");
            boolean isSelected = (selectedProject != null && p.getProjectId().equals(selectedProject.getProjectId()));
            styleProjectPill(pBtn, isSelected);
            pBtn.setOnAction(e -> {
                selectedProject = p;
                renderProjectSelectorPills();
                renderWorkersList();
            });
            projectSelectorPane.getChildren().add(pBtn);
        }
    }
    
    private void styleProjectPill(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle("-fx-background-color: #735c00; -fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: 800; -fx-padding: 8px 18px; -fx-background-radius: 20px; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(115,92,0,0.3), 8, 0, 0, 2px);");
        } else {
            btn.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #4c4637; -fx-font-size: 13px; -fx-font-weight: 700; -fx-padding: 8px 18px; -fx-background-radius: 20px; -fx-border-color: #d0c5af; -fx-border-radius: 20px; -fx-cursor: hand;");
        }
    }
    
    private void renderWorkersList() {
        workerListContainer.getChildren().clear();
        workerListContainer.getChildren().add(new Label("Loading worker attendance records..."));
        
        new Thread(() -> {
            try {
                JobApplicationController jc = new JobApplicationController();
                List<JobApplication> allApps = jc.getAllApplications();
                
                WorkerController wc = new WorkerController();
                List<Worker> allWorkers = wc.getAllWorkers();
                Map<String, Worker> workerMap = new HashMap<>();
                if (allWorkers != null) {
                    for (Worker w : allWorkers) {
                        if (w.getMobileNumber() != null) {
                            String raw = w.getMobileNumber().replaceAll("[\\s\\-\\(\\)]", "");
                            workerMap.put(raw, w);
                            String digits = raw.replaceAll("\\D", "");
                            if (digits.length() >= 10) {
                                workerMap.put(digits.substring(digits.length() - 10), w);
                            }
                        }
                    }
                }
                
                AttendanceController ac = new AttendanceController();
                String today = LocalDate.now().toString();
                
                // Map project ID to Project Name for easy reference
                Map<String, String> projectNameMap = new HashMap<>();
                for (Project p : recruiterProjects) {
                    projectNameMap.put(p.getProjectId(), p.getProjectName());
                }
                
                // Filter accepted applications for selected project (or all recruiter projects)
                Set<String> targetProjIds = new HashSet<>();
                if (selectedProject != null) {
                    targetProjIds.add(selectedProject.getProjectId());
                } else {
                    for (Project p : recruiterProjects) {
                        targetProjIds.add(p.getProjectId());
                    }
                }
                
                // Strictly deduplicate workers by workerMobile + projectId
                Map<String, JobApplication> deduplicatedApps = new LinkedHashMap<>();
                if (allApps != null) {
                    for (JobApplication a : allApps) {
                        if ("Accepted".equalsIgnoreCase(a.getStatus()) && a.getProjectId() != null && targetProjIds.contains(a.getProjectId())) {
                            String cleanMob = a.getWorkerMobile() != null ? a.getWorkerMobile().replaceAll("\\D", "") : "";
                            String key = cleanMob + "_" + a.getProjectId();
                            deduplicatedApps.putIfAbsent(key, a);
                        }
                    }
                }
                
                List<JobApplication> appList = new ArrayList<>(deduplicatedApps.values());
                
                // Pre-fetch attendance to compute KPIs
                int total = appList.size();
                int presentCount = 0;
                int absentCount = 0;
                Map<String, String> workerTodayStatus = new HashMap<>();
                
                for (JobApplication app : appList) {
                    List<Attendance> attList = ac.getAttendanceByWorker(app.getWorkerMobile());
                    for (Attendance att : attList) {
                        if (today.equals(att.getDate()) && app.getProjectId().equals(att.getProjectId())) {
                            workerTodayStatus.put(app.getWorkerMobile() + "_" + app.getProjectId(), att.getStatus());
                            if ("Present".equalsIgnoreCase(att.getStatus())) presentCount++;
                            else if ("Absent".equalsIgnoreCase(att.getStatus())) absentCount++;
                            break;
                        }
                    }
                }
                
                final int finalTotal = total;
                final int finalPresent = presentCount;
                final int finalAbsent = absentCount;
                final int finalPending = total - (presentCount + absentCount);
                
                Platform.runLater(() -> {
                    totalWorkersKpi.setText(String.valueOf(finalTotal));
                    presentWorkersKpi.setText(String.valueOf(finalPresent));
                    absentWorkersKpi.setText(String.valueOf(finalAbsent));
                    pendingWorkersKpi.setText(String.valueOf(Math.max(0, finalPending)));
                    
                    workerListContainer.getChildren().clear();
                    if (appList.isEmpty()) {
                        String msg = (selectedProject != null) 
                                ? "No accepted workers found for " + selectedProject.getProjectName() + "."
                                : "No accepted workers found across your projects.";
                        Label emptyLbl = new Label(msg);
                        emptyLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #685c52; -fx-padding: 30px;");
                        workerListContainer.getChildren().add(emptyLbl);
                    } else {
                        for (JobApplication app : appList) {
                            String mob = app.getWorkerMobile() != null ? app.getWorkerMobile() : "";
                            String cleanMob = mob.replaceAll("[\\s\\-\\(\\)]", "");
                            String digits = cleanMob.replaceAll("\\D", "");
                            Worker matchedWorker = workerMap.get(cleanMob);
                            if (matchedWorker == null && digits.length() >= 10) {
                                matchedWorker = workerMap.get(digits.substring(digits.length() - 10));
                            }
                            String pName = projectNameMap.getOrDefault(app.getProjectId(), "Assigned Project");
                            String existingStatus = workerTodayStatus.get(app.getWorkerMobile() + "_" + app.getProjectId());
                            workerListContainer.getChildren().add(createWorkerAttendanceCard(app, matchedWorker, pName, ac, today, existingStatus));
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private VBox createWorkerAttendanceCard(JobApplication app, Worker worker, String projectName, AttendanceController ac, String date, String existingStatus) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 14px; -fx-border-color: #d0c5af; -fx-border-width: 1.2px; -fx-border-radius: 14px; -fx-effect: dropshadow(gaussian, rgba(58,48,39,0.06), 12, 0, 0, 3px);");
        
        String name = "Worker";
        if (worker != null) {
            String fn = worker.getFirstName() != null ? worker.getFirstName().trim() : "";
            String mn = worker.getMiddleName() != null ? worker.getMiddleName().trim() : "";
            String ln = worker.getLastName() != null ? worker.getLastName().trim() : "";
            String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
            if (!full.isEmpty()) name = full;
        }
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-family: Georgia; -fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #1e1b15;");
        
        Label projectBadge = new Label("Project: " + projectName);
        projectBadge.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #735c00; -fx-background-color: #faf3e8; -fx-background-radius: 8px; -fx-padding: 3px 10px;");
        
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox topRow = new HBox(12, nameLabel, topSpacer, projectBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        String mobileVal = app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank() ? app.getWorkerMobile() : "Not provided";
        Label mobileLabel = new Label("Contact: " + mobileVal);
        mobileLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4c4637; -fx-font-weight: 600;");
        
        String roleVal = app.getJobTitle() != null && !app.getJobTitle().isBlank() ? app.getJobTitle() : (worker != null && worker.getWorkerType() != null ? worker.getWorkerType() : "General Worker");
        Label jobLabel = new Label("Role / Skill: " + roleVal);
        jobLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4c4637; -fx-font-weight: 600;");
        
        HBox infoRow = new HBox(24, mobileLabel, jobLabel);
        infoRow.setAlignment(Pos.CENTER_LEFT);
        
        Button presentBtn = new Button("Mark Present");
        presentBtn.setStyle("-fx-background-color: #2a7e3b; -fx-text-fill: white; -fx-font-weight: 800; -fx-padding: 8px 18px; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        Button absentBtn = new Button("Mark Absent");
        absentBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: 800; -fx-padding: 8px 18px; -fx-background-radius: 8px; -fx-cursor: hand;");
        
        Label statusLabel = new Label("Not Marked Today");
        statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #8c7e6b;");
        
        if ("Present".equalsIgnoreCase(existingStatus)) {
            presentBtn.setDisable(true);
            absentBtn.setDisable(false);
            statusLabel.setText("Verified Present Today");
            statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #2a7e3b;");
        } else if ("Absent".equalsIgnoreCase(existingStatus)) {
            presentBtn.setDisable(false);
            absentBtn.setDisable(true);
            statusLabel.setText("Marked Absent Today");
            statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #d32f2f;");
        }
        
        presentBtn.setOnAction(e -> {
            presentBtn.setDisable(true);
            absentBtn.setDisable(true);
            statusLabel.setText("Updating...");
            statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #735c00;");
            
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(
                            String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)),
                            app.getProjectId(),
                            app.getWorkerMobile(),
                            date,
                            "Present"
                    );
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        presentBtn.setDisable(true);
                        absentBtn.setDisable(false);
                        statusLabel.setText("Verified Present Today");
                        statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #2a7e3b;");
                        refreshKpiCounters();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        
        absentBtn.setOnAction(e -> {
            presentBtn.setDisable(true);
            absentBtn.setDisable(true);
            statusLabel.setText("Updating...");
            statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #735c00;");
            
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(
                            String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)),
                            app.getProjectId(),
                            app.getWorkerMobile(),
                            date,
                            "Absent"
                    );
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        presentBtn.setDisable(false);
                        absentBtn.setDisable(true);
                        statusLabel.setText("Marked Absent Today");
                        statusLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #d32f2f;");
                        refreshKpiCounters();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        
        HBox actionsRow = new HBox(14, presentBtn, absentBtn, statusLabel);
        actionsRow.setAlignment(Pos.CENTER_LEFT);
        
        card.getChildren().addAll(topRow, infoRow, actionsRow);
        return card;
    }
    
    private void refreshKpiCounters() {
        new Thread(() -> {
            try {
                JobApplicationController jc = new JobApplicationController();
                List<JobApplication> allApps = jc.getAllApplications();
                AttendanceController ac = new AttendanceController();
                String today = LocalDate.now().toString();
                
                Set<String> targetProjIds = new HashSet<>();
                if (selectedProject != null) {
                    targetProjIds.add(selectedProject.getProjectId());
                } else {
                    for (Project p : recruiterProjects) targetProjIds.add(p.getProjectId());
                }
                
                Map<String, JobApplication> deduplicatedApps = new LinkedHashMap<>();
                if (allApps != null) {
                    for (JobApplication a : allApps) {
                        if ("Accepted".equalsIgnoreCase(a.getStatus()) && a.getProjectId() != null && targetProjIds.contains(a.getProjectId())) {
                            String cleanMob = a.getWorkerMobile() != null ? a.getWorkerMobile().replaceAll("\\D", "") : "";
                            deduplicatedApps.putIfAbsent(cleanMob + "_" + a.getProjectId(), a);
                        }
                    }
                }
                
                int total = deduplicatedApps.size();
                int presentCount = 0;
                int absentCount = 0;
                
                for (JobApplication app : deduplicatedApps.values()) {
                    List<Attendance> attList = ac.getAttendanceByWorker(app.getWorkerMobile());
                    for (Attendance att : attList) {
                        if (today.equals(att.getDate()) && app.getProjectId().equals(att.getProjectId())) {
                            if ("Present".equalsIgnoreCase(att.getStatus())) presentCount++;
                            else if ("Absent".equalsIgnoreCase(att.getStatus())) absentCount++;
                            break;
                        }
                    }
                }
                
                final int finalTotal = total;
                final int finalPresent = presentCount;
                final int finalAbsent = absentCount;
                final int finalPending = total - (presentCount + absentCount);
                
                Platform.runLater(() -> {
                    totalWorkersKpi.setText(String.valueOf(finalTotal));
                    presentWorkersKpi.setText(String.valueOf(finalPresent));
                    absentWorkersKpi.setText(String.valueOf(finalAbsent));
                    pendingWorkersKpi.setText(String.valueOf(Math.max(0, finalPending)));
                });
            } catch (Exception ignored) {}
        }).start();
    }
    
    private boolean isMatch(Project p, Recruiter r) {
        if (p == null || r == null) return false;
        String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
        String rMob = r.getMobileNumber() != null ? r.getMobileNumber().replaceAll("\\D", "") : "";
        String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
        String rEmail = r.getEmail() != null ? r.getEmail().trim().toLowerCase() : "";
        String rComp = r.getCompanyName() != null ? r.getCompanyName().trim().toLowerCase() : "";
        String pComp = p.getContactName() != null ? p.getContactName().trim().toLowerCase() : "";
        
        if (!pMob.isEmpty() && !rMob.isEmpty() && (pMob.equals(rMob) || pMob.endsWith(rMob) || rMob.endsWith(pMob))) return true;
        if (!pEmail.isEmpty() && !rEmail.isEmpty() && pEmail.equals(rEmail)) return true;
        if (!rComp.isEmpty() && !pComp.isEmpty() && (pComp.contains(rComp) || rComp.contains(pComp))) return true;
        return false;
    }
}
