package com.tylz.aelos.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.adapter.StatusAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.Status;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   AddStatusActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/18 22:34
 *  @描述：    添加状态
 */
public class AddStatusActivity
        extends BaseActivity
        implements StatusAdapter.OnClickPlayListener
{
    public static final  String EXTRA_DATA       = "extra_data";
    private static final String ORDER_ADD_STATUS = "a400000000000000";
    @Bind(R.id.ib_hand_lock)
    ImageButton    mIbHandLock;
    @Bind(R.id.ib_leg_lock)
    ImageButton    mIbLegLock;
    @Bind(R.id.ib_lock)
    ImageButton    mIbLock;
    @Bind(R.id.iv_left)
    ImageButton    mIvLeft;
    @Bind(R.id.tv_title)
    TextView       mTvTitle;
    @Bind(R.id.tv_right)
    TextView       mTvRight;
    @Bind(R.id.ib_add_status)
    ImageButton    mIbAddStatus;
    @Bind(R.id.container_not_content)
    RelativeLayout mContainerNotContent;
    @Bind(R.id.ib_add_status1)
    ImageButton    mIbAddStatus1;
    @Bind(R.id.lv_listview)
    ListView       mLvListview;
    @Bind(R.id.container_have_content)
    LinearLayout   mContainerHaveContent;
    private List<Status>             mDatas;
    private CustomAction             mCustomAction;
    private boolean                  isLocked;                    // 默认为锁定状态
    private boolean                  isHandLocked;                    // 默认为锁定状态
    private boolean                  isLegLocked;                    // 默认为锁定状态
    private DbHelper                 mDbHelper;
    private int                      handCount;
    private int                      legCount;
    private int                      count;
    private StatusAdapter            mAdapter;
    private IBluetooth               mIBluetooth;
    private BlueServiceConnection    mServiceConnection;
    private ConnectBroadcastReceiver mReceiver;
    private String                   mPlayStatusOrder;
    private String                   mReturnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);
        ButterKnife.bind(this);
        showProgress();
        initBlue();
        initData();
        loadData();
    }

    private void initData() {
        mCustomAction = (CustomAction) getIntent().getSerializableExtra(EXTRA_DATA);
        mDatas = new ArrayList<>();
        mPlayStatusOrder = "9200000000100000";
        isLocked = true;
        isHandLocked = true;
        isLegLocked = true;
        handCount = 0;
        legCount = 0;
        count = 0;
        mReturnStatus = "";
        mDbHelper = new DbHelper(this);
        mTvTitle.setText(mCustomAction.title);
        mTvRight.setText(R.string.save);
    }

    private void loadData() {
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      mDatas = mDbHelper.findStatussByActionId("" + mCustomAction.id);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              mAdapter = new StatusAdapter(AddStatusActivity.this,
                                                                           mDatas,
                                                                           mCustomAction);
                                              mLvListview.setAdapter(mAdapter);
                                              mAdapter.setOnClickPlayListener(AddStatusActivity.this);
                                              notifyAdapter();
                                              showOrNotAddStatusView();
                                          }
                                      });
                                  }
                              });
    }

    private void notifyAdapter()
    {
        if (mDatas.size() != 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                LogUtils.d("arr[0] = " + mDatas.get(i).arr[0]);
                mDatas.get(i).isShow = false;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.ib_hand_lock,
              R.id.ib_leg_lock,
              R.id.ib_lock,
              R.id.iv_left,
              R.id.tv_right,
              R.id.ib_add_status,
              R.id.ib_add_status1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_hand_lock:
                lockHand();
                break;
            case R.id.ib_leg_lock:
                lockLeg();
                break;
            case R.id.ib_lock:
                lock();
                break;
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                save();
                break;
            case R.id.ib_add_status:
            case R.id.ib_add_status1:
                showProgress();
                mReturnStatus = "";
                mIBluetooth.callWrite(ORDER_ADD_STATUS);
                break;
        }
    }

    /**
     * 保存数据
     */
    private void save() {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      mDbHelper.deleteStatusByActionId(mCustomAction.id + "");
                                      for (Status status : mDatas) {
                                          mDbHelper.insertStatus(status.actionId,
                                                                 status.arr,
                                                                 status.progress);
                                      }
                                      String fileStream = CommUtils.toActString11(mCustomAction.fileName,
                                                                                  mDatas);
                                      mDbHelper.updateCustomAction(mCustomAction.id + "",
                                                                   fileStream);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              AddStatusActivity.this.finish();
                                          }
                                      });
                                  }
                              });
    }

    /**
     * 判断添加状态的按钮是否展示及展示正确的位置
     */
    private void showOrNotAddStatusView()
    {
        if (mDatas.size() == 0) {
            mContainerHaveContent.setVisibility(View.GONE);
            mContainerNotContent.setVisibility(View.VISIBLE);
        } else {
            mContainerHaveContent.setVisibility(View.VISIBLE);
            mContainerNotContent.setVisibility(View.GONE);
        }
    }

    private void initBlue() {
        Intent service = new Intent(this, BlueService.class);
        mServiceConnection = new BlueServiceConnection();
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_RETURN_DATA);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onClickPlay(Status status) {
        playStatus(status.arr, status.progress);
        changAllLockUI();
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
                    if (flag.equals(ORDER_ADD_STATUS) && !flag.equals(data)) {
                        if (isAddStatusSuccess(data)) {
                            closeProgress();
                            processReturnData(mReturnStatus);
                        }
                    }
                    break;
                default:
                    break;
            }

        }

    }

    /**
     * 处理添加状态的信息
     * @param result
     *      状态信息
     */
    private void processReturnData(String result) {
        if (TextUtils.isEmpty(result) || result.length() != 48) {
            ToastUtils.showToast(R.string.timeout_get_status);
            return;
        }
        int[] actionResults = CommUtils.str2arr(result);
        if (actionResults == null) {
            ToastUtils.showToast(R.string.timeout_get_status);
            return;
        }
        for (int i = 0; i < actionResults.length; i++) {
            if (0 == actionResults[i]) {
                String info = UIUtils.getString(R.string.error_steering);
                ToastUtils.showToast((i + 1) + info);
                break;
            }
        }
        Status status = new Status();
        status.actionId = mCustomAction.id;
        status.progress = Constants.DEFAULT_PRGRESS;
        status.arr = actionResults;
        mDatas.add(status);
        mAdapter.notifyDataSetChanged();
        notifyAdapter();
        showOrNotAddStatusView();
        SystemClock.sleep(Constants.SEND_SLEEP_TIME_SHORT);
        if (isHandLocked) {
            handLock();
        } else {
            handUnLock();
        }
        SystemClock.sleep(Constants.SEND_SLEEP_TIME_SHORT);
        if (isLegLocked) {
            legLock();
        } else {
            legUnLock();
        }
    }

    /**
     * 判断获取状态是否成功
     * @param data
     *      蓝牙返回数据
     * @return
     *      true代表成功
     */
    private boolean isAddStatusSuccess(String data) {
        mReturnStatus += data;
        if (mReturnStatus.length() == 48 && mReturnStatus.startsWith("92")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        alllock();
        super.onDestroy();


    }

    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;
            closeProgress();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void lock()
    {
        if (isLocked) {
            // 解锁操作
            /*
			 * 设置图片为加锁 isLocked = false; 调用工具类中解锁方法
			 * 全部解锁  腿和手也要解锁，并且图片变成加锁状态 且相应的count都变成1
			 */
            count = 1;
            allunlock();
            /****/
            handCount = 1;
            legCount = 1;
            count = 1;
            mIbLock.setImageResource(R.mipmap.lock_locked);
            mIbHandLock.setImageResource(R.mipmap.hand_lock);
            mIbLegLock.setImageResource(R.mipmap.leg_lock);

        } else {
            // 加锁操作

            alllock();
            count = 0;
            /******/
            handCount = 0;
            legCount = 0;
            count = 0;

            mIbLock.setImageResource(R.mipmap.lock_unlocked);
            mIbHandLock.setImageResource(R.mipmap.hand_unlock);
            mIbLegLock.setImageResource(R.mipmap.leg_unlock);
        }
        isLocked = !isLocked;//
    }

    /**
     * 改变加锁的UI
     * 还原到默认状态
     */
    private void changAllLockUI(){
        count = 0;
        /******/
        handCount = 0;
        legCount = 0;
        count = 0;
        mIbLock.setImageResource(R.mipmap.lock_unlocked);
        mIbLock.setImageResource(R.mipmap.lock_unlocked);
        mIbHandLock.setImageResource(R.mipmap.hand_unlock);
        mIbLegLock.setImageResource(R.mipmap.leg_unlock);
        isLocked = true;
        isHandLocked = true;
        isLegLocked = true;
    }
    private void lockHand() {
        if (isHandLocked) {
            handCount = 1;
            mIbHandLock.setImageResource(R.mipmap.hand_lock);
            handUnLock();
        } else {
            mIbHandLock.setImageResource(R.mipmap.hand_unlock);
            handLock();
            handCount = 0;
        }
        isHandLocked = !isHandLocked;
        judgeLock();
    }

    private void lockLeg()
    {
        if (isLegLocked) {
            legCount = 1;
            mIbLegLock.setImageResource(R.mipmap.leg_lock);
            legUnLock();
        } else {
            mIbLegLock.setImageResource(R.mipmap.leg_unlock);
            legLock();
            legCount = 0;
        }
        isLegLocked = !isLegLocked;
        judgeLock();
    }

    private void judgeLock() {
        /*
         * 判断是否手和腿都解锁了
		 * 都为1即为全部解锁，都为0即为全部加锁
		 * 都为1，那么要修改图片为全部加锁，手和腿也要加锁，同时至为0
		 */
        if (handCount == 1 && legCount == 1) {
            count = 1;
            mIbLock.setImageResource(R.mipmap.lock_locked);
            mIbHandLock.setImageResource(R.mipmap.hand_lock);
            mIbLegLock.setImageResource(R.mipmap.leg_lock);
        } else if (handCount == 0 && legCount == 0) {
            count = 0;
            mIbLock.setImageResource(R.mipmap.lock_unlocked);
            mIbHandLock.setImageResource(R.mipmap.hand_unlock);
            mIbLegLock.setImageResource(R.mipmap.leg_unlock);
        }
    }

    /**
     * 上半身解锁
     */
    public void handUnLock() {
        try {
            mIBluetooth.callWrite(
                    "78000000000100000178000000000100000278000000000100000378000000000100000978000000000100000a");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite("78000000000100000b780000000001000011");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上半身加锁
     */
    public void handLock() {
        try {
            mIBluetooth.callWrite(
                    "79000000000100000179000000000100000279000000000100000379000000000100000979000000000100000a");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite("79000000000100000b790000000001000011");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  下半身解锁
     */
    public void legUnLock() {
        try {
            mIBluetooth.callWrite(
                    "780000000001000004780000000001000005780000000001000006780000000001000007780000000001000008");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "78000000000100000c78000000000100000d78000000000100000e78000000000100000f780000000001000010");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  下半身加锁
     */
    public void legLock() {
        try {
            mIBluetooth.callWrite(
                    "790000000001000004790000000001000005790000000001000006790000000001000007790000000001000008");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "79000000000100000c79000000000100000d79000000000100000e79000000000100000f790000000001000010");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 全部解锁 无输入和返回
	 */
    public void allunlock() {
        try {

            mIBluetooth.callWrite(
                    "780000000001000001780000000001000002780000000001000003780000000001000004780000000001000005780000000001000006");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "78000000000100000778000000000100000878000000000100000978000000000100000a78000000000100000b78000000000100000c");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "78000000000100000d78000000000100000e78000000000100000f780000000001000010");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 全部加锁 无输入和返回
     */
    public void alllock() {
        try {

            mIBluetooth.callWrite(
                    "790000000001000001790000000001000002790000000001000003790000000001000004790000000001000005790000000001000006");
            //
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "79000000000100000779000000000100000879000000000100000979000000000100000a79000000000100000b79000000000100000c");
            Thread.sleep(Constants.SEND_SLEEP_TIME_SHORT);
            mIBluetooth.callWrite(
                    "79000000000100000d79000000000100000e79000000000100000f790000000001000010");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 播放状态
     * @param value
     *      状态数组
     * @param speed
     *      速度
     */
    public void playStatus(int[] value, int speed)
    {
        for (int i = 0; i < 16; i++) {
            String temp = Integer.toHexString(value[i]);
            if (temp.length() == 1) { temp = "0" + temp; }
            mPlayStatusOrder = mPlayStatusOrder + temp;
        }
        String hexString = Integer.toHexString(speed);
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        mIBluetooth.callWrite(mPlayStatusOrder + hexString);
        mPlayStatusOrder = "9200000000100000";
    }
}
