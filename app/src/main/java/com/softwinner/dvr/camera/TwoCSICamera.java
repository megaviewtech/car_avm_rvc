package com.softwinner.dvr.camera;

import android.hardware.Camera;

import com.softwinner.dvr.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class TwoCSICamera extends BaseCamera {
    private static final String TAG = "FourCSICamera";

    @Override
    public boolean openCamera() {
        mCamera = Camera.open(mIndex);
        if (mCamera == null) {
            Logger.e(TAG,"----openCamera----failed");
            return false;
        }
        return true;
    }

    @Override
    public void initCameraRec(String filename, int format, int width, int height, int framerate,
                              int bitrate, int audiomode, int audiobitrate) {
        width = 1280 * 2;
        height = 720;

        awCamRecInit(filename, format, width, height, framerate, bitrate, audiomode, audiobitrate);
    }

    @Override
    public List<Camera.Size> getSupportVideoSize() {
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        size.width = 1280 * 2;
        size.height = 720;
        List<Camera.Size> sizes = new ArrayList<>();
        sizes.add(size);
        return sizes;
    }

    @Override
    boolean checkDevice(int index) {
        String cameraPath = "/dev/video" + index;
        if (new File(cameraPath).exists()) {
            return true;
        }
        return false;
    }

    @Override
    void setPreviewSize() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(2560, 720);
        mCamera.setParameters(parameters);
    }

    @Override
    void setPictureQuality() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(2560, 720);
        mCamera.setParameters(parameters);
    }

    @Override
    public int getBitRate() {
        return 6000000;
    }
}
