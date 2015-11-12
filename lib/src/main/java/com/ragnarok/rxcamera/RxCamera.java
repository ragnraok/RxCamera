package com.ragnarok.rxcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.error.StartPreviewFailedException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ragnarok on 15/10/25.
 */
public class RxCamera  {

    private static final String TAG = "RxCamera";

    private Context context;

    private RxCameraInternal cameraInternal = new RxCameraInternal();

    public static Observable<RxCamera> open(final Context context, final RxCameraConfig config) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                RxCamera rxCamera = new RxCamera(context, config);
                if (rxCamera.cameraInternal.openCameraInternal()) {
                    subscriber.onNext(rxCamera);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(rxCamera.cameraInternal.openCameraExecption());
                }
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final SurfaceView surfaceView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.bindSurface(surfaceView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.startPreview();
            }
        });
    }

    public static Observable<RxCamera> openAndStartPreview(Context context, RxCameraConfig config, final TextureView textureView) {
        return open(context, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.bindTexture(textureView);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                return rxCamera.startPreview();
            }
        });
    }

    private RxCamera(Context context, RxCameraConfig config) {
        this.context = context;
        this.cameraInternal = new RxCameraInternal();
        this.cameraInternal.setConfig(config);
        this.cameraInternal.setContext(context);
    }

    public Observable<RxCamera> bindSurface(final SurfaceView surfaceView) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.bindSurfaceInternal(surfaceView);
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }

    public Observable<RxCamera> bindTexture(final TextureView textureView) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.bindTextureInternal(textureView);
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.bindSurfaceFailedException());
                }
            }
        });
    }


    public Observable<RxCamera> startPreview() {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                boolean result = cameraInternal.startPreviewInternal();
                if (result) {
                    subscriber.onNext(RxCamera.this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(cameraInternal.startPreviewFailedException());
                }
            }
        });
    }

    public Observable<Boolean> closeCameraWithResult() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(cameraInternal.closeCameraInternal());
                subscriber.onCompleted();
            }
        });
    }

    public boolean closeCamera() {
        return cameraInternal.closeCameraInternal();
    }

    public boolean isOpenCamera() {
        return cameraInternal.isOpenCamera();
    }

    public boolean isBindSurface() {
        return cameraInternal.isBindSurface();
    }
}
