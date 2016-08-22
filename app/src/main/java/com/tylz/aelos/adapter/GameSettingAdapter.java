package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;
import com.tylz.aelos.bean.SettingTypeData;

import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   GameSettingAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/14 16:38
 *  @描述：    动作配置界面适配器
 */
public class GameSettingAdapter extends MyBaseApdater<SettingTypeData> {
    public GameSettingAdapter(Context context, List<SettingTypeData> dataSource) {
        super(context, dataSource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_game_setting,null);
            convertView.setTag(holder);
            holder.tvAction = (TextView) convertView.findViewById(R.id.item_tv_action);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        SettingTypeData settingTypeData = mDataSource.get(position);
        holder.tvAction.setText(settingTypeData.title);
        if(settingTypeData.isFlag == 0){
            holder.tvAction.setBackgroundResource(R.mipmap.action_download_false);
        }else if(settingTypeData.isFlag == 1){
            holder.tvAction.setBackgroundResource(R.mipmap.action_download_true);
        }else if(settingTypeData.isFlag == 2){
            holder.tvAction.setBackgroundResource(R.mipmap.action_select);
        }
        return convertView;
    }
    private class ViewHolder{
        TextView tvAction;
    }
}
