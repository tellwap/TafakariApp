package com.blogspot.waptell.tafakari.models;

public class Status {

    private String username;
    private String description;
    private String time;
    private String image;

    public Status(){}

    public Status(String username, String description, String time) {
        this.username = username;
        this.description = description;
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
