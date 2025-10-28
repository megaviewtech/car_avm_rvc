package com.softwinner.dvr.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.adapter.VideoFileAdapter;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.widget.BionRecyclerView;
import com.softwinner.dvr.widget.DividerGridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFileFragment extends Fragment {
    private static final String TAG = "VideoFileFragment";
    private static final String ARG_TYPE = "file_type";
    public static final String TYPE_FRONT = "type_front";
    public static final String TYPE_BACK = "type_back";
    public static final String TYPE_LEFT = "type_left";
    public static final String TYPE_RIGHT = "type_right";
    public static final String TYPE_LOCK = "type_lock";

    private String mType = TYPE_FRONT;
    private VideoFileAdapter mAdapter;

    BionRecyclerView mVideoRV;


    public VideoFileFragment() {
    }

    public static VideoFileFragment newInstance(String type) {
        VideoFileFragment fragment = new VideoFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_file, container, false);

        mVideoRV = (BionRecyclerView) view.findViewById(R.id.videoRV);
        init();
        return view;
    }

    private void init() {
        mAdapter = new VideoFileAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        mVideoRV.setLayoutManager(layoutManager);
        mVideoRV.setAdapter(mAdapter);
        mVideoRV.addItemDecoration(new DividerGridItemDecoration(getActivity()));

        if (mType.equals(TYPE_FRONT)) {
            mAdapter.updateData(FileUtils.getFrontVideoFiles());
        } else if (mType.equals(TYPE_BACK)) {
            mAdapter.updateData(FileUtils.getReverseVideoFiles());
        } else if (mType.equals(TYPE_LOCK)) {
            mAdapter.updateData(FileUtils.getLockVideoFiles());
        } else if (mType.equals(TYPE_LEFT)) {
            mAdapter.updateData(FileUtils.getLeftVideoFiles());
        } else if (mType.equals(TYPE_RIGHT)) {
            mAdapter.updateData(FileUtils.getRightVideoFiles());
        }
    }

}
