package com.tylz.aelos.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   UserInfoActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/7 14:23
 *  @描述：    TODO
 */
public class UserInfoActivity
        extends BaseActivity
{
    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton     mIvRight;
    @Bind(R.id.civ_avator)
    CircleImageView mCivAvator;
    @Bind(R.id.tv_username)
    TextView        mTvUsername;
    @Bind(R.id.tv_nickname)
    TextView        mTvNickname;
    @Bind(R.id.tv_sex)
    TextView        mTvSex;
    @Bind(R.id.tv_location)
    TextView        mTvLocation;
    @Bind(R.id.tv_personal)
    TextView        mTvPersonal;
    @Bind(R.id.tv_qq)
    TextView        mTvQq;
    @Bind(R.id.tv_email)
    TextView        mTvEmail;
    @Bind(R.id.tv_birthday)
    TextView        mTvBirthday;
    @Bind(R.id.tv_age)
    TextView        mTvAge;
    @Bind(R.id.tv_hobby)
    TextView        mTvHobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initData();
        loadDataFromNet();
    }

    /**
     * 从网络加载数据
     */
    private void loadDataFromNet() {
       // showProgress();
        //OkHttpUtils.post().url(HttpUrl.BASE + "setUserInfo")
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mTvTitle.setText(R.string.personal);
        mIvRight.setImageResource(R.mipmap.compile);
        //从sp里面获取用户信息
        User user = mSpUtils.getUserInfoBySp();
        Picasso.with(this).load(user.avatar).placeholder(R.mipmap.defaultavatar).into(mCivAvator);
        mTvNickname.setText(user.nickname);
        mTvSex.setText(user.gender);
        mTvLocation.setText(user.address);
        mTvPersonal.setText(user.selfInfo);
        mTvQq.setText(user.qq);
        mTvEmail.setText(user.mailbox);
        mTvBirthday.setText(user.birth);
        mTvAge.setText(user.age);
        mTvHobby.setText(user.hobby);
        mTvUsername.setText(user.nickname);
    }

    @OnClick({R.id.iv_left,
              R.id.iv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                skipActivity(UserInfoEditActivity.class);
                break;
        }
    }
}
