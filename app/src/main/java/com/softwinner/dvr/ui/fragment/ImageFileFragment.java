package com.softwinner.dvr.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softwinner.dvr.R;
import com.softwinner.dvr.bean.ImageInfo;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.adapter.ImageFileAdapter;
import com.softwinner.dvr.util.FileUtils;
import com.softwinner.dvr.util.StorageUtils;
import com.softwinner.dvr.widget.BionRecyclerView;
import com.softwinner.dvr.widget.DividerGridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFileFragment extends Fragment {
    private static final String TAG = "ImageFileFragment";

    private ImageFileAdapter mAdapter;

    BionRecyclerView mPictureRV;


    public ImageFileFragment() {
    }

    public static ImageFileFragment newInstance() {
        ImageFileFragment fragment = new ImageFileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_file, container, false);

        mPictureRV = (BionRecyclerView) view.findViewById(R.id.pictureRV);
        init();

        return view;
    }

    private void init() {
        mAdapter = new ImageFileAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mPictureRV.setLayoutManager(layoutManager);
        mPictureRV.setAdapter(mAdapter);
        mPictureRV.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mAdapter.updateData(FileUtils.getImageFileList());
    }


}
