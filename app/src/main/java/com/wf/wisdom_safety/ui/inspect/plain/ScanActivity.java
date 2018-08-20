package com.wf.wisdom_safety.ui.inspect.plain;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wf.util.FileUtil;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 扫码界面
 * Created by Lionel on 2017/7/25.
 */

public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private String mPlainId;
    private String mExcuteId;
    List<OfflineDevice> offDevices;

    private static final String TAG = ScanActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_scan);

        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);
        Intent intent = getIntent();
        mPlainId = intent.getStringExtra("plainId");
        mExcuteId = intent.getStringExtra("excuteId");
        String deviceStrs = FileUtil.loadFile(mPlainId);
        offDevices = JSONObject.parseArray(deviceStrs, OfflineDevice.class);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mQRCodeView.startCamera();

        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeView.startCamera();

        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    /**
     * 设置Toolbar
     */
    private void setToolbar() {
        mToolbar.setTitle(R.string.inspect_plain_scan);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        vibrate();
        if(offDevices != null) {
            for(OfflineDevice device : offDevices) {
                if(device.getUuid().equals(result.trim())) {
                    Intent intent = new Intent(ScanActivity.this, DeviceInspectActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("device",device);
                    mBundle.putString("plainId", mPlainId);
                    mBundle.putString("excuteId", mExcuteId);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    return;
                }
            }
            Toast.makeText(this, "此二维码非本计划设备！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "获取设备数据异常！", Toast.LENGTH_LONG).show();
        }

        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开相机出错", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;
            case R.id.close_scan:
                ScanActivity.this.finish();
                break;
        }
    }

}
