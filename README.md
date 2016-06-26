# RxCamera

RxJava style camera API for android, it based on [android.hardware.camera](http://developer.android.com/intl/es/reference/android/hardware/Camera.html)

----

Add to your project dependence:

```groovy
repositories {
        jcenter()
}
dependencies {
	compile 'com.ragnarok.rxcamera:lib:0.0.4'
}
```

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
		it will return the camera data infinitely
		
	- periodicDataRequest
		
		```Java
		camera.request().periodicDataRequest(1000)
		```
		as the name, it will return camera data periodic, pass the interval in millisecond
		
	- oneShotRequest
	
		```Java
		camera.request().oneShotRequest()
		```
		it will return the camera data **only once**
		
	- takePictureRequest
	
		```Java
		camera.request().takePictureRequest(boolean isContinuePreview, Func shutterAction, boolean openFlash)
		```
		the encapsulation of [takePicture](http://goo.gl/xhlLbJ) API, if ``isContinuePreview`` set to true, the RxCamera will try to restart preview after capture the picture, otherwise will behave as system ``takePicture`` call, stop preview after captured successfully 
		
		and the ``shutterAction`` will called after picture just captured, like the [ShutterCallback]
(http://developer.android.com/intl/es/reference/android/hardware/Camera.ShutterCallback.html) (actually it is called in the system shutterCallback)

		and the ``openFlash`` if set to true, it will open the flash when taking picture, and automatically close it after this request
		
	- FaceDetectionRequest

		```Java
		camera.request().faceDetectionRequest()
		```

		the encapsulation of ``Camera.FaceDetectionListener``, it will return the faces location in  ``CameraData.faceList``

	----	
	
	All the data request will return an ``Observalbe<RxCameraData>``
	
	the ``RxCameraData`` contained these fields:
	
	- ``byte[] cameraData``, the raw data of camera, for the takePicture request, it will return the jpeg encode byte, other request just return raw camera preview data, if you don't set preview format, the default is YUV420SP
	- ``Matrix rotateMatrix``, this matrix help you rotate the camera data in portrait
	-  ``Camera.Face[] faceList``, the locatoin of faces, only returned in FaceDetectionRequest

5. camera action request
	
	the camera action request will change the behavior of the camera
	
	- zoom and somoothZoom action:
		
		```Java
		camera.action().zoom(int level)
		camera.action().smoothZoom(int level)
		```
		
		change the camera zoom level
		
	- open or close the flash
	
		```Java
		camera.action().flashAction(boolean isOn)
		```
		
	-  area focus and area metering
		
		```Java
		camera.action().areaFocusAction(List<Camera.Area> focusAreaList)
		camera.action().areaMeterAction(List<Camera.Area> focusAreaList)
		```
		
		and there is a helper function to convert the coordinate to [-1000, 1000], which is suitable for ``Camera.Area``, in CameraUtil:
		
		```Java
		Rect transferCameraAreaFromOuterSize(Point center, Point outerSize, int size)
		```
		
		check out the example to see how to use this

This project still in very early stage, and welcome the pull request
