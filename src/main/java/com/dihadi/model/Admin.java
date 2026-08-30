package com.dihadi.model;

import java.util.Date;

public class Admin {
    private String fullName;
    private String personalEmail;
    private String officialEmail;
    private String mobile;
    private String adminCode;
    private String password;
    private String uid;
    private Date createdAt;

    public Admin() {
        this.createdAt = new Date();
    }

    public Admin(String fullName, String personalEmail, String officialEmail,
                 String mobile, String adminCode, String password) {
        this.fullName = fullName;
        this.personalEmail = personalEmail;
        this.officialEmail = officialEmail;
        this.mobile = mobile;
        this.adminCode = adminCode;
        this.password = password;
        this.createdAt = new Date();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getOfficialEmail() { return officialEmail; }
    public void setOfficialEmail(String officialEmail) { this.officialEmail = officialEmail; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
