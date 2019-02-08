package de.timecrunch.timecrunch.database;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.TaskModel;

import static android.support.constraint.Constraints.TAG;

public class TaskDBHandler extends FireBaseDBHandler {

    private ListenerRegistration registration;

    public void getTasksAndRegisterListener(final String categoryId, final MutableLiveData<Map<String, TaskModel>> taskLiveData, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final Query query = db.collection(userId).document("data").collection("tasks").whereEqualTo("categoryId", categoryId).orderBy("sorting", Query.Direction.ASCENDING);
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
                Map<String, TaskModel> taskMap = new LinkedHashMap<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    TaskModel task = documentSnapshot.toObject(TaskModel.class);
                    taskMap.put(task.getId(), task);
                }

                taskLiveData.postValue(taskMap);
                hideProgressBar(progressBar);

            }
        });
    }

    public void addTask(String categoryId, TaskModel userTask, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final CollectionReference tasksCollection = db.collection(userId).document("data")
                .collection("tasks");
        tasksCollection.add(userTask).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                tasksCollection.document(id).update("id", id);
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

    public void removeTask(String taskId, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        DocumentReference taskDocument = db.collection(userId).document("data")
                .collection("tasks").document(taskId);
        taskDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgressBar(progressBar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar(progressBar);
            }
        });
    }


    public void changeTask(TaskModel task, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        DocumentReference taskDocument = db.collection(userId).document("data")
                .collection("tasks").document(task.getId());
        taskDocument.set(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressBar(progressBar);
            }
        });
    }
}

