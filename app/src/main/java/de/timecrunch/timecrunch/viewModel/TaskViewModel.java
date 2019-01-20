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

public class TaskViewModel extends AndroidViewModel {
    private MutableLiveData<Map<Category, List<TaskModel>>> tasksLiveData;
    private DBHandler dbHandler;
    private TaskModel lastRemovedTask;
    private int lastRemovedCategoryId;
    private boolean invalidated;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        dbHandler = new DBHandler(application.getApplicationContext());
        tasksLiveData = new MutableLiveData<>();
        Map<Category, List<TaskModel>> taskMap = new HashMap<>();
        tasksLiveData.setValue(taskMap);
    }

    public List<Category> getCategoryList() {
        if (tasksLiveData.getValue().isEmpty()) {
            initializeTasks();
        }
        return new ArrayList<Category>(tasksLiveData.getValue().keySet());
    }

    public LiveData<Map<Category, List<TaskModel>>> getTaskMapLiveData() {
        return tasksLiveData;
    }

    public Map<Category, List<TaskModel>> getTaskMap() {
        if (tasksLiveData.getValue().isEmpty() || invalidated) {
            initializeTasks();
            invalidated = false;
        }
        return tasksLiveData.getValue();
    }

    private void initializeTasks() {
        Map<Category, List<Category>> categoryMap = dbHandler.getCategories();
        Map<Category, List<TaskModel>> taskMap = new HashMap<>();
        for (Map.Entry<Category, List<Category>> entry : categoryMap.entrySet()) {
            Category parentCategory = entry.getKey();
            List<TaskModel> parentTaskList = dbHandler.getTasks(parentCategory.getId());
            taskMap.put(parentCategory, parentTaskList);
            for (Category childCategory : entry.getValue()) {
                List<TaskModel> childTaskList = dbHandler.getTasks(childCategory.getId());
                taskMap.put(childCategory, childTaskList);
            }
        }

//        Category morningRoutine = new Category(1,"Morning Routine", Color.GREEN, false);
//        List<TaskModel> taskList = new ArrayList<>();
//        taskList.add(new TaskModel(1,"Shower"));
//        taskList.add(new TaskModel(1,"Floss the teeth"));
//        taskList.add(new TaskModel(1,"Eat breakfast"));
//        taskMap.put(morningRoutine, taskList);
        tasksLiveData.postValue(taskMap);
    }

    public List<TaskModel> getTaskList(Category category) {
        List<TaskModel> taskList = tasksLiveData.getValue().get(category);
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        return taskList;
    }

    public void removeTask(int categoryId, TaskModel task) {
        boolean success = dbHandler.removeTask(task.getId());
        if (success) {
            lastRemovedCategoryId = categoryId;
            lastRemovedTask = task;
            Map<Category, List<TaskModel>> taskMap = tasksLiveData.getValue();
            List<TaskModel> taskList = getTaskListOfCategoryId(taskMap, categoryId);
            taskList.remove(task);
            tasksLiveData.postValue(taskMap);
        }
    }

    public void addTask(int categoryId, TaskModel userTask) {
        String text = userTask.getText();
        LatLng location = userTask.getLocation();
        TaskAlarm alarm = userTask.getAlarm();
        int id = dbHandler.createTask(text, categoryId);
        if (id != -1) {
            Map<Category, List<TaskModel>> taskMap = tasksLiveData.getValue();
            List<TaskModel> taskList = getTaskListOfCategoryId(taskMap, categoryId);
            TaskModel task = new TaskModel(id, text, location, alarm);
            taskList.add(task);
            tasksLiveData.postValue(taskMap);
        }
    }

    public void changeTask(int categoryId, TaskModel task) {
        // task equals works with the id only for removing
        // because of that the changed task obejct can be passed to remove and add.
        // Remove only needs the id and add only needs the tasks payload.
        removeTask(categoryId, task);
        addTask(categoryId, task);
    }

    private List<TaskModel> getTaskListOfCategoryId(Map<Category, List<TaskModel>> taskMap, int categoryId) {
        Category categoryDummy = new Category(categoryId, null, 0, false);
        List<TaskModel> taskList = taskMap.get(categoryDummy);
        return taskList;
    }

    public void invalidate(){
        invalidated = true;
    }
}
