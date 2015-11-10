package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 15/11/10.
 */
public class BindSurfaceFailedException extends Exception {

    public BindSurfaceFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
