package com.zhanlibrary.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.event.EMMediaProgressEvent;
import com.devbrackets.android.exomedia.util.TimeFormatUtil;
import com.devbrackets.android.exomedia.widget.DefaultControls;
import com.zhanlibrary.R;


/**
 * Created by zhandalin on 2016-03-21 16:32.
 * 说明:仅仅只有进度条的视频播放控制器
 */
public class OnlyProgressPlayerController extends DefaultControls {
    private SeekBar seekBar;
    private boolean pausedForSeek = false;
    private boolean userInteracting = false;
    private boolean isFullScreen;
    private int originHeight;
    private Activity activity;
    private View rl_loading;
    private OnFullScreenListener onFullScreenListener;


    public OnlyProgressPlayerController(Context context) {
        this(context, null);
    }

    public OnlyProgressPlayerController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnlyProgressPlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        canViewHide=false;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.video_only_progress_controls;
    }

    /**
     * Sets the current video position, updating the seek bar
     * and the current time field
     *
     * @param position The position in milliseconds
     */
    @Override
    public void setPosition(long position) {
        currentTime.setText(TimeFormatUtil.formatMs(position));
        seekBar.setProgress((int) position);
    }

    /**
     * Sets the video duration in Milliseconds to display
     * at the end of the progress bar
     *
     * @param duration The duration of the video in milliseconds
     */
    @Override
    public void setDuration(long duration) {
        if (duration != seekBar.getMax()) {
            endTime.setText(TimeFormatUtil.formatMs(duration));
            seekBar.setMax((int) duration);
        }
    }

    /**
     * Performs the progress update on the current time field,
     * and the seek bar
     *
     * @param event The most recent progress
     */
    @Override
    public void setProgressEvent(EMMediaProgressEvent event) {
        if (!userInteracting) {
            seekBar.setSecondaryProgress((int) (seekBar.getMax() * event.getBufferPercentFloat()));
            seekBar.setProgress((int) event.getPosition());
            currentTime.setText(TimeFormatUtil.formatMs(event.getPosition()));
        }
    }

    /**
     * Retrieves the view references from the xml layout
     */
    @Override
    protected void retrieveViews() {

        currentTime = (TextView) findViewById(R.id.exomedia_controls_current_time);
        endTime = (TextView) findViewById(R.id.exomedia_controls_end_time);
        playPauseButton = (ImageButton) findViewById(R.id.exomedia_controls_play_pause_btn);
        previousButton = (ImageButton) findViewById(R.id.exomedia_controls_previous_btn);
        nextButton = (ImageButton) findViewById(R.id.exomedia_controls_next_btn);
        loadingProgress = (ProgressBar) findViewById(R.id.exomedia_controls_video_loading);
        controlsContainer = (ViewGroup) findViewById(R.id.exomedia_controls_interactive_container);
        seekBar = (SeekBar) findViewById(R.id.exomedia_controls_video_seek);
        rl_loading = findViewById(R.id.rl_loading);

        View ib_switch_screen = findViewById(R.id.ib_switch_screen);
        ib_switch_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    quitFullScreen();
                } else {
                    setFullScreen();
                }

            }
        });


    }

    /**
     * Registers any internal listeners to perform the playback controls,
     * such as play/pause, next, and previous
     */
    @Override
    protected void registerListeners() {
        super.registerListeners();
        seekBar.setOnSeekBarChangeListener(new SeekBarChanged());
    }

    /**
     * After the specified delay the view will be hidden.  If the user is interacting
     * with the controls then we wait until after they are done to start the delay.
     *
     * @param delay The delay in milliseconds to wait to start the hide animation
     */
    @Override
    public void hideDelayed(long delay) {
        hideDelay = delay;

        if (delay < 0 || !canViewHide) {
            return;
        }

        //If the user is interacting with controls we don't want to start the delayed hide yet
        if (userInteracting) {
            return;
        }

        visibilityHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateVisibility(false);
            }
        }, delay);
    }

    public void setLoading(boolean isLoading) {
        super.setLoading(isLoading);
        rl_loading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    private class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        private int seekToTime;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            seekToTime = progress;
            if (seekCallbacks != null && seekCallbacks.onSeekStarted()) {
                return;
            }

            if (currentTime != null) {
                currentTime.setText(TimeFormatUtil.formatMs(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            userInteracting = true;

            if (videoView.isPlaying()) {
                pausedForSeek = true;
                videoView.pause();
            }

            //Make sure to keep the controls visible during seek
            show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            userInteracting = false;
            if (seekCallbacks != null && seekCallbacks.onSeekEnded(seekToTime)) {
                return;
            }

            videoView.seekTo(seekToTime);

            if (pausedForSeek) {
                pausedForSeek = false;
                videoView.start();
                hideDelayed(hideDelay);
            }
        }
    }

    /**
     * 设置全屏
     **/
    private void setFullScreen() {
        isFullScreen = true;
        if (null != onFullScreenListener) onFullScreenListener.onFullScreen(true);
        if (0 == originHeight)
            originHeight = videoView.getHeight();
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(layoutParams);

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    }


    /**
     * 退出全屏
     **/
    private void quitFullScreen() {
        isFullScreen = false;
        if (null != onFullScreenListener) onFullScreenListener.onFullScreen(false);
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.height = originHeight;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(layoutParams);

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public interface OnFullScreenListener {
        void onFullScreen(boolean isFullScreen);
    }

    public void setOnFullScreenListener(OnFullScreenListener onFullScreenListener) {
        this.onFullScreenListener = onFullScreenListener;
    }


}
