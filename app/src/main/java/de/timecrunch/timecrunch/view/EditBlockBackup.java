package de.timecrunch.timecrunch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class EditBlockBackup extends View {

    // 18 - 1 = 0.25h
    // 36 - 1 = 0.50h
    // 54 - 1 = 0.75h
    // 72 - 1 = 1.00h
    private final int ENTRY_PADDING = dpToPx(16);
    private final int TITLE_HEIGHT = dpToPx(20);
    private final int ENTRY_FIRSTLINE_SPACING = 2 * TITLE_HEIGHT;

    private Rect block;
    private Rect titleRect;
    private Paint blockFillColor;
    private Paint blockStrokeColor;
    private Paint titleFillColor;
    private Paint titleHighlightColor;
    private Paint titleTextColor;
    private Paint tasksTextColor;

    private String colorString;
    private int textHeight;

    private boolean editMode = false;

    private float circleX = 0;
    private float circleY = 0;

    private float circleRadius = dpToPx(6);

    private String[] tasks = {"Duschen", "Zähne putzen", "Bett machen", "Frühstücken", "Aufräumen", "Uni vorbereiten", "Stoßlüften", "Noch mehr"};

    public EditBlockBackup(Context context, String colorString, int hour, int quarter) {
        super(context);
        this.colorString = colorString;
        init(null);
    }

    public EditBlockBackup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EditBlockBackup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public EditBlockBackup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        createBlock("TITEL");
    }

    private void createBlock(String title) {

        block = new Rect();
        titleRect = new Rect();

        blockFillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockFillColor.setStyle(Paint.Style.FILL);
        blockFillColor.setColor(manipulateColor(Color.parseColor(colorString), 1.4f));

        blockStrokeColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockStrokeColor.setStyle(Paint.Style.STROKE);
        blockStrokeColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));
        blockStrokeColor.setStrokeWidth(2);

        titleFillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleFillColor.setStyle(Paint.Style.FILL);
        titleFillColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));

        titleHighlightColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleHighlightColor.setStyle(Paint.Style.FILL);
        titleHighlightColor.setColor(manipulateColor(Color.parseColor(colorString), 0.6f));

        titleTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleTextColor.setColor(Color.WHITE);
        titleTextColor.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titleTextColor.setTextSize(35);

        tasksTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        tasksTextColor.setColor(Color.BLACK);
        tasksTextColor.setTextSize(35);
    }

    private void setTitleRectBounds(Rect r) {
        r.left = 0;
        r.top = 0;
        r.right = getWidth();
        r.bottom = TITLE_HEIGHT;
    }

    private void setBlockBounds(Rect r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + getWidth();
        r.bottom = r.top + getHeight() - dpToPx(6);
    }

    private void initPosition() {
        setBlockBounds(block, 0, 0);
        setTitleRectBounds(titleRect);
    }

    private int calculateTaskEntryCount() {

        int height = getHeight() - TITLE_HEIGHT - dpToPx(6);

        String text = "TEXT";

        Rect textBounds = new Rect();
        Paint textPaint = new Paint();

        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        textHeight = textBounds.height() + ENTRY_PADDING;

        return height / textHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initPosition();

        // canvas.drawColor(Color.MAGENTA);
        canvas.drawRect(block, blockFillColor);
        canvas.drawRect(block, blockStrokeColor);

        if(!editMode) {
            canvas.drawRect(titleRect, titleFillColor);
        } else {
            canvas.drawRect(titleRect, titleHighlightColor);
            drawScaleHandle(canvas);
        }

        canvas.drawText("Title", dpToPx(5), dpToPx(15), titleTextColor);

        int counter = 0;
        if(tasks.length <= calculateTaskEntryCount()) {
            counter = tasks.length;
        } else {
            counter = calculateTaskEntryCount();
        }

        for(int i = 0; i < counter; i++) {
            canvas.drawText("□  " + tasks[i], dpToPx(25), (ENTRY_FIRSTLINE_SPACING + (i * textHeight)), tasksTextColor);
        }
    }

    private void drawScaleHandle(Canvas c) {
        if(circleX == 0 && circleY == 0) {
            circleX = getWidth() - dpToPx(15);
            circleY = getHeight() - dpToPx(6);
        }

        c.drawCircle(
                circleX,
                circleY,
                circleRadius,
                new Paint(titleFillColor));

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

    public void setEditMode(boolean b) {
        if(b) {
            this.setOnTouchListener(new BlockOnEditModeTouchListener());
            invalidate();
        } else {
            this.setOnTouchListener(null);
            invalidate();
        }
        this.editMode = b;
    }

    public Rect getBlock() {
        return block;
    }

    public float getCircleX() {
        return circleX;
    }

    public float getCircleY() {
        return circleY;
    }

    public void setCircleY(float circleY) {
        this.circleY = circleY;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
