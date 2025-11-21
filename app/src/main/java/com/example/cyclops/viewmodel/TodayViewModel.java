package com.example.cyclops.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.HabitCycleEngine;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TodayViewModel extends AndroidViewModel {

    private final HabitRepository habitRepository;
    private final LiveData<List<HabitCycle>> allHabitsLiveData;

    // 这些 LiveData 会根据 allHabitsLiveData 自动更新
    private final LiveData<List<HabitCycle>> todayHabitsLiveData;
    private final LiveData<Integer> completedCountLiveData;
    private final LiveData<Integer> totalCountLiveData;

    private final MutableLiveData<String> errorMessageLiveData;
    private final Executor executor;

    public TodayViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.executor = Executors.newSingleThreadExecutor();
        this.errorMessageLiveData = new MutableLiveData<>();

        // 1. 获取源数据 (所有习惯)
        this.allHabitsLiveData = habitRepository.getAllHabitCycles();

        // 2. [核心修改] 自动计算今日任务列表
        // 目前逻辑：所有习惯在每一天都有任务，所以"今日列表"等于"所有习惯"
        // 如果以后有"休息日"逻辑，可以在这里过滤
        this.todayHabitsLiveData = allHabitsLiveData;

        // 3. [核心修改] 自动计算总任务数 (基于列表大小)
        this.totalCountLiveData = Transformations.map(allHabitsLiveData, habits ->
                habits != null ? habits.size() : 0
        );

        // 4. [核心修改] 自动计算已完成数 (基于 isCompletedToday)
        // 只要数据库更新，这个逻辑就会重新运行，进度条就会自动更新
        this.completedCountLiveData = Transformations.map(allHabitsLiveData, habits -> {
            int count = 0;
            if (habits != null) {
                for (HabitCycle habit : habits) {
                    if (HabitCycleEngine.isCompletedToday(habit)) {
                        count++;
                    }
                }
            }
            return count;
        });
    }

    // Getters
    public LiveData<List<HabitCycle>> getHabitsLiveData() {
        return todayHabitsLiveData;
    }

    // 为了兼容 StatsFragment，提供这个方法
    public LiveData<List<HabitCycle>> getAllHabitsLiveData() {
        return allHabitsLiveData;
    }

    public LiveData<Integer> getCompletedCountLiveData() {
        return completedCountLiveData;
    }

    public LiveData<Integer> getTotalCountLiveData() {
        return totalCountLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    // 完成任务的操作
    public void completeTask(String habitId) {
        executor.execute(() -> {
            try {
                // 使用同步方法获取习惯，确保数据最新
                HabitCycle habitToComplete = habitRepository.getHabitCycleByIdSync(habitId);

                if (habitToComplete != null) {
                    // 计算今天是第几天
                    int currentDay = HabitCycleEngine.calculateCurrentDay(habitToComplete);

                    // 调用 Repository 更新数据库
                    // 注意：Repository 更新后，Room 会自动通知 allHabitsLiveData，
                    // 进而自动触发上面的 Transformations，进度条就会自动变了！
                    habitRepository.completeDay(habitId, currentDay);
                } else {
                    errorMessageLiveData.postValue("Habit not found: " + habitId);
                }
            } catch (Exception e) {
                errorMessageLiveData.postValue("Failed to complete task: " + e.getMessage());
            }
        });
    }
}