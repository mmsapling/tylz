package com.tylz.aelos.manager;

import com.tylz.aelos.R;
import com.tylz.aelos.util.FileUtils;
import com.tylz.aelos.util.LogUtils;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.manager
 *  @文件名:   Constants
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/13 15:54
 *  @描述：    TODO
 */
public interface Constants {
    /** sp名称 */
    String SP_FILE_NAME                 = "aelos";
    /** 日志标志 */
    String TAG                          = "tylz";
    /** 日志输出等级 */
    int    DEBUGLEVEL                   = LogUtils.LEVEL_ALL;
    /** 蓝牙扫描时长 */
    int    SCAN_PERIOD                  = 10000;
    /** 启动页等待时间 */
    long   LAUNCH_TIME                  = 4000;
    /** 启动页睡眠时间*/
    long   LAUNCH_SLEEP_TIME            = 200;
    /** 用户id */
    String USER_ID                      = "user_id";
    /** 音频目录 */
    String DIR_AUDIO                    = FileUtils.getDownloadDir();
    /** 数据库名称 */
    String DB_NAME                      = "aelos.db";
    /** 等待连接机器时长，声波*/
    long   WAITING_CONN_ROBOT_TIME      = 5000;
    /** 是否仅在wifi网络下载 默认为true*/
    String IS_DOWNLOAD_WIFI             = "is_download_wifi";
    /** 是否消息提醒 默认提醒true*/
    String IS_NOTIFICATION              = "is_notification";
    String USER_PWD                     = "user_pwd";
    /** 每页显示的数据量*/
    String PAGE_SIZE                    = "10000";
    /** 模型名称*/
    String MODEL_NAME                   = "android_z_1.3ds";
    /** 模型使用的纹理*/
    int    MODEL_TEXTURE                = R.mipmap.texture1024;
    /** 模型使用的纹理名称*/
    String MODEL_TEXTURE_NAME           = "texture_aoyun";
    /** 模型增减速度*/
    float  MODEL_REGULATION_SPEED       = 0.01f;
    /** 模型转动等待时间*/
    long   MODEL_ANIM_WAIT_TIME         = 10;
    /** 同步等待时间*/
    long   SYNCHRONIZATION_WAITING_TIME = 3000;
    /** 相隔多久读取蓝牙信号*/
    long   READ_RSSI_TIME               = 1000;
    /**是否是第一次启动主页*/
    String IS_FIRST_MAIN                = "is_first_main";
    /**是否是第一次启动遥控器*/
    String IS_FIRST_REMOTE_CONTROL      = "is_first_remote_control";
    /**是否是第一次启动商城*/
    String IS_FIRST_ACTION_SHOP         = "is_first_action_shop";
    /** 发送指令期间睡眠时间*/
    long   SEND_SLEEP_TIME              = 200;
    /** 发送指令期间睡眠短时间*/
    long   SEND_SLEEP_TIME_SHORT        = 200;
    /** 默认速度*/
    int    DEFAULT_PRGRESS              = 15;
    /** 播放动作时睡眠时间*/
    long   PLAY_ACTION_SLEEP_TIME       = 500;
    /** 下载动作等待时间*/
    long   DOWNLOAD_WATING_TIME         = 200;
    /** 动画时长*/
    long   ANIM_DURATION                = 700;
    /** 视频录制时长*/
    int    media_recorder_time          = 30000;
    /** 添加状态的时间*/
    long   ADD_STATUS_TIME              = 3000;
    String WIFI_NAME                    = "wifi_name";
    String WIFI_PWD                     = "wifi_pwd";
}
