package me.nereo.multi_image_selector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.nereo.multi_image_selector.utils.FileManager;
import me.nereo.multi_image_selector.view.RecordedButton;

public class VideoRecordActivity extends AppCompatActivity implements SurfaceHolder.Callback
        , RecordedButton.OnGestureListener, View.OnClickListener {

    private static final int LISTENER_START = 200;
    private static final String TAG = "MainActivity";
    public static final String VIDEO_SUFFIX = ".mp4";
    public static final String VIDEO_PREFIX = "blog_record_";
    public static final String EXTRA_PATH = "video_path";
    //预览SurfaceView
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private RecordedButton mRecordedButton;
    private Button mCameraSwitch;
    private Toolbar mToolbar;
    //录制视频
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mSurfaceHolder;
    //判断是否正在录制
    private boolean isRecording;
    //段视频保存的目录
    private File mTargetFile;
    //手势处理, 主要用于变焦 (双击放大缩小)
    private GestureDetector mDetector;
    //是否放大
    private boolean isZoomIn = false;
    private boolean isCameraBack = true;
    private int mCameraCount = 0;
    private int mRotation = 0;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mVideoQuality = CamcorderProfile.QUALITY_HIGH;
    private CamcorderProfile mCamcorderProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mis_activity_video_record);
        initView();
        setToolbar();
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setToolbar() {
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoRecordActivity.this.onBackPressed();
            }
        });
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.main_surface_view);

        mDetector = new GestureDetector(this, new ZoomGestureListener());
        /**
         * 单独处理mSurfaceView的双击事件
         */
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        mSurfaceHolder = mSurfaceView.getHolder();
        //设置屏幕分辨率
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecordedButton = (RecordedButton) findViewById(R.id.record_btn);
        mRecordedButton.setOnGestureListener(this);
        mCameraSwitch = (Button) findViewById(R.id.camera_switch);
        mCameraSwitch.setOnClickListener(this);

        mMediaRecorder = new MediaRecorder();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    ///////////////////////////////////////////////////////////////////////////
    // SurfaceView回调
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        //相机参数配置类
        mCamcorderProfile = CamcorderProfile.get(mVideoQuality);
        if(null == mCamcorderProfile) {
            mRecordedButton.setEnabled(false);
            mRecordedButton.setTouchable(false);
            mCameraSwitch.setEnabled(false);
            showProfileNullDialog();
            return;
        }
        startPreView(holder);
    }

    /**
     * 开启预览
     *
     * @param holder
     */
    private void startPreView(SurfaceHolder holder) {
        Log.d(TAG, "startPreView: ");

        if (mCamera == null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            mCameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            if(mCameraCount >= 1) {
                for(int i = 0; i < mCameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                    if(cameraInfo.facing  == mCameraId) {
                        try {
                            mCamera = Camera.open(i);//打开当前选中的摄像头
                        } catch (Exception e) {
                            Toast.makeText(this, R.string.mis_camera_open_fail, Toast.LENGTH_SHORT).show();
                            finish();
                            e.printStackTrace();
                        }
                        deal(i);
                        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                        break;
                    } else {
                        try {
                            mCamera = Camera.open(i);//打开当前选中的摄像头
                        } catch (Exception e) {
                            Toast.makeText(this, R.string.mis_camera_open_fail, Toast.LENGTH_SHORT).show();
                            finish();
                            e.printStackTrace();
                        }
                        deal(i);
                        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }
                }
            } else {
                Toast.makeText(this, R.string.mis_no_camera, Toast.LENGTH_SHORT).show();
                finish();
            }
            if(mCameraCount == 1) {
                mCameraSwitch.setVisibility(View.GONE);
            }
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            Log.d(TAG, "surfaceDestroyed: ");
            //停止预览并释放摄像头资源
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }


    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        if (mMediaRecorder != null) {
            //没有外置存储, 直接停止录制
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            try {
                File targetDir = new File(FileManager.getVideoDir());
                if(null == targetDir) {
                    targetDir = Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                }
                mTargetFile = new File(targetDir, VIDEO_PREFIX + System.currentTimeMillis() + VIDEO_SUFFIX);
                //mMediaRecorder.reset();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                //从相机采集视频
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                // 从麦克采集音频信息
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

                mMediaRecorder.setOutputFormat(mCamcorderProfile.fileFormat);
                mMediaRecorder.setAudioEncoder(mCamcorderProfile.audioCodec);
                mMediaRecorder.setVideoEncoder(mCamcorderProfile.videoCodec);
                mMediaRecorder.setOutputFile(mTargetFile.getAbsolutePath());
                mMediaRecorder.setVideoSize(mCamcorderProfile.videoFrameWidth, mCamcorderProfile.videoFrameHeight);
                mMediaRecorder.setVideoFrameRate(mCamcorderProfile.videoFrameRate);
                mMediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoBitRate);
                mMediaRecorder.setAudioEncodingBitRate(mCamcorderProfile.audioBitRate);
                mMediaRecorder.setAudioChannels(mCamcorderProfile.audioChannels);
                mMediaRecorder.setAudioSamplingRate(mCamcorderProfile.audioSampleRate);
                mMediaRecorder.setMaxDuration(10000);

                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                Log.i("VideoRecordActivity", "mRotation = " + mRotation);
                mMediaRecorder.setOrientationHint(mRotation);

                mMediaRecorder.prepare();
                //正式录制
                mMediaRecorder.start();
                isRecording = true;
                mCameraSwitch.setEnabled(false);
                Log.i("VideoRecordActivity", "isRecording = " + isRecording);
            } catch (Exception e) {
                finish();
                e.printStackTrace();
            }

        }
    }

    private void showProfileNullDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.mis_get_camera_info_fail)
                .setPositiveButton(R.string.mis_confirm, null)
                .create().show();
    }

    /**
     * 停止录制 并且保存
     */
    private void stopRecordSave() {
        if (isRecording) {
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                finish();
                e.printStackTrace();
            }
            isRecording = false;
            Intent intent = new Intent();
            intent.putExtra(EXTRA_PATH, mTargetFile.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 停止录制, 不保存
     */
    private void stopRecordUnSave() {
        if (isRecording) {
            mCameraSwitch.setEnabled(true);
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
//                finish();
                if (mMediaRecorder != null) {
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                }
                e.printStackTrace();
            }
            isRecording = false;
            if (mTargetFile.exists()) {
                //不保存直接删掉
                mTargetFile.delete();
            }
        }
    }

    /**
     * 相机变焦
     *
     * @param zoomValue
     */
    public void setZoom(int zoomValue) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {//判断是否支持
                int maxZoom = parameters.getMaxZoom();
                if (maxZoom == 0) {
                    return;
                }
                if (zoomValue > maxZoom) {
                    zoomValue = maxZoom;
                }
                parameters.setZoom(zoomValue);
                mCamera.setParameters(parameters);
            }
        }

    }

