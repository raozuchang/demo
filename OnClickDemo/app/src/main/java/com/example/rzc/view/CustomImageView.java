package com.example.rzc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.example.rzc.onclickdemo.R;

/**
 * Created by 93502 on 2017/6/26.
 */

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "CustomImageView";
    private Context mContext;
    private Paint mPaint;
    private DisplayMetrics metrics;
    private int mHeight, mWidth;
    private float maxScale = 3f;//设置最大缩放比例
    private float minScale = 0.1f;//设置最小缩放比例
    private final int NONE = 0;
    private final int MOVE = 1;
    private final int ZOOM = 2;
    private final int ROTATE = 3;
    private final int DELETE = 4;


    private int mode = NONE;


    private boolean isInEdit = true;

    private Bitmap mBitmap;//要绘制的图像
    private Bitmap deleteBitmap;
    private Bitmap moveBitmap;
    private Bitmap rotateBitmap;
    private Bitmap zoomBitmap;
    private Rect dst_delete;
    private Rect dst_move;
    private Rect dst_rotate;
    private Rect dst_zoom;
    private int deleteBitmapWidth;
    private int deleteBitmapHeight;
    private int moveBitmapWidth;
    private int moveBitmapHeight;
    private int rotateBitmapWidth;
    private int rotateBitmapHeight;
    private int zoomBitmapWidth;
    private int zoomBitmapHeight;

    private int mScreenWidth, mScreenHeight;
    private int imageId = 0;

    private PointF mCenterPoint = new PointF();

    private float lastRotateDegree;

    private float lastX, lastY;


    private PointF mid = new PointF();
    private PointF top_left = new PointF();

    private Matrix matrix = new Matrix();

    private static final float BITMAP_SCALE = 0.7f;

    private int mViewPaddingLeft;
    private int mViewPaddingTop;

    private int mViewWidth, mViewHeight;


    private float originWidth = 0;
    /**
     * 对角线的长度
     */
    private float lastLength;

    private double halfDiagonalLength;

    private OperationListener operationListener;


    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //obtainStyledAttributes(attrs);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                if (isInEdit) {
                    if (isInButton(event, dst_delete)) {
                        if (operationListener != null) {
                            operationListener.onDeleteClick();
                        }
                        mode = DELETE;
                    } else if (isInResize(event)) {
                        mode = ZOOM;
                        lastRotateDegree = rotationToStartPoint(event);
                        midPointToStartPoint(event);
                        lastLength = diagonalLength(event);

                    } else if (isInButton(event, dst_move)) {
                        //移动事件
                        mode = MOVE;
                        lastX = event.getX(0);
                        lastY = event.getY(0);
                    } else if (isInButton(event, dst_rotate)) {
                        //旋转事件
                        mode = ROTATE;
                        //计算角度
                        midDiagonalPoint(mid);
                        lastRotateDegree = rotationToStartPoint(event);
                    } else if (isInBitmap(event)) {
                        //移动事件

                        mode = MOVE;
                        lastX = event.getX(0);
                        lastY = event.getY(0);


                    } else {
                        handled = false;
                    }
                } else {
                    if (!isInBitmap(event)){
                        handled = false;
                    }
                    mode = NONE;
                }

                if (handled){
                    if (operationListener!=null)
                    operationListener.reset();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                switch (mode) {
                    case MOVE:

                        float x = event.getX(0);
                        float y = event.getY(0);
                        //TODO 移动区域判断 不能超出屏幕
                        matrix.postTranslate(x - lastX, y - lastY);
                        lastX = x;
                        lastY = y;
                        invalidate();


                        break;
                    case ZOOM:

                        float scale = diagonalLength(event) / lastLength;

                        if (((diagonalLength(event) / halfDiagonalLength <= minScale)) && scale < 1 ||
                                (diagonalLength(event) / halfDiagonalLength >= maxScale) && scale > 1) {
                            scale = 1;
                        } else {
                            lastLength = diagonalLength(event);
                        }
                        matrix.preScale(scale, scale);
                        invalidate();
                        break;
                    case ROTATE:
                        matrix.postRotate((rotationToStartPoint(event) - lastRotateDegree) * 2, mid.x, mid.y);
                        lastRotateDegree = rotationToStartPoint(event);
                        invalidate();
                        break;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mode==NONE){
                    isInEdit = !isInEdit;
                    invalidate();
                }
                mode = NONE;
                break;
        }


        return handled;
    }

    /**
     * 两个点之间的距离
     *
     * @return
     */
    private float distance4PointF(PointF pf1, PointF pf2) {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 获取自定义样式
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        metrics = mContext.getResources().getDisplayMetrics();



    }

    private void init(){
        dst_delete = new Rect();
        dst_zoom = new Rect();
        dst_rotate = new Rect();
        dst_move = new Rect();
        //初始化画笔
        mPaint = new Paint();

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tu);

        metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        ViewGroup parent = (ViewGroup) this.getParent();
        if (parent!=null){
            mScreenWidth = parent.getWidth();
            mScreenHeight = parent.getHeight();
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {

            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];

            canvas.save();
            canvas.drawBitmap(mBitmap, matrix, null);
            //删除在左上角
            dst_delete.left = (int) (f1 - deleteBitmapWidth / 2);
            dst_delete.right = (int) (f1 + deleteBitmapWidth / 2);
            dst_delete.top = (int) (f2 - deleteBitmapHeight / 2);
            dst_delete.bottom = (int) (f2 + deleteBitmapHeight / 2);
            //拉伸在右下角
            dst_zoom.left = (int) (f7 - zoomBitmapWidth / 2);
            dst_zoom.right = (int) (f7 + zoomBitmapWidth / 2);
            dst_zoom.top = (int) (f8 - zoomBitmapWidth / 2);
            dst_zoom.bottom = (int) (f8 + zoomBitmapWidth / 2);
            //移动在右上角
            dst_move.left = (int) (f3 - moveBitmapWidth / 2);
            dst_move.right = (int) (f3 + moveBitmapWidth / 2);
            dst_move.top = (int) (f4 - moveBitmapWidth / 2);
            dst_move.bottom = (int) (f4 + moveBitmapWidth / 2);
            //旋转左下角
            dst_rotate.left = (int) (f5 - rotateBitmapHeight / 2);
            dst_rotate.right = (int) (f5 + rotateBitmapHeight / 2);
            dst_rotate.top = (int) (f6 - rotateBitmapHeight / 2);
            dst_rotate.bottom = (int) (f6 + rotateBitmapHeight / 2);

            if (isInEdit) {

                canvas.drawLine(f1, f2, f3, f4, mPaint);
                canvas.drawLine(f3, f4, f7, f8, mPaint);
                canvas.drawLine(f5, f6, f7, f8, mPaint);
                canvas.drawLine(f5, f6, f1, f2, mPaint);

                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(zoomBitmap, null, dst_zoom, null);
                canvas.drawBitmap(moveBitmap, null, dst_move, null);
                canvas.drawBitmap(rotateBitmap, null, dst_rotate, null);
            }

            canvas.restore();

        }

    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取SingleTouchView所在父布局的中心点
        ViewGroup mViewGroup = (ViewGroup) getParent();
        Log.e(TAG, "onMeasure: " + mViewGroup.toString());
        if (null != mViewGroup) {
            int parentWidth = mViewGroup.getWidth();
            int parentHeight = mViewGroup.getHeight();
            Log.e(TAG, "onMeasure:  H=" + parentHeight + "W= " + parentWidth);
            mCenterPoint.set(parentWidth / 2, parentHeight / 2);
        }
    }


    @Override
    public void setImageResource(@DrawableRes int resId) {
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setBitmap(Bitmap bitmap) {
        matrix.reset();
        mBitmap = bitmap;
        setDiagonalLength();
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originWidth = w;
        float initScale = (minScale + maxScale) / 2;
        matrix.postScale(initScale, initScale, w / 2, h / 2);
        //Y坐标为 （顶部操作栏+正方形图）/2
        matrix.postTranslate(mScreenWidth / 2 - w / 2, (mScreenWidth) / 2 - h / 2);
        invalidate();

    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    /**
     * 计算图片的角度等属性
     *
     * @param model
     * @return
     */
    public ImageModel calculate(ImageModel model) {
        float[] v = new float[9];
        matrix.getValues(v);
        // translation is simple
        float tx = v[Matrix.MTRANS_X];
        float ty = v[Matrix.MTRANS_Y];
        Log.d(TAG, "tx : " + tx + " ty : " + ty);
        // calculate real scale
        float scalex = v[Matrix.MSCALE_X];
        float skewy = v[Matrix.MSKEW_Y];
        float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
        Log.d(TAG, "rScale : " + rScale);
        // calculate the degree of rotation
        float rAngle = Math.round(Math.atan2(v[Matrix.MSKEW_X], v[Matrix.MSCALE_X]) * (180 / Math.PI));
        Log.d(TAG, "rAngle : " + rAngle);

        PointF localPointF = new PointF();
        midDiagonalPoint(localPointF);

        Log.d(TAG, " width  : " + (mBitmap.getWidth() * rScale) + " height " + (mBitmap.getHeight() * rScale));

        float minX = localPointF.x;
        float minY = localPointF.y;

        Log.d(TAG, "midX : " + minX + " midY : " + minY);
        model.setDegree((float) Math.toRadians(rAngle));
        //TODO 占屏幕百分比
        float precentWidth = (mBitmap.getWidth() * rScale) / mScreenWidth;
        model.setScaling(precentWidth);
        model.setxLocation(minX / mScreenWidth);
        model.setyLocation(minY / mScreenWidth);
        model.setImageId(imageId);
        return model;
    }


    /**
     * 是否在四条线内部
     * 图片旋转后 可能存在菱形状态 不能用4个点的坐标范围去判断点击区域是否在图片内
     *
     * @return
     */
    private boolean isInBitmap(MotionEvent event) {
        float[] arrayOfFloat1 = new float[9];
        this.matrix.getValues(arrayOfFloat1);
        //左上角
        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f2 = 0.0F * arrayOfFloat1[3] + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //右上角
        float f3 = arrayOfFloat1[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f4 = arrayOfFloat1[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //左下角
        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
        //右下角
        float f7 = arrayOfFloat1[0] * this.mBitmap.getWidth() + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f8 = arrayOfFloat1[3] * this.mBitmap.getWidth() + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];

        float[] arrayOfFloat2 = new float[4];
        float[] arrayOfFloat3 = new float[4];
        //确定X方向的范围
        arrayOfFloat2[0] = f1;//左上的x
        arrayOfFloat2[1] = f3;//右上的x
        arrayOfFloat2[2] = f7;//右下的x
        arrayOfFloat2[3] = f5;//左下的x
        //确定Y方向的范围
        arrayOfFloat3[0] = f2;//左上的y
        arrayOfFloat3[1] = f4;//右上的y
        arrayOfFloat3[2] = f8;//右下的y
        arrayOfFloat3[3] = f6;//左下的y
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
    }


    /**
     * 判断点是否在一个矩形内部
     *
     * @param xRange
     * @param yRange
     * @param x
     * @param y
     * @return
     */
    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        //四条边的长度
        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
        //待检测点到四个点的距离
        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;

        //矩形的面积
        double s = a1 * a2;
        //海伦公式 计算4个三角形面积
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        return Math.abs(s - ss) < 0.5;


    }

    /**
     * 计算双指之间的距离
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    /**
     * 触摸是否在某个button范围
     *
     * @param event
     * @param rect
     * @return
     */
    private boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    /**
     * 触摸是否在拉伸区域内
     *
     * @param event
     * @return
     */
    private boolean isInResize(MotionEvent event) {
        int left = -20 + this.dst_zoom.left;
        int top = -20 + this.dst_zoom.top;
        int right = 20 + this.dst_zoom.right;
        int bottom = 20 + this.dst_zoom.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float diagonalLength(MotionEvent event) {
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
    }

    /**
     * 触摸的位置和图片右上角位置的中点
     *
     * @param event
     */
    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];//x轴偏移量
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];//y轴偏移量
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    /**
     * 计算对角线交叉的位置
     *
     * @param paramPointF
     */
    private void midDiagonalPoint(PointF paramPointF) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
        float f5 = f1 + f3;
        float f6 = f2 + f4;
        paramPointF.set(f5 / 2.0F, f6 / 2.0F);
    }

    /**
     * 在旋转过程中,总是以右上角原点作为绝对坐标计算偏转角度
     *
     * @param event
     * @return
     */
    private float rotationToStartPoint(MotionEvent event) {

        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        double arc = Math.atan2(event.getY(0) - y, event.getX(0) - x);
        return (float) Math.toDegrees(arc);
    }

    private void initBitmaps() {

        //当图片的宽比高大时 按照宽计算 缩放大小根据图片的大小而改变 最小为图片的1/8 最大为屏幕宽
        if (mBitmap.getWidth() >= mBitmap.getHeight()) {
            float minWidth = mScreenWidth / 8;
            if (mBitmap.getWidth() < minWidth) {
                minScale = 1f;
            } else {
                minScale = 1.0f * minWidth / mBitmap.getWidth();
            }

            if (mBitmap.getWidth() > mScreenWidth) {
                maxScale = 1;
            } else {
                maxScale = 1.0f * mScreenWidth / mBitmap.getWidth();
            }
        } else {
            //当图片高比宽大时，按照图片的高计算
            float minHeight = mScreenWidth / 8;
            if (mBitmap.getHeight() < minHeight) {
                minScale = 1f;
            } else {
                minScale = 1.0f * minHeight / mBitmap.getHeight();
            }

            if (mBitmap.getHeight() > mScreenWidth) {
                maxScale = 1;
            } else {
                maxScale = 1.0f * mScreenWidth / mBitmap.getHeight();
            }
        }

        zoomBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zoom);
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cancel);
        moveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mobile);
        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rotation);

        deleteBitmapWidth = (int) (deleteBitmap.getWidth() * BITMAP_SCALE);
        deleteBitmapHeight = (int) (deleteBitmap.getHeight() * BITMAP_SCALE);

        zoomBitmapWidth = (int) (zoomBitmap.getWidth() * BITMAP_SCALE);
        zoomBitmapHeight = (int) (zoomBitmap.getHeight() * BITMAP_SCALE);

        moveBitmapWidth = (int) (moveBitmap.getWidth() * BITMAP_SCALE);
        moveBitmapHeight = (int) (moveBitmap.getHeight() * BITMAP_SCALE);

        rotateBitmapWidth = (int) (rotateBitmap.getWidth() * BITMAP_SCALE);
        rotateBitmapHeight = (int) (rotateBitmap.getHeight() * BITMAP_SCALE);
    }

    public void setInEdit(boolean b) {
        isInEdit = b;
        invalidate();
    }

    public interface OperationListener {
        void onDeleteClick();
        void reset();
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }
}
