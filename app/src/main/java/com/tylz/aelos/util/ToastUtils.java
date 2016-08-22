package com.tylz.aelos.util;

import android.widget.Toast;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.util
 *  @文件名:   ToastUtils
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 12:50
 *  @描述：    Toast封装工具
 */
public class ToastUtils {
    /**
     * 弹出短Toast
     * @param resId
     *          资源id
     */
    public static  void showToast(int resId){
        Toast.makeText(UIUtils.getContext(), UIUtils.getString(resId), Toast.LENGTH_SHORT)
             .show();
    }
    public static  void showToast(String msg){
        Toast.makeText(UIUtils.getContext(), msg, Toast.LENGTH_SHORT)
             .show();
    }
}
