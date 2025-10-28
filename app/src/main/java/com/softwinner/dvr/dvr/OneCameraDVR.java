package com.softwinner.dvr.dvr;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Camera;

import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.fragment.CameraDrawer;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.util.Utils;

import java.util.List;



public class OneCameraDVR extends BaseDVR {
    private static final String TAG = "OneCameraDVR";

    public OneCameraDVR(BaseCamera camera, Context context) {
        mContext = context;
        mFrontCamera = camera;
    }

    @Override
    public int startRecorder() {
        if (mFrontCamera == null) {
            return RECORD_STATUS_OPEN_CAMERA_FAILED;
        }
        boolean initResult = initRecorder();

        if (initResult) {
            mFrontCamera.startRecord();
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }

        return RECORD_STATUS_SUCCESS;
    }

    private boolean initRecorder() {
        int width = 720;
        int height = 480;
        if ((DVRPreference.getInstance(mContext).getFrontCameraId() >= 0) && (DVRPreference
                .getInstance(mContext).getFrontCameraId() <= 3)) {
            width = 1280;
            height = 720;
        } else if ((DVRPreference.getInstance(mContext).getFrontCameraId() == 9) ||
                (DVRPreference.getInstance(mContext).getFrontCameraId() == 10)) {
            width = 1280;
            height = 720;
        }
//        Camera.Size size = getSupportVideoSize();
//        if (size != null) {
//            width = size.width;
//            height = size.height;
//        }
//        mMinSpace = DVRPreference.getInstance(mContext).getRecordDuration() * 1024 * 100;
        int frameRate = mFrontCamera.getFrameRate();
        int bitRate = mFrontCamera.getBitRate();

        String file = getVideoFile(FRONT_CAMERA);
        mPreFrontFile = file;
        //checkStorageSpaceNew
        if (checkStorageSpaceNew(file)) {
            Logger.i(TAG, "-initRecorder---file---" + file);
            int audiomode = 1;
            if (DVRPreference.getInstance(mContext).isRecordMute()) {
                audiomode = 0;
            }
            mFrontCamera.initCameraRec(file, DVRPreference.getInstance(mContext).getRecordFormat
                    (), width, height, frameRate, bitRate, audiomode, 0);
        } else {
            return false;
        }
        return true;

    }

    @Override
    public void stopRecorder() {
        mFrontCamera.stopRecord();
        if (isLock) {
            StorageUtils.lockFile(mPreFrontFile);
            mPreFrontFile = "";
            isLock = false;
        }
    }

    @Override
    public int setNextFile() {
        String file = getVideoFile(FRONT_CAMERA);
        //checkStorageSpaceNew
        if (checkStorageSpaceNew(file)) {
            Logger.i(TAG, "-setNextFile---file---" + file);
            mFrontCamera.setNextFile(file);
            Logger.i(TAG, "-setNextFile---file---2" + file);
            if (isLock) {
                StorageUtils.lockFile(mPreFrontFile);
            }
            mPreFrontFile = file;

        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }
        return RECORD_STATUS_SUCCESS;
    }


    @Override
    public int getCameraCount() {
        return 1;
    }

    @Override
    public void release() {
        Logger.i(TAG, "------release");
        if (mFrontCamera != null) {
            mFrontCamera.release();
            mFrontCamera = null;
        }
        haveInit = false;
    }

    @Override
    public boolean takePicture() {

        int debugFlag2 = 1;//1 debug mode,2021-06-09

        if (mFrontCamera != null) {

            if(debugFlag2 == 0)
                mFrontCamera.takePictureAsync(getPictureFile(FRONT_CAMERA));
            else
                mFrontCamera.takePictureAsync("/sdcard/avm/pic.jpg");


        }
        return true;
    }

    private Camera.Size getSupportVideoSize() {
        List<Camera.Size> sizes = mFrontCamera.getSupportVideoSize();
        Camera.Size pSize = Utils.stringToSize(DVRPreference.getInstance(mContext)
                .getFrontRecordSize(), mFrontCamera);
        if (sizes.size() != 0) {
            if (pSize == null) {
                return sizes.get(0);
            }
            for (Camera.Size size : sizes) {
                if (size.equals(pSize)) {
                    return size;
                }
            }
            return sizes.get(0);
        }

        return null;
    }

}
