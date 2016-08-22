package com.tylz.aelos.bean;

import java.io.Serializable;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.bean
 *  @文件名:   HelpData
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 13:27
 *  @描述：    主页帮助数据
 */
public class MainHelpData
        implements Serializable {
    public int    index;
    public String question;
    public String answer;

    public MainHelpData(int index, String question, String answer) {
        this.index = index;
        this.question = question;
        this.answer = answer;
    }
}
