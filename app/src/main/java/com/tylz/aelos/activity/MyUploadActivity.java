package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.UploadListViewAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.bean.UploadBean;
import com.tylz.aelos.manager.HttpUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   MyUploadActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/22 21:47
 *  @描述：    我的上传
 */
public class MyUploadActivity
        extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener
{
    private static final int REQUEST_CODE_DETAIL = 2000;
    @Bind(R.id.iv_left)
    ImageButton        mIvLeft;
    @Bind(R.id.tv_title)
    TextView           mTvTitle;
    @Bind(R.id.listview)
    ListView           mListview;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.tv_nothing)
    TextView           mTvNothing;
    private List<UploadBean>      mDatas;
    private UploadListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);
        ButterKnife.bind(this);
        initData();
        loadData();
    }

    private void initData() {
        mTvTitle.setText(R.string.my_upload);
        mDatas = new ArrayList<>();
        mAdapter = new UploadListViewAdapter(this, mDatas);
        mListview.setAdapter(mAdapter);
        mSwipeRefresh.setOnRefreshListener(this);
        mListview.setOnItemClickListener(this);
    }

    private void loadData() {
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getMyUpload")
                   .addParams("userid", mUser_id)
                   .build()
                   .execute(new StringCallback() {
                       @Override
                       public void onError(Call call, Exception e, int id) {

                       }

                       @Override
                       public void onResponse(String response, int id) {
                           mSwipeRefresh.setRefreshing(false);
                           closeProgress();
                           if (TextUtils.isEmpty(response)) {
                               mSwipeRefresh.setVisibility(View.GONE);
                               mTvNothing.setVisibility(View.VISIBLE);
                           } else {
                               processData(response);
                           }
                       }
                   });
    }

    private void processData(String response) {
        Type             type           = new TypeToken<List<UploadBean>>() {}.getType();
        Gson             gson           = new Gson();
        List<UploadBean> uploadBeanList = gson.fromJson(response, type);
        mDatas.clear();
        mDatas.addAll(uploadBeanList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefresh.setVisibility(View.VISIBLE);
        mTvNothing.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UploadBean uploadBean = mDatas.get(position);
        if (uploadBean.state.equals("1")) {
            ShopBean shopBean = upload2shopBean(uploadBean);
            Intent   intent   = new Intent(this, ActionDetailActivity.class);
            intent.putExtra(ActionDetailActivity.EXTRA_POS, position);
            intent.putExtra(ActionDetailActivity.EXTRA_DATA, shopBean);
            startActivityForResult(intent, REQUEST_CODE_DETAIL);
        } else {
            Intent intent = new Intent(this, UploadActionDetailActivity.class);
            intent.putExtra(UploadActionDetailActivity.EXTRA_DATA, uploadBean);
            startActivity(intent);
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
                    for (int i = 0; i < mDatas.size(); i++) {
                        UploadBean bean = mDatas.get(i);
                        if (bean.id.equals(shopBean.id)) {
                            bean.isdownload = shopBean.isdownload;
                            bean.iscollect = shopBean.iscollect;
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private ShopBean upload2shopBean(UploadBean uploadBean) {
        ShopBean shopBean = new ShopBean();
        shopBean.hasAction = uploadBean.hasAction;
        shopBean.id = uploadBean.id;
        shopBean.iscollect = uploadBean.iscollect;
        shopBean.isdownload = uploadBean.isdownload;
        shopBean.picurl = uploadBean.picurl;
        shopBean.second = uploadBean.second;
        shopBean.title = uploadBean.title;
        shopBean.titlestream = "";
        shopBean.type = uploadBean.type;
        return shopBean;
    }

}
