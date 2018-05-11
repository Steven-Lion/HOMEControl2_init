package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
 * 卧室
 */

public class BedroomActivity extends Activity {


    private TextView tvTitle;
    private SeekBar seekBar;//拖动条
    AppAction mAppAction;
    private int seekBarPosition = 0;//拖动条 档位
    private boolean Simulation = true;//是否模拟

    private RelativeLayout rlBackGround;

    private LinearLayout llbottomBackGround;

    private SharedPreferencesUtils sharedPreferencesUtils;
    private int statusID;//RGB灯带  亮度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom);
        //初始化视图
        initView();
        //初始化SharePreferences存储
        initSharedPreferences();
        //初始化 进度条监听
        initSeekBar();
    }

    /**
     * 初始化 视图
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        rlBackGround = (RelativeLayout) findViewById(R.id.rlBackGround);
        llbottomBackGround = (LinearLayout) findViewById(R.id.llbottomBackGround);
        llbottomBackGround.getBackground().setAlpha(220);
        tvTitle.setText("卧室灯控");
        mAppAction = new AppActionImpl(this);
    }

    /**
     * 初始化SharePreferences存储
     */
    private void initSharedPreferences() {

        sharedPreferencesUtils = SharedPreferencesUtils.getInstant(this);
        //得到SP中保存的是否是模拟操作的变量
        Simulation = sharedPreferencesUtils.getIsSimulation();

        if (sharedPreferencesUtils.getBedroom()) {
            setImage(sharedPreferencesUtils.getStatus());//进行开关等级设置
            seekBar.setProgress(sharedPreferencesUtils.getStatus());//设置进度灯条
            seekBarPosition = sharedPreferencesUtils.getStatus();
        } else {
            setImage(0);//进行开关等级设置
            seekBar.setProgress(0);//设置进度灯条
        }
        if (sharedPreferencesUtils.getISAutomatic()) {//是自动模式 隐藏下方按钮
            llbottomBackGround.setVisibility(View.INVISIBLE);
        } else {
            llbottomBackGround.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化 进度条监听
     */
    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                seekBarPosition = position;//赋值 当前进度条 档位
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO 3-4: 如果不是模拟操作，则调用operateRGB（int status）调整RGB灯亮度
                //获取进度条结束时的数值
                int progress = seekBar.getProgress();
                switch(progress){
                    //档位
                    case 0:
                        if (Simulation){
                            //模拟状态下,直接进行图片切换.
                            setImage(0);
                        }else{
                            //进行网络操作后切换图片.
                            operateRGB(0);
                        }
                        break;
                    case 1:
                        if (Simulation){
                            //模拟状态下,直接进行图片切换.
                            setImage(1);
                        }else{
                            //进行网络操作后切换图片.
                            operateRGB(1);
                        }
                        break;
                    case 2:
                        if (Simulation){
                            //模拟状态下,直接进行图片切换.
                            setImage(2);
                        }else{
                            //进行网络操作后切换图片.
                            operateRGB(2);
                        }
                        break;
                    case 3:
                        if (Simulation){
                            //模拟状态下,直接进行图片切换.
                            setImage(3);
                        }else{
                            //进行网络操作后切换图片.
                            operateRGB(3);
                        }
                        break;
                }

            }
        });
    }

    /**
     * 控制RGB灯带的颜色
     */
    private void operateRGB(final int status) {
        // TODO 3-1: 根据传过来的值调整RGB灯带颜色
        // TODO: 根据传过来的值调整RGB灯带颜色
        switch (status){
            case 0:
                statusID = 0;break;
            case 1:
                statusID = 110;break;
            case 2:
                statusID = 160;break;
            case 3:
                statusID = 254;break;
    }
        mAppAction.OperationAtuator(Global.LIGHT_ROOM, statusID, new ActionCallbackListener<ApiResponse<String>>() {
            @Override
            public void onSuccess(ApiResponse<String> stringApiResponse) {
                setImage(status);//图片展示设置
                seekBar.setProgress(status);//进度条设置
            }

            @Override
            public void onFailure(String s, String s1) {
                Toast.makeText(BedroomActivity.this, s1, Toast.LENGTH_SHORT).show();

            }
        });

    }
    /**
     * 设置亮灯情况图片
     */

    private void setImage(int image) {
        // TODO 3-2: 根据RGB灯的状态显示不同的图片
        //保存灯光等级
        sharedPreferencesUtils.setStatus(image);
        switch (image){

        //设置关灯图片
        //保存关灯状态
            case 0:
                rlBackGround.setBackgroundResource(R.mipmap.pic_bedroom_close3);
                sharedPreferencesUtils.setBedroom(false);
                break;
        //设置微亮图片
        //保存开灯图片
            case 1:
                rlBackGround.setBackgroundResource(R.mipmap.pic_bedroom_close2);
                sharedPreferencesUtils.setBedroom(false);
                break;
        //设置亮图片
        //保存开灯图片
            case 2:
                rlBackGround.setBackgroundResource(R.mipmap.pic_bedroom_close1);
                sharedPreferencesUtils.setBedroom(false);
                break;

        //设置超亮图片
        //保存开灯图片
            case 3:
                rlBackGround.setBackgroundResource(R.mipmap.pic_bedroom);
                sharedPreferencesUtils.setBedroom(false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //卧室界面activity结束时 发送广播 进行通知各卧室界面现在是处于哪个状态
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("check", "check");  //发送check消息 通知灯光控制界面 改变灯光展示图片
        sendBroadcast(intent); //发送广播
        super.onDestroy();
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void MyClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack://返回
                finish();
                break;
            // TODO 3-3:  “+”“-”判断
            case R.id.ivAdd://加号
                if (seekBarPosition == 3){
                    Toast.makeText(this, "已达到最大亮度", Toast.LENGTH_SHORT).show();
                    return;//不做动作.
                }
                if (Simulation){//如果是模拟状态只改变图片.
                    setImage(seekBarPosition + 1);//设置展示图片
                    seekBar.setProgress(seekBarPosition + 1); // 改变进度条
                }else{
                    operateRGB(seekBarPosition + 1);
                }
                break;
            case  R.id.ivLess:
                if (seekBarPosition - 1 <0){//如果是最低值则提示已经关灯.
                    Toast.makeText(this,"已关灯",Toast.LENGTH_LONG).show();
                    return;
                }
                if (Simulation){
                    setImage(seekBarPosition - 1);//设置展示图片
                    seekBar.setProgress(seekBarPosition - 1); // 改变进度条
                }else{
                    operateRGB(seekBarPosition - 1);
                }
                break;

        }
    }
}
