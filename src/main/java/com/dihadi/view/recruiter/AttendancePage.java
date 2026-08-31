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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class AttendancePage {
    private final Recruiter recruiter;
    
    public AttendancePage(Recruiter recruiter) {
        this.recruiter = recruiter;
    }
    
    public Scene getScene(Runnable back) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40, 72, 40, 72));
        content.setStyle("-fx-background-color: #f3e7ce;");
        content.setMaxWidth(1440);
        content.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Worker Attendance - " + LocalDate.now().toString());
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #735c00;");
        
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4c4637; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
        backBtn.setOnAction(e -> { if (back != null) back.run(); });
        HBox headerBox = new HBox(backBtn);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox listContainer = new VBox(15);
        listContainer.setMaxWidth(800);
        
        ComboBox<Project> projectCombo = new ComboBox<>();
        projectCombo.setPromptText("Select a Project...");
        projectCombo.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        projectCombo.setPrefWidth(400);
        
        projectCombo.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project p) {
                return p == null ? "" : (p.getProjectName() != null ? p.getProjectName() : p.getProjectId());
            }
            @Override
            public Project fromString(String string) {
                return null;
            }
        });
        
        new Thread(() -> {
            try {
                ProjectController pc = new ProjectController();
                List<Project> allProjects = pc.getAllProjects();
                List<Project> myProjects = new ArrayList<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (isMatch(p, recruiter)) {
                            myProjects.add(p);
                        }
                    }
                    if (myProjects.isEmpty()) {
                        myProjects.addAll(allProjects);
                    }
                }
                Platform.runLater(() -> {
                    projectCombo.getItems().addAll(myProjects);
                    if (!myProjects.isEmpty()) {
                        projectCombo.setValue(myProjects.get(0));
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
        
        projectCombo.setOnAction(e -> {
            Project selected = projectCombo.getValue();
            if (selected != null) {
                loadWorkersForProject(selected, listContainer);
            }
        });
        
        VBox mainBox = new VBox(20, headerBox, title, new HBox(10, new Label("Project:"), projectCombo), listContainer);
        mainBox.setMaxWidth(1000);
        content.getChildren().add(mainBox);
        
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        return new Scene(scroll, 1440, 900);
    }
    
    private void loadWorkersForProject(Project p, VBox container) {
        container.getChildren().clear();
        container.getChildren().add(new Label("Loading workers..."));
        
        new Thread(() -> {
            try {
                JobApplicationController jc = new JobApplicationController();
                List<JobApplication> allApps = jc.getAllApplications();
                List<JobApplication> apps = allApps.stream()
                        .filter(a -> p.getProjectId() != null && p.getProjectId().equals(a.getProjectId()) && "Accepted".equalsIgnoreCase(a.getStatus()))
                        .collect(Collectors.toList());
                        
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
                
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    if (apps.isEmpty()) {
                        Label l = new Label("No accepted workers found for this project.");
                        l.setStyle("-fx-font-size: 16px; -fx-text-fill: #4c4637;");
                        container.getChildren().add(l);
                    } else {
                        for (JobApplication app : apps) {
                            String mob = app.getWorkerMobile() != null ? app.getWorkerMobile() : "";
                            String cleanMob = mob.replaceAll("[\\s\\-\\(\\)]", "");
                            String digits = cleanMob.replaceAll("\\D", "");
                            Worker matchedWorker = workerMap.get(cleanMob);
                            if (matchedWorker == null && digits.length() >= 10) {
                                matchedWorker = workerMap.get(digits.substring(digits.length() - 10));
                            }
                            container.getChildren().add(createWorkerCard(app, matchedWorker, ac, today));
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    container.getChildren().add(new Label("Error loading workers."));
                });
            }
        }).start();
    }
    
    private VBox createWorkerCard(JobApplication app, Worker worker, AttendanceController ac, String date) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: #cfc6b2; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(34,34,34,0.05), 10, 0, 0, 4px);");
        
        String name = "Worker";
        if (worker != null) {
            String fn = worker.getFirstName() != null ? worker.getFirstName().trim() : "";
            String mn = worker.getMiddleName() != null ? worker.getMiddleName().trim() : "";
            String ln = worker.getLastName() != null ? worker.getLastName().trim() : "";
            String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
            if (!full.isEmpty()) {
                name = full;
            }
        }
        
        Label nameLabel = new Label("👤  " + name);
        nameLabel.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e1b15;");
        
        String mobileVal = app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank() 
                ? app.getWorkerMobile() 
                : (worker != null && worker.getMobileNumber() != null ? worker.getMobileNumber() : "Not provided");
        Label mobileLabel = new Label("Mobile: " + mobileVal);
        mobileLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637; -fx-font-weight: 600;");
        
        String roleVal = app.getJobTitle() != null && !app.getJobTitle().isBlank() 
                ? app.getJobTitle() 
                : (worker != null && worker.getWorkerType() != null ? worker.getWorkerType() : "General Worker");
        Label jobLabel = new Label("Role: " + roleVal);
        jobLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #735c00; -fx-font-weight: 600;");
        
        Button present = new Button("Mark Present");
        present.setStyle("-fx-background-color: #2a7e3b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        Button absent = new Button("Mark Absent");
        absent.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        new Thread(() -> {
            try {
                List<Attendance> atts = ac.getAttendanceByWorker(app.getWorkerMobile());
                for (Attendance att : atts) {
                    if (date.equals(att.getDate()) && app.getProjectId().equals(att.getProjectId())) {
                        final boolean isPresent = "Present".equalsIgnoreCase(att.getStatus());
                        Platform.runLater(() -> {
                            present.setDisable(isPresent);
                            absent.setDisable(!isPresent);
                            statusLabel.setText(isPresent ? "✓ Marked Present" : "✕ Marked Absent");
                            statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + (isPresent ? "#2a7e3b" : "#d32f2f") + ";");
                        });
                        break;
                    }
                }
            } catch (Exception ex) { }
        }).start();
        
        present.setOnAction(e -> {
            present.setDisable(true);
            absent.setDisable(true);
            statusLabel.setText("Saving...");
            statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #735c00;");
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)), app.getProjectId(), app.getWorkerMobile(), date, "Present");
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        present.setDisable(true);
                        absent.setDisable(false);
                        statusLabel.setText("✓ Marked Present");
                        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2a7e3b;");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        
        absent.setOnAction(e -> {
            present.setDisable(true);
            absent.setDisable(true);
            statusLabel.setText("Saving...");
            statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #735c00;");
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(String.valueOf(System.currentTimeMillis()) + String.format("%03d", (int)(Math.random() * 1000)), app.getProjectId(), app.getWorkerMobile(), date, "Absent");
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        present.setDisable(false);
                        absent.setDisable(true);
                        statusLabel.setText("✕ Marked Absent");
                        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #d32f2f;");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        
        HBox actions = new HBox(15, present, absent, statusLabel);
        actions.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(nameLabel, mobileLabel, jobLabel, actions);
        return card;
    }
    
    private boolean isMatch(Project p, Recruiter r) {
        if (p == null || r == null) return false;
        String pMob = p.getMobile() != null ? p.getMobile().replaceAll("\\D", "") : "";
        String rMob = r.getMobileNumber() != null ? r.getMobileNumber().replaceAll("\\D", "") : "";
        String pEmail = p.getEmail() != null ? p.getEmail().trim().toLowerCase() : "";
        String rEmail = r.getEmail() != null ? r.getEmail().trim().toLowerCase() : "";
        
        if (!pMob.isEmpty() && !rMob.isEmpty() && (pMob.equals(rMob) || pMob.endsWith(rMob) || rMob.endsWith(pMob))) return true;
        if (!pEmail.isEmpty() && !rEmail.isEmpty() && pEmail.equals(rEmail)) return true;
        return false;
    }
}

