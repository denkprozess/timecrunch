package de.timecrunch.timecrunch.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.timecrunch.timecrunch.R;

public class TaskOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_overview);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TaskOverviewFragment()).commit();
    }
}
