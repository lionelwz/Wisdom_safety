package com.wf.wisdom_safety.ui.inspect.danger;

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
import com.wf.wisdom_safety.bean.inspect.Danger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lionel on 2017/9/14.
 */

public class DangerMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Danger> mDangerList;
    ListView.OnItemClickListener onItemClickListener;

    public DangerMainAdapter(Context context, List<Danger> DangerList) {
        mContext = context;
        mDangerList = DangerList;
    }

    public void setDangerList(List<Danger> DangerList) {
        mDangerList = DangerList;
        notifyDataSetChanged();
    }

    public Danger getDanger(int position) {return mDangerList.get(position);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DangerMainAdapter.ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.danger_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DangerMainAdapter.ContentViewHolder vh = (DangerMainAdapter.ContentViewHolder) holder;
        Danger danger = mDangerList.get(position);
        vh.mDangerAddress.setText(danger.getAddress());
        vh.mDangerDescription.setText(danger.getDescription());
        vh.mDeviceCommitTime.setText(CalendarManager.millisecondToString(danger.getCreateTime(),CalendarManager.DATE_FORMAT));
        if(danger.getStatus() == 0) {
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
        return mDangerList.isEmpty() ? 0 : mDangerList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.danger_address)
        TextView mDangerAddress;
        @Bind(R.id.danger_description)
        TextView mDangerDescription;
        @Bind(R.id.danger_commit_time)
        TextView mDeviceCommitTime;
        @Bind(R.id.danger_deal_status)
        TextView mDeviceDealStatus;
        @Bind(R.id.item_view)
        LinearLayout mItemView;

        ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    
}
