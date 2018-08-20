package com.wf.wisdom_safety.ui.inspect.notdevice;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wf.util.CalendarManager;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.NotDevice;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lionel on 2017/9/15.
 */

public class NotDeviceMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<NotDevice> mNotDeviceList;

    public NotDeviceMainAdapter(Context context, List<NotDevice> notDeviceList) {
        this.mContext = context;
        this.mNotDeviceList = notDeviceList;
    }

    public void setNotDeviceList(List<NotDevice> notDeviceList) {
        mNotDeviceList = notDeviceList;
        notifyDataSetChanged();
    }

    public NotDevice getNotDevice(int position) {return mNotDeviceList.get(position);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotDeviceMainAdapter.ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notdevice_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotDeviceMainAdapter.ContentViewHolder vh = (NotDeviceMainAdapter.ContentViewHolder) holder;
        NotDevice notDevice = mNotDeviceList.get(position);
        vh.mNotDeviceAddress.setText(notDevice.getAddress());
        vh.mNotDeviceDescription.setText(notDevice.getDescription());
        vh.mDeviceCommitTime.setText(CalendarManager.millisecondToString(notDevice.getCreateTime(),CalendarManager.DATE_FORMAT));
        if(notDevice.getStatus() == 0) {
            vh.mDeviceDealStatus.setTextColor(Color.parseColor("red"));
            vh.mDeviceDealStatus.setText("未处理");
        } else {
            vh.mDeviceDealStatus.setTextColor(Color.parseColor("green"));
            vh.mDeviceDealStatus.setText("已处理");
        }
        vh.mItemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mNotDeviceList.isEmpty() ? 0 : mNotDeviceList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.fault_address)
        TextView mNotDeviceAddress;
        @Bind(R.id.fault_description)
        TextView mNotDeviceDescription;
        @Bind(R.id.fault_commit_time)
        TextView mDeviceCommitTime;
        @Bind(R.id.fault_deal_status)
        TextView mDeviceDealStatus;
        @Bind(R.id.item_view)
        LinearLayout mItemView;

        ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
