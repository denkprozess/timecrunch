package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.Map;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TaskModel;

public class TaskSelectionViewModel extends AndroidViewModel {

    private PlannerDay plannerDay;
    private Map<String, TaskModel> categoryTaskMap;

    public TaskSelectionViewModel(@NonNull Application application) {
        super(application);

    }

    public void updateTimeBlockTaskListFromDB(PlannerDay plannerDay){
        this.plannerDay = plannerDay;
    }

    public void updateCategoryTaskListFromDB(Map<String, TaskModel> categoryTaskMap){
        this.categoryTaskMap=categoryTaskMap;
    }

    private void updateLiveData(){

    }
}
