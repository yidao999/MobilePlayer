package com.example.argent.mobileplayer.activity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.argent.mobileplayer.R;
import com.example.argent.mobileplayer.activity.adapter.SearchAdapter;
import com.example.argent.mobileplayer.activity.domain.MediaItem;
import com.example.argent.mobileplayer.activity.domain.SearchBean;
import com.example.argent.mobileplayer.activity.utils.Constants;
import com.example.argent.mobileplayer.activity.utils.JsonParser;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * author: 小川
 * Date: 2019/2/1
 * Description:
 */
public class SearchActivity extends Activity {

    private EditText etInput;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressBar;
    private TextView tvNodata;
    private SearchAdapter adapter;
    private ArrayList<MediaItem> mediaItems;

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private String url;
    private List<SearchBean.ItemsData> items;
    private Object dataVideo;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-02-01 01:16:19 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etInput = (EditText) findViewById(R.id.et_input);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        //设置点击事件
        ivVoice.setOnClickListener(myOnClickListener);
        tvSearch.setOnClickListener(myOnClickListener);
    }

    public ArrayList<MediaItem> getDataVideo() {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                MediaItem mediaItem = new MediaItem();

                String title = items.get(i).getItemTitle();
                mediaItem.setName(title);
                String url = items.get(i).getDetailUrl();
                mediaItem.setData(url);

                mediaItems.add(mediaItem);
            }
        }
        return mediaItems;
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_voice: //语音输入
                    showDialog();
//                    Toast.makeText(SearchActivity.this,"语音输入",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_search://搜索
//                    Toast.makeText(SearchActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                    searchText();
                    break;
            }
        }
    }

    private void searchText() {
        String text = etInput.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {

            if (items != null && items.size() > 0) {
                items.clear();
            }

            try {
                text = URLEncoder.encode(text, "UTF-8");
                text = String.valueOf(text);
                url = Constants.SEARCH_URL_LEFT + text + Constants.SEARCH_URL_RIGHT;
                getDataFromNet();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromNet() {
        tvNodata.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void processData(String result) {
        SearchBean searchBean = parsedJson(result);
        items = searchBean.getItems();
        showData();
        mediaItems = getDataVideo();

    }


    private void showData() {
        if (items != null && items.size() > 0) {
            //设置适配器
            adapter = new SearchAdapter(this, items);
            listview.setAdapter(adapter);
            tvNodata.setVisibility(View.GONE);
        } else {
            tvNodata.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private SearchBean parsedJson(String result) {
        return new Gson().fromJson(result, SearchBean.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViews();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //资源不行，暂时注释不播放
                Toast.makeText(SearchActivity.this, "资源不行，暂时不播放", Toast.LENGTH_SHORT).show();
                //                Intent intent = new Intent(SearchActivity.this,SystemVideoPlayer.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("videolist",mediaItems);
//                intent.putExtras(bundle);
//                intent.putExtra("position",position-1);
//                startActivity(intent);
            }
        });

    }

    private void showDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult results, boolean b) {
            String result = results.getResultString();
            Log.e("MainActivity", "result ==" + result);
            String text = JsonParser.parseIatResult(result);
            //解析好的
            Log.e("MainActivity", "text" + text);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(result);
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            etInput.setText(resultBuffer.toString());
            etInput.setSelection(etInput.length());
            searchText();
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("MainActivity", "onError ==" + speechError.getMessage());
        }
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
