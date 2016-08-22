package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.bean.Status;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.util.CommUtils;
import com.tylz.aelos.util.ToastUtils;

import java.util.List;


/**
 * @项目名: Aelos1
 * @包名: com.gxy.adapter
 * @类名: CustomActionAdapter
 * @创建者: 陈选文
 * @创建时间: 2016-6-24 下午4:05:42
 * @描述: 状态的适配器
 *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class StatusAdapter
        extends MyBaseApdater<Status>
{
    private int                 mPosition;
    private DbHelper            mDbHelper;
    private CustomAction        mAction;
    private OnClickPlayListener mClickPlayListener;

    public StatusAdapter(Context context, List<Status> dataSource, CustomAction action) {
        super(context, dataSource);
        mDbHelper = new DbHelper(mContext);
        mAction = action;
        mPosition = -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_status, null);
            holder.itemContainerTop = (RelativeLayout) convertView.findViewById(R.id.item_container_top);
            holder.tvNum = (TextView) convertView.findViewById(R.id.item_tv_num);
            holder.ivTop = (ImageView) convertView.findViewById(R.id.item_iv_top);
            holder.ivBottom = (ImageView) convertView.findViewById(R.id.item_iv_bottom);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.item_iv_delete);
            holder.ivPlay = (ImageView) convertView.findViewById(R.id.item_iv_play);
            holder.itemContainer = (LinearLayout) convertView.findViewById(R.id.item_container);
            holder.itemContainerBottom = (LinearLayout) convertView.findViewById(R.id.item_container_bottom);
            holder.sbProgress = (SeekBar) convertView.findViewById(R.id.item_sb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Status status = (Status) getItem(position);
        holder.tvNum.setText("" + (position + 1));
        holder.sbProgress.setProgress(status.progress);
        if (status.isShow) {
            holder.itemContainerBottom.setVisibility(View.VISIBLE);
        } else {
            holder.itemContainerBottom.setVisibility(View.GONE);
        }
        // holder.itemContainerBottom.setVisibility(status.isShow ? 0 : 8);
        // 设置点击事件
        holder.ivDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                mDataSource.remove(position);
                mDbHelper.deleteStatusByStatusId(status.id + "");
                //更新自定义action状态
                String filestream = CommUtils.toActString11(mAction.fileName, mDataSource);
                mDbHelper.updateCustomAction(mAction.id + "", filestream);
                notifyDataSetChanged();
            }
        });
        holder.ivTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                toTop(position);
                notifyDataSetChanged();
            }
        });
        holder.ivBottom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                toBottom(position);
                notifyDataSetChanged();
            }
        });
        final ViewHolder tempHolder = holder;
        tempHolder.itemContainerTop.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v)
            {
                if (mDataSource.get(position).isShow) {
                    mPosition = -1;
                    mDataSource.get(position).isShow = false;
                    notifyDataSetChanged();
                } else {
                    if (mPosition != -1) {
                        mDataSource.get(mPosition).isShow = false;
                    }
                    mDataSource.get(position).isShow = true;
                    mPosition = position;
                    notifyDataSetChanged();
                }
                return true;
            }
        });


        holder.itemContainerTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                for (int i = 0; i < mDataSource.size(); i++) {
                    mDataSource.get(i).isShow = false;
                    notifyDataSetChanged();
                }
            }
        });

        holder.ivPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (mClickPlayListener != null) {
                    mClickPlayListener.onClickPlay(status);
                }
            }
        });

        holder.sbProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mDataSource.get(position).progress = seekBar.getProgress();
                notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }
        });
        return convertView;

    }

    private class ViewHolder {
        TextView       tvNum;
        ImageView      ivPlay;
        ImageView      ivTop;
        ImageView      ivBottom;
        ImageView      ivDelete;
        SeekBar        sbProgress;
        LinearLayout   itemContainer;
        LinearLayout   itemContainerBottom;
        RelativeLayout itemContainerTop;
        boolean isShow = false;
    }

    private void toTop(int position)
    {
        if (position == 0) {
            ToastUtils.showToast(R.string.first_recorder);
            return;
        }
        Status currentStatus = mDataSource.get(position);
        Status preStatus     = mDataSource.get(position - 1);
        Status tempStatus1   = new Status();
        Status tempStatus2   = new Status();
        tempStatus1 = currentStatus;
        tempStatus2 = preStatus;
        mDataSource.set(position, tempStatus2);
        mDataSource.set(position - 1, tempStatus1);
    }

    private void toBottom(int position)
    {
        if (position == mDataSource.size() - 1) {
            ToastUtils.showToast(R.string.end_recorder);
            return;
        }
        Status currentStatus = mDataSource.get(position);
        Status preStatus     = mDataSource.get(position + 1);
        Status tempStatus1   = new Status();
        Status tempStatus2   = new Status();
        tempStatus1 = currentStatus;
        tempStatus2 = preStatus;
        mDataSource.set(position, tempStatus2);
        mDataSource.set(position + 1, tempStatus1);
    }

    public interface OnClickPlayListener {
        void onClickPlay(Status status);
    }

    public void setOnClickPlayListener(OnClickPlayListener onClickPlayListener) {
        this.mClickPlayListener = onClickPlayListener;
    }
}
