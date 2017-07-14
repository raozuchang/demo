package com.example.rzc.onclickdemo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.example.rzc.view.CustomEditText;
import com.example.rzc.view.CustomImageView;
import com.example.rzc.view.CustomTextView;
import com.example.rzc.view.HandPointView;
import com.example.rzc.view.MyScrollView;
import com.example.rzc.view.RadioCtrlView;
import com.example.rzc.view.RecordManager;

import com.yanzhenjie.album.Album;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.MessageQueue.OnFileDescriptorEventListener.EVENT_ERROR;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,RecognitionListener {

    private static final String TAG = "main";
    protected static final int RESULT_SPEECH = 6666;//防止和系统默认冲突
    private static final int REQUEST_UI = 1;
    public static final int INT = 71;
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
    private MyScrollView scrollView;
    HandPointView drawView;

    private List<View> viewList = new ArrayList<>();
    private CustomEditText mEtNote;
    private LinearLayout mLlTitle;
    ViewGroup.LayoutParams params;
    private Timer mTimer;
    private TimerTask timerTask;
    MediaPlayer mediaPlayer=null;
    private TextView mTvSpeech;


    private Toast mToast;

    private SpeechRecognizer speechRecognizer;
    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private int status = STATUS_None;
    private long speechEndTime = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mRlParent = (RelativeLayout) findViewById(R.id.rl_parent);
        mRlParent.setOnClickListener(this);
        mFlParent = (FrameLayout) findViewById(R.id.fl_parent);
        mFlParent.setOnClickListener(this);
        mTvSpeech = (TextView) findViewById(R.id.speech);
        mTvSpeech.setOnClickListener(this);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setOnClickListener(this);
        mMenuMain = (ImageView) findViewById(R.id.menu_main);
        mMenuMain.setOnClickListener(this);
        mLinePop = findViewById(R.id.line_pop);
        mLinePop = (View) findViewById(R.id.line_pop);
        mTvFinishDraw = (TextView) findViewById(R.id.tv_finish_point);
        mTvFinishDraw.setOnClickListener(this);
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        scrollView = (MyScrollView) findViewById(R.id.sv_parent);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
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

        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        int titleHeight = mLlTitle.getHeight();

        params = new ViewGroup.LayoutParams(width, 2*(height-statusBarHeight-titleHeight));
        mEtNote = new CustomEditText(this);

        //mEtNote.setLineSpacing(0,1.1f);
        mEtNote.setTextSize(16f);
        mEtNote.setMyOnFullInput(new CustomEditText.OnFullInput() {
            @Override
            public void doNextPage() {
                Toast.makeText(MainActivity.this,"已到最底端",Toast.LENGTH_SHORT).show();
            }
        });
        mEtNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (View view:viewList){
                    removeEditState(view);
                    scrollView.setCanScroll(true);
                }
            }
        });
        mRlParent.addView(mEtNote, params);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_parent:
                Log.e(TAG, "onClick: are you ok");

                break;
            case R.id.tv_title:

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

                for (View view:viewList){
                    if (view!=drawView){
                        view.bringToFront();
                    }
                }
                mEtNote.bringToFront();
                mTvFinishDraw.setVisibility(View.GONE);
                mMenuMain.setVisibility(View.VISIBLE);
                scrollView.setCanScroll(true);
                break;
            case R.id.speech:
                startSpeech((TextView) v);
                break;
        }
    }

    private void startSpeech(TextView btn) {

       /* Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try{
            startActivityForResult(intent,RESULT_SPEECH);
        } catch (ActivityNotFoundException a){
            a.printStackTrace();
        }*/

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean api = sp.getBoolean("api", false);
        if (api) {
            switch (status) {
                case STATUS_None:
                    start();
                    btn.setText("取消");
                    status = STATUS_WaitingReady;
                    break;
                case STATUS_WaitingReady:
                    cancel();
                    status = STATUS_None;
                    btn.setText("开始");
                    break;
                case STATUS_Ready:
                    cancel();
                    status = STATUS_None;
                    btn.setText("开始");
                    break;
                case STATUS_Speaking:
                    stop();
                    status = STATUS_Recognition;
                    btn.setText("识别中");
                    break;
                case STATUS_Recognition:
                    cancel();
                    status = STATUS_None;
                    btn.setText("开始");
                    break;
            }
        } else {
            start();
        }
    }

    private void stop() {
        speechRecognizer.stopListening();
        showTip("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        showTip("点击了“取消”");
    }

    private void start() {

        showTip("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {

            String args = sp.getString("args", "");
            if (null != args) {
                showTip("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);
        if (api) {
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            startActivityForResult(intent, REQUEST_UI);
        }

        mEtNote.setText("");
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
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
                addRecord();
                Toast.makeText(MainActivity.this, "添加录音", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mLlPopHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "添加手绘", Toast.LENGTH_SHORT).show();
                addDraw(scrollView.getScrollX());
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

    private void addRecord() {
        final RadioCtrlView view =new RadioCtrlView(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String pathFile = Environment.getExternalStorageDirectory().getPath();
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        String time=format.format(date);
        pathFile = pathFile+"/"+time+".mp3";


        final RecordManager manage = new RecordManager(MainActivity.this,
                null,
                pathFile
        );
        //view.setTvName("录音1");
        view.setTimeText("00:00:00");
        view.setTopImage(R.mipmap.tu);
        view.startRecord(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manage.start_mp3();
                view.visitableWithout(view.getmTvFinish());
                mTimer = new Timer();
                timerTask = new TimerTask() {
                    int cnt = 0;
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.setTimeText(getStringTime(cnt++));

                            }
                        });
                    }
                };
                mTimer.schedule(timerTask,0,1000);
            }
        });


        view.finishRecord(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.visitableWithout(view.getmTvPlay());

                if (!timerTask.cancel()){
                    timerTask.cancel();
                    mTimer.cancel();
                }

                MyThread myThread = new MyThread(manage);
                myThread.run();


            }
        });
        final String finalPathFile = pathFile;
        view.playRecord(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try{
                    view.mViewTime.setVisibility(View.GONE);
                    view.mPbPlay.setVisibility(View.VISIBLE);
                    view.mPbPlay.setIndeterminate(false);

                    mediaPlayer.setDataSource(finalPathFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mTimer = new Timer();
                    timerTask = new TimerTask() {
                        int cnt = 0;
                        int sum = mediaPlayer.getDuration();
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cnt =  mediaPlayer.getCurrentPosition();

                                    view.mPbPlay.setProgress(100*cnt/sum);
                                }
                            });
                        }
                    };
                    mTimer.schedule(timerTask,0,1000);

                }catch(IOException e){
                    Log.e(TAG,"播放失败");
                }
                view.visitableWithout(view.getmTvPause());
            }
        });
        view.pauseRecord(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null){
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mTimer.cancel();
                }else {
                    Toast.makeText(MainActivity.this,"已播放结束",Toast.LENGTH_SHORT).show();
                }
                if (!timerTask.cancel()){
                    timerTask.cancel();
                    mTimer.cancel();
                }
            }
        });


        mRlParent.addView(view,param);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String text = (String) msg.obj;
                    Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    String text2 = (String) msg.obj;
                    Toast.makeText(MainActivity.this,text2,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        showTip("检测到用户的已经停止说话");
        mTvSpeech.setText("识别中");
    }

    @Override
    public void onError(int error) {
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        showTip("识别失败：" + sb.toString());
        mTvSpeech.setText("开始");
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        showTip("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            showTip("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            showTip("origin_result=[warning: bad json]\n" + json_res);
        }
        mTvSpeech.setText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        mEtNote.setText(nbest.get(0) + strEnd2Finish);
       // time = 0;
    }
   // long time;

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            showTip("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            mEtNote.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                showTip("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                showTip("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    class MyThread implements Runnable{
        RecordManager manage;

        public MyThread(RecordManager manage){
            this.manage=manage;
        }

        @Override
        public void run() {
            if (manage.stop_mp3()){
                Message msg = new Message();
                msg.what=0;
                msg.obj = "转码成功";
                handler.sendMessage(msg);
            }else {
                Message msg = new Message();
                msg.what=1;
                msg.obj = "转码失败";
                handler.sendMessage(msg);
            }
        }
    }

    private String getStringTime(int i) {
        int hour = i/3600;
        int min = i % 3600 / 60;
        int second = i % 60;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }

   /* private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };*/

    private void addDraw(int scrollX) {
        if (drawView==null){
            drawView = new HandPointView(this);
            mRlParent.addView(drawView, params);
        }else {
            drawView.bringToFront();
        }
        drawView.setStartX(scrollX);

       /* drawView.setFocusable(true);
        //拦截scrollview抢焦点
        drawView.setFocusableInTouchMode(true);
        mRlParent.setFocusableInTouchMode(true);

        drawView.requestFocus();
        drawView.requestFocusFromTouch();*/


        mMenuMain.setVisibility(View.GONE);
        mPopupWindow.dismiss();
        scrollView.setCanScroll(false);
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
       /* MyEditText editText = new MyEditText(this);

        editText.setInEdit(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(800, 60);

        mRlParent.addView(editText,layoutParams);
        viewList.add(editText);*/

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
                        scrollView.setCanScroll(false);
                    }
                });
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mRlParent.addView(imageView, lp);
                viewList.add(imageView);

            } else if (resultCode == RESULT_CANCELED) { // User canceled.
                // 用户取消了操作。
            }/*else if (requestCode==RESULT_SPEECH&& null != data){
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                mEtNote.setText(text.get(0));


            }*/

        }
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
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





}
