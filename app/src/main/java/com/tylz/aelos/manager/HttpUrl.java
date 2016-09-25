package com.tylz.aelos.manager;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.manager
 *  @文件名:   HttpUrl
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/13 17:07
 *  @描述：    TODO
 */
public interface HttpUrl {

    //String BASE                 = "http://192.168.1.92:8080/Workspace/network/app/interface.php?func=";
    String BASE                     = "http://www.lejurobot.com/client/interface.php?func=";
    //String BASE                     = "http://www.lejurobot.com/client/interface.php?func=";
    /**版本更新地址*/
    String LOCAL_VERSION_UPDATE_URL = "http://www.lejurobot.com/client/version.xml";

    String SETUP_KEY               = "http://www.lejurobot.com/uploads/video/set_up_key.mp4";
    String HOW_TO_CONTROL_ROBOT    = "http://www.lejurobot.com/uploads/video/how_to_control_robot.mp4";
    String BLUETOOTH_SPEAKER       = "http://www.lejurobot.com/uploads/video/bluetooth_speaker.mp4 ";
    String CONNECT_TO_ROBOT        = "http://www.lejurobot.com/uploads/video/connect_to_robot.mp4";
    String COLLECTION_AND_DOWNLOAD = "http://www.lejurobot.com/uploads/video/collection_and_download.mp4";
    String VOICE_CONTROL           = "http://www.lejurobot.com/uploads/video/voice_control.mp4";
    String ACTION_CUSTOM           = "http://www.lejurobot.com/uploads/video/action_custom.mp4";
}

