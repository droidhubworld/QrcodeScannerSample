package com.droidhubworld.qrscanner;

public enum AutoFocusMode {
    /**
     * Auto focus camera with the specified interval
     *
     * @see Scanner#setAutoFocusInterval(long)
     */
    SAFE,

    /**
     * Continuous auto focus, may not work on some devices
     */
    CONTINUOUS
}
