package com.dihadi.view;

import com.dihadi.model.Admin;
import com.dihadi.model.Recruiter;
import com.dihadi.model.Worker;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SessionManager {
    public static Worker currentWorker = null;
    public static Recruiter currentRecruiter = null;
    public static Admin currentAdmin = null;

    public enum Role {
        WORKER("Worker"),
        RECRUITER("Recruiter"),
        ADMIN("Admin");

        private final String displayName;

        Role(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static Role getActiveRole() {
        if (currentWorker != null) return Role.WORKER;
        if (currentRecruiter != null) return Role.RECRUITER;
        if (currentAdmin != null) return Role.ADMIN;
        return null;
    }

    public static boolean isLoggedIn() {
        return getActiveRole() != null;
    }

    public static void clearAllSessions() {
        currentWorker = null;
        currentRecruiter = null;
        currentAdmin = null;
    }

    public static void signOut() {
        clearAllSessions();
    }

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
    /**
     * Checks if navigation/action for targetRole is allowed.
     * If a conflicting role session is currently active, a modal popup is shown
     * alerting the user that they are already logged in and must sign out first.
     *
     * @param targetRole The role being accessed (WORKER, RECRUITER, or ADMIN)
     * @return true if access is allowed, false if blocked by an active conflicting session
     */
    public static boolean checkAccessAllowed(Role targetRole) {
        Role activeRole = getActiveRole();
        if (activeRole == null || activeRole == targetRole) {
            return true;
        }

        String activeName = getActiveRoleName(activeRole);
        String message = "Already logged in as " + activeRole.getDisplayName() + 
                (activeName.isEmpty() ? "" : " (" + activeName + ")") + 
                ". Please sign out from your dashboard first before accessing " + targetRole.getDisplayName() + ".";

        NotificationToast.showModal(
                "Active Session Conflict",
                message,
                NotificationToast.ToastType.ALERT
        );

        return false;
    }

    private static String getActiveRoleName(Role role) {
        if (role == Role.WORKER && currentWorker != null) {
            String name = (currentWorker.getFirstName() != null ? currentWorker.getFirstName() : "") + " " +
                    (currentWorker.getLastName() != null ? currentWorker.getLastName() : "");
            name = name.trim();
            if (!name.isEmpty()) return name;
            if (currentWorker.getMobileNumber() != null) return currentWorker.getMobileNumber();
        } else if (role == Role.RECRUITER && currentRecruiter != null) {
            String name = (currentRecruiter.getFirstName() != null ? currentRecruiter.getFirstName() : "") + " " +
                    (currentRecruiter.getLastName() != null ? currentRecruiter.getLastName() : "");
            name = name.trim();
            if (!name.isEmpty()) return name;
            if (currentRecruiter.getCompanyName() != null && !currentRecruiter.getCompanyName().isBlank()) {
                return currentRecruiter.getCompanyName();
            }
            if (currentRecruiter.getMobileNumber() != null) return currentRecruiter.getMobileNumber();
        } else if (role == Role.ADMIN && currentAdmin != null) {
            return getAdminDisplayName();
        }
        return "";
    }
}
