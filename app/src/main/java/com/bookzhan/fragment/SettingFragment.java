package com.bookzhan.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseFragment;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.SpUtils;

/**
 * Created by zhandalin on 2016-04-03 13:50.
 * 说明:
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private final int MAIN_VIDEO_RESULT_CODE = 10;
    private final int LEFT_VIDEO_RESULT_CODE = 11;
    private final int BOTTOM_VIDEO_RESULT_CODE = 12;
    private final int RIGHT_VIDEO_RESULT_CODE = 13;
    private final int TOP_VIDEO_RESULT_CODE = 14;


    private TextView tv_main_video;
    private TextView tv_left_video;
    private TextView tv_bottom_video;
    private TextView tv_right_video;
    private TextView tv_top_video;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_main_video = (TextView) view.findViewById(R.id.tv_main_video);
        view.findViewById(R.id.ll_choose_main_video).setOnClickListener(this);
        tv_left_video = (TextView) view.findViewById(R.id.tv_left_video);
        view.findViewById(R.id.ll_choose_left_video).setOnClickListener(this);
        tv_bottom_video = (TextView) view.findViewById(R.id.tv_bottom_video);
        view.findViewById(R.id.ll_choose_bottom_video).setOnClickListener(this);
        tv_right_video = (TextView) view.findViewById(R.id.tv_right_video);
        view.findViewById(R.id.ll_choose_right_video).setOnClickListener(this);
        tv_top_video = (TextView) view.findViewById(R.id.tv_top_video);
        view.findViewById(R.id.ll_choose_top_video).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choose_main_video:
                openFileExplorer(MAIN_VIDEO_RESULT_CODE);
                break;
            case R.id.ll_choose_left_video:
                openFileExplorer(LEFT_VIDEO_RESULT_CODE);
                break;
            case R.id.ll_choose_bottom_video:
                openFileExplorer(BOTTOM_VIDEO_RESULT_CODE);
                break;
            case R.id.ll_choose_right_video:
                openFileExplorer(RIGHT_VIDEO_RESULT_CODE);
                break;
            case R.id.ll_choose_top_video:
                openFileExplorer(TOP_VIDEO_RESULT_CODE);
                break;
        }
    }

    public void openFileExplorer(int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setType("video/*.mp4");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String uri = data.getData().getPath();
            switch (requestCode) {
                case MAIN_VIDEO_RESULT_CODE:
                    tv_main_video.setText(uri);
                    tv_main_video.setVisibility(View.VISIBLE);
                    SpUtils.put(context, Constants.VideoPath.MAIN_VIDEO_KEY, uri);
                    break;
                case LEFT_VIDEO_RESULT_CODE:
                    tv_left_video.setText(uri);
                    tv_left_video.setVisibility(View.VISIBLE);
                    SpUtils.put(context, Constants.VideoPath.LEFT_VIDEO_KEY, uri);
                    break;
                case BOTTOM_VIDEO_RESULT_CODE:
                    tv_bottom_video.setText(uri);
                    tv_bottom_video.setVisibility(View.VISIBLE);
                    SpUtils.put(context, Constants.VideoPath.BOTTOM_VIDEO_KEY, uri);
                    break;
                case RIGHT_VIDEO_RESULT_CODE:
                    SpUtils.put(context, Constants.VideoPath.RIGHT_VIDEO_KEY, uri);
                    tv_right_video.setVisibility(View.VISIBLE);
                    tv_right_video.setText(uri);
                    break;
                case TOP_VIDEO_RESULT_CODE:
                    SpUtils.put(context, Constants.VideoPath.TOP_VIDEO_KEY, uri);
                    tv_top_video.setVisibility(View.VISIBLE);
                    tv_top_video.setText(uri);
                    break;
            }
        }

    }
}
