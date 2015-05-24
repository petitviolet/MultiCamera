package net.petitviolet.viewsample.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class CameraSurfaceView extends CameraView {
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    public CameraSurfaceView(Context context) {
        super(context);
        mSurfaceView = new SurfaceView(mContext);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceView = new SurfaceView(mContext);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSurfaceView = new SurfaceView(mContext);
    }

    @Override
    public void show() {
        // カメラの準備
        super.show();
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(callback);
    }

    @Override
    public void takePicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (!success) return;
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        showPicture(data);
                    }
                });
            }
        });
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        private Boolean isPortrait() {
            int orientation = getResources().getConfiguration().orientation;
            return (orientation == Configuration.ORIENTATION_PORTRAIT);
        }

        private void setCameraFocusMode() {
            Camera.Parameters params = mCamera.getParameters();
            List<String> supportedFocusModes = params.getSupportedFocusModes();
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                // 継続的なフォーカスモード
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                // 継続的なフォーカスモード
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(params);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mCamera = Camera.open();
            if (isPortrait()) {
                //Portrait対応
                mCamera.setDisplayOrientation(90);
            }
            setCameraFocusMode();
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            //最適サイズを取得
            Camera.Parameters params = mCamera.getParameters();

            // Auto Focusモード設定
            setCameraFocusMode();
            // ホワイトバランスは適当に調整
            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            mCamera.setParameters(params);
            // カメラのサイズをいい感じにする
            Point point = new Point();
            ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
            for (Camera.Size size2 : params.getSupportedPictureSizes()) {
                if (size2.width <= point.x && size2.height <= point.y) {
                    params.setPreviewSize(size2.width, size2.height);
                    break;
                }
            }
            mCamera.setParameters(params);
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            //片付け
            mCamera.release();
            mCamera = null;
        }
    };
}
