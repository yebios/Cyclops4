package com.example.cyclops.utils;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;
import java.util.Calendar;

public class HabitCycleEngine {

    /**
     * 计算当前应该显示的习惯循环中的第几天
     */
    public static int calculateCurrentDay(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getStartDate() == 0) {
            return 1;
        }

        long startTime = habitCycle.getStartDate();
        long currentTime = System.currentTimeMillis();

        // 计算从开始日期到现在经过的天数
        long diff = currentTime - startTime;
        long daysPassed = diff / (24 * 60 * 60 * 1000);

        // 使用模运算确定当前在循环中的位置
        int currentDay = (int) (daysPassed % habitCycle.getCycleLength()) + 1;

        return currentDay;
    }

    /**
     * 获取当前天的任务
     */
    public static DayTask getCurrentDayTask(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            // 确保天数在有效范围内
            int actualDay = (currentDay - 1) % habitCycle.getDayTasks().size();
            return habitCycle.getDayTasks().get(actualDay);
        }
        return null;
    }

    /**
     * 检查是否是新的一天（用于重置完成状态）
     */
    public static boolean isNewDay(HabitCycle habitCycle, long lastCompletionTime) {
        if (lastCompletionTime == 0) return true;

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(lastCompletionTime);

        Calendar currentCal = Calendar.getInstance();

        return lastCal.get(Calendar.YEAR) != currentCal.get(Calendar.YEAR) ||
                lastCal.get(Calendar.MONTH) != currentCal.get(Calendar.MONTH) ||
                lastCal.get(Calendar.DAY_OF_MONTH) != currentCal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算连续完成天数
     */
    public static int calculateStreak(HabitCycle habitCycle, long[] completionDates) {
        // 简化实现：按时间顺序检查连续完成
        if (completionDates == null || completionDates.length == 0) {
            return 0;
        }

        int streak = 0;
        Calendar cal = Calendar.getInstance();

        // 按日期倒序检查连续性
        for (int i = completionDates.length - 1; i >= 0; i--) {
            cal.setTimeInMillis(completionDates[i]);
            // 这里需要更复杂的日期连续性检查
            // 简化：假设数组是按时间排序的
            if (i == completionDates.length - 1) {
                streak = 1;
            } else {
                // 检查是否连续（相差1天）
                long diff = completionDates[i + 1] - completionDates[i];
                long daysDiff = diff / (24 * 60 * 60 * 1000);
                if (daysDiff == 1) {
                    streak++;
                } else {
                    break;
                }
            }
        }

        return streak;
    }
}