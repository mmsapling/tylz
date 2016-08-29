package com.tylz.aelos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.Reply;
import com.tylz.aelos.bean.User;
import com.tylz.aelos.util.DateUtils;
import com.tylz.aelos.util.SPUtils;
import com.tylz.aelos.util.UIUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   ReplyAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/13 16:15
 *  @描述：    评论回复的适配器
 */
public class ReplyAdapter
        extends MyBaseApdater<Reply>
{
    private       SPUtils            mSPUtils;
    private       User               mUserInfo;
    private final SparseBooleanArray mCollapsedStatus;
    public ReplyAdapter(Context context, List<Reply> dataSource) {
        super(context, dataSource);
        mSPUtils = new SPUtils(context);
        mUserInfo = mSPUtils.getUserInfoBySp();
        mCollapsedStatus = new SparseBooleanArray();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_reply, null);
            convertView.setTag(holder);
            holder.civAvator = (CircleImageView) convertView.findViewById(R.id.item_civ_avator);
            holder.tvContent = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.item_tv_username);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Reply reply = mDataSource.get(position);

        Picasso.with(mContext)
               .load(reply.avatar)
               .placeholder(R.mipmap.defaultavatar)
               .into(holder.civAvator);
        holder.tvNickName.setText(reply.nickname);

        holder.tvTime.setText(DateUtils.formatStrDatetime(reply.updateTime));
        if (reply.linkid != 0) {
            String replyText = UIUtils.getString(R.string.reply);
            String replyName = reply.linkidNickname;
            String changeText = replyText + "@" + replyName + "：";
            holder.tvContent.setText(changeTextColor(changeText, reply.content),mCollapsedStatus,position);
        } else {
            holder.tvContent.setText(reply.content,mCollapsedStatus,position);
        }
        return convertView;
    }

    private class ViewHolder {
        CircleImageView    civAvator;
        TextView           tvTime;
        ExpandableTextView tvContent;
        TextView           tvNickName;
    }

    private SpannableStringBuilder changeTextColor(String changeText, String content) {
        SpannableStringBuilder builder = new SpannableStringBuilder(changeText);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(blueSpan, 2, changeText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(content);
        return builder;
    }
}
