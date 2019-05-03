package de.timecrunch.timecrunch.viewListener;

import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.View;

public class TemplateDragEventListener implements View.OnDragListener {
    @Override
    public boolean onDrag(View v, DragEvent event) {

        final int action = event.getAction();

        switch(action) {

            case DragEvent.ACTION_DRAG_STARTED:
                // Couldn't get DrawerLayout via id
                DrawerLayout d = (DrawerLayout) v.getParent().getParent().getParent().getParent();
                d.closeDrawers();
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                return true;

            case DragEvent.ACTION_DROP:
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
            default:
                break;
        }
        return false;
    }
}
