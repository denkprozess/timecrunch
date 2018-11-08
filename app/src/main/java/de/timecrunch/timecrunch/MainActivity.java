package de.timecrunch.timecrunch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView mcv;
    private ArrayList<String> mHours = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcv = findViewById(R.id.calendarView);
        mcv.setTopbarVisible(false);

        initHours();

        Intent intent = new Intent(this, TodoCategoriesActivity.class);
        startActivity(intent);
    }

    private void initHours() {
        for(int i = 0; i < 24; i++) {
            if(i < 10) {
                mHours.add("0" + String.valueOf(i) + ":00");
            } else {
                mHours.add(String.valueOf(i) + ":00");
            }
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView rv = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mHours);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

}
