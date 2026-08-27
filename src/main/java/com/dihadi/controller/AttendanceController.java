package com.dihadi.controller;

import com.dihadi.dao.AttendanceDao;
import com.dihadi.model.Attendance;
import java.util.List;

public class AttendanceController {
    private AttendanceDao dao;

    public AttendanceController() {
        this.dao = new AttendanceDao();
    }

    public void saveAttendance(Attendance attendance) {
        dao.saveAttendance(attendance);
    }

    public List<Attendance> getAttendanceByProject(String projectId) {
        return dao.getAttendanceByProject(projectId);
    }

    public List<Attendance> getAttendanceByWorker(String workerMobile) {
        return dao.getAttendanceByWorker(workerMobile);
    }
}
