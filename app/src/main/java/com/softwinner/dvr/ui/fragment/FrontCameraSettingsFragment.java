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
public class FrontCameraSettingsFragment extends Fragment {
    private static final String TAG = "FrontCameraSettingsFragment";


    TextView mFrontRecordQualityTV;
    View mFrontRecordQualityView;
    TextView mFrontPictureQualityTV;
    View mFrontPictureQualityView;


    public FrontCameraSettingsFragment() {
    }

    public static FrontCameraSettingsFragment newInstance() {
        FrontCameraSettingsFragment fragment = new FrontCameraSettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front_cameral_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mFrontRecordQualityTV = (TextView) view.findViewById(R.id.frontRecordQualityTV);
        mFrontRecordQualityView = view.findViewById(R.id.frontRecordQualityRL);
        mFrontPictureQualityTV = (TextView) view.findViewById(R.id.frontPictureQualityTV);
        mFrontPictureQualityView = view.findViewById(R.id.frontPictureQualityRL);
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

        mFrontPictureQualityTV.setText(DVRPreference.getInstance(getContext()).getFrontRecordSize
                ());

        mFrontRecordQualityTV.setText(DVRPreference.getInstance(getContext()).getFrontRecordSize());


        mFrontPictureQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPictureQuality();
            }
        });

        mFrontRecordQualityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecordQuality();
            }
        });

    }

    private void setPictureQuality() {

        final String[] selections = getSupportSize();
        int checkedItem = Arrays.binarySearch(selections, DVRPreference.getInstance(getContext())
                .getFrontRecordSize());

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_quality)).setSingleChoiceItems(selections,
                checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.d(TAG, "-----record_quality----" + selections[which]);
                        DVRPreference.getInstance(getContext()).setFrontRecordSize(selections[which]);
                        mFrontPictureQualityTV.setText(selections[which]);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void setRecordQuality() {
        final String[] selections = getSupportSize();
        int checkedItem = Arrays.binarySearch(selections, DVRPreference.getInstance(getContext())
                .getFrontRecordSize());

        new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(getString(R.string.record_quality)).setSingleChoiceItems(selections,
                checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logger.d(TAG, "-----record_quality----" + selections[which]);
                DVRPreference.getInstance(getContext()).setFrontRecordSize(selections[which]);
                mFrontRecordQualityTV.setText(selections[which]);
                dialog.dismiss();
            }
        }).create().show();
    }

    private String[] getSupportSize() {
        List<Camera.Size> sizes = DVRApplication.getInstance().getDVR().getFrontCamera()
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
