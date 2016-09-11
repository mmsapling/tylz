package com.tylz.aelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.LoopPicPagerAdapter;
import com.tylz.aelos.adapter.ShopGridViewAdapter;
import com.tylz.aelos.adapter.ShopListViewAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.LoopPicData;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.MeasureGridView;
import com.tylz.aelos.view.MeasureListView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ActionShopActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/26 21:10
 *  @描述：    动作商城
 */
public class ActionShopActivity
        extends BaseActivity
{

    private static final int REQUEST_CODE_DETAIL = 2000;
    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton     mIvRight;
    @Bind(R.id.shop_viewpager)
    ViewPager       mShopViewpager;
    @Bind(R.id.shop_gridview)
    MeasureGridView mShopGridview;
    @Bind(R.id.shop_listview)
    MeasureListView mShopListview;
    @Bind(R.id.shop_point_container)
    LinearLayout    mPointContainer;
    AutoScrollTask mAutoScrollTask;
    private List<ShopBean>      mDatas;
    private ShopListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_shop);
        ButterKnife.bind(this);
        setMenuData();
        setupGridView();
        loadPicDataFromNet();
        loadActionDataFromNet(true);
        boolean isFirst = mSpUtils.getBoolean(Constants.IS_FIRST_ACTION_SHOP, true);
        if(isFirst){
            skipActivity(GuideActionShopActivity.class);
        }
    }

    /**
     * 设置标题
     */
    private void setMenuData() {
        mTvTitle.setText(R.string.action_shop);
        mIvRight.setImageResource(R.mipmap.search);
    }

    /**
     * 设置listview
     * @param data
     */
    private void setupListView(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<ShopBean>>() {}.getType();
        mDatas = gson.fromJson(data, type);
        mAdapter = new ShopListViewAdapter(this, mDatas);
        mShopListview.setAdapter(mAdapter);
        mShopListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopBean shopBean = mDatas.get(position);
                Intent   intent   = new Intent(ActionShopActivity.this, ActionDetailActivity.class);
                intent.putExtra(ActionDetailActivity.EXTRA_POS, position);
                intent.putExtra(ActionDetailActivity.EXTRA_DATA, shopBean);
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadActionDataFromNet(false);
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

    /**
     * 设置轮播图数据
     * @param data
     *      从网络加载过来json字符串
     */
    private void setupViewPager(String data) {
        if (TextUtils.isEmpty(data) || "null".equals(data)) {
            return;
        }
        Type                    type         = new TypeToken<List<LoopPicData>>() {}.getType();
        Gson                    gson         = new Gson();
        final List<LoopPicData> loopPicDatas = gson.fromJson(data, type);
        if (loopPicDatas != null && loopPicDatas.size() != 0) {
            for (int i = 0; i < loopPicDatas.size(); i++) {
                //添加指示器
                ImageView indicator = new ImageView(this);
                indicator.setImageResource(R.drawable.shape_point_normal);
                if (i == 0) {
                    indicator.setImageResource(R.drawable.shape_point_pressed);
                }
                int                       width  = UIUtils.dp2Px(10);
                int                       height = UIUtils.dp2Px(10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = UIUtils.dp2Px(6);
                indicator.setLayoutParams(params);
                mPointContainer.addView(indicator);
            }
            LoopPicPagerAdapter loopPicPagerAdapter = new LoopPicPagerAdapter(this, loopPicDatas);
            mShopViewpager.setAdapter(loopPicPagerAdapter);
            mShopViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position,
                                           float positionOffset,
                                           int positionOffsetPixels)
                {

                }

                @Override
                public void onPageSelected(int position) {
                    position = position % loopPicDatas.size();
                    //还原所有的效果
                    for (int i = 0; i < loopPicDatas.size(); i++) {
                        ImageView indicator = (ImageView) mPointContainer.getChildAt(i);
                        indicator.setImageResource(R.drawable.shape_point_normal);
                    }
                    //设置选中的效果
                    ImageView selectIndicator = (ImageView) mPointContainer.getChildAt(position);
                    selectIndicator.setImageResource(R.drawable.shape_point_pressed);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            autoLoop(loopPicDatas.size());
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAutoScrollTask != null) {
            mAutoScrollTask.stop();
        }
    }


    /**
     * 自动轮播
     * @param length
     */
    private void autoLoop(int length) {
        //左右无限轮播
        mShopViewpager.setCurrentItem(length * 100);
        //自动轮播
        mAutoScrollTask = new AutoScrollTask(mShopViewpager);
        mAutoScrollTask.start();
        //按压下去停止 轮播
        mShopViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mAutoScrollTask.stop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mAutoScrollTask.start();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 自动轮播任务
     */
    private class AutoScrollTask
            implements Runnable
    {
        private ViewPager mViewPager;

        public AutoScrollTask(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        public void start() {
            UIUtils.getMainThreadHandler()
                   .removeCallbacks(this);
            UIUtils.getMainThreadHandler()
                   .postDelayed(this, 3000);
        }

        public void stop() {
            UIUtils.getMainThreadHandler()
                   .removeCallbacks(this);
        }

        @Override
        public void run() {
            int currentItem = mViewPager.getCurrentItem();
            currentItem++;
            mViewPager.setCurrentItem(currentItem);
            start();
        }
    }

    /**
     * 从网络获取数据
     * 1.加载轮播图片数据
     * 2.加载动作列表数据
     */
    private void loadPicDataFromNet() {
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getSlideshow")
                   .build()
                   .execute(new ResultCall(false) {
                       @Override
                       public void onResult(String response, int id) {
                           LogUtils.d("loop_pic", response);
                           setupViewPager(response);
                       }
                   });
    }

    /**
     * 获取动作列表从网络
     */
    private void loadActionDataFromNet(boolean isShow) {
        String              type   = UIUtils.getString(R.string.hot);
        Map<String, String> params = new HashMap<String, String>();
        String              id     = mSpUtils.getString(Constants.USER_ID, "");
        params.put("id", id);
        params.put("type", type);
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getGoodsList")
                   .params(params)
                   .build()
                   .execute(new ResultCall(isShow) {
                       @Override
                       public void onResult(String response, int id) {
                           LogUtils.d("loop_pic", response);
                           setupListView(response);
                       }
                   });
    }

    /**
     * Gridview相关操作
     */
    private void setupGridView() {
        ShopGridViewAdapter shopGridViewAdapter = new ShopGridViewAdapter(this);
        mShopGridview.setAdapter(shopGridViewAdapter);
        mShopGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳入到动作类型列表
                Intent intent = new Intent(ActionShopActivity.this, ActionTypeActivity.class);
                intent.putExtra(ActionTypeActivity.EXTRA_TYPE, position2type(position));
                startActivity(intent);
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
     * 位置对应的类型
     * @param position 下标
     * @return 类型
     */
    private String position2type(int position) {
        String str = UIUtils.getString(R.string.base_action);
        switch (position) {
            case 0:
                str = UIUtils.getString(R.string.base_action);
                break;
            case 1:
                str = UIUtils.getString(R.string.music_dance);
                break;
            case 2:
                str = UIUtils.getString(R.string.fable);
                break;
            case 3:
                str = UIUtils.getString(R.string.football);
                break;
            case 4:
                str = UIUtils.getString(R.string.boxing);
                break;
            case 5:
                str = UIUtils.getString(R.string.custom);
                break;
            default:
                break;
        }
        return str;
    }
}
