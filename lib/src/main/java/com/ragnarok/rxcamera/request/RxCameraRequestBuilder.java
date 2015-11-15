package com.ragnarok.rxcamera.request;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;

import rx.Observable;

/**
 * Created by ragnarok on 15/11/15.
 */
public class RxCameraRequestBuilder {

    private RxCamera rxCamera;

    public RxCameraRequestBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    public Observable<RxCameraData> successiveDataRequest() {
        return new SuccessiveDataRequest(rxCamera).get();
    }

    public Observable<RxCameraData> periodicDataRequest(long intervalMills) {
        return new PeriodicDataRequest(rxCamera, intervalMills).get();
    }
}
