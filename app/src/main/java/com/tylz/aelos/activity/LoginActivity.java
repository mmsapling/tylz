package com.tylz.aelos.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.bean.UserBean;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.ToastUtils;
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
 *  @文件名:   LoginActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/13 15:48
 *  @描述：    TODO
 */
public class LoginActivity
        extends BaseActivity
{

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Bind(R.id.et_user)
    EditText mEtUser;
    @Bind(R.id.tv_forget)
    TextView mTvForget;
    @Bind(R.id.et_pwd)
    EditText mEtPwd;
    @Bind(R.id.bt_login)
    Button   mBtLogin;
    @Bind(R.id.tv_quick_experience)
    TextView mTvQuickExperience;
    @Bind(R.id.tv_register_accout)
    TextView mTvRegisterAccout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.tv_forget,
              R.id.bt_login,
              R.id.tv_quick_experience,
              R.id.tv_register_accout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget:
                skipActivity(ForgetPwdActivity.class);
                break;
            case R.id.bt_login:
                doLogin();
                break;
            case R.id.tv_quick_experience:
               skipActivityF(ScanBleActivity.class);
                break;
            case R.id.tv_register_accout:
                skipActivity(PhoneVerifyActivity.class);
                break;
        }
    }

    /**
     * 登录
     */
    private void doLogin() {
        String user = mEtUser.getText()
                             .toString();
        String pwd = mEtPwd.getText()
                           .toString();
        if(TextUtils.isEmpty(user)){
            Toast.makeText(this,R.string.empty_username,Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,R.string.empty_password,Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", user);
        params.put("password", pwd);
        params.put("platform", "android");
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "fullLogin")
                   .params(params)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           if(response.equals("3") || response.equals("2")){
                               ToastUtils.showToast(R.string.error_login);
                           }else{
                               //跳入到扫描界面
                               ToastUtils.showToast(R.string.success_login);
                               processJson(response);
                           }
                       }
                   });

    }

    /**
     * 处理登录返回回来信息
     * @param json
     *      json字符串
     */
    private void processJson(String json) {
        Type           type  = new TypeToken<List<UserBean>>() {}.getType();
        Gson           gson  = new Gson();
        List<UserBean> datas = gson.fromJson(json, type);
        UserBean           user  = datas.get(0);
        User userInfo = CommomUtil.bean2user(user,
                                          mEtPwd.getText()
                                                .toString());
        mSpUtils.saveUserInfo(userInfo);
        setAligs();
        //跳转到扫描界面
        skipActivityF(ScanBleActivity.class);
    }

}
