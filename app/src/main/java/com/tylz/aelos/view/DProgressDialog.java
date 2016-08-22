package com.tylz.aelos.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

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
public class DProgressDialog
        extends Dialog
{
    @Bind(R.id._tv_loading_msg)
    TextView    mTvLoadingMsg;

    public DProgressDialog(Context context) {
        super(context, R.style.progress_dialog);
        initView();
    }


    public DProgressDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    private void initView() {
        setContentView(R.layout.view_progress_dialog);
        ButterKnife.bind(this);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mTvLoadingMsg.setText(UIUtils.getString(R.string.hard_loading));
    }
    public void setTip(int resId){
        mTvLoadingMsg.setText(UIUtils.getString(resId));
    }
    public void setTip(String tip){
        mTvLoadingMsg.setText(tip);
    }

}
