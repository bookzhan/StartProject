package com.zhanlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.zhanlibrary.R;


/**
 * Created by zhandalin on 2015-11-26 09:31.
 * 说明:
 */
public class MyDialog extends Dialog {

    private final Context context;
    private View contentView;
    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private LinearLayout rootView;
    private boolean mCancelable = true;

    /**
     * @param context
     * @param contentView 需要填充的View
     * @param gravity     内容View的布局位置
     */
    public MyDialog(Context context, View contentView, int gravity) {
        super(context, R.style.ad_dialog);
        this.context = context;
        this.contentView = contentView;
        initView(gravity);

    }
    private void initView(int gravity) {
        getWindow().setWindowAnimations(R.style.my_dialog_animstyle);
        contentView.setClickable(true);

        int w = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        int h = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        contentView.measure(w, h);
        if (Gravity.RIGHT == gravity) {
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            showAnimation = new TranslateAnimation(contentView.getMeasuredWidth(), 0, 0, 0);
            showAnimation.setDuration(200);
            hideAnimation = new TranslateAnimation(0, contentView.getMeasuredWidth(), 0, 0);
            hideAnimation.setDuration(200);
        } else if (Gravity.BOTTOM == gravity) {
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            showAnimation = new TranslateAnimation(0, 0, contentView.getMeasuredHeight(), 0);
            showAnimation.setDuration(200);
            hideAnimation = new TranslateAnimation(0, 0, 0, contentView.getMeasuredHeight());
            hideAnimation.setDuration(200);
        } else if (Gravity.LEFT == gravity) {
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            showAnimation = new TranslateAnimation(-contentView.getMeasuredWidth(), 0, 0, 0);
            showAnimation.setDuration(200);
            hideAnimation = new TranslateAnimation(0, -contentView.getMeasuredWidth(), 0, 0);
            hideAnimation.setDuration(200);
        } else if (Gravity.TOP == gravity) {
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            showAnimation = new TranslateAnimation(0, 0, -contentView.getMeasuredHeight(), 0);
            showAnimation.setDuration(2000);
            hideAnimation = new TranslateAnimation(0, 0, 0, -contentView.getMeasuredHeight());
            hideAnimation.setDuration(2000);
        }
        showAnimation.setFillAfter(true);
        hideAnimation.setFillAfter(true);
        rootView = new LinearLayout(context);
        rootView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setGravity(gravity);
        rootView.addView(contentView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing() && mCancelable) {
                    dismiss();
                }
            }
        });
        setContentView(rootView);
        getWindow().setLayout(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void show() {
        super.show();
        rootView.startAnimation(showAnimation);
    }

    @Override
    public void dismiss() {
        rootView.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShowing())
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyDialog.super.dismiss();
                        }
                    }, 50);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        this.mCancelable = cancel;
        super.setCanceledOnTouchOutside(cancel);
    }
}
