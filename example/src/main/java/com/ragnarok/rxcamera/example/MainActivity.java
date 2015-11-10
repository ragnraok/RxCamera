package com.ragnarok.rxcamera.example;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.config.RxCameraConfigChooser;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Example.MainActivity";

    private TextureView textureView;
    private Button openCameraBtn;
    private Button closeCameraBtn;

    private RxCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textureView = (TextureView) findViewById(R.id.preview_surface);
        openCameraBtn = (Button) findViewById(R.id.open_camera);
        closeCameraBtn = (Button) findViewById(R.id.close_camera);

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        closeCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.closeCameraWithResult().subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            Log.d(TAG, "close camera finished, success: " + aBoolean);
                        }
                    });
                }
            }
        });
    }

    private void openCamera() {
        RxCameraConfig config = RxCameraConfigChooser.obtain().
                useBackCamera().
                setAutoFocus(true).
                setPreferPreviewFrameRate(15, 30).
                setPreferPreviewSize(new Point(640, 480)).
                setHandleSurfaceEvent(true).
                get();
        Log.d(TAG, "config: " + config);
        RxCamera.openAndStartPreview(this, config, textureView).subscribe(new Subscriber<RxCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "open camera failed: " + e.getMessage());
            }

            @Override
            public void onNext(RxCamera rxCamera) {
                camera = rxCamera;
                Log.d(TAG, "open camera success: " + rxCamera.toString());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.closeCamera();
        }
    }
}
