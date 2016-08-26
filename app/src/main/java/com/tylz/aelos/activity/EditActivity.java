package com.tylz.aelos.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   EditActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/11 14:59
 *  @描述：    修改密码
 */
public class EditActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.et_original_pwd)
    EditText    mEtOriginalPwd;
    @Bind(R.id.et_new_pwd)
    EditText    mEtNewPwd;
    @Bind(R.id.et_confrim_pwd)
    EditText    mEtConfrimPwd;
    @Bind(R.id.btn_save)
    Button      mBtnSave;
    private String mNewPwd;
    private User   mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.edit_pwd);
        mUserInfo = mSpUtils.getUserInfoBySp();
    }

    @OnClick({R.id.iv_left,
              R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_save:
                if(checkInput()){
                    editPwd();
                }
                break;
        }
    }

    /**
     * 修改密码
     */
    private void editPwd() {
        OkHttpUtils.post().url(HttpUrl.BASE + "repass")
                .addParams("id",mUserInfo.id)
                .addParams("oldpass",mUserInfo.password)
                .addParams("newpass",mNewPwd)
                .build().execute(new ResultCall() {
            @Override
            public void onResult(String response, int id) {
                if(response.equals("true")){
                    mToastor.getSingletonToast(R.string.success_edit).show();
                    //更新本地密码
                    mUserInfo.password = mNewPwd;
                    mSpUtils.saveUserInfo(mUserInfo);
                    EditActivity.this.finish();
                }else{
                    mToastor.getSingletonToast(R.string.error_original_pwd).show();
                }
            }
        });
    }

    /**
     * 校验输入
     * @return true 正确，false 输入不正确
     */
    private boolean checkInput() {

        String originalPwd = mEtOriginalPwd.getText()
                                           .toString();
        mNewPwd = mEtNewPwd.getText()
                           .toString();
        String confirmPwd = mEtConfrimPwd.getText()
                                         .toString();
        if (TextUtils.isEmpty(originalPwd)) {
            mToastor.getSingletonToast(R.string.hint_input_original_pwd).show();
            return false;
        } else if (TextUtils.isEmpty(mNewPwd)) {
            mToastor.getSingletonToast(R.string.hint_input_new_pwd).show();
            return false;
        } else if (TextUtils.isEmpty(confirmPwd)) {
            mToastor.getSingletonToast(R.string.hint_input_confrim_new_pwd).show();
            return false;
        } else if (!originalPwd.equals(mUserInfo.password)) {
            mToastor.getSingletonToast(R.string.error_original_pwd).show();
            return false;
        }else if(!mNewPwd.equals(confirmPwd)){
            mToastor.getSingletonToast(R.string.error_input_pwd_twice).show();
            return false;
        }
        return true;
    }
}
