package com.tylz.aelos.activity;

import android.content.Intent;
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
 *  @文件名:   RegisterActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/9 16:00
 *  @描述：    注册账户
 */
public class PhoneVerifyActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton  mIvLeft;
    @Bind(R.id.tv_title)
    TextView     mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton  mIvRight;
    @Bind(R.id.tv_code)
    Button     mTvCode;
    @Bind(R.id.et_phone)
    EditText     mEtPhone;
    @Bind(R.id.et_code)
    EditText     mEtCode;
    @Bind(R.id.ll_gologin)
    LinearLayout mLlGologin;
    @Bind(R.id.bt_next)
    Button       mBtNext;
    private String    mLocalVerifyCode;
    private TimeCount mTimeCount;
    private boolean  isEnable   = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.phone_verify);
        if (mTimeCount == null) {
            mTimeCount = new TimeCount(60000, 1000);
        }
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
    @OnClick({R.id.iv_left,
              R.id.tv_code,
              R.id.ll_gologin,
              R.id.bt_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
               finish();
                break;
            case R.id.tv_code:
                getCode();
                break;
            case R.id.ll_gologin:
                finish();
                break;
            case R.id.bt_next:
                next();
                break;
        }
    }

    /**
     * 下一步
     */
    private void next() {
        String phone = mEtPhone.getText()
                               .toString();
        String verifycode = mEtCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(R.string.empty_phone);
            mToastor.getSingletonToast(R.string.empty_phone).show();
            return;
        }else if(TextUtils.isEmpty(verifycode)){
            mToastor.getSingletonToast(R.string.empty_verify_code).show();
            return;
        }else if(!verifycode.equals(mLocalVerifyCode)){
            mToastor.getSingletonToast(R.string.error_verify_code).show();
            return;
        }
        //跳转注册界面
        if(isEnable){
            //skipActivityF(RegisterActivity.class);
            Intent intent = new Intent(this,RegisterActivity.class);
            intent.putExtra(RegisterActivity.EXTRA_PHONE,phone);
            startActivity(intent);
            finish();
        }else{
            mToastor.getSingletonToast(R.string.error_verify_code).show();
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phone = mEtPhone.getText()
                               .toString();
        if (TextUtils.isEmpty(phone)) {
            mToastor.getSingletonToast(R.string.empty_phone).show();
            return;
        } else if (!StringUtils.isMobileNO(phone)) {
            mToastor.getSingletonToast(R.string.error_phone).show();
            return;
        }
        setVerifyCode();
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "sms")
                   .addParams("phone", phone)
                   .addParams("type", "register")
                   .addParams("code", mLocalVerifyCode)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           if (response.equals("true")) {
                               //...
                               isEnable = true;
                               mEtPhone.setEnabled(false);
                               mTimeCount.start();
                           } else {
                               mToastor.getSingletonToast(R.string.tip_registed_phone).show();
                           }
                       }
                   });
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
