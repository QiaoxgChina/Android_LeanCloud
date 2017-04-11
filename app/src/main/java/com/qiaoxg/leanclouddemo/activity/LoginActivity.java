package com.qiaoxg.leanclouddemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.qiaoxg.leanclouddemo.R;
import com.qiaoxg.leanclouddemo.utils.UIHelper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText mPasswordView, mPhoneView;
    private View mRegisterView;
    private View mLoginFormView;

    private String mPsw, mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mPasswordView = (EditText) findViewById(R.id.pswEd);
        mPhoneView = (EditText) findViewById(R.id.phoneNumEd);
        mLoginFormView = findViewById(R.id.loginBtn);
        mLoginFormView.setOnClickListener(this);
        mRegisterView = findViewById(R.id.registerBtn);
        mRegisterView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mPsw = mPasswordView.getText().toString().trim();
        mPhone = mPhoneView.getText().toString().trim();
        if (TextUtils.isEmpty(mPsw) || TextUtils.isEmpty(mPhone)) {
            return;
        }
        switch (v.getId()) {
            case R.id.loginBtn:
                login(mPhone, mPsw);
                break;
            case R.id.registerBtn:
                registerUser(mPhone, mPsw);
                break;
        }
    }

    private void login(String username, String password) {
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    LoginActivity.this.finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Log.e(TAG, "done: login Error : " + e.toString());
                    UIHelper.showToast(LoginActivity.this, "登录失败");
                }
            }
        });
    }

    private void registerUser(final String username, final String password) {
        AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(password);// 设置密码
        user.setEmail(null);//设置邮箱
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                    login(username, password);
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    Log.e(TAG, "done: register Error : " + e.toString());
                    UIHelper.showToast(LoginActivity.this, "注册失败");
                }
            }
        });
    }
}


