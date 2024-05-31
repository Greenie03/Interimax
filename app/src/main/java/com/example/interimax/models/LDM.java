package com.example.interimax.models;

public class LDM {
    private String userId;
    private String ldmUrl;

    public LDM() {
        // Required for Firestore
    }

    public LDM(String userId, String ldmUrl) {
        this.userId = userId;
        this.ldmUrl = ldmUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLdmUrl() {
        return ldmUrl;
    }

    public void setLdmUrl(String ldmUrl) {
        this.ldmUrl = ldmUrl;
    }
}
