package com.tylz.aelos.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   AboutActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:19
 *  @描述：    关于界面
 */
public class AboutActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton    mIvLeft;
    @Bind(R.id.tv_title)
    TextView       mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton    mIvRight;
    @Bind(R.id.rl_version_introduce)
    RelativeLayout mRlVersionIntroduce;
    @Bind(R.id.rl_check_update)
    RelativeLayout mRlCheckUpdate;
    @Bind(R.id.rl_instructions)
    RelativeLayout mRlInstructions;
    @Bind(R.id.rl_help)
    RelativeLayout mRlHelp;
    @Bind(R.id.rl_feedback)
    RelativeLayout mRlFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.about);
    }

    @OnClick({R.id.iv_left,
              R.id.rl_version_introduce,
              R.id.rl_check_update,
              R.id.rl_instructions,
              R.id.rl_help,
              R.id.rl_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.rl_version_introduce:
                skipActivity(VersionIntroduceActivity.class);
                break;
            case R.id.rl_check_update:
                mToastor.getSingletonToast(R.string.latest_version).show();
                break;
            case R.id.rl_instructions:
                skipActivity(InstructionsActivity.class);
                break;
            case R.id.rl_help:
                skipActivity(AboutHelpActivity.class);
                break;
            case R.id.rl_feedback:
                skipActivity(FeedBackActivity.class);
                break;
        }
    }
}
