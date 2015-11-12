package com.ragnarok.rxcamera.config;

import android.graphics.Point;
import android.hardware.Camera;

/**
 * Created by ragnarok on 15/11/1.
 * a config chooser for RxCamera, after finish choosing the config,
 * return a {@link RxCameraConfig} object
 */
public class RxCameraConfigChooser {

    private RxCameraConfigChooser() {
        configResult = new RxCameraConfig();
    }

    private RxCameraConfig configResult;

    public static RxCameraConfigChooser obtain() {
        return new RxCameraConfigChooser();
    }

    public RxCameraConfigChooser useFrontCamera() {
        configResult.isFaceCamera = true;
        configResult.currentCameraId = CameraUtil.getFrontCameraId();
        return this;
    }

    public RxCameraConfigChooser useBackCamera() {
        configResult.isFaceCamera = false;
        configResult.currentCameraId = CameraUtil.getBackCameraId();
        return this;
    }

    public RxCameraConfigChooser setPreferPreviewSize(Point size) {
        if (size == null) {
            return this;
        }
        configResult.preferPreviewSize = size;
        return this;
    }

    public RxCameraConfigChooser setPreferPreviewFrameRate(int minFrameRate, int maxFrameRate) {
        if (minFrameRate <= 0 || maxFrameRate <= 0 || maxFrameRate < minFrameRate) {
            return this;
        }
        configResult.minPreferPreviewFrameRate = minFrameRate;
        configResult.maxPreferPreviewFrameRate = maxFrameRate;
        return this;
    }

    public RxCameraConfigChooser setPreviewFormat(int previewFormat) {
        configResult.previewFormat = previewFormat;
        return this;
    }

    public RxCameraConfigChooser setDisplayOrientation(int displayOrientation) {
        configResult.displayOrientation = displayOrientation;
        return this;
    }

    public RxCameraConfigChooser setAutoFocus(boolean isAutoFocus) {
        configResult.isAutoFocus = isAutoFocus;
        return this;
    }

    public RxCameraConfigChooser setHandleSurfaceEvent(boolean isHandle) {
        configResult.isHandleSurfaceEvent = isHandle;
        return this;
    }

    public RxCameraConfigChooser setPreviewBufferSize(int size) {
        configResult.previewBufferSize = size;
        return this;
    }

    private RxCameraConfigChooser setProperConfigVal() {
        if (configResult.currentCameraId == -1) {
            if (configResult.isFaceCamera) {
                configResult.currentCameraId = CameraUtil.getFrontCameraId();
            } else {
                configResult.currentCameraId = CameraUtil.getBackCameraId();
            }
        }
        if (configResult.preferPreviewSize == null) {
            configResult.preferPreviewSize = RxCameraConfig.DEFAULT_PREFER_PREVIEW_SIZE;
        }

        Camera.CameraInfo cameraInfo = CameraUtil.getCameraInfo(configResult.currentCameraId);
        if (cameraInfo != null) {
            configResult.cameraOrien = cameraInfo.orientation;
        }
        return this;
    }


    public RxCameraConfig get() {
        setProperConfigVal();
        return configResult;
    }
}
