package com.wf.wisdom_safety.ui.inspect.record;

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
 * Created by Lionel on 2017/9/22.
 */

public class ExcuteRecordActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    private ExcuteRecordAdapter mExcuteRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);

        ButterKnife.bind(this);
        setToolbar();
        DaggerInspectComponent.create().inject(this);
        mExcuteRecordAdapter = new ExcuteRecordAdapter(this, new ArrayList<Map<String, Object>>());
        mRecyclerView.setAdapter(mExcuteRecordAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = DensityUtil.dip2px(ExcuteRecordActivity.this, 5);
            }
        });
        getRecords();
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
        mToolbar.setTitle(R.string.inspect_record_title);
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

    private void getRecords() {
        final ProgressDialog pd = ProgressDialog.show(this, "", "正在查询，请稍候...", false, false);
        Subscription subscription = mInspectManager.getRecordList().subscribe(new Observer<List<Map<String, Object>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                pd.dismiss();
                Toast.makeText(ExcuteRecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<Map<String, Object>> records) {
                pd.dismiss();
                if(records.isEmpty()) {
                    Toast.makeText(ExcuteRecordActivity.this, R.string.nothing_data, Toast.LENGTH_SHORT).show();
                } else {
                    mExcuteRecordAdapter.setExcuteRecordList(records);
                }
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }

}
