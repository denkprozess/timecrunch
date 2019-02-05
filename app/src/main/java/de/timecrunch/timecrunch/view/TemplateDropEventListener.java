package de.timecrunch.timecrunch.view;

import android.content.ClipData;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlock;
import de.timecrunch.timecrunch.viewModel.PlannerViewModel;

public class TemplateDropEventListener implements View.OnDragListener {

    private final int ROW_HEIGHT = 108;
    private PlannerViewModel plannerViewModel;
    private ProgressBar progressBar;

    public TemplateDropEventListener(PlannerViewModel plannerViewModel, ProgressBar progressBar) {
        this.plannerViewModel = plannerViewModel;
        this.progressBar = progressBar;
    }

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
        // String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes
        TimeBlock blockModel = new TimeBlock("1", color, hours, quarters * 15, hours + 2, quarters * 15);
        blockModel.addTask(new TaskModel("1", "Duschen"));
        blockModel.addTask(new TaskModel("1", "Zähne putzen"));
        blockModel.addTask(new TaskModel("1", "Zeitung lesen"));
        plannerViewModel.addTimeBlock("1", color, hours, quarters * 15, hours + 2, quarters * 15, progressBar);
    }

    private int dpToPx(View v, int dp) {
        float density = v.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
