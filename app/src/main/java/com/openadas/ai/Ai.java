
package com.openadas.ai;

import android.content.res.AssetManager;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;

public class Ai
{
    public static int aiOn = 0;
    public static int aiInitFlag = 0;

    public native static boolean loadModel(AssetManager mgr, int modelid, int mode);
    public native static Surface GetSurface(int width,int height,int rgbaColor,int channel);
    public native static float[] GetObjects(int index);//0 front  1 back  2 left   3 right

    static {
        System.loadLibrary("adas");
    }
}
