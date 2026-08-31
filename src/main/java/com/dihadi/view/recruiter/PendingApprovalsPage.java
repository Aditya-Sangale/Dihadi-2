package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import com.dihadi.model.Project;
import com.dihadi.model.JobApplication;
import com.dihadi.model.Worker;
import com.dihadi.controller.JobApplicationController;
import com.dihadi.controller.ProjectController;
import com.dihadi.controller.WorkerController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PendingApprovalsPage {
    private final Recruiter recruiter;
    
    public PendingApprovalsPage(Recruiter recruiter) {
        this.recruiter = recruiter;
    }
    
    public Scene getScene(Runnable back) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40, 72, 40, 72));
        content.setStyle("-fx-background-color: #f3e7ce;");
        content.setMaxWidth(1440);
        content.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Pending Worker Applications");
        title.setStyle("-fx-font-family: Georgia; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #735c00;");
        
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4c4637; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
        backBtn.setOnAction(e -> { if (back != null) back.run(); });
        HBox headerBox = new HBox(backBtn);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox listContainer = new VBox(15);
        listContainer.setMaxWidth(800);
        
        new Thread(() -> {
            try {
                JobApplicationController c = new JobApplicationController();
                List<JobApplication> allApps = c.getAllApplications();
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
                
                ProjectController pc = new ProjectController();
                List<Project> allProjects = pc.getAllProjects();
                Set<String> recruiterProjIds = new HashSet<>();
                if (allProjects != null) {
                    for (Project p : allProjects) {
                        if (isMatch(p, recruiter)) {
                            if (p.getProjectId() != null && !p.getProjectId().isBlank()) {
                                recruiterProjIds.add(p.getProjectId());
                            }
                        }
                    }
                }
                
                String rMobDigits = recruiter != null && recruiter.getMobileNumber() != null 
                        ? recruiter.getMobileNumber().replaceAll("\\D", "") 
                        : "";
                
                Platform.runLater(() -> {
                    boolean found = false;
                    for (JobApplication app : allApps) {
                        if ("Pending".equalsIgnoreCase(app.getStatus())) {
                            boolean matchesRecruiter = false;
                            if (app.getProjectId() != null && recruiterProjIds.contains(app.getProjectId())) {
                                matchesRecruiter = true;
                            } else {
                                String appRMob = app.getRecruiterMobile() != null ? app.getRecruiterMobile().replaceAll("\\D", "") : "";
                                if (!rMobDigits.isEmpty() && !appRMob.isEmpty()) {
                                    matchesRecruiter = appRMob.equals(rMobDigits) || appRMob.endsWith(rMobDigits) || rMobDigits.endsWith(appRMob);
                                }
                            }
                            
                            if (matchesRecruiter) {
                                String mob = app.getWorkerMobile() != null ? app.getWorkerMobile() : "";
                                String cleanMob = mob.replaceAll("[\\s\\-\\(\\)]", "");
                                String digits = cleanMob.replaceAll("\\D", "");
                                Worker matchedWorker = workerMap.get(cleanMob);
                                if (matchedWorker == null && digits.length() >= 10) {
                                    matchedWorker = workerMap.get(digits.substring(digits.length() - 10));
                                }
                                listContainer.getChildren().add(createCard(app, matchedWorker, c, listContainer));
                                found = true;
                            }
                        }
                    }
                    if (!found) {
                        Label lbl = new Label("No pending applications at this time.");
                        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #4c4637;");
                        listContainer.getChildren().add(lbl);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
        
        VBox mainBox = new VBox(20, headerBox, title, listContainer);
        mainBox.setMaxWidth(1000);
        content.getChildren().add(mainBox);
        
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#f3e7ce;-fx-background-color:#f3e7ce;-fx-border-width:0;");
        return new Scene(scroll, 1440, 900);
    }
    
    private VBox createCard(JobApplication app, Worker worker, JobApplicationController c, VBox listContainer) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: #cfc6b2; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(34,34,34,0.05), 10, 0, 0, 4px);");
        
        String name = "Worker";
        String locationStr = "";
        if (worker != null) {
            String fn = worker.getFirstName() != null ? worker.getFirstName().trim() : "";
            String mn = worker.getMiddleName() != null ? worker.getMiddleName().trim() : "";
            String ln = worker.getLastName() != null ? worker.getLastName().trim() : "";
            String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
            if (!full.isEmpty()) {
                name = full;
            }
            String city = worker.getCity() != null ? worker.getCity().trim() : "";
            String state = worker.getState() != null ? worker.getState().trim() : "";
            if (!city.isEmpty() && !state.isEmpty()) {
                locationStr = city + ", " + state;
            } else if (!city.isEmpty()) {
                locationStr = city;
            } else if (!state.isEmpty()) {
                locationStr = state;
            }
        }
        
        if (locationStr.isEmpty() && app.getJobLocation() != null && !app.getJobLocation().isBlank()) {
            locationStr = app.getJobLocation().trim();
        }
        final String finalWorkerName = name;
        
        Label nameLabel = new Label("👤  " + name);
        nameLabel.setStyle("-fx-font-family: Georgia; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e1b15;");
        
        String role = (app.getJobTitle() != null && !app.getJobTitle().isBlank()) 
                ? app.getJobTitle() 
                : (worker != null && worker.getWorkerType() != null ? worker.getWorkerType() : "Worker");
        String subtext = "Role: " + role + (locationStr.isEmpty() ? "" : "  •  Location: " + locationStr);
        Label subtextLabel = new Label(subtext);
        subtextLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #735c00;");
        
        String mobileVal = app.getWorkerMobile() != null && !app.getWorkerMobile().isBlank()
                ? app.getWorkerMobile()
                : (worker != null && worker.getMobileNumber() != null ? worker.getMobileNumber() : "Not provided");
        Label workerLabel = new Label("Worker Mobile: " + mobileVal);
        workerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637;");
        
        String wageVal = app.getJobWage() != null && !app.getJobWage().isBlank()
                ? app.getJobWage()
                : (worker != null && worker.getDailyWage() > 0 ? "₹" + worker.getDailyWage() + "/day" : "Standard project wage");
        Label wageLabel = new Label("Expected Wage: " + wageVal);
        wageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637;");
        
        Button accept = new Button("Accept");
        accept.setStyle("-fx-background-color: #2a7e3b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        Button reject = new Button("Reject");
        reject.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        accept.setOnAction(e -> {
            app.setStatus("Accepted");
            accept.setDisable(true);
            reject.setDisable(true);
            new Thread(() -> {
                c.saveApplication(app);
                String recName = (recruiter != null && recruiter.getFirstName() != null)
                        ? (recruiter.getFirstName() + " " + (recruiter.getLastName() != null ? recruiter.getLastName() : "")).trim()
                        : "Site Recruiter";
                String projName = app.getJobTitle();
                if (app.getProjectId() != null && !app.getProjectId().isBlank()) {
                    try {
                        com.dihadi.model.Project p = new com.dihadi.controller.ProjectController().getProject(app.getProjectId());
                        if (p != null && p.getProjectName() != null && !p.getProjectName().isBlank()) {
                            projName = p.getProjectName();
                        }
                    } catch (Exception ignored) {}
                }
                new com.dihadi.controller.NotificationController().notifyWorkerApplicationAccepted(app, recName, projName);
                Platform.runLater(() -> {
                    com.dihadi.view.NotificationToast.show(accept, "Worker Accepted! 🎉", "Your approval for " + finalWorkerName + " has been recorded and a notification was sent to the worker.", com.dihadi.view.NotificationToast.ToastType.SUCCESS);
                    listContainer.getChildren().remove(card);
                    if (listContainer.getChildren().isEmpty()) {
                        Label lbl = new Label("No pending applications at this time.");
                        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #4c4637;");
                        listContainer.getChildren().add(lbl);
                    }
                });
            }).start();
        });
        
        reject.setOnAction(e -> {
            app.setStatus("Rejected");
            accept.setDisable(true);
            reject.setDisable(true);
            new Thread(() -> {
                c.saveApplication(app);
                Platform.runLater(() -> {
                    com.dihadi.view.NotificationToast.show(reject, "Application Declined", "The application for " + finalWorkerName + " has been rejected.", com.dihadi.view.NotificationToast.ToastType.INFO);
                    listContainer.getChildren().remove(card);
                    if (listContainer.getChildren().isEmpty()) {
                        Label lbl = new Label("No pending applications at this time.");
                        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #4c4637;");
                        listContainer.getChildren().add(lbl);
                    }
                });
            }).start();
        });
        
        HBox actions = new HBox(15, accept, reject);
        card.getChildren().addAll(nameLabel, subtextLabel, workerLabel, wageLabel, actions);
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


