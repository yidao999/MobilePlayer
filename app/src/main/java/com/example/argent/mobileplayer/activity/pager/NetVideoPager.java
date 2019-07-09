package com.example.argent.mobileplayer.activity.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.argent.mobileplayer.R;
import com.example.argent.mobileplayer.activity.activity.SystemVideoPlayer;
import com.example.argent.mobileplayer.activity.adapter.NetVideoPagerAdapter;
import com.example.argent.mobileplayer.activity.base.BasePager;
import com.example.argent.mobileplayer.activity.domain.MediaItem;
import com.example.argent.mobileplayer.activity.utils.CacheUtils;
import com.example.argent.mobileplayer.activity.utils.Constants;
import com.example.argent.mobileplayer.activity.utils.LogUtil;
import com.example.argent.mobileplayer.activity.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * author: 小川
 * Date: 2018/12/7
 * Description:网络视频页面
 */
public class NetVideoPager extends BasePager {

    @ViewInject(R.id.listview)
    private XListView mListview;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    private ArrayList<MediaItem> mediaItems;

    private NetVideoPagerAdapter adapter;

    private boolean isLoadMore = false;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager, null);
        x.view().inject(NetVideoPager.this, view);
        //监听点击事件
        mListview.setOnItemClickListener(new MyOnItemClickListener());
        mListview.setPullLoadEnable(true);
        mListview.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    class MyIXListViewListener implements XListView.IXListViewListener{

        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功===" + result);
                isLoadMore = true;
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败===" + ex);
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled===" + cex);
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("finish");
            }
        });
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();

        String saveJson = CacheUtils.getString(context, Constants.NET_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CacheUtils.putString(context,Constants.NET_URL,result);
                LogUtil.e("联网成功===" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败===" + ex);
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled===" + cex);
            }

            @Override
            public void onFinished() {
                LogUtil.e("finish");
            }
        });
    }

    private void processData(String json) {

        if(!isLoadMore){
            mediaItems = parseJson(json);
            showData();


        }else{
//            ArrayList<MediaItem> moreDatas = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(parseJson(json));
            adapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void showData() {
        //设置适配器
        if(mediaItems != null && mediaItems.size() >0){
//            videoPagerAdapter = new VideoPagerAdapter(context,mediaItems);
            adapter = new NetVideoPagerAdapter(context,mediaItems);
            mListview.setAdapter(adapter);
            onLoad();
            mTv_nonet.setVisibility(View.GONE);
        }else{
            mTv_nonet.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private void onLoad(){
        mListview.stopLoadMore();
        mListview.stopRefresh();
        mListview.setRefreshTime("更新时间:"+getSystemTime());
    }

    public String getSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if(jsonArray != null&& jsonArray.length() > 0){
                for(int i= 0;i<jsonArray.length();i++){

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if(jsonObjectItem != null){

                        MediaItem mediaItem = new MediaItem();
                        String movieName = jsonObjectItem.optString("movieName");//name
                        mediaItem.setName(movieName);
                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);
                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);
                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
