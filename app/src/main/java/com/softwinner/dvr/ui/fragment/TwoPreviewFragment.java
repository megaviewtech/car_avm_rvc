package com.softwinner.dvr.ui.fragment;


import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.Constant;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.Logger;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TwoPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwoPreviewFragment extends Fragment {
    private static final String TAG = "OnePreviewFragment";

    public TwoPreviewFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnePreviewFragment.
     */
    public static TwoPreviewFragment newInstance() {
        TwoPreviewFragment fragment = new TwoPreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_preview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        SurfaceView surfaceView2 = (SurfaceView) view.findViewById(R.id.surfaceView2);
        surfaceView.setVisibility(View.VISIBLE);
        surfaceView2.setVisibility(View.VISIBLE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    DVRApplication.getInstance().getDVR().frontCameraStartPreview(holder);
                } catch (IOException e) {
                    Log.e(TAG, "-----startPreview ERROR");
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.preview_failed, Toast.LENGTH_SHORT)
                            .show();
                } catch (Exception e1) {
                    Toast.makeText(getContext(), R.string.preview_failed2, Toast.LENGTH_SHORT)
                            .show();
                    DVRApplication.getInstance().exit();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DVRApplication.getInstance().getDVR().stopPreview();
            }
        });

        surfaceView2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    DVRApplication.getInstance().getDVR().reverseCameraStartPreview(holder);
                    if (DVRPreference.getInstance(getContext()).haveWaterMark()) {
                        DVRApplication.getInstance().getDVR().startWaterMark();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "-----startPreview ERROR");
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.preview_failed, Toast.LENGTH_SHORT)
                            .show();
                } catch (Exception e1) {
                    Toast.makeText(getContext(), R.string.preview_failed2, Toast.LENGTH_SHORT)
                            .show();
                    DVRApplication.getInstance().exit();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                DVRApplication.getInstance().getDVR().stopPreview();
            }
        });
    }
}
