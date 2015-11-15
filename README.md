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
        Log.d(TAG, "isopen: " + rxCamera.isOpenCamera());
        camera = rxCamera;
        return rxCamera.bindTexture(textureView);
    }
}).subscribeOn(Schedulers.io()).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
    @Override
    public Observable<RxCamera> call(RxCamera rxCamera) {
        Log.d(TAG, "isbindsurface: " + rxCamera.isBindSurface());
        return rxCamera.startPreview();
    }
}).flatMap(new Func1<RxCamera, Observable<RxCameraData>>() {
    @Override
    public Observable<RxCameraData> call(RxCamera rxCamera) {
        return rxCamera.request().successiveData();
    }
}).subscribe(new Subscriber<RxCameraData>() {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(RxCameraData rxCameraData) {
        Log.d(TAG, "onNext, data.length: " + rxCameraData.cameraData.length);
    }
});
```

Still under heavily development
