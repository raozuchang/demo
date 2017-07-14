package com.example.rzc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * 固定死高度，当行数*行高>=固定高度 时，不再增加行数
 * Created by 93502 on 2017/7/3.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    private Rect mRect;
    private Paint mPaint;

    private final int padding = 15;

    private Layout mLayout;
    private int paddingTop;
    private int paddingBottom;
    private int mHeight;
    private int mLayoutHeight;

    private int lineHeight;
    private int viewHeight, viewWidth;
    private OnFullInput myOnFullInput;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#ffcc99"));
        mPaint.setAntiAlias(true);



        //this.setLineSpacing(2.0f,1.5f);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getHeight();
        mLayout = getLayout();
        mLayoutHeight = mLayout.getHeight();
        paddingBottom = getTotalPaddingBottom();
        paddingTop = getTotalPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();
        Rect r = mRect;
        Paint paint = mPaint;
        int maxCount = 60;

        lineHeight = getLineBounds(0, r);
        for (int i = 0; i <maxCount ; i++) {
            canvas.drawLine(r.left,lineHeight+padding,r.right,lineHeight+padding,paint);
            lineHeight+=getLineHeight();
        }
        /* int lineHeight = getLineHeight();
        int i = 0;

        while (i < count) {
            lineHeight = getLineBounds(i, r);
            //int baseline = (i+1)*getLineHeight();
            canvas.drawLine(r.left, lineHeight + padding, r.right, lineHeight + padding, paint);
            i++;
        }
        int maxLines = 60;
        int avgHeight = lineHeight / count;
        int currentLineHeight = lineHeight;

        while (i < maxLines) {
            currentLineHeight = currentLineHeight + avgHeight + padding;
            canvas.drawLine(r.left, currentLineHeight, r.right, currentLineHeight, paint);
            i++;
        }*/
        MyOnEditorActionListener myOnEditorActionListener = new MyOnEditorActionListener();
        setOnEditorActionListener(myOnEditorActionListener);
        super.onDraw(canvas);
    }


    public void setMyOnFullInput(OnFullInput input) {
        myOnFullInput = input;
    }

    public interface OnFullInput {
        void doNextPage();
    }

    public class MyOnEditorActionListener implements OnEditorActionListener {
        int mLineHeight = getLineHeight();
        int mCount = getLineCount();
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (mLineHeight * mCount >= mHeight-mLineHeight) {
                    myOnFullInput.doNextPage();
                    return true;
                }
            }
            return false;
        }
    }

}
