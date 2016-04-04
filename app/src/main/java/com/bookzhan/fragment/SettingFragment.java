package com.bookzhan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookzhan.activity.SettingMenuNameActivity;
import com.bookzhan.activity.SettingVideoActivity;
import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseFragment;

/**
 * Created by zhandalin on 2016-04-03 13:50.
 * 说明:
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_setting_menu_name).setOnClickListener(this);
        view.findViewById(R.id.tv_setting_video_file).setOnClickListener(this);
        view.findViewById(R.id.tv_setting_picture).setOnClickListener(this);
        view.findViewById(R.id.tv_setting_music).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_setting_menu_name:
                startActivity(new Intent(context, SettingMenuNameActivity.class));
                break;
            case R.id.tv_setting_video_file:
                intent = new Intent(context, SettingVideoActivity.class);
                intent.putExtra("index", 0);
                startActivity(intent);
                break;
            case R.id.tv_setting_picture:
                intent = new Intent(context, SettingVideoActivity.class);
                intent.putExtra("index", 1);
                startActivity(intent);
                break;
            case R.id.tv_setting_music:

                break;
        }
    }
}
