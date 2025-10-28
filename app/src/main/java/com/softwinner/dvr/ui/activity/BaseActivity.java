package com.softwinner.dvr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.softwinner.dvr.common.DVRApplication;

public class BaseActivity extends AppCompatActivity {

    private void checkAppStatus() {
        if(AppStatusManager.getInstance().getAppStatus()==AppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
            //该应用已被回收，执行相关操作（下面有详解）
            if(AppStatusManager.getInstance().getAppStatus()==AppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
                //应用启动入口SplashActivity，走重启流程

                Log.d("checkAppStatus", "-----checkAppStatus: APP_FORCE_KILLED");
                Intent intent = new Intent(this, ChoiceCameraActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAppStatus();
        DVRApplication.getInstance().addActivity(this);


    }

    @Override
    protected void onDestroy() {
        DVRApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
