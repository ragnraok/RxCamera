package com.ragnarok.rxcamera;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


/**
 * Created by ragnarok on 15/11/13.
 */
public class RxCameraRequest implements OnRxCameraPreviewFrameCallback {

    private static final String TAG = "RxCamera.RxCameraRequestBuilder";

    private RxCamera rxCamera;

    public RxCameraRequest(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    private boolean isInstallSuccessivePreviewCallback = false;

    private Subscriber<? super RxCameraData> successiveDataSubscriber = null;

    public Observable<RxCameraData> successiveData() {
        if (!isInstallSuccessivePreviewCallback) {
            rxCamera.installPreviewCallback(this);
            isInstallSuccessivePreviewCallback = true;
        }
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(final Subscriber<? super RxCameraData> subscriber) {
                successiveDataSubscriber = subscriber;
                successiveDataSubscriber.add(new Subscription() {
                    @Override
                    public void unsubscribe() {
                        Log.d(TAG, "unsubscribe successiveDataSubscriber");
                        rxCamera.uninstallPreviewCallback(RxCameraRequest.this);
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (successiveDataSubscriber != null && !successiveDataSubscriber.isUnsubscribed()) {
            RxCameraData cameraData = new RxCameraData();
            cameraData.cameraData = data;
            successiveDataSubscriber.onNext(cameraData);
        }
    }
}
