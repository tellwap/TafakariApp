package com.blogspot.waptell.tafakari.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
