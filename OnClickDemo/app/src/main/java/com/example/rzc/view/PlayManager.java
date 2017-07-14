package com.example.rzc.view;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * 播放管理
 * Created by 93502 on 2017/7/12.
 */

public class PlayManager {
    private Context mcontext;
    MediaPlayer mp;
    public PlayManager(Context context){
        this.mcontext = context;
    }

    public void play(String song){
        mp = new MediaPlayer();
        try {
//   存储在SD卡或其他文件路径下的媒体文件
//   例如：mp.setDataSource("/sdcard/test.mp3");
//   网络上的媒体文件
//   例如：mp.setDataSource("http://www...../music/test.mp3");
            mp.setDataSource(song);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
