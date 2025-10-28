package com.softwinner.dvr.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.softwinner.dvr.BuildConfig;
import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.dao.VideoFile;
import com.softwinner.dvr.ui.activity.VideoPlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.VideoFileViewHolder> {
    private List<VideoFile> mVideoInfos = new ArrayList<VideoFile>();
    private Context mContext;
    private RequestOptions mRequestOptions;

    public VideoFileAdapter(Context context) {
        mContext = context;
        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.mipmap.ic_launcher).centerInside();
    }

    public void updateData(List<VideoFile> videoInfos) {

        //2020-10-8,by liugang
        if(videoInfos!=null) {
            Collections.sort(videoInfos, new Comparator<VideoFile>() {
                @Override
                public int compare(VideoFile u1, VideoFile u2) {

                    return u2.getName().compareTo(u1.getName());

                }
            });
        }
        mVideoInfos = videoInfos;
        notifyDataSetChanged();
    }

    @Override
    public VideoFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_file, parent, false);
        return new VideoFileViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VideoFileViewHolder holder, int position) {
        final VideoFile file = mVideoInfos.get(position);
        File tmp = new File(file.getPath());
        String fileName=tmp.getName();
        fileName=fileName.substring(2,17);
        holder.nameTV.setText(fileName);
        //holder.nameTV.setTextColor(12);
        holder.nameTV.setBackgroundColor(12);

        Glide.with(mContext).load(Uri.fromFile(new File(file.getPath()))).apply(mRequestOptions)
                .into(holder.thumbIV);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //leo add
                StartVideoActivity(mContext, file.getPath());
            }
        });
    }

    //leo add
    private  void StartVideoActivity(Context context, String file_name ){
        try {
            File file =new File(file_name);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            String type = (file.getName().endsWith(".mp4")?"video/mp4":"video/ts");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri fileUri = FileProvider.getUriForFile(context, "com.softwinner.dvr.FileProvider", file);
                intent.setDataAndType(fileUri, type);
                GrantUriPermission(context, fileUri, intent);
            }else {
                intent.setDataAndType(Uri.fromFile(file), type);
            }
            context.startActivity(intent);
        }catch (Exception e){ e.printStackTrace(); }
    }

    //leo add
    private  void GrantUriPermission (Context context, Uri fileUri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }


    @Override
    public int getItemCount() {
        if (mVideoInfos == null) {
            return 0;
        }
        return mVideoInfos.size();
    }

    static class VideoFileViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView thumbIV;
        TextView nameTV;

        VideoFileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            thumbIV = (ImageView) itemView.findViewById(R.id.videoThumbIV);
            nameTV = (TextView) itemView.findViewById(R.id.videoNameTV);
        }

    }
}
