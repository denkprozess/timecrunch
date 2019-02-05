package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.utilities.PlannerDBHandler;

public class PlannerViewModel extends AndroidViewModel {

    private PlannerDBHandler plannerDBHandler;
    private MutableLiveData<PlannerDay> plannerLiveData;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
        plannerDBHandler = new PlannerDBHandler();
        plannerLiveData = new MutableLiveData<>();
    }

    private void initializePlanner(int day, int month, int year, ProgressBar progressBar) {
        plannerDBHandler.getPlannerAndRegisterListener(day, month, year, plannerLiveData, progressBar);
    }

    public MutableLiveData<PlannerDay> getPlannerLiveData() {
        return plannerLiveData;
    }

    public void setUpLiveDataForDate(int day, int month, int year, ProgressBar progressBar) {
        PlannerDay currentPlannerDay = plannerLiveData.getValue();
        if(currentPlannerDay==null){
            initializePlanner(day,month,year,progressBar);
        }
        // check if current plannerDay is already the correct one
        boolean isCorrectPlannerDayAlreadySet = currentPlannerDay != null && currentPlannerDay.getDay() == day &&
                currentPlannerDay.getMonth() == month && currentPlannerDay.getYear() == year;
        if (!isCorrectPlannerDayAlreadySet) {
            initializePlanner(day,month,year,progressBar);
        }
    }

    public TimeBlock getTimeBlock(String timeBlockId){
        return plannerLiveData.getValue().getTimeBlock(timeBlockId);
    }
    public String addTimeBlock(String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        String id = plannerDay.createBlock(categoryId,color,startHours,startMinutes,endHours,endMinutes);
        plannerDBHandler.savePlanner(plannerDay, progressBar);
        return id;
    }

    public void changeTimeBlock(String timeBlockId, TimeBlock timeBlock, ProgressBar progressBar){
        PlannerDay plannerDay = plannerLiveData.getValue();
        plannerDay.changeBlock(timeBlockId, timeBlock);
        plannerDBHandler.savePlanner(plannerDay,progressBar);
    }
}
