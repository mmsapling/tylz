package com.tylz.aelos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


/**
 * @项目名: Aelos1
 * @包名: com.gxy.views
 * @类名: MyGridView
 * @创建者: 陈选文
 * @创建时间: 2016-7-17	下午3:33:03
 * @描述: TODO
 *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class MeasureListView
        extends ListView
{
    public MeasureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureListView(Context context) {
        super(context);
    }

    public MeasureListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
