package com.tylz.aelos.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.CommentAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.ActionDetailBean;
import com.tylz.aelos.bean.Comment;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.HttpUtil;
import com.tylz.aelos.util.KeyBoardUtils;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;
import com.tylz.aelos.view.DNumProgressDialog;
import com.tylz.aelos.view.LoadMoreListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ActionDetailActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/27 18:03
 *  @描述：    动作详情界面
 */
public class ActionDetailActivity
        extends BaseActivity
        implements LoadMoreListView.OnLoadMore,
                   View.OnClickListener,
                   AdapterView.OnItemClickListener
{
    public static final  String EXTRA_DATA           = "extra_data";
    public static final  String EXTRA_POS            = "extra_pos";
    public static final  int    RESULT_CODE_RETURN   = 2000;
    private static final int    WHAT_LOAD_DATA       = 0;
    private static final int    WHAT_DOWLOAD_DATA    = 1;
    private static final int    WHAT_COMMENT         = 2;
    private static final int    WHAT_COMMENTS_DATA   = 3;
    private static final int    REQUEST_COMMENT_CODE = 3000;

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
    private ShopBean           mShopBean;
    private ActionDetailBean   mActionDetailBean;
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
    private DNumProgressDialog mNumProgressDialog;
    private int                mPage;
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
        initShopBeanData();
        loadDataForNet();
        loadCommentFroNet(1);
    }

    /**
     * 从网络加载评论数据
     * 1.默认页数是从1开始
     * 2.传0代表清除数据源，继续从1开始
     */
    private void loadCommentFroNet(int page) {
        if (page == 0) {
            mDatas.clear();
            mPage = 1;
        } else {
            mPage = page;
        }
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("goodsid", mShopBean.id);
                                      params.put("page", mPage + "");
                                        /*默认展示1000*/
                                      params.put("number", Constants.PAGE_SIZE);
                                      final String commentsJson = HttpUtil.doPost("getComments",
                                                                                  params);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              Type type = new TypeToken<List<Comment>>() {}.getType();
                                              Gson gson = new Gson();
                                              List<Comment> comments = gson.fromJson(commentsJson,
                                                                                     type);
                                              if (comments != null && comments.size() != 0) {
                                                  mDatas.addAll(comments);
                                                  mAdapter.notifyDataSetChanged();
                                                  mTvCommentCount.setText(mDatas.size() + "");
                                              }
                                          }
                                      });
                                  }
                              });
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
        mListview.setOnItemClickListener(this);
    }

    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 初始化数据
     * 1.设置标题
     * 2.类型，时长
     * 3.是否
     */
    private void initShopBeanData() {

        mShopBean = (ShopBean) getIntent().getSerializableExtra(EXTRA_DATA);
        mTvTitle.setText(mShopBean.title);
        mTvType.setText(mShopBean.type);
        mTvTime.setText(mShopBean.second);
        /*是否收藏过*/
        if (mShopBean.iscollect.equals("true")) {
            // mIvCollect.setPressed(true);
            mIvCollect.setImageResource(R.mipmap.heart_press);
        } else {
            mIvCollect.setImageResource(R.mipmap.heart_normal);
        }
        /*是否下载，需要从本地判断，暂时没做*/
        if (mDbHelper.isExistActionId(mShopBean.id)) {
            mIvDownload.setEnabled(false);
        } else {
            mIvDownload.setEnabled(true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOAD_DATA:
                    closeProgress();
                    String data = (String) msg.obj;
                    processData(data);
                    break;
                case WHAT_DOWLOAD_DATA:
                    int progress = (int) msg.obj;
                    if (mNumProgressDialog != null) {
                        mNumProgressDialog.setProgress(progress);
                    }
                    break;
                case WHAT_COMMENT:
                    String commentResult = (String) msg.obj;
                    processCommentData(commentResult);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 处理评论返回数据
     * 1.评论成功 重新请求评论接口
     * 2.清空评论输入框
     * @param commentResult
     *         json
     */
    private void processCommentData(String commentResult) {
        closeProgress();
        LogUtils.d("comment result = " + commentResult);
        try {
            JSONArray  jsonArray    = new JSONArray(commentResult);
            JSONObject jsonObject   = jsonArray.getJSONObject(0);
            String     commentCount = jsonObject.getString("commentCount");
            mTvCommentCount.setText(commentCount);
            boolean result = jsonObject.getBoolean("result");
            mEtContent.setText("");
            if (result) {
                mToastor.getSingletonToast(R.string.success_comment)
                        .show();
                KeyBoardUtils.closeKeybord(mEtContent, this);
                loadCommentFroNet(0);
            } else {
                mToastor.getSingletonToast(R.string.fail_comment)
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这个页面数据的处理
     * @param data
     *         json字符串
     */
    private void processData(String data) {
        if (TextUtils.isEmpty(data)) {
            //ToastUtils.showToast(R.string.tip_check_net);
        } else {
            Type                   type  = new TypeToken<List<ActionDetailBean>>() {}.getType();
            Gson                   gson  = new Gson();
            List<ActionDetailBean> datas = gson.fromJson(data, type);
            mActionDetailBean = datas.get(0);
            setAllView();
            initVideoView();
        }

    }

    /**
     * 初始化视频控件
     */
    private void initVideoView() {
        if (mShopBean.hasAction.equals("false")) {
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
                                              mActionDetailBean.video,
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
                if (TextUtils.isEmpty(mActionDetailBean.video)) {
                    mToastor.getSingletonToast(R.string.empty_video_path);
                    return;
                }
                boolean isWifi = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
                boolean wifi   = CommUtils.isWifi(getApplicationContext());
                if (!wifi && isWifi) {
                    showPlayTip();
                } else {
                    mPbProgress.setVisibility(View.VISIBLE);
                    mIbVideoPlay.setVisibility(View.GONE);
                    mVideoview.setVideoPath(mActionDetailBean.video);
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
                                      mVideoview.setVideoPath(mActionDetailBean.video);
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

    /**
     * 获取网络的数据后，设置整个界面视图
     */
    private void setAllView() {

        /*如果当前用户赞过，那么设置点赞按压下过，显示点赞数量，否则显示数量*/
        mTvPraiseCount.setText(mActionDetailBean.applaudCount);
        if (mActionDetailBean.isApplaud.equals("true")) {
            // mIvPraise.setPressed(true);
            mIvPraise.setImageResource(R.mipmap.good_press);
        } else {
            // mIvPraise.setPressed(false);
            mIvPraise.setImageResource(R.mipmap.good_normal);
        }
        mTvCommentCount.setText(mActionDetailBean.commentCount);
        mTvCollectCount.setText(mActionDetailBean.collectCount);
        mTvDownloadCount.setText(mActionDetailBean.downloadCount);
        mExpandTextView.setText(mActionDetailBean.content);
    }

    /**
     * 从网络加载数据
     */
    private void loadDataForNet() {
        //getGoods
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      String id = mSpUtils.getString(Constants.USER_ID, "");
                                      Map<String, String> params = new HashMap<>();
                                      params.put("id", mShopBean.id);
                                      params.put("userid", id);
                                      String  data = HttpUtil.doPost("getGoods", params);
                                      Message msg  = Message.obtain();
                                      msg.what = WHAT_LOAD_DATA;
                                      msg.obj = data;
                                      mHandler.sendMessage(msg);
                                  }
                              });

    }

    /**
     * 下载操作
     */
    private void download() {
        String fileName = CommomUtil.getFileNameByUrl(mActionDetailBean.audio);
        OkHttpUtils.get()
                   .url(mActionDetailBean.audio)
                   .build()
                   .execute(new FileCallBack(Constants.DIR_AUDIO, fileName) {
                       @Override
                       public void onError(Call call, Exception e, int id) {
                           closeProgress();
                           closeNumProcess();
                           mToastor.getSingletonToast(R.string.tip_check_net)
                                   .show();
                       }

                       @Override
                       public void onResponse(File response, int id) {
                           UIUtils.postTaskSafely(new Runnable() {
                               @Override
                               public void run() {
                                   closeNumProcess();
                                   long result = mDbHelper.insertAction(mActionDetailBean, "false");
                                   if (result != -1) {
                                       mShopBean.isdownload = "true";
                                       mIvDownload.setEnabled(false);
                                       mToastor.getSingletonToast(R.string.success_download)
                                               .show();
                                   } else {
                                       mShopBean.isdownload = "false";
                                       mIvDownload.setEnabled(true);
                                   }
                                   loadDownLoadCountFromNet();
                               }
                           });
                       }

                       @Override
                       public void inProgress(final float progress, long total, int id) {

                           UIUtils.postTaskSafely(new Runnable() {
                               @Override
                               public void run() {
                                   int index = (int) (progress * 100);
                                   if (mNumProgressDialog != null) {
                                       mNumProgressDialog.setProgress(index);
                                   }
                               }
                           });
                       }
                   });
    }

    /**从网络重新加载下载次数*/
    private void loadDownLoadCountFromNet() {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("goodsid", mShopBean.id);
                                      final String countDownload = HttpUtil.doPost("countDownload",
                                                                                   params);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              try {
                                                  JSONArray  jsonArray  = new JSONArray(
                                                          countDownload);
                                                  JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                  String downloadCount = jsonObject.getString(
                                                          "downloadCount");
                                                  mTvDownloadCount.setText(downloadCount);
                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                                  mTvDownloadCount.setText(mActionDetailBean.downloadCount);
                                              }
                                          }
                                      });
                                  }
                              });
    }

    /**
     * 携带数据返回
     */
    private void goBack() {
        int    position = getIntent().getIntExtra(EXTRA_POS, -1);
        Intent intent   = new Intent();
        intent.putExtra(EXTRA_DATA, mShopBean);
        intent.putExtra(EXTRA_POS, position);
        setResult(RESULT_CODE_RETURN, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    public void loadMore() {
        mListview.onLoadComplete();
    }

    @Override
    public void onClick(View v) {
        if (!isLogin() && v != mIvLeft) {
            showLoginTip();
        } else if (v == mIvLeft) {
            goBack();
        } else {
            if (mActionDetailBean == null) {
                mToastor.getSingletonToast(R.string.fail_operation)
                        .show();
                return;
            }

            if (v == mLlDownload) {
                if (mShopBean.hasAction.equals("false")) {
                    mToastor.getSingletonToast(R.string.support_preview)
                            .show();
                    return;
                }
                if (mDbHelper.isExistActionId(mShopBean.id)) {
                    mToastor.getSingletonToast(R.string.downloaded)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(mActionDetailBean.audio)) {
                    //不下载音频，直接保存数据
                    long result = mDbHelper.insertAction(mActionDetailBean, "false");
                    if (result != -1) {
                        mShopBean.isdownload = "true";
                        mIvDownload.setEnabled(false);
                        mToastor.getSingletonToast(R.string.success_download)
                                .show();
                    } else {
                        mShopBean.isdownload = "false";
                        mIvDownload.setEnabled(true);
                    }
                    loadDownLoadCountFromNet();
                } else {
                    showNumProcess();
                    download();
                }

            } else if (v == mLlCollect) {
                collect();
            } else if (v == mLlComment) {
                Intent intent = new Intent(this, AllCommentActivity.class);
                intent.putExtra(EXTRA_DATA, mActionDetailBean);
                startActivity(intent);
            } else if (v == mLlPriase) {
                praise();
            } else if (v == mBtnSend) {
                sendComment();
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //loadCommentFroNet(0);
    }

    /**发表评论*/
    private void sendComment() {
        final String comment = mEtContent.getText()
                                         .toString();
        if (TextUtils.isEmpty(comment)) {
            mToastor.getSingletonToast(R.string.empty_comment)
                    .show();
            return;
        }
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("goodsid", mShopBean.id);
                                      params.put("userid", mUser_id);
                                      params.put("content", comment);
                                      params.put("linkid", "");
                                      params.put("commentid", "");
                                      String  commentResult = HttpUtil.doPost("comment", params);
                                      Message msg           = Message.obtain();
                                      msg.what = WHAT_COMMENT;
                                      msg.obj = commentResult;
                                      mHandler.sendMessage(msg);

                                  }
                              });

    }

    /**展示进度条弹窗*/
    private void showNumProcess() {
        mNumProgressDialog = new DNumProgressDialog(this);
        if (!mNumProgressDialog.isShowing()) {
            mNumProgressDialog.showDialog();
        }
    }

    /**关闭进度条弹窗*/
    private void closeNumProcess() {
        if (mNumProgressDialog != null) {
            mNumProgressDialog.dismiss();
            mNumProgressDialog = null;
        }
    }

    /**
     * 点赞此动作，以及界面逻辑处理
     */
    private void praise() {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("userid", mUser_id);
                                      params.put("goodsid", mShopBean.id);
                                      final String praiseData = HttpUtil.doPost("applaud", params);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              if (!TextUtils.isEmpty(praiseData)) {
                                                  processPraiseData(praiseData);
                                              } else {
                                                  mToastor.getSingletonToast(R.string.fail_praise)
                                                          .show();
                                              }
                                          }
                                      });
                                  }
                              });
    }

    /**
     * 处理点赞返回数据
     * @param json
     */
    private void processPraiseData(String json) {
        try {
            JSONArray  jsonArray    = new JSONArray(json);
            JSONObject jsonObject   = jsonArray.getJSONObject(0);
            String     applaudCount = jsonObject.getString("applaudCount");
            boolean    result       = jsonObject.getBoolean("result");
            mTvPraiseCount.setText(applaudCount);
            if (result) {
                if (mActionDetailBean.isApplaud.equals("true")) {
                    //                    mIvPraise.setEnabled(true);
                    //ToastUtils.showToast(R.string.cancel_praise);
                    mActionDetailBean.isApplaud = "false";
                    mIvPraise.setImageResource(R.mipmap.good_normal);
                } else {
                    //                    mIvPraise.setEnabled(false);
                    mActionDetailBean.isApplaud = "true";
                    mIvPraise.setImageResource(R.mipmap.good_press);
                    //ToastUtils.showToast(R.string.success_praise);
                }
            } else {
                ToastUtils.showToast(R.string.fail_operation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收藏此动作，以及界面数据逻辑处理
     */
    private void collect() {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("id", mUser_id);
                                      params.put("goodsid", mShopBean.id);
                                      final String collectData = HttpUtil.doPost("collect", params);
                                      LogUtils.d("collectdata = " + collectData);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              if (!TextUtils.isEmpty(collectData)) {
                                                  processCollectData(collectData);
                                              } else {
                                                  mToastor.getSingletonToast(R.string.fail_operation)
                                                          .show();
                                              }
                                          }
                                      });
                                  }
                              });
    }

    /**
     * 收藏数据处理及界面逻辑
     * @param json
     */
    private void processCollectData(String json) {
        try {
            String     success_collect = UIUtils.getString(R.string.success_collect);
            JSONArray  jsonArray       = new JSONArray(json);
            JSONObject jsonObject      = jsonArray.getJSONObject(0);
            String     result          = jsonObject.getString("result");
            String     collectCount    = jsonObject.getString("collectCount");
            mTvCollectCount.setText(collectCount);
            if (result.equals(success_collect)) {
                mShopBean.iscollect = "true";
                //ToastUtils.showToast(R.string.success_collect);
                //                mIvCollect.setEnabled(false);
                mIvCollect.setImageResource(R.mipmap.heart_press);
            } else {
                // ToastUtils.showToast(R.string.cancel_collect);
                mShopBean.iscollect = "false";
                //                mIvCollect.setEnabled(true);
                mIvCollect.setImageResource(R.mipmap.heart_normal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int pos = position - 1;
        if (pos >= 0) {
            Intent  intent  = new Intent(this, ReplyCommentActivity.class);
            Comment comment = mDatas.get(pos);
            intent.putExtra(ReplyCommentActivity.EXTRA_DATA, comment);
            intent.putExtra(ReplyCommentActivity.EXTRA_POS, pos);
            intent.putExtra(ReplyCommentActivity.EXTRA_GOODSID, mShopBean.id);
            startActivityForResult(intent, REQUEST_COMMENT_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COMMENT_CODE && resultCode == ReplyCommentActivity.RESULT_COMMEND_CODE) {
            if (data != null) {
                Comment comment = (Comment) data.getSerializableExtra(ReplyCommentActivity.EXTRA_DATA);
                int     postion = data.getIntExtra(ReplyCommentActivity.EXTRA_POS, -1);
                if (postion != -1) {
                    mDatas.set(postion, comment);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
