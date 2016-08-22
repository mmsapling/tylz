package com.tylz.aelos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.bean.UploadType;
import com.tylz.aelos.util.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tylz.aelos.R.id.item_civ_type;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.adapter
 *  @文件名:   TypeAdapter
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/21 15:50
 *  @描述：    TODO
 */
public class TypeAdapter
        extends BaseAdapter
{
    private List<UploadType>   mDatas;
    private Context            mContext;
    private TranslateAnimation taLeft, taRight, taTop, taBlow;

    public TypeAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
        UploadType type1 = new UploadType(UIUtils.getString(R.string.base_action),
                                          R.mipmap.upload_base_action);
        UploadType type2 = new UploadType(UIUtils.getString(R.string.music_dance),
                                          R.mipmap.upload_music);
        UploadType type3 = new UploadType(UIUtils.getString(R.string.fable), R.mipmap.upload_fable);
        UploadType type4 = new UploadType(UIUtils.getString(R.string.football),
                                          R.mipmap.upload_football);
        UploadType type5 = new UploadType(UIUtils.getString(R.string.boxing),
                                          R.mipmap.upload_glave_fight);
        UploadType type6 = new UploadType(UIUtils.getString(R.string.custom),
                                          R.mipmap.upload_custom);
        mDatas.add(type1);
        mDatas.add(type2);
        mDatas.add(type3);
        mDatas.add(type4);
        mDatas.add(type5);
        mDatas.add(type6);
        InitAnima();
    }

    private void InitAnima() {
        taLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                                        1.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        0.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        0.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        0.0f);
        taRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                                         -1.0f,
                                         Animation.RELATIVE_TO_PARENT,
                                         0.0f,
                                         Animation.RELATIVE_TO_PARENT,
                                         0.0f,
                                         Animation.RELATIVE_TO_PARENT,
                                         0.0f);
        taTop = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                                       0.0f,
                                       Animation.RELATIVE_TO_PARENT,
                                       0.0f,
                                       Animation.RELATIVE_TO_PARENT,
                                       1.0f,
                                       Animation.RELATIVE_TO_PARENT,
                                       0.0f);
        taBlow = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                                        0.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        0.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        -1.0f,
                                        Animation.RELATIVE_TO_PARENT,
                                        0.0f);
        taLeft.setDuration(1000);
        taRight.setDuration(1000);
        taTop.setDuration(1000);
        taBlow.setDuration(1000);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.view_type_gridview, null);
        }
        UploadType      uploadType = mDatas.get(position);
        TextView        tv         = (TextView) convertView.findViewById(R.id.item_tv_title);
        CircleImageView civ        = (CircleImageView) convertView.findViewById(item_civ_type);
        tv.setText(uploadType.title);
        civ.setImageResource(uploadType.resId);
        Random ran  = new Random();
        int    rand = ran.nextInt(4);
        switch (rand) {
            case 0:
                convertView.startAnimation(taLeft);
                break;
            case 1:
                convertView.startAnimation(taRight);
                break;
            case 2:
                convertView.startAnimation(taTop);
                break;
            case 3:
                convertView.startAnimation(taBlow);
                break;
        }
        return convertView;
    }
}