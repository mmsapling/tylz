package com.tylz.aelos.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.tylz.aelos.R;

import java.util.List;
import java.util.Map;

/*
 *  @项目名：  MMSapling 
 *  @包名：    com.tylz.mmsapling.ui.example
 *  @文件名:   BaseParentUI
 *  @创建者:   陈选文
 *  @创建时间:  2016/9/5 16:16
 *  @描述：    TODO
 */
public abstract class BaseParentUI
        extends AppCompatActivity
{
    FrameLayout  mFlContent;
    LoadingPager mLoadingPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_base);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        initData();
        mLoadingPager = new LoadingPager(this) {
            @Override
            protected View onCreateSuccessView() {
                return onSuccessView();
            }

            @Override
            protected LoadedResult onStartLoadData() {
                return onLoadData();
            }
        };
        mFlContent.addView(mLoadingPager);
        loadData();

    }

    protected void loadData() {
        if (mLoadingPager != null) {
            mLoadingPager.loadData();
        }
    }

    protected void initData() {

    }

    protected abstract LoadingPager.LoadedResult onLoadData();

    protected abstract View onSuccessView();

    protected LoadingPager.LoadedResult checkState(Object data)
    {
        if (data == null ) { return LoadingPager.LoadedResult.EMPTY; }

        if (data instanceof List) {
            if (((List) data).size() == 0) { return LoadingPager.LoadedResult.EMPTY; }
        }

        if (data instanceof Map) {
            if (((Map) data).size() == 0) { return LoadingPager.LoadedResult.EMPTY; }
        }
        if(data instanceof  String){
            if(((String)data).equals("null")){
                return LoadingPager.LoadedResult.EMPTY;
            }
        }
        return LoadingPager.LoadedResult.SUCCESS;
    }
}
