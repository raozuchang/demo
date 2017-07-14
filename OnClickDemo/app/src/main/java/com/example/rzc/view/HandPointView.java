package com.example.rzc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 手绘
 * Created by 93502 on 2017/7/2.
 */

public class HandPointView extends View {
    private Paint mPaint;
    private Context mContext;

    private final int defColor = Color.RED;
    private int mPaintColor = defColor;
    private Path path;
    private Bitmap mBitmap;
    private Canvas cacheCanvas;
    int i = 0;

    private int width,height ;

    private int startX = 0;

    private float mPaintSize = 1;


    public HandPointView(Context context) {
        this(context, null);
    }

    public HandPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private void init() {
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mPaintColor);
        mPaint.setStrokeWidth(mPaintSize);

        path = new Path();

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height =2*wm.getDefaultDisplay().getHeight();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(mBitmap);
        cacheCanvas.drawColor(Color.parseColor("#00000000"));//透明背景
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width,height);
        //width = getWidth();
       // height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#00000000"));
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(path, mPaint);
    }

    private float cur_x, cur_y;
    private boolean isMoving;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = startX+ event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                cur_x = x;
                cur_y = y;
                path.moveTo(cur_x, cur_y);
                isMoving = true;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!isMoving)
                    break;

                // 二次曲线方式绘制
                //path.quadTo(cur_x, cur_y, x, y);
                // 下面这个方法貌似跟上面一样
                path.lineTo(x, y);
                //cacheCanvas.drawPoint(x,y,mPaint);
                Log.e("handPoint", "onTouchEvent: x="+x+"y="+y+" i= "+(i++));
                cur_x = x;
                cur_y = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                // 鼠标弹起保存最后状态
                cacheCanvas.drawPath(path, mPaint);
                path.reset();
                isMoving = false;
                break;
            }
        }

        // 通知刷新界面
        invalidate();

        return true;
    }

    public void setStartX(int startX){
        this.startX = startX;
    }

    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
        mPaint.setColor(mPaintColor);
    }

    public void setmPaintSize(float mPaintSize) {
        this.mPaintSize = mPaintSize;
        mPaint.setStrokeWidth(mPaintSize);
    }
}
