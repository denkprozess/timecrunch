package de.timecrunch.timecrunch.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;

public class PlannerFragment extends Fragment {

    private MaterialCalendarView mcv;
    private LinearLayout plannerContainer;
    private ArrayList<String> mHours = new ArrayList<String>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_planner, container, false);

         mcv = view.findViewById(R.id.calendarView);
         mcv.setTopbarVisible(false);

         plannerContainer = view.findViewById(R.id.planner_container);

         ScrollView sv = view.findViewById(R.id.planner_scrollview);
         sv.requestDisallowInterceptTouchEvent(true);

         initRows(plannerContainer);

        return this.view;
    }

    private void initRows(LinearLayout ll) {
        for (int i = 0; i < 25; i++) {
            HourView row = new HourView(this.getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (i == 0) {
                row.setPadding(20, 5, 20, 60);
            } else if (i > 0 && i < 25) {
                row.setPadding(20, 0, 20, 60);
            } else {
                row.setPadding(20, 0, 20, 0);
            }

            if(i < 10) {
                row.setText("0" + i + ":00");
            } else {
                row.setText(i + ":00");
            }

            ll.addView(row);
        }
    }
}
