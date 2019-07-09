package com.example.argent.mobileplayer.activity.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argent.mobileplayer.activity.base.BasePager;

import java.util.ArrayList;

/**
 * author: 小川
 * Date: 2018/12/7
 * Description:
 */
public class MyFragment extends Fragment {
    private ArrayList<BasePager> basePagers;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BasePager basePager = getBasePager();
        if (basePager != null) {
            return basePager.rootview;
        }
        return null;
    }

    public void initData(ArrayList<BasePager> basePager, int position) {
        this.basePagers = basePager;
        this.position = position;
    }

    public BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            basePager.initData();
            basePager.isInitData = true;
        }
        return basePager;
    }
}
