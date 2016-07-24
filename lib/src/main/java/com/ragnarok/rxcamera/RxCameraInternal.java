package com.ragnarok.rxcamera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.ragnarok.rxcamera.config.CameraUtil;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.error.BindSurfaceFailedException;
import com.ragnarok.rxcamera.error.OpenCameraException;
import com.ragnarok.rxcamera.error.OpenCameraFailedReason;
import com.ragnarok.rxcamera.error.StartPreviewFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ragnarok on 15/11/13.
 * the internal logic of camera
 */
public class RxCameraInternal implements SurfaceCallback.SurfaceListener, Camera.PreviewCallback {

    private static final String TAG = "RxCamera.CameraInternal";

    // the native camera object
    private Camera camera;

    // the camera config
    private RxCameraConfig cameraConfig;

    private Context context;

    private boolean isBindSurface = false;
    private boolean isOpenCamera = false;

    private SurfaceView bindSurfaceView;
    private TextureView bindTextureView;

    private SurfaceCallback surfaceCallback = new SurfaceCallback();

    private boolean isSurfaceAvailable = false;
    private boolean isNeedStartPreviewLater = false;

    private List<byte[]> callbackBuffList;
    private static final int CALLBACK_BUFF_COUNT = 3;

    // open camera error
    private OpenCameraFailedReason openCameraFailedReason;
    private Throwable openCameraFailedCause;

    // bind surface error
    private String bindSurfaceFailedMessage;
    private Throwable bindSurfaceFailedCause;

    // start preview error
    private String previewFailedMessage;
    private Throwable previewFailedCause;

    private Point finalPreviewSize;

    private boolean isSetPreviewCallback = false;
    private List<OnRxCameraPreviewFrameCallback> previewFrameCallbackList = new ArrayList<>();
    private List<OnRxCameraPreviewFrameCallback> oneshotPrevieFrameCallbackList = new ArrayList<>();

    public void setConfig(RxCameraConfig config) {
        this.cameraConfig = config;
    }

    public RxCameraConfig getConfig() {
        return this.cameraConfig;
    }

    public Camera getNativeCamera() {
        return this.camera;
    }

    public Point getFinalPreviewSize() {
        return finalPreviewSize;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public boolean openCameraInternal() {
        reset();
        if (cameraConfig == null) {
            openCameraFailedReason = OpenCameraFailedReason.PARAMETER_ERROR;
            return false;
        }
        surfaceCallback = new SurfaceCallback();
        // open camera
        try {
            this.camera = Camera.open(cameraConfig.currentCameraId);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.OPEN_FAILED;
            openCameraFailedCause = e;
            Log.e(TAG, "open camera failed: " + e.getMessage());
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = camera.getParameters();
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.GET_PARAMETER_FAILED;
            openCameraFailedCause = e;
            Log.e(TAG, "get parameter failed: " + e.getMessage());
        }

        if (parameters == null) {
            openCameraFailedReason = OpenCameraFailedReason.GET_PARAMETER_FAILED;
            return false;
        }

        // set fps
        if (cameraConfig.minPreferPreviewFrameRate != -1 && cameraConfig.maxPreferPreviewFrameRate != -1) {
            try {
                int[] range = CameraUtil.findClosestFpsRange(camera, cameraConfig.minPreferPreviewFrameRate, cameraConfig.maxPreferPreviewFrameRate);
                parameters.setPreviewFpsRange(range[0], range[1]);
            } catch (Exception e) {
                openCameraFailedReason = OpenCameraFailedReason.SET_FPS_FAILED;
                openCameraFailedCause = e;
                Log.e(TAG, "set preview fps range failed: " + e.getMessage());
                return false;
            }
        }
        // set preview size;
        if (cameraConfig.preferPreviewSize != null) {
            try {
                //check wether squared preview is accepted or not.
                if(cameraConfig.acceptSquarePreview) {
                    Camera.Size previewSize = CameraUtil.findClosestPreviewSize(camera, cameraConfig.preferPreviewSize);
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    finalPreviewSize = new Point(previewSize.width, previewSize.height);
                } else {
                    Camera.Size previewSize = CameraUtil.findClosestNonSquarePreviewSize(camera, cameraConfig.preferPreviewSize);
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    finalPreviewSize = new Point(previewSize.width, previewSize.height);
                }
            } catch (Exception e) {
                openCameraFailedReason = OpenCameraFailedReason.SET_PREVIEW_SIZE_FAILED;
                openCameraFailedCause = e;
                Log.e(TAG, "set preview size failed: " + e.getMessage());
                return false;
            }
        }

        // set format
        if (cameraConfig.previewFormat != -1) {
            try {
                parameters.setPreviewFormat(cameraConfig.previewFormat);
                parameters.setPictureFormat(ImageFormat.JPEG);
            } catch (Exception e) {
                openCameraFailedReason = OpenCameraFailedReason.SET_PREVIEW_FORMAT_FAILED;
                openCameraFailedCause = e;
                Log.e(TAG, "set preview format failed: " + e.getMessage());
                return false;
            }
        }

        // set auto focus
        if (cameraConfig.isAutoFocus) {
            try {
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            } catch (Exception e) {
                Log.e(TAG, "set auto focus failed: " + e.getMessage());
                openCameraFailedReason = OpenCameraFailedReason.SET_AUTO_FOCUS_FAILED;
                openCameraFailedCause = e;
                return false;
            }
        }

        // set enableShutterSound (only supported for API 17 and newer)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && cameraConfig.muteShutterSound) {
            if (CameraUtil.canDisableShutter(cameraConfig.currentCameraId)) {
                camera.enableShutterSound(false);
            }
        }

        // set all parameters
        try {
            camera.setParameters(parameters);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.SET_PARAMETER_FAILED;
            openCameraFailedCause = e;
            Log.e(TAG, "set final parameter failed: " + e.getMessage());
            return false;
        }

        // set display orientation
        int displayOrientation = cameraConfig.displayOrientation;
        if (displayOrientation == -1) {
            displayOrientation = CameraUtil.getPortraitCameraDisplayOrientation(context, cameraConfig.currentCameraId, cameraConfig.isFaceCamera);
        }
        try {
            camera.setDisplayOrientation(displayOrientation);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.SET_DISPLAY_ORIENTATION_FAILED;
            openCameraFailedCause = e;
            Log.e(TAG, "open camera failed: " + e.getMessage());
            return false;
        }
        isOpenCamera = true;
        return true;
    }

