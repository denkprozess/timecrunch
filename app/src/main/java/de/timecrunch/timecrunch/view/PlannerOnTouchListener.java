package de.timecrunch.timecrunch.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class PlannerOnTouchListener implements View.OnTouchListener {

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
                    if(vg.getChildAt(i) instanceof EditBlock) {
                        if(((EditBlock)vg.getChildAt(i)).isEditMode()) {
                            ((EditBlock)vg.getChildAt(i)).setEditMode(false);
                        }
                    }
                } return true;
            }
        } return true;
    }
}
