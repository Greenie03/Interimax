package com.example.interimax.models;

public class Application {
    private String id;
    private String userId;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String companyLogoUrl;
    private String location;
    private String salary;
    private String status; // could be "refused", "pending", "accepted"
    private long timestamp;

    // Required empty constructor for Firebase
    public Application() {
    }

    public Application(String id, String userId, String jobId, String jobTitle, String companyName, String companyLogoUrl, String location, String salary, String status, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.companyLogoUrl = companyLogoUrl;
        this.location = location;
        this.salary = salary;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
