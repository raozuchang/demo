package com.example.rzc.view;

/**
 * Created by 93502 on 2017/6/26.
 */

public class ImageModel {
    private static final long serialVersionUID = 3800737478616389410L;

    //贴纸id
    private long imageId;
    //文本
    private String text;
    //x坐标
    private float xLocation;
    //y坐标
    private float yLocation;
    //角度
    private float degree;
    //缩放值
    private float scaling;
    //气泡顺序
    private int order;

    //贴纸PNG URL
    private String stickerURL;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getxLocation() {
        return xLocation;
    }

    public void setxLocation(float xLocation) {
        this.xLocation = xLocation;
    }

    public float getyLocation() {
        return yLocation;
    }

    public void setyLocation(float yLocation) {
        this.yLocation = yLocation;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public float getScaling() {
        return scaling;
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStickerURL() {
        return stickerURL;
    }

    public void setStickerURL(String stickerURL) {
        this.stickerURL = stickerURL;
    }
}
