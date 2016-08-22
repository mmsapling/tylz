package com.tylz.aelos.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.base.MyBaseApdater;


/**
 * @项目名: SampleAelos
 * @包名: com.aelos.sampleaelos.adapter
 * @类名: LeDeviceAdapter
 * @创建者: 陈选文
 * @创建时间: 2016-6-25 下午3:12:50
 * @描述: 蓝牙设备适配器
 * 
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class LeDeviceAdapter extends MyBaseApdater<BluetoothDevice>
{

	public LeDeviceAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_le_device, null);
			holder.tvAddress = (TextView) convertView.findViewById(R.id.item_tv_address);
			holder.tvName = (TextView) convertView.findViewById(R.id.item_tv_name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		BluetoothDevice device = (BluetoothDevice) getItem(position);
		holder.tvAddress.setText(device.getAddress());
		holder.tvName.setText(device.getName());
		return convertView;
	}

	private class ViewHolder
	{
		TextView	tvName;
		TextView	tvAddress;
	}

	public void addDevice(BluetoothDevice device)
	{
		if (!mDataSource.contains(device))
		{
			mDataSource.add(device);
		}
	}

	public BluetoothDevice getDevice(int position)
	{
		return mDataSource.get(position);
	}

	public void clear()
	{
		mDataSource.clear();
	}

}
