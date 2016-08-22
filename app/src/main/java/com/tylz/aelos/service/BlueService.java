package com.tylz.aelos.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.tylz.aelos.base.BaseService;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.CommomUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.service
 *  @文件名:   BlueService
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/24 22:07
 *  @描述：    TODO
 */
public class BlueService
        extends BaseService
{
    public static final  String TAG                = "mmsapling";
    private static final String UUID1              = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String UUID2              = "0000ffe4-0000-1000-8000-00805f9b34fb";
    private static final String UUID3              = "0000ffe5-0000-1000-8000-00805f9b34fb";
    private static final String UUID4              = "0000ffe9-0000-1000-8000-00805f9b34fb";
    public static final  String EXTRA_DATA         = "data";
    public static final  String ACTION_RETURN_DATA = "com.tylz.aelos.service.return.data";
    public static final  String ACTION_RETURN_RSSI = "com.tylz.aelos.service.return.rssi";
    public static final  String EXTRA_RSSI         = "rssi";
    public static final  String ACTION_CONNECTED   = "com.tylz.aelos.service.connected";
    public static final  String ACTION_UNCONNECT   = "com.tylz.aelos.service.unconnect";
    public static final  String EXTRA_FLAG         = "extra_flag";
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private BluetoothManager            mBluetoothManager;
    private BluetoothAdapter            mBluetoothAdapter;
    private BluetoothDevice             mBluetoothDevice;
    private BluetoothGatt               mBluetoothGatt;
    private boolean isConnected = false;

    /*写入标志*/
    private String mWriteFlag      = "tylz";
    private String mRobotDirectory = "";
    private Object3D mObject3D;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BluetoothBinder();
    }

    /**3D模型*/
    @Override
    public void onCreate() {
        super.onCreate();
        /**加载模型*/
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      mObject3D = loadModel(Constants.MODEL_NAME, 1.2f);
                                      //是否需要广播发送加载完毕
                                      //                broadcastUpdate();
                                  }
                              });
    }

    public class BluetoothBinder
            extends Binder
            implements IBluetooth
    {
        public BlueService getService() {
            return BlueService.this;
        }

        @Override
        public void callWrite(String data) {
            write(data);
        }

        @Override
        public void callConnect(String address) {
            connect(address);
        }

        @Override
        public void callClose() {
            close();
        }

        @Override
        public void callTest() {
            test();
        }

        @Override
        public boolean callIsConnected() {

            return isConnected;
        }
        @Override
        public Object3D callLoad3DModel() {
            return getModel();
        }

        @Override
        public boolean callReadRssi() {
           return readRssi();
        }
    }

    private void test() {
        Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT)
             .show();
    }

    private void connect(String address) {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
    }

    private void write(String data) {
        byte[] bytes = CommomUtil.hexStringToBytes(data);
        mBluetoothGattCharacteristic.setValue(bytes);
        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
    }

    private void close() {
        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                stopSelf();
            }
        }
    }

    /**
     * 读取蓝牙信号
     */
    private boolean readRssi(){
        return mBluetoothGatt.readRemoteRssi();
    }
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "newState = 失去连接");
                isConnected = false;
                broadcastUpdate(ACTION_UNCONNECT);
            } else {
                isConnected = true;
                mBluetoothGatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                UUID                        uuid1          = UUID.fromString(UUID1);
                UUID                        uuid2          = UUID.fromString(UUID2);
                BluetoothGattService        service        = mBluetoothGatt.getService(uuid1);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuid2);
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                UUID                 uuid3                = UUID.fromString(UUID3);
                UUID                 uuid4                = UUID.fromString(UUID4);
                BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(uuid3);
                mBluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuid4);
                isConnected = true;
                broadcastUpdate(ACTION_CONNECTED);
            } else {
                isConnected = false;
            }
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status)
        {
            Log.d(TAG, "写入 = " + CommomUtil.bytesToHexString(characteristic.getValue()));
            mWriteFlag = CommomUtil.bytesToHexString(characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
            Log.d(TAG, "返回=" + CommomUtil.bytesToHexString(characteristic.getValue()));
            broadcastUpdate(ACTION_RETURN_DATA, characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            broadcastUpdate(ACTION_RETURN_RSSI, rssi);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_RSSI, rssi);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic)
    {
        final Intent intent = new Intent(action);
        String       data   = CommomUtil.bytesToHexString(characteristic.getValue());
        if (!TextUtils.isEmpty(data)) {
            intent.putExtra(EXTRA_DATA, data);
        }
        if (!TextUtils.isEmpty(mWriteFlag)) {
            intent.putExtra(EXTRA_FLAG, mWriteFlag);
        }
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        close();
        super.onDestroy();

    }

    /**
     * 得到机器人3D模型
     * @return
     *      没有就返回null;
     */
    private Object3D getModel() {
        if (mObject3D != null) {
            return mObject3D;
        }
        return null;
    }

    /* 加载3ds模型
    *
    * @param filename
    *      文件名称
    * @param scale
    *      缩放比例
    * @return 返回3d模型
    */
    private Object3D loadModel(String filename, float scale)
    {
        InputStream is = null;
        try {
            is = getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object3D[] model = Loader.load3DS(is, scale);
        Object3D   o3d   = new Object3D(0);
//        Object3D   temp  = null;
//        for (int i = 0; i < model.length; i++) {
//            temp = model[i];
//            temp.setCenter(SimpleVector.ORIGIN);
//            temp.rotateX((float) (-.5 * Math.PI));
//            temp.rotateMesh();
//            temp.setRotationMatrix(new Matrix());
//            o3d = Object3D.mergeObjects(o3d, temp);
//            o3d.build();
//        }
        return model[0];
    }

}
