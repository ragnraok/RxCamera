package com.ragnarok.rxcamera.config;

import android.graphics.Point;

/**
 * Created by ragnarok on 15/11/1.
 * store the config of camera, you must set the config from {@link RxCameraConfigChooser}
 */
public class RxCameraConfig {

    public static Point DEFAULT_PREFER_PREVIEW_SIZE = new Point(320, 240);

    public boolean isFaceCamera = false;

    public int currentCameraId = -1;

    public Point preferPreviewSize = null;

    public boolean acceptSquarePreview = true;

    public int minPreferPreviewFrameRate = -1;

    public int maxPreferPreviewFrameRate = -1;

    public int previewFormat = -1;

    public int displayOrientation = -1;

    public boolean isAutoFocus = false;

    public int previewBufferSize = -1;

    public boolean isHandleSurfaceEvent = false;

    public int cameraOrien = -1;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("RxCameraConfig ");
        result.append(String.format("isFaceCamera: %b, currentCameraId: %d, ", isFaceCamera, currentCameraId));
        result.append(String.format("preferPreviewSize: %s, ", preferPreviewSize));
        result.append(String.format("minPreferPreviewFrameRate: %d, maxPreferPreviewFrameRate: %d, ", minPreferPreviewFrameRate, maxPreferPreviewFrameRate));
        result.append(String.format("previewFormat: %d, ", previewFormat));
        result.append(String.format("displayOrientation: %d, ", displayOrientation));
        result.append(String.format("isAutoFocus: %b", isAutoFocus));
        result.append(String.format("previewBufferSize: %d, ", previewBufferSize));
        result.append(String.format("isHandleSurfaceEvent: %b, ", isHandleSurfaceEvent));
        result.append(String.format("cameraOrien: %d, ", cameraOrien));
        result.append(String.format("acceptSquarePreview: %s", acceptSquarePreview));
        return result.toString();
    }
}
