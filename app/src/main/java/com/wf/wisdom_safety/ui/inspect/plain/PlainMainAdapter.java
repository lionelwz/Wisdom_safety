package com.wf.wisdom_safety.ui.inspect.plain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.Plain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看巡检计划主页数据适配器
 * Created by Lionel on 2017/7/22.
 */

public class PlainMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mPlainList;
    ListView.OnItemClickListener onItemClickListener;

    public PlainMainAdapter(Context context, List<Map<String, Object>> plainList) {
        mContext = context;
        mPlainList = plainList;
    }

    /**
     * 设置数据
     * @param plainList
     */
    public void setPlainList(List<Map<String, Object>> plainList) {
        mPlainList = plainList;
        notifyDataSetChanged();
    }

    public Map<String, Object> getPlain(int position) {return  mPlainList.get(position);}

    public PlainMainAdapter setOnItemClickListener(ListView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.plain_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContentViewHolder vh = (ContentViewHolder) holder;
        Map<String, Object> plain = mPlainList.get(position);
        String name = plain.get("name").toString();
        String buildingName = plain.get("buildingName").toString();
        Integer type = plain.get("type") == null ? 0 : (int)Float.parseFloat(plain.get("type").toString());
        Integer cycle = plain.get("cycle") == null ? 0 : (int)Float.parseFloat(plain.get("cycle").toString());
        String endTime = plain.get("end_time").toString();
        Integer status = plain.get("status") == null ? 0 : (int)Float.parseFloat(plain.get("status").toString());
        vh.mTaskName.setText(name);
        vh.mBuildingName.setText(buildingName);
        vh.mTaskType.setText(formatType(type));
        vh.mTaskCycle.setText(formatCycle(type, cycle));
        BigDecimal bd = new BigDecimal(endTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        vh.mEndTime.setText(sdf.format(new Date(Long.valueOf(bd.toPlainString()))));
        vh.mExcuteStatus.setText(formatStatus(status));
        vh.mItemView.setTag(position);
        vh.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener) {
                    int position = (Integer)v.getTag();
                    onItemClickListener.onItemClick(null, v, position, 0);
                }
            }
        });
    }

    private String formatStatus(Integer status) {
        switch (status) {
            case 0:
                return "待执行";
            case 1:
                return "已完成";
            case 2:
                return "未完成";

        }
        return "未知";
    }

    private String formatType(Integer type) {
        switch (type) {
            case 1:
                return "按时巡检";
            case 2:
                return "按天巡检";
            case 3:
                return "按周巡检";
            case 4:
                return "按月巡检";
        }
        return "未知";
    }

    private String formatCycle(Integer type, Integer cycle) {
        switch (type) {
            case 1:
                return String.format("每%d小时执行", cycle);
            case 2:
                return String.format("每%d天执行", cycle);
            case 3:
                return String.format("每%d周执行", cycle);
            case 4:
                return String.format("每%d月执行", cycle);
        }
        return "未知";
    }

    @Override
    public int getItemCount() {
        return mPlainList.isEmpty() ? 0 : mPlainList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.plain_task_name)
        TextView mTaskName;
        @Bind(R.id.plain_building_name)
        TextView mBuildingName;
        @Bind(R.id.plain_task_type)
        TextView mTaskType;
        @Bind(R.id.plain_task_cycle)
        TextView mTaskCycle;
        @Bind(R.id.plain_end_time)
        TextView mEndTime;
        @Bind(R.id.plain_excute_status)
        TextView mExcuteStatus;
        @Bind(R.id.item_view)
        LinearLayout mItemView;

        ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
