package com.example.argent.mobileplayer.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.argent.mobileplayer.R;
import com.example.argent.mobileplayer.activity.domain.SearchBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * author: 小川
 * Date: 2018/12/13
 * Description:
 */
public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<SearchBean.ItemsData> mediaItems;
    private final LayoutInflater inflater;


    public SearchAdapter(Context context, List<SearchBean.ItemsData> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_netvideo_pager, null, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchBean.ItemsData mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getItemTitle());
        viewHolder.tv_desc.setText(mediaItem.getKeywords());
        //1.使用xUtil3请求图片
//        x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl());
        //2.使用Glide请求图片
//        Glide.with(context).load(mediaItem.getImageUrl())
//                .apply(RequestOptions.placeholderOf(R.drawable.video_default).error(R.drawable.video_default))
//                .into(viewHolder.iv_icon);
        //3.使用picasso
        Picasso.with(context).load(mediaItem.getItemImage().getImgUrl1())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);

        return convertView;
    }
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;

    }
}
