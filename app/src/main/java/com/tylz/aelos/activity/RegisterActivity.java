package com.tylz.aelos.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DActionSheetDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
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
 *  @文件名:   RegisterActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/9 17:27
 *  @描述：    注册界面
 */
public class RegisterActivity
        extends BaseActivity
{
    public static final String EXTRA_PHONE = "phone";
    private final int REQUEST_CODE_CAMERA  = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.civ_avator)
    CircleImageView mCivAvator;
    @Bind(R.id.et_nickname)
    EditText        mEtNickname;
    @Bind(R.id.et_pwd)
    EditText        mEtPwd;
    @Bind(R.id.et_confrim_pwd)
    EditText        mEtConfrimPwd;
    @Bind(R.id.bt_next)
    Button          mBtNext;
    @Bind(R.id.ll_gologin)
    LinearLayout    mLlGologin;
    List<PhotoInfo> mPhotoInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.register);
        mPhotoInfos = new ArrayList<>();
    }

    @OnClick({R.id.iv_left,
              R.id.civ_avator,
              R.id.bt_next,
              R.id.ll_gologin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.civ_avator:
                selectIcon();
                break;
            case R.id.bt_next:
                register();
                break;
            case R.id.ll_gologin:
                finish();
                break;
        }
    }

    /**
     * 注册
     */
    private void register() {
        /*
         校验数据
         */
        String nickname = mEtNickname.getText()
                                     .toString();
        String confirmPwd = mEtConfrimPwd.getText()
                                         .toString();
        String pwd = mEtPwd.getText()
                           .toString();
        if (TextUtils.isEmpty(nickname)) {
            mToastor.getSingletonToast(R.string.empty_nickname).show();
            return;
        } else if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirmPwd)) {
            mToastor.getSingletonToast(R.string.empty_password).show();
            return;
        } else if (mPhotoInfos.size() == 0) {
            mToastor.getSingletonToast(R.string.please_select_photo).show();
            return;
        } else if (!pwd.equals(confirmPwd)) {
            mToastor.getSingletonToast(R.string.error_pwd_twice).show();
            return;
        }
        //参数
        Bitmap              bitmap = BitmapFactory.decodeFile(mPhotoInfos.get(0)
                                                                         .getPhotoPath());
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getIntent().getStringExtra(EXTRA_PHONE));
        map.put("password", pwd);
        map.put("nickname", nickname);
        map.put("picurl", StringUtils.imgToBase64(bitmap));
        //请求注册
        showProgress();
        OkHttpUtils.post()
                   .url(HttpUrl.BASE + "Register")
                   .params(map)
                   .build()
                   .execute(new ResultCall() {
                       @Override
                       public void onResult(String response, int id) {
                           if(response.equals("1")){
                               mToastor.getSingletonToast(R.string.success_regist).show();
                               RegisterActivity.this.finish();
                           }else{
                               mToastor.getSingletonToast(R.string.username_repeat).show();
                           }
                       }
                   });
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
                try{
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                }catch (Exception e){}
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
            try{
                if (resultList != null && resultList.size() != 0) {
                    mPhotoInfos.clear();
                    mPhotoInfos.addAll(resultList);
                    PhotoInfo photoInfo = resultList.get(0);

                    Picasso.with(RegisterActivity.this)
                           .load(new File(photoInfo.getPhotoPath()))
                           .into(mCivAvator);
                }
            }catch (Exception e){}

        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.showToast(errorMsg);
        }
    };
}
