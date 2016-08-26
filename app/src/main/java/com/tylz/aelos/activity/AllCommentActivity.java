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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.CommentAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.ActionDetailBean;
import com.tylz.aelos.bean.Comment;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.util.HttpUtil;
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

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   AllCommentActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/12 23:34
 *  @描述：    动作详情页的所有评论
 */
public class AllCommentActivity
        extends BaseActivity
        implements AdapterView.OnItemClickListener
{
    private static final int WHAT_COMMENT         = 0;
    private static final int REQUEST_COMMENT_CODE = 3000;
    @Bind(R.id.iv_left)
    ImageButton        mIvLeft;
    @Bind(R.id.tv_title)
    TextView           mTvTitle;
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
    private int              mPage;
    private List<Comment>    mDatas;
    private CommentAdapter   mAdapter;
    private ActionDetailBean mActionDetailBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.comment);
        mActionDetailBean = (ActionDetailBean) getIntent().getSerializableExtra(ActionDetailActivity.EXTRA_DATA);
        mDatas = new ArrayList<>();
        mAdapter = new CommentAdapter(this, mDatas);
        mListview.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListview);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              loadCommentFroNet(0);
            }
        });
        mListview.setLoadMoreListen(new LoadMoreListView.OnLoadMore() {
            @Override
            public void loadMore() {
                mListview.onLoadComplete();
            }
        });
        mListview.setOnItemClickListener(this);
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
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                           @Override
                                           public void run() {
                                               Map<String, String> params = new HashMap<String, String>();
                                               params.put("goodsid", mActionDetailBean.id);
                                               params.put("page", mPage + "");
                                        /*默认展示1000*/
                                               params.put("number", 1000 + "");
                                               final String commentsJson = HttpUtil.doPost("getComments",
                                                                                           params);
                                               closeProgress();
                                               UIUtils.postTaskSafely(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       mSwipeRefresh.setRefreshing(false);
                                                       if (TextUtils.isEmpty(commentsJson)) {
                                                           mListview.setVisibility(View.GONE);
                                                           mTvNothing.setVisibility(View.VISIBLE);
                                                       } else {
                                                           Type type = new TypeToken<List<Comment>>() {}.getType();
                                                           Gson gson = new Gson();
                                                           List<Comment> comments = gson.fromJson(
                                                                   commentsJson,
                                                                   type);
                                                           if (comments != null && comments.size() != 0) {
                                                               mDatas.addAll(comments);
                                                               mAdapter.notifyDataSetChanged();
                                                           }
                                                       }

                                                   }
                                               });
                                           }

                                       }

                              );
    }


    @OnClick({R.id.iv_left,
              R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_send:
                sendComment();
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
                                      params.put("goodsid", mActionDetailBean.id);
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

    /**
     * 处理评论返回数据
     * 1.评论成功 重新请求评论接口
     * 2.清空评论输入框
     * @param commentResult
     *         json
     */
    private void processCommentData(String commentResult) {
        closeProgress();
        try {
            JSONArray  jsonArray    = new JSONArray(commentResult);
            JSONObject jsonObject   = jsonArray.getJSONObject(0);
            String     commentCount = jsonObject.getString("commentCount");
            // mActionDetailBean.commentCount = commentCount;
            boolean result = jsonObject.getBoolean("result");
            mEtContent.setText("");
            if (result) {
                mToastor.getSingletonToast(R.string.success_comment).show();
                mTvNothing.setVisibility(View.GONE);
                mListview.setVisibility(View.VISIBLE);
                loadCommentFroNet(0);
            } else {
                mToastor.getSingletonToast(R.string.fail_comment).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_COMMENT:
                    String commentResult = (String) msg.obj;
                    processCommentData(commentResult);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,ReplyCommentActivity.class);
        Comment comment = mDatas.get(position);
        intent.putExtra(ReplyCommentActivity.EXTRA_DATA,comment);
        intent.putExtra(ReplyCommentActivity.EXTRA_POS,position);
        intent.putExtra(ReplyCommentActivity.EXTRA_GOODSID,mActionDetailBean.id);
        startActivityForResult(intent,REQUEST_COMMENT_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COMMENT_CODE && resultCode == ReplyCommentActivity.RESULT_COMMEND_CODE) {
            if (data != null) {
               Comment comment = (Comment) data.getSerializableExtra(ReplyCommentActivity.EXTRA_DATA);
                int      postion  = data.getIntExtra(ReplyCommentActivity.EXTRA_POS, -1);
                if (postion != -1) {
                    mDatas.set(postion, comment);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
