package com.softwinner.dvr.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.softwinner.dvr.R;


public class ImageActivity extends BaseActivity {
    private static final String TAG = "ImageActivity";

    PhotoView mPicturePV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);
        mPicturePV = (PhotoView) findViewById(R.id.picturePV);
        String path = getIntent().getExtras().getString("path");
        if (path != null) {
            Glide.with(this).load(path).into(mPicturePV);
        }
    }
}
