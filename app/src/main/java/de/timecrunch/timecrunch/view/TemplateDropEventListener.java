package de.timecrunch.timecrunch.view;

import android.content.ClipData;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class TemplateDropEventListener implements View.OnDragListener {

    private final int ROW_HEIGHT = 108;

    @Override
    public boolean onDrag(View v, DragEvent event) {

        final int action = event.getAction();

        switch(action) {

            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                return true;

            case DragEvent.ACTION_DROP:

                ClipData.Item item = event.getClipData().getItemAt(0);
                String color = (String) item.getText();

                if(v instanceof FrameLayout) {
                    createNewEditBlock(event.getY(), color, v);
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
            default:
                break;
        }
        return false;
    }

    private void createNewEditBlock(float y, String color, View v) {

        int hours = 0;
        int quarters = 0;

        y = y - ROW_HEIGHT;
        float _y = y / ROW_HEIGHT;
        _y = _y / 2;
        hours = (int) _y;

        if(_y - hours > 0.49) {
            quarters = 2;
        }

        EditBlock editBlock = new EditBlock(v.getContext(), color, hours, quarters);
        editBlock.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        editBlock.setElevation(2);
        ((FrameLayout) v).addView(editBlock);
    }
}
