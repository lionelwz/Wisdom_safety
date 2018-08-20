package com.wf.wisdom_safety.ui.inspect.record;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wf.util.CalendarManager;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.ExcuteRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lionel on 2017/9/22.
 */

public class ExcuteRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mExcuteRecordList;
    ListView.OnItemClickListener onItemClickListener;

    public ExcuteRecordAdapter(Context context, List<Map<String, Object>> excuteRecordList) {
        mContext = context;
        mExcuteRecordList = excuteRecordList;
    }

    public void setExcuteRecordList(List<Map<String, Object>> excuteRecordList) {
        mExcuteRecordList = excuteRecordList;
        notifyDataSetChanged();
    }

    public Map<String, Object> getExcuteRecord(int position) {return mExcuteRecordList.get(position);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExcuteRecordAdapter.ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.record_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExcuteRecordAdapter.ContentViewHolder vh = (ExcuteRecordAdapter.ContentViewHolder) holder;
        Map<String, Object> excuteRecord = mExcuteRecordList.get(position);
        BigDecimal bd1 = new BigDecimal(excuteRecord.get("create_time").toString());
        vh.mExcuteRecordTime.setText(CalendarManager.millisecondToString(Long.valueOf(bd1.toPlainString()),CalendarManager.DATE_FORMAT));
        vh.mExcuteRecordPlain.setText(excuteRecord.get("plainName").toString());
        vh.mNormalCount.setText(String.valueOf((int)Float.parseFloat(excuteRecord.get("normalCount").toString())));
        vh.mFaultCount.setText(String.valueOf((int)Float.parseFloat(excuteRecord.get("faultCount").toString())));
        vh.mBrokenCount.setText(String.valueOf((int)Float.parseFloat(excuteRecord.get("brokenCount").toString())));

        vh.mItemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mExcuteRecordList.isEmpty() ? 0 : mExcuteRecordList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.record_excute_time)
        TextView mExcuteRecordTime;
        @Bind(R.id.record_excute_plain)
        TextView mExcuteRecordPlain;
        @Bind(R.id.normal_device_count)
        TextView mNormalCount;
        @Bind(R.id.fault_device_count)
        TextView mFaultCount;
        @Bind(R.id.broken_device_count)
        TextView mBrokenCount;
        @Bind(R.id.nocheck_device_count)
        TextView mNocheckCount;
        @Bind(R.id.item_view)
        LinearLayout mItemView;

        ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
