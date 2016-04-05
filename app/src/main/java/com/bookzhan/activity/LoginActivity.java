package com.bookzhan.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.GHSLogUtil;
import com.zhanlibrary.utils.SpUtils;


public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText et_login_tel;
    private EditText et_login_pwd;
    private Button bt_login;
    private String TAG = "LoginActivity";
    private CheckBox check_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean hasLogin= (boolean) SpUtils.get(context, Constants.User.USER_LOGIN_FLAG_KEY, false);
        if(hasLogin){
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }

    private void initView() {
//        CommonNavigation navigation = (CommonNavigation) findViewById(R.id.navigation);
//        navigation.hideLeftLayout();

        et_login_tel = (EditText) findViewById(R.id.et_login_tel);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        boolean hasLogin= (boolean) SpUtils.get(context, Constants.User.USER_LOGIN_FLAG_KEY, false);
        if(hasLogin){
            String user_name_key = (String) SpUtils.get(context, Constants.User.USER_NAME_KEY, "");
            String user_pwd_key = (String) SpUtils.get(context, Constants.User.USER_PWD_KEY, "");
            et_login_tel.setText(user_name_key);
            et_login_tel.setSelection(user_name_key.length());
            et_login_pwd.setText(user_pwd_key);
            et_login_pwd.setSelection(user_pwd_key.length());
        }
        bt_login = (Button) findViewById(R.id.bt_login);
        TextView tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tv_forget_pwd.setOnClickListener(this);
        tv_forget_pwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tv_forget_pwd.getPaint().setAntiAlias(true);//抗锯齿

        findViewById(R.id.ll_check_box).setOnClickListener(this);
        check_box = (CheckBox) findViewById(R.id.check_box);

        bt_login.setOnClickListener(this);
        et_login_tel.addTextChangedListener(this);
        et_login_pwd.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                submit();
                break;
            case R.id.tv_forget_pwd:
                GHSLogUtil.d(TAG, "忘记密码");
                break;
            case R.id.ll_check_box:
                check_box.setChecked(!check_box.isChecked());
                break;

        }
    }

    private void submit() {
        String tel = et_login_tel.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            showToastAtCenter("请输入用户名");
            return;
        }

        String pwd = et_login_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            showToastAtCenter("请输入密码");
            return;
        }
        if ("admin".equals(tel) && "admin".equals(pwd)) {
            if(check_box.isChecked()){
                SpUtils.put(context, Constants.User.USER_NAME_KEY, tel);
                SpUtils.put(context, Constants.User.USER_PWD_KEY, pwd);
                SpUtils.put(context, Constants.User.USER_LOGIN_FLAG_KEY, true);
            }
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToastAtCenter("账户或密码不正确");
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean enable = et_login_tel.getText().toString().trim().length() > 0 && et_login_pwd.getText().toString().trim().length() > 0;
        bt_login.setEnabled(enable);
    }
}
