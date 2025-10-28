package com.softwinner.dvr.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
//import android.tw.john.TWCan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector;

import com.megaview.avm.AVM;
import com.softwinner.dvr.R;
import com.softwinner.dvr.camera.CameraListener;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dvr.BaseDVR;
import com.softwinner.dvr.dvr.RecordListener;
import com.softwinner.dvr.media.RecordManager;
import com.softwinner.dvr.permission.PermissionsManager;
import com.softwinner.dvr.permission.PermissionsResultAction;
import com.softwinner.dvr.ui.fragment.CameraDrawer;
import com.softwinner.dvr.ui.fragment.FourPreviewFragment;
import com.softwinner.dvr.ui.fragment.OnePreviewFragment;
import com.softwinner.dvr.ui.fragment.TwoPreviewFragment;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;
import static android.view.GestureDetector.*;

import static com.megaview.avm.AVM.PREVIEW_SIZE_1280_720;
import static com.megaview.avm.AVM.VIEW_3D_LEFT_FRONT;
import static com.megaview.avm.AVM.VIEW_3D_RIGHT_FRONT;
import static java.lang.Thread.sleep;
//import static com.softwinner.dvr.ui.fragment.OnePreviewFragment.SetView;

/*enum view
{
    //定义一个枚举类型
    FRONT_ORI_VIEW(0);

    //必须增加一个构造函数,变量,得到该变量的值
    private int  mState=0;
    private view(int value)
    {
        mState=value;
    }
    *//**
     * @return 枚举变量实际返回值
     *//*
    public int getState()
    {
        return mState;
    }
}*/



