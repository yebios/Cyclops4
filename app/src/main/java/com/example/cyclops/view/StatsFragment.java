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
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.List;

public class StatsFragment extends Fragment {

    private HabitViewModel habitViewModel;
    // 暂时注释掉图表相关变量
    // private BarChart barChart;
    // private LineChart lineChart;
    private TextView tvTotalHabits;
    private TextView tvTotalCompletions;
    private TextView tvBestStreak;
    private TextView tvSuccessRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        initViews(view);
        // setupCharts();  // 暂时注释
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        // barChart = view.findViewById(R.id.bar_chart);
        // lineChart = view.findViewById(R.id.line_chart);
        tvTotalHabits = view.findViewById(R.id.tv_total_habits);
        tvTotalCompletions = view.findViewById(R.id.tv_total_completions);
        tvBestStreak = view.findViewById(R.id.tv_best_streak);
        tvSuccessRate = view.findViewById(R.id.tv_success_rate);
    }

    private void setupCharts() {
        // setupBarChart();
        // setupLineChart();
    }

    private void setupBarChart() {
        // 暂时注释图表配置代码
        /*
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setScaleEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        barChart.getAxisRight().setEnabled(false);
        */
    }

    private void setupLineChart() {
        // 暂时注释图表配置代码
        /*
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setScaleEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        lineChart.getAxisRight().setEnabled(false);
        */
    }

    private void observeViewModel() {
        habitViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                updateStats(habits);
                // updateCharts(habits);  // 暂时注释
            }
        });
    }

    private void updateStats(List<HabitCycle> habits) {
        int totalHabits = habits.size();
        int totalCompletions = 0;
        int bestStreak = 0;
        int totalPossibleCompletions = totalHabits * 30; // 假设30天

        for (HabitCycle habit : habits) {
            totalCompletions += habit.getTotalCompletions();
            if (habit.getCurrentStreak() > bestStreak) {
                bestStreak = habit.getCurrentStreak();
            }
        }

        double successRate = totalPossibleCompletions > 0 ?
                (double) totalCompletions / totalPossibleCompletions * 100 : 0;

        tvTotalHabits.setText(String.valueOf(totalHabits));
        tvTotalCompletions.setText(String.valueOf(totalCompletions));
        tvBestStreak.setText(String.valueOf(bestStreak));
        tvSuccessRate.setText(String.format("%.1f%%", successRate));
    }

    private void updateCharts(List<HabitCycle> habits) {
        // updateBarChart(habits);
        // updateLineChart(habits);
    }

    private void updateBarChart(List<HabitCycle> habits) {
        // 暂时注释图表更新代码
        /*
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < Math.min(habits.size(), 5); i++) {
            HabitCycle habit = habits.get(i);
            entries.add(new BarEntry(i, habit.getTotalCompletions()));
            labels.add(habit.getName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "完成次数");
        dataSet.setColor(Color.parseColor("#6200EE"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(barData);
        barChart.invalidate();
        */
    }

    private void updateLineChart(List<HabitCycle> habits) {
        // 暂时注释图表更新代码
        /*
        List<Entry> entries = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        for (int i = 0; i < 7; i++) {
            float completions = (float) (Math.random() * 5 + 2);
            entries.add(new Entry(i, completions));
        }

        LineDataSet dataSet = new LineDataSet(entries, "每日完成趋势");
        dataSet.setColor(Color.parseColor("#03DAC5"));
        dataSet.setCircleColor(Color.parseColor("#03DAC5"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        lineChart.setData(lineData);
        lineChart.invalidate();
        */
    }
}