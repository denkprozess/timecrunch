package de.timecrunch.timecrunch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;

/*
    Adapter for the CategoryTaskList in a TimeBlock
 */
public class TimeBlockCategoryTaskListAdapter extends RecyclerView.Adapter<TimeBlockCategoryTaskListAdapter.ViewHolder> {

    private List<TaskModel> taskList;

    public TimeBlockCategoryTaskListAdapter(List<TaskModel> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_block_task_list_item, viewGroup, false);
        TimeBlockCategoryTaskListAdapter.ViewHolder viewHolder = new TimeBlockCategoryTaskListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String taskText = taskList.get(i).getText();
        viewHolder.taskText.setText(taskText);

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
        }
    }
}
