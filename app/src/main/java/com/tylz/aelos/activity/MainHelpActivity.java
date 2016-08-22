package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.MainHelpData;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   MainHelpActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:33
 *  @描述：    主页帮助
 */
public class MainHelpActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton  mIvLeft;
    @Bind(R.id.tv_title)
    TextView     mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton  mIvRight;
    @Bind(R.id.help_tv_detail)
    TextView     mHelpTvDetail;
    @Bind(R.id.ll_help1)
    LinearLayout mLlHelp1;
    @Bind(R.id.ll_help2)
    LinearLayout mLlHelp2;
    @Bind(R.id.ll_help3)
    LinearLayout mLlHelp3;
    @Bind(R.id.ll_help4)
    LinearLayout mLlHelp4;
    TextView tvQuestion = null;
    TextView tvAnswer   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_help);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTvTitle.setText(R.string.help);
    }

    @OnClick({R.id.iv_left,
              R.id.ll_help1,
              R.id.ll_help2,
              R.id.ll_help3,
              R.id.ll_help4})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.ll_help1:
                tvQuestion = (TextView) mLlHelp1.getChildAt(0);
                tvAnswer = (TextView) mLlHelp1.getChildAt(1);
                enterDetail(new MainHelpData(1,
                                             tvQuestion.getText()
                                                       .toString(),
                                             tvAnswer.getText()
                                                     .toString()));
                break;
            case R.id.ll_help2:
                tvQuestion = (TextView) mLlHelp2.getChildAt(0);
                tvAnswer = (TextView) mLlHelp2.getChildAt(1);
                enterDetail(new MainHelpData(2,
                                         tvQuestion.getText()
                                                   .toString(),
                                         tvAnswer.getText()
                                                 .toString()));
                break;
            case R.id.ll_help3:
                tvQuestion = (TextView) mLlHelp3.getChildAt(0);
                tvAnswer = (TextView) mLlHelp3.getChildAt(1);
                enterDetail(new MainHelpData(3,
                                         tvQuestion.getText()
                                                   .toString(),
                                         tvAnswer.getText()
                                                 .toString()));
                break;
            case R.id.ll_help4:
                tvQuestion = (TextView) mLlHelp3.getChildAt(0);
                tvAnswer = (TextView) mLlHelp3.getChildAt(1);
                enterDetail(new MainHelpData(4,
                                         tvQuestion.getText()
                                                   .toString(),
                                         tvAnswer.getText()
                                                 .toString()));
                break;
        }
    }


    /**
     * 进入视频详情帮助
     */
    private void enterDetail(MainHelpData helpBean)
    {
        Intent intent = new Intent(MainHelpActivity.this, VideoDetailHelpActivity.class);
        intent.putExtra(VideoDetailHelpActivity.EXTRA_HELP, helpBean);
        startActivity(intent);
    }


}
