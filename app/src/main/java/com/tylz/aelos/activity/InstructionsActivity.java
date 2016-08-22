package com.tylz.aelos.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.InstructionsAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.Instructions;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   InstructionsActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 15:07
 *  @描述：    使用说明界面
 */
public class InstructionsActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton mIvRight;
    @Bind(R.id.listview)
    ListView    mListview;
    @Bind(R.id.tv_nothing)
    TextView    mTvNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucetions);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.instructions);
        loadDataFromNet();
    }
    /**
     * 从网络加载数据
     */
    private void loadDataFromNet() {
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getManual")
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
                                       mListview.setVisibility(View.GONE);
                                       mTvNothing.setVisibility(View.VISIBLE);
                                   }else{
                                       //给listview设置数据
                                       processJson(response);
                                   }

                               }
                           });
                       }
                   });
    }

    /**
     * 处理json数据,并为listview设置数据
     * @param json
     *         json数据
     */
    private void processJson(String json) {
        Type type = new TypeToken<List<Instructions>>(){}.getType();
        Gson gson = new Gson();
        List<Instructions> datas = gson.fromJson(json, type);
        InstructionsAdapter adapter = new InstructionsAdapter(this,datas);
        mListview.setAdapter(adapter);
    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }
}
