package com.example.cyclops.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cyclops.model.HabitCycle;
import com.example.cyclops.model.HabitTemplate;
import com.example.cyclops.model.DayTask;
import com.example.cyclops.repository.HabitRepository;
import com.example.cyclops.repository.HabitRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class TemplateViewModel extends ViewModel {

    private HabitRepository habitRepository;
    private MutableLiveData<List<HabitTemplate>> popularTemplatesLiveData;
    private MutableLiveData<List<HabitTemplate>> recentTemplatesLiveData;
    private MutableLiveData<List<HabitTemplate>> categoryTemplatesLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public TemplateViewModel() {
        this.habitRepository = HabitRepositoryImpl.getInstance();
        this.popularTemplatesLiveData = new MutableLiveData<>();
        this.recentTemplatesLiveData = new MutableLiveData<>();
        this.categoryTemplatesLiveData = new MutableLiveData<>();
        this.errorMessageLiveData = new MutableLiveData<>();
    }

    public LiveData<List<HabitTemplate>> getPopularTemplatesLiveData() {
        return popularTemplatesLiveData;
    }

    public LiveData<List<HabitTemplate>> getRecentTemplatesLiveData() {
        return recentTemplatesLiveData;
    }

    public LiveData<List<HabitTemplate>> getCategoryTemplatesLiveData() {
        return categoryTemplatesLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void loadPopularTemplates() {
        try {
            List<HabitTemplate> popularTemplates = getSamplePopularTemplates();
            popularTemplatesLiveData.setValue(popularTemplates);
        } catch (Exception e) {
            errorMessageLiveData.setValue("加载热门模板失败: " + e.getMessage());
        }
    }

    public void loadRecentTemplates() {
        try {
            List<HabitTemplate> recentTemplates = getSampleRecentTemplates();
            recentTemplatesLiveData.setValue(recentTemplates);
        } catch (Exception e) {
            errorMessageLiveData.setValue("加载最新模板失败: " + e.getMessage());
        }
    }

    public void loadCategoryTemplates(String category) {
        try {
            List<HabitTemplate> categoryTemplates = getSampleCategoryTemplates(category);
            categoryTemplatesLiveData.setValue(categoryTemplates);
        } catch (Exception e) {
            errorMessageLiveData.setValue("加载分类模板失败: " + e.getMessage());
        }
    }

    public void importTemplate(HabitTemplate template) {
        try {
            // 将模板转换为习惯循环并添加到用户的习惯中
            HabitCycle habitCycle = convertTemplateToHabitCycle(template);
            habitRepository.addHabitCycle(habitCycle);
        } catch (Exception e) {
            errorMessageLiveData.setValue("导入模板失败: " + e.getMessage());
        }
    }

    private HabitCycle convertTemplateToHabitCycle(HabitTemplate template) {
        HabitCycle habitCycle = new HabitCycle();
        habitCycle.setName(template.getName());
        habitCycle.setDescription(template.getDescription());
        habitCycle.setCycleLength(template.getCycleLength());

        List<DayTask> dayTasks = new ArrayList<>();
        for (String task : template.getTasks()) {
            dayTasks.add(new DayTask(dayTasks.size() + 1, task));
        }
        habitCycle.setDayTasks(dayTasks);

        return habitCycle;
    }

    private List<HabitTemplate> getSamplePopularTemplates() {
        List<HabitTemplate> templates = new ArrayList<>();

        // 示例模板1：健身计划
        List<String> fitnessTasks = new ArrayList<>();
        fitnessTasks.add("胸部训练：卧推、飞鸟");
        fitnessTasks.add("背部训练：引体向上、划船");
        fitnessTasks.add("腿部训练：深蹲、硬拉");
        fitnessTasks.add("休息日或轻度有氧");

        templates.add(new HabitTemplate(
                "1", "3天健身循环", "科学的肌肉训练计划",
                "健身", 4, fitnessTasks, 1500, 4.8f
        ));

        // 示例模板2：晨间习惯
        List<String> morningTasks = new ArrayList<>();
        morningTasks.add("冥想5分钟 + 喝水");
        morningTasks.add("晨间阅读15分钟");
        morningTasks.add("简单拉伸运动");
        morningTasks.add("计划今日任务");
        morningTasks.add("感恩日记");

        templates.add(new HabitTemplate(
                "2", "高效晨间习惯", "开启高效一天",
                "生活", 5, morningTasks, 890, 4.6f
        ));

        return templates;
    }

    private List<HabitTemplate> getSampleRecentTemplates() {
        List<HabitTemplate> templates = new ArrayList<>();

        // 示例模板：语言学习
        List<String> languageTasks = new ArrayList<>();
        languageTasks.add("新词汇学习（20个）");
        languageTasks.add("语法练习");
        languageTasks.add("听力训练");
        languageTasks.add("口语练习");
        languageTasks.add("阅读文章");
        languageTasks.add("写作练习");
        languageTasks.add("复习日");

        templates.add(new HabitTemplate(
                "3", "7天语言学习", "全面提升语言能力",
                "学习", 7, languageTasks, 320, 4.7f
        ));

        // 示例模板：编程学习
        List<String> codingTasks = new ArrayList<>();
        codingTasks.add("算法练习");
        codingTasks.add("项目开发");
        codingTasks.add("技术文档阅读");
        codingTasks.add("代码重构练习");

        templates.add(new HabitTemplate(
                "4", "编程技能提升", "系统化编程训练",
                "学习", 4, codingTasks, 210, 4.5f
        ));

        return templates;
    }

    private List<HabitTemplate> getSampleCategoryTemplates(String category) {
        List<HabitTemplate> templates = new ArrayList<>();

        if ("健身".equals(category)) {
            List<String> yogaTasks = new ArrayList<>();
            yogaTasks.add("基础体式练习");
            yogaTasks.add("流瑜伽序列");
            yogaTasks.add("阴瑜伽放松");
            yogaTasks.add("呼吸与冥想");

            templates.add(new HabitTemplate(
                    "5", "瑜伽入门计划", "身心平衡的瑜伽练习",
                    "健身", 4, yogaTasks, 430, 4.9f
            ));
        }

        return templates;
    }
}