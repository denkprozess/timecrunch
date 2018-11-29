package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class EventView extends View {

    // 18 - 1 = 0.25h
    // 36 - 1 = 0.50h
    // 54 - 1 = 0.75h
    // 72 - 1 = 1.00h
    private final int SQUARE_SIZE = dpToPx(71);
    private static final int PADDING = 60;

    private Rect mRectSquare;
    private Paint mPaintSquare;
    private Paint mPaintBorder;
    private Paint mPaintText;

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
        mPaintSquare.setStyle(Paint.Style.FILL);
        mPaintSquare.setColor(Color.parseColor("#FF58D903"));

        mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setColor(Color.parseColor("#FF44A703"));
        mPaintBorder.setStrokeWidth(2);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(35);
    }

    private int offsetX = 0;
    private int offsetY = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(offsetY == 0 && offsetX == 0) {
            setRectBounds(mRectSquare, dpToPx(PADDING - 11), dpToPx(19));
        }
        canvas.drawRect(mRectSquare, mPaintSquare);
        canvas.drawRect(mRectSquare, mPaintBorder);

        canvas.drawText("TEST", (mRectSquare.width() / 2) + 80, mRectSquare.top + mRectSquare.height() / 2, mPaintText);
    }

    private void createBlock(String title, Color color, int x, int y) {
        Rect block = new Rect();

        Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#FF44A703"));

        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#FF44A703"));
        strokePaint.setStrokeWidth(2);


    }

    private void setRectBounds(Rect r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + getWidth() - dpToPx(PADDING);
        r.bottom = r.top + SQUARE_SIZE;
    }

    private int dpToPx(int dp) {
        float density = this.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    float oldY = 0.0f;
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

                if (oldY == 0) {
                    oldY = y;
                }

                int DISTANCE = dpToPx(18);

                if (y > oldY + DISTANCE) {
                    setRectBounds(mRectSquare,
                            dpToPx(PADDING - 11),
                            mRectSquare.top + DISTANCE);
                    // (int) y - ((mRectSquare.bottom - mRectSquare.top) / 2));
                    oldY = y;
                } else if (y < (oldY - DISTANCE)) {
                    setRectBounds(mRectSquare,
                            dpToPx(PADDING - 11),
                            mRectSquare.top - DISTANCE);
                    // (int) y - ((mRectSquare.bottom - mRectSquare.top) / 2));
                    oldY = y;
                }

                Log.d(" ", "oldY: " + String.valueOf(oldY));
                Log.d(" ", "Y: " + String.valueOf(y));


                postInvalidate();

                return true;
            }
            case MotionEvent.ACTION_UP: {
                getParent().requestDisallowInterceptTouchEvent(false);
                oldY = 0;
            }
        }
        return value;
    }
}
