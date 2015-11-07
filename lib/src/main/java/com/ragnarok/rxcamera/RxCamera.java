package com.ragnarok.rxcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.error.OpenCameraExecption;
import com.ragnarok.rxcamera.error.OpenCameraFailedReason;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ragnarok on 15/10/25.
 */
public class RxCamera {

    private static final String TAG = "RxCamera";

    // the native camera object
    private Camera camera;

    // the camera config
    private RxCameraConfig cameraConfig;

    private OpenCameraFailedReason openCameraFailedReason;

    public static Observable<RxCamera> open(final RxCameraConfig config) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                RxCamera rxCamera = new RxCamera();
                if (rxCamera.openCamera()) {
                    subscriber.onNext(rxCamera);
                } else {
                    subscriber.onError(new OpenCameraExecption(rxCamera.openCameraFailedReason));
                }
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreview(SurfaceHolder surfaceHolder) {
        return null;
    }

    public static Observable<RxCamera> openAndStartPreview(SurfaceTexture surfaceTexture) {
        return null;
    }

    public static Observable<RxCamera> openAndStartPreviewWithPreviewCallback(SurfaceHolder surfaceHolder) {
        return null;
    }

    public static Observable<RxCamera> openAndStartPreviewWithPreviewCallback(SurfaceTexture surfaceTexture) {
        return null;
    }

    private boolean openCamera() {
        if (cameraConfig == null) {
            openCameraFailedReason = OpenCameraFailedReason.PARAMETER_ERROR;
            return false;
        }


        return true;
    }

    private boolean bindSurface(SurfaceHolder surfaceHolder) {
        return true;
    }

    private boolean bindSurfaceTexture(SurfaceTexture surfaceTexture) {
        return true;
    }

    public boolean startPreview() {
        return true;
    }

    public static void test() {
        // open -> bindSurface -> setPreviewCallback(optional) -> startPreview
        RxCamera.open(new RxCameraConfig()).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(final RxCamera rxCamera) {
                return Observable.create(new Observable.OnSubscribe<RxCamera>() {
                    @Override
                    public void call(Subscriber<? super RxCamera> subscriber) {
                        rxCamera.bindSurface(null);
                    }
                });
            }
        });
    }
}
