package com.tylz.aelos.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.CommentAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.Comment;
import com.tylz.aelos.bean.UploadBean;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ActionDetailActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/27 18:03
 *  @描述：    上传动作详情界面
 */
public class UploadActionDetailActivity
        extends BaseActivity
        implements LoadMoreListView.OnLoadMore, View.OnClickListener
{
    public static final  String EXTRA_DATA           = "extra_data";
    @Bind(R.id.tv_title)
    TextView  mTvTitle;
    @Bind(R.id.videoview)
    VideoView mVideoview;
    @Bind(R.id.iv_bg_video)
    ImageView mIvBgVideo;
    ExpandableTextView mExpandTextView;
    @Bind(R.id.listview)
    LoadMoreListView mListview;
    @Bind(R.id.et_content)
    EditText         mEtContent;
    @Bind(R.id.pb_progress)
    ProgressBar      mPbProgress;
    @Bind(R.id.ib_video_play)
    ImageButton      mIbVideoPlay;
    private TextView           mTvType;
    private TextView           mTvTime;
    private ImageButton        mIvLeft;
    private Button             mBtnSend;
    private UploadBean         mUploadBean;
    private TextView           mTvDownloadCount;
    private TextView           mTvCollectCount;
    private TextView           mTvCommentCount;
    private TextView           mTvPraiseCount;
    private ImageView          mIvDownload;
    private ImageView          mIvCollect;
    private ImageView          mIvPraise;
    private LinearLayout       mLlDownload;
    private LinearLayout       mLlCollect;
    private LinearLayout       mLlComment;
    private LinearLayout       mLlPriase;
    private DbHelper           mDbHelper;
    private List<Comment>      mDatas;
    private CommentAdapter     mAdapter;
    private int mVideoviewCurrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_detail);
        ButterKnife.bind(this);
        init();
        initListView();
        initUploadBeanData();
    }


    private void init() {
        mDbHelper = new DbHelper(this);
        mIvLeft = (ImageButton) findViewById(R.id.iv_left);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mIvLeft.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
    }

    /**
     * 初始化listview视图
     */
    private void initListView() {
        mDatas = new ArrayList<>();
        mAdapter = new CommentAdapter(this, mDatas);
        View headView = View.inflate(this, R.layout.include_action_operation, null);
        mTvDownloadCount = (TextView) headView.findViewById(R.id.tv_download);
        mTvCollectCount = (TextView) headView.findViewById(R.id.tv_collect);
        mTvCommentCount = (TextView) headView.findViewById(R.id.tv_comment_count);
        mTvPraiseCount = (TextView) headView.findViewById(R.id.tv_prise_count);
        mExpandTextView = (ExpandableTextView) headView.findViewById(R.id.expand_text_view);
        mTvType = (TextView) headView.findViewById(R.id.tv_type);
        mTvTime = (TextView) headView.findViewById(R.id.tv_time);
        mIvDownload = (ImageView) headView.findViewById(R.id.iv_download);
        mIvCollect = (ImageView) headView.findViewById(R.id.iv_collect);
        mIvPraise = (ImageView) headView.findViewById(R.id.iv_prise);
        mLlDownload = (LinearLayout) headView.findViewById(R.id.ll_download);
        mLlCollect = (LinearLayout) headView.findViewById(R.id.ll_collect);
        mLlComment = (LinearLayout) headView.findViewById(R.id.ll_comment);
        mLlPriase = (LinearLayout) headView.findViewById(R.id.ll_praise);
        mLlCollect.setOnClickListener(this);
        mLlComment.setOnClickListener(this);
        mLlDownload.setOnClickListener(this);
        mLlPriase.setOnClickListener(this);
        mListview.addHeaderView(headView);
        mListview.setAdapter(mAdapter);
        mListview.setLoadMoreListen(this);
    }

    /**
     * 初始化数据
     * 1.设置标题
     * 2.类型，时长
     * 3.是否
     */
    private void initUploadBeanData() {

        mUploadBean = (UploadBean) getIntent().getSerializableExtra(EXTRA_DATA);
        mTvTitle.setText(mUploadBean.title);
        mTvType.setText(mUploadBean.type);
        mTvTime.setText(mUploadBean.second);
        mExpandTextView.setText(mUploadBean.content);
        /*是否收藏过*/
        if (mUploadBean.iscollect.equals("true")) {
            mIvCollect.setPressed(true);
        }
        /*是否下载，需要从本地判断，暂时没做*/
        if (mDbHelper.isExistActionId(mUploadBean.id)) {
            mIvDownload.setEnabled(false);
        } else {
            mIvDownload.setEnabled(true);
        }
        initVideoView();
    }

    /**
     * 初始化视频控件
     */
    private void initVideoView() {
        if (mUploadBean.hasAction.equals("false")) {
            mIvDownload.setImageResource(R.drawable.selector_icon_prew);
            mIvDownload.setSelected(true);
            mIvDownload.setPressed(true);
            mTvDownloadCount.setText(R.string.preview);
        }

            /*去网络获取视频第一帧*/
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final Bitmap videoThumbnail = CommomUtil.createVideoThumbnail(
                                              mUploadBean.video,
                                              480,
                                              480);
                                      UIUtils.postTaskSafely(new Runnable() {
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
                mPbProgress.setVisibility(View.VISIBLE);
                mIbVideoPlay.setVisibility(View.GONE);
                mVideoview.setVideoPath(mUploadBean.video);
                mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mIvBgVideo.setVisibility(View.GONE);
                        mPbProgress.setVisibility(View.GONE);
                        mVideoview.start();
                    }
                });
            }
        });
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
    public void loadMore() {
        mListview.onLoadComplete();
    }

    @Override
    public void onClick(View v) {
        if (v == mIvLeft) {
            finish();
        } else {
            mToastor.getSingletonToast(R.string.support_preview).show();
        }

    }
}
