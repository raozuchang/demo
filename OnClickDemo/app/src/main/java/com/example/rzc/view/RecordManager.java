package com.example.rzc.view;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.pocketdigi.utils.FLameUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 93502 on 2017/7/12.
 */


public class RecordManager {
    //录制成MP3格式..............................................
    /**
     * 构造时候需要的Activity，主要用于获取文件夹的路径
     */
    private Activity activity;

    /**
     * 文件代号
     */
    public static final int RAW = 0X00000001;
    public static final int MP3 = 0X00000002;

    /**
     * 文件路径
     */
    private String rawPath = null;
    private String mp3Path = null;

    /**
     * 采样频率
     */
    private static final int SAMPLE_RATE = 11025;

    /**
     * 录音需要的一些变量
     */
    private short[] mBuffer;
    private AudioRecord mRecorder;

    /**
     * 录音状态
     */
    private boolean isRecording = false;
    /**
     * 是否转换ok
     */
    private boolean convertOk = false;

    public RecordManager(Activity activity, String rawPath, String mp3Path) {
        this.activity = activity;
        this.rawPath = rawPath;
        this.mp3Path = mp3Path;
    }

    /**
     * 开始录音
     */
    public boolean start_mp3() {
        // 如果正在录音，则返回
        if (isRecording) {
            return isRecording;
        }
        // 初始化
        if (mRecorder == null) {
            initRecorder();
        }

        getFilePath();
        mRecorder.startRecording();
        startBufferedWrite(new File(rawPath));

        isRecording = true;
        return isRecording;
    }

    /**
     * 停止录音，并且转换文件,这很可能是个耗时操作，建议在后台中做
     */
    public boolean stop_mp3() {
        if (!isRecording) {
            return isRecording;
        }

        // 停止
        mRecorder.stop();
        isRecording = false;
        //TODO
        // 开始转换
        FLameUtils lameUtils = new FLameUtils(1, SAMPLE_RATE, 96);
        convertOk = lameUtils.raw2mp3(rawPath, mp3Path);

        return isRecording ^ convertOk;// convertOk==true,return true
    }

    /**
     * 获取文件的路径
     */
    public String getFilePath(int fileAlias) {
        if (fileAlias == RAW) {
            return rawPath;
        } else if (fileAlias == MP3) {
            return mp3Path;
        } else
            return null;
    }

    /**
     * 清理文件
     */
    public void cleanFile(int cleanFlag) {
        File f = null;
        try {
            switch (cleanFlag) {
                case MP3:
                    f = new File(mp3Path);
                    if (f.exists())
                        f.delete();
                    break;
                case RAW:
                    f = new File(rawPath);
                    if (f.exists())
                        f.delete();
                    break;
                case RAW | MP3:
                    f = new File(rawPath);
                    if (f.exists())
                        f.delete();
                    f = new File(mp3Path);
                    if (f.exists())
                        f.delete();
                    break;
            }
            f = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭,可以先调用cleanFile来清理文件
     */
    public void close() {
        if (mRecorder != null)
            mRecorder.release();
        activity = null;
    }

    /**
     * 初始化
     */
    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
    }

    /**
     * 设置路径，第一个为raw文件，第二个为mp3文件
     */
    private void getFilePath() {
        try {
            String folder = "audio_recorder_2_mp3";
            String fileName = String.valueOf(System.currentTimeMillis());
            if (rawPath == null) {
                File raw = new File(activity.getDir(folder,
                        activity.MODE_PRIVATE), fileName + ".raw");
                raw.createNewFile();
                rawPath = raw.getAbsolutePath();
                raw = null;
            }
            if (mp3Path == null) {
                File mp3 = new File(activity.getDir(folder,
                        activity.MODE_PRIVATE), fileName + ".mp3");
                mp3.createNewFile();
                mp3Path = mp3.getAbsolutePath();
                mp3 = null;
            }

            Log.d("rawPath", rawPath);
            Log.d("mp3Path", mp3Path);

            runCommand("chmod 777 " + rawPath);
            runCommand("chmod 777 " + mp3Path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行cmd命令，并等待结果
     */
    private boolean runCommand(String command) {
        boolean ret = false;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 写入到raw文件
     */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(file)));
                    while (isRecording) {
                        int readSize = mRecorder.read(mBuffer, 0,
                                mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();

                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
