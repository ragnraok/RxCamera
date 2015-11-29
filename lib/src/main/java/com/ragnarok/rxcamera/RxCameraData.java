package com.ragnarok.rxcamera;

import android.graphics.Matrix;

/**
 * Created by ragnarok on 15/11/13.
 * the preview frame data
 */
public class RxCameraData {

    /**
     * the raw preview frame, the format is in YUV if you not set the
     * preview format in the config
     */
    public byte[] cameraData;

    /**
     * a matrix help you rotate the camera data
     */
    public Matrix rotateMatrix;
}
