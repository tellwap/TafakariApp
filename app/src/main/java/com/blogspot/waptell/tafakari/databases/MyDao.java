package com.blogspot.waptell.tafakari.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addSubject(Subject subject);

    @Query("select * from subjects order by time DESC")
     List<Subject> getAllSubject();

    @Insert()
    public void addStatus(UserStatus userStatus);

    @Update()
    public void updateStatus(UserStatus userStatus);

    @Query("select * from user_status")
    public List<UserStatus> getMystatus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void saveAllStatus(StatusEntity statusEntity);

    @Query("select * from status")
    public List<StatusEntity> getAllStatus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addUser(User user);

    @Query("select * from user")
    public List<User> getUserInfo();

    @Delete
    public void deleteStatus(UserStatus userStatus);

    @Update
    public void updateUserInfo(User user);
}
