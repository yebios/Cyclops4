package com.example.cyclops.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import com.example.cyclops.database.dao.HabitCycleDao;
import com.example.cyclops.database.dao.DayTaskDao;
import com.example.cyclops.database.entity.HabitCycleEntity;
import com.example.cyclops.database.entity.DayTaskEntity;
import com.example.cyclops.database.converter.DataConverter;
import com.example.cyclops.database.converter.DayTaskListConverter;

@Database(
        entities = {HabitCycleEntity.class, DayTaskEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({DataConverter.class, DayTaskListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract HabitCycleDao habitCycleDao();
    public abstract DayTaskDao dayTaskDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "cyclops.db"
                            )
                            .fallbackToDestructiveMigration() // 开发时使用，正式发布时移除
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}