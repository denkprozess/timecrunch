package de.timecrunch.timecrunch.utilities;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskModel;

import static android.support.constraint.Constraints.TAG;

public class CategoryDBHandler extends FireBaseDBHandler{


    public void getCategoriesAndRegisterListener(final MutableLiveData<Map<Category, List<Category>>> categoryLiveData, final ProgressBar progressBar){
        showProgressBar(progressBar);
        CollectionReference collectionReference = db.collection(userId).document("data").collection("categories");
        Query query = collectionReference.orderBy("sorting", Query.Direction.ASCENDING);//db.collection(userId).document("data").collection("categories").orderBy("sorting",Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                Map<Category, List<Category>> categoryListMap = new LinkedHashMap<>();
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Category category = documentSnapshot.toObject(Category.class);
                    categoryListMap.put(category, new ArrayList<Category>());
                }

                categoryLiveData.postValue(categoryListMap);
                hideProgressBar(progressBar);

            }
        });
    }

    public void addCategory(final Category category, final ProgressBar progressBar) {
        showProgressBar(progressBar);
        final CollectionReference categoriesCollection = db.collection(userId).document("data").collection("categories");
        categoriesCollection.add(category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                categoriesCollection.document(id).update("id", id);
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

    public void changeCategory(final Category category, final ProgressBar progressBar){
        showProgressBar(progressBar);
        String categoryId = category.getId();
        final DocumentReference categoryDocument = db.collection(userId).document("data").collection("categories").document(categoryId);
        categoryDocument.set(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressBar(progressBar);
            }
        });
    }

    public void removeCategory(final String categoryId, final ProgressBar progressBar){
        showProgressBar(progressBar);
        final DocumentReference categoryDocument = db.collection(userId).document("data").collection("categories").document(categoryId);
        categoryDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Query query = db.collection(userId).document("data").collection("tasks").whereEqualTo("categoryId", categoryId);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete();
                        }
                        hideProgressBar(progressBar);
                    }
                });
            }
        });

    }

}

