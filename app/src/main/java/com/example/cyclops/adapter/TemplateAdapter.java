package com.example.cyclops.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cyclops.R;
import com.example.cyclops.model.HabitTemplate;

import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private List<HabitTemplate> templates;
    private OnTemplateClickListener listener;

    public interface OnTemplateClickListener {
        void onTemplateClick(HabitTemplate template);
        void onImportClick(HabitTemplate template);
    }

    public TemplateAdapter(List<HabitTemplate> templates, OnTemplateClickListener listener) {
        this.templates = templates;
        this.listener = listener;
    }

    public void updateData(List<HabitTemplate> newTemplates) {
        this.templates = newTemplates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载新的卡片布局 item_template
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        HabitTemplate template = templates.get(position);
        holder.bind(template, listener);
    }

    @Override
    public int getItemCount() {
        return templates != null ? templates.size() : 0;
    }

    static class TemplateViewHolder extends RecyclerView.ViewHolder {
        private TextView templateName;
        private TextView templateDescription;
        private TextView templateCategory;
        private TextView templateCycleLength;
        private TextView downloadCount;
        private TextView ratingText; // [新增] 数字评分
        private Button importButton;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            templateName = itemView.findViewById(R.id.tv_template_name);
            templateDescription = itemView.findViewById(R.id.tv_template_description);
            templateCategory = itemView.findViewById(R.id.tv_template_category);
            templateCycleLength = itemView.findViewById(R.id.tv_cycle_length);
            downloadCount = itemView.findViewById(R.id.tv_download_count);
            ratingText = itemView.findViewById(R.id.tv_rating_text); // [新增]
            importButton = itemView.findViewById(R.id.btn_import);
        }

        public void bind(HabitTemplate template, OnTemplateClickListener listener) {
            templateName.setText(template.getName());
            templateDescription.setText(template.getDescription());
            templateCategory.setText(template.getCategory());

            // 国际化格式化字符串
            templateCycleLength.setText(itemView.getContext().getString(R.string.template_cycle_days, template.getCycleLength()));
            downloadCount.setText(template.getFormattedDownloadCount()); // 这里直接显示数量，或者用 string 资源拼接

            // 设置评分文字 (例如 "4.8")
            if (ratingText != null) {
                ratingText.setText(String.valueOf(template.getRating()));
            }

            // 设置按钮文字
            importButton.setText(R.string.btn_import_one_click);

            // 卡片整体点击
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onTemplateClick(template);
            });

            // 导入按钮点击
            importButton.setOnClickListener(v -> {
                if (listener != null) listener.onImportClick(template);
            });
        }
    }
}