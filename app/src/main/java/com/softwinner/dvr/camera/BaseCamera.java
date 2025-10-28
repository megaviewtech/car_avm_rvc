package com.softwinner.dvr.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.megaview.avm.AVM;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;


public abstract class BaseCamera {
    private static final String TAG = "BaseCamera";
    int mIndex;
    private boolean isPreviewing = false;
    private boolean isRecording = false;

    public Camera mCamera;
    private MediaRecorder mediaRecorder;
    static Camera camera0,camera1,camera2,camera3;
    public static int TS10 = 1;
    //public static int TS18 = 4;
    public static int T7 = 2;
    public static int T5 = 3;
    static int PlatformID = T5;


    CameraListener mListener;

    public abstract boolean openCamera();

    public abstract void initCameraRec(String filename, int format, int width, int height, int
            frameRate, int bitrate, int audioMode, int audioBitrate);

    public abstract List<Camera.Size> getSupportVideoSize();

    public abstract int getBitRate();

    public int getFrameRate() {
        return 30;
    }

    abstract boolean checkDevice(int index);

    abstract void setPreviewSize();

    abstract void setPictureQuality();

    public void startPreview(SurfaceHolder surfaceHolder) throws IOException {
        if (mCamera == null) {
            if (mListener != null) {
                mListener.onStartPreviewFailed();
            }
            return;
        }

        if (isPreviewing) {
            stopPreview();
        }
        setPreviewSize();
        mCamera.setPreviewDisplay(surfaceHolder);
        if (!isRecording) {
            mCamera.startPreview();
        } else {
            startRender();
        }
        isPreviewing = true;
    }

    public static String cameraSizeToString(Iterable<Camera.Size> sizes)
    {
        StringBuilder s = new StringBuilder();
        for (Camera.Size size : sizes)
        {
            if (s.length() != 0)
                s.append(",");
            s.append(size.width).append('x').append(size.height);
        }
        return s.toString();
    }


