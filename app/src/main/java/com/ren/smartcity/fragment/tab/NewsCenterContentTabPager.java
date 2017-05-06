package com.ren.smartcity.fragment.tab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ren.smartcity.R;
import com.ren.smartcity.adapter.TabTopNewsAdapter;
import com.ren.smartcity.bean.NewsCenterTabBean;
import com.ren.smartcity.utils.Constant;
import com.ren.smartcity.utils.MyLogger;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/5/1.
 */
public class NewsCenterContentTabPager {

    private String TAG = this.getClass().getSimpleName();
    private final Context context;
    public final View view;
    private ViewPager mViewPager;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private LinearLayout mPointContainer;

    public NewsCenterContentTabPager(Context context) {
        this.context = context;
        view = initView();
    }

    private View initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.newscenter_content_tab,null,false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_switch_image);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_news);
        mPointContainer = (LinearLayout) view.findViewById(R.id.ll_point_container);
        return view;
    }

    public void loadTabNetData(String url){
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        MyLogger.d(TAG,e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLogger.d(TAG,response);
                        parseData(response);
                    }
                });
    }

    private void parseData(String response) {
        Gson gson = new Gson();
        NewsCenterTabBean tabBean = gson.fromJson(response, NewsCenterTabBean.class);
        //绑定数据
        bindViewData(tabBean);
    }

    private void bindViewData(NewsCenterTabBean tabBean) {
        //轮播图数据
        bindTopImages(tabBean.getData().getTopnews());
    }

    private void bindTopImages(final List<NewsCenterTabBean.DataBean.TopnewsBean> topnews) {
        mPointContainer.removeAllViews();
        ArrayList<ImageView> images = new ArrayList<>();
        for (int i = 0 ; i < topnews.size() ; i ++){
            //图片
            ImageView view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String topimage = topnews.get(i).getTopimage();
            Picasso.with(context).load(Constant.replaceImageUrl(topimage)).placeholder(R.mipmap.ic_launcher).into(view);
            images.add(view);
            //指示器
            View pointView = new View(context);
            pointView.setBackgroundResource(R.drawable.point_gray_bg);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(5,5);
            params.rightMargin = 10;
            mPointContainer.addView(pointView,params);
        }
        //设置第一个点为红点
        mPointContainer.getChildAt(0).setBackgroundResource(R.drawable.point_red_bg);
        TabTopNewsAdapter adapter = new TabTopNewsAdapter(topnews,images,context);
        mViewPager.setAdapter(adapter);

        mTitle.setText(topnews.get(0).getTitle());


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitle.setText(topnews.get(position).getTitle());
                for (int i = 0 ; i <mPointContainer.getChildCount();i++ ){
                    if (position == i){
                        mPointContainer.getChildAt(i).setBackgroundResource(R.drawable.point_red_bg);
                    }else{
                        mPointContainer.getChildAt(i).setBackgroundResource(R.drawable.point_gray_bg);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}