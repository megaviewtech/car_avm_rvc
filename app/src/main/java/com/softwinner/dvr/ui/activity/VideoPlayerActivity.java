package com.softwinner.dvr.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.softwinner.dvr.R;


public class VideoPlayerActivity extends BaseActivity {
    private static final String TAG = "VideoPlayerActivity";

    VideoView mVideoView;

    private int mCurrPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        String path = getIntent().getExtras().getString("path");
        mVideoView = (VideoView) findViewById(R.id.videoView);
        if (path != null) {
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.setVideoPath(path);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoView.start();
        mVideoView.seekTo(mCurrPosition);
        mCurrPosition = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mCurrPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
    }
}
