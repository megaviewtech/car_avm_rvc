package com.softwinner.dvr.dvr;

import android.content.Context;
import android.hardware.Camera;

import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.util.Utils;

import java.util.List;



public class FourCameraDVR extends BaseDVR {
    private static final String TAG = "FourCameraDVR";
    private int mAudiomode = 1;

    public FourCameraDVR(BaseCamera frontCamera, BaseCamera reverseCamera, BaseCamera leftCamera,
                         BaseCamera rightCamera, Context context) {
        mFrontCamera = frontCamera;
        mReverseCamera = reverseCamera;
        mLeftCamera = leftCamera;
        mRightCamera = rightCamera;
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

        if (mLeftCamera == null) {
            return RECORD_STATUS_OPEN_CAMERA_FAILED;
        }

        if (mRightCamera == null) {
            return RECORD_STATUS_OPEN_CAMERA_FAILED;
        }

        mAudiomode = 1;
        if (DVRPreference.getInstance(mContext).isRecordMute()) {
            mAudiomode = 0;
        }
//        mMinSpace = DVRPreference.getInstance(mContext).getRecordDuration() * 1024 * 100;
        boolean initResult = initFrontRecorder() && initReverseRecord() && initLeftRecord() &&
                initRightRecord();
        if (initResult) {
            mFrontCamera.startRecord();
            mReverseCamera.startRecord();
            mLeftCamera.startRecord();
            mRightCamera.startRecord();
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }

        return RECORD_STATUS_SUCCESS;
    }

    private boolean initFrontRecorder() {

        int width = 720;
        int height = 480;
        if ((DVRPreference.getInstance(mContext).getFrontCameraId() > 0) && (DVRPreference
                .getInstance(mContext).getFrontCameraId() <= 3)) {
            width = 1280;
            height = 720;
        }

        Camera.Size size = getFrontSupportVideoSize();
        if (size != null) {
            width = size.width;
            height = size.height;
        }

        int frameRate = mFrontCamera.getFrameRate();
        int bitRate = mFrontCamera.getBitRate();

        String file = getVideoFile(FRONT_CAMERA);
        mPreFrontFile = file;
        if (checkStorageSpace(file)) {
            mFrontCamera.initCameraRec(file, DVRPreference.getInstance(mContext).getRecordFormat
                    (), width, height, frameRate, bitRate, mAudiomode, 0);
        } else {
            return false;
        }
        return true;
    }

    private boolean initReverseRecord() {
        int width2 = 720;
        int height2 = 480;
        if ((DVRPreference.getInstance(mContext).getReverseCameraId() > 0) && (DVRPreference
                .getInstance(mContext).getReverseCameraId() <= 3)) {
            width2 = 1280;
            height2 = 720;
        }
        Camera.Size size = getReverseSupportVideoSize();
        if (size != null) {
            width2 = size.width;
            height2 = size.height;
        }

        int frameRate2 = mReverseCamera.getFrameRate();
        int bitRate2 = mReverseCamera.getBitRate();
        String file = getVideoFile(REVERSE_CAMERA);
        mPreReverseFile = file;
        if (checkStorageSpace(file)) {
            mReverseCamera.initCameraRec(file, DVRPreference.getInstance(mContext)
                    .getRecordFormat(), width2, height2, frameRate2, bitRate2, mAudiomode, 0);
        } else {
            return false;
        }
        return true;
    }

    private boolean initLeftRecord() {
        int width2 = 720;
        int height2 = 480;
        if ((DVRPreference.getInstance(mContext).getLeftCameraId() > 0) && (DVRPreference
                .getInstance(mContext).getLeftCameraId() <= 3)) {
            width2 = 1280;
            height2 = 720;
        }
        Camera.Size size = getLeftSupportVideoSize();
        if (size != null) {
            width2 = size.width;
            height2 = size.height;
        }

        int frameRate2 = mLeftCamera.getFrameRate();
        int bitRate2 = mLeftCamera.getBitRate();

        String file = getVideoFile(LEFT_CAMERA);
        mPreLeftFile = file;
        if (checkStorageSpace(file)) {
            mLeftCamera.initCameraRec(file, DVRPreference.getInstance(mContext).getRecordFormat()
                    , width2, height2, frameRate2, bitRate2, mAudiomode, 0);
        } else {
            return false;
        }
        return true;
    }

