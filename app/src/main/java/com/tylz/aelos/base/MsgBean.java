package com.tylz.aelos.base;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.base
 *  @文件名:   MsgBean
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/19 21:22
 *  @描述：    消息数据
 */
public class MsgBean {
    /**
     * 0 赞动作，1评论动作，2回复评论
     */
    public int    type;
    public String opUserid;
    public String opUserNickname;
    public String avatar;
    public String goodsid;
    public String goodsname;
    public String content;
    public String commented;
    public String updateTime;
    public String commentid;
    public String state;//0 未读，1已读
    public String id;
}
