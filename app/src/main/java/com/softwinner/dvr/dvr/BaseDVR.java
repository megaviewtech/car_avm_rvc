package com.softwinner.dvr.dvr;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.camera.CameraListener;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



public abstract class BaseDVR {
    private static final String TAG = "BaseDVR";
    public BaseCamera mFrontCamera;
    protected BaseCamera mReverseCamera;
    protected BaseCamera mLeftCamera;
    protected BaseCamera mRightCamera;
    protected long mMinSpace = 1 * 1000 * 1000 * 1000l;

    public static final int FRONT_CAMERA = 1;
    public static final int REVERSE_CAMERA = 2;
    public static final int LEFT_CAMERA = 3;
    public static final int RIGHT_CAMERA = 4;

    public static final int RECORD_STATUS_SUCCESS = 0;
    public static final int RECORD_STATUS_OPEN_CAMERA_FAILED = 1;
    public static final int RECORD_STATUS_STORAGE_ERROR = 2;
    public static final int RECORD_STATUS_INIT_ERROR = 3;

    private PowerManager.WakeLock mWakeLock;

    protected String mPreFrontFile;
    protected String mPreReverseFile;
    protected String mPreLeftFile;
    protected String mPreRightFile;

    Context mContext;
    boolean haveInit = false;

    boolean isLock = false;

    public void stopPreview() {

        Logger.d("liugang","stopPreview");
        if (mFrontCamera != null) {
            mFrontCamera.stopPreview();
        }

        if (mReverseCamera != null) {
            mReverseCamera.stopPreview();
        }

        if (mLeftCamera != null) {
            mLeftCamera.stopPreview();
        }

        if (mRightCamera != null) {
            mRightCamera.stopPreview();
        }
    }

    public abstract int startRecorder();

    public abstract void stopRecorder();

    public abstract int setNextFile();

    public abstract int getCameraCount();

    public abstract void release();

    public abstract boolean takePicture();

    public BaseCamera getFrontCamera() {
        return mFrontCamera;
    }

    public BaseCamera getReverseCamera() {
        return mReverseCamera;
    }

    public BaseCamera getLeftCamera() {
        return mLeftCamera;
    }

    public BaseCamera getRightCamera() {
        return mRightCamera;
    }

    public void frontCameraStartPreview(SurfaceTexture surfaceTexture) throws IOException {
        Logger.d("liugang", "frontCameraStartPreview");
        if (mFrontCamera != null) {
            mFrontCamera.startPreview(surfaceTexture);
/*            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mFrontCamera.startWaterMark();
            }*/
        } else {
            Logger.e(TAG, "frontCameraStartPreview camera == null");
        }
    }

    public void frontCameraStartPreview(SurfaceHolder surfaceHolder) throws IOException {
        Logger.i(TAG, "frontCameraStartPreview");
        if (mFrontCamera != null) {
            mFrontCamera.startPreview(surfaceHolder);
/*            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mFrontCamera.startWaterMark();
            }*/
        } else {
            Logger.e(TAG, "frontCameraStartPreview camera == null");
        }
    }

