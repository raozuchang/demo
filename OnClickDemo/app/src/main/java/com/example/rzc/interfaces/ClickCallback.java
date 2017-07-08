package com.example.rzc.interfaces;

/**
 * Created by 93502 on 2017/6/27.
 */

public interface ClickCallback {
    void addPic(int id);
    void addEdit(int id);
    void addVoice(int id);
    void addVideo(int id);
    void onWatch(int id);
    void onSave(int id);
}
