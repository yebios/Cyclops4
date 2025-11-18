package com.example.cyclops.database;

import android.app.Application;
import android.util.Log;

import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.repository.RoomHabitRepository;

public class DatabaseTestHelper {

    private static final String TAG = "DatabaseTest";

    public static void runDatabaseTests(Application application) {
        RoomHabitRepository repository = RoomHabitRepository.getInstance(application);

        Log.d(TAG, "开始数据库测试...");

        // 测试1: 添加习惯
        testAddHabit(repository);

        // 测试2: 查询习惯
        testQueryHabits(repository);

        // 测试3: 完成任务
        testCompleteTask(repository);

        Log.d(TAG, "数据库测试完成");
    }

    private static void testAddHabit(RoomHabitRepository repository) {
        Log.d(TAG, "测试1: 添加新习惯");

        HabitCycle testHabit = new HabitCycle();
        testHabit.setName("测试习惯");
        testHabit.setDescription("这是一个测试习惯");
        testHabit.setCycleLength(3);
        testHabit.updateDayTask(0, "测试任务1");
        testHabit.updateDayTask(1, "测试任务2");
        testHabit.updateDayTask(2, "测试任务3");

        repository.addHabitCycle(testHabit);
        Log.d(TAG, "测试习惯添加完成: " + testHabit.getName());
    }

    private static void testQueryHabits(RoomHabitRepository repository) {
        Log.d(TAG, "测试2: 查询习惯列表");

        repository.getAllHabitCycles().observeForever(habits -> {
            if (habits != null) {
                Log.d(TAG, "查询到 " + habits.size() + " 个习惯");
                for (HabitCycle habit : habits) {
                    Log.d(TAG, "习惯: " + habit.getName() +
                            ", 完成次数: " + habit.getTotalCompletions() +
                            ", 连续天数: " + habit.getCurrentStreak());

                    if (habit.getDayTasks() != null) {
                        for (DayTask task : habit.getDayTasks()) {
                            Log.d(TAG, "  任务" + task.getDayNumber() + ": " +
                                    task.getTaskName() + " - 完成: " + task.isCompleted());
                        }
                    }
                }
            }
        });
    }

    private static void testCompleteTask(RoomHabitRepository repository) {
        Log.d(TAG, "测试3: 完成任务");

        // 获取第一个习惯并完成第1天的任务
        repository.getAllHabitCycles().observeForever(habits -> {
            if (habits != null && !habits.isEmpty()) {
                HabitCycle firstHabit = habits.get(0);
                repository.completeDay(firstHabit.getId(), 1);
                Log.d(TAG, "完成任务: " + firstHabit.getName() + " - 第1天");
            }
        });
    }
}