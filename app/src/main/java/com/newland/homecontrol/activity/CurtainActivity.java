package com.newland.homecontrol.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * 窗帘控制界面
 */

public class CurtainActivity extends Activity {

    private TextView tvTitle;//标题
    private ImageView ivOffCurtain;//窗帘开
    private CheckBox cbCurtain;//窗帘开关
    private LinearLayout llOffOn;//控制背景
    AppAction mAppAction;//网络请求Api
    private AnimationDrawable animationOffCurtain;//可以加载Drawable资源实现帧动画
    private AnimationDrawable animationOnCurtain;//可以加载Drawable资源实现帧动画
    private boolean isCurtain = true;//窗帘开关判断，默认值为true，true为要执行开窗帘操作
    SharedPreferencesUtils share;//存储工具类

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain);
        //初始化视图
        initView();
        //初始化开关监听
        initCheckListenser();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        //设置标题
        tvTitle.setText("窗帘控制");
        //得到关窗帘的ImageView
        ivOffCurtain = (ImageView) findViewById(R.id.ivOffCurtain);
        //得到控制窗帘的开关对应CheckBox
        cbCurtain = (CheckBox) findViewById(R.id.cbCurtain);
        //得到窗帘开关所在的LinearLayout
        llOffOn = (LinearLayout) findViewById(R.id.llOffOn);
        //设置透明度
        llOffOn.getBackground().setAlpha(220);
        share = SharedPreferencesUtils.getInstant(this);
        mAppAction=new AppActionImpl(CurtainActivity.this);
    }


    /**
     * 开关风扇监听
     */
    private void initCheckListenser() {
        //窗帘开关监听
        cbCurtain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (share.getIsSimulation()) {//是否是模拟模式，模拟模式则直接进行动画操作。
                    setAnimation(isChecked);
                } else {
                    oprateCurtain(Global.CURTAIN, isCurtain);//不是模拟模式的话执行行网络请求开关窗帘
                }
            }
        });

    }

    /**
     * 通知设备开关
     *
     * @param ID        执行器ID
     * @param isChecked 开关
     */
    private void oprateCurtain(final int ID, final boolean isChecked) {
            //调取网络开关窗帘 方法
            mAppAction.onOff(ID, isChecked,
                    new ActionCallbackListener<ApiResponse<String>>() {

                        @Override
                        public void onSuccess(ApiResponse<String> data) {
                            cbCurtain.setChecked(isChecked);//设置按钮状态
                            setAnimation(isChecked);//执行动画
                            isCurtain = !isChecked; //设置下次执行开或者关
                        }

                        @Override
                        public void onFailure(String errorEven, String message) {
                            Toast.makeText(CurtainActivity.this, message,
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

    }

    /**
     * 启动或停止窗帘动画
     *
     * @param isChecked
     */
    private void setAnimation(boolean isChecked) {
        if (isChecked) {
            ivOffCurtain.setBackgroundResource(R.drawable.anim_curtain_on);
            animationOnCurtain = (AnimationDrawable) ivOffCurtain.getBackground();
            animationOnCurtain.start();
        } else {
            ivOffCurtain.setBackgroundResource(R.drawable.anim_curtain_off);
            animationOffCurtain = (AnimationDrawable) ivOffCurtain.getBackground();
            animationOffCurtain.start();
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void MyClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack://返回
                finish();//关闭 结束
                break;
        }
    }
}
