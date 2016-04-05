package com.bookzhan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bookzhan.fragment.EducationFragment;
import com.bookzhan.fragment.SettingFragment;
import com.zhanlibrary.base.BaseFragment;

/**
 * Created by zhandalin on 2016-02-27 12:02.
 * 说明:
 */
public class HomePageAdapter extends FragmentPagerAdapter {
    private String[] title = {"教学", "设置"};

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {//首页的页面会全部缓存,不需要处理其他的
            case 0:
                fragment = new EducationFragment();
                break;
            case 1:
                fragment = new SettingFragment();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
