package com.example.rzc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;

/**
 * Created by 93502 on 2017/7/3.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    private Rect mRect;
    private Paint mPaint;

    private final int padding = 10;

    private Layout mLayout;
    private int paddingTop;
    private int paddingBottom;
    private int mHeight;
    private int mLayoutHeight;


    private int lineHeight;
    private int viewHeight,viewWidth;
    public CustomEditText(Context context) {
        this(context,null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        //this.setLineSpacing(2.0f,1.5f);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = heightMeasureSpec;
        mLayout =getLayout();
        mLayoutHeight = mLayout.getHeight();
        paddingBottom = getTotalPaddingBottom();
        paddingTop = getTotalPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
        int lineHeight = 0;
        int i = 0;
        while (i < count) {
            lineHeight = getLineBounds(i, r);
            //int baseline = (i+1)*getLineHeight();
            canvas.drawLine(r.left, lineHeight + padding, r.right, lineHeight + padding, paint);
            i++;
        }
        int maxLines = 30;
        int avgHeight = lineHeight / count;
        int currentLineHeight = lineHeight;

        while(i < maxLines){
            currentLineHeight = currentLineHeight + avgHeight + padding;
            canvas.drawLine(r.left, currentLineHeight, r.right, currentLineHeight, paint);
            i++;
        }
        super.onDraw(canvas);
    }







}
