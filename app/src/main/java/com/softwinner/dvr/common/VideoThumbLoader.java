package com.softwinner.dvr.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.LruCache;
import android.widget.ImageView;

import com.softwinner.dvr.R;
import com.softwinner.dvr.util.Logger;



public class VideoThumbLoader {
    private static final String TAG = "VideoThumbLoader";
    private ImageView imgView;
    private String path;
    //创建cache
    private LruCache<String, Bitmap> lruCache;
    private Context context;

    @SuppressLint("NewApi")
    public VideoThumbLoader(Context context) {
        int maxSize = 5 * 1024 * 1024;//拿到缓存的内存大小 5MB
        this.context = context;
        Logger.i(TAG, "LruCache maxSize = " + maxSize);
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //这个方法会在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    public void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbFromCache(path) == null) {
            //当前地址没有缓存时，就添加
            lruCache.put(path, bitmap);
        }
    }

    public Bitmap getVideoThumbFromCache(String path) {
        return lruCache.get(path);
    }

    public void showThumbByAsynctack(String path, ImageView imgview) {

        if (getVideoThumbFromCache(path) == null) {
            //异步加载
            new MyBobAsyncTask(imgview, path).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbFromCache(path));
        }

    }

    class MyBobAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBobAsyncTask(ImageView imageView, String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail
            // (vidioPath, kind);
            long start = System.currentTimeMillis();
            Logger.i(TAG, "createVideoThumbnail--file = " + params[0]);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
            mmr.setDataSource(params[0]);
            Bitmap bitmap = mmr.getFrameAtTime();//获得视频第一帧的Bitmap对象
            mmr.release();
            long end = System.currentTimeMillis();

            Logger.i(TAG, "createVideoThumbnail  used " + (end - start) + " ms");
            //加入缓存中
            if (getVideoThumbFromCache(params[0]) == null) {
                Logger.i(TAG, "---cache == null");
                if (bitmap == null) {
                    Logger.i(TAG, "---bitmap == null");
                    bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.mipmap
                            .ic_launcher)).getBitmap();
                }
                Logger.i(TAG, "---bitmap SIZE ==" + bitmap.getByteCount());
                addVideoThumbToCache(path, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (path.equals(imgView.getTag())) {//通过 Tag可以绑定 图片地址和
                // imageView，这是解决Listview加载图片错位的解决办法之一
                imgView.setImageBitmap(bitmap);
            }
        }
    }
}
