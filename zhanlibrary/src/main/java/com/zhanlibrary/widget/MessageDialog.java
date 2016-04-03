package com.zhanlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhanlibrary.R;


/**
 * Created by bookzhan on 2015/9/16.
 * 最后修改者: bookzhan  version 1.0
 * 说明:用于显示消息的dialog
 */
public class MessageDialog extends Dialog {
    private ProgressBar progressBar;
    private TextView tvMsg;

    public MessageDialog(Context context) {
        super(context, R.style.loading_dialog);
        init();
    }

    public MessageDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.msg_layout);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
    }

    /**
     * 默认是"加载中..."
     * @param message
     */
    public void setMessage(String message) {
        tvMsg.setText(message);
    }
}
