package in.prashanthrao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentSwitcherView extends View {

    private String leftSideText;
    private String rightSideText;

    private Integer leftSideColor;
    private Integer rightSideColor;

    private Integer leftSideTextColor;
    private Integer rightSideTextColor;

    private Integer leftSideTextSize;
    private Integer rightSideTextSize;

    private Integer selectedTextSize;
    private Integer nonSelectedTextSize;

    private Integer selectedTextColor;
    private Integer nonSelectedTextColor;

    private Drawable leftSideLeftImage;
    private Drawable leftSideRightImage;

    private Drawable rightSideLeftImage;
    private Drawable rightSideRightImage;

    private Paint paintShape;
    private TextPaint textPaint;

    private int selected = LEFT;
    private float density;

    public static int LEFT = 0;
    public static int RIGHT = 1;

    private Set<ISelectionChangeListener> selectionChangeListeners = new HashSet<>();

    public ContentSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ContentSwitcherView, 0, 0);
        // Extract custom attributes into member variables
        try {

            leftSideText = a.getString(R.styleable.ContentSwitcherView_leftSideText);
            rightSideText = a.getString(R.styleable.ContentSwitcherView_rightSideText);

            leftSideColor = a.getColor(R.styleable.ContentSwitcherView_leftSideColor, Color.GREEN);
            rightSideColor = a.getColor(R.styleable.ContentSwitcherView_rightSideColor, Color.RED);

            leftSideTextColor = a.getColor(R.styleable.ContentSwitcherView_leftSideTextColor, Color.BLACK);
            rightSideTextColor = a.getColor(R.styleable.ContentSwitcherView_rightSideTextColor, Color.BLACK);

            leftSideTextSize = a.getInteger(R.styleable.ContentSwitcherView_leftSideTextSize, 18);
            rightSideTextSize = a.getInteger(R.styleable.ContentSwitcherView_rightSideTextSize, 18);

            selectedTextSize = a.getColor(R.styleable.ContentSwitcherView_selectedTextSize, 24);
            nonSelectedTextSize = a.getColor(R.styleable.ContentSwitcherView_nonSelectedTextSize, 18);

            selectedTextColor = a.getColor(R.styleable.ContentSwitcherView_selectedTextColor, Color.BLACK);
            nonSelectedTextColor = a.getColor(R.styleable.ContentSwitcherView_nonSelectedTextColor, Color.BLACK);

            leftSideLeftImage = a.getDrawable(R.styleable.ContentSwitcherView_leftSideLeftImage);
            leftSideRightImage = a.getDrawable(R.styleable.ContentSwitcherView_leftSideRightImage);

            rightSideLeftImage = a.getDrawable(R.styleable.ContentSwitcherView_rightSideLeftImage);
            rightSideRightImage = a.getDrawable(R.styleable.ContentSwitcherView_rightSideRightImage);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }

        density = getResources().getDisplayMetrics().density;


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        paintShape = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintShape.setStyle(Paint.Style.FILL);
//        paintShape.setShadowLayer(12, 0, 0, Color.GRAY);
////        // Important for certain APIs
//        setLayerType(LAYER_TYPE_SOFTWARE, paintShape);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 10);
                }
            };
            setOutlineProvider(viewOutlineProvider); //Notice I have used inheritance
            setClipToOutline(true);
        }


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int clickedOnLeftSide = isClickedOnLeftSide(motionEvent);
//                        isLeftSelected = !isLeftSelected;
                        int oldValue = selected;
                        if(clickedOnLeftSide == selected) {
                        } else {
                            selected = clickedOnLeftSide;
                            invalidate();
                            for (ISelectionChangeListener selectionChangeListener : selectionChangeListeners) {
                                selectionChangeListener.onSelectionChange(view, oldValue, selected);
                            }
                        }

                        return true;
                }
                return false;
            }

            private int isClickedOnLeftSide(MotionEvent motionEvent) {
                float clickX = motionEvent.getX();
                float clickY = motionEvent.getY();

                int width = getWidth();
                int height = getHeight();

                if(clickX > width/2) {
                    return RIGHT;
                }
                return LEFT;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY) {

        } else if(specMode == MeasureSpec.AT_MOST) {

        } else if(specMode == MeasureSpec.UNSPECIFIED) {

        }

        Rect leftBounds = getLeftTextRect();
        int leftSideTextSize = (int)(leftBounds.height() * density);

        Rect rightBounds = getRightTextRect();
        int rightSideTextSize =  (int)(rightBounds.height() * density);

        int height = leftSideTextSize > rightSideTextSize? leftSideTextSize : rightSideTextSize;
        return height;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY) {

        } else if(specMode == MeasureSpec.AT_MOST) {

        } else if(specMode == MeasureSpec.UNSPECIFIED) {

        }
        return specSize;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

//         Draw now for the width of parent and height constant
        Rect leftBounds = getLeftTextRect();
        Rect rightBounds = getRightTextRect();

        int width = getWidth();
        int height = getHeight();
//        height = getHeight() < height? height : getHeight(); //TODO validate the calculation of height

        // draw rectangle
        drawLeftRect(canvas, width, height);
        drawRightRect(canvas, width, height);

        // draw text
        if (selected == LEFT) {
            drawRightText(canvas, width, height, rightBounds);
            drawLeftText(canvas, width, height, leftBounds);
        } else {
            drawLeftText(canvas, width, height, leftBounds);
            drawRightText(canvas, width, height, rightBounds);
        }


