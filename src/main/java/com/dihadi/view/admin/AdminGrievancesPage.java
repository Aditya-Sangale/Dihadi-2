package com.dihadi.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/** Native grievance resolution command center recreated from the supplied design. */
public class AdminGrievancesPage {
    private static final String PRIMARY="#735c00", BG="#f3e7ce", SURFACE="#ffffff", BORDER="#d0c5af";
    private static final String[][] CASES={
            {"#GR-1024","Payment Delay","J. Doe (W-882)","Project Alpha","Critical","Investigating"},
            {"#GR-1025","Safety Breach","M. Smith (W-910)","Site Supervisor B","High","New"},
            {"#GR-1021","Contract Dispute","A. Garcia (W-442)","Recruiter Omega","Medium","Escalated"},
            {"#GR-1026","Equipment Damage","R. Chen (W-112)","Logistics Hub 4","Medium","New"},
            {"#GR-1027","Wage Mismatch","L. Thompson (W-554)","Payroll Dept","High","Investigating"},
            {"#GR-1028","Unfair Dismissal","K. Patel (W-229)","Project Beta","Critical","Escalated"},
            {"#GR-1029","Site Conduct","S. Miller (W-881)","Site Manager D","Medium","New"},
            {"#GR-1030","Overtime Dispute","B. Wilson (W-332)","Project Gamma","Medium","Investigating"},
            {"#GR-1031","Safety Violation","C. Lee (W-309)","Site A","High","New"},
            {"#GR-1032","Contract Breach","D. Martin (W-432)","Recruiter Delta","Medium","Investigating"},
            {"#GR-1033","Harassment Claim","E. White (W-772)","Project Alpha","Critical","Escalated"},
            {"#GR-1034","Payment Error","G. Harris (W-119)","Finance Team","High","New"},
            {"#GR-1035","Workplace Hazard","H. Clark (W-663)","Site B","Critical","Investigating"},
            {"#GR-1036","Shift Dispute","I. Lewis (W-441)","Scheduling Dept","Medium","New"},
            {"#GR-1037","Equipment Failure","J. Walker (W-221)","Maintenance Team","High","Investigating"},
            {"#GR-1038","Policy Violation","K. Young (W-559)","Project Delta","Critical","Escalated"}};

    public Scene getGrievancesScene(Runnable dashboardAction) {
        BorderPane root=new BorderPane(); root.setLeft(sidebar(dashboardAction)); root.setCenter(content());
        return new Scene(root,1400,780);
    }

    private VBox sidebar(Runnable dashboardAction) {
        ImageView logo=image("/assets/logo/dihadi logo.jpeg",42,42);
        VBox identity=new VBox(2,new HBox(9,logo,new VBox(2,label("DIHADI","-fx-font-size:16px;-fx-font-family:Georgia;-fx-font-weight:700;-fx-text-fill:"+PRIMARY+";"),label("Enterprise Admin","-fx-font-size:10px;-fx-text-fill:#4c4637;")))); identity.setPadding(new Insets(4,0,24,0));
        Button command=nav("Command Center",false);command.setOnAction(e->dashboardAction.run());
        VBox main=new VBox(6,command,nav("Workforce",false),nav("Financials",false),nav("Verification",false),nav("Grievances",true));VBox.setVgrow(main,Priority.ALWAYS);
        Button report=new Button("Generate Report");report.setOnAction(e->message("Generate Report","Grievance report generation is ready."));report.setMaxWidth(Double.MAX_VALUE);report.setStyle("-fx-background-color:"+PRIMARY+";-fx-background-radius:99px;-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:800;-fx-padding:10px;-fx-cursor:hand;");
        VBox bottom=new VBox(7,nav("Support",false),nav("Compliance",false),report);bottom.setPadding(new Insets(16,0,0,0));bottom.setStyle("-fx-border-color:"+BORDER+";-fx-border-width:1px 0 0 0;");
        VBox rail=new VBox(identity,main,bottom);rail.setPrefWidth(250);rail.setPadding(new Insets(28,16,26,16));rail.setStyle("-fx-background-color:#f4ede2;-fx-border-color:"+BORDER+";-fx-border-width:0 1px 0 0;");return rail;
    }

