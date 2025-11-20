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
import com.example.cyclops.viewmodel.TodayViewModel;

public class StatsFragment extends Fragment {

    private TodayViewModel todayViewModel;
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
        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        tvTotalHabits = view.findViewById(R.id.tv_total_habits);
        tvTotalCompletions = view.findViewById(R.id.tv_total_completions);
        tvBestStreak = view.findViewById(R.id.tv_best_streak);
        tvSuccessRate = view.findViewById(R.id.tv_success_rate);
    }

    private void observeViewModel() {
        todayViewModel.getTodayHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                updateStats(habits.size(), todayViewModel.getCompletedCountLiveData().getValue());
            }
        });

        todayViewModel.getCompletedCountLiveData().observe(getViewLifecycleOwner(), completedCount -> {
            if (completedCount != null) {
                updateStats(todayViewModel.getTotalCountLiveData().getValue(), completedCount);
            }
        });

        todayViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), totalCount -> {
            if (totalCount != null) {
                updateStats(totalCount, todayViewModel.getCompletedCountLiveData().getValue());
            }
        });
    }

    private void updateStats(Integer totalCount, Integer completedCount) {
        if (totalCount == null || completedCount == null) {
            return;
        }

        int totalHabits = totalCount;
        int totalCompletions = completedCount;

        // The logic for bestStreak and successRate might need to be adjusted
        // based on how you want to calculate them. For now, we'll use a simplified approach.
        int bestStreak = 0; // This would require more complex logic to calculate accurately.
        double successRate = totalHabits > 0 ? (double) totalCompletions / totalHabits * 100 : 0;

        tvTotalHabits.setText(String.valueOf(totalHabits));
        tvTotalCompletions.setText(String.valueOf(totalCompletions));
        tvBestStreak.setText(String.valueOf(bestStreak)); // Placeholder
        tvSuccessRate.setText(String.format("%.1f%%", successRate));
    }
}