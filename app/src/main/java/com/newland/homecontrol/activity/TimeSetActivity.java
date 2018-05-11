package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.homecontrol.R;
import com.newland.homecontrol.utils.SharedPreferencesUtils;
import com.newland.homecontrol.utils.TimeDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 自动开关
 */

public class TimeSetActivity extends Activity implements View.OnClickListener {

    private TextView tvStartLiving;
    private TextView tvEndLiving;
    private TextView tvStartBedroom;
    private TextView tvEndBedroom;
    private TextView tvStartStudy;
    private TextView tvEndStudy;
    private ImageView ivClose;
    private TextView tvOk;

    private LinearLayout llBackGround;

    private boolean checkFinish = true;//用来判断是否关闭弹窗


    private SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeset);

        //初始化视图
        initView();
        //初始化SharedPreferences存储
        initSharedPreferences();

    }

    private void initView() {
        llBackGround = (LinearLayout) findViewById(R.id.llBackGround);
        tvStartLiving = (TextView) findViewById(R.id.tvStartLiving);
        tvEndLiving = (TextView) findViewById(R.id.tvEndLiving);
        tvStartBedroom = (TextView) findViewById(R.id.tvStartBedRoom);
        tvEndBedroom = (TextView) findViewById(R.id.tvEndBedRoom);
        tvStartStudy = (TextView) findViewById(R.id.tvStartStudy);
        tvEndStudy = (TextView) findViewById(R.id.tvEndStudy);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        tvOk = (TextView) findViewById(R.id.tvOk);


        //点击监听
        tvStartLiving.setOnClickListener(this);
        tvEndLiving.setOnClickListener(this);
        tvStartBedroom.setOnClickListener(this);
        tvEndBedroom.setOnClickListener(this);
        tvStartStudy.setOnClickListener(this);
        tvEndStudy.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        tvOk.setOnClickListener(this);

        llBackGround.getBackground().setAlpha(230);//设置标题栏透明度
    }

    private void initSharedPreferences() {
        sharedPreferencesUtils = SharedPreferencesUtils.getInstant(this);

        //设置 获取用户时间设置的值 进行显示
        tvStartLiving.setText(sharedPreferencesUtils.getLivingStartTime());
        tvEndLiving.setText(sharedPreferencesUtils.getLivingEndTime());
        tvStartBedroom.setText(sharedPreferencesUtils.getBedRoomStartTime());
        tvEndBedroom.setText(sharedPreferencesUtils.getBedRoomEndTime());
        tvStartStudy.setText(sharedPreferencesUtils.getStudyStartTime());
        tvEndStudy.setText(sharedPreferencesUtils.getStudyEndTime());
    }


    /**
     * 发送广播
     */
    private void setMess() {
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        intent.putExtra("check", "update");  //发送广播 通知首页进行更新  开启新一轮判断
        sendBroadcast(intent);      //发送广播
    }

    /**
     * 选择时间弹窗
     *
     * @param context
     * @param btnTime 选择控件 用于显示所选时间
     * @param time    所选择的时间
     */
    public void showTime(Context context,
                         final TextView btnTime, final String time) {
        int hour;
        int minute;
        if (time.equals("请选择")) {//如果 还未选择 默认当前时间  如果不是 显示选择时间
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        } else {
            String[] temp = null;
            temp = time.split(":");//分隔字符串
            hour = Integer.valueOf(temp[0]);
            minute = Integer.valueOf(temp[1]);
        }
        new TimeDialog(TimeSetActivity.this, hour, minute, new TimeDialog.DataCallBack() {
            @Override
            public void onDismiss(int hour, int minute) {
                String mhour = hour < 10 ? "0" + hour : hour + "";
                String mminute = minute < 10 ? "0" + minute : minute
                        + "";
                String strData = mhour + ":" + mminute;
                btnTime.setText(strData);
            }
        }).show();

    }

    /**
     * String转Date
     *
     * @param time
     * @return
     */
    private Date getDateTime(String time) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 用来判断 是否满足条件
     */
    private void checkCondition() {

        //当客厅开灯时间和关灯时间不是“请选择”则用户进行选取时间操作，否则如果开灯时间或者关灯时间有一个是“请选择”则进行 "请选择您的开始时间，关灯时间"提示
        if (!tvStartLiving.getText().toString().equals("请选择") && !tvEndLiving.getText().toString().equals("请选择")) {
            //开灯的时间要小于关灯的时间 否则提示“客厅关灯时间少于开灯时间”
            if (getDateTime(tvStartLiving.getText().toString()).getTime() < getDateTime(tvEndLiving.getText().toString()).getTime()) {
                checkFinish = true;//标识 关掉弹窗
                //存取设置的开始时间 关闭时间
                sharedPreferencesUtils.setLivingStartTime(tvStartLiving.getText().toString());
                sharedPreferencesUtils.setLivingEndTime(tvEndLiving.getText().toString());
            } else {
                Toast.makeText(this, "客厅关灯时间必须大于开灯时间", Toast.LENGTH_SHORT).show();
                checkFinish = false;//标识 不关掉弹窗
                return;
            }
        } else if (!tvStartLiving.getText().toString().equals("请选择") || !tvEndLiving.getText().toString().equals("请选择")) {
            Toast.makeText(this, "请选择您的开灯时间，关灯时间", Toast.LENGTH_SHORT).show();
            checkFinish = false;
            return;
        }
        //当卧室开灯时间和关灯时间不是“请选择”则用户进行选取时间操作，否则如果开灯时间或者关灯时间有一个是“请选择”则进行 "请选择您的开始时间，关灯时间"提示
        if (!tvStartBedroom.getText().toString().equals("请选择") && !tvEndBedroom.getText().toString().equals("请选择")) {
            ///开灯的时间要小于关灯的时间 否则提示“卧室关灯时间少于开灯时间”
            if (getDateTime(tvStartBedroom.getText().toString()).getTime() < getDateTime(tvEndBedroom.getText().toString()).getTime()) {
                checkFinish = true;//标识 关掉弹窗
                sharedPreferencesUtils.setBedRoomStartTime(tvStartBedroom.getText().toString());
                sharedPreferencesUtils.setBedRoomEndTime(tvEndBedroom.getText().toString());

            } else {
                Toast.makeText(this, "卧室关灯时间必须大于开灯时间", Toast.LENGTH_SHORT).show();
                checkFinish = false;//标识 不关掉弹窗
                return;
            }
        } else if (!tvStartBedroom.getText().toString().equals("请选择") || !tvEndBedroom.getText().toString().equals("请选择")) {
            Toast.makeText(this, "请选择您的开始时间，关灯时间", Toast.LENGTH_SHORT).show();
            checkFinish = false;
            return;
        }
        //当书房开灯时间和关灯时间不是“请选择”则用户进行选取时间操作，否则如果开灯时间或者关灯时间有一个是“请选择”则进行 "请选择您的开始时间，关灯时间"提示
        if (!tvStartStudy.getText().toString().equals("请选择") && !tvEndStudy.getText().toString().equals("请选择")) {
            ///开灯的时间要小于关灯的时间 否则提示“书房关灯时间少于开灯时间”
            if (getDateTime(tvStartStudy.getText().toString()).getTime() < getDateTime(tvEndStudy.getText().toString()).getTime()) {
                checkFinish = true;//标识 关掉弹窗
                sharedPreferencesUtils.setStudyStartTime(tvStartStudy.getText().toString());
                sharedPreferencesUtils.setStudyEndTime(tvEndStudy.getText().toString());
            } else {
                Toast.makeText(this, "书房关灯时间必须大于开灯时间", Toast.LENGTH_SHORT).show();
                checkFinish = false;//标识 不关掉弹窗
                return;

            }
        } else if (!tvStartStudy.getText().toString().equals("请选择") || !tvEndStudy.getText().toString().equals("请选择")) {
            Toast.makeText(this, "请选择您的开始时间，关灯时间", Toast.LENGTH_SHORT).show();
            checkFinish = false;
            return;
        }
    }


    /**
     * 点击时间
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivClose:
                finish();
                break;
            case R.id.tvOk:
                //对有线程启动时间 赋值  用来控制启动的是哪个房间 开始 还是关闭
                checkCondition();
                if (checkFinish) {//用来判断是否关闭弹窗
                    finish();
                    setMess();//开始执行轮询
                }
                break;
            case R.id.tvStartLiving://开灯  客厅
                showTime(this, tvStartLiving, tvStartLiving.getText().toString());
                break;
            case R.id.tvEndLiving://关灯  客厅
                showTime(this, tvEndLiving, tvEndLiving.getText().toString());
                break;
            case R.id.tvStartBedRoom://开灯  卧室
                showTime(this, tvStartBedroom, tvStartBedroom.getText().toString());
                break;
            case R.id.tvEndBedRoom://关灯  卧室
                showTime(this, tvEndBedroom, tvEndBedroom.getText().toString());
                break;
            case R.id.tvEndStudy://关灯  书房
                showTime(this, tvEndStudy, tvEndStudy.getText().toString());
                break;
            case R.id.tvStartStudy://关灯  书房
                showTime(this, tvStartStudy, tvStartStudy.getText().toString());
                break;
        }
    }
}
