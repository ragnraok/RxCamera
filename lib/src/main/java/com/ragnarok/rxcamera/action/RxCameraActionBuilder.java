package com.ragnarok.rxcamera.action;

import android.hardware.Camera;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.error.SettingAreaFocusError;
import com.ragnarok.rxcamera.error.SettingFlashException;
import com.ragnarok.rxcamera.error.SettingMeterAreaError;
import com.ragnarok.rxcamera.error.ZoomFailedException;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ragnarok on 16/1/9.
 */
public class RxCameraActionBuilder {

    private RxCamera rxCamera;

    public RxCameraActionBuilder(RxCamera rxCamera) {
        this.rxCamera = rxCamera;
    }

    /**
     * set the zoom level of the camera
     * @param level
     * @return
     */
    public Observable<RxCamera> zoom(final int level) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (!parameters.isZoomSupported()) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_NOT_SUPPORT));
                    return;
                }
                int maxZoomLevel = parameters.getMaxZoom();
                if (level < 0 || level > maxZoomLevel) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_RANGE_ERROR));
                    return;
                }
                parameters.setZoom(level);
                rxCamera.getNativeCamera().setParameters(parameters);
                subscriber.onNext(rxCamera);
            }
        });
    }

    /**
     * smooth zoom the camera, which will gradually change the preview content
     * @param level
     * @return
     */
    public Observable<RxCamera> smoothZoom(final int level) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (!parameters.isZoomSupported() || !parameters.isSmoothZoomSupported()) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_NOT_SUPPORT));
                    return;
                }
                int maxZoomLevel = parameters.getMaxZoom();
                if (level < 0 || level > maxZoomLevel) {
                    subscriber.onError(new ZoomFailedException(ZoomFailedException.Reason.ZOOM_RANGE_ERROR));
                    return;
                }
                rxCamera.getNativeCamera().startSmoothZoom(level);
                subscriber.onNext(rxCamera);
            }
        });
    }

    public Observable<RxCamera> flashAction(final boolean isOn) {
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getSupportedFlashModes() == null || parameters.getSupportedFlashModes().size() <= 0) {
                    subscriber.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    return;
                }
                if (isOn) {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        rxCamera.getNativeCamera().setParameters(parameters);
                        subscriber.onNext(rxCamera);
                        return;
                    } else {
                        subscriber.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    }
                } else {
                    if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_OFF)) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        rxCamera.getNativeCamera().setParameters(parameters);
                        subscriber.onNext(rxCamera);
                    } else {
                        subscriber.onError(new SettingFlashException(SettingFlashException.Reason.NOT_SUPPORT));
                    }
                }
            }
        });
    }

    public Observable<RxCamera> areaFocusAction(final List<Camera.Area> focusAreaList) {
        if (focusAreaList == null || focusAreaList.size() == 0) {
            return null;
        }
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(final Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getMaxNumFocusAreas() < focusAreaList.size()) {
                    subscriber.onError(new SettingAreaFocusError(SettingAreaFocusError.Reason.NOT_SUPPORT));
                } else {
                    if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
                        List<String> focusModes = parameters.getSupportedFocusModes();
                        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        }
                    }
                    parameters.setFocusAreas(focusAreaList);
                    rxCamera.getNativeCamera().setParameters(parameters);
                    rxCamera.getNativeCamera().autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                subscriber.onNext(rxCamera);
                            } else {
                                subscriber.onError(new SettingAreaFocusError(SettingAreaFocusError.Reason.SET_AREA_FOCUS_FAILED));
                            }
                        }
                    });
                }
            }
        });
    }

    public Observable<RxCamera> areaMeterAction(final List<Camera.Area> meterAreaList) {
        if (meterAreaList == null || meterAreaList.size() == 0) {
            return null;
        }
        return Observable.create(new Observable.OnSubscribe<RxCamera>() {
            @Override
            public void call(Subscriber<? super RxCamera> subscriber) {
                Camera.Parameters parameters = rxCamera.getNativeCamera().getParameters();
                if (parameters.getMaxNumMeteringAreas() < meterAreaList.size()) {
                    subscriber.onError(new SettingMeterAreaError(SettingMeterAreaError.Reason.NOT_SUPPORT));
                } else {
                    parameters.setFocusAreas(meterAreaList);
                    rxCamera.getNativeCamera().setParameters(parameters);
                    subscriber.onNext(rxCamera);
                }
            }
        });
    }
}
