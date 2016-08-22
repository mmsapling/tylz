package com.tylz.aelos.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tylz.aelos.R;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.util.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   GuideActionShopActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/17 16:04
 *  @描述：    动作商城引导页
 */
public class GuideActionShopActivity
        extends Activity
{
    @Bind(R.id.iv_search)
    ImageView      mIvSearch;
    @Bind(R.id.iv_classify)
    ImageView      mIvClassify;
    @Bind(R.id.iv_list)
    ImageView      mIvList;
    @Bind(R.id.main_rl)
    RelativeLayout mMainRl;
    private int mClickCount;
    private SPUtils mSPUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_action_shop);
        ButterKnife.bind(this);
        mSPUtils = new SPUtils(this);
        mClickCount = 0;
        doStartAnim(mIvSearch);
    }

    @OnClick(R.id.main_rl)
    public void onClick() {
        if (mClickCount == 2) {
            doEndAnim(mIvList, true);
            mClickCount++;
        }
        if (mClickCount == 1) {
            doEndAnim(mIvClassify, false);
            doStartAnim(mIvList);
            mClickCount++;
        }
        if (mClickCount == 0) {
            doEndAnim(mIvSearch, false);
            doStartAnim(mIvClassify);
            mClickCount++;
        }
    }

    private void doStartAnim(ImageView iv)
    {
        iv.setVisibility(View.VISIBLE);
        AnimationSet   animationSet = new AnimationSet(true);
        AlphaAnimation animation    = new AlphaAnimation(0.1f, 1f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        iv.startAnimation(animationSet);
    }

    private void doEndAnim(final ImageView iv, final boolean finish)
    {
        AnimationSet   animationSet = new AnimationSet(true);
        AlphaAnimation animation    = new AlphaAnimation(1f, 0.1f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                iv.setVisibility(View.GONE);
                if (finish) {
                    mSPUtils.putBoolean(Constants.IS_FIRST_ACTION_SHOP, false);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        iv.startAnimation(animationSet);
    }
}
