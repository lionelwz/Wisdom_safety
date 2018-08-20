package com.wf.wisdom_safety.ui.inspect.building;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.wf.util.DensityUtil;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.Building;
import com.wf.wisdom_safety.bean.inspect.Plain;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;
import com.wf.wisdom_safety.ui.inspect.plain.PlainMainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Lionel on 2017/7/24.
 */

public class BuildingMainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private BuildingMainAdapter mBuildingMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_list);
        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);
        mBuildingMainAdapter = new BuildingMainAdapter(this, new ArrayList<Building>());

        mRecyclerView.setAdapter(mBuildingMainAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = DensityUtil.dip2px(BuildingMainActivity.this, 5);
            }
        });
        getBuildings();
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
        mToolbar.setTitle(R.string.inspect_building_title);
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

    private void getBuildings() {
        final ProgressDialog pd = ProgressDialog.show(this, "", "正在查询，请稍候...", false, false);
        Subscription subscription = mInspectManager.getBuildingList().subscribe(new Observer<List<Building>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toast.makeText(BuildingMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<Building> buildings) {
                pd.dismiss();
                if(buildings.isEmpty()) {
                    Toast.makeText(BuildingMainActivity.this, R.string.nothing_data, Toast.LENGTH_SHORT).show();
                } else {
                    mBuildingMainAdapter.setBuildingList(buildings);
                }
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }

}
