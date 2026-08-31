package com.dihadi.controller;

import com.dihadi.dao.AdminDao;
import com.dihadi.model.Admin;

public class AdminController {
    private final AdminDao dao = new AdminDao();

    public boolean registerAdmin(String fullName, String personalEmail, String officialEmail,
                                 String mobile, String adminCode, String password) {
        Admin admin = new Admin(fullName, personalEmail, officialEmail, mobile, adminCode, password);
        return dao.saveAdmin(admin);
    }

    public Admin authenticate(String identifier, String password) {
        return dao.validateCredentials(identifier, password);
    }

    public Admin getAdmin(String identifier) {
        return dao.getAdminByEmailOrMobile(identifier);
    }
}
