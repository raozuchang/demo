package com.example.rzc.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rzc.onclickdemo.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 93502 on 2017/7/12.
 */

public class RadioCtrlView extends LinearLayout {
    private Context mContext;

    public TextView mViewTime;
    ImageView mIvTop;
    EditText mTvName;
    TextView mTvStart;
    TextView mTvFinish;
    TextView mTvPlay;
    TextView mTvPause;
    public ProgressBar mPbPlay;
    List<TextView> textViews = new ArrayList<>();


    public RadioCtrlView(Context context) {
        this(context, null);
    }

    public RadioCtrlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioCtrlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.record_view, this, true);
        mViewTime = (TextView) findViewById(R.id.view_time);
        mIvTop = (ImageView) findViewById(R.id.iv_top);
        mTvName = (EditText) findViewById(R.id.tv_name);
        mTvStart = (TextView) findViewById(R.id.tv_start);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mTvPlay = (TextView) findViewById(R.id.tv_play);
        mTvPause = (TextView) findViewById(R.id.tv_pause);
        mPbPlay = (ProgressBar) findViewById(R.id.pb_play);
        textViews.add(mTvFinish);
        textViews.add(mTvPause);
        textViews.add(mTvPlay);
        textViews.add(mTvStart);
    }

    public void setTopImage(int resId){
        Picasso.with(mContext).load(resId).transform(new CircleImageTransformation()).into(mIvTop);
        invalidate();
    }
    public void setTopImage(File file){
        Picasso.with(mContext).load(file).transform(new CircleImageTransformation()).into(mIvTop);
        invalidate();
    }
    public void setTopImage(Uri uri){
        Picasso.with(mContext).load(uri).transform(new CircleImageTransformation()).into(mIvTop);
        invalidate();
    }
    public void setTopImage(String path){
        Picasso.with(mContext).load(path).transform(new CircleImageTransformation()).into(mIvTop);
        invalidate();
    }


    public void setTimeText(String text){
        mViewTime.setText(text);
        invalidate();
    }

    public void setTvName(String text) {
        mTvName.setText(text);
        invalidate();
    }

    public String getTvName(){
        return mTvName.getText().toString().trim();
    }


    public void startRecord(OnClickListener listener){
        if (listener!=null){
            mTvStart.setOnClickListener(listener);
        }
    }

    public void finishRecord(OnClickListener listener){
        if (listener!=null){
            mTvFinish.setOnClickListener(listener);
        }
    }
    public void playRecord(OnClickListener listener){
        if (listener!=null){
            mTvPlay.setOnClickListener(listener);
        }
    }
    public void pauseRecord(OnClickListener listener){
        if (listener!=null){
            mTvPause.setOnClickListener(listener);
        }
    }

    public void visitableWithout(TextView view){
        for (TextView textView : textViews){
            if (view!=textView){
                textView.setVisibility(GONE);
            }else {
                textView.setVisibility(VISIBLE);
            }
        }

    }

    public TextView getmTvFinish() {
        return mTvFinish;
    }

    public EditText getmTvName() {
        return mTvName;
    }

    public TextView getmTvPause() {
        return mTvPause;
    }

    public TextView getmTvStart() {
        return mTvStart;
    }

    public TextView getmTvPlay() {
        return mTvPlay;
    }
}
