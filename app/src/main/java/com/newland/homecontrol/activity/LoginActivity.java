package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.homecontrol.R;
import com.newland.homecontrol.utils.SharedPreferencesUtils;
import com.nlecloud.nlecloudlibrary.api.net.HttpEngine;
import com.nlecloud.nlecloudlibrary.core.ActionCallbackListener;
import com.nlecloud.nlecloudlibrary.core.AppAction;
import com.nlecloud.nlecloudlibrary.core.AppActionImpl;
import com.nlecloud.nlecloudlibrary.domain.AccessToken;

/**
 * 登录
 */

public class LoginActivity extends Activity {


    private ImageView ivBack;
    private TextView tvTitle;
    private RelativeLayout rlTitle;
    private EditText edtUserName;//用户名
    private EditText edtPassword;//密码
    private Button btnLogin;//登录
    private CheckBox cbSaveMsg;//是否记住密码
    private AppAction mAppAction;
    private EditText edtIp;//ip
    private EditText edtProject;//项目标识

    private LinearLayout llLogin;//登录信息模块
    private LinearLayout llRight;//右半部分登陆信息设置所在的LinearLayout
    private boolean isSimulation = false;
    private boolean isSetAccount = false;//是否要设置账号，是为true

    SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化视图
        initView();
        //初始化SharePreferences存储
        initSharePreferences();
    }

    private void initView() {
        //获得Inent传递过来的数据"isSetAccount",默认为false，如果为true表示是要设置登陆信息
        isSetAccount = getIntent().getBooleanExtra("isSetAccount", false);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        llRight = (LinearLayout) findViewById(R.id.llRight);
        edtIp = (EditText) findViewById(R.id.edtIp);
        edtProject = (EditText) findViewById(R.id.edtProject);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        cbSaveMsg = (CheckBox) findViewById(R.id.cbSaveMsg);
        ivBack.setImageResource(R.mipmap.icon);//设置图标
        tvTitle.setText("远程智能家居控制系统");
        rlTitle.getBackground().setAlpha(180);//设置标题栏透明度
        llRight.getBackground().setAlpha(180);//设置右侧面板透明度
        mAppAction = new AppActionImpl(this);
    }

    /**
     * 判断是否记住登陆信息，如果记住再判断是否是其他页面跳过来要进行账号设置
     */
    private void initSharePreferences() {
        // TODO 1-1:判断是否记住登陆信息，如果记住了在判断是否是其他页面跳过来否要进行账号设置。
        //获得SharePrefences对象
        sharedPreferencesUtils = SharedPreferencesUtils.getInstant(this);
        if (sharedPreferencesUtils.getIsSaveMsg()) {
            if (isSetAccount) {
                //如果是设置账号密码并且记住了登录信息，设置checkbox选中状态，填充用户名密码等信息
                cbSaveMsg.setChecked(true);
                edtUserName.setText(sharedPreferencesUtils.getUsername());
                edtPassword.setText(sharedPreferencesUtils.getPassword());
                edtIp.setText(sharedPreferencesUtils.getIp());
                edtProject.setText(sharedPreferencesUtils.getProjectID());
            } else {
                //如果记住了登录信息但是不是要设置用户名密码的，在功能选择页面登陆
                startIntent(true);
            }
        }
    }

    /**
     * 进行页面跳转
     *
     * @param isGotoLogin 是否要在功能选择页面登陆
     */
    private void startIntent(boolean isGotoLogin) {
        // TODO 1-2:判断如果不是要设置用户名密码的，附加数据表明要在功能选择页面登陆，跳转到功能选择页面并结束当前Activity
        Intent intent = new Intent(LoginActivity.this, FunctionSelectActivity.class);
        if (isGotoLogin) {
            intent.putExtra("isGoToLogin", true);
        }
        startActivity(intent);
        finish();
    }


    /**
     * 判断是否为空 进行提示
     */
    private void judgment() {
        String UserName = edtUserName.getText().toString().trim();
        String Pwd = edtPassword.getText().toString().trim();
        String IP = edtIp.getText().toString().trim();
        String ProjectId = edtProject.getText().toString().trim();
        if (IP.isEmpty()) {
            Toast.makeText(this, "请填写云平台IP地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ProjectId.isEmpty()) {
            Toast.makeText(this, "请填写项目标识", Toast.LENGTH_SHORT).show();
            return;
        }
        if (UserName.isEmpty()) {
            Toast.makeText(this, "请填写您的用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Pwd.isEmpty()) {
            Toast.makeText(this, "请填写您的密码", Toast.LENGTH_SHORT).show();
            return;
        }
        login(UserName, Pwd, IP, ProjectId);
    }

    /**
     * 登陆云平台
     */
    private void login(String UserName, String PWD, String IP, String ProjectId) {
        try {
            HttpEngine.SERVER_URL = IP;
// 参数1：账号 ,参数2:密码,参数3:项目标示符
            mAppAction.login(UserName, PWD, ProjectId,
                    new ActionCallbackListener<AccessToken>() {
                        @Override
                        public void onSuccess(AccessToken data) {
                            // TODO Auto-generated method stub
                            // 登录成功设置服务器返回的Token，下次请求将带上Token
                            HttpEngine.ACCESSTOKEN = data.getAccessToken();
                            if (cbSaveMsg.isChecked()) {
                                sharedPreferencesUtils.setIsSaveMsg(true);
                                sharedPreferencesUtils.setIp(edtIp.getText().toString());
                                sharedPreferencesUtils.setProjectID(edtProject.getText().toString());
                                sharedPreferencesUtils.setPassword(edtPassword.getText().toString());
                                sharedPreferencesUtils.setUsername(edtUserName.getText().toString());
                                sharedPreferencesUtils.setPassword(edtPassword.getText().toString());
                                sharedPreferencesUtils.setIsSimulation(false);
                                if (!TextUtils.isEmpty(edtIp.getText().toString())) {
                                    sharedPreferencesUtils.setIp(edtIp.getText().toString());//存储服务器地址
                                }
                            } else {
                                sharedPreferencesUtils.setIsSaveMsg(false);
                            }
                            Toast.makeText(LoginActivity.this, "登录成功",
                                    Toast.LENGTH_SHORT).show();
                            startIntent(false);//进项跳转
                        }

                        @Override
                        public void onFailure(String errorEven, String message) {
                            // TODO Auto-generated method stub
                            Toast.makeText(LoginActivity.this, message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击事件
    public void MyClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                sharedPreferencesUtils.setIsSimulation(isSimulation);//设置是否是模拟模式，保存到SP中
                if (isSimulation) {
                    //模拟模式直接进行跳转
                    startIntent(false);
                } else {
                    //非模拟模式进行登录信息的判断
                    judgment();
                }
                break;
            case R.id.rbLogin://登录模式
                llLogin.setVisibility(View.VISIBLE);//显示
                isSimulation = false;
                break;
            case R.id.rbSimulation://模拟模式
                llLogin.setVisibility(View.INVISIBLE);//消失
                isSimulation = true;
                break;
        }
    }
}
