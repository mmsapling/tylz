package com.tylz.aelos.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.CacheClearUtils;
import com.tylz.aelos.util.LogUtils;
import com.zcw.togglebutton.ToggleButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   AboutActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:19
 *  @描述：    设置界面
 */
public class SettingActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton    mIvLeft;
    @Bind(R.id.tv_title)
    TextView       mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton    mIvRight;
    @Bind(R.id.tv_cache)
    TextView       mTvCache;
    @Bind(R.id.tb_toggle_wifi)
    ToggleButton   mTbToggleWifi;
    @Bind(R.id.tb_toggle_msg)
    ToggleButton   mTbToggleMsg;
    @Bind(R.id.rl_pwd_safe)
    RelativeLayout mRlPwdSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
        toggle();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTvTitle.setText(R.string.setting);
        mTvCache.setText(getCacheSize2Str());
        /*默认是wifi环境下播放*/
        boolean is_wifi_play = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
        if (is_wifi_play) {
            mTbToggleWifi.setToggleOn();
        } else {
            mTbToggleWifi.setToggleOff();
        }
        /*默认消息提醒*/
        boolean is_notification = mSpUtils.getBoolean(Constants.IS_NOTIFICATION, true);
        if (is_notification) {
            mTbToggleMsg.setToggleOn();
        } else {
            mTbToggleMsg.setToggleOff();
        }
    }

    /**
     * 得到缓存大小
     * @return 返回缓存大小字符串
     */
    private String getCacheSize2Str() {
        String cache = "0.0B";
        try {
            cache = CacheClearUtils.getTotalCacheSize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    @OnClick({R.id.iv_left,
              R.id.tv_cache,
             R.id.rl_pwd_safe})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_cache:
                clearCache();
                break;
            case R.id.rl_pwd_safe:
                if(isLogin()){
                    skipActivity(EditActivity.class);
                }else{
                    showLoginTip();
                }
                break;

        }
    }

    private void toggle() {
        mTbToggleWifi.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                LogUtils.d("on = " + on);
                    /*
                     * 1.true 为仅wifi网络下载，写入到sp
                     * 2.false 不是wifi网络也可以下载，写入到sp
                     * */
                mSpUtils.putBoolean(Constants.IS_DOWNLOAD_WIFI, on);
            }
        });
        mTbToggleMsg.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                /** 1.true 消息提醒，写入到sp
                 * 2.false 消息不提醒，写入到sp
                 * */
                mSpUtils.putBoolean(Constants.IS_NOTIFICATION, on);
                if(!on){
                    JPushInterface.setSilenceTime(SettingActivity.this,0,0,23,59);
                }else{
                    JPushInterface.setSilenceTime(SettingActivity.this,0,0,0,0);
                }
            }
        });
    }

    /**
     * 清理缓存
     */
    private void clearCache() {

        CacheClearUtils.clearAllCache(this);
        mToastor.getSingletonToast(R.string.success_clear_cache).show();
        mTvCache.setText(getCacheSize2Str());
    }
}
