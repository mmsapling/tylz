package com.tylz.aelos.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.UpdateInfo;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.AppUtils;
import com.tylz.aelos.util.FileUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.util.UpdateInfoParser;
import com.tylz.aelos.view.DAlertDialog;
import com.tylz.aelos.view.DNumProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   AboutActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:19
 *  @描述：    关于界面
 */
public class AboutActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton    mIvLeft;
    @Bind(R.id.tv_title)
    TextView       mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton    mIvRight;
    @Bind(R.id.rl_version_introduce)
    RelativeLayout mRlVersionIntroduce;
    @Bind(R.id.rl_check_update)
    RelativeLayout mRlCheckUpdate;
    @Bind(R.id.rl_instructions)
    RelativeLayout mRlInstructions;
    @Bind(R.id.rl_help)
    RelativeLayout mRlHelp;
    @Bind(R.id.rl_feedback)
    RelativeLayout mRlFeedback;
    private DNumProgressDialog mNumProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.about);
    }

    @OnClick({R.id.iv_left,
              R.id.rl_version_introduce,
              R.id.rl_check_update,
              R.id.rl_instructions,
              R.id.rl_help,
              R.id.rl_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.rl_version_introduce:
                skipActivity(VersionIntroduceActivity.class);
                break;
            case R.id.rl_check_update:

                checkUpdate();
                break;
            case R.id.rl_instructions:
                skipActivity(InstructionsActivity.class);
                break;
            case R.id.rl_help:
                skipActivity(AboutHelpActivity.class);
                break;
            case R.id.rl_feedback:
                skipActivity(FeedBackActivity.class);
                break;
        }
    }

    private void checkUpdate() {
        showProgress();
        OkHttpUtils.get()
                   .url(HttpUrl.LOCAL_VERSION_UPDATE_URL)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           closeProgress();
                           try {
                               UpdateInfo updateInfo = UpdateInfoParser.getUpdateInfo(response);
                               int     versionCode = AppUtils.getVersionCode(AboutActivity.this);
                               int serverVersionCode = Integer.parseInt(updateInfo.version);
                               if(serverVersionCode > versionCode){
                                   showUpdateTip(updateInfo);
                               }else{
                                   mToastor.getSingletonToast(R.string.latest_version)
                                           .show();
                               }
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                   });
    }

    private void showUpdateTip(final UpdateInfo updateInfo) {
        new DAlertDialog(this).builder()
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

                                  }
                              })
                              .show();
    }

    private void downloadApk(UpdateInfo updateInfo) {
        String fileName = System.currentTimeMillis() + ".apk";
        showNumProcess();
        OkHttpUtils.get().url(updateInfo.url).build().execute(new FileCallBack(FileUtils.getDownloadDir(),fileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                closeNumProcess();
                mToastor.getSingletonToast(R.string.tip_check_net).show();
            }

            @Override
            public void onResponse(File response, int id) {
                closeNumProcess();
                installApk(response);
            }

            @Override
            public void inProgress(final float progress, long total, int id) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        int index = (int) (progress * 100);
                        if (mNumProgressDialog != null) {
                            mNumProgressDialog.setProgress(index);
                        }
                    }
                });
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
