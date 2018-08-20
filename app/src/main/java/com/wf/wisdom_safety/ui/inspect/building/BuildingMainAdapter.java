package com.wf.wisdom_safety.ui.inspect.building;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.Building;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看建筑物主页数据适配器
 * Created by Lionel on 2017/7/24.
 */
public class BuildingMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Building> mBuildingList;
    ListView.OnItemClickListener onItemClickListener;

    public BuildingMainAdapter(Context context, List<Building> buildingList) {
        mContext = context;
        mBuildingList = buildingList;
    }

    public void setBuildingList(List<Building> buildingList) {
        mBuildingList = buildingList;
        notifyDataSetChanged();
    }

    public Building getBuilding(int position) {return mBuildingList.get(position);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BuildingMainAdapter.ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.building_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BuildingMainAdapter.ContentViewHolder vh = (BuildingMainAdapter.ContentViewHolder) holder;
        Building building = mBuildingList.get(position);
        vh.mBuildingName.setText(building.getName());
        vh.mBuildingFloor.setText(building.getCode());
        vh.mDeviceCount.setText(building.getOffdeviceCount() == null ? "0" : building.getOffdeviceCount().toString());
        vh.mItemView.setTag(position);
    }

    private String formatFloor(Integer minFloor, Integer maxFloor) {
        String floorStr = "";
        if (minFloor != null && minFloor < 0 ){
            floorStr += "地下"+Math.abs(minFloor)+"层，";
        }
        if(maxFloor != null && maxFloor > 0) {
            floorStr += "地上"+Math.abs(maxFloor)+"层";
        }
        return floorStr;
    }

    @Override
    public int getItemCount() {
        return mBuildingList.isEmpty() ? 0 : mBuildingList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.building_name)
        TextView mBuildingName;
        @Bind(R.id.building_floor)
        TextView mBuildingFloor;
        @Bind(R.id.building_device_count)
        TextView mDeviceCount;
        @Bind(R.id.item_view)
        LinearLayout mItemView;

        ContentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
