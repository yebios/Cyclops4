package com.example.cyclops.model;

public class DayTask {
    private int dayNumber;
    private String taskName;
    private boolean completed;

    public DayTask(int dayNumber, String taskName) {
        this.dayNumber = dayNumber;
        this.taskName = taskName;
        this.completed = false;
    }

    // Getters and Setters
    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}