package com.zhanlibrary.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zhanlibrary.R;
import com.zhanlibrary.utils.DensityUtil;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;
import com.zhanlibrary.widget.LoadingProgressDialog;
import com.zhanlibrary.widget.MessageDialog;


public abstract class BaseActivity extends AppCompatActivity {
    protected boolean isDestroyed;
    protected Context context;
    protected LoadingProgressDialog loadingProgress;
    protected MessageDialog msgDialog;
    public View rootView;
    public View errorView;
    private View currentView;
    private Handler handler = new Handler();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        context = this;
        isDestroyed = false;
    }

    protected void initRootAndErrorView(int rootViewId, int errorViewId) {
        rootView = LayoutInflater.from(this).inflate(rootViewId, null);
        setContentView(rootView);
        errorView = LayoutInflater.from(this).inflate(errorViewId, null);
        errorView.findViewById(R.id.bt_to_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reTry();
            }
        });
    }

    protected void reTry() {
    }

    ;

    protected void initRootView(int rootViewId) {
        rootView = LayoutInflater.from(this).inflate(rootViewId, null);
        setContentView(rootView);
    }

    @Override
    public void setContentView(View view) {
        // if(currentView==view)return;
        if (view == null) return;//从onFalure 进来,view 可能为null
        if (currentView != null) {
            if (view == currentView) return;
            currentView = view;
        } else {
            currentView = view;
        }
        // currentView=view;
        super.setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //umeng统计开始
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hiddenDialogMsg();
        //umeng统计结束
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        if (loadingProgress != null && loadingProgress.isShowing()) {
            loadingProgress.dismiss();
        }
        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dismiss();
        }
        // GhsActivityManager.getScreenManager().popActivity();

        super.onDestroy();
    }

    /**
     * 很久才会弹一个Toast,不提升为成员变量
     *
     * @param message 传null则默认显示为 网络请求失败~
     */
    public void showToastAtCenter(String message) {
        if (isDestroyed) {
            return;
        }
        if (null == message) {
            message = "网络请求失败~";
        }
//        hiddenKeyboard();
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            View view = mToast.getView();
            view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
            TextView tv = (TextView) view.findViewById(android.R.id.message);
            tv.setTextColor(Color.WHITE);
            mToast.setMargin(0, 0);
        } else
            mToast.setText(message);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public void showToastAtBottom(String message) {
        if (isDestroyed) {
            return;
        }
        hiddenKeyboard();
        if (null == mToast) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            View view = mToast.getView();
            view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
            TextView tv = (TextView) view.findViewById(android.R.id.message);
            tv.setTextColor(Color.WHITE);
            mToast.setMargin(0, 0);
        } else
            mToast.setText(message);

        mToast.setGravity(Gravity.BOTTOM, 0, DensityUtil.dip2px(context, 70));
        mToast.show();
    }


    public void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 切换输入法,如果显示就隐藏,如果隐藏就显示
     */
    public void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示"加载中"对话框
     */
    public void showLoading() {
        showLoading(null, true, true);
    }

    /**
     * 显示加载中对话框
     *
     * @param messageID 默认为“加载中...”
     * @return
     */
    public void showLoading(int messageID) {
        showLoading(getString(messageID));
    }

    /**
     * 显示加载中对话框
     *
     * @param message 默认为“加载中...”
     * @return
     */
    public void showLoading(String message) {
        showLoading(message, true, true);
    }

    /**
     * 显示加载中对话框
     *
     * @param message              默认为“加载中...”
     * @param cancelable           设置进度条是否可以按退回键取消
     * @param canceledTouchOutside 设置点击进度对话框外的区域对话框不消失
     */
    public void showLoading(String message, boolean cancelable, boolean canceledTouchOutside) {
        //由于于要调用hiddenLoadingView,会把loadingProgress置空,所以是不影响的,而且这样也能保证只显示一个
        if (loadingProgress == null) {
            loadingProgress = new LoadingProgressDialog(this);
        }
        if (message != null) {
            loadingProgress.setMessage(message);
        }
        loadingProgress.setCancelable(cancelable);
        loadingProgress.setCanceledOnTouchOutside(canceledTouchOutside);
        loadingProgress.show();
    }


    /**
     * 隐藏加载框
     */
    public void hiddenLoadingView() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingProgress != null && loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                    loadingProgress = null;
                }
            }
        }, 800);
    }

    /**
     * 显示dialog风格的Toast
     *
     * @param message
     */
    public void showDialogMsg(String message) {
        msgDialog = new MessageDialog(this);
        if (message != null) {
            msgDialog.setMessage(message);
        }
        msgDialog.setCancelable(true);
        msgDialog.setCanceledOnTouchOutside(true);
        msgDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenDialogMsg();
            }
        }, 1500);
    }

    public void showDialogMsgAtBottom(String message) {
        msgDialog = new MessageDialog(this);
        if (message != null) {
            msgDialog.setMessage(message);
        }
        msgDialog.setCancelable(true);
        msgDialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = msgDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.y = DensityUtil.dip2px(context, 100);
        dialogWindow.setAttributes(attributes);
        msgDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenDialogMsg();
            }
        }, 1500);
    }


    /**
     * 隐藏dialog风格的Toast
     */
    public void hiddenDialogMsg() {
        if (null != msgDialog && msgDialog.isShowing()) {
            msgDialog.dismiss();
            msgDialog = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void startActivity(Intent intent) {

        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    public void startActivityNoAnimation(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public boolean isLogin() {
        return !GHSStringUtil.isEmpty((String) SpUtils.get(context, Constants.User.USER_ID_KEY, ""));
    }


}
