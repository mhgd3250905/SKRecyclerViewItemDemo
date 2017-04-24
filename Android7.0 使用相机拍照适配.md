## Android7.0 使用相机拍照适配

### 1.配置Manifest.xml
```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@style/AppTheme">
    ...
    <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="com.skkk.ww.skrecyclerviewitemdemo.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths">
        </meta-data>
    </provider>

</application>
```

### 2.配置res/xml/file_paths.xml
位置：

![效果](https://github.com/mhgd3250905/SKRecyclerViewItemDemo/blob/master/img/file_paths.png?raw=true)
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <paths>
        <external-path name="camera_photos" path="" />
    </paths>
</resources>
```

### 3.配置java文件
```java
public final static int CAMERA_REQUEST_CODE = 3;
public static String SAVED_IMAGE_DIR_PATH =
        Environment.getExternalStorageDirectory().getPath()
                + "/SKRecyclerViewDemo/camera/";// 拍照路径
String cameraPath;
```

```java
//判断是否有SD内存可用
String state = Environment.getExternalStorageState();
if (state.equals(Environment.MEDIA_MOUNTED)) {
    //照片的全路径
    cameraPath = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis() + ".png";
    Intent intent = new Intent();
    // 指定开启系统相机的Action
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

    //生成照片文件夹
    String out_file_path = SAVED_IMAGE_DIR_PATH;
    File dir = new File(out_file_path);
    if (!dir.exists()) {
        dir.mkdirs();
    }

    // 把文件地址转换成Uri格式
    Uri uri;
    if (Build.VERSION.SDK_INT<24) {//Android7.0
        // 从文件中创建uri
        uri = Uri.fromFile(new File(cameraPath));
    } else {//Android7.0以下
        //兼容android7.0 使用共享文件的形式
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA,cameraPath);
        uri = FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getPackageName()+".fileprovider",new File(cameraPath));
    }
    // 设置系统相机拍摄照片完成后图片文件的存放地址
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    startActivityForResult(intent, CAMERA_REQUEST_CODE);
} else {
    Toast.makeText(getContext().getApplicationContext(), "请确认已经插入SD卡",
            Toast.LENGTH_LONG).show();
}
```

回调：
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            //调用path获取图片
            adapter.notifyDataSetChanged();
        }
    }
}
```
