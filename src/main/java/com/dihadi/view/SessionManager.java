package com.dihadi.view;

import com.dihadi.model.Admin;
import com.dihadi.model.Recruiter;
import com.dihadi.model.Worker;

public class SessionManager {
    public static Worker currentWorker = null;
    public static Recruiter currentRecruiter = null;
    public static Admin currentAdmin = null;

    public static String getAdminDisplayName() {
        if (currentAdmin != null && currentAdmin.getFullName() != null && !currentAdmin.getFullName().isBlank()) {
            return currentAdmin.getFullName();
        }
        return "Admin";
    }

    public static String getCurrentRecruiterId() {
        if (currentRecruiter != null) {
            if (currentRecruiter.getUid() != null && !currentRecruiter.getUid().isBlank()) {
                return currentRecruiter.getUid();
            }
            if (currentRecruiter.getMobileNumber() != null && !currentRecruiter.getMobileNumber().isBlank()) {
                return currentRecruiter.getMobileNumber();
            }
        }
        return "REC_DEFAULT";
    }

    public static String getCurrentWorkerId() {
        if (currentWorker != null) {
            if (currentWorker.getUid() != null && !currentWorker.getUid().isBlank()) {
                return currentWorker.getUid();
            }
            if (currentWorker.getMobileNumber() != null && !currentWorker.getMobileNumber().isBlank()) {
                return currentWorker.getMobileNumber();
            }
        }
        return "WRK_DEFAULT";
    }
}
