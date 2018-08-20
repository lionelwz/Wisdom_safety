package com.wf.wisdom_safety.ui.inspect.plain;

import android.os.Bundle;

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class ImageSelectorActivity extends MultiImageSelectorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCameraShot(final File imageFile) {
        super.onCameraShot(imageFile);
//        if (null != imageFile && imageFile.exists()) {
//            // 图片文件
//            new Compressor.Builder(this)
//                    .setMaxWidth(480)
//                    .setMaxHeight(800)
//                    .setQuality(100)
//                    .build()
//                    .compressToBitmapAsObservable(imageFile)
//                    .map(new Func1<Bitmap, File>() {
//                        @Override
//                        public File call(Bitmap bitmap) {
//                            String bitName = imageFile.getParent() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
//                            return FileUtils.saveBitmap(bitName, bitmap);
//                        }
//                    })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<File>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//
//                }
//
//                @Override
//                public void onNext(File file) {
//                    Log.i("ImageSelectorActivity", "file==" + file.length());
//                    if(null != file)
//                        ImageSelectorActivity.super.onCameraShot(file);
//                }
//            });
//        }
    }
}
