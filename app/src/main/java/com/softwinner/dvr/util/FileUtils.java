package com.softwinner.dvr.util;

import android.text.TextUtils;
import android.util.Log;

import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dao.ImageFile;
import com.softwinner.dvr.dao.VideoFile;
import com.softwinner.dvr.dao.VideoFileDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static void updateFileList() {
        DVRApplication.getInstance().getDaoSession().getVideoFileDao().deleteAll();
        updateFrontFileList();
        updateReverseFileList();
        updateLeftFileList();
        updateRightFileList();
        updateLockFileList();
        DVRApplication.getInstance().getDaoSession().getImageFileDao().deleteAll();
        updateImageFileList();
    }

    private static void updateFrontFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getFrontVideoPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<VideoFile> videoFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".ts") || name.endsWith(".mp4")) {
                VideoFile videoFile = new VideoFile(null, name, tmp.getAbsolutePath(), 0);
                videoFileList.add(videoFile);
            }
        }
        if (videoFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insertInTx
                    (videoFileList);
        }
    }

    private static void updateReverseFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getReverseVideoPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<VideoFile> videoFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".ts") || name.endsWith(".mp4")) {
                VideoFile videoFile = new VideoFile(null, name, tmp.getAbsolutePath(), 1);
                videoFileList.add(videoFile);
            }
        }
        if (videoFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insertInTx
                    (videoFileList);
        }
    }

    private static void updateLeftFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getLeftVideoPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<VideoFile> videoFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".ts") || name.endsWith(".mp4")) {
                VideoFile videoFile = new VideoFile(null, name, tmp.getAbsolutePath(), 2);
                videoFileList.add(videoFile);
            }
        }
        if (videoFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insertInTx
                    (videoFileList);
        }
    }

    private static void updateRightFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getRightVideoPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<VideoFile> videoFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".ts") || name.endsWith(".mp4")) {
                VideoFile videoFile = new VideoFile(null, name, tmp.getAbsolutePath(), 3);
                videoFileList.add(videoFile);
            }
        }
        if (videoFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insertInTx
                    (videoFileList);
        }
    }

    private static void updateLockFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getLockPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<VideoFile> videoFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".ts") || name.endsWith(".mp4")) {
                VideoFile videoFile = new VideoFile(null, name, tmp.getAbsolutePath(), 4);
                videoFileList.add(videoFile);
            }
        }
        if (videoFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insertInTx
                    (videoFileList);
        }
    }

    private static void updateImageFileList() {
        File file = new File(DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getPhotoPath());
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] videoFiles = file.listFiles();
        List<ImageFile> imageFileList = new ArrayList<>();
        for (File tmp : videoFiles) {
            String name = tmp.getName();
            if (name.endsWith(".jpg")) {
                ImageFile imageFile = new ImageFile(null, name, tmp.getAbsolutePath());
                imageFileList.add(imageFile);
            }
        }
        if (imageFileList.size() > 0) {
            DVRApplication.getInstance().getDaoSession().getImageFileDao().insertInTx
                    (imageFileList);
        }
    }

    public static List<ImageFile> getImageFileList() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getPhotoPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        return DVRApplication.getInstance().getDaoSession().getImageFileDao().queryBuilder().list();
    }

    public static List<VideoFile> getFrontVideoFiles() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getFrontVideoPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }

        Log.d(TAG, "---getFrontVideoFiles: ");
        return DVRApplication.getInstance().getDaoSession().getVideoFileDao().queryBuilder()
                .where(VideoFileDao.Properties.Type.eq(0)).list();
    }

    public static List<VideoFile> getVideoFilesByPath(String path) {
        Logger.i(TAG, "getVideoFilesByPath----path=" + path);
        if (TextUtils.isEmpty(path)) {
            Logger.e(TAG, "getVideoFilesByPath----path isEmpty");
            return null;
        }

        if (path.contains("f_")) {
            return getFrontVideoFiles();
        } else if (path.contains("b_")) {
            return getReverseVideoFiles();
        } else if (path.contains("r_")) {
            return getRightVideoFiles();
        } else if (path.contains("l_")) {
            return getLeftVideoFiles();
        }
        Logger.e(TAG, "getVideoFilesByPath----return null");
        return null;
    }

    public static List<VideoFile> getReverseVideoFiles() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getReverseVideoPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        return DVRApplication.getInstance().getDaoSession().getVideoFileDao().queryBuilder()
                .where(VideoFileDao.Properties.Type.eq(1)).list();
    }

    public static List<VideoFile> getLeftVideoFiles() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getLeftVideoPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        return DVRApplication.getInstance().getDaoSession().getVideoFileDao().queryBuilder()
                .where(VideoFileDao.Properties.Type.eq(2)).list();
    }

    public static List<VideoFile> getRightVideoFiles() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getRightVideoPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        return DVRApplication.getInstance().getDaoSession().getVideoFileDao().queryBuilder()
                .where(VideoFileDao.Properties.Type.eq(3)).list();
    }

    public static List<VideoFile> getLockVideoFiles() {
        String fileName = DVRPreference.getInstance(DVRApplication.getInstance()
                .getApplicationContext()).getLockPath();
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
            return null;
        }
        return DVRApplication.getInstance().getDaoSession().getVideoFileDao().queryBuilder()
                .where(VideoFileDao.Properties.Type.eq(4)).list();
    }

    public static void addFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        if (fileName.endsWith(".ts") || fileName.endsWith(".mp4")) {
            int type = 0;
            if (DVRPreference.getInstance(DVRApplication.getInstance().getApplicationContext())
                    .getFrontVideoPath().contains(file.getParent())) {
                type = 0;
            } else if (DVRPreference.getInstance(DVRApplication.getInstance()
                    .getApplicationContext()).getReverseVideoPath().contains(file.getParent())) {
                type = 1;
            } else if (DVRPreference.getInstance(DVRApplication.getInstance()
                    .getApplicationContext()).getLeftVideoPath().contains(file.getParent())) {
                type = 2;
            } else if (DVRPreference.getInstance(DVRApplication.getInstance()
                    .getApplicationContext()).getRightVideoPath().contains(file.getParent())) {
                type = 3;
            } else if (DVRPreference.getInstance(DVRApplication.getInstance()
                    .getApplicationContext()).getLockPath().contains(file.getParent())) {
                type = 4;
            }
            VideoFile videoFile = new VideoFile(null, file.getName(), fileName, type);
            DVRApplication.getInstance().getDaoSession().getVideoFileDao().insert(videoFile);
        } else if (fileName.endsWith("jpg")) {
            ImageFile imageFile = new ImageFile(null, file.getName(), fileName);
            DVRApplication.getInstance().getDaoSession().getImageFileDao().insert(imageFile);
        }
    }

    public static void removeVideoFile(String file) {
        if (TextUtils.isEmpty(file)) {
            return;
        }
        VideoFile videoFile = DVRApplication.getInstance().getDaoSession().getVideoFileDao()
                .queryBuilder().where(VideoFileDao.Properties.Path.eq(file)).unique();
        if (videoFile == null) {
            return;
        }

        DVRApplication.getInstance().getDaoSession().getVideoFileDao().delete(videoFile);
    }
}
