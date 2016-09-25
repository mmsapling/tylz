package com.tylz.aelos.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ForgetPwdActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/10 13:52
 *  @描述：    忘记密码
 */
public class ForgetPwdActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton  mIvLeft;
    @Bind(R.id.tv_title)
    TextView     mTvTitle;
    @Bind(R.id.et_phone)
    EditText     mEtPhone;
    @Bind(R.id.tv_code)
    Button       mTvCode;
    @Bind(R.id.et_code)
    EditText     mEtCode;
    @Bind(R.id.et_pwd)
    EditText     mEtPwd;
    @Bind(R.id.bt_confirm)
    Button       mBtConfirm;
    @Bind(R.id.ll_gologin)
    LinearLayout mLlGologin;
    private String    mLocalVerifyCode;
    private TimeCount mTimeCount;
    private boolean  isEnable   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.password_retake);
        if (mTimeCount == null) {
            mTimeCount = new TimeCount(60000, 1000);
        }
    }

    @OnClick({R.id.iv_left,
              R.id.tv_code,
              R.id.bt_confirm,
              R.id.ll_gologin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_code:
                getCode();
                break;
            case R.id.bt_confirm:
                confirmPwdTake();
                break;
            case R.id.ll_gologin:
                finish();
                break;
        }
    }

    /**
     * 重置密码
     */
    private void confirmPwdTake() {
        String phone = mEtPhone.getText()
                               .toString();
        String verifycode = mEtCode.getText().toString();
        String pwd = mEtPwd.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mToastor.getSingletonToast(R.string.empty_phone).show();
            return;
        }else if(TextUtils.isEmpty(verifycode)){
            mToastor.getSingletonToast(R.string.empty_verify_code).show();
            return;
        }else if(TextUtils.isEmpty(pwd)){
            mToastor.getSingletonToast(R.string.empty_password).show();
            return;
        }else if(!verifycode.equals(mLocalVerifyCode)){
            mToastor.getSingletonToast(R.string.error_verify_code).show();
            return;
        }else if(!isEnable){
            mToastor.getSingletonToast(R.string.error_verify_code).show();
            return;
        }
        showProgress();
        OkHttpUtils.post().url(HttpUrl.BASE + "forget")
                .addParams("phone",phone)
                .addParams("newpass",pwd)
                .build().execute(new ResultCall() {
            @Override
            public void onResult(String response, int id) {
                if(response.equals("true")){
                    mToastor.getSingletonToast(R.string.success_pwd_retake).show();
                    //重置密码后，如果本地存在该用户信息，那么清理掉
                    User user = mSpUtils.getUserInfoBySp();
                    if(!TextUtils.isEmpty(user.phone)){
                        if(user.phone.equals(mEtPhone.getText().toString())){
                            mSpUtils.clearUserInfo();
                        }
                    }
                }else{
                    mToastor.getSingletonToast(R.string.not_exist_phone).show();
                }
                ForgetPwdActivity.this.finish();
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phone = mEtPhone.getText()
                               .toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(R.string.empty_phone);
            return;
        } else if (!StringUtils.isMobileNO(phone)) {
            ToastUtils.showToast(R.string.error_phone);
            return;
        }
        setVerifyCode();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "sms")
                   .addParams("phone", phone)
                   .addParams("type", "forget")
                   .addParams("code", mLocalVerifyCode)
                   .build()
                   .execute(new ResultCall(false) {
                       @Override
                       public void onResult(String response, int id) {
                           if (response.equals("true")) {
                               //...
                               isEnable = true;
                               mTimeCount.start();
                               mEtPhone.setEnabled(false);
                           } else {
                               mToastor.getSingletonToast(R.string.tip_registed_phone).show();
                           }
                       }
                   });
    }

    /**
     * 接口这么写，需要将本地生成验证码
     * 设置验证码
     */
    private void setVerifyCode() {
        Random random = new Random();
        mLocalVerifyCode = String.valueOf(random.nextInt(8999) + 1000);
        mSpUtils.putString("verify_code", mLocalVerifyCode);
    }
    /**
     * 计时器
     */
    private class TimeCount
            extends CountDownTimer
    {
        /**

         * @param millisInFuture The number of millis in the future from the call
         *   to {@link #start()} until the countdown is done and {@link #onFinish()}
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTvCode.setClickable(false);
            String str = UIUtils.getString(R.string.time_count);
            mTvCode.setText(millisUntilFinished / 1000 + str);
        }

        @Override
        public void onFinish() {
            mTvCode.setClickable(true);
            mTvCode.setText(R.string.retry_verify_code);
        }
    }
}
