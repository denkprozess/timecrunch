package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.Task;
import de.timecrunch.timecrunch.utilities.DBHandler;

public class TaskViewModel extends AndroidViewModel {
    private MutableLiveData<Map<Category, List<Task>>> tasksLiveData;
    private DBHandler dbHandler;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        dbHandler = new DBHandler(application.getApplicationContext());
    }

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
        Map<Category, List<Category>> categoryMap = dbHandler.getCategories();
        Map<Category, List<Task>> taskMap = new HashMap<>();
        for(Map.Entry<Category, List<Category>> entry: categoryMap.entrySet()){
            Category parentCategory = entry.getKey();
            List<Task> parentTaskList = dbHandler.getTasks(parentCategory.getId());
            taskMap.put(parentCategory,parentTaskList);
            for(Category childCategory:entry.getValue()){
                List<Task> childTaskList = dbHandler.getTasks(childCategory.getId());
                taskMap.put(childCategory,childTaskList);
            }
        }

//        Category morningRoutine = new Category(1,"Morning Routine", Color.GREEN, false);
//        List<Task> taskList = new ArrayList<>();
//        taskList.add(new Task(1,"Shower"));
//        taskList.add(new Task(1,"Floss the teeth"));
//        taskList.add(new Task(1,"Eat breakfast"));
//        taskMap.put(morningRoutine, taskList);
        tasksLiveData = new MutableLiveData<>();
        tasksLiveData.setValue(taskMap);
    }

    public List<Task> getTaskList(Category category){
        Category morningRoutine = tasksLiveData.getValue().keySet().iterator().next();
        return tasksLiveData.getValue().get(morningRoutine);
    }

    public void addTask(Category category, Task userTask){
        int categoryId = category.getId();
        String text = userTask.getText();
        int id = dbHandler.createTask(text, categoryId);
        if(id!=-1) {
            Map<Category, List<Task>> taskMap = tasksLiveData.getValue();
            List<Task> taskList = taskMap.get(category);
            Task task = new Task(id,text);
            taskList.add(task);
            tasksLiveData.setValue(taskMap);
        }
    }
}
