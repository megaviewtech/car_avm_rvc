package com.softwinner.dvr.camera;


public interface CameraListener {
    void onStartPreviewFailed();

    void onOpenCameraFailed();

    void onTakePictureSuccess();

    void onTakePictureFailed();

}
