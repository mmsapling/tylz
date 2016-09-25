package com.tylz.aelos.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.MainHelpData;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

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
    VideoView   mVideoview;
    @Bind(R.id.tv_question)
    TextView    mTvQuestion;
    @Bind(R.id.tv_answer)
    TextView    mTvAnswer;
    @Bind(R.id.iv_bg_video)
    ImageView   mIvBgVideo;
    @Bind(R.id.pb_progress)
    ProgressBar mPbProgress;
    @Bind(R.id.ib_video_play)
    ImageButton mIbVideoPlay;
    private MainHelpData mHelpData;
    private int mVideoviewCurrentPosition = -1;

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
        mHelpData = (MainHelpData) getIntent().getSerializableExtra(EXTRA_HELP);
        mTvTitle.setText(R.string.help_detail);
        mTvAnswer.setText(mHelpData.answer);
        mTvQuestion.setText(mHelpData.question);
             /*去网络获取视频第一帧*/
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final Bitmap videoThumbnail = CommomUtil.createVideoThumbnail(
                                              mHelpData.url,
                                              480,
                                              480);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mIvBgVideo.setImageBitmap(videoThumbnail);
                                        }
                                    });

                                  }
                              });

        MediaController mediaController = new MediaController(this);
        // mediaController.set
        mVideoview.setMediaController(mediaController);
        /*视频中间按钮播放*/
        mIbVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWifi = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
                boolean wifi   = CommUtils.isWifi(getApplicationContext());
                if(!wifi && isWifi){
                   showPlayTip();
                }else{
                    mPbProgress.setVisibility(View.VISIBLE);
                    mIbVideoPlay.setVisibility(View.GONE);
                    mVideoview.setVideoPath(mHelpData.url);
                    mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mIvBgVideo.setVisibility(View.GONE);
                            mPbProgress.setVisibility(View.GONE);
                            mVideoview.start();
                        }
                    });
                }
            }
        });
    }

    private void showPlayTip() {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.not_wifi_conn_play))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      mPbProgress.setVisibility(View.VISIBLE);
                                      mIbVideoPlay.setVisibility(View.GONE);
                                      mVideoview.setVideoPath(mHelpData.url);
                                      mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                          @Override
                                          public void onPrepared(MediaPlayer mp) {
                                              mIvBgVideo.setVisibility(View.GONE);
                                              mPbProgress.setVisibility(View.GONE);
                                              mVideoview.start();
                                          }
                                      });
                                  }
                              })
                              .show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoview != null && mVideoview.isPlaying()) {
            /*记录当前位置*/
            mVideoviewCurrentPosition = mVideoview.getCurrentPosition();
            mVideoview.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoviewCurrentPosition != -1) {
            if (mVideoview != null && mVideoview.isPlaying()) {
                mVideoview.seekTo(mVideoviewCurrentPosition);
            } else {
                mVideoview.seekTo(mVideoviewCurrentPosition);
            }
        }
    }

    @OnClick({R.id.iv_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }
}
