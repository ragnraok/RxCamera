package com.ragnarok.rxcamera;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.TextureView;

/**
 * Created by ragnarok on 15/11/8.
 */
/* package */ class SurfaceCallback implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    public interface SurfaceListener {
        void onAvailable(SurfaceHolder holder);
        void onAvailable(SurfaceTexture surface);
        void onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onSurfaceAvailable(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onSurfaceDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        onSurfaceDestroy();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void onSurfaceAvailable(SurfaceHolder holder) {
        if (listener != null) {
            listener.onAvailable(holder);
        }
    }

    public void onSurfaceAvailable(SurfaceTexture surface) {
        if (listener != null) {
            listener.onAvailable(surface);
        }
    }

    public void onSurfaceDestroy() {
        if (listener != null) {
            listener.onDestroy();
        }
    }

    private SurfaceListener listener;

    public void setSurfaceListener(SurfaceListener l) {
        this.listener = l;
    }
}
