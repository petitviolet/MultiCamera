# Custom Camera View Sample

`SurfaceView`と`TextureView`を使い分けるCameraのCustomView

![camera](./20150531-222444.png)

# build.gradle

```groovy
repositories {
    maven {
        url "https://raw.githubusercontent.com/petitviolet/MultiCamera/master/repository/"
    }
}

dependencies {
    compile "net.petitviolet:multicamera:0.1.0"
}
```

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

# License

```
Copyright 2015 petitviolet

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
