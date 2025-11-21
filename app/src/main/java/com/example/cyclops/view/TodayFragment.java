package com.example.cyclops.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

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
        adapter = new HabitCycleAdapter(new ArrayList<>(), new HabitCycleAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(HabitCycle habitCycle) {
                openHabitDetail(habitCycle);
            }

            @Override
            public void onCompleteClick(HabitCycle habitCycle) {
                // 点击打卡
                todayViewModel.completeTask(habitCycle.getId());

                // 显示提示
                String msg = getString(R.string.checking_in_toast, habitCycle.getName());
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        // [修复] 这里调用正确的方法名 getHabitsLiveData()
        todayViewModel.getHabitsLiveData().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                adapter.updateData(habits);
            }
        });

        // 观察已完成数量
        todayViewModel.getCompletedCountLiveData().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvCompletedCount.setText(String.valueOf(count));
                updateProgressBar();
            }
        });

        // 观察总数量
        todayViewModel.getTotalCountLiveData().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvTotalCount.setText(String.valueOf(count));
                updateProgressBar();
            }
        });

        // 观察错误信息
        todayViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressBar() {
        Integer completed = todayViewModel.getCompletedCountLiveData().getValue();
        Integer total = todayViewModel.getTotalCountLiveData().getValue();

        if (completed != null && total != null && total > 0) {
            int progress = (completed * 100) / total;
            progressBar.setProgress(progress);
        } else {
            progressBar.setProgress(0);
        }
    }

    private void openHabitDetail(HabitCycle habitCycle) {
        Intent intent = new Intent(getContext(), HabitDetailActivity.class);
        intent.putExtra("HABIT_ID", habitCycle.getId());
        startActivity(intent);
    }
}