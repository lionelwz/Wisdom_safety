package com.wf.wisdom_safety.ui.inspect.plain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.wf.util.DensityUtil;
import com.wf.wisdom_safety.R;

import java.io.File;
import java.util.List;


/**
 * 现场图片adapter
 * Created by Lionel on 2017/7/26.
 */

public class InspectImageAdapter extends BaseItemDraggableAdapter<String> {

    private Context mContext;
    private Picasso mPicasso;
    private int size;
    private boolean isShowDeleteView = false;

    public InspectImageAdapter(Context context, List<String> imagePaths) {
        super(R.layout.scene_image_item, imagePaths);
        mContext = context;
        size = DensityUtil.dip2px(context, 100);
    }

    /**
     * 删除按钮是否在显示
     * @return
     */
    public boolean isShowDeleteView() {
        return isShowDeleteView;
    }

    /**
     * 显示删除按钮
     * @param showDeleteView
     */
    public void setShowDeleteView(boolean showDeleteView) {
        isShowDeleteView = showDeleteView;
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        super.remove(position);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, String s) {
        int position = baseViewHolder.getAdapterPosition() - getHeaderLayoutCount();
        setItemView(baseViewHolder, position);
    }

    /**
     * 设置item
     * @param baseViewHolder
     * @param position
     */
    private void setItemView(BaseViewHolder baseViewHolder, final int position) {
        final ImageView imageView = baseViewHolder.getView(R.id.selected_image);
        String s = mData.get(position);
        File imageFile = new File(mData.get(position));
        if (imageFile.exists()) {
            if(s.endsWith(".jpg") || s.endsWith(".png")) {
                // 显示图片
                Glide.with(mContext)
                        .load(imageFile)
                        .centerCrop()
                        .override(size, size)
                        .placeholder(R.mipmap.default_error)
                        .crossFade()
                        .into(imageView);
            }
        } else {
            if(s.endsWith(".jpg") || s.endsWith(".png")) {
                // 显示图片
                Glide.with(mContext)
                        .load(s)
                        .centerCrop()
                        .override(size, size)
                        .placeholder(R.mipmap.default_error)
                        .crossFade()
                        .into(imageView);
            }
        }
        baseViewHolder.setVisible(R.id.delete_image, isShowDeleteView);
        baseViewHolder.setOnClickListener(R.id.delete_image, new OnItemChildClickListener());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int positions) {
        super.onBindViewHolder(holder, positions);
    }

    @Override
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder) {
        super.onItemDragStart(viewHolder);
    }

    @Override
    public void onItemDragMoving(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        super.onItemDragMoving(source, target);
    }

    @Override
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder) {
        super.onItemDragEnd(viewHolder);
    }
}
