package net.petitviolet.viewsample.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

public class CameraView extends FrameLayout {
    private static final String TAG = CameraView.class.getSimpleName();
    protected Context mContext;
    protected View mView;
    protected Camera mCamera;

    public CameraView(Context context) {
        super(context);
        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public CameraView initView() {
        ViewGroup parentView = (ViewGroup) getParent();
        int position = parentView.indexOfChild(this);
        CameraView newView;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            newView = new CameraSurfaceView(mContext);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            newView = new CameraTextureView(mContext);
        } else {
            // TODO: camera2使った実装
//            newView = new Camera2TextureView(mContext);
            newView = new CameraTextureView(mContext);
        }
        newView.setLayoutParams(getLayoutParams());
        newView.setId(getId());
        newView.setView();

        parentView.removeViewAt(position);
        parentView.addView(newView, position);
        newView.setView();
        return newView;
    }

    protected void setView() {
        // Must to be Override
    }

    public interface TakePictureCallback {
        void onSuccess(byte[] data);

        void onFail();
    }

    public void show() {
        Log.d(TAG, "show");
        // Must to be Override
    }

    public void showPictureDialog(byte[] bytes) {
        ImageView imageView = new ImageView(mContext);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, 0, options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getHeight()));
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(imageView);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                restartCamera();
            }
        });
        dialog.show();
    }

    public void takePicture(final TakePictureCallback callback) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (!success) {
                    if (callback != null) {
                        callback.onFail();
                    }
                    camera.startPreview();
                    return;
                }
                Camera.Parameters params = camera.getParameters();
                if (isPortrait()) {
                    params.setRotation(90);
                }
                camera.setParameters(params);

                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        showPictureDialog(data);
                        if (callback != null) {
                            callback.onSuccess(data);
                        }
                    }
                });
            }
        });
    }

    protected void restartCamera() {
        mCamera.startPreview();
    }

    protected Boolean isPortrait() {
        int orientation = getResources().getConfiguration().orientation;
        return (orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    protected void setCameraFocusMode() {
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
}
