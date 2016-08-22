package com.tylz.aelos.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.MainHelpData;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   VideoDetailHelpActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 12:54
 *  @描述：    视频帮助详情
 */
public class VideoDetailHelpActivity
        extends BaseActivity
{
    public static final String EXTRA_HELP = "extra_help";
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton mIvRight;
    @Bind(R.id.help_vv)
    VideoView   mHelpVv;
    @Bind(R.id.tv_question)
    TextView    mTvQuestion;
    @Bind(R.id.tv_answer)
    TextView    mTvAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail_help);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        MainHelpData helpData = (MainHelpData) getIntent().getSerializableExtra(EXTRA_HELP);
        mTvTitle.setText(R.string.help_detail);
        mTvAnswer.setText(helpData.answer);
        mTvQuestion.setText(helpData.question);
    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }
}
