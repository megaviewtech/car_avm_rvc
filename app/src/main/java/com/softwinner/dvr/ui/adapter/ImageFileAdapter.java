package com.softwinner.dvr.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.softwinner.dvr.R;
import com.softwinner.dvr.bean.ImageInfo;
import com.softwinner.dvr.dao.ImageFile;
import com.softwinner.dvr.ui.activity.ImageActivity;

import java.util.ArrayList;
import java.util.List;



public class ImageFileAdapter extends RecyclerView.Adapter<ImageFileAdapter.ImageFileViewHolder> {
    private List<ImageFile> mImageFiles = new ArrayList<ImageFile>();
    private Context mContext;
    private RequestOptions mRequestOptions;

    public ImageFileAdapter(Context context) {
        mContext = context;
        mRequestOptions = new RequestOptions();
        mRequestOptions.placeholder(R.mipmap.ic_launcher);
    }

    public void updateData(List<ImageFile> imageFiles) {
        mImageFiles = imageFiles;
        notifyDataSetChanged();
    }

    @Override
    public ImageFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image_file, null);

        return new ImageFileViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ImageFileViewHolder holder, int position) {
        final ImageFile imageFile = mImageFiles.get(position);
        Glide.with(mContext).load(imageFile.getPath()).apply(mRequestOptions).into(holder.thumbIV);
        holder.nameTV.setText(imageFile.getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path", imageFile.getPath());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mImageFiles == null) {
            return 0;
        }
        return mImageFiles.size();
    }

    static class ImageFileViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView thumbIV;
        TextView nameTV;

        ImageFileViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            thumbIV = (ImageView) itemView.findViewById(R.id.imageThumbIV);
            nameTV = (TextView) itemView.findViewById(R.id.imageNameTV);
        }


    }
}
