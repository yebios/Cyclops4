package com.example.cyclops;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.DayTask;

import java.util.Calendar;

public class HabitCycleEngine {

    /**
     * 计算当前应该是第几天
     */
    public static int calculateCurrentDay(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getStartDate() == 0) {
            return 1;
        }
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(habitCycle.getStartDate());
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(System.currentTimeMillis());
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        long diff = currentCal.getTimeInMillis() - startCal.getTimeInMillis();
        long daysPassed = diff / (24 * 60 * 60 * 1000);

        int currentDay = (int) (daysPassed % habitCycle.getCycleLength()) + 1;
        return Math.max(1, Math.min(habitCycle.getCycleLength(), currentDay));
    }

    /**
     * 检查该习惯今天是否已经完成
     */
    public static boolean isCompletedToday(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getLastCompletionDate() == 0) {
            return false;
        }

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(habitCycle.getLastCompletionDate());

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTimeInMillis(System.currentTimeMillis());

        return lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 检查是否断签
     */
    public static boolean isStreakBroken(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getLastCompletionDate() == 0) {
            return true; // 从未打卡，视为断签
        }

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(habitCycle.getLastCompletionDate());
        lastCal.set(Calendar.HOUR_OF_DAY, 0);
        lastCal.set(Calendar.MINUTE, 0);
        lastCal.set(Calendar.SECOND, 0);
        lastCal.set(Calendar.MILLISECOND, 0);

        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1); // 昨天
        yesterdayCal.set(Calendar.HOUR_OF_DAY, 0);
        yesterdayCal.set(Calendar.MINUTE, 0);
        yesterdayCal.set(Calendar.SECOND, 0);
        yesterdayCal.set(Calendar.MILLISECOND, 0);

        return lastCal.getTimeInMillis() < yesterdayCal.getTimeInMillis();
    }

    /**
     * 检查最后一次是否是“昨天”打的卡
     */
    public static boolean wasCompletedYesterday(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getLastCompletionDate() == 0) {
            return false;
        }

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(habitCycle.getLastCompletionDate());

        Calendar yesterdayCal = Calendar.getInstance();
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);

        return lastCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * [新增] 计算单个习惯的成功率
     * 逻辑：(已完成的完整循环天数 + 当前循环已完成天数) / (创建至今的总天数)
     */
    public static double calculateSuccessRate(HabitCycle habitCycle) {
        if (habitCycle == null || habitCycle.getStartDate() == 0) {
            return 0.0;
        }

        // 1. 计算分母：创建至今的总天数
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(habitCycle.getStartDate());
        // 归零
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        // 毫秒差转天数 (+1 是因为第一天也算一天)
        long daysElapsed = (currentCal.getTimeInMillis() - startCal.getTimeInMillis()) / (24 * 60 * 60 * 1000) + 1;
        if (daysElapsed <= 0) daysElapsed = 1; // 防止除以0

        // 2. 计算分子：总共打卡的天数

        // A. 完整循环贡献的天数 (totalCompletions = 完整循环次数)
        long completedDaysFromCycles = (long) habitCycle.getTotalCompletions() * habitCycle.getCycleLength();

        // B. 当前循环贡献的天数
        int currentDayInCycle = calculateCurrentDay(habitCycle); // 当前是第几天 (1 ~ cycleLength)

        // 计算当前循环已完成的天数：
        // 基础进度是 currentDay - 1 (比如今天是第3天，那前2天肯定是完成了的，除非之前就断了，但这里我们按进度条逻辑算)
        // 如果今天也打卡了，再 +1
        int currentCycleProgress = currentDayInCycle - 1;
        if (isCompletedToday(habitCycle)) {
            currentCycleProgress += 1;
        }

        // 总有效工作天数
        long totalDaysWorked = completedDaysFromCycles + currentCycleProgress;

        // 3. 计算比率 (封顶 100%)
        double rate = (double) totalDaysWorked / daysElapsed * 100;
        return Math.min(100.0, Math.max(0.0, rate));
    }

    public static DayTask getCurrentDayTask(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            return habitCycle.getDayTasks().get(actualDayIndex);
        }
        return null;
    }

    public static DayTask getCurrentDayTaskForDisplay(HabitCycle habitCycle) {
        int currentDay = calculateCurrentDay(habitCycle);
        if (habitCycle.getDayTasks() != null && !habitCycle.getDayTasks().isEmpty()) {
            int actualDayIndex = Math.min(currentDay - 1, habitCycle.getDayTasks().size() - 1);
            return habitCycle.getDayTasks().get(actualDayIndex);
        }
        return null;
    }
}