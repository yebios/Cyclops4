package com.example.cyclops.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.HabitCycleEngine;

import java.util.List;

public class HabitCycleAdapter extends RecyclerView.Adapter<HabitCycleAdapter.HabitViewHolder> {

    private List<HabitCycle> habitCycles;
    private OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onHabitClick(HabitCycle habitCycle);
        void onCompleteClick(HabitCycle habitCycle);
    }

    public HabitCycleAdapter(List<HabitCycle> habitCycles, OnHabitClickListener listener) {
        this.habitCycles = habitCycles;
        this.listener = listener;
    }

    public void updateData(List<HabitCycle> newHabitCycles) {
        this.habitCycles = newHabitCycles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_cycle, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        HabitCycle habitCycle = habitCycles.get(position);
        holder.bind(habitCycle, listener);
    }

    @Override
    public int getItemCount() {
        return habitCycles != null ? habitCycles.size() : 0;
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        private TextView habitName;
        private TextView habitDescription;
        private TextView currentDay;
        private ProgressBar progressBar;
        private TextView progressText;
        private Button completeButton;
        private TextView tvStreak;
        private TextView tvCompletions;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.tv_habit_name);
            habitDescription = itemView.findViewById(R.id.tv_habit_description);
            currentDay = itemView.findViewById(R.id.tv_current_day);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressText = itemView.findViewById(R.id.tv_progress);
            completeButton = itemView.findViewById(R.id.btn_complete);
            tvStreak = itemView.findViewById(R.id.tv_streak);
            tvCompletions = itemView.findViewById(R.id.tv_completions);
        }

        public void bind(HabitCycle habitCycle, OnHabitClickListener listener) {
            habitName.setText(habitCycle.getName());
            habitDescription.setText(habitCycle.getDescription());

            // 1. 显示当前是“理论上”的第几天 (指导用户今天该做哪个任务)
            int currentDayNumber = HabitCycleEngine.calculateCurrentDay(habitCycle);
            currentDay.setText(itemView.getContext().getString(R.string.day_format, currentDayNumber));

            // 2. 获取有效 Streak (用于计算真实进度)
            int effectiveStreak = habitCycle.getCurrentStreak();
            // 如果断签且今天没补救，视为 0
            boolean isStreakBroken = HabitCycleEngine.isStreakBroken(habitCycle);
            boolean isCompletedToday = HabitCycleEngine.isCompletedToday(habitCycle);

            if (isStreakBroken && !isCompletedToday) {
                effectiveStreak = 0;
            }

            // 3. [核心修改] 基于 Streak 计算可视化进度
            // 逻辑：进度条显示的是“当前循环完成了几天”
            int cycleLength = Math.max(1, habitCycle.getCycleLength());

            // 取模：例如 Streak 4，Cycle 3 -> 4 % 3 = 1 (新循环第1天)
            int progressCount = effectiveStreak % cycleLength;

            // 特殊情况：刚好完成一个循环 (例如 Streak 3, Cycle 3 -> 3%3=0)
            // 如果是今天刚完成的，我们希望看到满格 (3/3)，而不是空 (0/3)
            // 如果是昨天完成的，今天还没开始，那确实应该显示空 (新循环开始)
            if (effectiveStreak > 0 && effectiveStreak % cycleLength == 0) {
                if (isCompletedToday) {
                    progressCount = cycleLength; // 显示满格
                } else {
                    progressCount = 0; // 显示空 (准备开始下一轮)
                }
            }

            int progressPercent = (progressCount * 100) / cycleLength;

            // 4. 更新进度条 UI
            progressBar.setProgress(progressPercent);
            progressText.setText(itemView.getContext().getString(R.string.progress_format, progressPercent));

            // 5. 绑定统计数据文字
            if (tvStreak != null) {
                tvStreak.setText(String.valueOf(effectiveStreak));
            }
            if (tvCompletions != null) {
                tvCompletions.setText(String.valueOf(habitCycle.getTotalCompletions()));
            }

            // 6. 按钮状态
            if (isCompletedToday) {
                completeButton.setText(R.string.btn_completed_today);
                completeButton.setEnabled(false);
                completeButton.setAlpha(0.5f);
            } else {
                completeButton.setText(R.string.btn_check_in);
                completeButton.setEnabled(true);
                completeButton.setAlpha(1.0f);
            }

            // 7. 点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onHabitClick(habitCycle);
            });

            completeButton.setOnClickListener(v -> {
                if (listener != null) listener.onCompleteClick(habitCycle);
            });
        }
    }
}