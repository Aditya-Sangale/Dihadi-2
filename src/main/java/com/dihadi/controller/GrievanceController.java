package com.dihadi.controller;

import java.util.List;
import com.dihadi.dao.GrievanceDao;
import com.dihadi.model.Grievance;

public class GrievanceController {
    private GrievanceDao dao = new GrievanceDao();

    public void saveGrievance(Grievance grievance) {
        dao.saveGrievance(grievance);
    }

    public List<Grievance> getAllGrievances() {
        return dao.getAllGrievances();
    }

    public boolean deleteGrievance(String grievanceId) {
        return dao.deleteGrievance(grievanceId);
    }

    public boolean resolveGrievance(String grievanceId, String resolutionNotes) {
        return dao.updateStatus(grievanceId, "Resolved", resolutionNotes);
    }
}
