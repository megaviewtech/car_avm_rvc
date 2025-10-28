package com.softwinner.dvr.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dvr.RecordListener;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;



public class RecordManager {

    private static final String TAG = "RecordManager";
    private RecordService mRecordService = null;
    private static RecordManager sInstance;
    private RecordListener mListener;
    private Context mContext;
    private Disposable mDisposable;

    private boolean isInit = false;

    private RecordManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static RecordManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RecordManager(context);
        }
        return sInstance;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "-----onServiceDisconnected------");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(TAG, "-----onServiceConnected------");
            mRecordService = ((RecordService.RecordBinder) service).getRecordService();
            mRecordService.setHandler(mHandler);
            if (mListener != null) {
                mListener.onInitFinished();
            }
            isInit = true;
        }
    };

    public void init() {
        Logger.d(TAG, "-----init------");
        if (mRecordService == null) {
            mContext.bindService(new Intent(mContext, RecordService.class), mServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }

    }

    public boolean isInit() {
        return isInit;
    }

    public boolean isRecording() {
        if (mRecordService == null) {
            return false;
        }
        return mRecordService.isRecording();
    }

    public void registerListener(RecordListener listener) {
        Logger.i(TAG, "-----registerListener------" + listener);
        mListener = listener;
    }

    public void unRegisterListener() {
        mListener = null;
    }

    private void refreshTime() {
        final long duration = DVRPreference.getInstance(mContext).getRecordDuration();
        mDisposable = Flowable.interval(0L, 1L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long time) throws Exception {
                String result = "";
                if (time != 0L && time % duration == 0L) {
                    result = Utils.getFormatRecordTime(duration);
                } else {
                    result = Utils.getFormatRecordTime(time % duration);
                }
                if (mListener != null) {
                    mListener.onFreshTime(result);
                }
            }
        });
    }

    public void startRecord() {
        if (mRecordService != null) {
            Log.i(TAG, "----record,startRecord");
            mRecordService.startRecord();
        }else
            Logger.i(TAG, "----record,RecordService not ok");
    }

    public void stopRecord() {
        if (mRecordService != null) {
            Log.i(TAG, "----record,stopRecord");
            mRecordService.stopRecord();
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RecordService.MESSAGE_START_RECORD:
                    if (mListener != null) {
                        mListener.onStartRecord();
                    }
                    refreshTime();
                    break;
                case RecordService.MESSAGE_STOP_RECORD:
                    mDisposable.dispose();
                    if (mListener != null) {
                        mListener.onStopRecord();
                    }
                    break;
                case RecordService.MESSAGE_NEXT_RECORD:
                    mDisposable.dispose();
                    refreshTime();
                    if (mListener != null) {
                        mListener.onNextRecord();
                    }
                    break;
                case RecordService.MESSAGE_INIT_FAILED:
                    if (mListener != null) {
                        mListener.onRecordFailed(mContext.getString(R.string.init_error));
                    }
                    break;
                case RecordService.MESSAGE_OPEN_CAMERA_FAILED:
                    if (mListener != null) {
                        mListener.onRecordFailed(mContext.getString(R.string.open_camera_failed));
                    }
                    break;
                case RecordService.MESSAGE_STORAGE_NOT_EXIT:
                    if (mListener != null) {
                        mListener.onRecordFailed(mContext.getString(R.string.record_storage_error));
                    }
                    break;
            }
        }
    };


    public void release() {
        mHandler = null;
        stopRecord();
        if (mRecordService != null) {
            mContext.unbindService(mServiceConnection);
            mRecordService.stopSelf();
            mRecordService = null;
            mServiceConnection = null;
        }

        isInit = false;
        sInstance = null;
    }
}
