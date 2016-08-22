package com.tylz.aelos.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tylz.aelos.service.BlueService;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.base
 *  @文件名:   BaseConnection
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/25 23:26
 *  @描述：    TODO
 */
public class BaseConnection {
    public  IBluetooth            mIBluetooth;
    private BlueServiceConnection mBlueServiceConnection;

    /**
     * 绑定开启服务
     */
    public void startAndrBindService(Context context) {
        Intent service = new Intent(context, BlueService.class);
        context.startService(service);
        mBlueServiceConnection = new BlueServiceConnection();
        context.bindService(service, mBlueServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService(Context context) {
        if (mBlueServiceConnection != null) {
            context.unbindService(mBlueServiceConnection);
        }
    }

    public void bindService(Context context) {
        Intent service = new Intent(context, BlueService.class);
        mBlueServiceConnection = new BlueServiceConnection();
        context.bindService(service, mBlueServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
