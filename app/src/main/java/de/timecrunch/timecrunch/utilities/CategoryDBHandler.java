package de.timecrunch.timecrunch.utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.timecrunch.timecrunch.model.Category;

import static android.support.constraint.Constraints.TAG;

public class CategoryDBHandler {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;

    public CategoryDBHandler() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
    }

    public void testWriteCategory(final Category category) {
        db.collection(userId).document("data").collection("categories").add(category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                Category categoryWithId = new Category(id, category.getName(),category.getColor(),category.hasTimeBlock());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
