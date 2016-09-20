package com.tylz.aelos.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnRobotActivity
        extends BaseActivity
        implements SinVoicePlayer.Listener, SinVoiceRecognition.Listener,
                   CompoundButton.OnCheckedChangeListener
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
    @Bind(R.id.cb_select)
    CheckBox mCbSelect;
    @Bind(R.id.iv_select)
    ImageButton mIbSelect;
    String mWifeName;

    static {
        System.loadLibrary("sinvoice");
    }

    @Bind(R.id.iv_right)
    ImageButton mIvRight;


    private SinVoicePlayer      mSinVoicePlayer;
    private SinVoiceRecognition mSinVoiceRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn_robot);
        ButterKnife.bind(this);
        mCbSelect.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mIvRight.setImageResource(R.mipmap.help_play);
        //默认选中wifi配置
        mTvTitle.setText(R.string.voice_control);
        mBtnWifiConfig.setBackgroundColor(Color.rgb(2, 177, 230));
        mBtnWifiHelp.setBackgroundColor(Color.rgb(193, 193, 193));
        mIvRight.setVisibility(View.GONE);
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
            String pwd = mSpUtils.getString(mWifeName, "");
            LogUtils.d("wifiname = " + mWifeName + "---pwd =" + pwd);
            mEtWifipassword.setText(pwd);
        }
        mEtWifipassword.setSelection(mEtWifipassword.getText().length());
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
              R.id.iv_left,
             R.id.iv_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_config:
                mBtnWifiConfig.setBackgroundColor(Color.rgb(2, 177, 230));
                mBtnWifiHelp.setBackgroundColor(Color.rgb(193, 193, 193));
                mLlConfig.setVisibility(View.VISIBLE);
                mLlHelp.setVisibility(View.GONE);
                mIvRight.setVisibility(View.GONE);
                break;
            case R.id.btn_wifi_help:
                mBtnWifiConfig.setBackgroundColor(Color.rgb(193, 193, 193));
                mBtnWifiHelp.setBackgroundColor(Color.rgb(2, 177, 230));
                mLlConfig.setVisibility(View.GONE);
                mLlHelp.setVisibility(View.VISIBLE);
                mIvRight.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_next:
                processClick();
                break;
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_select:
                select();
                break;
        }
    }
    private boolean isSelect = false;
    private void select() {
        isSelect = !isSelect;
        if(isSelect){
            //明文显示
            mEtWifipassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            mEtWifipassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mIbSelect.setImageResource(isSelect?R.mipmap.eye_open:R.mipmap.eye_close);
        //保证每次切换光标都在最后面
        mEtWifipassword.setSelection(mEtWifipassword.getText().length());

    }

    /**
     * 设置无wifi密码配置
     */
    private void setNotWifiPwd() {
        //        WifiConfiguration config = new WifiConfiguration();
        //        config.allowedAuthAlgorithms.clear();
        //        config.allowedGroupCiphers.clear();
        //        config.allowedKeyManagement.clear();
        //        config.allowedPairwiseCiphers.clear();
        //        config.allowedProtocols.clear();
        //        config.SSID = "\"" + getWifissid() + "\"";
        //        // 没有密码
        //        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //        wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
    }

    /**
     * 处理点击事件
     */
    private void processClick() {
        String btnName = mBtNext.getText()
                                .toString();
        String password = mEtWifipassword.getText()
                                         .toString();
        String wifiname = mTvWifiname.getText()
                                     .toString();
        if (TextUtils.isEmpty(wifiname)) {
            mToastor.getSingletonToast(R.string.empty_wifi_name)
                    .show();
            return;
        }
        String name = UIUtils.getString(R.string.not_wifi_conn);

        if (name.equals(wifiname)) {
            mToastor.getSingletonToast(R.string.not_wifi)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            // setNotWifiPwd();
            mToastor.getSingletonToast(R.string.empty_wifi_pwd)
                    .show();
            return;
        }
        if (btnName.equals(UIUtils.getString(R.string.next))) {
            /*保存用户名和密码*/
            if (!wifiname.equals(UIUtils.getString(R.string.not_wifi_conn))) {
                //组成规则 key = wifiname ;value = wifipassword
                mSpUtils.putString(wifiname, password);
                LogUtils.d("put --wifiname = " + mWifeName + "---pwd =" + password);
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
                              })
                              .show();
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


    @OnClick(R.id.iv_right)
    public void onClick() {
        boolean isWifi = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
        boolean wifi   = CommUtils.isWifi(getApplicationContext());
        if (!wifi && isWifi) {
            showPlayTip();
        } else {
            Intent it  = new Intent(Intent.ACTION_VIEW);
            Uri    uri = Uri.parse(HttpUrl.VOICE_CONTROL);
            it.setDataAndType(uri, "video/mp4");
            startActivity(it);
        }
    }

    private void showPlayTip() {
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
                                      Uri    uri = Uri.parse(HttpUrl.VOICE_CONTROL);
                                      it.setDataAndType(uri, "video/mp4");
                                      startActivity(it);
                                  }
                              })
                              .show();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //明文显示
            mEtWifipassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            mEtWifipassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        //保证每次切换光标都在最后面
        mEtWifipassword.setSelection(mEtWifipassword.getText().length());
    }
}
