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

public class EditBlock extends View {

    // 18 - 1 = 0.25h
    // 36 - 1 = 0.50h
    // 54 - 1 = 0.75h
    // 72 - 1 = 1.00h
    private final int SQUARE_SIZE = dpToPx(72);

    private final int PADDING_TOP = dpToPx(19);
    private final int PADDING_LEFT = dpToPx(49);

    private final int QUARTER = dpToPx(18);
    private final int HALFHOUR = dpToPx(36);
    private final int HOUR = dpToPx(72);

    private static final int PADDING = 60;

    private Rect block;
    private Paint fillColor;
    private Paint strokeColor;
    private Paint mPaintText;

    private boolean clicked = false;
    private String colorString;
    private int hour;
    private int quarter;

    public EditBlock(Context context, String colorString, int hour, int quarter) {
        super(context);
        this.colorString = colorString;
        this.hour = hour;
        this.quarter = quarter;
        init(null);
    }

    public EditBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public EditBlock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        createBlock("TITEL");
    }

    private void createBlock(String title) {

        block = new Rect();

        fillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillColor.setStyle(Paint.Style.FILL);
        fillColor.setColor(Color.parseColor(colorString));

        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setStyle(Paint.Style.STROKE);
        strokeColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));
        strokeColor.setStrokeWidth(2);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(35);
    }

    private void setRectBounds(Rect r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + getWidth() - dpToPx(PADDING);
        r.bottom = r.top + SQUARE_SIZE;
    }

    private void initPosition() {
        setRectBounds(block, PADDING_LEFT, PADDING_TOP + (hour * HOUR) + (quarter * QUARTER));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!clicked) {
            initPosition();
        }

        canvas.drawRect(block, fillColor);
        canvas.drawRect(block, strokeColor);

        canvas.drawText("TEST", (block.width() / 2) + 80, block.top + block.height() / 2, mPaintText);
    }

    private int dpToPx(int dp) {
        float density = this.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    // factor below 1.0f to darken color
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
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

                if (block.left < x && block.right > x) {
                    if (block.top < y && block.bottom > y) {
                        clicked = true;
                        return clicked;
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
                    setRectBounds(block,
                            dpToPx(PADDING - 11),
                            block.top + DISTANCE);
                    // (int) y - ((block.bottom - block.top) / 2));
                    oldY = y;
                } else if (y < (oldY - DISTANCE)) {
                    setRectBounds(block,
                            dpToPx(PADDING - 11),
                            block.top - DISTANCE);
                    // (int) y - ((block.bottom - block.top) / 2));
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
