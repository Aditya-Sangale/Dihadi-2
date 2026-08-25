package com.dihadi.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectId;
    private String projectName;
    private String contactName;
    private String mobile;
    private String alternateMobile;
    private String email;
    private String pincode;
    private String city;
    private String state;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private List<String> imageUrls = new ArrayList<>();

    public Project() {
    }

    public Project(String projectId, String projectName, String contactName, String mobile,
                   String alternateMobile, String email, String pincode, String city,
                   String state, String addressLine1, String addressLine2, String landmark) {
        this(projectId, projectName, contactName, mobile, alternateMobile, email, pincode,
             city, state, addressLine1, addressLine2, landmark, new ArrayList<>());
    }

    public Project(String projectId, String projectName, String contactName, String mobile,
                   String alternateMobile, String email, String pincode, String city,
                   String state, String addressLine1, String addressLine2, String landmark,
                   List<String> imageUrls) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.contactName = contactName;
        this.mobile = mobile;
        this.alternateMobile = alternateMobile;
        this.email = email;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.landmark = landmark;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
