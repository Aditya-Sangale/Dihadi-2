package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import com.dihadi.model.Project;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Attendance;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.AttendanceController;
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
import java.util.List;
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
        content.setStyle("-fx-background-color: #fff8f0;");
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
                List<Project> myProjects = pc.getAllProjects().stream()
                        .filter(p -> p.getMobile() != null && p.getMobile().equals(recruiter.getMobileNumber()))
                        .collect(Collectors.toList());
                Platform.runLater(() -> {
                    projectCombo.getItems().addAll(myProjects);
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
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");
        return new Scene(scroll, 1440, 900);
    }
    
    private void loadWorkersForProject(Project p, VBox container) {
        container.getChildren().clear();
        container.getChildren().add(new Label("Loading workers..."));
        
        new Thread(() -> {
            try {
                JobApplicationController jc = new JobApplicationController();
                List<JobApplication> apps = jc.getApplicationsByRecruiter(recruiter.getMobileNumber()).stream()
                        .filter(a -> p.getProjectId().equals(a.getProjectId()) && "Accepted".equalsIgnoreCase(a.getStatus()))
                        .collect(Collectors.toList());
                        
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
                            container.getChildren().add(createWorkerCard(app, ac, today));
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
    
    private VBox createWorkerCard(JobApplication app, AttendanceController ac, String date) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: #cfc6b2; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(34,34,34,0.05), 10, 0, 0, 4px);");
        
        Label workerLabel = new Label("Worker Mobile: " + app.getWorkerMobile());
        workerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e1b15;");
        
        Label jobLabel = new Label("Role: " + app.getJobTitle());
        jobLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637;");
        
        Button present = new Button("Mark Present");
        present.setStyle("-fx-background-color: #2a7e3b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        Button absent = new Button("Mark Absent");
        absent.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        new Thread(() -> {
            try {
                List<Attendance> atts = ac.getAttendanceByWorker(app.getWorkerMobile());
                boolean markedToday = false;
                for (Attendance att : atts) {
                    if (date.equals(att.getDate()) && app.getProjectId().equals(att.getProjectId())) {
                        markedToday = true;
                        final boolean isPresent = "Present".equalsIgnoreCase(att.getStatus());
                        Platform.runLater(() -> {
                            present.setDisable(true);
                            absent.setDisable(true);
                            statusLabel.setText(isPresent ? "Marked Present" : "Marked Absent");
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
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(java.util.UUID.randomUUID().toString(), app.getProjectId(), app.getWorkerMobile(), date, "Present");
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        statusLabel.setText("Marked Present");
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
            new Thread(() -> {
                try {
                    Attendance att = new Attendance(java.util.UUID.randomUUID().toString(), app.getProjectId(), app.getWorkerMobile(), date, "Absent");
                    ac.saveAttendance(att);
                    Platform.runLater(() -> {
                        statusLabel.setText("Marked Absent");
                        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #d32f2f;");
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        
        HBox actions = new HBox(15, present, absent, statusLabel);
        actions.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(workerLabel, jobLabel, actions);
        return card;
    }
}
