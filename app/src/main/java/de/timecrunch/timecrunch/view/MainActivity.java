package de.timecrunch.timecrunch.view;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private MaterialCalendarView mcv;
    private ArrayList<String> mHours = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PlannerFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.action_tasks:
                            selectedFragment = new TaskCategoriesFragment();
                            break;
                        case R.id.action_dayview:
                            selectedFragment = new PlannerFragment();
                            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                            break;
                        case R.id.action_templates:
                            selectedFragment = new TemplatesFragment();
                            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
