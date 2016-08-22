package com.tylz.aelos.bean;

import java.io.Serializable;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.bean
 *  @文件名:   CustomAction
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/18 17:52
 *  @描述：    自定义动作数据
 */
public class CustomAction implements Serializable {
    public int		id;
    public int 		speed = 20;
    public boolean  isEdit = true;
    public String   fileName; //这里用action表中的id字段代替
    //
    public String filestream;
    public String title;  // 动作名
    public String titlestream;// 长度 + 动作名 不带.act
    public String type ="自定义";//默认自定义
}
