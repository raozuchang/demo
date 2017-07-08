package com.example.rzc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.rzc.onclickdemo.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by 93502 on 2017/6/23.
 */

public class SingleImageView extends View {
    private Context mContext;

    public Bitmap zoomMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.zoom);
    public Bitmap deleteMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.cancel);
    public Bitmap rotateMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.rotation);
    public Bitmap moveMarkBM = BitmapFactory.decodeResource(getResources(), R.drawable.mobile);

    public RectF markerZoomRect = new RectF(0, 0, zoomMarkBM.getWidth(), zoomMarkBM.getHeight());//移动标记边界
    public RectF markerDeleteRect = new RectF(0, 0, deleteMarkBM.getWidth(), deleteMarkBM.getHeight());//删除标记边界
    public RectF markerRotateRect = new RectF(0, 0, rotateMarkBM.getWidth(), rotateMarkBM.getHeight());//旋转标记边界
    public RectF markerMoveRect = new RectF(0, 0, moveMarkBM.getWidth(), moveMarkBM.getHeight());//平移标记边界

    private Drawable zoomDraw,deleteDraw,rotateDraw,moveDraw;

    /**
     * 用于缩放，旋转，平移的矩阵
     */
    private Matrix matrix = new Matrix();

    /**
     * 缩放，旋转图标的宽和高
     */
    private int mDrawableWidth, mDrawableHeight;

    private float mScale;
    /**
     * 所要编辑的bitmap
     */
    private Bitmap mBitmap;

    /**
     * SingleImageView距离父类布局的左间距
     */
    private int mViewPaddingLeft;

    /**
     * SingleImageView距离父类布局的上间距
     */
    private int mViewPaddingTop;

    /**
     * 图片在旋转时x方向的偏移量
     */
    private int offsetX;
    /**
     * 图片在旋转时y方向的偏移量
     */
    private int offsetY;

    /**
     * 用于移动.删除.旋转.缩放控制点的坐标
     */
    private Point mMovePoint = new Point();
    private Point mZoomPoint = new Point();
    private Point mRotatePoint = new Point();
    private Point mDeletePoint = new Point();

    /**
     * 一些默认的常量
     */
    public static final int DEFAULT_FRAME_PADDING = 1;
    public static final int DEFAULT_FRAME_WIDTH = 1;
    public static final int DEFAULT_FRAME_COLOR = Color.WHITE;
    public static final float DEFAULT_SCALE = 1.0f;
    public static final float DEFAULT_DEGREE = 0;
    public static final boolean DEFAULT_EDITABLE = true;
    private static final String TAG = "SingleTouchView";

    /**
     * 外边框与图片之间的间距, 单位是dip
     */
    private int framePadding = DEFAULT_FRAME_PADDING;
    /**
     * 外边框线条粗细, 单位是 dip
     */
    private int frameWidth = DEFAULT_FRAME_WIDTH;

    /**
     * 图片的旋转角度
     */
    private float mDegree = DEFAULT_DEGREE;

    public static final int MOVE = 1;
    public static final int ZOOM = 2;
    public static final int ROTATE = 3;
    public static final int DELETE = 4;
    public static final int NONE = 0;
    //默认无状态
    private int mode = NONE;

    /**
     * 外边框颜色
     */
    private int frameColor = DEFAULT_FRAME_COLOR;
    /**
     * 是否处于可编辑状态
     */
    private boolean isEditAble = true;


    private PointF mPreMovePointF = new PointF();
    private PointF mCurMovePointF = new PointF();
    /**
     * 图片四个点坐标
     */
    private Point mLTPoint;
    private Point mRTPoint;
    private Point mRBPoint;
    private Point mLBPoint;
    /**
     * 画外围框的Path
     */
    private Path mPath = new Path();
    /**
     * SingleTouchView的中心点坐标，相对于其父类布局而言的
     */
    private PointF mCenterPoint = new PointF();

    /**
     * View的宽度和高度，随着图片的旋转而变化(不包括控制旋转，缩放图片的宽高)
     */
    private int mViewWidth, mViewHeight;

    public Paint mPaint;

    private DisplayMetrics metrics;


    public SingleImageView(Context context) {
        this(context, null);
    }

    public SingleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        obtainStyledAttributes(attrs);
        init();

    }

    /**
     * 获取自定义样式
     *
     * @param attrs
     */

    private void obtainStyledAttributes(AttributeSet attrs) {
        metrics = mContext.getResources().getDisplayMetrics();

        framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_PADDING, metrics);
        frameWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_WIDTH, metrics);

        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.SingleImageView);

        Drawable srcDrawable = mTypedArray.getDrawable(R.styleable.SingleImageView_src);
        if (srcDrawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) srcDrawable;
            this.mBitmap = bd.getBitmap();
        }
      /*  Drawable topRight = mTypedArray.getDrawable(R.styleable.SingleImageView_topRightDrawable);
        if (topRight instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) topRight;
            this.moveMarkBM = bd.getBitmap();
        }
        Drawable topLeft = mTypedArray.getDrawable(R.styleable.SingleImageView_topLeftDrawable);
        if (topLeft instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) topLeft;
            this.deleteMarkBM = bd.getBitmap();
        }
        Drawable bottomLeft = mTypedArray.getDrawable(R.styleable.SingleImageView_bottomLeftDrawable);
        if (bottomLeft instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) bottomLeft;
            this.rotateMarkBM = bd.getBitmap();
        }
        Drawable bottomRight = mTypedArray.getDrawable(R.styleable.SingleImageView_bottomRightDrawable);
        if (bottomRight instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) bottomRight;
            this.zoomMarkBM = bd.getBitmap();
        }*/

        framePadding = mTypedArray.getDimensionPixelSize(R.styleable.SingleImageView_framePadding, framePadding);
        frameWidth = mTypedArray.getDimensionPixelSize(R.styleable.SingleImageView_frameWidth, frameWidth);
        frameColor = mTypedArray.getColor(R.styleable.SingleImageView_frameColor, DEFAULT_FRAME_COLOR);
        mScale = mTypedArray.getFloat(R.styleable.SingleImageView_scale, DEFAULT_SCALE);
        mDegree = mTypedArray.getFloat(R.styleable.SingleImageView_degree, DEFAULT_DEGREE);

        deleteDraw  = mTypedArray.getDrawable(R.styleable.SingleImageView_topLeftDrawable);
        moveDraw = mTypedArray.getDrawable(R.styleable.SingleImageView_topRightDrawable);
        rotateDraw = mTypedArray.getDrawable(R.styleable.SingleImageView_bottomLeftDrawable);
        zoomDraw = mTypedArray.getDrawable(R.styleable.SingleImageView_bottomRightDrawable);

        isEditAble = mTypedArray.getBoolean(R.styleable.SingleImageView_editable, DEFAULT_EDITABLE);

        mTypedArray.recycle();

    }


    private void init() {
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);



        transformRotateDraw();


    }

    @Override
    protected void onDraw(Canvas canvas) {

        //每次draw之前调整View的位置和大小
        adjustLayout();

        super.onDraw(canvas);
        if (mBitmap == null) return;
        canvas.drawBitmap(mBitmap, matrix, null);
        if (isEditAble) {
            //画边框
            /*mPath.reset();
            mPath.moveTo(mLTPoint.x, mLTPoint.y);
            mPath.lineTo(mRTPoint.x, mRTPoint.y);
            mPath.lineTo(mRBPoint.x, mRBPoint.y);
            mPath.lineTo(mLBPoint.x, mLBPoint.y);
            mPath.lineTo(mLTPoint.x, mLTPoint.y);
            mPath.lineTo(mRTPoint.x, mRTPoint.y);
            canvas.drawPath(mPath, mPaint);*/
            //drawMarks(canvas,);
            //画四个角标
            float[] photoCorners = calculateCorners(mLTPoint, mRTPoint, mLBPoint, mRBPoint);//计算图片四个角点和中心点
            drawBoard(canvas, photoCorners);//绘制图形边线
            drawMarks(canvas, photoCorners);//绘制边角图片

        }


    }


    //绘制图像边线（由于图形旋转或不一定是矩形，所以用Path绘制边线）
    public void drawBoard(Canvas canvas, float[] photoCorners) {
        Path photoBorderPath = new Path();
        photoBorderPath.moveTo(photoCorners[0], photoCorners[1]);
        photoBorderPath.lineTo(photoCorners[2], photoCorners[3]);
        photoBorderPath.lineTo(photoCorners[4], photoCorners[5]);
        photoBorderPath.lineTo(photoCorners[6], photoCorners[7]);
        photoBorderPath.lineTo(photoCorners[0], photoCorners[1]);
        canvas.drawPath(photoBorderPath, mPaint);
    }


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

    public float[] calculateCorners(Point tl, Point tr, Point bl, Point br) {
        float[] photoCornersSrc = new float[8];//0,1代表左上角点XY，2,3代表右上角点XY，4,5代表右下角点XY，6,7代表左下角点XY
        float[] photoCorners = new  float[8];//0,1代表左上角点XY，2,3代表右上角点XY，4,5代表右下角点XY，6,7代表左下角点XY
        photoCornersSrc[0] = tl.x;
        photoCornersSrc[1] = tl.y;
        photoCornersSrc[2] = tr.x;
        photoCornersSrc[3] = tr.y;
        photoCornersSrc[4] = br.x;
        photoCornersSrc[5] = br.y;
        photoCornersSrc[6] = bl.x;
        photoCornersSrc[7] = bl.y;

        //curPhotoRecord.matrix.mapPoints(photoCorners, photoCornersSrc);
        return photoCorners;
    }

    public void drawMarks(Canvas canvas, float[] photoCorners) {
        float x;
        float y;
        x = photoCorners[0] - markerDeleteRect.width() / 2;
        y = photoCorners[1] - markerDeleteRect.height() / 2;
        markerDeleteRect.offsetTo(x, y);
        canvas.drawBitmap(deleteMarkBM, x, y, null);

        x = photoCorners[2] - markerMoveRect.width() / 2;
        y = photoCorners[3] - markerMoveRect.height() / 2;
        markerMoveRect.offsetTo(x, y);
        canvas.drawBitmap(moveMarkBM, x, y, null);

        x = photoCorners[4] - markerRotateRect.width() / 2;
        y = photoCorners[5] - markerRotateRect.height() / 2;
        markerRotateRect.offsetTo(x, y);
        canvas.drawBitmap(rotateMarkBM, x, y, null);

        x = photoCorners[6] - markerZoomRect.width() / 2;
        y = photoCorners[7] - markerZoomRect.height() / 2;
        markerZoomRect.offsetTo(x, y);
        canvas.drawBitmap(zoomMarkBM, x, y, null);
    }


    /**
     * 设置Matrix矩阵, 强制刷新
     * 旋转模式下
     */
    private void transformRotateDraw() {
        int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
        int bitmapHeight = (int) (mBitmap.getHeight() * mScale);

        computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, mDegree);
        //设置缩放比例
        //matrix.setScale(mScale, mScale);
        //绕着图片中心进行旋转
        matrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
        //设置画该图片的起始点
        matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);

        invalidate();

    }

    /**
     * 设置Matrix矩阵, 强制刷新
     * 缩放模式下
     */
    private void transformZoomDraw() {
        int bitmapWidth = (int) (mBitmap.getWidth() * mScale);
        int bitmapHeight = (int) (mBitmap.getHeight() * mScale);

        computeRect(-framePadding, -framePadding, bitmapWidth + framePadding, bitmapHeight + framePadding, mDegree);
        //设置缩放比例
        matrix.setScale(mScale, mScale);//这个方法是相对中心点

        //绕着图片中心进行旋转
        //matrix.postRotate(mDegree % 360, bitmapWidth / 2, bitmapHeight / 2);
        //设置画该图片的起始点
        matrix.postTranslate(offsetX + mDrawableWidth / 2, offsetY + mDrawableHeight / 2);

        invalidate();

    }




    /**
     * 调整View位置大小
     */
    private void adjustLayout() {

        int actualWidth = mViewWidth + mDrawableWidth;
        int actualHeight = mViewHeight + mDrawableHeight;
        Log.e(TAG, "adjustLayout: actH="+actualHeight+"W="+actualWidth);

        int newPaddingLeft = (int) (mCenterPoint.x - actualWidth /2);
        int newPaddingTop = (int) (mCenterPoint.y - actualHeight/2);
        Log.e(TAG, "adjustLayout: newPadL="+newPaddingLeft+"top="+newPaddingTop );

        mLTPoint.set(newPaddingLeft,newPaddingTop);
        mRTPoint.set(newPaddingLeft+actualWidth,newPaddingTop);
        mLBPoint.set(newPaddingLeft,newPaddingTop+actualHeight);
        mRBPoint.set(newPaddingLeft+actualWidth,newPaddingTop+actualHeight);

        if(mViewPaddingLeft != newPaddingLeft || mViewPaddingTop != newPaddingTop){
            mViewPaddingLeft = newPaddingLeft;
            mViewPaddingTop = newPaddingTop;
            Log.e(TAG, "adjustLayout: mViewPaddingLeft="+newPaddingLeft+"newPaddingTop="+newPaddingTop );
            layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
        }

    }

    /**
     * 设置旋转图
     *
     * @param bitmap
     */
    public void setImageBitamp(Bitmap bitmap) {
        this.mBitmap = bitmap;
        transformRotateDraw();
    }


    /**
     * 设置旋转图
     *
     * @param drawable
     */
    public void setImageDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            this.mBitmap = bd.getBitmap();

            transformRotateDraw();
        } else {
            throw new NotSupportedException("SingleTouchView not support this Drawable " + drawable);
        }
    }

    /**
     * 根据id设置旋转图
     *
     * @param resId
     */
    public void setImageResource(int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId);
        setImageDrawable(drawable);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEditAble) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
                mode = JudgeStatus(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                mCurMovePointF.set(event.getX() + mViewPaddingLeft, event.getY() + mViewPaddingTop);
                switch (mode) {
                    case MOVE:
                        //平移模式

                        // 修改中心点
                        mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
                        mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;
                        //重绘布局
                        adjustLayout();

                        break;

                    case ZOOM:
                        //缩放模式，相对左上角
                        float scale = 1f;
                        //计算缩放比例

                        //图片某个点到图片中心的距离
                        int halfBitmapWidth = mBitmap.getWidth() / 2;
                        int halfBitmapHeight = mBitmap.getHeight() /2 ;
                        float bitmapToCenterDistance = (float) Math.sqrt(halfBitmapWidth * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);

                        //移动的点到图片中心的距离
                        float moveToCenterDistance = distance4PointF(mCenterPoint, mCurMovePointF);

                        //计算缩放比例
                        scale = moveToCenterDistance / bitmapToCenterDistance;

                        transformZoomDraw();





                        break;

                    case ROTATE:
                        //旋转，相对中心点




                        break;

                    case DELETE:

                        break;


                }


                break;
        }
        return true;
    }


    /**
     * 根据点击的位置判断点击的位置
     *
     * @param x
     * @param y
     * @return
     */
    private int JudgeStatus(float x, float y) {
        PointF touchPoint = new PointF(x, y);
        PointF deletePointF = new PointF(mDeletePoint);
        PointF movePointF = new PointF(mMovePoint);
        PointF rotatePointF = new PointF(mRotatePoint);
        PointF zoomPointF = new PointF(mZoomPoint);


        float distanceToDelete = distance4PointF(touchPoint, deletePointF);

        if (distanceToDelete < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
            return DELETE;
        }
        float distanceToMove = distance4PointF(touchPoint, movePointF);

        if (distanceToMove < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
            return MOVE;
        }

        float distanceToRotate = distance4PointF(touchPoint, rotatePointF);

        if (distanceToRotate < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
            return ROTATE;
        }

        float distanceToZoom = distance4PointF(touchPoint, zoomPointF);

        if (distanceToZoom < Math.min(mDrawableWidth / 2, mDrawableHeight / 2)) {
            return ZOOM;
        }

        return NONE;
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
     * 获取四个点和View的大小
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param degree
     */
    private void computeRect(int left, int top, int right, int bottom, float degree) {
        Point lt = new Point(left, top);
        Point rt = new Point(right, top);
        Point rb = new Point(right, bottom);
        Point lb = new Point(left, bottom);
        Point cp = new Point((left + right) / 2, (top + bottom) / 2);
        mLTPoint = obtainRoationPoint(cp, lt, degree);
        mRTPoint = obtainRoationPoint(cp, rt, degree);
        mRBPoint = obtainRoationPoint(cp, rb, degree);
        mLBPoint = obtainRoationPoint(cp, lb, degree);

        //计算X坐标最大的值和最小的值
        int maxCoordinateX = getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);
        int minCoordinateX = getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);
        ;

        mViewWidth = maxCoordinateX - minCoordinateX;


        //计算Y坐标最大的值和最小的值
        int maxCoordinateY = getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);
        int minCoordinateY = getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);

        mViewHeight = maxCoordinateY - minCoordinateY;


        //View中心点的坐标
        Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);

        offsetX = mViewWidth / 2 - viewCenterPoint.x;
        offsetY = mViewHeight / 2 - viewCenterPoint.y;


        int halfDrawableWidth = mDrawableWidth / 2;
        int halfDrawableHeight = mDrawableHeight / 2;

        //将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
        mLTPoint.x += (offsetX + halfDrawableWidth);
        mRTPoint.x += (offsetX + halfDrawableWidth);
        mRBPoint.x += (offsetX + halfDrawableWidth);
        mLBPoint.x += (offsetX + halfDrawableWidth);

        //将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
        mLTPoint.y += (offsetY + halfDrawableHeight);
        mRTPoint.y += (offsetY + halfDrawableHeight);
        mRBPoint.y += (offsetY + halfDrawableHeight);
        mLBPoint.y += (offsetY + halfDrawableHeight);

        //  mControlPoint = LocationToPoint(controlLocation);
    }

    /**
     * 获取旋转某个角度之后的点
     *
     * @param viewCenter
     * @param source
     * @param degree
     * @return
     */
    public static Point obtainRoationPoint(Point viewCenter, Point source, float degree) {
        //两者之间的距离
        Point disPoint = new Point();
        disPoint.x = source.x - viewCenter.x;
        disPoint.y = source.y - viewCenter.y;

        //没旋转之前的弧度
        double originRadian = 0;

        //没旋转之前的角度
        double originDegree = 0;

        //旋转之后的角度
        double resultDegree = 0;

        //旋转之后的弧度
        double resultRadian = 0;

        //经过旋转之后点的坐标
        Point resultPoint = new Point();

        double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y * disPoint.y);
        if (disPoint.x == 0 && disPoint.y == 0) {
            return viewCenter;
            // 第一象限
        } else if (disPoint.x >= 0 && disPoint.y >= 0) {
            // 计算与x正方向的夹角
            originRadian = Math.asin(disPoint.y / distance);

            // 第二象限
        } else if (disPoint.x < 0 && disPoint.y >= 0) {
            // 计算与x正方向的夹角
            originRadian = Math.asin(Math.abs(disPoint.x) / distance);
            originRadian = originRadian + Math.PI / 2;

            // 第三象限
        } else if (disPoint.x < 0 && disPoint.y < 0) {
            // 计算与x正方向的夹角
            originRadian = Math.asin(Math.abs(disPoint.y) / distance);
            originRadian = originRadian + Math.PI;
        } else if (disPoint.x >= 0 && disPoint.y < 0) {
            // 计算与x正方向的夹角
            originRadian = Math.asin(disPoint.x / distance);
            originRadian = originRadian + Math.PI * 3 / 2;
        }

        // 弧度换算成角度
        originDegree = radianToDegree(originRadian);
        resultDegree = originDegree + degree;

        // 角度转弧度
        resultRadian = degreeToRadian(resultDegree);

        resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
        resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
        resultPoint.x += viewCenter.x;
        resultPoint.y += viewCenter.y;

        return resultPoint;
    }

    /**
     * 弧度换算成角度
     *
     * @return
     */
    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }


    /**
     * 角度换算成弧度
     *
     * @param degree
     * @return
     */
    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /**
     * 获取变长参数最大的值
     *
     * @param array
     * @return
     */
    public int getMaxValue(Integer... array) {
        List<Integer> list = Arrays.asList(array);
        Collections.sort(list);
        return list.get(list.size() - 1);
    }


    /**
     * 获取变长参数最小的值
     *
     * @param array
     * @return
     */
    public int getMinValue(Integer... array) {
        List<Integer> list = Arrays.asList(array);
        Collections.sort(list);
        return list.get(0);
    }

    /**
     * 设置图标
     *
     * @param zoomMarkBM
     */
    public void setZoomMarkBM(Bitmap zoomMarkBM) {
        this.zoomMarkBM = zoomMarkBM;
    }

    public void setDeleteMarkBM(Bitmap deleteMarkBM) {
        this.deleteMarkBM = deleteMarkBM;
    }

    public void setRotateMarkBM(Bitmap rotateMarkBM) {
        this.rotateMarkBM = rotateMarkBM;
    }

    public void setMoveMarkBM(Bitmap moveMarkBM) {
        this.moveMarkBM = moveMarkBM;
    }


    public boolean isEditAble() {
        return isEditAble;
    }

    public void setEditAble(boolean editAble) {
        isEditAble = editAble;
    }


    public static class NotSupportedException extends RuntimeException {
        private static final long serialVersionUID = 1674773263868453754L;

        public NotSupportedException() {
            super();
        }

        public NotSupportedException(String detailMessage) {
            super(detailMessage);
        }

    }

}
