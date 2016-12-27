package cn.edu.pku.guoziyang.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.guoziyang.bean.TodayWeather;

public class Guide extends Activity implements ViewPager.OnPageChangeListener{

    private String TAG = "ViewPager";

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    //导航视图的三个布局文件
    private List<View> views;

    private ImageView[] dots;
    //存放三个导航圆点的id
    private  int[] ids = {R.id.iv1,R.id.iv2};

    private Button start_btn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();//动态的加载，需要在ViewPager中显示的视图
        initDots(); //将三个导航圆点对象存入数组中

        //viewpager中关联控件
        start_btn = (Button) views.get(1).findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this, TodayWeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    //动态的加载，需要在ViewPager中显示的视图
    private void initViews(){
        //获取LayoutInflater类型对象
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        //LayoutInflater用于加载布局
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));

        //为ViewPager添加适配器
        vpAdapter = new ViewPagerAdapter(views,this);
        //获取ViewPager对象
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    //将三个导航圆点对象存入数组中
    void initDots(){
        dots = new ImageView[views.size()];
        for(int i = 0; i < views.size();i++){
            dots[i] = (ImageView) findViewById(ids[i]);
        }

    }

    //动态修改小圆点的属性，即可实现相应的导航效果
    @Override
    public void onPageSelected(int i) {
        for(int a=0;a<ids.length;a++){
            if(a == i){//设置选中效果
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else{//设置未选中效果
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

}
