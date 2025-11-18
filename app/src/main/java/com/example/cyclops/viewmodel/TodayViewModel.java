package com.example.cyclops.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;
import com.example.cyclops.utils.HabitCycleEngine;

import java.util.ArrayList;
import java.util.List;

public class TodayViewModel extends AndroidViewModel {

    private HabitRepository habitRepository;
    private MutableLiveData<List<HabitCycle>> todayHabitsLiveData;
    private MutableLiveData<String> errorMessageLiveData;
    private MutableLiveData<Integer> completedCountLiveData;

    public TodayViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.todayHabitsLiveData = new MutableLiveData<>();
        this.errorMessageLiveData = new MutableLiveData<>();
        this.completedCountLiveData = new MutableLiveData<>();
        loadTodayHabits();
    }

    public LiveData<List<HabitCycle>> getTodayHabitsLiveData() {
        return todayHabitsLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Integer> getCompletedCountLiveData() {
        return completedCountLiveData;
    }

    public void loadTodayHabits() {
        LiveData<List<HabitCycle>> allHabitsLiveData = habitRepository.getAllHabitCycles();
        allHabitsLiveData.observeForever(allHabits -> {
            if (allHabits != null) {
                List<HabitCycle> todayHabits = new ArrayList<>();
                int completedCount = 0;

                for (HabitCycle habit : allHabits) {
                    DayTask todayTask = HabitCycleEngine.getCurrentDayTask(habit);
                    if (todayTask != null) {
                        HabitCycle todayHabit = new HabitCycle();
                        todayHabit.setId(habit.getId());
                        todayHabit.setName(habit.getName());
                        todayHabit.setDescription(habit.getDescription());
                        todayHabit.setCycleLength(habit.getCycleLength());

                        List<DayTask> todayTasks = new ArrayList<>();
                        todayTasks.add(todayTask);
                        todayHabit.setDayTasks(todayTasks);

                        todayHabit.setCurrentStreak(habit.getCurrentStreak());
                        todayHabits.add(todayHabit);

                        if (todayTask.isCompleted()) {
                            completedCount++;
                        }
                    }
                }

                todayHabitsLiveData.setValue(todayHabits);
                completedCountLiveData.setValue(completedCount);
            }
        });
    }

    public void completeTask(String habitId) {
        HabitCycle habit = getHabitById(habitId);
        if (habit != null) {
            int currentDay = HabitCycleEngine.calculateCurrentDay(habit);
            habitRepository.completeDay(habitId, currentDay);
            loadTodayHabits();
        }
    }

    public void skipTask(String habitId) {
        loadTodayHabits();
    }

    private HabitCycle getHabitById(String habitId) {
        List<HabitCycle> currentHabits = todayHabitsLiveData.getValue();
        if (currentHabits != null) {
            for (HabitCycle habit : currentHabits) {
                if (habit.getId().equals(habitId)) {
                    return habit;
                }
            }
        }
        return null;
    }
}