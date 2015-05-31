# Custom Camera View Sample

`SurfaceView`と`TextureView`を使い分けるCameraのCustomView

# How To Use

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cameraView = ((CameraView) findViewById(R.id.camera)).initView();
    findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.takePicture(new CameraView.TakePictureCallback() {
                @Override
                public void onSuccess(byte[] data) {
                    Log.d("MainActivity", "onSuccess");
                }

                @Override
                public void onFail() {
                    Log.d("MainActivity", "onFail");
                }
            });
        }
    });
    cameraView.show();
}
```
