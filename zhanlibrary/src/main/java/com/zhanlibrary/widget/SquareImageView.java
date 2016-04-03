package com.zhanlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by youzi on 2016/1/15.
 */
public class SquareImageView extends ImageView {
    private Context context;
    public SquareImageView(Context context) {
        super(context);
        this.context=context;
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w=MeasureSpec.makeMeasureSpec(widthMeasureSpec,MeasureSpec.AT_MOST);
        int h=MeasureSpec.makeMeasureSpec(heightMeasureSpec,MeasureSpec.AT_MOST);

        DisplayMetrics display = context.getResources().getDisplayMetrics();
        if(display.heightPixels>display.widthPixels){
            setMeasuredDimension(w, w);
        }else{
            setMeasuredDimension(h, h);
        }
    }
}
