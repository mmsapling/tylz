package com.tylz.aelos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tylz.aelos.R;
import com.tylz.aelos.activity.ActionIdDetailActivity;
import com.tylz.aelos.activity.ActionSpecialActivity;
import com.tylz.aelos.base.BasePageAdapter;
import com.tylz.aelos.bean.LoopPicData;

import java.util.List;

/**
 * @author tylz
 * @time 2016/3/23 0023 12:41
 * @des
 *
 * @updateAuthor
 * @updateDate 2016/3/23 0023
 * @updateDes
 */
public class LoopPicPagerAdapter
        extends BasePageAdapter<LoopPicData>
{

    public LoopPicPagerAdapter(Context context, List<LoopPicData> dataSource) {
        super(context, dataSource);
    }

    @Override
    public int getCount() {
        if (mDataSource != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position = position % mDataSource.size();
        ImageView view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        final LoopPicData loopPicData = mDataSource.get(position);
        Picasso.with(mContext).load(loopPicData.picurl).placeholder(R.mipmap.applogo).into(view);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loopPicData.type.equals("2")){
                    Intent intent = new Intent(mContext, ActionSpecialActivity.class);
                    intent.putExtra(ActionSpecialActivity.EXTRA_DATA,loopPicData);
                    mContext.startActivity(intent);
                }else if(loopPicData.type.equals("1")){
                    Intent intent = new Intent(mContext, ActionIdDetailActivity.class);
                    intent.putExtra(ActionIdDetailActivity.EXTRA_DATA,loopPicData.ids);
                    mContext.startActivity(intent);
                }else if(loopPicData.type.equals("3")){
                    //跳网页
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(loopPicData.url));
                    mContext.startActivity(intent);
                }

            }
        });
        return view;
    }

}
