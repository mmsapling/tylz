package com.tylz.aelos.base;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.tylz.aelos.gallery.GlideImageLoader;
import com.tylz.aelos.gallery.GlidePauseOnScrollListener;
import com.tylz.aelos.util.FileUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * @author cxw
 * @time 2016/3/18 0018 15:02
 * @des 骄阳物流的入口类，保存一些公共方法和属性
 * @updateAuthor tylz
 * @updateDate 2016/3/18 0018
 * @updateDes
 */

public class BaseApplication
        extends Application
{
    private static Context mContext;                                                // member
    private static long    mMainThreadId;
    private static Handler mMainThreadHandler;
    public static boolean isLoadConfigModel;
    private List<Activity> activitys = new LinkedList<Activity>();
    private List<Service>  services  = new LinkedList<Service>();


    public static Context getContext() {
        return mContext;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        init();
        initOkHttp();
        initGallery();
        isLoadConfigModel = false;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
    /**
     * 初始化Gallery
     */
    private void initGallery() {
        //设置主题
        ThemeConfig themeConfig = ThemeConfig.DEFAULT;
        //配置功能
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setEnableCamera(true)
                             .setEnableEdit(true)
                             .setEnableCrop(true)
                             .setCropHeight(320)
                             .setForceCrop(true)
                             .setCropWidth(320)
                             .setCropReplaceSource(true)
                             .setRotateReplaceSource(true)
                             .setEnableRotate(true);
        PauseOnScrollListener pauseOnScrollListener = new GlidePauseOnScrollListener(false,true);
        FunctionConfig                        functionConfig = functionConfigBuilder.build();
        cn.finalteam.galleryfinal.ImageLoader imageLoader    = new GlideImageLoader();
        File                                  editCacheFile  = new File(FileUtils.getIconDir() + "/edit");
        File                                  cacheFile      = new File(FileUtils.getIconDir());
        CoreConfig coreConfig = new CoreConfig.Builder(this,
                                                       imageLoader,
                                                       themeConfig)
                                                                   .setEditPhotoCacheFolder(editCacheFile)
                                                                   .setPauseOnScrollListener(pauseOnScrollListener)
                                                                   .setTakePhotoFolder(cacheFile)
                                                                   .setFunctionConfig(functionConfig)
                                                                   .build();
        GalleryFinal.init(coreConfig);
    }

    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("okHttp"))
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    private void init() {
        // 1.上下文
        mContext = getApplicationContext();

        // 2.主线程的Id
        /**
         * Tid Thread Pid Process Uid User
         */
        mMainThreadId = android.os.Process.myTid();

        // 3.创建一个主线程的handler
        mMainThreadHandler = new Handler();
    }



    public void addActivity(Activity activity) {
        activitys.add(activity);
    }

    public void removeActivity(Activity activity) {
        activitys.remove(activity);
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
    }

    public void closeApplication() {
        isLoadConfigModel = false;
        closeActivitys();
        closeServices();
    }

    private void closeActivitys() {
        ListIterator<Activity> iterator = activitys.listIterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    private void closeServices() {
        ListIterator<Service> iterator = services.listIterator();
        while (iterator.hasNext()) {
            Service service = iterator.next();
            if (service != null) {
                stopService(new Intent(this, service.getClass()));
            }
        }
    }


}
