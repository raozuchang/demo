package com.example.rzc.bean;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by 93502 on 2017/6/22.
 */

public class PhotoRecord {

    public Bitmap bitmap;//图形
    public Matrix matrix;//图形
    public RectF photoRectSrc = new RectF();
    public float scaleMax = 3;

}