package com.example.cyclops.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;
import com.example.cyclops.utils.HabitCycleEngine;

import java.util.List;

public class HabitViewModel extends AndroidViewModel {

    private HabitRepository habitRepository;
    private MutableLiveData<List<HabitCycle>> habitsLiveData;
    private MutableLiveData<HabitCycle> selectedHabitLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public HabitViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.habitsLiveData = new MutableLiveData<>();
        this.selectedHabitLiveData = new MutableLiveData<>();
        this.errorMessageLiveData = new MutableLiveData<>();
        loadAllHabits();
    }

    public LiveData<List<HabitCycle>> getHabitsLiveData() {
        return habitsLiveData;
    }

    public LiveData<HabitCycle> getSelectedHabitLiveData() {
        return selectedHabitLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void loadAllHabits() {
        LiveData<List<HabitCycle>> habitsLiveDataFromRepo = habitRepository.getAllHabitCycles();
        habitsLiveDataFromRepo.observeForever(habits -> {
            habitsLiveData.setValue(habits);
        });
    }

    public void addHabitCycle(HabitCycle habitCycle) {
        habitRepository.addHabitCycle(habitCycle);
        loadAllHabits();
    }

    public void updateHabitCycle(HabitCycle habitCycle) {
        habitRepository.updateHabitCycle(habitCycle);
        loadAllHabits();
    }

    public void deleteHabitCycle(String habitId) {
        habitRepository.deleteHabitCycle(habitId);
        loadAllHabits();
    }

    public void selectHabitCycle(String habitId) {
        LiveData<HabitCycle> habitLiveData = habitRepository.getHabitCycleById(habitId);
        habitLiveData.observeForever(habit -> {
            selectedHabitLiveData.setValue(habit);
        });
    }

    public void completeDay(String habitId, int dayNumber) {
        habitRepository.completeDay(habitId, dayNumber);
        selectHabitCycle(habitId);
        loadAllHabits();
    }

    public int getCurrentDayForHabit(HabitCycle habitCycle) {
        return HabitCycleEngine.calculateCurrentDay(habitCycle);
    }
}