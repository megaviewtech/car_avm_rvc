package com.softwinner.dvr.ui.activity;

import static android.content.ContentValues.TAG;

import static com.megaview.ghost.Ghost.CMD_PRESET_PIC_MODE;
import static com.megaview.ghost.Ghost.CMD_RECORDER_OUTPUT_SIZE;
import static com.megaview.ghost.Ghost.sixChannelCameraFlag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.opengl.GLES20;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;


import com.megaview.avm.AVM;
import com.megaview.ghost.Ghost;
import com.openadas.ai.Ai;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RenderThread extends Thread implements SurfaceTexture.OnFrameAvailableListener {

    //

    public Activity mContext;

    //
    private int[] mTextures = new int[4];
    private int[] mTexturesPic = new int[4];

    public static SurfaceTexture mSurfaceTexture;
    public static SurfaceTexture mSurfaceTexture2;
    public static SurfaceTexture mSurfaceTexture3;
    public static SurfaceTexture mSurfaceTexture4;


    //
    private MediaRecorder mMediaRecorder;
    private Surface mRecorderSurface;
    private int OutputWidth;
    private int OutputHeight;
    //
    //private AvcRecorder avcRecorder;
    //public Surface outputSurface;


    //
    public ImageReader imageReader;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;



    //
    private boolean running = true;
    static public int frame = 0;


    private void setupMediaRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Set the video source to Surface
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoSize(1920, 1080);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setOutputFile("/data/data/com.megaview.avm/files/test2.mp4");

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorderSurface = mMediaRecorder.getSurface();


    }


    private void startRecording() {
        try {
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {

        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
    }

    public RenderThread() {
        //录制分辨率
        OutputWidth = 1920;
        OutputHeight = 1080;
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public void saveBitmapToFile(byte[] rgbaData, int width, int height, String filename) {
        // 创建 Bitmap 对象
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 将 RGBA 数据填充到 Bitmap 中
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rgbaData));

        // 获取文件路径
        File directory = mContext.getFilesDir();
        if (directory != null && !directory.exists()) {
            directory.mkdirs(); // 创建目录
        }

        File file = new File(directory, filename);

        // 保存 Bitmap 为文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 使用 PNG 格式保存图像
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {

            Image image = reader.acquireNextImage();

            if (image == null) {
                return;
            }

            Image.Plane[] planes = image.getPlanes();
            int width = image.getWidth();
            int height = image.getHeight();

            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();

            ByteBuffer buffer = planes[0].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];

            buffer.get(bytes);

            if(frame == 10)
                saveBitmapToFile(bytes,640,480,"test.png");

            Log.d("AAA", "-----mylog,onImageAvailable:"+width+","+height+","+pixelStride+","+rowStride);
            image.close();
        }
    };

    @Override
    public void run() {


        //录制文件
        //String outputPath = "/data/data/com.example.cameraglrender/files/test.mp4";


        //
        startBackgroundThread();
        imageReader = ImageReader.newInstance(640, 480, PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
        //Log.d(TAG, "-----mylog:imageReader");

        //创建录制对象，获得outputSurface
        //avcRecorder = new AvcRecorder(OutputWidth, OutputHeight);
        //avcRecorder.start(1, outputPath);
        //outputSurface = avcRecorder.inputSurface;

/*        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG, "-----run: 发生异常:" + e.getMessage());
        }*/

        //
        //GlRenderWrapper.mSurfaceTexture.setDefaultBufferSize(1920*2, 1080*2);
        //Surface surfacePreview = new Surface(GlRenderWrapper.mSurfaceTexture);

        if(Ghost.picModeFlag ==1)
            Ghost.GhostSetCmd(Ghost.CMD_PRESET_PIC_MODE,1);
        //初始化ghost,把录制模块的Surface设置到ghost模块
        Ghost.GhostInit(null, null);


        Ghost.GhostSetCmd(Ghost.CMD_RECORDER_OUTPUT_SIZE, (OutputWidth << 16) + OutputHeight);

        //创建4个surfaceTexture,设置到camera2接口
        GLES20.glGenTextures(4, mTextures, 0);

        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mSurfaceTexture2 = new SurfaceTexture(mTextures[1]);
        mSurfaceTexture3 = new SurfaceTexture(mTextures[2]);
        mSurfaceTexture4 = new SurfaceTexture(mTextures[3]);

        //
        if(Ghost.picModeFlag == 1) {
            GLES20.glGenTextures(4, mTexturesPic, 0);

            if(Ghost.bigCameraFlag == 1)
                Ghost.LoadTexturePng("/data/data/com.megaview.avm/files/pic.bmp", mTexturesPic[0]);
                //Ghost.LoadTexturePng("/sdcard/avmAdas/pic.bmp", mTexturesPic[0]);
            else {
                Ghost.LoadTexturePng("/data/data/com.megaview.avm/files/1.bmp", mTexturesPic[0]);
                Ghost.LoadTexturePng("/data/data/com.megaview.avm/files/2.bmp", mTexturesPic[1]);
                Ghost.LoadTexturePng("/data/data/com.megaview.avm/files/3.bmp", mTexturesPic[2]);
                Ghost.LoadTexturePng("/data/data/com.megaview.avm/files/4.bmp", mTexturesPic[3]);
            }
        }

        Ghost.GhostSetCmd(Ghost.CMD_BIG_CAMERA,Ghost.bigCameraFlag);
        Ghost.GhostSetCmd(Ghost.CMD_CAMERA_INPUT_SIZE,(Ghost.cameraWidth << 16) + Ghost.cameraHeight);

        if(Ghost.sixChannelCameraFlag == 1)
            Ghost.GhostSetCmd(Ghost.CMD_SET_SIX_CAMERA,1);

        //set inputSurface
        //camera2Helper.openCamera(1920, 1080, mSurfaceTexture, null);

        setupMediaRecorder();
        Ghost.GhostSetRecordSurface(mRecorderSurface);
        startRecording();

        //adas init
        if(Ai.aiOn == 1 && Ai.aiInitFlag == 0) {
            int adasWidth = 1280, adasHeight = 720;
            boolean ret_init = Ai.loadModel(mContext.getAssets(), 0, 1);
            Ghost.GhostSetAdasSurface(Ai.GetSurface(adasWidth, adasHeight, 1, 0), 0);
            Ghost.GhostSetAdasSurface(Ai.GetSurface(adasWidth, adasHeight, 1, 1), 1);
            Ghost.GhostSetAdasSurface(Ai.GetSurface(adasWidth, adasHeight, 1, 2), 2);
            Ghost.GhostSetAdasSurface(Ai.GetSurface(adasWidth, adasHeight, 1, 3), 3);
            Ghost.GhostSetCmd(Ghost.CMD_ADAS_OUTPUT_SIZE, (adasWidth << 16) + adasHeight);

            Ghost.GhostSetCmd(Ghost.CMD_ADAS_OUTPUT_MODE, 1);

            Ai.aiInitFlag = 1;
        }

        while (running) {
            //update data
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture2.updateTexImage();
            mSurfaceTexture3.updateTexImage();
            mSurfaceTexture4.updateTexImage();

            //
            if(Ai.aiOn == 1) {
                float[] imageObjects2;
                imageObjects2 = Ai.GetObjects(0);
                AVM.SetObjects(0, imageObjects2);

                imageObjects2 = Ai.GetObjects(1);
                AVM.SetObjects(1, imageObjects2);

                imageObjects2 = Ai.GetObjects(2);
                AVM.SetObjects(2, imageObjects2);

                imageObjects2 = Ai.GetObjects(3);
                AVM.SetObjects(3, imageObjects2);

                //if(imageObjects2 != null && imageObjects2.length > 0) {
                //    Log.d(TAG, "-----imageObjects2:" + imageObjects2.length);

                    //imageObjects2[8] = 1;
               // }

            }

            //draw,把纹理传给ghost绘制输出4合1大图
            if(Ghost.picModeFlag == 1)
                Ghost.GhostDraw(mTexturesPic);
            else
                Ghost.GhostDraw(mTextures);

            //Log.d(TAG, "-----mylog,GhostDraw");
            //录制模块处理数据
            //avcRecorder.getCodec(false);

            try {
                Thread.sleep(40); // 大约25帧每秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //录制1分钟后停止录制
            if(frame == 25*60) {
                //avcRecorder.stop();

                stopRecording();
                Ghost.GhostReleaseRecordSurface();
                //running = false;
            }

            frame++;
        }

    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        //Log.d(TAG, "-----mylog,SurfaceTexture onFrameAvailable");

    }

}


