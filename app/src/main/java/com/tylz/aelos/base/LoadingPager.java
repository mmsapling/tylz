package com.tylz.aelos.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.tylz.aelos.R;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.util.UIUtils;


/*
 *  @文件名:   LoadingPager
 *  @创建者:   陈选文
 *  @创建时间:  2016/9/3 22:39
 *  @描述：    TODO
 */
public abstract class LoadingPager
        extends FrameLayout
        implements View.OnClickListener
{
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private View mSuccessView;
    public static final int STATE_NONE    = -1;
    public static final int STATE_LOADING = 0;
    public static final int STATE_EMPTY   = 1;
    public static final int STATE_ERROR   = 2;
    public static final int STATE_SUCCESS = 3;
    /** 用来标记当前属于什么状态，就显示什么View*/
    private             int mCurrentState = STATE_NONE;
    private View mBtnRetry;
    public LoadingPager(Context context) {
        super(context);
        initView();
    }

    public LoadingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        if (mLoadingView == null) {
            mLoadingView = View.inflate(getContext(), R.layout.pager_loading, null);
            addView(mLoadingView);
        }
        if (mEmptyView == null) {
            mEmptyView = View.inflate(getContext(), R.layout.pager_empty, null);
            addView(mEmptyView);
        }
        if (mErrorView == null) {
            mErrorView = View.inflate(getContext(), R.layout.pager_error, null);
            addView(mErrorView);
            mBtnRetry = mErrorView.findViewById(R.id.error_btn_retry);
            mBtnRetry.setOnClickListener(this);
        }
        //根据状态显示view
        safeUpdateUIStyle();
    }
    private void safeUpdateUIStyle(){
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                updateUIStyle();
            }
        });

    }
    private void updateUIStyle() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(mCurrentState == STATE_LOADING || mCurrentState == STATE_NONE
                                       ? VISIBLE
                                       : GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(mCurrentState == STATE_EMPTY
                                     ? VISIBLE
                                     : GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(mCurrentState == STATE_ERROR
                                     ? VISIBLE
                                     : GONE);
        }
        if (mSuccessView == null && mCurrentState == STATE_SUCCESS) {
            mSuccessView = onCreateSuccessView();
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                                                   LayoutParams.MATCH_PARENT);
            addView(mSuccessView, params);
        }
        if (mSuccessView != null) {
            mSuccessView.setVisibility(mCurrentState == STATE_SUCCESS
                                       ? VISIBLE
                                       : GONE);
        }
    }

    public void loadData() {
        if(mCurrentState == STATE_EMPTY || mCurrentState == STATE_ERROR || mCurrentState == STATE_NONE){
            mCurrentState = STATE_LOADING;
            ThreadPoolProxyFactory.createNormalThreadPoolProxy().execute(new LoadDataTask());
        }
        safeUpdateUIStyle();
    }

    protected abstract View onCreateSuccessView();

    protected abstract LoadedResult onStartLoadData();

    @Override
    public void onClick(View view) {
        if(view == mBtnRetry){
            loadData();
        }
    }

    class LoadDataTask implements Runnable {

        @Override
        public void run() {
            //获取数据
            LoadedResult result = onStartLoadData();
            mCurrentState  = result.getState();
            //ui改变
            safeUpdateUIStyle();
        }
    }
    public enum LoadedResult{
        EMPTY(STATE_EMPTY),ERROR(STATE_ERROR),SUCCESS(STATE_SUCCESS);
        int state;
        LoadedResult(int state) {
            this.state = state;
        }
        public int getState(){
            return state;
        }
    }
}