public class RecordActivity extends BaseActivity implements RecordListener, CameraListener, View
            .OnClickListener,View.OnTouchListener,AVM.OnSignalListener,Camera.ErrorCallback {
        private static String TAG = "RecordActivity";
        public static  int debugFlag = 0;
        public static  int carIndex = 0;
        public static  int carColorIndex = 0;
        public static int captureMode = 0;
        public static int settingActivityFlag = 0;
        public static int videoActivityFlag = 0;
        public static int carSettingFlag=0;
        private ProgressDialog pd;
        private static final int MSG_INIT_SUCCESS = 110;
        private static final int MSG_INIT_FAILED = 111;
        private static final int MSG_NO_STORAGE = 112;
        private static final int MSG_NO_PERMISSION = 113;
        private static final int MSG_ROTATE_COMPLETE = 150;
        private static final int MSG_ROTATE_45_COMPLETE = 151;
        private static final int MSG_ROTATE_COMPLETE2 = 152;
        private static final int MSG_START_MOVE = 160;
        private static final int MSG_STOP_MOVE = 161;
        private static final int MSG_ADAS_FRONT_WARNING = 200;
        private static final int MSG_ADAS_BACK_WARNING = 201;
        private static final int MSG_ADAS_LEFTBACK_WARNING = 202;
        private static final int MSG_ADAS_RIGHTBACK_WARNING = 203;
        private static final int MSG_ADAS_GUARD = 205;//哨兵警告

        //public static TWCan mTWCan = new TWCan();
        public LocationManager locationManager;
        TextView mRecordDurationTV;
        TextView mDebugTV;
        RelativeLayout mRecordDurationView;
        RelativeLayout mRetBtnView;
        RelativeLayout mCaptureBtnView;
        RelativeLayout mPreView;
        RelativeLayout mNextView;
        LinearLayout mFloatBtnView;
        LinearLayout mColorView;
        private RenderThread renderThread;

    private ImageButton mTopViewBtn = null;
    private ImageButton mBottomViewBtn = null;
    private ImageButton mLeftViewBtn = null;
    private ImageButton mRightViewBtn = null;
    private ImageButton mNarrowViewBtn = null;
    private ImageButton mRotateBtn = null;
    private ImageButton mSettingBtn = null;

    private ImageButton mRetBtn = null;

    private ImageButton mCaptureBtn = null;

    private ImageButton mBlackBtn = null;
    private ImageButton mWhiteBtn = null;
    private ImageButton mBlueBtn = null;
    private ImageButton mRedBtn = null;
    private ImageButton mGrayBtn = null;
    private ImageButton mYellowBtn = null;
    private ImageButton mBlue2Btn = null;
    private ImageButton mGreenBtn = null;
    private ImageButton mPinkBtn = null;
    private ImageButton mBrownBtn = null;

    private ImageButton mPreBtn = null;
    private ImageButton mNextBtn = null;

    private BaseDVR mBaseDvr;

    private boolean isPaused = true;
    private boolean isExit = false;
    private Fragment mFragment;
    private String mFragmentTag;

    public int angle=0;
    public int currentView=0;
    public int preView=0;
    // 环视触摸功能
    private float preX=100,preY=100;
    private int fIndex=0,bIndex=0,lIndex=1,rIndex=1,nIndex=0;

    private GestureDetector mGestureDetector = null;
    public int currentSpeed = 0;

    //
    Timer timer;
    TimerTask task;

    BroadcastReceiverHelper  rhelper;
    BroadcastAccReceiverHelper rhelperAcc;

    @Override
    public void onSignal(int msg, int param1, int param2, int param3) {
        Logger.i(TAG, "---onSignal new,"+msg+","+param1+","+param2+","+param3);

        if(msg>=200) {
            Message message = Message.obtain();
            message.what = msg; // 消息类型或标识
            message.arg1 = param1; // 整型参数
            message.arg2 = param2; // 整型参数
            mHandler.sendMessage(message);
        }else
            mHandler.sendEmptyMessage(msg);

    }

    //add by liugang,2020-7-7
    @Override
    public void onError(int error, Camera camera) {

        switch(error) {
            case  Camera.CAMERA_ERROR_EVICTED:
                Log.i(TAG, "onError: CAMERA_ERROR_EVICTED");
                break;

            case  Camera.CAMERA_ERROR_SERVER_DIED:
                Log.i(TAG, "onError: CAMERA_ERROR_SERVER_DIED");
                if (DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
                    Logger.i(TAG, "---stopRecord");
                    stopRecord();
                }

                mBaseDvr.mFrontCamera.stopPreview();

                while(!(mBaseDvr.mFrontCamera.openCamera())) {
                    try {

                        sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                setCameraErrorListener();

                try {
                    mBaseDvr.mFrontCamera.startPreview(CameraDrawer.surfaceTexture);
                    //DVRApplication.getInstance().getDVR().frontCameraStartPreview(CameraDrawer.surfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed, Toast.LENGTH_SHORT)
                    //        .show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    //Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed2, Toast.LENGTH_SHORT)
                    //        .show();
                    DVRApplication.getInstance().exit();
                }

                if (DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
                    Logger.i(TAG, "---clickRecordBtn");
                    clickRecordBtn();
                }

                break;

            case  Camera.CAMERA_ERROR_UNKNOWN:
                Log.i(TAG, "onError: CAMERA_ERROR_UNKNOWN");
                break;
        }
    }

    private class gesturelistener implements OnGestureListener{

        public boolean onDown(MotionEvent e) {
            return false;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onScroll(MotionEvent e1,MotionEvent e2,float distanceX,float distanceY){


            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            return false;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Logger.i(TAG, "onTouchEvent2"+motionEvent.getAction());
        //preX
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Logger.i(TAG, "ACTION_DOWN");
            //preX = motionEvent.getX();
            //preY = motionEvent.getY();
        }


        if (mGestureDetector != null) {
            Logger.i(TAG, "mGestureDetector");
            mGestureDetector.onTouchEvent(motionEvent);
        }
        //mStandby.delay();
        return true;
    }

    void setCameraErrorListener()
    {
        mBaseDvr.mFrontCamera.mCamera.setErrorCallback(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_SUCCESS:
                    Logger.i(TAG, "dvr init success");
                    //setCameraErrorListener();
                    //initRecordManager();
                    loadFragment();
                    freshLockView();
                    if (DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
                        Logger.i(TAG, "---clickRecordBtn");
                        clickRecordBtn();
                    }
                    break;
                case MSG_INIT_FAILED:
                    Logger.e(TAG, "dvr init failed");
                    Toast.makeText(getApplicationContext(), R.string.open_camera_failed, Toast
                            .LENGTH_SHORT).show();
                    break;
                case MSG_NO_STORAGE:
                    mTopViewBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.storage_not_exit, Toast
                            .LENGTH_LONG).show();
                    break;
                case MSG_NO_PERMISSION:
                    mTopViewBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.no_storage_permission, Toast
                            .LENGTH_LONG).show();
                    break;

                case MSG_ROTATE_COMPLETE:
                    try {

                        sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AVM.avmSetCmd(AVM.CMD_NO_NAME1,60);//设置旋转速度

                    //AVM.avmSetCmd(AVM.CMD_VIEW,AVM.VIEW_3D_L);
                    AVM.avmSetCmd(AVM.CMD_VIEW,AVM.VIEW_SINGLE_2DF);

                    //AVM.avmSetCmd(AVM.CMD_RESET,0);
                    //AVM.angle = 0;


                    break;

                case MSG_ROTATE_45_COMPLETE:
                    if(AVM.rotateFlag == 1) {



                        if (AVM.angle % 360 == 270) {
                            AVM.rotateFlag = 0;

                            try {

                                sleep(1000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AVM.avmSetCmd(AVM.CMD_VIEW,AVM.VIEW_SINGLE_F);



                        }
                        else {
                            if(AVM.angle % (45*2) == 0) {

                                try {

                                    sleep(800);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                AVM.angle += (45*2);
                                AVM.avmSetCmd(AVM.CMD_3D_ANGLE, AVM.angle);
                            }
                        }
                    }


                    break;

                case MSG_START_MOVE:

                    //AVM.moving_flag = 1;
                    break;

                case MSG_STOP_MOVE:

                    //AVM.moving_flag = 0;
                    break;

                case MSG_ADAS_FRONT_WARNING:

                    Logger.d(TAG, "-----test,MSG_ADAS_FRONT_WARNING:"+msg.arg1+"cm");
                    if(msg.arg1<150)
                        ;//audio tip
                break;

                case MSG_ADAS_BACK_WARNING:
                    Logger.d(TAG, "-----test,MSG_ADAS_BACK_WARNING:"+msg.arg1+"cm");

                    if(msg.arg1<150)
                        ;//audio tip
                    break;
                case MSG_ADAS_LEFTBACK_WARNING:

                    Logger.d(TAG, "-----test,MSG_ADAS_LEFTBACK_WARNING:"+msg.arg1+"cm");
                    if(msg.arg1<500)
                        ;//audio tip
                    break;
                case MSG_ADAS_RIGHTBACK_WARNING:
                    Logger.d(TAG, "-----test,MSG_ADAS_RIGHTBACK_WARNING:"+msg.arg1+"cm");
                    if(msg.arg1<500)
                        ;//audio tip
                    break;

                case MSG_ADAS_GUARD:
                    Logger.d(TAG, "-----test,MSG_ADAS_GUARD:"+msg.arg1);

                    break;
                default:
                    break;
            }
        }
    };




    /**
     * 判断应用是否在后台
     * @param context 上下文
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onRestart(){

        Logger.d(TAG, "-----test,--------onRestart------");


        super.onRestart();

    }



    @Override
    public void onStart(){

/*        try {

            sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.d(TAG, "-----test,--------onStart------");*/
    Logger.d(TAG, "-----test,--------onStart------");

    super.onStart();



    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //AppStatusManager.getInstance().setAppStatus(AppStatusManager.AppStatusConstant.APP_NORMAL);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        //Log.i(TAG, "---onCreate: 1");
        requestPermission();
        //Log.i(TAG, "---onCreate: 2");
        setContentView(R.layout.activity_record);


        File file = new File(this.getFilesDir().getAbsolutePath()+ File.separator+"common");

        //if not exists then copy,also have a new version assets need copy
        if (!file.exists()) {
            stopTimer();
            copyAssetsDir(this, "common");
            copyAssetsDir(this, "pic.bmp");

            CameraDrawer.assets_flag = 1;
            Log.i(TAG, "---onCreate: assets_flag 1");
            File carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car");
            if (!carDir.exists())
                carDir.mkdirs();

        }else
            CameraDrawer.assets_flag = 0;


        //创建sdcard/avm目录
        File avmDir = new File (Environment.getExternalStorageDirectory()+File.separator+"avm");
        if (!avmDir.exists())
            avmDir.mkdirs();


/*        File carDir3 = new File (this.getExternalFilesDir("")+File.separator+".car");
        carDir3.mkdirs();*/

        //demo flag,2020-7-5,liugang
        if(DVRPreference.getInstance(this).haveWaterMark())
            AVM.avmSetCmd(13,1);//set demo mode

        //cold start mode,write 1 to stop single camera reverse and switch to 360 mode,2020-9-17 by liugang
        writeCarReverse("/sys/class/car_reverse/start360","1");

        init();
/*        if(!mTWCan.onCreate(this, new TWCan.ICallback() {
            @Override
            public void onReverse(int r) {
                //mDebugTV.setText("onReverse:"+r);
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;

                if((debugFlag == 0 && captureMode==0 && settingActivityFlag==0 && videoActivityFlag==0 && carSettingFlag==0)) {
                    if (r == 1) {
                        stopTimer();

                        if (isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                            Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                        }

                        if (bIndex == 0)
                            currentView = 1;
                        else
                            currentView = 9;
                        AVM.avmSetCmd(0, currentView);
                        preView = currentView;

                        mFloatBtnView.setVisibility(View.VISIBLE);
                        mFloatBtnView.bringToFront();
                        mRetBtnView.setVisibility(View.GONE);

                        mTopViewBtn.setImageResource(R.drawable.top_gray);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_selected);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);

                        AVM.avmSetCmd(1, 1);


                    } else {

                        // if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())!= true) {
                        //moveTaskToBack(true);
                        //}

                        if (fIndex == 0)
                            currentView = 0;
                        else
                            currentView = 8;
                        AVM.avmSetCmd(0, currentView);
                        preView = currentView;

                        mFloatBtnView.setVisibility(View.VISIBLE);
                        mFloatBtnView.bringToFront();
                        mRetBtnView.setVisibility(View.GONE);

                        mTopViewBtn.setImageResource(R.drawable.top_selected);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);


                        AVM.avmSetCmd(1, 0);


                        startHideTimer(2000);
                    }
                }
            }



            @Override
            public void onMCU(int speed, int rev) {
                //mDebugTV.setText("mcu speed:"+speed);
            }

            @Override
            public void onAcc(boolean acc){
                Log.i(TAG, "---onAcc: 2");
*//*                mDebugTV.setText("onAcc:"+acc);
                if(acc == false) {
                    //stopRecord();
                    Log.d(TAG, "onAcc:false");

                }else{
                    Log.d(TAG, "onAcc:true");
                    //finish();
                    //System.exit(0);
                }*//*

            }

            @Override
            public void onLRReverse(int lr) {
                //mDebugTV.setText("LRReverse:"+lr);

                //0->off   1->right on  2->left on   3-> all on
                if(DVRApplication.getInstance().getAvmFlag() == false )
                    return;

                if(DVRPreference.getInstance(getApplicationContext()).isLR()==false && (lr==1 || lr ==2) )
                    return;

                if(DVRPreference.getInstance(getApplicationContext()).isEmergency()==false && (lr==3) )
                    return;

                if(lr == 0){
                    startHideTimer(1000);
                }else {

                    if (currentSpeed < 30000) {
                        if (isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                            Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                        }




                        if (lr == 2 && (debugFlag == 0 && captureMode==0 && settingActivityFlag==0 && videoActivityFlag==0 && carSettingFlag==0)) {//left
                            stopTimer();
                            if (lIndex == 0)
                                currentView = 2;
                            else
                                currentView = 6;
                            AVM.avmSetCmd(0, currentView);
                            preView = currentView;

                            mFloatBtnView.setVisibility(View.VISIBLE);
                            mFloatBtnView.bringToFront();
                            mRetBtnView.setVisibility(View.GONE);

                            mTopViewBtn.setImageResource(R.drawable.top_gray);
                            mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                            mLeftViewBtn.setImageResource(R.drawable.left_selected);
                            mRightViewBtn.setImageResource(R.drawable.right_gray);
                            mNarrowViewBtn.setImageResource(R.drawable.lr_gray);



                        } else if (lr == 1 && (debugFlag == 0 && captureMode==0 && settingActivityFlag==0 && videoActivityFlag==0 && carSettingFlag==0)) {//right
                            stopTimer();
                            if (rIndex == 0)
                                currentView = 3;
                            else
                                currentView = 7;
                            AVM.avmSetCmd(0, currentView);
                            preView = currentView;

                            mFloatBtnView.setVisibility(View.VISIBLE);
                            mFloatBtnView.bringToFront();
                            mRetBtnView.setVisibility(View.GONE);

                            mTopViewBtn.setImageResource(R.drawable.top_gray);
                            mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                            mLeftViewBtn.setImageResource(R.drawable.left_gray);
                            mRightViewBtn.setImageResource(R.drawable.right_selected);
                            mNarrowViewBtn.setImageResource(R.drawable.lr_gray);


                        } else if (lr == 3) {
                            stopTimer();
                        }
                    }
                }
                //AVM.avmSetCmd(6,lr);//set turn lamps

            }

            @Override
            public void onGps(int speed) {
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;

                //AVM.avmSetCmd(2,speed*3600);
                //mDebugTV.setText("gps speed:"+speed);
            }

            @Override
            public void onFRadar(int l, int cl, int cr, int r) {


                if(l == 255)
                    l = 0;//很远
                else if(l == 16)
                    l = 1;//远
                else if(l == 50)
                    l = 2;//近
                else if(l == 150)
                    l = 3;//很近
                else
                    l = 0;

                if(cl == 255)
                    cl = 0;
                else if(cl == 16)
                    cl = 1;
                else if(cl == 50)
                    cl = 2;
                else if(cl == 150)
                    cl = 3;
                else
                    cl =0;


                if(cr == 255)
                    cr = 0;
                else if(cr == 16)
                    cr = 1;
                else if(cr == 50)
                    cr = 2;
                else if(cr == 150)
                    cr = 3;
                else
                    cr=0;


                if(r == 255)
                    r = 0;
                else if(r == 16)
                    r = 1;
                else if(r == 50)
                    r = 2;
                else if(r == 150)
                    r = 3;
                else
                    r=0;

                int frontRadar = (l&0x0f) | (cl&0x0f << 4) | (cr&0x0f << 8) | (r&0x0f << 12) | 0x00000;
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;



                //mDebugTV.setText("onFRadar:"+l+cl+cr+r);
                AVM.avmSetCmd(5,frontRadar);
            }

            @Override
            public void onRRadar(int l, int cl, int cr, int r) {

                if(l == 255)
                    l = 0;//很远
                else if(l == 16)
                    l = 1;//远
                else if(l == 50)
                    l = 2;//近
                else if(l == 150)
                    l = 3;//很近
                else
                    l = 0;

                if(cl == 255)
                    cl = 0;
                else if(cl == 16)
                    cl = 1;
                else if(cl == 50)
                    cl = 2;
                else if(cl == 150)
                    cl = 3;
                else
                    cl =0;


                if(cr == 255)
                    cr = 0;
                else if(cr == 16)
                    cr = 1;
                else if(cr == 50)
                    cr = 2;
                else if(cr == 150)
                    cr = 3;
                else
                    cr=0;


                if(r == 255)
                    r = 0;
                else if(r == 16)
                    r = 1;
                else if(r == 50)
                    r = 2;
                else if(r == 150)
                    r = 3;
                else
                    r=0;

                int RearRadar = (l&0x0f) | (cl&0x0f << 4) | (cr&0x0f << 8) | (r&0x0f << 12) | 0x10000;
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;
                //mDebugTV.setText("onRRadar:"+l+cl+cr+r);

                //0->255 1->16 2->50 3->100  4->150
                AVM.avmSetCmd(5,RearRadar);
            }

            @Override
            public void onDoor(boolean fl, boolean fr, boolean bl, boolean br, boolean b, boolean f) {
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;
                //true->open  false->close
                int doors=0;
                if(fl)
                    doors = doors | 0x1;

                if(fr)
                    doors = doors | 0x2;

                if(bl)
                    doors = doors | 0x4;

                if(br)
                    doors = doors | 0x8;

                //mDebugTV.setText("onDoor:"+fl+fr+br+b);
                 //AVM.avmSetCmd(4,0xff);
            }

            @Override
            public void onAngle(int angle) {
                if(DVRApplication.getInstance().getAvmFlag() == false)
                    return;
                AVM.avmSetCmd(3,(int)(-angle*(32.0/780.0)));
                //mDebugTV.setText("onAngle:"+(int)(angle*(32.0/780.0)));
            }
        }, true)){

        }*/

        mGestureDetector = new GestureDetector(new gesturelistener());
        AVM.setOnSignalListener(this);


        rhelper=new BroadcastReceiverHelper(this);
        rhelper.registerAction("com.percherry.roundadas.ALLROUND_LOOKING_3D");

        rhelperAcc=new BroadcastAccReceiverHelper(this);
        rhelperAcc.registerAction("com.percherry.roundadas.LOOK_AROUND_3D_POWER");

        //rhelper.registerAction("com.megaview.avm");
        //Log.d(TAG, "onStart: "+"registerAction");

        startLocation(this);
        //startHideTimer(20000);//hide after 10 seconds
        Logger.i(TAG, "-------------onCreate");


        //copyFile(this.getFilesDir().getAbsolutePath()+ File.separator+"pic.jpg","/sdcard/pic.jpg");
        renderThread = new RenderThread();
        renderThread.mContext = this;
        renderThread.start();
    }

    public void startHideTimer(int timeLong){


        //timer task
        if(timer!= null) {

            timer.cancel();
            if(task!=null)
                task.cancel();
        }

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())!= true) {
                    moveTaskToBack(true);
                }
            }
        };

        timer.schedule(task,timeLong);

    }

    public void stopTimer(){

        //timer task
        if(timer!= null) {

            timer.cancel();
            if(task!=null)
                task.cancel();
        }

    }

    private void init() {
        Logger.i(TAG, "init");
        mRecordDurationTV = (TextView) findViewById(R.id.recordDurationTV);
        mRecordDurationView = (RelativeLayout) findViewById(R.id.recordDurationRL);

        mDebugTV = (TextView) findViewById(R.id.testTV);

        mTopViewBtn = (ImageButton) findViewById(R.id.topViewBtn);
        mBottomViewBtn = (ImageButton) findViewById(R.id.bottomViewBtn);
        mLeftViewBtn = (ImageButton) findViewById(R.id.leftViewBtn);
        mRightViewBtn = (ImageButton) findViewById(R.id.rightViewBtn);
        mNarrowViewBtn = (ImageButton) findViewById(R.id.narrowViewBtn);
        mRotateBtn = (ImageButton) findViewById(R.id.rotateBtn);
        mSettingBtn = (ImageButton) findViewById(R.id.settingBtn);

        mRetBtn = (ImageButton) findViewById(R.id.retBtn);

        mCaptureBtn = (ImageButton) findViewById(R.id.captureBtn);

        mBlackBtn = (ImageButton) findViewById(R.id.blackBtn);
        mWhiteBtn = (ImageButton) findViewById(R.id.whiteBtn);
        mBlueBtn = (ImageButton) findViewById(R.id.blueBtn);
        mRedBtn = (ImageButton) findViewById(R.id.redBtn);
        mGrayBtn = (ImageButton) findViewById(R.id.grayBtn);
        mYellowBtn = (ImageButton) findViewById(R.id.yellowBtn);

        mBlue2Btn = (ImageButton) findViewById(R.id.blue2Btn);
        mGreenBtn = (ImageButton) findViewById(R.id.greenBtn);
        mPinkBtn = (ImageButton) findViewById(R.id.pinkBtn);
        mBrownBtn = (ImageButton) findViewById(R.id.brownBtn);


        mPreBtn = (ImageButton) findViewById(R.id.PreBtn);
        mNextBtn = (ImageButton) findViewById(R.id.NextBtn);

        mRetBtnView = (RelativeLayout) findViewById(R.id.retCtrl);
        mFloatBtnView = (LinearLayout) findViewById(R.id.recordCtl);
        mCaptureBtnView = (RelativeLayout) findViewById(R.id.captureCtrl);
        mColorView = (LinearLayout) findViewById(R.id.colorCtl);
        mPreView = (RelativeLayout) findViewById(R.id.PreCtrl);
        mNextView = (RelativeLayout) findViewById(R.id.NextCtrl);


        mTopViewBtn.setOnClickListener(this);
        mBottomViewBtn.setOnClickListener(this);
        mLeftViewBtn.setOnClickListener(this);
        mRightViewBtn.setOnClickListener(this);
        mNarrowViewBtn.setOnClickListener(this);
        mRotateBtn.setOnClickListener(this);
        mSettingBtn.setOnClickListener(this);

        mRetBtn.setOnClickListener(this);

        mCaptureBtn.setOnClickListener(this);

        mBlackBtn.setOnClickListener(this);
        mWhiteBtn.setOnClickListener(this);
        mBlueBtn.setOnClickListener(this);
        mRedBtn.setOnClickListener(this);
        mGrayBtn.setOnClickListener(this);
        mYellowBtn.setOnClickListener(this);

        mBlue2Btn.setOnClickListener(this);
        mGreenBtn.setOnClickListener(this);
        mPinkBtn.setOnClickListener(this);
        mBrownBtn.setOnClickListener(this);

        mPreBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        mTopViewBtn.setImageResource(R.drawable.top_selected);
        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
        mLeftViewBtn.setImageResource(R.drawable.left_gray);
        mRightViewBtn.setImageResource(R.drawable.right_gray);
        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);


