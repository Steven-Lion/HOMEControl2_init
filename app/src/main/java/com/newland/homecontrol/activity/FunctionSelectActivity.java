package com.newland.homecontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.homecontrol.bean.Global;
import com.newland.homecontrol.broadcast.MyReceiver;
import com.newland.homecontrol.R;
import com.newland.homecontrol.utils.SharedPreferencesUtils;
import com.newland.homecontrol.utils.TimeUtils;
import com.nlecloud.nlecloudlibrary.api.ApiResponse;
import com.nlecloud.nlecloudlibrary.api.net.HttpEngine;
import com.nlecloud.nlecloudlibrary.core.ActionCallbackListener;
import com.nlecloud.nlecloudlibrary.core.AppAction;
import com.nlecloud.nlecloudlibrary.core.AppActionImpl;
import com.nlecloud.nlecloudlibrary.domain.AccessToken;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* 远程智能家居控制系统（项目2,3）
 */
public class FunctionSelectActivity extends Activity implements MyReceiver.Message {


    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivRight;
    private RelativeLayout rlTitle;
    private SharedPreferencesUtils sharedPreferencesUtils;//数据 存储工具

    private AppAction mAppAction;
    private MyReceiver myReceiver;//广播接收器
    //客户
    private Long LivingStartTime;
    private Long LivingEndTime;

    private String tvLivingStartTime = "";
    private String tvLivingEndTime = "";
    //卧室
    private Long BedRoomStartTime;
    private Long BedRoomEndTime;
    private String tvBedRoomStartTime = "";
    private String tvBedRoomEndTime = "";
    //书房
    private Long StudyStartTime;
    private Long StudyEndTime;
    private String tvStudyStartTime = "";
    private String tvStudyEndTime = "";
//    //标记当前客厅灯的开关状态，默认为false
//    private boolean isLivingRoomOpen = false;
//    //标记当前卧室灯的开关状态，默认为false
//    private boolean isBedRoomOpen = false;
//    //标记当前书房灯的开关状态，默认为false
//    private boolean isStudyOpen = false;

