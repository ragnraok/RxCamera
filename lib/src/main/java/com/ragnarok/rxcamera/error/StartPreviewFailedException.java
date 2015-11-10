package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 15/11/10.
 */
public class StartPreviewFailedException extends Exception {

    public StartPreviewFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Cause: " + getCause();
    }
}
