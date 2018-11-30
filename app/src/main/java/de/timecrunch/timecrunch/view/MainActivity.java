package de.timecrunch.timecrunch.view;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.utilities.DBHandler;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private MaterialCalendarView mcv;
    private ArrayList<String> mHours = new ArrayList<String>();
    private Menu menu;

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

        DBHandler dbh = new DBHandler(this);

/*        dbh.createCategory("Android Praktikum", Color.BLACK, false);
        dbh.createCategory("Morgen Routine", Color.BLACK, false);
        dbh.createCategory("Abend Routine", Color.BLACK, false);
        dbh.createSubcategory("Übungszettel", Color.BLACK, false, 1);
        dbh.createSubcategory("Sonstiges", Color.BLACK, false, 1);
        dbh.createTask("Dibo eine Mail schreiben", 1);
        dbh.createTask("Lehrevaluation durchführen", 1);
        dbh.createTask("Zähne putzen", 2);
        dbh.createTask("Duschen", 2);
        dbh.closeDB();

        dbh.getTasks(1);
        dbh.closeDB();

        LinkedHashMap<Category, List<Category>> categories = dbh.getCategories();
        dbh.closeDB();
        Log.d("DB", String.valueOf(categories.size()));*/

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
                            break;
                        case R.id.action_templates:
                            selectedFragment = new TemplatesFragment();
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
