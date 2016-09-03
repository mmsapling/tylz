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

    String BASE_IP                  = "192.168.1.92:8080";
    String BASE                     = "http://" + BASE_IP + "/Workspace/network/app/interface.php?func=";
    /**版本更新地址*/
    String VERSION_UPDATE_URL       = "http://www.lejurobot.com/app/version.xml";
    String LOCAL_VERSION_UPDATE_URL = "http://" + BASE_IP + "/Workspace/network/app/version.xml";
}

