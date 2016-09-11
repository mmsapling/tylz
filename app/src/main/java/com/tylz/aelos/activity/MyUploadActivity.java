package com.tylz.aelos.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStripExtends;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.BaseFragment;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.bean.UploadBean;
import com.tylz.aelos.fragment.AllUploadFra;
import com.tylz.aelos.fragment.CheckedErrorUploadFra;
import com.tylz.aelos.fragment.CheckedUploadFra;
import com.tylz.aelos.fragment.CheckingUploadFra;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

{
    private static final int REQUEST_CODE_DETAIL = 2000;
    @Bind(R.id.iv_left)
    ImageButton                 mIvLeft;
    @Bind(R.id.tv_title)
    TextView                    mTvTitle;
    @Bind(R.id.tabs)
    PagerSlidingTabStripExtends mTabs;
    @Bind(R.id.viewpager)
    ViewPager                   mViewpager;

    private String[]             mTabDatas;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private List<BaseFragment>   mFragments;
    private List<UploadBean>     mDatas;

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
        mTabDatas = UIUtils.getStrings(R.array.tabs_upload);
        mDatas = new ArrayList<>();
        mFragments = new ArrayList<>();
        AllUploadFra          allUploadFra      = new AllUploadFra();
        CheckingUploadFra     CheckingUploadFra = new CheckingUploadFra();
        CheckedUploadFra      CheckedUploadFra  = new CheckedUploadFra();
        CheckedErrorUploadFra ErrorUploadFra    = new CheckedErrorUploadFra();
        mFragments.add(allUploadFra);
        mFragments.add(CheckingUploadFra);
        mFragments.add(CheckedUploadFra);
        mFragments.add(ErrorUploadFra);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabDatas[position];
            }
        };

        mViewpager.setAdapter(mFragmentPagerAdapter);
        //        mViewpager.setOffscreenPageLimit(2);
        mTabs.setViewPager(mViewpager);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    AllUploadFra allUploadFra = (AllUploadFra) mFragments.get(position);
                    allUploadFra.setData(mDatas);
                } else if (position == 1) {
                    List<UploadBean> tempDatas = new ArrayList<UploadBean>();
                    for(UploadBean bean : mDatas){
                        if(bean.state.equals("0")){
                            tempDatas.add(bean);
                        }
                    }
                    CheckingUploadFra checkingfra = (CheckingUploadFra) mFragments.get(1);
                    checkingfra.setData(tempDatas);
                } else if (position == 2) {
                    List<UploadBean> tempDatas = new ArrayList<UploadBean>();
                    for(UploadBean bean : mDatas){
                        if(bean.state.equals("1")){
                            tempDatas.add(bean);
                        }
                    }
                    CheckedUploadFra checkedfra = (CheckedUploadFra) mFragments.get(2);
                    checkedfra.setData(tempDatas);
                } else if (position == 3) {
                    List<UploadBean> tempDatas = new ArrayList<UploadBean>();
                    for(UploadBean bean : mDatas){
                        if(bean.state.equals("2")){
                            tempDatas.add(bean);
                        }
                    }
                    CheckedErrorUploadFra errorfra = (CheckedErrorUploadFra) mFragments.get(3);
                    errorfra.setData(tempDatas);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadData() {
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getMyUpload")
                   .addParams("userid", mUser_id)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           if(response.equals("null")){
                           }else{
                            processData(response);
                           }
                       }
                   });
    }

    private void processData(String response) {
        LogUtils.d(response);
        Type             type           = new TypeToken<List<UploadBean>>() {}.getType();
        Gson             gson           = new Gson();
        List<UploadBean> uploadBeanList = gson.fromJson(response, type);
        mDatas.clear();
        mDatas.addAll(uploadBeanList);
        AllUploadFra alluploadFra = (AllUploadFra) mFragments.get(0);
        alluploadFra.setData(uploadBeanList);
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

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }
}
