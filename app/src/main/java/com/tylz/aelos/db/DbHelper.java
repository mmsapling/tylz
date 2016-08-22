package com.tylz.aelos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.tylz.aelos.R;
import com.tylz.aelos.bean.ActionDetailBean;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.KeySetting;
import com.tylz.aelos.bean.SettingTypeData;
import com.tylz.aelos.bean.Status;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.db
 *  @文件名:   DbHelper
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/29 12:43
 *  @描述：    TODO
 */
public class DbHelper
        extends SQLiteOpenHelper
{
    public static final int DEFAULT_VERSION = 1; //默认版本

    public DbHelper(Context context, int version)
    {
        super(context, Constants.DB_NAME, null, version);
    }

    public DbHelper(Context context)
    {
        super(context, Constants.DB_NAME, null, DEFAULT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ISql.T_Action.ACTION_SQL);
        db.execSQL(ISql.T_Key_Setting.KEYSETTING_SQL);
        db.execSQL(ISql.T_Status.STATUS_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 是否存在动作id
     * @param actionId 动作id
     * @return 存在返回true
     */
    public boolean isExistActionId(String actionId) {
        SQLiteDatabase db        = getReadableDatabase();
        String         selection = ISql.T_Action.ID + "=?";
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                 null,
                                 selection,
                                 new String[]{actionId},
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * 插入动作数据
     * @param fileName
     *         音频名称
     * @param content
     *         动作介绍
     * @param filestream
     *          act字符流
     * @param titlestream
     *          长度 + 动作名称的编码，不带.act
     * @param id
     *          不是主键的id，是这个动作的id，实际也是唯一的
     * @param picurl
     *           后台的一个路径，不知道啥用
     * @param second
     *          动作时间 如10s
     * @param thumbnails
     * @param title
     *          动作名称
     * @param type
     *          动作类型 如 基本动作，足球
     * @param video
     *         这个动作的视频地址
     * @param isEdit
     *          是否可以修改,true是代表可以修改
     * @return
     *          如果插入成功返回新行的id，如果不成功返回-1
     */
    public long insertAction(String fileName,
                             String content,
                             String filestream,
                             String titlestream,
                             String id,
                             String picurl,
                             String second,
                             String thumbnails,
                             String title,
                             String type,
                             String video,
                             String isEdit)
    {
        SQLiteDatabase db     = getWritableDatabase();
        ContentValues  values = new ContentValues();
        values.put(ISql.T_Action.AUDIO, fileName);
        values.put(ISql.T_Action.CONTENT, content);
        values.put(ISql.T_Action.FILESTREAM, filestream);
        values.put(ISql.T_Action.TITLE, title);
        values.put(ISql.T_Action.ID, id);
        values.put(ISql.T_Action.PICURL, picurl);
        values.put(ISql.T_Action.SECOND, second);
        values.put(ISql.T_Action.THUMBNAILS, thumbnails);
        values.put(ISql.T_Action.TITLESTREAM, titlestream);
        values.put(ISql.T_Action.TYPE, type);
        values.put(ISql.T_Action.VIDEO, video);
        values.put(ISql.T_Action.IS_EDIT, isEdit);
        long insert = db.insert(ISql.T_Action.T_ACTION, null, values);
        return insert;

    }

    /**
     *
     * @param detailBean
     *       动作详情页面数据
     * @param isEdit
     *       能编辑动作  true,不能编辑动作false
     * @return
     *      如果插入成功返回新行的id，如果不成功返回-1
     */
    public long insertAction(ActionDetailBean detailBean, String isEdit) {
        SQLiteDatabase db       = getWritableDatabase();
        ContentValues  values   = new ContentValues();
        String         fileName = CommomUtil.getFileNameByUrl(detailBean.audio);
        values.put(ISql.T_Action.AUDIO, fileName);
        values.put(ISql.T_Action.CONTENT, detailBean.content);
        values.put(ISql.T_Action.FILESTREAM, detailBean.filestream);
        values.put(ISql.T_Action.TITLE, detailBean.title);
        values.put(ISql.T_Action.ID, detailBean.id);
        values.put(ISql.T_Action.PICURL, detailBean.picurl);
        values.put(ISql.T_Action.SECOND, detailBean.second);
        values.put(ISql.T_Action.THUMBNAILS, detailBean.thumbnails);
        values.put(ISql.T_Action.TYPE, detailBean.type);
        values.put(ISql.T_Action.VIDEO, detailBean.video);
        values.put(ISql.T_Action.TITLESTREAM, detailBean.titlestream);
        values.put(ISql.T_Action.IS_EDIT, isEdit);
        long insert = db.insert(ISql.T_Action.T_ACTION, null, values);
        return insert;
    }

    /**
     * 得到titlestream 从键配置里面
     * @return List<String>
     */
    public List<KeySetting> findTitleStreamByKeySetting() {
        SQLiteDatabase   db    = getReadableDatabase();
        List<KeySetting> datas = new ArrayList<>();
        Cursor cursor = db.query(ISql.T_Key_Setting.T_KEY_SETTING,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                KeySetting keySetting  = new KeySetting();
                String     titlestream = cursor.getString(3);
                String     title       = cursor.getString(2);
                String     key         = cursor.getString(1);
                int        id          = cursor.getInt(0);
                keySetting.title = title;
                keySetting.id = id;
                keySetting.key = key;
                keySetting.titlestream = titlestream;
                datas.add(keySetting);
            }
            cursor.close();
        }
        return datas;
    }

    /**
     * 得到某一类型的所有数据
     * @param type
     *      类型
     * @return
     *      此类型的全部数据
     */
    public List<SettingTypeData> findSettingTypeDataByType(String type) {
        List<SettingTypeData> settingTypeDataList = new ArrayList<>();
        SQLiteDatabase        db                  = getReadableDatabase();
        String[] columns = new String[]{ISql.T_Action.ID,
                                        ISql.T_Action.TYPE,
                                        ISql.T_Action.TITLE,
                                        ISql.T_Action.TITLESTREAM,
                                        ISql.T_Action.FILESTREAM};
        String   selection     = ISql.T_Action.TYPE + "=?";
        String[] selectionArgs = new String[]{type};
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                 columns,
                                 selection,
                                 selectionArgs,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SettingTypeData typeData    = new SettingTypeData();
                String          id          = cursor.getString(0);
                String          _type       = cursor.getString(1);
                String          title       = cursor.getString(2);
                String          titlestream = cursor.getString(3);
                String          filestream  = cursor.getString(4);
                typeData.title = title;
                typeData.id = id;
                typeData.type = _type;
                typeData.titlestream = titlestream;
                typeData.filestream = filestream;
                settingTypeDataList.add(typeData);
            }
            cursor.close();
        }
        return settingTypeDataList;
    }

    /**
     * 得到动作所属的那个类型
     * @param titlestream
     *          名称流
     * @return
     *      类型
     */
    public String findTypeByTitleStream(String titlestream) {
        String         type          = "";
        SQLiteDatabase db            = getReadableDatabase();
        String[]       columns       = new String[]{ISql.T_Action.TYPE};
        String         selection     = ISql.T_Action.TITLESTREAM + " = ? ";
        String[]       selectionArgs = new String[]{titlestream};
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                 columns,
                                 selection,
                                 selectionArgs,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                type = cursor.getString(0);
            }
            cursor.close();
        }
        return type;
    }

    /**
     * 得到动作的名称
     * @param titlestream
     *          名称流
     * @return
     *      动作名称
     */
    public String findTitleByTitleStream(String titlestream) {
        String         title         = "";
        SQLiteDatabase db            = getReadableDatabase();
        String[]       columns       = new String[]{ISql.T_Action.TITLE};
        String         selection     = ISql.T_Action.TITLESTREAM + " = ? ";
        String[]       selectionArgs = new String[]{titlestream};
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                 columns,
                                 selection,
                                 selectionArgs,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                title = cursor.getString(0);
            }
            cursor.close();
        }
        return title;
    }

    /**
     * 插入键设置
     * @param gameKey 按键
     * @param titlestream  动作名称流
     * @param title  动作名称
     */
    public long insertKeySetting(String gameKey, String titlestream, String title) {
        SQLiteDatabase db     = getWritableDatabase();
        ContentValues  values = new ContentValues();
        values.put(ISql.T_Key_Setting.GAME_KEY, gameKey);
        values.put(ISql.T_Key_Setting.TITLESTREAM, titlestream);
        values.put(ISql.T_Key_Setting.TITLE, title);
        long id = db.insert(ISql.T_Key_Setting.T_KEY_SETTING, null, values);
        return id;
    }

    /**
     * 删除所有键设置
     */
    public void clearKeySetting() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ISql.T_Key_Setting.T_KEY_SETTING, null, null);
    }

    /**
     * 删除摸个动作
     * @param titlestream
     */
    public void deleteActionByTitleStream(String titlestream) {
        SQLiteDatabase db          = getWritableDatabase();
        String         whereClause = ISql.T_Action.TITLESTREAM + " = ?";
        String[]       whereArgs   = new String[]{titlestream};
        db.delete(ISql.T_Action.T_ACTION, whereClause, whereArgs);
    }

    /**
     * 查出按键对应的音乐名称
     * @param
     *      key 按键
     * @return
     *      所有的音乐名称
     */
    public List<String> findKeySettingMusic(String key) {
        List<String> list = new ArrayList<String>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = "select b.audio  from " + ISql.T_Key_Setting.T_KEY_SETTING + " a left join " +
                    ISql.T_Action.T_ACTION + " b on a.titlestream=b.titlestream where a.game_key = '" + key + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String audio = cursor.getString(0);
                    if (!TextUtils.isEmpty(audio)) {
                        list.add(audio);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 通过一个按键得到这个按键配置的动作字符串
     * @param key
     *      按键
     * @return
     *      动作名称字符串
     */
    public String findTitleStrByKey(String key) {
        String         titles  = "";
        SQLiteDatabase db      = getReadableDatabase();
        String[]       columns = new String[]{ISql.T_Key_Setting.TITLE};

        String selection = ISql.T_Key_Setting.GAME_KEY + " =?";
        Cursor cursor = db.query(ISql.T_Key_Setting.T_KEY_SETTING,
                                 columns,
                                 selection,
                                 new String[]{key},
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                titles += title + " ";
            }
            cursor.close();
        }
        if (TextUtils.isEmpty(titles)) {
            titles = UIUtils.getString(R.string.not_setting);
        }
        return titles;
    }

    /**
     * 找出所有的自定义动作
     * @return
     *      自定义动作
     */
    public List<CustomAction> findCustomActionList() {
        List<CustomAction> actions = new ArrayList<CustomAction>();
        SQLiteDatabase     db      = getReadableDatabase();
        String[] columns = new String[]{ISql.T_Action._ID,
                                        ISql.T_Action.FILESTREAM,
                                        ISql.T_Action.CONTENT,
                                        ISql.T_Action.TITLE,
                                        ISql.T_Action.ID,
                                        ISql.T_Action.TITLESTREAM,
                                        ISql.T_Action.TYPE};
        String selection = ISql.T_Action.TYPE + "=? and " + ISql.T_Action.IS_EDIT + "=?";
        String[] selectionArgs = new String[]{"自定义",
                                              "true"};
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                 columns,
                                 selection,
                                 selectionArgs,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CustomAction action = new CustomAction();
                action.filestream = cursor.getString(cursor.getColumnIndex(ISql.T_Action.FILESTREAM));
                action.id = cursor.getInt(cursor.getColumnIndex(ISql.T_Action._ID));
                action.type = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TYPE));
                action.titlestream = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TITLESTREAM));
                action.title = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TITLE));
                //因为这个id唯一，且空缺，文件名也是唯一，暂时用id替代
                action.fileName = cursor.getString(cursor.getColumnIndex(ISql.T_Action.ID));
                actions.add(action);
            }
            cursor.close();
        }
        return actions;
    }

    /**
     * 找到所有的状态通过动作的id
     * @param actionId
     *      动作的id
     * @return
     *      此动作id的所有状态
     */
    public List<Status> findStatussByActionId(String actionId) {
        List<Status>   statuss = new ArrayList<Status>();
        SQLiteDatabase db      = getReadableDatabase();
        String[] columns = new String[]{ISql.T_Status._ID,
                                        ISql.T_Status.ACTIONID,
                                        ISql.T_Status.PROCESS,
                                        ISql.T_Status.STATUS1,
                                        ISql.T_Status.STATUS2,
                                        ISql.T_Status.STATUS3,
                                        ISql.T_Status.STATUS4,
                                        ISql.T_Status.STATUS5,
                                        ISql.T_Status.STATUS6,
                                        ISql.T_Status.STATUS7,
                                        ISql.T_Status.STATUS8,
                                        ISql.T_Status.STATUS9,
                                        ISql.T_Status.STATUS10,
                                        ISql.T_Status.STATUS11,
                                        ISql.T_Status.STATUS12,
                                        ISql.T_Status.STATUS13,
                                        ISql.T_Status.STATUS14,
                                        ISql.T_Status.STATUS15,
                                        ISql.T_Status.STATUS16};
        String   selection     = ISql.T_Status.ACTIONID + "= ?";
        String[] selectionArgs = new String[]{actionId};
        Cursor cursor = db.query(ISql.T_Status.T_STATUS,
                                 columns,
                                 selection,
                                 selectionArgs,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Status status = new Status();
                status.id = cursor.getInt(cursor.getColumnIndex(ISql.T_Status._ID));
                status.actionId = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.ACTIONID));
                status.progress = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.PROCESS));
                status.arr[0] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS1));
                status.arr[1] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS2));
                status.arr[2] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS3));
                status.arr[3] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS4));
                status.arr[4] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS5));
                status.arr[5] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS6));
                status.arr[6] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS7));
                status.arr[7] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS8));
                status.arr[8] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS9));
                status.arr[9] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS10));
                status.arr[10] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS11));
                status.arr[11] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS12));
                status.arr[12] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS13));
                status.arr[13] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS14));
                status.arr[14] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS15));
                status.arr[15] = cursor.getInt(cursor.getColumnIndex(ISql.T_Status.STATUS16));
                statuss.add(status);
            }
            cursor.close();
        }
        return statuss;
    }

    /**
     * 根据id删除状态
     * @param statusId
     *      状态id
     * @return
     *      删除的行数
     */
    public long deleteStatusByStatusId(String statusId) {
        SQLiteDatabase db            = getWritableDatabase();
        String         selection     = ISql.T_Status._ID + "=?";
        String[]       selectionArgs = new String[]{statusId};
        return db.delete(ISql.T_Status.T_STATUS, selection, selectionArgs);
    }

    /**
     * 更新自定义动作的filestream
     * @param id
     * @param filestream
     * @return
     */
    public int updateCustomAction(String id, String filestream) {
        SQLiteDatabase db        = getWritableDatabase();
        String         where     = ISql.T_Action._ID + "=?";
        String[]       whereArgs = {id};
        ContentValues  values    = new ContentValues();
        values.put(ISql.T_Action.FILESTREAM, filestream);
        return db.update(ISql.T_Action.T_ACTION, values, where, whereArgs);
    }

    /**
     * 删除状态根据动作id
     * @param actionId
     *          动作id
     * @return
     *          受影响的行数
     */
    public int deleteStatusByActionId(String actionId) {
        String         selection     = ISql.T_Status.ACTIONID + "=?";
        String[]       selectionArgs = new String[]{"" + actionId};
        SQLiteDatabase db            = getWritableDatabase();
        return db.delete(ISql.T_Status.T_STATUS, selection, selectionArgs);
    }

    /**
     * 插入状态数据
     * @param actionId
     *        动作id
     * @param arr
     *         状态数组
     * @param process
     *          进度
     * @return
     *      新行的id
     */
    public long insertStatus(int actionId, int[] arr, int process) {
        ContentValues values = new ContentValues();
        values.put(ISql.T_Status.ACTIONID, actionId);
        values.put(ISql.T_Status.PROCESS, process);
        for (int i = 0; i < arr.length; i++) {
            values.put("status" + (i + 1), arr[i]);

        }
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(ISql.T_Status.T_STATUS, null, values);
    }

    /**
     * 找到指定的动作
     * @param titlestream
     *      名称流
     * @return
     *      动作
     */
    public CustomAction findCustomAction(String titlestream) {
        CustomAction action = new CustomAction();
        action.id = -1;
        SQLiteDatabase db      = getReadableDatabase();
        String[]       columns = new String[]{ISql.T_Action._ID,
                                              ISql.T_Action.FILESTREAM,
                                              ISql.T_Action.ID,
                                              ISql.T_Action.TITLESTREAM,
                                              ISql.T_Action.TITLE,
                                              ISql.T_Action.TYPE};
        String selection = ISql.T_Action.TITLESTREAM + "=?";
        String[] selectionArgs = new String[] { titlestream };
        Cursor cursor = db.query(ISql.T_Action.T_ACTION,
                                columns,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null);
        if(cursor != null){
            if(cursor.moveToNext()){
                action.filestream = cursor.getString(cursor.getColumnIndex(ISql.T_Action.FILESTREAM));
                action.id = cursor.getInt(cursor.getColumnIndex(ISql.T_Action._ID));
                action.type = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TYPE));
                action.titlestream = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TITLESTREAM));
                action.fileName = cursor.getString(cursor.getColumnIndex(ISql.T_Action.ID));
                action.title = cursor.getString(cursor.getColumnIndex(ISql.T_Action.TITLE));
            }
            cursor.close();
        }
        return action;
    }


}
