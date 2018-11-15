package de.timecrunch.timecrunch.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;

public class PlannerFragment extends Fragment {

    private MaterialCalendarView mcv;
    private ArrayList<String> mHours = new ArrayList<String>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_planner, container, false);

         mcv = view.findViewById(R.id.calendarView);
         mcv.setTopbarVisible(false);

         initHours();

        return this.view;
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
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this.getContext(), mHours);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
