package com.tylz.aelos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.threed.jpct.Object3D;
import com.threed.jpct.TextureManager;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.BaseApplication;
import com.tylz.aelos.base.ILoadModel;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.bean.UserBean;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.object3d.MyRender;
import com.tylz.aelos.service.LoadObject3DService;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   MainActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/13 17:43
 *  @描述：    主界面
 */
public class MainActivity
        extends BaseActivity
{
    public static final  String DEVICE_NAME     = "device_name";
    public static final  String DEVICE_ADDRESS  = "device_address";
    private static final int    WHAT_LOAD_MODEL = 0;

    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton     mIvRight;
    @Bind(R.id.iv_control)
    ImageView       mIvControl;
    @Bind(R.id.iv_action_shop)
    ImageView       mIvActionShop;
    @Bind(R.id.fl_robot)
    FrameLayout     mFlRobot;
    @Bind(R.id.civ_avator)
    CircleImageView mCivAvator;
    @Bind(R.id.tv_username)
    TextView        mTvUsername;
    @Bind(R.id.rl_info)
    RelativeLayout  mRlInfo;
    @Bind(R.id.tv_msg)
    TextView        mTvMsg;
    @Bind(R.id.tv_setting)
    TextView        mTvSetting;
    @Bind(R.id.tv_about)
    TextView        mTvAbout;
    @Bind(R.id.tv_logout)
    TextView        mTvLogout;
    @Bind(R.id.tv_collect)
    TextView        mTvCollect;
    @Bind(R.id.tv_upload)
    TextView        mTvUpload;
    @Bind(R.id.iv_connect_robot)
    ImageView       mIvConnectRobot;
    private SlidingMenu                 mSlidingMenu;
    private LoadObjectServiceConnection mServiceConnection;
    private float                       mXPos;
    private float                       mYPos;
    private MyRender                    mRender;
    private float mModelLeftSpeed  = 0.0f;
    private float mModelRightSpeed = 0.0f;
    private ModelAnimTask mModelAnimTask;
    private long          mPreClickTime;
    private ILoadModel    mILoadModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        //设置全屏触摸
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置菜单的背景阴影宽度
        mSlidingMenu.setShadowWidth(UIUtils.dp2Px(200));
        //设置菜单的背景阴影的图片
        //slidingMenu.setShadowDrawable(R.drawable.bg_loading);

        //设置屏幕主内容的宽度
        mSlidingMenu.setBehindOffset(UIUtils.dp2Px(140));
        //设置渐入渐出效果值
        mSlidingMenu.setFadeDegree(0.75f);

        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.left_menu);
        ButterKnife.bind(this);
        initData();
        /*SlidingMenu 忽视控件*/
        mSlidingMenu.addIgnoredView(mFlRobot);
        /**绑定服务*/
        Intent service = new Intent(this, LoadObject3DService.class);
        mServiceConnection = new LoadObjectServiceConnection();
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        boolean is_first = mSpUtils.getBoolean(Constants.IS_FIRST_MAIN, true);
        loadDataFromNet();
        if (is_first) {
            skipActivity(GuideMainActivity.class);
        }
    }
    /**
    * 网络重新登录一遍
    */
    private void loadDataFromNet() {
        //        getUserInfo
        final User user = mSpUtils.getUserInfoBySp();
        if (!TextUtils.isEmpty(user.id) && !TextUtils.isEmpty(user.password)) {
            OkHttpUtils.post()
                       .url(HttpUrl.BASE + "fullLogin")
                       .addParams("username", user.phone)
                       .addParams("password", user.password)
                       .addParams("platform", "android")
                       .build()
                       .execute(new ResultCall(false) {
                           @Override
                           public void onResult(String response, int id) {
                               if(response.equals("3") || response.equals("2")){
                                   LogUtils.d("登录失败！");
                               }else{
                                   //跳入到扫描界面
                                   processJson(response,user);
                               }
                           }
                       });
        }

    }
    private void processJson(String response,User userinfo) {
        Type           type  = new TypeToken<List<UserBean>>() {}.getType();
        Gson           gson  = new Gson();
        List<UserBean> datas = gson.fromJson(response, type);
        UserBean       user  = datas.get(0);
        mSpUtils.saveUserInfo(CommomUtil.bean2user(user, userinfo.password));
        setAligs();

    }
    private void initData() {
        mIvLeft.setImageResource(R.mipmap.menu);
        mTvTitle.setText(R.string.my_robot);
        mIvRight.setImageResource(R.mipmap.help_icon);
        User user = mSpUtils.getUserInfoBySp();
        if (!TextUtils.isEmpty(user.avatar)) {
            Picasso.with(this)
                   .load(user.avatar)
                   .placeholder(R.mipmap.defaultavatar)
                   .into(mCivAvator);
        }
        if (!TextUtils.isEmpty(user.nickname)) {
            mTvUsername.setText(user.nickname);
        }
        if(mModelAnimTask == null){
            mModelAnimTask = new ModelAnimTask();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mServiceConnection != null) {
            /*是否取消绑定，考虑到模型加载速度。。。。*/
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }

    @OnClick({R.id.iv_left,
              R.id.iv_right,
              R.id.iv_control,
              R.id.iv_action_shop,
              R.id.rl_info,
              R.id.tv_msg,
              R.id.tv_setting,
              R.id.tv_about,
              R.id.tv_logout,
              R.id.tv_collect,
              R.id.tv_upload,
              R.id.iv_connect_robot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                mSlidingMenu.toggle(true);
                break;
            case R.id.iv_right:
                skipActivity(MainHelpActivity.class);
                break;
            case R.id.iv_control:
                skipActivity(GameActivity.class);
                break;
            case R.id.iv_action_shop:
                skipActivity(ActionShopActivity.class);
                break;
            case R.id.rl_info:
                if (isLogin()) {
                    skipActivity(UserInfoActivity.class);
                } else {
                    showLoginTip();
                }
                break;
            case R.id.tv_msg:
                if (isLogin()) {
                    skipActivity(MsgActivity.class);
                } else {
                    showLoginTip();
                }
                break;
            case R.id.tv_setting:
                skipActivity(SettingActivity.class);
                break;
            case R.id.tv_about:
                skipActivity(AboutActivity.class);
                break;
            case R.id.tv_logout:
                logout();
                break;
            case R.id.tv_collect:
                if (isLogin()) {
                    skipActivity(MyCollectActivity.class);
                } else {
                    showLoginTip();
                }
                break;
            case R.id.tv_upload:
                if(isLogin()){
                    skipActivity(MyUploadActivity.class);
                }else{
                    showLoginTip();
                }
                break;
            case R.id.iv_connect_robot:
                skipActivity(ConnRobotActivity.class);
                break;
        }
    }

    /**
     * 退出登录
     * 1.清除登录信息
     * 2.跳转到登录页面并结束当前界面
     */
    private void logout() {
        mSpUtils.clearUserInfo();
        skipActivityF(LoginActivity.class);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mPreClickTime > 2000) {// 两次连续点击的时间间隔>2s
            mToastor.getSingletonToast(R.string.exit_app).show();
            mPreClickTime = System.currentTimeMillis();
            return;
        } else {   // 点的快 完全退出
            ((BaseApplication) getApplication()).closeApplication();
            super.onBackPressed();// finish

        }
        super.onBackPressed();
    }

    private class LoadObjectServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mILoadModel = (ILoadModel) service;
            mHandler.sendEmptyMessage(WHAT_LOAD_MODEL);
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
                case WHAT_LOAD_MODEL:
                    initObject3D();
                    break;
            }
        }
    };

    /**
     * 处理模型事物
     */
    private void initObject3D() {
        if (mModelAnimTask == null) {
            mModelAnimTask = new ModelAnimTask();
        }
        int textureCount = TextureManager.getInstance()
                                         .getTextureCount();
        try {
            if (textureCount >= 2) {
                TextureManager.getInstance()
                              .removeTexture(Constants.MODEL_TEXTURE_NAME);
            }
        } catch (Exception e) {
            mToastor.getSingletonToast(R.string.FAIL_LOAD_MODEL).show();
            e.printStackTrace();
        }
        if (mILoadModel != null) {
            Object3D object3D = mILoadModel.callLoad3DModel();
            if (object3D != null) {
                mRender = new MyRender(getApplication(), object3D);
                mFlRobot.removeAllViews();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                               FrameLayout.LayoutParams.MATCH_PARENT);
                mFlRobot.addView(mRender, params);
                mFlRobot.requestFocus();
                mFlRobot.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mXPos = event.getRawX();
                                mYPos = event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                break;
                            case MotionEvent.ACTION_UP:
                                float rawX = event.getRawX();
                                float rawY = event.getRawY();
                                float x = rawX - mXPos;
                                float y = rawY - mYPos;
                                if (x == 0 && y == 0) {
                                    stopModelAnim();
                                }
                                    /*判断是向左还是向右*/
                                if (x > 0) {//向右 以300为界限短滑侧滑菜单出来，长滑机器人向右转动
                                        /*以100为界限是否向上或者向下*/
                                    if (y < -100) { //向右上
                                    } else if (y > 100) { //向右下
                                    } else if (x > 300) {
                                        //向右长滑
                                        mModelRightSpeed -= Constants.MODEL_REGULATION_SPEED;
                                        mModelLeftSpeed = 0;
                                        mModelAnimTask.start(mModelRightSpeed);
                                    } else if (x < 300) { //向右短滑
                                            /*
                                            * 1.如果当前侧滑菜单是关闭的，那么开启
                                            * 2.如果当前侧滑菜单是开启的，那么关闭
                                            * */
                                        mSlidingMenu.toggle(true);
                                    }
                                } else if (x < 0) {
                                    //向左
                                    if (y > 100) {
                                        //向左下
                                    } else if (y < -100) {
                                        //向左上
                                    } else if (x > -300 && x < -100) { //向左短滑
                                        mModelRightSpeed = 0;
                                        mModelLeftSpeed += Constants.MODEL_REGULATION_SPEED;
                                        mModelAnimTask.start(mModelLeftSpeed);
                                    } else if (x < -300) { //向左长滑
                                        mModelRightSpeed = 0;
                                        //向左
                                        mModelLeftSpeed += Constants.MODEL_REGULATION_SPEED;
                                        mModelAnimTask.start(mModelLeftSpeed);
                                    }
                                }
                                break;
                        }
                        return true;
                    }
                });
            }
        }


    }

    /**
     * 暂停模型动画，并且左转速度右转速度设为0
     */
    private void stopModelAnim() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mPreClickTime < 1000) {
            /*停止动画，并且速度设成0*/
            mModelAnimTask.stop();
            mModelLeftSpeed = 0;
            mModelRightSpeed = 0;
        } else {
            mPreClickTime = currentTimeMillis;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mModelAnimTask != null && mRender != null) {
            if (mModelLeftSpeed != 0) {
                mModelAnimTask.start(mModelLeftSpeed);
            } else {
                mModelAnimTask.start(mModelRightSpeed);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*重置速度*/
        if (mModelAnimTask != null) {
            if (mModelLeftSpeed != 0) {
                mModelAnimTask.start(mModelLeftSpeed);
            } else {
                mModelAnimTask.start(mModelRightSpeed);
            }
        }
        if (mRender != null) {
            mRender.onResume();
        }else{
            //mModelAnimTask = null;
            mModelRightSpeed = 0;
            mModelLeftSpeed = 0;
            //initObject3D();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mModelAnimTask != null) {
            mModelAnimTask.stop();
        }
        if (mRender != null) {
            mRender.onPause();
        }
    }

    /**模型转动任务*/
    private class ModelAnimTask
            implements Runnable
    {
        private float mSpeed = 0;

        public void start(float speed) {
            mSpeed = speed;
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, Constants.MODEL_ANIM_WAIT_TIME);
        }

        public void stop() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            if (mRender != null) {
                mRender.setTouchTurn(mSpeed);
                start(mSpeed);
            }
        }
    }

}
