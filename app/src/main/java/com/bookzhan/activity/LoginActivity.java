package com.bookzhan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bookzhan.video.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.widget.CommonNavigation;


public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private EditText et_login_tel;
    private EditText et_login_pwd;
    private TextView tv_login_find_pwd;
    private Button bt_login;
    private TextView tv_login_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        CommonNavigation navigation = (CommonNavigation) findViewById(R.id.navigation);
        navigation.hideLeftLayout();

        et_login_tel = (EditText) findViewById(R.id.et_login_tel);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        tv_login_find_pwd = (TextView) findViewById(R.id.tv_login_find_pwd);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_login_register = (TextView) findViewById(R.id.tv_login_register);

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
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
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
