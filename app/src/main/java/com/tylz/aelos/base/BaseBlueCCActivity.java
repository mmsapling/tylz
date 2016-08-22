package com.tylz.aelos.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.tylz.aelos.R;
import com.tylz.aelos.activity.ScanBleActivity;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.base
 *  @文件名:   BaseBlueCCActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/6 19:41
 *  @描述：    TODO
 */
public class BaseBlueCCActivity extends BaseActivity {
    /** 蓝牙服务接口*/
    public  IBluetooth               mIBluetooth;
    public  BlueServiceConnection    mBlueServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBluetooth();
    }

    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;
            checkConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * 初始化蓝牙服务
     */
    private void initBluetooth() {
        Intent service = new Intent(this, BlueService.class);
        startService(service);
        mBlueServiceConnection = new BlueServiceConnection();
        bindService(service, mBlueServiceConnection, Context.BIND_AUTO_CREATE);
    }
    /**
     * 检查蓝牙连接是否成功，成功进入离线模式，反之跳转扫描界面
     */
    public void checkConnect() {
        if (mIBluetooth.callIsConnected()) {
            //设置为离线模式
            mIBluetooth.callWrite("cc");
        } else {
            skipScanUI();
        }
    }
    /**
     * 弹出失去连接提示，跳转到扫描页面
     */
    public void skipScanUI() {
        new DAlertDialog(this).builder().setTitle(UIUtils.getString(R.string.tip))
                                           .setMsg(UIUtils.getString(R.string.tip_ble_connect_fail))
                                           .setPositiveButton(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(BaseBlueCCActivity.this,
                                                                              ScanBleActivity.class);
                                                   startActivity(intent);
                                                   BaseBlueCCActivity.this.finish();
                                               }
                                           })
                                           .show();
    }

}
