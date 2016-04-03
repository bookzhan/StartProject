package com.zhanlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.devbrackets.android.exomedia.EMVideoView;
import com.devbrackets.android.exomedia.event.EMVideoViewClickedEvent;
import com.devbrackets.android.exomedia.util.EMEventBus;
import com.devbrackets.android.exomedia.widget.DefaultControls;


/**
 * Created by zhandalin on 2016-03-08 17:06.
 * 说明: 适合于自定义播放控制器
 */
public class MyVideoPlayer extends EMVideoView {
    public static final long DEFAULT_CONTROL_HIDE_DELAY = 3000;
    private EMEventBus bus;

    public MyVideoPlayer(Context context) {
        super(context);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setPlayController(DefaultControls defaultControls) {
        this.defaultControls = defaultControls;
        bus = new EMEventBus() {
            @Override
            public void post(Object event) {
            }
        };
        setBus(bus);

        defaultControls.setVideoView(this);
        defaultControls.setBus(bus);
        addView(defaultControls);
        startProgressPoll();

        TouchListener listener = new TouchListener(getContext());
        setOnTouchListener(listener);


    }


    private class TouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private GestureDetector gestureDetector;

        public TouchListener(Context context) {
            gestureDetector = new GestureDetector(context, this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (defaultControls != null) {
                defaultControls.show();

                if (isPlaying()) {
                    defaultControls.hideDelayed(DEFAULT_CONTROL_HIDE_DELAY);
                }
            }
            if (bus != null) {
                bus.post(new EMVideoViewClickedEvent());
            }

            return true;
        }
    }


}
