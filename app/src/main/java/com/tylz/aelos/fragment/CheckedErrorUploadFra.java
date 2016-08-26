package com.tylz.aelos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.activity.ActionDetailActivity;
import com.tylz.aelos.activity.UploadActionDetailActivity;
import com.tylz.aelos.adapter.UploadListViewAdapter;
import com.tylz.aelos.base.BaseFragment;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.bean.UploadBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.fragment
 *  @文件名:   AllUploadFra
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/24 13:40
 *  @描述：    TODO
 */
public class CheckedErrorUploadFra
        extends BaseFragment
        implements AdapterView.OnItemClickListener
{
    private static final int REQUEST_CODE_DETAIL = 2000;

    @Bind(R.id.listview)
    ListView           mListview;
    @Bind(R.id.tv_nothing)
    TextView           mTvNothing;
    private List<UploadBean>      mDatas;
    private UploadListViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(mContext, R.layout.fra_upload, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDatas = new ArrayList<>();
        mAdapter = new UploadListViewAdapter(mContext,mDatas);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(this);
        isEmptyUI();
    }
    public void setData(List<UploadBean> uploadBeanList){
        mDatas.clear();
        mDatas.addAll(uploadBeanList);
        mAdapter.notifyDataSetChanged();
        isEmptyUI();
    }
    private void isEmptyUI(){
        if(mDatas == null || mDatas.size() == 0){
            mListview.setVisibility(View.GONE);
            mTvNothing.setVisibility(View.VISIBLE);
        }else{
            mListview.setVisibility(View.VISIBLE);
            mTvNothing.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UploadBean uploadBean = mDatas.get(position);
        if (uploadBean.state.equals("1")) {
            ShopBean shopBean = upload2shopBean(uploadBean);
            Intent   intent   = new Intent(mContext, ActionDetailActivity.class);
            intent.putExtra(ActionDetailActivity.EXTRA_POS, position);
            intent.putExtra(ActionDetailActivity.EXTRA_DATA, shopBean);
            startActivityForResult(intent, REQUEST_CODE_DETAIL);
        } else {
            Intent intent = new Intent(mContext, UploadActionDetailActivity.class);
            intent.putExtra(UploadActionDetailActivity.EXTRA_DATA, uploadBean);
            startActivity(intent);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
