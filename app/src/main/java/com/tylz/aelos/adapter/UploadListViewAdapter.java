package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.UploadBean;
import com.tylz.aelos.db.DbHelper;
import com.tylz.aelos.util.UIUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   ShopListViewAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/27 15:19
 *  @描述：    TODO
 */
public class UploadListViewAdapter
        extends MyBaseApdater<UploadBean>
{

    private DbHelper mDbHelper;

    public UploadListViewAdapter(Context context, List<UploadBean> dataSource) {
        super(context, dataSource);
        mDbHelper = new DbHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_upload_lisview, null);
            holder = new ViewHolder();
            holder.tvDownload = (TextView) convertView.findViewById(R.id.item_tv_download);
            holder.tvName = (TextView) convertView.findViewById(R.id.item_tv_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);
            holder.tvType = (TextView) convertView.findViewById(R.id.item_tv_type);
            holder.ivDownload = (ImageView) convertView.findViewById(R.id.item_iv_download);
            holder.ivCollect = (ImageView) convertView.findViewById(R.id.item_ib_collect);
            holder.tvCheckStatus = (TextView) convertView.findViewById(R.id.item_tv_check_status);
            holder.civ = (CircleImageView) convertView.findViewById(R.id.item_civ_avator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UploadBean shopBean = mDataSource.get(position);
        //LogUtils.d("is= " +  shopBean.isdownload);
        holder.tvCheckStatus.setText(state2Str(shopBean.state));
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
        }else{
            holder.tvDownload.setText(UIUtils.getString(R.string.download) + " " + shopBean.downloadCount);
        }
        Picasso.with(mContext).load(shopBean.picurl).placeholder(R.mipmap.noimg).into(holder.civ);
        return convertView;
    }

    private class ViewHolder {
        CircleImageView civ;
        TextView tvName;
        TextView tvTime;
        TextView tvType;
        TextView tvCheckStatus;

        TextView  tvDownload;
        ImageView ivDownload;
        ImageView ivCollect;
    }

    private String state2Str(String state) {
        String result = UIUtils.getString(R.string.checking);
        switch (state) {
            case "0":
                result = UIUtils.getString(R.string.checking);
                break;
            case "1":
                result = UIUtils.getString(R.string.checked);
                break;
            case "2":
                result = UIUtils.getString(R.string.checked_error);
                break;
        }
        return result;
    }
}