    public void reverseCameraStartPreview(SurfaceTexture surfaceTexture) throws IOException {
        Logger.i(TAG, "reverseCameraStartPreview");
        if (mReverseCamera != null) {
            mReverseCamera.startPreview(surfaceTexture);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mReverseCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "reverseCameraStartPreview camera == null");
        }
    }

    public void reverseCameraStartPreview(SurfaceHolder surfaceHolder) throws IOException {
        Logger.i(TAG, "reverseCameraStartPreview");
        if (mReverseCamera != null) {
            mReverseCamera.startPreview(surfaceHolder);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mReverseCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "reverseCameraStartPreview camera == null");
        }
    }

    public void rightCameraStartPreview(SurfaceTexture surfaceTexture) throws IOException {
        Logger.i(TAG, "rightCameraStartPreview");
        if (mRightCamera != null) {
            mRightCamera.startPreview(surfaceTexture);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mRightCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "rightCameraStartPreview camera == null");
        }
    }

    public void rightCameraStartPreview(SurfaceHolder surfaceHolder) throws IOException {
        Logger.i(TAG, "rightCameraStartPreview");
        if (mRightCamera != null) {
            mRightCamera.startPreview(surfaceHolder);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mRightCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "rightCameraStartPreview camera == null");
        }
    }

    public void leftCameraStartPreview(SurfaceTexture surfaceTexture) throws IOException {
        Logger.i(TAG, "leftCameraStartPreview");
        if (mLeftCamera != null) {
            mLeftCamera.startPreview(surfaceTexture);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mLeftCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "leftCameraStartPreview camera == null");
        }
    }

    public void leftCameraStartPreview(SurfaceHolder surfaceHolder) throws IOException {
        Logger.i(TAG, "leftCameraStartPreview");
        if (mLeftCamera != null) {
            mLeftCamera.startPreview(surfaceHolder);
            if (DVRPreference.getInstance(mContext.getApplicationContext()).haveWaterMark()) {
                mLeftCamera.startWaterMark();
            }
        } else {
            Logger.e(TAG, "leftCameraStartPreview camera == null");
        }
    }

    protected boolean checkStorageSpace(String file) {
        //检查空间
//        String path = DVRPreference.getInstance(mContext).getFrontVideoPath();
        String path = file.substring(0, file.lastIndexOf(File.separatorChar));
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (StorageUtils.getAvailableSpace(path) < mMinSpace) {
            Logger.i(TAG, "----checkStorageSpace,getSortFiles before:"+file);
            List<String> fileList = StorageUtils.getSortFiles(file);
            //List<String> fileList = StorageUtils.getSortFiles("/storage/usb2/MEGAVIEW/front/");


            if (fileList == null || fileList.size() == 0) {
                Logger.i(TAG, "--getSortFiles after fileList is Empty");
                return false;
            }
            //清空
            String fileName = "";

            if (DVRPreference.getInstance(mContext).isPreAllocation()) {

                for (int i = 0; i < fileList.size(); i++) {
                    fileName = fileList.get(i);
                    Logger.i(TAG, "--releasePreFile before");
                    if (0 == StorageUtils.releasePreFile(fileName)) {
                        Logger.i(TAG, "--releasePreFile after");
                        new File(fileName).renameTo(new File(file));
                        FileUtils.removeVideoFile(fileName);
                        FileUtils.addFile(file);
                        break;
                    } else if (i == fileList.size() - 1) {
                        Logger.i(TAG, "--checkStorageSpace--" + fileName + "releasePreFile failed");
                        return false;
                    } else {
                        Logger.i(TAG, "--checkStorageSpace--" + fileName + "releasePreFile " +
                                "failed" + " go to next");
                    }

                }
            } else {
                while (StorageUtils.getAvailableSpace(path) < mMinSpace) {
                    new File(fileList.get(0)).delete();
                    FileUtils.removeVideoFile(fileList.get(0));
                    fileList.remove(0);
                    Log.d(TAG, "---checkStorageSpace:delete file ");
                }

                FileUtils.addFile(file);
            }

        } else {
            if (DVRPreference.getInstance(mContext).isPreAllocation()) {
                long preSize = 400 * 1024 * 1024;

                int result = StorageUtils.createPreFile(file, preSize);
                Logger.i(TAG, "--checkStorageSpace--FileUtil.create-" + file + "-result=" + result);
                if (result == -1) {
                    return false;
                } else {
                    FileUtils.addFile(file);
                }
            }else {
                FileUtils.addFile(file);
            }

        }
        return true;
    }



    protected boolean checkStorageSpaceNew(String file) {
        //检查空间
//        String path = DVRPreference.getInstance(mContext).getFrontVideoPath();
        String path = file.substring(0, file.lastIndexOf(File.separatorChar));
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Logger.i(TAG, "----------------checkStorageSpace,getAvailableSpace = " + StorageUtils.getAvailableSpace
                (path) + "---mMinSpace = " + mMinSpace);



        while (StorageUtils.getAvailableSpace(path) < mMinSpace) {
            Logger.i(TAG, "----checkStorageSpace,getSortFiles before:"+file);

            File[] files = new File(path).listFiles();
            List<File> list;
            if (files == null) {
                //list = new ArrayList<>();
                return false;
            } else {
                list = Arrays.asList(files);
            }


           // Log.d(TAG, "checkStorageSpaceNew2: "+list.get(0).getName()+list.get(1).getName()+list.get(2).getName()+"size:"+list.size());


                    int minIndex = 0;
                    for(int i=0;i<list.size();i++) {


                        if(list.get(i).getName().compareTo(list.get(minIndex).getName())<0)
                            minIndex = i;

                    }

                    Log.d(TAG, "---checkStorageSpace:delete file: "+list.get(minIndex).getName());
                    list.get(minIndex).delete();

                    //list.remove(list.size() - 1);





        }
        return true;
    }




    /**
     * 检查当前环境（如存储）是否可以拍照
     */
    private boolean canTakePicture() {
        String rootPath = DVRPreference.getInstance(mContext).getPhotoPath();
        while (StorageUtils.getAvailableSpace(rootPath) < mMinSpace) {
            File[] files = new File(rootPath).listFiles();
            if (files == null || files.length == 0) {
                return false;
            }
            //排序
            ArrayList<String> fileList = new ArrayList<>();
            for (File tmpFile : files) {
                if (tmpFile.getName().endsWith(".jpg")) {
                    fileList.add(tmpFile.getAbsolutePath());
                }
            }
            Collections.sort(fileList);
            //清空
            String fileName = fileList.get(0);
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
        }

        return true;
    }

    /**
     * 获取视频文件名
     *
     * @return
     */
    protected String getVideoFile(int cameraType) {
        String suffix = ".ts";
        String result = "";
        switch (cameraType) {
            case FRONT_CAMERA:
                if (isLock) {
                    result = DVRPreference.getInstance(mContext).getLockPath() + "f_" + Utils
                            .getCurrentDateTime() + suffix;
                } else {
                    result = DVRPreference.getInstance(mContext).getFrontVideoPath() + "f_" +
                            Utils.getCurrentDateTime() + suffix;
                }
                break;
            case REVERSE_CAMERA:
                if (isLock) {
                    result = DVRPreference.getInstance(mContext).getLockPath() + "b_" + Utils
                            .getCurrentDateTime() + suffix;
                } else {
                    result = DVRPreference.getInstance(mContext).getReverseVideoPath() + "b_" +
                            Utils.getCurrentDateTime() + suffix;
                }
                break;
            case LEFT_CAMERA:
                if (isLock) {
                    result = DVRPreference.getInstance(mContext).getLockPath() + "l_" + Utils
                            .getCurrentDateTime() + suffix;
                } else {
                    result = DVRPreference.getInstance(mContext).getLeftVideoPath() + "l_" +
                            Utils.getCurrentDateTime() + suffix;
                }
                break;
            case RIGHT_CAMERA:
                if (isLock) {
                    result = DVRPreference.getInstance(mContext).getLockPath() + "r_" + Utils
                            .getCurrentDateTime() + suffix;
                } else {
                    result = DVRPreference.getInstance(mContext).getRightVideoPath() + "r_" +
                            Utils.getCurrentDateTime() + suffix;
                }
                break;
        }
        return result;
    }

    /**
     * 获取视频文件名
     *
     * @return
     */
    protected String getPictureFile(int cameraType) {
        String suffix = ".jpg";
        switch (cameraType) {
            case FRONT_CAMERA:
                return DVRPreference.getInstance(mContext).getPhotoPath() + "f_" + Utils
                        .getCurrentDateTime() + suffix;
            case REVERSE_CAMERA:
                return DVRPreference.getInstance(mContext).getPhotoPath() + "b_" + Utils
                        .getCurrentDateTime() + suffix;
            case LEFT_CAMERA:
                return DVRPreference.getInstance(mContext).getPhotoPath() + "l_" + Utils
                        .getCurrentDateTime() + suffix;
            case RIGHT_CAMERA:
                return DVRPreference.getInstance(mContext).getPhotoPath() + "r_" + Utils
                        .getCurrentDateTime() + suffix;
        }
        return null;
    }

    public long getMinSpace() {
        return mMinSpace;
    }

    public synchronized boolean initDVR() {
        if (haveInit) {
            return true;
        }
        boolean result = false;
        if (mFrontCamera != null) {
            result = mFrontCamera.openCamera();
        }

        if (!result) {
            return false;
        }

        if (mReverseCamera != null) {
            result = mReverseCamera.openCamera();
            if (!result) {
                return false;
            }
        }

        if (mLeftCamera != null) {
            result = mLeftCamera.openCamera();
            if (!result) {
                return false;
            }
        }

        if (mRightCamera != null) {
            result = mRightCamera.openCamera();
            if (!result) {
                return false;
            }
        }

//        acquirePowerLock();
        haveInit = result;
        return result;
    }

    public boolean isHaveInit() {
        return haveInit;
    }

    public void setCameraListener(CameraListener listener) {
        if (mFrontCamera != null) {
            mFrontCamera.setCameraListener(listener);
        }

        if (mReverseCamera != null) {
            mReverseCamera.setCameraListener(listener);
        }

        if (mLeftCamera != null) {
            mLeftCamera.setCameraListener(listener);
        }

        if (mRightCamera != null) {
            mRightCamera.setCameraListener(listener);
        }
    }

    public void startWaterMark() {
        Logger.d(TAG, "----startWaterMark ");
        if (mFrontCamera != null) {
            mFrontCamera.startWaterMark();
        }

        if (mReverseCamera != null) {
            mReverseCamera.startWaterMark();
        }

        if (mLeftCamera != null) {
            mLeftCamera.startWaterMark();
        }

        if (mRightCamera != null) {
            mRightCamera.startWaterMark();
        }
    }

    public void stopWaterMark() {
        Logger.d(TAG, "----stopWaterMark ");
        if (mFrontCamera != null) {
            mFrontCamera.stopWaterMark();
        }

        if (mReverseCamera != null) {
            mReverseCamera.stopWaterMark();
        }

        if (mLeftCamera != null) {
            mLeftCamera.stopWaterMark();
        }

        if (mRightCamera != null) {
            mRightCamera.stopWaterMark();
        }
    }


    private void acquirePowerLock() {
        if (mWakeLock == null) {
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context
                    .POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DVR");
        }
        mWakeLock.acquire();
    }

    protected void releasePowerLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        mWakeLock = null;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

}
