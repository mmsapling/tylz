package com.tylz.aelos.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   VersionIntroduceActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 14:21
 *  @描述：    版本介绍
 */
public class VersionIntroduceActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton mIvRight;
    @Bind(R.id.webview)
    WebView     mWebview;
    @Bind(R.id.tv_nothing)
    TextView    mTvNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_introduce);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.version_introduce);
        initWebView();
        loadDataFromNet();
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        WebSettings settings = mWebview.getSettings();
        settings.setSaveFormData(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        mWebview.setWebViewClient(new WebViewClient());

    }

    private void loadDataFromNet() {
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getVersionInfo")
                   .build()
                   .execute(new StringCallback() {
                       @Override
                       public void onError(Call call, Exception e, int id) {
                           closeProgress();
                       }

                       @Override
                       public void onResponse(final String response, int id) {
                           closeProgress();
                           UIUtils.postTaskSafely(new Runnable() {
                               @Override
                               public void run() {
                                   if(TextUtils.isEmpty(response)){
                                       mWebview.setVisibility(View.GONE);
                                       mTvNothing.setVisibility(View.VISIBLE);
                                   }else{
                                       mWebview.loadDataWithBaseURL(null,
                                                                    response,
                                                                    "text/html",
                                                                    "UTF-8",
                                                                    null);
                                   }

                               }
                           });
                       }
                   });
    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }
}