/*        if (PermissionsManager.getInstance().hasPermission(getApplicationContext(), Manifest
                .permission.CAMERA)) {
            initDVR();

        }else{

            for(int i=0;i<5000;i++) {
                if (PermissionsManager.getInstance().hasPermission(getApplicationContext(), Manifest
                        .permission.CAMERA)) {
                    initDVR();
                    break;
                }

                try {

                    sleep(50);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/

        mHandler.sendEmptyMessage(MSG_INIT_SUCCESS);
    }

    private void initRecordManager() {
        RecordManager.getInstance(getApplicationContext()).registerListener(this);
        if (RecordManager.getInstance(getApplicationContext()).isInit()) {
            //mTopViewBtn.setEnabled(true);
        } else {
            //mTopViewBtn.setEnabled(false);
            RecordManager.getInstance(getApplicationContext()).init();
        }

        if (RecordManager.getInstance(getApplicationContext()).isRecording()) {
            //mTopViewBtn.setImageResource(R.drawable.ic_fiber_manual_record_red_600_48dp);
            mRecordDurationView.setVisibility(View.VISIBLE);
            mRecordDurationView.bringToFront();
        } else {
            //mTopViewBtn.setImageResource(R.drawable.ic_fiber_manual_record_grey_400_48dp);
            mRecordDurationView.setVisibility(View.GONE);
        }
    }

    private void freshLockView() {
        if (DVRApplication.getInstance().getDVR().isLock()) {
            //mBottomViewBtn.setImageResource(R.drawable.ic_lock_outline_red_a400_48dp);
        } else {
           // mBottomViewBtn.setImageResource(R.drawable.ic_lock_open_grey_400_48dp);
        }
    }


    private void startRecord() {

        if (PermissionsManager.getInstance().hasPermission(getApplicationContext(), Manifest
                .permission.WRITE_EXTERNAL_STORAGE)) {
            if (StorageUtils.getAvailableSpace(DVRPreference.getInstance(getApplicationContext())
                    .getRecordPath()) != -1) {

                RecordManager.getInstance(getApplicationContext()).startRecord();
            } else {

            }
        } else {

            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new
                    PermissionsResultAction() {
                @Override
                public void onGranted() {
                    if (StorageUtils.getAvailableSpace(DVRPreference.getInstance
                            (getApplicationContext()).getRecordPath()) != 1) {
                        RecordManager.getInstance(getApplicationContext()).startRecord();
                    } else {
                        mHandler.sendEmptyMessage(MSG_NO_STORAGE);
//                        Toast.makeText(RecordActivity.this, R.string.storage_not_exit, Toast
//                                .LENGTH_LONG).show();
//                        mTopViewBtn.setEnabled(true);
                    }
                }

                @Override
                public void onDenied(String permission) {
                    mHandler.sendEmptyMessage(MSG_NO_PERMISSION);
                }
            });
        }
    }

    private void stopRecord() {
        RecordManager.getInstance(getApplicationContext()).stopRecord();
        Log.i(TAG, "----record,activity stopRecord");
    }

    private void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void startFileActivity() {
      //  if (videoFlag) { videoFlag =false;
            FileUtils.updateFileList();

       // }
        startActivity(new Intent(this, FileActivity.class));
    }

    private void startAboutActivity() {

        startActivity(new Intent(this, AboutActivity.class));
    }
    //add by liugang
    private void startDebugActivity() {

        //startActivity(new Intent(this, DebugActivity.class));
        startActivity(new Intent(this, DebugActivity.class));
    }

    @Override
    protected void onResume() {

/*        try {

            sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Logger.d(TAG, "-----test,--------onResume------");


        super.onResume();
        isPaused = false;
        //init();

        //enter capture mode,liugang 2020-7-2
        if(debugFlag == 1)
        {

            mFloatBtnView.setVisibility(View.GONE);
            mCaptureBtnView.setVisibility(View.VISIBLE);
            mRetBtnView.setVisibility(View.VISIBLE);
            mCaptureBtnView.bringToFront();
            AVM.avmSetCmd(0,4);
            debugFlag = 0;

        }

        if(captureMode == 1)
        {
            //back to normal mode
            mFloatBtnView.setVisibility(View.VISIBLE);
            mFloatBtnView.bringToFront();
            mRetBtnView.setVisibility(View.GONE);
            mColorView.setVisibility(View.GONE);
            mPreView.setVisibility(View.GONE);
            mNextView.setVisibility(View.GONE);
            mCaptureBtnView.setVisibility(View.GONE);

            AVM.avmSetCmd(0,preView);

            captureMode = 0;
        }
       // if(mBaseDvr.mFrontCamera.mCamera != null)
            //mBaseDvr.mFrontCamera.mCamera.startPreview();
            //mBaseDvr.mFrontCamera.startRender();

/*        if(RecordManager.getInstance(getApplicationContext()).isRecording())
        {


        }else{

        }*/

        if(settingActivityFlag == 1)
        {

            if (DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
                if(!RecordManager.getInstance(getApplicationContext()).isRecording())
                {
                    Logger.i(TAG, "---clickRecordBtn");
                    clickRecordBtn();
                }

            }else{

                if(RecordManager.getInstance(getApplicationContext()).isRecording())
                {
                    //Logger.i(TAG, "---clickRecordBtn");
                    stopRecord();
                }
            }
            settingActivityFlag = 0;
        }

        if(videoActivityFlag == 1) {
            if (DVRPreference.getInstance(getApplicationContext()).isBackstageRecord() && RecordManager.getInstance(getApplicationContext()).isRecording() == false)
                clickRecordBtn();

            videoActivityFlag = 0;
        }

