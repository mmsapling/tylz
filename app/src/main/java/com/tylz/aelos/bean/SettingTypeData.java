package com.tylz.aelos.bean;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.bean
 *  @文件名:   SettingTypeData
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/14 20:24
 *  @描述：    动作配置中类型的数据
 */
public class SettingTypeData {
    public String id;
    public String type;
    public String title;
    public String titlestream;
    public String filestream;
    /**
     * 0 代表下位机没有
     * 1 代表下位机有但是没配置
     * 2 代表配置进去了
     * */
    public int isFlag = 0;
}
