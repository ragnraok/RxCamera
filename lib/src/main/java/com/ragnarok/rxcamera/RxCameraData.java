package com.ragnarok.rxcamera;

import android.graphics.Matrix;
import android.hardware.Camera;

/**
 * Created by ragnarok on 15/11/13.
 * the preview frame data
 */
public class RxCameraData {

    /**
     * the raw preview frame, the format is in YUV if you not set the
     * preview format in the config, it will null on face detect request
     */
    public byte[] cameraData;

    /**
     * a matrix help you rotate the camera data in portrait mode,
     * it will null on face detect request
     */
    public Matrix rotateMatrix;

    /**
     * the face detector return's face list, only has values if you
     * request face detection
     */
    public Camera.Face[] faceList;
}
