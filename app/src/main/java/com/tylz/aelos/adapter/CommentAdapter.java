package com.tylz.aelos.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.Comment;
import com.tylz.aelos.util.DateUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   CommentAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/11 21:10
 *  @描述：    TODO
 */
public class CommentAdapter
        extends MyBaseApdater<Comment>
{
    private final SparseBooleanArray mCollapsedStatus;

    public CommentAdapter(Context context, List<Comment> dataSource) {
        super(context, dataSource);
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_comment, null);
            convertView.setTag(holder);
            holder.civAvator = (CircleImageView) convertView.findViewById(R.id.item_civ_avator);
            holder.tvComment = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.item_tv_username);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_tv_time);
            holder.tvReplyCount = (TextView) convertView.findViewById(R.id.item_tv_replay_count);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Comment comment = mDataSource.get(position);
        Picasso.with(mContext)
               .load(comment.avatar)
               .placeholder(R.mipmap.defaultavatar)
               .into(holder.civAvator);
        holder.tvComment.setText(comment.content, mCollapsedStatus, position);
        holder.tvNickName.setText(comment.nickname);
        holder.tvTime.setText(DateUtils.formatStrDatetime(comment.updateTime));
        holder.tvReplyCount.setText("" + comment.replyCount);
        return convertView;
    }

    private class ViewHolder {
        CircleImageView    civAvator;
        TextView           tvNickName;
        TextView           tvTime;
        TextView           tvReplyCount;
        ExpandableTextView tvComment;
    }

}
