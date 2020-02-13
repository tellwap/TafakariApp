package com.blogspot.waptell.tafakari.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Subject.class, StatusEntity.class, UserStatus.class, User.class}, version = 3)
public abstract class MyDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
