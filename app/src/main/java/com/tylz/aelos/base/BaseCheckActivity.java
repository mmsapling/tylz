package com.tylz.aelos.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.base
 *  @文件名:   BaseCheckActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/9/10 16:00
 *  @描述：    TODO
 */
public class BaseCheckActivity
        extends BaseActivity
{
    // 判断是否需要检测，防止不停的弹框
    private              boolean  isNeedCheck           = true;
    private static final int      PERMISSON_REQUESTCODE = 4000;
    protected            String[] needPermissions       = {
                                                            Manifest.permission.CAMERA,
                                                            Manifest.permission.RECORD_AUDIO,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions(needPermissions);
    }

    /**
     * @param
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                                              needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                                              PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                                                  perm) != PackageManager.PERMISSION_GRANTED)
            {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                //                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
