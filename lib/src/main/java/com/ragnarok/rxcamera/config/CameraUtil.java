package com.ragnarok.rxcamera.config;

import android.hardware.Camera;

/**
 * Created by ragnarok on 15/11/1.
 * some utilities for retrieve system camera config
 */
public class CameraUtil {

    private static int frontCameraId = -1;
    private static int backCameraId = -1;
    private static int cameraNumber = -1;

    public static int getCameraNumber() {
        if (cameraNumber == -1) {
            cameraNumber = Camera.getNumberOfCameras();
        }
        return cameraNumber;
    }

    public static int getFrontCameraId() {
        if (frontCameraId == -1) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < getCameraNumber(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCameraId = i;
                    break;
                }
            }
        }
        return frontCameraId;
    }

    public static int getBackCameraId() {
        if (backCameraId == -1) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < getCameraNumber(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backCameraId = i;
                    break;
                }
            }
        }
        return backCameraId;
    }
}
