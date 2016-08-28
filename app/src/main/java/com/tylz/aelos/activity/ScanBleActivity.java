package com.tylz.aelos.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.adapter.LeDeviceAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.BaseApplication;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.GifView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ScannBleActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/24 21:14
 *  @描述：    TODO
 */
public class ScanBleActivity
        extends BaseActivity
        implements AdapterView.OnItemClickListener
{
    private static final int    REQUEST_ENABLE_BLUE       = 100;
    private static final String SCAN_NOW                  = UIUtils.getString(R.string.scan_now);
    private static final String SCANING                   = UIUtils.getString(R.string.scaning);
    private static final int    WHAT_CONNECT_WAITING_TIME = 1;
    @Bind(R.id.gif)
    GifView  mGif;
    @Bind(R.id.tv_scan)
    Button   mBtScan;
    @Bind(R.id.tv_no_sacn)
    TextView mTvNoSacn;
    @Bind(R.id.listview)
    ListView mListview;
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceAdapter  mAdapter;
    private MyLeScanCallBack mLeScanCallBack;

    private ConnectBroadcastReceiver mReceiver;
    public  IBluetooth               mIBluetooth;
    private BlueServiceConnection    mBlueServiceConnection;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CONNECT_WAITING_TIME:
                    closeProgress();
                    mToastor.getSingletonToast(R.string.fail_connect_robot)
                            .show();
                    break;
            }
        }
    };
    private ScanTask mScanTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scann_ble);
        ButterKnife.bind(this);
        //初始化
        mGif.setMovieResource(R.mipmap.scanning);
        mGif.setPaused(true);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mAdapter = new LeDeviceAdapter(this);
        mLeScanCallBack = new MyLeScanCallBack();
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(this);
        startAndrBindService(this);
        //广播接收
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_CONNECTED);
        registerReceiver(mReceiver, intentFilter);

    }


    /**
     * 开始并绑定服务
     * @param context 上下文
     */
    public void startAndrBindService(Context context) {
        Intent service = new Intent(context, BlueService.class);
        context.startService(service);
        mBlueServiceConnection = new BlueServiceConnection();
        context.bindService(service, mBlueServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @OnClick({R.id.tv_scan,
              R.id.tv_no_sacn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_scan:
                scan();
                break;
            case R.id.tv_no_sacn:
                skipActivityF(MainActivity.class);
                break;
        }
    }

    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void scan() {
        if (!checkBlueSupport()) { return; }
        // 检查蓝牙权限
        if (!mBluetoothAdapter.isEnabled()) {
            LogUtils.d("蓝牙设备没有开启！");
            Intent blueIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blueIntent, REQUEST_ENABLE_BLUE);
        }
        mBtScan.setText(SCANING);
        mBtScan.setClickable(false);

        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        scanLeDevice(false);
        super.onPause();
    }

    private class ScanTask
            implements Runnable
    {

        @Override
        public void run() {
            mGif.setPaused(true);
            mBluetoothAdapter.stopLeScan(mLeScanCallBack);
            mBtScan.setText(SCAN_NOW);
            mBtScan.setClickable(true);
        }
    }

    /**
     * 扫描设备
     * @param enable
     *          true 开始扫描 false 停止扫描
     */
    private void scanLeDevice(final boolean enable)
    {
        if (enable) {
            if (mScanTask == null) {
                mScanTask = new ScanTask();
            }
            mHandler.removeCallbacks(mScanTask);
            mHandler.postDelayed(mScanTask, Constants.SCAN_PERIOD);
            if (mAdapter != null) {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
            }
            mGif.setPaused(false);
            mBluetoothAdapter.startLeScan(mLeScanCallBack);
            mBtScan.setText(SCANING);
            mBtScan.setClickable(false);
        } else {
            mGif.setPaused(true);
            mBluetoothAdapter.stopLeScan(mLeScanCallBack);
            mBtScan.setText(SCAN_NOW);
            mBtScan.setClickable(true);
        }
    }

    /**
     * 蓝牙连接的广播接受者
     */
    private class ConnectBroadcastReceiver
            extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                      .equals(BlueService.ACTION_CONNECTED))
            {
                closeProgress();
                mHandler.removeMessages(WHAT_CONNECT_WAITING_TIME);
                Intent activity = new Intent(ScanBleActivity.this, MainActivity.class);
                startActivity(activity);
                ScanBleActivity.this.finish();
            } else if (intent.getAction()
                             .equals(BlueService.ACTION_UNCONNECT))
            {
                mToastor.getSingletonToast(R.string.fail_connect_robot)
                        .show();
                closeProgress();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 扫描停止
        // showProgress(R.string.connect_bluetooth);
        showProgress();
        BluetoothDevice device = mAdapter.getDevice(position);
        mSpUtils.putString(MainActivity.DEVICE_NAME, device.getName());
        mSpUtils.putString(MainActivity.DEVICE_ADDRESS, device.getAddress());
        mHandler.sendEmptyMessageDelayed(WHAT_CONNECT_WAITING_TIME,
                                         Constants.WAITING_CONN_ROBOT_TIME);
        mIBluetooth.callConnect(device.getAddress());
        scanLeDevice(false);
    }

    private class MyLeScanCallBack
            implements BluetoothAdapter.LeScanCallback
    {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run()
                {
                    if (device != null) {
                        LogUtils.d("name = " + device.getName() + " address = " + device.getAddress());
                        String deviceName = device.getName();
                        if (!TextUtils.isEmpty(deviceName)) {
                            if (deviceName.contains("AELOS") || deviceName.contains("aelos")) {
                                LogUtils.d("扫描到设备" + deviceName);
                                mAdapter.addDevice(device);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mBlueServiceConnection != null) {
            unbindService(mBlueServiceConnection);
        }
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mPreClickTime > 2000) {// 两次连续点击的时间间隔>2s
            mToastor.getSingletonToast(R.string.exit_app)
                    .show();
            mPreClickTime = System.currentTimeMillis();
            return;
        } else {   // 点的快 完全退出
            ((BaseApplication) getApplication()).closeApplication();
            super.onBackPressed();// finish

        }
        super.onBackPressed();
    }

    /**
     * 检查设备是否支持蓝牙
     *
     * @return false 代表不支持
     */
    private boolean checkBlueSupport()
    {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mToastor.getSingletonToast(R.string.ble_not_supported)
                    .show();
            return false;
        }
        // 检查设备是否支持蓝牙
        if (bluetoothAdapter == null) {
            mToastor.getSingletonToast(R.string.error_bluetooth_not_supported)
                    .show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUE && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
