package com.tylz.aelos.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.libra.sinvoice.Common;
import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.WifiHelpAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnRobotActivity
        extends BaseActivity
        implements SinVoicePlayer.Listener, SinVoiceRecognition.Listener
{

    @Bind(R.id.btn_wifi_config)
    Button       mBtnWifiConfig;
    @Bind(R.id.btn_wifi_help)
    Button       mBtnWifiHelp;
    @Bind(R.id.tv_wifiname)
    TextView     mTvWifiname;
    @Bind(R.id.et_wifipassword)
    EditText     mEtWifipassword;
    @Bind(R.id.ll_wifi_input)
    LinearLayout mLlWifiInput;
    @Bind(R.id.ll_img)
    LinearLayout mLlImg;
    @Bind(R.id.bt_next)
    Button       mBtNext;
    @Bind(R.id.ll_config)
    LinearLayout mLlConfig;
    @Bind(R.id.ll_help)
    FrameLayout  mLlHelp;

    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.listview)
    ListView    mListview;
    String mWifeName;

    static {
        System.loadLibrary("sinvoice");
    }


    private SinVoicePlayer      mSinVoicePlayer;
    private SinVoiceRecognition mSinVoiceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn_robot);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //默认选中wifi配置
        mTvTitle.setText(R.string.connect_robot);
        mBtnWifiConfig.setBackgroundColor(Color.rgb(2, 177, 230));
        mBtnWifiHelp.setBackgroundColor(Color.rgb(193, 193, 193));
        //设置ListView
        mListview.setAdapter(new WifiHelpAdapter(this));
        mSinVoicePlayer = new SinVoicePlayer();
        mSinVoicePlayer.init(this);
        mSinVoicePlayer.setListener(this);
        mSinVoiceRecognition = new SinVoiceRecognition();
        mSinVoiceRecognition.init(this);
        mSinVoiceRecognition.setListener(this);
        mWifeName = getWifissid();
        if (mWifeName.equals("unknown ssid")) {
            mTvWifiname.setText(R.string.not_wifi_conn);
        } else {
            mTvWifiname.setText(mWifeName);
            String name = mSpUtils.getString(Constants.WIFI_NAME, "");
            if(!TextUtils.isEmpty(name) && name.equals(mWifeName)){
                String pwd = mSpUtils.getString(Constants.WIFI_PWD, "");
                mEtWifipassword.setText(pwd);
            }
        }
    }

    /**
     * 获取wifi信息
     * @return
     */
    public String getWifissid() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo    wifiInfo    = wifiManager.getConnectionInfo();
        String      temp        = wifiInfo.getSSID();
        LogUtils.d(temp);
        return temp.substring(1, temp.length() - 1);
    }

    @OnClick({R.id.btn_wifi_config,
              R.id.bt_next,
              R.id.btn_wifi_help,
              R.id.iv_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_config:
                mBtnWifiConfig.setBackgroundColor(Color.rgb(2, 177, 230));
                mBtnWifiHelp.setBackgroundColor(Color.rgb(193, 193, 193));
                mLlConfig.setVisibility(View.VISIBLE);
                mLlHelp.setVisibility(View.GONE);
                break;
            case R.id.btn_wifi_help:
                mBtnWifiConfig.setBackgroundColor(Color.rgb(193, 193, 193));
                mBtnWifiHelp.setBackgroundColor(Color.rgb(2, 177, 230));
                mLlConfig.setVisibility(View.GONE);
                mLlHelp.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_next:
                processClick();
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }
    private void setNotWifiPwd(){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + getWifissid() + "\"";
        // 没有密码
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
    }
    /**
     * 处理点击事件
     */
    private void processClick() {
        String btnName = mBtNext.getText()
                                .toString();
        String password = mEtWifipassword.getText()
                                  .toString();
        String wifiname = mTvWifiname.getText().toString();
        if(TextUtils.isEmpty(password)){
            setNotWifiPwd();
        }
        if (btnName.equals(UIUtils.getString(R.string.next))) {
            /*保存用户名和密码*/
            if(!wifiname.equals(UIUtils.getString(R.string.not_wifi_conn))){
                mSpUtils.putString(Constants.WIFI_NAME,wifiname);
                mSpUtils.putString(Constants.WIFI_PWD,password);
            }

            mLlImg.setVisibility(View.VISIBLE);
            mLlWifiInput.setVisibility(View.GONE);
            showSendVoiceTip();

        } else if (btnName.equals(UIUtils.getString(R.string.confirm))) {
            finish();
        } else if (btnName.equals(UIUtils.getString(R.string.retry))) {
            sendVoice();
        }
    }

    private void showSendVoiceTip() {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setCancelable(false)
                              .setMsg(UIUtils.getString(R.string.tip_connect_robot))
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      sendVoice();
                                  }
                              }).show();
    }

    /**
     * 发送声波
     */
    private void sendVoice() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtNext.setText(R.string.retry);
            }
        }, Constants.WAITING_CONN_ROBOT_TIME);
        mBtNext.setText(R.string.confirm);
        String wifiStr = mWifeName + "&" + mEtWifipassword.getText()
                                                          .toString();
        try {
            byte[] wifiStrBytes = wifiStr.getBytes("UTF-8");
            if (null != wifiStrBytes) {
                int   length          = wifiStrBytes.length;
                int[] tokens          = new int[length];
                int   maxEncoderIndex = mSinVoicePlayer.getMaxEncoderIndex();
                for (int i = 0; i < length; ++i) {
                    if (maxEncoderIndex < 255) {
                        tokens[i] = Common.DEFAULT_CODE_BOOK.indexOf(wifiStr.charAt(i));
                    } else {
                        tokens[i] = wifiStrBytes[i];
                    }
                }
                mSinVoicePlayer.play(tokens, length, false, 2000);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSinVoicePlayStart() {

    }

    @Override
    public void onSinVoicePlayEnd() {

    }

    @Override
    public void onSinToken(int[] tokens) {

    }

    @Override
    public void onSinVoiceRecognitionStart() {

    }

    @Override
    public void onSinVoiceRecognition(char ch) {

    }

    @Override
    public void onSinVoiceRecognitionEnd(int result) {

    }


}
