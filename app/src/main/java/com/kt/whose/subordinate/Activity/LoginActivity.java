package com.kt.whose.subordinate.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kt.whose.subordinate.BaseApplication;
import com.kt.whose.subordinate.Broadcast.BroadcastTag;
import com.kt.whose.subordinate.HttpEntity.Devices;
import com.kt.whose.subordinate.HttpEntity.DevicesList;
import com.kt.whose.subordinate.HttpEntity.Login;
import com.kt.whose.subordinate.HttpEntity.Register;
import com.kt.whose.subordinate.HttpEntity.VerifyCode;
import com.kt.whose.subordinate.R;
import com.kt.whose.subordinate.Utils.Preferences;
import com.kt.whose.subordinate.Utils.Tool;
import com.rxjava.rxlife.RxLife;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.observers.BlockingBaseObserver;
import rxhttp.wrapper.param.RxHttp;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    EditText edit_account, edit_pwd, edit_reg_account, edit_reg_pwd, edit_reg_email, edit_reg_verify;
    TextView login_go_reg, forget_pwd, reg_go_login, reg_get_verify;
    LinearLayout login_btn, login_linear, reg_linear, reg_btn;
    Toolbar toolbar;
    Handler handler;

    @Override
    public int initLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {

        toolbar = findViewById(R.id.login_toolbar);
        toolbar.setOnClickListener(this);
        login_linear = findViewById(R.id.login_login_linear);
        reg_linear = findViewById(R.id.login_reg_linear);

        edit_account = findViewById(R.id.login_edit_account);
        edit_pwd = findViewById(R.id.login_edit_pwd);
        login_go_reg = findViewById(R.id.login_login_go_reg);
        login_go_reg.setOnClickListener(this);
        forget_pwd = findViewById(R.id.login_forget_pwd);
        forget_pwd.setOnClickListener(this);
        login_btn = findViewById(R.id.login_login);
        login_btn.setOnClickListener(this);


        edit_reg_account = findViewById(R.id.login_reg_edit_account);
        edit_reg_pwd = findViewById(R.id.login_reg_edit_pwd);
        edit_reg_email = findViewById(R.id.login_reg_edit_emial);
        edit_reg_verify = findViewById(R.id.login_reg_edit_verify);
        reg_btn = findViewById(R.id.login_reg);
        reg_btn.setOnClickListener(this);
        reg_go_login = findViewById(R.id.login_reg_go_login);
        reg_go_login.setOnClickListener(this);
        reg_get_verify = findViewById(R.id.reg_get_verify);
        reg_get_verify.setOnClickListener(this);


    }

    @Override
    protected void initEvent() {
        broadcastFilter();
        handler = new Handler();

    }


    @Override
    public void onClick(View view) {
        if (view == login_go_reg) {
            reg_linear.setVisibility(View.VISIBLE);
            login_linear.setVisibility(View.GONE);
        } else if (view == reg_go_login) {
            login_linear.setVisibility(View.VISIBLE);
            reg_linear.setVisibility(View.GONE);
        } else if (view == login_btn) {
            login();
        } else if (view == reg_btn) {
            reg();
        } else if (view == reg_get_verify) {
            verifyCode();
        } else if (view == toolbar) {
            finish();
        }


    }


    private void verifyCode() {
        String account = edit_reg_account.getText().toString();
        if (account.length() == 0) {
            PopTip.show("请输入账号").iconWarning();
            return;
        }
        String email = edit_reg_email.getText().toString();
        if (email.length() == 0) {
            PopTip.show("请输入邮箱").iconWarning();
            return;
        }
        WaitDialog.show("发送中...");
        RxHttp.postJson("/user/register/verifyCode")
                .connectTimeout(30000)
                .readTimeout(30000)
                .writeTimeout(30000)
                .add("user", account)
                .add("email", email)
                .toObservableResponse(VerifyCode.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    loadSucceed("验证码发送成功");
                    djs.start();
                }, e -> {
                    loadError(e.getMessage());
                });

    }

    private void reg() {
        String account = edit_reg_account.getText().toString();
        if (account.length() == 0) {
            PopTip.show("请输入账号").iconWarning();
            return;
        }
        String pwd = edit_reg_pwd.getText().toString();
        if (pwd.length() == 0) {
            PopTip.show("请输入密码").iconWarning();
            return;
        }
        String email = edit_reg_email.getText().toString();
        if (email.length() == 0) {
            PopTip.show("请输入邮箱").iconWarning();
            return;
        }

        String code = edit_reg_verify.getText().toString();
        if (code.length() == 0) {
            PopTip.show("请输入验证码").iconWarning();
            return;
        }
        WaitDialog.show("注册中...");
        RxHttp.postJson("/user/register")
                .add("user", account)
                .add("pwd", pwd)
                .add("email", email)
                .add("code", code)
                .toObservableResponse(Register.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    loadSucceed("注册成功");
                    broadcastUpdateLoginState(true);
                    String md5 = Tool.md5(account+"-"+pwd);
                    Preferences.setValue("userId",md5);
                    Preferences.setValue("account",account);
                    Preferences.setValue("account_pwd",pwd);
                    BaseApplication.setToken(s.getToken());
                }, e -> {
                    loadError(e.getMessage());
                });

    }


    private void login() {
        String account = edit_account.getText().toString();
        if (account.length() == 0) {
            PopTip.show("请输入账号").iconWarning();
            return;
        }
        String pwd = edit_pwd.getText().toString();
        if (pwd.length() == 0) {
            PopTip.show("请输入密码").iconWarning();
            return;
        }

        WaitDialog.show("登录中...");
        RxHttp.postJson("/user/login")
                .add("user", account)
                .add("pwd", pwd)
                .toObservableResponse(Login.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    loadSucceed("登录成功");
                    BaseApplication.setToken(s.getToken());
                    broadcastUpdateLoginState(true);
                    broadcastUpdateLoginData(s.getDevices());
                    String md5 = Tool.md5(account+"-"+pwd);
                    Preferences.setValue("userId",md5);
                    Preferences.setValue("account",account);
                    Preferences.setValue("account_pwd",pwd);
                }, throwable -> {
                    loadError(throwable.getMessage());
                });
    }


    private void loadSucceed(String s) {
        WaitDialog.dismiss();
        PopTip.show(s).iconSuccess();
    }

    private void loadError(String s) {
        WaitDialog.dismiss();
        PopTip.show(s != null ? s : "未知错误").iconError();
    }


    private CountDownTimer djs = new CountDownTimer(120000,1080) {
        @Override
        public void onTick(long l) {
            reg_get_verify.setEnabled(false);
            reg_get_verify.setText((l / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            reg_get_verify.setEnabled(true);
            reg_get_verify.setText(getResources().getString(R.string.reg_get_verify_code));
        }
    };


    /*
    * 登录成功后数据广播
    * */
    void broadcastUpdateLoginData(List<Devices> devices) {
        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_SUCCEED_DEVICES_LIST);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_SUCCEED_DEVICES_LIST, (Serializable) devices);
        localBroadcastManager.sendBroadcast(intent);
    }

    /*
    * 登录状态
    * */
    void broadcastUpdateLoginState(boolean is) {
        Intent intent = new Intent(BroadcastTag.ACTION_LOGIN_STATE);
        intent.putExtra(BroadcastTag.ACTION_LOGIN_STATE, is);
        localBroadcastManager.sendBroadcast(intent);
    }



}
