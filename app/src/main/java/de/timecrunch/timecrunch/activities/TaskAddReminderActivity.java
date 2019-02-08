package de.timecrunch.timecrunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.fragments.TaskAddReminderFragment;

/*
    Wrapper activity for the TaskAddReminderFragment
 */
public class TaskAddReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (!(bundle.containsKey("TASK_ID") && bundle.containsKey("CATEGORY_ID") && bundle.containsKey("CATEGORY_NAME"))) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
        TaskAddReminderFragment fragment = new TaskAddReminderFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

}