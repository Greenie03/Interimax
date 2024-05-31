package com.example.interimax.models;

public class User {
    private String id;
    private String Firstname;
    private String Lastname;
    private String email;
    private String password;
    private String DoB;
    private String profileImageUrl;
    private String phoneNumber;
    private String country;
    private String role;
    private String bio;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String id, String firstname, String lastname, String email, String password, String DoB, String profileImageUrl, String phoneNumber, String country, String role, String bio) {
        this.id = id;
        Firstname = firstname;
        Lastname = lastname;
        this.email = email;
        this.password = password;
        this.DoB = DoB;
        this.profileImageUrl = profileImageUrl;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.role = role;
        this.bio = bio;
    }
    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

