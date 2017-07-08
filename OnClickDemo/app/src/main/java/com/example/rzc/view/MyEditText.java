package com.example.rzc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * 需求:可以移动缩放的editText
 * 左上角:删除，右上角:移动，右下角:缩放
 * Created by 93502 on 2017/7/6.
 */

public class MyEditText extends android.support.v7.widget.AppCompatEditText{
    private Context mContext;
    private Paint mPaint;
    private PointF mPonitF;
    private boolean isInEdit = true;
    private int width;
    private int height;

    private Bitmap deleteBitmap;
    private Bitmap moveBitmap;
    private Bitmap zoomBitmap;
    private Rect dst_delete;
    private Rect dst_move;
    private Rect dst_zoom;

    public MyEditText(Context context) {
        this(context,null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = widthMeasureSpec;
        height = heightMeasureSpec;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEdit){


        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
