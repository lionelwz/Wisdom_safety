package com.wf.wisdom_safety.ui.inspect.plain;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wf.util.DirTraversal;
import com.wf.util.FileUtil;
import com.wf.util.GlobleConstants;
import com.wf.util.RxSubscriptionCollection;
import com.wf.util.ZipUtils;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observer;

/**
 * 设备详情页面
 * Created by Lionel on 2017/7/24.
 */

public class OffDevicesActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final String TAG = OffDevicesActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.offdevices_gridview)
    GridView gview;
    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private String mPlainId;
    private String mExcuteId;

    private List<OfflineDevice> offDevices;

    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    JSONArray inspectResult = new JSONArray();//巡检结果

    boolean mMenuFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offdevices_list);
        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);
        //获取数据
        getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        if(mMenu != null && mMenuFlag) {
            mMenuFlag = false;
            File jsonFile = new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName(), mExcuteId+".json");
            if(jsonFile.exists()) {
                getMenuInflater().inflate(R.menu.inspect_commit, mMenu);
            }
        }
    }

    @OnClick({R.id.inspect_danger_button, R.id.inspect_repair_button, R.id.inspect_qrcode_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inspect_danger_button:
                Intent intent = new Intent(OffDevicesActivity.this, DangerCommitActivity.class);
                intent.putExtra("plainId", mExcuteId);
                intent.putExtra("excuteId", mExcuteId);
                startActivity(intent);
                break;
            case R.id.inspect_repair_button:
                Intent intentNotDevice = new Intent(OffDevicesActivity.this, NotDeviceCommitActivity.class);
                intentNotDevice.putExtra("plainId", mExcuteId);
                intentNotDevice.putExtra("excuteId", mExcuteId);
                startActivity(intentNotDevice);
                break;
            case R.id.inspect_qrcode_button:
                requestCodeQRCodePermissions();
                break;
        }
    }

    /**
     * 提交检查结果数据
     */
    boolean dangerFlag = false;
    boolean notdeviceFlag = false;

    boolean dangerCompleFlag = true;
    boolean notdeviceCompleFlag = true;
    boolean plainCompleFlag = false;
    private void commitInspectResult() {
        String tipMsg = "";
       if(offDevices.size() != inspectResult.size()) {

           Toast.makeText(OffDevicesActivity.this, "还有设备未检查，无法提交", Toast.LENGTH_LONG).show();
           return;
       } else {
           tipMsg = "确定提交巡检结果?";
       }
           final AlertDialog.Builder confirmDialog =
                   new AlertDialog.Builder(OffDevicesActivity.this);
           confirmDialog.setMessage(tipMsg);
           confirmDialog.setPositiveButton("提交",
                   new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           final String jsonFileStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId+".json";
                           final String jsonDangerfileStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId+"_danger"+".json";
                           final String jsonNotDevicefileStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId+"_notdevice"+".json";
                           final String attachementFoldStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId;
                           final String attachementDangerFoldStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId+"_danger";
                           final String attachementNotDeviceFoldStr = Environment.getExternalStorageDirectory()+"/"+OffDevicesActivity.this.getPackageName()+"/"+mExcuteId+"_notdevice";

                           final long timestamp = System.currentTimeMillis();
                           final AlertDialog.Builder normalDialog =
                                   new AlertDialog.Builder(OffDevicesActivity.this);
                           normalDialog.setMessage("该操作耗时较长并消耗大量数据流量，尽可能在WIFI下操作，是否继续?");
                           normalDialog.setPositiveButton("继续",
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {

                                           final ProgressDialog pd = ProgressDialog.show(OffDevicesActivity.this, "", "正在上传设备巡检数据，请勿关闭...", false, false);

                                           String attachementPath = attachementFoldStr+timestamp+".zip";
                                           String attachementDangerPath = attachementDangerFoldStr+timestamp+".zip";
                                           String attachementNotDevicePath = attachementNotDeviceFoldStr+timestamp+".zip";
                                           File attachementFile = new File(attachementPath);
                                           File attachementDangerFile = new File(attachementDangerPath);
                                           File attachementNotDeviceFile = new File(attachementNotDevicePath);

                                           //根据文件夹压缩里面的文件
                                           try {
                                               //TODO 后面这里需要对图片进行压缩处理，否则文件太大
                                               List<File> files = DirTraversal.refreshFileList(attachementFoldStr);
                                               if( files != null && files.size() > 0) {
                                                   if(!attachementFile.exists()) {
                                                       attachementFile.createNewFile();
                                                   }
                                                   ZipUtils.zipFiles(files, attachementFile);
                                               } else {
                                                   attachementPath = null;
                                               }

                                               File dangerJsonFile = new File(jsonDangerfileStr);
                                               if(dangerJsonFile.exists()) {
                                                   dangerFlag = true;
                                                   dangerCompleFlag = false;

                                                   List<File> dangerfiles = DirTraversal.refreshFileList(attachementDangerFoldStr);
                                                   if( dangerfiles != null && dangerfiles.size() > 0) {
                                                       if(!attachementDangerFile.exists()) {
                                                           attachementDangerFile.createNewFile();
                                                       }
                                                       ZipUtils.zipFiles(dangerfiles, attachementDangerFile);
                                                   } else {
                                                       attachementDangerPath = null;
                                                   }
                                               }

                                               File notdeviceJsonFile = new File(jsonNotDevicefileStr);
                                               if(notdeviceJsonFile.exists()) {
                                                   notdeviceFlag = true;
                                                   notdeviceCompleFlag = false;

                                                   List<File> notdevicefiles = DirTraversal.refreshFileList(attachementNotDeviceFoldStr);
                                                   if( notdevicefiles != null && notdevicefiles.size() > 0) {
                                                       if(!attachementNotDeviceFile.exists()) {
                                                           attachementNotDeviceFile.createNewFile();
                                                       }
                                                       ZipUtils.zipFiles(notdevicefiles, attachementNotDeviceFile);
                                                   } else {
                                                       attachementNotDevicePath = null;
                                                   }
                                               }

                                           } catch (IOException e) {
                                               e.printStackTrace();
                                               pd.dismiss();
                                               Toast.makeText(OffDevicesActivity.this, "操作异常！", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                           mRxSubscriptionCollection.add(mInspectManager.commitInspectResult(mExcuteId, jsonFileStr, attachementPath).subscribe(new Observer<String>() {

                                               @Override
                                               public void onCompleted() {
                                                   plainCompleFlag = true;
                                                   //删除数据
                                                   File delJsonFile = new File(jsonFileStr);
                                                   if(delJsonFile.exists()) {
                                                       delJsonFile.delete();
                                                   }
                                                   File delAttFold = new File(attachementFoldStr);
                                                   if(delAttFold.exists()) {
                                                       FileUtil.deleteAllFilesOfDir(delAttFold);
                                                   }
                                                   File zipFile = new File(attachementFoldStr+timestamp+".zip");
                                                   if(zipFile.exists()) {
                                                       zipFile.delete();
                                                   }
                                                   close();
                                               }

                                               @Override
                                               public void onError(Throwable e) {
                                                   pd.dismiss();
                                                   Toast.makeText(OffDevicesActivity.this, "检查结果上传失败！", Toast.LENGTH_SHORT).show();
                                               }

                                               @Override
                                               public void onNext(String s) {
                                               }

                                               final void close(){
                                                   if(dangerCompleFlag && notdeviceCompleFlag && plainCompleFlag) {
                                                       pd.dismiss();
                                                       Toast.makeText(OffDevicesActivity.this, "检查结果上传成功！", Toast.LENGTH_SHORT).show();
                                                       OffDevicesActivity.this.finish();
                                                   }
                                               }
                                           }));
                                           if(dangerFlag) {
                                               mRxSubscriptionCollection.add(mInspectManager.commitDangerResult(mExcuteId, jsonDangerfileStr, attachementDangerPath).subscribe(new Observer<String>() {

                                                   @Override
                                                   public void onCompleted() {
                                                       dangerCompleFlag = true;
                                                       //删除数据
                                                       File delJsonFile = new File(jsonDangerfileStr);
                                                       if(delJsonFile.exists()) {
                                                           delJsonFile.delete();
                                                       }
                                                       File delAttFold = new File(attachementDangerFoldStr);
                                                       if(delAttFold.exists()) {
                                                           FileUtil.deleteAllFilesOfDir(delAttFold);
                                                       }
                                                       File zipFile = new File(attachementDangerFoldStr+timestamp+".zip");
                                                       if(zipFile.exists()) {
                                                           zipFile.delete();
                                                       }
                                                       close();
                                                   }

                                                   @Override
                                                   public void onError(Throwable e) {
                                                       pd.dismiss();
                                                       Toast.makeText(OffDevicesActivity.this, "检查结果上传失败！", Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onNext(String s) {
                                                   }

                                                   final void close(){
                                                       if(dangerCompleFlag && notdeviceCompleFlag && plainCompleFlag) {
                                                           pd.dismiss();
                                                           Toast.makeText(OffDevicesActivity.this, "检查结果上传成功！", Toast.LENGTH_SHORT).show();
                                                           OffDevicesActivity.this.finish();
                                                       }
                                                   }
                                               }));
                                           }
                                           if(notdeviceFlag) {
                                               mRxSubscriptionCollection.add(mInspectManager.commitNotDeviceResult(mExcuteId, jsonNotDevicefileStr, attachementNotDevicePath).subscribe(new Observer<String>() {

                                                   @Override
                                                   public void onCompleted() {
                                                       notdeviceCompleFlag = true;
                                                       //删除数据
                                                       File delJsonFile = new File(jsonNotDevicefileStr);
                                                       if(delJsonFile.exists()) {
                                                           delJsonFile.delete();
                                                       }
                                                       File delAttFold = new File(attachementNotDeviceFoldStr);
                                                       if(delAttFold.exists()) {
                                                           FileUtil.deleteAllFilesOfDir(delAttFold);
                                                       }
                                                       File zipFile = new File(attachementNotDeviceFoldStr+timestamp+".zip");
                                                       if(zipFile.exists()) {
                                                           zipFile.delete();
                                                       }
                                                       close();
                                                   }

                                                   @Override
                                                   public void onError(Throwable e) {
                                                       pd.dismiss();
                                                       Toast.makeText(OffDevicesActivity.this, "检查结果上传失败！", Toast.LENGTH_SHORT).show();
                                                   }

                                                   @Override
                                                   public void onNext(String s) {
                                                   }

                                                   final void close(){
                                                       if(dangerCompleFlag && notdeviceCompleFlag && plainCompleFlag) {
                                                           pd.dismiss();
                                                           Toast.makeText(OffDevicesActivity.this, "检查结果上传成功！", Toast.LENGTH_SHORT).show();
                                                           OffDevicesActivity.this.finish();
                                                       }
                                                   }
                                               }));
                                           }

                                       }
                                   });
                           normalDialog.setNegativeButton("取消",
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           //...To-do
                                       }
                                   });
                           // 显示
                           normalDialog.show();
                       }
                   });
           confirmDialog.setNegativeButton("取消",
                   new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           //...To-do
                       }
                   });
           // 显示
           confirmDialog.show();

    }

    /**
     * 获取设备数据
     * @return
     */
    public List<Map<String, Object>> getData(){
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        Intent intent = getIntent();
        mPlainId = intent.getStringExtra("plainId");
        mExcuteId = intent.getStringExtra("excuteId");
        String deviceStrs = FileUtil.loadFile(mPlainId);
        offDevices = JSONObject.parseArray(deviceStrs, OfflineDevice.class);

        File jsonFile = new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName()+"/"+mExcuteId+".json");
        if(jsonFile.exists()) {
            inspectResult = JSONArray.parseArray(FileUtil.loadFile(jsonFile));
        }

        for(OfflineDevice device : offDevices) {
            Map<String, Object> map = new HashMap<String, Object>();
            JSONObject nowDevice = null;
            for(Object obj : inspectResult) {
                JSONObject jsonObject = (JSONObject) obj;
                String deviceId = jsonObject.getString("deviceId");
                if (deviceId.equals(device.getUuid())) {
                    nowDevice = jsonObject;
                }
            }
            Integer state = 0;
            if(nowDevice == null) {
                switch (device.getType()) {
                    case GlobleConstants.DEVICE_TYPE_EXTINGUISHER :
                        map.put("icon", R.drawable.device_icon_extinguisher_blue);
                        break;
                    case GlobleConstants.DEVICE_TYPE_FIREPLUG :
                        map.put("icon", R.drawable.device_icon_fireplug_blue);
                        break;
                    case GlobleConstants.DEVICE_TYPE_ENTRYTOOL :
                        map.put("icon", R.drawable.device_icon_general_blue);
                        break;
                    case GlobleConstants.DEVICE_TYPE_ANNUNCIATOR :
                        map.put("icon", R.drawable.device_icon_annunciator_blue);
                        break;
                    case GlobleConstants.DEVICE_TYPE_ELEVATOR :
                        map.put("icon", R.drawable.device_icon_elevator_blue);
                        break;
                    default:
                        map.put("icon", R.drawable.device_icon_general_blue);
                        break;
                }
            } else {
                state = nowDevice.getInteger("state");
                if(state == 1) {
                    //检查结果为正常的
                    switch (device.getType()) {
                        case GlobleConstants.DEVICE_TYPE_EXTINGUISHER :
                            map.put("icon", R.drawable.device_icon_extinguisher_green);
                            break;
                        case GlobleConstants.DEVICE_TYPE_FIREPLUG :
                            map.put("icon", R.drawable.device_icon_fireplug_green);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ENTRYTOOL :
                            map.put("icon", R.drawable.device_icon_general_green);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ANNUNCIATOR :
                            map.put("icon", R.drawable.device_icon_annunciator_green);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ELEVATOR :
                            map.put("icon", R.drawable.device_icon_elevator_green);
                            break;
                        default:
                            map.put("icon", R.drawable.device_icon_general_green);
                            break;
                    }
                } else if(state == 2) {
                    //检查结果为故障的
                    switch (device.getType()) {
                        case GlobleConstants.DEVICE_TYPE_EXTINGUISHER :
                            map.put("icon", R.drawable.device_icon_extinguisher_yellow);
                            break;
                        case GlobleConstants.DEVICE_TYPE_FIREPLUG :
                            map.put("icon", R.drawable.device_icon_fireplug_yellow);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ENTRYTOOL :
                            map.put("icon", R.drawable.device_icon_general_yellow);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ANNUNCIATOR :
                            map.put("icon", R.drawable.device_icon_annunciator_yellow);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ELEVATOR :
                            map.put("icon", R.drawable.device_icon_elevator_yellow);
                            break;
                        default:
                            map.put("icon", R.drawable.device_icon_general_yellow);
                            break;
                    }
                } else {
                    //检查结果为损坏的
                    switch (device.getType()) {
                        case GlobleConstants.DEVICE_TYPE_EXTINGUISHER :
                            map.put("icon", R.drawable.device_icon_extinguisher_red);
                            break;
                        case GlobleConstants.DEVICE_TYPE_FIREPLUG :
                            map.put("icon", R.drawable.device_icon_fireplug_red);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ENTRYTOOL :
                            map.put("icon", R.drawable.device_icon_general_red);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ANNUNCIATOR :
                            map.put("icon", R.drawable.device_icon_annunciator_red);
                            break;
                        case GlobleConstants.DEVICE_TYPE_ELEVATOR :
                            map.put("icon", R.drawable.device_icon_elevator_red);
                            break;
                        default:
                            map.put("icon", R.drawable.device_icon_general_red);
                            break;
                    }
                }

            }

            map.put("floor", "楼层："+(device.getFloor() == null ? "" : device.getFloor()+"层"));
            map.put("name", "设备："+device.getName());
            map.put("state", state);
            map.put("offDeviceId", device.getUuid());
            data_list.add(map);
        }
        String [] from ={"icon", "floor", "name"};
        int [] to = {R.id.device_icon, R.id.device_floor, R.id.device_name};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.offdevices_list_item, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = data_list.get(position);
                Integer state = Integer.parseInt(map.get("state").toString());
                String offDeviceId = map.get("offDeviceId").toString();
                if(state == 0) {
                    Toast.makeText(OffDevicesActivity.this, "该设备还未检查", Toast.LENGTH_LONG).show();
                } else {
                    if(offDevices != null) {
                        for (OfflineDevice device : offDevices) {
                            if (device.getUuid().equals(offDeviceId)) {
                                Intent intent = new Intent(OffDevicesActivity.this, DeviceInspectActivity.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("device", device);
                                mBundle.putString("plainId", mPlainId);
                                mBundle.putString("excuteId", mExcuteId);
                                intent.putExtras(mBundle);
                                startActivity(intent);
                                return;
                            }
                        }
                    }
                }
            }
        });
        return data_list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
    }

    Menu mMenu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        File jsonFile = new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName(), mExcuteId+".json");
        if(jsonFile.exists()) {
            mMenuFlag = false;
            getMenuInflater().inflate(R.menu.inspect_commit, menu);
        }
        return true;
    }

    /**
     * 设置Toolbar
     */
    private void setToolbar() {
        mToolbar.setTitle(R.string.inspect_plain_offdevice);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_inspect_commit:
                        commitInspectResult();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            Intent intent = new Intent(OffDevicesActivity.this, ScanActivity.class);
            intent.putExtra("plainId", mPlainId);
            intent.putExtra("excuteId", mExcuteId);
            startActivity(intent);
        }
    }
}
