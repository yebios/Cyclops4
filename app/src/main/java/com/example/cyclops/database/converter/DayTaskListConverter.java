package com.example.cyclops.database.converter;

import androidx.room.TypeConverter;

import com.example.cyclops.database.entity.DayTaskEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DayTaskListConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<DayTaskEntity> fromString(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<DayTaskEntity>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<DayTaskEntity> list) {
        if (list == null) {
            return null;
        }
        return gson.toJson(list);
    }
}