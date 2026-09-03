package com.dihadi.model;

/**
 * Model representing a Grievance, Dispute, or Contact Us Online Query.
 */
public class Grievance {
    private String grievanceId;
    private String subject;
    private String complainant;
    private String contactMobile;
    private String contactEmail;
    private String project;
    private String location;
    private String priority;
    private String status;
    private String category;
    private String disputeAmount;
    private String timestamp;
    private String incidentDescription;
    private String resolutionNotes;

    public Grievance() {}

    public Grievance(String grievanceId, String subject, String complainant, String contactMobile,
                     String contactEmail, String project, String location, String priority,
                     String status, String category, String disputeAmount, String timestamp,
                     String incidentDescription, String resolutionNotes) {
        this.grievanceId = grievanceId;
        this.subject = subject;
        this.complainant = complainant;
        this.contactMobile = contactMobile;
        this.contactEmail = contactEmail;
        this.project = project;
        this.location = location;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.disputeAmount = disputeAmount;
        this.timestamp = timestamp;
        this.incidentDescription = incidentDescription;
        this.resolutionNotes = resolutionNotes;
    }

    public String getGrievanceId() { return grievanceId; }
    public void setGrievanceId(String grievanceId) { this.grievanceId = grievanceId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getComplainant() { return complainant; }
    public void setComplainant(String complainant) { this.complainant = complainant; }

    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDisputeAmount() { return disputeAmount; }
    public void setDisputeAmount(String disputeAmount) { this.disputeAmount = disputeAmount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
