package com.softwinner.dvr.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.activity.ChoiceCameraActivity;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;

import java.util.Arrays;


public class NormalSettingsFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "NormalSettingsFragment";

    Switch mAutoRunSwitch;
    Switch mEmergencyRunSwitch;
    Switch mWaterMakerSwitch;
    Switch mMuteSwitch;
    Switch mBackstageRecordSwitch;
    View mStoragePathView;
    TextView mStoragePathTV;
    View mDurationView;
    TextView mDurationTV;
    RelativeLayout mDVRLayout;
    TextView mDVRTypeTV;

    TextView mPreTextView;
    ImageView mPreImageView;
    Switch mPreSwitch;

    public NormalSettingsFragment() {
    }

    public static NormalSettingsFragment newInstance() {
        NormalSettingsFragment fragment = new NormalSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mAutoRunSwitch = (Switch) view.findViewById(R.id.autoRun);
        mEmergencyRunSwitch = (Switch) view.findViewById(R.id.emergencyRun);

        mMuteSwitch = (Switch) view.findViewById(R.id.mute_S);
        mBackstageRecordSwitch = (Switch) view.findViewById(R.id.background_record_S);
        mWaterMakerSwitch = (Switch) view.findViewById(R.id.waterMaker_S);
        mStoragePathView = view.findViewById(R.id.storagePathRL);
        mStoragePathTV = (TextView) view.findViewById(R.id.storagePathTV);
        mDurationTV = (TextView) view.findViewById(R.id.durationTV);
        mDurationView = view.findViewById(R.id.durationRL);
        mDVRTypeTV = (TextView) view.findViewById(R.id.typeTV);
        mDVRLayout = (RelativeLayout) view.findViewById(R.id.dvrTypeRL);
        mPreImageView = (ImageView) view.findViewById(R.id.show_info_iv);
        mPreTextView = (TextView) view.findViewById(R.id.pre_allocation_tv);
        mPreSwitch = (Switch) view.findViewById(R.id.pre_allocation_S);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mStoragePathTV.setText(DVRPreference.getInstance(getContext()).getRecordPath());
        mStoragePathView.setOnClickListener(this);
        mDurationView.setOnClickListener(this);
        mDurationTV.setText(DVRPreference.getInstance(getContext()).getRecordDuration() / 1000 +
                "s");
        mDVRTypeTV.setText(getResources().getStringArray(R.array.choice_type)[DVRPreference
                .getInstance(getActivity()).getDVRType()]);
        mDVRLayout.setOnClickListener(this);
        mAutoRunSwitch.setChecked(DVRPreference.getInstance(getContext()).isLR());
        mEmergencyRunSwitch.setChecked(DVRPreference.getInstance(getContext()).isEmergency());
        mWaterMakerSwitch.setChecked(DVRPreference.getInstance(getContext()).haveWaterMark());
        mBackstageRecordSwitch.setChecked(DVRPreference.getInstance(getContext())
                .isBackstageRecord());
        mMuteSwitch.setChecked(DVRPreference.getInstance(getContext()).isRecordMute());
        mPreSwitch.setChecked(DVRPreference.getInstance(getContext()).isPreAllocation());
        mAutoRunSwitch.setOnCheckedChangeListener(this);
        mEmergencyRunSwitch.setOnCheckedChangeListener(this);
        mWaterMakerSwitch.setOnCheckedChangeListener(this);
        mMuteSwitch.setOnCheckedChangeListener(this);
        mBackstageRecordSwitch.setOnCheckedChangeListener(this);
        mPreSwitch.setOnCheckedChangeListener(this);
        mPreImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dvrTypeRL:
                choiceDVRType();
                break;
            case R.id.durationRL:
                setDuration();
                break;
            case R.id.storagePathRL:
                selectStorage();
                break;
            case R.id.show_info_iv:
                if (mPreTextView.getVisibility() == View.VISIBLE) {
                    mPreTextView.setVisibility(View.GONE);
                    mPreImageView.setImageResource(R.drawable.ic_to_bottom);
                } else {
                    mPreTextView.setVisibility(View.VISIBLE);
                    mPreImageView.setImageResource(R.drawable.ic_to_top);
                }
                break;
        }
    }

    private void choiceDVRType() {
        DVRPreference.getInstance(getContext()).setFirstStart(true);
        Intent intent = new Intent(getActivity(), ChoiceCameraActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setDuration() {

    }

    private void selectStorage() {
        final String[] paths = StorageUtils.getStoragePaths(getContext());
        int checkedItem = Arrays.binarySearch(paths, DVRPreference.getInstance(getContext())
                .getRecordPath());
        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.settings_file_storage_path)).setSingleChoiceItems
                (paths, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logger.d(TAG, "-----storage path----" + paths[which]);
                DVRPreference.getInstance(getContext()).setRecordPath(paths[which]);
                mStoragePathTV.setText(paths[which]);
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.autoRun:
                //setAutoRun(isChecked);
                DVRPreference.getInstance(getContext()).setLR(isChecked);
                break;
            case R.id.emergencyRun:
                //setAutoRun(isChecked);
                DVRPreference.getInstance(getContext()).setEmergency(isChecked);
                break;
            case R.id.background_record_S:
                DVRPreference.getInstance(getContext()).setBackstageRecord(isChecked);
                break;
            case R.id.mute_S:
                DVRPreference.getInstance(getContext()).setRecordMute(isChecked);
                break;
            case R.id.waterMaker_S:
                DVRPreference.getInstance(getContext()).setWaterMark(isChecked);
                break;
            case R.id.pre_allocation_S:
                DVRPreference.getInstance(getContext()).setPreAllocation(isChecked);
                break;
        }
    }

    private void setAutoRun(boolean isAutoRun) {
        DVRPreference.getInstance(getContext()).setAutoStart(isAutoRun);
    }
}