    //用于判断是否要执行开关灯操作
    //表示可以开客厅灯
    private boolean startTimeCheckLiving = true;
    //表示可以开卧室灯
    private boolean startTimeCheckBedRoom = true;
    //表示可以开书房灯
    private boolean startTimeCheckStudy = true;
    //表示可以关客厅灯
    private boolean endTimeCheckLiving = true;
    //表示可以关卧室灯
    private boolean endTimeCheckBedRoom = true;
    //表示可以关书房灯
    private boolean endTimeCheckStudy = true;
    //是否是自动
    private boolean ISAutomatic = false;
    //是否登陆云平台
    private boolean isLogin = false;
    //亮度值
    private int brightness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionselect);
        initView();//初始化视图
        receiver();//注册广播
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        rlTitle.getBackground().setAlpha(180);//设置透明度
        ivRight.setVisibility(View.VISIBLE);//设置显示
        sharedPreferencesUtils = SharedPreferencesUtils.getInstant(this);//获取实例
        ivBack.setImageResource(R.mipmap.icon);//消失不可见
        tvTitle.setText("远程智能家居控制系统");
        mAppAction = new AppActionImpl(this);//实例化云平台操作类
        // TODO 1-3: 接收登录页面传来的是否要登陆的信息，决定是否在功能选择页面登录，如果是则在功能页面登录，否则直接获取各房间灯的初始状态
        isLogin = getIntent().getBooleanExtra("isGoToLogin", false);
        if (isLogin) {
            String UserName = sharedPreferencesUtils.getUsername();
            String Pwd = sharedPreferencesUtils.getPassword();
            String IP = sharedPreferencesUtils.getIp();
            String ProjectId = sharedPreferencesUtils.getProjectID();
            login(UserName, Pwd, IP, ProjectId);
        }else{
            //调用获取各房间灯的初始状态的方法
            getLightState();
        }

        }
    //获取各房间灯的初始状态
    private void getLightState(){
        //todo 2-1:如果不是模拟操作，则获取三个房间灯的初始状态
        if (!sharedPreferencesUtils.getIsSimulation()){
            //获取各个房间开关灯的状态.
            getDataHttp(Global.LIGHT_LIVING,"客厅");
            getDataHttp(Global.LIGHT_ROOM,"卧室");
            getDataHttp(Global.LIGHT_STUDY,"书房");
            //发送广播.
            setMess();

        }

    }

    private void login(String UserName, String PWD, String IP, String ProjectId) {
        HttpEngine.SERVER_URL = IP;
        // 参数1：账号 ,参数2:密码,参数3:项目标示符
        mAppAction.login(UserName, PWD, ProjectId,
                new ActionCallbackListener<AccessToken>() {
                    @Override
                    public void onSuccess(AccessToken data) {
                        // 登录成功设置服务器返回的Token，下次请求将带上Token
                        HttpEngine.ACCESSTOKEN = data.getAccessToken();
                        Toast.makeText(FunctionSelectActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        //调用获取各房间灯的初始状态的方法
                        getLightState();
                    }

                    @Override
                    public void onFailure(String errorEven, String message) {
                        Toast.makeText(FunctionSelectActivity.this, message,
                                Toast.LENGTH_SHORT).show();
                        Log.d("-----shuju", message + "登陆");
                    }
                });
    }

    /**
     * 设置登录信息
     */
    private void setLoginMSG() {
        Intent intent = new Intent(FunctionSelectActivity.this, LoginActivity.class);
        intent.putExtra("isSetAccount", true);
        startActivity(intent);
    }


    /**
     * 执行网络请求
     */
    private void getDataHttp(int status, final String sort) {
        // TODO 2-2: 获取房间灯的开关状态
        mAppAction.getActuatorStateByActuatorId(status, new ActionCallbackListener<ApiResponse<Integer>>() {
            @Override
            public void onSuccess(ApiResponse<Integer> apiResponse) {
                if (apiResponse.getMessage().equals("获取执行器状态成功")){
                    // 0 is close another is open ,其他分别存储器开灯状态.
                    switch (sort){
                        case "客厅":
                            if (apiResponse.getAppendData() == 0){
                                sharedPreferencesUtils.setLivingroom(false);
                            }else{
                                sharedPreferencesUtils.setLivingroom(true);
                            }break;
                        case "卧室":
                            if (apiResponse.getAppendData() == 0){
                                sharedPreferencesUtils.setBedroom(false);
                                sharedPreferencesUtils.setStatus(0);
                            }else{
                                sharedPreferencesUtils.setBedroom(true);
                                //当开启状态时，我们获取其状态值，分别去设置灯带的3个档位的亮度.
                                switch (apiResponse.getAppendData()){
                                    case 110:
                                        sharedPreferencesUtils.setStatus(1);
                                        break;
                                    case 160:
                                        sharedPreferencesUtils.setStatus(2);
                                        break;
                                    case 254:
                                        sharedPreferencesUtils.setStatus(3);
                                        break;
                                }

                            }break;
                        case "书房":
                            if (apiResponse.getAppendData() == 0){
                                sharedPreferencesUtils.setStudy(false);
                            }else{
                                sharedPreferencesUtils.setStudy(true);
                            }break;
                    }
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                int b = 1;


            }
        });

    }

    /**
     * 初始化SharePreferences
     */
    private void initSharePreferences() {
        //获取各个房间 时间设置的开关时间
        //获取客厅 时间设置的 开灯时间和关灯时间
        tvLivingStartTime = sharedPreferencesUtils.getLivingStartTime();
        tvLivingEndTime = sharedPreferencesUtils.getLivingEndTime();
        //获取卧室 时间设置的 开灯时间和关灯时间
        tvBedRoomStartTime = sharedPreferencesUtils.getBedRoomStartTime();
        tvBedRoomEndTime = sharedPreferencesUtils.getBedRoomEndTime();
        //获取书房 时间设置的 开灯时间和关灯时间
        tvStudyStartTime = sharedPreferencesUtils.getStudyStartTime();
        tvStudyEndTime = sharedPreferencesUtils.getStudyEndTime();

       /* //初始 设置各个房间标识 防止开启后又进行开启（避免重复执行开启）
        startTimeCheckLiving = true;
        startTimeCheckBedRoom = true;
        startTimeCheckStudy = true;
        endTimeCheckLiving = true;
        endTimeCheckBedRoom = true;
        endTimeCheckStudy = true;*/
    }

    /**
     * 执行线程轮询
     */
    private void initTask() {
        ScheduledExecutorService singleThreadScheduledPool = Executors.newSingleThreadScheduledExecutor();//定时执行指定的任务
        //延迟1秒后，每隔5秒执行一次该任务
        singleThreadScheduledPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //是自动模式 才去执行我们时间设置的 开灯关灯
                if (ISAutomatic) {
                    //当客厅取出的时间是请选择证明用户是没进行设置的，不需要再继续执行 开关灯
                    if (!tvLivingStartTime.equals("请选择") && !tvLivingEndTime.equals("请选择")) {
                        //转换为Long进行时间对比比较
                        LivingStartTime = TimeUtils.getDateTime(tvLivingStartTime).getTime();
                        LivingEndTime = TimeUtils.getDateTime(tvLivingEndTime).getTime();
                        Long newTime = TimeUtils.getNewTime();//获取当前时间
                        //如果客厅现在时间大等于开始时间且小于结束时间并且可以开客厅灯变量为true，开灯
                        if (LivingStartTime <= newTime && newTime < LivingEndTime && startTimeCheckLiving) {
                            //开客厅灯
                            getHttpOpen(Global.LIGHT_LIVING, true, "客厅");
                            // 否者如果设置的客厅灯结束时间大于开始时间且现在时间大于设置的结束时间，并且可以关客厅灯变量为true，关灯
                        } else if (LivingEndTime > LivingStartTime && newTime >= LivingEndTime && endTimeCheckLiving) {
                            //关客厅灯
                            getHttpOpen(Global.LIGHT_LIVING, false, "客厅");
                        }
                    }
                    //当卧室取出的时间 是请选择 证明用户是没进行设置的，不需要再继续执行 开关灯
                    if (!tvBedRoomStartTime.equals("请选择") && !tvBedRoomEndTime.equals("请选择")) {
                        //转换为Long进行时间对比比较
                        BedRoomStartTime = TimeUtils.getDateTime(tvBedRoomStartTime).getTime();
                        BedRoomEndTime = TimeUtils.getDateTime(tvBedRoomEndTime).getTime();
                        Long newTime = TimeUtils.getNewTime();//获取当前时间
                        //如果卧室现在时间大等于开灯时间且小于关灯时间并且可以开卧室灯变量为true，打开RGB灯带
                        if (BedRoomStartTime <= newTime && newTime < BedRoomEndTime && startTimeCheckBedRoom) {
                            getDataHttp(true, "卧室");
                            // 否者如果卧室关灯时间大于开灯时间且现在时间大灯与关灯时间，可以关卧室灯变量为true，关闭卧室灯
                        } else if (BedRoomEndTime > BedRoomStartTime && newTime >= BedRoomEndTime && endTimeCheckBedRoom) {
                            //执行关闭
                            getDataHttp(false, "卧室");
                        }
                    }
                    //当书房取出的时间 是请选择 证明用户是没进行设置的，不需要再继续执行 开关灯
                    if (!tvStudyStartTime.equals("请选择") && !tvStudyEndTime.equals("请选择")) {
                        //转换为Long进行时间对比比较
                        StudyStartTime = TimeUtils.getDateTime(tvStudyStartTime).getTime();
                        StudyEndTime = TimeUtils.getDateTime(tvStudyEndTime).getTime();
                        Long newTime = TimeUtils.getNewTime();//获取当前时间
                        //如果现在时间大等于设置的开书房灯的时间且小于关书房灯的时间，并且可以开书房灯变量为true，开启书房灯
                        if (StudyStartTime <= newTime && newTime < StudyEndTime && startTimeCheckStudy) {
                            getHttpOpen(Global.LIGHT_STUDY, true, "书房");
                            // 否者如果设置的关书房灯的时间大于开书房灯的时间且现在时间大于关书房灯的时间，并且可以关书房灯变量为true,关闭书房灯
                        } else if (StudyEndTime > StudyStartTime && newTime >= StudyEndTime && endTimeCheckStudy) {
                            //执行关闭
                            getHttpOpen(Global.LIGHT_STUDY, false, "书房");
                        }
                    }

                }
            }
        }, 1, 5, TimeUnit.SECONDS);

    }

    /**
     * 网络请求 进行开关灯
     *
     * @param id        执行器id
     * @param isChecked 是执行开 或者关
     * @param mark      标识 是哪个房间
     */
    private void getHttpOpen(final int id, final boolean isChecked, final String mark) {

        mAppAction.onOff(id, isChecked,
                new ActionCallbackListener<ApiResponse<String>>() {

                    @Override
                    public void onSuccess(ApiResponse<String> data) {

                        checkToast(isChecked, mark);
                    }

                    @Override
                    public void onFailure(String errorEven, String message) {
                        Toast.makeText(FunctionSelectActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("-----shuju", message + "开关灯");
                    }
                });
    }

    /**
     * 执行卧室RGB灯
     */
    private void getDataHttp(final boolean isChecked, final String mark) {
        if (isChecked) {
            brightness = 254;
        } else {
            brightness = 0;
        }
        mAppAction.OperationAtuator(Global.LIGHT_ROOM, brightness, new ActionCallbackListener<ApiResponse<String>>() {
            @Override
            public void onSuccess(ApiResponse<String> stringApiResponse) {
                checkToast(isChecked, mark);//进行开关灯状态存储
            }

            @Override
            public void onFailure(String s, String s1) {
                Toast.makeText(FunctionSelectActivity.this, s1.toString(), Toast.LENGTH_SHORT).show();
                Log.d("-----shuju", "Rgv");
            }
        });
    }

    /**
     * 进行toast 和存状态
     *
     * @param isChecked
     * @param mark
     */
    private void checkToast(boolean isChecked, String mark) {
        switch (mark) {
            case "客厅":
                if (isChecked) {//客厅开灯时 ，就设置可以开客厅灯标识为false，后面轮询的时候不会再开客厅灯
                    startTimeCheckLiving = false;
                } else {//客厅关灯时 ，就设置可以关客厅灯标识为false，后面轮询的时候不会关开客厅灯
                    endTimeCheckLiving = false;
                }
                sharedPreferencesUtils.setLivingroom(isChecked);//保存现在灯的状态
                break;
            case "卧室":
                if (isChecked) {//卧室开灯时 ，就设置可以开客厅灯标识为false，后面轮询的时候不会再开RGB灯带
                    startTimeCheckBedRoom = false;
                } else {//卧室关灯时 ，就设置可以开客厅灯标识为false，后面轮询的时候不会再关RGB灯带
                    endTimeCheckBedRoom = false;
                }
                sharedPreferencesUtils.setStatus(3);
                sharedPreferencesUtils.setBedroom(isChecked);//保存现在RGB灯的状态
                break;
            case "书房":
                if (isChecked) {//书房开灯时 ，就设置可以开书房灯标识为false，后面轮询的时候不会再开书房
                    startTimeCheckStudy = false;
                } else {//书房关灯时 ，就设置可以关书房灯标识为false，后面轮询的时候不会再关书房
                    endTimeCheckStudy = false;
                }
                sharedPreferencesUtils.setStudy(isChecked);//保存现在灯的状态
                break;
        }
        setMess();//发送广播 通知灯光控制界面 改变灯光展示图片
    }

    @Override
    protected void onDestroy() {
        //结束时 设置模拟状态为false 避免直接进入还是 模拟状态
        sharedPreferencesUtils.setIsSimulation(false);
        super.onDestroy();
    }

    /**
     * 注册广播
     */
    private void receiver() {
        //动态注册广播接收器
        myReceiver = new MyReceiver();//自定义广播接收器
        IntentFilter intentFilter = new IntentFilter();//意图过滤
        intentFilter.addAction("com.nangch.broadcasereceiver.MYRECEIVER");
        registerReceiver(myReceiver, intentFilter);
        myReceiver.setMessage(this);
    }

    @Override
    public void getMsg(String str) {
        //收到广播消息，更新数据和标识
        //收到时间设置界面发来的广播update时 执行更新数据和标识。（收到广播证明 操作者有进行更改时间设置 需马上获取用户新改时间去进行判断开关灯）
        if (str.equals("update")) {
            initSharePreferences();
        }
        //收到灯光控制界面发来的广播Automatic 自动时。 替换存取为自动模式 并进开光标识的重置 可进行再次进入轮询去执行设置时间开关灯（刷新各个房间标识为初始状态 才能进行继续控制）
        if (str.equals("Automatic")) {
            ISAutomatic = sharedPreferencesUtils.getISAutomatic();
            if (ISAutomatic) {//是自动 就更新下状态 使其能进行再次开关灯
                startTimeCheckLiving = true;
                startTimeCheckBedRoom = true;
                startTimeCheckStudy = true;
                endTimeCheckLiving = true;
                endTimeCheckBedRoom = true;
                endTimeCheckStudy = true;
            }
        }
    }

    /**
     * 发送广播,通知灯光控制界面 改变灯光展示图片
     */
    private void setMess() {
        // TODO 2-3: 发送广播,通知灯光控制界面 改变灯光展示图片
        Intent intent = new Intent("com.nangch.broadcasereceiver.MYRECEIVER");
        //发送check消息,通知灯光控制界面，改变展示灯光图片.
        intent.putExtra("check","check");
        sendBroadcast(intent);//发送广播.
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void MyClick(View view) {
        switch (view.getId()) {
            case R.id.ivRight:
                //点击右上角设置按钮跳到设置登陆信息页面
                setLoginMSG();
                //结束Activity
                finish();
                break;
            case R.id.btnCurtain://跳转 窗帘界面
                startActivity(new Intent(FunctionSelectActivity.this, CurtainActivity.class));
                break;
            case R.id.btnLight://跳转 灯光界面
                startActivity(new Intent(FunctionSelectActivity.this, RoomSelectActivity.class));
                break;
        }
    }

}