    public void startPreview(SurfaceTexture surfaceTexture) throws IOException {
        if (mCamera == null) {
            if (mListener != null) {
                mListener.onStartPreviewFailed();
            }
            return;
        }
        //Logger.d("liugang", "-----startPreview");
        if (isPreviewing) {
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSizes = params.getSupportedVideoSizes();
        //Logger.d("test","camera 6,Video supported sizes: " + cameraSizeToString( previewSizes));




        mCamera.setPreviewTexture(surfaceTexture);
        //Logger.d("test","----lgtest,mCamera.setPreviewTexture:");
        setPreviewSize();
        //mCamera.startPreview();

        if (!isRecording) {
            //Logger.d("test","----lgtest,mCamera.startPreview()");
            mCamera.startPreview();

        } else {
		 if(PlatformID==T7 || PlatformID==T5)
            startRender();
        }

        isPreviewing = true;

        //AVM.luma_on = 1;

    }

    public void stopPreview() {
        if (mCamera == null) {
            return;
        }
        Logger.d("liugang", "-----stopPreview");
        if (isPreviewing) {
            if (isRecording) {
                Logger.i(TAG, "----- To stopPreview");
                if(PlatformID==T7 || PlatformID==T5)
				    stopRender();
            } else {
                stopWaterMark();
                mCamera.stopPreview();
                camera0.stopPreview();
                camera1.stopPreview();
                camera2.stopPreview();
                camera3.stopPreview();
            }

            isPreviewing = false;
        }

    }

    public void startRecord() {
        if (mCamera == null) {
            return;
        }

        if (isRecording) {
            return;
        }
		
 		if(PlatformID==T7 || PlatformID==T5){
	        try {
	            Class<Camera> cls = Camera.class;
	            Method startRecord = cls.getMethod("awCamRecStart");
	            startRecord.invoke(mCamera);
	            isRecording = true;
	        } catch (Exception e) {
	            Logger.e(TAG, "-----Error To awCamRecStart");
	            e.printStackTrace();
	        }
		}
    }


    public void stopRecord() {
        if (mCamera == null) {
            return;
        }

        if (!isRecording) {
            return;
        }

		if(PlatformID==T7 || PlatformID==T5){
	        try {
	            Class<Camera> cls = Camera.class;
	            Method stopRecord = cls.getMethod("awCamRecStop");
	            stopRecord.invoke(mCamera);
	            awCamRecRelease();
	        } catch (Exception e) {
	            Logger.e(TAG, "-----Error To awCamRecStop");
	        } finally {
	            isRecording = false;
	        }
		}else{
		
	        mediaRecorder.stop();
	        mediaRecorder.reset();
	        mediaRecorder.release();

	        try {
	            mCamera.reconnect();
	        } catch (IOException e) {

	            e.printStackTrace();
	        }
		}
    }

    public void setNextFile(String file) {
        if (mCamera == null) {
            return;
        }

		if(PlatformID==T7 || PlatformID==T5){
	        try {
	            Class<Camera> cls = Camera.class;
	            Method stopRecord = cls.getMethod("awCamRecStartNextFile", String.class);
	            stopRecord.invoke(mCamera, file);
	        } catch (Exception e) {
	            Logger.e(TAG, "-----Error To awCamRecStart");
	        }
		}else{

	       mediaRecorder.stop();
	       mediaRecorder.reset();
	       mediaRecorder.release();

	        try {
	            mCamera.reconnect();
	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	/*        try {
	            mediaRecorder.setNextOutputFile(new File(file));
	        }catch (IOException e){

	        }*/


	        mCamera.unlock();

	        mediaRecorder = new MediaRecorder();

	        mediaRecorder.setCamera(mCamera);
	        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);  //CAMCORDER);//
	        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


	        {
	            CamcorderProfile mCamcorderProfile = CamcorderProfile.get(2, CamcorderProfile.QUALITY_HIGH);
	            mediaRecorder.setProfile(mCamcorderProfile);

	            mediaRecorder.setVideoSize(1920, 1080);
	        }

	        mediaRecorder.setOutputFile(file);

	        try {
	            mediaRecorder.prepare();
	        } catch (Exception e) {

	            e.printStackTrace();
	            mCamera.lock();
	        }

	        try {
	            mediaRecorder.start(); // Recording is now started
	        } catch (RuntimeException e) {
	            Log.e("xcy", "Could not start media recorder. ", e);

	            mCamera.lock();

	        }
		}
    }

    protected static Camera open(int startId, int cameraNum, int width, int height) {


        if(PlatformID == T7) {
            try {
                Class<Camera> cls = Camera.class;
                Method open = cls.getMethod("open", int.class, int.class, int.class, int.class);
                return (Camera) open.invoke(null, startId, cameraNum, width, height);
            } catch (Exception e) {
                Logger.e(TAG, "-----Error To open");
                e.printStackTrace();
            }
            return null;
        }else if(PlatformID == TS10)
            return Camera.open(0);//for ts18
        //return Camera.open(2);//for ts10

        else if(PlatformID == T5){


            camera0 = Camera.open(0);
            camera1 = Camera.open(1);
            camera2 = Camera.open(2);
            camera3 = Camera.open(3);




            Camera.Parameters parameters;// = camera0.getParameters();
            //List<Camera.Size> previewSizes = parameters.getSupportedVideoSizes();
            //Logger.d("test","camera 0,Video supported sizes: " + cameraSizeToString( previewSizes));

            if(AVM.camera_1920_flag == 1){
                parameters = camera0.getParameters();
                parameters.setPreviewSize(1920, 1080);
                camera0.setParameters(parameters);

                parameters = camera1.getParameters();
                parameters.setPreviewSize(1920, 1080);
                camera1.setParameters(parameters);

                parameters = camera2.getParameters();
                parameters.setPreviewSize(1920, 1080);
                camera2.setParameters(parameters);

                parameters = camera3.getParameters();
                parameters.setPreviewSize(1920, 1080);
                camera3.setParameters(parameters);
            }

            camera0.startPreview();
            camera1.startPreview();
            camera2.startPreview();
            camera3.startPreview();

            Logger.d(TAG, "------lgtest,2");

/*            try {
                Class<Camera> cls = Camera.class;
                Method open = cls.getMethod("open",int.class, int.class, int.class, int.class);
                return (Camera) open.invoke(null, 2, 4, width, height);
            } catch (Exception e) {
                Logger.e(TAG, "-----Error To open");
                e.printStackTrace();
            }
            return null;*/

/*            try {
                Class<Camera> cls = Camera.class;
                Method open = cls.getMethod("open360");
                return (Camera) open.invoke(null);
            } catch (Exception e) {
                Logger.e(TAG, "-----Error To open");
                e.printStackTrace();
            }
            return null;*/
            return Camera.open(6);

        }else
            return null;

    }

    public void release() {
        Logger.i(TAG, "------release");

        AVM.luma_on = 0;//2021-11-22

        if (isRecording) {
            stopRecord();
        }
        if (isPreviewing) {
            stopPreview();
        }

        //night 3 clock,quick acc off->startprivew ->fail ->toast ->exception????
//delete for acc on,2021-01-05,liugang
/*        if (mListener != null) {
            mListener = null;
        }*/

        if (camera0 != null) {
            camera0.release();
            camera0 = null;
        }

        if (camera1 != null) {
            camera1.release();
            camera1 = null;
        }

        if (camera2 != null) {
            camera2.release();
            camera2 = null;
        }

        if (camera3 != null) {
            camera3.release();
            camera3 = null;
        }

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }



    private String getName(int X, int Y) {
        String fileName = "/mnt/sdcard/"+"Record"+Integer.toString(X)+"X"+
                Integer.toString(Y)+".mp4";
        Log.e("xcy","fileName"+fileName);
        //System.currentTimeMillis()
        return fileName;
    }

    boolean awCamRecInit(String filename, int format, int width, int height, int framerate, int
            bitrate, int audiomode, int audiobitrate) {
			
		if(PlatformID == T7 || PlatformID==T5)
		{
		
	        Logger.i(TAG, "------awCamRecInit");

	        if (mCamera == null) {
	            return false;
	        }
	        try {
	            Class<Camera> cls = Camera.class;
	            Method initRecord = cls.getMethod("awCamRecInit", String.class, int.class, int.class,
	                    int.class, int.class, int.class, int.class, int.class);
	            //initRecord.invoke(mCamera, filename, 0, width, height, framerate, bitrate,audiomode, audiobitrate);
                initRecord.invoke(mCamera, filename, 0, 1280, 720, 25, 2000000,0, 0);
	            Logger.i(TAG, "------awCamRecInit，"+filename+","+1280+","+720+","+25+","+2000000);

	            return true;
	        } catch (Exception e) {
	            Logger.e(TAG, "-----Error To awCamRecStart");
	            e.printStackTrace();
	            return false;
	        }		
		
		
		}else{	
	        mCamera.unlock();
	        //mIsRecording = true;
	        mediaRecorder = new MediaRecorder();
	        //mediaRecorder.reset();
	        mediaRecorder.setCamera(mCamera);
	        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);  //CAMCORDER);//
	        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	        //CamcorderProfile mCamcorderProfile = CamcorderProfile.get(VideoIndex, CamcorderProfile.QUALITY_LOW);

	       // if(CaptureX == 2560 && CaptureY == 1440)
	        {
	            //CamcorderProfile mCamcorderProfile_1 = CamcorderProfile.get(0, 4);//Back Camera for T2 camera 640*480 (720*480)
	            //CamcorderProfile mCamcorderProfile = CamcorderProfile.get(0, 4);//
	            // CamcorderProfile mCamcorderProfile = CamcorderProfile.get(CameraId, CamcorderProfile.QUALITY_HIGH);
	            CamcorderProfile mCamcorderProfile = CamcorderProfile.get(2, CamcorderProfile.QUALITY_HIGH);
	            mediaRecorder.setProfile(mCamcorderProfile);
	            //mediaRecorder.setAudioChannels(1);
	            //mediaRecorder.setVideoSize(mCamcorderProfile.videoFrameWidth, mCamcorderProfile.videoFrameHeight);
	            mediaRecorder.setVideoSize(1920, 1080);
	            //mediaRecorder.setVideoFrameRate(10);
	            //mediaRecorder.setMaxDuration(60*1000*1); //add 2017-02-22
	        }

	        //mediaRecorder.setVideoSize(CaptureX, CaptureY);
	        mediaRecorder.setOutputFile(filename);




	        //mediaRecorder.setVideoFrameRate(30);
	        //mediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoBitRate);



	        try {
	            mediaRecorder.prepare();
	        } catch (Exception e) {
	            //mIsRecording = false;
	            //Toast.makeText(this, "fail", 0).show();
	            e.printStackTrace();
	            mCamera.lock();
	        }

	        //mediaRecorder.setVideoFrameRate(mCamcorderProfile.videoFrameRate);
	        //mediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoBitRate);
	        //mediaRecorder.setVideoEncoder(mCamcorderProfile.videoCodec);

	        try {
	            mediaRecorder.start(); // Recording is now started
	        } catch (RuntimeException e) {
	            Log.e("xcy", "Could not start media recorder. ", e);
	            //stopmediaRecorder();//releaseMediaRecorder();
	            mCamera.lock();
	            return false;
	        }
	        return  true;
		}
    }


