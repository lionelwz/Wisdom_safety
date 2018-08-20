package com.wf.wisdom_safety.ui.inspect.plain;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.wf.util.DensityUtil;
import com.wf.util.FileUtil;
import com.wf.util.RxSubscriptionCollection;
import com.wf.util.SharedPreferenceUtil;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.OfflineDevice;
import com.wf.wisdom_safety.bean.inspect.Plain;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;

/**
 * 巡检计划主页面
 * Created by Lionel on 2017/7/22.
 */
public class PlainMainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private PlainMainAdapter mPlainMainAdapter;
    private List<String> mDownloadPlains;
    private static final String plainFileKey = "plain_download_file_key";
    private static final String plainValueKey = "plain_download_value_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plain_list);
        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);
        mDownloadPlains = (ArrayList<String>)SharedPreferenceUtil.get(this.plainFileKey,this.plainValueKey);

        mPlainMainAdapter = new PlainMainAdapter(this, new ArrayList<Map<String, Object>>());
        mPlainMainAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> plain = mPlainMainAdapter.getPlain(position);
                String plainId = plain.get("plain_id").toString();
                String excuteId = plain.get("uuid").toString();
                Integer status = plain.get("status") == null ? 0 : (int)Float.parseFloat(plain.get("status").toString());
                if(status == 0) {
                    if(mDownloadPlains != null) {
                        for(String str : mDownloadPlains) {
                            if(plainId.equals(str)) {

                                Intent intent = new Intent(PlainMainActivity.this, OffDevicesActivity.class);
                                intent.putExtra("plainId", plainId);
                                intent.putExtra("excuteId", excuteId);
                                startActivity(intent);
                                return;
                            }
                        }
                    }
                    showDownloadDialog(plainId);
                } else {
                    Toast.makeText(PlainMainActivity.this, "该计划已过期", Toast.LENGTH_LONG).show();
                }
            }
        });
        mRecyclerView.setAdapter(mPlainMainAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = DensityUtil.dip2px(PlainMainActivity.this, 5);
            }
        });
        getPlainList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlainList();
    }

    /**
     * 对于未下载计划设备数据的弹出提示下载
     * @param plainId
     */
    private void showDownloadDialog(final String plainId) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PlainMainActivity.this);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        //normalDialog.setTitle("我是一个普通Dialog")
        normalDialog.setMessage("是否下载本计划的离线设备数据?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadPlainDeviceData(plainId);
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

    /**
     * 下载计划设备数据并保存
     * @param plainId
     */
    private void downloadPlainDeviceData(final String plainId) {
        final ProgressDialog pd = ProgressDialog.show(this, "", "正在下载，请稍候...", false, false);
        Subscription subscription = mInspectManager.getOffDevices(plainId).subscribe(new Observer<List<OfflineDevice>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toast.makeText(PlainMainActivity.this, R.string.error_tip, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<OfflineDevice> offlineDevices) {
                pd.dismiss();
                if(offlineDevices.isEmpty()) {
                    Toast.makeText(PlainMainActivity.this, R.string.nothing_data, Toast.LENGTH_SHORT).show();
                } else {
                    String json = JSON.toJSONString(offlineDevices);
                    FileUtil.saveFile(plainId, json);
                    if(mDownloadPlains != null && !mDownloadPlains.contains(plainId)) {
                        mDownloadPlains.add(plainId);
                        SharedPreferenceUtil.save(PlainMainActivity.plainFileKey, PlainMainActivity.plainValueKey, mDownloadPlains);
                    }
                    if(mDownloadPlains == null) {
                        mDownloadPlains = new ArrayList<String>();
                        mDownloadPlains.add(plainId);
                        SharedPreferenceUtil.save(PlainMainActivity.plainFileKey, PlainMainActivity.plainValueKey, mDownloadPlains);
                    }
                    Toast.makeText(PlainMainActivity.this, "离线数据下载成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRxSubscriptionCollection.add(subscription);
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
        mToolbar.setTitle(R.string.inspect_plain_title);
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

    private void getPlainList() {
        final ProgressDialog pd = ProgressDialog.show(this, "", "正在查询，请稍候...", false, false);
        Subscription subscription = mInspectManager.getPlainList().subscribe(new Observer<List<Map<String, Object>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toast.makeText(PlainMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<Map<String, Object>> plains) {
                pd.dismiss();
                if(plains.isEmpty()) {
                    Toast.makeText(PlainMainActivity.this, R.string.nothing_data, Toast.LENGTH_SHORT).show();
                } else {
                    mPlainMainAdapter.setPlainList(plains);
                }
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }

}
