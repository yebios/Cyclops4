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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.adapter.TemplateAdapter;
import com.example.cyclops.model.HabitTemplate;
import com.example.cyclops.viewmodel.TemplateViewModel;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    private TemplateViewModel templateViewModel;
    private RecyclerView recyclerViewPopular;
    private RecyclerView recyclerViewRecent;
    private RecyclerView recyclerViewCategories;
    private TemplateAdapter popularAdapter;
    private TemplateAdapter recentAdapter;
    private TemplateAdapter categoriesAdapter;
    private TextView tvEmptyPopular;
    private TextView tvEmptyRecent;
    private TextView tvEmptyCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews(view);
        setupRecyclerViews();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        templateViewModel = new ViewModelProvider(this).get(TemplateViewModel.class);
        observeViewModel();
        loadTemplates();
    }

    private void initViews(View view) {
        recyclerViewPopular = view.findViewById(R.id.recycler_view_popular);
        recyclerViewRecent = view.findViewById(R.id.recycler_view_recent);
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);

        tvEmptyPopular = view.findViewById(R.id.tv_empty_popular);
        tvEmptyRecent = view.findViewById(R.id.tv_empty_recent);
        tvEmptyCategories = view.findViewById(R.id.tv_empty_categories);
    }

    private void setupRecyclerViews() {
        // 热门模板
        popularAdapter = new TemplateAdapter(new ArrayList<>(), new TemplateAdapter.OnTemplateClickListener() {
            @Override
            public void onTemplateClick(HabitTemplate template) {
                openTemplateDetail(template);
            }

            @Override
            public void onImportClick(HabitTemplate template) {
                importTemplate(template);
            }
        });

        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPopular.setAdapter(popularAdapter);

        // 最新模板
        recentAdapter = new TemplateAdapter(new ArrayList<>(), new TemplateAdapter.OnTemplateClickListener() {
            @Override
            public void onTemplateClick(HabitTemplate template) {
                openTemplateDetail(template);
            }

            @Override
            public void onImportClick(HabitTemplate template) {
                importTemplate(template);
            }
        });

        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecent.setAdapter(recentAdapter);

        // 分类模板
        categoriesAdapter = new TemplateAdapter(new ArrayList<>(), new TemplateAdapter.OnTemplateClickListener() {
            @Override
            public void onTemplateClick(HabitTemplate template) {
                openTemplateDetail(template);
            }

            @Override
            public void onImportClick(HabitTemplate template) {
                importTemplate(template);
            }
        });

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(categoriesAdapter);
    }

    private void observeViewModel() {
        templateViewModel.getPopularTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                popularAdapter.updateData(templates);
                updateEmptyState(tvEmptyPopular, templates.isEmpty());
            }
        });

        templateViewModel.getRecentTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                recentAdapter.updateData(templates);
                updateEmptyState(tvEmptyRecent, templates.isEmpty());
            }
        });

        templateViewModel.getCategoryTemplatesLiveData().observe(getViewLifecycleOwner(), templates -> {
            if (templates != null) {
                categoriesAdapter.updateData(templates);
                updateEmptyState(tvEmptyCategories, templates.isEmpty());
            }
        });

        templateViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // 显示错误信息
                // Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTemplates() {
        templateViewModel.loadPopularTemplates();
        templateViewModel.loadRecentTemplates();
        templateViewModel.loadCategoryTemplates("健身");
    }

    private void updateEmptyState(TextView textView, boolean isEmpty) {
        textView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void openTemplateDetail(HabitTemplate template) {
        // 打开模板详情
        // Intent intent = new Intent(getContext(), TemplateDetailActivity.class);
        // intent.putExtra("TEMPLATE_ID", template.getId());
        // startActivity(intent);
    }

    private void importTemplate(HabitTemplate template) {
        templateViewModel.importTemplate(template);
        // 显示导入成功提示
        // Toast.makeText(getContext(), "模板导入成功！", Toast.LENGTH_SHORT).show();
    }
}