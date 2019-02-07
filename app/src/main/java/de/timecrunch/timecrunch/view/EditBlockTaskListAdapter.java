package de.timecrunch.timecrunch.view;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;

public class EditBlockTaskListAdapter extends RecyclerView.Adapter<EditBlockTaskListAdapter.ViewHolder> {

    private List<TimeBlockTaskModel> taskList;

    public EditBlockTaskListAdapter(List<TimeBlockTaskModel> taskList){
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_block_task_list_item, viewGroup, false);
        EditBlockTaskListAdapter.ViewHolder viewHolder = new EditBlockTaskListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String taskText = taskList.get(i).getTask().getText();
        viewHolder.taskText.setText(taskText);
        if(taskList.get(i).getIsFinished()) {
            viewHolder.taskText.setPaintFlags(viewHolder.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public TimeBlockTaskModel deleteItem(int position){
        TimeBlockTaskModel taskToRemove = taskList.get(position);
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
