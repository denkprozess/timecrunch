package de.timecrunch.timecrunch.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.activities.LoginActivity;
import de.timecrunch.timecrunch.utilities.LoginUtilities;


public class RegisterFragment extends Fragment {

    private FirebaseAuth authentication;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the register form.
        this.activity = getActivity();
        mEmailView = (EditText) activity.findViewById(R.id.register_email);

        mPasswordView = (EditText) activity.findViewById(R.id.register_password);
        mRepeatPasswordView = (EditText) activity.findViewById(R.id.register_repeat_password);
        mRepeatPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) activity.findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = activity.findViewById(R.id.register_form);
        mProgressView = activity.findViewById(R.id.register_progress);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        authentication = FirebaseAuth.getInstance();
    }


    /**
     * Attempts to sign in or register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String repeatPassword = mRepeatPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for matching passwords
        if (!LoginUtilities.doPasswordsMatch(password, repeatPassword)) {
            mRepeatPasswordView.setError(getString(R.string.error_passwords_mismatch));
            focusView = mRepeatPasswordView;
            cancel = true;
        }

        // Check for a valid password
        if (!LoginUtilities.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid repeatPassword
        if (!LoginUtilities.isPasswordValid(repeatPassword)) {
            mRepeatPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRepeatPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!LoginUtilities.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            attemptFirebaseRegister(email, password);
        }
    }

    private void attemptFirebaseRegister(String email, String password) {
        authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = authentication.getCurrentUser();
                            user.sendEmailVerification();
                            Snackbar snackbar = Snackbar.make(mRegisterFormView, R.string.register_success_please_verify, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.putExtra("FROM_REGISTRATION", true);
                            startActivity(intent);
                        } else {
                            Snackbar snackbar = Snackbar.make(mRegisterFormView, R.string.error_email_in_use, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        showProgress(false);
                    }
                });
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
