package de.timecrunch.timecrunch.view;

import android.view.View;

public class BlockOnLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        BlockView eb = (BlockView) v;
        if(!eb.isEditMode()) {
            eb.setEditMode(true);
            return true;
        } else {
            return false;
        }
    }
}
