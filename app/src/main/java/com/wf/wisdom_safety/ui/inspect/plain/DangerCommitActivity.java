package com.wf.wisdom_safety.ui.inspect.plain;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.wf.util.FileUtil;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Lionel on 2017/9/15.
 */

public class DangerCommitActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG =DangerCommitActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private String mPlainId;
    private String mExcuteId;
    File packageFold;//计划文件夹
    File jsonFile;//json文件

    JSONArray inspectDangerResult = new JSONArray();//巡检结果

    @Bind(R.id.inspect_danger_address)
    EditText dangerAddress;
    @Bind(R.id.danger_level_group)
    RadioGroup dangerLevelGroup;//设备状态选择
    @Bind(R.id.danger_desc_text)
    EditText dangerDescription;
    @Bind(R.id.danger_upload_image_btn)
    Button uploadBtn;//上传图片按钮
    @Bind(R.id.image_recycler)
    RecyclerView imageRecycler;//已上传的图片
    @Bind(R.id.inspect_danger_sure)
    Button sureBtn;//确定按钮

    private ArrayList<String> mImgsNames = new ArrayList<>();
    private ArrayList<String> mData = new ArrayList<>();
    private InspectImageAdapter mAdapter;

    Integer dangerLevel=1;//设备状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_danger);
        ButterKnife.bind(this);
        setToolbar();

        DaggerInspectComponent.create().inject(this);
        mPlainId = getIntent().getStringExtra("plainId");
        mExcuteId = getIntent().getStringExtra("excuteId");

        initStateGroup();
        initFile();

        mAdapter = new InspectImageAdapter(this, mData);
        imageRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        imageRecycler.setAdapter(mAdapter);
        setAdapterProperties(true);
    }


    /**
     * 设置Toolbar
     */
    private void setToolbar() {
        mToolbar.setTitle(R.string.inspect_danger_title);
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

    @OnClick({R.id.danger_upload_image_btn, R.id.inspect_danger_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.danger_upload_image_btn:
                if(mData.size() >=5 ) {
                    Toast.makeText(DangerCommitActivity.this, "每次最多可上传5张图片！", Toast.LENGTH_LONG).show();
                    return;
                }
                requiresPermission();
                break;
            case R.id.inspect_danger_sure:
                sureDanger();
                break;
        }
    }

    /**
     * 设备状态选择
     */
    private void initStateGroup() {
        dangerLevelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();

                //设置设备状态值
                switch (radioButtonId) {
                    case R.id.danger_one:
                        dangerLevel = 1;
                        break;
                    case R.id.danger_two:
                        dangerLevel = 2;
                        break;
                    case R.id.danger_three:
                        dangerLevel = 3;
                        break;

                }
            }
        });
    }

    private void sureDanger() {
        String address = dangerAddress.getText() == null ? null : dangerAddress.getText().toString().trim();
        String desc = dangerDescription.getText() == null ? null : dangerDescription.getText().toString().trim();

        if(address == null || address.equals("")) {
            Toast.makeText(DangerCommitActivity.this, "请输入地址信息！", Toast.LENGTH_LONG).show();
            return;
        }
        if(desc == null || desc.equals("")) {
            Toast.makeText(DangerCommitActivity.this, "请输入描述信息！", Toast.LENGTH_LONG).show();
            return;
        }
        if(mData.size() == 0) {
            Toast.makeText(DangerCommitActivity.this, "请上传隐患图片！", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject result = new JSONObject();

        result.put("plainId", mPlainId);
        result.put("excuteId", mExcuteId);
        result.put("address", address);
        result.put("level", dangerLevel);
        result.put("description", desc);
        result.put("createTime", new Date());
        result.put("imgs",mImgsNames);

        inspectDangerResult.add(result);
        FileUtil.saveFile(jsonFile, inspectDangerResult.toString());
        Toast.makeText(DangerCommitActivity.this, "添加安全隐患成功！", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
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
            packageFold= new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName()+"/"+mExcuteId+"_danger");
            if(!packageFold.exists()) {
                packageFold.mkdirs();
            }
            jsonFile = new File(Environment.getExternalStorageDirectory()+"/"+this.getPackageName(), mExcuteId+"_danger"+".json");
            if(jsonFile.exists()) {
                inspectDangerResult = JSONArray.parseArray(FileUtil.loadFile(jsonFile));
            }
        } else {
            Toast.makeText(DangerCommitActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
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
                Toast.makeText(DangerCommitActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //删除按钮显示时，点击返回键取消删除按钮显示
        if (mAdapter.isShowDeleteView()) {
            mAdapter.setShowDeleteView(false);
        } else {
            super.onBackPressed();
        }
    }

    @AfterPermissionGranted(11)
    private void requiresPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(DangerCommitActivity.this, perms)) {
            onAddImageClicked();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request_denied),
                    11, perms);
        }
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
