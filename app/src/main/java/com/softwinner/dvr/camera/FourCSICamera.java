package com.softwinner.dvr.camera;

import android.hardware.Camera;
import android.util.Log;

import com.megaview.avm.AVM;
import com.softwinner.dvr.util.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class FourCSICamera extends BaseCamera implements Camera.ErrorCallback{
    private static final String TAG = "FourCSICamera";

    @Override
    public boolean openCamera() {
        Logger.d(TAG,"----lgtest,openCamera");
        mCamera = BaseCamera.open(0, 4, 1280, 720);
        Logger.d(TAG,"----lgtest,openCamera2");
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
        height = 720 * 2;
        awCamRecInit(filename, format, width, height, framerate, bitrate, audiomode, audiobitrate);
    }

    @Override
    public List<Camera.Size> getSupportVideoSize() {
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        size.width = 1280 * 2;
        size.height = 720 * 2;
        List<Camera.Size> sizes = new ArrayList<>();
        sizes.add(size);
        return sizes;
    }

    @Override
    public int getBitRate() {
        return 16 * 1000 * 1000;
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

        if(AVM.camera_1920_flag == 1)
            parameters.setPreviewSize(1920*2, 1080*2);
        else
            parameters.setPreviewSize(2560, 1440);

        //Logger.d("liugang", "-----setPreviewSize222");
        mCamera.setParameters(parameters);
    }

    @Override
    void setPictureQuality() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(2560, 1440);
        //parameters.setPictureSize(1920*2, 1080*2);
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
