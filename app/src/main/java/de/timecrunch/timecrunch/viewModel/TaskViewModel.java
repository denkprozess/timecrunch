package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.timecrunch.timecrunch.model.TaskAlarm;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.utilities.DBHandler;
import de.timecrunch.timecrunch.utilities.TaskDBHandler;

public class TaskViewModel extends AndroidViewModel {
    private MutableLiveData<Map<String, TaskModel>> tasksLiveData;
    private TaskDBHandler taskDBHandler;
    private TaskModel lastRemovedTask;
    private String lastRemovedCategoryId;
    private boolean invalidated;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskDBHandler = new TaskDBHandler();
        tasksLiveData = new MutableLiveData<>();
        Map<String, TaskModel> taskList = new LinkedHashMap<>();
        tasksLiveData.setValue(taskList);
    }


    public LiveData<Map<String, TaskModel>> getTaskMapLiveData() {
        return tasksLiveData;
    }

    private void initializeTasks(String categoryId, ProgressBar progressBar) {
        // DB-Calls are asynchronous by default, so no need for AsyncTask
        taskDBHandler.getTasksAndRegisterListener(categoryId,tasksLiveData,progressBar);
    }

    public TaskModel getTaskAtPosition(int position){
        Map<String,TaskModel> taskModelMap = tasksLiveData.getValue();
        ArrayList<TaskModel> taskList = new ArrayList<>(taskModelMap.values());
        if(taskList.size()>position){
            return taskList.get(position);
        }else{
            return null;
        }
    }

    public void setUpLiveData(String categoryId, ProgressBar progressBar) {
        Map<String, TaskModel> taskMap = tasksLiveData.getValue();
        boolean categoryIdMatchesTaskList = false;
        if(!taskMap.isEmpty()){
            categoryIdMatchesTaskList = categoryId.equals(getTaskAtPosition(0).getCategoryId());
        }
        if (!categoryIdMatchesTaskList) {
            initializeTasks(categoryId, progressBar);
        }
    }

    public TaskModel getTask(String taskId){
        return tasksLiveData.getValue().get(taskId);
    }
    public void removeTask(TaskModel task, ProgressBar progressBar) {
        taskDBHandler.removeTask(task.getId(), progressBar);
    }

    public void addTask(String categoryId, TaskModel userTask, ProgressBar progressBar) {
        Map<String,TaskModel> taskModelMap = tasksLiveData.getValue();
        ArrayList<TaskModel> taskList = new ArrayList<>(taskModelMap.values());
        if(!taskList.isEmpty()) {
            Collections.sort(taskList, new Comparator<TaskModel>() {
                @Override
                public int compare(TaskModel o1, TaskModel o2) {
                    return o1.getSorting() - o2.getSorting();
                }
            });
            TaskModel lastTask = taskList.get(taskList.size() - 1);
            int highestSorting = lastTask.getSorting();
            // append new entry at the end of the list via the sorting field users can move their Tasks in the list in the future
            userTask.setSorting(highestSorting + 1);
        }else{
            userTask.setSorting(1);
        }
        taskDBHandler.addTask(categoryId, userTask, progressBar);
    }

    public void changeTask(TaskModel task, ProgressBar progressBar) {
        taskDBHandler.changeTask(task, progressBar);
    }

    public void invalidate(){
        invalidated = true;
    }

}
