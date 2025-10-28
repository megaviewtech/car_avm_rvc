package com.softwinner.dvr.camera;

import android.hardware.Camera;

import com.softwinner.dvr.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class FourCvbsCamera extends BaseCamera {
    private static final String TAG = "FourCvbsNCamera";
    private boolean isNTSC;

    @Override
    public boolean openCamera() {
        try {
            if (isNTSCCamera()) {
                isNTSC = true;
                Thread.sleep(1000);//release camera need time
                mCamera = BaseCamera.open(4, 4, 720, 480);
            } else {
                isNTSC = false;
                Thread.sleep(1000);//release camera need time
                mCamera = BaseCamera.open(4, 4, 720, 576);
            }

            if (mCamera == null) {
                Logger.e(TAG, "----openCamera----failed");
                return false;
            }

            return true;
        } catch (InterruptedException e) {
            Logger.e(TAG, "Camera was not released, can't open again.");
            if (mListener != null) {
                mListener.onOpenCameraFailed();
            }
            return false;
        }

    }

    @Override
    public void initCameraRec(String filename, int format, int width, int height, int framerate,
                              int bitrate, int audiomode, int audiobitrate) {
        if (isNTSC) {
            width = 720 * 2;
            height = 480 * 2;
        } else {
            width = 720 * 2;
            height = 576 * 2;
        }

        awCamRecInit(filename, format, width, height, framerate, bitrate, audiomode, audiobitrate);
    }

    @Override
    public List<Camera.Size> getSupportVideoSize() {
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        size.width = 720 * 2;
        size.height = 480 * 2;
        List<Camera.Size> sizes = new ArrayList<>();
        sizes.add(size);
        return sizes;
    }

    @Override
    public int getBitRate() {
        return 8 * 1000 * 1000;
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
        if (isNTSC) {
            parameters.setPreviewSize(1440, 480 * 2);
        } else {
            parameters.setPreviewSize(1440, 576 * 2);
        }
        mCamera.setParameters(parameters);
    }

    @Override
    void setPictureQuality() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (isNTSC) {
            parameters.setPictureSize(1440, 480 * 2);
        } else {
            parameters.setPictureSize(1440, 576 * 2);
        }
        mCamera.setParameters(parameters);
    }


    private boolean isNTSCCamera() {
        Camera camera = Camera.open(4);
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        camera.release();
        for (Camera.Size size : sizes) {
            if ((size.width == 720) && (size.height == 480)) {
                return true;
            }
        }
        sizes.clear();
        return false;
    }

}
