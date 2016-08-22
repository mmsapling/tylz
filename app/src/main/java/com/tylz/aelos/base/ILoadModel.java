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
public interface ILoadModel {

    /**
     * 调用加载的3d模型
     * @return
     *  加载完返回3d模型，否则返回null
     */
    Object3D callLoad3DModel();
}
