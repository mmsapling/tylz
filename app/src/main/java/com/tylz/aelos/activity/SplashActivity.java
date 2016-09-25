package com.tylz.aelos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.threed.jpct.Object3D;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseCheckActivity;
import com.tylz.aelos.base.ILoadModel;
import com.tylz.aelos.bean.UpdateInfo;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.service.LoadObject3DService;
import com.tylz.aelos.util.AppUtils;
import com.tylz.aelos.util.FileUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.util.UpdateInfoParser;
import com.tylz.aelos.view.DAlertDialog;
import com.tylz.aelos.view.DNumProgressDialog;
import com.tylz.aelos.view.GifView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * 启动页
 */

public class SplashActivity
        extends BaseCheckActivity
{


    private static final int WHAT_CHECK_LOAD = 0;
    GifView                     mSplashGif;
    ILoadModel                  mILoadModel;
    LoadObjectServiceConnection mServiceConnection;
    private DNumProgressDialog mNumProgressDialog;
    private DAlertDialog       mDAlertDialog;
    private Handler mLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (WHAT_CHECK_LOAD == msg.what) {
                processLoadInfo();
            }
        }
    };

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

        checkUpdate();
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

    private boolean isFinish = false;

    private void processLoadInfo() {
        if (mILoadModel != null) {
            Object3D object3D = mILoadModel.callLoad3DModel();
            if (object3D != null) {
                isFinish = true;
                /**
                 * 如果存在用户信息，那么跳过登录，直接进入扫描
                 * 否则进入登录
                 */
                if (!TextUtils.isEmpty(mUser_id)) {
                    skipActivityF(ScanBleActivity.class);
                } else {
                    skipActivityF(LoginActivity.class);
                }
            }

        }
        if (isFinish) {
            mLoadHandler.removeMessages(WHAT_CHECK_LOAD);
        } else {
            startCheckLoad();
        }

    }

    private void startCheckLoad() {
        mLoadHandler.removeMessages(WHAT_CHECK_LOAD);
        mLoadHandler.sendEmptyMessageDelayed(WHAT_CHECK_LOAD, Constants.LAUNCH_SLEEP_TIME);
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
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        mLoadHandler.removeMessages(WHAT_CHECK_LOAD);
        mLoadHandler = null;
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        OkHttpUtils.get()
                   .url(HttpUrl.LOCAL_VERSION_UPDATE_URL)
                   .build()
                   .execute(new ResultCall(false) {
                       @Override
                       public void onResult(String response, int id) {
                           try {
                               UpdateInfo updateInfo = UpdateInfoParser.getUpdateInfo(response);
                               int versionCode       = AppUtils.getVersionCode(SplashActivity.this);
                               int serverVersionCode = Integer.parseInt(updateInfo.version);
                               if (serverVersionCode > versionCode) {
                                   showUpdateTip(updateInfo);
                               } else {
                                   //                                   mToastor.getSingletonToast(R.string.latest_version)
                                   //                                           .show();
                                   startCheckLoad();
                               }
                           } catch (Exception e) {
                               e.printStackTrace();
                               startCheckLoad();
                           }
                       }

                       @Override
                       public void onError(Call call, Exception e, int id) {
                           super.onError(call, e, id);
                           startCheckLoad();
                       }

                       @Override
                       public void onEmpty() {
                           super.onEmpty();
                           startCheckLoad();
                       }
                   });
    }

    private void showUpdateTip(final UpdateInfo updateInfo) {
        if (mDAlertDialog == null) {
            mDAlertDialog = new DAlertDialog(this).builder()
                                                  .setTitle(UIUtils.getString(R.string.tip))
                                                  .setCancelable(false)
                                                  .setMsg(UIUtils.getString(R.string.tip_app_update))
                                                  .setPositiveButton(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          downloadApk(updateInfo);
                                                      }
                                                  })
                                                  .setNegativeButton(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          startCheckLoad();
                                                      }
                                                  });
        }
        mDAlertDialog.show();
    }

    private void downloadApk(UpdateInfo updateInfo) {
        String fileName = System.currentTimeMillis() + ".apk";
        showNumProcess();
        OkHttpUtils.get()
                   .url(updateInfo.url)
                   .build()
                   .execute(new FileCallBack(FileUtils.getDownloadDir(), fileName) {
                       @Override
                       public void onError(Call call, Exception e, int id) {
                           closeNumProcess();
                           mToastor.getSingletonToast(R.string.tip_check_net)
                                   .show();
                           startCheckLoad();
                       }

                       @Override
                       public void onResponse(File response, int id) {
                           closeNumProcess();
                           installApk(response);
                       }

                       @Override
                       public void inProgress(final float progress, long total, int id) {


                           int index = (int) (progress * 100);
                           if (mNumProgressDialog != null) {
                               mNumProgressDialog.setProgress(index);
                           }


                       }
                   });
    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**展示进度条弹窗*/
    private void showNumProcess() {
        mNumProgressDialog = new DNumProgressDialog(this);
        if (!mNumProgressDialog.isShowing()) {
            mNumProgressDialog.showDialog();
        }
    }

    /**关闭进度条弹窗*/
    private void closeNumProcess() {
        if (mNumProgressDialog != null) {
            mNumProgressDialog.dismiss();
            mNumProgressDialog = null;
        }
    }
}
