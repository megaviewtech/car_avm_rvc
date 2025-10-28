package com.softwinner.dvr.bean;

import android.graphics.Bitmap;



public class VideoInfo {
    public int hashcode;
    public String name;
    public int duration;
    public String path;
    public long size;
//    public Bitmap bitmap;

    public VideoInfo(int hashcode, String name, String path, long size, int duration) {
        this.hashcode = hashcode;
        this.name = name;
        this.duration = duration;
        this.path = path;
        this.size = size;
//        this.bitmap = bitmap;
    }
}
