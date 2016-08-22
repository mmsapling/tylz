package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.ShopBean;
import com.tylz.aelos.db.DbHelper;

import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   ShopListViewAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/27 15:19
 *  @描述：    TODO
 */
public class ShopListViewAdapter
        extends MyBaseApdater<ShopBean>
{

    private DbHelper mDbHelper;

    public ShopListViewAdapter(Context context, List<ShopBean> dataSource) {
        super(context, dataSource);
        mDbHelper = new DbHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_shop_lisview, null);
            holder = new ViewHolder();
            holder.tvDownload = (TextView) convertView.findViewById(R.id.item_tv_download);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);
            holder.tvType = (TextView) convertView.findViewById(R.id.item_tv_type);
            holder.ivDownload = (ImageView) convertView.findViewById(R.id.item_iv_download);
            holder.ivCollect = (ImageView) convertView.findViewById(R.id.item_ib_collect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShopBean shopBean = mDataSource.get(position);
        //LogUtils.d("is= " +  shopBean.isdownload);

        holder.tvType.setText(shopBean.type);
        holder.tvTime.setText(shopBean.second);
        holder.tvName.setText(shopBean.title);
        if (shopBean.iscollect.equals("false")) {
            holder.ivCollect.setSelected(false);
        } else {
            holder.ivCollect.setSelected(true);
        }
        if (shopBean.isdownload.equals("false")) {
            holder.ivDownload.setSelected(false);
            holder.tvDownload.setSelected(false);
        } else {
            holder.ivDownload.setSelected(true);
            holder.tvDownload.setSelected(true);
        }
        if (mDbHelper.isExistActionId(shopBean.id)) {
            holder.ivDownload.setSelected(true);
            holder.tvDownload.setSelected(true);
        } else {
            holder.ivDownload.setSelected(false);
            holder.tvDownload.setSelected(false);
        }
        if (shopBean.hasAction.equals("false")) {
            holder.tvDownload.setText(R.string.icon_preview);
            holder.tvDownload.setSelected(true);
            holder.ivDownload.setImageResource(R.drawable.selector_icon_prew);
            holder.ivDownload.setPressed(true);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView  tvName;
        TextView  tvTime;
        TextView  tvType;
        TextView  tvDownload;
        ImageView ivDownload;
        ImageView ivCollect;
    }
}
