package com.tylz.aelos.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.tylz.aelos.base.BaseService;
import com.tylz.aelos.base.ILoadModel;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;

import java.io.IOException;
import java.io.InputStream;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.service
 *  @文件名:   LoadObject3DService
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/14 18:01
 *  @描述：    模型加载服务
 */
public class LoadObject3DService
        extends BaseService
{
    /**3D模型*/
    private Object3D mObject3D;

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
                                      // broadcastUpdate();
                                  }
                              });
    }

    private void broadcastUpdate() {
        Intent intent = new Intent(Constants.ACTION_MODEL_LOADED);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LoadObject3DBinder();
    }

    public class LoadObject3DBinder
            extends Binder
            implements ILoadModel
    {
        @Override
        public Object3D callLoad3DModel() {
            return getModel();
        }
    }

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
        Object3D    o3d = new Object3D(0);
        InputStream is  = null;
        try {
            is = getAssets().open(filename);

            Object3D[] model = Loader.load3DS(is, scale);

            Object3D temp = null;
            for (int i = 0; i < model.length; i++) {
                temp = model[i];
                temp.setCenter(SimpleVector.ORIGIN);
                temp.rotateX((float) (-.5 * Math.PI));
                temp.rotateMesh();
                temp.setRotationMatrix(new Matrix());
                o3d = Object3D.mergeObjects(o3d, temp);
                o3d.build();
            }
        } catch (IOException e) {
            o3d = null;
            e.printStackTrace();
        }
        return o3d;
    }
}
