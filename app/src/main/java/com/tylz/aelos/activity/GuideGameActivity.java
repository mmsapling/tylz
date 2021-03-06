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
 *  @文件名:   GuideGameActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/17 16:03
 *  @描述：    遥控器引导页
 */
public class GuideGameActivity
        extends Activity
{
    @Bind(R.id.iv_remote)
    ImageView      mIvRemote;
    @Bind(R.id.iv_store)
    ImageView      mIvStore;
    @Bind(R.id.iv_voice)
    ImageView      mIvVoice;
    @Bind(R.id.iv_help)
    ImageView      mIvHelp;
    @Bind(R.id.iv_menu)
    ImageView      mIvMenu;
    @Bind(R.id.main_rl)
    RelativeLayout mMainRl;
    private int mClickCount;
    private SPUtils mSPUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_game);
        ButterKnife.bind(this);
        mSPUtils = new SPUtils(this);
        mClickCount = 0;
        doStartAnim(mIvRemote);
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
                    mSPUtils.putBoolean(Constants.IS_FIRST_REMOTE_CONTROL, false);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        iv.startAnimation(animationSet);
    }

    @OnClick(R.id.main_rl)
    public void onClick() {
        if (mClickCount == 4) {
            doEndAnim(mIvMenu, true);
            mClickCount++;
        }
        if (mClickCount == 3) {
            doEndAnim(mIvHelp, false);
            doStartAnim(mIvMenu);
            mClickCount++;
        }
        if (mClickCount == 2) {
            doEndAnim(mIvVoice, false);
            doStartAnim(mIvHelp);
            mClickCount++;
        }
        if (mClickCount == 1) {
            doEndAnim(mIvStore, false);
            doStartAnim(mIvVoice);
            mClickCount++;
        }
        if (mClickCount == 0) {
            doEndAnim(mIvRemote, false);
            doStartAnim(mIvStore);
            mClickCount++;
        }
    }
}
