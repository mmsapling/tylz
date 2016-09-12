package com.tylz.aelos.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.adapter.TypeAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.UploadType;
import com.tylz.aelos.bean.VideoEntity;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.CommomUtil;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DActionSheetDialog;
import com.tylz.aelos.view.DNumProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   UploadActionActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/19 19:20
 *  @描述：    上传动作界面
 */
@RuntimePermissions
public class UploadActionActivity
        extends BaseActivity
{
    public static final  String EXTRA_DATA             = "extra_data";
    private static final int    REQUEST_LOCAL_VIDEO    = 1002;
    private static final int    REQUEST_RECORDER_VIDEO = 1003;
    private final        int    REQUEST_CODE_CAMERA    = 1000;
    private final        int    REQUEST_CODE_GALLERY   = 1001;
    @Bind(R.id.iv_left)
    ImageButton     mIvLeft;
    @Bind(R.id.tv_title)
    TextView        mTvTitle;
    @Bind(R.id.iv_right)
    ImageButton     mIvRight;
    @Bind(R.id.et_action_name)
    EditText        mEtActionName;
    @Bind(R.id.civ_type)
    CircleImageView mCivType;
    @Bind(R.id.civ_photo)
    CircleImageView mCivPhoto;
    @Bind(R.id.civ_video)
    CircleImageView mCivVideo;
    @Bind(R.id.et_action_des)
    EditText        mEtActionDes;
    /*下面四个为上传的数据*/
    private CustomAction             mCustomAction;
    private List<PhotoInfo>          mPhotoInfos;
    private VideoEntity              mVideoEntity;
    private UploadType               mUploadType;
    private String                   mActionName;
    private String                   mActionDes;
    private DNumProgressDialog       mNumProgressDialog;
    private ConnectBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_action);
        ButterKnife.bind(this);
        initData();
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_CONNECTED);
        intentFilter.addAction(BlueService.ACTION_RETURN_RSSI);
        intentFilter.addAction(BlueService.ACTION_RETURN_DATA);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 蓝牙连接相关广播接受者
     */
    private class ConnectBroadcastReceiver
            extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BlueService.ACTION_UNCONNECT:
                    skipScanUI();
                    break;
                default:
                    break;
            }

        }

    }

    private void initData() {
        mPhotoInfos = new ArrayList<>();
        mCustomAction = (CustomAction) getIntent().getSerializableExtra(EXTRA_DATA);
        mTvTitle.setText(R.string.upload_action);
        mIvRight.setImageResource(R.mipmap.upload_uploading);
        mEtActionName.setText(mCustomAction.title);
    }

    @OnClick({R.id.iv_left,
              R.id.iv_right,
              R.id.civ_type,
              R.id.civ_photo,
              R.id.civ_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                if (isLogin()) {
                    upload();
                } else {
                    showLoginTip();
                }
                break;
            case R.id.civ_type:
                clickType();
                break;
            case R.id.civ_photo:
                clickPhoto();
                break;
            case R.id.civ_video:
                clickVideo();
                break;
        }
    }

    private boolean checkUpload() {
        mActionName = mEtActionName.getText()
                                   .toString();
        mActionDes = mEtActionDes.getText()
                                 .toString();
        if (TextUtils.isEmpty(mActionName)) {
            mToastor.getSingletonToast(R.string.empty_action_name)
                    .show();
            return false;
        } else if (TextUtils.isEmpty(mActionDes)) {
            mToastor.getSingletonToast(R.string.empty_action_des)
                    .show();
            return false;
        } else if (mUploadType == null) {
            mToastor.getSingletonToast(R.string.please_select_type)
                    .show();
            return false;
        } else if (mPhotoInfos.size() == 0) {
            mToastor.getSingletonToast(R.string.please_select_photo)
                    .show();
            return false;
        } else if (mVideoEntity == null) {
            mToastor.getSingletonToast(R.string.please_select_video)
                    .show();
            return false;
        } else if (TextUtils.isEmpty(mCustomAction.filestream)) {
            mToastor.getSingletonToast(R.string.empty_action)
                    .show();
            return false;
        }
        return true;
    }

    private void upload() {
        if (!checkUpload()) {
            return;
        }
        File picFile = new File(mPhotoInfos.get(0)
                                           .getPhotoPath());
        LogUtils.d(picFile.getAbsolutePath());
        File videoFile = new File(mVideoEntity.filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoInfos.get(0)
                                                            .getPhotoPath());
        showNumProcess();

        OkHttpUtils.post()

                   .url(HttpUrl.BASE + "uploadAction")
                   .addParams("title", mActionName)
                   .addParams("content", mActionDes)
                   .addParams("type", mUploadType.title)
                   .addParams("actionStream", mCustomAction.filestream)
                   .addParams("userId", mUser_id)
                   .addParams("titlestream", mCustomAction.titlestream)
                   .addParams("picurl", StringUtils.imgToBase64(bitmap))
                   .addFile("video", videoFile.getName(), videoFile)
                   .build()
                   .execute(new Callback() {
                       @Override
                       public Object parseNetworkResponse(Response response, int id)
                               throws Exception
                       {
                           closeNumProcess();
                           LogUtils.d("parseNetworkResponse = " + response.message());
                           return null;
                       }

                       @Override
                       public void inProgress(float progress, long total, int id) {
                           mNumProgressDialog.setProgress((int) (progress * 100));
                       }

                       @Override
                       public void onError(Call call, Exception e, int id) {
                           ToastUtils.showToast(R.string.error_upload);
                           LogUtils.d("onError = " + e.getMessage());
                           closeNumProcess();
                       }

                       @Override
                       public void onResponse(Object response, int id) {
                           if (null != response) {
                               LogUtils.d("upload = " + response.toString());
                           }
                           closeNumProcess();
                           mToastor.getSingletonToast(R.string.success_upload)
                                   .show();
                           UploadActionActivity.this.finish();

                       }
                   });
    }

    /**展示进度条弹窗*/
    private void showNumProcess() {
        mNumProgressDialog = new DNumProgressDialog(this);
        if (!mNumProgressDialog.isShowing()) {
            mNumProgressDialog.showDialog();
        }
    }

    /**关闭进度条弹窗*/
    private void closeNumProcess() {
        if (mNumProgressDialog != null) {
            mNumProgressDialog.dismiss();
            mNumProgressDialog = null;
        }
    }

    private void clickType() {


        final Dialog dialog = new Dialog(this, R.style.ThemeIOSDialog);
        dialog.setContentView(R.layout.dialog_view_gridview);
        GridView          gridView = (GridView) dialog.findViewById(R.id.view_gridview);
        final TypeAdapter adapter  = new TypeAdapter(this);
        //  gridView.setLayoutAnimation(getAnimationController());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUploadType = (UploadType) adapter.getItem(position);
                mCivType.setImageResource(mUploadType.resId);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * 动画
     * @return
     *      LayoutAnimationController
     * @deprecated
     */
    private LayoutAnimationController getAnimationController() {
        int          duration = 300;
        AnimationSet set      = new AnimationSet(false);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                                           0.0f,
                                           Animation.RELATIVE_TO_SELF,
                                           0.0f,
                                           Animation.RELATIVE_TO_SELF,
                                           -1.0f,
                                           Animation.RELATIVE_TO_SELF,
                                           0.0f);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    private void clickVideo() {
        new DActionSheetDialog(this).builder()
                                    .setTitle(UIUtils.getString(R.string.please_select))
                                    .setCancelable(false)
                                    .setCanceledOnTouchOutside(false)
                                    .addSheetItem(UIUtils.getString(R.string.recording),
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectVideo(which);
                                                      }
                                                  })
                                    .addSheetItem(UIUtils.getString(R.string.local_video),
                                                  DActionSheetDialog.SheetItemColor.Blue,
                                                  new DActionSheetDialog.OnSheetItemClickListener() {
                                                      @Override
                                                      public void onClick(int which) {
                                                          selectVideo(which);
                                                      }
                                                  })
                                    .show();
    }

    private void selectVideo(int which) {
        switch (which) {
            case 1: //录制视频
                UploadActionActivityPermissionsDispatcher.showOpenCarmeraAndRecordWithCheck(this);
                break;
            case 2: //本地视频
                Intent intent2 = new Intent(this, ShowVideoActivity.class);
                startActivityForResult(intent2, REQUEST_LOCAL_VIDEO);
                break;
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showOpenCarmera() {
        try {
            GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showOpenCarmeraAndRecord() {
        UploadActionActivityPermissionsDispatcher.showOpenRecorderWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void showOpenRecorder() {
        Intent intent1 = new Intent(UploadActionActivity.this, RecorderVideoActivity.class);
        intent1.putExtra(RecorderVideoActivity.EXTRA_DATA, mCustomAction);
        startActivityForResult(intent1, REQUEST_RECORDER_VIDEO);
    }


    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(PermissionRequest request) {
        showRationaleDialog(R.string.permission_camera_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        mToastor.getSingletonToast(R.string.deny_camera_please_open)
                .show();
    }
    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onRecorderDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        mToastor.getSingletonToast(R.string.deny_recorder_please_open)
                .show();
    }
    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    void showRationaleForRecorder(PermissionRequest request) {
        showRationaleDialog(R.string.permission_recorder_rationale, request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UploadActionActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this).setPositiveButton(R.string.button_allow,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(@NonNull DialogInterface dialog,
                                                                                int which)
                                                            {
                                                                request.proceed();
                                                            }
                                                        })
                                     .setNegativeButton(R.string.button_deny,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(@NonNull DialogInterface dialog,
                                                                                int which)
                                                            {
                                                                request.cancel();
                                                            }
                                                        })
                                     .setCancelable(false)
                                     .setMessage(messageResId)
                                     .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCAL_VIDEO && resultCode == ShowVideoActivity.RESULT_LOCAL_VIDEO) {
            if (data != null) {
                mVideoEntity = (VideoEntity) data.getSerializableExtra(ShowVideoActivity.EXTRA_DATA);
                loadFisrtPhotoVideo(mVideoEntity);
            }
        } else if (requestCode == REQUEST_RECORDER_VIDEO && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getParcelableExtra("uri");

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            VideoEntity entity = new VideoEntity();
            if (cursor.moveToFirst()) {
                // 路径：MediaStore.Audio.Media.DATA
                // 总播放时长：MediaStore.Audio.Media.DURATION
                int    id       = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title    = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String url      = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                int    duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                int    size     = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                entity.id = id;
                entity.title = title;
                entity.filePath = url;
                entity.duration = duration;
                entity.size = size;
            }
            mVideoEntity = entity;
            loadFisrtPhotoVideo(mVideoEntity);
        }
    }

    /**
     * 加载第一帧图片
     * @param videoEntity
     *      视频实体类
     */
    private void loadFisrtPhotoVideo(final VideoEntity videoEntity) {
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      int size = UIUtils.dp2Px(100);
                                      LogUtils.d("filepath =" + videoEntity.filePath);
                                      final Bitmap bitmap = CommomUtil.createVideoThumbnail(
                                              videoEntity.filePath,
                                              size,
                                              size);
                                      UIUtils.postTaskSafely(new Runnable() {
                                          @Override
                                          public void run() {
                                              if (bitmap == null) {
                                                  mCivVideo.setImageResource(R.mipmap.help_play);
                                              } else {
                                                  mCivVideo.setImageBitmap(bitmap);
                                              }
                                          }
                                      });

                                  }
                              });
    }

    protected void clickPhoto()
    {
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
                UploadActionActivityPermissionsDispatcher.showOpenCarmeraWithCheck(this);
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
            try {
                if (resultList != null && resultList.size() != 0) {
                    mPhotoInfos.clear();
                    mPhotoInfos.addAll(resultList);
                    PhotoInfo photoInfo = resultList.get(0);

                    Picasso.with(UploadActionActivity.this)
                           .load(new File(photoInfo.getPhotoPath()))
                           .into(mCivPhoto);
                }
            } catch (Exception e) {}

        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            ToastUtils.showToast(errorMsg);
        }
    };
}
