package com.ragnarok.rxcamera.action;

import android.hardware.Camera;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.error.ZoomFailedException;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ragnarok on 16/1/9.
 */
public class RxCameraActionBuilder {

    private RxCamera rxCamera;

    public RxCameraActionBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    /**
     * set the zoom level of the camera
     * @param level
     * @return
     */
    public Observable<RxCamera> zoom(final int level) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (!parameters.isZoomSupported()) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_NOT_SUPPORT));
                    return;
                }
                int maxZoomLevel = parameters.getMaxZoom();
                if (level < 0 || level > maxZoomLevel) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_RANGE_ERROR));
                    return;
                }
                parameters.setZoom(level);
                rxCamera.getNativeCamera().setParameters(parameters);
                subscriber.onNext(rxCamera);
            }
        });
    }
}
