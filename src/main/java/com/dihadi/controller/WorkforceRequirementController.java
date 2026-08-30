package com.dihadi.controller;

import java.util.List;
import com.dihadi.dao.WorkforceRequirementDao;
import com.dihadi.model.WorkforceRequirement;

public class WorkforceRequirementController {
    private WorkforceRequirementDao dao = new WorkforceRequirementDao();

    public void addRequirement(WorkforceRequirement req) {
        dao.saveRequirement(req);
    }

    public List<WorkforceRequirement> getRequirementsForProject(String projectId) {
        return dao.getRequirementsForProject(projectId);
    }

    public List<WorkforceRequirement> getAllRequirements() {
        return dao.getAllRequirements();
    }
}
