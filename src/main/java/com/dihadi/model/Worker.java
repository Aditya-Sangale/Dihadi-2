package com.dihadi.model;

public class Worker {
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private String alternateMobile;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String education;
    private String experience;
    private int dailyWage;
    private String uid;
    private String profilePhotoUrl;

    public Worker() {
    }

    public Worker(String firstName, String middleName, String lastName,
                  String mobileNumber, String alternateMobile, String email,
                  String gender, String dateOfBirth, String education,
                  String experience, int dailyWage) {
        this(firstName, middleName, lastName, mobileNumber, alternateMobile, email,
             gender, dateOfBirth, education, experience, dailyWage, null);
    }

    public Worker(String firstName, String middleName, String lastName,
                  String mobileNumber, String alternateMobile, String email,
                  String gender, String dateOfBirth, String education,
                  String experience, int dailyWage, String profilePhotoUrl) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.alternateMobile = alternateMobile;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.education = education;
        this.experience = experience;
        this.dailyWage = dailyWage;
        this.profilePhotoUrl = profilePhotoUrl;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public int getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(int dailyWage) {
        this.dailyWage = dailyWage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
