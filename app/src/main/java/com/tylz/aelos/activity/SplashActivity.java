package com.tylz.aelos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.threed.jpct.Object3D;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseCheckActivity;
import com.tylz.aelos.base.ILoadModel;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.service.LoadObject3DService;
import com.tylz.aelos.view.GifView;

import cn.jpush.android.api.JPushInterface;

/**
 * 启动页
 */

public class SplashActivity
        extends BaseCheckActivity
{


    GifView mSplashGif;
    ILoadModel mILoadModel;
    LoadObjectServiceConnection mServiceConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /**开启服务，去加载模型*/
        mServiceConnection = new LoadObjectServiceConnection();
        Intent service = new Intent(this, LoadObject3DService.class);
        startService(service);
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        mSplashGif = (GifView) findViewById(R.id.splash_gif);
        mSplashGif.setMovieResource(R.mipmap.welcome);
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      while(true){
                                          if(mILoadModel != null){
                                              Object3D object3D = mILoadModel.callLoad3DModel();
                                              if(object3D != null){
                                                  /**
                                                   * 如果存在用户信息，那么跳过登录，直接进入扫描
                                                   * 否则进入登录
                                                   */
                                                  if(!TextUtils.isEmpty(mUser_id)){
                                                      skipActivityF(ScanBleActivity.class);
                                                  }else{
                                                      skipActivityF(LoginActivity.class);
                                                  }
                                                  break;
                                              }
                                          }
                                          SystemClock.sleep(Constants.LAUNCH_SLEEP_TIME);
                                      }


                                  }
                              });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private class LoadObjectServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mILoadModel = (ILoadModel) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceConnection != null){
            unbindService(mServiceConnection);
        }
    }
}
