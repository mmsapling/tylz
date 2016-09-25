package com.tylz.aelos.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.adapter.CustomActionAdapter;
import com.tylz.aelos.adapter.ImgAdapter;
import com.tylz.aelos.base.BaseActivity;
import com.tylz.aelos.base.IBluetooth;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.Status;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.factory.ThreadPoolProxyFactory;
import com.tylz.aelos.manager.Constants;
import com.tylz.aelos.manager.HttpUrl;
import com.tylz.aelos.service.BlueService;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.LogUtils;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   CustomActionActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/18 17:26
 *  @描述：    自建动作
 */
public class CustomActionActivity
        extends BaseActivity
        implements AdapterView.OnItemClickListener,
                   CustomActionAdapter.OnClickPlayListener,
                   AdapterView.OnItemLongClickListener
{
    @Bind(R.id.iv_left)
    ImageButton    mIvLeft;
    @Bind(R.id.tv_title)
    TextView       mTvTitle;
    @Bind(R.id.iv_new_action)
    ImageView      mIvNewAction;
    @Bind(R.id.iv_help)
    ImageView      mIvHelp;
    @Bind(R.id.ib_add_action)
    ImageButton    mIbAddAction;
    @Bind(R.id.container_not_content)
    RelativeLayout mContainerNotContent;
    @Bind(R.id.ib_add_action1)
    ImageButton    mIbAddAction1;
    @Bind(R.id.lv_listview)
    ListView       mLvListview;
    @Bind(R.id.container_have_content)
    LinearLayout   mContainerHaveContent;
    @Bind(R.id.fl_action)
    FrameLayout    mFlAction;
    @Bind(R.id.help_listview)
    ListView       mHelpListview;
    @Bind(R.id.fl_help)
    FrameLayout    mFlHelp;
    @Bind(R.id.iv_right)
    ImageButton    mIvRight;
    private List<CustomAction>       mDatas;
    private CustomActionAdapter      mAdapter;
    private DbHelper                 mDbHelper;
    private IBluetooth               mIBluetooth;
    private BlueServiceConnection    mServiceConnection;
    private ConnectBroadcastReceiver mReceiver;
    private String                   mPlayStatusOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_action);
        ButterKnife.bind(this);
        initBlue();
        initData();
        loadData();
    }

    private void initBlue() {
        Intent service = new Intent(this, BlueService.class);
        mServiceConnection = new BlueServiceConnection();
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        mReceiver = new ConnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueService.ACTION_UNCONNECT);
        intentFilter.addAction(BlueService.ACTION_RETURN_DATA);
        registerReceiver(mReceiver, intentFilter);
    }

    private void initData() {
        mPlayStatusOrder = "9200000000100000";
        mDbHelper = new DbHelper(this);
        mTvTitle.setText(R.string.custom_action);
        mIvRight.setImageResource(R.mipmap.help_play);
        mDatas = new ArrayList<>();
        mHelpListview.setAdapter(new ImgAdapter(this));
        mAdapter = new CustomActionAdapter(this, mDatas);
        mAdapter.setOnClickPlayListener(this);
        mLvListview.setAdapter(mAdapter);
        selectMenu(true);
        mLvListview.setOnItemClickListener(this);
        mLvListview.setOnItemLongClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    /**
     * 从数据库中加载数据
     */
    private void loadData() {

        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      final List<CustomAction> actions = mDbHelper.findCustomActionList();
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              mDatas.clear();
                                              mDatas.addAll(actions);
                                              mAdapter.notifyDataSetChanged();
                                              showOrNotAddActionView();
                                          }
                                      });

                                  }
                              });
    }

    @OnClick({R.id.iv_left,
              R.id.iv_new_action,
              R.id.iv_help,
              R.id.ib_add_action,
              R.id.ib_add_action1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_new_action:
                selectMenu(true);
                break;
            case R.id.iv_help:
                selectMenu(false);
                break;
            case R.id.ib_add_action:
            case R.id.ib_add_action1:
                addAction();
                break;
        }
    }

    /**
     * 设置菜单选择时候的状态变化
     * @param isSelected
     *      true 选中左边状态，false选中右边状态
     */
    private void selectMenu(boolean isSelected)
    {
        if (isSelected) {
            mIvNewAction.setImageResource(R.mipmap.new_action_select);
            mIvHelp.setImageResource(R.mipmap.help_normal);
            mFlAction.setVisibility(View.VISIBLE);
            mFlHelp.setVisibility(View.GONE);
            mIvRight.setVisibility(View.GONE);
        } else {
            mIvHelp.setImageResource(R.mipmap.help_select);
            mIvNewAction.setImageResource(R.mipmap.new_action_normal);
            mFlAction.setVisibility(View.GONE);
            mFlHelp.setVisibility(View.VISIBLE);
            mIvRight.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 添加动作
     */
    private void addAction()
    {

        final Dialog dialog = new Dialog(this, R.style.DActionSheetDialogStyle);
        dialog.setContentView(R.layout.dialog_alterdialog);
        Button         btCancel     = (Button) dialog.findViewById(R.id.alterdialog_button_cancel);
        Button         btConfirm    = (Button) dialog.findViewById(R.id.alterdialog_button_confirm);
        final EditText etActionName = (EditText) dialog.findViewById(R.id.et_action_name);
        dialog.show();
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                String name = etActionName.getText()
                                          .toString();
                if (TextUtils.isEmpty(name)) {
                    mToastor.getSingletonToast(R.string.empty_action_name)
                            .show();
                    return;
                }
                dialog.dismiss();
                insertData(name);
            }
        });

    }

    private void insertData(final String name) {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      CustomAction action = new CustomAction();
                                      action.title = name;
                                      action.fileName = System.currentTimeMillis() + ".act";
                                      action.title = name;
                                      action.titlestream = CommUtils.toAsciiAddLen(action.fileName);
                                      String fileName    = "";
                                      String content     = "";
                                      String filestream  = "";
                                      String titlestream = action.titlestream;
                                      String id          = action.fileName;
                                      String picurl      = "";
                                      String second      = "";
                                      String thumbnails  = "";
                                      String title       = name;
                                      String type        = "自定义";
                                      String video       = "";
                                      String isEdit      = "true";
                                      long isSuccess = mDbHelper.insertAction(fileName,
                                                                              content,
                                                                              filestream,
                                                                              titlestream,
                                                                              id,
                                                                              picurl,
                                                                              second,
                                                                              thumbnails,
                                                                              title,
                                                                              type,
                                                                              video,
                                                                              isEdit);

                                      //找出此action的id  然后更新数据源

                                      CustomAction realAction = mDbHelper.findCustomAction(action.titlestream);
                                      mDatas.add(realAction);
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mAdapter.notifyDataSetChanged();
                                              showOrNotAddActionView();
                                          }
                                      });

                                  }
                              });

    }

    /**
     * 判断添加动作的按钮是否展示及展示正确的位置
     */
    private void showOrNotAddActionView()
    {
        if (mDatas.size() == 0) {
            mContainerHaveContent.setVisibility(View.GONE);
            mContainerNotContent.setVisibility(View.VISIBLE);
        } else {
            mContainerHaveContent.setVisibility(View.VISIBLE);
            mContainerNotContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomAction customAction = mDatas.get(position);
        Intent       intent       = new Intent(this, AddStatusActivity.class);
        intent.putExtra(AddStatusActivity.EXTRA_DATA, customAction);
        startActivity(intent);
    }
    private long mCurrentTime;
    private boolean isFirst = true;
    private boolean isSecond;
    @Override
    public void onClickPlay(final CustomAction action) {
        mCurrentTime = System.currentTimeMillis();
        LogUtils.d("mCurrentTime = " + System.currentTimeMillis());
        //得到速度
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      //CustomAction customAction = mDbHelper.findCustomAction(action.titlestream);
                                      List<Status> statusList = mDbHelper.findStatussByActionId(
                                              action.id + "");
                                      long playTime = CommUtils.getTotalPlayTime(statusList);
                                      long totalTime = playTime + Constants.PLAY_ACTION_SLEEP_TIME * statusList.size() - 1;
                                      LogUtils.d("totalTime = " + totalTime);
                                      LogUtils.d("CurrentTime = " + System.currentTimeMillis());
                                      if(!isFirst){
                                          if(System.currentTimeMillis() - mCurrentTime < totalTime){
                                              LogUtils.d("返回");
                                              ThreadPoolProxyFactory.createNormalThreadPoolProxy().remove(this);
                                              return;
                                          }
                                      }
                                      isFirst = false;
                                      if (statusList != null && statusList.size() != 0) {
                                          for (int i = 0; i < statusList.size(); i++) {
                                              Status status = statusList.get(i);
                                              playStatus(status.arr,
                                                         status.progress);
                                              SystemClock.sleep(Constants.PLAY_ACTION_SLEEP_TIME);
                                          }
                                      } else {
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  mToastor.getSingletonToast(R.string.not_status)
                                                          .show();
                                              }
                                          });

                                      }
                                      isFirst = true;
                                  }
                              });
    }

    @Override
    public void onClickUpload(CustomAction action) {
        Intent intent = new Intent(this, UploadActionActivity.class);
        intent.putExtra(UploadActionActivity.EXTRA_DATA, action);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final CustomAction customAction = mDatas.get(position);

        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setCancelable(false)
                              .setMsg(UIUtils.getString(R.string.tip_delete_action))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      deleteAction(customAction);
                                  }
                              })
                              .show();
        return true;
    }

    /**
     * 删除此动作，并且删除关联的状态
     * @param customAction
     *
     */
    private void deleteAction(final CustomAction customAction) {
        showProgress();
        ThreadPoolProxyFactory.createNormalThreadPoolProxy()
                              .execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      mDbHelper.deleteActionByTitleStream(customAction.titlestream);
                                      mDbHelper.deleteStatusByActionId(customAction.id + "");
                                      mDatas.remove(customAction);
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              closeProgress();
                                              mAdapter.notifyDataSetChanged();
                                          }
                                      });

                                  }
                              });
    }

    @OnClick(R.id.iv_right)
    public void onClick() {
        clickPlay(HttpUrl.ACTION_CUSTOM);
    }
    private void clickPlay(String url) {
        boolean isWifi = mSpUtils.getBoolean(Constants.IS_DOWNLOAD_WIFI, true);
        boolean wifi   = CommUtils.isWifi(getApplicationContext());
        if(!wifi && isWifi){
            showPlayTip(url);
        }else{
            Intent it  = new Intent(Intent.ACTION_VIEW);
            Uri    uri = Uri.parse(url);
            it.setDataAndType(uri, "video/mp4");
            startActivity(it);
        }
    }
    private void showPlayTip(final String url) {
        new DAlertDialog(this).builder()
                              .setTitle(UIUtils.getString(R.string.tip))
                              .setMsg(UIUtils.getString(R.string.not_wifi_conn_play))
                              .setNegativeButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                  }
                              })
                              .setPositiveButton(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      Intent it  = new Intent(Intent.ACTION_VIEW);
                                      Uri    uri = Uri.parse(url);
                                      it.setDataAndType(uri, "video/mp4");
                                      startActivity(it);
                                  }
                              })
                              .show();

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
                case BlueService.ACTION_RETURN_DATA:
                    closeProgress();
                    String data = intent.getStringExtra(BlueService.EXTRA_DATA);
                    String flag = intent.getStringExtra(BlueService.EXTRA_FLAG);

                    break;
                default:
                    break;
            }

        }

    }

    /**
     * 播放状态
     * @param value
     *      状态数组
     * @param speed
     *      速度
     */
    public void playStatus(int[] value, int speed)
    {
        for (int i = 0; i < 16; i++) {
            String temp = Integer.toHexString(value[i]);
            if (temp.length() == 1) { temp = "0" + temp; }
            mPlayStatusOrder = mPlayStatusOrder + temp;
        }
        String hexString = Integer.toHexString(speed);
        if (hexString.length() == 1) {
            hexString = "0" + hexString;
        }
        mIBluetooth.callWrite(mPlayStatusOrder + hexString);
        mPlayStatusOrder = "9200000000100000";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private class BlueServiceConnection
            implements ServiceConnection
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBluetooth = (IBluetooth) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
