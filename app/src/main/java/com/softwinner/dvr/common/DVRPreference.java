package com.softwinner.dvr.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;


public final class DVRPreference {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static DVRPreference sInstance;
    private final String NAME = "dvr";
    public static final int DVR_TYPE_SINGLE_CAMERA = 0;
    public static final int DVR_TYPE_TWO_CAMERA = 1;
    public static final int DVR_TYPE_FOUR_CVBS = 2;
    public static final int DVR_TYPE_FOUR_CSI = 3;
    public static final int DVR_TYPE_CVBS_FOUR_TO_ONE = 4;
    public static final int DVR_TYPE_CSI_FOUR_TO_ONE = 5;
    public static final int DVR_TYPE_CSI_TWO_TWO = 6;

    public static final int RECORD_FORMAT_H264_TS = 0;
    public static final int RECORD_FORMAT_H265_TS = 1;
    public static final int RECORD_FORMAT_H264_MP4 = 2;
    public static final int RECORD_FORMAT_H265_MP4 = 3;

    private final String KEY_DVR_TYPE = "dvr_type";

    private final String KEY_FIRST_START = "first_start";

    private final String KEY_FRONT_CAMERA_ID = "front_id";
    private final String KEY_REVERSE_CAMERA_ID = "reverse_id";
    private final String KEY_LEFT_CAMERA_ID = "left_id";
    private final String KEY_RIGHT_CAMERA_ID = "right_id";

    private final String KEY_FRONT_CAMERA_RECODE_SIZE = "front_recode_size";
    private final String KEY_REVERSE_CAMERA_RECODE_SIZE = "reverse_recode_size";
    private final String KEY_LEFT_CAMERA_RECODE_SIZE = "left_recode_size";
    private final String KEY_RIGHT_CAMERA_RECODE_SIZE = "right_recode_size";

    private final String KEY_RECORD_DURATION = "record_duration";

    private final String KEY_RECORD_FORMAT = "record_format";

    private final String KEY_CAR_INDEX = "car_index";

    private final String KEY_COLOR_INDEX = "color_index";

    private final String KEY_RECORD_MUTE = "record_mute";

    private final String KEY_WATER_MARK = "water_mark";

    private final String KEY_AUTO_START = "auto_start";
    private final String KEY_LR = "lr";
    private final String KEY_EMERGENCY = "emergency_start";
    private final String KEY_BACKSTAGE_RECORD = "backstage_record";
    private final String KEY_PRE_ALLOCATION = "pre_allocation";

    private final int DEFAULT_RECORD_DURATION = 60 * 1000;

    private final String KEY_RECORD_PATH = "record_path";

    private final String DEFAULT_RECORD_PATH = "/storage/usb2";//usb2,udiskh,udisk3

    private final String FRONT_VIDEO_PATH = "/MEGAVIEW/front/";
    private final String REVERSE_VIDEO_PATH = "/MEGAVIEW/reverse/";
    private final String RIGHT_VIDEO_PATH = "/MEGAVIEW/right/";
    private final String LEFT_VIDEO_PATH = "/MEGAVIEW/left/";
    private final String LOCK_VIDEO_PATH = "/MEGAVIEW/lock/";
    private final String PHOTO_PATH = "/MEGAVIEW/photo/";


