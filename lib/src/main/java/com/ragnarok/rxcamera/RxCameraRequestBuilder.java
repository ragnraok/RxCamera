package com.ragnarok.rxcamera;

import rx.Observable;

/**
 * Created by ragnarok on 15/11/13.
 */
public class RxCameraRequestBuilder {

    private RxCamera rxCamera;

    public RxCameraRequestBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    public Observable<RxCameraData> successiveData() {
        return null;
    }
}
