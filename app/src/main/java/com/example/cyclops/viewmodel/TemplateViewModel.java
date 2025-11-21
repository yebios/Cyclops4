package com.example.cyclops.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cyclops.R;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.HabitTemplate;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.RoomHabitRepository;

import java.util.ArrayList;
import java.util.List;

public class TemplateViewModel extends AndroidViewModel {

    private HabitRepository habitRepository;
    private MutableLiveData<List<HabitTemplate>> popularTemplatesLiveData;
    private MutableLiveData<List<HabitTemplate>> recentTemplatesLiveData;
    private MutableLiveData<List<HabitTemplate>> categoryTemplatesLiveData;
    private MutableLiveData<String> importMessageLiveData;

    // [新增] 用于备份完整的"全部模板"列表，以便搜索后恢复
    private List<HabitTemplate> allTemplatesBackup = new ArrayList<>();

    public TemplateViewModel(Application application) {
        super(application);
        this.habitRepository = RoomHabitRepository.getInstance(application);
        this.popularTemplatesLiveData = new MutableLiveData<>();
        this.recentTemplatesLiveData = new MutableLiveData<>();
        this.categoryTemplatesLiveData = new MutableLiveData<>();
        this.importMessageLiveData = new MutableLiveData<>();

        // 初始加载
        refreshData();
    }

    public LiveData<List<HabitTemplate>> getPopularTemplatesLiveData() { return popularTemplatesLiveData; }
    public LiveData<List<HabitTemplate>> getRecentTemplatesLiveData() { return recentTemplatesLiveData; }
    public LiveData<List<HabitTemplate>> getCategoryTemplatesLiveData() { return categoryTemplatesLiveData; }
    public LiveData<String> getImportMessageLiveData() { return importMessageLiveData; }

    public void refreshData() {
        loadPopularTemplates();
        loadRecentTemplates();
        loadAllTemplatesCombined();
    }

    // [新增] 搜索过滤逻辑
    public void searchTemplates(String query) {
        // 如果备份为空，说明还没加载数据，直接返回
        if (allTemplatesBackup == null || allTemplatesBackup.isEmpty()) {
            return;
        }

        // 1. 如果搜索词为空，恢复显示所有数据
        if (query == null || query.trim().isEmpty()) {
            categoryTemplatesLiveData.setValue(new ArrayList<>(allTemplatesBackup));
            return;
        }

        // 2. 执行过滤
        List<HabitTemplate> filteredList = new ArrayList<>();
        // 转为小写以实现不区分大小写的搜索
        String lowerCaseQuery = query.toLowerCase().trim();

        for (HabitTemplate template : allTemplatesBackup) {
            // 匹配规则：标题包含 OR 描述包含 OR 分类包含
            // 因为 template 中的文字已经是根据系统语言加载的（中文或英文），
            // 所以这里直接匹配即可实现"中英文都可搜"。
            boolean matchesName = template.getName().toLowerCase().contains(lowerCaseQuery);
            boolean matchesDesc = template.getDescription().toLowerCase().contains(lowerCaseQuery);
            boolean matchesCat = template.getCategory().toLowerCase().contains(lowerCaseQuery);

            if (matchesName || matchesDesc || matchesCat) {
                filteredList.add(template);
            }
        }

        // 3. 更新 LiveData，UI 会自动刷新只显示匹配项
        categoryTemplatesLiveData.setValue(filteredList);
    }

    private String getString(int resId) {
        return getApplication().getString(resId);
    }

    private void loadPopularTemplates() {
        List<HabitTemplate> templates = new ArrayList<>();
        // ... (这里省略具体的添加代码，保持你原来的不变) ...
        // 1. Fitness
        List<String> fitnessTasks = new ArrayList<>();
        fitnessTasks.add(getString(R.string.task_fitness_d1));
        fitnessTasks.add(getString(R.string.task_fitness_d2));
        fitnessTasks.add(getString(R.string.task_fitness_d3));
        fitnessTasks.add(getString(R.string.task_fitness_d4));
        templates.add(new HabitTemplate("1", getString(R.string.tmpl_fitness_title), getString(R.string.tmpl_fitness_desc), getString(R.string.tmpl_fitness_cat), 4, fitnessTasks, 1250, 4.8f));

        // 2. Sleep
        List<String> sleepTasks = new ArrayList<>();
        sleepTasks.add(getString(R.string.task_sleep_d1));
        sleepTasks.add(getString(R.string.task_sleep_d2));
        sleepTasks.add(getString(R.string.task_sleep_d3));
        templates.add(new HabitTemplate("2", getString(R.string.tmpl_sleep_title), getString(R.string.tmpl_sleep_desc), getString(R.string.tmpl_sleep_cat), 3, sleepTasks, 980, 4.6f));

        // 5. Morning
        List<String> morningTasks = new ArrayList<>();
        morningTasks.add(getString(R.string.task_morning_d1));
        morningTasks.add(getString(R.string.task_morning_d2));
        morningTasks.add(getString(R.string.task_morning_d3));
        templates.add(new HabitTemplate("5", getString(R.string.tmpl_morning_title), getString(R.string.tmpl_morning_desc), getString(R.string.tmpl_morning_cat), 3, morningTasks, 850, 4.7f));

        popularTemplatesLiveData.setValue(templates);
    }

