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
}
