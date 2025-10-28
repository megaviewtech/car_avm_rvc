package com.softwinner.dvr.common;

import android.app.Application;
import android.os.Handler;

import com.softwinner.dvr.dao.DaoMaster;
import com.softwinner.dvr.dao.DaoSession;
import com.softwinner.dvr.dvr.BaseDVR;
import com.softwinner.dvr.dvr.DVRBuilder;
import com.softwinner.dvr.ui.activity.BaseActivity;
import com.softwinner.dvr.ui.activity.RecordActivity;
import com.softwinner.dvr.util.Logger;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;



public class DVRApplication extends Application {

    private BaseDVR mDVR;
    private static final String TAG = "DVRApplication";

    public static DVRApplication sInstance;

    private List<BaseActivity> mCurActivitys;
    private DaoSession mDaoSession;
    public static final boolean ENCRYPTED = false;
    boolean avmInitFlag;

    @Override
    public void onCreate() {
        Logger.i(TAG, "------onCreate");
        super.onCreate();
        mCurActivitys = new ArrayList<>();
        initDatabase();
        setAvmFlag(false);
        sInstance = this;
    }

    public static DVRApplication getInstance() {
        return sInstance;
    }

    public BaseDVR getDVR() {
        //Logger.i(TAG, "------getDVR");
        if (mDVR == null) {
            mDVR = DVRBuilder.build(this);
        }
        return mDVR;
    }

    public boolean getAvmFlag() {

        return avmInitFlag;
    }

    public void setAvmFlag(boolean avmFlag) {

        avmInitFlag = avmFlag;
    }

    public void reset() {
        for (BaseActivity activity : mCurActivitys) {
            if (activity instanceof RecordActivity) {
                activity.finish();
            }
        }

        Logger.i(TAG, "------reset");
        DVRPreference.getInstance(getApplicationContext()).reset();
        if (mDVR != null) {
            mDVR.release();
            mDVR = null;
        }
    }

    public void sleep() {
        Logger.i(TAG, "------sleep");
/*        if (mDVR != null) {
            mDVR.release();
            mDVR = null;
        }*/
    }

    private void initDatabase() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper
                        (getApplicationContext(), ENCRYPTED ? "dvr-db-encrypted" : "dvr-db");
                Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper
                        .getWritableDb();
                mDaoSession = new DaoMaster(db).newSession();
            }
        });

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void addActivity(BaseActivity activity) {
        if (!mCurActivitys.contains(activity)) {
            mCurActivitys.add(activity);
            Logger.d(TAG, "addActivity  size=" + mCurActivitys.size());
        }
    }

    public void removeActivity(BaseActivity activity) {

        if (mCurActivitys.contains(activity)) {
            mCurActivitys.remove(activity);
            Logger.d(TAG, "removeActivity  size=" + mCurActivitys.size() + "class name = " +
                    activity.getClass().getSimpleName());
        }
    }

    public void exit() {

        Logger.i(TAG, "------exit");

        if (mCurActivitys.size() > 0) {
            for (BaseActivity activity : mCurActivitys) {
                activity.finish();
            }
            mCurActivitys.clear();
        }

        if (mDVR != null) {
            mDVR.release();
            mDVR = null;
        }
    }
}
