package com.example.cyclops.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText etSearch; // [新增] 搜索框

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
        setupSearchListener(); // [新增] 设置搜索监听
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerViewPopular = view.findViewById(R.id.recycler_view_popular);
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);

        tvEmptyPopular = view.findViewById(R.id.tv_empty_popular);
        tvEmptyCategories = view.findViewById(R.id.tv_empty_categories);

        etSearch = view.findViewById(R.id.et_search); // [新增]
    }

    private void setupRecyclerViews() {
        TemplateAdapter.OnTemplateClickListener listener = new TemplateAdapter.OnTemplateClickListener() {
            @Override
            public void onTemplateClick(HabitTemplate template) {
                showTemplatePreviewDialog(template);
            }

            @Override
            public void onImportClick(HabitTemplate template) {
                importTemplate(template);
            }
        };

        // 1. 热门模板 (横向)
        popularAdapter = new TemplateAdapter(new ArrayList<>(), listener);
        // 改回横向可能更好看，或者保持你喜欢的双列
        // 这里我根据之前的优化保持双列网格
        GridLayoutManager popularGridManager = new GridLayoutManager(getContext(), 2);
        recyclerViewPopular.setLayoutManager(popularGridManager);
        recyclerViewPopular.setAdapter(popularAdapter);
        recyclerViewPopular.setNestedScrollingEnabled(false);

        // 2. 全部模板 (双列网格) - 搜索结果主要在这里显示
        categoriesAdapter = new TemplateAdapter(new ArrayList<>(), listener);
        GridLayoutManager categoriesGridManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCategories.setLayoutManager(categoriesGridManager);
        recyclerViewCategories.setAdapter(categoriesAdapter);
        recyclerViewCategories.setNestedScrollingEnabled(false);
    }

    // [新增] 搜索监听器
    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入文字变化时，通知 ViewModel 过滤数据
                if (templateViewModel != null) {
                    templateViewModel.searchTemplates(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        // 热门数据
        templateViewModel.getPopularTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                popularAdapter.updateData(templates);
                updateEmptyState(tvEmptyPopular, templates.isEmpty());
            }
        });

        // 观察分类数据 (搜索结果会更新到这里)
        templateViewModel.getCategoryTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                categoriesAdapter.updateData(templates);
                updateEmptyState(tvEmptyCategories, templates.isEmpty());
            }
        });

        templateViewModel.getImportMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTemplatePreviewDialog(HabitTemplate template) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_template_preview, null);

        TextView tvCategory = dialogView.findViewById(R.id.tv_preview_category);
        TextView tvCycle = dialogView.findViewById(R.id.tv_preview_cycle);
        TextView tvTitle = dialogView.findViewById(R.id.tv_preview_title);
        TextView tvDesc = dialogView.findViewById(R.id.tv_preview_desc);
        TextView tvTaskList = dialogView.findViewById(R.id.tv_preview_task_list);
        Button btnClose = dialogView.findViewById(R.id.btn_preview_cancel);
        Button btnImport = dialogView.findViewById(R.id.btn_preview_import);

        tvCategory.setText(template.getCategory());
        tvCycle.setText(getString(R.string.template_cycle_days, template.getCycleLength()));
        tvTitle.setText(template.getName());
        tvDesc.setText(template.getDescription());

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

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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
        if (templateViewModel != null) {
            // 每次页面可见时，重置数据（清空搜索状态，重新加载语言）
            etSearch.setText("");
            templateViewModel.refreshData();
        }
    }
}