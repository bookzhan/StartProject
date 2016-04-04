package com.bookzhan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookzhan.activity.PlayerActivity;
import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseFragment;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;

/**
 * Created by zhandalin on 2016-04-03 13:50.
 * 说明:
 */
public class EducationFragment extends BaseFragment implements View.OnClickListener {

    private TextView tv_menu_1;
    private TextView tv_menu_2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.education_fragment_layout, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_menu_1 = (TextView) view.findViewById(R.id.tv_menu_1);
        tv_menu_2 = (TextView) view.findViewById(R.id.tv_menu_2);

        tv_menu_1.setOnClickListener(this);
        tv_menu_2.setOnClickListener(this);
        setMenuName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_menu_1:
                Intent intent = new Intent(context, PlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_menu_2:

                break;
        }
    }

    @Override
    protected void onMyResume() {
        super.onMyResume();
        setMenuName();
    }
    private void setMenuName(){
        String menu_name_1 = (String) SpUtils.get(context, Constants.Setting.MENU_NAME_1_KEY, "");
        if (!GHSStringUtil.isEmpty(menu_name_1)) {
            tv_menu_1.setText(menu_name_1);
        }else {
            tv_menu_1.setText("请设置名字");
        }
        String menu_name_2 = (String) SpUtils.get(context, Constants.Setting.MENU_NAME_2_KEY, "");
        if (!GHSStringUtil.isEmpty(menu_name_2)) {
            tv_menu_2.setText(menu_name_2);
        }else {
            tv_menu_2.setText("请设置名字");
        }
    }
}
