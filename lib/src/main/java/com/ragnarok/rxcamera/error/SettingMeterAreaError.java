package com.ragnarok.rxcamera.error;

/**
 * Created by ragnarok on 16/3/27.
 */
public class SettingMeterAreaError extends Exception {

    public enum Reason {
        NOT_SUPPORT,
    }

    private Reason reason;

    public SettingMeterAreaError(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }
}
