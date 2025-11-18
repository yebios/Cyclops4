package com.example.cyclops.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "day_tasks")
public class DayTaskEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "habit_cycle_id")
    public String habitCycleId;

    public int dayNumber;
    public String taskName;
    public boolean completed;

    // 空构造函数 - Room需要
    public DayTaskEntity() {}

    // 业务构造函数
    public DayTaskEntity(String habitCycleId, int dayNumber, String taskName) {
        this.habitCycleId = habitCycleId;
        this.dayNumber = dayNumber;
        this.taskName = taskName;
        this.completed = false;
    }
}