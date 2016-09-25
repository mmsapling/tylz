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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.adapter.GameSettingAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.KeySetting;
import com.tylz.aelos.bean.SettingTypeData;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.db.ISql;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;
import com.tylz.aelos.view.DNumProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   GameSettingActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/14 15:07
 *  @描述：    配置界面
 */
public class GameSettingActivity
        extends BaseActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    public static final  String EXTRA_DATA            = "extra_data";
    /** 下载动作*/
    private static final String DOWNLOAD_TYPE_ACTION  = "download_type_action";
    /** 配置动作*/
    private static final String DOWNLOAD_TYPE_SETTING = "download_type_setting";
    /*下载请求指令*/
    private static final String CODE_DOWNLOAD_REQUEST = "8400000000000000";
    /** 下载动作*/
    private static final int    WHAT_IBLUETOOTH       = 0;
    private static final int    WHAT_DOWNLOAD_ACTION  = 1;
    private static final int    WHAT_DOWNLOAD_SUCCESS = 2;
    private static final int    WHAT_DOWNLOAD_FAIL    = 3;
    private static final int    WHAT_DOWLOAD_PROGRESS = 4;
    private static final int    WHAT_ROBOT_BUZY       = 5;
    private static final int    WHAT_SETTING_FAIL     = 6;
    private static final int    WHAT_SETTING_SUCCESS  = 7;
    private static final int    WHAT_SETTING_ACTION   = 8;
    private static final int    WHAT_DOWNLOAD_EMPTY   = 9;
    private static final int    WHAT_SETTING_EMPTY    = 10;

    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.tv_right)
    TextView    mTvRight;
    @Bind(R.id.iv_key1)
    ImageView   mIvKey1;
    @Bind(R.id.iv_key2)
    ImageView   mIvKey2;
    @Bind(R.id.iv_key3)
    ImageView   mIvKey3;
    @Bind(R.id.iv_key4)
    ImageView   mIvKey4;
    @Bind(R.id.iv_key5)
    ImageView   mIvKey5;
    @Bind(R.id.iv_key6)
    ImageView   mIvKey6;
    @Bind(R.id.iv_guide1)
    ImageView   mIvGuide1;
    @Bind(R.id.iv_guide2)
    ImageView   mIvGuide2;
    @Bind(R.id.iv_guide3)
    ImageView   mIvGuide3;
    @Bind(R.id.iv_guide4)
    ImageView   mIvGuide4;
    @Bind(R.id.iv_guide5)
    ImageView   mIvGuide5;
    @Bind(R.id.iv_guide6)
    ImageView   mIvGuide6;
    @Bind(R.id.tv_type1_base)
    TextView    mTvType1Base;
    @Bind(R.id.tv_type2_music)
    TextView    mTvType2Music;
    @Bind(R.id.tv_type3_fable)
    TextView    mTvType3Fable;
    @Bind(R.id.tv_type4_football)
    TextView    mTvType4Football;
    @Bind(R.id.tv_type5_boxing)
    TextView    mTvType5Boxing;
    @Bind(R.id.tv_type6_custom)
    TextView    mTvType6Custom;
    @Bind(R.id.gridview)
    GridView    mGridview;
    private IBluetooth               mIBluetooth;
    private ConnectBroadcastReceiver mReceiver;
    private BlueServiceConnection    mServiceConnection;

    private DbHelper              mDbHelper;
    /*每个按键中的动作*/
    private List<String>          mkey1;
    private List<String>          mkey2;
    private List<String>          mkey3;
    private List<String>          mkey4;
    private List<String>          mkey5;
    private List<String>          mkey6;
    private List<SettingTypeData> mTypeDatas;
    private GameSettingAdapter    mAdapter;
    private int                   mCurrentKeyPosition;
    private String                mRobotDirectory;
    private String                mBaseAction;
    private String                mMusicDance;
    private String                mFable;
    private String                mFootball;
    private String                mBoxing;
    private String                mCustom;
    /** 下载或配置缓存数据*/
    private List<String>          mTempData;
    /** 下载缓存数据*/
    private SettingTypeData       mTempTypeData;
    /**进度条*/
    private DNumProgressDialog    mNumProgressDialog;
    /**广播返回数据*/
    private String                mReturnData;
    private Timer                 mTimer;
    /** false,表示 正常状态， true，表示有图标状态*/
    private boolean               mType1Status;
    private boolean               mType2Status;
    private boolean               mType3Status;
    private boolean               mType4Status;
    private boolean               mType5Status;
    private boolean               mType6Status;
    /**是否初始化key集合*/
    private boolean               isInitKey;
    /** 写入的下标*/
    private int                   mWriteIndx;
    /** 下载超时*/
    private int                   mOutOfTime;
    private int                   m88Count;
    /** 默认下载模式*/
    private String                mDefaultDowload;
    private List<String>          mTempDeleteDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);
        ButterKnife.bind(this);
        /*绑定蓝牙服务 及注册广播接受者*/
        init();
        initAllType();
        initCurrentKeyPress();
    }

    /**
     * 动作类型对应的textview
     * @param type 动作类型
     * @return textview
     */
    private TextView type2tv(String type) {
        TextView tv = mTvType1Base;
        switch (type) {
            case "基础动作":
                tv = mTvType1Base;
                break;
            case "音乐舞蹈":
                tv = mTvType2Music;
                break;
            case "寓言故事":
                tv = mTvType3Fable;
                break;
            case "足球":
                tv = mTvType4Football;
                break;
            case "拳击":
                tv = mTvType5Boxing;
                break;
            case "自定义":
                tv = mTvType6Custom;
                break;
        }
        return tv;
    }

    /**
     * 初始化动作类型集合
     * @param type 动作类型
     */
    private void initType(final String type) {
        showProgress();

        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      List<SettingTypeData> typeDatas = procesData(type);

                                      mTypeDatas.clear();
                                      mTypeDatas.addAll(typeDatas);
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mAdapter.notifyDataSetChanged();
                                              initCurrentTypePress(type2tv(type));
                                          }
                                      });
                                  }
                              });


    }

    /**
     * 处理类型数据逻辑
     * @param type 动作类型
     * @return 返回此类型的所有数据
     */
    private List<SettingTypeData> procesData(String type) {
        if (!isInitKey) {
            initKeyList();
            isInitKey = true;
        }
        List<SettingTypeData> typeDatas = mDbHelper.findSettingTypeDataByType(type);
         /*过滤掉已删除的元素*/
        ListIterator<SettingTypeData> typeDataListIterator = typeDatas.listIterator();
        while (typeDataListIterator.hasNext()) {
            SettingTypeData next = typeDataListIterator.next();
            for (String titlestream : mTempDeleteDatas) {
                if (next.titlestream.equals(titlestream)) {
                    LogUtils.d("删除 = " + next.title);
                    typeDataListIterator.remove();
                }
            }
        }
        /*重置标志*/
        for (SettingTypeData data : typeDatas) {
            String substring = data.titlestream.substring(2);
            if (mRobotDirectory.contains(substring)) {
                data.isFlag = 1;
            }
            /*判断动作有没有被配置*/
            switch (mCurrentKeyPosition) {
                case 1:
                    if (mkey1.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
                case 2:
                    if (mkey2.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
                case 3:
                    if (mkey3.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
                case 4:
                    if (mkey4.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
                case 5:
                    if (mkey5.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
                case 6:
                    if (mkey6.contains(data.titlestream) && mRobotDirectory.contains(substring)) {
                        data.isFlag = 2;
                    }
                    break;
            }
        }
        changeType(type, typeDatas);
        return typeDatas;
    }

    /** 改变类型*/
    private void changeType(String type, List<SettingTypeData> typeDatas) {
        switch (mCurrentKeyPosition) {
            case 1:
                changeTypeStatus(type, mkey1, typeDatas);
                break;
            case 2:
                changeTypeStatus(type, mkey2, typeDatas);
                break;
            case 3:
                changeTypeStatus(type, mkey3, typeDatas);
                break;
            case 4:
                changeTypeStatus(type, mkey4, typeDatas);
                break;
            case 5:
                changeTypeStatus(type, mkey5, typeDatas);
                break;
            case 6:
                changeTypeStatus(type, mkey6, typeDatas);
                break;
        }
    }

    /**
     * 改变类型状态
     * @param type  动作类型
     */
    private void changeTypeStatus(String type, List<String> keys, List<SettingTypeData> typeDatas) {
        for (SettingTypeData settingTypeData : typeDatas) {
            switch (type) {
                case "基础动作":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType1Status = true;
                        return;
                    } else {
                        mType1Status = false;
                    }
                    break;
                case "音乐舞蹈":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType2Status = true;
                        return;
                    } else {
                        mType2Status = false;
                    }
                    break;
                case "寓言故事":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType3Status = true;
                        return;
                    } else {
                        mType3Status = false;
                    }
                    break;
                case "足球":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType4Status = true;
                        return;
                    } else {
                        mType4Status = false;
                    }
                    break;
                case "拳击":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType5Status = true;
                        return;
                    } else {
                        mType5Status = false;
                    }
                    break;
                case "自定义":
                    if (keys.contains(settingTypeData.titlestream)) {
                        mType6Status = true;
                        return;
                    } else {
                        mType6Status = false;
                    }
                    break;
            }
        }
    }

    /*绑定蓝牙服务 及注册广播接受者*/
    private void init() {
        mTempDeleteDatas = new ArrayList<>();
        mDefaultDowload = DOWNLOAD_TYPE_ACTION;
        mCurrentKeyPosition = 1;
        mRobotDirectory = "";
        mTvTitle.setText(R.string.set_key_function);
        mTvRight.setText(R.string.save);
        mRobotDirectory = getIntent().getStringExtra(EXTRA_DATA);
        mDbHelper = new DbHelper(this);
        mTypeDatas = new ArrayList<>();
        mAdapter = new GameSettingAdapter(this, mTypeDatas);
        mGridview.setAdapter(mAdapter);
        mGridview.setOnItemClickListener(this);
        mGridview.setOnItemLongClickListener(this);
        mkey1 = new ArrayList<>();
        mkey2 = new ArrayList<>();
        mkey3 = new ArrayList<>();
        mkey4 = new ArrayList<>();
        mkey5 = new ArrayList<>();
        mkey6 = new ArrayList<>();
        mType1Status = false;
        mType2Status = false;
        mType3Status = false;
        mType4Status = false;
        mType5Status = false;
        mType6Status = false;
        isInitKey = false;
        mTempData = new ArrayList<>();
        mBaseAction = UIUtils.getString(R.string.base_action);
        mMusicDance = UIUtils.getString(R.string.music_dance);
        mFable = UIUtils.getString(R.string.fable);
        mFootball = UIUtils.getString(R.string.football);
        mBoxing = UIUtils.getString(R.string.boxing);
        mCustom = UIUtils.getString(R.string.custom);
        Intent service = new Intent(this, BlueService.class);
        mServiceConnection = new BlueServiceConnection();
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_CONNECTED);
        intentFilter.addAction(BlueService.ACTION_RETURN_RSSI);
        intentFilter.addAction(BlueService.ACTION_RETURN_DATA);
        registerReceiver(mReceiver, intentFilter);
    }


    /**
     * 初始化key集合
     */
    private void initKeyList() {
        List<KeySetting> keySettings = mDbHelper.findTitleStreamByKeySetting();
        for (KeySetting keySetting : keySettings) {
            if (keySetting.key.equals(ISql.T_Key_Setting.C1)) {
                mkey1.add(keySetting.titlestream);
            } else if (keySetting.key.equals(ISql.T_Key_Setting.C2)) {
                mkey2.add(keySetting.titlestream);
            } else if (keySetting.key.equals(ISql.T_Key_Setting.C3)) {
                mkey3.add(keySetting.titlestream);
            } else if (keySetting.key.equals(ISql.T_Key_Setting.C4)) {
                mkey4.add(keySetting.titlestream);
            } else if (keySetting.key.equals(ISql.T_Key_Setting.C5)) {
                mkey5.add(keySetting.titlestream);
            } else if (keySetting.key.equals(ISql.T_Key_Setting.C6)) {
                mkey6.add(keySetting.titlestream);
            }
        }
    }

    @OnClick({R.id.iv_left,
              R.id.iv_key1,
              R.id.iv_key2,
              R.id.iv_key3,
              R.id.iv_key4,
              R.id.iv_key5,
              R.id.iv_key6,
              R.id.tv_type1_base,
              R.id.tv_type2_music,
              R.id.tv_type3_fable,
              R.id.tv_type4_football,
              R.id.tv_type5_boxing,
              R.id.tv_type6_custom,
              R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                /*
                    如果配置数据为空，那么至删除，不发下载请求
                 */
                if (isSettingDataEmpty()) {
                    deleteAndSaveKeySetting(true,true,true);
                } else {
                     /*发送配置下载请求*/
                    mDownloadType = DOWNLOAD_TYPE_SETTING;
                    mIBluetooth.callWrite(CODE_DOWNLOAD_REQUEST);
                }
                break;
            case R.id.iv_key1:
                mCurrentKeyPosition = 1;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.iv_key2:
                mCurrentKeyPosition = 2;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.iv_key3:
                mCurrentKeyPosition = 3;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.iv_key4:
                mCurrentKeyPosition = 4;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.iv_key5:
                mCurrentKeyPosition = 5;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.iv_key6:
                mCurrentKeyPosition = 6;
                initAllType();
                initCurrentKeyPress();
                break;
            case R.id.tv_type1_base:
                initType(mBaseAction);
                break;
            case R.id.tv_type2_music:
                initType(mMusicDance);
                break;
            case R.id.tv_type3_fable:
                initType(mFable);
                break;
            case R.id.tv_type4_football:
                initType(mFootball);
                break;
            case R.id.tv_type5_boxing:
                initType(mBoxing);
                break;
            case R.id.tv_type6_custom:
                initType(mCustom);
                break;
        }
    }

    /**
     * 判断配置数据是否为空
     * @return
     *      不为为空返回true
     */
    private boolean isSettingDataEmpty() {
        if (mkey1.size() == 0 && mkey2.size() == 0 && mkey3.size() == 0 && mkey4.size() == 0 && mkey5.size() == 0 && mkey6.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 保存动作配置
     */
    private void saveKeySetting() {
        /*
        * 1.对下位机进行配置，配置成功后进入第2步
        * 2.清理键设置
        * 3.对应的键插入到数据库中
        * */
             /*键1配置*/
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      mDbHelper.clearKeySetting();
                                      for (String temp : mkey1) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C1,
                                                                         temp,
                                                                         title);
                                          }
                                      }
             /*键2配置*/
                                      for (String temp : mkey2) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C2,
                                                                         temp,
                                                                         title);
                                          }
                                      }
              /*键3配置*/
                                      for (String temp : mkey3) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C3,
                                                                         temp,
                                                                         title);
                                          }
                                      }
              /*键4配置*/
                                      for (String temp : mkey4) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C4,
                                                                         temp,
                                                                         title);
                                          }
                                      }
              /*键5配置*/
                                      for (String temp : mkey5) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C5,
                                                                         temp,
                                                                         title);
                                          }
                                      }
              /*键6配置*/
                                      for (String temp : mkey6) {
                                          String substring = temp.substring(2);
                                          if (mRobotDirectory.contains(substring)) {
                                              String title = mDbHelper.findTitleByTitleStream(temp);
                                              mDbHelper.insertKeySetting(ISql.T_Key_Setting.C6,
                                                                         temp,
                                                                         title);
                                          }
                                      }
                                  }
                              });

    }

    /**
     * 初始化所有的类型
     * 1.默认选中基础动作
     * 2.选中当前是那个按键
     */
    private void initAllType() {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      procesData(mCustom);
                                      procesData(mBoxing);
                                      procesData(mFootball);
                                      procesData(mFable);
                                      procesData(mMusicDance);
                                      List<SettingTypeData> typeDatas = procesData(mBaseAction);
                                      mTypeDatas.clear();
                                      mTypeDatas.addAll(typeDatas);
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mAdapter.notifyDataSetChanged();
                                              initCurrentTypePress(mTvType1Base);
                                          }
                                      });

                                  }
                              });
    }

    private String mDownloadType = DOWNLOAD_TYPE_ACTION;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTempTypeData = mTypeDatas.get(position);
        /*
        * 0.知道点击的是那个类型
        * 1.如果flag = 0 那么去下载
        * 2.如果flag = 1 那么选中
        * 3.如果flag = 2 那么取消选中
        * */
        if (mTempTypeData.isFlag == 0) {
             /*发送下载请求*/
            mDownloadType = DOWNLOAD_TYPE_ACTION;
            if (TextUtils.isEmpty(mTempTypeData.filestream)) {
                ToastUtils.showToast(R.string.empty_action);
            } else {
                mIBluetooth.callWrite(CODE_DOWNLOAD_REQUEST);
            }
        } else if (mTempTypeData.isFlag == 1) {
            selectAction(mTempTypeData);
        } else if (mTempTypeData.isFlag == 2) {
            cancelSelectAction(mTempTypeData);
        }
    }

    /**
     * 取消选中这个动作
     * @param tempTypeData
     *          类型动作
     */
    private void cancelSelectAction(SettingTypeData tempTypeData)
    {
        /*
        * 1.设置白色背景
        * 2.从对应按键的选中集合中删除
        * 3.通知适配器改变
        * */
        tempTypeData.isFlag = 1;
        switch (mCurrentKeyPosition) {
            case 1:
                mkey1.remove(tempTypeData.titlestream);
                break;
            case 2:
                mkey2.remove(tempTypeData.titlestream);
                break;
            case 3:
                mkey3.remove(tempTypeData.titlestream);
                break;
            case 4:
                mkey4.remove(tempTypeData.titlestream);
                break;
            case 5:
                mkey5.remove(tempTypeData.titlestream);
                break;
            case 6:
                mkey6.remove(tempTypeData.titlestream);
                break;
        }
        initType(tempTypeData.type);
    }

    /**
     * 选中这个动作
     * @param tempTypeData
     *      类型动作
     */
    private void selectAction(SettingTypeData tempTypeData) {
        /*
        * 1.设置蓝色背景
        * 2.添加到对应按键的选中集合中
        * 3.通知适配器改变
        * */
        tempTypeData.isFlag = 2;
        switch (mCurrentKeyPosition) {
            case 1:
                mkey1.add(tempTypeData.titlestream);
                break;
            case 2:
                mkey2.add(tempTypeData.titlestream);
                break;
            case 3:
                mkey3.add(tempTypeData.titlestream);
                break;
            case 4:
                mkey4.add(tempTypeData.titlestream);
                break;
            case 5:
                mkey5.add(tempTypeData.titlestream);
                break;
            case 6:
                mkey6.add(tempTypeData.titlestream);
                break;
        }
        //        mAdapter.notifyDataSetChanged();
        initType(tempTypeData.type);
    }

    public String getLength(int length)
    {
        String datalength = "";
        String str        = String.valueOf(Integer.toHexString(length));
        if (str.length() == 1) {
            datalength = "000" + str + "0000";
        } else if (str.length() == 2) {
            datalength = "00" + str + "0000";
        } else if (str.length() == 3) {
            datalength = "0" + str + "0000";
        }
        return datalength;
    }

    /**
     * 配置动作
     */
    private void downloadSetting() {
        mWriteIndx = 0;
        mOutOfTime = 0;
            /*清空缓存数据*/
        mTempData.clear();
        setSettingData();
        if (mTempData.get(0)
                     .startsWith("00000000000"))
        {
            mHandler.sendEmptyMessage(WHAT_SETTING_EMPTY);
            return;
        }
        showNumProcess();
        mTimer = new Timer();
        TimerTask downloadTask = new TimerTask() {
            @Override
            public void run() {
                if (mWriteIndx < mTempData.size()) {
                    if (mWriteIndx != 0 && mWriteIndx % 16 == 0) {
                        if ("8800000000000000".equals(mReturnData)) {
                            mWriteIndx -= 16;
                            mOutOfTime -= 16;
                            mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                            int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                            Message msg      = Message.obtain();
                            msg.what = WHAT_DOWLOAD_PROGRESS;
                            msg.obj = progress;
                            mHandler.sendMessage(msg);
                            m88Count++;
                            mWriteIndx++;
                            if (m88Count >= 2) {
                                //两次88后退出
                                mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                                mTimer.cancel();
                            }
                        } else if ("8700000000000000".equals(mReturnData)) {
                            mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                            mWriteIndx++;
                            int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                            Message msg      = Message.obtain();
                            msg.what = WHAT_DOWLOAD_PROGRESS;
                            msg.obj = progress;
                            mHandler.sendMessage(msg);
                        } else if (mReturnData.length() == 18) {
                            if (mReturnData.substring(16, 18)
                                           .equals("cc"))
                            {
                                mHandler.sendEmptyMessage(WHAT_SETTING_FAIL);
                                mTimer.cancel();
                            } else if (mReturnData.indexOf("fe") != -1) {
                                mHandler.sendEmptyMessage(WHAT_SETTING_FAIL);
                                mTimer.cancel();
                            }
                        }
                    } else {
                        mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                        mWriteIndx++;
                        int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                        Message msg      = Message.obtain();
                        msg.what = WHAT_DOWLOAD_PROGRESS;
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    if ("8700000000000000".equals(mReturnData)) {
                        mHandler.sendEmptyMessage(WHAT_SETTING_SUCCESS);
                        mTimer.cancel();
                    } else if ("8800000000000000".equals(mReturnData)) {
                        mWriteIndx -= 16;
                        mOutOfTime -= 16;
                        mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                        int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                        Message msg      = Message.obtain();
                        msg.what = WHAT_DOWLOAD_PROGRESS;
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                        m88Count++;
                        mWriteIndx++;
                        if(m88Count >= 2){
                            mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                            mTimer.cancel();
                        }
                    } else if (mReturnData.length() == 18) {
                        if (mReturnData.substring(16, 18)
                                       .equals("cc"))
                        {
                            mHandler.sendEmptyMessage(WHAT_SETTING_FAIL);
                            mTimer.cancel();
                        } else if (mReturnData.indexOf("fe") != -1) {
                            mHandler.sendEmptyMessage(WHAT_SETTING_FAIL);
                            mTimer.cancel();
                        }
                    }
                }
                if (mOutOfTime > mTempData.size() * 2) {
                    mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                    mTimer.cancel();
                }
                mOutOfTime++;
            }
        };
        mTimer.schedule(downloadTask, 20, Constants.DOWNLOAD_WATING_TIME);
    }

    /**
     * 下载动作
     */
    private void downloadAction() {
        mWriteIndx = 0;
        mOutOfTime = 0;
        SettingTypeData typeData = mTempTypeData;
            /*清空缓存数据*/
        mTempData.clear();
        int totalLength = typeData.filestream.length() / 64;
        for (int i = 0; i < totalLength; i++) {
            String temp = typeData.filestream.substring(i * 64, (i + 1) * 64);
            mTempData.add(temp);
        }
        mOutOfTime = 0;
        mWriteIndx = 0;
        m88Count = 0;
        showNumProcess();
        mTimer = new Timer();
        TimerTask downloadTask = new TimerTask() {
            @Override
            public void run() {
                if (mWriteIndx < mTempData.size()) {
                    if (mWriteIndx != 0 && mWriteIndx % 16 == 0) {
                        if ("8800000000000000".equals(mReturnData)) {
                            m88Count++;
                            if (m88Count >= 2) {
                                //两次88后退出
                                LogUtils.d("< 88");
                                mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                                mTimer.cancel();
                            }
                            mWriteIndx -= 16;
                            mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                            int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                            Message msg      = Message.obtain();
                            msg.what = WHAT_DOWLOAD_PROGRESS;
                            msg.obj = progress;
                            mHandler.sendMessage(msg);

                            mWriteIndx++;

                        } else if ("8700000000000000".equals(mReturnData)) {
                            mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                            mWriteIndx++;
                            int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                            Message msg      = Message.obtain();
                            msg.what = WHAT_DOWLOAD_PROGRESS;
                            msg.obj = progress;
                            mHandler.sendMessage(msg);
                        } else if (mReturnData.length() == 18) {
                            if (mReturnData.substring(16, 18)
                                           .equals("cc"))
                            {
                                mHandler.sendEmptyMessage(WHAT_DOWNLOAD_FAIL);
                                mTimer.cancel();
                            } else if (mReturnData.indexOf("fe") != -1) {
                                mHandler.sendEmptyMessage(WHAT_DOWNLOAD_FAIL);
                                mTimer.cancel();
                            }
                        }
                    } else {
                        mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                        mWriteIndx++;
                        int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                        Message msg      = Message.obtain();
                        msg.what = WHAT_DOWLOAD_PROGRESS;
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    if ("8700000000000000".equals(mReturnData)) {
                        mHandler.sendEmptyMessage(WHAT_DOWNLOAD_SUCCESS);
                        mTimer.cancel();
                    } else if ("8800000000000000".equals(mReturnData)) {
                        mWriteIndx -= 16;
                        mIBluetooth.callWrite(mTempData.get(mWriteIndx));
                        int     progress = (int) (100.00 / mTempData.size() * mWriteIndx);
                        Message msg      = Message.obtain();
                        msg.what = WHAT_DOWLOAD_PROGRESS;
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                        m88Count++;
                        mWriteIndx++;
                        if(m88Count >= 2){
                            LogUtils.d("> 88");
                            mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                            mTimer.cancel();
                        }
                    } else if (mReturnData.length() == 18) {
                        if (mReturnData.substring(16, 18)
                                       .equals("cc"))
                        {
                            mHandler.sendEmptyMessage(WHAT_DOWNLOAD_FAIL);
                            mTimer.cancel();
                        } else if (mReturnData.indexOf("fe") != -1) {
                            mHandler.sendEmptyMessage(WHAT_DOWNLOAD_FAIL);
                            mTimer.cancel();
                        }
                    }
                }
                if (mOutOfTime > mTempData.size() + 32 + 32) {
                    LogUtils.d("buzy");
                    mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                    mTimer.cancel();
                }
                mOutOfTime++;
            }
        };
        mTimer.schedule(downloadTask, 20, Constants.DOWNLOAD_WATING_TIME);
    }

    /**
     * 设置配置数据
     */
    private void setSettingData() {
        StringBuffer download = new StringBuffer();
            /*键1配置*/
        for (String temp : mkey1) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC1CCCC" + temp + "DDDD01");
            }
        }
             /*键2配置*/
        for (String temp : mkey2) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC2CCCC" + temp + "DDDD01");
            }
        }
              /*键3配置*/
        for (String temp : mkey3) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC3CCCC" + temp + "DDDD01");
            }
        }
              /*键4配置*/
        for (String temp : mkey4) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC4CCCC" + temp + "DDDD01");
            }
        }
              /*键5配置*/
        for (String temp : mkey5) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC5CCCC" + temp + "DDDD01");
            }
        }
              /*键6配置*/
        for (String temp : mkey6) {
            String substring = temp.substring(2);
            if (mRobotDirectory.contains(substring)) {
                download.append("BBBBC6CCCC" + temp + "DDDD01");
            }
        }

        StringBuffer keydownload = new StringBuffer();
        for (int i = 1; i < (int) Math.ceil((double) download.length() / 988) + 1; i++) {
            if (i == (int) Math.ceil((double) download.length() / 988)) {
                keydownload.append("700" + i + "0001" + getLength(download.length() / 2 - (i - 1) * 494 + 10) + "AAAA076b65792e646174" + download.substring(
                        (i - 1) * 988,
                        download.length()));
            } else {
                keydownload.append("700" + i + "000001F80000AAAA076b65792e646174" + download.substring(
                        (i - 1) * 988,
                        i * 988));
            }
        }
        int          curlength = keydownload.length() % 1024;
        StringBuffer bu        = new StringBuffer();
        for (int i = 0; i < 1024 - curlength; i++) {
            bu.append("0");
        }
        keydownload.append(bu);
        mTempData.clear();
        for (int i = 0; i <= keydownload.length() / 64 - 1; i++) {
            mTempData.add(keydownload.substring(i * 64, (i + 1) * 64));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        SettingTypeData settingTypeData = mTypeDatas.get(position);

        deleteActionFalse(settingTypeData);
        return true;
    }

    /**
     * 删除动作，只是适配器中移除
     * @param settingTypeData
     */
    private void deleteActionFalse(final SettingTypeData settingTypeData) {
        new DAlertDialog(this).builder()
                              .setCancelable(false)
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.tip_delete_action))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      //记录这条动作，保存时才真正删除
                                      //如果这条动作是选中的，那么从key集合中移除
                                      if (mkey1.contains(settingTypeData.titlestream)) {
                                          mkey1.remove(settingTypeData.titlestream);
                                      }
                                      if (mkey2.contains(settingTypeData.titlestream)) {
                                          mkey2.remove(settingTypeData.titlestream);
                                      }
                                      if (mkey3.contains(settingTypeData.titlestream)) {
                                          mkey3.remove(settingTypeData.titlestream);
                                      }
                                      if (mkey4.contains(settingTypeData.titlestream)) {
                                          mkey4.remove(settingTypeData.titlestream);
                                      }
                                      if (mkey5.contains(settingTypeData.titlestream)) {
                                          mkey5.remove(settingTypeData.titlestream);
                                      }
                                      if (mkey6.contains(settingTypeData.titlestream)) {
                                          mkey6.remove(settingTypeData.titlestream);
                                      }
                                      mTempDeleteDatas.add(settingTypeData.titlestream);
                                      mTypeDatas.remove(settingTypeData);
                                      //mDbHelper.deleteActionByTitleStream(settingTypeData.titlestream);
                                      initType(settingTypeData.type);
                                      mAdapter.notifyDataSetChanged();
                                  }
                              })
                              .show();
    }

    /**展示进度条弹窗*/
    private void showNumProcess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNumProgressDialog = new DNumProgressDialog(GameSettingActivity.this);
                if (!mNumProgressDialog.isShowing()) {
                    mNumProgressDialog.showDialog();
                }
            }
        });


    }

    /**关闭进度条弹窗*/
    private void closeNumProcess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mNumProgressDialog != null) {
                    mNumProgressDialog.dismiss();
                    mNumProgressDialog = null;
                }
            }
        });


    }

    /*蓝牙服务连接*/
    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;
            mHandler.sendEmptyMessage(WHAT_IBLUETOOTH);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_DOWNLOAD_ACTION:
                    ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                                          .execute(new Runnable() {
                                              @Override
                                              public void run() {
                                                  if (mDownloadType.equals(DOWNLOAD_TYPE_ACTION)) {
                                                      downloadAction();
                                                  } else {
                                                      downloadSetting();
                                                  }
                                              }
                                          });

                    break;
                case WHAT_DOWNLOAD_SUCCESS:
                    closeNumProcess();
                    changeTypeData(mTempTypeData);
                    break;
                case WHAT_DOWNLOAD_FAIL:
                    closeNumProcess();
                    ToastUtils.showToast(R.string.fail_download);
                    deleteAndSaveKeySetting(false, false, true);
                    break;
                case WHAT_DOWLOAD_PROGRESS:
                    int progress = (int) msg.obj;
                    mNumProgressDialog.setProgress(progress);
                    break;
                case WHAT_ROBOT_BUZY:
                    ToastUtils.showToast(R.string.robot_buzy);
                    deleteAndSaveKeySetting(false, false, true);
                    break;
                case WHAT_SETTING_FAIL:
                    closeNumProcess();
                    ToastUtils.showToast(R.string.fail_setting);
                    deleteAndSaveKeySetting(false, false, true);
                    break;
                case WHAT_SETTING_SUCCESS:
                    closeNumProcess();
                    deleteAndSaveKeySetting(true, true, true);
                    break;
                case WHAT_DOWNLOAD_EMPTY:
                    ToastUtils.showToast(R.string.empty_action);
                    break;
                case WHAT_SETTING_EMPTY:
                    deleteAndSaveKeySetting(true, true, true);
                    break;
            }
        }
    };

    /**
     * 保存键设置和删除数据
     * @param isClear
     *          是否清理键设置
     * @param isSave
     *          是否保存键设置
     * @param isDelete
     *         是否删除动作
     */
    private void deleteAndSaveKeySetting(final boolean isClear,
                                         final boolean isSave,
                                         final boolean isDelete)
    {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      if (isClear) {
                                          mDbHelper.clearKeySetting();
                                      }
                                      if (isSave) {
                                          saveKeySetting();
                                      }
                                      if (isDelete) {
                                          deleteActionTrue();
                                      }
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              GameSettingActivity.this.finish();
                                          }
                                      });
                                     
                                  }
                              });
    }

    /**
     * 真正的删除动作
     * 1.从数据库删除
     * 2.从下位机删除
     */
    private void deleteActionTrue() {
        for (String titilestream : mTempDeleteDatas) {
            mDbHelper.deleteActionByTitleStream(titilestream);
            //删除此动作的状态
            CustomAction customAction = mDbHelper.findCustomAction(titilestream);
            if(customAction.id != -1){
                mDbHelper.deleteStatusByActionId(customAction.id+"");
            }
            String order        = titilestream.substring(2) + "2E616374";
            String len      = Integer.toHexString(order.length() / 2);
            if(len.length() == 1){
                len = "0" + len;
            }
            String deleteData = "B6000000000F0000" + len + order;
            mIBluetooth.callWrite(deleteData);
            SystemClock.sleep(100);
        }
    }

    /**
     * 改变适配器数据
     * @param tempTypeData
     */
    private void changeTypeData(SettingTypeData tempTypeData) {
        for (int i = 0; i < mTypeDatas.size(); i++) {
            SettingTypeData settingTypeData = mTypeDatas.get(i);
            if (settingTypeData.titlestream.equals(tempTypeData.titlestream)) {
                mTypeDatas.get(i).isFlag = 1;
                //将之添加到目录中去
                mRobotDirectory = mRobotDirectory + mTypeDatas.get(i).titlestream.substring(2);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
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
                    skipScanUI();
                    break;
                case BlueService.ACTION_RETURN_DATA:

                    String data = intent.getStringExtra(BlueService.EXTRA_DATA);
                    String flag = intent.getStringExtra(BlueService.EXTRA_FLAG);
                    if (flag.equals(CODE_DOWNLOAD_REQUEST) && !flag.equals(data)) {
                        if ("8500000000000000".equals(data)) {
                            //开始下载
                            mHandler.sendEmptyMessage(WHAT_DOWNLOAD_ACTION);
                        } else {
                            ToastUtils.showToast(R.string.fail_download_quest);
                        }

                    }
                    if (flag.equals(data)) {
                        mHandler.sendEmptyMessage(WHAT_ROBOT_BUZY);
                    }
                    mReturnData = data;
                    break;
                default:
                    break;
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }

    /**
     * 根据位置初始化按键的UI效果
     */
    private void initCurrentKeyPress() {
        mIvKey1.setSelected(false);
        mIvKey2.setSelected(false);
        mIvKey3.setSelected(false);
        mIvKey4.setSelected(false);
        mIvKey5.setSelected(false);
        mIvKey6.setSelected(false);
        switch (mCurrentKeyPosition) {
            case 1:
                mIvKey1.setSelected(true);
                break;
            case 2:
                mIvKey2.setSelected(true);
                break;
            case 3:
                mIvKey3.setSelected(true);
                break;
            case 4:
                mIvKey4.setSelected(true);
                break;
            case 5:
                mIvKey5.setSelected(true);
                break;
            case 6:
                mIvKey6.setSelected(true);
                break;
        }
    }

    /**
     * 根据位置初始化按键的UI效果
     * @param tv
     *      当前所按类型tv
     */
    private void initCurrentTypePress(TextView tv) {
        if (mType1Status) {
            mTvType1Base.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType1Base.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        if (mType2Status) {
            mTvType2Music.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType2Music.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        if (mType3Status) {
            mTvType3Fable.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType3Fable.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        if (mType4Status) {
            mTvType4Football.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType4Football.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        if (mType5Status) {
            mTvType5Boxing.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType5Boxing.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        if (mType6Status) {
            mTvType6Custom.setBackgroundResource(R.drawable.selector_type_tv_bg_have);
        } else {
            mTvType6Custom.setBackgroundResource(R.drawable.selctor_type_tv_bg);
        }
        mTvType1Base.setSelected(false);
        mTvType2Music.setSelected(false);
        mTvType3Fable.setSelected(false);
        mTvType4Football.setSelected(false);
        mTvType5Boxing.setSelected(false);
        mTvType6Custom.setSelected(false);
        tv.setSelected(true);
    }
}
