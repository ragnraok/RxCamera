# RxCamera

RxJava style camera API for android, it based on [android.hardware.camera](http://developer.android.com/intl/es/reference/android/hardware/Camera.html)

----

Usage:

1. set the camera parameter by choose a [RxCameraConfig](https://github.com/ragnraok/RxCamera/blob/master/lib/src/main/java/com/ragnarok/rxcamera/config/RxCameraConfig.java), which created by [RxCameraConfigChooser](https://github.com/ragnraok/RxCamera/blob/master/lib/src/main/java/com/ragnarok/rxcamera/config/RxCameraConfigChooser.java):
	
	```Java
	RxCameraConfig config = RxCameraConfigChooser.obtain().
            useBackCamera().
            setAutoFocus(true).
            setPreferPreviewFrameRate(15, 30).
            setPreferPreviewSize(new Point(640, 480)).
            setHandleSurfaceEvent(true).
            get();
	```
	for all camera currently support, please see [RxCameraConfig](https://github.com/ragnraok/RxCamera/blob/master/lib/src/main/java/com/ragnarok/rxcamera/config/RxCameraConfig.java)
	
2. open camera
	
	```Java
		RxCamera.open(context, config)
	```
	it return an RxJava Observable object, the type is ``Observable<RxCamera>``
	
3. bind a ``SurfaceView`` or ``TextureView`` and startPreview

	since ``RxCamera.open`` is return an Observable, so you can chain the call like this
	
	```Java
	RxCamera.open(this, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
          @Override
          public Observable<RxCamera> call(RxCamera rxCamera) {
              return rxCamera.bindTexture(textureView);
              // or bind a SurfaceView:
              // rxCamera.bindSurface(SurfaceView)
          }
    }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
          @Override
          public Observable<RxCamera> call(RxCamera rxCamera) {
              return rxCamera.startPreview();
          }
    });
	```
	both ``RxCamera.bindTexture`` and ``RxCamera.startPreview`` will return an ``Observable<RxCamera>`` object
	
	PS: if set ``isHandleSurfaceEvent``to true(set by ``setHandleSurfaceEvent(true)`` in ``RxCameraConfigChooser``), RxCamera will do the actual camera start preview action when the surface is available,  otherwise it will start preview immediately, and may failed if surface is not available, in this case, the return Observable will call ``onError``
	
4. request camera data

	RxCamera support many styles of camera data requests:
	
	-  successiveDataRequest
		
		```Java
		camera.request().successiveDataRequest()
		```
		it will return the camera infinitely
		
	- periodicDataRequest
		
		```Java
		camera.request().periodicDataRequest(1000)
		```
		as the name, it will return camera periodic, pass the interval in millisecond
		
	- oneShotRequest
	
		```Java
		camera.request().oneShotRequest()
		```
		it will return the camera data just **one time**
		
	- takePictureRequest
	
		```Java
		camera.request().takePictureRequest(boolean isContinuePreview, Func shutterAction)
		```
		the encapsulation of [takePicture](http://goo.gl/xhlLbJ) API, if ``isContinuePreview`` set to true, the RxCamera will try to restart preview after capture the picture, otherwise will behave as system ``takePicture`` call, stop preview after captured successfully <br/>
		and the ``shutterAction`` will called after picture just captured, just like the [ShutterCallback](http://developer.android.com/intl/es/reference/android/hardware/Camera.ShutterCallback.html) (actually it is called in the shutterCallback)
		
	all the data request will return an ``Observalbe<RxCameraData>``
	
	the ``RxCameraData`` contained two fields:
	
	- ``byte[] cameraData``, the raw data of camera, for the takePicture request, it will return the jpeg encode byte, other request just return raw camera preview data, if you don't set preview format, the default is YUV420SP
	- ``Matrix rotateMatrix``, this matrix help you rotate the camera data in portrait

Still under heavily development