    public void startRender() {
        if (mCamera == null) {
            return;
        }
        try {
            Class<Camera> cls = Camera.class;
            Method startRender = cls.getMethod("startRender");
            startRender.invoke(mCamera);
        } catch (Exception e) {
            Logger.e(TAG, "-----Error To startRender");
            e.printStackTrace();
        }
    }

   public  void stopRender() {
        Logger.i(TAG, "----- To stopRender");
        if (mCamera == null) {
            return;
        }
        try {
            Class<Camera> cls = Camera.class;
            Method stopRender = cls.getMethod("stopRender");
            stopRender.invoke(mCamera);
        } catch (Exception e) {
            Logger.e(TAG, "-----Error To stopRender");
            e.printStackTrace();
        }
    }


    private void awCamRecRelease() {
        if (mCamera == null) {
            return;
        }
        try {
            Class<Camera> cls = Camera.class;
            Method awCamRecRelease = cls.getMethod("awCamRecRelease");
            awCamRecRelease.invoke(mCamera);
        } catch (Exception e) {
            Logger.e(TAG, "-----Error To awCamRecRelease");
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    public void takePictureAsync(final String path) {
        Logger.d(TAG, "----takePictureAsync cameraId = ");

        Flowable.just("takePictureAsync").observeOn(Schedulers.io()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                if (mCamera != null) {
                    setPictureQuality();
                    try {
                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                Logger.d(TAG, "----takePicture1："+path);
                                boolean result = StorageUtils.savePicture(path, data);
                                Logger.d(TAG, "----takePicture2");
                                if (result) {
                                    if (mListener != null) {
                                        mListener.onTakePictureSuccess();
                                    }
                                } else {
                                    if (mListener != null) {
                                        mListener.onTakePictureFailed();
                                    }
                                }

                                if(PlatformID == TS10) {
                                    mCamera.startPreview();
                                }

                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "-----Error To takePictureAsync-------");
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onTakePictureFailed();
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置水印-反射调用
     *
     * @param camera
     */
    public void setWaterMark(Camera camera) {
        Class<?> c = camera.getClass();
        Method startWaterMark = null;
        try {
            startWaterMark = c.getMethod("startWaterMark");
            startWaterMark.invoke(camera);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "setWaterMark -------> not have startWaterMark method!!!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "setWaterMark -------> IllegalAccessException!!!");
            e.printStackTrace();
        }
    }

    /**
     * 设置水印-反射调用
     *
     * @param camera
     */
    public void setWaterMark(Camera camera, String message) {
        Class<?> c = camera.getClass();
        Method setWaterMarkMultiple = null;
        try {
            setWaterMarkMultiple = c.getMethod("setWaterMarkMultiple", message.getClass());
            setWaterMarkMultiple.invoke(camera, message);
        } catch (NoSuchMethodException e) {
            Logger.e(TAG, "setWaterMark -------> not have startWaterMark method!!!");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Logger.e(TAG, "setWaterMark -------> IllegalAccessException!!!");
            e.printStackTrace();
        }
    }


    /**
     * 开启水印
     */
    public void startWaterMark() {
        Class<Camera> cls = Camera.class;
        try {
            Method method = cls.getMethod("startWaterMark");
            method.invoke(mCamera);
        } catch (Exception e) {
            Log.e(TAG, "-----Error To startWaterMark-------");
            e.printStackTrace();
        }
    }

    /**
     * 停止水印
     */
    public void stopWaterMark() {
        Class<Camera> cls = Camera.class;
        try {
            Method method = cls.getMethod("stopWaterMark");
            method.invoke(mCamera);
        } catch (Exception e) {
            Log.e(TAG, "-----Error To startWaterMark-------");
            e.printStackTrace();
        }
    }

    /**
     * 设置水印
     *
     * @param waterMark
     */
    public void setWaterMarkMultiple(String waterMark) {
        Class<Camera> cls = Camera.class;
        try {
            Method method = cls.getMethod("setWaterMarkMultiple", String.class, int.class);
            method.invoke(mCamera, waterMark, 0);
        } catch (Exception e) {
            Log.e(TAG, "-----Error To setWaterMarkMultiple-------");
            e.printStackTrace();
        }
    }

    public void setCameraListener(CameraListener listener) {
        mListener = listener;
    }


    //get params values,liugang 2020-06-16
    public float[] GetParamsValue() {

        float[] paramsValue = new float[8];
        float[] paramsValue2;

        //2021-11-22
        if(mCamera == null){
            paramsValue[0] = 100;
            paramsValue[1] = 100;
            paramsValue[2] = 100;
            paramsValue[3] = 100;
            paramsValue[4] = 100;
            paramsValue[5] = 100;
            paramsValue[6] = 100;
            paramsValue[7] = 100;

        }else {
            try {
                Class<Camera> cls = Camera.class;
                Method getParamsValue = cls.getMethod("MegaviewGetParams");

                paramsValue = (float[]) getParamsValue.invoke(mCamera);

                //Log.d(TAG, "GetParamsValue: "+paramsValue[0]+" "+paramsValue[1]+" "+paramsValue[2]+" "+paramsValue[3]+" "+paramsValue[4]+" "+paramsValue[5]+" "+paramsValue[6]+" "+paramsValue[7]);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return paramsValue;
    }

    //SetRegions,liugang 2020-06-16
    public int SetRegions(int[] regions) {

        Log.d(TAG, "-----SetRegions: ");

        if(mCamera == null) {
            Log.d(TAG, "-----mCamera == null: ");
            return -1;
        }

        try {
            Class<Camera> cls = Camera.class;
            Method setRegions = cls.getMethod("MegaviewSetRegions",int[].class);

            setRegions.invoke(mCamera, regions);

        }catch (Exception e) {

            e.printStackTrace();
            return -1;
        }

        return 1;
    }

}
