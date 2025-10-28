package com.softwinner.dvr.util;

import android.content.Context;
import android.os.Parcelable;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dao.VideoFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static final String PATH = "/storage/card";
    /**
     * 反射获取内、外存储设备路径
     *
     * @return
     */
    public static String[] getStoragePaths(Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        Class<?> storageVolumeClazz;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);

            ArrayList<String> pathList = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);

                String state = (String) getState.invoke(storageVolumeElement);
                if ("mounted".equals(state)) {
                    pathList.add(path);
                }
            }
            if (pathList != null && pathList.size() > 0) {
                String paths[] = new String[pathList.size()];
                pathList.toArray(paths);
                return paths;
            }
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存照片
     *
     * @param data
     */
    public static boolean savePicture(String picturePath, byte[] data) {
        Logger.d(TAG, "------savePicture-----" + picturePath);
        File file = new File(picturePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            FileUtils.addFile(picturePath);
            return true;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, "-----Fail To Take Picture For FileNotFoundException----");
            e.printStackTrace();
        } catch (IOException e) {
            Logger.e(TAG, "-----Fail To Take Picture For IOException----");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获得对应路径的剩余空间
     *
     * @param path
     * @return
     */
    public static long getAvailableSpace(String path) {
        if (new File(path).exists()) {
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSizeLong();
            long availCount = statFs.getAvailableBlocksLong();
            return blockSize * availCount;
        } else {
            return -1;
        }
    }

    public static void diskFormat(Context context, String dirPath) {
//        ComponentName formatter = new ComponentName("android", "com.android.internal.os.storage" +
//                ".ExternalStorageFormatter");
//        Intent intent = new Intent("com.android.internal.os.storage.FORMAT_ONLY"); //
//        // ExternalStorageFormatter.FORMAT_ONLY
//        intent.setComponent(formatter);
//        Parcelable sv = getStoragePath(context, dirPath);
//        if (sv != null) {
//            intent.putExtra("storage_volume", sv); // StorageVolume.EXTRA_STORAGE_VOLUME
//            context.startService(intent);
//        } else {
//            Logger.i(TAG, "---diskFormat  sv == null");
//        }

        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        try {
            Logger.i(TAG, "format storage id=" + getStorageId(context, dirPath) + "  success");
            Method unmount = mStorageManager.getClass().getMethod("unmount", String.class);
            unmount.invoke(mStorageManager, getStorageId(context, dirPath));
            Method format = mStorageManager.getClass().getMethod("format", String.class);
            format.invoke(mStorageManager, getStorageId(context, dirPath));
            Method mount = mStorageManager.getClass().getMethod("mount", String.class);
            mount.invoke(mStorageManager, getStorageId(context, dirPath));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    // 获取 StorageVolume 对象
    private static Parcelable getStoragePath(Context mContext, String dirPath) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context
                .STORAGE_SERVICE);

        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Logger.i(TAG, "---getStoragePath  length= " + length);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                Logger.i(TAG, "---getStoragePath  path=" + path);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                Logger.i(TAG, "---getStoragePath  removable=" + removable);
                if (removable && path.equals(dirPath)) {
                    return (Parcelable) storageVolumeElement;
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "---getStoragePath  " + e.getMessage());
            e.printStackTrace();
        }
        Logger.i(TAG, "---getStoragePath  return null");
        return null;
    }

    // 获取 StorageVolume 对象
    private static String getStorageId(Context mContext, String dirPath) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context
                .STORAGE_SERVICE);

        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getId = storageVolumeClazz.getMethod("getId");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Logger.i(TAG, "---getStoragePath  length= " + length);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                Logger.i(TAG, "---getStoragePath  path=" + path);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                Logger.i(TAG, "---getStoragePath  removable=" + removable);
                if (removable && path.equals(dirPath)) {
                    return (String) getId.invoke(storageVolumeElement);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "---getStoragePath  " + e.getMessage());
            e.printStackTrace();
        }
        Logger.i(TAG, "---getStoragePath  return null");
        return null;
    }

    public static boolean haveExtraSdcard(Context context) {
        String[] paths = getStoragePaths(context);
        if (paths == null || paths.length == 0) {
            return false;
        }
        for (String path : paths) {
            Logger.i(TAG, "---haveExtraSdcard  " + path);
            if (path.equals(PATH)) {
                return true;
            }
        }
        return false;
    }

    public static int createPreFile(String file, long size) {
        try {
            Class FileUtil = Class.forName("android.util.FileUtil");
            Object fileUtil = FileUtil.newInstance();
            Method create = FileUtil.getDeclaredMethod("create", String.class, long.class);
            return (Integer) create.invoke(fileUtil, file, size);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return -1;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int releasePreFile(String file) {
        try {
            Class FileUtil = Class.forName("android.util.FileUtil");
            Object fileUtil = FileUtil.newInstance();
            Method release = FileUtil.getMethod("release", String.class, long.class);
            return (Integer) release.invoke(fileUtil, file, 0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void lockFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        Logger.i(TAG, "---lockFile---" + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            if (filePath.contains("front")) {
                filePath = filePath.replace("front", "lock");
            } else if (filePath.contains("reverse")) {
                filePath = filePath.replace("reverse", "lock");
            } else if (filePath.contains("left")) {
                filePath = filePath.replace("left", "lock");
            } else if (filePath.contains("right")) {
                filePath = filePath.replace("right", "lock");
            } else {
                return;
            }
            Logger.i(TAG, "---lockFile-renameTo-->" + filePath);
            File newFile = new File(filePath.substring(0, filePath.lastIndexOf("/")));
            if (!newFile.exists()) {
                newFile.mkdirs();
            }

            file.renameTo(new File(filePath));
            FileUtils.removeVideoFile(file.getAbsolutePath());
            FileUtils.addFile(filePath);
        }
    }


    public static List<String> getSortFiles(String path) {
        //获取列表
        Logger.i(TAG, "---getSortFiles-listFiles--> before");
        List<VideoFile> videoFiles = FileUtils.getVideoFilesByPath(path);
        Logger.i(TAG, "---getSortFiles-listFiles--> after");
        if (videoFiles == null || videoFiles.size() == 0) {
            return null;
        }
        //排序
        ArrayList<String> fileList = new ArrayList<>();
        for (VideoFile tmpFile : videoFiles) {
            if (tmpFile.getName().endsWith(".ts") || tmpFile.getName().endsWith(".mp4")) {
                if (!tmpFile.getPath().contains("lock")) {
                    fileList.add(tmpFile.getPath());
                }
            }
        }
        Logger.i(TAG, "---getSortFiles-sort--> before");
        Collections.sort(fileList);
        Logger.i(TAG, "---getSortFiles-sort--> after");
        return fileList;
    }
}
