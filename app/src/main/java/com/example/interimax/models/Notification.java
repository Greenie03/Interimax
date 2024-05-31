package com.example.interimax.models;

public class Notification {
    private String title;
    private String description;
    private String time;
    private int imageResId; // Resource ID for the image

    public Notification(String title, String description, String time, int imageResId) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public int getImageResId() {
        return imageResId;
    }
}
