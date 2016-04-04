package com.bookzhan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bookzhan.model.VideoFileModel;
import com.bookzhan.video.R;
import com.google.gson.Gson;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.FileUtils;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;

import java.io.File;

/**
 * Created by zhandalin on 2016-04-04 22:23.
 * 说明:
 */
public class SettingVideoActivity extends BaseActivity implements View.OnClickListener {

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
    private int index;
    private VideoFileModel videoFileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_setting);
        index = getIntent().getIntExtra("index", 0);
        initView();
    }

    private void initView() {
        tv_main_video = (TextView) findViewById(R.id.tv_main_video);
        findViewById(R.id.ll_choose_main_video).setOnClickListener(this);
        tv_left_video = (TextView) findViewById(R.id.tv_left_video);
        findViewById(R.id.ll_choose_left_video).setOnClickListener(this);
        tv_bottom_video = (TextView) findViewById(R.id.tv_bottom_video);
        findViewById(R.id.ll_choose_bottom_video).setOnClickListener(this);
        tv_right_video = (TextView) findViewById(R.id.tv_right_video);
        findViewById(R.id.ll_choose_right_video).setOnClickListener(this);
        tv_top_video = (TextView) findViewById(R.id.tv_top_video);
        findViewById(R.id.ll_choose_top_video).setOnClickListener(this);

        File file = new File(context.getFilesDir() + "/video_" + index + ".json");
        if (file.exists()) {
            try {
                String readFile = FileUtils.readFile(file.getAbsolutePath());
                videoFileData = new Gson().fromJson(readFile, VideoFileModel.class);

                String main_video_path = videoFileData.getFrontVideoPath();
                String left_video_path = videoFileData.getLeftVideoPath();
                String bottom_video_path = videoFileData.getBottomVideoPath();
                String right_video_path = videoFileData.getRightVideoPath();
                String top_video_path = videoFileData.getTopVideoPath();
                if (!GHSStringUtil.isEmpty(main_video_path)) {
                    tv_main_video.setText(main_video_path);
                    tv_main_video.setVisibility(View.VISIBLE);
                }
                if (!GHSStringUtil.isEmpty(left_video_path)) {
                    tv_left_video.setText(left_video_path);
                    tv_left_video.setVisibility(View.VISIBLE);
                }
                if (!GHSStringUtil.isEmpty(bottom_video_path)) {
                    tv_bottom_video.setText(bottom_video_path);
                    tv_bottom_video.setVisibility(View.VISIBLE);
                }
                if (!GHSStringUtil.isEmpty(right_video_path)) {
                    tv_right_video.setText(right_video_path);
                    tv_right_video.setVisibility(View.VISIBLE);
                }
                if (!GHSStringUtil.isEmpty(top_video_path)) {
                    tv_top_video.setText(top_video_path);
                    tv_top_video.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                    videoFileData.setFrontVideoPath(uri);
                    break;
                case LEFT_VIDEO_RESULT_CODE:
                    tv_left_video.setText(uri);
                    tv_left_video.setVisibility(View.VISIBLE);
                    SpUtils.put(context, Constants.VideoPath.LEFT_VIDEO_KEY, uri);
                    videoFileData.setLeftVideoPath(uri);
                    break;
                case BOTTOM_VIDEO_RESULT_CODE:
                    tv_bottom_video.setText(uri);
                    tv_bottom_video.setVisibility(View.VISIBLE);
                    SpUtils.put(context, Constants.VideoPath.BOTTOM_VIDEO_KEY, uri);
                    videoFileData.setBottomVideoPath(uri);
                    break;
                case RIGHT_VIDEO_RESULT_CODE:
                    SpUtils.put(context, Constants.VideoPath.RIGHT_VIDEO_KEY, uri);
                    tv_right_video.setVisibility(View.VISIBLE);
                    tv_right_video.setText(uri);
                    videoFileData.setRightVideoPath(uri);
                    break;
                case TOP_VIDEO_RESULT_CODE:
                    SpUtils.put(context, Constants.VideoPath.TOP_VIDEO_KEY, uri);
                    tv_top_video.setVisibility(View.VISIBLE);
                    tv_top_video.setText(uri);
                    videoFileData.setTopVideoPath(uri);
                    break;
            }
        }

    }


    @Override
    protected void onDestroy() {
        try {
            String toJson = new Gson().toJson(videoFileData);
            FileUtils.writeFile(context, "video_" + index + ".json", toJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
