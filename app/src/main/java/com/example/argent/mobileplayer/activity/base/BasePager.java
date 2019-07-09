package com.example.argent.mobileplayer.activity.base;

import android.content.Context;
import android.view.View;

/**
 * author: 小川
 * Date: 2018/12/7
 * Description: 基类，公共类
 */
public abstract class BasePager {

    public final Context context;
    public final View rootview;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootview = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
