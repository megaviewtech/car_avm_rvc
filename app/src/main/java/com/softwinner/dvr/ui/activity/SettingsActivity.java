package com.softwinner.dvr.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.softwinner.dvr.R;
import com.softwinner.dvr.ui.adapter.SettingsFragmentPagerAdapter;
import com.softwinner.dvr.ui.fragment.BackCameraSettingsFragment;
import com.softwinner.dvr.ui.fragment.FrontCameraSettingsFragment;
import com.softwinner.dvr.ui.fragment.LeftCameraSettingsFragment;
import com.softwinner.dvr.ui.fragment.NormalSettingsFragment;
import com.softwinner.dvr.ui.fragment.RightCameraSettingsFragment;
import com.softwinner.dvr.widget.BionIndicator;
import com.softwinner.dvr.widget.VerticalViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    BionIndicator mSettingsIndicator;
    VerticalViewPager mSettingsViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        mSettingsIndicator = (BionIndicator) findViewById(R.id.settingsIndicator);
        mSettingsViewPager = (VerticalViewPager) findViewById(R.id.settingsViewPager);
        List<String> mTabTitles = new ArrayList<>();
        List<Fragment> mFragments = new ArrayList<>();
//        switch (DVRApplication.getInstance().getDVR().getCameraCount()) {
        switch (0) {
            case 1:
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array.settings_one_tab));
                mFragments.add(NormalSettingsFragment.newInstance());
                mFragments.add(FrontCameraSettingsFragment.newInstance());
                break;
            case 2:
                mFragments.add(NormalSettingsFragment.newInstance());
                mFragments.add(FrontCameraSettingsFragment.newInstance());
                mFragments.add(BackCameraSettingsFragment.newInstance());
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array.settings_two_tab));
                break;
            case 4:
                mFragments.add(NormalSettingsFragment.newInstance());
                mFragments.add(FrontCameraSettingsFragment.newInstance());
                mFragments.add(BackCameraSettingsFragment.newInstance());
                mFragments.add(LeftCameraSettingsFragment.newInstance());
                mFragments.add(RightCameraSettingsFragment.newInstance());
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array
                        .settings_four_tab));
                break;
            default:
                mTabTitles = Arrays.asList(getResources().getStringArray(R.array
                        .settings_common_tab));
                mFragments.add(NormalSettingsFragment.newInstance());
                break;
        }


        mSettingsViewPager.setAdapter(new SettingsFragmentPagerAdapter(getSupportFragmentManager
                (), mFragments));
        mSettingsIndicator.setTabTitles(mTabTitles);
        mSettingsIndicator.setViewPager(mSettingsViewPager, 0);
    }
}
