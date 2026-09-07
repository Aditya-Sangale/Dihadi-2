package com.dihadi.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Recruiter {
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String mobileNumber;
    private String alternateMobile;
    private String email;
    private String alternateEmail;
    private String companyName;
    private String businessType;
    private String uid;
    private String password;
    private double walletBalance;
    private String lastLogin;

    public Recruiter() {
    }

    public Recruiter(String firstName, String middleName, String lastName,
                     String gender, String mobileNumber, String alternateMobile,
                     String email, String alternateEmail, String companyName,
                     String businessType, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.alternateMobile = alternateMobile;
        this.email = email;
        this.alternateEmail = alternateEmail;
        this.companyName = companyName;
        this.businessType = businessType;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAlternateMobile() {
        return alternateMobile;
    }

    public void setAlternateMobile(String alternateMobile) {
        this.alternateMobile = alternateMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getId() {
        if (uid != null && !uid.isBlank()) return uid;
        if (mobileNumber != null && !mobileNumber.isBlank()) return mobileNumber;
        return "";
    }

    public void setId(String id) {
        if ((this.uid == null || this.uid.isBlank()) && id != null) {
            this.uid = id;
        }
    }

    public String getName() {
        String fn = firstName != null ? firstName.trim() : "";
        String mn = middleName != null ? middleName.trim() : "";
        String ln = lastName != null ? lastName.trim() : "";
        String full = (fn + (mn.isEmpty() ? "" : " " + mn) + (ln.isEmpty() ? "" : " " + ln)).trim();
        return !full.isEmpty() ? full : (companyName != null ? companyName : "Recruiter");
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            if (this.firstName == null || this.firstName.isBlank()) {
                String[] parts = name.trim().split("\\s+", 3);
                this.firstName = parts[0];
                if (parts.length == 2) {
                    this.lastName = parts[1];
                } else if (parts.length > 2) {
                    this.middleName = parts[1];
                    this.lastName = parts[2];
                }
            }
        }
    }

    public String getPhone() {
        return mobileNumber != null ? mobileNumber : "";
    }

    public void setPhone(String phone) {
        if ((this.mobileNumber == null || this.mobileNumber.isBlank()) && phone != null) {
            this.mobileNumber = phone;
        }
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}
