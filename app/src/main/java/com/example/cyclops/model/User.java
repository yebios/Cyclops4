package com.example.cyclops.model;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private int level;
    private int experience;
    private long joinDate;

    public User() {}

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.level = 1;
        this.experience = 0;
        this.joinDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public long getJoinDate() { return joinDate; }
    public void setJoinDate(long joinDate) { this.joinDate = joinDate; }

    public void addExperience(int exp) {
        this.experience += exp;
        // 简单的升级逻辑：每100经验升一级
        this.level = (this.experience / 100) + 1;
    }
}