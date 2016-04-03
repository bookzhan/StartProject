package com.bookzhan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhanlibrary.base.BaseFragment;

/**
 * Created by zhandalin on 2016-04-03 13:50.
 * 说明:
 */
public class EducationFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(context);
        textView.setText("EducationFragment");
        return textView;
    }
}
