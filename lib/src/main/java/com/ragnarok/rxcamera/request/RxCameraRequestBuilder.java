package com.ragnarok.rxcamera.request;

import android.hardware.Camera;

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
     * @param openFlash will open the flash when taking picture if set to true
     * @return
     */
    public Observable<RxCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction, int width, int height, boolean openFlash) {
        return new TakePictureRequest(rxCamera, shutterAction, isContinuePreview, width, height, -1, openFlash).get();
    }

    /**
     * take picture request with specific size and picture format, after call, will stop camera preview just like {@code Camera.takePicture}
     * @param isContinuePreview if continue preview after picture is captured
     * @param shutterAction
     * @param width
     * @param height
     * @param format the final format of the picture, must be one of <var>ImageFormat.NV21</var>, <var>ImageFormat.RGB_565</var>, or <var>ImageFormat.JPEG</var>, the default is JPG
     * @param openFlash will open the flash when taking picture if set to true
     * @return
     */
    public Observable<RxCameraData> takePictureRequest(boolean isContinuePreview, Func shutterAction, int width, int height, int format, boolean openFlash) {
        return new TakePictureRequest(rxCamera, shutterAction, isContinuePreview, width, height, format, openFlash).get();
    }


    /**
     * the face detection request, after set this, the returned {@link RxCameraData#faceList} will contain the
     * faces position list
     * this request use {@link Camera#startFaceDetection()}, so after setting this, the focus area the metering
     * have no effect
     * @return
     */
    public Observable<RxCameraData> faceDetectionRequest() {
        return new FaceDetectionRequest(rxCamera).get();
    }
}
