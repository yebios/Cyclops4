package com.example.cyclops.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.adapter.HabitCycleAdapter;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.TodayViewModel;

public class TodayFragment extends Fragment {

    private TodayViewModel todayViewModel;
    private RecyclerView recyclerView;
    private HabitCycleAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvCompletedCount;
    private TextView tvTotalCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        todayViewModel = new ViewModelProvider(requireActivity()).get(TodayViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_today);
        progressBar = view.findViewById(R.id.progress_bar_today);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
    }

    private void setupRecyclerView() {
        adapter = new HabitCycleAdapter(null, new HabitCycleAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(HabitCycle habitCycle) {
                // 打开习惯详情
                openHabitDetail(habitCycle);
            }

            @Override
            public void onCompleteClick(HabitCycle habitCycle) {
                // 完成任务
                todayViewModel.completeTask(habitCycle.getId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        todayViewModel.getTodayHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter.updateData(habits);
                updateProgress(habits.size());
            }
        });

        todayViewModel.getCompletedCountLiveData().observe(getViewLifecycleOwner(), completedCount -> {
            if (completedCount != null) {
                tvCompletedCount.setText(String.valueOf(completedCount));
            }
        });

        todayViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // 显示错误信息
                // Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgress(int totalCount) {
        tvTotalCount.setText(String.valueOf(totalCount));
        if (totalCount > 0) {
            Integer completedCount = todayViewModel.getCompletedCountLiveData().getValue();
            if (completedCount != null) {
                int progress = (completedCount * 100) / totalCount;
                progressBar.setProgress(progress);
            }
        }
    }

    private void openHabitDetail(HabitCycle habitCycle) {
        // 打开习惯详情页面的逻辑
        // Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        // intent.putExtra("HABIT_ID", habitCycle.getId());
        // startActivity(intent);
    }
}