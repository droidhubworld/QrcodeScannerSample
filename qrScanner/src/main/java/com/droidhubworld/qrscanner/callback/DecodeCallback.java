package com.droidhubworld.qrscanner.callback;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.google.zxing.Result;
public interface DecodeCallback {
    /**
     * Called when decoder has successfully decoded the code
     * <br>
     * Note that this method always called on a worker thread
     *
     * @param result Encapsulates the result of decoding a barcode within an image
     * @see Handler
     * @see Looper#getMainLooper()
     * @see Activity#runOnUiThread(Runnable)
     */
    @WorkerThread
    void onDecoded(@NonNull Result result);
}
