package com.example.cyclops.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HabitCycle {
    private String id;
    private String name;
    private String description;
    private int cycleLength;
    private List<com.example.cyclops.model.DayTask> dayTasks;
    private String userId;
    private long startDate;
    private boolean isPublic;
    private int currentStreak;
    private int totalCompletions;

    public HabitCycle() {
        this.id = UUID.randomUUID().toString();
        this.dayTasks = new ArrayList<>();
        this.startDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCycleLength() { return cycleLength; }
    public void setCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
        // 自动创建对应天数的任务
        while (dayTasks.size() < cycleLength) {
            dayTasks.add(new com.example.cyclops.model.DayTask(dayTasks.size() + 1, "Day " + (dayTasks.size() + 1)));
        }
    }

    public List<com.example.cyclops.model.DayTask> getDayTasks() { return dayTasks; }
    public void setDayTasks(List<com.example.cyclops.model.DayTask> dayTasks) { this.dayTasks = dayTasks; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    public void updateDayTask(int dayIndex, String taskName) {
        if (dayIndex >= 0 && dayIndex < dayTasks.size()) {
            dayTasks.get(dayIndex).setTaskName(taskName);
        }
    }
}