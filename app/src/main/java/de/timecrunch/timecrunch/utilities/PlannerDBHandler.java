package de.timecrunch.timecrunch.utilities;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.PlannerDay;

import static android.support.constraint.Constraints.TAG;

public class PlannerDBHandler extends FireBaseDBHandler {

    private ListenerRegistration registration;

    public void getPlannerAndRegisterListener(int year, int month, int day, final MutableLiveData<PlannerDay> plannerLiveData, final ProgressBar progressBar) {
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
                    plannerLiveData.postValue(plannerDay);
                } else {
                    plannerLiveData.postValue(null);
                }
                hideProgressBar(progressBar);
            }
        });
    }

    public void savePlanner(final PlannerDay plannerDay, final ProgressBar progressBar){
        int year = plannerDay.getYear();
        int month = plannerDay.getMonth();
        int day = plannerDay.getDay();
        // first get planner data for specified date to check if it exists
        final CollectionReference plannerDaysCollection = db.collection(userId).document("data")
                .collection("plannerDays");
        final Query query = plannerDaysCollection.whereEqualTo("year", year)
                .whereEqualTo("month", month).whereEqualTo("day", day);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // if there already is data for this data change it
                if (!queryDocumentSnapshots.isEmpty()) {
                    // there only ever is at most one document for the specified date
                    DocumentReference documentSnapshot = queryDocumentSnapshots.getDocuments().get(0).getReference();
                    String documentId = documentSnapshot.getId();
                    DocumentReference taskDocument = plannerDaysCollection.document(documentId);
                    documentSnapshot.set(plannerDay).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressBar(progressBar);
                        }
                    });
                // if there is not data for the data, create it
                } else {
                    plannerDaysCollection.add(plannerDay).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            hideProgressBar(progressBar);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
                hideProgressBar(progressBar);
            }
        });
    }
}

