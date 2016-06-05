package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 16/6/5.
 */
public class FaceDetectionNotSupportError extends Exception {

    public FaceDetectionNotSupportError(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
