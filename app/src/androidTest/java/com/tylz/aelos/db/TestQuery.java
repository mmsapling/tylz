package com.tylz.aelos.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.tylz.aelos.bean.ActionData;
import com.tylz.aelos.util.LogUtils;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.db
 *  @文件名:   TestQuery
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/12 20:33
 *  @描述：    TODO
 */
public class TestQuery
        extends AndroidTestCase
{
    //    _ID + " integer primary key autoincrement, " +
    //    AUDIO + " text," +
    //    CONTENT + " text, " +
    //    FILESTREAM + " text, " +
    //    TITLESTREAM + " text, " +
    //    ID + " varchar(10), " +
    //    PICURL + " text, " +
    //    SECOND + " varchar(20), " +
    //    THUMBNAILS + " text, " +
    //    TITLE + " varchar(50), " +
    //    TYPE + " varchar(20), " +
    //    VIDEO + " text, " +
    //    IS_EDIT + " varchar(10)" +
    //            ");";
    public void query() {
        DbHelper       dbHelper = new DbHelper(getContext());
        SQLiteDatabase db       = dbHelper.getReadableDatabase();
        String[] columns = new String[]{ISql.T_Action._ID,
                                        ISql.T_Action.AUDIO,
                                        ISql.T_Action.CONTENT,
                                        ISql.T_Action.FILESTREAM,
                                        ISql.T_Action.TITLESTREAM,
                                        ISql.T_Action.ID,
                                        ISql.T_Action.PICURL,
                                        ISql.T_Action.SECOND,
                                        ISql.T_Action.THUMBNAILS,
                                        ISql.T_Action.TITLE,
                                        ISql.T_Action.TYPE,
                                        ISql.T_Action.VIDEO,
                                        ISql.T_Action.IS_EDIT};
        Cursor cursor = db.query(ISql.T_Action.T_ACTION, columns, null, null, null, null, null);
        if(cursor != null){
            if(cursor.moveToNext()){
                int _id = cursor.getInt(0);
                String fileName = cursor.getString(1);
                String content = cursor.getString(2);
                String fileStream = cursor.getString(3);
                String titleStream = cursor.getString(4);
                String id = cursor.getString(5);
                String picurl = cursor.getString(6);
                String second = cursor.getString(7);
                String thumbnails = cursor.getString(8);
                String title = cursor.getString(9);
                String type = cursor.getString(10);
                String video = cursor.getString(11);
                String isEdit = cursor.getString(12);
                ActionData actionData = new ActionData();
                actionData._id = _id;
                actionData.audioName = fileName;
                actionData.content = content;
                actionData.filestream = fileStream;
                actionData.titlestream = titleStream;
                actionData.id = id;
                actionData.picurl = picurl;
                actionData.second = second;
                actionData.thumbnails = thumbnails;
                actionData.title = title;
                actionData.type = type;
                actionData.video = video;
                actionData.isEnable = isEdit;
                LogUtils.d(actionData.toString());
            }
        }
    }
    public void findKeySettingMusic(String key){
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select b.audio  from " + ISql.T_Key_Setting .T_KEY_SETTING+ " a left join " +
                ISql.T_Action.T_ACTION + " b on a.titlestream=b.titlestream where a.game_key = ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{key});
        if(cursor != null){
            while(cursor.moveToNext()){
                String audio = cursor.getString(0);
                LogUtils.d("audio = " + audio);
            }
            cursor.close();
        }
    }
}
