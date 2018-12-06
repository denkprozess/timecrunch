package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RectangleView extends View {

    int c;

    public RectangleView(Context context, int color) {
        super(context);
        c = color;
    }

    public RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawColor(c);
        canvas.drawRect(10,10,50,50, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        return 300;
    }

    private int measureHeight(int measureSpec) {
        return 100;
    }
}