/*        mBaseDvr.mFrontCamera.stopPreview();
        //mBaseDvr.mFrontCamera.startRender();

        mBaseDvr.mFrontCamera.openCamera();

        try {
            mBaseDvr.mFrontCamera.startPreview(CameraDrawer.surfaceTexture);
            //DVRApplication.getInstance().getDVR().frontCameraStartPreview(CameraDrawer.surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed, Toast.LENGTH_SHORT)
            //        .show();
        } catch (Exception e1) {
            e1.printStackTrace();
            //Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed2, Toast.LENGTH_SHORT)
            //        .show();
            DVRApplication.getInstance().exit();
        }*/

/*        try {

            sleep(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        Logger.d(TAG, "-----test,--------onResume out------");

    }

    @Override
    protected void onPause() {
        isPaused = true;
        //removeFragment();
        super.onPause();
      //  if(mBaseDvr.mFrontCamera.mCamera != null)
            //mBaseDvr.mFrontCamera.mCamera.stopPreview();
            //mBaseDvr.mFrontCamera.stopRender();
        Logger.d(TAG, "--------onPause------");
    }

    @Override
    protected void onDestroy() {
        RecordManager.getInstance(getApplicationContext()).unRegisterListener();
        mBaseDvr.setCameraListener(null);
        mBaseDvr = null;
        unregisterReceiver(rhelper);
        super.onDestroy();
        Logger.d(TAG, "------onDestroy------");
        if (isExit) {
            RecordManager.getInstance(getApplicationContext()).release();
            DVRApplication.getInstance().exit();
        }
    }

    /**
     * 根据当前状态决定加载哪个Fragment
     */
    private void loadFragment() {
        Logger.i(TAG, "loadFragment");
        switch (1) {
            case 1:
                mFragmentTag = "record_fragment";
                //mFragment = getSupportFragmentManager().findFragmentByTag(mFragmentTag);
                if (mFragment == null) {
                    CameraDrawer.rootPath=this.getFilesDir().getAbsolutePath()+ File.separator;

                    mFragment = OnePreviewFragment.newInstance();
                }
                break;
            case 2:
                mFragmentTag = "2record_fragment";
                //mFragment = getSupportFragmentManager().findFragmentByTag(mFragmentTag);
                if (mFragment == null) {
                    mFragment = TwoPreviewFragment.newInstance();
                }
                break;
            case 4:
                mFragmentTag = "4record_fragment";
                //mFragment = getSupportFragmentManager().findFragmentByTag(mFragmentTag);
                if (mFragment == null) {
                    mFragment = FourPreviewFragment.newInstance();
                }
                break;
        }

        if (mFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.previewContent,
                    mFragment, mFragmentTag).commit();
        }
    }

    private void removeFragment() {
        getSupportFragmentManager().beginTransaction().remove(mFragment);
        mFragment = null;
    }

    @Override
    public void onInitFinished() {
        //mTopViewBtn.setEnabled(true);
    }

    @Override
    public void onStartRecord() {
        //mTopViewBtn.setImageResource(R.drawable.ic_fiber_manual_record_red_600_48dp);
        mRecordDurationView.setVisibility(View.VISIBLE);
        mRecordDurationView.bringToFront();
        //mTopViewBtn.setEnabled(true);
        //Toast.makeText(this, R.string.start_record, Toast.LENGTH_SHORT).show();

        //Log.i(TAG, "----record,onStartRecord: ");
    }

    @Override
    public void onRecordFailed(String reason) {
        //mTopViewBtn.setEnabled(true);
        //Log.i(TAG, "----record,onRecordFailed: ");
        if (isPaused) {
            return;
        }
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopRecord() {
        //Log.i(TAG, "----record,onStopRecord: ");
        if (isPaused) {
            return;
        }
        DVRApplication.getInstance().getDVR().setLock(false);
        //mBottomViewBtn.setImageResource(R.drawable.ic_lock_open_grey_400_48dp);
        //mTopViewBtn.setImageResource(R.drawable.ic_fiber_manual_record_grey_400_48dp);
        mRecordDurationView.setVisibility(View.GONE);
       //mTopViewBtn.setEnabled(true);
        //Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextRecord() {

    }

    @Override
    public void onFreshTime(String time) {
        if (isPaused) {
            return;
        }

        //time = time.substring(3);
        mRecordDurationTV.setText("REC");

    }


    private void requestPermission() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new
                PermissionsResultAction() {
            @Override
            public void onGranted() {
                Logger.i(TAG, "--PermissionsManager-requestPermission----onGranted");
                Toast.makeText(getApplicationContext(), R.string.message_granted, Toast
                        .LENGTH_SHORT).show();
                //initDVR();//deleted by liugang,2020-7-8
            }

            @Override
            public void onDenied(String permission) {
                Logger.i(TAG, "--PermissionsManager-requestPermission----onDenied");
                String message = String.format(Locale.getDefault(), getString(R.string
                        .message_denied), permission);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initDVR() {
        Logger.i(TAG, "initDVR");
        mBaseDvr = DVRApplication.getInstance().getDVR();
        mBaseDvr.setCameraListener(this);

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

    @Override
    public void onStartPreviewFailed() {
        Toast.makeText(RecordActivity.this, R.string.preview_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOpenCameraFailed() {
        Toast.makeText(getApplicationContext(), R.string.open_camera_failed, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onTakePictureSuccess() {

        //add by liugang 2020-7-2
        int debugFlag2 = 1;//1 debug mode,2021-06-09

        if(debugFlag2  == 1)
            AVM.avmSetCmd(10,0);//splite picture

        Toast.makeText(getApplicationContext(), R.string.take_picture_success, Toast
                .LENGTH_SHORT).show();

        pd.dismiss();

        if(debugFlag2  == 1) {
            startActivity(new Intent(this, LenActivity.class));
            captureMode = 1;
        }

    }

    @Override
    public void onTakePictureFailed() {
        pd.dismiss();
        Toast.makeText(getApplicationContext(), R.string.take_picture_failed, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onBackPressed() {

/*        new AlertDialog.Builder(RecordActivity.this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(R.string.exit_app_alert).setPositiveButton(R.string.sure, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isExit = true;
                dialog.dismiss();
                //finish();
                moveTaskToBack(true);
            }
        }).setNegativeButton(R.string.backstage_run, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isExit = false;
                dialog.dismiss();
                moveTaskToBack(true);
                //finish();
            }
        }).show();*/

        moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
            RecordManager.getInstance(getApplicationContext()).stopRecord();
        }


    }

    private void showPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(this,mSettingBtn);
        popupMenu.inflate(R.menu.main);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.car:
                        mRetBtnView.setVisibility(View.VISIBLE);
                        mRetBtnView.bringToFront();

                        mColorView.setVisibility(View.VISIBLE);
                        mColorView.bringToFront();

                        mPreView.setVisibility(View.VISIBLE);
                        mPreView.bringToFront();

                        mNextView.setVisibility(View.VISIBLE);
                        mNextView.bringToFront();

                        mFloatBtnView.setVisibility(View.GONE);
                        currentView = AVM.VIEW_3D_ALL_CAR;
                        AVM.avmSetCmd(0,currentView);

                        carSettingFlag = 1;

                        return true;
                    case R.id.files:
                        //Toast.makeText(getApplicationContext(),"Option 2",Toast.LENGTH_SHORT).show();
                        clickVideoFileBtn();
                        return true;
/*                    case R.id.record:
                        //Toast.makeText(getApplicationContext(),"Option 3",Toast.LENGTH_SHORT).show();
                        clickRecordBtn();
                        return true;*/
                    case R.id.debug:
                        //Toast.makeText(getApplicationContext(),"Option 3",Toast.LENGTH_SHORT).show();
/*                        mFloatBtnView.setVisibility(View.GONE);
                        mCaptureBtnView.setVisibility(View.VISIBLE);
                        mCaptureBtnView.bringToFront();
                        AVM.avmSetCmd(0,4);*/
                        //Logger.i(TAG, "----startDebugActivity1:");
                        startDebugActivity();
                        //Logger.i(TAG, "----startDebugActivity2:");
                        return true;
                    case R.id.setting:
                        //Toast.makeText(getApplicationContext(),"Option 3",Toast.LENGTH_SHORT).show();

                        clickSettingBtn();


                        return true;

                    case R.id.about:
                        startAboutActivity();


                        return true;

                    default:
                        //do nothing
                }

                return false;
            }
        });
        popupMenu.show();
    }

            @Override
        public void onClick(View v) {

        //String carName="bentley_mulsanne_2011";
        String carNameArray[]={"lamborghini_gallardo_2018","audi_a4_2016","nissan_xtrail_2017"};
        File carDir;
        int carIndexTemp;
        int carColorIndexTemp;

        int cameraIndex = 0;
        int itemIndex = 3;
        int direction = 0;//0:sub   1:add

        switch (v.getId()) {
            case R.id.topViewBtn:
                //AVM.SetCmd(AVM.CMD_SHOW_BEV_LINES,1);
                //AVM.SetCmd(AVM.CMD_LINE_ANGLE,1);

                AVM.avmSetCmd(0,0);
                //AVM.avmSetCmd(125,cameraIndex<<16 | itemIndex<<8 | direction);


                break;
            case R.id.bottomViewBtn:
                //AVM.SetCmd(AVM.CMD_LINE_ANGLE,-1);

                AVM.avmSetCmd(0,1);
                //AVM.avmSetCmd(126,1);

                break;
            case R.id.leftViewBtn:
                //AVM.SetCmd(AVM.CMD_LINE_OFFSET,1);

                //AVM.avmSetCmd(34,1);
                AVM.avmSetCmd(0,AVM.VIEW_3D_L);
                //AVM.avmSetCmd(0,68);
                AVM.avmSetCmd(AVM.CMD_TURN_LAMP,1);

                break;
            case R.id.rightViewBtn:
                //AVM.SetCmd(AVM.CMD_LINE_OFFSET,-1);
                //AVM.avmSetCmd(34,-1);
                AVM.avmSetCmd(0,AVM.VIEW_3D_R);
                //AVM.avmSetCmd(0,69);

                AVM.avmSetCmd(AVM.CMD_TURN_LAMP,2);
                break;
            case R.id.narrowViewBtn:
                //AVM.SetCmd(AVM.CMD_LINE_NEXT,1);
                AVM.avmSetCmd(0,AVM.VIEW_NARROW_F2);

                //int doorStatus = 0x0f; //车门全开
                //低4位为车门状态[ 0 0 0 0   x    x    x    x ]
                //                          右后  左后 右前 左前         0 车门关闭，1 车门打开
                //AVM.avmSetCmd(AVM.CMD_DOOR,doorStatus);

            break;
            case R.id.rotateBtn:
                //先隐藏Float再显示Reture按钮，否则会导致菜单显示问题
                mFloatBtnView.setVisibility(View.GONE);
                mRetBtnView.setVisibility(View.VISIBLE);
                mRetBtnView.bringToFront();

                AVM.avmSetCmd(0,AVM.VIEW_3D_ALL);
                currentView = 5;

                break;
            case R.id.settingBtn:
                //AVM.SetCmd(AVM.CMD_LINE_OPT,1);

                stopTimer();
                showPopupMenu();

                break;

            case R.id.retBtn:

                mRetBtnView.setVisibility(View.GONE);
                mColorView.setVisibility(View.GONE);
                mPreView.setVisibility(View.GONE);
                mNextView.setVisibility(View.GONE);

                mCaptureBtnView.setVisibility(View.GONE);

                currentView = preView;
                AVM.avmSetCmd(0,1);

                mFloatBtnView.setVisibility(View.VISIBLE);
                mFloatBtnView.bringToFront();

                carSettingFlag = 0;
                break;

            case R.id.captureBtn:
                //liugang 2020-7-2
               // pd= ProgressDialog.show(this, "拍照", "请等待…");
               // takePicture();


                AVM.avmSetCmd(AVM.CMD_TAKE_PHOTO,1);
                try {
                    sleep(10);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Logger.i(TAG, "----ProgressDialog.show");
                AVM.avmSetCmd(AVM.CMD_PHOTO_SPLIT,0);
                startActivity(new Intent(this, LenActivity.class));
                //captureMode = 1;
                break;

            case R.id.blackBtn:

/*                carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car/car"+carIndex+"/color"+0);
                if (carDir.exists()) {
                    carColorIndex = 0;
                    AVM.avmSetCmd(9, carColorIndex);
                    DVRPreference.getInstance(getApplicationContext()).setColorIndex(carColorIndex);
                }*/
                carColorIndex = 0;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.whiteBtn:
/*                carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car/car"+carIndex+"/color"+1);
                if (carDir.exists()) {
                    carColorIndex = 1;
                    AVM.avmSetCmd(9, carColorIndex);
                    DVRPreference.getInstance(getApplicationContext()).setColorIndex(carColorIndex);
                }*/
                carColorIndex = 1;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.blueBtn:
/*                carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car/car"+carIndex+"/color"+2);
                if (carDir.exists()) {
                    carColorIndex = 2;
                    AVM.avmSetCmd(9, carColorIndex);
                    DVRPreference.getInstance(getApplicationContext()).setColorIndex(carColorIndex);
                }*/
                carColorIndex = 2;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.redBtn:
/*                carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car/car"+carIndex+"/color"+3);
                if (carDir.exists()) {
                    carColorIndex = 3;
                    AVM.avmSetCmd(9, carColorIndex);
                    DVRPreference.getInstance(getApplicationContext()).setColorIndex(carColorIndex);
                }*/
                carColorIndex = 3;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.grayBtn:
/*                carDir = new File(this.getFilesDir().getAbsolutePath()+ File.separator+".car/car"+carIndex+"/color"+4);
                if (carDir.exists()) {
                    carColorIndex = 4;
                    AVM.avmSetCmd(9, carColorIndex);
                    DVRPreference.getInstance(getApplicationContext()).setColorIndex(carColorIndex);
                }*/
                carColorIndex = 4;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;


            case R.id.yellowBtn:
                carColorIndex = 5;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;


            case R.id.blue2Btn:
                carColorIndex = 6;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.greenBtn:
                carColorIndex = 7;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.pinkBtn:
                carColorIndex = 8;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.brownBtn:
                carColorIndex = 9;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.PreBtn:

                if(carIndex>0)
                    carIndex--;

                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;

            case R.id.NextBtn:
                if(carIndex<1)
                    carIndex++;
                AVM.SetCar(carNameArray[carIndex]+"_color0"+carColorIndex,0);
                break;
        }
    }



    private void clickRecordBtn() {
        //mTopViewBtn.setEnabled(false);
        new Thread() {
            @Override
            public void run() {

                try {
                    for(int i=0;(!(RecordManager.getInstance(getApplicationContext()).isInit())) && i< 10;i++)
                        sleep(1000);//wait record service ready,add by liugang 2020-06-24;


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!(RecordManager.getInstance(getApplicationContext()).isRecording()) && DVRPreference.getInstance(getApplicationContext()).isBackstageRecord()) {
                    startRecord();

                } else if((RecordManager.getInstance(getApplicationContext()).isRecording()) && !(DVRPreference.getInstance(getApplicationContext()).isBackstageRecord())){
                    stopRecord();

                }
            }
        }.start();
    }

    private void clickSettingBtn() {
/*        if (RecordManager.getInstance(getApplicationContext()).isRecording()) {
            new AlertDialog.Builder(RecordActivity.this, android.R.style
                    .Theme_Material_Dialog_Alert).setTitle("进入设置会停止录像").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopRecord();
                    startSettingsActivity();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else {
            startSettingsActivity();
        }*/

        settingActivityFlag = 1;
        startSettingsActivity();
    }

    private void clickVideoFileBtn() {
/*        if (RecordManager.getInstance(getApplicationContext()).isRecording()) {
            new AlertDialog.Builder(RecordActivity.this, android.R.style
                    .Theme_Material_Dialog_Alert).setTitle("进入文件预览会停止录像").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopRecord();
                    startFileActivity();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else {
            startFileActivity();
        }*/

        videoActivityFlag = 1;
        if (RecordManager.getInstance(getApplicationContext()).isRecording())
            stopRecord();

        startFileActivity();
    }

    private void takePicture() {
/*        long availableSpace = StorageUtils.getAvailableSpace(DVRPreference.getInstance
                (getApplicationContext()).getRecordPath());
        if (availableSpace > mBaseDvr.getMinSpace()) {
            mBaseDvr.takePicture();
        } else if (availableSpace == -1) {
            Toast.makeText(getApplicationContext(), R.string.storage_not_exit, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.storage_not_enough, Toast
                    .LENGTH_LONG).show();
        }*/

        mBaseDvr.takePicture();
    }


    private void recordLock() {
        if (!RecordManager.getInstance(getApplicationContext()).isRecording()) {
            // 如果不处于录制状态，则不做任何处理
            Toast.makeText(getApplicationContext(), "不在录制状态", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isLocked = DVRApplication.getInstance().getDVR().isLock();
        DVRApplication.getInstance().getDVR().setLock(!isLocked);
        if (!isLocked) {
            // 锁定
            Toast.makeText(getApplicationContext(), "锁定", Toast.LENGTH_SHORT).show();
            //mBottomViewBtn.setImageResource(R.drawable.ic_lock_outline_red_a400_48dp);
        } else {
            // 解锁
            Toast.makeText(getApplicationContext(), "解锁", Toast.LENGTH_SHORT).show();
            //mBottomViewBtn.setImageResource(R.drawable.ic_lock_open_grey_400_48dp);
        }
    }



    //function：assets - > /data/data/package/files/ or /sdcard
    //filePath example /assets/
    public static void copyAssetsDir(Activity activity, String filePath){
        try {
            String[] fileList = activity.getAssets().list(filePath);
            if(fileList.length>0) {//dir
                File file=new File(activity.getFilesDir().getAbsolutePath()+ File.separator+filePath);///data/data/package/files/
                //File file=new File(Environment.getExternalStorageDirectory()+ File.separator+filePath);//sdcard
                file.mkdirs();
                for (String fileName:fileList){
                    filePath=filePath+File.separator+fileName;

                    copyAssetsDir(activity,filePath);

                    filePath=filePath.substring(0,filePath.lastIndexOf(File.separator));

                }
            } else {//file
                InputStream inputStream=activity.getAssets().open(filePath);
                File file=new File(activity.getFilesDir().getAbsolutePath()+ File.separator+filePath);
                //File file=new File(Environment.getExternalStorageDirectory()+ File.separator+filePath);
                if(!file.exists() || file.length()==0) {
                    FileOutputStream fos=new FileOutputStream(file);
                    int len=-1;
                    byte[] buffer=new byte[1024];
                    while ((len=inputStream.read(buffer))!=-1){
                        fos.write(buffer,0,len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();

                } else {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        int srcWidth=1024,srcHeight=600;
        switch(AVM.resolutionIndex){

            case 0:
                srcWidth = 1024;
                srcHeight = 600;
                break;

            case 1:
                srcWidth = 1280;
                srcHeight = 720;
                break;

            case 2:
                srcWidth = 1280;
                srcHeight = 480;
                break;

            case 3:
                srcWidth = 1280;
                srcHeight = 800;
                break;

            case 4:
                srcWidth = 1920;
                srcHeight = 720;
                break;

            case 5:
                srcWidth = 1920;
                srcHeight = 860;
                break;

            case 6:
                srcWidth = 1920;
                srcHeight = 1080;
                break;

            case 7:
                srcWidth = 2000;
                srcHeight = 1200;
                break;

            default:
                srcWidth = 1024;
                srcHeight = 600;
                break;
        }

/*        if(AVM.resolutionIndex == 0) {
            srcWidth = 1024;
            srcHeight = 600;
        }else if(AVM.resolutionIndex == 1){
            srcWidth = 1280;
            srcHeight = 720;

        }*/


        int front[] = {220,220,110,0};//front,x2,y2,x1,y1
        int back[] = {220,600,110,400};//bottom
        int left[] = {110,400,0,200};//left
        int right[] = {330,400,220,200};//right
        int narrow[] = {220,400,110,200};//narrow


        if(AVM.h_2d_left_flag == 0){
            front[0]+=674;//1024-350
            front[2]+=674;

            back[0]+=674;
            back[2]+=674;

            left[0]+=674;
            left[2]+=674;

            right[0]+=674;
            right[2]+=674;

            narrow[0]+=674;
            narrow[2]+=674;

        }


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if(currentView != AVM.VIEW_3D_ALL && currentView != AVM.VIEW_3D_ALL_CAR) {
                    if ((event.getX() < (int)(front[0]*(srcWidth/1024.0)) && event.getY() < (int)(front[1]*(srcHeight/600.0))) && (event.getX() > (int)(front[2]*(srcWidth/1024.0)) && event.getY() >= (int)(front[3]*(srcHeight/600.0)))) {

                        if(currentView ==0)
                            fIndex = 1;
                        else if(currentView ==8)
                            fIndex = 0;



                        if(fIndex ==0)
                            currentView = 0;
                        else
                            currentView = 8;
                        AVM.avmSetCmd(0,currentView);
                        preView = currentView;
                        mTopViewBtn.setImageResource(R.drawable.top_selected);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);


                    } else if ((event.getX() < (int)(back[0]*(srcWidth/1024.0)) && event.getY() < (int)(back[1]*(srcHeight/600.0))) && (event.getX() > (int)(back[2]*(srcWidth/1024.0)) && event.getY() >= (int)(back[3]*(srcHeight/600.0)))) {

                        if(currentView ==1)
                            bIndex = 1;
                        else if(currentView ==9)
                            bIndex = 0;


                        if(bIndex ==0)
                            currentView = 1;
                        else
                            currentView = 9;
                        AVM.avmSetCmd(0,currentView);
                        preView = currentView;
                        mTopViewBtn.setImageResource(R.drawable.top_gray);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_selected);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);


                    } else if ((event.getX() < (int)(left[0]*(srcWidth/1024.0)) && event.getY() < (int)(left[1]*(srcHeight/600.0))) && (event.getX() > (int)(left[2]*(srcWidth/1024.0)) && event.getY() >= (int)(left[3]*(srcHeight/600.0)))) {
                        if(lIndex ==0)
                            currentView = 2;
                        else
                            currentView = 6;
                        AVM.avmSetCmd(0,currentView);
                        preView = currentView;
                        mTopViewBtn.setImageResource(R.drawable.top_gray);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                        mLeftViewBtn.setImageResource(R.drawable.left_selected);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);
                    } else if ((event.getX() < (int)(right[0]*(srcWidth/1024.0)) && event.getY() < (int)(right[1]*(srcHeight/600.0))) && (event.getX() > (int)(right[2]*(srcWidth/1024.0)) && event.getY() >= (int)(right[3]*(srcHeight/600.0)))) {
                        if(rIndex ==0)
                            currentView = 3;
                        else
                            currentView = 7;
                        AVM.avmSetCmd(0,currentView);
                        preView = currentView;
                        mTopViewBtn.setImageResource(R.drawable.top_gray);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_selected);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_gray);
                    } else if ((event.getX() < (int)(narrow[0]*(srcWidth/1024.0)) && event.getY() < (int)(narrow[1]*(srcHeight/600.0))) && (event.getX() > (int)(narrow[2]*(srcWidth/1024.0)) && event.getY() >= (int)(narrow[3]*(srcHeight/600.0)))) {
                        if(nIndex ==0)
                            currentView = 10;
                        else
                            currentView = 11;
                        AVM.avmSetCmd(0,currentView);
                        preView = currentView;
                        mTopViewBtn.setImageResource(R.drawable.top_gray);
                        mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                        mLeftViewBtn.setImageResource(R.drawable.left_gray);
                        mRightViewBtn.setImageResource(R.drawable.right_gray);
                        mNarrowViewBtn.setImageResource(R.drawable.lr_selected);
                    }
                    //preX = -1;//2021-12-07，任意旋转合并



                }else{
                    //2021-12-07，任意旋转合并
                    preX = event.getX();
                    AVM.avmSetCmd(AVM.CMD_TOUCH_SCREEN, 1);//touch down

                }


                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                if(AVM.rotate_style == 1) {
                    float offset = event.getX() - preX;

                    //Log.d(TAG, "---onTouchEvent: "+offset);
                    preX = event.getX();


                    //AVM.angle -= (int) (offset / 5);

                    int angleTemp = AVM.angle;
                    angleTemp -= (int) (offset / 5);


                    if(AVM.avmSetCmd(7, angleTemp) == 1)
                        AVM.angle = angleTemp;

                }
                break;
            case MotionEvent.ACTION_UP:
                if(AVM.rotate_style == 0) {
                    if ((currentView == AVM.VIEW_3D_ALL || currentView == AVM.VIEW_3D_ALL_CAR) && preX > 0.0) {
                        if (event.getX() - preX > 4) {
                            AVM.angle -= 45;
                            AVM.avmSetCmd(7, AVM.angle);
                        } else if (preX - event.getX() > 4) {
                            AVM.angle += 45;
                            AVM.avmSetCmd(7, AVM.angle);
                        }
                    }
                }

                AVM.avmSetCmd(AVM.CMD_TOUCH_SCREEN, 0);//touch up
                break;
        }



        //preX
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {

            //preX = event.getX();
           //preY = event.getY();
        }

        if (mGestureDetector != null) {
            //Logger.i(TAG, "mGestureDetector");
            mGestureDetector.onTouchEvent(event);
        }
        //mStandby.delay();


        return super.onTouchEvent(event);
    }

    public void startLocation(Context mContext) {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String best = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //this.listener = listener;
        locationManager.requestLocationUpdates(best, 1000, 0, MyLocationListener);//if open this?
        Log.d(TAG, "----startLocation");
    }


    private LocationListener MyLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //if (listener != null && location != null) {
            //    listener.onLocation(location.getLatitude() + "", location.getLongitude() + "");
            //}
            float speed = location.getSpeed();
            //Log.d(TAG, "----onLocationChanged: "+location.getLatitude() + ""+location.getLongitude());
            //Log.d(TAG, "----onLocationChanged: "+location.hasSpeed() + ""+location.getSpeed());
            currentSpeed = (int)(speed*3600.0);
            //AVM.avmSetCmd(2,currentSpeed);// m/hour

            //mDebugTV.setText("my gps speed:"+speed);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    public class BroadcastReceiverHelper extends BroadcastReceiver {


        Context ct=null;
        BroadcastReceiverHelper receiver;

        public BroadcastReceiverHelper(Context c){
            ct=c;
            receiver=this;
        }

        //注册
        public void registerAction(String action){
            IntentFilter filter=new IntentFilter();
            filter.addAction(action);
            ct.registerReceiver(receiver, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            String msg=intent.getStringExtra("audioControl");
            Log.d(TAG, "---onReceive:"+msg);

            if(DVRApplication.getInstance().getAvmFlag() == false)
                return;


            if(msg.equals("open360"))
            {

                if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                    Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                }
            }else  if(msg.equals("close360"))
            {

                if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())!= true) {
                    moveTaskToBack(true);
                }
            }else  if (msg.equals("frontView")){

                    mFloatBtnView.setVisibility(View.VISIBLE);
                    mFloatBtnView.bringToFront();
                    mRetBtnView.setVisibility(View.GONE);

                    if(fIndex ==0)
                        currentView = 0;
                    else
                        currentView = 8;
                    AVM.avmSetCmd(0,currentView);
                    preView = currentView;
                    mTopViewBtn.setImageResource(R.drawable.top_selected);
                    mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                    mLeftViewBtn.setImageResource(R.drawable.left_gray);
                    mRightViewBtn.setImageResource(R.drawable.right_gray);
                    mNarrowViewBtn.setImageResource(R.drawable.lr_gray);

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }



                }else if(msg.equals("rearView")){

                    mFloatBtnView.setVisibility(View.VISIBLE);
                    mFloatBtnView.bringToFront();
                    mRetBtnView.setVisibility(View.GONE);

                    if(bIndex ==0)
                        currentView = 1;
                    else
                        currentView = 9;
                    AVM.avmSetCmd(0,currentView);
                    preView = currentView;
                    mTopViewBtn.setImageResource(R.drawable.top_gray);
                    mBottomViewBtn.setImageResource(R.drawable.bottom_selected);
                    mLeftViewBtn.setImageResource(R.drawable.left_gray);
                    mRightViewBtn.setImageResource(R.drawable.right_gray);
                    mNarrowViewBtn.setImageResource(R.drawable.lr_gray);

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }
                }else  if(msg.equals("leftView")){

                AVM.avmSetCmd(AVM.CMD_3D_SCALE_ALL,1);


/*                    mFloatBtnView.setVisibility(View.VISIBLE);
                    mFloatBtnView.bringToFront();
                    mRetBtnView.setVisibility(View.GONE);

                    if(lIndex ==0)
                        currentView = 2;
                    else
                        currentView = 6;
                    AVM.avmSetCmd(0,currentView);
                    preView = currentView;
                    mTopViewBtn.setImageResource(R.drawable.top_gray);
                    mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                    mLeftViewBtn.setImageResource(R.drawable.left_selected);
                    mRightViewBtn.setImageResource(R.drawable.right_gray);
                    mNarrowViewBtn.setImageResource(R.drawable.lr_gray);

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }*/
                }else  if(msg.equals("rightView")) {

                AVM.avmSetCmd(AVM.CMD_3D_SCALE_ALL,-1);
/*                    Log.d(TAG, "---onReceive:4");
                    mFloatBtnView.setVisibility(View.VISIBLE);
                    mFloatBtnView.bringToFront();
                    mRetBtnView.setVisibility(View.GONE);

                    if(rIndex ==0)
                        currentView = 3;
                    else
                        currentView = 7;
                    AVM.avmSetCmd(0,currentView);
                    preView = currentView;
                    mTopViewBtn.setImageResource(R.drawable.top_gray);
                    mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                    mLeftViewBtn.setImageResource(R.drawable.left_gray);
                    mRightViewBtn.setImageResource(R.drawable.right_selected);
                    mNarrowViewBtn.setImageResource(R.drawable.lr_gray);

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }*/
                }else  if(msg.equals("allView")) {

                    //先隐藏Float再显示Reture按钮，否则会导致菜单显示问题
                    mFloatBtnView.setVisibility(View.GONE);
                    mRetBtnView.setVisibility(View.VISIBLE);
                    mRetBtnView.bringToFront();

                    AVM.avmSetCmd(0,5);
                    currentView = 5;

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }
                }else if(msg.equals("narrowView")){
                    mFloatBtnView.setVisibility(View.VISIBLE);
                    mFloatBtnView.bringToFront();
                    mRetBtnView.setVisibility(View.GONE);

                    if(nIndex ==0)
                        currentView = 10;
                    else
                        currentView = 11;
                    AVM.avmSetCmd(0,currentView);
                    preView = currentView;
                    mTopViewBtn.setImageResource(R.drawable.top_gray);
                    mBottomViewBtn.setImageResource(R.drawable.bottom_gray);
                    mLeftViewBtn.setImageResource(R.drawable.left_gray);
                    mRightViewBtn.setImageResource(R.drawable.right_gray);
                    mNarrowViewBtn.setImageResource(R.drawable.lr_selected);

                    if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                        Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                    }
                }
            }
        }



    public class BroadcastAccReceiverHelper extends BroadcastReceiver {


        Context ct=null;
        BroadcastAccReceiverHelper receiver;

        public BroadcastAccReceiverHelper(Context c){
            ct=c;
            receiver=this;
        }

        //注册
        public void registerAction(String action){
            IntentFilter filter=new IntentFilter();
            filter.addAction(action);
            ct.registerReceiver(receiver, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            int acc = intent.getIntExtra("acc", 0);



            if (acc == 0) {//off
                Log.d(TAG, "-----onReceive acc off");
                //if (RecordManager.getInstance(getApplicationContext()).isRecording())
                //    stopRecord();


                //finish();
                //System.exit(0);



                mBaseDvr.mFrontCamera.release();

                if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())!= true) {
                    moveTaskToBack(true);
                }

                ;
            } else {//on
                Log.d(TAG, "-----onReceive acc on");
                AVM.firstFrameFlag = 0;
                AVM.SetCmd(AVM.CMD_RESET,1);
                AVM.SetCmd(AVM.CMD_VIEW,AVM.VIEW_LOGO);//显示logo视图



                if(isAppIsInBackground(DVRApplication.sInstance.getApplicationContext())) {
                    Intent intent2 = new Intent(DVRApplication.sInstance.getApplicationContext(), RecordActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    DVRApplication.sInstance.getApplicationContext().startActivity(intent2);
                }


                //启动一个1秒定时器，下面部分放到定时器里面执行，延长logo显示时间，现在logo显示时间太短
                mBaseDvr.mFrontCamera.openCamera();

                //mBaseDvr.setCameraListener(this);
                setCameraErrorListener();


                //add for setregions
 /*               if(AVM.luma_on == 1) {

                    int regions[];
                    int regions2[] = new int[32+1];//regions2[0] cmd value,regions2[1] to regions2[32] for 8 regions
                    regions2[32]=100;//1:set regions cmd

                    regions = AVM.GetLumaRect();




                    System.arraycopy(regions, 0, regions2, 0, 32);


                    if(AVM.camera_1920_flag == 1)
                    {
                        //1920*1080
                        for(int k=0;k<8;k++) {
                            regions2[k*4+0] = (int) (regions2[k*4+0] * (1920.0 / 1280.0));
                            regions2[k*4+1] = (int) (regions2[k*4+1] * (1920.0 / 1280.0));
                            regions2[k*4+2] = (int) (regions2[k*4+2] * (1920.0 / 1280.0));
                            regions2[k*4+3] = (int) (regions2[k*4+3] * (1920.0 / 1280.0));
                        }

                    }

                    //
                    for(int k=0;k<8;k++) {
                        regions2[k * 4 + 2] = (regions2[k * 4 + 2] / 10) * 10;
                        regions2[k * 4 + 3] = (regions2[k * 4 + 3] / 10) * 10;
                    }

                    int ret = 0;
                    ret = DVRApplication.getInstance().getDVR().getFrontCamera().SetRegions(regions2);
                    if(ret == -1) {
                        AVM.luma_on = 0;
                        Log.d(TAG, "------------SetRegions not support!");
                    }
                }*/


                try {
                    mBaseDvr.mFrontCamera.startPreview(CameraDrawer.surfaceTexture);

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (Exception e1) {
                    e1.printStackTrace();

                }
            }
        }
    }

    private void writeCarReverse(String devPath,String cmd) {
        File file = new File(devPath);
        if(file.exists()){
            try {
                RandomAccessFile rdf = new RandomAccessFile(file, "rw");
                rdf.write(cmd.getBytes());
                rdf.close();
                //Toast.makeText(this, "write value success!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            //Toast.makeText(this, "file no exists", Toast.LENGTH_SHORT).show();
        }
    }


}
