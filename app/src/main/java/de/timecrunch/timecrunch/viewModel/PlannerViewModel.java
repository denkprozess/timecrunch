package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;
import de.timecrunch.timecrunch.utilities.PlannerDBHandler;
import de.timecrunch.timecrunch.utilities.TaskDBHandler;

public class PlannerViewModel extends AndroidViewModel {

    private PlannerDBHandler plannerDBHandler;
    private TaskDBHandler taskDBHandler;
    private MutableLiveData<PlannerDay> plannerLiveData;
    private int currentDay;
    private int currentMonth;
    private int currentYear;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
        plannerDBHandler = new PlannerDBHandler();
        taskDBHandler = new TaskDBHandler();
        plannerLiveData = new MutableLiveData<>();
    }

    private void initializePlanner(ProgressBar progressBar) {
        plannerDBHandler.getPlannerAndRegisterListener(currentYear, currentMonth, currentDay, plannerLiveData, progressBar);
    }

    public MutableLiveData<PlannerDay> getPlannerLiveData() {
        return plannerLiveData;
    }

    public void setUpLiveDataForDate(int year, int month, int day, ProgressBar progressBar) {
        PlannerDay currentPlannerDay = plannerLiveData.getValue();
        // check if current plannerDay is already the correct one
        boolean isCorrectPlannerDayAlreadySet = currentPlannerDay != null && currentDay == day &&
                currentMonth == month && currentYear == year;
        if (!isCorrectPlannerDayAlreadySet) {
            currentYear = year;
            currentMonth = month;
            currentDay = day;
            initializePlanner(progressBar);
        }
    }

    public TimeBlock getTimeBlock(String timeBlockId){
        return plannerLiveData.getValue().getTimeBlock(timeBlockId);
    }
    public String addTimeBlock(String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        if(plannerDay == null){
            plannerDay = new PlannerDay(currentYear, currentMonth, currentDay);
        }
        String id = plannerDay.createBlock(categoryId,color,startHours,startMinutes,endHours,endMinutes);
        plannerDBHandler.savePlanner(plannerDay, progressBar);
        return id;
    }

    public void changeTimeBlock(String timeBlockId, TimeBlock timeBlock, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        plannerDay.changeBlock(timeBlockId, timeBlock);
        plannerDBHandler.savePlanner(plannerDay,progressBar);
    }

    public void changeFinishedStatusOfTask(String timeBlockId, String taskId, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        TimeBlock timeBlock = plannerDay.getTimeBlock(timeBlockId);
        for(TimeBlockTaskModel task: timeBlock.getTasks()){
            if(task.getTask().getId().equals(taskId)){
                boolean newIsFinished = !task.getIsFinished();
                task.setIsFinished(newIsFinished);
                // If task is set to be finished and is not a repeating task, delete it from tasklist of category
                if(newIsFinished && !task.getTask().getIsRepeating()){
                    taskDBHandler.removeTask(task.getTask().getId(), progressBar);
                }
            }
        }
        plannerDBHandler.savePlanner(plannerDay,progressBar);
    }

    public void removeTimeBlock(String timeBlockId, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        plannerDay.removeTimeBlock(timeBlockId);
    }
}
