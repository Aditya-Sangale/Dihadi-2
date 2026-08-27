package com.dihadi.view.recruiter;

import com.dihadi.model.Recruiter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/** Recruiter Portal overview matching the updated dashboard reference. */
public class RecruiterDashboard {
    private final Recruiter recruiter;
    public RecruiterDashboard(Recruiter recruiter) { this.recruiter = recruiter; }

    public Scene getScene(Runnable back) {
        String first = val(recruiter.getFirstName(), "Recruiter");
        String name = first + (blank(recruiter.getLastName()) ? "" : " " + recruiter.getLastName());
        String company = val(recruiter.getCompanyName(), "Organisation not provided");
        VBox content = new VBox(26, header(name), hero(name, company), metrics(), body(company), footer(back));
        content.setPadding(new Insets(26, 72, 38, 72)); content.setMaxWidth(1440); content.setAlignment(Pos.TOP_CENTER);
        ScrollPane scroll = new ScrollPane(content); scroll.setFitToWidth(true); scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;-fx-border-width:0;");
        return new Scene(scroll, 1440, 900);
    }

    private HBox header(String name) {
        Label logo = label("DIHADI", "-fx-font-family:Georgia;-fx-font-size:26px;-fx-font-weight:800;-fx-text-fill:#735c00;");
        Button overview = nav("Overview", true), projects = nav("My Projects", false), attendance = nav("Attendance", false), wallet = nav("Wallet", false), docs = nav("Documents", false), support = nav("Support", false);
        attendance.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) attendance.getScene().getWindow();
            javafx.scene.Scene currentScene = attendance.getScene();
            stage.setScene(new AttendancePage(recruiter).getScene(() -> stage.setScene(currentScene)));
        });
        HBox nav = new HBox(22, overview, projects, attendance, wallet, docs, support); nav.setAlignment(Pos.CENTER);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label profile = label(name, "-fx-font-size:14px;-fx-font-weight:700;-fx-text-fill:#4c4637;-fx-background-color:#faf3e8;-fx-background-radius:999px;-fx-border-color:#cfc6b2;-fx-border-radius:999px;-fx-padding:9px 15px;");
        HBox bar = new HBox(30, logo, nav, spacer, profile); bar.setAlignment(Pos.CENTER_LEFT); bar.setPadding(new Insets(0, 0, 15, 0)); bar.setStyle("-fx-border-color:transparent transparent #cfc6b2 transparent;-fx-border-width:0 0 1px 0;"); return bar;
    }
    private VBox hero(String name, String company) {
        VBox welcome = new VBox(9, label("Welcome back, " + name + "!", "-fx-font-family:Georgia;-fx-font-size:32px;-fx-font-weight:700;-fx-text-fill:#1e1b15;"), label(company + "  •  Recruiter Account", "-fx-font-size:17px;-fx-font-weight:700;-fx-text-fill:#735c00;"), label("Manage your projects, workforce and hiring activity from one place.", "-fx-font-size:15px;-fx-text-fill:#4c4637;")); welcome.setPrefWidth(420);
        VBox active = panel("ACTIVE ONGOING PROJECT", label("No active project assigned", "-fx-font-family:Georgia;-fx-font-size:24px;-fx-font-weight:700;"), detail("Company", company), detail("Site status", "No project data yet"), new HBox(12, action("CREATE PROJECT", false), action("HIRE WORKERS", true))); HBox.setHgrow(active, Priority.ALWAYS);
        HBox row = new HBox(48, welcome, active); row.setAlignment(Pos.CENTER_LEFT); return new VBox(row);
    }
    private HBox metrics() { 
        VBox wallet = metric("Escrow / Wallet", "₹0.00", "Add funds");
        VBox workers = metric("Total Workers", "Loading...", "Available on platform");
        VBox recruiters = metric("Total Recruiters", "Loading...", "Partner network");
        VBox projects = metric("Total Projects", "Loading...", "Active projects");
        new Thread(() -> {
            try {
                int totalWorkers = new com.dihadi.controller.WorkerController().getAllWorkers().size();
                int totalRecruiters = new com.dihadi.controller.RecruiterController().getAllRecruiters().size();
                java.util.List<com.dihadi.model.Project> allProjects = new com.dihadi.controller.ProjectController().getAllProjects();
                int totalProjects = allProjects.size();
                javafx.application.Platform.runLater(() -> {
                    ((Label)workers.getChildren().get(1)).setText(String.valueOf(totalWorkers));
                    ((Label)recruiters.getChildren().get(1)).setText(String.valueOf(totalRecruiters));
                    ((Label)projects.getChildren().get(1)).setText(String.valueOf(totalProjects));
                });
            } catch (Exception e) {}
        }).start();
        HBox row = new HBox(20, wallet, workers, recruiters, projects); 
        for(Node n:row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS); 
        return row; 
    }
    private VBox metric(String t,String v,String n){VBox p=panel(t,label(v,"-fx-font-family:Georgia;-fx-font-size:25px;-fx-font-weight:700;-fx-text-fill:#735c00;"),label(n,"-fx-font-size:13px;-fx-text-fill:#4c4637;"));p.setMinHeight(138);return p;}
    private HBox body(String company){
        VBox activeProjPanel = panel("ACTIVE PROJECTS", label("Loading...","-fx-font-size:16px;"));
        Label reqLabel = label("Loading...", "-fx-font-size:16px;-fx-font-weight:700;");
        Button viewReqs = action("VIEW APPROVALS", true);
        viewReqs.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) viewReqs.getScene().getWindow();
            javafx.scene.Scene currentScene = viewReqs.getScene();
            stage.setScene(new PendingApprovalsPage(recruiter).getScene(() -> stage.setScene(currentScene)));
        });
        VBox reqPanel = panel("PENDING APPROVAL REQUESTS", reqLabel, viewReqs);
        new Thread(() -> {
            try {
                java.util.List<com.dihadi.model.Project> allProjects = new com.dihadi.controller.ProjectController().getAllProjects();
                long myProjects = allProjects.stream().filter(p -> p.getMobile() != null && p.getMobile().equals(recruiter.getMobileNumber())).count();
                java.util.List<com.dihadi.model.JobApplication> apps = new com.dihadi.controller.JobApplicationController().getAllApplications();
                long myReqs = apps.stream().filter(a -> "Pending".equals(a.getStatus())).count(); // Simplified for now
                javafx.application.Platform.runLater(() -> {
                    ((Label)activeProjPanel.getChildren().get(1)).setText(myProjects == 0 ? "No active projects yet." : myProjects + " active project(s)");
                    ((Label)reqPanel.getChildren().get(1)).setText(myReqs == 0 ? "No pending worker approvals." : myReqs + " pending application(s)");
                });
            } catch (Exception e) {}
        }).start();
        VBox left=new VBox(20, activeProjPanel, panel("UPCOMING PROJECTS",label("Projects scheduled by your organisation will appear here.","-fx-font-size:14px;-fx-text-fill:#4c4637;")),panel("PAST COMPLETED PROJECTS",label("No completed project records yet.","-fx-font-size:14px;-fx-text-fill:#4c4637;")));
        VBox right=new VBox(20, reqPanel, panel("RECRUITER PROFILE",detail("Mobile",recruiter.getMobileNumber()),detail("Email",recruiter.getEmail()),detail("Business Type",recruiter.getBusinessType())));
        HBox h=new HBox(20,left,right);HBox.setHgrow(left,Priority.ALWAYS);HBox.setHgrow(right,Priority.ALWAYS);return h;
    }
    private HBox footer(Runnable back){Button b=action("BACK TO RECRUITER PAGE",true);b.setOnAction(e->{if(back!=null)back.run();});HBox h=new HBox(b);h.setAlignment(Pos.CENTER_RIGHT);return h;}
    private VBox panel(String title,Node...nodes){VBox v=new VBox(12,label(title,"-fx-font-size:12px;-fx-font-weight:800;-fx-letter-spacing:1.3px;-fx-text-fill:#685c52;"));v.getChildren().addAll(nodes);v.setPadding(new Insets(22));v.setStyle("-fx-background-color:#ffffff;-fx-background-radius:16px;-fx-border-color:#cfc6b2;-fx-border-radius:16px;-fx-effect:dropshadow(gaussian,rgba(34,34,34,.08),18,0,0,5px);");return v;}
    private Button action(String text,boolean filled){Button b=new Button(text);b.setStyle(filled?"-fx-background-color:#d4af37;-fx-background-radius:8px;-fx-text-fill:#222222;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:10px 16px;-fx-cursor:hand;":"-fx-background-color:#faf3e8;-fx-background-radius:8px;-fx-border-color:#cfc6b2;-fx-border-radius:8px;-fx-text-fill:#4c4637;-fx-font-size:12px;-fx-font-weight:700;-fx-padding:9px 15px;-fx-cursor:hand;");return b;}
    private Button nav(String text,boolean active){Button b=new Button(text);b.setStyle("-fx-background-color:transparent;-fx-padding:8px 2px;-fx-font-size:13px;-fx-font-weight:"+(active?"800":"500")+";-fx-text-fill:"+(active?"#735c00":"#4c4637")+";-fx-border-color:"+(active?"#735c00":"transparent")+";-fx-border-width:0 0 2px 0;-fx-cursor:hand;");return b;}
    private VBox detail(String k,String v){return new VBox(3,label(k,"-fx-font-size:11px;-fx-text-fill:#7e7665;"),label(val(v,"Not provided"),"-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"));}
    private boolean blank(String v){return v==null||v.isBlank();} private String val(String v,String fallback){return blank(v)?fallback:v;} private Label label(String v,String s){Label l=new Label(v);l.setWrapText(true);l.setStyle("-fx-font-family:'Segoe UI';"+s);return l;}
}