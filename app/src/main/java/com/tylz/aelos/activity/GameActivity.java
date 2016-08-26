package com.tylz.aelos.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.db.ISql;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.MusicPlayerUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   GameActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/25 15:51
 *  @描述：    遥控器界面
 */
public class GameActivity
        extends BaseActivity
        implements View.OnTouchListener
{
    /** 同步目录指令*/
    private static final String SYNCHRONIZE_DIRS            = "9000000000000000";
    private static final String TYPE_CUSTOM                 = "type_custom";
    private static final String TYPE_SETTING                = "type_setting";
    /**是否同步完成*/
    private static final int    WHAT_FINISH_SYNCHRONIZATION = 1;
    /** 读取蓝牙信号*/
    private static final int    WHAT_READ_RSSI              = 2;
    private static final int    WHAT_FINISH_CHANGE          = 3;
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.ib_setting)
    ImageButton mIbSetting;
    @Bind(R.id.ib_custom)
    ImageButton mIbCustom;
    @Bind(R.id.iv_rssi)
    ImageView   mIvRssi;
    @Bind(R.id.ib_left_turn)
    ImageButton mIbLeftTurn;
    @Bind(R.id.ib_top)
    ImageButton mIbTop;
    @Bind(R.id.ib_right_turn)
    ImageButton mIbRightTurn;
    @Bind(R.id.ib_left)
    ImageButton mIbLeft;
    @Bind(R.id.ib_bottom)
    ImageButton mIbBottom;
    @Bind(R.id.ib_right)
    ImageButton mIbRight;
    @Bind(R.id.ib_quick)
    ImageButton mIbQuick;
    @Bind(R.id.ib_stop)
    ImageButton mIbStop;
    @Bind(R.id.ib_tumbler)
    ImageButton mIbTumbler;
    @Bind(R.id.ib_menu_6)
    ImageButton mIbMenu6;
    @Bind(R.id.ib_menu_1)
    ImageButton mIbMenu1;
    @Bind(R.id.ib_menu_2)
    ImageButton mIbMenu2;
    @Bind(R.id.ib_menu_5)
    ImageButton mIbMenu5;
    @Bind(R.id.ib_menu_4)
    ImageButton mIbMenu4;
    @Bind(R.id.ib_menu_3)
    ImageButton mIbMenu3;
    private int mGameType = 1; // 1 正常，2,快走模式
    private ConnectBroadcastReceiver mReceiver;
    public  IBluetooth               mIBluetooth;
    public  BlueServiceConnection    mBlueServiceConnection;
    /** 下位机目录 */
    private String mRobotDirectory = "";
    private ReadRssiTask     mReadRssiTask;
    private DbHelper         mDbHelper;
    private MusicPlayerUtils mPlayerUtils;
    /** 自定义动作 custom  setting*/
    private String           mEnterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        init();
        initTouchListener();

    }

    private void initTouchListener() {
        mIbLeftTurn.setOnTouchListener(this);
        mIbTop.setOnTouchListener(this);
        mIbRightTurn.setOnTouchListener(this);
        mIbLeft.setOnTouchListener(this);
        mIbRight.setOnTouchListener(this);
        mIbBottom.setOnTouchListener(this);
    }

    private void init() {

        mDbHelper = new DbHelper(this);
        mPlayerUtils = new MusicPlayerUtils();
        mTvTitle.setText(R.string.remote_control);
        Intent service = new Intent(this, BlueService.class);
        startService(service);
        mBlueServiceConnection = new BlueServiceConnection();
        bindService(service, mBlueServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_CONNECTED);
        intentFilter.addAction(BlueService.ACTION_RETURN_RSSI);
        intentFilter.addAction(BlueService.ACTION_RETURN_DATA);
        registerReceiver(mReceiver, intentFilter);
        if (mReadRssiTask == null) {
            mReadRssiTask = new ReadRssiTask();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRobotDirectory = "";
        if (mIBluetooth != null) {
            /*切换为离线模式*/
            mIBluetooth.callWrite("cc");
        }
    }

    @OnClick({R.id.iv_left,
              R.id.ib_setting,
              R.id.ib_custom,
              R.id.ib_quick,
              R.id.ib_stop,
              R.id.ib_tumbler,
              R.id.ib_menu_6,
              R.id.ib_menu_1,
              R.id.ib_menu_2,
              R.id.ib_menu_5,
              R.id.ib_menu_4,
              R.id.ib_menu_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_setting:
                if (!isPlaying()) {
                    showProgress();
                    mIBluetooth.callWrite("da");
                    SystemClock.sleep(Constants.SEND_SLEEP_TIME_SHORT);
                    mEnterType = TYPE_SETTING;
                    mIBluetooth.callWrite("83");
                    mHandler.sendEmptyMessageDelayed(WHAT_FINISH_SYNCHRONIZATION,
                                                     Constants.SYNCHRONIZATION_WAITING_TIME);
                }
                break;
            case R.id.ib_custom:
                if (!isPlaying()) {
                    showProgress();
                    mIBluetooth.callWrite("da");
                    SystemClock.sleep(Constants.SEND_SLEEP_TIME_SHORT);
                    mEnterType = TYPE_CUSTOM;
                    mIBluetooth.callWrite("83");
                    mHandler.sendEmptyMessageDelayed(WHAT_FINISH_CHANGE,
                                                     Constants.SYNCHRONIZATION_WAITING_TIME);
                }
                break;
            case R.id.iv_left:
                skipMainUI();
                break;
            case R.id.ib_quick:
                if (mGameType == 1) {
                    setQuickMode();
                } else {
                    setNormalMode();
                }
                break;
            case R.id.ib_stop:
                mIBluetooth.callWrite("da");
                mPlayerUtils.clear();
                mToastor.getSingletonToast(R.string.stop_action);
                break;
            case R.id.ib_tumbler:
                openTumblerMode();
                break;
            case R.id.ib_menu_6:
                startAction(ISql.T_Key_Setting.C6);
                break;
            case R.id.ib_menu_1:
                startAction(ISql.T_Key_Setting.C1);
                break;
            case R.id.ib_menu_2:
                startAction(ISql.T_Key_Setting.C2);
                break;
            case R.id.ib_menu_5:
                startAction(ISql.T_Key_Setting.C5);
                break;
            case R.id.ib_menu_4:
                startAction(ISql.T_Key_Setting.C4);
                break;
            case R.id.ib_menu_3:
                startAction(ISql.T_Key_Setting.C3);
                break;
        }
        /*
            1.点击相关逻辑，如果点的不是不倒翁模式，那么如果当前是不倒翁模式，设置为不倒翁模式正常图片。
         */
        if (view.getId() != R.id.ib_tumbler) {
            setNormalTumbler();
        }
    }

    /**
     * 判断当前是否在做按键动作
     * @return
     *      正在播放，返回true，并toast
     */
    private boolean isPlaying() {
        if (mPlayerUtils.isPlaying()) {
            mToastor.getSingletonToast(R.string.current_playing_action).show();
            return true;
        }
        return false;
    }

    /**
     * 开始做动作
     * @param key
     *        按键
     */
    private void startAction(final String key) {
        if (isPlaying()) {
            return;
        }
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final String titles     = mDbHelper.findTitleStrByKey(key);
                                      String       notSetting = UIUtils.getString(R.string.not_setting);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              mToastor.getSingletonToast(titles).show();
                                          }
                                      });
                                      if (!titles.equals(notSetting)) {
                                          List<String> musics = mDbHelper.findKeySettingMusic(key);
                                          mIBluetooth.callWrite(key);
                                          mPlayerUtils.setMusics(musics);
                                          mPlayerUtils.play();
                                      }

                                  }
                              });


    }

    /**
     * 开启不倒翁模式
     * 弹出提示
     * 设置不倒翁图片
     */
    private void openTumblerMode() {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.tip_open_tumbler))
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      mIbTumbler.setImageResource(R.mipmap.tumbler_on);
                                      mIBluetooth.callWrite("db");
                                      mToastor.getSingletonToast(R.string.open_tumbler_mode).show();
                                  }
                              })
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      //空实现
                                  }
                              })
                              .show();
    }

    /**
     * 跳转到主页
     */
    private void skipMainUI() {
        //可能会做逻辑
        finish();
    }


    /**
     *  切换为正常模式图片
     *  gameType = 1;
     * 弹出Toast说明
     */
    private void setNormalMode() {
        mGameType = 1;
        mIbQuick.setImageResource(R.mipmap.kuaizou);
        CommomUtil.showToast(R.string.switch_normal_mode);
    }

    /**
     * 设置为不倒翁模式正常图片
     */
    private void setNormalTumbler() {
        mIbTumbler.setImageResource(R.mipmap.tumbler);
    }

    /**
     * 弹出提示
     * 切换为快走模式图片
     * gameType = 2;
     * 弹出Toast说明
     */
    private void setQuickMode() {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.tip_quick_mode))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      mGameType = 2;
                                      mToastor.getSingletonToast(R.string.switch_quick_mode).show();
                                      mIbQuick.setImageResource(R.mipmap.kuaizou_on);
                                  }
                              })
                              .show();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.ib_left_turn:
                    mIBluetooth.callWrite("d5");
                    mToastor.getSingletonToast(R.string.left_turn).show();
                    break;
                case R.id.ib_top:
                    if (mGameType == 1) {
                        mIBluetooth.callWrite("d1");
                    } else {
                        mIBluetooth.callWrite("d7");
                    }
                    mToastor.getSingletonToast(R.string.advance).show();
                    break;
                case R.id.ib_right_turn:
                    mIBluetooth.callWrite("d6");
                    mToastor.getSingletonToast(R.string.right_turn).show();
                    break;
                case R.id.ib_left:
                    mIBluetooth.callWrite("d3");
                    mToastor.getSingletonToast(R.string.turn_left).show();
                    break;
                case R.id.ib_bottom:
                    if (mGameType == 1) {
                        mIBluetooth.callWrite("d2");
                    } else {
                        mIBluetooth.callWrite("d8");
                    }
                    mToastor.getSingletonToast(R.string.back_coming).show();
                    break;
                case R.id.ib_right:
                    mIBluetooth.callWrite("d4");
                    mToastor.getSingletonToast(R.string.turn_right).show();
                    break;
                default:
                    break;
            }
            setNormalTumbler();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mIBluetooth.callWrite("da");
        }
        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case WHAT_FINISH_SYNCHRONIZATION:
                    /*如果目录同步失败*/
                    if (!isRobotDirectory(mRobotDirectory)) {
                        closeProgress();
                        mIBluetooth.callWrite("cc");
                        mToastor.getSingletonToast(R.string.fail_synchronization).show();
                        mRobotDirectory = "";
                    }
                    break;
                case WHAT_FINISH_CHANGE:
                    closeProgress();
                    mIBluetooth.callWrite("cc");
                    mToastor.getSingletonToast(R.string.fail_online).show();
                    break;
            }
        }
    };

    /**
     * 读取信号任务
     */
    private class ReadRssiTask
            implements Runnable
    {
        public void start() {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, Constants.READ_RSSI_TIME);
        }

        public void stop() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            if (mIBluetooth != null) {
                mIBluetooth.callReadRssi();
            }
            start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIBluetooth != null) {
            if (mIBluetooth.callIsConnected()) {
                mReadRssiTask.stop();
                mReadRssiTask.start();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mReadRssiTask.stop();
    }

    private void initRobot() {
        showProgress();
        //进入在线模式成功，发送同步目录请求
        mIBluetooth.callWrite(SYNCHRONIZE_DIRS);// 同步目录
    }

    /**
     * 蓝牙连接相关广播接受者
     */
    private class ConnectBroadcastReceiver
            extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BlueService.ACTION_UNCONNECT:
                    mPlayerUtils.clear();
                    skipScanUI();
                    break;
                case BlueService.ACTION_RETURN_DATA:
                    closeProgress();
                    String data = intent.getStringExtra(BlueService.EXTRA_DATA);
                    String flag = intent.getStringExtra(BlueService.EXTRA_FLAG);
                    if (flag.equals("83") && data.contains("83") && mEnterType.equals(TYPE_SETTING)) {
                        initRobot();
                    } else if (flag.equals("83") && data.contains("83") && mEnterType.equals(
                            TYPE_CUSTOM))
                    {
                        mHandler.removeMessages(WHAT_FINISH_CHANGE);
                        closeProgress();
                        skipActivity(CustomActionActivity.class);
                    } else if (flag.equals(SYNCHRONIZE_DIRS) && !flag.equals(data)) {
                        mRobotDirectory += data;
                        boolean isSuccess = isRobotDirectory(mRobotDirectory);
                        if (isSuccess) {
                            mHandler.removeMessages(WHAT_FINISH_SYNCHRONIZATION);
                            Intent gameSettingIntent = new Intent(GameActivity.this,
                                                                  GameSettingActivity.class);
                            gameSettingIntent.putExtra(GameSettingActivity.EXTRA_DATA,
                                                       mRobotDirectory);
                            startActivity(gameSettingIntent);
                        }
                    }
                    break;
                case BlueService.ACTION_RETURN_RSSI:
                    int rssi = intent.getIntExtra(BlueService.EXTRA_RSSI, 0);
                    changeRssi(rssi);
                    break;
                default:
                    break;
            }

        }

    }

    /**
     * 改变信号ui
     * @param rssi 信号
     */
    private void changeRssi(int rssi) {
        rssi = Math.abs(rssi);
        if (rssi >= 100) {
            mIvRssi.setImageResource(R.mipmap.rssi10);
        } else if (rssi >= 90 && rssi < 100) {
            mIvRssi.setImageResource(R.mipmap.rssi20);
        } else if (rssi >= 78 && rssi < 90) {
            mIvRssi.setImageResource(R.mipmap.rssi30);
        } else if (rssi >= 75 && rssi < 78) {
            mIvRssi.setImageResource(R.mipmap.rssi40);
        } else if (rssi >= 72 && rssi < 75) {
            mIvRssi.setImageResource(R.mipmap.rssi50);
        } else if (rssi >= 69 && rssi < 72) {
            mIvRssi.setImageResource(R.mipmap.rssi60);
        } else if (rssi >= 66 && rssi < 69) {
            mIvRssi.setImageResource(R.mipmap.rssi70);
        } else if (rssi >= 63 && rssi < 66) {
            mIvRssi.setImageResource(R.mipmap.rssi80);
        } else if (rssi >= 60 && rssi < 63) {
            mIvRssi.setImageResource(R.mipmap.rssi90);
        } else if (rssi < 60) {
            mIvRssi.setImageResource(R.mipmap.rssi100);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
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
     * 判断目录是否同步成功
     * @param robotDirectory
     */
    private boolean isRobotDirectory(String robotDirectory) {
        if (robotDirectory.length() > 15) {
            String lenStr = mRobotDirectory.substring(8, 12);
            int    len    = Integer.parseInt(lenStr, 16);
            int    dirLen = len * 2 + 16;
            if (dirLen == robotDirectory.length()) {
                closeProgress();
                //目录同步成功
                return true;
            }
        }
        return false;
    }

    /**
     * 检查蓝牙连接是否成功，成功进入离线模式，反之跳转扫描界面
     */
    private void checkConnect() {
        if (mIBluetooth.callIsConnected()) {
            //设置为离线模式
            mIBluetooth.callWrite("cc");
            if (mReadRssiTask != null) {
                mReadRssiTask.start();
            }
            boolean is_first = mSpUtils.getBoolean(Constants.IS_FIRST_REMOTE_CONTROL, true);
            if (is_first) {
                skipActivity(GuideGameActivity.class);
            }
        } else {
            skipScanUI();
        }
    }
}
