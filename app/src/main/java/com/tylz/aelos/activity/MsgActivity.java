package com.tylz.aelos.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.MsgAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.MsgBean;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.util.HttpUtil;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.LoadMoreListView;

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
 *  @文件名:   AboutActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:19
 *  @描述：    关于界面
 */
public class MsgActivity
        extends BaseActivity
        implements LoadMoreListView.OnLoadMore, SwipeRefreshLayout.OnRefreshListener
{
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
    @Bind(R.id.tv_right)
    TextView           mTvRight;
    private MsgAdapter    mAdapter;
    private List<MsgBean> mDatas;
    private int           mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.my_msg);
        mDatas = new ArrayList<>();
        mAdapter = new MsgAdapter(this, mDatas);
        mListview.setAdapter(mAdapter);
        mListview.setLoadMoreListen(this);
        mSwipeRefresh.setOnRefreshListener(this);
        loadData(0);
    }

    private void loadData(int page) {
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
                                      Map<String, String> params = new HashMap<>();
                                      params.put("userid", mUser_id);
                                      params.put("page", mPage + "");
                                      final String data = HttpUtil.doPost("getUserPush", params);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mSwipeRefresh.setRefreshing(false);
                                              mListview.onLoadComplete();
                                              if (TextUtils.isEmpty(data)) {
                                                  if (mPage != 1) {
                                                      mPage--;
                                                  } else if (mPage == 1 && mDatas.size() == 0) {
                                                      mTvNothing.setVisibility(View.VISIBLE);
                                                      mListview.setVisibility(View.GONE);
                                                  }
                                                  mAdapter.notifyDataSetChanged();
                                              } else {

                                                  mTvNothing.setVisibility(View.GONE);
                                                  mListview.setVisibility(View.VISIBLE);
                                                  processData(data);
                                              }
                                          }
                                      });
                                  }
                              });
    }

    private void processData(String data) {
        LogUtils.d(data);
        Type          type        = new TypeToken<List<MsgBean>>() {}.getType();
        Gson          gson        = new Gson();
        List<MsgBean> msgBeanList = gson.fromJson(data, type);
        mDatas.addAll(msgBeanList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMore() {
        loadData(++mPage);
    }

    @Override
    public void onRefresh() {
        loadData(0);
    }

    @OnClick({R.id.iv_left,
              R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                break;
        }
    }
}
