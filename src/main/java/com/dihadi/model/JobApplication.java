package com.dihadi.model;

import java.util.Date;

public class JobApplication {
    private String applicationId;
    private String workerMobile;
    private String jobTitle;
    private String jobLocation;
    private String jobWage;
    private String status;
    private Date timestamp;
    private String projectId;
    private String recruiterMobile;
    private String requirementId;

    public JobApplication() {}

    public JobApplication(String applicationId, String workerMobile, String jobTitle, String jobLocation, String jobWage, String status) {
        this(applicationId, workerMobile, jobTitle, jobLocation, jobWage, status, null, null, null);
    }

    public JobApplication(String applicationId, String workerMobile, String jobTitle, String jobLocation, String jobWage, String status, String projectId, String recruiterMobile, String requirementId) {
        this.applicationId = applicationId;
        this.workerMobile = workerMobile;
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobWage = jobWage;
        this.status = status;
        this.projectId = projectId;
        this.recruiterMobile = recruiterMobile;
        this.requirementId = requirementId;
        this.timestamp = new Date();
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getWorkerMobile() { return workerMobile; }
    public void setWorkerMobile(String workerMobile) { this.workerMobile = workerMobile; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobLocation() { return jobLocation; }
    public void setJobLocation(String jobLocation) { this.jobLocation = jobLocation; }

    public String getJobWage() { return jobWage; }
    public void setJobWage(String jobWage) { this.jobWage = jobWage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getRecruiterMobile() { return recruiterMobile; }
    public void setRecruiterMobile(String recruiterMobile) { this.recruiterMobile = recruiterMobile; }

    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }
}
