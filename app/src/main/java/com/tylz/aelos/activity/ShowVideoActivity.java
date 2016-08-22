package com.tylz.aelos.activity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tylz.aelos.R;
import com.tylz.aelos.bean.VideoEntity;
import com.tylz.aelos.util.StringUtils;
import com.tylz.aelos.util.ToastUtils;
import com.tylz.aelos.util.videoutils.ImageCache;
import com.tylz.aelos.util.videoutils.ImageResizer;
import com.tylz.aelos.util.videoutils.Utils;
import com.tylz.aelos.view.RecyclingImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.activity
 *  @文件名:   ShowVideoActivity
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/20 13:26
 *  @描述：    展示视频界面
 *  @描述：    从凡信上面搞过来的
 */
public class ShowVideoActivity
        extends FragmentActivity
        implements AdapterView.OnItemClickListener
{
    public static final int    RESULT_LOCAL_VIDEO = 2000;
    public static final String EXTRA_DATA         = "extra_data";
    @Bind(R.id.gridView)
    GridView    mGridView;
    @Bind(R.id.iv_left)
    ImageButton mIvLeft;
    @Bind(R.id.tv_title)
    TextView    mTvTitle;
    private List<VideoEntity> mDatas;
    private ImageResizer      mImageResizer;
    private int               mImageThumbSize;
    private int               mImageThumbSpacing;
    private VideoAdapter      mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        mDatas = new ArrayList<>();
        mTvTitle.setText(R.string.please_select);
        mAdapter = new VideoAdapter(this);
        getVideoFile();
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageResizer = new ImageResizer(this, mImageThumbSize);
        mImageResizer.setLoadingImage(R.mipmap.empty_photo);
        mImageResizer.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mAdapter = new VideoAdapter(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help
                    // with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount)
            {
            }
        });

        mGridView.getViewTreeObserver()
                 .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                     @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                     @Override
                     public void onGlobalLayout() {
                         final int numColumns = (int) Math.floor(mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                         if (numColumns > 0) {
                             final int columnWidth = (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                             mAdapter.setItemHeight(columnWidth);

                             if (Utils.hasJellyBean()) {
                                 mGridView.getViewTreeObserver()
                                          .removeOnGlobalLayoutListener(this);
                             } else {
                                 mGridView.getViewTreeObserver()
                                          .removeGlobalOnLayoutListener(this);
                             }
                         }
                     }
                 });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageResizer.closeCache();
        mImageResizer.clearCache();
    }

    /**
     * 得到本地视频
     */
    private void getVideoFile() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                              null,
                                              null,
                                              null,
                                              null,
                                              null);
        if (cursor.moveToFirst()) {
            do {
                int         id       = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String      title    = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String      url      = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                int         duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                int         size     = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                VideoEntity entity   = new VideoEntity();
                entity.id = id;
                entity.title = title;
                entity.filePath = url;
                entity.duration = duration;
                entity.size = size;
                mDatas.add(entity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mImageResizer.setPauseWork(true);
        VideoEntity videoEntity = mDatas.get(position);
        // 限制大小不能超过10M
        if (videoEntity.size > 1024 * 1024 * 10) {
            ToastUtils.showToast(R.string.video_not_supported);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, videoEntity);
        setResult(RESULT_LOCAL_VIDEO, intent);
        finish();
    }

    @OnClick(R.id.iv_left)
    public void onClick() {
        finish();
    }

    private class VideoAdapter
            extends BaseAdapter
    {
        private Context mContext;
        private int mItemHeight = 0;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;

        public VideoAdapter(Context context) {
            mContext = context;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                                     RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the
         * height can be set to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                                     mItemHeight);
            mImageResizer.setImageSize(height);
            notifyDataSetChanged();
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_show_video, null);
                holder.imageView = (RecyclingImageView) convertView.findViewById(R.id.imageView);
                holder.icon = (ImageView) convertView.findViewById(R.id.video_icon);
                holder.tvDur = (TextView) convertView.findViewById(R.id.chatting_length_iv);
                holder.tvSize = (TextView) convertView.findViewById(R.id.chatting_size_iv);
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Check the height matches our calculated column width
            if (holder.imageView.getLayoutParams().height != mItemHeight) {
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this
            // also takes care of
            // setting a placeholder image while the background thread runs
            holder.icon.setVisibility(View.VISIBLE);
            VideoEntity entty = mDatas.get(position);
            holder.tvDur.setVisibility(View.VISIBLE);
            holder.tvDur.setText(StringUtils.dur2Str(entty.duration));
            holder.tvSize.setText(StringUtils.formatFileSize(entty.size));
            holder.imageView.setImageResource(R.mipmap.empty_photo);
            mImageResizer.loadImage(entty.filePath, holder.imageView);
            return convertView;
        }

        class ViewHolder {
            RecyclingImageView imageView;
            ImageView          icon;
            TextView           tvDur;
            TextView           tvSize;
        }
    }
}
