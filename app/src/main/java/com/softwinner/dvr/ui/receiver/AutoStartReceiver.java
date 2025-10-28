package com.softwinner.dvr.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.widget.Toast;

import com.softwinner.dvr.R;
import com.softwinner.dvr.camera.CameraListener;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dvr.BaseDVR;
import com.softwinner.dvr.dvr.RecordListener;
import com.softwinner.dvr.media.RecordManager;
import com.softwinner.dvr.ui.activity.ChoiceCameraActivity;
import com.softwinner.dvr.ui.activity.RecordActivity;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;

import java.io.IOException;



public class AutoStartReceiver extends BroadcastReceiver {
    BaseDVR mBaseDvr;
    Context mContext;
    private static final String TAG = "AutoStartReceiver";
    private static final int MSG_INIT_SUCCESS = 310;
    private static final int MSG_INIT_FAILED = 311;
    private static final int MSG_NO_STORAGE = 312;
    private static final int MSG_NO_PERMISSION = 313;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Logger.i(TAG, "  receiver--ACTION_BOOT_COMPLETED");
            if (DVRPreference.getInstance(context).isAutoStart()) {
                Intent activity = new Intent(context, ChoiceCameraActivity.class);
                activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activity);
            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //更新数据库
//                    if (StorageUtils.haveExtraSdcard(context)) {
//                        FileUtils.updateFileList();
//                    }
//
//                }
//            }, 1000);
//            initDVR();
        }
    }

    private void initDVR() {
        Logger.i(TAG, "initDVR");
        mBaseDvr = DVRApplication.getInstance().getDVR();
        mBaseDvr.setCameraListener(new CameraListener() {
            @Override
            public void onStartPreviewFailed() {

            }

            @Override
            public void onOpenCameraFailed() {

            }

            @Override
            public void onTakePictureSuccess() {

            }

            @Override
            public void onTakePictureFailed() {

            }
        });

        if (mBaseDvr.isHaveInit()) {
            mHandler.sendEmptyMessage(MSG_INIT_SUCCESS);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Logger.i(TAG, "mBaseDvr initDVR before ");
                boolean result = mBaseDvr.initDVR();
                Logger.i(TAG, "mBaseDvr initDVR after ");
                if (result) {
                    mHandler.sendEmptyMessage(MSG_INIT_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(MSG_INIT_FAILED);
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_SUCCESS:
                    Logger.i(TAG, "dvr init success");
                    RecordManager.getInstance(mContext).registerListener(new RecordListener() {
                        @Override
                        public void onInitFinished() {
                            SurfaceView surfaceView = new SurfaceView(mContext);
                            try {
                                DVRApplication.getInstance().getDVR().frontCameraStartPreview
                                        (surfaceView.getHolder());
                                RecordManager.getInstance(mContext).startRecord();
                            } catch (IOException e) {
                                Logger.i(TAG, "frontCameraStartPreview  failed");
                            }

                        }

                        @Override
                        public void onStartRecord() {

                        }

                        @Override
                        public void onRecordFailed(String reason) {

                        }

                        @Override
                        public void onStopRecord() {

                        }

                        @Override
                        public void onNextRecord() {

                        }

                        @Override
                        public void onFreshTime(String time) {

                        }
                    });

                    RecordManager.getInstance(mContext).init();
                    break;
                case MSG_INIT_FAILED:
                    Logger.e(TAG, "dvr init failed");
                    Toast.makeText(mContext, R.string.open_camera_failed, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MSG_NO_STORAGE:
                    Toast.makeText(mContext, R.string.storage_not_exit, Toast.LENGTH_LONG).show();
                    break;
                case MSG_NO_PERMISSION:
                    Toast.makeText(mContext, R.string.no_storage_permission, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };
}
