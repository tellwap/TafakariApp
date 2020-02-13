package com.blogspot.waptell.tafakari.databases;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "status")
public class StatusEntity {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "status_username")
    private String username;

    @ColumnInfo(name = "status_description")
    private String description;

    @ColumnInfo(name = "status_time")
    private Long time;

    @ColumnInfo(name = "profile_image")
    private String profileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
