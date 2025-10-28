package com.softwinner.dvr.ui.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackCameraSettingsFragment extends Fragment {
    private static final String TAG = "BackCameraSettingsFragment";

    TextView mBackRecordQualityTV;
    View mBackRecordQualityView;
    TextView mBackPictureQualityTV;
    View mBackPictureQualityView;


    public BackCameraSettingsFragment() {
    }

    public static BackCameraSettingsFragment newInstance() {
        BackCameraSettingsFragment fragment = new BackCameraSettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_cameral_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mBackRecordQualityTV = (TextView) view.findViewById(R.id.backRecordQualityTV);
        mBackRecordQualityView = view.findViewById(R.id.backRecordQualityRL);
        mBackPictureQualityTV = (TextView) view.findViewById(R.id.backPictureQualityTV);
        mBackPictureQualityView = view.findViewById(R.id.backPictureQualityRL);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {

        mBackPictureQualityTV.setText(DVRPreference.getInstance(getContext()).getReverseRecordSize());

        mBackRecordQualityTV.setText(DVRPreference.getInstance(getContext()).getReverseRecordSize());

        mBackPictureQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPictureQuality();
            }
        });

        mBackRecordQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordQuality();
            }
        });
    }

    private void setPictureQuality() {

        final String[] selections = getSupportSize();
        int checkedItem = Arrays.binarySearch(selections, DVRPreference.getInstance(getContext())
                .getReverseRecordSize());

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_quality)).setSingleChoiceItems(selections,
                checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.d(TAG, "-----record_quality----" + selections[which]);
                        DVRPreference.getInstance(getContext()).setFrontRecordSize(selections[which]);
                        mBackPictureQualityTV.setText(selections[which]);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void setRecordQuality() {
        final String[] selections = getSupportSize();
        int checkedItem = Arrays.binarySearch(selections, DVRPreference.getInstance(getContext())
                .getReverseRecordSize());

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_quality)).setSingleChoiceItems(selections,
                checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.d(TAG, "-----record_quality----" + selections[which]);
                        DVRPreference.getInstance(getContext()).setFrontRecordSize(selections[which]);
                        mBackPictureQualityTV.setText(selections[which]);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private String[] getSupportSize() {
        List<Camera.Size> sizes = DVRApplication.getInstance().getDVR().getReverseCamera()
                .getSupportVideoSize();
        String[] result = null;
        if (sizes.size() > 0) {
            result = new String[sizes.size()];
            for (int i = 0; i < sizes.size(); i++) {
                result[i] = Utils.sizeToString(sizes.get(i));
            }
        }
        return result;
    }

}
