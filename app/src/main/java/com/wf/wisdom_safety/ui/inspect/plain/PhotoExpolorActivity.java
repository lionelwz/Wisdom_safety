package com.wf.wisdom_safety.ui.inspect.plain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.ADInfo;
import com.wf.wisdom_safety.ui.inspect.DaggerInspectComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class PhotoExpolorActivity extends Activity implements ImageViewTouchViewPager.OnPageSelectedListener{

    /** 图片显示模式 */
    public static final String EXTRA_SHOW_MODE = "extra_show_mode";

    @Bind(R.id.pager)
    ImageViewTouchViewPager mPager;
    @Bind(R.id.viewGroup)
    LinearLayout mViewGroup;

    @Inject
    Picasso mPicasso;

    private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
    private String[] imageUrls;
    private HashMap<String, String> mScrawlMap;
    private int mPosition;
    /**
     * 图片轮播指示器-个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 1;

    /**
     * 手机密度
     */
    private float mScale;

    public static void start(Context context, List<String> selectPath, int position) {
        String[] imageUrls = new String[]{};
        imageUrls = selectPath.toArray(imageUrls);
        Intent intent = new Intent(context, PhotoExpolorActivity.class);
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void start(Context context, List<String> selectPath, HashMap<String, String> scrawlUrls, int position) {
        String[] imageUrls = new String[]{};
        imageUrls = selectPath.toArray(imageUrls);
        Intent intent = new Intent(context, PhotoExpolorActivity.class);
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("position", position);
        intent.putExtra("imageScrawlUrls", scrawlUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_touch);
        ButterKnife.bind(this);
        DaggerInspectComponent.create().inject(this);
        infos.clear();
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        imageUrls = intent.getStringArrayExtra("imageUrls");
        mScrawlMap = (HashMap<String, String>) intent.getSerializableExtra("imageScrawlUrls");
        if (null != imageUrls) {
            for (int i = 0; i < imageUrls.length; i++) {
                ADInfo info = new ADInfo();
                info.setUrl(imageUrls[i]);
                info.setContent("top-->" + i);
                infos.add(info);
            }
            mPager.setAdapter(new ImageAdapter(infos));
            setImageResources(infos);
            mPager.setCurrentItem(mPosition);
            mPager.setOnPageSelectedListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewGroup.removeAllViews();
    }

    /**
     * 装填图片数据
     *
     * @param infoList
     */
    public void setImageResources(ArrayList<ADInfo> infoList) {
        // 清除所有子视图
        mViewGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = infoList.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(this);
//            int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，适应不同分辨率
//            int imagePadding = (int) (mScale * 5 + 0.5f);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setMargins(3, 0, 3, 0);
            mImageView.setLayoutParams(layout);
            //mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            mImageViews[i] = mImageView;
            if (i == mPosition) {
                mImageViews[i].setBackgroundResource(R.drawable.blue_circle);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.white_circle);
            }
            mViewGroup.addView(mImageViews[i]);
        }
    }

    @Override
    public void onPageSelected(int index) {
        // 设置图片滚动指示器背景
        mImageIndex = index;
        mImageViews[index].setBackgroundResource(R.drawable.blue_circle);
        for (int i = 0; i < mImageViews.length; i++) {
            if (index != i) {
                mImageViews[i].setBackgroundResource(R.drawable.white_circle);
            }
        }
    }

    private class ImageAdapter extends PagerAdapter {

        private List<ADInfo> list;

        private int total;

        public ImageAdapter(List<ADInfo> list) {
            this.list = list;
            total = list.size();
        }

        @Override
        public int getCount() {
            return total;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == (View) obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View parent = LayoutInflater.from(PhotoExpolorActivity.this).inflate(R.layout.pager_item, null);
            final ImageViewTouch mImage = (ImageViewTouch) parent.findViewById(R.id.image);
            mImage.setMaxScale(8.0f);
            mImage.setMinScale(1.0f);
            File imageFile = new File(list.get(position).getUrl());
            if (imageFile.exists()) {
                // 显示图片
                Glide.with(PhotoExpolorActivity.this)
                        .load(imageFile)
                        .centerCrop()
                        .fitCenter()
                        .placeholder(R.mipmap.default_error)
                        .crossFade()
                        .into(mImage);
            }else{
                if(null == mScrawlMap || null == mScrawlMap.get(list.get(position).getUrl())) {
                    Glide.with(PhotoExpolorActivity.this)
                            .load(list.get(position).getUrl())
                            .centerCrop()
                            .fitCenter()
                            .placeholder(R.mipmap.default_error)
                            .crossFade()
                            .into(mImage);
                } else {

                }
            }

            mImage.setTag(ImageViewTouchViewPager.VIEW_PAGER_OBJECT_TAG + position);

            mImage.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                @Override
                public void onSingleTapConfirmed() {
                    Log.d("##", "##onSingleTapConfirmed");
                    PhotoExpolorActivity.this.finish();
                }
            });
            ((ViewPager) container).addView(parent, 0);
            return parent;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}