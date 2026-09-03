package com.dihadi.model;

import java.util.Date;

public class Attendance {
    private String attendanceId;
    private String projectId;
    private String workerMobile;
    private String date; // Format: YYYY-MM-DD
    private String status; // Present, Absent, Half-day
    private Date timestamp;

    private String transactionId;
    private double paidAmount;
    private String recruiterId;
    private String workerId;
    private String paymentStatus;
    private String paymentTransactionId;

    public Attendance() {}

    public Attendance(String attendanceId, String projectId, String workerMobile, String date, String status) {
        this.attendanceId = attendanceId;
        this.projectId = projectId;
        this.workerMobile = workerMobile;
        this.date = date;
        this.status = status;
        this.timestamp = new Date();
    }

    public Attendance(String attendanceId, String projectId, String workerMobile, String date, String status, String transactionId, double paidAmount) {
        this.attendanceId = attendanceId;
        this.projectId = projectId;
        this.workerMobile = workerMobile;
        this.date = date;
        this.status = status;
        this.transactionId = transactionId;
        this.paidAmount = paidAmount;
        this.timestamp = new Date();
    }

    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getWorkerMobile() { return workerMobile; }
    public void setWorkerMobile(String workerMobile) { this.workerMobile = workerMobile; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
}
