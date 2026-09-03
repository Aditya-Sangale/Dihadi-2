package com.dihadi.controller;

import com.dihadi.dao.JobApplicationDao;
import com.dihadi.model.JobApplication;

import java.util.List;

public class JobApplicationController {
    private JobApplicationDao dao;

    public JobApplicationController() {
        this.dao = new JobApplicationDao();
    }

    public void saveApplication(JobApplication application) {
        dao.saveApplication(application);
    }

    public void deleteApplication(String applicationId) {
        dao.deleteApplication(applicationId);
    }

    public List<JobApplication> getApplicationsByWorker(String workerMobile) {
        return dao.getApplicationsByWorker(workerMobile);
    }

    public List<JobApplication> getApplicationsByRecruiter(String recruiterMobile) {
        return dao.getApplicationsByRecruiter(recruiterMobile);
    }

    public List<JobApplication> getAllApplications() {
        return dao.getAllApplications();
    }

    public void updateWorkerApplicationsForProject(String workerMobile, String projectId, String newStatus) {
        dao.updateWorkerApplicationsForProject(workerMobile, projectId, newStatus);
    }

    public static boolean isApplicationMatching(JobApplication app, String projectId, String requirementId, String roleTitle, String location) {
        if (app == null) return false;

        // 1. Precise match by requirementId if both present
        if (requirementId != null && !requirementId.isBlank() && app.getRequirementId() != null && !app.getRequirementId().isBlank()) {
            if (requirementId.trim().equals(app.getRequirementId().trim())) {
                return true;
            }
        }

        // 2. Match by projectId if both present
        if (projectId != null && !projectId.isBlank() && app.getProjectId() != null && !app.getProjectId().isBlank()) {
            if (projectId.trim().equals(app.getProjectId().trim())) {
                if (roleTitle == null || roleTitle.isBlank() || app.getJobTitle() == null || app.getJobTitle().isBlank()) {
                    return true;
                }
                return roleTitle.trim().equalsIgnoreCase(app.getJobTitle().trim());
            }
            return false;
        }

        // 3. Fallback match for curated jobs without database projectId:
        // Must match BOTH role title AND location
        if (roleTitle != null && !roleTitle.isBlank() && app.getJobTitle() != null && !app.getJobTitle().isBlank()) {
            boolean titleMatches = roleTitle.trim().equalsIgnoreCase(app.getJobTitle().trim());
            if (titleMatches && location != null && !location.isBlank() && app.getJobLocation() != null && !app.getJobLocation().isBlank()) {
                return location.trim().equalsIgnoreCase(app.getJobLocation().trim());
            }
        }

        return false;
    }

    public boolean hasWorkerApplied(String workerMobile, String projectId, String requirementId, String roleTitle, String location) {
        if (workerMobile == null || workerMobile.isBlank()) return false;
        List<JobApplication> apps = getApplicationsByWorker(workerMobile);
        if (apps == null || apps.isEmpty()) return false;
        for (JobApplication app : apps) {
            if (isApplicationMatching(app, projectId, requirementId, roleTitle, location)) {
                return true;
            }
        }
        return false;
    }
}
