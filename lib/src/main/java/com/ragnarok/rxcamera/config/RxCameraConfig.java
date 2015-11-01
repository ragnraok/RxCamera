package com.ragnarok.rxcamera.config;

import android.graphics.Point;

/**
 * Created by ragnarok on 15/11/1.
 * store the config of camera
 */
public class RxCameraConfig {

    public boolean isFaceCamera = false;

    public int currentCameraId = -1;

    public Point preferPreviewSize = null;

    public int previewFrameRate = -1;

    public int previewForamt = -1;

    public int displayOrientation = -1;

    public boolean isAutoFocus = false;
}
