package com.tylz.aelos.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.bean.UserBean;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DActionSheetDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   UserInfoEditActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 20:25
 *  @描述：    用户信息编辑界面
 */
public class UserInfoEditActivity
        extends BaseActivity
{
    private final int REQUEST_CODE_CAMERA  = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton     mIvRight;
    @Bind(R.id.tv_right)
    TextView        mTvRight;
    @Bind(R.id.civ_avator)
    CircleImageView mCivAvator;
    @Bind(R.id.tv_username)
    TextView        mTvUsername;
    @Bind(R.id.et_nickname)
    EditText        mEtNickname;

    @Bind(R.id.et_location)
    EditText mEtLocation;
    @Bind(R.id.et_personal)
    EditText mEtPersonal;
    @Bind(R.id.et_qq)
    EditText mEtQq;
    @Bind(R.id.et_email)
    EditText mEtEmail;

    @Bind(R.id.et_hobby)
    EditText     mEtHobby;
    @Bind(R.id.tv_sex)
    TextView     mTvSex;
    @Bind(R.id.ll_sex)
    LinearLayout mLlSex;
    @Bind(R.id.tv_birthday)
    TextView     mTvBirthday;
    @Bind(R.id.ll_birthday)
    LinearLayout mLlBirthday;
    @Bind(R.id.et_age)
    EditText     mEtAge;
    @Bind(R.id.fl_avator)
    FrameLayout  mFlAvator;

    private List<PhotoInfo> mPhotoInfos;
    private User            mUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_edit);
        ButterKnife.bind(this);
        mPhotoInfos = new ArrayList<>();
        initData();
    }

    /**
     * 初始化用户数据
     */
    private void initData() {
        mTvTitle.setText(R.string.edit_info);
        mTvRight.setText(R.string.save);
        mUserInfo = mSpUtils.getUserInfoBySp();
        Picasso.with(this)
               .load(mUserInfo.avatar)
               .placeholder(R.mipmap.defaultavatar)
               .into(mCivAvator);
        mEtNickname.setText(mUserInfo.nickname);
        mTvUsername.setText(mUserInfo.phone);
        mTvSex.setText(mUserInfo.gender);
        mEtLocation.setText(mUserInfo.address);
        mEtPersonal.setText(mUserInfo.selfInfo);
        mEtQq.setText(mUserInfo.qq);
        mEtEmail.setText(mUserInfo.mailbox);
        mTvBirthday.setText(mUserInfo.birth);
        mEtAge.setText(mUserInfo.age);
        mEtHobby.setText(mUserInfo.hobby);

    }


    /**
     * 选择照片
     */
    private void selectIcon() {
        new DActionSheetDialog(this).builder()
                                    .setTitle(UIUtils.getString(R.string.please_select))
                                    .setCancelable(false)
                                    .setCanceledOnTouchOutside(false)
                                    .addSheetItem(UIUtils.getString(R.string.camera),
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectIcon(which);
                                                      }
                                                  })
                                    .addSheetItem(UIUtils.getString(R.string.open_photo),
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectIcon(which);
                                                      }
                                                  })
                                    .show();

    }

    /**
     * 根据点击事件来判断是拍照还是打开相册
     * @param which  事件的标志
     */
    private void selectIcon(int which) {
        switch (which) {
            case 1: //拍照
                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                break;
            case 2: //打开相册
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, mOnHanlderResultCallback);
                break;
        }
    }

    /**
     * 处理图片结果
     */
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                mPhotoInfos.clear();
                mPhotoInfos.addAll(resultList);
                PhotoInfo photoInfo = resultList.get(0);

                Picasso.with(UserInfoEditActivity.this)
                       .load(new File(photoInfo.getPhotoPath()))
                       .into(mCivAvator);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.showToast(errorMsg);
        }
    };


    @OnClick({R.id.iv_left,
              R.id.tv_right,
              R.id.fl_avator,
              R.id.ll_sex,
              R.id.ll_birthday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                saveInfo();
                break;
            case R.id.fl_avator:
                selectIcon();
                break;
            case R.id.ll_sex:
                selectSex();
                break;
            case R.id.ll_birthday:
                selectBirth();
                break;
        }
    }

    /**
     * 选择年龄
     */
    private void selectSex() {
        String title  = UIUtils.getString(R.string.please_select);
        String man    = UIUtils.getString(R.string.man);
        String woman  = UIUtils.getString(R.string.woman);
        String secret = UIUtils.getString(R.string.keep_secret);
        new DActionSheetDialog(this).builder()
                                    .setTitle(title)
                                    .setCancelable(false)
                                    .setCanceledOnTouchOutside(false)
                                    .addSheetItem(secret,
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectSex(which);
                                                      }
                                                  })
                                    .addSheetItem(man,
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectSex(which);
                                                      }
                                                  })
                                    .addSheetItem(woman,
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectSex(which);
                                                      }
                                                  })
                                    .show();
    }

    /**
     * 点击不同的条目的处理时间
     * @param index 位置
     */
    private void selectSex(int index) {
        switch (index) {
            case 1: //女人
                mTvSex.setText(R.string.woman);
                break;
            case 2: //男人
                mTvSex.setText(R.string.man);
                break;
            case 3: //保密
                mTvSex.setText(R.string.keep_secret);
                break;
            default:
                break;
        }
    }

    /**
     * 选择生日
     */
    private void selectBirth() {
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this,
                                                                     new DatePickerPopWin.OnDatePickedListener() {
                                                                         @Override
                                                                         public void onDatePickCompleted(
                                                                                 int year,
                                                                                 int month,
                                                                                 int day,
                                                                                 String dateDesc)
                                                                         {
                                                                             mTvBirthday.setText(
                                                                                     dateDesc);
                                                                         }
                                                                     }).build();
        pickerPopWin.showPopWin(this);

    }

    /**
     * 保存数据
     */
    private void saveInfo() {
        String nickname = mEtNickname.getText()
                                     .toString();
        String sex = mTvSex.getText()
                           .toString();
        String address = mEtLocation.getText()
                                    .toString();
        String personal = mEtPersonal.getText()
                                     .toString();
        String qq = mEtQq.getText()
                         .toString();
        String email = mEtEmail.getText()
                               .toString();
        String birthday = mTvBirthday.getText()
                                     .toString();
        String age = mEtAge.getText()
                           .toString();
        String hobby = mEtHobby.getText()
                               .toString();

        //请求参数
        Map<String, String> params = new HashMap<>();
        params.put("id", mUserInfo.id);
        params.put("nickname", nickname);
        params.put("selfInfo", personal);
        params.put("mailbox", email);
        params.put("qq", qq);
        params.put("address", address);
        params.put("birth", birthday);
        params.put("gender", sex);
        params.put("hobby", hobby);
        params.put("age", age);
        if (mPhotoInfos.size() != 0) {
            String photoPath = mPhotoInfos.get(0)
                                          .getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            params.put("avatar", StringUtils.imgToBase64(bitmap));
        }
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "setUserInfo")
                   .params(params)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           LogUtils.d("edit info = " + response);
                           if (response.equals("true")) {
                               //修改成功 去后台拉取下数据
                               loadDataFromNet();
                           } else {
                               mToastor.getSingletonToast(R.string.fail_edit).show();
                           }
                       }
                   });
    }

    /**
     * 修改成功后从网络重新加载一遍数据
     */
    private void loadDataFromNet() {
        //        getUserInfo
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "getUserInfo")
                   .addParams("id", mUserInfo.id)
                   .build()
                   .execute( new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           LogUtils.d("edit info = " + response);
                           //解析出数据，保存在本地
                           Type           type  = new TypeToken<List<UserBean>>() {}.getType();
                           Gson           gson  = new Gson();
                           List<UserBean> users = gson.fromJson(response, type);
                           if (users != null && users.size() == 1) {
                               UserBean user = users.get(0);
                               String   pwd  = mSpUtils.getString(Constants.USER_PWD, "");
                               mSpUtils.saveUserInfo(CommomUtil.bean2user(user, pwd));
                           }
                           //返回上一个界面
                           UserInfoEditActivity.this.finish();
                       }
                   });
    }


}