    private ScrollPane content() {
        VBox body=new VBox(25,heading(),summaries(),caseTable());body.setPadding(new Insets(44,48,44,48));body.setMaxWidth(1280);body.setStyle("-fx-background-color:"+BG+";");
        ScrollPane scroll=new ScrollPane(body);scroll.setFitToWidth(true);scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);scroll.setStyle("-fx-background:transparent;-fx-background-color:"+BG+";-fx-border-width:0;");return scroll;
    }
    private VBox heading(){return new VBox(7,label("Grievance Resolution Command Center","-fx-font-family:Georgia;-fx-font-size:31px;-fx-text-fill:"+PRIMARY+";"),label("Manage, investigate, and resolve workforce disputes.","-fx-font-size:14px;-fx-text-fill:#4c4637;"));}
    private GridPane summaries(){GridPane grid=grid(4);grid.setHgap(18);grid.add(summary("TOTAL OPEN CASES","162","+12%","#685c52"),0,0);grid.add(summary("PENDING INVESTIGATION","48","Requiring Action","#d4af37"),1,0);grid.add(summary("HIGH PRIORITY DISPUTES","12","Escalated","#ba1a1a"),2,0);grid.add(summary("RESOLVED (30 DAYS)","324","+5%","#415ba4"),3,0);return grid;}
    private VBox summary(String title,String number,String note,String accent){VBox card=new VBox(12,label(title,"-fx-font-size:11px;-fx-font-weight:800;-fx-letter-spacing:.6px;-fx-text-fill:#4c4637;"),new HBox(9,label(number,"-fx-font-family:Georgia;-fx-font-size:31px;-fx-text-fill:#1e1b15;"),label(note,"-fx-font-size:11px;-fx-font-weight:700;-fx-text-fill:"+accent+";")));card.setPadding(new Insets(20));card.setPrefHeight(115);card.setStyle("-fx-background-color:"+SURFACE+";-fx-background-radius:12px;-fx-border-color:"+BORDER+";-fx-border-width:0 0 0 4px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),8,.12,0,2px);");return card;}

    private VBox caseTable() {
        Button filter=small("Filter");filter.setOnAction(e->message("Filter Cases","Filter controls are ready to connect."));Button sort=small("Sort");sort.setOnAction(e->message("Sort Cases","Cases are currently ordered by recent activity."));
        HBox title=new HBox(label("Active Grievances","-fx-font-size:20px;-fx-font-weight:700;-fx-text-fill:"+PRIMARY+";"),spacer(),filter,sort);title.setAlignment(Pos.CENTER_LEFT);title.setPadding(new Insets(18,20,18,20));title.setStyle("-fx-background-color:#faf3e8;-fx-border-color:"+BORDER+";-fx-border-width:0 0 1px 0;");
        GridPane header=gridColumns();header.setPadding(new Insets(13,20,13,20));header.setStyle("-fx-background-color:#faf3e8;-fx-border-color:"+BORDER+";-fx-border-width:0 0 1px 0;");header.add(label("CASE ID","-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#4c4637;"),0,0);header.add(label("SUBJECT / DETAILS","-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#4c4637;"),1,0);header.add(label("PRIORITY / STATUS","-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#4c4637;"),2,0);header.add(label("ACTIONS","-fx-font-size:11px;-fx-font-weight:800;-fx-text-fill:#4c4637;"),3,0);
        VBox rows=new VBox();for(String[] row:CASES)rows.getChildren().add(caseRow(row));
        Button all=new Button("View All Cases");all.setOnAction(e->message("All Cases","All available grievance cases are displayed."));all.setStyle("-fx-background-color:transparent;-fx-text-fill:"+PRIMARY+";-fx-font-size:12px;-fx-font-weight:800;-fx-cursor:hand;");HBox footer=new HBox(all);footer.setAlignment(Pos.CENTER);footer.setPadding(new Insets(12));footer.setStyle("-fx-background-color:#faf3e8;-fx-border-color:"+BORDER+";-fx-border-width:1px 0 0 0;");
        VBox table=new VBox(title,header,rows,footer);table.setMaxWidth(1050);table.setStyle("-fx-background-color:"+SURFACE+";-fx-background-radius:12px;-fx-border-color:"+BORDER+";-fx-border-radius:12px;-fx-effect:dropshadow(gaussian,rgba(58,48,39,.08),10,.12,0,2px);");return table;
    }
    private GridPane caseRow(String[] c){GridPane row=gridColumns();row.setPadding(new Insets(15,20,15,20));row.setStyle("-fx-border-color:#e9e2d7;-fx-border-width:0 0 1px 0;");row.add(label(c[0],"-fx-font-size:13px;-fx-font-weight:800;-fx-text-fill:"+PRIMARY+";"),0,0);VBox details=new VBox(4,label(c[1],"-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1e1b15;"),label("Filed by: "+c[2],"-fx-font-size:12px;-fx-text-fill:#4c4637;"),label("Against: "+c[3],"-fx-font-size:12px;-fx-text-fill:#4c4637;"));row.add(details,1,0);VBox tags=new VBox(6,tag(c[4],priorityColor(c[4])),tag(c[5],statusColor(c[5])));row.add(tags,2,0);HBox actions=new HBox(4,action("View",c[0]),action("Assign",c[0]),action("Resolve",c[0]));actions.setAlignment(Pos.CENTER_RIGHT);row.add(actions,3,0);return row;}
    private Label tag(String value,String color){return label(value,"-fx-background-color:"+color+"22;-fx-background-radius:99px;-fx-text-fill:"+color+";-fx-font-size:10px;-fx-font-weight:800;-fx-padding:4px 9px;");}
    private String priorityColor(String value){return value.equals("Critical")?"#ba1a1a":value.equals("High")?"#735c00":"#685c52";}
    private String statusColor(String value){return value.equals("Escalated")?"#ba1a1a":value.equals("New")?"#27438a":"#685c52";}
    private Button action(String label,String id){Button button=small(label);button.setOnAction(e->message(label+" "+id,"Action selected for grievance "+id+"."));return button;}
    private Button small(String caption){Button button=new Button(caption);button.setStyle("-fx-background-color:transparent;-fx-text-fill:"+PRIMARY+";-fx-font-size:11px;-fx-font-weight:800;-fx-padding:6px 7px;-fx-cursor:hand;");return button;}
    private Button nav(String caption,boolean active){Button button=new Button(caption);button.setMaxWidth(Double.MAX_VALUE);button.setAlignment(Pos.CENTER_LEFT);button.setStyle("-fx-background-color:"+(active?PRIMARY:"transparent")+";-fx-background-radius:8px;-fx-text-fill:"+(active?"#f6d676":"#4c4637")+";-fx-font-size:13px;-fx-font-weight:"+(active?"800":"600")+";-fx-padding:12px 13px;-fx-cursor:hand;");if(!active)button.setOnAction(e->message(caption,caption+" module is ready to connect."));return button;}
    private GridPane grid(int columns){GridPane grid=new GridPane();for(int i=0;i<columns;i++){ColumnConstraints col=new ColumnConstraints();col.setPercentWidth(100.0/columns);col.setHgrow(Priority.ALWAYS);grid.getColumnConstraints().add(col);}return grid;}
    private GridPane gridColumns(){GridPane grid=new GridPane();double[] columns={15,43,22,20};for(double percent:columns){ColumnConstraints col=new ColumnConstraints();col.setPercentWidth(percent);col.setHgrow(Priority.ALWAYS);grid.getColumnConstraints().add(col);}return grid;}
    private ImageView image(String path,double width,double height){ImageView view=new ImageView(new Image(getClass().getResource(path).toExternalForm()));view.setFitWidth(width);view.setFitHeight(height);view.setPreserveRatio(true);return view;}
    private Label label(String text,String style){Label label=new Label(text);label.setStyle("-fx-font-family:'Segoe UI',sans-serif;"+style);return label;}
    private Region spacer(){Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);return spacer;}
    private void message(String title,String copy){Alert alert=new Alert(Alert.AlertType.INFORMATION);alert.setTitle(title);alert.setHeaderText(null);alert.setContentText(copy);alert.show();}
}