    private boolean initRightRecord() {
        int width2 = 720;
        int height2 = 480;
        if ((DVRPreference.getInstance(mContext).getRightCameraId() > 0) && (DVRPreference
                .getInstance(mContext).getRightCameraId() <= 3)) {
            width2 = 1280;
            height2 = 720;
        }
        Camera.Size size = getRightSupportVideoSize();
        if (size != null) {
            width2 = size.width;
            height2 = size.height;
        }

        int frameRate2 = mRightCamera.getFrameRate();
        int bitRate2 = mRightCamera.getBitRate();

        String file = getVideoFile(RIGHT_CAMERA);
        mPreRightFile = file;
        if (checkStorageSpace(file)) {
            mRightCamera.initCameraRec(file, DVRPreference.getInstance(mContext).getRecordFormat
                    (), width2, height2, frameRate2, bitRate2, mAudiomode, 0);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public synchronized void stopRecorder() {
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
                StorageUtils.lockFile(mPreReverseFile);
                mPreReverseFile = "";
            }
        }

        if (mLeftCamera != null) {
            mLeftCamera.stopRecord();
            if (isLock) {
                StorageUtils.lockFile(mPreLeftFile);
                mPreLeftFile = "";
            }
        }

        if (mRightCamera != null) {
            mRightCamera.stopRecord();
            if (isLock) {
                StorageUtils.lockFile(mPreRightFile);
                mPreRightFile = "";
                isLock = false;
            }
        }
    }

    @Override
    public synchronized int setNextFile() {
        String file = getVideoFile(FRONT_CAMERA);
        Logger.i(TAG, "--------NEXT FILE=" + file);
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

        String file3 = getVideoFile(LEFT_CAMERA);
        if (checkStorageSpace(file3)) {
            mLeftCamera.setNextFile(file3);
            if (isLock) {
                StorageUtils.lockFile(mPreLeftFile);
            }
            mPreLeftFile = file3;
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }

        String file4 = getVideoFile(RIGHT_CAMERA);
        if (checkStorageSpace(file4)) {
            mRightCamera.setNextFile(file4);
            if (isLock) {
                StorageUtils.lockFile(mPreRightFile);
            }
            mPreRightFile = file4;
        } else {
            return RECORD_STATUS_STORAGE_ERROR;
        }

        Logger.i(TAG, "--------NEXT FILE2=" + file);
        return RECORD_STATUS_SUCCESS;
    }

    @Override
    public int getCameraCount() {
        return 4;
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

        if (mRightCamera != null) {
            mRightCamera.release();
            mRightCamera = null;
        }

        if (mLeftCamera != null) {
            mLeftCamera.release();
            mLeftCamera = null;
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

        if (mLeftCamera != null) {
            mLeftCamera.takePictureAsync(getPictureFile(LEFT_CAMERA));
        }

        if (mRightCamera != null) {
            mRightCamera.takePictureAsync(getPictureFile(RIGHT_CAMERA));
        }
        return true;
    }

    private Camera.Size getFrontSupportVideoSize() {
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

    private Camera.Size getReverseSupportVideoSize() {
        List<Camera.Size> sizes = mReverseCamera.getSupportVideoSize();
        Camera.Size pSize = Utils.stringToSize(DVRPreference.getInstance(mContext)
                .getReverseRecordSize(), mReverseCamera);
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

    private Camera.Size getLeftSupportVideoSize() {
        List<Camera.Size> sizes = mLeftCamera.getSupportVideoSize();
        Camera.Size pSize = Utils.stringToSize(DVRPreference.getInstance(mContext)
                .getLeftRecordSize(), mLeftCamera);
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

    private Camera.Size getRightSupportVideoSize() {
        List<Camera.Size> sizes = mRightCamera.getSupportVideoSize();
        Camera.Size pSize = Utils.stringToSize(DVRPreference.getInstance(mContext)
                .getRightRecordSize(), mRightCamera);
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
