package com.example.camerapractice;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera = null;

    private int mCameraID;
    private Camera.CameraInfo mCameraInfo;
    private int mDisplayOrientation;

    public CameraSurfaceView(Context context) {
        super(context);

        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        holder = getHolder();
        holder.addCallback(this);

        // 0 -> CAMERA_FACING_BACK
        // 1 -> CAMERA_FACING_FRONT
        mCameraID = 0;

        // get display orientation
        mDisplayOrientation = ((Activity) context).getWindowManager()
                                    .getDefaultDisplay().getRotation();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open(mCameraID);

        // retireve camera's info.
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        mCameraInfo = cameraInfo;

        /**
         * 카메라를 오픈하고 프리뷰 디스플레이를 설정하겠다.
         * 프리뷰 디스플레이는 holder 쪽으로 설정하겠다.
         *
         * 하드웨어 카메라를 통해 들어온 영상이 홀더 쪽으로 전달되고
         * 홀더에 의해 서피스 뷰에 표시가 됩니다.
         **/
        try {
            camera.setPreviewDisplay(holder);
        }
        catch (Exception e) {
            Log.e("CameraSurfaceView", "Failed to set camera preview", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        camera.setDisplayOrientation(orientation);

        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean capture(Camera.PictureCallback callback) {
        if (camera != null) {
            camera.takePicture(null, null, callback);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 안드로이드 디바이스 방향에 맞는 카메라 프리뷰를 화면에 보여 주기 위해 계산합니다.
     **/
    public int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
        int degress = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degress = 0;
                break;
            case Surface.ROTATION_90:
                degress = 90;
                break;
            case Surface.ROTATION_180:
                degress = 180;
                break;
            case Surface.ROTATION_270:
                degress = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degress) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else { // back-facing
            result = (info.orientation - degress + 360) % 360;
        }

        return result;
    }
}
