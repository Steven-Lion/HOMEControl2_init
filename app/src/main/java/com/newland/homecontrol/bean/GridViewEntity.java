package com.newland.homecontrol.bean;

/**
 * gridview实体类
 * Created by yizhong.xu on 2017/8/3.
 */

public class GridViewEntity {

    private int imgoff;//背景图片关
    private int imgon;//背景图片开
    private int imgClass;//类别
    private boolean isOffOn;//是开 或者关



    public int getImgoff() {
        return imgoff;
    }

    public void setImgoff(int imgoff) {
        this.imgoff = imgoff;
    }

    public int getImgon() {
        return imgon;
    }

    public void setImgon(int imgon) {
        this.imgon = imgon;
    }

    public boolean isOffOn() {
        return isOffOn;
    }

    public void setOffOn(boolean offOn) {
        isOffOn = offOn;
    }

    public int getImgClass() {
        return imgClass;
    }

    public void setImgClass(int imgClass) {
        this.imgClass = imgClass;
    }
}
