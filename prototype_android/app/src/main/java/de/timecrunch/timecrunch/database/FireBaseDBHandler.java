package de.timecrunch.timecrunch.database;

import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseDBHandler {
    protected FirebaseFirestore db;
    protected FirebaseUser user;
    protected String userId;

    public FireBaseDBHandler() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
    }

    protected void showProgressBar(ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setClickable(true);
        }
    }

    protected void hideProgressBar(ProgressBar progressBar) {
        if (progressBar !=null){
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            progressBar.setClickable(false);
        }
    }
}
