package com.ragnarok.rxcamera.config;

import android.graphics.Point;

/**
 * Created by ragnarok on 15/11/1.
 * store the config of camera
 */
public class RxCameraConfig {

    public static Point DEFAULT_PREFER_PREVIEW_SIZE = new Point(320, 240);

    public boolean isFaceCamera = false;

    public int currentCameraId = -1;

    public Point preferPreviewSize = null;

    public int preferPreviewFrameRate = -1;

    public int previewFormat = -1;

    public int displayOrientation = -1;

    public boolean isAutoFocus = false;
}
