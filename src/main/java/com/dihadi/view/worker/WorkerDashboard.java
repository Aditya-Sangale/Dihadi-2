package com.dihadi.view.worker;

import com.dihadi.model.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/** Full Worker Portal dashboard, populated from the authenticated Worker record. */
public class WorkerDashboard {
    private final Worker worker;
    public WorkerDashboard(Worker worker) { this.worker = worker; }

    public Scene getScene(Runnable back) {
        String name = value(worker.getFirstName()) + (worker.getLastName() == null ? "" : " " + worker.getLastName());
        String category = value(worker.getWorkerType(), "Skilled Worker");
        String location = (value(worker.getCity()) + ", " + value(worker.getState())).replace("Not provided, Not provided", "Location not provided");
        VBox content = new VBox(24, header(name), hero(name, category), metrics(), lowerSections(location), footer(back));
        content.setPadding(new Insets(32, 80, 40, 80)); content.setMaxWidth(1440); content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scroll = new ScrollPane(content); scroll.setFitToWidth(true); scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;");
        return new Scene(scroll, 1440, 900);
    }

    private HBox header(String name) {
        Label brand = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label user = label(name, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#4c4637;");
        HBox h = new HBox(16, brand, spacer, user); h.setAlignment(Pos.CENTER_LEFT); h.setPadding(new Insets(0, 0, 16, 0)); h.setStyle("-fx-border-color:transparent transparent #d0c5af transparent;-fx-border-width:0 0 1px 0;"); return h;
    }
    private VBox hero(String name, String category) {
        Label title = label("Namaste, " + name + "!", "-fx-font-family:Georgia;-fx-font-size:31px;-fx-font-weight:700;-fx-text-fill:#1e1b15;");
        Label role = label(category + "   •   KYC Verified ✓", "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#735c00;");
        Label copy = label("Your skills are in high demand. Check your active projects and upcoming invitations below.", "-fx-font-size:15px;-fx-text-fill:#4c4637;"); copy.setMaxWidth(420); copy.setWrapText(true);
        VBox welcome = new VBox(10, title, role, copy); welcome.setPrefWidth(410);
        VBox project = panel("ACTIVE SITE PROJECT", label("No active project assigned", "-fx-font-family:Georgia;-fx-font-size:23px;-fx-font-weight:700;"), detail("Role", category), detail("Daily wage", "Rs. " + worker.getDailyWage() + " / day"), detail("Site status", "Ready for opportunities"));
        HBox h = new HBox(36, welcome, project); h.setAlignment(Pos.CENTER_LEFT); return new VBox(h);
    }
    private HBox metrics() { HBox row = new HBox(24, metric("Wallet Balance", "Rs. 0", "+ Earnings will appear here"), metric("Attendance (Month)", "0 Days Present", "No attendance recorded"), metric("Job Requests", "0 New", "Invitations"), metric("Reputation", "New", "No deployments yet")); for (Node n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS); return row; }
    private VBox metric(String title,String value,String note){ VBox v=panel(title,label(value,"-fx-font-family:Georgia;-fx-font-size:28px;-fx-font-weight:700;-fx-text-fill:#735c00;"),label(note,"-fx-font-size:13px;-fx-text-fill:#3f4938;")); v.setMinHeight(145); return v; }
    private HBox lowerSections(String location) {
        VBox history = panel("RECENT WORK HISTORY", label("No completed work history yet.", "-fx-font-size:15px;-fx-text-fill:#4c4637;"), detail("Current location", location), detail("Experience", value(worker.getExperience())));
        
        VBox invitations = panel("PENDING JOB INVITATIONS", label("Loading applications...", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
        new Thread(() -> {
            java.util.List<com.dihadi.model.JobApplication> apps = new com.dihadi.controller.JobApplicationController().getApplicationsByWorker(worker.getMobileNumber());
            javafx.application.Platform.runLater(() -> {
                invitations.getChildren().clear();
                invitations.getChildren().add(label("PENDING JOB INVITATIONS", "-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#685c52;"));
                if (apps.isEmpty()) {
                    invitations.getChildren().addAll(label("No pending invitations", "-fx-font-size:17px;-fx-font-weight:700;"), label("New opportunities will appear here.", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                } else {
                    for (com.dihadi.model.JobApplication app : apps) {
                        Label withdraw = new Label("Withdraw application");
                        withdraw.setStyle("-fx-font-size:12px;-fx-text-fill:#e74c3c;-fx-underline:true;-fx-cursor:hand;");
                        
                        VBox appCard = new VBox(3, 
                            label(app.getJobTitle(), "-fx-font-size:15px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"),
                            label("⌖ " + app.getJobLocation(), "-fx-font-size:12px;-fx-text-fill:#4d4635;"),
                            label("Status: " + app.getStatus(), "-fx-font-size:13px;-fx-text-fill:#d4af37;-fx-font-weight:700;"),
                            withdraw
                        );
                        appCard.setPadding(new Insets(10));
                        appCard.setStyle("-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#e5d9c7;-fx-border-radius:8px;");
                        
                        withdraw.setOnMouseClicked(ev -> {
                            new Thread(() -> {
                                new com.dihadi.controller.JobApplicationController().deleteApplication(app.getApplicationId());
                                javafx.application.Platform.runLater(() -> {
                                    invitations.getChildren().remove(appCard);
                                    if (invitations.getChildren().size() == 1) {
                                        invitations.getChildren().addAll(label("No pending invitations", "-fx-font-size:17px;-fx-font-weight:700;"), label("New opportunities will appear here.", "-fx-font-size:14px;-fx-text-fill:#4c4637;"));
                                    }
                                });
                            }).start();
                        });
                        
                        invitations.getChildren().add(appCard);
                    }
                }
            });
        }).start();

        VBox kyc = panel("KYC VERIFICATION HUB", detail("Profile status", "Submitted for verification"), detail("Education", value(worker.getEducation())), detail("Documents", "Upload documents when available"));
        VBox settings = panel("SKILL & PREF SETTINGS", detail("Primary skill", value(worker.getSubSkill())), detail("Desired daily wage", "Rs. " + worker.getDailyWage()), detail("Work radius", "Open to suitable projects"));
        VBox left = new VBox(16, history, invitations); VBox right = new VBox(16, kyc, settings); HBox h = new HBox(18, left, right); HBox.setHgrow(left, Priority.ALWAYS); HBox.setHgrow(right, Priority.ALWAYS); return h;
    }
    private HBox footer(Runnable back) { Button exit=new Button("Back to Worker Page"); exit.setOnAction(e->{if(back!=null)back.run();}); exit.setStyle("-fx-background-color:#d4af37;-fx-background-radius:999px;-fx-text-fill:#231b00;-fx-font-weight:700;-fx-padding:11px 22px;"); HBox h=new HBox(exit);h.setAlignment(Pos.CENTER_RIGHT);return h; }
    private VBox panel(String title, Node... nodes){VBox v=new VBox(12);v.getChildren().add(label(title,"-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1px;-fx-text-fill:#685c52;"));v.getChildren().addAll(nodes);v.setPadding(new Insets(24));v.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:#cfc6b2;-fx-border-radius:16px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.09),14,0,0,4px);");return v;}
    private VBox detail(String k,String v){return new VBox(3,label(k,"-fx-font-size:11px;-fx-text-fill:#7e7665;"),label(value(v),"-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"));}
    private String value(String s){return s==null||s.isBlank()?"Not provided":s;} private String value(String s,String fallback){return s==null||s.isBlank()?fallback:s;}
    private Label label(String s,String st){Label l=new Label(s);l.setWrapText(true);l.setStyle("-fx-font-family:'Segoe UI';"+st);return l;}
}