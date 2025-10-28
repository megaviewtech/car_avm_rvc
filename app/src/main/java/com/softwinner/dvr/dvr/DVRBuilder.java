package com.softwinner.dvr.dvr;

import android.content.Context;

import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.camera.CameraFactory;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.util.Logger;



public class DVRBuilder {
    private static final String TAG = "DVRBuilder";

    public static BaseDVR build(Context context) {
        BaseDVR result = null;
        switch (DVRPreference.getInstance(context).getDVRType()) {
            case DVRPreference.DVR_TYPE_SINGLE_CAMERA:
            case DVRPreference.DVR_TYPE_CSI_FOUR_TO_ONE:
            case DVRPreference.DVR_TYPE_CVBS_FOUR_TO_ONE:
                result = oneCameraDVRBuild(context);
                break;
            case DVRPreference.DVR_TYPE_CSI_TWO_TWO:
            case DVRPreference.DVR_TYPE_TWO_CAMERA:
                result = twoCameraDVRBuild(context);
                break;
            case DVRPreference.DVR_TYPE_FOUR_CVBS:
            case DVRPreference.DVR_TYPE_FOUR_CSI:
                result = fourCameraDVRBuild(context);
                break;
        }

        return result;
    }

    private static OneCameraDVR oneCameraDVRBuild(Context context) {
        BaseCamera camera = null;
        switch (DVRPreference.getInstance(context).getDVRType()) {
            case DVRPreference.DVR_TYPE_SINGLE_CAMERA:
                camera = CameraFactory.createNormalCamera(DVRPreference.getInstance(context)
                        .getFrontCameraId());
                break;
            case DVRPreference.DVR_TYPE_CVBS_FOUR_TO_ONE:
                camera = CameraFactory.createFourCvbsCamera();
                break;
            case DVRPreference.DVR_TYPE_CSI_FOUR_TO_ONE:
                camera = CameraFactory.createFourCSICamera();
                break;
        }

        if (camera != null) {
            return new OneCameraDVR(camera, context);
        } else {
            return null;
        }

    }

    private static TwoCameraDVR twoCameraDVRBuild(Context context) {
        BaseCamera frontCamera = null;
        BaseCamera reverseCamera = null;
        switch (DVRPreference.getInstance(context).getDVRType()) {
            case DVRPreference.DVR_TYPE_CSI_TWO_TWO:
                frontCamera = CameraFactory.createTwoCSICamera();
                reverseCamera = CameraFactory.createTwoCSICamera();
                break;
            case DVRPreference.DVR_TYPE_TWO_CAMERA:
                Logger.i(TAG, "twoCameraDVRBuild front id=" + DVRPreference.getInstance(context)
                        .getFrontCameraId() + " reverse id=" + DVRPreference.getInstance(context)
                        .getReverseCameraId());
                frontCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance(context)
                        .getFrontCameraId());
                reverseCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance
                        (context).getReverseCameraId());
                break;
        }

        if ((frontCamera != null) && (reverseCamera != null)) {
            return new TwoCameraDVR(frontCamera, reverseCamera, context);
        } else {
            return null;
        }

    }

    private static FourCameraDVR fourCameraDVRBuild(Context context) {
        BaseCamera frontCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance
                (context).getFrontCameraId());
        BaseCamera reverseCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance
                (context).getReverseCameraId());
        BaseCamera leftCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance
                (context).getLeftCameraId());
        BaseCamera rightCamera = CameraFactory.createNormalCamera(DVRPreference.getInstance
                (context).getRightCameraId());

        if ((frontCamera != null) && (reverseCamera != null) && (leftCamera != null) &&
                (rightCamera != null)) {
            return new FourCameraDVR(frontCamera, reverseCamera, leftCamera, rightCamera, context);
        } else {
            return null;
        }

    }

}
