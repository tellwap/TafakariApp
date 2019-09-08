package com.blogspot.waptell.tafakari.models;

public class MyStatus {

    private long id;
    private String username;
    private String description;
    private String time;
    private String profile;

    public MyStatus(){}

    public MyStatus(String username, String description, String time) {
        this.username = username;
        this.description = description;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
