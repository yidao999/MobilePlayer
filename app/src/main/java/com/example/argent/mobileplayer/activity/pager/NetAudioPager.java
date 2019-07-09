package com.example.argent.mobileplayer.activity.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.argent.mobileplayer.R;
import com.example.argent.mobileplayer.activity.adapter.NetAudioPagerAdapter;
import com.example.argent.mobileplayer.activity.base.BasePager;
import com.example.argent.mobileplayer.activity.domain.NetAudioPagerData;
import com.example.argent.mobileplayer.activity.utils.CacheUtils;
import com.example.argent.mobileplayer.activity.utils.Constants;
import com.example.argent.mobileplayer.activity.utils.LogUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * author: 小川
 * Date: 2018/12/7
 * Description:网络音乐
 */
public class NetAudioPager extends BasePager {

    @ViewInject(R.id.listview)
    private ListView mListView;
    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;
    private List<NetAudioPagerData.ListBean> datas;

    private NetAudioPagerAdapter adapter;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netaudio_pager, null);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音频的数据被初始化了。。。");
        String saveJson = CacheUtils.getString(context, Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            //解析数据
            processData(saveJson);
        }
        //联网
        getDataFromNet();

    }


    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                //保存数据
                CacheUtils.putString(context, Constants.ALL_RES_URL, result);
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {
        //解析数据
        NetAudioPagerData data = parsedJson(json);
        datas = data.getList();

        if (datas != null && datas.size() > 0) {
            //有数据
            tv_nonet.setVisibility(View.GONE);
            //设置适配器
            adapter = new NetAudioPagerAdapter(context,datas);
            mListView.setAdapter(adapter);
        }else{
            tv_nonet.setText("没有对应的数据...");
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson解析数据
     *
     * @param result
     * @return
     */
    private NetAudioPagerData parsedJson(String result) {
        return new Gson().fromJson(result, NetAudioPagerData.class);
    }
}
