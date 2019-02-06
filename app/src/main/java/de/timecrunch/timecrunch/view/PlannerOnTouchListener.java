package de.timecrunch.timecrunch.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.viewModel.PlannerViewModel;

public class PlannerOnTouchListener implements View.OnTouchListener {

    private PlannerViewModel plannerViewModel;
    private ProgressBar progressBar;

    public PlannerOnTouchListener(PlannerViewModel plannerViewModel, ProgressBar progressBar) {
        this.plannerViewModel = plannerViewModel;
        this.progressBar = progressBar;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                return false;
            }

            case MotionEvent.ACTION_UP: {
                ViewGroup vg = (ViewGroup) v;
                for(int i = 0; i < vg.getChildCount(); i++) {
                    if(vg.getChildAt(i) instanceof BlockView) {
                        if(((BlockView)vg.getChildAt(i)).isEditMode()) {
                            BlockView eb = ((BlockView)vg.getChildAt(i));
                            TimeBlock tb = plannerViewModel.getTimeBlock(eb.getBlockId());
                            tb.setStartHours(eb.getStartHours());
                            tb.setStartMinutes(eb.getStartMinutes());
                            tb.setEndHours(eb.getEndHours());
                            tb.setEndMinutes(eb.getEndMinutes());
                            plannerViewModel.changeTimeBlock(eb.getBlockId(), tb, progressBar);
                            ((BlockView)vg.getChildAt(i)).setEditMode(false);
                        }
                    }
                } return true;
            }
        } return true;
    }
}

