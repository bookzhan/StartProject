package com.bookzhan.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;

/**
 * Created by zhandalin on 2016-04-04 22:43.
 * 说明:
 */
public class SettingMenuNameActivity extends BaseActivity {
    private EditText et_menu_name_1;
    private EditText et_menu_name_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_menu_name);
        initView();
    }

    private void initView() {
        et_menu_name_1 = (EditText) findViewById(R.id.et_menu_name_1);
        et_menu_name_2 = (EditText) findViewById(R.id.et_menu_name_2);

        String menu_name_1 = (String) SpUtils.get(context, Constants.Setting.MENU_NAME_1_KEY, "");
        if (!GHSStringUtil.isEmpty(menu_name_1)) {
            et_menu_name_1.setText(menu_name_1);
            et_menu_name_1.setSelection(menu_name_1.length());
        }
        String menu_name_2 = (String) SpUtils.get(context, Constants.Setting.MENU_NAME_2_KEY, "");
        if (!GHSStringUtil.isEmpty(menu_name_2)) {
            et_menu_name_2.setText(menu_name_2);
            et_menu_name_2.setSelection(menu_name_2.length());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        String menu_name_1 = et_menu_name_1.getText().toString().trim();
        if (!GHSStringUtil.isEmpty(menu_name_1)) {
            SpUtils.put(context, Constants.Setting.MENU_NAME_1_KEY, menu_name_1);
        }

        String menu_name_2 = et_menu_name_2.getText().toString().trim();
        if (!GHSStringUtil.isEmpty(menu_name_2)) {
            SpUtils.put(context, Constants.Setting.MENU_NAME_2_KEY, menu_name_2);
        }
    }

}
