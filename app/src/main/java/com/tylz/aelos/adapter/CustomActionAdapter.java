package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.CustomAction;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.util.UIUtils;
import com.tylz.aelos.view.DActionSheetDialog;

import java.util.List;

/**
 * @项目名: Aelos1
 * @包名: com.gxy.adapter
 * @类名: CustomActionAdapter
 * @创建者: 陈选文
 * @创建时间: 2016-6-24 下午4:05:42
 * @描述: 自定义动作界面的适配器
 *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class CustomActionAdapter
        extends MyBaseApdater<CustomAction>
{
    private DbHelper            mDbHelper;
    private OnClickPlayListener mOnClickPlayListener;

    public CustomActionAdapter(Context context, List<CustomAction> dataSource) {
        super(context, dataSource);
        mDbHelper = new DbHelper(mContext);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_custom_action, null);
            holder.tvNum = (TextView) convertView.findViewById(R.id.item_tv_num);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.ibPlay = (ImageButton) convertView.findViewById(R.id.item_ib_play);
            holder.ibUpload = (ImageButton) convertView.findViewById(R.id.item_ib_upload);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final CustomAction action = mDataSource.get(position);
        holder.tvNum.setText("" + (position + 1));
        holder.tvName.setText(action.title);
        holder.ibPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickPlayListener != null) {
                    mOnClickPlayListener.onClickPlay(action);
                }
            }
        });
        holder.ibUpload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
               if(mOnClickPlayListener != null){
                   mOnClickPlayListener.onClickUpload(action);
               }
            }
        });
        return convertView;

    }



    public void addAction(CustomAction action)
    {
        mDataSource.add(action);
        notifyDataSetChanged();
    }

    public void addAllAction(List<CustomAction> datas)
    {
        mDataSource = datas;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView    tvNum;
        TextView    tvName;
        ImageButton ibPlay;
        ImageButton ibUpload;
    }

    public interface OnClickPlayListener {
        /**播放动作*/
        void onClickPlay(CustomAction action);

        /**动作上传*/
        void onClickUpload(CustomAction action);
    }

    public void setOnClickPlayListener(OnClickPlayListener onClickPlayListener) {
        this.mOnClickPlayListener = onClickPlayListener;
    }
}