    private void loadRecentTemplates() {
        List<HabitTemplate> templates = new ArrayList<>();
        // ... (这里省略具体的添加代码，保持你原来的不变) ...
        // 3. Coding
        List<String> codingTasks = new ArrayList<>();
        codingTasks.add(getString(R.string.task_coding_d1));
        codingTasks.add(getString(R.string.task_coding_d2));
        codingTasks.add(getString(R.string.task_coding_d3));
        templates.add(new HabitTemplate("3", getString(R.string.tmpl_coding_title), getString(R.string.tmpl_coding_desc), getString(R.string.tmpl_coding_cat), 3, codingTasks, 45, 4.9f));

        // 4. Water
        List<String> waterTasks = new ArrayList<>();
        waterTasks.add(getString(R.string.task_water_d1));
        waterTasks.add(getString(R.string.task_water_d2));
        templates.add(new HabitTemplate("4", getString(R.string.tmpl_water_title), getString(R.string.tmpl_water_desc), getString(R.string.tmpl_water_cat), 2, waterTasks, 120, 4.2f));

        // 6. Focus
        List<String> focusTasks = new ArrayList<>();
        focusTasks.add(getString(R.string.task_focus_d1));
        focusTasks.add(getString(R.string.task_focus_d2));
        focusTasks.add(getString(R.string.task_focus_d3));
        templates.add(new HabitTemplate("6", getString(R.string.tmpl_focus_title), getString(R.string.tmpl_focus_desc), getString(R.string.tmpl_focus_cat), 3, focusTasks, 340, 4.9f));

        // 7. Detox
        List<String> detoxTasks = new ArrayList<>();
        detoxTasks.add(getString(R.string.task_detox_d1));
        detoxTasks.add(getString(R.string.task_detox_d2));
        templates.add(new HabitTemplate("7", getString(R.string.tmpl_detox_title), getString(R.string.tmpl_detox_desc), getString(R.string.tmpl_detox_cat), 2, detoxTasks, 210, 4.5f));

        // 8. Reading
        List<String> readTasks = new ArrayList<>();
        readTasks.add(getString(R.string.task_read_d1));
        readTasks.add(getString(R.string.task_read_d2));
        templates.add(new HabitTemplate("8", getString(R.string.tmpl_read_title), getString(R.string.tmpl_read_desc), getString(R.string.tmpl_read_cat), 2, readTasks, 500, 4.8f));

        recentTemplatesLiveData.setValue(templates);
    }

    // [修改] 加载全部数据并建立备份
    private void loadAllTemplatesCombined() {
        List<HabitTemplate> allTemplates = new ArrayList<>();
        if (popularTemplatesLiveData.getValue() != null) {
            allTemplates.addAll(popularTemplatesLiveData.getValue());
        }
        if (recentTemplatesLiveData.getValue() != null) {
            allTemplates.addAll(recentTemplatesLiveData.getValue());
        }

        // 更新 UI 显示列表
        categoryTemplatesLiveData.setValue(allTemplates);

        // [关键] 更新备份列表 (创建一个新列表，而不是引用)
        allTemplatesBackup = new ArrayList<>(allTemplates);
    }

    public void importTemplate(HabitTemplate template) {
        if (template == null) return;

        HabitCycle newHabit = new HabitCycle();
        newHabit.setName(template.getName());
        newHabit.setDescription(template.getDescription());
        newHabit.setCycleLength(template.getCycleLength());
        newHabit.setUserId("");

        List<DayTask> dayTasks = new ArrayList<>();
        if (template.getTasks() != null) {
            for (int i = 0; i < template.getTasks().size(); i++) {
                String taskName = template.getTasks().get(i);
                dayTasks.add(new DayTask(i + 1, taskName));
            }
        }
        newHabit.setDayTasks(dayTasks);

        habitRepository.addHabitCycle(newHabit);
        importMessageLiveData.setValue(getApplication().getString(R.string.toast_import_success));
    }
}