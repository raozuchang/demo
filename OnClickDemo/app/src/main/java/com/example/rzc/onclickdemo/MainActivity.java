package com.example.rzc.onclickdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.agilie.RotatableAutofitEditText;
import com.example.rzc.view.CustomEditText;
import com.example.rzc.view.CustomImageView;
import com.example.rzc.view.CustomTextView;
import com.example.rzc.view.HandPointView;
import com.example.rzc.view.SketchView;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.yanzhenjie.album.Album;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "main";
    private RelativeLayout mRlParent;
    private FrameLayout mFlParent;
    private PopupWindow mPopupWindow;
    float xPoint, yPoint;
    /**
     * title
     */
    private TextView mTvTitle;
    private ImageView mMenuMain;
    private View mLinePop;
    private TextView mTvFinishDraw;
    private ScrollView scrollView;

    private List<View> viewList = new ArrayList<>();
    private CustomEditText mEtNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mRlParent = (RelativeLayout) findViewById(R.id.rl_parent);
        mRlParent.setOnClickListener(this);
        mFlParent = (FrameLayout) findViewById(R.id.fl_parent);
        mFlParent.setOnClickListener(this);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setOnClickListener(this);
        mMenuMain = (ImageView) findViewById(R.id.menu_main);
        mMenuMain.setOnClickListener(this);
        mLinePop = findViewById(R.id.line_pop);
        mLinePop = (View) findViewById(R.id.line_pop);
        mTvFinishDraw = (TextView) findViewById(R.id.tv_finish_point);
        mTvFinishDraw.setOnClickListener(this);
        /*scrollView = (ScrollView) findViewById(R.id.sv_parent);

        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
*/

        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,2*height);
        mEtNote = new CustomEditText(this);
        mEtNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (canVerticalScroll(mEtNote)){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if (event.getAction()==MotionEvent.ACTION_UP){
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });
        mRlParent.addView(mEtNote,params);
    }

    private void showPop(final float x, final float y) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_menu, null);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        ViewHolder holder = new ViewHolder(view);


        holder.mLlAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "addPhoto" + x + "," + y, Toast.LENGTH_SHORT).show();
                addPhoto(x, y);
            }
        });
        holder.mLlAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "addText" + x + "," + y, Toast.LENGTH_SHORT).show();
                addText(x, y);
            }
        });
        holder.mLlAddVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "addVoice" + x + "," + y, Toast.LENGTH_SHORT).show();
                addVoice(x, y);
            }
        });
        Log.w(TAG, "showPop: " + 111);
        mPopupWindow.showAtLocation(mRlParent, Gravity.NO_GRAVITY, (int) x, (int) y);
        //mPopupWindow.showAsDropDown(mTvTitle);
    }

    private void addVoice(float x, float y) {

    }

    private void addText(float x, float y) {
        /*RelativeLayout relativeLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 90);
        relativeLayout.setLayoutParams(params);
        relativeLayout.setX(x);
        relativeLayout.setY(y);
        relativeLayout.setBackground(getResources().getDrawable(R.drawable.bg_layout));
        mRlParent.addView(relativeLayout);


        EditText ed = new EditText(this);*/
        RotatableAutofitEditText rotatableAutofitEditText = new RotatableAutofitEditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        rotatableAutofitEditText.setBackground(getResources().getDrawable(R.drawable.bg_layout));
        rotatableAutofitEditText.setLayoutParams(params);
        rotatableAutofitEditText.setX(x);
        rotatableAutofitEditText.setY(y);

        mRlParent.addView(rotatableAutofitEditText);


    }

    private void addPhoto(float x, float y) {
        RotateLayout layout = new RotateLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        layout.setLayoutParams(params);
        EditText childView = new EditText(this);
        childView.setPadding(5, 5, 5, 5);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        childView.setLayoutParams(childParams);
        layout.addView(childView);
        layout.setX(x);
        layout.setY(y);


        mRlParent.addView(layout);
    }


    static class ViewHolder {
        View view;
        LinearLayout mLlAddText;
        LinearLayout mLlAddPhoto;
        LinearLayout mLlAddVoice;

        ViewHolder(View view) {
            this.view = view;
            this.mLlAddText = (LinearLayout) view.findViewById(R.id.ll_add_text);
            this.mLlAddPhoto = (LinearLayout) view.findViewById(R.id.ll_add_photo);
            this.mLlAddVoice = (LinearLayout) view.findViewById(R.id.ll_add_voice);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_parent:
                Log.e(TAG, "onClick: are you ok");

                break;
            case R.id.tv_title:
                showPop(500f, 500f);
                break;
            case R.id.menu_main:
                showMenuPop();
                break;
            case R.id.fl_parent:
                if (!viewList.isEmpty()) {
                    for (int i = 0; i < viewList.size(); i++) {
                        removeEditState(viewList.get(i));
                    }
                }
                break;
            case R.id.tv_finish_point:
                mEtNote.bringToFront();
                mTvFinishDraw.setVisibility(View.GONE);
                mMenuMain.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showMenuPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_window, null);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        PopViewHolder holder = new PopViewHolder(view);
        holder.mLlPopAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "添加录音", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mLlPopHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "添加手绘", Toast.LENGTH_SHORT).show();
                addDraw();
            }
        });
        holder.mLlPopPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPic();
                Toast.makeText(MainActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mLlPopText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText();
                Toast.makeText(MainActivity.this, "添加文本", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mLlPopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "添加视屏", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mLlPopWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "总览", Toast.LENGTH_SHORT).show();

            }
        });


        Log.w(TAG, "showPop: " + 111);
        //mPopupWindow.showAtLocation(mRlParent);
        mPopupWindow.showAsDropDown(mLinePop);
    }

    private void addDraw() {
        HandPointView drawView = new HandPointView(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mFlParent.addView(drawView, param);
        mMenuMain.setVisibility(View.GONE);
        mTvFinishDraw.setVisibility(View.VISIBLE);
    }

    private void addText() {
        final CustomTextView textView = new CustomTextView(this);
        textView.setImageResource(R.mipmap.background);
        textView.setInEdit(true);
        textView.setOperationListener(new CustomTextView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mRlParent.removeView(textView);
            }

            @Override
            public void reset() {
                textView.bringToFront();
            }

            @Override
            public void onDoubleCLick(CustomTextView view) {
                showInputPop(view);
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRlParent.addView(textView, params);
        viewList.add(textView);
    }

    private void showInputPop(CustomTextView view) {


    }

    private void addPic() {
        /*final CustomImageView imageView = new CustomImageView(this);
        imageView.setInEdit(true);
        imageView.setImageResource(R.mipmap.tu);
        imageView.setOperationListener(new CustomImageView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mRlParent.removeView(imageView);
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRlParent.addView(imageView,lp);*/
        Album.albumRadio(this)
                //.toolBarColor(toolbarColor) // Toolbar 颜色，默认蓝色。
                //.statusBarColor(statusBarColor) // StatusBar 颜色，默认蓝色。
                //.navigationBarColor(navigationBarColor) // NavigationBar 颜色，默认黑色，建议使用默认。
                .title("图库") // 配置title。

                .columnCount(3) // 相册展示列数，默认是2列。
                .camera(true) // 是否有拍照功能。
                .start(999); // 999是请求码，返回时onActivityResult()的第一个参数。
        mPopupWindow.dismiss();
    }


    static class PopViewHolder {
        View view;
        LinearLayout mLlPopAudio;
        LinearLayout mLlPopPic;
        LinearLayout mLlPopText;
        LinearLayout mLlPopHand;
        LinearLayout mLlPopWatch;
        LinearLayout mLlPopVideo;

        PopViewHolder(View view) {
            this.view = view;
            this.mLlPopAudio = (LinearLayout) view.findViewById(R.id.ll_pop_audio);
            this.mLlPopPic = (LinearLayout) view.findViewById(R.id.ll_pop_pic);
            this.mLlPopText = (LinearLayout) view.findViewById(R.id.ll_pop_text);
            this.mLlPopHand = (LinearLayout) view.findViewById(R.id.ll_pop_hand);
            this.mLlPopWatch = (LinearLayout) view.findViewById(R.id.ll_pop_watch);
            this.mLlPopVideo = (LinearLayout) view.findViewById(R.id.ll_pop_video);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999) {
            if (resultCode == RESULT_OK) { // Successfully.
                ArrayList<String> pathList = Album.parseResult(data);
                final CustomImageView imageView = new CustomImageView(this);
                imageView.setInEdit(true);
                Bitmap bitmap = BitmapFactory.decodeFile(pathList.get(0));
                imageView.setBitmap(bitmap);
                imageView.setOperationListener(new CustomImageView.OperationListener() {
                    @Override
                    public void onDeleteClick() {
                        mRlParent.removeView(imageView);
                    }

                    @Override
                    public void reset() {
                        imageView.bringToFront();
                    }
                });
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mRlParent.addView(imageView, lp);
                viewList.add(imageView);

            } else if (resultCode == RESULT_CANCELED) { // User canceled.
                // 用户取消了操作。
            }
        }
    }

    private void removeEditState(View view) {
        if (view instanceof CustomImageView) {
            ((CustomImageView) view).setInEdit(false);
        }
        if (view instanceof CustomTextView) {
            ((CustomTextView) view).setInEdit(false);
        }

    }

    /**
     * EditText竖直方向能否够滚动
     * @param editText  须要推断的EditText
     * @return  true：能够滚动   false：不能够滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

}
