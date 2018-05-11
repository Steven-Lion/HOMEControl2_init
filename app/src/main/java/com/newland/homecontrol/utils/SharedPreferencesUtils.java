package com.newland.homecontrol.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具
 * Created by shirley.zang on 2017/7/27.
 */

public class SharedPreferencesUtils {
    //定义的是要保存的信息的KEY
    private static final String IS_SIMULATION = "isSimulation";//是否是模拟
    private static final String TOKEN = "Token";//用户标识token
    private static final String IP = "Ip";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PROJECTID = "projectId";
    private static final String ISSAVEMSG = "isSaveMsg";//是否保存登录信息

    private static final String BEDROOM = "bedroom";//卧室灯开关标识
    private static final String BEDROOM_STATUS = "bedroom_status";//卧室灯亮度标识
    private static final String STUDY = "study";//书房灯开关标识
    private static final String LIVINGROOM = "livingroom";//客厅灯开关标识

    private static final String LIVINGROOM_START_TIME = "livingroom_start_time";//客厅开灯时间
    private static final String LIVINGROOM_END_TIME = "livingroom_end_time";//客厅关灯时间
    private static final String BEDROOM_START_TIME = "bedroom_start_time";//卧室开灯时间
    private static final String BEDROOM_END_TIME = "bedroom_end_time";//卧室关灯时间
    private static final String STUDY_START_TIME = "study_start_time";//书房开灯时间
    private static final String STUDY_END_TIME = "study_end_time";//书房关灯时间
    private static final String AUTOMATIC = "Automatic";//手动 自动 时间

    private static final String USER = "user";//文件名

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static SharedPreferencesUtils instant;

    private SharedPreferencesUtils(Context context, String file) {

        //实例化SharedPreferences
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);

        //获取Editor对象
        editor = sp.edit();
    }

    public synchronized static SharedPreferencesUtils getInstant(Context context) {
        if (instant == null)
            instant = new SharedPreferencesUtils(context, USER);//初始化
        return instant;
    }

    public void addKey(String key, String Value) {
        //保存一个String值
        editor.putString(key, Value);

        //提交数据
        editor.commit();
    }

    //根据key获取value值
    public String getKey(String key) {
        return sp.getString(key, "");
    }


    public void clear() {
        //清除所有值
        editor.clear();

        //提交数据
        editor.commit();
    }

    /**
     * 获取  是否模拟
     *
     * @return
     */
    public boolean getIsSimulation() {
        return sp.getBoolean(IS_SIMULATION, false);
    }

    /**
     * 设置 是否模拟
     *
     * @param simulation
     */
    public void setIsSimulation(boolean simulation) {
        editor.putBoolean(IS_SIMULATION, simulation);
        editor.commit();
    }

    /**
     * 设置 服务器IP
     *
     * @param ip
     */
    public void setIp(String ip) {
        editor.putString(IP, ip);
        editor.commit();
    }

    /**
     * 获取 IP
     *
     * @return
     */
    public String getIp() {
        return sp.getString(IP, "");
    }

    /**
     * 获取 用户唯一标识Token
     *
     * @return
     */
//    public String getToken() {
//        return sp.getString(TOKEN, "");
//    }

    /**
     * 获取 用户唯一标识Token
     *
     * @return
     */
    public void setToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }
    /**
     * 项目标识
     *
     * @return
     */
    public String getProjectID() {
        return sp.getString(PROJECTID, "");
    }

    public void setProjectID(String id) {
        editor.putString(PROJECTID, id);
        editor.commit();
    }

    /**
     * 用户名
     *
     * @return
     */
    public String getUsername() {
        return sp.getString(USERNAME, "");
    }

    public void setUsername(String username) {
        editor.putString(USERNAME, username);
        editor.commit();
    }

    /**
     * 密码
     *
     * @return
     */
    public String getPassword() {
        return sp.getString(PASSWORD, "");
    }

    public void setPassword(String password) {
        editor.putString(PASSWORD, password);
        editor.commit();
    }
    /**
     * 用户名
     *
     * @return
     */
    public boolean getIsSaveMsg() {
        return sp.getBoolean(ISSAVEMSG, false);
    }

    public void setIsSaveMsg(boolean isSave) {
        editor.putBoolean(ISSAVEMSG, isSave);
        editor.commit();
    }

    /**
     * 存取客厅 灯状态
     */

    public void setLivingroom(boolean livingroom) {
        editor.putBoolean(LIVINGROOM, livingroom);
        editor.commit();
    }

    public boolean getLivingroom() {
        return sp.getBoolean(LIVINGROOM, false);
    }

    /**
     * 存取卧室 灯状态
     */
    public void setBedroom(boolean bedroom) {
        editor.putBoolean(BEDROOM, bedroom);
        editor.commit();
    }

    public boolean getBedroom() {
        return sp.getBoolean(BEDROOM, false);
    }

    /**
     * 卧室灯光状态
     * 0 黑暗 1暗 2微亮 3全亮
     *
     * @param status
     * @return
     */
    public void setStatus(int status) {
        editor.putInt(BEDROOM_STATUS, status);
        editor.commit();
    }

    public int getStatus() {
        return sp.getInt(BEDROOM_STATUS, 0);
    }

    /**
     * 存取书房 灯状态
     */
    public void setStudy(boolean study) {
        editor.putBoolean(STUDY, study);
        editor.commit();
    }

    public boolean getStudy() {
        return sp.getBoolean(STUDY, false);
    }

    /**
     * 存取 客厅 开灯 关灯 时间戳
     */
    public void setLivingStartTime(String startTime) {
        editor.putString(LIVINGROOM_START_TIME, startTime);
        editor.commit();
    }

    public String getLivingStartTime() {
        return sp.getString(LIVINGROOM_START_TIME, "请选择");
    }

    public void setLivingEndTime(String endTime) {
        editor.putString(LIVINGROOM_END_TIME, endTime);
        editor.commit();
    }

    public String getLivingEndTime() {
        return sp.getString(LIVINGROOM_END_TIME, "请选择");
    }

    /**
     * 存取 卧室 开灯 关灯 时间戳
     */
    public void setBedRoomStartTime(String startTime) {
        editor.putString(BEDROOM_START_TIME, startTime);
        editor.commit();
    }

    public String getBedRoomStartTime() {
        return sp.getString(BEDROOM_START_TIME, "请选择");
    }

    public void setBedRoomEndTime(String endTime) {
        editor.putString(BEDROOM_END_TIME, endTime);
        editor.commit();
    }

    public String getBedRoomEndTime() {
        return sp.getString(BEDROOM_END_TIME, "请选择");
    }

    /**
     * 存取 书房 开灯 关灯 时间戳
     */
    public void setStudyStartTime(String startTime) {
        editor.putString(STUDY_START_TIME, startTime);
        editor.commit();
    }

    public String getStudyStartTime() {
        return sp.getString(STUDY_START_TIME, "请选择");
    }

    public void setStudyEndTime(String endTime) {
        editor.putString(STUDY_END_TIME, endTime);
        editor.commit();
    }

    public String getStudyEndTime() {
        return sp.getString(STUDY_END_TIME, "请选择");
    }

    /**
     * 保留 选择 手动 或者自动
     */

    public void setISAutomatic(boolean automatic) {
        editor.putBoolean(AUTOMATIC, automatic);
        editor.commit();
    }

    public boolean getISAutomatic() {
        return sp.getBoolean(AUTOMATIC, false);
    }
}
