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
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   FeedBackActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 16:19
 *  @描述：    意见反馈界面
 */
public class FeedBackActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton mIvRight;
    @Bind(R.id.et_content)
    EditText    mEtContent;
    @Bind(R.id.bt_feedback)
    Button      mBtFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.feedback);

    }

    @OnClick({R.id.iv_left,
              R.id.bt_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.bt_feedback:
                feedback();
                break;
        }
    }

    /**
     * 反馈
     */
    private void feedback() {
        String feedback = mEtContent.getText()
                             .toString();
        if(TextUtils.isEmpty(feedback)){
            ToastUtils.showToast(R.string.empty_feedback);
            return;
        }
        /*得到用户的id*/
        String id = mSpUtils.getString(Constants.USER_ID, "");
        showProgress();
        OkHttpUtils.post().url(HttpUrl.BASE + "Opinion")
                .addParams("id",id)
                .addParams("content",feedback)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                closeProgress();
            }

            @Override
            public void onResponse(String response, int id) {
                closeProgress();
                ToastUtils.showToast(R.string.tip_thanks_feedback);
                /*退出当前页面*/
                FeedBackActivity.this.finish();
            }
        });
    }
}
