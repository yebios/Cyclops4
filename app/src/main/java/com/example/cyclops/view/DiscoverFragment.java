package com.example.cyclops.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.adapter.TemplateAdapter;
import com.example.cyclops.model.HabitTemplate;
import com.example.cyclops.viewmodel.TemplateViewModel;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    private TemplateViewModel templateViewModel;
    private RecyclerView recyclerViewPopular;
    private RecyclerView recyclerViewCategories;

    private TemplateAdapter popularAdapter;
    private TemplateAdapter categoriesAdapter;

    private TextView tvEmptyPopular;
    private TextView tvEmptyCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        templateViewModel = new ViewModelProvider(this).get(TemplateViewModel.class);

        setupRecyclerViews();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerViewPopular = view.findViewById(R.id.recycler_view_popular);
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);

        tvEmptyPopular = view.findViewById(R.id.tv_empty_popular);
        tvEmptyCategories = view.findViewById(R.id.tv_empty_categories);
    }

    private void setupRecyclerViews() {
        // 定义点击监听器
        TemplateAdapter.OnTemplateClickListener listener = new TemplateAdapter.OnTemplateClickListener() {
            @Override
            public void onTemplateClick(HabitTemplate template) {
                // [新增] 点击卡片 -> 显示预览弹窗
                showTemplatePreviewDialog(template);
            }

            @Override
            public void onImportClick(HabitTemplate template) {
                // 点击按钮 -> 直接导入 (快捷操作)
                importTemplate(template);
            }
        };

        // 1. [修改] 热门模板 -> 改为双列网格
        popularAdapter = new TemplateAdapter(new ArrayList<>(), listener);
        // 使用 GridLayoutManager，2列
        GridLayoutManager popularGridManager = new GridLayoutManager(getContext(), 2);
        recyclerViewPopular.setLayoutManager(popularGridManager);
        recyclerViewPopular.setAdapter(popularAdapter);
        // 禁用自身滚动，解决与 NestedScrollView 的冲突
        recyclerViewPopular.setNestedScrollingEnabled(false);

        // 2. 全部模板 -> 也是双列网格
        categoriesAdapter = new TemplateAdapter(new ArrayList<>(), listener);
        GridLayoutManager categoriesGridManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCategories.setLayoutManager(categoriesGridManager);
        recyclerViewCategories.setAdapter(categoriesAdapter);
        recyclerViewCategories.setNestedScrollingEnabled(false);
    }

    private void observeViewModel() {
        // 观察热门数据
        templateViewModel.getPopularTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                popularAdapter.updateData(templates);
                updateEmptyState(tvEmptyPopular, templates.isEmpty());
            }
        });

        // 观察全部数据
        templateViewModel.getCategoryTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                categoriesAdapter.updateData(templates);
                updateEmptyState(tvEmptyCategories, templates.isEmpty());
            }
        });

        // 观察导入结果消息
        templateViewModel.getImportMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // [新增] 显示模板详情预览弹窗
    private void showTemplatePreviewDialog(HabitTemplate template) {
        if (getContext() == null) return;

        // 1. 加载自定义布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_template_preview, null);

        // 2. 绑定控件
        TextView tvCategory = dialogView.findViewById(R.id.tv_preview_category);
        TextView tvCycle = dialogView.findViewById(R.id.tv_preview_cycle);
        TextView tvTitle = dialogView.findViewById(R.id.tv_preview_title);
        TextView tvDesc = dialogView.findViewById(R.id.tv_preview_desc);
        TextView tvTaskList = dialogView.findViewById(R.id.tv_preview_task_list);
        Button btnClose = dialogView.findViewById(R.id.btn_preview_cancel);
        Button btnImport = dialogView.findViewById(R.id.btn_preview_import);

        // 3. 填充数据
        tvCategory.setText(template.getCategory());
        tvCycle.setText(getString(R.string.template_cycle_days, template.getCycleLength()));
        tvTitle.setText(template.getName());
        tvDesc.setText(template.getDescription());

        // 格式化任务列表
        StringBuilder tasksBuilder = new StringBuilder();
        if (template.getTasks() != null) {
            for (int i = 0; i < template.getTasks().size(); i++) {
                String taskName = template.getTasks().get(i);
                tasksBuilder.append(getString(R.string.day_format, (i + 1)))
                        .append(": ")
                        .append(taskName);
                if (i < template.getTasks().size() - 1) {
                    tasksBuilder.append("\n");
                }
            }
        }
        tvTaskList.setText(tasksBuilder.toString());

        // 4. 创建并显示 Dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        // 设置背景透明，以便显示 CardView 的圆角
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 5. 按钮事件
        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnImport.setOnClickListener(v -> {
            importTemplate(template);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateEmptyState(TextView textView, boolean isEmpty) {
        if (textView != null) {
            textView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
    }

    private void importTemplate(HabitTemplate template) {
        templateViewModel.importTemplate(template);
    }

    @Override
    public void onResume() {
        super.onResume();
        // [核心修复] 每次页面显示时刷新数据，确保语言切换生效
        if (templateViewModel != null) {
            templateViewModel.refreshData();
        }
    }
}
