package net.petitviolet.viewsample.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * TODO: document your custom view class.
 */
public class CameraView extends View {
    Context mContext;

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

    public CameraView createView(ViewGroup parentView) {
        int position = parentView.indexOfChild(this);
        CameraView newView;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            newView = new CameraSurfaceView(mContext);
        } else {
            newView = new CameraTextureView(mContext);
        }
        newView.setLayoutParams(getLayoutParams());
        newView.setId(getId());
        parentView.removeViewAt(position);
        parentView.addView(newView, position);
        return newView;
    }

    public void takePicture() {
    }
    public void show() {
        Log.d("CameraView", "show");
    }

    public void showPicture(byte[] bytes) {
        ImageView imageView = new ImageView(mContext);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, 0, options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, 0, options);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(getWidth(), getHeight()));
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(imageView);
        dialog.show();
    }
}
