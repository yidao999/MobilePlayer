package com.example.argent.mobileplayer.activity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.argent.mobileplayer.activity.service.MusicPlayerService;

/**
 * author: 小川
 * Date: 2019/1/23
 * Description: 缓存工具类
 */

public class CacheUtils {

    public static void putPlaymode(Context context,String key,int values){
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu",context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key,values);
        edit.commit();
    }

    public static int getPlaymode(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu",context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }

    public static void putString(Context context, String key, String values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, values).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

}