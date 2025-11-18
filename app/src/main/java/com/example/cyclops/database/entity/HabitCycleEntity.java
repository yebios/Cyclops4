package com.example.cyclops.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.cyclops.database.converter.DataConverter;
import com.example.cyclops.database.converter.DayTaskListConverter;

import java.util.Date;
import java.util.List;

@Entity(tableName = "habit_cycles")
public class HabitCycleEntity {
    @PrimaryKey
    @NonNull  // 添加这个注解
    public String id;

    public String name;
    public String description;
    public int cycleLength;

    @TypeConverters(DayTaskListConverter.class)
    public List<DayTaskEntity> dayTasks;

    public String userId;

    @TypeConverters(DataConverter.class)
    public Date startDate;

    public boolean isPublic;
    public int currentStreak;
    public int totalCompletions;

    @TypeConverters(DataConverter.class)
    public Date lastCompletionDate;

    @TypeConverters(DataConverter.class)
    public Date createdAt;

    @TypeConverters(DataConverter.class)
    public Date updatedAt;

    // 空构造函数 - Room需要
    public HabitCycleEntity() {
        this.id = "";  // 提供默认值
    }

    // 业务构造函数 - 更新以包含lastCompletionDate
    public HabitCycleEntity(@NonNull String id, String name, String description, int cycleLength,
                            List<DayTaskEntity> dayTasks, String userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cycleLength = cycleLength;
        this.dayTasks = dayTasks;
        this.userId = userId;
        this.startDate = new Date();
        this.isPublic = false;
        this.currentStreak = 0;
        this.totalCompletions = 0;
        this.lastCompletionDate = null;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // 可选：添加getter和setter方法
    public Date getLastCompletionDate() {
        return lastCompletionDate;
    }

    public void setLastCompletionDate(Date lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }

    // 添加 id 的 getter 和 setter
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }
}