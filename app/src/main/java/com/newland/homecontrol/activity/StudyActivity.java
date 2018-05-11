package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
 * 书房
 */

public class StudyActivity extends Activity {

    private TextView tvTitle;
    private LinearLayout llbottomBackGround;//下部分背景 需要设置透明度
    private RelativeLayout rlBackGround;//背景图片
    private ImageView ivCurtain;
    private CheckBox cbStudy;
    private AppAction mAppAction;
    private SharedPreferencesUtils spUtils;
    private boolean isSave=false;//是否要保存灯的状态到SP中


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        //初始化视图
        initView();
        //初始化监听
        initCheckListenser();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        llbottomBackGround = (LinearLayout) findViewById(R.id.llbottomBackGround);
        rlBackGround = (RelativeLayout) findViewById(R.id.rlBackGround);
        cbStudy = (CheckBox) findViewById(R.id.cbStudy);
        llbottomBackGround.getBackground().setAlpha(220);
        tvTitle.setText("书房灯控");

        mAppAction = new AppActionImpl(this);
        // 设置服务器地址
        spUtils = SharedPreferencesUtils.getInstant(this);
        cbStudy.setChecked(spUtils.getStudy());//设置灯控开关初始状态
        setLightBackground(spUtils.getStudy(),false);//设置书房灯的初始状态
        if (spUtils.getISAutomatic()) {//是自动模式 隐藏下方按钮
            llbottomBackGround.setVisibility(View.INVISIBLE);
        } else {
            llbottomBackGround.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开关灯状态监听
     */
    private void initCheckListenser() {
        //窗帘开关监听
        cbStudy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (spUtils.getIsSimulation()) {
                    //如果是模拟，开关灯直接切换图片
                    setLightBackground(isChecked,true);
                } else {
                    //如果不是模拟，去获取云平台上灯的开关状态
                    operateLight(isChecked);
                }
            }
        });
    }

    /**
     * 模拟的时候设置灯的灯的状态（直接切换图片）
     */
    private void setLightBackground(boolean check,boolean isSave) {
        if(isSave){
            //存储当前卧室灯的开关状态
            spUtils.setStudy(cbStudy.isChecked());
        }
        if (check) {
            rlBackGround.setBackgroundResource(R.mipmap.pic_study);
        } else {
            rlBackGround.setBackgroundResource(R.mipmap.pic_study_close);
        }
    }

    /**
     * 通知设备开关
     *
     * @param isChecked
     */

    private void operateLight(final boolean isChecked) {

        mAppAction.onOff(Global.LIGHT_STUDY, isChecked,
                new ActionCallbackListener<ApiResponse<String>>() {

                    @Override
                    public void onSuccess(ApiResponse<String> data) {
                        setLightBackground(isChecked,true);//进行开关灯图片替换
                    }

                    @Override
                    public void onFailure(String errorEven, String message) {
                        Toast.makeText(StudyActivity.this, message,
                                Toast.LENGTH_SHORT).show();

                    }
                });

    }


    @Override
    protected void onDestroy() {
        //卧室界面activity结束时 发送广播 进行通知各卧室界面现在是处于哪个状态
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("check", "check");  //向广播接收器传递数据
        sendBroadcast(intent);//发送check消息 通知灯光控制界面 改变灯光展示图片
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
