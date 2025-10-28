package com.softwinner.dvr.media;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dvr.BaseDVR;
import com.softwinner.dvr.dvr.RecordListener;
import com.softwinner.dvr.ui.receiver.UsbCameraStateReceiver;
import com.softwinner.dvr.util.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;



public class RecordService extends Service implements UsbCameraStateReceiver.CameraStatusListener {

    public static final int MESSAGE_START_RECORD = 0;
    public static final int MESSAGE_NEXT_RECORD = 1;
    public static final int MESSAGE_STOP_RECORD = 2;
    public static final int MESSAGE_STORAGE_NOT_EXIT = 3;
    public static final int MESSAGE_INIT_FAILED = 4;
    public static final int MESSAGE_OPEN_CAMERA_FAILED = 5;

    private static final String TAG = "RecordService";

    private RecordBinder mBinder = new RecordBinder();
    private UsbCameraStateReceiver mUsbCameraStateReceiver;
    private Handler mHandler;
    private Timer mRecordTimer;

    private boolean isRecording = false;
    private PowerManager.WakeLock mWakeLock;
    private SleepReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
//        if ((DVRPreference.getInstance(this).getDVRType() == DVRPreference.DVR_TYPE_SINGLE_USB)
//                || (DVRPreference.getInstance(this).getDVRType() == DVRPreference
//                .DVR_TYPE_TWO_CAMERA_AND_USB)) {
//            mUsbCameraStateReceiver = new UsbCameraStateReceiver(this);
//            IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action" + "" + ""
//                    + ".USB_CAMERA_PLUG_IN_OUT");
//            registerReceiver(mUsbCameraStateReceiver, intentFilter);
//        }
        registerPowerReceiver();

    }


    @Override
    public void onDestroy() {
        Logger.d(TAG, "--------onDestroy------");
        unRegisterPowerReceiver();
        super.onDestroy();
        if (mUsbCameraStateReceiver != null) {
            unregisterReceiver(mUsbCameraStateReceiver);
            mUsbCameraStateReceiver = null;
        }
    }

    @Override
    public void onPlugIn(int cameraType) {

    }

    @Override
    public void onPlugOut(int cameraType) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(TAG, "--------onBind------");
        return mBinder;
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }


    /**
     * 开始录像
     */
    public void startRecord() {
        Logger.d(TAG, "-----startRecord-------");
        if (isRecording) {
            return;
        }
        int result = DVRApplication.getInstance().getDVR().startRecorder();
        switch (result) {
            case BaseDVR.RECORD_STATUS_SUCCESS:
                mRecordTimer = new Timer();
                int duration = DVRPreference.getInstance(this).getRecordDuration();
                mRecordTimer.schedule(new RecordTimerTask(), duration, duration);
                sendMessage(MESSAGE_START_RECORD);
                isRecording = true;
                break;
            case BaseDVR.RECORD_STATUS_OPEN_CAMERA_FAILED:
                sendMessage(MESSAGE_OPEN_CAMERA_FAILED);
                break;
            case BaseDVR.RECORD_STATUS_STORAGE_ERROR:
                sendMessage(MESSAGE_STORAGE_NOT_EXIT);
                break;
            case BaseDVR.RECORD_STATUS_INIT_ERROR:
                sendMessage(MESSAGE_INIT_FAILED);
                break;
        }
    }

    /**
     * 停止录像
     */
    public void stopRecord() {
        Logger.d(TAG, "-----stopRecord-------");
        if (!isRecording) {
            return;
        }

        if (mRecordTimer != null) {
            mRecordTimer.cancel();
            mRecordTimer = null;
        }

        DVRApplication.getInstance().getDVR().stopRecorder();
        sendMessage(MESSAGE_STOP_RECORD);
        isRecording = false;
    }

    private class RecordTimerTask extends TimerTask {

        @Override
        public void run() {
            if (isRecording) {

                int result = DVRApplication.getInstance().getDVR().setNextFile();

                switch (result) {
                    case BaseDVR.RECORD_STATUS_SUCCESS:
                        sendMessage(MESSAGE_NEXT_RECORD);
                        break;
                    case BaseDVR.RECORD_STATUS_OPEN_CAMERA_FAILED:
                        sendMessage(MESSAGE_OPEN_CAMERA_FAILED);
                        Logger.e(TAG, "setNextFile result = RECORD_STATUS_OPEN_CAMERA_FAILED");
                        stopRecord();
                        break;
                    case BaseDVR.RECORD_STATUS_STORAGE_ERROR:
                        sendMessage(MESSAGE_STORAGE_NOT_EXIT);
                        Logger.e(TAG, "setNextFile result = RECORD_STATUS_STORAGE_ERROR");
                        stopRecord();
                        break;
                }

            }
        }

    }

    public boolean isRecording() {
        return isRecording;
    }


    public void setHandler(Handler handler) {
        mHandler = handler;
    }


    private void sendMessage(int message) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(message);
        } else {
            Logger.e(TAG, "mHandler == null");
        }

    }

    class SleepReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Logger.d(TAG, "ACTION_SCREEN_OFF");
                requestWakeLock();
                stopRecord();
                DVRApplication.getInstance().getDVR().stopPreview();
                DVRApplication.getInstance().sleep();
                releaseWakeLock();
            }
        }
    }

    private void registerPowerReceiver() {
        if (mReceiver == null) {
            mReceiver = new SleepReceiver();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
    }

    private void unRegisterPowerReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void requestWakeLock() {
        if (mWakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DVR");
        }
        Log.e(TAG, "---requestWakeLock");
        mWakeLock.acquire(5000);
    }

    public void releaseWakeLock() {
        if (mWakeLock != null) {
            Log.e(TAG, "---releaseWakeLock");
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
