package com.example.rzc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.rzc.onclickdemo.R;

/**
 * 需求:可以移动缩放的editText
 * 左上角:删除，右上角:移动，右下角:缩放
 * Created by 93502 on 2017/7/6.
 */

public class MyEditText extends android.support.v7.widget.AppCompatEditText{
    private static final String TAG = "MyEdit";
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
    private int deleteBitmapWidth;
    private int deleteBitmapHeight;
    private int moveBitmapWidth;
    private int moveBitmapHeight;
    private int rotateBitmapWidth;
    private int rotateBitmapHeight;
    private int zoomBitmapWidth;
    private int zoomBitmapHeight;

    private PointF tlPointF, trPointF,brPointF;

    int textWidth;
    int textHeight;

    private PointF centerPoint;

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
        dst_delete = new Rect();
        dst_zoom = new Rect();
        dst_move = new Rect();
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        width = wm.getDefaultDisplay().getWidth();
        height =wm.getDefaultDisplay().getHeight();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         textWidth = getMeasuredWidth();
         textHeight = getMeasuredHeight();
        //设置中心点
        centerPoint = new PointF(textHeight,width/2);


        ViewGroup mViewGroup = (ViewGroup) getParent();
        Log.e(TAG, "onMeasure: " + mViewGroup.toString());
        if (null != mViewGroup) {
            int parentWidth = mViewGroup.getWidth();
            int parentHeight = mViewGroup.getHeight();
            Log.e(TAG, "onMeasure:  H=" + parentHeight + "W= " + parentWidth);
            setMeasuredDimension(parentWidth,parentHeight);

        }

        tlPointF = new PointF(getX(),getY());
        trPointF = new PointF(getX()+textWidth,getY());
        brPointF = new PointF(getX()+textWidth,textHeight+getY());
        initBitMaps();
    }

    private void initBitMaps() {

        zoomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zoom);
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cancel);
        moveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mobile);

        deleteBitmapWidth =  (deleteBitmap.getWidth());
        deleteBitmapHeight =  (deleteBitmap.getHeight() );

        zoomBitmapWidth =  (zoomBitmap.getWidth() );
        zoomBitmapHeight =  (zoomBitmap.getHeight() );

        moveBitmapWidth =  (moveBitmap.getWidth() );
        moveBitmapHeight = (moveBitmap.getHeight() );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEdit){
            canvas.save();

            //删除在左上角
            dst_delete.left = (int) (tlPointF.x - deleteBitmapWidth / 2);
            dst_delete.right = (int) (tlPointF.x + deleteBitmapWidth / 2);
            dst_delete.top = (int) (tlPointF.y - deleteBitmapHeight / 2);
            dst_delete.bottom = (int) (tlPointF.y + deleteBitmapHeight / 2);
            //拉伸在右下角
            dst_zoom.left = (int) (brPointF.x - zoomBitmapWidth / 2);
            dst_zoom.right = (int) (brPointF.x + zoomBitmapWidth / 2);
            dst_zoom.top = (int) (brPointF.y - zoomBitmapWidth / 2);
            dst_zoom.bottom = (int) (brPointF.y + zoomBitmapWidth / 2);
            //移动在右上角
            dst_move.left = (int) (trPointF.x -moveBitmapWidth / 2);
            dst_move.right = (int) (trPointF.x + moveBitmapWidth / 2);
            dst_move.top = (int) (trPointF.y - moveBitmapWidth / 2);
            dst_move.bottom = (int) (trPointF.y + moveBitmapWidth / 2);


            canvas.drawLine(tlPointF.x, tlPointF.y, trPointF.x, trPointF.y, mPaint);
            canvas.drawLine( trPointF.x, trPointF.y, brPointF.x, brPointF.y, mPaint);
            canvas.drawLine( brPointF.x, brPointF.y, getX(), getY()+textHeight, mPaint);
            canvas.drawLine(getX(), getY()+textHeight, tlPointF.x, tlPointF.y, mPaint);

            canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
            canvas.drawBitmap(zoomBitmap, null, dst_zoom, null);
            canvas.drawBitmap(moveBitmap, null, dst_move, null);


        }

    }

    public void setInEdit(boolean inEdit) {
        isInEdit = inEdit;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
