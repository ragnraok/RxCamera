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
RxCamera.open(this, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
        @Override
        public Observable<RxCamera> call(RxCamera rxCamera) {
            camera = rxCamera;
            return rxCamera.bindTexture(textureView);
        }
    }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
        @Override
        public Observable<RxCamera> call(RxCamera rxCamera) {
          return rxCamera.startPreview();
        }
    }).subscribe(new Subscriber<RxCamera>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "error: " + e.getMessage());
        }

        @Override
        public void onNext(RxCamera rxCamera) {
            Log.d(TAG, "success: " + rxCamera);
        }
    });
}
```

Still under heavily development
