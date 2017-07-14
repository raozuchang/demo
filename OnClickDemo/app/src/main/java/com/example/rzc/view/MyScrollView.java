package com.example.rzc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by 93502 on 2017/7/10.
 */

public class MyScrollView extends ScrollView{
    private boolean canScroll;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (canScroll){
            return super.onTouchEvent(ev);
        }
        else {
            return false;
        }
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canScroll){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }

    public int getYScroll(){

        return 0;
    }


}
