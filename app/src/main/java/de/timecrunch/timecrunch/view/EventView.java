package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class EventView extends View {

    // 18 - 1 = 0.25h
    // 36 - 1 = 0.50h
    // 54 - 1 = 0.75h
    // 72 - 1 = 1.00h
    private final int SQUARE_SIZE = dpToPx(71);

    private Rect mRectSquare;
    private Paint mPaintSquare;

    public EventView(Context context) {
        super(context);
        init(null);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSquare.setColor(Color.GREEN);
    }

    private int offsetX = 0;
    private int offsetY = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(offsetY == 0 && offsetX == 0) {
            setRectBounds(mRectSquare, SQUARE_SIZE, SQUARE_SIZE);
        }
        canvas.drawRect(mRectSquare, mPaintSquare);
    }

    private void setRectBounds(Rect r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + SQUARE_SIZE;
        r.bottom = r.top + SQUARE_SIZE;
    }

    private int dpToPx(int dp) {
        float density = this.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getParent().requestDisallowInterceptTouchEvent(true);
                float x = event.getX();
                float y = event.getY();

                if (mRectSquare.left < x && mRectSquare.right > x) {
                    if (mRectSquare.top < y && mRectSquare.bottom > y) {
                        offsetX = (int) (x - mRectSquare.top);
                        offsetY = (int) (y - mRectSquare.left);
                        return true;
                    }
                }
                return value;
            }
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();

                setRectBounds(mRectSquare, (int) x - ((mRectSquare.bottom - mRectSquare.top) / 2),
                        (int) y - ((mRectSquare.right - mRectSquare.left) / 2));
                postInvalidate();

                return true;
            }
            case MotionEvent.ACTION_UP: {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return value;
    }
}
