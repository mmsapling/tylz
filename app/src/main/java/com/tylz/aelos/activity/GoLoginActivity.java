package com.tylz.aelos.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.UserBean;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.CommomUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   GoLoginActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/9 15:26
 *  @描述：    仅登录用
 */
public class GoLoginActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton mIvRight;
    @Bind(R.id.tv_right)
    TextView    mTvRight;
    @Bind(R.id.et_username)
    EditText    mEtUsername;
    @Bind(R.id.et_pwd)
    EditText    mEtPwd;
    @Bind(R.id.btn_login)
    Button      mBtnLogin;
    private String mPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_login);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mTvTitle.setText(R.string.login);
    }

    @OnClick({R.id.iv_left,
              R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        String account = mEtUsername.getText()
                                    .toString();
        mPwd = mEtPwd.getText()
                           .toString();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(mPwd)) {
            mToastor.getSingletonToast(R.string.empty_account_or_pwd).show();
            return;
        }
        showProgress();
        Map<String, String> params = new HashMap<>();
        params.put("username", account);
        params.put("password", mPwd);
        params.put("platform", "android");

        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "fullLogin")
                   .params(params)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           if(response.equals("3") || response.equals("2")){
                               mToastor.getSingletonToast(R.string.error_login).show();
                           }else{
                               //跳入到扫描界面
                               mToastor.getSingletonToast(R.string.success_login).show();
                               processJson(response);
                           }
                       }
                   });
    }

    private void processJson(String response) {
        Type           type  = new TypeToken<List<UserBean>>() {}.getType();
        Gson           gson  = new Gson();
        List<UserBean> datas = gson.fromJson(response, type);
        UserBean           user  = datas.get(0);
        mSpUtils.saveUserInfo(CommomUtil.bean2user(user,mPwd));
        setAligs();
        finish();
        overridePendingTransition(0,0);
    }
}
