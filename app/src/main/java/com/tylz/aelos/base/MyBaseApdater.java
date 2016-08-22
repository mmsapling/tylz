package com.tylz.aelos.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @项目名: Aelos1
 * @包名: com.gxy.base
 * @类名: MyBaseApdater
 * @创建者: 陈选文
 * @创建时间: 2016-6-24 下午4:07:02
 * @描述: 适配器的基类
 * 
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class MyBaseApdater<ITEMBEANTYPE> extends BaseAdapter
{
	public List<ITEMBEANTYPE>	mDataSource	= new ArrayList<ITEMBEANTYPE>();
	public Context				mContext;

	/**
	 * 通过构造方法,让外部传入具体的数据源
	 */
	public MyBaseApdater(List<ITEMBEANTYPE> dataSource) {
		super();
		mDataSource = dataSource;
	}
	public MyBaseApdater(Context context, List<ITEMBEANTYPE> dataSource) {
		super();
		mDataSource = dataSource;
		mContext = context;
	}
	public MyBaseApdater(Context context) {
		super();
		mContext = context;
	}
	@Override
	public int getCount()
	{
		if (mDataSource != null) { return mDataSource.size(); }
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (mDataSource != null) { return mDataSource.get(position); }
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO
		return null;
	}
}
