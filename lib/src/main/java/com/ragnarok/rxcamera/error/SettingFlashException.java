package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 16/3/20.
 */
public class SettingFlashException extends Exception {

    public enum Reason {
        NOT_SUPPORT,
    }

    private Reason reason;

    public SettingFlashException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
