package com.ragnarok.rxcamera.request;

import android.util.Log;

import com.ragnarok.rxcamera.OnRxCameraPreviewFrameCallback;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.error.CameraDataNullException;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ragnarok on 15/11/15.
 */
public class PeriodicDataRequest extends BaseRxCameraRequest implements OnRxCameraPreviewFrameCallback {

    private static final String TAG = "MicroMsg.PeriodicDataRequest";

    private long intervalMills;

    private boolean isInstallCallback = false;

    private Subscriber<? super RxCameraData> subscriber = null;

    private RxCameraData currentData = new RxCameraData();

    private long lastSendDataTimestamp = 0;

    public PeriodicDataRequest(RxCamera rxCamera, long intervalMills) {
        super(rxCamera);
        this.intervalMills = intervalMills;
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(final Subscriber<? super RxCameraData> subscriber) {
                PeriodicDataRequest.this.subscriber = subscriber;
                subscriber.add(Schedulers.newThread().createWorker().schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        if (currentData.cameraData != null && !subscriber.isUnsubscribed() && rxCamera.isOpenCamera()) {
                            subscriber.onNext(currentData);
                        }
                    }
                }, 0, intervalMills, TimeUnit.MILLISECONDS));

            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                rxCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!isInstallCallback) {
                    rxCamera.installPreviewCallback(PeriodicDataRequest.this);
                    isInstallCallback = true;
                }
            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                rxCamera.uninstallPreviewCallback(PeriodicDataRequest.this);
                isInstallCallback = false;
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        if (subscriber != null && !subscriber.isUnsubscribed() && rxCamera.isOpenCamera()) {
            if (data == null || data.length == 0) {
                subscriber.onError(new CameraDataNullException());
            }
            currentData.cameraData = data;
            currentData.rotateMatrix = rxCamera.getRotateMatrix();
        }
    }
}
