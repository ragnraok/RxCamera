# RxCamera
RxJava style API for android camera
----
Usage:

```Java
RxCameraConfig config = RxCameraConfigChooser.obtain().
                useBackCamera().
                setAutoFocus(true).
                setPreferPreviewFrameRate(15, 30).
                setPreferPreviewSize(new Point(640, 480)).
                setHandleSurfaceEvent(true).
                get();
Log.d(TAG, "config: " + config);
RxCamera.open(this, config).subscribe(new Subscriber<RxCamera>() {
      @Override
      public void onCompleted() {
        
      }

      @Override
      public void onError(Throwable e) {
        Log.d(TAG, "open camera failed: " + e.getMessage());
      }

      @Override
      public void onNext(RxCamera rxCamera) {
          Log.d(TAG, "open camera success: " + rxCamera.toString());
          camera = rxCamera;
          camera.bindTexture(textureView);
          camera.startPreview();
      }
});
```

Still under heavily development
