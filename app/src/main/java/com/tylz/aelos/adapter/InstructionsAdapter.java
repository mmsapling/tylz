package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.Instructions;
import com.tylz.aelos.util.StringUtils;

import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   InstructionsAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/8 15:20
 *  @描述：    TODO
 */
public class InstructionsAdapter
        extends MyBaseApdater<Instructions>
{
    public InstructionsAdapter(Context context, List<Instructions> dataSource)
    {
        super(context, dataSource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_instructions,null);
            convertView.setTag(holder);
            holder.ivArrow = (ImageButton) convertView.findViewById(R.id.iv_arrow);
            holder.rlContent = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            holder.rlTitle = (RelativeLayout) convertView.findViewById(R.id.rl_title);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Instructions data = mDataSource.get(position);
        /*赋值*/
        holder.tvTitle.setText(data.title);
        holder.tvContent.setText(StringUtils.clearHtml(data.content));
        /*箭头点击事件*/
        final ViewHolder finalHolder = holder;
        /*默认不显示*/
        finalHolder.ivArrow.setImageResource(R.mipmap.itemdown);
        finalHolder.rlContent.setVisibility(View.GONE);
        holder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = finalHolder.rlContent.getVisibility();
                /*
                    1.内容区域是隐藏的，那么箭头方向为下，点击后为内容区域可视，箭头向上
                    2.内容区域是可视的，那么箭头向上，点击后内容区域隐藏，箭头向下
                 */
                if(visibility == View.GONE){
                    finalHolder.ivArrow.setImageResource(R.mipmap.itemup);
                    finalHolder.rlContent.setVisibility(View.VISIBLE);
                }else{
                    finalHolder.ivArrow.setImageResource(R.mipmap.itemdown);
                    finalHolder.rlContent.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout rlTitle;
        RelativeLayout rlContent;
        TextView       tvTitle;
        TextView       tvContent;
        ImageButton    ivArrow;

    }
}
