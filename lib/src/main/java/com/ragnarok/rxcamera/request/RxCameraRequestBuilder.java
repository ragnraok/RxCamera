package com.ragnarok.rxcamera.request;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;

import rx.Observable;

/**
 * Created by ragnarok on 15/11/15.
 * CameraRequestBuilder can help you get the camera preview frame data in different way
 */
public class RxCameraRequestBuilder {

    private RxCamera rxCamera;

    public RxCameraRequestBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    /**
     * successive camera preview frame data
     * @return Observable contained the camera data
     */
    public Observable<RxCameraData> successiveDataRequest() {
        return new SuccessiveDataRequest(rxCamera).get();
    }

    /**
     * periodic camera preview frame data
     * @param intervalMills the interval of the preview frame data will return, in millseconds
     * @returni Observable contained the camera data
     */
    public Observable<RxCameraData> periodicDataRequest(long intervalMills) {
        return new PeriodicDataRequest(rxCamera, intervalMills).get();
    }

    /**
     * only one shot camera data, encapsulated the setOneShotPreviewCallback
     * @return Observable contained the camera data
     */
    public Observable<RxCameraData> oneShotRequest() {
        return new TakeOneShotRequest(rxCamera).get();
    }

    /**
     * take picture request with default picture format and size, after call, will stop camera preview just like {@code Camera.takePicture}
     * @param isContinuePreview if continue preview after picture is captured
     * @param shutterAction call when the image is captured, it will be invoked before retrieve the actual image data
     * @return
     */
    public Observable<RxCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction) {
        return new TakePictureRequest(rxCamera, shutterAction, isContinuePreview).get();
    }

    /**
     * take picture request with specific size, after call, will stop camera preview just like {@code Camera.takePicture}
     * @param isContinuePreview if continue preview after picture is captured
     * @param shutterAction
     * @param width
     * @param height
     * @return
     */
    public Observable<RxCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction, int width, int height) {
        return new TakePictureRequest(rxCamera, shutterAction, isContinuePreview, width, height, -1).get();
    }

    /**
     * take picture request with specific size and picture format, after call, will stop camera preview just like {@code Camera.takePicture}
     * @param isContinuePreview if continue preview after picture is captured
     * @param shutterAction
     * @param width
     * @param height
     * @param format the final format of the picture, must be one of <var>ImageFormat.NV21</var>, <var>ImageFormat.RGB_565</var>, or <var>ImageFormat.JPEG</var>, the default is JPG
     * @return
     */
    public Observable<RxCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction, int width, int height, int format) {
        return new TakePictureRequest(rxCamera, shutterAction, isContinuePreview, width, height, format).get();
    }
}
