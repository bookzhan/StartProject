package com.zhanlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.zhanlibrary.R;


/**
 * Created by zhandalin on 2016-03-03 20:25.
 * 说明: 这个GridView 不处理复用逻辑,适合用在listView与ScrollView的嵌套
 */
public class MyGridView extends LinearLayout implements View.OnClickListener {

    private final Context context;
    private int numColumns;
    private AdapterView.OnItemClickListener onItemClickListener;
    private int verticalSpacing;

    public MyGridView(Context context) {
        this(context, null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.myGridView);
        numColumns = typedArray.getInt(R.styleable.myGridView_numColumns, 1);
        verticalSpacing = typedArray.getDimensionPixelOffset(R.styleable.myGridView_verticalSpacing, 0);
        typedArray.recycle();
    }


    public void setAdapter(BaseAdapter adapter) {
        LinearLayout viewContainer = null;
        LayoutParams childParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        childParams.weight = 1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i % numColumns == 0) {
                viewContainer = new LinearLayout(context);
                viewContainer.setClickable(true);
                viewContainer.setGravity(Gravity.CENTER);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if (i != 0)
                    params.topMargin = verticalSpacing;
                viewContainer.setLayoutParams(params);
                addView(viewContainer);
            }
            assert viewContainer != null;
            View view = adapter.getView(i, null, null);
            if (null != onItemClickListener) {
                view.setClickable(true);
                view.setTag(i);
                view.setOnClickListener(this);
            }
            viewContainer.addView(view, childParams);
        }
        int num = adapter.getCount() % numColumns;
        if (num != 0) {//表示最后一行不够,补齐
            for (int i = 0; i < numColumns - num; i++) {
                View view = new View(context);
                assert viewContainer != null;
                viewContainer.addView(view, childParams);
            }
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (null != onItemClickListener)
            onItemClickListener.onItemClick(null, v, (int) v.getTag(), 0);
    }
}
