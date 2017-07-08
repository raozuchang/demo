package com.example.rzc.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by 93502 on 2017/6/24.
 */

public class MyView extends View {
    private Context mContext;

    private DisplayMetrics metrics;
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






    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        obtainStyledAttributes(attrs);

    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        metrics = mContext.getResources().getDisplayMetrics();

        framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_PADDING, metrics);
        frameWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_WIDTH, metrics);



    }
}
