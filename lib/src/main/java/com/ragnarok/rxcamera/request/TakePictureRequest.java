package com.ragnarok.rxcamera.request;

import android.hardware.Camera;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.error.TakePictureFailedException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * Created by ragnarok on 15/11/29.
 */
public class TakePictureRequest extends BaseRxCameraRequest {

    private Func0 shutterAction;

    public TakePictureRequest(RxCamera rxCamera, Func0 shutterAction) {
        super(rxCamera);
        this.shutterAction = shutterAction;
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
