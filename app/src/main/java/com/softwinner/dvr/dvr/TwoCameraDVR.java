package com.softwinner.dvr.dvr;

import android.content.Context;
import android.hardware.Camera;

import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.util.Utils;

import java.util.List;



public class TwoCameraDVR extends BaseDVR {
    private static final String TAG = "NormalTwoCameraDVR";

    public TwoCameraDVR(BaseCamera frontCamera, BaseCamera reverseCamera, Context context) {
        mFrontCamera = frontCamera;
        mReverseCamera = reverseCamera;
        mContext = context;
    }

    @Override
    public int startRecorder() {
        if (mFrontCamera == null) {
            return RECORD_STATUS_OPEN_CAMERA_FAILED;
        }

        if (mReverseCamera == null) {
            return RECORD_STATUS_OPEN_CAMERA_FAILED;
        }

        boolean initResult = initRecorder();

        if (initResult) {
            mReverseCamera.startRecord();
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
            width = 1920;
            height = 1080;
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

        int frameRate = mFrontCamera.getFrameRate();
        int bitRate = mFrontCamera.getBitRate();
//        mMinSpace = DVRPreference.getInstance(mContext).getRecordDuration() * 1024 * 200;
        String frontFile = getVideoFile(FRONT_CAMERA);
        mPreFrontFile = frontFile;
        int audiomode = 1;
        if (DVRPreference.getInstance(mContext).isRecordMute()) {
            audiomode = 0;
        }
        if (checkStorageSpace(frontFile)) {
            mFrontCamera.initCameraRec(frontFile, DVRPreference.getInstance(mContext)
                    .getRecordFormat(), width, height, frameRate, bitRate, audiomode, 0);
        } else {
            return false;
        }


        int width2 = 720;
        int height2 = 480;
        if ((DVRPreference.getInstance(mContext).getReverseCameraId() >= 0) && (DVRPreference
                .getInstance(mContext).getReverseCameraId() <= 3)) {
            width2 = 1280;
            height2 = 720;
        } else if ((DVRPreference.getInstance(mContext).getFrontCameraId() == 9) ||
                (DVRPreference.getInstance(mContext).getFrontCameraId() == 10)) {
            width2 = 1280;
            height2 = 720;
        }
//        Camera.Size size2 = getSupportVideoSize2();
//        if (size2 != null) {
//            width2 = size2.width;
//            height2 = size2.height;
//        }

        int frameRate2 = mFrontCamera.getFrameRate();
        int bitRate2 = mFrontCamera.getBitRate();
        String reverseFile = getVideoFile(REVERSE_CAMERA);
        mPreReverseFile = reverseFile;
        if (checkStorageSpace(reverseFile)) {
            mReverseCamera.initCameraRec(reverseFile, DVRPreference.getInstance(mContext)
                    .getRecordFormat(), width2, height2, frameRate2, bitRate2, audiomode, 0);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void stopRecorder() {
        if (mFrontCamera != null) {
            mFrontCamera.stopRecord();
            if (isLock) {
                StorageUtils.lockFile(mPreFrontFile);
                mPreFrontFile = "";
            }
        }

        if (mReverseCamera != null) {
            mReverseCamera.stopRecord();
            if (isLock) {
                StorageUtils.lockFile(mPreRightFile);
                mPreRightFile = "";
                isLock = false;
            }
        }
    }

    @Override
    public int setNextFile() {
        String file = getVideoFile(FRONT_CAMERA);

        if (checkStorageSpace(file)) {
            mFrontCamera.setNextFile(file);
            if (isLock) {
                StorageUtils.lockFile(mPreFrontFile);
            }
            mPreFrontFile = file;
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }

        String file2 = getVideoFile(REVERSE_CAMERA);

        if (checkStorageSpace(file2)) {
            mReverseCamera.setNextFile(file2);
            if (isLock) {
                StorageUtils.lockFile(mPreReverseFile);
            }
            mPreReverseFile = file2;
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }
        return RECORD_STATUS_SUCCESS;
    }

    @Override
    public int getCameraCount() {
        return 2;
    }

    @Override
    public void release() {
        Logger.i(TAG, "------release");
        if (mFrontCamera != null) {
            mFrontCamera.release();
            mFrontCamera = null;
        }

        if (mReverseCamera != null) {
            mReverseCamera.release();
            mReverseCamera = null;
        }
        haveInit = false;
    }

    @Override
    public boolean takePicture() {
        if (mFrontCamera != null) {
            mFrontCamera.takePictureAsync(getPictureFile(FRONT_CAMERA));
        }

        if (mReverseCamera != null) {
            mReverseCamera.takePictureAsync(getPictureFile(REVERSE_CAMERA));
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

    private Camera.Size getSupportVideoSize2() {
        List<Camera.Size> sizes = mReverseCamera.getSupportVideoSize();
        Camera.Size pSize = Utils.stringToSize(DVRPreference.getInstance(mContext)
                .getFrontRecordSize(), mReverseCamera);
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

    @Override
    public BaseCamera getFrontCamera() {
        return super.getFrontCamera();
    }

}
