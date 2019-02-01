package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskAlarm;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.utilities.DBHandler;
import de.timecrunch.timecrunch.utilities.TaskDBHandler;

public class TaskViewModel extends AndroidViewModel {
    private MutableLiveData<Map<String, List<TaskModel>>> tasksLiveData;
    private DBHandler dbHandler;
    private TaskDBHandler taskDBHandler;
    private TaskModel lastRemovedTask;
    private String lastRemovedCategoryId;
    private boolean invalidated;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        dbHandler = new DBHandler(application.getApplicationContext());
        taskDBHandler = new TaskDBHandler();
        tasksLiveData = new MutableLiveData<>();
        Map<String, List<TaskModel>> taskMap = new HashMap<>();
        tasksLiveData.setValue(taskMap);
    }

    public List<String> getCategoryIDList() {
        if (tasksLiveData.getValue().isEmpty()) {
            initializeTasks();
        }
        return new ArrayList<String>(tasksLiveData.getValue().keySet());
    }

    public LiveData<Map<String, List<TaskModel>>> getTaskMapLiveData() {
        return tasksLiveData;
    }

    public Map<String, List<TaskModel>> getTaskMap() {
        if (tasksLiveData.getValue().isEmpty() || invalidated) {
            initializeTasks();
            invalidated = false;
        }
        return tasksLiveData.getValue();
    }

    private void initializeTasks() {
        Map<Category, List<Category>> categoryMap = dbHandler.getCategories();
        Map<String, List<TaskModel>> taskMap = new HashMap<>();
        for (Map.Entry<Category, List<Category>> entry : categoryMap.entrySet()) {
            Category parentCategory = entry.getKey();
            List<TaskModel> parentTaskList = dbHandler.getTasks(parentCategory.getId());
            taskMap.put(parentCategory.getId(), parentTaskList);
            for (Category childCategory : entry.getValue()) {
                List<TaskModel> childTaskList = dbHandler.getTasks(childCategory.getId());
                taskMap.put(childCategory.getId(), childTaskList);
            }
        }
        tasksLiveData.postValue(taskMap);
    }

    public List<TaskModel> getTaskList(Category category) {
        List<TaskModel> taskList = tasksLiveData.getValue().get(category);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        return taskList;
    }

    public void removeTask(String categoryId, TaskModel task) {
        boolean success = dbHandler.removeTask(task.getId());
        if (success) {
            lastRemovedCategoryId = categoryId;
            lastRemovedTask = task;
            Map<String, List<TaskModel>> taskMap = tasksLiveData.getValue();
            List<TaskModel> taskList = getTaskListOfCategoryId(taskMap, categoryId);
            taskList.remove(task);
            tasksLiveData.postValue(taskMap);
        }
    }

    public void addTask(String categoryId, TaskModel userTask) {
        String text = userTask.getText();
        LatLng location = userTask.getLocation();
        TaskAlarm alarm = userTask.getAlarm();
//        int id = dbHandler.createTask(text, categoryId);
//        if (id != -1) {
//            Map<Category, List<TaskModel>> taskMap = tasksLiveData.getValue();
//            List<TaskModel> taskList = getTaskListOfCategoryId(taskMap, categoryId);
//            TaskModel task = new TaskModel(id, text, location, alarm);
//            taskList.add(task);
//            tasksLiveData.postValue(taskMap);
//        }
    }

    public void changeTask(String categoryId, TaskModel task) {
        // task equals works with the id only for removing
        // because of that the changed task obejct can be passed to remove and add.
        // Remove only needs the id and add only needs the tasks payload.
        removeTask(categoryId, task);
        addTask(categoryId, task);
    }

    private List<TaskModel> getTaskListOfCategoryId(Map<String, List<TaskModel>> taskMap, String categoryId) {
        Category categoryDummy = new Category(categoryId, null, 0, false);
        List<TaskModel> taskList = taskMap.get(categoryDummy.getId());
        return taskList;
    }

    public void invalidate(){
        invalidated = true;
    }
}
