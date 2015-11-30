package com.ragnarok.rxcamera.request;

import android.hardware.Camera;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.error.TakePictureFailedException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by ragnarok on 15/11/29.
 */
public class TakePictureRequest extends BaseRxCameraRequest {

    private Func shutterAction;
    private boolean isContinuePreview;

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview) {
        super(rxCamera);
        this.shutterAction = shutterAction;
        this.isContinuePreview = true;
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(final Subscriber<? super RxCameraData> subscriber) {
                rxCamera.getNativeCamera().takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        if (shutterAction != null) {
                            shutterAction.call();
                        }
                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                    }
                }, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (isContinuePreview) {
                            rxCamera.startPreview().doOnError(new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    subscriber.onError(throwable);
                                }
                            }).subscribe();
                        }
                        if (data != null) {
                            RxCameraData rxCameraData = new RxCameraData();
                            rxCameraData.cameraData = data;
                            rxCameraData.rotateMatrix = rxCamera.getRotateMatrix();
                            subscriber.onNext(rxCameraData);

                        } else {
                            subscriber.onError(new TakePictureFailedException("cannot get take picture data"));
                        }
                    }
                });
            }
        });
    }
}
