package com.zhanlibrary.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhanlibrary.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.utils.GHSStringUtil;


/**
 * Created by zhandalin on 2015/8/9.
 * 说明:统一的导航栏, 该类支持自定义左,中,右的布局,当需要添加分享按钮什么的自行替换
 * 需要修改这个类的时候,需告知,这个类整个工程都用的,建议只由一个人来维护
 */
public class CommonNavigation extends LinearLayout {
    private Context context = null;
    private TextView bar_title;
    private TextView bar_right_text;
    private TextView bar_left_text;
    private String jumpClassName;
    private LinearLayout leftLayoutView, titleLayoutView, rightLayoutView;

    private OnRightLayoutClick onRightLayoutClick;
    private OnLeftLayoutClick onLeftLayoutClick;
    private OnTitleLayoutClick onTitleLayoutClick;
    private ImageView iv_navigation_bg;

    public CommonNavigation(Context context) {
        this(context, null);
    }

    public CommonNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        initAttribute(attrs);
        initListener();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.navigation_layout, null);
        iv_navigation_bg = (ImageView) view.findViewById(R.id.iv_navigation_bg);

        leftLayoutView = (LinearLayout) view.findViewById(R.id.leftLayoutView);
        bar_left_text = ((TextView) view.findViewById(R.id.bar_left_text));

        titleLayoutView = (LinearLayout) view.findViewById(R.id.titleLayoutView);
        bar_title = ((TextView) view.findViewById(R.id.bar_title));

        rightLayoutView = (LinearLayout) view.findViewById(R.id.rightLayoutView);
        bar_right_text = ((TextView) view.findViewById(R.id.bar_right_text));

        addView(view);
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.navigation);
        String rightText = typedArray.getString(R.styleable.navigation_rightText);
        if (!GHSStringUtil.isEmpty(rightText)) {
            bar_right_text.setText(rightText);
        }
        String title = typedArray.getString(R.styleable.navigation_titleText);
        if (!GHSStringUtil.isEmpty(title)) {
            bar_title.setText(title);
        }
        String leftText = typedArray.getString(R.styleable.navigation_leftText);
        if (!GHSStringUtil.isEmpty(leftText)) {
            bar_left_text.setVisibility(VISIBLE);
            bar_left_text.setText(leftText);
        }

        jumpClassName = typedArray.getString(R.styleable.navigation_jumpClass);
        typedArray.recycle();
    }


    public void setTitle(String str) {
        bar_title.setVisibility(VISIBLE);
        bar_title.setText(str);
    }

    public void setRightText(String rightStr) {
        bar_right_text.setText(rightStr);
        bar_right_text.setVisibility(View.VISIBLE);
    }

    public String getRightText() {
        return bar_right_text.getText().toString();
    }

    public void setLeftText(String rightStr) {
        bar_left_text.setText(rightStr);
        bar_left_text.setVisibility(View.VISIBLE);
    }

    public String getLeftText() {
        return bar_left_text.getText().toString();
    }

    public void showLeftLayout() {
        leftLayoutView.setVisibility(VISIBLE);
    }

    public void hideLeftLayout() {
        leftLayoutView.setVisibility(GONE);
    }


    public void showTitleLayout() {
        titleLayoutView.setVisibility(VISIBLE);
    }

    public void hideTitleLayout() {
        titleLayoutView.setVisibility(GONE);
    }


    public void showRightLayout() {
        rightLayoutView.setVisibility(VISIBLE);
    }

    public void hideRightLayout() {
        rightLayoutView.setVisibility(GONE);
    }

    private void initListener() {
        leftLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLeftLayoutClick != null) {
                    onLeftLayoutClick.onClick(v);
                    return;
                }
                if (!GHSStringUtil.isEmpty(jumpClassName)) {
                    Intent intent = new Intent();
                    intent.setClassName(context.getPackageName(), "net.ghs.activity." + jumpClassName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                ((BaseActivity) context).finish();
                ((BaseActivity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        });
        rightLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightLayoutClick != null) {
                    onRightLayoutClick.onClick(v);
                }
            }
        });

        titleLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onTitleLayoutClick) {
                    onTitleLayoutClick.onClick(v);
                }
            }
        });

    }

    /**
     * 设置导航栏的背景透明度
     *
     * @param alpha 取值0到1
     */
    public void setNavigationBackgroundAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        iv_navigation_bg.setAlpha(alpha);
    }


    /**
     * @param view 要替换的左边View
     */
    public void setLeftLayoutView(View view) {
        leftLayoutView.removeAllViews();
        leftLayoutView.addView(view);
    }

    /**
     * @param view 要替换的标题View
     */
    public void setTitleLayoutView(View view) {
        titleLayoutView.removeAllViews();
        titleLayoutView.addView(view);
    }

    /**
     * @param view 要替换的右边布局
     */
    public void setRightLayoutView(View view) {
        rightLayoutView.removeAllViews();
        rightLayoutView.addView(view);
    }

    public interface OnLeftLayoutClick {
        void onClick(View v);
    }

    public void setOnLeftLayoutClickListener(OnLeftLayoutClick onLeftLayoutClick) {
        this.onLeftLayoutClick = onLeftLayoutClick;
    }


    public interface OnTitleLayoutClick {
        void onClick(View v);
    }

    public void setOnTitleLayoutClickListener(OnTitleLayoutClick onTitleLayoutClick) {
        this.onTitleLayoutClick = onTitleLayoutClick;
    }

    public interface OnRightLayoutClick {
        void onClick(View v);
    }

    public void setOnRightLayoutClickListener(OnRightLayoutClick onRightLayoutClick) {
        this.onRightLayoutClick = onRightLayoutClick;
    }


}
