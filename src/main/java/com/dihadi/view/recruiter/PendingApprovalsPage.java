package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import com.dihadi.model.JobApplication;
import com.dihadi.controller.JobApplicationController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;

public class PendingApprovalsPage {
    private final Recruiter recruiter;
    
    public PendingApprovalsPage(Recruiter recruiter) {
        this.recruiter = recruiter;
    }
    
    public Scene getScene(Runnable back) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(40, 72, 40, 72));
        content.setStyle("-fx-background-color: #fff8f0;");
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
                List<JobApplication> apps = c.getApplicationsByRecruiter(recruiter.getMobileNumber());
                Platform.runLater(() -> {
                    boolean found = false;
                    for (JobApplication app : apps) {
                        if ("Pending".equalsIgnoreCase(app.getStatus())) {
                            listContainer.getChildren().add(createCard(app, c, listContainer));
                            found = true;
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
        scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");
        return new Scene(scroll, 1440, 900);
    }
    
    private VBox createCard(JobApplication app, JobApplicationController c, VBox listContainer) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-border-color: #cfc6b2; -fx-border-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(34,34,34,0.05), 10, 0, 0, 4px);");
        
        Label jobLabel = new Label(app.getJobTitle() + " @ " + app.getJobLocation());
        jobLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1e1b15;");
        
        Label wageLabel = new Label("Wage: " + app.getJobWage());
        wageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637;");
        
        Label workerLabel = new Label("Worker Mobile: " + app.getWorkerMobile());
        workerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4c4637;");
        
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
                Platform.runLater(() -> listContainer.getChildren().remove(card));
            }).start();
        });
        
        reject.setOnAction(e -> {
            app.setStatus("Rejected");
            accept.setDisable(true);
            reject.setDisable(true);
            new Thread(() -> {
                c.saveApplication(app);
                Platform.runLater(() -> listContainer.getChildren().remove(card));
            }).start();
        });
        
        HBox actions = new HBox(15, accept, reject);
        card.getChildren().addAll(jobLabel, wageLabel, workerLabel, actions);
        return card;
    }
}
