package com.tylz.aelos.db;

import android.provider.BaseColumns;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.db
 *  @文件名:   ISql
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/29 16:06
 *  @描述：    sql 语句相关的
 */
public interface ISql

{
    /**
     * 动作相关
     */
    interface T_Action
            extends BaseColumns
    {
        /**动作表名*/
        String T_ACTION    = "t_action";
        /**音频名称*/
        String AUDIO       = "audio";
        /**动作介绍*/
        String CONTENT     = "content";
        /**act文件流*/
        String FILESTREAM  = "filestream";
        /**动作名称流*/
        String TITLESTREAM = "titlestream";
        /**id*/
        String ID          = "id";
        /**上传图片路径*/
        String PICURL      = "picurl";
        /**动作时长*/
        String SECOND      = "second";
        /**未知的东西*/
        String THUMBNAILS  = "thumbnails";
        /**动作名称*/
        String TITLE       = "title";
        /**动作类型*/
        String TYPE        = "type";
        /**视频路径*/
        String VIDEO       = "video";
        /**是否可以编辑*/
        String IS_EDIT     = "is_edit";
        /** 动作建表语句*/
        String ACTION_SQL  = "create table " + T_ACTION + " ( " +
                _ID + " integer primary key autoincrement, " +
                AUDIO + " text," +
                CONTENT + " text, " +
                FILESTREAM + " text, " +
                TITLESTREAM + " text, " +
                ID + " varchar(50), " +
                PICURL + " text, " +
                SECOND + " varchar(20), " +
                THUMBNAILS + " text, " +
                TITLE + " varchar(50), " +
                TYPE + " varchar(20), " +
                VIDEO + " text, " +
                IS_EDIT + " varchar(10)" +
                ");";
    }

    interface T_Key_Setting
            extends BaseColumns
    {

        /**动作表名*/
        String T_KEY_SETTING  = "t_key_setting";
        /** 按键*/
        String GAME_KEY       = "game_key";
        /** 动作名称 */
        String TITLE          = "title";
        /**动作名称流*/
        String TITLESTREAM    = "titlestream";
        String KEYSETTING_SQL = " create table " + T_KEY_SETTING + " ( " +
                _ID + " integer primary key autoincrement, " +
                GAME_KEY + " varchar(50)," +
                TITLE + " varchar(100), " +
                TITLESTREAM + " varchar(100) " + " );";
        /** 按键*/
        String C1             = "c1";
        String C2             = "c2";
        String C3             = "c3";
        String C4             = "c4";
        String C5             = "c5";
        String C6             = "c6";
    }

    interface T_Status
            extends BaseColumns
    {
        /**表名*/
        String T_STATUS   = "t_status";
        /**
         * 动作名称
         */
        String ACTIONID   = "actionId";
        /**
         * 速度
         */
        String PROCESS    = "process";
        String STATUS1    = "status1";
        String STATUS2    = "status2";
        String STATUS3    = "status3";
        String STATUS4    = "status4";
        String STATUS5    = "status5";
        String STATUS6    = "status6";
        String STATUS7    = "status7";
        String STATUS8    = "status8";
        String STATUS9    = "status9";
        String STATUS10   = "status10";
        String STATUS11   = "status11";
        String STATUS12   = "status12";
        String STATUS13   = "status13";
        String STATUS14   = "status14";
        String STATUS15   = "status15";
        String STATUS16   = "status16";
        String STATUS_SQL = "create table " + T_STATUS + "(" +
                _ID + " integer primary key autoincrement," +
                ACTIONID + " integer," +
                PROCESS + " integer," +
                STATUS1 + " integer," +
                STATUS2 + " integer," +
                STATUS3 + " integer," +
                STATUS4 + " integer," +
                STATUS5 + " integer," +
                STATUS6 + " integer," +
                STATUS7 + " integer," +
                STATUS8 + " integer," +
                STATUS9 + " integer," +
                STATUS10 + " integer," +
                STATUS11 + " integer," +
                STATUS12 + " integer," +
                STATUS13 + " integer," +
                STATUS14 + " integer," +
                STATUS15 + " integer," +
                STATUS16 + " integer," +
                _COUNT + " text " +
                ")";
    }
}
