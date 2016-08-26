package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.ReplyAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.Comment;
import com.tylz.aelos.bean.Reply;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.HttpUtil;
import com.tylz.aelos.util.KeyBoardUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.LoadMoreListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ReplyCommentActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/13 14:01
 *  @描述：    回复评论页面
 */
public class ReplyCommentActivity
        extends BaseActivity
{
    public static final  String EXTRA_DATA          = "extra_data";
    public static final  String EXTRA_POS           = "extra_pos";
    public static final  int    RESULT_COMMEND_CODE = 3000;
    public static final  String EXTRA_GOODSID       = "goodsid";
    private static final int    WHAT_REPLY_EMPTY_ID = 0;
    private static final int    WHAT_REPLY_LINK_ID  = 1;
    private static final int    WHAT_REPLY          = 2;
    private static final int    WHAT_ALL_REPLY      = 3;
    @Bind(R.id.iv_left)
    ImageButton        mIvLeft;
    @Bind(R.id.tv_title)
    TextView           mTvTitle;
    @Bind(R.id.civ_avator)
    CircleImageView    mCivAvator;
    @Bind(R.id.tv_username)
    TextView           mTvUsername;
    @Bind(R.id.tv_time)
    TextView           mTvTime;
    @Bind(R.id.expand_text_view)
    ExpandableTextView mTvContent;
    @Bind(R.id.ll_comment)
    LinearLayout       mLlComment;
    @Bind(R.id.listview)
    LoadMoreListView   mListview;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.tv_nothing)
    TextView           mTvNothing;
    @Bind(R.id.btn_send)
    Button             mBtnSend;
    @Bind(R.id.et_content)
    EditText           mEtContent;
    @Bind(R.id.rl_bottom)
    RelativeLayout     mRlBottom;
    private Comment mComment;
    private String  mGoodsId;
    private String mLinkId = "0";  //默认不回复任何人，如果有回复，那么linkid为所回复人的id
    private int          mPage;
    private List<Reply>  mDatas;
    private ReplyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_comment);
        ButterKnife.bind(this);
        init();
        loadReplyFromNet(1);
    }

    /**
     * 从网络加载评论数据
     * 1.默认页数是从1开始
     * 2.传0代表清除数据源，继续从1开始
     */
    private void loadReplyFromNet(int page) {
        if (page == 0) {
            mDatas.clear();
            mPage = 1;
        } else {
            mPage = page;
        }
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                           @Override
                                           public void run() {
                                               Map<String, String> params = new HashMap<String, String>();
                                               params.put("id", mComment.id);
                                               params.put("page", mPage + "");
                                        /*默认展示1000*/
                                               params.put("number", Constants.PAGE_SIZE);
                                               final String replyJson = HttpUtil.doPost("getReply", params);
                                               Message      msg       = Message.obtain();
                                               msg.obj = replyJson;
                                               msg.what = WHAT_ALL_REPLY;
                                               mHandler.sendMessage(msg);

                                           }

                                       }

                              );
    }

    private void init() {
        mComment = (Comment) getIntent().getSerializableExtra(EXTRA_DATA);
        mGoodsId = getIntent().getStringExtra(EXTRA_GOODSID);
        mTvTitle.setText(R.string.comment_detail);
        mTvContent.setText(mComment.content);
        mTvTime.setText(mComment.updateTime);
        mTvUsername.setText(mComment.nickname);
        Picasso.with(this)
               .load(mComment.avatar)
               .placeholder(R.mipmap.defaultavatar)
               .into(mCivAvator);
        mDatas = new ArrayList<>();
        mAdapter = new ReplyAdapter(this, mDatas);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Reply reply = mDatas.get(position);
                mLinkId = reply.id + "";
                String hint = "@" + reply.nickname;
                mEtContent.setHint(hint);
                mEtContent.requestFocus();
                KeyBoardUtils.openKeybord(mEtContent, ReplyCommentActivity.this);
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReplyFromNet(0);
            }
        });
        mListview.setLoadMoreListen(new LoadMoreListView.OnLoadMore() {
            @Override
            public void loadMore() {
                mListview.onLoadComplete();
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_REPLY:
                    closeProgress();
                    String reply = (String) msg.obj;
                    processReplyData(reply);
                    break;
                case WHAT_ALL_REPLY:
                    closeProgress();
                    mSwipeRefresh.setRefreshing(false);
                    String replyJson = (String) msg.obj;
                    if (TextUtils.isEmpty(replyJson)) {
                        mListview.setVisibility(View.GONE);
                        mTvNothing.setVisibility(View.VISIBLE);
                    } else {
                        mListview.setVisibility(View.VISIBLE);
                        mTvNothing.setVisibility(View.GONE);
                        Type        type   = new TypeToken<List<Reply>>() {}.getType();
                        Gson        gson   = new Gson();
                        List<Reply> replys = gson.fromJson(replyJson, type);
                        if (replys != null && replys.size() != 0) {
                            mDatas.addAll(replys);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    break;
            }
        }
    };

    /**
     * 处理回复返回的数据
     * @param reply
     */
    private void processReplyData(String reply) {
        if (TextUtils.isEmpty(reply)) {
            mToastor.getSingletonToast(R.string.tip_check_net).show();
            return;
        }
        mEtContent.setText("");
        try {
            JSONArray  jsonArray  = new JSONArray(reply);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String     result     = jsonObject.getString("result");
            if (result.equals("true")) {
                KeyBoardUtils.closeKeybord(mEtContent, ReplyCommentActivity.this);
                mToastor.getSingletonToast(R.string.success_reply).show();
                loadReplyFromNet(0);
            } else {
                mToastor.getSingletonToast(R.string.success_reply).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        KeyBoardUtils.closeKeybord(mEtContent, this);
        super.onDestroy();
    }

    @OnClick({R.id.iv_left,
              R.id.btn_send,
              R.id.ll_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                goBack();
                break;
            case R.id.btn_send:
                if (isLogin()) {
                    sendComment();
                } else {
                    showLoginTip();
                }
                break;
            case R.id.ll_comment:
                if (isLogin()) {
                    mLinkId = "0";
                    String hint = UIUtils.getString(R.string.hint_input_comment);
                    mEtContent.requestFocus();
                    mEtContent.setHint(hint);
                    //  KeyBoardUtils.openKeybord(mEtContent, ReplyCommentActivity.this);
                } else {
                    showLoginTip();
                }
                break;
        }
    }

    /**发表评论*/
    private void sendComment() {
        final String comment = mEtContent.getText()
                                         .toString();
        if (TextUtils.isEmpty(comment)) {
            mToastor.getSingletonToast(R.string.empty_comment).show();
            return;
        }
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      Map<String, String> params = new HashMap<String, String>();
                                      params.put("goodsid", mGoodsId);
                                      params.put("userid", mUser_id);
                                      params.put("content", comment);
                                      params.put("linkid", mLinkId);
                                      params.put("commentid", mComment.id);
                                      String  commentResult = HttpUtil.doPost("comment", params);
                                      Message msg           = Message.obtain();
                                      msg.what = WHAT_REPLY;
                                      msg.obj = commentResult;
                                      mHandler.sendMessage(msg);
                                  }
                              });

    }

    private void goBack() {
        Intent intent   = new Intent();
        int    position = getIntent().getIntExtra(EXTRA_POS, -1);
        intent.putExtra(EXTRA_POS, position);
        mComment.replyCount = mDatas.size();
        intent.putExtra(EXTRA_DATA, mComment);
        setResult(RESULT_COMMEND_CODE, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        goBack();
    }
}
