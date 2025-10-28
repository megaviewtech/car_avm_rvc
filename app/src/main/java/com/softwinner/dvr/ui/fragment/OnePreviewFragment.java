package com.softwinner.dvr.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.activity.RecordActivity;
import com.softwinner.dvr.util.Logger;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnePreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnePreviewFragment extends Fragment {
    private static final String TAG = "OnePreviewFragment";
    static CameraView mCameraView;
    public static String rootPath;

    public OnePreviewFragment() {
        Logger.d(TAG, "OnePreviewFragment");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnePreviewFragment.
     */
    public static OnePreviewFragment newInstance() {
        OnePreviewFragment fragment = new OnePreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Logger.d(TAG,"-------onCreateView");
        View view = inflater.inflate(R.layout.fragment_one_preview, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        Logger.d(TAG,"-------initView");
        mCameraView = (CameraView) view.findViewById(R.id.surfaceView);
        //mCameraView.setVisibility(View.VISIBLE);


    }

    @Override
    public void onDestroy() {
        Logger.d(TAG, "------onDestroy");
        super.onDestroy();
    }

/*    static public void SetView(String view) {
        mCameraView.SetView(view);
    }*/


}
