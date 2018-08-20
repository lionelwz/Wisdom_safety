package com.wf.wisdom_safety.ui.inspect.plain;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.google.zxing.common.StringUtils;
import com.wf.util.FileUtil;
import com.wf.util.GlobleConstants;
import com.wf.util.RxSubscriptionCollection;
import com.wf.util.enum_type.OffDeviceState;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 设备巡检
 * Created by Lionel on 2017/7/25.
 */

public class DeviceInspectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_IMAGE = 30;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    @Bind(R.id.device_type_icon)
    ImageView deviceTypeIcon;
    @Bind(R.id.device_name)
    TextView deviceName;
    @Bind(R.id.device_type)
    TextView deviceType;
    @Bind(R.id.device_building)
    TextView deviceBuilding;
    @Bind(R.id.device_floor)
    TextView deviceFloor;
    @Bind(R.id.device_state_group)
    RadioGroup deviceStateGroup;//设备状态选择

    @Bind(R.id.fault_area_layout)
    LinearLayout areaLayout;
    @Bind(R.id.desc_layout)
    LinearLayout descLayout;
    @Bind(R.id.cur_image_layout)
    RelativeLayout curImageLayout;
    @Bind(R.id.cur_video_layout)
    RelativeLayout curVideoLayout;

    @Bind(R.id.video_image)
    ImageView mVideoImage;
    @Bind(R.id.video_play)
    ImageView mVideoPlay;

    @Bind(R.id.fault_desc_text)
    EditText faultDesc;//非正常情况描述
    @Bind(R.id.upload_image_btn)
    Button uploadBtn;//上传图片按钮
    @Bind(R.id.inspect_device_sure)
    Button sureBtn;//确定按钮
    @Bind(R.id.image_recycler)
    RecyclerView imageRecycler;//已上传的图片

    private ArrayList<String> mImgsNames = new ArrayList<>();
    private ArrayList<String> mData = new ArrayList<>();

    private ArrayList<String> mVideoSelectPath = new ArrayList<>();
    private InspectImageAdapter mAdapter;
    private static final String TAG = DeviceInspectActivity.class.getSimpleName();

    Integer deviceState=OffDeviceState.NORMAL.getValue();//设备状态

    private OfflineDevice offdevice;
    private String mPlainId;
    private String mExcuteId;
    File packageFold;//计划文件夹
    File jsonFile;//json文件
    JSONArray inspectResult = new JSONArray();//巡检结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_offdevices);
        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);

        offdevice = (OfflineDevice)getIntent().getSerializableExtra("device");
        mPlainId = getIntent().getStringExtra("plainId");
        mExcuteId = getIntent().getStringExtra("excuteId");
        //显示设备详情信息
        //设置设备显示icon
        if(offdevice != null) {
            switch (offdevice.getType()) {
                case GlobleConstants.DEVICE_TYPE_EXTINGUISHER :
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_extinguisher_blue);
                    break;
                case GlobleConstants.DEVICE_TYPE_FIREPLUG :
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_fireplug_blue);
                    break;
                case GlobleConstants.DEVICE_TYPE_ENTRYTOOL :
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_general_blue);
                    break;
                case GlobleConstants.DEVICE_TYPE_ANNUNCIATOR :
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_annunciator_blue);
                    break;
                case GlobleConstants.DEVICE_TYPE_ELEVATOR :
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_elevator_blue);
                    break;
                default:
                    deviceTypeIcon.setImageResource(R.drawable.device_icon_general_blue);
                    break;
            }
            deviceName.setText(offdevice.getName());
            Integer type = offdevice.getType();
            if(type == 1) {
                deviceType.setText("灭火器");
            } else if(type == 2) {
                deviceType.setText("消防栓");
            } else if(type == 3) {
                deviceType.setText("破拆工具");
            } else if(type == 4) {
                deviceType.setText("报警器");
            } else if(type == 5) {
                deviceType.setText("灌水器");
            } else if(type == 6) {
                deviceType.setText("消防泵");
            } else if(type == 7) {
                deviceType.setText("喷淋泵");
            } else if(type == 8) {
                deviceType.setText("排烟风机");
            } else if(type == 9) {
                deviceType.setText("防火卷帘");
            } else if(type == 10) {
                deviceType.setText("防烟风机");
            } else {
                deviceType.setText("未知");
            }
            deviceBuilding.setText(offdevice.getBuildingName());
            deviceFloor.setText(offdevice.getFloor() == null ? "" : (offdevice.getFloor()+"层"));

            initStateGroup();
            initFile();
            initInspectResult();
            if(offdevice.getType() == 6 || offdevice.getType() == 7 || offdevice.getType() == 8 || offdevice.getType() == 9 || offdevice.getType() == 10 ) {
                areaLayout.setVisibility(View.VISIBLE);
                descLayout.setVisibility(View.GONE);
                curVideoLayout.setVisibility(View.VISIBLE);

                if(mData != null && mData.size() > 0) {
                    File videoFile = new File(mData.get(0));
                    Glide.with(this)
                            .load(Uri.fromFile(videoFile))
                            .placeholder(R.drawable.mis_default_error)
                            .centerCrop()
                            .into(mVideoImage);
                }
            } else {
                mAdapter = new InspectImageAdapter(this, mData);
                imageRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
                imageRecycler.setAdapter(mAdapter);
                setAdapterProperties(true);
            }
        } else {
            Toast.makeText(DeviceInspectActivity.this, "设备数据获取失败", Toast.LENGTH_SHORT).show();
            DeviceInspectActivity.this.finish();
        }
    }

    /**
     * 设备状态选择
     */
    private void initStateGroup() {
        deviceStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //在设备非正常情况下显示描述和现场图片
                if(offdevice.getType() == 6 || offdevice.getType() == 7 || offdevice.getType() == 8 || offdevice.getType() == 9 || offdevice.getType() == 10 ) {
                    areaLayout.setVisibility(View.VISIBLE);
                    curVideoLayout.setVisibility(View.VISIBLE);
                    if(radioButtonId != R.id.device_normal) {
                        descLayout.setVisibility(View.VISIBLE);
                    } else {
                        descLayout.setVisibility(View.GONE);
                    }
                } else {
                    if(radioButtonId != R.id.device_normal){
                        areaLayout.setVisibility(View.VISIBLE);
                        curImageLayout.setVisibility(View.VISIBLE);
                    } else {
                        areaLayout.setVisibility(View.GONE);
                    }
                }

                //设置设备状态值
                switch (radioButtonId) {
                    case R.id.device_normal:
                        deviceState = OffDeviceState.NORMAL.getValue();
                        break;
                    case R.id.device_fault:
                        deviceState = OffDeviceState.FAULT.getValue();
                        break;
                    case R.id.device_breakdown_light:
                        deviceState = OffDeviceState.BREAKDOWN_LIGHT.getValue();
                        break;
                    case R.id.device_breakdown_middle:
                        deviceState = OffDeviceState.BREAKDOWN_MIDDLE.getValue();
                        break;
                    case R.id.device_breakdown_serious:
                        deviceState = OffDeviceState.BREAKDOWN_SERIOUS.getValue();
                        break;
                }
            }
        });
    }

    @OnClick({R.id.upload_image_btn, R.id.inspect_device_sure, R.id.video_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload_image_btn:
                if(mData.size() >=5 ) {
                    Toast.makeText(DeviceInspectActivity.this, "每个设备最多可上传5张图片！", Toast.LENGTH_LONG).show();
                    return;
                }
                requiresPermission();
                break;
            case R.id.video_image:
                chooseVideoPermission();
                break;
            case R.id.inspect_device_sure:
                sureDevice();
                break;

        }
    }

    //初始化设备巡检结果
    private void initInspectResult() {
        for(Object obj : inspectResult) {
            JSONObject jsonObject = (JSONObject)obj;
            String deviceId = jsonObject.getString("deviceId");
            if(deviceId.equals(offdevice.getUuid())){
                deviceState = jsonObject.getInteger("state");
                   if(deviceState == OffDeviceState.FAULT.getValue()) {
                       deviceStateGroup.check(R.id.device_fault);
                   } else if(deviceState == OffDeviceState.BREAKDOWN_LIGHT.getValue()) {
                       deviceStateGroup.check(R.id.device_breakdown_light);
                   } else if(deviceState == OffDeviceState.BREAKDOWN_MIDDLE.getValue()) {
                       deviceStateGroup.check(R.id.device_breakdown_middle);
                   } else if(deviceState == OffDeviceState.BREAKDOWN_SERIOUS.getValue()) {
                       deviceStateGroup.check(R.id.device_breakdown_serious);
                   }
                   if(deviceState != OffDeviceState.NORMAL.getValue()) {
                       areaLayout.setVisibility(View.VISIBLE);
                   }
                String desc = jsonObject.getString("remark");
                if(desc != null) {
                    faultDesc.setText(desc);
                }
                JSONArray imgsArray = jsonObject.getJSONArray("imgs");

                if(imgsArray != null) {
                    //Toast.makeText(DeviceInspectActivity.this, imgsArray.size(), Toast.LENGTH_SHORT).show();
                    for(Object imgObj : imgsArray) {
                        String oldOmgName = imgObj.toString();
                       // Toast.makeText(DeviceInspectActivity.this, packageFold.getAbsolutePath()+"/"+oldOmgName, Toast.LENGTH_SHORT).show();
                        mData.add(packageFold.getAbsolutePath()+"/"+oldOmgName);

                    }
                }

                //mAdapter.setNewData(mData);
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(DeviceInspectActivity.this);
                normalDialog.setMessage("该设备已检查，是否更改?");
                normalDialog.setPositiveButton("不更改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < deviceStateGroup.getChildCount(); i++) {
                                    deviceStateGroup.getChildAt(i).setEnabled(false);
                                }
                                faultDesc.setEnabled(false);
                                uploadBtn.setEnabled(false);
                                sureBtn.setVisibility(View.GONE);
                            }
                        });
                normalDialog.setNegativeButton("更改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }
        }
    }

    //确定设备状态
    private void sureDevice() {
        String desc = faultDesc.getText().toString().trim();
        if(offdevice.getType() == 6 || offdevice.getType() == 7 || offdevice.getType() == 8 || offdevice.getType() == 9 || offdevice.getType() == 10 ) {
            if(deviceState != 1) {
                if(desc == null || desc.equals("")) {
                    Toast.makeText(DeviceInspectActivity.this, "请输入异常描述信息！", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if(mData.size() == 0) {
                Toast.makeText(DeviceInspectActivity.this, "请上传视频！", Toast.LENGTH_LONG).show();
                return;
            }

        } else {
            if(deviceState != 1) {
                if(desc == null || desc.equals("")) {
                    Toast.makeText(DeviceInspectActivity.this, "请输入异常描述信息！", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mData.size() == 0) {
                    Toast.makeText(DeviceInspectActivity.this, "请上传异常图片或视频！", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        JSONObject result = new JSONObject();
        result.put("deviceId", offdevice.getUuid());
        result.put("state",deviceState);
        result.put("createTime", new Date());
        if(offdevice.getType() == 6 || offdevice.getType() == 7 || offdevice.getType() == 8 || offdevice.getType() == 9 || offdevice.getType() == 10 ) {
            result.put("remark", desc);
            result.put("imgs",mImgsNames);
        } else {
            if(deviceState != 1) {
                result.put("remark", desc);
                result.put("imgs",mImgsNames);
            }
        }


        //如果已经存在，先删除再保存
        for(Object obj : inspectResult) {
            JSONObject jsonObject = (JSONObject) obj;
            String deviceId = jsonObject.getString("deviceId");
            if (deviceId.equals(offdevice.getUuid())) {
                inspectResult.remove(obj);
            }
        }

        inspectResult.add(result);
        FileUtil.saveFile(jsonFile, inspectResult.toString());
        Toast.makeText(DeviceInspectActivity.this, "该设备检查成功！", Toast.LENGTH_LONG).show();
        this.finish();
    }


    /**
     * 图片删除监听
     */
    private BaseQuickAdapter.OnRecyclerViewItemChildClickListener mOnRecyclerViewItemChildClickListener
            = new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            mData.remove(mAdapter.getItem(position));
            mImgsNames.remove(mAdapter.getItem(position));
            mAdapter.remove(position);
        }
    };


    private ItemTouchHelper mItemTouchHelper;
    private ItemDragAndSwipeCallback mItemDragAndSwipeCallback;
    /**
     * 设置适配器属性
     * @param enable false:禁止所有属性
     */
   private void setAdapterProperties(boolean enable) {
        if (enable) {
            mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
            mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
            mItemTouchHelper.attachToRecyclerView(imageRecycler);
            //设置可拖动
            mAdapter.enableDragItem(mItemTouchHelper);
            mAdapter.setOnItemDragListener(mOnItemDragListener);
            //删除按钮点击监听
            mAdapter.setOnRecyclerViewItemChildClickListener(mOnRecyclerViewItemChildClickListener);
        } else {
            mAdapter.disableDragItem();
            //删除按钮点击监听
            mAdapter.setOnRecyclerViewItemChildClickListener(null);
            //列表项点击监听
            mAdapter.setOnRecyclerViewItemClickListener(null);
        }
    }

    /**
     * item拖动监听
     */
    private OnItemDragListener mOnItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag start");
            if (!mAdapter.isShowDeleteView()) {
                mAdapter.setShowDeleteView(true);
            }
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            Log.d(TAG, "move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition());

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag end");
            mAdapter.notifyDataSetChanged();
        }
    };

    private void initFile() {
        // 从相机返回的数据
        if (hasSdcard()) {
            packageFold= new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName()+"/"+mExcuteId);
            if(!packageFold.exists()) {
                packageFold.mkdirs();
            }
            jsonFile = new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName(), mExcuteId+".json");
            if(jsonFile.exists()) {
                inspectResult = JSONArray.parseArray(FileUtil.loadFile(jsonFile));
            }
        } else {
            Toast.makeText(DeviceInspectActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
    }

    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private File tempFile;
    private String imgName;
    /**
     * 调用相机
     */
    public void onAddImageClicked() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            imgName = UUID.randomUUID().toString()+".jpg";
            tempFile = new File(packageFold.getAbsolutePath(), imgName);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
    * 判断sdcard是否被挂载
    */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PHOTO_REQUEST_CAREMA && resultCode == RESULT_OK) {
            // 从相机返回的数据
            if (hasSdcard()) {
                mData.add(tempFile.getAbsolutePath());
                mImgsNames.add(tempFile.getName());
                mAdapter.setNewData(mData);
            } else {
                Toast.makeText(DeviceInspectActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_VIDEO && resultCode == RESULT_OK) {
            if (hasSdcard()) {
                mData.add(tempFile.getAbsolutePath());
                mImgsNames.add(tempFile.getName());
                File videoFile = new File(tempFile.getAbsolutePath());
                if (videoFile.exists()) {
                    mVideoPlay.setVisibility(View.VISIBLE);
                    // 显示图片
                    Glide.with(this)
                            .load(Uri.fromFile(videoFile))
                            .placeholder(R.drawable.mis_default_error)
                            .centerCrop()
                            .into(mVideoImage);
                } else {
                    mVideoImage.setImageResource(R.drawable.icon_add_video);
                    mVideoPlay.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(DeviceInspectActivity.this, "未找到存储卡，无法存储视频！", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //删除按钮显示时，点击返回键取消删除按钮显示
        if (mAdapter != null && mAdapter.isShowDeleteView()) {
            mAdapter.setShowDeleteView(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
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
    }

    @AfterPermissionGranted(11)
    private void requiresPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(DeviceInspectActivity.this, perms)) {
            onAddImageClicked();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request_denied),
                    11, perms);
        }
    }

    @AfterPermissionGranted(13)
    private void chooseVideoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(DeviceInspectActivity.this, perms)) {
            onAddVideoClicked();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request_denied),
                    13, perms);
        }
    }

    private static final int REQUEST_VIDEO = 32;
    public void onAddVideoClicked() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (hasSdcard()) {
            imgName = UUID.randomUUID().toString()+".mp4";
            tempFile = new File(packageFold.getAbsolutePath(), imgName);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);//限制录制时间10秒
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024L * 1024 * 5);

        startActivityForResult(intent, REQUEST_VIDEO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("DeviceInspectActivity", "**********onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
