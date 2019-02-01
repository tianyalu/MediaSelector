package com.sty.media.selector.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.sty.media.selector.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian on 2019/1/22.
 */

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder>{
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> selectedList = new ArrayList<>();  //被选中的列表
    private int selectMax = 9;
    private Context context;

    /**
     * 点击添加图片跳转
     */
    private OnAddPicClickListener mOnAddPicClickListener;
    private OnItemClickListener mOnItemClickListener;

    public interface OnAddPicClickListener{
        void onAddPicClick();
    }

    public interface OnItemClickListener{
        void onItemClick(int position, View v);
    }

    public void setmOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public GridImageAdapter(Context context, OnAddPicClickListener mOnAddPicClickListener){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax){
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> selectedList) {
        this.selectedList = selectedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image_grid, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //少于设置的最大选中图片张数时显示添加按钮
        if(getItemViewType(position) == TYPE_CAMERA){
            holder.mImg.setImageResource(R.drawable.addimg_1x);
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnAddPicClickListener != null){
                        mOnAddPicClickListener.onAddPicClick();
                    }
                }
            });
            holder.llDel.setVisibility(View.INVISIBLE);
        }else {
            holder.llDel.setVisibility(View.VISIBLE);
            holder.llDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = holder.getAdapterPosition();
                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码,
                    // 通过源码分析应该是bindViewHolder()暂时未绘制完成导致
                    if(index != RecyclerView.NO_POSITION){
                        selectedList.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, selectedList.size());
                    }
                }
            });

            LocalMedia media = selectedList.get(position);
            int mimeType = media.getMimeType();
            String path = "";
            if(media.isCut() && !media.isCompressed()){
                // 裁剪过
                path = media.getCutPath();
            } else if(media.isCompressed() || (media.isCut() && media.isCompressed())){
                //压缩过,或者裁剪同时压缩过,以最终压缩过的图片为准
                path = media.getCompressPath();
            } else {
                //原图
                path = media.getPath();
            }

            //图片
            if(media.isCompressed()){
                Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "K");
                Log.i("压缩地址: ", media.getCompressPath());
            }

            Log.i("原图地址: ", media.getPath());
            int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            if(media.isCut()){
                Log.i("裁剪地址:", media.getCutPath());
            }

            long duration = media.getDuration();
            holder.tvDuration.setVisibility(pictureType == PictureConfig.TYPE_VIDEO ? View.VISIBLE : View.GONE);
            if(mimeType == PictureMimeType.ofAudio()){
                holder.tvDuration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(holder.tvDuration, drawable, 0);
            }else {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(holder.tvDuration, drawable, 0);
            }
            holder.tvDuration.setText(DateUtils.timeParse(duration));

            if(mimeType == PictureMimeType.ofAudio()){
                holder.mImg.setImageResource(R.drawable.audio_placeholder);
            } else {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(holder.itemView.getContext())
                        .load(path)
                        .apply(options)
                        .into(holder.mImg);
            }

            //itemView的点击事件
            if(mOnItemClickListener != null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        mOnItemClickListener.onItemClick(adapterPosition, v);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if(selectedList.size() < selectMax){
            return selectedList.size() + 1;
        }else {
            return selectedList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isShowAddItem(position)){
            return TYPE_CAMERA;
        }else {
            return TYPE_PICTURE;
        }
    }

    private boolean isShowAddItem(int position){
        int size = selectedList.size() == 0 ? 0 : selectedList.size();
        return position == size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mImg;
        LinearLayout llDel;
        TextView tvDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.iv_img);
            llDel = (LinearLayout) itemView.findViewById(R.id.ll_del);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
        }
    }
}
