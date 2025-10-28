package com.softwinner.dvr.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.media.RecordManager;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.util.Logger;



public class StorageReceiver extends BroadcastReceiver {
    private static final String TAG = "StorageReceiver";
    //private static final String PATH = "/storage/usb1";


    @Override
    public void onReceive(Context context, Intent intent) {
        String usb1Path = "/storage/usb1";
        String usb2Path = "/storage/usb2";
        //String usb_path = DVRPreference.getInstance(context).getRecordPath();

        String action = intent.getAction();
        if (Intent.ACTION_MEDIA_EJECT.equals(action)) {//usb disk plug-out
            String path = intent.getData().getPath();
            Logger.i(TAG, "  receiver--ACTION_MEDIA_EJECT  " + path);
            if (usb1Path.equals(path) || usb2Path.equals(path)) {
                Logger.i(TAG, "  receiver--ACTION_MEDIA_EJECT");
                if(RecordManager.getInstance(context).isRecording())
                    RecordManager.getInstance(context).stopRecord();//停止录像

            }
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {//plug-in usb disk
            String path2 = intent.getData().getPath();
            Logger.i(TAG, "  receiver--ACTION_MEDIA_MOUNTED"+path2);
            if (usb1Path.equals(path2) || usb2Path.equals(path2)) {
                Logger.i(TAG, "  receiver--ACTION_MEDIA_MOUNTED");
                //提示
                FileUtils.updateFileList();
                if (DVRPreference.getInstance(context).isBackstageRecord() && (RecordManager.getInstance(context).isRecording()==false) && RecordManager.getInstance(context).isInit())//record option open
                    RecordManager.getInstance(context).startRecord();
            }
        }
    }
}
