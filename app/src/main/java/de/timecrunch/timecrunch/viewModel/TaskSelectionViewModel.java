package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;
import de.timecrunch.timecrunch.utilities.PlannerDBHandler;
import de.timecrunch.timecrunch.utilities.TaskSelectionDBHandler;

public class TaskSelectionViewModel extends AndroidViewModel {


    private List<TaskModel> categoryTaskList;
    private PlannerDBHandler plannerDBHandler;
    private TaskSelectionDBHandler taskSelectionDBHandler;
    private MutableLiveData<Map<TaskModel, Boolean>> selectionLiveData;

    private PlannerDay currentPlannerDay;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private String currentTimeBlockId;

    public TaskSelectionViewModel(@NonNull Application application) {
        super(application);
        selectionLiveData = new MutableLiveData<>();
        plannerDBHandler = new PlannerDBHandler();
        taskSelectionDBHandler = new TaskSelectionDBHandler();

    }

    public LiveData<Map<TaskModel, Boolean>> getTaskSelectionLiveData() {
        return selectionLiveData;
    }

    public void updateTimeBlockTaskListFromDB(PlannerDay plannerDay) {
        this.currentPlannerDay = plannerDay;
        updateLiveData();
    }

    public void updateCategoryTaskListFromDB(List<TaskModel> categoryTaskList) {
        this.categoryTaskList = categoryTaskList;
        updateLiveData();
    }

    private void updateLiveData() {
        if (currentPlannerDay != null && categoryTaskList != null) {
            // build hashmap with already selected tasks
            Map<String, TimeBlockTaskModel> plannerTaskMap = new HashMap<>();
            List<TimeBlockTaskModel> taskModelList = currentPlannerDay.getTimeBlock(currentTimeBlockId).getTasks();
            if (taskModelList != null) {
                for (TimeBlockTaskModel task : currentPlannerDay.getTimeBlock(currentTimeBlockId).getTasks()) {
                    plannerTaskMap.put(task.getTask().getId(), task);
                }
            }
            // map to store all tasks with their selection states in
            Map<TaskModel, Boolean> selectionMap = new LinkedHashMap<>();
            // compare against all tasks from category to build list of tasks with selected flag
            for (TaskModel task : categoryTaskList) {
                // if task is already selected
                if (plannerTaskMap.containsKey(task.getId())) {
                    selectionMap.put(task, true);
                }else{
                    selectionMap.put(task,false);
                }
            }
            selectionLiveData.postValue(selectionMap);
        }
    }

    public void initializeSelectedTasks(ProgressBar progressBar) {
        taskSelectionDBHandler.getTaskSelectionAndRegisterListener(currentYear, currentMonth, currentDay, currentTimeBlockId, this, progressBar);
        //taskSelectionDBHandler.getTasksAndRegisterListener(currentCategoryId,this,progressBar);
    }

    public void setUpLiveData(int year, int month, int day, String timeBlockId, ProgressBar progressBar) {
        // check if current plannerDay is already the correct one
        boolean isCorrectPlannerDayAndCategoryAlreadySet = currentPlannerDay != null && currentDay == day &&
                currentMonth == month && currentYear == year && currentTimeBlockId != null && currentTimeBlockId.equals(timeBlockId);
        if (!isCorrectPlannerDayAndCategoryAlreadySet) {
            currentYear = year;
            currentMonth = month;
            currentDay = day;
            currentTimeBlockId = timeBlockId;
            initializeSelectedTasks(progressBar);
        }
    }

    public void addTaskToTimeBlock(String timeBlockId, TimeBlockTaskModel task, ProgressBar progressBar) {
        TimeBlock timeBlock = currentPlannerDay.getTimeBlock(timeBlockId);
        timeBlock.addTask(task);
        changeTimeBlock(timeBlockId, timeBlock, progressBar);

    }

    public void removeTaskFromTimeBlock(String timeBlockId, String taskId, ProgressBar progressBar) {
        TimeBlock timeBlock = currentPlannerDay.getTimeBlock(timeBlockId);
        timeBlock.removeTask(taskId);
        changeTimeBlock(timeBlockId, timeBlock, progressBar);
    }

    private void changeTimeBlock(String timeBlockId, TimeBlock timeBlock, ProgressBar progressBar) {
        currentPlannerDay.changeBlock(timeBlockId, timeBlock);
        plannerDBHandler.savePlanner(currentPlannerDay, progressBar);
    }
}
