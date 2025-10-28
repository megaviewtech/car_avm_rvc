package com.softwinner.dvr.bean;

import android.graphics.Bitmap;


public class ImageInfo {
    public int hashcode;
    public String name;
    public String path;
//    public Bitmap bitmap;

    public ImageInfo(int hashcode, String name, String path) {
        this.hashcode = hashcode;
        this.name = name;
        this.path = path;
//        this.bitmap = bitmap;
    }
}
