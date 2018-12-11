package de.timecrunch.timecrunch.view;

import android.util.Log;
import android.view.View;

public class BlockOnLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        EditBlock eb = (EditBlock) v;
        if(!eb.isEditMode()) {
            eb.setEditMode(true);
            return true;
        } else {
            return false;
        }
    }
}
