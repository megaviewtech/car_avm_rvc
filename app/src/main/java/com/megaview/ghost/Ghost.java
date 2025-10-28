package com.megaview.ghost;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.Surface;

import java.io.FileInputStream;
import java.io.IOException;

public class Ghost {

    static {
        System.loadLibrary("ghost");
    }

    static private Bitmap mBitmap;

    public static int picModeFlag = 1;
    public static int bigCameraFlag = 1;
    public static int sixChannelCameraFlag = 0;

    public static int cameraWidth = 1280;
    public static int cameraHeight = 720;

    public static int CMD_START_RECORD = 0;
    public static int CMD_STOP_RECORD = 1;
    public static int CMD_START_PREVIEW = 2;
    public static int CMD_STOP_PREVIEW = 3;
    public static int CMD_START_ADAS = 4;
    public static int CMD_STOP_ADAS = 5;
    public static int CMD_CAMERA_INPUT_SIZE = 6;
    public static int CMD_RECORDER_OUTPUT_SIZE = 7;
    public static int CMD_PREVIEW_OUTPUT_SIZE = 8;
    public static int CMD_ADAS_OUTPUT_SIZE = 9;
    public static int CMD_BIG_CAMERA = 11;
    public static int CMD_CURRENT_AVM_VIEW = 14;//set avm current view to ghost
    public static int CMD_ADAS_OUTPUT_MODE = 15;//value 1:哨兵模式    0:正常模式
    public static int CMD_SET_SIX_CAMERA = 16;//value 0:    1:six channel camera

    public static int CMD_PRESET_PIC_MODE = 101;


    public static int LoadTexturePng(String file,int textureID){

        try {
            if(file.startsWith("/"))
                mBitmap = BitmapFactory.decodeStream(new FileInputStream(file));


        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        if(mBitmap!=null&&!mBitmap.isRecycled()){

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            // 设置纹理过滤参数
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // 设置纹理包裹参数
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            return textureID;
        }
        return 0;
    }


    //previewSurface,if no preview set null
    //adasSurface,if no adas set null
    public static native int GhostInit(Surface previewSurface,Surface adasSurface);

    public static native int GhostSetPreviewSurface(Surface previewSurface);
    public static native int GhostSetPreviewSurface6(Surface previewSurface);
    public static native int GhostSetRecordSurface(Surface recordSurface);
    public static native int GhostReleaseRecordSurface();

    public static native int GhostSetAdasSurface(Surface adasSurface,int channel);

    //textureArray,input
    public static native int GhostDraw(int[] textureArray);

    //
    public static native int GhostSetCmd(int cmd,int value);
}
