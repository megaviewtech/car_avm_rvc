package com.softwinner.dvr.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.ui.adapter.FileFragmentPagerAdapter;
import com.softwinner.dvr.ui.fragment.ImageFileFragment;
import com.softwinner.dvr.ui.fragment.VideoFileFragment;
import com.softwinner.dvr.widget.BionIndicator;
import com.softwinner.dvr.widget.VerticalViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileActivity extends BaseActivity {
    private static final String TAG = "FileActivity";

    BionIndicator mFileIndicator;
    VerticalViewPager mFileViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        init();
    }

    private void init() {
        mFileIndicator = (BionIndicator) findViewById(R.id.fileIndicator);
        mFileViewPager = (VerticalViewPager) findViewById(R.id.fileViewPager);
        List<String> mTabTitles = new ArrayList<>();
        List<Fragment> mFragments = new ArrayList<>();
        switch (DVRApplication.getInstance().getDVR().getCameraCount()) {
            case 1:
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_FRONT));
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array.file_one_tab));
                break;
            case 2:
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_FRONT));
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_BACK));
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array.file_two_tab));
                break;
            case 4:
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_FRONT));
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_BACK));
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_LEFT));
                mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_RIGHT));
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array.file_four_tab));
                break;
        }
        mFragments.add(VideoFileFragment.newInstance(VideoFileFragment.TYPE_LOCK));
        mFragments.add(ImageFileFragment.newInstance());
        mFileViewPager.setAdapter(new FileFragmentPagerAdapter(getSupportFragmentManager(),
                mFragments));
        mFileIndicator.setTabTitles(mTabTitles);
        mFileIndicator.setViewPager(mFileViewPager, 0);
    }
}
