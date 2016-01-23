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

    private int pictureFormat = -1;
    private int pictureWidth = -1;
    private int pictureHeight = -1;

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview) {
        this(rxCamera, shutterAction, isContinuePreview, -1, -1, -1);
    }

    public TakePictureRequest(RxCamera rxCamera, Func shutterAction, boolean isContinuePreview, int width, int height, int format) {
        super(rxCamera);
        this.shutterAction = shutterAction;
        this.isContinuePreview = isContinuePreview;
        this.pictureWidth = width;
        this.pictureHeight = height;
        this.pictureFormat = format;
    }

    @Override
    public Observable<RxCameraData> get() {
        return Observable.create(new Observable.OnSubscribe<RxCameraData>() {
            @Override
            public void call(final Subscriber<? super RxCameraData> subscriber) {
                try {
                    Camera.Parameters param = rxCamera.getNativeCamera().getParameters();
                    // set the picture format
                    if (pictureFormat != -1) {
                        param.setPictureFormat(pictureFormat);
                    }
                    rxCamera.getNativeCamera().setParameters(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Camera.Parameters param = rxCamera.getNativeCamera().getParameters();
                    // set the picture size
                    if (pictureWidth != -1 && pictureHeight != -1) {
                        param.setPictureSize(pictureWidth, pictureHeight);
                    }
                    rxCamera.getNativeCamera().setParameters(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }


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
