package com.newland.homecontrol.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 广播 方式 通知变更开灯状态 广播接收器
 * Created by yizhong.xu on 2017/8/4.
 */

public class MyReceiver extends BroadcastReceiver {

    public Message message;
    @Override
    public void onReceive(Context context, Intent intent) {
        message.getMsg(intent.getStringExtra("check"));
    }

    public interface Message {
         void getMsg(String str);
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
