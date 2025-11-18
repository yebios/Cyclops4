package com.example.cyclops.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cyclops.R;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.viewmodel.HabitViewModel;

import java.util.ArrayList;
import java.util.List;

public class HabitCreationActivity extends AppCompatActivity {

    private EditText etHabitName;
    private EditText etHabitDescription;
    private Spinner spinnerCycleLength;
    private LinearLayout layoutDayTasks;
    private Button btnCreateHabit;

    private HabitViewModel habitViewModel;
    private int currentCycleLength = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_creation);

        initViews();
        setupSpinner();
        setupClickListeners();

        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
    }

    private void initViews() {
        etHabitName = findViewById(R.id.et_habit_name);
        etHabitDescription = findViewById(R.id.et_habit_description);
        spinnerCycleLength = findViewById(R.id.spinner_cycle_length);
        layoutDayTasks = findViewById(R.id.layout_day_tasks);
        btnCreateHabit = findViewById(R.id.btn_create_habit);
    }

    private void setupSpinner() {
        // 创建适配器
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.cycle_length_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycleLength.setAdapter(adapter);

        spinnerCycleLength.setSelection(0); // 默认选择3天

        // 监听选择变化
        spinnerCycleLength.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentCycleLength = position + 3; // 3, 4, 5, 6, 7天
                updateDayTaskInputs();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // 初始创建输入框
        updateDayTaskInputs();
    }

    private void setupClickListeners() {
        btnCreateHabit.setOnClickListener(v -> createHabitCycle());

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void updateDayTaskInputs() {
        layoutDayTasks.removeAllViews();

        for (int i = 1; i <= currentCycleLength; i++) {
            View dayTaskView = getLayoutInflater().inflate(R.layout.item_day_task_input, layoutDayTasks, false);

            TextView tvDayNumber = dayTaskView.findViewById(R.id.tv_day_number);
            EditText etTaskName = dayTaskView.findViewById(R.id.et_task_name);

            tvDayNumber.setText("第 " + i + " 天");
            etTaskName.setHint("输入第 " + i + " 天的任务");
            etTaskName.setTag(i); // 用tag标记天数

            layoutDayTasks.addView(dayTaskView);
        }
    }

    private void createHabitCycle() {
        String habitName = etHabitName.getText().toString().trim();
        String habitDescription = etHabitDescription.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(habitName)) {
            Toast.makeText(this, "请输入习惯名称", Toast.LENGTH_SHORT).show();
            return;
        }

        // 收集每日任务
        List<DayTask> dayTasks = new ArrayList<>();
        for (int i = 0; i < layoutDayTasks.getChildCount(); i++) {
            View dayTaskView = layoutDayTasks.getChildAt(i);
            EditText etTaskName = dayTaskView.findViewById(R.id.et_task_name);
            String taskName = etTaskName.getText().toString().trim();

            if (TextUtils.isEmpty(taskName)) {
                taskName = "第 " + (i + 1) + " 天"; // 默认任务名
            }

            dayTasks.add(new DayTask(i + 1, taskName));
        }

        // 创建习惯循环
        HabitCycle habitCycle = new HabitCycle();
        habitCycle.setName(habitName);
        habitCycle.setDescription(habitDescription);
        habitCycle.setCycleLength(currentCycleLength);
        habitCycle.setDayTasks(dayTasks);

        // 保存到ViewModel
        habitViewModel.addHabitCycle(habitCycle);

        Toast.makeText(this, "习惯创建成功！", Toast.LENGTH_SHORT).show();
        finish();
    }
}