package com.tylz.aelos.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tylz.aelos.R;


/**
 * @项目名: 	Aelos1
 * @包名:	com.gxy.adapter
 * @类名:	ImgAdapter
 * @创建者:	陈选文
 * @创建时间:	2016-7-20	下午7:14:44 
 * @描述:	TODO
 * 
 * @svn版本:	$Rev$
 * @更新人:	$Author$
 * @更新时间:	$Date$
 * @更新描述:	TODO
 */
public class ImgAdapter extends BaseAdapter{
	private int[]	mDatas = {
			R.mipmap.help1, R.mipmap.help2, R.mipmap.help3, R.mipmap.help4, R.mipmap.help5,
			};
	private Context mContext;
	public ImgAdapter(Context context){
		mContext = context;
	}
	@Override
	public int getCount()
	{
		return mDatas.length;
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas[position];
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_img, null);
		}
		ImageView iv = (ImageView) convertView;
		iv.setImageResource(mDatas[position]);
		return convertView;
	}

	
}