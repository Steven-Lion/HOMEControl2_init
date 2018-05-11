package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.homecontrol.bean.Global;
import com.newland.homecontrol.R;
import com.newland.homecontrol.utils.SharedPreferencesUtils;
import com.nlecloud.nlecloudlibrary.api.ApiResponse;
import com.nlecloud.nlecloudlibrary.core.ActionCallbackListener;
import com.nlecloud.nlecloudlibrary.core.AppAction;
import com.nlecloud.nlecloudlibrary.core.AppActionImpl;


/**
 * 客厅
 */

public class LivingRoomActivity extends Activity {

    private TextView tvTitle;//标题

    private LinearLayout llbottomBackGround;//下部分背景 需要设置透明度

    private RelativeLayout rlBackGround;//背景图片

    private CheckBox cbLight;//灯 开关

    private SharedPreferencesUtils sharedPreferencesUtils;

    private AppAction mAppAction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livingroom);
        //初始化视图
        initView();
        //初始化SharePreferences存储
        initSharedPreferences();
        // 按钮监听
        initCheck();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        llbottomBackGround = (LinearLayout) findViewById(R.id.llbottomBackGround);
        rlBackGround = (RelativeLayout) findViewById(R.id.rlBackGround);
        cbLight = (CheckBox) findViewById(R.id.cbLight);
        llbottomBackGround.getBackground().setAlpha(220);
        tvTitle.setText("客厅灯控");

    }

    /**
     * 初始化SharePreferences存储
     */
    private void initSharedPreferences(){
        sharedPreferencesUtils = SharedPreferencesUtils.getInstant(this);
        mAppAction = new AppActionImpl(this);

        setAnimation(sharedPreferencesUtils.getLivingroom());//进项开关灯
        check(sharedPreferencesUtils.getLivingroom());//设置开光状态
        if (sharedPreferencesUtils.getISAutomatic()) {//是自动模式 隐藏下方按钮
            llbottomBackGround.setVisibility(View.INVISIBLE);
        } else {
            llbottomBackGround.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 设置按钮监听
     */
    private void initCheck() {
        cbLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {

                if (sharedPreferencesUtils.getIsSimulation()) {
                    setAnimation(check);
                } else {
                    operateLight(check);
                }
            }
        });
    }

    /**
     * 实现开关灯
     */
    private void setAnimation(boolean check) {
        sharedPreferencesUtils.setLivingroom(check);//存取开关灯状态
        if (check) {
            rlBackGround.setBackgroundResource(R.mipmap.pic_live);
        } else {
            rlBackGround.setBackgroundResource(R.mipmap.pic_live_close);
        }
    }
    /**
     * 开关按钮状态
     */
    private void check(boolean check) {
        cbLight.setChecked(check);
    }

    /**
     * 通知设备开关
     *
     * @param isChecked 开关
     */
    private void operateLight(final boolean isChecked) {

        //调取网络开关窗帘 方法
        mAppAction.onOff(Global.LIGHT_LIVING, isChecked,
                new ActionCallbackListener<ApiResponse<String>>() {

                    @Override
                    public void onSuccess(ApiResponse<String> data) {
                        setAnimation(isChecked);//实现开关灯图片切换
                    }

                    @Override
                    public void onFailure(String errorEven, String message) {
                        Toast.makeText(LivingRoomActivity.this, message,
                                Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    protected void onDestroy() {
        //卧室界面activity结束时 发送广播 进行通知各卧室界面现在是处于哪个状态
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("check", "check"); //发送check消息 通知灯光控制界面 改变灯光展示图片
        sendBroadcast(intent);      //发送广播
        super.onDestroy();
    }

    /**
     * 点击事件
     * @param view
     */
    public void MyClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack://返回
                finish();
                break;
        }
    }

}
