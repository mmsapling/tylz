package com.tylz.aelos.activity;

import android.os.Bundle;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);
        loadData(1);
    }

    private void loadData(int page) {
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getMyUpload")
                   .addParams("userid", mUser_id)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           LogUtils.d("upload = " + response);
                       }
                   });
    }
}
