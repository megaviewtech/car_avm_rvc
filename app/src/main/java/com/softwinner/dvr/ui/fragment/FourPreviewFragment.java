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
 * Use the {@link FourPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FourPreviewFragment extends Fragment {
    private static final String TAG = "OnePreviewFragment";

    public FourPreviewFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnePreviewFragment.
     */
    public static FourPreviewFragment newInstance() {
        FourPreviewFragment fragment = new FourPreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        Logger.i(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_four_preview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        SurfaceView surfaceView2 = (SurfaceView) view.findViewById(R.id.surfaceView2);
        SurfaceView surfaceView3 = (SurfaceView) view.findViewById(R.id.surfaceView3);
        SurfaceView surfaceView4 = (SurfaceView) view.findViewById(R.id.surfaceView4);

        surfaceView.setVisibility(View.VISIBLE);
        surfaceView2.setVisibility(View.VISIBLE);
        surfaceView3.setVisibility(View.VISIBLE);
        surfaceView4.setVisibility(View.VISIBLE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logger.i(TAG,"surfaceCreated");
                try {
                    Logger.i(TAG,"frontCameraStartPreview   before");
                    DVRApplication.getInstance().getDVR().frontCameraStartPreview(holder);
                    Logger.i(TAG,"frontCameraStartPreview   after");
                } catch (IOException e) {
                    Log.e(TAG, "-----startPreview ERROR");
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
        surfaceView3.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    DVRApplication.getInstance().getDVR().leftCameraStartPreview(holder);
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

        surfaceView4.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    DVRApplication.getInstance().getDVR().rightCameraStartPreview(holder);
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
