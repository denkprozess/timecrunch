package de.timecrunch.timecrunch.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.Task;

public class TaskViewModel extends ViewModel {
    private MutableLiveData<Map<Category, List<Task>>> tasksLiveData;

    public List<Category> getCategoryList(){
        if(tasksLiveData ==null){
            initializeTasks();
        }
        return new ArrayList<Category>(tasksLiveData.getValue().keySet());
    }

    public LiveData<Map<Category, List<Task>>> getTaskMap(){
        if(tasksLiveData==null){
            initializeTasks();
        }
        return tasksLiveData;
    }

    private void initializeTasks(){
        Map<Category, List<Task>> taskMap = new HashMap<>();
        Category morningRoutine = new Category(1,"Morning Routine", Color.GREEN, false);
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(1,"Shower"));
        taskList.add(new Task(1,"Floss the teeth"));
        taskList.add(new Task(1,"Eat breakfast"));
        taskMap.put(morningRoutine, taskList);
        tasksLiveData = new MutableLiveData<>();
        tasksLiveData.setValue(taskMap);
    }

    public List<Task> getTaskList(Category category){
        Category morningRoutine = tasksLiveData.getValue().keySet().iterator().next();
        return tasksLiveData.getValue().get(morningRoutine);
    }

    public void addTask(Category category, Task task){
        Map<Category, List<Task>> taskMap = tasksLiveData.getValue();
        List<Task> taskList = taskMap.get(category);
        taskList.add(task);
        tasksLiveData.setValue(taskMap);
    }
}
