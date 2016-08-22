package com.tylz.aelos.base;

import com.threed.jpct.Object3D;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.service
 *  @文件名:   IBluetooth
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/24 23:05
 *  @描述：    蓝牙服务接口
 */
public interface IBluetooth {
    /**
     * 向下位机写入数据
     * @param data
     *      十六进制数据
     */
    void callWrite(String data);

    /**
     * 连接蓝牙
     * @param address
     *      蓝牙地址
     */
    void callConnect(String address);

    /**
     * 关闭蓝牙
     */
    void callClose();

    /**
     * 测试方法
     */
    void callTest();

    /**
     * 蓝牙是否连接
     * @return
     *      连接成功返回true, 反之返回false
     */
    boolean callIsConnected();


    /**
     * 调用加载的3d模型
     * @return
     *  加载完返回3d模型，否则返回null
     */
    Object3D callLoad3DModel();

    /**
     *调用读取蓝牙信号
     *
     * @return 是否成功
     */
    boolean callReadRssi();
}
