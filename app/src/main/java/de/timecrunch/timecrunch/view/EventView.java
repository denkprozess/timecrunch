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

    private static final int SQUARE_SIZE = 200;

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
            setRectBounds(mRectSquare, 50, 50);
        }
        canvas.drawRect(mRectSquare, mPaintSquare);
    }

    private void setRectBounds(Rect r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + SQUARE_SIZE;
        r.bottom = r.top + SQUARE_SIZE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();

                if (mRectSquare.left < x && mRectSquare.right > x) {
                    if (mRectSquare.top < y && mRectSquare.bottom > y) {
                        offsetX = (int) (x - mRectSquare.top);
                        offsetY = (int) (y - mRectSquare.left);
                    }
                    return true;
                }
                return value;
            }
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();

                setRectBounds(mRectSquare, (int) x - ((mRectSquare.bottom - mRectSquare.top) / 2), (int) y - ((mRectSquare.right - mRectSquare.left) / 2));
                postInvalidate();

                return true;
            }
        }
        return value;
    }
}