    public OpenCameraException openCameraException() {
        return new OpenCameraException(openCameraFailedReason, openCameraFailedCause);
    }

    public boolean isBindSurface() {
        return isBindSurface;
    }

    public boolean isOpenCamera() {
        return isOpenCamera;
    }

    public boolean installPreviewCallback(OnRxCameraPreviewFrameCallback onRxCameraPreviewFrameCallback) {
        if (isOpenCamera) {
            if (callbackBuffList == null) {
                initCallbackBuffList();
            }
            for (int i = 0; i < callbackBuffList.size(); i++) {
                camera.addCallbackBuffer(callbackBuffList.get(i));
            }
            this.previewFrameCallbackList.add(onRxCameraPreviewFrameCallback);
            if (!isSetPreviewCallback) {
                camera.setPreviewCallbackWithBuffer(this);
                isSetPreviewCallback = true;
            }
            return true;
        }

        return false;
    }

    public boolean installOneShotPreviewCallback(OnRxCameraPreviewFrameCallback onRxCameraPreviewFrameCallback) {
        if (isOpenCamera) {
            this.oneshotPrevieFrameCallbackList.add(onRxCameraPreviewFrameCallback);
            camera.setOneShotPreviewCallback(this);
            isSetPreviewCallback = false; // the oneshot callback will only be called once
            return true;
        }
        return false;
    }

    public boolean uninstallPreviewCallback(OnRxCameraPreviewFrameCallback onRxCameraPreviewFrameCallback) {
        return previewFrameCallbackList.remove(onRxCameraPreviewFrameCallback);
    }

    public boolean uninstallOneShotPreviewCallback(OnRxCameraPreviewFrameCallback onRxCameraPreviewFrameCallback) {
        return oneshotPrevieFrameCallbackList.remove(onRxCameraPreviewFrameCallback);
    }

    private void initCallbackBuffList() {
        int buffSize = cameraConfig.previewBufferSize;
        if (cameraConfig.previewBufferSize == -1) {
            buffSize = getPreviewBufferSizeFromParameter();
        }
        callbackBuffList = new ArrayList<>();
        for (int i = 0; i < CALLBACK_BUFF_COUNT; i++) {
            callbackBuffList.add(new byte[buffSize]);
        }
    }

