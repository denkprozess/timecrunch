package de.timecrunch.timecrunch.view;

import android.content.Intent;
import android.support.v4.app.NavUtils;
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
        if(!(bundle.containsKey("CATEGORY_ID")&& bundle.containsKey("CATEGORY_NAME"))){
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
        setContentView(R.layout.activity_task_overview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TaskOverviewFragment fragment = new TaskOverviewFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
    }
}
