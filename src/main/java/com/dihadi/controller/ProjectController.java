package com.dihadi.controller;

import java.util.List;
import com.dihadi.dao.ProjectDao;
import com.dihadi.model.Project;

public class ProjectController {
    private ProjectDao dao = new ProjectDao();

    public void addProject(Project project) {
        dao.saveProject(project);
    }

    public Project getProject(String projectId) {
        return dao.getProject(projectId);
    }

    public Project getProjectById(String projectId) {
        return dao.getProject(projectId);
    }

    public List<Project> getAllProjects() {
        return dao.getAllProjects();
    }

    public boolean deleteProject(String projectId) {
        return dao.deleteProject(projectId);
    }

    public void updateProjectStatus(String projectId, String newStatus) {
        dao.updateProjectStatus(projectId, newStatus);
    }

    /**
     * Calculates total targeted workers required across all workforce requirements for the project.
     */
    public int getTargetWorkforceCount(String projectId) {
        if (projectId == null || projectId.isBlank()) return 0;
        try {
            List<com.dihadi.model.WorkforceRequirement> reqs =
                    new WorkforceRequirementController().getRequirementsForProject(projectId);
            int target = 0;
            if (reqs != null) {
                for (com.dihadi.model.WorkforceRequirement r : reqs) {
                    target += r.getQuantity();
                }
            }
            return target;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculates count of currently accepted/assigned workers for the project.
     */
    public int getAcceptedWorkerCount(String projectId) {
        if (projectId == null || projectId.isBlank()) return 0;
        try {
            List<com.dihadi.model.JobApplication> apps =
                    new JobApplicationController().getAllApplications();
            int accepted = 0;
            if (apps != null) {
                for (com.dihadi.model.JobApplication a : apps) {
                    if (projectId.equals(a.getProjectId()) && "Accepted".equalsIgnoreCase(a.getStatus())) {
                        accepted++;
                    }
                }
            }
            return accepted;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Updates project status to "Requirement Fulfilled" ONLY IF accepted workers >= targeted requirements.
     * If targeted requirements have not been reached, ensures project status remains or reverts to "Active".
     *
     * @return true if requirement is fulfilled, false otherwise.
     */
    public boolean checkAndUpdateProjectFulfilledStatus(String projectId) {
        if (projectId == null || projectId.isBlank()) return false;
        try {
            int targetWorkers = getTargetWorkforceCount(projectId);
            if (targetWorkers <= 0) {
                return false;
            }

            int acceptedCount = getAcceptedWorkerCount(projectId);

            if (acceptedCount >= targetWorkers) {
                updateProjectStatus(projectId, "Requirement Fulfilled");
                Project p = getProject(projectId);
                if (p != null) {
                    p.setStatus("Requirement Fulfilled");
                    if (com.dihadi.view.SessionManager.currentRecruiterProject != null
                            && projectId.equals(com.dihadi.view.SessionManager.currentRecruiterProject.getProjectId())) {
                        com.dihadi.view.SessionManager.currentRecruiterProject.setStatus("Requirement Fulfilled");
                    }
                }
                return true;
            } else {
                Project p = getProject(projectId);
                if (p != null && ("Requirement Fulfilled".equalsIgnoreCase(p.getStatus()) || "Unavailable".equalsIgnoreCase(p.getStatus()))) {
                    updateProjectStatus(projectId, "Active");
                    p.setStatus("Active");
                    if (com.dihadi.view.SessionManager.currentRecruiterProject != null
                            && projectId.equals(com.dihadi.view.SessionManager.currentRecruiterProject.getProjectId())) {
                        com.dihadi.view.SessionManager.currentRecruiterProject.setStatus("Active");
                    }
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
