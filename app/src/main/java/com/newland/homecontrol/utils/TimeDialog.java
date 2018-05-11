package com.newland.homecontrol.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.newland.homecontrol.R;


/**
 *
 */

public class TimeDialog extends Dialog {

    private TimePicker tpTime;
    private Button btnOk, btnCancel;
    private DataCallBack callBack;


    public TimeDialog(Context context, int hour, int minute, DataCallBack callBack) {
        super(context, R.style.DialogStyle);
        setContentView(R.layout.dialog_time);
        this.callBack = callBack;
        // 设置对话框空白处（对话框外面）点击对话框对话框不消失
        this.setCanceledOnTouchOutside(false);
        // 获取窗体
        Window window = this.getWindow();

        // 获取对话框当前的参数值
        WindowManager.LayoutParams wl = window.getAttributes();

        // 获取WindowManager的实例
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // 设置宽度为屏幕的三分之一
        wl.width = ((int) (wm.getDefaultDisplay().getWidth()) / 3) * 1;

        // 设置窗体参数
        window.setAttributes(wl);
        initView(hour, minute);
    }

    private void initView(int hour, int minute) {
        tpTime = (TimePicker) findViewById(R.id.tpTime);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        tpTime.setIs24HourView(true);
        tpTime.setCurrentHour(hour);
        tpTime.setCurrentMinute(minute);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeDialog.this.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onDismiss(tpTime.getCurrentHour(), tpTime.getCurrentMinute());
                TimeDialog.this.dismiss();
            }
        });

    }


    public interface DataCallBack {
        public void onDismiss(int hour, int minute);
    }
}
