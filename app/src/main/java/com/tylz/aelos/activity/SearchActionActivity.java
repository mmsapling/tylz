package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.ShopListViewAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
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
 *  @文件名:   SearchActionActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/11 13:00
 *  @描述：    搜索动作界面
 */
public class SearchActionActivity
        extends BaseActivity
        implements LoadMoreListView.OnLoadMore, SwipeRefreshLayout.OnRefreshListener,
                   AdapterView.OnItemClickListener
{
    @Bind(R.id.iv_left)
    ImageButton        mIvLeft;
    @Bind(R.id.et_search)
    EditText           mEtSearch;
    @Bind(R.id.iv_right)
    ImageButton        mIvRight;
    @Bind(R.id.listView)
    LoadMoreListView   mListView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.tv_nothing)
    TextView           mTvNothing;
    private int mPage = 0;
    private List<ShopBean>      mDatas;
    private ShopListViewAdapter mAdapter;
    private boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_action);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new ShopListViewAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setLoadMoreListen(this);
        mListView.setOnItemClickListener(this);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,
                                              android.R.color.holo_red_light,
                                              android.R.color.holo_orange_light,
                                              android.R.color.holo_green_light);
    }

    /**
     * 从后台加载类型数据
     */
    private void loadTypeData(int page) {
        mPage = page;
        /*当刷新时 清空数据源*/
        if (mPage == 0) {
            mDatas.clear();
        }
        final Map<String, String> params = new HashMap<String, String>();
        String                    id     = mSpUtils.getString(Constants.USER_ID, "");
        String keyword = mEtSearch.getText()
                                  .toString();
        params.put("id", id);
        params.put("keyword", keyword);
        params.put("page", String.valueOf(page));
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final String data = HttpUtil.doPost("search", params);
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mSwipeRefresh.setRefreshing(false);
                                              mListView.onLoadComplete();
                                              isSearch = false;
                                              LogUtils.d(data);
                                              if (TextUtils.isEmpty(data) || "null".equals(data)) {
                                                  if (mPage != 0) {
                                                      mPage--;
                                                  } else if (mPage == 0 && mDatas.size() == 0) {
                                                      mTvNothing.setVisibility(View.VISIBLE);
                                                      mListView.setVisibility(View.GONE);
                                                  }
                                                  mAdapter.notifyDataSetChanged();
                                              } else {
                                                  mTvNothing.setVisibility(View.GONE);
                                                  mListView.setVisibility(View.VISIBLE);
                                                  processData(data);
                                              }
                                          }
                                      });

                                  }
                              });
    }

    /**
     * 设置listview
     * @param data
     */
    private void processData(String data) {
        try {
            Gson           gson         = new Gson();
            Type           type         = new TypeToken<List<ShopBean>>() {}.getType();
            List<ShopBean> shopBeanList = gson.fromJson(data, type);
            if (shopBeanList != null && shopBeanList.size() > 0) {
                mDatas.addAll(shopBeanList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            mTvNothing.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }


    }

    @OnClick({R.id.iv_left,
              R.id.iv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                String key = mEtSearch.getText()
                                      .toString();
                if (TextUtils.isEmpty(key)) {
                    mToastor.getSingletonToast(R.string.hint_input_key_search)
                            .show();
                    return;
                }
                if (!isSearch) {
                    showProgress();
                    loadTypeData(0);
                    isSearch = true;
                } else {
                    mToastor.getSingletonToast(R.string.please_waiting_searching)
                            .show();
                }
                break;
        }
    }

    @Override
    public void loadMore() {
        loadTypeData(++mPage);
    }

    @Override
    public void onRefresh() {
        loadTypeData(0);
    }
    private static final int REQUEST_CODE_DETAIL = 2000;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShopBean shopBean = mDatas.get(position);
        Intent   intent   = new Intent(this, ActionDetailActivity.class);
        intent.putExtra(ActionDetailActivity.EXTRA_POS, position);
        intent.putExtra(ActionDetailActivity.EXTRA_DATA, shopBean);
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode && resultCode == ActionDetailActivity.RESULT_CODE_RETURN) {
            if (data != null) {
                ShopBean shopBean = (ShopBean) data.getSerializableExtra(ActionDetailActivity.EXTRA_DATA);
                int      postion  = data.getIntExtra(ActionDetailActivity.EXTRA_POS, -1);
                if (postion != -1) {
                    mDatas.set(postion, shopBean);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
