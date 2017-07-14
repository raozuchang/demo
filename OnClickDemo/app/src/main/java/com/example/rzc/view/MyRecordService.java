package com.example.rzc.view;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by 93502 on 2017/7/13.
 */

public class MyRecordService extends Service {
    private MediaPlayer mediaPlayer;

    public MyRecordService(MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer;
    }

    public class MyBind extends Binder{

        //获得录音长度
        public int getSoundsDuration(){
            int rtn = 0;
            if (mediaPlayer!=null){
                rtn = mediaPlayer.getDuration();
            }
            return rtn;
        }

        //获取当前播放进度
        public int getMusicCurrentPosition(){
            int rtn = 0;
            if (mediaPlayer!=null){
             rtn = mediaPlayer.getCurrentPosition();
            }
            return rtn;
        }
        //是否完成播放
        public boolean isFinish(){
            boolean flag = false;
            if (mediaPlayer!=null){
                if (mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
                    flag=true;
                }

            }
            return flag;
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }
}
