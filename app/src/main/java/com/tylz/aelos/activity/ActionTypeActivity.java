package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
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
 *  @文件名:   ActionTypeActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/6 19:35
 *  @描述：    动作类型列表
 */
public class ActionTypeActivity
        extends BaseActivity
        implements LoadMoreListView.OnLoadMore, SwipeRefreshLayout.OnRefreshListener

{
    public static final  String EXTRA_TYPE          = "type";
    private static final int    REQUEST_CODE_DETAIL = 2000;
    @Bind(R.id.iv_left)
    ImageButton        mIvLeft;
    @Bind(R.id.tv_title)
    TextView           mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton        mIvRight;
    @Bind(R.id.listview)
    LoadMoreListView   mListview;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.tv_nothing)
    TextView           mTvNothing;


    private String mType;
    private int mPage = 0;
    private List<ShopBean>      mDatas;
    private ShopListViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_type);
        ButterKnife.bind(this);
        initData();
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
        params.put("id", id);
        params.put("type", mType);
        params.put("page", String.valueOf(page));
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final String data = HttpUtil.doPost("getGoodsList", params);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mSwipeRefresh.setRefreshing(false);
                                              mListview.onLoadComplete();
                                              if ("null".equals(data)) {
                                                  if (mPage != 0) {
                                                      mPage--;
                                                  } else if (mPage == 0 && mDatas.size() == 0) {
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

    /**
     * 初始化本地数据
     */
    private void initData() {
        mType = getIntent().getStringExtra(EXTRA_TYPE);
        mTvTitle.setText(mType);
        mIvRight.setImageResource(R.mipmap.search);
        mDatas = new ArrayList<>();
        mAdapter = new ShopListViewAdapter(this, mDatas);
        mListview.setAdapter(mAdapter);
        mListview.setLoadMoreListen(this);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,
                                              android.R.color.holo_red_light,
                                              android.R.color.holo_orange_light,
                                              android.R.color.holo_green_light);
        showProgress();
        loadTypeData(0);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopBean shopBean = mDatas.get(position);
                Intent   intent   = new Intent(ActionTypeActivity.this, ActionDetailActivity.class);
                intent.putExtra(ActionDetailActivity.EXTRA_POS, position);
                intent.putExtra(ActionDetailActivity.EXTRA_DATA, shopBean);
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });
    }

    @OnClick({R.id.iv_left,
              R.id.iv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                skipActivity(SearchActionActivity.class);
                break;
        }
    }

    /**
     * 设置listview
     * @param data
     */
    private void processData(String data) {

        Gson           gson         = new Gson();
        Type           type         = new TypeToken<List<ShopBean>>() {}.getType();
        List<ShopBean> shopBeanList = gson.fromJson(data, type);
        if (shopBeanList != null && shopBeanList.size() > 0) {
            mDatas.addAll(shopBeanList);
            mAdapter.notifyDataSetChanged();
        }

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

    @Override
    public void loadMore() {
        loadTypeData(++mPage);
    }

    @Override
    public void onRefresh() {
        loadTypeData(0);
    }
}