//        changeSelection(canvas, width, height);
    }

    private void changeSelection(Canvas canvas, int width, int height) {
        drawRightRect(canvas, width, height);
    }

    private void drawLeftRect(Canvas canvas, int width, int height) {
        int color = selected == LEFT? leftSideColor : Color.GRAY;
        paintShape.setColor(color);
        canvas.drawRect(0, 0, width/2, height, paintShape);
    }

    private void drawRightRect(Canvas canvas, int width, int height) {
        int color = selected == RIGHT? rightSideColor : Color.GRAY;
        paintShape.setColor(color);
        canvas.drawRect(width/2, 0, width, height, paintShape);
    }

    private Rect getLeftTextRect() {
        Rect bounds = new Rect();
        textPaint.setTextSize(leftSideTextSize * density);
        textPaint.getTextBounds(leftSideText, 0, leftSideText.length(), bounds);
        return bounds;
    }

    private Rect getRightTextRect() {
        Rect bounds = new Rect();
        textPaint.setTextSize(rightSideTextSize * density);
        textPaint.getTextBounds(rightSideText, 0, rightSideText.length(), bounds);
        return bounds;
    }

    private void drawLeftText(Canvas canvas, int width, int height, Rect bounds) {

        textPaint.setTextSize(leftSideTextSize * density);
        textPaint.setColor(leftSideTextColor);

        int textWidth = bounds.width();
        int x = getPaddingLeft() + (width/2 - textWidth)/2;

        int textHeight = bounds.height();
        int y = getPaddingTop() + (height - textHeight)/2 - textHeight/4;

        canvas.save();
        canvas.translate(x, y);
        StaticLayout staticLayout = new StaticLayout(leftSideText, textPaint, width/2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawRightText(Canvas canvas, int width, int height, Rect bounds) {

        textPaint.setTextSize(rightSideTextSize * density);
        textPaint.setColor(rightSideTextColor);

        int textWidth = bounds.width();
        int x = getPaddingRight() + (width/2 - textWidth)/2 + width/2;

        int textHeight = bounds.height();
        int y = getPaddingTop() + (height - textHeight)/2 - textHeight/4;

        canvas.save();
        canvas.translate(x, y);
        StaticLayout staticLayout = new StaticLayout(rightSideText, textPaint, width/2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void refresh() {
        invalidate();
        requestLayout();
    }

    public String getLeftSideText() {
        return leftSideText;
    }

    public void setLeftSideText(String leftSideText) {
        this.leftSideText = leftSideText;
        refresh();
    }

    public String getRightSideText() {
        return rightSideText;
    }

    public void setRightSideText(String rightSideText) {
        this.rightSideText = rightSideText;
        refresh();
    }

    public Integer getLeftSideColor() {
        return leftSideColor;
    }

    public void setLeftSideColor(Integer leftSideColor) {
        this.leftSideColor = leftSideColor;
        refresh();
    }

    public Integer getRightSideColor() {
        return rightSideColor;
    }

    public void setRightSideColor(Integer rightSideColor) {
        this.rightSideColor = rightSideColor;
        refresh();
    }

    public Integer getLeftSideTextColor() {
        return leftSideTextColor;
    }

    public void setLeftSideTextColor(Integer leftSideTextColor) {
        this.leftSideTextColor = leftSideTextColor;
        refresh();
    }

    public Integer getRightSideTextColor() {
        return rightSideTextColor;
    }

    public void setRightSideTextColor(Integer rightSideTextColor) {
        this.rightSideTextColor = rightSideTextColor;
        refresh();
    }

    public Integer getLeftSideTextSize() {
        return leftSideTextSize;
    }

    public void setLeftSideTextSize(Integer leftSideTextSize) {
        this.leftSideTextSize = leftSideTextSize;
        refresh();
    }

    public Integer getRightSideTextSize() {
        return rightSideTextSize;
    }

    public void setRightSideTextSize(Integer rightSideTextSize) {
        this.rightSideTextSize = rightSideTextSize;
        refresh();
    }

    public Integer getSelectedTextSize() {
        return selectedTextSize;
    }

    public void setSelectedTextSize(Integer selectedTextSize) {
        this.selectedTextSize = selectedTextSize;
        refresh();
    }

    public Integer getNonSelectedTextSize() {
        return nonSelectedTextSize;
    }

    public void setNonSelectedTextSize(Integer nonSelectedTextSize) {
        this.nonSelectedTextSize = nonSelectedTextSize;
        refresh();
    }

    public Integer getSelectedTextColor() {
        return selectedTextColor;
    }

    public void setSelectedTextColor(Integer selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
        refresh();
    }

    public Integer getNonSelectedTextColor() {
        return nonSelectedTextColor;
    }

    public void setNonSelectedTextColor(Integer nonSelectedTextColor) {
        this.nonSelectedTextColor = nonSelectedTextColor;
        refresh();
    }

    public Drawable getLeftSideLeftImage() {
        return leftSideLeftImage;
    }

    public void setLeftSideLeftImage(Drawable leftSideLeftImage) {
        this.leftSideLeftImage = leftSideLeftImage;
        refresh();
    }

    public Drawable getLeftSideRightImage() {
        return leftSideRightImage;
    }

    public void setLeftSideRightImage(Drawable leftSideRightImage) {
        this.leftSideRightImage = leftSideRightImage;
        refresh();
    }

    public Drawable getRightSideLeftImage() {
        return rightSideLeftImage;
    }

    public void setRightSideLeftImage(Drawable rightSideLeftImage) {
        this.rightSideLeftImage = rightSideLeftImage;
        refresh();
    }

    public Drawable getRightSideRightImage() {
        return rightSideRightImage;
    }

    public void setRightSideRightImage(Drawable rightSideRightImage) {
        this.rightSideRightImage = rightSideRightImage;
        refresh();
    }

    public void setOnSelectionChangeListeners(ISelectionChangeListener selectionChangeListener) {
        selectionChangeListeners.add(selectionChangeListener);
    }
}
