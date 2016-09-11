package com.tylz.aelos.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.MainHelpData;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   MainHelpActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:33
 *  @描述：    主页帮助
 */
public class MainHelpActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton  mIvLeft;
    @Bind(R.id.tv_title)
    TextView     mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton  mIvRight;
    @Bind(R.id.help_tv_detail)
    TextView     mHelpTvDetail;
    @Bind(R.id.ll_help1)
    LinearLayout mLlHelp1;
    @Bind(R.id.ll_help2)
    LinearLayout mLlHelp2;
    @Bind(R.id.ll_help3)
    LinearLayout mLlHelp3;
    @Bind(R.id.ll_help4)
    LinearLayout mLlHelp4;
    @Bind(R.id.ll_help5)
    LinearLayout mLlHelp5;
    TextView tvQuestion = null;
    TextView tvAnswer   = null;
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_help);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTvTitle.setText(R.string.help);
    }

    @OnClick({R.id.iv_left,
              R.id.ll_help1,
              R.id.ll_help2,
              R.id.ll_help3,
              R.id.ll_help4,
              R.id.ll_help5})
    public void onClick(View view) {
        // http://www.lejurobot.com/uploads/video/蓝牙音响.mp4
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.ll_help1:
                clickPlay(HttpUrl.CONNECT_TO_ROBOT);
                break;
            case R.id.ll_help2:
                clickPlay(HttpUrl.HOW_TO_CONTROL_ROBOT);
                break;
            case R.id.ll_help3:
                clickPlay(HttpUrl.COLLECTION_AND_DOWNLOAD);
                break;
            case R.id.ll_help4:
                clickPlay(HttpUrl.SETUP_KEY);
                break;
            case R.id.ll_help5:
                clickPlay(HttpUrl.BLUETOOTH_SPEAKER);
                break;
        }
    }

    private void clickPlay(String url) {
        boolean isWifi = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
        boolean wifi   = CommUtils.isWifi(getApplicationContext());
        if(!wifi && isWifi){
            showPlayTip(url);
        }else{
            Intent it  = new Intent(Intent.ACTION_VIEW);
            Uri    uri = Uri.parse(url);
            it.setDataAndType(uri, "video/mp4");
            startActivity(it);
        }
    }

    private void showPlayTip(final String url) {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.not_wifi_conn_play))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      Intent it  = new Intent(Intent.ACTION_VIEW);
                                      Uri    uri = Uri.parse(url);
                                      it.setDataAndType(uri, "video/mp4");
                                      startActivity(it);
                                  }
                              })
                              .show();

    }

    /**
     * 进入视频详情帮助
     */
    private void enterDetail(MainHelpData helpBean)
    {
        Intent intent = new Intent(MainHelpActivity.this, VideoDetailHelpActivity.class);
        intent.putExtra(VideoDetailHelpActivity.EXTRA_HELP, helpBean);
        startActivity(intent);
    }


}
