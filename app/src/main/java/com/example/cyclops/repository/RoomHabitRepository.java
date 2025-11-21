package com.example.cyclops.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cyclops.R;
import com.example.cyclops.database.AppDatabase;
import com.example.cyclops.database.dao.DayTaskDao;
import com.example.cyclops.database.dao.HabitCycleDao;
import com.example.cyclops.database.entity.HabitCycleEntity;
import com.example.cyclops.database.entity.mapper.Mapper;
import com.example.cyclops.model.HabitCycle;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RoomHabitRepository implements HabitRepository {

    private static RoomHabitRepository instance;
    private final HabitCycleDao habitCycleDao;
    private final DayTaskDao dayTaskDao;
    private final Executor executor;
    private final MutableLiveData<String> errorLiveData;
    private final Application application;

    private static final String CURRENT_USER_ID = "";

    private RoomHabitRepository(Application application) {
        this.application = application;
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

    @Override
    public void addHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                habitCycle.setUserId(CURRENT_USER_ID);
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.insert(entity);

                if (entity.dayTasks != null && !entity.dayTasks.isEmpty()) {
                    for (com.example.cyclops.database.entity.DayTaskEntity task : entity.dayTasks) {
                        task.habitCycleId = entity.id;
                    }
                    dayTaskDao.insertAll(entity.dayTasks);
                }
            } catch (Exception e) {
                String msg = application.getString(R.string.error_add_habit, e.getMessage());
                errorLiveData.postValue(msg);
            }
        });
    }

    @Override
    public void updateHabitCycle(HabitCycle habitCycle) {
        executor.execute(() -> {
            try {
                habitCycle.setUserId(CURRENT_USER_ID);
                HabitCycleEntity entity = Mapper.toHabitCycleEntity(habitCycle);
                habitCycleDao.update(entity);
            } catch (Exception e) {
                String msg = application.getString(R.string.error_update_habit, e.getMessage());
                errorLiveData.postValue(msg);
            }
        });
    }

    @Override
    public void deleteHabitCycle(String habitId) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = new HabitCycleEntity();
                entity.id = habitId;
                habitCycleDao.delete(entity);
            } catch (Exception e) {
                String msg = application.getString(R.string.error_delete_habit, e.getMessage());
                errorLiveData.postValue(msg);
            }
        });
    }

    @Override
    public LiveData<List<HabitCycle>> getAllHabitCycles() {
        return Transformations.map(
                habitCycleDao.getAllHabitCycles(CURRENT_USER_ID),
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
    public HabitCycle getHabitCycleByIdSync(String habitId) {
        HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);
        return Mapper.toHabitCycle(entity);
    }

    @Override
    public void completeDay(String habitId, int dayNumber) {
        executor.execute(() -> {
            try {
                HabitCycleEntity entity = habitCycleDao.getHabitCycleByIdSync(habitId);

                if (entity != null) {
                    HabitCycle tempModel = Mapper.toHabitCycle(entity);

                    // 1. 防止重复打卡
                    boolean isCompletedToday = com.example.cyclops.HabitCycleEngine.isCompletedToday(tempModel);

                    if (!isCompletedToday) {
                        // 2. 计算 Streak (断签重置)
                        if (com.example.cyclops.HabitCycleEngine.wasCompletedYesterday(tempModel)) {
                            // 昨天打了 -> 连击+1
                            entity.currentStreak = entity.currentStreak + 1;
                        } else {
                            // 断签了 -> 重置为1
                            entity.currentStreak = 1;
                        }

                        // 3. 更新最佳连续
                        if (entity.currentStreak > entity.bestStreak) {
                            entity.bestStreak = entity.currentStreak;
                        }

                        // 4. [核心修改] 计算循环完成次数 (Total Completions)
                        // 逻辑：只有当连续打卡数 (Streak) 是周期长度的整数倍时，才算完成了一个完整循环。
                        // 例如：周期4天。
                        // Streak = 4 -> 完成1次
                        // Streak = 8 -> 完成2次
                        // Streak = 1 (断签重置后) -> 不增加完成次数
                        if (entity.currentStreak > 0 && entity.currentStreak % entity.cycleLength == 0) {
                            entity.totalCompletions = entity.totalCompletions + 1;
                            android.util.Log.d("Repository", "恭喜！完成了一个完整的循环！");
                        }

                        // 5. 更新时间
                        entity.lastCompletionDate = new java.util.Date();
                        entity.updatedAt = new java.util.Date();

                        // 6. 保存
                        habitCycleDao.update(entity);
                        dayTaskDao.updateDayTaskCompletion(habitId, dayNumber, true);
                    }
                }
            } catch (Exception e) {
                String msg = application.getString(R.string.error_complete_task, e.getMessage());
                errorLiveData.postValue(msg);
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
}