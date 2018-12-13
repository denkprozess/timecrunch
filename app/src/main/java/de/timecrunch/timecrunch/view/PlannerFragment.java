package de.timecrunch.timecrunch.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;

public class PlannerFragment extends Fragment {

    private MaterialCalendarView mcv;
    private LinearLayout plannerContainer;
    private FrameLayout plannerFrame;
    private ArrayList<String> mHours = new ArrayList<String>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_planner, container, false);

        mcv = view.findViewById(R.id.calendarView);
        mcv.setTopbarVisible(false);

        plannerContainer = view.findViewById(R.id.planner_container);
        plannerFrame = view.findViewById(R.id.planner_framelayout);

        ScrollView sv = view.findViewById(R.id.planner_scrollview);
        sv.requestDisallowInterceptTouchEvent(true);

        initRows(plannerContainer);
        plannerFrame.setOnTouchListener(new PlannerOnTouchListener());
        plannerFrame.setOnDragListener(new TemplateDropEventListener());

        return this.view;
    }

    private void initRows(LinearLayout ll) {
        int j = 0;
        for (int i = 0; i < 48; i++) {
            HourView row = new HourView(this.getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            row.setHeight(dpToPx(36));

            if(j == 0) {
                j++;
                row.setTextColor(Color.BLACK);
            } else {
                j--;
                row.setTextColor(Color.TRANSPARENT);
            }

            int padd = 9;
            row.setPadding(24, this.dpToPx(padd), 0, this.dpToPx(padd));

            if(i < 20) {
                row.setText("0" + i / 2 + ":00");
            } else {
                row.setText(i / 2 + ":00");
            }

            ll.addView(row);
        }
    }

    private int dpToPx(int dp) {
        float density = this.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
