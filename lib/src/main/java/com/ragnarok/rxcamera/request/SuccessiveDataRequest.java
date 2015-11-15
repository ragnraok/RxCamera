package com.ragnarok.rxcamera.request;

import android.util.Log;

import com.ragnarok.rxcamera.OnRxCameraPreviewFrameCallback;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


/**
 * Created by ragnarok on 15/11/13.
 */
public class SuccessiveDataRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {

    private static final String TAG = "RxCamera.RxCameraRequestBuilder";

    private boolean isInstallSuccessivePreviewCallback = false;

    private Subscriber<? super RxCameraData> successiveDataSubscriber = null;

    public SuccessiveDataRequest(RxCamera rxCamera) {
        super(rxCamera);
    }

    public Observable<RxCameraData> get() {
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
                        rxCamera.uninstallPreviewCallback(SuccessiveDataRequest.this);
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
