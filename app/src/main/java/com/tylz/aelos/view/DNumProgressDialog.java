package com.tylz.aelos.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.tylz.aelos.R;
import com.tylz.aelos.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author tylz
 * @time 2016/3/31 0031 14:04
 * @des 自定义个进度条
 *
 * @updateAuthor
 * @updateDate 2016/3/31 0031
 * @updateDes
 */
public class DNumProgressDialog
        extends Dialog
{
    @Bind(R.id._tv_loading_msg)
    TextView    mTvLoadingMsg;
    @Bind(R.id.numberbar)
    NumberProgressBar mNumberProgressBar;
    public DNumProgressDialog(Context context) {
        super(context, R.style.DAlertDialogStyle);
        initView();
    }


    public DNumProgressDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    private void initView() {
        setContentView(R.layout.view_numprogress_dialog);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mTvLoadingMsg.setText(UIUtils.getString(R.string.hard_loading));
    }
    public void setTip(int resId){
        mTvLoadingMsg.setText(UIUtils.getString(resId));
    }
    public void setTip(String tip){
        mTvLoadingMsg.setText(tip);
    }
    public void showDialog(){
        show();
        WindowManager              windowManager = getWindow().getWindowManager();
        Display                    display       = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp            = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); //设置宽度
        getWindow().setAttributes(lp);
    }
    public void setProgress(int progress){
        mNumberProgressBar.setProgress(progress);
    }
}
