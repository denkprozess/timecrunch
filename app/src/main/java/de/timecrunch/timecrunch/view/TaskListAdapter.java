package de.timecrunch.timecrunch.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<TaskModel> taskList;

    public TaskListAdapter(List<TaskModel> taskList){
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_item, viewGroup, false);
        TaskListAdapter.ViewHolder viewHolder = new TaskListAdapter.ViewHolder(view);
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

    public TaskModel deleteItem(int position){
        TaskModel taskToRemove = taskList.get(position);
        taskList.remove(position);
        notifyItemRemoved(position);
        return taskToRemove;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView taskText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
        }
    }
}
