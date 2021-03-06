package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

public class HourView extends android.support.v7.widget.AppCompatTextView {

    private static final int PADDING = 24;

    public HourView(Context context) {
        super(context);
    }

    public HourView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFullHourLine(canvas);
    }

    private void drawFullHourLine(Canvas canvas) {
        Paint paint = getPaint();
        paint.setColor(Color.parseColor("#F0F0F0"));
        paint.setStyle(Paint.Style.STROKE);

        int left = getPaddingLeft();
        int top = (getHeight() + getPaddingTop() - getPaddingBottom()) / 2;
        int right = getWidth() - PADDING;
        int bottom = top + 1;

        int textWidth = (int) paint.measureText(getText().toString());

        canvas.drawRect(left + textWidth + PADDING + 10, top, right - 10, bottom, paint);
    }
}
