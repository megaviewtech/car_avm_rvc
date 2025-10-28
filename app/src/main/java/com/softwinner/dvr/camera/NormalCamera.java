package com.softwinner.dvr.camera;

import android.hardware.Camera;
import android.util.Log;

import com.softwinner.dvr.util.Logger;

import java.io.File;
import java.util.List;



public class NormalCamera extends BaseCamera  implements Camera.ErrorCallback {
    private static final String TAG = "NormalCamera";

    public NormalCamera(int index) {
        mIndex = index;
    }

    @Override
    public boolean openCamera() {
        mCamera = Camera.open(mIndex);
        if (mCamera == null) {
            Logger.e(TAG,"----openCamera----failed");
            return false;
        }

        //add by liugang,2020-7-6
        //mCamera.setErrorCallback(this);
        return true;
    }

    @Override
    public void initCameraRec(String filename, int format, int width, int height, int framerate,
                              int bitrate, int audiomode, int audiobitrate) {
        awCamRecInit(filename, format, width, height, framerate, bitrate, audiomode, audiobitrate);
    }

    @Override
    public List<Camera.Size> getSupportVideoSize() {
        return mCamera.getParameters().getSupportedVideoSizes();
    }

    @Override
    public int getBitRate() {
        return 16 * 1000 * 1000;
    }

    @Override
    boolean checkDevice(int index) {
        String cameraPath = "/dev/video" + index;
        if ((new File(cameraPath).exists()) && (index >= 0) && (index < 8)) {
            return true;
        }
        return false;
    }

    @Override
    void setPreviewSize() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Logger.i(TAG, "getSupportedPreviewSizes -- size = " + sizes.size());
        for (Camera.Size size : sizes) {
            switch (mIndex) {
                case 0:
                case 1:
                case 2:
                case 3:
                    //CSI
                    if (size.width == 1280) {
                        parameters.setPreviewSize(size.width, size.height);
                    }
//                    parameters.setPreviewSize(1920, 1080);
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                    //CVBS
                    //parameters.set("enable-yuv422", 1);//enable:1, disable:0
                    if (size.width == 720) {
                        //parameters.setPreviewSize(size.width, size.height);
                    }
                    break;
                case 8:
                    //CVBS拼接
                    break;
                case 9:
                case 10:
                case 11:
                    //if (size.width == 1280) {
                        parameters.setPreviewSize(1280, 720);
                    //}
                    //USB
                    break;
            }
        }


        mCamera.setParameters(parameters);
    }

    @Override
    void setPictureQuality() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Logger.i(TAG, "getSupportedPreviewSizes -- size = " + sizes.size());
        for (Camera.Size size : sizes) {
            switch (mIndex) {
                case 0:
                case 1:
                case 2:
                case 3:
                    if (size.width == 1280) {
                        parameters.setPictureSize(size.width, size.height);
                    }

                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                    if (size.width == 720) {
                        parameters.setPictureSize(size.width, size.height);
                    }
                    break;
                case 8:
                    //CVBS拼接
                    break;
                case 9:
                case 10:
                case 11:
                    //if (size.width == 1280) {
                        parameters.setPictureSize(1280, 720);
                    //}
                    //USB
                    break;
            }
        }


        mCamera.setParameters(parameters);
    }

    @Override
    public void onError(int error, Camera camera) {

        switch(error) {
            case  Camera.CAMERA_ERROR_EVICTED:
                Log.i(TAG, "onError: CAMERA_ERROR_EVICTED");
                break;

            case  Camera.CAMERA_ERROR_SERVER_DIED:
                Log.i(TAG, "onError: CAMERA_ERROR_SERVER_DIED");

                break;

            case  Camera.CAMERA_ERROR_UNKNOWN:
                Log.i(TAG, "onError: CAMERA_ERROR_UNKNOWN");
                break;
        }
    }
}
