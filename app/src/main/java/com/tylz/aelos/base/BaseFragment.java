package com.tylz.aelos.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.tylz.aelos.bean.User;
import com.tylz.aelos.util.SPUtils;
import com.tylz.aelos.view.DProgressDialog;


/**
 * @author tylz
 * @time 2016/3/18 0018 15:02
 * @des 所有Fragment的基类，保存一些公共方法和属性
 * @updateAuthor tylz
 * @updateDate 2016/3/18 0018
 * @updateDes
 */

public abstract class BaseFragment
        extends Fragment
{
    public  SPUtils         mSpUtils;
    public  Activity        mContext;
    public  LayoutInflater  mLayoutInflater;
    private DProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpUtils = new SPUtils(getActivity());
        mContext = getActivity();
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onDestroyView() {
        closeProgress();
        super.onDestroyView();

    }
    @Override
    public void onStart() {
        super.onStart();
    }




    /**
     * 开启进度条
     */
    public void showProgress() {
        mProgressDialog = new DProgressDialog(mContext);
        mProgressDialog.show();
    }

    /**
     * 关闭进度条
     */
    public void closeProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
