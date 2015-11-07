package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 15/11/8.
 * throw this exception if open camera failed
 */
public class OpenCameraExecption extends Exception {

    private OpenCameraFailedReason reason;

    public OpenCameraExecption(OpenCameraFailedReason reason) {
        this.reason = reason;
    }

    public OpenCameraFailedReason getReason() {
        return reason;
    }

    @Override
    public String getMessage() {
        return String.format("Open camera failed: %s", reason);
    }
}
