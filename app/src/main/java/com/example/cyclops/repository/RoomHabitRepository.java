package com.example.cyclops.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.database.AppDatabase;
import com.example.cyclops.database.dao.DayTaskDao;
import com.example.cyclops.database.dao.HabitCycleDao;
import com.example.cyclops.database.entity.HabitCycleEntity;
import com.example.cyclops.database.entity.mapper.Mapper;
import com.example.cyclops.model.HabitCycle;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RoomHabitRepository implements HabitRepository {

    private static RoomHabitRepository instance;
    private final HabitCycleDao habitCycleDao;
    private final DayTaskDao dayTaskDao;
    private final Executor executor;
    private final MutableLiveData<String> errorLiveData;

    // 临时用户ID，后续集成认证系统时替换
    private static final String TEMP_USER_ID = "temp_user_001";

    private RoomHabitRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.habitCycleDao = database.habitCycleDao();
        this.dayTaskDao = database.dayTaskDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.errorLiveData = new MutableLiveData<>();
    }

    public static synchronized RoomHabitRepository getInstance(Application application) {
        if (instance == null) {
            instance = new RoomHabitRepository(application);
        }
        return instance;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    public void addHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                // 设置用户ID
                habitCycle.setUserId(TEMP_USER_ID);

                // 确保habitCycle有ID
                if (habitCycle.getId() == null) {
                    habitCycle.setId(java.util.UUID.randomUUID().toString());
                }

                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);

                // 直接插入，不使用返回值
                habitCycleDao.insert(entity);

                // 插入每日任务
                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    // 设置habitCycleId
                    for (int i = 0; i < entity.dayTasks.size(); i++) {
                        entity.dayTasks.get(i).habitCycleId = habitCycle.getId();
                    }
                    dayTaskDao.insertAll(entity.dayTasks);
                }
            } catch (Exception e) {
                errorLiveData.postValue("添加习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.update(entity);

                // 更新每日任务 - 先删除旧的，再插入新的
                dayTaskDao.deleteByHabitCycleId(habitCycle.getId());
                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    dayTaskDao.insertAll(entity.dayTasks);
                }
            } catch (Exception e) {
                errorLiveData.postValue("更新习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteHabitCycle(String habitId) {
        executor.execute(() -> {
            try {
                // 先删除关联的每日任务
                dayTaskDao.deleteByHabitCycleId(habitId);

                // 然后删除习惯循环
                habitCycleDao.deleteById(habitId);
            } catch (Exception e) {
                errorLiveData.postValue("删除习惯失败: " + e.getMessage());
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
        return Transformations.map(
                habitCycleDao.getAllHabitCycles(TEMP_USER_ID),
                Mapper::toHabitCycleList
        );
    }

    @Override
    public LiveData<HabitCycle> getHabitCycleById(String habitId) {
        return Transformations.map(
                habitCycleDao.getHabitCycleById(habitId),
                Mapper::toHabitCycle
        );
    }

    @Override
    public void completeDay(String habitId, int dayNumber) {
        executor.execute(() -> {
            try {
                // 更新任务完成状态
                dayTaskDao.updateDayTaskCompletion(habitId, dayNumber, true);

                // 增加完成次数 - 使用当前时间
                Date currentDate = new Date();
                habitCycleDao.incrementCompletions(habitId, currentDate);

                // 更新连续天数
                HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);
                if (entity != null) {
                    int newStreak = calculateNewStreak(entity, currentDate);
                    habitCycleDao.updateStreak(habitId, newStreak, currentDate);
                }
            } catch (Exception e) {
                errorLiveData.postValue("完成任务失败: " + e.getMessage());
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getPopularHabitCycles() {
        return Transformations.map(
                habitCycleDao.getPopularHabitCycles(),
                Mapper::toHabitCycleList
        );
    }

    // 初始化示例数据
    public void initializeSampleData() {
        executor.execute(() -> {
            try {
                List<HabitCycleEntity> existingHabits = habitCycleDao.getAllHabitCyclesSync(TEMP_USER_ID);
                if (existingHabits == null || existingHabits.isEmpty()) {
                    createSampleHabits();
                }
            } catch (Exception e) {
                errorLiveData.postValue("初始化数据失败: " + e.getMessage());
            }
        });
    }

    private void createSampleHabits() {
        // 创建示例健身习惯
        HabitCycle fitnessCycle = new HabitCycle();
        fitnessCycle.setName("健身计划");
        fitnessCycle.setDescription("3天循环健身计划");
        fitnessCycle.setCycleLength(3);
        fitnessCycle.updateDayTask(0, "胸部训练：卧推、飞鸟");
        fitnessCycle.updateDayTask(1, "背部训练：引体向上、划船");
        fitnessCycle.updateDayTask(2, "腿部训练：深蹲、硬拉");

        addHabitCycle(fitnessCycle);

        // 创建示例学习习惯
        HabitCycle studyCycle = new HabitCycle();
        studyCycle.setName("语言学习");
        studyCycle.setDescription("每日语言学习计划");
        studyCycle.setCycleLength(4);
        studyCycle.updateDayTask(0, "词汇学习");
        studyCycle.updateDayTask(1, "语法练习");
        studyCycle.updateDayTask(2, "听力训练");
        studyCycle.updateDayTask(3, "口语练习");

        addHabitCycle(studyCycle);
    }

    // 计算新的连续天数
    // 计算新的连续天数
    private int calculateNewStreak(HabitCycleEntity entity, Date currentDate) {
        if (entity.lastCompletionDate != null) {
            long diff = currentDate.getTime() - entity.lastCompletionDate.getTime();
            long daysDiff = diff / (1000 * 60 * 60 * 24);

            if (daysDiff <= 1) {
                // 连续完成，增加 streak
                return entity.currentStreak + 1;
            } else if (daysDiff == 2) {
                // 昨天没完成，但今天完成了，保持 streak
                return entity.currentStreak;
            }
        }
        // 重新开始或第一次完成
        return 1;
    }
}