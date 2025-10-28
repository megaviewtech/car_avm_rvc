
package com.softwinner.dvr.ui.fragment;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.megaview.avm.AVM;
import com.megaview.ghost.Ghost;
import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.util.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Thread.*;
import static java.lang.Thread.sleep;


/**
 * Description:
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer,MediaPlayer.OnVideoSizeChangedListener{
    private static final String TAG = "megaview";

    public CameraDrawer mCameraDrawer;
    public String view="0";
    Context mViewContext;
    private MediaPlayer mediaPlayer;

    int frameCount = 0;
    //
    Timer timer;
    TimerTask task;

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }

    class MyConfigChooser implements GLSurfaceView.EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, javax.microedition.khronos.egl.EGLDisplay display) {
            int attribs[] = {
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, 1,
                    EGL10.EGL_SAMPLES, 4,  // 4->4xMSAA，再改大运行不正常，liugang 2020-06-16
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                Log.e(TAG, "Failed! Error handling.");
                return null;
            } else {
                return configs[0];
            }
        }
    }

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mViewContext = context;
        init();
    }

    private void init(){

        setEGLContextClientVersion(2);
        setEGLConfigChooser(new MyConfigChooser());
        setRenderer(this);
        //setRenderMode(RENDERMODE_WHEN_DIRTY);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        mCameraDrawer=new CameraDrawer(getResources(),mViewContext);

    }

/*    public void SetView(String val){
        view = val;
        mCameraDrawer.SetView(val);
    }*/


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //initMediaPlayer();


       mCameraDrawer.onSurfaceCreated(gl,config);

        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();

                //Log.d(TAG, "-----onFrameAvailable");
                if(AVM.firstFrameFlag == 0 && frameCount == 3) {
                    AVM.firstFrameFlag = 1;
                    Log.d(TAG, "-----onFrameAvailable");
                    //AVM.SetCmd(AVM.CMD_VIEW,5);
                    startRoateTimer(1000);
                }
                frameCount++;
                //Log.d(TAG, "-----test,onFrameAvailable");
            }
        });


/*        try {
            DVRApplication.getInstance().getDVR().frontCameraStartPreview(mCameraDrawer.getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed, Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), R.string.preview_failed2, Toast.LENGTH_SHORT)
                    .show();
            DVRApplication.getInstance().exit();
        }*/


/*        Surface surface = new Surface(mCameraDrawer.getSurfaceTexture());
        mediaPlayer.setSurface(surface);*/
        Surface surface = new Surface(mCameraDrawer.getSurfaceTexture());
        mCameraDrawer.getSurfaceTexture().setDefaultBufferSize(Ghost.cameraWidth*2, Ghost.cameraHeight*2);
        Ghost.GhostSetPreviewSurface(surface);

        //
        if(Ghost.sixChannelCameraFlag == 1) {
            Surface surface6_2 = new Surface(mCameraDrawer.getSurfaceTexture6_2());
            mCameraDrawer.getSurfaceTexture6_2().setDefaultBufferSize(Ghost.cameraWidth*2, Ghost.cameraHeight);
            Ghost.GhostSetPreviewSurface6(surface6_2);
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Logger.d(TAG, "-----test,--------onSurfaceChanged------");
/*
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                                              @Override
                                              public void onPrepared(MediaPlayer mediaPlayer){
                                                  mediaPlayer.start();
                                              }
                                          }



        );*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        mCameraDrawer.onDrawFrame(gl);

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "-----test,--------onPause2------");

    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "-----test,--------onResume2------");

    }

    public void startRoateTimer(int timeLong){


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


                AVM.avmSetCmd(AVM.CMD_NO_NAME1,24);//设置旋转速度
                AVM.avmSetCmd(AVM.CMD_3D_ANGLE,45*2*3);
                AVM.angle = (45*2*3);

                //AVM.rotateFlag = 1;

/*                AVM.avmSetCmd(AVM.CMD_3D_ANGLE,45*6);
                AVM.angle = 45*6;*/

            }
        };

        timer.schedule(task,timeLong);

    }

    public void stopRotateTimer(){

        //timer task
        if(timer!= null) {

            timer.cancel();
            if(task!=null)
                task.cancel();
        }

    }

    private void initMediaPlayer(){

        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource("/sdcard/f_20200711_091714.ts");

        }catch(IOException e){

            e.printStackTrace();
        }

        mediaPlayer.setOnVideoSizeChangedListener(this);

    }



    }
