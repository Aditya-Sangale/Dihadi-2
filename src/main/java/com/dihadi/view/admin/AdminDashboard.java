package com.dihadi.view.admin;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/** Exact native layout of the supplied DIHADI Admin Command Center dashboard. */
public class AdminDashboard {
    private static final String DARK="#272727", GOLD="#D4AF37", MAIN="#f3e7ce", BORDER="#E0D9CE";
    private Timeline clock;

    public Scene getDashboardScene(Runnable logout) {
        BorderPane root=new BorderPane(); root.setLeft(sidebar(logout)); root.setCenter(main());
        return new Scene(root,1400,780);
    }

    private VBox sidebar(Runnable logout) {
        ImageView logo=image("/assets/logo/dihadi logo.jpeg",82,82);
        VBox identity=new VBox(10,logo,label("DIHADI","-fx-font-family:Georgia;-fx-font-size:28px;-fx-text-fill:"+GOLD+";"),label("ADMIN CONTROL CENTER","-fx-font-size:11px;-fx-letter-spacing:1.2px;-fx-text-fill:#dcdad4;"));
        identity.setAlignment(Pos.CENTER); identity.setPadding(new Insets(28,10,35,10));
        Button grievances = nav("Grievances", false);
        grievances.setOnAction(e -> {
            if (clock != null) clock.stop();
            javafx.stage.Stage stage = (javafx.stage.Stage) grievances.getScene().getWindow();
            stage.setScene(new AdminGrievancesPage().getGrievancesScene(
                    () -> stage.setScene(getDashboardScene(logout))));
        });

        Button workersNav = nav("Workers", false);
        workersNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            javafx.stage.Stage stage = (javafx.stage.Stage) workersNav.getScene().getWindow();
            stage.setScene(new AdminWorkersPage().getWorkersScene(
                    () -> stage.setScene(getDashboardScene(logout)),
                    logout));
        });

        Button recruitersNav = nav("Recruiters", false);
        recruitersNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            javafx.stage.Stage stage = (javafx.stage.Stage) recruitersNav.getScene().getWindow();
            stage.setScene(new AdminRecruitersPage().getRecruitersScene(
                    () -> stage.setScene(getDashboardScene(logout)),
                    logout));
        });

        Button projectsNav = nav("Projects", false);
        projectsNav.setOnAction(e -> {
            if (clock != null) clock.stop();
            javafx.stage.Stage stage = (javafx.stage.Stage) projectsNav.getScene().getWindow();
            stage.setScene(new AdminProjectsPage().getProjectsScene(
                    () -> stage.setScene(getDashboardScene(logout)),
                    logout));
        });

        VBox links=new VBox(4,nav("Command Center",true),workersNav,recruitersNav,projectsNav,nav("Financials",false),nav("Verification",false),grievances);
        VBox.setVgrow(links,Priority.ALWAYS);
        Button profile=nav("Admin User\nSystem Administrator",false); profile.setOnAction(e->logout.run());
        VBox bottom=new VBox(4,nav("Support",false),nav("Compliance",false),profile); bottom.setPadding(new Insets(14,0,14,0)); bottom.setStyle("-fx-border-color:#ffffff1a;-fx-border-width:1px 0 0 0;");
        VBox bar=new VBox(identity,links,bottom); bar.setPrefWidth(312); bar.setMinWidth(312); bar.setStyle("-fx-background-color:"+DARK+";"); return bar;
    }

    private BorderPane main() {
        HBox breadcrumb=new HBox(label("Admin","-fx-font-size:16px;-fx-text-fill:#1A1A1A;"),label("   >   ","-fx-font-size:16px;-fx-text-fill:#4A4A4A;"),label("Dashboard","-fx-font-size:16px;-fx-text-fill:"+GOLD+";"));
        breadcrumb.setAlignment(Pos.CENTER_LEFT); breadcrumb.setPadding(new Insets(0,40,0,40)); breadcrumb.setPrefHeight(80); breadcrumb.setStyle("-fx-background-color:"+MAIN+";-fx-border-color:"+BORDER+"80;-fx-border-width:0 0 1px 0;");
        VBox content=new VBox(28,heading(),metrics(),analytics(),operations()); content.setPadding(new Insets(42,40,58,40)); content.setMaxWidth(1280);
        ScrollPane scroll=new ScrollPane(content); scroll.setFitToWidth(true); scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); scroll.setStyle("-fx-background:transparent;-fx-background-color:"+MAIN+";-fx-border-width:0;");
        BorderPane page=new BorderPane(scroll); page.setTop(breadcrumb); page.setBottom(status()); page.setStyle("-fx-background-color:"+MAIN+";"); return page;
    }

    private VBox heading(){return new VBox(7,label("Command Center Overview","-fx-font-family:Georgia;-fx-font-size:38px;-fx-text-fill:#1A1A1A;"),label("Real-time ecosystem metrics and operational status.","-fx-font-size:16px;-fx-text-fill:#4A4A4A;"));}
    private GridPane metrics(){GridPane grid=grid(3);grid.setHgap(28);grid.add(kpi("TOTAL WORKERS","12,450","workers",new String[][]{{"Active (Verified)","8,200"},{"Pending Verification","3,150"},{"Inactive","1,100"}}),0,0);grid.add(kpi("TOTAL RECRUITERS","1,840","briefcase",new String[][]{{"Contractors","1,200"},{"Individual Employers","540"},{"Agencies","100"}}),1,0);grid.add(kpi("ACTIVE PROJECTS","740","project",new String[][]{{"Construction","450"},{"Manufacturing","180"},{"Logistics","110"}}),2,0);return grid;}
    private VBox kpi(String heading,String number,String icon,String[][] rows){VBox details=new VBox(15);for(int i=0;i<rows.length;i++)details.getChildren().add(detail(rows[i][0],rows[i][1],i<rows.length-1));HBox top=new HBox(label(heading,"-fx-font-size:14px;-fx-font-weight:800;-fx-letter-spacing:2px;-fx-text-fill:"+GOLD+";"),spacer(),icon(icon));VBox card=new VBox(22,top,label(number,"-fx-font-family:Georgia;-fx-font-size:34px;-fx-text-fill:#1A1A1A;"),details);card.setPadding(new Insets(29));card.setPrefHeight(294);card.setStyle(card());return card;}
    private HBox detail(String name,String value,boolean line){HBox row=new HBox(label(name,"-fx-font-size:15px;-fx-text-fill:#4A4A4A;"),spacer(),label(value,"-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;"));row.setPadding(new Insets(0,0,10,0));if(line)row.setStyle("-fx-border-color:"+BORDER+"80;-fx-border-width:0 0 1px 0;");return row;}

    private GridPane analytics(){GridPane grid=grid(2);grid.setHgap(28);VBox workforce=new VBox(25,label("Workforce Status Distribution",title()),pieChart());workforce.setPadding(new Insets(29));workforce.setPrefHeight(425);workforce.setStyle(card());VBox heatmap=new VBox(21,label("Skill Demand Heatmap (Top 5 Trades)",title()),progress("Masonry",85),progress("Carpentry",72),progress("Plumbing",68),progress("Electrical",60),progress("Painting",45));heatmap.setPadding(new Insets(29));heatmap.setPrefHeight(425);heatmap.setStyle(card());grid.add(workforce,0,0);grid.add(heatmap,1,0);return grid;}
    private HBox pieChart(){Circle inactive=new Circle(75,Color.web(BORDER));Arc active=arc(75,0,-234,GOLD),pending=arc(75,-234,-90,"#4A4A4A");StackPane chart=new StackPane(inactive,active,pending);chart.setPrefSize(170,170);VBox legend=new VBox(21,legend(GOLD,"Active (65%)"),legend("#4A4A4A","Pending (25%)"),legend(BORDER,"Inactive (10%)"));HBox row=new HBox(42,chart,legend);row.setAlignment(Pos.CENTER);return row;}
    private Arc arc(double r,double start,double length,String color){Arc arc=new Arc(r,r,r,r,start,length);arc.setType(ArcType.ROUND);arc.setFill(Color.web(color));return arc;}
    private HBox legend(String color,String caption){HBox row=new HBox(10,new Circle(7,Color.web(color)),label(caption,"-fx-font-size:15px;-fx-text-fill:#4A4A4A;"));row.setAlignment(Pos.CENTER_LEFT);return row;}
    private VBox progress(String name,int value){HBox names=new HBox(label(name,"-fx-font-size:16px;-fx-font-weight:700;-fx-text-fill:#1A1A1A;"),spacer(),label(value+"%","-fx-font-size:15px;-fx-text-fill:#4A4A4A;"));Region track=new Region();track.setPrefHeight(10);track.setMaxWidth(Double.MAX_VALUE);track.setStyle("-fx-background-color:"+BORDER+"80;-fx-background-radius:99px;");Region fill=new Region();fill.setPrefHeight(10);fill.setStyle("-fx-background-color:"+GOLD+";-fx-background-radius:99px;");fill.prefWidthProperty().bind(track.widthProperty().multiply(value/100.0));StackPane bar=new StackPane(track,fill);StackPane.setAlignment(fill,Pos.CENTER_LEFT);return new VBox(8,names,bar);}

    private GridPane operations(){GridPane grid=grid(2);grid.setHgap(28);HBox miniRow=new HBox(15,mini("Processed","\u20B915.2 L"),mini("Pending","\u20B93.3 L"));miniRow.setAlignment(Pos.CENTER);VBox money=new VBox(12,label("Today's Financial Overview",title()),label("Total Transaction Volume","-fx-font-size:15px;-fx-text-fill:#4A4A4A;"),label("\u20B918.5 Lakhs","-fx-font-family:Georgia;-fx-font-size:42px;-fx-text-fill:"+GOLD+";"),miniRow);money.setAlignment(Pos.CENTER);money.setPadding(new Insets(30));money.setPrefHeight(285);money.setStyle(card());VBox alerts=new VBox(16,label("Requires Immediate Attention",title()),alert("12 Grievances Escalated","Pending resolution over 48 hours.","View","#fff0f0","#d82828"),alert("85 Pending Verifications","KYC documents require manual review.","Review","#fff6e7","#c7780e"),alert("System Update Scheduled","Maintenance window at 02:00 AM IST.","Details","#edf5ff","#2474b5"));alerts.setPadding(new Insets(29));alerts.setPrefHeight(285);alerts.setStyle(card());grid.add(money,0,0);grid.add(alerts,1,0);return grid;}
    private VBox mini(String title,String number){VBox box=new VBox(6,label(title,"-fx-font-size:12px;-fx-text-fill:#4A4A4A;"),label(number,"-fx-font-size:17px;-fx-font-weight:800;"));box.setAlignment(Pos.CENTER);box.setPrefWidth(185);box.setPadding(new Insets(12));box.setStyle("-fx-background-color:"+MAIN+";-fx-background-radius:8px;-fx-border-color:"+BORDER+"80;-fx-border-radius:8px;");return box;}
    private HBox alert(String heading,String detail,String action,String background,String color){VBox text=new VBox(4,label(heading,"-fx-font-size:14px;-fx-font-weight:800;-fx-text-fill:#1A1A1A;"),label(detail,"-fx-font-size:12px;-fx-text-fill:#4A4A4A;"));Button button=new Button(action);button.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,heading).show());button.setStyle("-fx-background-color:transparent;-fx-text-fill:"+color+";-fx-font-size:12px;-fx-font-weight:800;-fx-cursor:hand;");HBox row=new HBox(12,new Circle(5,Color.web(color)),text,spacer(),button);row.setAlignment(Pos.CENTER_LEFT);row.setPadding(new Insets(10));row.setStyle("-fx-background-color:"+background+";-fx-background-radius:8px;-fx-border-color:"+color+"33;-fx-border-radius:8px;");return row;}

    private HBox status(){Label time=label("","-fx-font-family:Consolas;-fx-font-size:11px;-fx-text-fill:#dedbd5;");clock=new Timeline(new KeyFrame(Duration.ZERO,e->time.setText("System Time: "+ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("hh:mm:ss a 'IST'")))),new KeyFrame(Duration.seconds(1)));clock.setCycleCount(Timeline.INDEFINITE);clock.play();HBox footer=new HBox(28,health("Database: OK"),health("SMS Gateway: OK"),health("Maps API: OK"),spacer(),time);footer.setAlignment(Pos.CENTER_LEFT);footer.setPadding(new Insets(0,28,0,28));footer.setPrefHeight(36);footer.setStyle("-fx-background-color:"+DARK+";");return footer;}
    private HBox health(String value){HBox item=new HBox(8,new Circle(4,Color.web("#22c55e")),label(value,"-fx-font-family:Consolas;-fx-font-size:11px;-fx-text-fill:#dedbd5;"));item.setAlignment(Pos.CENTER_LEFT);return item;}
    private Button nav(String title,boolean active){Button button=new Button(title);button.setMaxWidth(Double.MAX_VALUE);button.setAlignment(Pos.CENTER_LEFT);button.setStyle("-fx-background-color:"+(active?GOLD:"transparent")+";-fx-background-radius:9px;-fx-text-fill:"+(active?"white":"#dcdad4")+";-fx-font-size:16px;-fx-font-weight:"+(active?"800":"500")+";-fx-padding:16px 28px;-fx-cursor:hand;");if(!active)button.setOnAction(e->new Alert(Alert.AlertType.INFORMATION,title+" module is ready to connect.").show());return button;}
    private StackPane icon(String name){String svg=name.equals("workers")?"M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zM8 11c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C15 14.17 10.33 13 8 13z":name.equals("briefcase")?"M20 6h-4V4c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10-2h4v2h-4V4zm10 14H4v-5h6v1h4v-1h6v5z":"M10 2h4v2h5v18H5V4h5V2zm0 4H7v14h10V6h-3v2h-4V6z";SVGPath path=new SVGPath();path.setContent(svg);path.setFill(Color.web(GOLD,0.28));path.setScaleX(1.25);path.setScaleY(1.25);return new StackPane(path);}
    private GridPane grid(int columns){GridPane grid=new GridPane();for(int i=0;i<columns;i++){ColumnConstraints col=new ColumnConstraints();col.setPercentWidth(100.0/columns);col.setHgrow(Priority.ALWAYS);grid.getColumnConstraints().add(col);}return grid;}
    private ImageView image(String path,double width,double height){ImageView view=new ImageView(new Image(getClass().getResource(path).toExternalForm()));view.setFitWidth(width);view.setFitHeight(height);view.setPreserveRatio(true);return view;}
    private Label label(String value,String style){Label label=new Label(value);label.setStyle("-fx-font-family:'Segoe UI',sans-serif;"+style);return label;}
    private Region spacer(){Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);return spacer;}
    private String title(){return "-fx-font-family:Georgia;-fx-font-size:25px;-fx-text-fill:#1A1A1A;";}
    private String card(){return "-fx-background-color:#FFFFFF;-fx-background-radius:14px;-fx-border-color:"+BORDER+";-fx-border-radius:14px;-fx-effect:dropshadow(gaussian,rgba(0,0,0,.05),6,.1,0,2px);";}
}
