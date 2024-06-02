package com.example.interimax.models;

import androidx.annotation.Nullable;

public class User {
    private String id;
    private String firstname;
    private String lastname;
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

    public User(String id, String firstname, String lastname, String email, String password, String DoB, @Nullable String profileImageUrl, String phoneNumber, String country, String role, @Nullable String bio) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.DoB = DoB;
        if(profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.role = role;
        if(bio != null) {
            this.bio = bio;
        }
    }
    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getDoB() {
        return DoB;
    }

    public void setBirthDate(String dob) {
        this.DoB = DoB;
    }
}

