package com.example.rzc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.example.rzc.onclickdemo.R;
import com.example.rzc.utils.DensityUtils;

/**
 * 思路:绘制一张空白图片，然后添加文字
 * Created by 93502 on 2017/6/27.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG ="customText" ;
    private Context mContext;
    private String defStr ;
    private int fontColor;

    private DisplayMetrics metrics;
    private Bitmap mBitmap;
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
    private float maxScale = 3f;//设置最大缩放比例
    private float minScale = 0.1f;//设置最小缩放比例
    private Paint mPaint;

    private Matrix matrix = new Matrix();


    private final int NONE = 0;
    private final int MOVE = 1;
    private final int ZOOM = 2;
    private final int ROTATE = 3;
    private final int DELETE = 4;


    private int mode = NONE;

    private float lastRotateDegree;

    private float lastX, lastY;

    private float lastLength;

    /**
     * 文字部分
     */
    //显示的字符串
    private String mStr = "";
    private Bitmap originBitmap;

    //字号默认16sp
    private final float mDefultSize = 16;
    private float mFontSize = 16;
    //最大最小字号
    private final float mMaxFontSize = 25;
    private final float mMinFontSize = 14;

    //字离旁边的距离
    private final float mDefaultMargin = 20;
    private float mMargin = 20;


    //绘制文字的画笔
    private TextPaint mFontPaint;

    private Canvas canvasText;

    private Paint.FontMetrics fm;
    //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
    private float baseline;

    boolean isInit = true;

    private int mScreenWidth, mScreenHeight;
    private static final float BITMAP_SCALE = 0.7f;
    private PointF mid = new PointF();
    private boolean isInEdit = true;

    private double halfDiagonalLength;

    private OperationListener operationListener;

    private float originWidth = 0;


    public CustomTextView(Context context) {
        this(context,null);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        defStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.BLACK;

        init();
    }

    private void init() {
        metrics = getResources().getDisplayMetrics();
        dst_delete = new Rect();
        dst_zoom = new Rect();
        dst_rotate = new Rect();
        dst_move = new Rect();
        //初始化画笔
        mPaint = new Paint();

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mFontSize = mDefultSize;

        mFontPaint = new TextPaint();
        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, metrics));
        mFontPaint.setColor(fontColor);
        mFontPaint.setTextAlign(Paint.Align.CENTER);
        mFontPaint.setAntiAlias(true);
        fm = new Paint.FontMetrics();
        baseline = fm.descent-fm.ascent;
        isInit  =true;
        mStr = defStr;


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup mViewGroup = (ViewGroup) getParent();
        Log.e(TAG, "onMeasure: " + mViewGroup.toString());
        if (null != mViewGroup) {
            int parentWidth = mViewGroup.getWidth();
            int parentHeight = mViewGroup.getHeight();
            Log.e(TAG, "onMeasure:  H=" + parentHeight + "W= " + parentWidth);
            setMeasuredDimension(parentWidth,parentHeight);
            //mCenterPoint.set(parentWidth / 2, parentHeight / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvasText.setBitmap(mBitmap);
            canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            float left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
            float scalex = arrayOfFloat[Matrix.MSCALE_X];
            float skewy = arrayOfFloat[Matrix.MSKEW_Y];
            float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);

            float size = rScale * 0.75f * mDefultSize;
            if (size > mMaxFontSize) {
                mFontSize = mMaxFontSize;
            } else if (size < mMinFontSize) {
                mFontSize = mMinFontSize;
            } else {
                mFontSize = size;
            }
            mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, metrics));
            String[] texts = autoSplit(mStr, mFontPaint, mBitmap.getWidth() - left * 3);
            float height = (texts.length * (baseline + fm.leading) + baseline);
            float top = (mBitmap.getHeight() - height) / 2;
            //基于底线开始画的
            top += baseline;
            for (String text : texts) {
                if (TextUtils.isEmpty(text)) {
                    continue;
                }
                canvasText.drawText(text, mBitmap.getWidth() / 2, top, mFontPaint);  //坐标以控件左上角为原点
                top += baseline + fm.leading; //添加字体行间距
            }
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

    @Override
    public void setImageResource(@DrawableRes int resId) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setImageResource(int resId, TextModel model) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(BitmapFactory.decodeResource(getResources(), resId), model);
    }


    public void setBitmap(Bitmap bitmap, TextModel model) {
        mFontSize = mDefultSize;
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasText = new Canvas(mBitmap);
        setDiagonalLength();
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originWidth = w;

        mStr = model.getText();
        float scale = model.getScaling() * mScreenWidth / mBitmap.getWidth();
        if (scale > maxScale) {
            scale = maxScale;
        } else if (scale < minScale) {
            scale = minScale;
        }
        float degree = (float) Math.toDegrees(model.getDegree());
        matrix.postRotate(-degree, w >> 1, h >> 1);
        matrix.postScale(scale, scale, w >> 1, h >> 1);
        float midX = model.getxLocation() * mScreenWidth;
        float midY = model.getyLocation() * mScreenWidth;
        float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics);
        midX = midX - (w * scale) / 2 - offset;
        midY = midY - (h * scale) / 2 - offset;
        matrix.postTranslate(midX, midY);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mFontSize = mDefultSize;
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ALPHA_8, true);
        canvasText = new Canvas(mBitmap);
        setDiagonalLength();
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originWidth = w;
        float topbarHeight = DensityUtils.dip2px(getContext(), 50);
        //Y坐标为 （顶部操作栏+正方形图）/2
        matrix.postTranslate(mScreenWidth / 2 - w / 2, (mScreenWidth) / 2 - h / 2);
        invalidate();
    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    private void initBitmaps() {

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

    private long preClickTime;

    private final long doubleClickTimeLimit = 200;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        switch (action){
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


                        long currentTime = System.currentTimeMillis();
                        Log.d(TAG, (currentTime - preClickTime) + "");
                        if (currentTime - preClickTime > doubleClickTimeLimit) {
                            preClickTime = currentTime;
                            //移动事件
                            mode = MOVE;
                            lastX = event.getX(0);
                            lastY = event.getY(0);
                        } else {
                            //编辑文字模式
                            if (isInEdit && operationListener != null) {
                                operationListener.onDoubleCLick(this);
                            }
                        }

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

    private boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private boolean isInResize(MotionEvent event) {
        int left = -20 + this.dst_zoom.left;
        int top = -20 + this.dst_zoom.top;
        int right = 20 + this.dst_zoom.right;
        int bottom = 20 + this.dst_zoom.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

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
     * 在滑动过程中X,Y是不会改变的，这里减Y，减X，其实是相当于把X,Y当做原点
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
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        return Math.abs(s - ss) < 0.5;


    }


    /**
     * 是否在四条线内部
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
        arrayOfFloat2[0] = f1;//左上的左
        arrayOfFloat2[1] = f3;//右上的右
        arrayOfFloat2[2] = f7;//右下的右
        arrayOfFloat2[3] = f5;//左下的左
        //确定Y方向的范围
        arrayOfFloat3[0] = f2;//左上的上
        arrayOfFloat3[1] = f4;//右上的上
        arrayOfFloat3[2] = f8;
        arrayOfFloat3[3] = f6;
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
    }



    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   指定的宽度
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

    public interface OperationListener {
        void onDeleteClick();
        void reset();
        void onDoubleCLick(CustomTextView view);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public String getmStr() {
        return mStr;
    }

    public void setInEdit(boolean b){
        isInEdit = b;
        invalidate();
    }

}
