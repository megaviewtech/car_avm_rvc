package com.softwinner.dvr.util;

import android.util.Log;



public class Logger {
    private static final String TAG = "DVR";
    private static final int LOG_LEVEL_INFO = 0;
    private static final int LOG_LEVEL_DEBUG = 1;

    private static final int APP_LOG_LEVEL = LOG_LEVEL_INFO;

    public static final void e(String tag, String error) {
        Log.e(TAG, tag + "-error->" + error);
    }

    public static final void d(String tag, String debug) {
        if (APP_LOG_LEVEL > LOG_LEVEL_DEBUG) {
            return;
        }
        Log.d(TAG, tag + "-debug->" + debug);
    }

    public static final void i(String tag, String info) {
        if (APP_LOG_LEVEL > LOG_LEVEL_INFO) {
            return;
        }
        Log.i(TAG, tag + "-info->" + info);
    }
}
