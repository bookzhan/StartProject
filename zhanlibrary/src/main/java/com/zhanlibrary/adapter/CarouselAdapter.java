package com.zhanlibrary.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.zhanlibrary.model.HomeBasesData;
import com.zhanlibrary.utils.CommonUtils;
import com.zhanlibrary.utils.GHSStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhandalin 2015年09月10日 14:21.
 * 最后修改者: zhandalin  version 1.0
 * 说明:轮播图的Adapter
 */
public class CarouselAdapter extends PagerAdapter {
    private List<HomeBasesData> carousel_data;
    private Context context;
    private List<ImageView> imageList = new ArrayList();

    public CarouselAdapter(Context context, List<HomeBasesData> carousel_data) {
        this.context = context;
        this.carousel_data = carousel_data;
        for (int i = 0; i < 3; i++) {

            imageList.addAll(imageList);
        }
    }

    @Override
    public int getCount() {//注意这个,不要改,自己在ViewPager里面改
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final HomeBasesData focusImage = carousel_data.get(position % carousel_data.size());
        ImageView imageView = new ImageView(context);//图片还在内存里,这里就不做缓存了
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setClickable(true);
//        ImageView imageView = imageList.get(position % 3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "carousel_image");
                CommonUtils.judgeWhereToGo(context, focusImage);
            }
        });
        if (!GHSStringUtil.isEmpty(focusImage.getImage())) {
            Picasso.with(context).load(focusImage.getImage()).into(imageView);
        }
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void changeList(List<HomeBasesData> carousel_data) {
        this.carousel_data = carousel_data;
        notifyDataSetChanged();
    }
}