    private DVRPreference(Context context) {
        mPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static DVRPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DVRPreference(context.getApplicationContext());
        }
        return sInstance;
    }


    public boolean isFirstStart() {
        if (mPreferences == null) {
            return true;
        }
        return mPreferences.getBoolean(KEY_FIRST_START, true);
    }

    public void setFirstStart(boolean isFirstStart) {
        if (mPreferences == null) {
            return;
        }
        mEditor.putBoolean(KEY_FIRST_START, isFirstStart);
        mEditor.commit();
    }


    public void setDVRType(int type) {
        if (mPreferences == null) {
            return;
        }
        mEditor.putInt(KEY_DVR_TYPE, type);
        mEditor.commit();
    }

    public int getDVRType() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_DVR_TYPE, 0);
    }

    public int getFrontCameraId() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_FRONT_CAMERA_ID, 0);
    }

    public void setFrontCameraId(int cameraId) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_FRONT_CAMERA_ID, cameraId);
        mEditor.commit();
    }

    public int getReverseCameraId() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_REVERSE_CAMERA_ID, 1);
    }

    public void setReverseCameraId(int cameraId) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_REVERSE_CAMERA_ID, cameraId);
        mEditor.commit();
    }

    public int getLeftCameraId() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_LEFT_CAMERA_ID, 1);
    }

    public void setLeftCameraId(int cameraId) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_LEFT_CAMERA_ID, cameraId);
        mEditor.commit();
    }

    public int getRightCameraId() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_RIGHT_CAMERA_ID, 1);
    }

    public void setRightCameraId(int cameraId) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_RIGHT_CAMERA_ID, cameraId);
        mEditor.commit();
    }

    public String getFrontRecordSize() {
        if (mPreferences == null) {
            return "";
        }
        return mPreferences.getString(KEY_FRONT_CAMERA_RECODE_SIZE, "");
    }

    public void setFrontRecordSize(String recordSize) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putString(KEY_FRONT_CAMERA_RECODE_SIZE, recordSize);
        mEditor.commit();
    }

    public String getReverseRecordSize() {
        if (mPreferences == null) {
            return "";
        }
        return mPreferences.getString(KEY_FRONT_CAMERA_RECODE_SIZE, "");
    }

    public void setReverseRecordSize(String recordSize) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putString(KEY_REVERSE_CAMERA_RECODE_SIZE, recordSize);
        mEditor.commit();
    }

    public String getLeftRecordSize() {
        if (mPreferences == null) {
            return "";
        }
        return mPreferences.getString(KEY_LEFT_CAMERA_RECODE_SIZE, "");
    }

    public void setLeftRecordSize(String recordSize) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putString(KEY_LEFT_CAMERA_RECODE_SIZE, recordSize);
        mEditor.commit();
    }

    public String getRightRecordSize() {
        if (mPreferences == null) {
            return "";
        }
        return mPreferences.getString(KEY_RIGHT_CAMERA_RECODE_SIZE, "");
    }

    public void setRightRecordSize(String recordSize) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putString(KEY_RIGHT_CAMERA_RECODE_SIZE, recordSize);
        mEditor.commit();
    }

    public int getRecordDuration() {
        if (mPreferences == null) {
            return DEFAULT_RECORD_DURATION;
        }
        return mPreferences.getInt(KEY_RECORD_DURATION, DEFAULT_RECORD_DURATION);
    }

    public void setRecordDuration(int duration) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_RECORD_DURATION, duration);
        mEditor.commit();
    }

    public int getRecordFormat() {
        if (mPreferences == null) {
            return RECORD_FORMAT_H265_TS;
        }
        return mPreferences.getInt(KEY_RECORD_FORMAT, RECORD_FORMAT_H265_TS);
    }

    public void setVideoFormat(int format) {

        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_RECORD_FORMAT, format);
        mEditor.commit();
    }

    String path1 = "/storage/usb1";
    String path2 = "/storage/usb2";

    public String getRecordPath() {


        File file = new File(path1);

        if (file.exists()) {
            return path1;
        }

        file = new File(path2);

        if (file.exists()) {
            return path2;
        }

        if (mPreferences == null) {
            return DEFAULT_RECORD_PATH;
        }
        return mPreferences.getString(KEY_RECORD_PATH, DEFAULT_RECORD_PATH);
    }

    public void setRecordPath(String path) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putString(KEY_RECORD_PATH, path);
        mEditor.commit();
    }


    public String getFrontVideoPath() {
        return getRecordPath() + FRONT_VIDEO_PATH;
    }

    public String getReverseVideoPath() {
        return getRecordPath() + REVERSE_VIDEO_PATH;
    }

    public String getRightVideoPath() {
        return getRecordPath() + RIGHT_VIDEO_PATH;
    }

    public String getLeftVideoPath() {
        return getRecordPath() + LEFT_VIDEO_PATH;
    }

    public String getPhotoPath() {
        return getRecordPath() + PHOTO_PATH;
    }

    public String getLockPath() {
        return getRecordPath() + LOCK_VIDEO_PATH;
    }

    public boolean isRecordMute() {
        if (mPreferences == null) {
            return true;
        }
        return mPreferences.getBoolean(KEY_RECORD_MUTE, true);
    }

    public void setRecordMute(boolean mute) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_RECORD_MUTE, mute);
        mEditor.commit();
    }

    public boolean isAutoStart() {
        if (mPreferences == null) {
            return false;
        }
        return mPreferences.getBoolean(KEY_AUTO_START, false);
    }

    public void setAutoStart(boolean autoStart) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_AUTO_START, autoStart);
        mEditor.commit();
    }

    public boolean isBackstageRecord() {
        if (mPreferences == null) {
            return true;
        }
        return mPreferences.getBoolean(KEY_BACKSTAGE_RECORD, false);
    }

    public void setBackstageRecord(boolean isBackstage) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_BACKSTAGE_RECORD, isBackstage);
        mEditor.commit();
    }


    public boolean isLR() {
        if (mPreferences == null) {
            return true;
        }

        return mPreferences.getBoolean(KEY_LR, true);
    }

    public void setLR(boolean lr) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_LR, lr);
        mEditor.commit();
    }


    public boolean isEmergency() {
        if (mPreferences == null) {
            return true;
        }

        return mPreferences.getBoolean(KEY_EMERGENCY, true);
    }

    public void setEmergency(boolean emergency) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_EMERGENCY, emergency);
        mEditor.commit();
    }

    //for demo flag,2020-7-5 by liugang
    public boolean haveWaterMark() {
        if (mPreferences == null) {
            return false;
        }
        return mPreferences.getBoolean(KEY_WATER_MARK, false);
    }

    public void setWaterMark(boolean waterMark) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_WATER_MARK, waterMark);
        mEditor.commit();
    }

    public void setPreAllocation(boolean isPre) {
        if (mPreferences == null) {
            return;
        }

        mEditor.putBoolean(KEY_PRE_ALLOCATION, isPre);
        mEditor.commit();
    }

    public boolean isPreAllocation() {
        if (mPreferences == null) {
            return true;
        }
        return mPreferences.getBoolean(KEY_PRE_ALLOCATION, false);//默认关闭pre allocation
    }

    public int getCarIndex() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_CAR_INDEX, 0);
    }

    public void setCarIndex(int carIndex) {

        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_CAR_INDEX, carIndex);
        mEditor.commit();
    }

    public int getColorIndex() {
        if (mPreferences == null) {
            return 0;
        }
        return mPreferences.getInt(KEY_COLOR_INDEX, 0);
    }

    public void setColorIndex(int colorIndex) {

        if (mPreferences == null) {
            return;
        }

        mEditor.putInt(KEY_COLOR_INDEX, colorIndex);
        mEditor.commit();
    }

    public void reset() {
        mEditor.remove(KEY_FRONT_CAMERA_RECODE_SIZE);
        mEditor.remove(KEY_REVERSE_CAMERA_RECODE_SIZE);
        mEditor.remove(KEY_RIGHT_CAMERA_RECODE_SIZE);
        mEditor.remove(KEY_LEFT_CAMERA_RECODE_SIZE);
        mEditor.commit();
    }
}
