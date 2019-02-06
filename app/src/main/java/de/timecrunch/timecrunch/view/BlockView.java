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
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;

public class BlockView extends View {

    // 18 - 1 = 0.25h
    // 36 - 1 = 0.50h
    // 54 - 1 = 0.75h
    // 72 - 1 = 1.00h
    private final int ENTRY_PADDING = dpToPx(15);
    private final int TITLE_HEIGHT = dpToPx(20);
    private final int ENTRY_FIRSTLINE_SPACING = TITLE_HEIGHT;

    private Rect block;
    private RectF roundedBlock;
    private Paint blockFillColor;
    private Paint scaleHandleColor;
    private Paint blockHighlightColor;
    private Paint tasksTextColor;
    private Paint ruledTasksTextColor;

    private String colorString;
    private String blockId;
    private int textHeight;
    private int startHours;
    private int startMinutes;
    private int endHours;
    private int endMinutes;

    private boolean editMode = false;

    private float circleX = 0;
    private float circleY = 0;

    private float circleRadius = dpToPx(6);
    private ArrayList<TimeBlockTaskModel> tasks;


    public BlockView(Context context, String blockId, ArrayList<TimeBlockTaskModel> tasks, String colorString,
                     int width, int startHours, int startMinutes, int endHours, int endMinutes) {
        super(context);
        this.blockId = blockId;
        this.colorString = colorString;
        this.startHours = startHours;
        this.startMinutes = startMinutes;
        this.endHours = endHours;
        this.endMinutes = endMinutes;
        init(null);

        if(tasks != null) {
            this.tasks = new ArrayList<>(tasks);
        } else {
            this.tasks = new ArrayList<TimeBlockTaskModel>();
        }

        setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        setLayoutParams(new ViewGroup.LayoutParams(width - dpToPx(60), (dpToPx(5) + (getCalculatedSize() * dpToPx(18)))));

        setX(dpToPx(49));
        setY(dpToPx(19) + (startHours * dpToPx(72) + ((startMinutes / 15) * dpToPx(18))));

        setElevation(2);

        setOnLongClickListener(new BlockOnLongClickListener());
    }

    public BlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public BlockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        createBlock("TITEL");
    }

    private void createBlock(String title) {

        block = new Rect();
        roundedBlock = new RectF();

        blockFillColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockFillColor.setStyle(Paint.Style.FILL);
        blockFillColor.setColor(manipulateColor(Color.parseColor(colorString), 1.2f));

        blockHighlightColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockHighlightColor.setStyle(Paint.Style.STROKE);
        blockHighlightColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));
        blockHighlightColor.setStrokeWidth(10);

        scaleHandleColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleHandleColor.setStyle(Paint.Style.FILL);
        scaleHandleColor.setColor(manipulateColor(Color.parseColor(colorString), 0.8f));

        tasksTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        tasksTextColor.setColor(Color.WHITE);
        tasksTextColor.setTextSize(35);
        tasksTextColor.setFakeBoldText(true);

        ruledTasksTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        ruledTasksTextColor.setColor(Color.parseColor("#F0F0F0"));
        ruledTasksTextColor.setTextSize(35);
        ruledTasksTextColor.setStrikeThruText(true);
    }

    private void setRoundedBlockBounds(RectF r, int left, int top) {
        r.left = left;
        r.top = top;
        r.right = r.left + getWidth();
        r.bottom = r.top + getHeight() - dpToPx(6);
    }

    private void setRoundedBlockHighlightBounds(RectF r, int left, int top) {
        r.left = left + 4f;
        r.top = top + 4f;
        r.right = (r.left + getWidth()) - 8f;
        r.bottom = (r.top + getHeight() - dpToPx(6)) - 8f;
    }

    private void initPosition() {
        setRoundedBlockBounds(roundedBlock, 0, 0);
    }

    private int calculateTaskEntryCount() {

        int height = getHeight() - dpToPx(6);

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

        int cornerRadius = 25;

        canvas.drawRoundRect(
                roundedBlock,
                cornerRadius,
                cornerRadius,
                blockFillColor
        );

        if(editMode) {
            drawScaleHandle(canvas);
            RectF highlight = new RectF();
            setRoundedBlockHighlightBounds(highlight, 0, 0);
            canvas.drawRoundRect(highlight, cornerRadius, cornerRadius, blockHighlightColor);
        }

        int counter = 0;
        if(tasks.size() <= calculateTaskEntryCount()) {
            counter = tasks.size();
        } else {
            counter = calculateTaskEntryCount();
        }

        for(int i = 0; i < counter; i++) {
            TaskModel task = tasks.get(i).getTask();
            boolean isFinished = tasks.get(i).getIsFinished();
            int textWidth = (int) tasksTextColor.measureText(task.getText());
            int posX = (getWidth() / 2) - (textWidth / 2);
            if(isFinished) {
                canvas.drawText(task.getText(), posX, (ENTRY_FIRSTLINE_SPACING + (i * textHeight)), ruledTasksTextColor);
            } else {
                canvas.drawText(task.getText(), posX, (ENTRY_FIRSTLINE_SPACING + (i * textHeight)), tasksTextColor);
            }
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
                new Paint(scaleHandleColor));

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

    public RectF getBlock() {
        return roundedBlock;
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

    private int getCalculatedSize() {
        int startTime = (startHours * 60) + startMinutes;
        int endTime = (endHours * 60) + endMinutes;
        return (endTime - startTime) / 15;
    }

    public void moveUp() {
        int startTime = (startHours * 60) + startMinutes;
        int endTime = (endHours * 60) + endMinutes;

        startTime -= 15;
        endTime -= 15;

        this.startHours = startTime / 60;
        this.startMinutes = startTime % 60;
        this.endHours = endTime / 60;
        this.endMinutes = endTime % 60;
    }

    public void moveDown() {
        int startTime = (startHours * 60) + startMinutes;
        int endTime = (endHours * 60) + endMinutes;

        startTime += 15;
        endTime += 15;

        this.startHours = startTime / 60;
        this.startMinutes = startTime % 60;
        this.endHours = endTime / 60;
        this.endMinutes = endTime % 60;
    }

    public void scaleUp() {
        int endTime = (endHours * 60) + endMinutes;

        endTime += 15;

        this.endHours = endTime / 60;
        this.endMinutes = endTime % 60;
    }

    public void scaleDown() {
        int endTime = (endHours * 60) + endMinutes;

        endTime -= 15;

        this.endHours = endTime / 60;
        this.endMinutes = endTime % 60;
    }

    public String getBlockId() {
        return blockId;
    }

    public int getStartHours() {
        return startHours;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public int getEndHours() {
        return endHours;
    }

    public int getEndMinutes() {
        return endMinutes;
    }
}
