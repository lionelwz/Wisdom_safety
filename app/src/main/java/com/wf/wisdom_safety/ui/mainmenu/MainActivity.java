package com.wf.wisdom_safety.ui.mainmenu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.tencent.bugly.beta.Beta;
import com.wf.util.ChangeColorIconWithTextView;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.ui.homepage.HomepageFragment;
import com.wf.wisdom_safety.ui.inspect.InspectFragment;
import com.wf.wisdom_safety.ui.monitor.MonitorFragment;
import com.wf.wisdom_safety.ui.user.UserFragment;
import com.wf.wisdom_safety.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by Lionel on 2017/7/17.
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    CustomViewPager mViewpager;

    String mTabNames[] = new String[4];
    MainFragmentPagerAdapter mFragmentPagerAdapter;
    @Bind(R.id.id_indicator_one)
    ChangeColorIconWithTextView mIdIndicatorOne;
    @Bind(R.id.id_indicator_two)
    ChangeColorIconWithTextView mIdIndicatorTwo;
    @Bind(R.id.id_indicator_three)
    ChangeColorIconWithTextView mIdIndicatorThree;
    @Bind(R.id.id_indicator_four)
    ChangeColorIconWithTextView mIdIndicatorFour;

    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<>();
    long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTabIndicator.add(mIdIndicatorOne);
        mTabIndicator.add(mIdIndicatorTwo);
        mTabIndicator.add(mIdIndicatorThree);
        mTabIndicator.add(mIdIndicatorFour);

        mIdIndicatorOne.setIconAlpha(1.0f);
        mIdIndicatorOne.setChecked(true);

        mViewpager.setCurrentItem(0);

        mFragmentPagerAdapter = new MainFragmentPagerAdapter(this, getSupportFragmentManager());
        mViewpager.setAdapter(mFragmentPagerAdapter);
        // enable status bar tint
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(false);

        //检查更新
        Beta.checkUpgrade();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        Log.i("MainActivity", "onCreateOptionsMenu");
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    @OnClick({R.id.id_indicator_one, R.id.id_indicator_two, R.id.id_indicator_three, R.id.id_indicator_four})
    public void onClick(View view) {
        resetOtherTabs();

        switch (view.getId()) {
            case R.id.id_indicator_one:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewpager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewpager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewpager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewpager.setCurrentItem(3, false);
                break;
        }
    }

    @OnPageChange(value = R.id.viewpager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onPageSelected(int arg0) {
        mTabIndicator.get(0).setChecked(false);
        mTabIndicator.get(1).setChecked(false);
        mTabIndicator.get(2).setChecked(false);
        mTabIndicator.get(3).setChecked(false);
        mTabIndicator.get(arg0).setChecked(true);
    }

    @OnPageChange(value = R.id.viewpager, callback = OnPageChange.Callback.PAGE_SCROLLED)
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
       return;
    }

    @OnPageChange(value = R.id.viewpager, callback = OnPageChange.Callback.PAGE_SCROLL_STATE_CHANGED)
    public void onPageScrollStateChanged(int state) {

    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MainFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.list = new ArrayList<>();
            list.add(new MonitorFragment());
            list.add(new HomepageFragment());
            list.add(new InspectFragment());
            list.add(new UserFragment());

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTabNames[position];
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
