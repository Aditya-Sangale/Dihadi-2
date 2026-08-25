package com.dihadi.model;

public class WorkforceRequirement {
    private String requirementId;
    private String projectId;
    private String priority;
    private String workerType;
    private String subSkill;
    private int quantity;
    private double dailyWages;
    private boolean waterFacility;
    private boolean electricityFacility;
    private boolean accommodationFacility;
    private boolean transportationFacility;

    public WorkforceRequirement() {
    }

    public WorkforceRequirement(String requirementId, String projectId, String priority,
                                String workerType, String subSkill, int quantity, double dailyWages,
                                boolean waterFacility, boolean electricityFacility,
                                boolean accommodationFacility, boolean transportationFacility) {
        this.requirementId = requirementId;
        this.projectId = projectId;
        this.priority = priority;
        this.workerType = workerType;
        this.subSkill = subSkill;
        this.quantity = quantity;
        this.dailyWages = dailyWages;
        this.waterFacility = waterFacility;
        this.electricityFacility = electricityFacility;
        this.accommodationFacility = accommodationFacility;
        this.transportationFacility = transportationFacility;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public String getSubSkill() {
        return subSkill;
    }

    public void setSubSkill(String subSkill) {
        this.subSkill = subSkill;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDailyWages() {
        return dailyWages;
    }

    public void setDailyWages(double dailyWages) {
        this.dailyWages = dailyWages;
    }

    public boolean isWaterFacility() {
        return waterFacility;
    }

    public void setWaterFacility(boolean waterFacility) {
        this.waterFacility = waterFacility;
    }

    public boolean isElectricityFacility() {
        return electricityFacility;
    }

    public void setElectricityFacility(boolean electricityFacility) {
        this.electricityFacility = electricityFacility;
    }

    public boolean isAccommodationFacility() {
        return accommodationFacility;
    }

    public void setAccommodationFacility(boolean accommodationFacility) {
        this.accommodationFacility = accommodationFacility;
    }

    public boolean isTransportationFacility() {
        return transportationFacility;
    }

    public void setTransportationFacility(boolean transportationFacility) {
        this.transportationFacility = transportationFacility;
    }
}
