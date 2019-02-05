package de.timecrunch.timecrunch.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class BlockOnTouchListener implements View.OnTouchListener {

    boolean move = false;
    boolean scale = false;

    float oldY = 0.0f;
    float oldCircleY = 0.0f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        EditBlock b = (EditBlock) v;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                b.getParent().requestDisallowInterceptTouchEvent(true);
                float x = event.getX();
                float y = event.getY();

                double dx = Math.pow(x - b.getCircleX(), 2);
                double dy = Math.pow(y - b.getCircleY(), 2);

                // Scale
                if(dx + dy < Math.pow(b.getCircleRadius() * 4, 2)) {
                    scale = true;
                }

                // Move
                else if (b.getBlock().left < x && b.getBlock().right > x) {
                    if (b.getBlock().top < y && b.getBlock().bottom > y) {
                        move = true;
                        // return move;
                    }
                }
                b.setElevation(5);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float y = event.getY();

                if (oldY == 0) {
                    oldY = y;
                }

                if (oldCircleY == 0) {
                    oldCircleY = y;
                }

                int DISTANCE = dpToPx(b, 18);
                int LOWER_BOUND = (DISTANCE * 24 * 4) + DISTANCE;

                if (scale) {
                    if((y > oldCircleY + DISTANCE) && (b.getY() + b.getHeight()) < LOWER_BOUND) {
                        b.scaleUp();
                        ViewGroup.LayoutParams params = b.getLayoutParams();
                        params.height = params.height + DISTANCE;
                        b.setLayoutParams(params);

                        b.setCircleY(b.getCircleY() + DISTANCE);
                        oldCircleY = y;
                        b.invalidate();
                    } else if ((y < oldCircleY - DISTANCE) && (oldCircleY - DISTANCE > DISTANCE)) {
                        b.scaleDown();
                        ViewGroup.LayoutParams params = b.getLayoutParams();
                        params.height = params.height - DISTANCE;
                        b.setLayoutParams(params);

                        b.setCircleY(b.getCircleY() - DISTANCE);
                        oldCircleY = y;
                        b.invalidate();
                    }
                } else if (move) {
                    if ((y > (oldY + DISTANCE)) && ((b.getY() + b.getHeight()) < LOWER_BOUND)) {
                        b.setY(b.getY() + DISTANCE);
                        b.moveDown();
                    } else if ((y < (oldY - DISTANCE)) && ((b.getY() - DISTANCE) > DISTANCE)) {
                        b.setY(b.getY() - DISTANCE);
                        b.moveUp();
                    }
                }

                return true;
            }
            case MotionEvent.ACTION_UP: {
                move = false;
                scale = false;
                b.getParent().requestDisallowInterceptTouchEvent(false);
                oldY = 0;
                oldCircleY = 0;
                b.setElevation(2);
            }
        }
        return true;
    }

    private int dpToPx(View v, int dp) {
        float density = v.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
