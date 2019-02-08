package de.timecrunch.timecrunch.viewListener;

import android.content.ClipData;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import de.timecrunch.timecrunch.viewModel.PlannerViewModel;

public class TemplateDropEventListener implements View.OnDragListener {

    // private final int ROW_HEIGHT = 108;
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

                ClipData.Item item2 = event.getClipData().getItemAt(1);
                String categoryId = (String) item2.getText();

                if(v instanceof FrameLayout) {
                    createNewEditBlock(event.getY(), color, categoryId, v);
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;
            default:
                break;
        }
        return false;
    }

    private void createNewEditBlock(float y, String color, String categoryId, View v) {

        final int ROW_HEIGHT = dpToPx(v, 36);

        int hours = 0;
        int quarters = 0;

        y = y - ROW_HEIGHT;
        float _y = y / ROW_HEIGHT;
        _y = _y / 2;
        hours = (int) _y;

        if(_y - hours > 0.49) {
            quarters = 2;
        }

        plannerViewModel.addTimeBlock(categoryId, color, hours, quarters * 15, hours + 1, quarters * 15, progressBar);
    }

    private int dpToPx(View v, int dp) {
        float density = v.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
