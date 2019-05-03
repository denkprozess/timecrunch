package de.timecrunch.timecrunch.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.PlannerDay;
import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.view.HourView;
import de.timecrunch.timecrunch.view.TimeBlockView;
import de.timecrunch.timecrunch.viewListener.PlannerOnTouchListener;
import de.timecrunch.timecrunch.viewListener.TemplateDropEventListener;
import de.timecrunch.timecrunch.viewModel.PlannerViewModel;

public class PlannerFragment extends Fragment {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
    private ProgressBar progressBar;
    private ActionBar actionBar;
    private MaterialCalendarView mcv;
    private LinearLayout plannerContainer;
    private FrameLayout plannerFrame;
    private View view;
    private PlannerViewModel plannerViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_planner, container, false);

        plannerViewModel = ViewModelProviders.of(getActivity()).get(PlannerViewModel.class);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        updateActionBar(CalendarDay.today(), false);

        progressBar = getActivity().findViewById(R.id.tasks_progress_bar);

        // Calendar
        mcv = view.findViewById(R.id.calendarView);
        mcv.setTopbarVisible(false);
        mcv.setSelectedDate(CalendarDay.today());

        // Planner
        plannerContainer = view.findViewById(R.id.planner_container);
        plannerFrame = view.findViewById(R.id.planner_framelayout);
        initRows(plannerContainer);

        final ScrollView sv = view.findViewById(R.id.planner_scrollview);
        sv.requestDisallowInterceptTouchEvent(true);
        jumpToCurrentTime(sv);

        plannerFrame.setOnTouchListener(new PlannerOnTouchListener(plannerViewModel, progressBar));
        plannerFrame.setOnDragListener(new TemplateDropEventListener(plannerViewModel, progressBar));

        // Load data
        updatePlanner(mcv.getSelectedDate().getYear(), mcv.getSelectedDate().getMonth(), mcv.getSelectedDate().getDay());

        mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView,
                                       @NonNull CalendarDay date,
                                       boolean selected) {
                updateActionBar(date, selected);
                updatePlanner(date.getYear(), date.getMonth(), date.getDay());
            }
        });

        return this.view;
    }

    /*
        Updates month and year in the action bar
     */
    private void updateActionBar(CalendarDay date, boolean selected) {
        String title = (selected ? FORMATTER.format(date.getDate()) : FORMATTER.format(CalendarDay.today().getDate()));
        TextView textView = new TextView(getActivity());
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.materialcolorpicker__white));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
    }

    /*
        Updates the LiveData for this block and redraws the Planner on change
     */
    private void updatePlanner(int year, int month, int day) {
        if (plannerViewModel.getPlannerLiveData().hasObservers()) {
            plannerViewModel.getPlannerLiveData().removeObservers(this);
        }
        plannerViewModel.setUpLiveDataForDate(year, month, day, progressBar);
        plannerViewModel.getPlannerLiveData().observe(this, new Observer<PlannerDay>() {
            @Override
            public void onChanged(@Nullable PlannerDay planner) {
                drawPlanner(planner);
            }
        });
    }

    /*
        Scrolls the ScrollView roughly to current time
     */
    private void jumpToCurrentTime(final ScrollView sv) {
        sv.post(new Runnable() {
            public void run() {
                Calendar rightNow = Calendar.getInstance();
                int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                currentHour = currentHour * 2;
                int position = (plannerContainer.getBottom() / 48) * currentHour;
                if (position < 0) {
                    sv.scrollTo(0, plannerContainer.getTop());
                } else if (position > plannerContainer.getBottom()) {
                    sv.scrollTo(0, plannerContainer.getBottom());
                } else {
                    sv.scrollTo(0, position);
                }

            }
        });
    }

    /*
        Draws the TimeBlocks on the view
     */
    private void drawPlanner(PlannerDay planner) {

        resetPlannerView();

        if (planner != null) {
            if (planner.getBlocks() != null) {
                for (Map.Entry<String, TimeBlock> entry : planner.getBlocks().entrySet()) {
                    TimeBlock t = entry.getValue();
                    TimeBlockView block = new TimeBlockView(
                            getContext(),
                            entry.getKey(),
                            t.getTasks(),
                            t.getColor(),
                            plannerFrame.getWidth(),
                            t.getStartHours(),
                            t.getStartMinutes(),
                            t.getEndHours(),
                            t.getEndMinutes());
                    block.setOnClickListener(new BlockOnClickListener(entry.getKey()));
                    plannerFrame.addView(block);
                }
            }
        }
    }

    private void resetPlannerView() {
        plannerFrame.removeAllViews();
        plannerFrame.addView(plannerContainer);
    }

    private void showEditDialog(String timeblockId) {
        FragmentManager fm = getFragmentManager();
        TimeBlockTasksDialogFragment timeBlockTasksDialogFragment =
                TimeBlockTasksDialogFragment.newInstance(
                        "",
                        mcv.getSelectedDate().getYear(),
                        mcv.getSelectedDate().getMonth(),
                        mcv.getSelectedDate().getDay(),
                        timeblockId,
                        plannerViewModel);
        timeBlockTasksDialogFragment.show(fm, "fragment_edit_block_tasks");
    }

    private void initRows(LinearLayout ll) {
        int j = 0;
        for (int i = 0; i < 49; i++) {
            HourView row = new HourView(this.getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            row.setHeight(dpToPx(36));

            if (j == 0) {
                j++;
                row.setTextColor(Color.BLACK);
            } else {
                j--;
                row.setTextColor(Color.TRANSPARENT);
            }

            int padding = 9;
            row.setPadding(24, this.dpToPx(padding), 0, this.dpToPx(padding));

            if (i < 20) {
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

    public class BlockOnClickListener implements View.OnClickListener {

        String timeblockId;

        public BlockOnClickListener(String timeblockId) {
            this.timeblockId = timeblockId;
        }

        @Override
        public void onClick(View v) {
            showEditDialog(timeblockId);
        }
    }
}
