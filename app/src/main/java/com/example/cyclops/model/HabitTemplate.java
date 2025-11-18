package com.example.cyclops.model;

import java.util.List;

public class HabitTemplate {
    private String id;
    private String name;
    private String description;
    private String category;
    private int cycleLength;
    private List<String> tasks;
    private int downloadCount;
    private float rating;
    private String author;
    private long createDate;

    public HabitTemplate() {}

    public HabitTemplate(String id, String name, String description, String category,
                         int cycleLength, List<String> tasks, int downloadCount, float rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.cycleLength = cycleLength;
        this.tasks = tasks;
        this.downloadCount = downloadCount;
        this.rating = rating;
        this.createDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getCycleLength() { return cycleLength; }
    public void setCycleLength(int cycleLength) { this.cycleLength = cycleLength; }

    public List<String> getTasks() { return tasks; }
    public void setTasks(List<String> tasks) { this.tasks = tasks; }

    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getCreateDate() { return createDate; }
    public void setCreateDate(long createDate) { this.createDate = createDate; }

    public String getFormattedDownloadCount() {
        if (downloadCount >= 1000) {
            return String.format("%.1fk", downloadCount / 1000.0);
        }
        return String.valueOf(downloadCount);
    }
}