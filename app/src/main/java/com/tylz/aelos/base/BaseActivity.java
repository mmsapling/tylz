package com.tylz.aelos.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tylz.aelos.R;
import com.tylz.aelos.activity.GoLoginActivity;
import com.tylz.aelos.activity.ScanBleActivity;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.MD5Utils;
import com.tylz.aelos.util.SPUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;
import com.tylz.aelos.view.DProgressDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;


/**
 * @author tylz
 * @time 2016/3/18 0018 15:02
 * @des 所有Activity的基类，保存一些公共方法和属性
 * @updateAuthor tylz
 * @updateDate 2016/3/18 0018
 * @updateDes
 */
public class BaseActivity
        extends AppCompatActivity
{
    public  SPUtils         mSpUtils;
    // 再按一次退出应用程序
    private DProgressDialog mProgressDialog;

    // 再按一次退出应用程序
    public long   mPreClickTime;
    public String mUser_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpUtils = new SPUtils(this);
        mUser_id = mSpUtils.getString(Constants.USER_ID, "");
        ((BaseApplication) getApplication()).addActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mUser_id = mSpUtils.getString(Constants.USER_ID, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeProgress();
        ((BaseApplication) getApplication()).removeActivity(this);
    }

    /**
     * 开启进度条
     */
    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new DProgressDialog(this);
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 关闭进度条
     */
    public void closeProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 跳转另一Activity，并结束当前界面
     * @param clazz
     *          Activity
     */
    public void skipActivityF(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * 弹出失去连接提示，跳转到扫描页面
     */
    public void skipScanUI() {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setCancelable(false)
                              .setMsg(UIUtils.getString(R.string.tip_ble_connect_fail))
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      skipActivityF(ScanBleActivity.class);
                                  }
                              })
                              .show();
    }

    /**
     * 跳转另一Activity，不结束当前界面
     * @param clazz
     *          Activity
     */
    public void skipActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 判断是否有登录信息
     * @return 有返回true，无返回false
     */
    public boolean isLogin() {
        User user = mSpUtils.getUserInfoBySp();
        if (TextUtils.isEmpty(user.id)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 弹出登录提示
     * 1.登录后更新本地缓存用户数据
     * 1.如果界面需要用户id的地方，需要再次请求网络刷新界面数据
     */
    public void showLoginTip() {
        new DAlertDialog(this).builder()
                              .setCancelable(false)
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.tip_must_login))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      skipActivity(GoLoginActivity.class);
                                  }
                              })
                              .show();
    }

    /**
     * 网络接口请求回来的数据逻辑封装
     */
    public abstract class ResultCall
            extends StringCallback
    {
        public ResultCall() {
            showProgress();
        }

        public ResultCall(boolean isShow) {
            if (isShow) {
                showProgress();
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            closeProgress();
            ToastUtils.showToast(R.string.tip_check_net);
        }

        @Override
        public void onResponse(String response, int id) {
            closeProgress();
            if (TextUtils.isEmpty(response)) {
                onEmpty();
            } else {
                onResult(response, id);
            }
        }

        /**
         * 选择性实现
         */
        public void onEmpty() {

        }

        /**
         * 必须实现
         * @param response
         * @param id
         */
        public abstract void onResult(String response, int id);
    }

    /**
     * 设置别名
     */
    public void setAligs() {
        Set<String> tags = new HashSet<>();
        User        user = mSpUtils.getUserInfoBySp();
        if (!TextUtils.isEmpty(user.phone)) {
            String encode = MD5Utils.encode(user.phone);
            LogUtils.d("md5 = " + encode);
            JPushInterface.setAliasAndTags(this, encode, null, new TagAliasCallback() {

                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (i != 0) {
                        LogUtils.d("设置别名失败");
                    }else{
                        LogUtils.d("设置别名成功");
                    }
                }
            });
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
