package com.ragnarok.rxcamera.config;

import android.graphics.Point;

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

    public void useFrontCamera() {
        configResult.isFaceCamera = true;
        configResult.currentCameraId = CameraUtil.getFrontCameraId();
    }

    public void useBackCamera() {
        configResult.isFaceCamera = false;
        configResult.currentCameraId = CameraUtil.getBackCameraId();
    }

    public void setPreferPreviewSize(Point size) {
        if (size == null) {
            return;
        }
        configResult.preferPreviewSize = size;
    }

    public void setPreferPreviewFrameRate(int minFrameRate, int maxFrameRate) {
        if (minFrameRate <= 0 || maxFrameRate <= 0 || maxFrameRate < minFrameRate) {
            return;
        }
        configResult.minPreferPreviewFrameRate = minFrameRate;
        configResult.maxPreferPreviewFrameRate = maxFrameRate;
    }

    public void setPreviewFormat(int previewFormat) {
        configResult.previewFormat = previewFormat;
    }

    public void setDisplayOrientation(int displayOrientation) {
        configResult.displayOrientation = displayOrientation;
    }

    public void setAutoFocus(boolean isAutoFocus) {
        configResult.isAutoFocus = isAutoFocus;
    }

    private void setProperConfigVal() {
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
    }

    public RxCameraConfig get() {
        setProperConfigVal();
        return configResult;
    }
}
