package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class TemplateBlock extends View {

    private final int RECT_WIDTH = dpToPx(120);
    private final int RECT_HEIGHT = dpToPx(50);
    private final int PADDING = dpToPx(16);

    private Rect block;
    private RectF roundedBlock;
    private Paint fillColor;
    private Paint strokeColor;
    private Paint textColor;
    private String colorString;
    private String title;

    public TemplateBlock(Context context, String colorString, String title) {
        super(context);
        this.colorString = colorString;
        this.title = title;
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

        setRectBounds(roundedBlock, 0, 0);
        canvas.drawRoundRect(
                roundedBlock,
                25,
                25,
                fillColor
        );

        int textWidth = (int) textColor.measureText(title);
        int posX = (getWidth() / 2) - (textWidth / 2);
        canvas.drawText(
                title,
                posX,
                roundedBlock.top + roundedBlock.height() / 1.8f,
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
        roundedBlock = new RectF();

        fillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillColor.setStyle(Paint.Style.FILL);
        fillColor.setColor(Color.parseColor(colorString));

        strokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeColor.setStyle(Paint.Style.STROKE);
        strokeColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));
        strokeColor.setStrokeWidth(2);

        textColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        textColor.setColor(Color.WHITE);
        textColor.setTextSize(30);
    }

    private void setRectBounds(RectF r, int left, int top) {
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
