package de.timecrunch.timecrunch.utilities;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskModel;

import static android.support.constraint.Constraints.TAG;

public class TaskDBHandler extends FireBaseDBHandler {

    public void getTasks(final MutableLiveData<Map<String, List<TaskModel>>> taskLiveData, final ProgressBar progressBar){
        showProgressBar(progressBar);
        final Query query = db.collection(userId).document("data").collection("tasks");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                Map<String, List<TaskModel>> categoryListMap = new HashMap<>();
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                    String categoryID = documentSnapshot.getId();
                    ArrayList<TaskModel> taskList = new ArrayList<>();
                    Query innerQuery = ((CollectionReference) query).document(categoryID).collection("taskList");
                    innerQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e != null){
                                Log.w(TAG, "Listen failed.", e);
                                return;

                            }
                        }
                    })
                    for(QueryDocumentSnapshot innerDocument: documentSnapshot.)
                    categoryListMap.put(categoryID,taskList);
                }

                taskLiveData.postValue(categoryListMap);
                hideProgressBar(progressBar);

            }
        });
    }

    public void addTask(String categoryId, TaskModel userTask, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final CollectionReference tasksCollection = db.collection(userId).document("data")
                .collection("tasks").document(categoryId).collection("taskList");
        tasksCollection.add(userTask).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                db.document(id).update("id", id);
                hideProgressBar(progressBar);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        hideProgressBar(progressBar);
                    }
                });
    }
}

