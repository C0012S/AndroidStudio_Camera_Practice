package com.example.capture;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pedro.library.AutoPermissions;

public class MainActivity extends AppCompatActivity {
    CameraSurfaceView cameraSurfaceView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        FrameLayout container = findViewById(R.id.container);
        cameraSurfaceView = new CameraSurfaceView(this);
        container.addView(cameraSurfaceView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        // 시작되는 시점에 위험 권한을 요청
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    public void capture() {
        cameraSurfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // 사진을 byte array로 전달해 줌
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); // 메모리에 만든 Bitmap 객체
                // 화면에 표시해 줌
                imageView.setImageBitmap(bitmap); // bitmap을 imageView에 설정
            }
        });
    }
}