    private int getPreviewBufferSizeFromParameter() {
        Log.d(TAG, "getPreviewBufferSizeFromParameter, previewFormat: " + camera.getParameters().getPreviewFormat() + ", " +
                "previewSize: " + camera.getParameters().getPreviewSize() + ", bitsPerPixels: " +
                ImageFormat.getBitsPerPixel(camera.getParameters().getPreviewFormat()));
        if (camera.getParameters().getPreviewFormat() == ImageFormat.YV12) {
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;
            int yStride = (int) Math.ceil(width / 16.0) * 16;
            int uvStride = (int) Math.ceil((yStride / 2) / 16.0) * 16;
            int ySize = yStride * height;
            int uvSize = uvStride * height / 2;
            int size = ySize + uvSize * 2;
            return size;
        } else {
            return camera.getParameters().getPreviewSize().width *
                    camera.getParameters().getPreviewSize().height *
                    ImageFormat.getBitsPerPixel(camera.getParameters().getPreviewFormat()) / 8;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        for (OnRxCameraPreviewFrameCallback callback : previewFrameCallbackList) {
            callback.onPreviewFrame(data);
        }
        for (OnRxCameraPreviewFrameCallback callback : oneshotPrevieFrameCallbackList) {
            callback.onPreviewFrame(data);
        }
        oneshotPrevieFrameCallbackList.clear();
        camera.addCallbackBuffer(data);
    }

    public boolean bindSurfaceInternal(SurfaceView surfaceView) {
        if (camera == null || isBindSurface || surfaceView == null) {
            return false;
        }
        try {
            bindSurfaceView = surfaceView;
            if (cameraConfig.isHandleSurfaceEvent) {
                surfaceCallback.setSurfaceListener(this);
                bindSurfaceView.getHolder().addCallback(surfaceCallback);
            }
            if (bindSurfaceView.getHolder() != null) {
                bindSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                camera.setPreviewDisplay(surfaceView.getHolder());
            }
            isBindSurface = true;
        } catch (Exception e) {
            bindSurfaceFailedMessage = e.getMessage();
            bindSurfaceFailedCause = e;
            Log.e(TAG, "bindSurface failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public BindSurfaceFailedException bindSurfaceFailedException() {
        return new BindSurfaceFailedException(bindSurfaceFailedMessage, bindSurfaceFailedCause);
    }

    public boolean bindTextureInternal(TextureView textureView) {
        if (camera == null || isBindSurface || textureView == null) {
            return false;
        }
        try {
            bindTextureView = textureView;
            if (cameraConfig.isHandleSurfaceEvent) {
                surfaceCallback.setSurfaceListener(this);
                bindTextureView.setSurfaceTextureListener(surfaceCallback);
            }
            if (bindTextureView.getSurfaceTexture() != null) {
                camera.setPreviewTexture(bindTextureView.getSurfaceTexture());
            }
            isBindSurface = true;
        } catch (Exception e) {
            bindSurfaceFailedMessage = e.getMessage();
            bindSurfaceFailedCause = e;
            Log.e(TAG, "bindSurfaceTexture failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean startPreviewInternal() {
        if (camera == null || !isBindSurface) {
            return false;
        }
        try {
            isSurfaceAvailable = false;
            if (bindTextureView != null && bindTextureView.isAvailable()) {
                isSurfaceAvailable = true;
            }
            if (bindSurfaceView != null && bindSurfaceView.getWindowToken() != null
                    && bindSurfaceView.getHolder() != null && !bindSurfaceView.getHolder().isCreating()) {
                isSurfaceAvailable = true;
            }
            if (!isSurfaceAvailable && cameraConfig.isHandleSurfaceEvent) {
                isNeedStartPreviewLater = true;
                return true;
            }
            camera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "start preview failed: " + e.getMessage());
            previewFailedMessage = e.getMessage();
            previewFailedCause = e;
            return false;
        }
        return true;
    }

    public StartPreviewFailedException startPreviewFailedException() {
        return new StartPreviewFailedException(previewFailedMessage, previewFailedCause);
    }

    public boolean closeCameraInternal() {
        if (camera == null) {
            return false;
        }
        try {
            camera.setPreviewCallback(null);
            camera.release();
            reset();
        } catch (Exception e) {
            Log.e(TAG, "close camera failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean switchCameraInternal() {
        if (camera == null) {
            return false;
        }
        try {
            camera.setPreviewCallback(null);
            camera.release();

            RxCameraConfig.Builder builder = new RxCameraConfig.Builder();
            builder.from(getConfig());
            if (getConfig().isFaceCamera) {
                builder.useBackCamera();
            } else {
                builder.useFrontCamera();
            }
            this.cameraConfig = builder.build();

            if (bindSurfaceView != null) {
                SurfaceView oldSurfaceView = bindSurfaceView;
                openCameraInternal();
                bindSurfaceInternal(oldSurfaceView);
            } else if (bindTextureView != null) {
                TextureView oldTextureView = bindTextureView;
                openCameraInternal();
                bindTextureInternal(oldTextureView);
            }
            return startPreviewInternal();

        } catch (Exception e) {
            Log.e(TAG, "switchCamera error: " + e.getMessage());
            return false;
        }
    }

    public void onAvailable() {
        if (isNeedStartPreviewLater) {
            try {
                if (bindSurfaceView != null) {
                    bindSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    camera.setPreviewDisplay(bindSurfaceView.getHolder());
                } else if (bindTextureView != null) {
                    camera.setPreviewTexture(bindTextureView.getSurfaceTexture());
                }
                camera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "onAvailable, start preview failed");
            }
        }
    }


    @Override
    public void onDestroy() {
        isSurfaceAvailable = false;
    }

    private void reset() {
        isBindSurface = false;
        isOpenCamera = false;
        isNeedStartPreviewLater = false;
        isSurfaceAvailable = false;
        openCameraFailedCause = null;
        openCameraFailedReason = null;
        previewFailedCause = null;
        previewFailedMessage = null;
        bindSurfaceFailedCause = null;
        bindSurfaceFailedMessage = null;
        finalPreviewSize = null;
        previewFrameCallbackList.clear();
        oneshotPrevieFrameCallbackList.clear();
        if (bindSurfaceView != null && surfaceCallback != null) {
            bindSurfaceView.getHolder().removeCallback(surfaceCallback);
        }
        bindSurfaceView = null;
        if (bindTextureView != null) {
            bindTextureView.setSurfaceTextureListener(null);
        }
        bindTextureView = null;
        if (surfaceCallback != null) {
            surfaceCallback.setSurfaceListener(null);
            surfaceCallback = null;
        }
    }
}
