package com.dihadi.view.recruiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Final project summary shown after workforce requirements are added. */
public class ProjectDetailsPage {
    private final String name, contact, mobile, email, address, priority, type, skill, workers, wage, imagePath, facilities;
    public ProjectDetailsPage(String name,String contact,String mobile,String email,String address,String priority,String type,String skill,String workers,String wage,String imagePath,String facilities){this.name=name;this.contact=contact;this.mobile=mobile;this.email=email;this.address=address;this.priority=priority;this.type=type;this.skill=skill;this.workers=workers;this.wage=wage;this.imagePath=imagePath;this.facilities=facilities;}
    public Scene getScene(Runnable exit){
        ImageView image=new ImageView(load(imagePath.isBlank()?"/assets/images/recruiter/slide-03.jpeg":imagePath)); image.setFitWidth(400); image.setFitHeight(260); image.setPreserveRatio(false);
        VBox media=new VBox(10,label("Site Images","-fx-font-family:Georgia;-fx-font-size:20px;-fx-text-fill:#685c52;"),image); media.setPadding(new Insets(20)); media.setStyle(box());
        VBox overview=new VBox(10,heading("Project & Contact Overview"),detail("Project Name",name),detail("Contact Person",contact),detail("Contact Number",mobile),detail("Email",email)); overview.setPadding(new Insets(20)); overview.setStyle(box());
        VBox addressBox=new VBox(10,heading("Site Address & Location"),detail("Address",address)); addressBox.setPadding(new Insets(20)); addressBox.setStyle(box());
        Label urgency=label(priority,"-fx-background-color:#ffdad6;-fx-background-radius:999px;-fx-padding:7px 12px;-fx-text-fill:#93000a;-fx-font-weight:700;");
        VBox workforce=new VBox(12,heading("Workforce & Wage Requirement"),new HBox(12,detail("Worker Type",type),detail("Sub Skill",skill),detail("Workers Needed",workers),detail("Daily Wage", "Rs. "+wage+" / day")),urgency,detail("Facilities Provided",facilities)); workforce.setPadding(new Insets(20)); workforce.setStyle(box());
        Button exitButton=new Button("EXIT"); exitButton.setStyle("-fx-background-color:transparent;-fx-border-color:#d4af37;-fx-border-width:2px;-fx-border-radius:999px;-fx-background-radius:999px;-fx-text-fill:#735c00;-fx-font-weight:700;-fx-padding:10px 28px;"); exitButton.setOnAction(e->{if(exit!=null)exit.run();});
        HBox footer=new HBox(exitButton); footer.setAlignment(Pos.CENTER_RIGHT); footer.setPadding(new Insets(18,0,0,0));
        VBox content=new VBox(22,new VBox(4,heading("Project Details Summary"),label("Your project and workforce requirement are ready.","-fx-text-fill:#4c4637;")),new HBox(18,overview,media),addressBox,workforce,footer); content.setPadding(new Insets(28)); content.setMaxWidth(1050); ScrollPane scroll=new ScrollPane(content);scroll.setFitToWidth(true);scroll.setStyle("-fx-background:#fff8f0;-fx-background-color:#fff8f0;"); StackPane card=new StackPane(scroll); card.setMaxSize(1080,680); card.setStyle("-fx-background-color:#fffdf9;-fx-background-radius:20px;-fx-border-color:#d0c5af;-fx-border-radius:20px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.18),26,0,0,8px);"); StackPane root=new StackPane(card); root.setPadding(new Insets(28)); root.setStyle("-fx-background-color:#e9e2d7;"); Scene scene=new Scene(root,1120,740);scene.windowProperty().addListener((o,a,w)->{if(w instanceof Stage s){s.setMinWidth(980);s.setMinHeight(680);}});return scene;
    }
    private Label heading(String s){return label(s,"-fx-font-family:Georgia;-fx-font-size:22px;-fx-font-weight:700;-fx-text-fill:#685c52;");}
    private VBox detail(String k,String v){return new VBox(3,label(k,"-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:#7e7665;"),label(v==null||v.isBlank()?"Not provided":v,"-fx-font-size:15px;-fx-font-weight:600;-fx-text-fill:#1e1b15;"));}
    private Label label(String s,String st){Label l=new Label(s);l.setWrapText(true);l.setStyle("-fx-font-family:'Segoe UI';"+st);return l;}
    private String box(){return "-fx-background-color:#f4ede2;-fx-background-radius:10px;-fx-border-color:#e9e2d7;-fx-border-radius:10px;";}
    private Image load(String p){var r=getClass().getResource(p);return r==null?null:new Image(r.toExternalForm());}
}