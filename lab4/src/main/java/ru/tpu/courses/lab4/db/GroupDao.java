package ru.tpu.courses.lab4.db;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM `group`")
    List<Group> getAll();

    @Insert
    long insert(@NonNull Group group);

    @Query("SELECT count(*) FROM `group` WHERE group_name = :groupName")
    int count(@NonNull String groupName);

    @Query("SELECT count(*) FROM `group` WHERE id = :groupId")
    int count(int groupId);
}
