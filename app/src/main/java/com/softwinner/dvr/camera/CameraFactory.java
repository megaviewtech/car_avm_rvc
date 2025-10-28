package com.softwinner.dvr.camera;


import com.softwinner.dvr.util.Logger;



public class CameraFactory {
    private static final String TAG = "CameraFactory";

    public static NormalCamera createNormalCamera(int index) {
        Logger.i(TAG, "createNormalCamera index=" + index);
//        NormalCamera normalCamera = new NormalCamera(index);
//        boolean result = normalCamera.openCamera();
//        if (result) {
//            return normalCamera;
//        } else {
//            return null;
//        }
        return new NormalCamera(index);
    }

    public static BaseCamera createFourCvbsCamera() {
        Logger.i(TAG, "createFourCvbsCamera");
//        FourCvbsCamera fourCamera = new FourCvbsCamera();
//        if (fourCamera.openCamera()) {
//            return fourCamera;
//        }
        return new FourCvbsCamera();
    }


    public static BaseCamera createFourCSICamera() {
        Logger.i(TAG, "createFourCSICamera");
//        FourCSICamera fourCSICamera = new FourCSICamera();
//        if (fourCSICamera.openCamera()) {
//            return fourCSICamera;
//        }
        return new FourCSICamera();
    }

    public static BaseCamera createTwoCSICamera() {
//        TwoCSICamera twoCSICamera = new TwoCSICamera();
//        if (twoCSICamera.openCamera()) {
//            return twoCSICamera;
//        }
        return new TwoCSICamera();
    }
}
