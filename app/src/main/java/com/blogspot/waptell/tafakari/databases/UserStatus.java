package com.blogspot.waptell.tafakari.databases;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_status")
public class UserStatus {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "status")
    private Boolean status;

    @ColumnInfo(name = "status_description")
    private String statusDiscription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatusDiscription() {
        return statusDiscription;
    }

    public void setStatusDiscription(String statusDiscription) {
        this.statusDiscription = statusDiscription;
    }
}
