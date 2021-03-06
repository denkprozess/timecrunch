package de.timecrunch.timecrunch.database;

import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.viewModel.TaskSelectionViewModel;

import static android.support.constraint.Constraints.TAG;

public class TaskSelectionDBHandler extends FireBaseDBHandler {

    private ListenerRegistration plannerRegistration;
    private ListenerRegistration tasksRegistration;

    public void getTaskSelectionAndRegisterListener(int year, int month, int day, final String timeBlockId, final TaskSelectionViewModel viewModel, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final Query query = db.collection(userId).document("data").collection("plannerDays").whereEqualTo("year", year)
                .whereEqualTo("month", month).whereEqualTo("day", day);
        if (plannerRegistration != null) {
            plannerRegistration.remove();
        }
        plannerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()) {
                    // there only ever is at most one document for the specified date
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    PlannerDay plannerDay = documentSnapshot.toObject(PlannerDay.class);
                    String categoryId = plannerDay.getTimeBlock(timeBlockId).getCategoryId();
                    getTasksAndRegisterListener(categoryId, viewModel, progressBar);
                    viewModel.updateTimeBlockTaskListFromDB(plannerDay);
                } else {
                    viewModel.updateTimeBlockTaskListFromDB(null);
                }
                hideProgressBar(progressBar);
            }
        });
    }

    public void getTasksAndRegisterListener(final String categoryId, final TaskSelectionViewModel viewModel, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final Query query = db.collection(userId).document("data").collection("tasks").whereEqualTo("categoryId", categoryId).orderBy("sorting", Query.Direction.ASCENDING);
        if (tasksRegistration != null) {
            tasksRegistration.remove();
        }
        tasksRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<TaskModel> taskList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    TaskModel task = documentSnapshot.toObject(TaskModel.class);
                    taskList.add(task);
                }

                viewModel.updateCategoryTaskListFromDB(taskList);
                hideProgressBar(progressBar);

            }
        });
    }

    public void unregisterListeners() {
        if (plannerRegistration != null) {
            plannerRegistration.remove();
        }
        if (tasksRegistration != null) {
            tasksRegistration.remove();
        }
    }
}
