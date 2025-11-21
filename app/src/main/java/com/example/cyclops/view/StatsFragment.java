package com.example.cyclops.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cyclops.R;
import com.example.cyclops.HabitCycleEngine; // 核心引擎，用于计算单个成功率
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private TextView tvTotalHabits;
    private TextView tvTotalCompletions;
    private TextView tvBestStreak;
    private TextView tvSuccessRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 获取 ViewModel
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        tvTotalHabits = view.findViewById(R.id.tv_total_habits);
        tvTotalCompletions = view.findViewById(R.id.tv_total_completions);
        tvBestStreak = view.findViewById(R.id.tv_best_streak);
        tvSuccessRate = view.findViewById(R.id.tv_success_rate);
    }

    private void observeViewModel() {
        // 观察所有习惯的数据变化
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), this::calculateAndShowStats);
    }

    /**
     * 计算并显示统计数据 (方案 A: 平均成功率)
     */
    private void calculateAndShowStats(List<HabitCycle> habits) {
        if (habits == null) return;

        int totalHabitsCount = habits.size();
        int grandTotalCompletions = 0; // 总循环完成次数
        int maxGlobalStreak = 0;       // 最高连续记录
        double sumOfSuccessRates = 0.0; // 所有习惯成功率的总和

        for (HabitCycle habit : habits) {
            // 1. 累加循环次数 (完整循环数)
            grandTotalCompletions += habit.getTotalCompletions();

            // 2. 寻找最佳 Streak (在所有习惯中找最大的)
            if (habit.getBestStreak() > maxGlobalStreak) {
                maxGlobalStreak = habit.getBestStreak();
            }

            // 3. [方案A核心] 计算单个习惯的成功率并累加
            // HabitCycleEngine.calculateSuccessRate 返回 0.0 ~ 100.0
            double singleRate = HabitCycleEngine.calculateSuccessRate(habit);
            sumOfSuccessRates += singleRate;
        }

        // 4. 计算平均成功率 = 总和 / 数量
        double averageSuccessRate = 0.0;
        if (totalHabitsCount > 0) {
            averageSuccessRate = sumOfSuccessRates / totalHabitsCount;
        }

        // 更新 UI
        tvTotalHabits.setText(String.valueOf(totalHabitsCount));
        tvTotalCompletions.setText(String.valueOf(grandTotalCompletions));
        tvBestStreak.setText(String.valueOf(maxGlobalStreak));

        // 格式化显示 (保留1位小数，例如 85.5%)
        tvSuccessRate.setText(String.format(Locale.getDefault(), "%.1f%%", averageSuccessRate));
    }
}