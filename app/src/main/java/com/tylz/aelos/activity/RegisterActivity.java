package com.tylz.aelos.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tylz.aelos.R;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DActionSheetDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.hybridsquad.android.library.BitmapUtil;
import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   RegisterActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/9 17:27
 *  @描述：    注册界面
 */
@RuntimePermissions
public class RegisterActivity
        extends BaseActivity
        implements CropHandler
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
    private Bitmap mBitmap;
    CropParams mCropParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mTvTitle.setText(R.string.register);
        mCropParams = new CropParams(this);
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
        } else if (null == mBitmap) {
            mToastor.getSingletonToast(R.string.please_select_photo).show();
            return;
        } else if (!pwd.equals(confirmPwd)) {
            mToastor.getSingletonToast(R.string.error_pwd_twice).show();
            return;
        }
        //参数

        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getIntent().getStringExtra(EXTRA_PHONE));
        map.put("password", pwd);
        map.put("nickname", nickname);
        map.put("picurl", StringUtils.imgToBase64(mBitmap));
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
               RegisterActivityPermissionsDispatcher.showOpenCameraWithCheck(this);
                break;
            case 2: //打开相册
                mCropParams.refreshUri();
                mCropParams.enable = true;
                mCropParams.compress = false;
                Intent intent2 = CropHelper.buildGalleryIntent(mCropParams);
                startActivityForResult(intent2, CropHelper.REQUEST_CROP);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RegisterActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
     void showOpenCamera() {
        mCropParams.refreshUri();
        mCropParams.enable = true;
        mCropParams.compress = false;
        Intent intent1 = CropHelper.buildCameraIntent(mCropParams);
        startActivityForResult(intent1, CropHelper.REQUEST_CAMERA);
    }
    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForScan(PermissionRequest request) {
        showRationaleDialog(R.string.permission_camera_rationale,request);
    }
    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        mToastor.getSingletonToast(R.string.deny_camera_please_open).show();
    }
    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        if (!mCropParams.compress) {
            mBitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
            mCivAvator.setImageBitmap(mBitmap);
        }
    }

    @Override
    public void onCompressed(Uri uri) {
        mBitmap = BitmapUtil.decodeUriAsBitmap(this, uri);
        mCivAvator.setImageBitmap(mBitmap);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, "Crop failed: " + message, Toast.LENGTH_LONG)
             .show();
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }
}
