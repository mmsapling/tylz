package com.tylz.aelos.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tylz.aelos.R;

/**
 * @项目名: Aelos1
 * @包名: com.gxy.adapter
 * @类名: ShopGridViewAdapter
 * @创建者: 陈选文
 * @创建时间: 2016-7-17 下午3:14:13
 * @描述: TODO
 *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class ShopGridViewAdapter
        extends BaseAdapter
{
    private Context mContext;
    private int[] mDatas = {R.drawable.selector_base_action,
                            R.drawable.selector_music,
                            R.drawable.selector_fable,
                            R.drawable.selector_football,
                            R.drawable.selector_glave_fight,
                            R.drawable.selector_shop_custom};

    public ShopGridViewAdapter(Context context) {
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
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_shop_gird, null);
        }
        ImageView view = (ImageView) convertView.findViewById(R.id.item_iv);
        view.setImageResource(mDatas[position]);
        return convertView;
    }

}
