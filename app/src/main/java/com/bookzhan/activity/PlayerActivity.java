package com.bookzhan.activity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bookzhan.video.R;
import com.devbrackets.android.exomedia.event.EMMediaProgressEvent;
import com.devbrackets.android.exomedia.listener.EMProgressCallback;
import com.devbrackets.android.exomedia.listener.ExoPlayerListener;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.GHSLogUtil;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;
import com.zhanlibrary.widget.MultiVideoPlayerController;
import com.zhanlibrary.widget.MyVideoPlayer;

public class PlayerActivity extends BaseActivity implements MediaPlayer.OnPreparedListener {

    private MyVideoPlayer player;
    private String main_video_path;
    private String left_video_path;
    private String bottom_video_path;
    private String right_video_path;
    private String top_video_path;
    private int time;
    private static final String TAG = "PlayerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        initVideoPath();
        setupPlayer();
    }

    private void initVideoPath() {
        main_video_path = (String) SpUtils.get(context, Constants.VideoPath.MAIN_VIDEO_KEY, "");
        left_video_path = (String) SpUtils.get(context, Constants.VideoPath.LEFT_VIDEO_KEY, "");
        bottom_video_path = (String) SpUtils.get(context, Constants.VideoPath.BOTTOM_VIDEO_KEY, "");
        right_video_path = (String) SpUtils.get(context, Constants.VideoPath.RIGHT_VIDEO_KEY, "");
        top_video_path = (String) SpUtils.get(context, Constants.VideoPath.TOP_VIDEO_KEY, "");
    }

    private void setupPlayer() {
        player = (MyVideoPlayer) findViewById(R.id.player);

        MultiVideoPlayerController playerController = new MultiVideoPlayerController(context);
        if (GHSStringUtil.isEmpty(left_video_path)) {
            playerController.hideSubControl(0);
        }
        if (GHSStringUtil.isEmpty(bottom_video_path)) {
            playerController.hideSubControl(1);
        }
        if (GHSStringUtil.isEmpty(right_video_path)) {
            playerController.hideSubControl(2);
        }
        if (GHSStringUtil.isEmpty(top_video_path)) {
            playerController.hideSubControl(3);
        }
        playerController.setOnswitchVideoListener(new MultiVideoPlayerController.OnSwitchVideoListener() {
            @Override
            public void onSwitchVideo(int flag) {
                switch (flag) {
                    case 0://左视频
                        startNewPlay(left_video_path);
                        break;
                    case 1://下视频
                        startNewPlay(bottom_video_path);
                        break;
                    case 2://右视频
                        startNewPlay(right_video_path);
                        break;
                    case 3://上视频
                        startNewPlay(top_video_path);
                        break;
                }
            }
        });
        player.setProgressCallback(new EMProgressCallback() {
            @Override
            public boolean onProgressUpdated(EMMediaProgressEvent progressEvent) {
                int position = (int) progressEvent.getPosition();
                if (0 != position)
                    time = position;
                return false;
            }
        });
        player.setPlayController(playerController);
        player.setOnPreparedListener(this);
        player.addExoPlayerListener(new ExoPlayerListener() {
            @Override
            public void onStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) player.seekTo(time);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onVideoSizeChanged(int width, int height, int unAppliedRotationDegrees, float pixelWidthHeightRatio) {

            }
        });
        player.setVideoURI(Uri.parse(main_video_path));
    }

    private void startNewPlay(String uri) {
        player.setVideoURI(Uri.parse(uri));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        GHSLogUtil.d(TAG, "onPrepared---time=" + time);
        player.start();
    }
}
