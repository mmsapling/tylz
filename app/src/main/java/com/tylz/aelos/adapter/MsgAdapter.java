package com.tylz.aelos.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.tylz.aelos.R;
import com.tylz.aelos.base.MsgBean;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.util.DateUtils;

import java.util.List;

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
            holder.ivFlag = (ImageView) convertView.findViewById(R.id.item_iv_flag);
            holder.ivGo = (ImageView) convertView.findViewById(R.id.item_iv_go);
            holder.tvContent = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);

            holder.tvTitle = (TextView) convertView.findViewById(R.id.item_tv_title);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final MsgBean msgBean = mDataSource.get(position);
        holder.tvTime.setText(DateUtils.formatStrDatetime(msgBean.updateTime));
        holder.tvTitle.setText(type2Str(msgBean));
        holder.tvContent.setText(msgBean.content,mCollapsedStatus,position);
        if(msgBean.type == 1 || msgBean.type == 2){
            holder.ivFlag.setImageResource(R.mipmap.msg_red);
        }else{
            holder.ivFlag.setImageResource(R.mipmap.msg_blue);
        }
        return convertView;
    }


    private String type2Str(MsgBean bean){
        if(bean.type == 0){
            return bean.opUserNickname + "对" + bean.goodsname + "动作点赞";
        }else if(bean.type == 1){
            return bean.opUserNickname + "对你的" + bean.goodsname + "动作进行了新的评论！";
        }else if(bean.type == 2){
            return bean.opUserNickname + "回复了你的评论！";
        }
        return "";
    }
    private class ViewHolder {
        ImageView          ivFlag;
        ImageView          ivGo;
        TextView           tvTime;
        TextView           tvTitle;
        ExpandableTextView tvContent;
    }
}
