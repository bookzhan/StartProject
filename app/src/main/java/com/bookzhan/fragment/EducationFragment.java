package com.bookzhan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bookzhan.activity.PlayerActivity;
import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseFragment;

/**
 * Created by zhandalin on 2016-04-03 13:50.
 * 说明:
 */
public class EducationFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout ll_choose_video_1;
    private LinearLayout ll_choose_video_2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.education_fragment_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ll_choose_video_1 = (LinearLayout) view.findViewById(R.id.ll_choose_video_1);
        ll_choose_video_2 = (LinearLayout) view.findViewById(R.id.ll_choose_video_2);

        ll_choose_video_1.setOnClickListener(this);
        ll_choose_video_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choose_video_1:
                Intent intent = new Intent(context, PlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_choose_video_2:

                break;
        }
    }
}
