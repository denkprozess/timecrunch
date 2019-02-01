package de.timecrunch.timecrunch.utilities;

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
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setClickable(true);
    }

    protected void hideProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        progressBar.setClickable(false);
    }
}
