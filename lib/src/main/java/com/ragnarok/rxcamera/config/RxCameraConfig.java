package com.ragnarok.rxcamera.config;

import android.graphics.Point;
import android.hardware.Camera;

/**
 * Created by ragnarok on 15/11/1.
 * store the config of camera, you must set the config from {@link RxCameraConfig.Builder}
 */
public class RxCameraConfig {

    public static Point DEFAULT_PREFER_PREVIEW_SIZE = new Point(320, 240);

    public final boolean isFaceCamera;

    public final int currentCameraId;

    public final Point preferPreviewSize;

    public final boolean acceptSquarePreview;

    public final int minPreferPreviewFrameRate;

    public final int maxPreferPreviewFrameRate;

    public final int previewFormat;

    public final int displayOrientation;

    public final boolean isAutoFocus;

    public final int previewBufferSize;

    public final boolean isHandleSurfaceEvent;

    public final int cameraOrientation;

    public final boolean muteShutterSound;

    public RxCameraConfig(Builder builder) {
        isFaceCamera = builder.isFaceCamera;
        currentCameraId = builder.currentCameraId;
        preferPreviewSize = builder.preferPreviewSize;
        acceptSquarePreview = builder.acceptSquarePreview;
        minPreferPreviewFrameRate = builder.minPreferPreviewFrameRate;
        maxPreferPreviewFrameRate = builder.maxPreferPreviewFrameRate;
        previewFormat = builder.previewFormat;
        displayOrientation = builder.displayOrientation;
        isAutoFocus = builder.isAutoFocus;
        previewBufferSize = builder.previewBufferSize;
        isHandleSurfaceEvent = builder.isHandleSurfaceEvent;
        cameraOrientation = builder.cameraOrientation;
        muteShutterSound = builder.muteShutterSound;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("RxCameraConfig ");
        result.append(String.format("isFaceCamera: %b, currentCameraId: %d, ", isFaceCamera, currentCameraId));
        result.append(String.format("preferPreviewSize: %s, ", preferPreviewSize));
        result.append(String.format("minPreferPreviewFrameRate: %d, maxPreferPreviewFrameRate: %d, ", minPreferPreviewFrameRate, maxPreferPreviewFrameRate));
        result.append(String.format("previewFormat: %d, ", previewFormat));
        result.append(String.format("displayOrientation: %d, ", displayOrientation));
        result.append(String.format("isAutoFocus: %b", isAutoFocus));
        result.append(String.format("previewBufferSize: %d, ", previewBufferSize));
        result.append(String.format("isHandleSurfaceEvent: %b, ", isHandleSurfaceEvent));
        result.append(String.format("cameraOrientation: %d, ", cameraOrientation));
        result.append(String.format("acceptSquarePreview: %s, ", acceptSquarePreview));
        result.append(String.format("muteShutterSound: %s", muteShutterSound));
        return result.toString();
    }

    public static class Builder {
        private boolean isFaceCamera = false;
        private int currentCameraId = -1;
        private Point preferPreviewSize = null;
        private boolean acceptSquarePreview = true;
        private int minPreferPreviewFrameRate = -1;
        private int maxPreferPreviewFrameRate = -1;
        private int previewFormat = -1;
        private int displayOrientation = -1;
        private boolean isAutoFocus = false;
        private int previewBufferSize = -1;
        private boolean isHandleSurfaceEvent = false;
        private int cameraOrientation = -1;
        private boolean muteShutterSound = false;

        public Builder useFrontCamera() {
            isFaceCamera = true;
            currentCameraId = CameraUtil.getFrontCameraId();
            return this;
        }

        public Builder useBackCamera() {
            isFaceCamera = false;
            currentCameraId = CameraUtil.getBackCameraId();
            return this;
        }

        public Builder setPreferPreviewSize(Point size, boolean acceptSquarePreview) {
            if (size == null) {
                return this;
            }
            preferPreviewSize = size;
            this.acceptSquarePreview = acceptSquarePreview;
            return this;
        }

        public Builder setPreferPreviewFrameRate(int minFrameRate, int maxFrameRate) {
            if (minFrameRate <= 0 || maxFrameRate <= 0 || maxFrameRate < minFrameRate) {
                return this;
            }
            minPreferPreviewFrameRate = minFrameRate;
            maxPreferPreviewFrameRate = maxFrameRate;
            return this;
        }

        public Builder setPreviewFormat(int previewFormat) {
            this.previewFormat = previewFormat;
            return this;
        }

        public Builder setDisplayOrientation(int displayOrientation) {
            if (displayOrientation < 0) {
                return this;
            }
            if (displayOrientation != 0 &&
                    displayOrientation != 90 &&
                    displayOrientation != 180 &&
                    displayOrientation != 270)
                throw new IllegalArgumentException("display orientation: " +displayOrientation + ". (must be 0, 90, 180, or 270)");

            this.displayOrientation = displayOrientation;
            return this;
        }

        public Builder setAutoFocus(boolean isAutoFocus) {
            this.isAutoFocus = isAutoFocus;
            return this;
        }

        public Builder setHandleSurfaceEvent(boolean isHandle) {
            isHandleSurfaceEvent = isHandle;
            return this;
        }

        public Builder setPreviewBufferSize(int size) {
            previewBufferSize = size;
            return this;
        }

        public Builder setMuteShutterSound(boolean mute){
            muteShutterSound = mute;
            return this;
        }

        private Builder setProperConfigVal() {
            if (currentCameraId == -1) {
                if (isFaceCamera) {
                    currentCameraId = CameraUtil.getFrontCameraId();
                } else {
                    currentCameraId = CameraUtil.getBackCameraId();
                }
            }
            if (preferPreviewSize == null) {
                preferPreviewSize = RxCameraConfig.DEFAULT_PREFER_PREVIEW_SIZE;
            }

            Camera.CameraInfo cameraInfo = CameraUtil.getCameraInfo(currentCameraId);
            if (cameraInfo != null) {
                cameraOrientation = cameraInfo.orientation;
            }
            return this;
        }

        public Builder from(RxCameraConfig config) {
            if (config.isFaceCamera) {
                useFrontCamera();
            } else {
                useBackCamera();
            }
            setPreferPreviewSize(config.preferPreviewSize, config.acceptSquarePreview);
            setPreferPreviewFrameRate(config.maxPreferPreviewFrameRate, config.minPreferPreviewFrameRate);
            setPreviewFormat(config.previewFormat);
            setDisplayOrientation(config.displayOrientation);
            setAutoFocus(config.isAutoFocus);
            setHandleSurfaceEvent(config.isHandleSurfaceEvent);
            setPreviewBufferSize(config.previewBufferSize);
            setMuteShutterSound(config.muteShutterSound);
            return this;
        }

        public RxCameraConfig build() {
            setProperConfigVal();
            return new RxCameraConfig(this);
        }
    }
}
