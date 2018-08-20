package com.wf.wisdom_safety.ui.inspect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.model.user.UserManager;
import com.wf.wisdom_safety.ui.inspect.building.BuildingMainActivity;
import com.wf.wisdom_safety.ui.inspect.danger.DangerMainActivity;
import com.wf.wisdom_safety.ui.inspect.notdevice.NotDeviceMainActivity;
import com.wf.wisdom_safety.ui.inspect.plain.PlainMainActivity;
import com.wf.wisdom_safety.ui.inspect.record.ExcuteRecordActivity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;

import static android.content.ContentValues.TAG;

/**
 * 巡检首页
 * Created by Lionel on 2017/7/21.
 */

public class InspectFragment extends Fragment {

    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;
    @Inject
    UserManager mUserManager;
    @Inject
    Picasso mPicasso;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.inspect_plain_layout)
    RelativeLayout mPlainLayout;


    private Context mContext;

    public InspectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        DaggerInspectComponent.create().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        getInspectDatas();
    }

    private void getInspectDatas() {
        Subscription subscribe = mInspectManager.getInspectDatas().subscribe(new Observer<Map<String, Object>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Map<String, Object> stringObjectMap) {
                setInspectPage(stringObjectMap);
            }
        });
        mRxSubscriptionCollection.add(subscribe);
    }

    @Bind(R.id.inspect_time_task_count)
    TextView plainTimeTaskView;
    @Bind(R.id.inspect_day_task_count)
    TextView plainDayTaskView;
    @Bind(R.id.inspect_week_task_count)
    TextView plainWeekTaskView;
    @Bind(R.id.inspect_month_task_count)
    TextView plainmonthTaskView;

    @Bind(R.id.inspect_record_day)
    TextView dayRecordView;
    @Bind(R.id.inspect_record_week)
    TextView weekRecordView;
    @Bind(R.id.inspect_record_month)
    TextView monthRecordView;

    @Bind(R.id.inspect_danger_nodeal_count)
    TextView nodealCountView;
    @Bind(R.id.inspect_danger_month_count)
    TextView dangerMonthCountView;
    @Bind(R.id.inspect_danger_month_deal)
    TextView dangerDealMonthCountView;

    @Bind(R.id.inspect_fault_nodeal_count)
    TextView nodealNotDeviceCountView;
    @Bind(R.id.inspect_fault_month_total)
    TextView notDeviceMonthCountView;
    @Bind(R.id.inspect_fault_month_deal)
    TextView notDeviceDealMonthCountView;

    @Bind(R.id.inspect_device_count_total)
    TextView deviceCountTotalView;
    @Bind(R.id.inspect_device_count_normal)
    TextView deviceCountNormalView;
    @Bind(R.id.inspect_device_count_fault)
    TextView deviceCountFaultView;
    @Bind(R.id.inspect_device_count_breakdown)
    TextView deviceCountBreakdownView;

    @Bind(R.id.inspect_building_total)
    TextView buildingCountView;

    /**
     * 设置巡检首页数据
     * @param inspectDatas
     */
    private void setInspectPage(Map<String, Object> inspectDatas) {
        if(inspectDatas != null) {
            //巡检计划
            List<Map<String, Object>> plainDatas = (List<Map<String, Object>>)inspectDatas.get("plainDatas");
            if(plainDatas != null) {
                for(Map<String, Object> map : plainDatas) {

                    Integer plainType = map.get("plainType") == null ? null : (int)Float.parseFloat(map.get("plainType").toString());
                    if(plainType != null) {
                        Integer taskCount = (int)Float.parseFloat(map.get("plainCount").toString());
                        if(plainType == 1) {
                            plainTimeTaskView.setText(taskCount+"");
                        } else if(plainType == 2) {
                            plainDayTaskView.setText(taskCount+"");
                        } else if(plainType == 3) {
                            plainWeekTaskView.setText(taskCount+"");
                        } else if(plainType == 4) {
                            plainmonthTaskView.setText(taskCount+"");
                        }
                    }

                }
            }

            //建筑总数
            String buildingCount = inspectDatas.get("buildingCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("buildingCount").toString()));
            buildingCountView.setText(buildingCount);

            String offdeviceCount = inspectDatas.get("offdeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("offdeviceCount").toString()));
            deviceCountTotalView.setText(offdeviceCount);
            String normalDeviceCount = inspectDatas.get("normalDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("normalDeviceCount").toString()));
            deviceCountNormalView.setText(normalDeviceCount);
            String faultDeviceCount = inspectDatas.get("faultDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("faultDeviceCount").toString()));
            deviceCountFaultView.setText(faultDeviceCount);
            String breakdownDeviceCount = inspectDatas.get("breakdownDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("breakdownDeviceCount").toString()));
            deviceCountBreakdownView.setText(breakdownDeviceCount);

            String dayRecordCount = inspectDatas.get("dayRecordCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("dayRecordCount").toString()));
            dayRecordView.setText(dayRecordCount);
            String weekRecordCount = inspectDatas.get("weekRecordCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("weekRecordCount").toString()));
            weekRecordView.setText(weekRecordCount);
            String monthRecordCount = inspectDatas.get("monthRecordCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("monthRecordCount").toString()));
            monthRecordView.setText(monthRecordCount);

            //未处理隐患
            String noDealDangerCount = inspectDatas.get("noDealDangerCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("noDealDangerCount").toString()));
            nodealCountView.setText(noDealDangerCount);
            //本月上报隐患
            String thisMonthDangerCount = inspectDatas.get("thisMonthDangerCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("thisMonthDangerCount").toString()));
            dangerMonthCountView.setText(thisMonthDangerCount);
            //本月处理隐患
            String dealMonthDangerCount = inspectDatas.get("dealMonthDangerCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("dealMonthDangerCount").toString()));
            dangerDealMonthCountView.setText(dealMonthDangerCount);

            //未处理非设备报修
            String noDealNotDeviceCount = inspectDatas.get("noDealNotDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("noDealNotDeviceCount").toString()));
            nodealNotDeviceCountView.setText(noDealNotDeviceCount);
            //本月上报非设备报修
            String thisMonthNotDeviceCount = inspectDatas.get("thisMonthNotDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("thisMonthNotDeviceCount").toString()));
            notDeviceMonthCountView.setText(thisMonthNotDeviceCount);
            //本月处理非设备报修
            String dealMonthNotDeviceCount = inspectDatas.get("dealMonthNotDeviceCount") == null ? "0" : String.valueOf((int)Float.parseFloat(inspectDatas.get("dealMonthNotDeviceCount").toString()));
            notDeviceDealMonthCountView.setText(dealMonthNotDeviceCount);

        }
    }

    private void setToolbar() {
        setHasOptionsMenu(false);
        mToolbar.setTitle(R.string.inspect_title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_inspect, container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
        ButterKnife.unbind(this);
        mRxSubscriptionCollection.cancelAll();
    }

    @OnClick({R.id.inspect_plain_layout, R.id.inspect_danger_layout, R.id.inspect_fault_layout, R.id.inspect_record_layout, R.id.inspect_building_layout})
    public void onClieck(View view) {
        switch (view.getId()) {
            case R.id.inspect_plain_layout:
                Intent plainIntent = new Intent(mContext, PlainMainActivity.class);
                startActivity(plainIntent);
                break;
            case R.id.inspect_danger_layout:
                Intent dangerIntent = new Intent(mContext, DangerMainActivity.class);
                startActivity(dangerIntent);
                break;
            case R.id.inspect_fault_layout:
                Intent falutIntent = new Intent(mContext, NotDeviceMainActivity.class);
                startActivity(falutIntent);
                break;
            case R.id.inspect_record_layout:
                Intent recordIntent = new Intent(mContext, ExcuteRecordActivity.class);
                startActivity(recordIntent);
                break;
            case R.id.inspect_building_layout:
                Intent buildingIntent = new Intent(mContext, BuildingMainActivity.class);
                startActivity(buildingIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

}
