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
}
