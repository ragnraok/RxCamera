package com.ragnarok.rxcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.ragnarok.rxcamera.config.CameraUtil;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.error.OpenCameraExecption;
import com.ragnarok.rxcamera.error.OpenCameraFailedReason;
import com.ragnarok.rxcamera.error.StartPreviewFailedException;
import com.ragnarok.rxcamera.listeners.SurfaceCallback;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ragnarok on 15/10/25.
 */
public class RxCamera implements SurfaceCallback.SurfaceListener {

    private static final String TAG = "RxCamera";

    // the native camera object
    private Camera camera;

    // the camera config
    private RxCameraConfig cameraConfig;

    private Context context;

    private OpenCameraFailedReason openCameraFailedReason;

    private boolean isBindSurface = false;

    private SurfaceView bindSurfaceView;
    private TextureView bindTextureView;

    private SurfaceCallback surfaceCallback = new SurfaceCallback();

    private boolean isSurfaceAvailable = false;
    private boolean isNeedStartPreviewLater = false;

    public static Observable<RxCamera> open(final Context context, final RxCameraConfig config) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                RxCamera rxCamera = new RxCamera(context);
                rxCamera.cameraConfig = config;
                if (rxCamera.openCamera()) {
                    subscriber.onNext(rxCamera);
                } else {
                    subscriber.onError(new OpenCameraExecption(rxCamera.openCameraFailedReason));
                }
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final SurfaceView surfaceView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(final RxCamera rxCamera) {
                return Observable.create(new Observable.OnSubscribe<RxCamera>() {
                    @Override
                    public void call(Subscriber<? super RxCamera> subscriber) {
                        rxCamera.bindSurface(surfaceView);
                        boolean result = rxCamera.startPreviewInternal();
                        if (result) {
                            subscriber.onNext(rxCamera);
                        } else {
                            subscriber.onError(new StartPreviewFailedException());
                        }
                    }
                });
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final TextureView textureView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(final RxCamera rxCamera) {
                return Observable.create(new Observable.OnSubscribe<RxCamera>() {
                    @Override
                    public void call(Subscriber<? super RxCamera> subscriber) {
                        rxCamera.bindTexture(textureView);
                        boolean result = rxCamera.startPreviewInternal();
                        if (result) {
                            subscriber.onNext(rxCamera);
                        } else {
                            subscriber.onError(new StartPreviewFailedException());
                        }
                    }
                });
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreviewWithPreviewCallback(SurfaceHolder surfaceHolder) {
        return null;
    }

    public static Observable<RxCamera> openAndStartPreviewWithPreviewCallback(SurfaceTexture surfaceTexture) {
        return null;
    }

    private RxCamera(Context context) {
        this.context = context;
        this.surfaceCallback.setSurfaceListener(this);
    }

    public boolean bindSurface(SurfaceView surfaceView) {
        if (camera == null || isBindSurface || surfaceView == null) {
            return false;
        }
        try {
            bindSurfaceView = surfaceView;
            if (cameraConfig.isHandleSurfaceEvent) {
                bindSurfaceView.getHolder().addCallback(surfaceCallback);
            }
            if (surfaceView.getHolder() != null) {
                camera.setPreviewDisplay(surfaceView.getHolder());
            }
            isBindSurface = true;
        } catch (Exception e) {
            Log.e(TAG, "bindSurface failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean bindTexture(TextureView textureView) {
        if (camera == null || isBindSurface || textureView == null) {
            return false;
        }
        try {
            bindTextureView = textureView;
            if (cameraConfig.isHandleSurfaceEvent) {
                bindTextureView.setSurfaceTextureListener(surfaceCallback);
            }
            if (bindTextureView.getSurfaceTexture() != null) {
                camera.setPreviewTexture(bindTextureView.getSurfaceTexture());
            }
            isBindSurface = true;
        } catch (Exception e) {
            Log.e(TAG, "bindSurfaceTexture failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Observable<Boolean> startPreview() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result = startPreviewInternal();
                subscriber.onNext(result);
            }
        });
    }

    private boolean startPreviewInternal() {
        if (camera == null || !isBindSurface) {
            return false;
        }
        try {
            if (bindTextureView != null && bindTextureView.isAvailable()) {
                isSurfaceAvailable = true;
            }
            if (!isSurfaceAvailable && cameraConfig.isHandleSurfaceEvent) {
                isNeedStartPreviewLater = true;
                return true;
            }
            camera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "start preview failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean closeCamera() {
        if (camera == null) {
            return false;
        }
        try {
            camera.setPreviewCallback(null);
            camera.release();
            isBindSurface = false;
            isNeedStartPreviewLater = false;
            isSurfaceAvailable = false;
        } catch (Exception e) {
            Log.e(TAG, "close camera failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Camera getNativeCamera() {
        return camera;
    }

    public RxCameraConfig getCameraConfig() {
        return cameraConfig;
    }

    private boolean openCamera() {
        if (cameraConfig == null) {
            openCameraFailedReason = OpenCameraFailedReason.PARAMETER_ERROR;
            return false;
        }
        // open camera
        try {
            this.camera = Camera.open(cameraConfig.currentCameraId);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.OPEN_FAILED;
            Log.e(TAG, "open camera failed: " + e.getMessage());
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = camera.getParameters();
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.GET_PARAMETER_FAILED;
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
                Log.e(TAG, "set preview fps range failed: " + e.getMessage());
                return false;
            }
        }
        // set preview size;
        if (cameraConfig.preferPreviewSize != null) {
            try {
                Camera.Size previewSize = CameraUtil.findClosetPreviewSize(camera, cameraConfig.preferPreviewSize);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
            } catch (Exception e) {
                openCameraFailedReason = OpenCameraFailedReason.SET_PREVIEW_SIZE_FAILED;
                Log.e(TAG, "set preview size failed: " + e.getMessage());
                return false;
            }
        }

        // set format
        if (cameraConfig.previewFormat != -1) {
            try {
                parameters.setPreviewFormat(cameraConfig.previewFormat);
            } catch (Exception e) {
                openCameraFailedReason = OpenCameraFailedReason.SET_PREVIEW_FORMAT_FAILED;
                Log.e(TAG, "set preview format failed: " + e.getMessage());
                return false;
            }
        }

        // set auto focus
        if (cameraConfig.isAutoFocus) {
            try {
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
            } catch (Exception e) {
                Log.e(TAG, "set auto focus failed: " + e.getMessage());
                openCameraFailedReason = OpenCameraFailedReason.SET_AUTO_FOCUS_FAILED;
                return false;
            }
        }

        // set all parameters
        try {
            camera.setParameters(parameters);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.SET_PARAMETER_FAILED;
            Log.e(TAG, "set final parameter failed: " + e.getMessage());
            return false;
        }

        // set display orientation
        if (cameraConfig.displayOrientation == -1) {
            cameraConfig.displayOrientation = CameraUtil.getPortraitCamearaDisplayOrientation(context, cameraConfig.currentCameraId, cameraConfig.isFaceCamera);
        }
        try {
            camera.setDisplayOrientation(cameraConfig.displayOrientation);
        } catch (Exception e) {
            openCameraFailedReason = OpenCameraFailedReason.SET_DISPLAY_ORIENTATION_FAILED;
            Log.e(TAG, "open camera failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean installPreviewCallback() {
        return false;
    }

    @Override
    public void onAvailable() {
        if (isNeedStartPreviewLater) {
            try {
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
}
