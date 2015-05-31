package net.petitviolet.multicamera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraSurfaceView extends CameraView {
    private static final String TAG = CameraSurfaceView.class.getSimpleName();
    private SurfaceView mSurfaceView;

    public CameraSurfaceView(Context context) {
        super(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void show() {
        // カメラの準備
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(callback);
        Log.d(TAG, "show");
    }

    @Override
    protected void setView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.camera_surface, this, false);
        addView(mView);
        mSurfaceView = (SurfaceView) mView.findViewById(R.id.camera_surface);
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "camera open");
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
