package com.bookzhan.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.widget.MyPlayerController;
import com.zhanlibrary.widget.MyVideoPlayer;

public class PlayerActivity extends BaseActivity {

    private MyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        player = (MyVideoPlayer) findViewById(R.id.player);
        setupPlayer();
    }

    private void setupPlayer() {
        MyPlayerController playerController = new MyPlayerController(context);
        player.setPlayController(playerController);
        String url = Environment.getExternalStorageDirectory().toString() + "/Download/test_1.mp4";
        player.setVideoURI(Uri.parse(url));
        player.start();
    }
}
