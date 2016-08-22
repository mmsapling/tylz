package com.tylz.aelos.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tylz.aelos.bean.User;
import com.tylz.aelos.manager.Constants;


/**
 * @author cxw
 * @time 2016/3/18 0018 15:02
 * @des 保存一些公有的属性到sp
 * @updateAuthor tylz
 * @updateDate 2016/3/18 0018
 * @updateDes
 */
public class SPUtils {

    private SharedPreferences mSp;
    private Editor            mEditor;

    public SPUtils(Context context) {
        mSp = context.getSharedPreferences(Constants.SP_FILE_NAME, context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }

    /**保存boolean型变量*/
    public void putBoolean(String key, boolean value) {
        if (key != null) {
            mEditor.putBoolean(key, value);
            mEditor.commit();
        }
    }

    ;

    /**保存String型变量*/
    public void putString(String key, String value) {
        if (key != null) {
            mEditor.putString(key, value);
            mEditor.commit();
        }
    }

    /**保存int型变量*/
    public void putInt(String key, int value) {
        if (key != null) {
            mEditor.putInt(key, value);
            mEditor.commit();
        }
    }

    /**保存float型变量*/
    public void putFloat(String key, float value) {
        if (key != null) {
            mEditor.putFloat(key, value);
            mEditor.commit();
        }
    }

    /**得到int值*/
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    /**得到float值*/
    public float getFloat(String key, int defValue) {
        return mSp.getFloat(key, defValue);
    }

    /**得到boolean值*/
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    /**得到String值*/
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    /**删除Key值,返回boolean是否执行成功！*/
    public boolean removeKey(String key) {
        return mSp.edit()
                  .remove(key)
                  .commit();
    }

    /**删除全部Key值,返回boolean是否执行成功！*/
    public boolean clear() {
        return mSp.edit()
                  .clear()
                  .commit();
    }

    /**
     * 存放User信息
     * @param user
     *      用户信息
     */
    public void saveUserInfo(User user) {
        putString("user_avatar", user.avatar);
        putString("user_id", user.id);
        putString("user_nickname", user.nickname);
        putString("user_phone", user.phone);
        //
        putString("user_selfInfo", user.selfInfo);
        putString("user_mailbox", user.mailbox);
        putString("user_qq", user.qq);
        putString("user_address", user.address);
        putString("user_birth", user.birth);
        putString("user_gender", user.gender);
        putString("user_hobby", user.hobby);
        putString("user_age", user.age);
        putString("user_pwd",user.password);
    }

    /**
     * 清理用户信息
     */
    public void clearUserInfo() {
        removeKey("user_avatar");
        removeKey("user_id");
        removeKey("user_nickname");
        removeKey("user_phone");
        //
        removeKey("user_selfInfo");
        removeKey("user_mailbox");
        removeKey("user_qq");
        removeKey("user_address");
        removeKey("user_birth");
        removeKey("user_gender");
        removeKey("user_hobby");
        removeKey("user_age");
        removeKey("user_pwd");
    }

    /**
     * 从sp里面得到用户信息
     * @return
     *      用户信息
     */
    public User getUserInfoBySp() {
        User user = new User();
        user.avatar = getString("user_avatar", "");
        user.id = getString("user_id", "");
        user.nickname = getString("user_nickname", "");
        user.phone = getString("user_phone", "");
        //新加
        user.selfInfo = getString("user_selfInfo", "");
        user.mailbox = getString("user_mailbox", "");
        user.qq = getString("user_qq", "");
        user.address = getString("user_address", "");
        user.birth = getString("user_birth", "");
        user.gender = getString("user_gender", "");
        user.hobby = getString("user_hobby", "");
        user.age = getString("user_age", "");
        user.password = getString("user_pwd","");
        return user;
    }
}
