package com.tylz.aelos.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.MsgBean;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.util.UIUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   MsgAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/19 21:22
 *  @描述：    消息适配器
 */
public class MsgAdapter
        extends MyBaseApdater<MsgBean>
{
    private final SparseBooleanArray mCollapsedStatus;
    public MsgAdapter(Context context, List<MsgBean> dataSource) {
        super(context, dataSource);
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_my_msg, null);
            convertView.setTag(holder);
            holder.civAvator = (CircleImageView) convertView.findViewById(R.id.item_civ_avator);
            holder.tvContent = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.item_tv_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);
            holder.tvType = (TextView) convertView.findViewById(R.id.item_tv_type);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MsgBean msgBean = mDataSource.get(position);
        holder.tvTitle.setText(msgBean.opUserNickname);
        holder.tvType.setText(type2Str(msgBean.type));
        holder.tvTime.setText(msgBean.updateTime);
        holder.tvContent.setText(msgBean.content,mCollapsedStatus,position);
        Picasso.with(mContext).load(msgBean.avatar).placeholder(R.mipmap.defaultavatar).into(holder.civAvator);
        return convertView;
    }
    private String type2Str(int type){
        if(type == 0){
            return UIUtils.getString(R.string.praise);
        }else if(type == 1){
            return UIUtils.getString(R.string.comment);
        }else if(type == 2){
            return UIUtils.getString(R.string.reply);
        }
        return "";
    }
    private class ViewHolder {
        CircleImageView    civAvator;
        TextView           tvTitle;
        TextView           tvType;
        TextView           tvTime;
        ExpandableTextView tvContent;
    }
}
