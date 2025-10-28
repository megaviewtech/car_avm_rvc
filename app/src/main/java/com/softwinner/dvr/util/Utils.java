package com.softwinner.dvr.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;

import com.softwinner.dvr.camera.BaseCamera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Utils {
    private static final String TAG = "Utils";

    /**
     * 获得当前时间的格式化字符串表示，用于文件命名
     *
     * @return
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = formater.format(new Date(System.currentTimeMillis()));
        return date;
    }

    /**
     * 获得当前时间的格式化字符串表示，用于水印
     *
     * @return
     */
    public static String getCurrentTimeForWaterMark() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formater.format(new Date(System.currentTimeMillis()));
        return date;
    }

    /**
     * 判断当前camera是否是松涵的
     *
     * @param cameraId
     * @return
     */
    public static int isUVCCameraSonix(int cameraId) {
        if (cameraId == 0 && new File("/dev/video1").exists()) {
            return 1;
        }

        return cameraId;
    }

    /**
     * 格式化录制时间
     *
     * @param duration
     * @return
     */
    public static String getFormatRecordTime(long duration) {
        int hours = (int) (duration / 3600);
        int temp = (int) duration % 3600;
        int minutes = temp / 60;
        temp %= 60;
        int seconds = temp;

        String result;
        if (String.valueOf(hours).length() == 1) {
            result = ("0" + hours + ":");
        } else {
            result = (hours + ":");
        }
        if (String.valueOf(minutes).length() == 1) {
            result += ("0" + minutes + ":");
        } else {
            result += (minutes + ":");
        }
        if (String.valueOf(seconds).length() == 1) {
            result += ("0" + seconds);
        } else {
            result += seconds;
        }

        return result;
    }

    /**
     * 获得bitmap的size
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static String sizeToString(Camera.Size size) {
        return size.width + "x" + size.height;
    }

    public static Camera.Size stringToSize(String size, BaseCamera camera) {
        if (TextUtils.isEmpty(size)) {
            return null;
        }
        String[] values = size.split("x");
        int width = Integer.parseInt(values[0]);
        int height = Integer.parseInt(values[1]);
        Camera.Size result = null;
        try {
            result = camera.getSupportVideoSize().get(0);
        } catch (IndexOutOfBoundsException e) {
        }

        if (result != null) {
            result.width = width;
            result.height = height;
        }
        return result;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config
                .ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }
}
