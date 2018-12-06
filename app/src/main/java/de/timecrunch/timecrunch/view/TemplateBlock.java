package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class TemplateBlock extends View {

    private final int RECT_WIDTH = dpToPx(120);
    private final int RECT_HEIGHT = dpToPx(72);
    private final int PADDING = dpToPx(16);

    private Rect block;
    private Paint fillColor;
    private Paint strokeColor;
    private Paint textColor;
    private String colorString;

    public TemplateBlock(Context context, String colorString) {
        super(context);
        this.colorString = colorString;
        init(null);
    }

    public TemplateBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TemplateBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public TemplateBlock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        createBlock();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setRectBounds(block, 0, 0);
        canvas.drawRect(block, fillColor);
        canvas.drawRect(block, strokeColor);
        canvas.drawText(
                "TEMPLATE",
                (block.width() / 2 - 10),
                block.top + block.height() / 2,
                textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = RECT_WIDTH + (2 * PADDING);
        int height = RECT_HEIGHT + PADDING;
        setMeasuredDimension(width, height);
    }

    private void createBlock() {

        block = new Rect();

        fillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillColor.setStyle(Paint.Style.FILL);
        fillColor.setColor(Color.parseColor(colorString));

        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setStyle(Paint.Style.STROKE);
        strokeColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));
        strokeColor.setStrokeWidth(2);

        textColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        textColor.setColor(Color.BLACK);
        textColor.setTextSize(25);
    }

    private void setRectBounds(Rect r, int left, int top) {
        r.left = left + PADDING;
        r.top = top + PADDING;
        r.right = r.left + RECT_WIDTH;
        r.bottom = r.top + RECT_HEIGHT;
    }

    private int dpToPx(int dp) {
        float density = this.getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

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

    public String getColor() {
        return this.colorString;
    }
}
