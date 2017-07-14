package com.example.rzc.view;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/**
 * 控制录音播放
 * Created by 93502 on 2017/7/12.
 */

public class RecordController{

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private String FileName = null;
    private static final String LOG_TAG ="record" ;

    public void startRecord(String name){
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/"+name+".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(FileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    public void stopRecord(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }





}
