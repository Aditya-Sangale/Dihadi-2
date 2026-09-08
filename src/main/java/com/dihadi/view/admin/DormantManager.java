package com.dihadi.view.admin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Centralized Manager for the Dormant Category in the Admin Portal.
 * Handles archiving, retrieval, deletion, and restoration of dormant cards across
 * Workers, Recruiters, Projects, and Grievances.
 */
public class DormantManager {
    private static final DormantManager INSTANCE = new DormantManager();

    public static DormantManager getInstance() {
        return INSTANCE;
    }

    public record DormantItem(
            String id,
            String category,         // "Worker", "Recruiter", "Project", "Grievance"
            String title,            // Name / Company / Project / Subject
            String subtitle,         // Trade / Director / Contractor / Complainant
            String identifierText,   // Phone / ID / Code
            String location,         // City / State / Site
            String highlight1Title,  // e.g. "DAILY RATE", "ACTIVE SITES", "WORKFORCE", "CLAIM VALUE"
            String highlight1Val,
            String highlight2Title,  // e.g. "EXPERIENCE", "HIRED WORKFORCE", "BUDGET", "FILED"
            String highlight2Val,
            String dateDeleted,      // Formatted timestamp
            String detailsSummary,   // Expanded statement for inspection modal
            Object rawObject         // Reference to source data record
    ) {}

    private final List<DormantItem> dormantList = new CopyOnWriteArrayList<>();
    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();

    private DormantManager() {
        seedInitialDormantRecords();
    }

    public void addListener(Runnable listener) {
        if (listener != null) {
            changeListeners.add(listener);
        }
    }

    public void removeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    private void notifyListeners() {
        for (Runnable r : changeListeners) {
            try {
                r.run();
            } catch (Exception ignored) {}
        }
    }

    public List<DormantItem> getAllDormantItems() {
        return Collections.unmodifiableList(new ArrayList<>(dormantList));
    }

    public void removeDormantItem(String id) {
        dormantList.removeIf(item -> item.id().equals(id));
        notifyListeners();
    }

    private String nowFormatted() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    public void addDormantWorker(AdminWorkersPage.AdminWorkerData w) {
        if (w == null) return;
        // Avoid duplicates
        dormantList.removeIf(item -> item.id().equals(w.mobileNumber()));

        String summary = "Worker Profile: " + w.fullName() + "\n"
                + "Trade: " + w.trade() + " (" + w.subSkill() + ")\n"
                + "Wage: " + w.wage() + " | Experience: " + w.experience() + "\n"
                + "Location: " + w.location() + "\n"
                + "Rating: ★ " + w.rating() + " (" + w.completedJobs() + " completed jobs)\n"
                + "Account status when archived: " + (w.isInactive() ? "Inactive (" + w.daysInactive() + " days)" : "Active") + "\n"
                + "Phone: " + w.mobileNumber() + " | Alternate: " + w.alternateMobile();

        DormantItem item = new DormantItem(
                w.mobileNumber(),
                "Worker",
                w.fullName(),
                w.trade() + " (" + w.subSkill() + ")",
                "Mobile: " + w.mobileNumber(),
                w.location(),
                "DAILY RATE",
                w.wage(),
                "EXPERIENCE",
                w.experience(),
                nowFormatted(),
                summary,
                w
        );
        dormantList.add(0, item);
        notifyListeners();
    }

    public void addDormantRecruiter(AdminRecruitersPage.AdminRecruiterData r) {
        if (r == null) return;
        dormantList.removeIf(item -> item.id().equals(r.mobileNumber()));

        String summary = "Recruiter Organization: " + r.companyName() + "\n"
                + "Director: " + r.contactPerson() + " | Region: " + r.region() + "\n"
                + "Mobile: " + r.mobileNumber() + " | Email: " + r.email() + "\n"
                + "Business Type: " + r.businessType() + " | Verification: " + r.verificationStatus() + "\n"
                + "Workforce History: " + r.workersHired() + " Personnel Across " + r.activeSites() + " Projects\n"
                + "Escrow Balance: ₹" + String.format("%,d", (long) r.walletBalance()) + "\n"
                + "Account status when archived: " + (r.isInactive() ? "Inactive (" + r.daysInactive() + " days)" : "Active");

        DormantItem item = new DormantItem(
                r.mobileNumber(),
                "Recruiter",
                r.companyName(),
                r.contactPerson() + " • " + r.region(),
                "Phone: " + r.mobileNumber(),
                r.region(),
                "ACTIVE SITES",
                r.activeSites() + " Sites",
                "WORKFORCE",
                r.workersHired() + " Hired",
                nowFormatted(),
                summary,
                r
        );
        dormantList.add(0, item);
        notifyListeners();
    }

    public void addDormantProject(AdminProjectsPage.AdminProjectData p) {
        if (p == null) return;
        dormantList.removeIf(item -> item.id().equals(p.projectId()));

        String summary = "Project Title: " + p.projectName() + " [#" + p.projectId() + "]\n"
                + "Contractor: " + p.company() + " | Sector: " + p.sector() + "\n"
                + "Site Location: " + p.location() + "\n"
                + "Trade: " + p.trade() + " | Wage: " + p.wage() + "\n"
                + "Workforce Required: " + p.workersNeeded() + " Workers (" + p.workerQuantity() + " Headcount)\n"
                + "Project Status: " + p.status() + " | Lifecycle: " + p.lifecycle() + "\n"
                + "Contact Phone: " + p.recruiterPhone() + " | Supervisor: " + p.supervisorName();

        DormantItem item = new DormantItem(
                p.projectId(),
                "Project",
                p.projectName(),
                p.company() + " • " + p.sector(),
                "Project ID: #" + p.projectId(),
                p.location(),
                "WAGE RATE",
                p.wage(),
                "WORKFORCE",
                p.workersNeeded(),
                nowFormatted(),
                summary,
                p
        );
        dormantList.add(0, item);
        notifyListeners();
    }

