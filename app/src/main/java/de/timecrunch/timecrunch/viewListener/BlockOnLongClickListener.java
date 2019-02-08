package de.timecrunch.timecrunch.viewListener;

import android.view.View;

import de.timecrunch.timecrunch.view.TimeBlockView;

/*
    Sets the Block in EditMode (to scale, move)
 */
public class BlockOnLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        TimeBlockView eb = (TimeBlockView) v;
        if(!eb.isEditMode()) {
            eb.setEditMode(true);
            return true;
        } else {
            return false;
        }
    }
}