//    @AfterPermissionGranted(13)
//    private void chooseVideoPermission() {
//        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
//                , Manifest.permission.RECORD_AUDIO};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            initView();
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, "",
//                    13, perms);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//
//    }

    @Override
    public void onLongClick() {
        startRecord();
        mRecordedButton.startProgress();
    }

    @Override
    public void onCancel() {
        stopRecordUnSave();
    }

    @Override
    public void onOver() {
        stopRecordSave();
    }

    private static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        //degrees  the angle that the picture will be rotated clockwise. Valid values are 0, 90, 180, and 270.
        //The starting position is 0 (landscape).
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public int onOrientationChanged(Activity activity, int cameraId, Camera.Parameters parameters) {
        int orientation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        parameters.setRotation(rotation);
        return rotation;
    }

    @Override
    public void onClick(View v) {
        if(null == mCamera) {
            return;
        }
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
            mCamera = Camera.open(mCameraId);//打开当前选中的摄像头
            deal(mCameraId);
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开当前选中的摄像头
            deal(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    public void deal(int cameraId){
        if (mCamera != null) {
            try {
                mCamera.lock();
                Camera.Parameters parameters = mCamera.getParameters();
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    mVideoQuality = CamcorderProfile.QUALITY_720P;
                } else {
                    mVideoQuality = CamcorderProfile.QUALITY_HIGH;
                }
                mRotation = onOrientationChanged(this, cameraId, parameters);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                //实现Camera自动对焦
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        if(mode.contains("continuous-video")) {
                            parameters.setFocusMode("continuous-video");
                        }
                    }
                }
                mCamera.setParameters(parameters);
                setCameraDisplayOrientation(this, cameraId, mCamera);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (RuntimeException e) {
                finish();
                e.printStackTrace();
            }
        }
    }

    /**
     * 变焦手势处理类
     */
    class ZoomGestureListener extends GestureDetector.SimpleOnGestureListener {
        //双击手势事件
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onDoubleTap(e);
            Log.d(TAG, "onDoubleTap: 双击事件");
            if (mMediaRecorder != null) {
                if (!isZoomIn) {
                    setZoom(20);
                    isZoomIn = true;
                } else {
                    setZoom(0);
                    isZoomIn = false;
                }
            }
            return true;
        }
    }

}
