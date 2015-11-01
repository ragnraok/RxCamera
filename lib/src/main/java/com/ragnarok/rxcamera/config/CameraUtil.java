package com.ragnarok.rxcamera.config;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

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

    public static int getPortraitCamearaDisplayOrientation(Context context, int cameraId, boolean isFrontCamera) {
        if (cameraId < 0 || cameraId > getCameraNumber()) {
            return -1;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (isFrontCamera) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