    public void addDormantGrievance(AdminGrievancesPage.AdminGrievanceData g) {
        if (g == null) return;
        dormantList.removeIf(item -> item.id().equals(g.caseId()));

        String summary = "Grievance Case: #" + g.caseId() + "\n"
                + "Subject: " + g.subject() + "\n"
                + "Complainant: " + g.complainant() + " | Site: " + g.project() + "\n"
                + "Category: " + g.category() + " | Priority: " + g.priority() + "\n"
                + "Claim Value: " + g.disputeAmount() + " | Location: " + g.location() + "\n"
                + "Incident Statement: " + g.description() + "\n"
                + "Contractor Response: " + g.contractorResponse();

        DormantItem item = new DormantItem(
                g.caseId(),
                "Grievance",
                g.subject(),
                "Case #" + g.caseId() + " • " + g.complainant(),
                "Complainant: " + g.complainant(),
                g.location(),
                "CLAIM VALUE",
                g.disputeAmount(),
                "PRIORITY",
                g.priority(),
                nowFormatted(),
                summary,
                g
        );
        dormantList.add(0, item);
        notifyListeners();
    }

    /**
     * Seeds realistic sample archived cards so the Dormant category is immediately rich and functional.
     */
    private void seedInitialDormantRecords() {
        dormantList.add(new DormantItem(
                "9822998811",
                "Worker",
                "Jagdish M. Solanki",
                "Shuttering Carpenter (Formwork Specialist)",
                "Mobile: 9822998811",
                "Chakan, Pune",
                "DAILY RATE",
                "₹820/day",
                "EXPERIENCE",
                "9 Years",
                "02 Sep 2026, 03:45 PM",
                "Inactive worker archived under the 30-day administrative inactivity policy.\nLast recorded shift: Pune Ring Road Flyover Sector 4.",
                null
        ));

        dormantList.add(new DormantItem(
                "9822774433",
                "Recruiter",
                "Vanguard Heavy Infra Pvt Ltd",
                "Rajiv Kulkarni • PCMC Pune",
                "Phone: 9822774433",
                "Pimpri-Chinchwad, Pune",
                "ACTIVE SITES",
                "0 Sites",
                "WORKFORCE",
                "18 Hired",
                "28 Aug 2026, 11:20 AM",
                "Contractor organization moved to dormant following completion of their toll plaza concession agreement.",
                null
        ));

        dormantList.add(new DormantItem(
                "PRJ-ARCH-08",
                "Project",
                "Industrial Logistics Warehouse Bay 4",
                "Kalyani Logistics • Commercial",
                "Project ID: #PRJ-ARCH-08",
                "Talegaon Dabhade, Pune",
                "BUDGET",
                "₹18,50,000",
                "COMPLETION",
                "100%",
                "24 Aug 2026, 06:15 PM",
                "Project successfully delivered and commissioned. Moved to dormant archives by Site Administrator.",
                null
        ));

        dormantList.add(new DormantItem(
                "GRV-904",
                "Grievance",
                "Dispute on Monsoon Rain Overhead Allowances",
                "Case #GRV-904 • Kailash Yadav",
                "Complainant: Kailash Yadav",
                "Hinjawadi Phase 3, Pune",
                "CLAIM VALUE",
                "₹14,200",
                "PRIORITY",
                "Medium",
                "19 Aug 2026, 02:10 PM",
                "Arbitration concluded. Settlement disbursed directly to workforce bank accounts.",
                null
        ));
    }

    /**
     * Creates a standardized dustbin button for cards across all categories.
     * Replaces standard delete/remove text buttons with a sleek, interactive vector trash icon button.
     *
     * @param tooltipText Descriptive tooltip
     * @param onAction Action to execute when clicked
     * @return Fully styled JavaFX Button
     */
    public static Button createDustbinButton(String tooltipText, Runnable onAction) {
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        trashIcon.setFill(Color.web("#ba1a1a"));
        trashIcon.setScaleX(0.78);
        trashIcon.setScaleY(0.78);

        Button btn = new Button();
        btn.setGraphic(trashIcon);
        btn.setAlignment(Pos.CENTER);
        btn.setTooltip(new Tooltip(tooltipText != null ? tooltipText : "Delete / Move to Dormant"));

        String normalStyle = "-fx-background-color:#ffebee;-fx-background-radius:8px;-fx-border-color:#ffcdd2;-fx-border-width:1.2px;-fx-border-radius:8px;-fx-padding:6px 11px;-fx-cursor:hand;";
        String hoverStyle = "-fx-background-color:#ba1a1a;-fx-background-radius:8px;-fx-border-color:#991b1b;-fx-border-width:1.2px;-fx-border-radius:8px;-fx-padding:6px 11px;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(186,26,26,.35),6,0,0,2px);";

        btn.setStyle(normalStyle);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            trashIcon.setFill(Color.WHITE);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(normalStyle);
            trashIcon.setFill(Color.web("#ba1a1a"));
        });

        if (onAction != null) {
            btn.setOnAction(e -> onAction.run());
        }

        return btn;
    }
}
