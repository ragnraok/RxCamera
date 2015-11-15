package com.ragnarok.rxcamera.request;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;

import rx.Observable;

/**
 * Created by ragnarok on 15/11/15.
 */
public abstract class BaseRxCameraRequest {

    protected RxCamera rxCamera;

    public BaseRxCameraRequest(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    public abstract Observable<RxCameraData> get();
}
