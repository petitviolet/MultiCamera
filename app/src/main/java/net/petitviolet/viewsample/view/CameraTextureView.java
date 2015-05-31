package net.petitviolet.viewsample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.WindowManager;

import net.petitviolet.viewsample.R;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraTextureView extends CameraView {
    private static final String TAG = CameraTextureView.class.getSimpleName();
    TextureView mTextureView;

    public CameraTextureView(Context context) {
        super(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.camera_texture, this, false);
        addView(mView);
        mTextureView = (TextureView) mView.findViewById(R.id.camera_texture);
    }

    @Override
    public void show() {
        Log.d(TAG, "show");
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            mCamera = Camera.open();
            Log.d(TAG, "available");
            if (isPortrait()) {
                //Portrait対応
                mCamera.setDisplayOrientation(90);
            }
            setCameraFocusMode();
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
            try {
                mCamera.setPreviewTexture(texture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "startPreview");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "surfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            mCamera.release();
            mCamera = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

}
