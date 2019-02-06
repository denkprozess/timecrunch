package de.timecrunch.timecrunch.utilities;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.viewModel.TaskSelectionViewModel;

import static android.support.constraint.Constraints.TAG;

public class TaskSelectionDBHandler extends FireBaseDBHandler {

    private ListenerRegistration registration;

    public void getPlannerAndRegisterListener(int year, int month, int day, final TaskSelectionViewModel viewModel, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final Query query = db.collection(userId).document("data").collection("plannerDays").whereEqualTo("year", year)
                .whereEqualTo("month", month).whereEqualTo("day", day);
        if (registration != null) {
            registration.remove();
        }
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                   // plannerLiveData.postValue(plannerDay);
                } else {
                   // plannerLiveData.postValue(null);
                }
                hideProgressBar(progressBar);
            }
        });
    }
}
