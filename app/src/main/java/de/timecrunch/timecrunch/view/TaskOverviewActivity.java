package de.timecrunch.timecrunch.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.timecrunch.timecrunch.R;

public class TaskOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        setContentView(R.layout.activity_task_overview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TaskOverviewFragment fragment = new TaskOverviewFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }

    public void createReminder(View view) {
        Intent intent = new Intent(this, TaskAddReminderActivity.class);
        startActivity(intent);
    }

}
